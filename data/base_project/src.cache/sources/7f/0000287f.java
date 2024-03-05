package javax.xml.datatype;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/* loaded from: FactoryFinder.class */
final class FactoryFinder {
    private static final String CLASS_NAME = "javax.xml.datatype.FactoryFinder";
    private static boolean debug;
    private static Properties cacheProps = new Properties();
    private static boolean firstTime = true;
    private static final int DEFAULT_LINE_LENGTH = 80;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: javax.xml.datatype.FactoryFinder.findJarServiceProvider(java.lang.String):java.lang.Object, file: FactoryFinder.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private static java.lang.Object findJarServiceProvider(java.lang.String r0) throws javax.xml.datatype.FactoryFinder.ConfigurationError {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: javax.xml.datatype.FactoryFinder.findJarServiceProvider(java.lang.String):java.lang.Object, file: FactoryFinder.class
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.xml.datatype.FactoryFinder.findJarServiceProvider(java.lang.String):java.lang.Object");
    }

    static {
        debug = false;
        String val = System.getProperty("jaxp.debug");
        debug = (val == null || "false".equals(val)) ? false : true;
    }

    private FactoryFinder() {
    }

    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println("javax.xml.datatype.FactoryFinder:" + msg);
        }
    }

    private static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (debug) {
            debugPrintln("Using context class loader: " + classLoader);
        }
        if (classLoader == null) {
            classLoader = FactoryFinder.class.getClassLoader();
            if (debug) {
                debugPrintln("Using the class loader of FactoryFinder: " + classLoader);
            }
        }
        return classLoader;
    }

    static Object newInstance(String className, ClassLoader classLoader) throws ConfigurationError {
        Class spiClass;
        try {
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                spiClass = classLoader.loadClass(className);
            }
            if (debug) {
                debugPrintln("Loaded " + className + " from " + which(spiClass));
            }
            return spiClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw new ConfigurationError("Provider " + className + " not found", x);
        } catch (Exception x2) {
            throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object find(String factoryId, String fallbackClassName) throws ConfigurationError {
        ClassLoader classLoader = findClassLoader();
        String systemProp = System.getProperty(factoryId);
        if (systemProp != null && systemProp.length() > 0) {
            if (debug) {
                debugPrintln("found " + systemProp + " in the system property " + factoryId);
            }
            return newInstance(systemProp, classLoader);
        }
        try {
            String javah = System.getProperty("java.home");
            String configFile = javah + File.separator + "lib" + File.separator + "jaxp.properties";
            if (firstTime) {
                synchronized (cacheProps) {
                    if (firstTime) {
                        File f = new File(configFile);
                        firstTime = false;
                        if (f.exists()) {
                            if (debug) {
                                debugPrintln("Read properties file " + f);
                            }
                            cacheProps.load(new FileInputStream(f));
                        }
                    }
                }
            }
            String factoryClassName = cacheProps.getProperty(factoryId);
            if (debug) {
                debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties");
            }
            if (factoryClassName != null) {
                return newInstance(factoryClassName, classLoader);
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            }
        }
        Object provider = findJarServiceProvider(factoryId);
        if (provider != null) {
            return provider;
        }
        if (fallbackClassName == null) {
            throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
        }
        if (debug) {
            debugPrintln("loaded from fallback value: " + fallbackClassName);
        }
        return newInstance(fallbackClassName, classLoader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: FactoryFinder$ConfigurationError.class */
    public static class ConfigurationError extends Error {
        private static final long serialVersionUID = -3644413026244211347L;
        private Exception exception;

        ConfigurationError(String msg, Exception x) {
            super(msg);
            this.exception = x;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Exception getException() {
            return this.exception;
        }
    }

    private static String which(Class clazz) {
        URL it;
        try {
            String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
            ClassLoader loader = clazz.getClassLoader();
            if (loader != null) {
                it = loader.getResource(classnameAsResource);
            } else {
                it = ClassLoader.getSystemResource(classnameAsResource);
            }
            if (it != null) {
                return it.toString();
            }
            return "unknown location";
        } catch (ThreadDeath td) {
            throw td;
        } catch (VirtualMachineError vme) {
            throw vme;
        } catch (Throwable t) {
            if (debug) {
                t.printStackTrace();
                return "unknown location";
            }
            return "unknown location";
        }
    }
}