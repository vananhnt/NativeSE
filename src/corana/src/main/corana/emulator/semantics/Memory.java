package main.corana.emulator.semantics;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.*;
import main.corana.emulator.taint.TaintModel;
import main.corana.executor.Configs;
import main.corana.executor.DBDriver;
import main.corana.external.connector.ArithmeticUtils;
import main.corana.external.handler.JNIEnvHelper;
import main.corana.pojos.BitVec;
import main.corana.utils.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.corana.external.handler.JNIEnvHelper.addHex;

public class Memory {
    private final HashMap<String, BitVec> memory;
    private final int length;

    public Memory(int length) {
        this.memory = new HashMap<>();
        this.length = length;
    }

    public void put(BitVec address, BitVec value) {
        memory.put(address.toString().trim(), value);
        DBDriver.addMemoryDocument(address.getSym(), value.getSym());
    }

    public void put(String address, BitVec value) {
        memory.put(address.trim(), value);
    }

    public static void loadMemory() {
        DBDriver.startConnection("tmp");
    }
    public static void loadMemory(String filePath) {
        String[] nparts = filePath.split("[\\/]");
        String dbname = nparts[nparts.length - 1];
        String colname = dbname.length() < 6 ? "col_" + dbname : "col_" + dbname.substring(0, 6);
        DBDriver.startConnection(colname);
        Logs.infoLn(" + Parsing " + filePath + " ...");
        String disassembleCmd = "arm-none-eabi-objdump -D -S ";
        String execResult = SysUtils.execCmd(disassembleCmd + filePath);
        if (execResult == null) {
            Logs.infoLn("-> Parsing binary file error !");
        }
        String[] resultLines = execResult.split("\n");
        HashMap<String, BitVec> mem = new HashMap<>();
        String funcLabel;
        for (String line : resultLines) {
            line = line.trim().replaceAll(" +", " ");
            if (line.contains(";")) {
                line = line.split("\\;")[0];
            }
            if (line.contains("\t")) {
                line = line.replace("\t", " ");
                line = line.replaceAll(" +", " ");
                String[] parts = line.split("\\:");
                String label = parts[0];
                String[] contents = parts[1].split("\\s+");
                if (contents.length >= 2) {
                    if (contents[1].matches("-?[0-9a-fA-F]+")) { //contents[1] is address in hex
                        if (parts[1].contains("<")) {
                            funcLabel = parts[1].contains("+") ? parts[1].substring(parts[1].indexOf('<') + 1, parts[1].indexOf('+')) : parts[1].substring(parts[1].indexOf('<') + 1, parts[1].indexOf('>'));
                            DBDriver.addMemoryDocument(label, contents[1], funcLabel);
                        } else {
                            DBDriver.addMemoryDocument(label, contents[1]);
                        }
                    }
                }
            }
        }
        // update memory after resolve dynamic linking
        loadDynamicLink(filePath);
        // set up initial contents in the stack
        setupStack();
        Logs.infoLn();

    }

