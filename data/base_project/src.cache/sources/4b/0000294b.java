package org.apache.commons.logging;

import com.android.internal.telephony.IccCardConstants;
import gov.nist.core.Separators;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.commons.logging.impl.Jdk14Logger;

/* loaded from: LogFactory.class */
public abstract class LogFactory {
    public static final String PRIORITY_KEY = "priority";
    public static final String TCCL_KEY = "use_tccl";
    public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";
    public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.LogFactoryImpl";
    public static final String FACTORY_PROPERTIES = "commons-logging.properties";
    protected static final String SERVICE_ID = "META-INF/services/org.apache.commons.logging.LogFactory";
    public static final String DIAGNOSTICS_DEST_PROPERTY = "org.apache.commons.logging.diagnostics.dest";
    private static String diagnosticPrefix;
    public static final String HASHTABLE_IMPLEMENTATION_PROPERTY = "org.apache.commons.logging.LogFactory.HashtableImpl";
    private static final String WEAK_HASHTABLE_CLASSNAME = "org.apache.commons.logging.impl.WeakHashtable";
    protected static Hashtable factories;
    private static PrintStream diagnosticsStream = null;
    protected static LogFactory nullClassLoaderFactory = null;
    private static ClassLoader thisClassLoader = getClassLoader(LogFactory.class);

    public abstract Object getAttribute(String str);

    public abstract String[] getAttributeNames();

    public abstract Log getInstance(Class cls) throws LogConfigurationException;

    public abstract Log getInstance(String str) throws LogConfigurationException;

    public abstract void release();

    public abstract void removeAttribute(String str);

    public abstract void setAttribute(String str, Object obj);

    static {
        factories = null;
        initDiagnostics();
        logClassLoaderEnvironment(LogFactory.class);
        factories = createFactoryStore();
        if (isDiagnosticsEnabled()) {
            logDiagnostic("BOOTSTRAP COMPLETED");
        }
    }

