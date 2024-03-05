package org.apache.harmony.kernel.vm;

/* loaded from: StringUtils.class */
public final class StringUtils {
    private StringUtils() {
    }

    public static String combineStrings(Object[] list) {
        int listLength = list.length;
        switch (listLength) {
            case 0:
                return "";
            case 1:
                return (String) list[0];
            default:
                int strLength = 0;
                for (Object obj : list) {
                    strLength += ((String) obj).length();
                }
                StringBuilder sb = new StringBuilder(strLength);
                for (Object obj2 : list) {
                    sb.append(obj2);
                }
                return sb.toString();
        }
    }
}