package org.apache.commons.logging;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.NoOpLog;

/* loaded from: LogSource.class */
public class LogSource {
    protected static boolean log4jIsAvailable;
    protected static boolean jdk14IsAvailable;
    protected static Hashtable logs = new Hashtable();
    protected static Constructor logImplctor = null;

    static {
        log4jIsAvailable = false;
        jdk14IsAvailable = false;
        try {
            if (null != Class.forName("org.apache.log4j.Logger")) {
                log4jIsAvailable = true;
            } else {
                log4jIsAvailable = false;
            }
        } catch (Throwable th) {
            log4jIsAvailable = false;
        }
        try {
            if (null != Class.forName("java.util.logging.Logger") && null != Class.forName("org.apache.commons.logging.impl.Jdk14Logger")) {
                jdk14IsAvailable = true;
            } else {
                jdk14IsAvailable = false;
            }
        } catch (Throwable th2) {
            jdk14IsAvailable = false;
        }
        String name = null;
        try {
            name = System.getProperty("org.apache.commons.logging.log");
            if (name == null) {
                name = System.getProperty(LogFactoryImpl.LOG_PROPERTY);
            }
        } catch (Throwable th3) {
        }
        if (name != null) {
            try {
                setLogImplementation(name);
                return;
            } catch (Throwable th4) {
                try {
                    setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
                    return;
                } catch (Throwable th5) {
                    return;
                }
            }
        }
        try {
            if (log4jIsAvailable) {
                setLogImplementation("org.apache.commons.logging.impl.Log4JLogger");
            } else if (jdk14IsAvailable) {
                setLogImplementation("org.apache.commons.logging.impl.Jdk14Logger");
            } else {
                setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
            }
        } catch (Throwable th6) {
            try {
                setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
            } catch (Throwable th7) {
            }
        }
    }

    private LogSource() {
    }

    public static void setLogImplementation(String classname) throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException, ClassNotFoundException {
        try {
            Class logclass = Class.forName(classname);
            Class[] argtypes = {"".getClass()};
            logImplctor = logclass.getConstructor(argtypes);
        } catch (Throwable th) {
            logImplctor = null;
        }
    }

    public static void setLogImplementation(Class logclass) throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException {
        Class[] argtypes = {"".getClass()};
        logImplctor = logclass.getConstructor(argtypes);
    }

    public static Log getInstance(String name) {
        Log log = (Log) logs.get(name);
        if (null == log) {
            log = makeNewLogInstance(name);
            logs.put(name, log);
        }
        return log;
    }

    public static Log getInstance(Class clazz) {
        return getInstance(clazz.getName());
    }

    public static Log makeNewLogInstance(String name) {
        Log log;
        try {
            Object[] args = {name};
            log = (Log) logImplctor.newInstance(args);
        } catch (Throwable th) {
            log = null;
        }
        if (null == log) {
            log = new NoOpLog(name);
        }
        return log;
    }

    public static String[] getLogNames() {
        return (String[]) logs.keySet().toArray(new String[logs.size()]);
    }
}