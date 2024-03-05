package java.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.WeakHashMap;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ObjectStreamClass.class */
public class ObjectStreamClass implements Serializable {
    public static final ObjectStreamField[] NO_FIELDS = null;

    ObjectStreamClass() {
        throw new RuntimeException("Stub!");
    }

    public Class<?> forClass() {
        throw new RuntimeException("Stub!");
    }

    public ObjectStreamField getField(String name) {
        throw new RuntimeException("Stub!");
    }

    public ObjectStreamField[] getFields() {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public long getSerialVersionUID() {
        throw new RuntimeException("Stub!");
    }

    public static ObjectStreamClass lookup(Class<?> cl) {
        throw new RuntimeException("Stub!");
    }

    public static ObjectStreamClass lookupAny(Class<?> cl) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.io.ObjectStreamClass$1  reason: invalid class name */
    /* loaded from: ObjectStreamClass$1.class */
    static class AnonymousClass1 implements Comparator<Class<?>> {
        AnonymousClass1() {
        }

        @Override // java.util.Comparator
        public int compare(Class<?> itf1, Class<?> itf2) {
            return itf1.getName().compareTo(itf2.getName());
        }
    }

    /* renamed from: java.io.ObjectStreamClass$2  reason: invalid class name */
    /* loaded from: ObjectStreamClass$2.class */
    static class AnonymousClass2 implements Comparator<Field> {
        AnonymousClass2() {
        }

        @Override // java.util.Comparator
        public int compare(Field field1, Field field2) {
            return field1.getName().compareTo(field2.getName());
        }
    }

    /* renamed from: java.io.ObjectStreamClass$3  reason: invalid class name */
    /* loaded from: ObjectStreamClass$3.class */
    static class AnonymousClass3 implements Comparator<Constructor<?>> {
        AnonymousClass3() {
        }

        @Override // java.util.Comparator
        public int compare(Constructor<?> ctr1, Constructor<?> ctr2) {
            return ObjectStreamClass.getConstructorSignature(ctr1).compareTo(ObjectStreamClass.getConstructorSignature(ctr2));
        }
    }

    /* renamed from: java.io.ObjectStreamClass$4  reason: invalid class name */
    /* loaded from: ObjectStreamClass$4.class */
    static class AnonymousClass4 implements Comparator<Method> {
        AnonymousClass4() {
        }

        @Override // java.util.Comparator
        public int compare(Method m1, Method m2) {
            int result = m1.getName().compareTo(m2.getName());
            if (result == 0) {
                return ObjectStreamClass.getMethodSignature(m1).compareTo(ObjectStreamClass.getMethodSignature(m2));
            }
            return result;
        }
    }

    /* renamed from: java.io.ObjectStreamClass$5  reason: invalid class name */
    /* loaded from: ObjectStreamClass$5.class */
    static class AnonymousClass5 extends ThreadLocal<WeakHashMap<Class<?>, ObjectStreamClass>> {
        AnonymousClass5() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public WeakHashMap<Class<?>, ObjectStreamClass> initialValue() {
            return new WeakHashMap<>();
        }
    }
}