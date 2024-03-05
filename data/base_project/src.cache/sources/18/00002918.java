package libcore.reflect;

import gov.nist.javax.sip.parser.TokenNames;
import java.lang.reflect.Array;

/* loaded from: InternalNames.class */
public final class InternalNames {
    private InternalNames() {
    }

    public static Class<?> getClass(ClassLoader classLoader, String internalName) {
        if (internalName.startsWith("[")) {
            Class<?> componentClass = getClass(classLoader, internalName.substring(1));
            return Array.newInstance(componentClass, 0).getClass();
        } else if (internalName.equals("Z")) {
            return Boolean.TYPE;
        } else {
            if (internalName.equals("B")) {
                return Byte.TYPE;
            }
            if (internalName.equals(TokenNames.S)) {
                return Short.TYPE;
            }
            if (internalName.equals(TokenNames.I)) {
                return Integer.TYPE;
            }
            if (internalName.equals("J")) {
                return Long.TYPE;
            }
            if (internalName.equals(TokenNames.F)) {
                return Float.TYPE;
            }
            if (internalName.equals("D")) {
                return Double.TYPE;
            }
            if (internalName.equals(TokenNames.C)) {
                return Character.TYPE;
            }
            if (internalName.equals(TokenNames.V)) {
                return Void.TYPE;
            }
            String name = internalName.substring(1, internalName.length() - 1).replace('/', '.');
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                NoClassDefFoundError error = new NoClassDefFoundError(name);
                error.initCause(e);
                throw error;
            }
        }
    }

    public static String getInternalName(Class<?> c) {
        if (c.isArray()) {
            return '[' + getInternalName(c.getComponentType());
        }
        if (c == Boolean.TYPE) {
            return "Z";
        }
        if (c == Byte.TYPE) {
            return "B";
        }
        if (c == Short.TYPE) {
            return TokenNames.S;
        }
        if (c == Integer.TYPE) {
            return TokenNames.I;
        }
        if (c == Long.TYPE) {
            return "J";
        }
        if (c == Float.TYPE) {
            return TokenNames.F;
        }
        if (c == Double.TYPE) {
            return "D";
        }
        if (c == Character.TYPE) {
            return TokenNames.C;
        }
        if (c == Void.TYPE) {
            return TokenNames.V;
        }
        return 'L' + c.getName().replace('.', '/') + ';';
    }
}