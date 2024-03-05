package libcore.reflect;

import com.android.dex.Dex;
import com.android.dex.EncodedValueReader;
import com.android.dex.FieldId;
import com.android.dex.MethodId;
import com.android.dex.ProtoId;
import com.android.dex.TypeList;
import gov.nist.core.Separators;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import libcore.util.EmptyArray;

/* loaded from: AnnotationAccess.class */
public final class AnnotationAccess {
    private static final Class<?>[] NO_ARGUMENTS = null;
    private static final byte VISIBILITY_BUILD = 0;
    private static final byte VISIBILITY_RUNTIME = 1;
    private static final byte VISIBILITY_SYSTEM = 2;

    private AnnotationAccess() {
    }

    public static <A extends Annotation> A getAnnotation(Class<?> c, Class<A> annotationType) {
        if (annotationType == null) {
            throw new NullPointerException("annotationType == null");
        }
        A annotation = (A) getDeclaredAnnotation(c, annotationType);
        if (annotation != null) {
            return annotation;
        }
        if (isInherited(annotationType)) {
            Class<?> superclass = c.getSuperclass();
            while (true) {
                Class<?> sup = superclass;
                if (sup != null) {
                    A annotation2 = (A) getDeclaredAnnotation(sup, annotationType);
                    if (annotation2 == null) {
                        superclass = sup.getSuperclass();
                    } else {
                        return annotation2;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private static boolean isInherited(Class<? extends Annotation> annotationType) {
        return isDeclaredAnnotationPresent(annotationType, Inherited.class);
    }

    public static Annotation[] getAnnotations(Class<?> c) {
        HashMap<Class<?>, Annotation> map = new HashMap<>();
        for (Annotation declaredAnnotation : getDeclaredAnnotations(c)) {
            map.put(declaredAnnotation.annotationType(), declaredAnnotation);
        }
        Class<?> superclass = c.getSuperclass();
        while (true) {
            Class<?> sup = superclass;
            if (sup != null) {
                for (Annotation declaredAnnotation2 : getDeclaredAnnotations(sup)) {
                    Class<? extends Annotation> clazz = declaredAnnotation2.annotationType();
                    if (!map.containsKey(clazz) && isInherited(clazz)) {
                        map.put(clazz, declaredAnnotation2);
                    }
                }
                superclass = sup.getSuperclass();
            } else {
                Collection<Annotation> coll = map.values();
                return (Annotation[]) coll.toArray(new Annotation[coll.size()]);
            }
        }
    }

    public static boolean isAnnotationPresent(Class<?> c, Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            throw new NullPointerException("annotationType == null");
        }
        if (isDeclaredAnnotationPresent(c, annotationType)) {
            return true;
        }
        if (isInherited(annotationType)) {
            Class<?> superclass = c.getSuperclass();
            while (true) {
                Class<?> sup = superclass;
                if (sup != null) {
                    if (!isDeclaredAnnotationPresent(sup, annotationType)) {
                        superclass = sup.getSuperclass();
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static List<Annotation> getDeclaredAnnotations(AnnotatedElement element) {
        int offset = getAnnotationSetOffset(element);
        return annotationSetToAnnotations(getDexClass(element), offset);
    }

    public static <A extends Annotation> A getDeclaredAnnotation(AnnotatedElement element, Class<A> annotationClass) {
        com.android.dex.Annotation a = getMethodAnnotation(element, annotationClass);
        if (a != null) {
            return (A) toAnnotationInstance(getDexClass(element), annotationClass, a);
        }
        return null;
    }

    public static boolean isDeclaredAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        return getMethodAnnotation(element, annotationClass) != null;
    }

    private static com.android.dex.Annotation getMethodAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        int annotationSetOffset;
        Class<?> dexClass = getDexClass(element);
        Dex dex = dexClass.getDex();
        int annotationTypeIndex = getTypeIndex(dex, annotationClass);
        if (annotationTypeIndex == -1 || (annotationSetOffset = getAnnotationSetOffset(element)) == 0) {
            return null;
        }
        Dex.Section setIn = dex.open(annotationSetOffset);
        int size = setIn.readInt();
        for (int i = 0; i < size; i++) {
            int annotationOffset = setIn.readInt();
            Dex.Section annotationIn = dex.open(annotationOffset);
            com.android.dex.Annotation candidate = annotationIn.readAnnotation();
            if (candidate.getTypeIndex() == annotationTypeIndex) {
                return candidate;
            }
        }
        return null;
    }

    private static int getAnnotationSetOffset(AnnotatedElement element) {
        Class<?> dexClass = getDexClass(element);
        int directoryOffset = dexClass.getDexAnnotationDirectoryOffset();
        if (directoryOffset == 0) {
            return 0;
        }
        Dex.Section directoryIn = dexClass.getDex().open(directoryOffset);
        int classSetOffset = directoryIn.readInt();
        if (element instanceof Class) {
            return classSetOffset;
        }
        int fieldsSize = directoryIn.readInt();
        int methodsSize = directoryIn.readInt();
        directoryIn.readInt();
        int fieldIndex = element instanceof Field ? ((Field) element).getDexFieldIndex() : -1;
        for (int i = 0; i < fieldsSize; i++) {
            int candidateFieldIndex = directoryIn.readInt();
            int annotationSetOffset = directoryIn.readInt();
            if (candidateFieldIndex == fieldIndex) {
                return annotationSetOffset;
            }
        }
        if (element instanceof Field) {
            return 0;
        }
        int methodIndex = element instanceof Method ? ((Method) element).getDexMethodIndex() : ((Constructor) element).getDexMethodIndex();
        for (int i2 = 0; i2 < methodsSize; i2++) {
            int candidateMethodIndex = directoryIn.readInt();
            int annotationSetOffset2 = directoryIn.readInt();
            if (candidateMethodIndex == methodIndex) {
                return annotationSetOffset2;
            }
        }
        return 0;
    }

    private static Class<?> getDexClass(AnnotatedElement element) {
        return element instanceof Class ? (Class) element : ((Member) element).getDeclaringClass();
    }

    public static int getFieldIndex(Class<?> declaringClass, Class<?> type, String name) {
        Dex dex = declaringClass.getDex();
        int declaringClassIndex = getTypeIndex(dex, declaringClass);
        int typeIndex = getTypeIndex(dex, type);
        int nameIndex = dex.findStringIndex(name);
        FieldId fieldId = new FieldId(dex, declaringClassIndex, typeIndex, nameIndex);
        return dex.findFieldIndex(fieldId);
    }

    public static int getMethodIndex(Class<?> declaringClass, String name, int protoIndex) {
        Dex dex = declaringClass.getDex();
        int declaringClassIndex = getTypeIndex(dex, declaringClass);
        int nameIndex = dex.findStringIndex(name);
        MethodId methodId = new MethodId(dex, declaringClassIndex, protoIndex, nameIndex);
        return dex.findMethodIndex(methodId);
    }

    /* JADX WARN: Type inference failed for: r0v48, types: [java.lang.annotation.Annotation[], java.lang.annotation.Annotation[][]] */
    public static Annotation[][] getParameterAnnotations(Class<?> declaringClass, int methodDexIndex) {
        Dex dex = declaringClass.getDex();
        int protoIndex = dex.methodIds().get(methodDexIndex).getProtoIndex();
        ProtoId proto = dex.protoIds().get(protoIndex);
        TypeList parametersList = dex.readTypeList(proto.getParametersOffset());
        short[] types = parametersList.getTypes();
        int typesCount = types.length;
        int directoryOffset = declaringClass.getDexAnnotationDirectoryOffset();
        if (directoryOffset == 0) {
            return new Annotation[typesCount][0];
        }
        Dex.Section directoryIn = dex.open(directoryOffset);
        directoryIn.readInt();
        int fieldsSize = directoryIn.readInt();
        int methodsSize = directoryIn.readInt();
        int parametersSize = directoryIn.readInt();
        for (int i = 0; i < fieldsSize; i++) {
            directoryIn.readInt();
            directoryIn.readInt();
        }
        for (int i2 = 0; i2 < methodsSize; i2++) {
            directoryIn.readInt();
            directoryIn.readInt();
        }
        for (int i3 = 0; i3 < parametersSize; i3++) {
            int candidateMethodDexIndex = directoryIn.readInt();
            int annotationSetRefListOffset = directoryIn.readInt();
            if (candidateMethodDexIndex == methodDexIndex) {
                Dex.Section refList = dex.open(annotationSetRefListOffset);
                int parameterCount = refList.readInt();
                ?? r0 = new Annotation[parameterCount];
                for (int p = 0; p < parameterCount; p++) {
                    int annotationSetOffset = refList.readInt();
                    List<Annotation> annotations = annotationSetToAnnotations(declaringClass, annotationSetOffset);
                    r0[p] = (Annotation[]) annotations.toArray(new Annotation[annotations.size()]);
                }
                return r0;
            }
        }
        return new Annotation[typesCount][0];
    }

    public static Object getDefaultValue(Method method) {
        Class<?> annotationClass = method.getDeclaringClass();
        Dex dex = annotationClass.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, annotationClass, "Ldalvik/annotation/AnnotationDefault;");
        if (reader == null) {
            return null;
        }
        int fieldCount = reader.readAnnotation();
        if (reader.getAnnotationType() != getTypeIndex(dex, annotationClass)) {
            throw new AssertionError("annotation value type != annotation class");
        }
        int methodNameIndex = dex.findStringIndex(method.getName());
        for (int i = 0; i < fieldCount; i++) {
            int candidateNameIndex = reader.readAnnotationName();
            if (candidateNameIndex == methodNameIndex) {
                Class<?> returnType = method.getReturnType();
                return decodeValue(annotationClass, returnType, dex, reader);
            }
            reader.skipValue();
        }
        return null;
    }

    public static Class<?> getDeclaringClass(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, c, "Ldalvik/annotation/EnclosingClass;");
        if (reader == null) {
            return null;
        }
        return c.getDexCacheType(dex, reader.readType());
    }

    public static AccessibleObject getEnclosingMethodOrConstructor(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, c, "Ldalvik/annotation/EnclosingMethod;");
        if (reader == null) {
            return null;
        }
        return indexToMethod(c, dex, reader.readMethod());
    }

    public static Class<?>[] getMemberClasses(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, c, "Ldalvik/annotation/MemberClasses;");
        if (reader == null) {
            return EmptyArray.CLASS;
        }
        return (Class[]) decodeValue(c, Class[].class, dex, reader);
    }

    public static String getSignature(AnnotatedElement element) {
        Class<?> dexClass = getDexClass(element);
        Dex dex = dexClass.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, element, "Ldalvik/annotation/Signature;");
        if (reader == null) {
            return null;
        }
        String[] array = (String[]) decodeValue(dexClass, String[].class, dex, reader);
        StringBuilder result = new StringBuilder();
        for (String s : array) {
            result.append(s);
        }
        return result.toString();
    }

    public static Class<?>[] getExceptions(AnnotatedElement element) {
        Class<?> dexClass = getDexClass(element);
        Dex dex = dexClass.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, element, "Ldalvik/annotation/Throws;");
        if (reader == null) {
            return EmptyArray.CLASS;
        }
        return (Class[]) decodeValue(dexClass, Class[].class, dex, reader);
    }

    public static int getInnerClassFlags(Class<?> c, int defaultValue) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getAnnotationReader(dex, c, "Ldalvik/annotation/InnerClass;", 2);
        if (reader == null) {
            return defaultValue;
        }
        reader.readAnnotationName();
        return reader.readInt();
    }