    public static void loadEmptyMemory() {
    }
    public static void loadSOMemory(String filePath) {
        String[] nparts = filePath.split("[\\/]");
        String dbname = nparts[nparts.length - 1];
        String colname = dbname.length() < 6 ? "col_" + dbname : "col_" + dbname.substring(0, 6);
        DBDriver.startConnection(colname);
        Logs.infoLn(" + Parsing " + filePath + " ...");
        String disassembleCmd = "arm-none-eabi-objdump -D -S ";
        String execResult = SysUtils.execCmd(disassembleCmd + filePath);
        if (execResult == null) {
            Logs.infoLn("-> Parsing binary file error !");
        }
        String[] resultLines = execResult.split("\n");
        HashMap<String, BitVec> mem = new HashMap<>();
        String funcLabel = null;
        for (String line : resultLines) {
            line = line.trim().replaceAll(" +", " ");
            if (line.contains(";")) {
                line = line.split("\\;")[0];
            }
            if (line.contains("\t")) {
                line = line.replace("\t", " ");
                line = line.replaceAll(" +", " ");
                String[] parts = line.split("\\:");
                String label = parts[0];
                String[] contents = parts[1].split("\\s+");
                if (contents.length >= 2) {
                    if (contents[1].matches("-?[0-9a-fA-F]+")) { //contents[1] is address in hex
                        if (parts[1].contains("<") && parts[1].contains(">")) {
                            //funcLabel = parts[1].contains("+") ? parts[1].substring(parts[1].indexOf('<') + 1, parts[1].indexOf('+')) : parts[1].substring(parts[1].indexOf('<') + 1, parts[1].indexOf('>'));
                              funcLabel = parts[1].contains("+") ? "" :  parts[1].substring(parts[1].indexOf('<') + 1, parts[1].indexOf('>'));
//                            Address ad =BinParser.getProgram().getAddressFactory().getAddress(contents[1]);
//                            Function func = BinParser.getProgram().getFunctionManager().getFunctionAt(ad);
            //                if (func != null) {
                            if (funcLabel != null) {
                                //funcLabel = ((Function) func).getName();
                                DBDriver.addMemoryDocument(label, contents[1], funcLabel);
                            } else {
                                DBDriver.addMemoryDocument(label, contents[1]);
                            }
                        } else {
                            DBDriver.addMemoryDocument(label, contents[1]);
                        }
                    }
                }
            }
        }
        for (Long key : GhidraRun.getGotSection().keySet()) {
            String addressHex = Arithmetic.intToHex(key - 65536);
            String[] contents = GhidraRun.getGotSection().get(key).split("<");
            String value = Arithmetic.intToHex(Arithmetic.hexToInt(contents[0])  - 65536);
            if (DBDriver.getValueOrNull(addressHex) != null) {
                DBDriver.updateMemoryDocument(addressHex, value);
            } else {
                DBDriver.addMemoryDocument(addressHex, value, contents[1].replace(">", ""));
            }
        }

        // update memory after resolve dynamic linking
        loadDynamicLink(filePath);
        // set up initial contents in the stack
        setupStack();
        Logs.infoLn();
        loadJNIEnv();
    }