    private static final Hashtable createFactoryStore() {
        Hashtable result = null;
        String storeImplementationClass = System.getProperty(HASHTABLE_IMPLEMENTATION_PROPERTY);
        if (storeImplementationClass == null) {
            storeImplementationClass = WEAK_HASHTABLE_CLASSNAME;
        }
        try {
            Class implementationClass = Class.forName(storeImplementationClass);
            result = (Hashtable) implementationClass.newInstance();
        } catch (Throwable th) {
            if (!WEAK_HASHTABLE_CLASSNAME.equals(storeImplementationClass)) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[ERROR] LogFactory: Load of custom hashtable failed");
                } else {
                    System.err.println("[ERROR] LogFactory: Load of custom hashtable failed");
                }
            }
        }
        if (result == null) {
            result = new Hashtable();
        }
        return result;
    }

    public static LogFactory getFactory() throws LogConfigurationException {
        BufferedReader rd;
        String useTCCLStr;
        ClassLoader contextClassLoader = getContextClassLoader();
        if (contextClassLoader == null && isDiagnosticsEnabled()) {
            logDiagnostic("Context classloader is null.");
        }
        LogFactory factory = getCachedFactory(contextClassLoader);
        if (factory != null) {
            return factory;
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] LogFactory implementation requested for the first time for context classloader " + objectId(contextClassLoader));
            logHierarchy("[LOOKUP] ", contextClassLoader);
        }
        Properties props = getConfigurationFile(contextClassLoader, FACTORY_PROPERTIES);
        ClassLoader baseClassLoader = contextClassLoader;
        if (props != null && (useTCCLStr = props.getProperty(TCCL_KEY)) != null && !Boolean.valueOf(useTCCLStr).booleanValue()) {
            baseClassLoader = thisClassLoader;
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] Looking for system property [org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
        }
        try {
            String factoryClass = System.getProperty(FACTORY_PROPERTY);
            if (factoryClass != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Creating an instance of LogFactory class '" + factoryClass + "' as specified by system property " + FACTORY_PROPERTY);
                }
                factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
            } else if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] No system property [org.apache.commons.logging.LogFactory] defined.");
            }
        } catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + e.getMessage().trim() + "]. Trying alternative implementations...");
            }
        } catch (RuntimeException e2) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] An exception occurred while trying to create an instance of the custom factory class: [" + e2.getMessage().trim() + "] as specified by a system property.");
            }
            throw e2;
        }
        if (factory == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] Looking for a resource file of name [META-INF/services/org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
            }
            try {
                InputStream is = getResourceAsStream(contextClassLoader, SERVICE_ID);
                if (is != null) {
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (UnsupportedEncodingException e3) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    String factoryClassName = rd.readLine();
                    rd.close();
                    if (factoryClassName != null && !"".equals(factoryClassName)) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("[LOOKUP]  Creating an instance of LogFactory class " + factoryClassName + " as specified by file '" + SERVICE_ID + "' which was present in the path of the context classloader.");
                        }
                        factory = newFactory(factoryClassName, baseClassLoader, contextClassLoader);
                    }
                } else if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] No resource file with name 'META-INF/services/org.apache.commons.logging.LogFactory' found.");
                }
            } catch (Exception ex) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + ex.getMessage().trim() + "]. Trying alternative implementations...");
                }
            }
        }
        if (factory == null) {
            if (props != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Looking in properties file for entry with key 'org.apache.commons.logging.LogFactory' to define the LogFactory subclass to use...");
                }
                String factoryClass2 = props.getProperty(FACTORY_PROPERTY);
                if (factoryClass2 != null) {
                    if (isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] Properties file specifies LogFactory subclass '" + factoryClass2 + Separators.QUOTE);
                    }
                    factory = newFactory(factoryClass2, baseClassLoader, contextClassLoader);
                } else if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Properties file has no entry specifying LogFactory subclass.");
                }
            } else if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] No properties file available to determine LogFactory subclass from..");
            }
        }
        if (factory == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] Loading the default LogFactory implementation 'org.apache.commons.logging.impl.LogFactoryImpl' via the same classloader that loaded this LogFactory class (ie not looking in the context classloader).");
            }
            factory = newFactory(FACTORY_DEFAULT, thisClassLoader, contextClassLoader);
        }
        if (factory != null) {
            cacheFactory(contextClassLoader, factory);
            if (props != null) {
                Enumeration names = props.propertyNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    String value = props.getProperty(name);
                    factory.setAttribute(name, value);
                }
            }
        }
        return factory;
    }

    public static Log getLog(Class clazz) throws LogConfigurationException {
        return getLog(clazz.getName());
    }

    public static Log getLog(String name) throws LogConfigurationException {
        return new Jdk14Logger(name);
    }

    public static void release(ClassLoader classLoader) {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Releasing factory for classloader " + objectId(classLoader));
        }
        synchronized (factories) {
            if (classLoader == null) {
                if (nullClassLoaderFactory != null) {
                    nullClassLoaderFactory.release();
                    nullClassLoaderFactory = null;
                }
            } else {
                LogFactory factory = (LogFactory) factories.get(classLoader);
                if (factory != null) {
                    factory.release();
                    factories.remove(classLoader);
                }
            }
        }
    }

    public static void releaseAll() {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Releasing factory for all classloaders.");
        }
        synchronized (factories) {
            Enumeration elements = factories.elements();
            while (elements.hasMoreElements()) {
                LogFactory element = (LogFactory) elements.nextElement();
                element.release();
            }
            factories.clear();
            if (nullClassLoaderFactory != null) {
                nullClassLoaderFactory.release();
                nullClassLoaderFactory = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ClassLoader getClassLoader(Class clazz) {
        try {
            return clazz.getClassLoader();
        } catch (SecurityException ex) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to get classloader for class '" + clazz + "' due to security restrictions - " + ex.getMessage());
            }
            throw ex;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ClassLoader getContextClassLoader() throws LogConfigurationException {
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.apache.commons.logging.LogFactory.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return LogFactory.directGetContextClassLoader();
            }
        });
    }

    protected static ClassLoader directGetContextClassLoader() throws LogConfigurationException {
        ClassLoader classLoader = null;
        try {
            Method method = Thread.class.getMethod("getContextClassLoader", null);
            try {
                try {
                    classLoader = (ClassLoader) method.invoke(Thread.currentThread(), null);
                } catch (InvocationTargetException e) {
                    if (!(e.getTargetException() instanceof SecurityException)) {
                        throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
                    }
                }
            } catch (IllegalAccessException e2) {
                throw new LogConfigurationException("Unexpected IllegalAccessException", e2);
            }
        } catch (NoSuchMethodException e3) {
            classLoader = getClassLoader(LogFactory.class);
        }
        return classLoader;
    }

    private static LogFactory getCachedFactory(ClassLoader contextClassLoader) {
        LogFactory factory;
        if (contextClassLoader == null) {
            factory = nullClassLoaderFactory;
        } else {
            factory = (LogFactory) factories.get(contextClassLoader);
        }
        return factory;
    }

    private static void cacheFactory(ClassLoader classLoader, LogFactory factory) {
        if (factory != null) {
            if (classLoader == null) {
                nullClassLoaderFactory = factory;
            } else {
                factories.put(classLoader, factory);
            }
        }
    }

    protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader, ClassLoader contextClassLoader) throws LogConfigurationException {
        Object result = AccessController.doPrivileged(new PrivilegedAction() { // from class: org.apache.commons.logging.LogFactory.2
            @Override // java.security.PrivilegedAction
            public Object run() {
                return LogFactory.createFactory(String.this, classLoader);
            }
        });
        if (result instanceof LogConfigurationException) {
            LogConfigurationException ex = (LogConfigurationException) result;
            if (isDiagnosticsEnabled()) {
                logDiagnostic("An error occurred while loading the factory class:" + ex.getMessage());
            }
            throw ex;
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Created object " + objectId(result) + " to manage classloader " + objectId(contextClassLoader));
        }
        return (LogFactory) result;
    }

    protected static LogFactory newFactory(String factoryClass, ClassLoader classLoader) {
        return newFactory(factoryClass, classLoader, null);
    }

    protected static Object createFactory(String factoryClass, ClassLoader classLoader) {
        String msg;
        Class logFactoryClass = null;
        try {
            if (classLoader != null) {
                try {
                    try {
                        try {
                            logFactoryClass = classLoader.loadClass(factoryClass);
                            if (LogFactory.class.isAssignableFrom(logFactoryClass)) {
                                if (isDiagnosticsEnabled()) {
                                    logDiagnostic("Loaded class " + logFactoryClass.getName() + " from classloader " + objectId(classLoader));
                                }
                            } else if (isDiagnosticsEnabled()) {
                                logDiagnostic("Factory class " + logFactoryClass.getName() + " loaded from classloader " + objectId(logFactoryClass.getClassLoader()) + " does not extend '" + LogFactory.class.getName() + "' as loaded by this classloader.");
                                logHierarchy("[BAD CL TREE] ", classLoader);
                            }
                            return (LogFactory) logFactoryClass.newInstance();
                        } catch (NoClassDefFoundError e) {
                            if (classLoader == thisClassLoader) {
                                if (isDiagnosticsEnabled()) {
                                    logDiagnostic("Class '" + factoryClass + "' cannot be loaded via classloader " + objectId(classLoader) + " - it depends on some other class that cannot be found.");
                                }
                                throw e;
                            }
                        }
                    } catch (ClassCastException e2) {
                        if (classLoader == thisClassLoader) {
                            boolean implementsLogFactory = implementsLogFactory(logFactoryClass);
                            String msg2 = "The application has specified that a custom LogFactory implementation should be used but Class '" + factoryClass + "' cannot be converted to '" + LogFactory.class.getName() + "'. ";
                            if (implementsLogFactory) {
                                msg = msg2 + "The conflict is caused by the presence of multiple LogFactory classes in incompatible classloaders. Background can be found in http://jakarta.apache.org/commons/logging/tech.html. If you have not explicitly specified a custom LogFactory then it is likely that the container has set one without your knowledge. In this case, consider using the commons-logging-adapters.jar file or specifying the standard LogFactory from the command line. ";
                            } else {
                                msg = msg2 + "Please check the custom implementation. ";
                            }
                            String msg3 = msg + "Help can be found @http://jakarta.apache.org/commons/logging/troubleshooting.html.";
                            if (isDiagnosticsEnabled()) {
                                logDiagnostic(msg3);
                            }
                            ClassCastException ex = new ClassCastException(msg3);
                            throw ex;
                        }
                    }
                } catch (ClassNotFoundException ex2) {
                    if (classLoader == thisClassLoader) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Unable to locate any class called '" + factoryClass + "' via classloader " + objectId(classLoader));
                        }
                        throw ex2;
                    }
                }
            }
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to load factory class via classloader " + objectId(classLoader) + " - trying the classloader associated with this LogFactory.");
            }
            return (LogFactory) Class.forName(factoryClass).newInstance();
        } catch (Exception e3) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to create LogFactory instance.");
            }
            if (logFactoryClass != null && !LogFactory.class.isAssignableFrom(logFactoryClass)) {
                return new LogConfigurationException("The chosen LogFactory implementation does not extend LogFactory. Please check your configuration.", e3);
            }
            return new LogConfigurationException(e3);
        }
    }

    private static boolean implementsLogFactory(Class logFactoryClass) {
        boolean implementsLogFactory = false;
        if (logFactoryClass != null) {
            try {
                ClassLoader logFactoryClassLoader = logFactoryClass.getClassLoader();
                if (logFactoryClassLoader == null) {
                    logDiagnostic("[CUSTOM LOG FACTORY] was loaded by the boot classloader");
                } else {
                    logHierarchy("[CUSTOM LOG FACTORY] ", logFactoryClassLoader);
                    Class factoryFromCustomLoader = Class.forName(FACTORY_PROPERTY, false, logFactoryClassLoader);
                    implementsLogFactory = factoryFromCustomLoader.isAssignableFrom(logFactoryClass);
                    if (implementsLogFactory) {
                        logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " implements LogFactory but was loaded by an incompatible classloader.");
                    } else {
                        logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " does not implement LogFactory.");
                    }
                }
            } catch (ClassNotFoundException e) {
                logDiagnostic("[CUSTOM LOG FACTORY] LogFactory class cannot be loaded by classloader which loaded the custom LogFactory implementation. Is the custom factory in the right classloader?");
            } catch (LinkageError e2) {
                logDiagnostic("[CUSTOM LOG FACTORY] LinkageError thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + e2.getMessage());
            } catch (SecurityException e3) {
                logDiagnostic("[CUSTOM LOG FACTORY] SecurityException thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + e3.getMessage());
            }
        }
        return implementsLogFactory;
    }

    private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
        return (InputStream) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.apache.commons.logging.LogFactory.3
            @Override // java.security.PrivilegedAction
            public Object run() {
                if (ClassLoader.this != null) {
                    return ClassLoader.this.getResourceAsStream(name);
                }
                return ClassLoader.getSystemResourceAsStream(name);
            }
        });
    }

    private static Enumeration getResources(final ClassLoader loader, final String name) {
        PrivilegedAction action = new PrivilegedAction() { // from class: org.apache.commons.logging.LogFactory.4
            @Override // java.security.PrivilegedAction
            public Object run() {
                try {
                    if (ClassLoader.this != null) {
                        return ClassLoader.this.getResources(name);
                    }
                    return ClassLoader.getSystemResources(name);
                } catch (IOException e) {
                    if (LogFactory.isDiagnosticsEnabled()) {
                        LogFactory.logDiagnostic("Exception while trying to find configuration file " + name + Separators.COLON + e.getMessage());
                        return null;
                    }
                    return null;
                } catch (NoSuchMethodError e2) {
                    return null;
                }
            }
        };
        Object result = AccessController.doPrivileged(action);
        return (Enumeration) result;
    }

    private static Properties getProperties(final URL url) {
        PrivilegedAction action = new PrivilegedAction() { // from class: org.apache.commons.logging.LogFactory.5
            @Override // java.security.PrivilegedAction
            public Object run() {
                try {
                    InputStream stream = URL.this.openStream();
                    if (stream != null) {
                        Properties props = new Properties();
                        props.load(stream);
                        stream.close();
                        return props;
                    }
                    return null;
                } catch (IOException e) {
                    if (LogFactory.isDiagnosticsEnabled()) {
                        LogFactory.logDiagnostic("Unable to read URL " + URL.this);
                        return null;
                    }
                    return null;
                }
            }
        };
        return (Properties) AccessController.doPrivileged(action);
    }

    private static final Properties getConfigurationFile(ClassLoader classLoader, String fileName) {
        Enumeration urls;
        Properties props = null;
        double priority = 0.0d;
        URL propsUrl = null;
        try {
            urls = getResources(classLoader, fileName);
        } catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("SecurityException thrown while trying to find/read config files.");
            }
        }
        if (urls == null) {
            return null;
        }
        while (urls.hasMoreElements()) {
            URL url = (URL) urls.nextElement();
            Properties newProps = getProperties(url);
            if (newProps != null) {
                if (props == null) {
                    propsUrl = url;
                    props = newProps;
                    String priorityStr = props.getProperty("priority");
                    priority = 0.0d;
                    if (priorityStr != null) {
                        priority = Double.parseDouble(priorityStr);
                    }
                    if (isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] Properties file found at '" + url + Separators.QUOTE + " with priority " + priority);
                    }
                } else {
                    String newPriorityStr = newProps.getProperty("priority");
                    double newPriority = 0.0d;
                    if (newPriorityStr != null) {
                        newPriority = Double.parseDouble(newPriorityStr);
                    }
                    if (newPriority > priority) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("[LOOKUP] Properties file at '" + url + Separators.QUOTE + " with priority " + newPriority + " overrides file at '" + propsUrl + Separators.QUOTE + " with priority " + priority);
                        }
                        propsUrl = url;
                        props = newProps;
                        priority = newPriority;
                    } else if (isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] Properties file at '" + url + Separators.QUOTE + " with priority " + newPriority + " does not override file at '" + propsUrl + Separators.QUOTE + " with priority " + priority);
                    }
                }
            }
        }
        if (isDiagnosticsEnabled()) {
            if (props == null) {
                logDiagnostic("[LOOKUP] No properties file of name '" + fileName + "' found.");
            } else {
                logDiagnostic("[LOOKUP] Properties file of name '" + fileName + "' found at '" + propsUrl + '\"');
            }
        }
        return props;
    }

    private static void initDiagnostics() {
        String classLoaderName;
        try {
            String dest = System.getProperty(DIAGNOSTICS_DEST_PROPERTY);
            if (dest == null) {
                return;
            }
            if (dest.equals("STDOUT")) {
                diagnosticsStream = System.out;
            } else if (dest.equals("STDERR")) {
                diagnosticsStream = System.err;
            } else {
                try {
                    FileOutputStream fos = new FileOutputStream(dest, true);
                    diagnosticsStream = new PrintStream(fos);
                } catch (IOException e) {
                    return;
                }
            }
            try {
                ClassLoader classLoader = thisClassLoader;
                if (thisClassLoader == null) {
                    classLoaderName = "BOOTLOADER";
                } else {
                    classLoaderName = objectId(classLoader);
                }
            } catch (SecurityException e2) {
                classLoaderName = IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
            }
            diagnosticPrefix = "[LogFactory from " + classLoaderName + "] ";
        } catch (SecurityException e3) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isDiagnosticsEnabled() {
        return diagnosticsStream != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void logDiagnostic(String msg) {
        if (diagnosticsStream != null) {
            diagnosticsStream.print(diagnosticPrefix);
            diagnosticsStream.println(msg);
            diagnosticsStream.flush();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final void logRawDiagnostic(String msg) {
        if (diagnosticsStream != null) {
            diagnosticsStream.println(msg);
            diagnosticsStream.flush();
        }
    }

    private static void logClassLoaderEnvironment(Class clazz) {
        if (!isDiagnosticsEnabled()) {
            return;
        }
        try {
            logDiagnostic("[ENV] Extension directories (java.ext.dir): " + System.getProperty("java.ext.dir"));
            logDiagnostic("[ENV] Application classpath (java.class.path): " + System.getProperty("java.class.path"));
        } catch (SecurityException e) {
            logDiagnostic("[ENV] Security setting prevent interrogation of system classpaths.");
        }
        String className = clazz.getName();
        try {
            ClassLoader classLoader = getClassLoader(clazz);
            logDiagnostic("[ENV] Class " + className + " was loaded via classloader " + objectId(classLoader));
            logHierarchy("[ENV] Ancestry of classloader which loaded " + className + " is ", classLoader);
        } catch (SecurityException e2) {
            logDiagnostic("[ENV] Security forbids determining the classloader for " + className);
        }
    }

    private static void logHierarchy(String prefix, ClassLoader classLoader) {
        if (!isDiagnosticsEnabled()) {
            return;
        }
        if (classLoader != null) {
            String classLoaderString = classLoader.toString();
            logDiagnostic(prefix + objectId(classLoader) + " == '" + classLoaderString + Separators.QUOTE);
        }
        try {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            if (classLoader != null) {
                StringBuffer buf = new StringBuffer(prefix + "ClassLoader tree:");
                do {
                    buf.append(objectId(classLoader));
                    if (classLoader == systemClassLoader) {
                        buf.append(" (SYSTEM) ");
                    }
                    try {
                        classLoader = classLoader.getParent();
                        buf.append(" --> ");
                    } catch (SecurityException e) {
                        buf.append(" --> SECRET");
                    }
                } while (classLoader != null);
                buf.append("BOOT");
                logDiagnostic(buf.toString());
            }
        } catch (SecurityException e2) {
            logDiagnostic(prefix + "Security forbids determining the system classloader.");
        }
    }

    public static String objectId(Object o) {
        if (o == null) {
            return "null";
        }
        return o.getClass().getName() + Separators.AT + System.identityHashCode(o);
    }
}