    public static String getInnerClassName(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getAnnotationReader(dex, c, "Ldalvik/annotation/InnerClass;", 2);
        if (reader == null) {
            return null;
        }
        reader.readAnnotationName();
        reader.readInt();
        reader.readAnnotationName();
        if (reader.peek() == 30) {
            return null;
        }
        return (String) decodeValue(c, String.class, dex, reader);
    }

    public static boolean isAnonymousClass(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getAnnotationReader(dex, c, "Ldalvik/annotation/InnerClass;", 2);
        if (reader == null) {
            return false;
        }
        reader.readAnnotationName();
        reader.readInt();
        reader.readAnnotationName();
        return reader.peek() == 30;
    }

    private static int getTypeIndex(Dex dex, Class<?> c) {
        if (dex == c.getDex()) {
            return c.getDexTypeIndex();
        }
        if (dex == null) {
            return -1;
        }
        int typeIndex = dex.findTypeIndex(InternalNames.getInternalName(c));
        if (typeIndex < 0) {
            typeIndex = -1;
        }
        return typeIndex;
    }

    private static EncodedValueReader getAnnotationReader(Dex dex, AnnotatedElement element, String annotationName, int expectedFieldCount) {
        int annotationSetOffset = getAnnotationSetOffset(element);
        if (annotationSetOffset == 0) {
            return null;
        }
        Dex.Section setIn = dex.open(annotationSetOffset);
        com.android.dex.Annotation annotation = null;
        int i = 0;
        int size = setIn.readInt();
        while (true) {
            if (i >= size) {
                break;
            }
            int annotationOffset = setIn.readInt();
            Dex.Section annotationIn = dex.open(annotationOffset);
            com.android.dex.Annotation candidate = annotationIn.readAnnotation();
            String candidateAnnotationName = dex.typeNames().get(candidate.getTypeIndex());
            if (!annotationName.equals(candidateAnnotationName)) {
                i++;
            } else {
                annotation = candidate;
                break;
            }
        }
        if (annotation == null) {
            return null;
        }
        EncodedValueReader reader = annotation.getReader();
        int fieldCount = reader.readAnnotation();
        String readerAnnotationName = dex.typeNames().get(reader.getAnnotationType());
        if (!readerAnnotationName.equals(annotationName)) {
            throw new AssertionError();
        }
        if (fieldCount != expectedFieldCount) {
            return null;
        }
        return reader;
    }

