package main.corana.executor;

import main.corana.emulator.cortex.*;
import main.corana.emulator.semantics.Environment;
import main.corana.enums.Variation;
import main.corana.utils.Logs;

//implements Runnable
public class Process {
    Variation variation;
    Environment env;
    String genesis;
    String startAddress;

    public Process(Variation v, Environment e, String label, String addr) {
        variation = v;
        env = e;
        genesis = label;
        startAddress = addr;
    }

    public void run() {
        //File file = new File("./stream_"+Corana.inpFile+"_"+Arithmetic.intToHex(Integer.valueOf(startAddress))+".txt");
        try {
//          PrintStream stream = new PrintStream(file);
            // TODO: Start from asmNodes.get(_start), not the first node
            if (variation == Variation.M0) {
                Executor.execFrom(new M0(env), genesis, startAddress);
                //execFrom(new M0(env), genesis.label, String.valueOf(Arithmetic.hexToInt("0000e854")));
            } else if (variation == Variation.M0_PLUS) {
                Executor.execFrom(new M0_Plus(env), genesis, startAddress);
            } else if (variation == Variation.M3) {
                Executor.execFrom(new M3(env), genesis, startAddress);
            } else if (variation == Variation.M4) {
                Executor.execFrom(new M4(env), genesis, startAddress);
            } else if (variation == Variation.M7) {
                Executor.execFrom(new M7(env), genesis, startAddress);
            } else if (variation == Variation.M33) {
                Executor.execFrom(new M33(env), genesis, startAddress);
            } else {
                Logs.infoLn("-> Unsupported ARM Variation.");
                return;
            }
            Executor.gg();
        } catch (Exception e) {
            //Thread.currentThread().interrupt();
            Executor.gg();
        }
        //Logs.closeLog();
    }
}
