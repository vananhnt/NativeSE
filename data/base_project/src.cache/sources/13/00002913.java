package libcore.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: AnnotationFactory.class */
public final class AnnotationFactory implements InvocationHandler, Serializable {
    private static final transient Map<Class<? extends Annotation>, AnnotationMember[]> cache = new WeakHashMap();
    private final Class<? extends Annotation> klazz;
    private AnnotationMember[] elements;

    public static AnnotationMember[] getElementsDescription(Class<? extends Annotation> annotationType) {
        synchronized (cache) {
            AnnotationMember[] desc = cache.get(annotationType);
            if (desc != null) {
                return desc;
            }
            if (!annotationType.isAnnotation()) {
                throw new IllegalArgumentException("Type is not annotation: " + annotationType.getName());
            }
            Method[] declaredMethods = annotationType.getDeclaredMethods();
            AnnotationMember[] desc2 = new AnnotationMember[declaredMethods.length];
            for (int i = 0; i < declaredMethods.length; i++) {
                Method element = declaredMethods[i];
                String name = element.getName();
                Class<?> type = element.getReturnType();
                try {
                    desc2[i] = new AnnotationMember(name, element.getDefaultValue(), type, element);
                } catch (Throwable t) {
                    desc2[i] = new AnnotationMember(name, t, type, element);
                }
            }
            synchronized (cache) {
                cache.put(annotationType, desc2);
            }
            return desc2;
        }
    }

    public static <A extends Annotation> A createAnnotation(Class<? extends Annotation> annotationType, AnnotationMember[] elements) {
        AnnotationFactory factory = new AnnotationFactory(annotationType, elements);
        return (A) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, factory);
    }

    private AnnotationFactory(Class<? extends Annotation> klzz, AnnotationMember[] values) {
        this.klazz = klzz;
        AnnotationMember[] defs = getElementsDescription(this.klazz);
        if (values == null) {
            this.elements = defs;
            return;
        }
        this.elements = new AnnotationMember[defs.length];
        for (int i = this.elements.length - 1; i >= 0; i--) {
            int len$ = values.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    AnnotationMember val = values[i$];
                    if (!val.name.equals(defs[i].name)) {
                        i$++;
                    } else {
                        this.elements[i] = val.setDefinition(defs[i]);
                        break;
                    }
                } else {
                    this.elements[i] = defs[i];
                    break;
                }
            }
        }
    }

    private void readObject(ObjectInputStream os) throws IOException, ClassNotFoundException {
        os.defaultReadObject();
        AnnotationMember[] defs = getElementsDescription(this.klazz);
        AnnotationMember[] old = this.elements;
        List<AnnotationMember> merged = new ArrayList<>(defs.length + old.length);
        for (AnnotationMember el1 : old) {
            int len$ = defs.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    AnnotationMember el2 = defs[i$];
                    if (el2.name.equals(el1.name)) {
                        break;
                    }
                    i$++;
                } else {
                    merged.add(el1);
                    break;
                }
            }
        }
        for (AnnotationMember def : defs) {
            int len$2 = old.length;
            int i$2 = 0;
            while (true) {
                if (i$2 < len$2) {
                    AnnotationMember val = old[i$2];
                    if (!val.name.equals(def.name)) {
                        i$2++;
                    } else {
                        merged.add(val.setDefinition(def));
                        break;
                    }
                } else {
                    merged.add(def);
                    break;
                }
            }
        }
        this.elements = (AnnotationMember[]) merged.toArray(new AnnotationMember[merged.size()]);
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x008d, code lost:
        r10 = r10 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean equals(java.lang.Object r5) {
        /*
            Method dump skipped, instructions count: 291
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.reflect.AnnotationFactory.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int hash = 0;
        AnnotationMember[] arr$ = this.elements;
        for (AnnotationMember element : arr$) {
            hash += element.hashCode();
        }
        return hash;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('@');
        result.append(this.klazz.getName());
        result.append('(');
        for (int i = 0; i < this.elements.length; i++) {
            if (i != 0) {
                result.append(", ");
            }
            result.append(this.elements[i]);
        }
        result.append(')');
        return result.toString();
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        Class[] params = method.getParameterTypes();
        if (params.length == 0) {
            if ("annotationType".equals(name)) {
                return this.klazz;
            }
            if ("toString".equals(name)) {
                return toString();
            }
            if ("hashCode".equals(name)) {
                return Integer.valueOf(hashCode());
            }
            AnnotationMember element = null;
            AnnotationMember[] arr$ = this.elements;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                AnnotationMember el = arr$[i$];
                if (!name.equals(el.name)) {
                    i$++;
                } else {
                    element = el;
                    break;
                }
            }
            if (element == null || !method.equals(element.definingMethod)) {
                throw new IllegalArgumentException(method.toString());
            }
            Object value = element.validateValue();
            if (value == null) {
                throw new IncompleteAnnotationException(this.klazz, name);
            }
            return value;
        } else if (params.length == 1 && params[0] == Object.class && "equals".equals(name)) {
            return Boolean.valueOf(equals(args[0]));
        } else {
            throw new IllegalArgumentException("Invalid method for annotation type: " + method);
        }
    }
}