    private static EncodedValueReader getOnlyAnnotationValue(Dex dex, AnnotatedElement element, String annotationName) {
        EncodedValueReader reader = getAnnotationReader(dex, element, annotationName, 1);
        if (reader == null) {
            return null;
        }
        reader.readAnnotationName();
        return reader;
    }

    private static Class<? extends Annotation> getAnnotationClass(Class<?> context, Dex dex, int typeIndex) {
        try {
            Class<? extends Annotation> result = context.getDexCacheType(dex, typeIndex);
            if (!result.isAnnotation()) {
                throw new IncompatibleClassChangeError("Expected annotation: " + result.getName());
            }
            return result;
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    private static AccessibleObject indexToMethod(Class<?> context, Dex dex, int methodIndex) {
        Class<?> declaringClass = context.getDexCacheType(dex, dex.declaringClassIndexFromMethodIndex(methodIndex));
        String name = context.getDexCacheString(dex, dex.nameIndexFromMethodIndex(methodIndex));
        short[] types = dex.parameterTypeIndicesFromMethodIndex(methodIndex);
        Class<?>[] parametersArray = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            parametersArray[i] = context.getDexCacheType(dex, types[i]);
        }
        try {
            return name.equals("<init>") ? declaringClass.getDeclaredConstructor(parametersArray) : declaringClass.getDeclaredMethod(name, parametersArray);
        } catch (NoSuchMethodException e) {
            throw new IncompatibleClassChangeError("Couldn't find " + declaringClass.getName() + Separators.DOT + name + Arrays.toString(parametersArray));
        }
    }

    private static List<Annotation> annotationSetToAnnotations(Class<?> context, int offset) {
        Class<? extends Annotation> annotationClass;
        if (offset == 0) {
            return Collections.emptyList();
        }
        Dex dex = context.getDex();
        Dex.Section setIn = dex.open(offset);
        int size = setIn.readInt();
        List<Annotation> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int annotationOffset = setIn.readInt();
            Dex.Section annotationIn = dex.open(annotationOffset);
            com.android.dex.Annotation annotation = annotationIn.readAnnotation();
            if (annotation.getVisibility() == 1 && (annotationClass = getAnnotationClass(context, dex, annotation.getTypeIndex())) != null) {
                result.add(toAnnotationInstance(context, dex, annotationClass, annotation.getReader()));
            }
        }
        return result;
    }