    private static void loadJNIEnv() {
        try {
            List<String[]> interfaces = FileUtils.readCSVResource("jnienv-offset.csv");
            // Skip i = 0 since first line is header
            for (int i = 1; i < interfaces.size(); i++) {
                String[] line = interfaces.get(i);
                //"Offset","Length","Mnemonic","DataType","Name","Comment"
                String offset = line[0].replace("0x", "#x");
                String offsetFull = SysUtils.normalizeNumInHex(offset);
                String methodName = line[4];
                String realPosition = JNIEnvHelper.addHex(Configs.jniOffset, offsetFull);
                DBDriver.updateResolveJump(realPosition, SysUtils.getAddressFull(realPosition), "JNIEnv_" + methodName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupTaintMemory(TaintModel taintModel) {
        for (String offset : taintModel.memory.memoryTaint.keySet()) {
            Memory.set(Arithmetic.fromHexStr(offset), new BitVec(taintModel.memory.memory.get(offset)));
            if (taintModel.memory.memoryTaint.get(offset)) Memory.taint(offset);
        }
    }

    public static void setupStack() {
        // Initialize the value at the stack's top as 0x01
        // argc = 1; args.length = 0
        String currentAddress = Configs.topStack;
        DBDriver.addMemoryDocument(currentAddress, "#x00000001");

        currentAddress = SysUtils.getNextAdress(currentAddress);
        DBDriver.addMemoryDocument(currentAddress, SysUtils.addSymVar());

        currentAddress = SysUtils.getNextAdress(currentAddress);
        DBDriver.addMemoryDocument(currentAddress, "#x00000000");

        for (int i = 0; i < 17; i++) {
            currentAddress = SysUtils.getNextAdress(currentAddress);
            DBDriver.addMemoryDocument(currentAddress, SysUtils.addSymVar());
        }
        currentAddress = SysUtils.getNextAdress(currentAddress);
        DBDriver.addMemoryDocument(currentAddress, "#x00000000");
    }

    /**
     * Resolve dynamic linking
     * @param binpath
     */
    private static void loadDynamicLink(String binpath) {
        String tbCmd = "arm-none-eabi-objdump -t " + binpath;
        String exRes1 = SysUtils.execCmd(tbCmd);
        String[] resultLines1 = exRes1.split("\n");

        HashMap<String, String> symTable = new HashMap<>();
        for (String line : resultLines1) {
            String[] contents = line.split("\\s+");
            if (contents.length >= 6) {
                if (!symTable.containsKey(contents[0])) {
                    //symTable.put(contents[0].trim(), contents[contents.length - 1].trim()); // e.g. 00014fa0 printf
                    symTable.put(contents[contents.length - 1].trim(), contents[0].trim()); //e.g. printf 14fa0
                } else {
                    if (!contents[5].contains("_") && !contents[5].equals(".hidden")) {
                        //symTable.put(contents[0].trim(), contents[5].trim());
                        symTable.put(contents[5].trim(), contents[0].trim());
                    }
                }
            }
        }
        String hex = "000000";
        String functionName = "";

        // Resolve .got section
        String objCmd = "arm-none-eabi-objdump -D -S -R --section=.got " + binpath;
        String exRes = SysUtils.execCmd(objCmd);
        String[] resultLines = exRes.split("\n");

        String functionPtr = "@@Base";
        String label = "R_ARM_JUMP_SLOT";
        for (String line : resultLines) {
            if (line.contains("JUMP_SLOT")) {
                hex = line.split(":")[0].trim();
                functionName = line.split(":")[1].split("\t")[1].replace("@@Base", "").replace("@GLIBC_2.4","").trim();
                if (symTable.containsKey(functionName)) {
                    functionPtr = symTable.get(functionName);
                    DBDriver.updateResolveJump(hex, functionPtr, functionName);
                } else {
                    DBDriver.updateResolveJump(hex, functionName);
                }
            }
        }
        // Resolve .plt section
        String pltCmd = "arm-none-eabi-objdump -D -S -R --section=.plt " + binpath + " | awk '(NF==2)'";
        String pltEx = SysUtils.execCmd(pltCmd);
        String[] pltResultLines = pltEx.split("\n");
        String pltPtr = "@plt";
        for (String line: pltResultLines) {
            line = line.replace(":", "").trim();
            hex = line.split("<")[0].trim();

            functionName = line.split("\\s+|\t")[1].replace("<", "").replace(">", "").replace("@plt", "");
            //functionName = functionName + "_EXTERNAL";
            if (symTable.containsKey(functionName)) {
                pltPtr = symTable.get(functionName);
                DBDriver.updateResolveJump(hex, pltPtr, functionName);
            } else {
                DBDriver.updateResolveJump(hex, functionName);
            }
        }

        //Resolve .got section
        for (Long key : GhidraRun.getGotSection().keySet()) {
            String addressHex = Arithmetic.intToHex(key);
        }
    }

    public static BitVec get(BitVec address) {
        //Address is in hex
        BitVec result;
        String key = SysUtils.getAddressValue(address.getSym().trim());
        String findRes = SysUtils.getAddressFull(DBDriver.getValue(key)); //hex value
        result = findRes.matches("-?[0-9a-fA-F]+") ? Arithmetic.fromHexStr(findRes) : new BitVec(findRes);
        result.taint = address.taint;
        return result;
        //return memory.containsKey(key) ? memory.get(key) : new BitVec(0);
    }

    /*
        Get the value at an address (address can contain #x)
     */
    public static BitVec get(String address) {
        String findRes = SysUtils.getAddressFull(DBDriver.getValue(SysUtils.getAddressValue(address))); //hex value

        String taintStat = DBDriver.getTaintStatus(SysUtils.getAddressValue(address));
        BitVec resultBitVec = findRes.matches("-?[0-9a-fA-F]+") ? Arithmetic.fromHexStr(findRes) : new BitVec(findRes);
        if (taintStat.equals("true")) resultBitVec.taint();
        return resultBitVec;
        //return memory.containsKey(key) ? memory.get(key) : new BitVec(0);
    }

    public static void set(BitVec address, BitVec value) {
        DBDriver.updateMemoryDocument(SysUtils.getAddressValue(address.getSym()), value.getSym());
    }

    public static void taint(String address) {
        DBDriver.taintMemoryDocument(SysUtils.getAddressValue(address));
        Logs.infoLn("+++ Taint memory at " + address);
        //System.out.println("Taint memory #" + SysUtils.getAddressValue(address));
    }
    public static void sanitize(String address) {
        DBDriver.sanitizeMemoryDocument(SysUtils.getAddressValue(address));
    }
    @Override
    public String toString() {
        MyStr result = new MyStr("+ Memory:\n");
        //Logs.infoLn(memory.size());
        if (memory != null && memory.size() > 0) {
            for (String k : memory.keySet()) {
                result.append("\t- ", k, " : ", memory.get(k).toString(), "\n");
            }
        }
        return result.value();
    }

    /*
     SETTERS
     */
    public void setTextReference(BitVec atAdd, String text) {
    }

    public void setStringReference(BitVec atAdd, String text) {
    }

    public void setPointer(BitVec add, Object p) {
        //int val = ((IntByReference) p).getValue();
        //long val =
        //memory.put(add.getSym(), new BitVec(val));
    }

    public void setByte(BitVec add, byte val) {
    }

    public void setInt(BitVec add, int val) {
    }

    public void setIntReference(BitVec atAdd, int val) {
        BitVec value = new BitVec(val);
        DBDriver.updateMemoryDocument(atAdd.getSym(), value.getSym());
    }

    public void setNativeLong(BitVec atAdd, NativeLong val) {

    }

    public void setNativeLongReference(BitVec atAdd, NativeLong val) {

    }

    public void setShortReference(BitVec atAdd, short val) {
    }

    public void setLongReference(BitVec atAdd, long val) {

    }

    public void setArray(BitVec add, int size, Object[] arr) {

    }

    public void setByteArray(BitVec add, int size, byte[] arr) {
        int intSize = Configs.getIntSize(); // bytes
        for (int i = 0; i < size; i++) {
            String word = add.getSym();
            BitVec value = new BitVec(arr[i]);
            DBDriver.updateMemoryDocument(word, value.getSym());
            add = add.add(intSize);
        }
    }

    public void setIntArray(BitVec add, int size, int[] arr) {
        int intSize = Configs.getIntSize(); // bytes
        for (int i = 0; i < size; i++) {
            String word = add.getSym();
            BitVec value = new BitVec(arr[i]);
            DBDriver.updateMemoryDocument(word, value.getSym());
            add = add.add(intSize);
        }
    }

    public void setShortArray(BitVec add, int size, short[] arr) {

    }

    public void setBuffer(BitVec add, int size, char[] buf) {
        int intSize = Configs.getIntSize(); // bytes
        for (int i = 0; i < size; i++) {
            String word = add.getSym();
            BitVec value = new BitVec((int) buf[i]);
            DBDriver.updateMemoryDocument(word, value.getSym());
            add = add.add(intSize);
        }
    }

    /*
     GETTERS
     */
    public String getTextFromReference(BitVec atAddress) {
        String text = "";
        boolean flag = true;
        String word = atAddress.getSym();
        while (flag) {
            String memValue = DBDriver.getValue(word);
            String nextText = HexToASCII(memValue);
            if (DBDriver.getValue(word).contains("00") || memValue.length() < 8) flag = false;
            text += nextText;
            word = Arithmetic.intToHex(Arithmetic.hexToInt(word) + Configs.wordSize);
        }
        return text;
    }

    private String HexToASCII(String memstr) {
        StringBuilder result = new StringBuilder();
        for (int i = memstr.length() - 1; i > 0; i -= 2) {
            String hexStr = memstr.substring(i - 1, i + 1);
            if (hexStr.equals("00")) break;
            char c = (char) Integer.parseInt(hexStr, 16);
            result.append(c);
        }
        return result.toString();
    }

    public String getStringFromReference(BitVec add) {
        return getTextFromReference(add);
    }

    public Object[] getArray(BitVec atAddress, int size) {
        return new Object[size];
    }

    public byte[] getByteArray(BitVec atAddress, int size) {
        byte[] arr = new byte[size];
        int intSize = Configs.getIntSize(); // bytes
        for (int i = 0; i < size; i++) {
            String word = atAddress.getSym();
            String memValue = DBDriver.getValue(word);
            arr[i] = (byte) Arithmetic.hexToInt(memValue);
            atAddress = atAddress.add(intSize);
        }
        return arr;
    }

    public short[] getShortArray(BitVec atAddress, int size) {
        return null;
    }

    public int[] getIntArray(BitVec atAddress, int size) {
        int[] arr = new int[size];
        int intSize = Configs.getIntSize(); // bytes
        for (int i = 0; i < size; i++) {
            String word = atAddress.getSym();
            String memValue = DBDriver.getValue(word);
            arr[i] = (int) Arithmetic.hexToInt(memValue);
            atAddress = atAddress.add(intSize);
        }
        return arr;
    }

    public char[] getBuffer(BitVec atAddress, int size) {
        char[] arr = new char[size];
        int intSize = Configs.getIntSize(); // bytes
        for (int i = 0; i < size; i++) {
            String word = atAddress.getSym();
            String memValue = DBDriver.getValue(word);
            arr[i] = (char) Arithmetic.hexToInt(memValue);
            atAddress = atAddress.add(intSize);
        }
        return arr;
    }

    //for 32bit machine
    public BitVec getWordMemoryValue(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValueOrNull(word);
        if (memValue == null) {
            return new BitVec(SysUtils.addSymVar());
        }
        return Arithmetic.fromHexStr(memValue);
    }

    public LongByReference getPointer(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
//        Pointer ptr = new com.sun.jna.Memory(8);
//        ptr.setLong(0, Arithmetic.hexToInt(memValue));
        return new LongByReference((int) Arithmetic.hexToInt(word));
    }

    public NativeLongByReference getPointerByRef(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        NativeLongByReference ref = new NativeLongByReference(new NativeLong(Arithmetic.hexToInt(memValue)));
        return ref;
    }

    public IntByReference getIntRef(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        //String haftWord = memValue.substring(Configs.wordSize-Configs.getIntSize(), Configs.wordSize);
        IntByReference ref = new IntByReference((int) Arithmetic.hexToInt(memValue));
        return ref;
    }

    public int getIntFromReference(BitVec atAddress) {
        String word = atAddress.getSym();
        try {
            String memValue = DBDriver.getValue(word);
            return (int) Arithmetic.hexToInt(memValue);
        } catch (Exception e) {
            String address = atAddress.getSym();
            String hexStr = address;
            if (address.charAt(0) == '#') {
                hexStr = Arithmetic.intToHex(Arithmetic.hexToInt(address));
            }
            String memValue = memory.get("0x" + hexStr).getSym();
            return (int) Arithmetic.hexToInt(memValue);
        }

    }

    public NativeLong getNativeLongFromReference(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        return new NativeLong(Arithmetic.hexToInt(memValue));
    }

    public NativeLongByReference getNativeLongRef(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        return new NativeLongByReference(new NativeLong(Arithmetic.hexToInt(memValue)));
    }

    public long getLongFromReference(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        return Arithmetic.hexToInt(memValue);
    }

    public short getShortFromReference(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        return (short) Arithmetic.hexToInt(memValue);
    }

    public ShortByReference getShortRef(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        return new ShortByReference((short) Arithmetic.hexToInt(memValue));
    }

    public FloatByReference getFloatRef(BitVec atAddress) {
        String word = atAddress.getSym();
        String memValue = DBDriver.getValue(word);
        return new FloatByReference((float) Arithmetic.hexToInt(memValue));
    }

    public int getInt(BitVec atAddress) {
        String word = atAddress.getSym();
        return (int) Arithmetic.hexToInt(word);
    }

    public float getFloat(BitVec atAddress) {
        String word = atAddress.getSym();
        return (float) Arithmetic.hexToInt(word);
    }

    public short getShort(BitVec atAddress) {
        String word = atAddress.getSym();
        return (short) Arithmetic.hexToInt(word);
    }

    public byte getByte(BitVec atAddress) {
        String word = atAddress.getSym();
        return (byte) Arithmetic.hexToInt(word);
    }

    public double getDouble(BitVec address) {
        String word = address.getSym();
        return (int) Arithmetic.hexToInt(word);
    }

    public DoubleByReference getDoubleRef(BitVec address) {
        String word = address.getSym();
        return new DoubleByReference((double) Arithmetic.hexToInt(word));
    }

    public void setDouble(BitVec add, double val) {
    }

    public void setFloat(BitVec add, float val) {
    }

    public NativeLong getNativeLong(BitVec atAddress) {
        String word = atAddress.getSym();
        return new NativeLong(Arithmetic.hexToInt(word));
    }

    public long getLong(BitVec atAddress) {
        String word = atAddress.getSym();
        return Arithmetic.hexToInt(word);
    }

}