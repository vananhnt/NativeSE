package org.xml.sax.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: NewInstance.class */
class NewInstance {
    NewInstance() {
    }

    static Object newInstance(ClassLoader classLoader, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class driverClass;
        if (classLoader == null) {
            driverClass = Class.forName(className);
        } else {
            driverClass = classLoader.loadClass(className);
        }
        return driverClass.newInstance();
    }

    static ClassLoader getClassLoader() {
        try {
            Method m = Thread.class.getMethod("getContextClassLoader", new Class[0]);
            try {
                return (ClassLoader) m.invoke(Thread.currentThread(), new Object[0]);
            } catch (IllegalAccessException e) {
                throw new UnknownError(e.getMessage());
            } catch (InvocationTargetException e2) {
                throw new UnknownError(e2.getMessage());
            }
        } catch (NoSuchMethodException e3) {
            return NewInstance.class.getClassLoader();
        }
    }
}