    private static <A extends Annotation> A toAnnotationInstance(Class<?> context, Class<A> annotationClass, com.android.dex.Annotation annotation) {
        return (A) toAnnotationInstance(context, context.getDex(), annotationClass, annotation.getReader());
    }

    private static <A extends Annotation> A toAnnotationInstance(Class<?> context, Dex dex, Class<A> annotationClass, EncodedValueReader reader) {
        int fieldCount = reader.readAnnotation();
        if (annotationClass != context.getDexCacheType(dex, reader.getAnnotationType())) {
            throw new AssertionError("annotation value type != return type");
        }
        AnnotationMember[] members = new AnnotationMember[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            int name = reader.readAnnotationName();
            String nameString = dex.strings().get(name);
            try {
                Method method = annotationClass.getMethod(nameString, NO_ARGUMENTS);
                Class<?> returnType = method.getReturnType();
                Object value = decodeValue(context, returnType, dex, reader);
                members[i] = new AnnotationMember(nameString, value, returnType, method);
            } catch (NoSuchMethodException e) {
                throw new IncompatibleClassChangeError("Couldn't find " + annotationClass.getName() + Separators.DOT + nameString);
            }
        }
        return (A) AnnotationFactory.createAnnotation(annotationClass, members);
    }

    private static Object decodeValue(Class<?> context, Class<?> type, Dex dex, EncodedValueReader reader) {
        if (type.isArray()) {
            int size = reader.readArray();
            Class<?> componentType = type.getComponentType();
            Object array = Array.newInstance(componentType, size);
            for (int i = 0; i < size; i++) {
                Array.set(array, i, decodeValue(context, componentType, dex, reader));
            }
            return array;
        } else if (type.isEnum()) {
            int fieldIndex = reader.readEnum();
            FieldId fieldId = dex.fieldIds().get(fieldIndex);
            String enumName = dex.strings().get(fieldId.getNameIndex());
            return Enum.valueOf(type, enumName);
        } else if (type.isAnnotation()) {
            return toAnnotationInstance(context, dex, type, reader);
        } else {
            if (type == String.class) {
                int index = reader.readString();
                return context.getDexCacheString(dex, index);
            } else if (type == Class.class) {
                int index2 = reader.readType();
                return context.getDexCacheType(dex, index2);
            } else if (type == Byte.TYPE) {
                return Byte.valueOf(reader.readByte());
            } else {
                if (type == Short.TYPE) {
                    return Short.valueOf(reader.readShort());
                }
                if (type == Integer.TYPE) {
                    return Integer.valueOf(reader.readInt());
                }
                if (type == Long.TYPE) {
                    return Long.valueOf(reader.readLong());
                }
                if (type == Float.TYPE) {
                    return Float.valueOf(reader.readFloat());
                }
                if (type == Double.TYPE) {
                    return Double.valueOf(reader.readDouble());
                }
                if (type == Character.TYPE) {
                    return Character.valueOf(reader.readChar());
                }
                if (type == Boolean.TYPE) {
                    return Boolean.valueOf(reader.readBoolean());
                }
                throw new AssertionError("Unexpected annotation value type: " + type);
            }
        }
    }
}