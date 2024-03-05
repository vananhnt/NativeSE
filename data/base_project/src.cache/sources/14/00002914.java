package libcore.reflect;

import gov.nist.core.Separators;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

/* loaded from: AnnotationMember.class */
public final class AnnotationMember implements Serializable {
    protected static final char ERROR = '!';
    protected static final char ARRAY = '[';
    protected static final char OTHER = '*';
    protected static final Object NO_VALUE = DefaultValues.NO_VALUE;
    protected final String name;
    protected final Object value;
    protected final char tag;
    protected transient Class<?> elementType;
    protected transient Method definingMethod;

    /* loaded from: AnnotationMember$DefaultValues.class */
    private enum DefaultValues {
        NO_VALUE
    }

    public AnnotationMember(String name, Object val) {
        this.name = name;
        this.value = val == null ? NO_VALUE : val;
        if (this.value instanceof Throwable) {
            this.tag = '!';
        } else if (this.value.getClass().isArray()) {
            this.tag = '[';
        } else {
            this.tag = '*';
        }
    }

    public AnnotationMember(String name, Object val, Class type, Method m) {
        this(name, val);
        this.definingMethod = m;
        if (type == Integer.TYPE) {
            this.elementType = Integer.class;
        } else if (type == Boolean.TYPE) {
            this.elementType = Boolean.class;
        } else if (type == Character.TYPE) {
            this.elementType = Character.class;
        } else if (type == Float.TYPE) {
            this.elementType = Float.class;
        } else if (type == Double.TYPE) {
            this.elementType = Double.class;
        } else if (type == Long.TYPE) {
            this.elementType = Long.class;
        } else if (type == Short.TYPE) {
            this.elementType = Short.class;
        } else if (type == Byte.TYPE) {
            this.elementType = Byte.class;
        } else {
            this.elementType = type;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AnnotationMember setDefinition(AnnotationMember copy) {
        this.definingMethod = copy.definingMethod;
        this.elementType = copy.elementType;
        return this;
    }

    public String toString() {
        if (this.tag == '[') {
            StringBuilder sb = new StringBuilder(80);
            sb.append(this.name).append("=[");
            int len = Array.getLength(this.value);
            for (int i = 0; i < len; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(Array.get(this.value, i));
            }
            return sb.append("]").toString();
        }
        return this.name + Separators.EQUALS + this.value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AnnotationMember) {
            AnnotationMember that = (AnnotationMember) obj;
            if (this.name.equals(that.name) && this.tag == that.tag) {
                if (this.tag == '[') {
                    return equalArrayValue(that.value);
                }
                if (this.tag == '!') {
                    return false;
                }
                return this.value.equals(that.value);
            }
            return false;
        }
        return false;
    }

    public boolean equalArrayValue(Object otherValue) {
        if ((this.value instanceof Object[]) && (otherValue instanceof Object[])) {
            return Arrays.equals((Object[]) this.value, (Object[]) otherValue);
        }
        Class type = this.value.getClass();
        if (type != otherValue.getClass()) {
            return false;
        }
        if (type == int[].class) {
            return Arrays.equals((int[]) this.value, (int[]) otherValue);
        }
        if (type == byte[].class) {
            return Arrays.equals((byte[]) this.value, (byte[]) otherValue);
        }
        if (type == short[].class) {
            return Arrays.equals((short[]) this.value, (short[]) otherValue);
        }
        if (type == long[].class) {
            return Arrays.equals((long[]) this.value, (long[]) otherValue);
        }
        if (type == char[].class) {
            return Arrays.equals((char[]) this.value, (char[]) otherValue);
        }
        if (type == boolean[].class) {
            return Arrays.equals((boolean[]) this.value, (boolean[]) otherValue);
        }
        if (type == float[].class) {
            return Arrays.equals((float[]) this.value, (float[]) otherValue);
        }
        if (type == double[].class) {
            return Arrays.equals((double[]) this.value, (double[]) otherValue);
        }
        return false;
    }

    public int hashCode() {
        int hash = this.name.hashCode() * 127;
        if (this.tag == '[') {
            Class type = this.value.getClass();
            if (type == int[].class) {
                return hash ^ Arrays.hashCode((int[]) this.value);
            }
            if (type == byte[].class) {
                return hash ^ Arrays.hashCode((byte[]) this.value);
            }
            if (type == short[].class) {
                return hash ^ Arrays.hashCode((short[]) this.value);
            }
            if (type == long[].class) {
                return hash ^ Arrays.hashCode((long[]) this.value);
            }
            if (type == char[].class) {
                return hash ^ Arrays.hashCode((char[]) this.value);
            }
            if (type == boolean[].class) {
                return hash ^ Arrays.hashCode((boolean[]) this.value);
            }
            if (type == float[].class) {
                return hash ^ Arrays.hashCode((float[]) this.value);
            }
            if (type == double[].class) {
                return hash ^ Arrays.hashCode((double[]) this.value);
            }
            return hash ^ Arrays.hashCode((Object[]) this.value);
        }
        return hash ^ this.value.hashCode();
    }

    public void rethrowError() throws Throwable {
        if (this.tag == '!') {
            if (this.value instanceof TypeNotPresentException) {
                TypeNotPresentException tnpe = (TypeNotPresentException) this.value;
                throw new TypeNotPresentException(tnpe.typeName(), tnpe.getCause());
            } else if (this.value instanceof EnumConstantNotPresentException) {
                EnumConstantNotPresentException ecnpe = (EnumConstantNotPresentException) this.value;
                throw new EnumConstantNotPresentException(ecnpe.enumType(), ecnpe.constantName());
            } else if (this.value instanceof ArrayStoreException) {
                ArrayStoreException ase = (ArrayStoreException) this.value;
                throw new ArrayStoreException(ase.getMessage());
            } else {
                Throwable error = (Throwable) this.value;
                StackTraceElement[] ste = error.getStackTrace();
                ByteArrayOutputStream bos = new ByteArrayOutputStream(ste == null ? 512 : (ste.length + 1) * 80);
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(error);
                oos.flush();
                oos.close();
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bis);
                Throwable error2 = (Throwable) ois.readObject();
                ois.close();
                throw error2;
            }
        }
    }

    public Object validateValue() throws Throwable {
        if (this.tag == '!') {
            rethrowError();
        }
        if (this.value == NO_VALUE) {
            return null;
        }
        if (this.elementType == this.value.getClass() || this.elementType.isInstance(this.value)) {
            return copyValue();
        }
        throw new AnnotationTypeMismatchException(this.definingMethod, this.value.getClass().getName());
    }

    public Object copyValue() throws Throwable {
        if (this.tag != '[' || Array.getLength(this.value) == 0) {
            return this.value;
        }
        Class type = this.value.getClass();
        if (type == int[].class) {
            return ((int[]) this.value).clone();
        }
        if (type == byte[].class) {
            return ((byte[]) this.value).clone();
        }
        if (type == short[].class) {
            return ((short[]) this.value).clone();
        }
        if (type == long[].class) {
            return ((long[]) this.value).clone();
        }
        if (type == char[].class) {
            return ((char[]) this.value).clone();
        }
        if (type == boolean[].class) {
            return ((boolean[]) this.value).clone();
        }
        if (type == float[].class) {
            return ((float[]) this.value).clone();
        }
        if (type == double[].class) {
            return ((double[]) this.value).clone();
        }
        return ((Object[]) this.value).clone();
    }
}