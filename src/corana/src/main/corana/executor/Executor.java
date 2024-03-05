package main.corana.executor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import ghidra.util.InvalidNameException;
//import ghidra.util.exception.CancelledException;
//import ghidra.util.exception.DuplicateNameException;
//import ghidra.util.exception.VersionException;
import main.corana.emulator.base.Emulator;
import main.corana.emulator.cortex.*;
import main.corana.emulator.semantics.EnvModel;
import main.corana.emulator.semantics.Environment;
import main.corana.emulator.semantics.Memory;
import main.corana.emulator.taint.Taint;
import main.corana.emulator.taint.TaintModel;
import main.corana.enums.Variation;
import main.corana.external.handler.APIStub;
import main.corana.external.handler.ExternalCall;
import main.corana.pojos.AsmNode;
import main.corana.pojos.BitVec;
import main.corana.utils.*;

import java.io.IOException;
import java.util.Random;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Executor {

    protected static HashMap<String, Integer> nodeLabelToIndex = new HashMap<>();
    protected static ArrayList<AsmNode> asmNodes = null;
    protected static String jumpFrom = null;
    protected static String jumpTo = null;
    protected static int loopLimitation = 5;
    protected static HashMap<String, Integer> countJumpedPair = new HashMap<>();
    protected static HashMap<String, EnvModel> labelToEnvModel = new HashMap<>();
    protected static Stack<Map.Entry<EnvModel, HashMap<String, EnvModel>>> envStack = new Stack<>();
    protected static Map.Entry<EnvModel, HashMap<String, EnvModel>> recentPop = null;
    protected static String triggerPrevLabelTwoUnsat = null;
    protected static List<String> internalFunctions = new ArrayList<>();
    protected static String forkFrom = null;
    protected static String waitFrom = null;
    protected static Stack<Map.Entry<EnvModel, HashMap<String, EnvModel>>> processStack = new Stack<>();
    protected static Stack<Environment> forkEnv = new Stack<>();
    protected static Stack<Environment> waitEnv = new Stack<>();
    protected static long startTime;
    protected static int processCount = 10;
    protected static Variation variation;
    protected static List<String> callFunctions = new ArrayList<>();
    private static long procEnd;
    private static Environment returnEnv=null;
    private static List<String> currentPC = new ArrayList<>();

    private static void fork(Environment env, String prev, String from) {
        long procCheckpoint = System.currentTimeMillis();
        procEnd = procCheckpoint + 2 * 60 * 60 * 1000;
        processCount++;
        Process p = new Process(variation, env, prev, from);
        p.run();

    }

    public static Map.Entry<Environment, String> byteExecute(Variation var, byte[] bs, Environment initEnv)  {
        List<Map.Entry<Environment, String>> result = new ArrayList<>();
        long startCapstone = System.currentTimeMillis();
        asmNodes = BinParser.parseInstruction(bs, 0);
        Logs.infoLn("-> Capstone disassembler elapsed: " + (System.currentTimeMillis() - startCapstone) + "ms");
        if (asmNodes != null) {
            for (AsmNode n : asmNodes) {
                String saveAsm = n.getLabel() + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams();
                Exporter.addOriginAsm(saveAsm);
            }
            Exporter.exportOriginAsm(Corana.inpFile + ".capstone-asm");
            asmNodes = BinParser.expand(asmNodes);

            for (int i = 0; i < asmNodes.size(); i++) {
                if (!nodeLabelToIndex.containsKey(asmNodes.get(i).getLabel())) {
                    nodeLabelToIndex.put(asmNodes.get(i).getLabel(), i);
                }
            }

            EnvModel genesis = new EnvModel("-", "");
            labelToEnvModel.put(genesis.label, genesis);
            startTime = System.currentTimeMillis();
            variation = var;
            //Load memory and initialize stack pointer
            recentPop = null;
            BinParser._start = 0; BinParser.end = 4;
            currentPC = new ArrayList<>();
            Z3Solver.init();
            Memory.loadMemory();
            Environment env = new Environment(initEnv);
            fork(env, genesis.label, String.valueOf(0)); //-10000 hex
        }

        String pc = recentPop == null ? "" : recentPop.getKey().pathCondition;

        return Pair.of(returnEnv, pc);
    }

    public static Map.Entry<Environment, List<String>> customExecute(Variation var, String inpFile, long startPC, long endPC, Environment initEnv, TaintModel taintModel)  {
        List<Map.Entry<Environment, String>> result = new ArrayList<>();
        Logs.logFile(inpFile + ".out");
        if (!FileUtils.isExist(inpFile)) {
            Logs.infoLn("-> Input file doest not exist.");
        } else if (!isARM(inpFile)) {
            Logs.infoLn("-> Input file is not an ARM variation.");
        } else {

            long startCapstone = System.currentTimeMillis();
            asmNodes = BinParser.parseBySection(inpFile);
            internalFunctions = BinParser.getInternalSymbols(inpFile);

            Logs.infoLn("-> Capstone disassembler elapsed: " + (System.currentTimeMillis() - startCapstone) + "ms");
            if (asmNodes != null) {

                for (AsmNode n : asmNodes) {
                    String saveAsm = n.getLabel() + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams();
                    Exporter.addOriginAsm(saveAsm);
                }
                Exporter.exportOriginAsm(Corana.inpFile + ".capstone-asm");
                asmNodes = BinParser.expand(asmNodes);

                for (int i = 0; i < asmNodes.size(); i++) {
                    if (!nodeLabelToIndex.containsKey(asmNodes.get(i).getLabel())) {
                        nodeLabelToIndex.put(asmNodes.get(i).getLabel(), i);
                    }
                }

                EnvModel genesis = new EnvModel("-", "");
                labelToEnvModel.put(genesis.label, genesis);
                startTime = System.currentTimeMillis();
                variation = var;

                //Load memory and initialize stack pointer
                recentPop = null;
                currentPC = new ArrayList<>();
                BinParser.set_start(startPC);
                BinParser.set_end(endPC);
                Z3Solver.init();
                Environment env = new Environment(initEnv);

                Memory.loadSOMemory(inpFile);
                Memory.set(new BitVec(100), new BitVec("SYM_STR_100"));
                Memory.setupTaintMemory(taintModel);

                for (int i = 0; i < env.stacks.stack.size(); i++) {
                    // write new sp
                    BitVec ele = env.stacks.stack.get(i);
                    String sym = String.format("(bvadd %s (bvneg #x00000004))", initEnv.register.getFormula('s'));
                    BitSet concreteValue = Arithmetic.intToBitSet(Arithmetic.bitSetToInt(initEnv.register.get('s').getVal()) - 4);
                    initEnv.register.set('s', new BitVec(sym, concreteValue));
                    Memory.set(initEnv.register.get('s'), ele);
                }
                //Setting taint variables
                for (Character r: taintModel.register.regs.keySet())  {
                    if (taintModel.register.regs.get(r)) {
                        env.register.get(r).taint = true;
                        Logs.info("+++ Taint r" + r + " ");
                    }
                }
                fork(env, genesis.label, String.valueOf(startPC )); //-10000 hex
            }
        }
        String pc = recentPop == null ? "" : recentPop.getKey().pathCondition;

        return Pair.of(returnEnv, currentPC);
    }

    public static void execute(Variation var, String inpFile) {
        //Logs.logFile(inpFile + ".out");
        if (!FileUtils.isExist(inpFile)) {
            Logs.infoLn("-> Input file doest not exist.");
        } else if (!isARM(inpFile)) {
            Logs.infoLn("-> Input file is not an ARM variation.");
        } else {
            long startCapstone = System.currentTimeMillis();
            asmNodes = BinParser.parseBySection(inpFile);
            internalFunctions = BinParser.getInternalSymbols(inpFile);

            Logs.infoLn("-> Capstone disassembler elapsed: " + (System.currentTimeMillis() - startCapstone) + "ms");
            if (asmNodes != null) {
                for (AsmNode n : asmNodes) {
                    String saveAsm = n.getLabel() + " " +    n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams();
                    Exporter.addOriginAsm(saveAsm);
                }
                Exporter.exportOriginAsm(Corana.inpFile + ".capstone-asm");
                Exporter.exportDot(Corana.dotOutput + ".dot");
                asmNodes = BinParser.expand(asmNodes);
                for (int i = 0; i < asmNodes.size(); i++) {
                    //asmNodes.get(i).getAddress() ==
                    if (!nodeLabelToIndex.containsKey(asmNodes.get(i).getLabel())) {
                        nodeLabelToIndex.put(asmNodes.get(i).getLabel(), i);
                    }
                }

                EnvModel genesis = new EnvModel("-", "");
                labelToEnvModel.put(genesis.label, genesis);
                startTime = System.currentTimeMillis();
                variation = var;
                Z3Solver.init();
                //Load memory and initialize stack pointer
                Environment env = new Environment();
                Memory.loadMemory(inpFile);
                fork(env, genesis.label, String.valueOf(BinParser.get_start()));
            }
        }
        Logs.closeLog();
    }


    protected static void execFrom(Emulator emulator, String prevLabel, String label) {
        AsmNode n;
        while (!nodeLabelToIndex.containsKey(label)) {
            Integer index = Integer.parseInt(label)-2;
            label = String.valueOf(index);
        }
        n = asmNodes.get(nodeLabelToIndex.get(label));

        try {
        if (label.equals(String.valueOf(BinParser.end))) {
            //gg();
            Logs.info("-> Executing", n.getAddress(), ":", n.getOpcode(), n.getParams(), '\n');
            Logs.infoLn("-> Process ended. Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
            processCount--;
            if (!processStack.empty() && !waitEnv.empty() && processCount > 0) {
                //Corana.inpFile += "_fork";
                Logs.info(String.format("\t-> Continue parent process from %s\n", asmNodes.get(nodeLabelToIndex.get(waitFrom)).getAddress()));

                Map.Entry<EnvModel, HashMap<String, EnvModel>> model = processStack.pop(); /// PROBABLY NOT CORRECT
                Environment env = waitEnv.pop();
                recentPop = model;
                labelToEnvModel = model.getValue();
                fork(env, waitFrom, waitFrom);

                //execFrom(new M0(env), waitFrom, waitFrom); // ??? prev
            } else {
                returnEnv = emulator.getEnv();
                return;
            }
        } else if (label.equals(waitFrom)) {
            if (!processStack.empty() && !forkEnv.empty() && processCount > 0) {
                Logs.info(String.format("\t-> Run child process from %s\n", asmNodes.get(nodeLabelToIndex.get(forkFrom)).getAddress()));

                Map.Entry<EnvModel, HashMap<String, EnvModel>> model = processStack.pop();
                Environment env = forkEnv.pop();
                recentPop = model;
                //forkFrom = model.getKey().label;
                labelToEnvModel = model.getValue();
                //execFrom(new M0(env), forkFrom, forkFrom); // ??? prev
                fork(env, forkFrom, forkFrom);
            }
        }
        Logs.info("-> Executing", n.getAddress(), ":", n.getOpcode(), n.getParams(), '\n');

            if (System.currentTimeMillis() > procEnd) {
                Logs.info("\t-> Process timeout.");
                throw new Exception();
            }
            if (emulator.getClass() == M0.class) {
                singleExec((M0) emulator, prevLabel, n);
            } else if (emulator.getClass() == M0_Plus.class) {
                singleExec((M0_Plus) emulator, prevLabel, n);
            } else if (emulator.getClass() == M3.class) {
                singleExec((M3) emulator, prevLabel, n);
            } else if (emulator.getClass() == M4.class) {
                singleExec((M4) emulator, prevLabel, n);
            } else if (emulator.getClass() == M7.class) {
                singleExec((M7) emulator, prevLabel, n);
            } else if (emulator.getClass() == M33.class) {
                singleExec((M33) emulator, prevLabel, n);
            } else {
                Logs.infoLn("-> Wrong Variation!");
                return;
            }

            String newLabel = (jumpTo == null) ? nextInst(label) : jumpTo;
            String address = n.getAddress();
            String newAddress = "";
            String newOpcode = "";
            AsmNode newNode = null;
            // Check if function call is external
            if (nodeLabelToIndex.get(newLabel) == null && nodeLabelToIndex.get(nextInst(label)) != null) {
                // Deal with external call
                newNode = asmNodes.get(nodeLabelToIndex.get(nextInst(label)));
                newAddress = newNode.getAddress();
                newOpcode = newNode.getOpcode();
            } else {
                if (nodeLabelToIndex.get(newLabel) != null) {
                    newNode = asmNodes.get(nodeLabelToIndex.get(newLabel));
                    newAddress = newNode.getAddress();
                    newOpcode = newNode.getOpcode();
                }
            }

            boolean isFault = false;

            if (jumpTo != null) {
                newLabel = nodeLabelToIndex.containsKey(newLabel) ? newLabel :  String.valueOf(Integer.parseInt(newLabel) - 2);
                jumpTo = newLabel;
                if (nodeLabelToIndex.containsKey(newLabel)) {
                    String pair = jumpFrom + " --> " + jumpTo;
                    Logs.info(String.format("\t-> Start Jumping from %s --> %s\n", asmNodes.get(nodeLabelToIndex.get(jumpFrom)).getAddress(), asmNodes.get(nodeLabelToIndex.get(jumpTo)).getAddress()));
                    countJumpedPair.put(pair, countJumpedPair.containsKey(pair) ? countJumpedPair.get(pair) + 1 : 1);
                    if (countJumpedPair.get(pair) <= loopLimitation) {
                        jumpTo = null;
                        jumpFrom = null;
                        Exporter.add(address+ "_" + n.getOpcode() + "," + newAddress + "_" + newOpcode + "," + countJumpedPair.get(pair) + "\n");

                        int savedAsmSize = Exporter.savedAsm.size();
                        String lastSaved = Exporter.savedAsm.get(savedAsmSize - 1);
                        String[] arr = lastSaved.split(" ");
                        Exporter.savedAsm.set(savedAsmSize - 1, arr[0] + " " + arr[1] + " " + newLabel);

                        String finalPrevLabel = triggerPrevLabelTwoUnsat == null ? label : triggerPrevLabelTwoUnsat;
                        triggerPrevLabelTwoUnsat = null;
                        execFrom(emulator, finalPrevLabel, newLabel);
                    } else {
                        Logs.infoLn("\t-> Loop limitation exceeded, break.");
                        isFault = true;
                    }
                } else {
                    Logs.infoLn("\t-> Non-existing label, break.");
                    isFault = true;
                }
            } else {
                isFault = true;
            }
            if (isFault) {
                jumpTo = null;
                jumpFrom = null;

                // Recover from next Instruction
                String nextLabel = nextInst(label);
                String lrLabel = String.valueOf(Arithmetic.hexToInt(emulator.getEnv().register.get('l').getSym()));

                if (nodeLabelToIndex.containsKey(nextLabel) || nodeLabelToIndex.containsKey(lrLabel)) {
                    newLabel = nodeLabelToIndex.containsKey(nextLabel) ? nextLabel : lrLabel;

                    newNode = asmNodes.get(nodeLabelToIndex.get(newLabel));
                    newAddress = newNode.getAddress();
                    newOpcode = newNode.getOpcode();
                    Exporter.add(address + "_" + n.getOpcode()  + "," + newAddress + "_" + newOpcode);
                    execFrom(emulator, label, newLabel);
                }
            }

        } catch (Exception | StackOverflowError e) {
            //return;
            e.printStackTrace();
            Logs.infoLn("-> Process ended. Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
            processCount--;
            if (!processStack.empty() && !waitEnv.empty() && processCount > 0) {
                Logs.info(String.format("\t-> Continute parent process from %s\n", asmNodes.get(nodeLabelToIndex.get(waitFrom)).getAddress()));

                Map.Entry<EnvModel, HashMap<String, EnvModel>> model = processStack.pop();
                Environment env = waitEnv.pop();
                recentPop = model;
                //waitFrom = model.getKey().label;
                labelToEnvModel = model.getValue();
                fork(env, waitFrom, waitFrom); // ??? prev
            } else if (!processStack.empty() && !forkEnv.empty() && processCount > 0) {
                Logs.info(String.format("\t-> Run child process from %s\n", asmNodes.get(nodeLabelToIndex.get(forkFrom)).getAddress()));

                Map.Entry<EnvModel, HashMap<String, EnvModel>> model = processStack.pop();
                Environment env = forkEnv.pop();
                recentPop = model;
                //forkFrom = model.getKey().label;
                labelToEnvModel = model.getValue();
                fork(env, forkFrom, forkFrom); // ??? prev
            } else {
                returnEnv = emulator.getEnv();
            }
        }
        returnEnv = emulator.getEnv();
    }

    protected static void singleExec(M0 emulator, String prevLabel, AsmNode n) throws Exception {
        String nLabel = n.getLabel();
        if (nLabel == null) System.exit(0);
        // Calculate pc of next instruction
        //if (Arithmetic.bitSetToInt(emulator.getEnv().register.get('p').getVal()) == Long.parseLong(prevLabel) + 8)
            if (!nLabel.contains("+") && !nLabel.contains("-"))
                emulator.write('p', new BitVec(Integer.parseInt(nLabel) + Configs.instructionSize*2));

        // Start single instruction execution
        Exporter.addAsm(n.getLabel() + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams());
        Exporter.exportDot(Corana.dotOutput + ".dot");

        Character suffix = n.isUpdateFlag() ? 's' : null;
        String[] arrParams = Objects.requireNonNull(n.getParams()).split("\\,");
        if (n.getOpcode() != null) {
            String opcode = n.getOpcode();
            if ("b".equals(opcode) || "bl".equals(opcode) || "bx".equals(opcode) || "blx".equals((opcode))) {
                Character condSuffix = Mapping.condStrToChar.get(Objects.requireNonNull(n.getCondSuffix()).toUpperCase());
                String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                jumpFrom = nLabel;

                EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                String strLabel = null;
                Character charLabel = null;
                if (isConcreteLabel(arrParams[0])) {
                    strLabel = arrParams[0].contains("-") ? arrParams[0] :
                            String.valueOf(Arithmetic.hexToInt(arrParams[0].replace("#0x", "").replace("0x", "")));
                } else {
                    charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                }
                Map.Entry<EnvModel, EnvModel> envPair;
                switch (opcode) {
                    case "b":
                        envPair = emulator.b(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bl":
                        envPair = emulator.bl(Integer.parseInt(nextInst(nLabel)), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bx":
                        envPair = emulator.bx(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "blx":
                        envPair = emulator.bl(Integer.parseInt(nextInst(nLabel)), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    default:
                        envPair = null;
                        break;
                }
                if (envPair == null) {
                    return;
                }
                EnvModel modelTrue = envPair.getKey();
                EnvModel modelFalse = envPair.getValue();

                EnvModel modelFork = null;
                EnvModel modelWait = null;
                // If it is a direct jump
                boolean isFunctionCall = false;
                if (isConcreteLabel(arrParams[0])) {
                    if (modelTrue != null && modelTrue.envData != null) {
                        String funcname = ExternalCall.findFunctionName(arrParams[0]);
                        if (funcname.equals("")) {
                            funcname = DBDriver.getFunctionLabel(SysUtils.getAddressValue(n.getAddress().replace("#0x", "").replace("x0", "")));
                        }
                        boolean isGlibC = !funcname.equals("") && ExternalCall.isLibraryC(funcname);
                        if (isGlibC) {
                            if (!callFunctions.contains(funcname)) callFunctions.add(funcname);
                            Logs.infoLn("\t === Call to library function: " + funcname);
                            emulator.exec(funcname);
                            //Exporter.add(n.getAddress() + "_" + n.getOpcode()  + "," + "CALL API: " + funcname);
                            Exporter.addAPICall(funcname);
                            modelTrue.label = String.valueOf(Arithmetic.hexToInt(emulator.getEnv().register.get('l').getSym()));
                            modelTrue.prevLabel = prevLabel;
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        } else {
                        if (!ExternalCall.isExternalFucntion(arrParams[0])) {
                            //Internal Function
                            if (!funcname.equals("")) {
                                Logs.infoLn("\t ==+ Call to: " + funcname);
                                //Exporter.add(n.getAddress() + "_" + n.getOpcode()  + "," + "Call func: " + funcname);
                                Exporter.addAPICall(funcname);
                            }

                            modelTrue.label = strLabel;
                            modelTrue.prevLabel = prevLabel;
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                        if (ExternalCall.isExternalFucntion(arrParams[0])) {
                            if (!callFunctions.contains(funcname)) callFunctions.add(funcname);
                            isFunctionCall = true;
                            Logs.infoLn("\t === Call to library function: " + funcname);
                            //emulator.write('0', new BitVec(SysUtils.addSymVar()));
                            if (funcname.equals("fork")) {
                                Logs.infoLn("\t === Fork a new process. Run parent process:");
                                processCount++;
                                forkFrom = nextInst(jumpFrom);
                                modelFork = emulator.fork(thisEnvModel);
                                modelFork.label = nextInst(jumpFrom);
                                modelFork.prevLabel = prevLabel;
                                int randomId = (new Random()).nextInt(100);
                                emulator.write('0', new BitVec(randomId));
                                labelToEnvModel.put(modelFork.label, modelFork);
                                decideToFork(modelFork, emulator);
                            } else if (funcname.equals("wait")) {
                                Logs.infoLn("\t === Wait for child process.");
                                waitFrom = nextInst(jumpFrom);
                                modelWait = new EnvModel(modelTrue);
                                modelWait.prevLabel = prevLabel;
                                modelWait.label = nextInst(jumpFrom);
                                labelToEnvModel.put(modelWait.label, modelWait);
                                decideToWait(modelWait, emulator);
                            } else {
                                emulator.call(arrParams[0]);
                            }
                            Exporter.addAPICall(funcname);
                            modelTrue.label = nextInst(jumpFrom);
                            modelTrue.prevLabel = prevLabel;
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                            if (emulator.getCurrentSource() != "") {
                                emulator.taint('0'); //TODO
                                emulator.cleanSource();
                            }
                        }
                    }
                } else {
                    // Indirect jump
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = modelTrue.envData.eval;
                        modelTrue.prevLabel = prevLabel;
                        if (modelTrue.label == null) {
                            // Function return bx lr
                            if (charLabel.equals('l')) {
                                int foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('l').getVal());
                                if (n.getParams().equals("pc")) foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('p').getVal())-4;
                                if (foundLabel != 0 && foundLabel % 2 == 0) {
                                    modelTrue.label = String.valueOf(foundLabel);
                                    labelToEnvModel.put(modelTrue.label, modelTrue);
                                    Logs.infoLn("\t-> Found the destination: " + Arithmetic.intToHex(foundLabel));
                                } else {
                                    Logs.infoLn("\t-> Destination is undetectable.");
                                    if (!processStack.empty() && !forkEnv.empty() && processCount > 0) {
                                        Logs.info(String.format("\t-> Run child process from %s\n", asmNodes.get(nodeLabelToIndex.get(forkFrom)).getAddress()));
                                        //Exporter.add(address + "," + newAddress + "," + countJumpedPair.get(pair) + "\n");
                                        Map.Entry<EnvModel, HashMap<String, EnvModel>> model = processStack.pop();
                                        Environment env = forkEnv.pop();
                                        recentPop = model;
                                        //forkFrom = model.getKey().label;
                                        labelToEnvModel = model.getValue();
                                        execFrom(new M0(env), forkFrom, forkFrom); // ??? prev
                                    }
                                }
                            } else { // If call using function pointer (e.g., JNIEnv->)
                                modelTrue.label = nextInst(jumpFrom);;
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                                // If detect call source methods
                                if (charLabel != 'p') {
                                    if (emulator.getCurrentSource() != "") {
                                        emulator.taint('0');
                                        emulator.cleanSource();
                                    } else { // If call unknown JNI methods -> over-tainting
                                        // First try to find JNIEnv by Offset
                                        Character regChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                                        String regValue = emulator.getEnv().register.get(regChar).getSym();
                                        String funcname = ExternalCall.findFunctionName(regValue);
                                        List<String> APIlist = Arrays.stream(APIStub.class.getMethods()).map(s -> s.getName()).collect(Collectors.toList());

                                        if (funcname.equals("")) {
                                            funcname = DBDriver.getFunctionLabel(SysUtils.getAddressValue(regValue));
                                        }
                                        if (!funcname.equals("") && APIlist.contains(funcname)) {
                                            //If can find JNIEnv methods
                                            Logs.infoLn("\t === Call to JNIEnv function: " + funcname);
                                            emulator.exec(funcname);
                                            //Exporter.add(n.getAddress() + "_" + n.getOpcode()  + "," + "CALL API: " + funcname);
                                        } else {
                                            //Second, if cannot find JNI methods -> Overtainting
                                            Logs.infoLn("\t === Call to JNIEnv function: " + funcname);
                                            emulator.callUnknownJNIEnv();
                                        }

                                    }
                                }
                            }
                        } else {
                            if (modelTrue.label.startsWith("#x")) {
                                modelTrue.label = String.valueOf(Arithmetic.hexToInt(modelTrue.label.replace("#x", "")));
                            }
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                    }
                    if (modelTrue != null && modelFalse!= null)
                    currentPC = Arrays.asList(modelTrue.pathCondition, modelFalse.pathCondition);
                }

                if (modelFalse != null && modelFalse.envData != null) {
                    modelFalse.label = nextInst(jumpFrom);
                    modelFalse.prevLabel = prevLabel;
                    labelToEnvModel.put(modelFalse.label, modelFalse);
                }

                if (!isFunctionCall) decideToJump(modelTrue, modelFalse);
            } else if ("adr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                GhidraRun.AssemblyInstruction ghidraInst = GhidraRun.getAssemblyList().get(Long.parseLong(nLabel));
                emulator.adr(p0, Arithmetic.fromHexStr(ghidraInst.getRefAddress()));
                if (Taint.isSource(ghidraInst.getRefString())) {
                    emulator.markSource(ghidraInst.getRefString());
                }
                //Logs.infoLn(ghidraInst.getRefString());
            }
            else if ("nop".equals(opcode)) {
                emulator.nop(suffix);
            } else if ("bic".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (im == null) {
                    emulator.bics(p0, p1, p2, suffix);
                } else {
                    emulator.bic(p0, p1, im, suffix);
                }
            } else if ("lsl".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsls(p0, p1, p2, im, suffix);
            } else if ("lsr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsrs(p0, p1, p2, im, suffix);
            } else if ("mul".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.muls(p0, p1, p2, suffix);
            } else if ("mvn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mvns(p0, p1, suffix);
                } else {
                    emulator.mvn(p0, im, suffix);
                }
            } else if ("mov".equals(opcode) || "movw".equals(opcode) || "movt".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                switch (opcode) {
                    case "mov":
                        emulator.mov(p0, p1, im, suffix);
                        break;
                    case "movw":
                        emulator.movw(p0, p1, im, suffix);
                        break;
                    case "movt":
                        emulator.movt(p0, p1, im, suffix);
                        break;
                }

            } else if ("adc".equals(opcode)) {
                Integer im;
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams[2].contains("#")) {
                    im = SysUtils.normalizeNumInParam(arrParams[2].trim());
                    emulator.adc(p0, p1, im, suffix);
                } else {
                    Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    emulator.adcs(p0, p1, p2, suffix);
                }
            } else if ("and".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer p2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2And = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (p2 != null) {
                        emulator.and(p0, p1, p2, suffix);
                    } else {
                        emulator.ands(p0, p1, p2And, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.and(p0, p1, p2And, p2, extType, extraChar, extraNum, suffix);
                }
            } else if ("rsb".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.rsb(p0, p1, im, suffix);
                    } else {
                        emulator.rsbs(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.rsb(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("orr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.orr(p0, p1, im, suffix);
                    } else {
                        emulator.orrs(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.orr(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("eor".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.eor(p0, p1, im, suffix);
                    } else {
                        emulator.eors(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.eor(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("add".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                GhidraRun.AssemblyInstruction ghidraInst = GhidraRun.getAssemblyList().get(Long.parseLong(nLabel));
                if (ghidraInst.getRefString() != null) {
                    if (Taint.isSource(ghidraInst.getRefString())) {
                        emulator.markSource(ghidraInst.getRefString());
                    }
                }
                //FIX:2023/10/25 FIX ADD p0, pc
                Character p1 ;
                Integer im ;
                Character p2;
                if (arrParams.length < 3) {
                    p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                    p1 = arrParams[1].contains("#") ? null: Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                    im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                    emulator.add(p0, p0, p1, im, suffix);
                } else {
                    p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                    p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                    im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                    p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    if (arrParams.length == 3) {
                        emulator.add(p0, p1, p2, im, suffix);
                    } else {
                        String[] extArr = arrParams[3].split("\\s+");
                        String extType = extArr[0];
                        Integer extraNum = null;
                        Character extraChar = null;
                        if (extArr.length > 1) {
                            if (extArr[1].contains("#")) {
                                extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                            } else {
                                extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                        }
                        emulator.add(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                    }
                }
                //TODO //Fix PC jump (Oct/09/2023) - temporary CODE !!!
                p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                BitVec pcValue = emulator.getEnv().register.get('p');
                if (p0.equals('p')) {
                    String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                    if (preCond.length() > 5000) preCond = "";
                    //if (Arithmetic.hexToInt(pcValue.getSym()) != 0) {
                    if (Arithmetic.hexToInt(pcValue.getSym()) > 1000) {
                        jumpFrom = nLabel;

                        EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                        labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                        String strLabel = null;
                        Character charLabel = null;

                        long pcLabel = Arithmetic.hexToInt(pcValue.getSym().replace("#0x", "").replace("0x", "")) - 65536;
                        strLabel = String.valueOf(pcLabel);
                        charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));

                        Map.Entry<EnvModel, EnvModel> envPair;
                        envPair = emulator.b(preCond, null, strLabel == null ? charLabel : strLabel);
                        EnvModel modelTrue = envPair.getKey();
                        EnvModel modelFalse = envPair.getValue();
                        // If it is a direct jump
                        boolean isFunctionCall = false;
                        String funcname = ExternalCall.findFunctionName(pcValue.getSym());
                        if (funcname.equals("")) {
                            funcname = DBDriver.getFunctionLabel(SysUtils.getAddressValue(Arithmetic.intToHex(pcLabel)));
                        }
                        //String value = Memory.get(result);
                        List<String> APIlist = Arrays.stream(APIStub.class.getMethods()).map(s -> s.getName()).collect(Collectors.toList());
                        boolean isGlibC = !funcname.equals("") && (APIlist.contains(funcname) || APIlist.contains("__aeabi_"+funcname));
                        if (!funcname.equals("") || ExternalCall.isExternalFucntion(Arithmetic.intToHex(pcLabel))) {
                            if (!callFunctions.contains(funcname)) callFunctions.add(funcname);

                            Logs.infoLn("\t === Call to library function: " + funcname);
                            emulator.exec(funcname);
                        } else if (isGlibC) {
                            if (!callFunctions.contains(funcname)) callFunctions.add(funcname);
                            if (APIlist.contains(funcname)) {
                                Logs.infoLn("\t === Call to library function: " + funcname);
                                emulator.exec(funcname);
                            }
                        }
                        else {
                            if (modelTrue != null && modelTrue.envData != null) {
                                Logs.infoLn("\t ==+ PC jump to: " + pcValue.getSym());
                                modelTrue.label = strLabel;
                                modelTrue.prevLabel = prevLabel;
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                            }
                            pcJump(modelTrue, modelFalse);
                            jumpTo = modelTrue.label;
                        }
                    }

                }

            } else if ("sub".equals(opcode)) { //FIX:2022/08/22
                Character p0;
                Character p1 ;
                Integer im ;
                Character p2;
                if (arrParams.length < 3) {
                    p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                    im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                    emulator.sub(p0, p0, null, im, suffix);
                } else {
                    p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                    p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                    im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                    p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    if (arrParams.length == 3) {
                        emulator.sub(p0, p1, p2, im, suffix);
                    } else {
                        String[] extArr = arrParams[3].split("\\s+");
                        String extType = extArr[0];
                        Integer extraNum = null;
                        Character extraChar = null;
                        if (extArr.length > 1) {
                            if (extArr[1].contains("#")) {
                                extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                            } else {
                                extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                        }
                        emulator.sub(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                    }
                }
            } else if ("cmp".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    emulator.cmp(p0, p1, im, suffix);
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmp(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmn(p0, im, suffix);
                    } else {
                        emulator.cmn(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmn(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("svc".equals(opcode)) {
                Integer imSvc = arrParams[0].contains("#") ? SysUtils.normalizeNumInParam(arrParams[0].trim()) : null;
                emulator.svc(imSvc);

            } else if ("pop".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.pop(c);
                    //TODO: fix PC jump
                    if (c.equals('p')) {
                        String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                        if (preCond.length() > 5000) preCond = "";

                        BitVec pcValue = emulator.getEnv().register.get('p');
                        if (Arithmetic.hexToInt(pcValue.getSym()) > 1000) {
                            jumpFrom = nLabel;

                            EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                            labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                            String strLabel = null;
                            Character charLabel = null;

                                strLabel = String.valueOf(Arithmetic.hexToInt(pcValue.getSym().replace("#0x", "").replace("0x", "")));
                                charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));

                                Map.Entry<EnvModel, EnvModel> envPair;
                                envPair = emulator.b(preCond, null, strLabel == null ? charLabel : strLabel);
                                EnvModel modelTrue = envPair.getKey();
                                EnvModel modelFalse = envPair.getValue();
                                // If it is a direct jump
                                if (modelTrue != null && modelTrue.envData != null) {
                                    Logs.infoLn("\t ==+ PC jump to: " + pcValue.getSym());
                                    modelTrue.label = strLabel;
                                    modelTrue.prevLabel = prevLabel;
                                    labelToEnvModel.put(modelTrue.label, modelTrue);
                                }
                                pcJump(modelTrue, modelFalse);
                        }

                    }
                }
            } else if ("push".equals(opcode)) {
                for (int i = arrParams.length - 1; i >= 0; i--) {
                    String p = arrParams[i];
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.push(c);
                }
            } else if ("ldrb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrb(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrb(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldr".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                    // TODO: tmp
                    if (arrParams[1].contains("]")) {
                        type = 4;
                    }
                }

                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                BitVec newValue = null;
                BitVec extValue = null;
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldr(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldr(p0, newValue);
                        break;
                    case 2: //@address mode: pre-indexed ldr r0, [r1, #12]!
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3: //@address mode: offset
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 4: //@address mode: post-indexed
                        // ldr r3, [r1], #4    Load the value at memory address found in R1 to register R3. Base register (R1) modified: R1 = R1+4
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].replace("[", "").replace("]", "")));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.val(p1);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);
                            newValue = emulator.val(p1);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrAt(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
                //TODO//Fix PC jump (27/02/2022) - temporary
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                BitVec pcValue = emulator.getEnv().register.get('p');
                if (p0.equals('p')) {
                    String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                    if (preCond.length() > 5000) preCond = "";
                    //if (Arithmetic.hexToInt(pcValue.getSym()) != 0) {
                    if (Arithmetic.hexToInt(pcValue.getSym()) > 1000) {
                        jumpFrom = nLabel;

                        EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                        labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                        String strLabel = null;
                        Character charLabel = null;

                        strLabel = String.valueOf(Arithmetic.hexToInt(pcValue.getSym().replace("#0x", "").replace("0x", "")));
                        charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));

                        Map.Entry<EnvModel, EnvModel> envPair;
                        envPair = emulator.b(preCond, null, strLabel == null ? charLabel : strLabel);
                        EnvModel modelTrue = envPair.getKey();
                        EnvModel modelFalse = envPair.getValue();
                        // If it is a direct jump
                        boolean isFunctionCall = false;
                        String funcname = ExternalCall.findFunctionName(pcValue.getSym());
                        if (funcname.equals("")) {
                            funcname = DBDriver.getFunctionLabel(SysUtils.getAddressValue(newValue.getSym().replace("#0x", "").replace("x0", "")));
                        }
                        //String value = Memory.get(result);
                        List<String> APIlist = Arrays.stream(APIStub.class.getMethods()).map(s -> s.getName()).collect(Collectors.toList());
                        boolean isGlibC = !funcname.equals("") && (APIlist.contains(funcname) || APIlist.contains("__aeabi_"+funcname));
                        if (ExternalCall.isExternalFucntion(pcValue.getSym())) {
                            if (!callFunctions.contains(funcname)) callFunctions.add(funcname);

                            Logs.infoLn("\t === Call to library function: " + funcname);
                            emulator.call(pcValue.getSym());
                            modelTrue.label = String.valueOf(Arithmetic.hexToInt(emulator.getEnv().register.get('l').getSym()));
                            modelTrue.prevLabel = prevLabel;
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        } else if (isGlibC) {
                            if (!callFunctions.contains(funcname)) callFunctions.add(funcname);
                            if (APIlist.contains(funcname)) {
                                Logs.infoLn("\t === Call to library function: " + funcname);
                                emulator.exec(funcname);
                            }

                            modelTrue.label = String.valueOf(Arithmetic.hexToInt(emulator.getEnv().register.get('l').getSym()));
                            modelTrue.prevLabel = prevLabel;
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                        else {
                            if (modelTrue != null && modelTrue.envData != null) {
                                Logs.infoLn("\t ==+ PC jump to: " + pcValue.getSym());
                                modelTrue.label = strLabel;
                                modelTrue.prevLabel = prevLabel;
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                            }
                            pcJump(modelTrue, modelFalse);
                        }
                    jumpTo = modelTrue.label;
                    }

                }



            } else if ("ldrh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrh(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrh(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("str".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.str(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.str(emulator.val(p0), newValue);
                        break;
                    case 2: //@ address mode: offset.  str r2, [r1, #2]
                        // Store the value found in R2 (0x03) to the memory address found in R1 plus 2.
                        // Base register (R1) unmodified.
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = newValue;
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue); //base register is unmodified
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("stmib".equals(opcode)) {
                // Store multiple increase before
                // addr = Rn + 4; for each Ri in params: addr = addr + 4; ri = M[addr]
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("{", "").replace("}", "").replace("!", "");
                }
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                BitVec baseAddr = emulator.val(p0);
                for (int i = 1; i < arrParams.length; i++) {
                    baseAddr = emulator.add(baseAddr, new BitVec(Configs.instructionSize));
                    Character pi = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[i]));
                    emulator.str(emulator.val(pi), baseAddr);
                }
            } else if ("strb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strb(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strh(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:

                        break;
                }
            }
            // Added instructions

        } else {
            Logs.infoLn("Error: Opcode is null!");
        }
    }

    protected static void singleExec(M0_Plus emulator, String prevLabel, AsmNode n) {
        String nLabel = n.getLabel();
        if (nLabel == null) System.exit(0);
        if (!nLabel.contains("+") && !nLabel.contains("-")) emulator.write('p', new BitVec(Integer.parseInt(nLabel)));
        Exporter.addAsm(nLabel + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams());
        Exporter.exportDot(Corana.dotOutput + ".dot");

        Character suffix = n.isUpdateFlag() ? 's' : null;
        String[] arrParams = Objects.requireNonNull(n.getParams()).split("\\,");
        if (n.getOpcode() != null) {
            String opcode = n.getOpcode();
            if ("b".equals(opcode) || "bl".equals(opcode) || "bx".equals(opcode)) {
                Character condSuffix = Mapping.condStrToChar.get(Objects.requireNonNull(n.getCondSuffix()).toUpperCase());
                String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                jumpFrom = nLabel;
                EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                String strLabel = null;
                Character charLabel = null;
                if (isConcreteLabel(arrParams[0])) {
                    strLabel = arrParams[0].contains("-") ? arrParams[0] :
                            String.valueOf(Arithmetic.hexToInt(arrParams[0].replace("#0x", "").replace("0x", "")));
                } else {
                    charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                }
                Map.Entry<EnvModel, EnvModel> envPair;
                switch (opcode) {
                    case "b":
                        envPair = emulator.b(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bl":
                        envPair = emulator.bl(Integer.parseInt(nLabel), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bx":
                        envPair = emulator.bx(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    default:
                        envPair = null;
                        break;
                }
                EnvModel modelTrue = envPair.getKey();
                EnvModel modelFalse = envPair.getValue();
                if (isConcreteLabel(arrParams[0])) {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = strLabel;
                        modelTrue.prevLabel = prevLabel;
                        labelToEnvModel.put(modelTrue.label, modelTrue);
                    }
                } else {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = modelTrue.envData.eval;
                        modelTrue.prevLabel = prevLabel;
                        if (modelTrue.label == null) {
                            int foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('l').getVal());
                            if (foundLabel != 0 && foundLabel % 4 == 0) {
                                modelTrue.label = String.valueOf(foundLabel);
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                                Logs.infoLn("\t-> Found the destination: " + foundLabel);
                            } else {
                                Logs.infoLn("\t-> Destination is undetectable.");
                            }
                        } else {
                            if (modelTrue.label.startsWith("#x")) {
                                modelTrue.label = String.valueOf(Arithmetic.hexToInt(modelTrue.label.replace("#x", "")));
                            }
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                    }
                }
                if (modelFalse != null && modelFalse.envData != null) {
                    modelFalse.label = nextInst(jumpFrom);
                    modelFalse.prevLabel = prevLabel;
                    labelToEnvModel.put(modelFalse.label, modelFalse);
                }
                decideToJump(modelTrue, modelFalse);
            } else if ("nop".equals(opcode)) {
                emulator.nop(suffix);
            } else if ("bic".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (im == null) {
                    emulator.bics(p0, p1, p2, suffix);
                } else {
                    emulator.bic(p0, p1, im, suffix);
                }
            } else if ("lsl".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsls(p0, p1, p2, im, suffix);
            } else if ("lsr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsrs(p0, p1, p2, im, suffix);
            } else if ("mul".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.muls(p0, p1, p2, suffix);
            } else if ("mvn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mvns(p0, p1, suffix);
                } else {
                    emulator.mvn(p0, im, suffix);
                }
            } else if ("mov".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                emulator.mov(p0, p1, im, suffix);
            } else if ("adc".equals(opcode)) {
                Integer im;
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams[2].contains("#")) {
                    im = SysUtils.normalizeNumInParam(arrParams[2].trim());
                    emulator.adc(p0, p1, im, suffix);
                } else {
                    Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    emulator.adcs(p0, p1, p2, suffix);
                }
            } else if ("and".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer p2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2And = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (p2 != null) {
                        emulator.and(p0, p1, p2, suffix);
                    } else {
                        emulator.ands(p0, p1, p2And, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.and(p0, p1, p2And, p2, extType, extraChar, extraNum, suffix);
                }
            } else if ("rsb".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.rsb(p0, p1, im, suffix);
                    } else {
                        emulator.rsbs(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.rsb(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("orr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.orr(p0, p1, im, suffix);
                    } else {
                        emulator.orrs(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.orr(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("eor".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.eor(p0, p1, im, suffix);
                    } else {
                        emulator.eors(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.eor(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("add".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.add(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.add(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }

            } else if ("sub".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.sub(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.sub(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmp".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    emulator.cmp(p0, p1, im, suffix);
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmp(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmn(p0, im, suffix);
                    } else {
                        emulator.cmn(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmn(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("svc".equals(opcode)) {
                Integer imSvc = arrParams[0].contains("#") ? SysUtils.normalizeNumInParam(arrParams[0].trim()) : null;
                emulator.svc(imSvc);
                gg();
            } else if ("pop".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.pop(c);
                }
            } else if ("push".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.push(c);
                }
            } else if ("ldrb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrb(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrb(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldr".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldr(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldr(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldrh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrh(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrh(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("str".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.str(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.str(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strb(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strh(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:

                        break;
                }
            }
        } else {
            Logs.infoLn("Error: Opcode is null!");
        }
    }

    protected static void singleExec(M3 emulator, String prevLabel, AsmNode n) {
        String nLabel = n.getLabel();
        if (nLabel == null) System.exit(0);
        if (!nLabel.contains("+") && !nLabel.contains("-")) emulator.write('p', new BitVec(Integer.parseInt(nLabel)));
        Exporter.addAsm(nLabel + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams());
        Exporter.exportDot(Corana.inpFile + ".dot");

        Character suffix = n.isUpdateFlag() ? 's' : null;
        String[] arrParams = Objects.requireNonNull(n.getParams()).split("\\,");
        if (n.getOpcode() != null) {
            String opcode = n.getOpcode();
            if ("b".equals(opcode) || "bl".equals(opcode) || "bx".equals(opcode)) {
                Character condSuffix = Mapping.condStrToChar.get(Objects.requireNonNull(n.getCondSuffix()).toUpperCase());
                String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                jumpFrom = nLabel;
                EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                String strLabel = null;
                Character charLabel = null;
                if (isConcreteLabel(arrParams[0])) {
                    strLabel = arrParams[0].contains("-") ? arrParams[0] :
                            String.valueOf(Arithmetic.hexToInt(arrParams[0].replace("#0x", "").replace("0x", "")));
                } else {
                    charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                }
                Map.Entry<EnvModel, EnvModel> envPair;
                switch (opcode) {
                    case "b":
                        envPair = emulator.b(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bl":
                        envPair = emulator.bl(Integer.parseInt(nLabel), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bx":
                        envPair = emulator.bx(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    default:
                        envPair = null;
                        break;
                }
                EnvModel modelTrue = envPair.getKey();
                EnvModel modelFalse = envPair.getValue();
                if (isConcreteLabel(arrParams[0])) {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = strLabel;
                        modelTrue.prevLabel = prevLabel;
                        labelToEnvModel.put(modelTrue.label, modelTrue);
                    }
                } else {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = modelTrue.envData.eval;
                        modelTrue.prevLabel = prevLabel;
                        if (modelTrue.label == null) {
                            int foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('l').getVal());
                            if (foundLabel != 0 && foundLabel % Configs.instructionSize == 0) {
                                modelTrue.label = String.valueOf(foundLabel);
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                                Logs.infoLn("\t-> Found the destination: " + foundLabel);
                            } else {
                                Logs.infoLn("\t-> Destination is undetectable.");
                            }
                        } else {
                            if (modelTrue.label.startsWith("#x")) {
                                modelTrue.label = String.valueOf(Arithmetic.hexToInt(modelTrue.label.replace("#x", "")));
                            }
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                    }
                }
                if (modelFalse != null && modelFalse.envData != null) {
                    modelFalse.label = nextInst(jumpFrom);
                    modelFalse.prevLabel = prevLabel;
                    labelToEnvModel.put(modelFalse.label, modelFalse);
                }
                decideToJump(modelTrue, modelFalse);
            } else if ("nop".equals(opcode)) {
                emulator.nop(suffix);
            } else if ("bic".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (im == null) {
                    emulator.bic(p0, p1, p2, suffix);
                } else {
                    emulator.bic(p0, p1, im, suffix);
                }
            } else if ("lsl".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsl(p0, p1, p2, im, suffix);
            } else if ("lsr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsr(p0, p1, p2, im, suffix);
            } else if ("asr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.asr(p0, p1, p2, im, suffix);
            } else if ("mul".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.mul(p0, p1, p2, suffix);
            } else if ("mvn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mvn(p0, p1, suffix);
                } else {
                    emulator.mvn(p0, im, suffix);
                }
            } else if ("mov".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mov(p0, p1, suffix);
                } else {
                    emulator.movw(p0, im, suffix);
                }
            } else if ("movw".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = SysUtils.normalizeNumInParam(arrParams[1].trim());
                emulator.movw(p0, im, suffix);
            } else if ("umull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.umull(p0, p1, p2, p3);
            } else if ("smull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.smull(p0, p1, p2, p3);
            } else if ("adc".equals(opcode)) {
                Integer im;
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams[2].contains("#")) {
                    im = SysUtils.normalizeNumInParam(arrParams[2].trim());
                    emulator.adc(p0, p1, im, suffix);
                } else {
                    Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    emulator.adc(p0, p1, p2, suffix);
                }
            } else if ("and".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer p2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2And = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (p2 != null) {
                        emulator.and(p0, p1, p2, suffix);
                    } else {
                        emulator.and(p0, p1, p2And, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.and(p0, p1, p2And, p2, extType, extraChar, extraNum, suffix);
                }
            } else if ("rsb".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.rsb(p0, p1, im, suffix);
                    } else {
                        emulator.rsb(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.rsb(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("orr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.orr(p0, p1, im, suffix);
                    } else {
                        emulator.orr(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.orr(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("eor".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.eor(p0, p1, im, suffix);
                    } else {
                        emulator.eor(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.eor(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("add".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.add(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.add(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("sub".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.sub(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.sub(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmp".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmp(p0, im, suffix);
                    } else {
                        emulator.cmp(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmp(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmn(p0, im, suffix);
                    } else {
                        emulator.cmn(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmn(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("svc".equals(opcode)) {
                Integer imSvc = arrParams[0].contains("#") ? SysUtils.normalizeNumInParam(arrParams[0].trim()) : null;
                emulator.svc(imSvc);
                gg();
            } else if ("pop".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.pop(c);
                }
            } else if ("push".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.push(c);
                }
            } else if ("ldrb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrb(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrb(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldr".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldr(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldr(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldrh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrh(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrh(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("str".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.str(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.str(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strb(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strh(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:

                        break;
                }
            }
        } else {
            Logs.infoLn("Error: Opcode is null!");
        }
    }

    protected static void singleExec(M4 emulator, String prevLabel, AsmNode n) {
        String nLabel = n.getLabel();
        if (nLabel == null) System.exit(0);
        if (!nLabel.contains("+") && !nLabel.contains("-")) emulator.write('p', new BitVec(Integer.parseInt(nLabel)));
        Exporter.addAsm(nLabel + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams());
        Exporter.exportDot(Corana.inpFile + ".dot");

        Character suffix = n.isUpdateFlag() ? 's' : null;
        String[] arrParams = Objects.requireNonNull(n.getParams()).split("\\,");
        if (n.getOpcode() != null) {
            String opcode = n.getOpcode();
            if ("b".equals(opcode) || "bl".equals(opcode) || "bx".equals(opcode)) {
                Character condSuffix = Mapping.condStrToChar.get(Objects.requireNonNull(n.getCondSuffix()).toUpperCase());
                String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                jumpFrom = nLabel;
                EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                String strLabel = null;
                Character charLabel = null;
                if (isConcreteLabel(arrParams[0])) {
                    strLabel = arrParams[0].contains("-") ? arrParams[0] :
                            String.valueOf(Arithmetic.hexToInt(arrParams[0].replace("#0x", "").replace("0x", "")));
                } else {
                    charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                }
                Map.Entry<EnvModel, EnvModel> envPair;
                switch (opcode) {
                    case "b":
                        envPair = emulator.b(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bl":
                        envPair = emulator.bl(Integer.parseInt(nLabel), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bx":
                        envPair = emulator.bx(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    default:
                        envPair = null;
                        break;
                }
                EnvModel modelTrue = envPair.getKey();
                EnvModel modelFalse = envPair.getValue();
                if (isConcreteLabel(arrParams[0])) {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = strLabel;
                        modelTrue.prevLabel = prevLabel;
                        labelToEnvModel.put(modelTrue.label, modelTrue);
                    }
                } else {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = modelTrue.envData.eval;
                        modelTrue.prevLabel = prevLabel;
                        if (modelTrue.label == null) {


                            int foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('l').getVal());
                            if (foundLabel != 0 && foundLabel % 4 == 0) {
                                modelTrue.label = String.valueOf(foundLabel);
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                                Logs.infoLn("\t-> Found the destination: " + foundLabel);
                            } else {
                                Logs.infoLn("\t-> Destination is undetectable.");
                            }
                        } else {
                            if (modelTrue.label.startsWith("#x")) {
                                modelTrue.label = String.valueOf(Arithmetic.hexToInt(modelTrue.label.replace("#x", "")));
                            }
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                    }
                }
                if (modelFalse != null && modelFalse.envData != null) {
                    modelFalse.label = nextInst(jumpFrom);
                    modelFalse.prevLabel = prevLabel;
                    labelToEnvModel.put(modelFalse.label, modelFalse);
                }
                decideToJump(modelTrue, modelFalse);
            } else if ("nop".equals(opcode)) {
                emulator.nop(suffix);
            } else if ("bic".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (im == null) {
                    emulator.bic(p0, p1, p2, suffix);
                } else {
                    emulator.bic(p0, p1, im, suffix);
                }
            } else if ("lsl".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsl(p0, p1, p2, im, suffix);
            } else if ("lsr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsr(p0, p1, p2, im, suffix);
            } else if ("asr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.asr(p0, p1, p2, im, suffix);
            } else if ("mul".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.mul(p0, p1, p2, suffix);
            } else if ("mvn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mvn(p0, p1, suffix);
                } else {
                    emulator.mvn(p0, im, suffix);
                }
            } else if ("mov".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                emulator.mov(p0, p1, im, suffix);
            } else if ("movw".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = SysUtils.normalizeNumInParam(arrParams[1].trim());
                emulator.movw(p0, im, suffix);
            } else if ("umull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.umull(p0, p1, p2, p3);
            } else if ("smull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.smull(p0, p1, p2, p3);
            } else if ("adc".equals(opcode)) {
                Integer im;
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams[2].contains("#")) {
                    im = SysUtils.normalizeNumInParam(arrParams[2].trim());
                    emulator.adc(p0, p1, im, suffix);
                } else {
                    Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    emulator.adc(p0, p1, p2, suffix);
                }
            } else if ("and".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer p2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2And = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (p2 != null) {
                        emulator.and(p0, p1, p2, suffix);
                    } else {
                        emulator.and(p0, p1, p2And, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.and(p0, p1, p2And, p2, extType, extraChar, extraNum, suffix);
                }
            } else if ("rsb".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.rsb(p0, p1, im, suffix);
                    } else {
                        emulator.rsb(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.rsb(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("orr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.orr(p0, p1, im, suffix);
                    } else {
                        emulator.orr(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.orr(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("eor".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.eor(p0, p1, im, suffix);
                    } else {
                        emulator.eor(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.eor(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("add".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.add(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.add(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("sub".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.sub(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.sub(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmp".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmp(p0, im, suffix);
                    } else {
                        emulator.cmp(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmp(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmn(p0, im, suffix);
                    } else {
                        emulator.cmn(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmn(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("svc".equals(opcode)) {
                Integer imSvc = arrParams[0].contains("#") ? SysUtils.normalizeNumInParam(arrParams[0].trim()) : null;
                emulator.svc(imSvc);
            } else if ("pop".equals(opcode) || "vpop".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.vpop(c);
                }
            } else if ("push".equals(opcode) || "vpush".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.vpush(c);
                }
            } else if ("ldrb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrb(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrb(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldr".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldr(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldr(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldrh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrh(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrh(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("str".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.str(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.str(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strb(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strh(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:

                        break;
                }
            }
        } else {
            Logs.infoLn("Error: Opcode is null!");
        }
    }

    protected static void singleExec(M7 emulator, String prevLabel, AsmNode n) {
        String nLabel = n.getLabel();
        if (nLabel == null) System.exit(0);
        if (!nLabel.contains("+") && !nLabel.contains("-")) emulator.write('p', new BitVec(Integer.parseInt(nLabel)));
        Exporter.addAsm(nLabel + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams());
        Exporter.exportDot(Corana.inpFile + ".dot");

        Character suffix = n.isUpdateFlag() ? 's' : null;
        String[] arrParams = Objects.requireNonNull(n.getParams()).split("\\,");
        if (n.getOpcode() != null) {
            String opcode = n.getOpcode();
            if ("b".equals(opcode) || "bl".equals(opcode) || "bx".equals(opcode)) {
                Character condSuffix = Mapping.condStrToChar.get(Objects.requireNonNull(n.getCondSuffix()).toUpperCase());
                String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                jumpFrom = nLabel;
                EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                String strLabel = null;
                Character charLabel = null;
                if (isConcreteLabel(arrParams[0])) {
                    strLabel = arrParams[0].contains("-") ? arrParams[0] :
                            String.valueOf(Arithmetic.hexToInt(arrParams[0].replace("#0x", "").replace("0x", "")));
                } else {
                    charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                }
                Map.Entry<EnvModel, EnvModel> envPair;
                switch (opcode) {
                    case "b":
                        envPair = emulator.b(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bl":
                        envPair = emulator.bl(Integer.parseInt(nLabel), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bx":
                        envPair = emulator.bx(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    default:
                        envPair = null;
                        break;
                }
                EnvModel modelTrue = envPair.getKey();
                EnvModel modelFalse = envPair.getValue();
                if (isConcreteLabel(arrParams[0])) {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = strLabel;
                        modelTrue.prevLabel = prevLabel;
                        labelToEnvModel.put(modelTrue.label, modelTrue);
                    }
                } else {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = modelTrue.envData.eval;
                        modelTrue.prevLabel = prevLabel;
                        if (modelTrue.label == null) {


                            int foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('l').getVal());
                            if (foundLabel != 0 && foundLabel % 4 == 0) {
                                modelTrue.label = String.valueOf(foundLabel);
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                                Logs.infoLn("\t-> Found the destination: " + foundLabel);
                            } else {
                                Logs.infoLn("\t-> Destination is undetectable.");
                            }
                        } else {
                            if (modelTrue.label.startsWith("#x")) {
                                modelTrue.label = String.valueOf(Arithmetic.hexToInt(modelTrue.label.replace("#x", "")));
                            }
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                    }
                }
                if (modelFalse != null && modelFalse.envData != null) {
                    modelFalse.label = nextInst(jumpFrom);
                    modelFalse.prevLabel = prevLabel;
                    labelToEnvModel.put(modelFalse.label, modelFalse);
                }
                decideToJump(modelTrue, modelFalse);
            } else if ("nop".equals(opcode)) {
                emulator.nop(suffix);
            } else if ("bic".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (im == null) {
                    emulator.bic(p0, p1, p2, suffix);
                } else {
                    emulator.bic(p0, p1, im, suffix);
                }
            } else if ("lsl".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsl(p0, p1, p2, im, suffix);
            } else if ("lsr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsr(p0, p1, p2, im, suffix);
            } else if ("asr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.asr(p0, p1, p2, im, suffix);
            } else if ("mul".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.mul(p0, p1, p2, suffix);
            } else if ("mvn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mvn(p0, p1, suffix);
                } else {
                    emulator.mvn(p0, im, suffix);
                }
            } else if ("mov".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                emulator.mov(p0, p1, im, suffix);
            } else if ("movw".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = SysUtils.normalizeNumInParam(arrParams[1].trim());
                emulator.movw(p0, im, suffix);
            } else if ("umull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.umull(p0, p1, p2, p3);
            } else if ("smull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.smull(p0, p1, p2, p3);
            } else if ("adc".equals(opcode)) {
                Integer im;
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams[2].contains("#")) {
                    im = SysUtils.normalizeNumInParam(arrParams[2].trim());
                    emulator.adc(p0, p1, im, suffix);
                } else {
                    Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    emulator.adc(p0, p1, p2, suffix);
                }
            } else if ("and".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer p2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2And = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (p2 != null) {
                        emulator.and(p0, p1, p2, suffix);
                    } else {
                        emulator.and(p0, p1, p2And, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.and(p0, p1, p2And, p2, extType, extraChar, extraNum, suffix);
                }
            } else if ("rsb".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.rsb(p0, p1, im, suffix);
                    } else {
                        emulator.rsb(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.rsb(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("orr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.orr(p0, p1, im, suffix);
                    } else {
                        emulator.orr(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.orr(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("eor".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.eor(p0, p1, im, suffix);
                    } else {
                        emulator.eor(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.eor(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("add".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.add(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.add(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("sub".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.sub(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.sub(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmp".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmp(p0, im, suffix);
                    } else {
                        emulator.cmp(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmp(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmn(p0, im, suffix);
                    } else {
                        emulator.cmn(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmn(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("svc".equals(opcode)) {
                Integer imSvc = arrParams[0].contains("#") ? SysUtils.normalizeNumInParam(arrParams[0].trim()) : null;
                emulator.svc(imSvc);
            } else if ("pop".equals(opcode) || "vpop".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.vpop(c);
                }
            } else if ("push".equals(opcode) || "vpush".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.vpush(c);
                }
            } else if ("ldrb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrb(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrb(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldr".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldr(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldr(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldrh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrh(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrh(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("str".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.str(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.str(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strb(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strh(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:

                        break;
                }
            }
        } else {
            Logs.infoLn("Error: Opcode is null!");
        }
    }

    protected static void singleExec(M33 emulator, String prevLabel, AsmNode n) {
        String nLabel = n.getLabel();
        if (nLabel == null) System.exit(0);
        if (!nLabel.contains("+") && !nLabel.contains("-")) emulator.write('p', new BitVec(Integer.parseInt(nLabel)));
        Exporter.addAsm(nLabel + " " + n.getOpcode() + n.getCondSuffix() + (n.isUpdateFlag() ? "s" : "") + " " + n.getParams());
        Exporter.exportDot(Corana.inpFile + ".dot");

        Character suffix = n.isUpdateFlag() ? 's' : null;
        String[] arrParams = Objects.requireNonNull(n.getParams()).split("\\,");
        if (n.getOpcode() != null) {
            String opcode = n.getOpcode();
            if ("b".equals(opcode) || "bl".equals(opcode) || "bx".equals(opcode)) {
                Character condSuffix = Mapping.condStrToChar.get(Objects.requireNonNull(n.getCondSuffix()).toUpperCase());
                String preCond = recentPop == null ? "" : recentPop.getKey().pathCondition;
                jumpFrom = nLabel;
                EnvModel thisEnvModel = new EnvModel(jumpFrom, preCond);
                labelToEnvModel.put(thisEnvModel.label, thisEnvModel);

                String strLabel = null;
                Character charLabel = null;
                if (isConcreteLabel(arrParams[0])) {
                    strLabel = arrParams[0].contains("-") ? arrParams[0] :
                            String.valueOf(Arithmetic.hexToInt(arrParams[0].replace("#0x", "").replace("0x", "")));
                } else {
                    charLabel = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                }
                Map.Entry<EnvModel, EnvModel> envPair;
                switch (opcode) {
                    case "b":
                        envPair = emulator.b(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bl":
                        envPair = emulator.bl(Integer.parseInt(nLabel), preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    case "bx":
                        envPair = emulator.bx(preCond, condSuffix, strLabel == null ? charLabel : strLabel);
                        break;
                    default:
                        envPair = null;
                        break;
                }
                EnvModel modelTrue = envPair.getKey();
                EnvModel modelFalse = envPair.getValue();
                if (isConcreteLabel(arrParams[0])) {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = strLabel;
                        modelTrue.prevLabel = prevLabel;
                        labelToEnvModel.put(modelTrue.label, modelTrue);
                    }
                } else {
                    if (modelTrue != null && modelTrue.envData != null) {
                        modelTrue.label = modelTrue.envData.eval;
                        modelTrue.prevLabel = prevLabel;
                        if (modelTrue.label == null) {
                            int foundLabel = Arithmetic.bitSetToInt(emulator.getEnv().register.get('l').getVal());
                            if (foundLabel != 0 && foundLabel % 4 == 0) {
                                modelTrue.label = String.valueOf(foundLabel);
                                labelToEnvModel.put(modelTrue.label, modelTrue);
                                Logs.infoLn("\t-> Found the destination: " + foundLabel);
                            } else {
                                Logs.infoLn("\t-> Destination is undetectable.");
                            }
                        } else {
                            if (modelTrue.label.startsWith("#x")) {
                                modelTrue.label = String.valueOf(Arithmetic.hexToInt(modelTrue.label.replace("#x", "")));
                            }
                            labelToEnvModel.put(modelTrue.label, modelTrue);
                        }
                    }
                }
                if (modelFalse != null && modelFalse.envData != null) {
                    modelFalse.label = nextInst(jumpFrom);
                    modelFalse.prevLabel = prevLabel;
                    labelToEnvModel.put(modelFalse.label, modelFalse);
                }
                decideToJump(modelTrue, modelFalse);
            } else if ("nop".equals(opcode)) {
                emulator.nop(suffix);
            } else if ("bic".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (im == null) {
                    emulator.bic(p0, p1, p2, suffix);
                } else {
                    emulator.bic(p0, p1, im, suffix);
                }
            } else if ("lsl".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsl(p0, p1, p2, im, suffix);
            } else if ("lsr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.lsr(p0, p1, p2, im, suffix);
            } else if ("asr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.asr(p0, p1, p2, im, suffix);
            } else if ("mul".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                emulator.mul(p0, p1, p2, suffix);
            } else if ("mvn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                if (im == null) {
                    emulator.mvn(p0, p1, suffix);
                } else {
                    emulator.mvn(p0, im, suffix);
                }
            } else if ("mov".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                emulator.mov(p0, p1, im, suffix);
            } else if ("movw".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = SysUtils.normalizeNumInParam(arrParams[1].trim());
                emulator.movw(p0, im, suffix);
            } else if ("umull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.umull(p0, p1, p2, p3);
            } else if ("smull".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                Character p3 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[3].trim()));
                emulator.smull(p0, p1, p2, p3);
            } else if ("adc".equals(opcode)) {
                Integer im;
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams[2].contains("#")) {
                    im = SysUtils.normalizeNumInParam(arrParams[2].trim());
                    emulator.adc(p0, p1, im, suffix);
                } else {
                    Character p2 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                    emulator.adc(p0, p1, p2, suffix);
                }
            } else if ("and".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer p2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2And = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (p2 != null) {
                        emulator.and(p0, p1, p2, suffix);
                    } else {
                        emulator.and(p0, p1, p2And, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.and(p0, p1, p2And, p2, extType, extraChar, extraNum, suffix);
                }
            } else if ("rsb".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.rsb(p0, p1, im, suffix);
                    } else {
                        emulator.rsb(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.rsb(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("orr".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.orr(p0, p1, im, suffix);
                    } else {
                        emulator.orr(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.orr(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("eor".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    if (im != null) {
                        emulator.eor(p0, p1, im, suffix);
                    } else {
                        emulator.eor(p0, p1, p2, suffix);
                    }
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.eor(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("add".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.add(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.add(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("sub".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                Integer im = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                if (arrParams.length == 3) {
                    emulator.sub(p0, p1, p2, im, suffix);
                } else {
                    String[] extArr = arrParams[3].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.sub(p0, p1, p2, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmp".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmp(p0, im, suffix);
                    } else {
                        emulator.cmp(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmp(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("cmn".equals(opcode)) {
                Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0].trim()));
                Integer im = arrParams[1].contains("#") ? SysUtils.normalizeNumInParam(arrParams[1].trim()) : null;
                Character p1 = arrParams[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1].trim()));
                if (arrParams.length == 2) {
                    if (im != null) {
                        emulator.cmn(p0, im, suffix);
                    } else {
                        emulator.cmn(p0, p1, suffix);
                    }
                } else {
                    String[] extArr = arrParams[2].split("\\s+");
                    String extType = extArr[0];
                    Integer extraNum = null;
                    Character extraChar = null;
                    if (extArr.length > 1) {
                        if (extArr[1].contains("#")) {
                            extraNum = Integer.parseInt(extArr[1].replace("#", ""));
                        } else {
                            extraChar = Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                        }
                    }
                    emulator.cmn(p0, p1, im, extType, extraChar, extraNum, suffix);
                }
            } else if ("svc".equals(opcode)) {
                Integer imSvc = arrParams[0].contains("#") ? SysUtils.normalizeNumInParam(arrParams[0].trim()) : null;
                emulator.svc(imSvc);
            } else if ("pop".equals(opcode) || "vpop".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.vpop(c);
                }
            } else if ("push".equals(opcode) || "vpush".equals(opcode)) {
                for (String p : arrParams) {
                    p = p.replace("{", "").replace("}", "");
                    Character c = Mapping.regStrToChar.get(SysUtils.normalizeRegName(p));
                    emulator.vpush(c);
                }
            } else if ("ldrb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrb(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrb(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrb(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldr".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldr(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldr(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldr(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("ldrh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.ldrh(p0, p1);
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.ldrh(p0, newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.ldrh(p0, newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("str".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.str(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.str(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.str(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strb".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strb(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strb(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:
                        break;
                }
            } else if ("strh".equals(opcode)) {
                int type;
                if (arrParams.length == 2) {
                    type = 0;
                } else {
                    String lastP = arrParams[arrParams.length - 1];
                    type = lastP.endsWith("]") ? 1 : (lastP.endsWith("!") ? 2 : 3);
                }
                for (int k = 0; k < arrParams.length; k++) {
                    arrParams[k] = arrParams[k].trim().replace("[", "").replace("]", "").replace("!", "");
                }
                switch (type) {
                    case 0:
                        Character p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        Character p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));
                        emulator.strh(emulator.val(p0), emulator.val(p1));
                        break;
                    case 1:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        Integer i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        Character p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        BitVec extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        BitVec newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            newValue = emulator.add(newValue, emulator.handleExtra(extValue, extType, extraChar, extraNum));
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        break;
                    case 2:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = emulator.add(newValue, extValue);
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = emulator.add(newValue, handledExt);
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    case 3:
                        p0 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[0]));
                        p1 = Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[1]));

                        i2 = arrParams[2].contains("#") ? SysUtils.normalizeNumInParam(arrParams[2].trim()) : null;
                        p2 = arrParams[2].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(arrParams[2].trim()));
                        extValue = (i2 == null) ? emulator.valCheckNeg(p2, arrParams[2].contains("-")) : new BitVec(i2);

                        newValue = emulator.valCheckNeg(p1, arrParams[1].contains("-"));
                        if (arrParams.length == 3) {
                            newValue = newValue;
                            extValue = emulator.add(newValue, extValue);
                        } else {
                            String[] extArr = arrParams[3].split("\\s+");
                            String extType = extArr[0];
                            Integer extraNum = null;
                            Character extraChar = null;
                            if (extArr.length > 1) {
                                extraNum = extArr[1].contains("#") ? Integer.parseInt(extArr[1].replace("#", "")) : null;
                                extraChar = extArr[1].contains("#") ? null : Mapping.regStrToChar.get(SysUtils.normalizeRegName(extArr[1]));
                            }
                            BitVec handledExt = emulator.handleExtra(extValue, extType, extraChar, extraNum);

                            newValue = newValue;
                            extValue = emulator.add(newValue, handledExt);
                        }
                        emulator.strh(emulator.val(p0), newValue);
                        emulator.write(p1, extValue);
                        break;
                    default:

                        break;
                }
            }
        } else {
            Logs.infoLn("Error: Opcode is null!");
        }
    }

    protected static void decideToFork(EnvModel modelShare, Emulator emulator) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(labelToEnvModel);
        Type type = new TypeToken<HashMap<String, EnvModel>>() {
        }.getType();
        HashMap<String, EnvModel> clonedMap = gson.fromJson(jsonString, type);
        if (modelShare != null && labelToEnvModel.containsKey(modelShare.label)) {
            if (modelShare.label != null) {
                processStack.push(Pair.of(new EnvModel(modelShare), clonedMap));
            }
        }
        if (processStack.empty()) gg();

        String jsonEmulator = gson.toJson(emulator.getEnv());
        Type typeEmu = new TypeToken<Environment>() {
        }.getType();
        Environment clonedEnv = gson.fromJson(jsonEmulator, typeEmu);
        if (modelShare != null && labelToEnvModel.containsKey(modelShare.label)) {
            //Add fork result of child process to register
            int randomId = (new Random()).nextInt(100);
            clonedEnv.register.set('0', new BitVec(0));
            forkEnv.push(clonedEnv);
        }
    }

    protected static void decideToWait(EnvModel modelShare, Emulator emulator) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(labelToEnvModel);
        Type type = new TypeToken<HashMap<String, EnvModel>>() {
        }.getType();
        HashMap<String, EnvModel> clonedMap = gson.fromJson(jsonString, type);
        if (modelShare != null && labelToEnvModel.containsKey(modelShare.label)) {
            if (modelShare.label != null) {
                processStack.push(Pair.of(new EnvModel(modelShare), clonedMap));
            }
        }
        if (processStack.empty()) gg();

        String jsonEmulator = gson.toJson(emulator.getEnv());
        Type typeEmu = new TypeToken<Environment>() {
        }.getType();
        Environment clonedEnv = gson.fromJson(jsonEmulator, typeEmu);
        if (modelShare != null && labelToEnvModel.containsKey(modelShare.label)) {
            waitEnv.push(clonedEnv);
        }
    }

    //labelToEnvModel: EnvModel is never changed???
    protected static void decideToJump(EnvModel modelTrue, EnvModel modelFalse) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(labelToEnvModel);
        Type type = new TypeToken<HashMap<String, EnvModel>>() {
        }.getType();
        HashMap<String, EnvModel> clonedMap = gson.fromJson(jsonString, type);
        if (modelFalse != null && labelToEnvModel.containsKey(modelFalse.label)) {
            if (modelFalse.label != null) {
                envStack.push(Pair.of(new EnvModel(modelFalse), clonedMap));
            }
        }
        if (modelTrue != null && labelToEnvModel.containsKey(modelTrue.label)) {
            if (modelTrue.label != null) {
                envStack.push(Pair.of(new EnvModel(modelTrue), clonedMap));
            }
        }
        if (envStack.empty()) gg();
        Map.Entry<EnvModel, HashMap<String, EnvModel>> model = envStack.pop();
        recentPop = model;
        jumpTo = model.getKey().label;
        labelToEnvModel = model.getValue();
        if ((modelTrue == null || modelTrue.label == null || !jumpTo.equals(modelTrue.label))
                && (modelFalse == null || modelFalse.label == null || !jumpTo.equals(modelFalse.label))) {
            String triggerPrevLabelTwoUnsat = prevInst(jumpTo);
            if (Integer.parseInt(triggerPrevLabelTwoUnsat) < 0) gg();
            Logs.infoLn("-----> Recursively roll back to the parent branch: " + jumpTo);
        }
    }

    protected static void pcJump(EnvModel modelTrue, EnvModel modelFalse) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(labelToEnvModel);
        Type type = new TypeToken<HashMap<String, EnvModel>>() {
        }.getType();
        HashMap<String, EnvModel> clonedMap = gson.fromJson(jsonString, type);
        if (modelTrue != null && labelToEnvModel.containsKey(modelTrue.label)) {
            if (modelTrue.label != null) {
                envStack.push(Pair.of(new EnvModel(modelTrue), clonedMap));
            }
        }
        if (envStack.empty()) gg();
        Map.Entry<EnvModel, HashMap<String, EnvModel>> model = envStack.pop();
        recentPop = model;
        jumpTo = model.getKey().label;
        labelToEnvModel = model.getValue();
        if ((modelTrue == null || modelTrue.label == null || !jumpTo.equals(modelTrue.label))
                && (modelFalse == null || modelFalse.label == null || !jumpTo.equals(modelFalse.label))) {
            String triggerPrevLabelTwoUnsat = prevInst(jumpTo);
            if (Integer.parseInt(triggerPrevLabelTwoUnsat) < 0) gg();
            Logs.infoLn("-----> Recursively roll back to the parent branch: " + jumpTo);
        }
    }


    public static boolean isARM(String inpFile) {
        String info = SysUtils.execCmd("file " + inpFile);
        return Objects.requireNonNull(info).contains("ARM");
    }

    protected static String nextInstLabel(String label) {
        //TODO: temporary skip external call
        if (label.contains("+")) {
            label = label.split("\\+")[0];
        }
        return label.contains("-") ? label.replace("-", "+") : String.valueOf(Integer.parseInt(label) + 2);
    }

    protected static boolean isFunctionCall(String jumpFrom, String jumpTo) {
        return (!ExternalCall.findFunctionName(jumpTo).equals(""));
    }

    protected static String nextInst(String label) {
        label = nextInstLabel(label);
        if (!nodeLabelToIndex.containsKey(label)) {
            label = nextInstLabel(label);
        }
        return label;
    }
    protected static String nextInstBySize(String label, int preSize) {
        if (label.contains("+")) {
            label = label.split("\\+")[0];
        }
        return label.contains("-") ? label.replace("-", "+") : String.valueOf(Integer.parseInt(label) + preSize);
    }

    protected static String prevInst(String label) {
        return label.contains("+") ? label.replace("+", "-") : String.valueOf(Integer.parseInt(label) - Configs.instructionSize);
    }

    protected static boolean isConcreteLabel(String label) {
        return label.contains("0x") || label.contains("-") || label.contains("+");
    }

    protected static void gg() {
        Logs.infoLn("-> Time elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
        if (!processStack.empty() && !forkEnv.empty() && processCount > 0) {
            //Corana.inpFile += "_fork";
            Logs.info(String.format("\t-> Run child process from %s\n", asmNodes.get(nodeLabelToIndex.get(forkFrom)).getAddress()));
            //Exporter.add(address + "," + newAddress + "," + countJumpedPair.get(pair) + "\n");

            Map.Entry<EnvModel, HashMap<String, EnvModel>> model = processStack.pop();
            Environment env = forkEnv.pop();
            labelToEnvModel = model.getValue();
            fork(env, forkFrom, forkFrom);
        } else {
            //System.exit(0);
            Thread.currentThread().interrupt();
        }
    }
}
