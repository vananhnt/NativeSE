package main.corana.external.handler;

import main.corana.executor.BinParser;
import main.corana.pojos.BitVec;
import main.corana.utils.Arithmetic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExternalCall {

    // statically linked
    public static boolean isExternalFucntion(String functionSym) {
        // add all function interfaces to the database
        // query function name
        List<String> list = Arrays.stream(APIStub.class.getMethods()).map(s -> s.getName()).collect(Collectors.toList());
        return list.contains(findFunctionName(functionSym));
    }

    public static boolean isLibraryC(String funcName) {
        boolean result = false;
        List<String> list = Arrays.stream(APIStub.class.getMethods()).map(s -> s.getName()).collect(Collectors.toList());
        List<String> prefixes = Arrays.asList("__aeabi_", "__android_log_", "__libc_");
        for (String e : prefixes) {
            funcName = funcName.replace(e, "");
        }
        result = list.contains(funcName);
        return result;
    }

    public static String findFunctionName(String jmpAddress) {
        // find Function name from jmpAddress
        jmpAddress = jmpAddress.contains("-") ? jmpAddress.substring(0,jmpAddress.indexOf("-")) : jmpAddress;
        List<String> prefixes = Arrays.asList("__libc");
        String fullSym = jmpAddress.replace("#0x", "");
        String aboveSym = Arithmetic.intToHex(Arithmetic.hexToInt(fullSym) - 1);
        String belowSym = Arithmetic.intToHex(Arithmetic.hexToInt(fullSym) + 1);

        while (fullSym.length() < 8) {
            fullSym = "0" + fullSym;
        }

        HashMap<String, String> tbl = BinParser.getSymbolTable();
        String res = "";

        if (tbl.containsKey(fullSym)) {
            res = tbl.get(fullSym);
        } else if (tbl.containsKey(belowSym)) {
            res = tbl.get(belowSym);
        } else if (tbl.containsKey(aboveSym)) {
            res = tbl.get(aboveSym);
        }
        return res;
    }

    public static String getStringParam(BitVec memoryValue) {
        return "";
    }

}
