package com.android.internal.os;

import android.net.Credentials;
import android.net.LocalSocket;
import android.os.Process;
import android.os.SELinux;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.os.ZygoteInit;
import dalvik.system.PathClassLoader;
import dalvik.system.Zygote;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ZygoteConnection.class */
public class ZygoteConnection {
    private static final String TAG = "Zygote";
    private static final int[][] intArray2d = new int[0][0];
    private static final int CONNECTION_TIMEOUT_MILLIS = 1000;
    private static final int MAX_ZYGOTE_ARGC = 1024;
    private final LocalSocket mSocket;
    private final DataOutputStream mSocketOutStream;
    private final BufferedReader mSocketReader;
    private final Credentials peer;
    private final String peerSecurityContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZygoteConnection(LocalSocket socket) throws IOException {
        this.mSocket = socket;
        this.mSocketOutStream = new DataOutputStream(socket.getOutputStream());
        this.mSocketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()), 256);
        this.mSocket.setSoTimeout(1000);
        try {
            this.peer = this.mSocket.getPeerCredentials();
            this.peerSecurityContext = SELinux.getPeerContext(this.mSocket.getFileDescriptor());
        } catch (IOException ex) {
            Log.e(TAG, "Cannot read peer credentials", ex);
            throw ex;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileDescriptor getFileDesciptor() {
        return this.mSocket.getFileDescriptor();
    }

    void run() throws ZygoteInit.MethodAndArgsCaller {
        int loopCount = 10;
        do {
            if (loopCount <= 0) {
                ZygoteInit.gc();
                loopCount = 10;
            } else {
                loopCount--;
            }
        } while (!runOnce());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean runOnce() throws ZygoteInit.MethodAndArgsCaller {
        Arguments parsedArgs = null;
        try {
            String[] args = readArgumentList();
            FileDescriptor[] descriptors = this.mSocket.getAncillaryFileDescriptors();
            if (args == null) {
                closeSocket();
                return true;
            }
            PrintStream newStderr = null;
            if (descriptors != null && descriptors.length >= 3) {
                newStderr = new PrintStream(new FileOutputStream(descriptors[2]));
            }
            int pid = -1;
            FileDescriptor childPipeFd = null;
            FileDescriptor serverPipeFd = null;
            try {
                parsedArgs = new Arguments(args);
                applyUidSecurityPolicy(parsedArgs, this.peer, this.peerSecurityContext);
                applyRlimitSecurityPolicy(parsedArgs, this.peer, this.peerSecurityContext);
                applyCapabilitiesSecurityPolicy(parsedArgs, this.peer, this.peerSecurityContext);
                applyInvokeWithSecurityPolicy(parsedArgs, this.peer, this.peerSecurityContext);
                applyseInfoSecurityPolicy(parsedArgs, this.peer, this.peerSecurityContext);
                applyDebuggerSystemProperty(parsedArgs);
                applyInvokeWithSystemProperty(parsedArgs);
                int[][] rlimits = null;
                if (parsedArgs.rlimits != null) {
                    rlimits = (int[][]) parsedArgs.rlimits.toArray(intArray2d);
                }
                if (parsedArgs.runtimeInit && parsedArgs.invokeWith != null) {
                    FileDescriptor[] pipeFds = Libcore.os.pipe();
                    childPipeFd = pipeFds[1];
                    serverPipeFd = pipeFds[0];
                    ZygoteInit.setCloseOnExec(serverPipeFd, true);
                }
                pid = Zygote.forkAndSpecialize(parsedArgs.uid, parsedArgs.gid, parsedArgs.gids, parsedArgs.debugFlags, rlimits, parsedArgs.mountExternal, parsedArgs.seInfo, parsedArgs.niceName);
            } catch (ZygoteSecurityException ex) {
                logAndPrintError(newStderr, "Zygote security policy prevents request: ", ex);
            } catch (IOException ex2) {
                logAndPrintError(newStderr, "Exception creating pipe", ex2);
            } catch (IllegalArgumentException ex3) {
                logAndPrintError(newStderr, "Invalid zygote arguments", ex3);
            } catch (ErrnoException ex4) {
                logAndPrintError(newStderr, "Exception creating pipe", ex4);
            }
            try {
                if (pid == 0) {
                    IoUtils.closeQuietly(serverPipeFd);
                    handleChildProc(parsedArgs, descriptors, childPipeFd, newStderr);
                    IoUtils.closeQuietly(childPipeFd);
                    IoUtils.closeQuietly((FileDescriptor) null);
                    return true;
                }
                IoUtils.closeQuietly(childPipeFd);
                boolean handleParentProc = handleParentProc(pid, descriptors, serverPipeFd, parsedArgs);
                IoUtils.closeQuietly((FileDescriptor) null);
                IoUtils.closeQuietly(serverPipeFd);
                return handleParentProc;
            } catch (Throwable th) {
                IoUtils.closeQuietly(childPipeFd);
                IoUtils.closeQuietly(serverPipeFd);
                throw th;
            }
        } catch (IOException ex5) {
            Log.w(TAG, "IOException on command socket " + ex5.getMessage());
            closeSocket();
            return true;
        }
    }

    void closeSocket() {
        try {
            this.mSocket.close();
        } catch (IOException ex) {
            Log.e(TAG, "Exception while closing command socket in parent", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ZygoteConnection$Arguments.class */
    public static class Arguments {
        boolean uidSpecified;
        boolean gidSpecified;
        int[] gids;
        int debugFlags;
        int targetSdkVersion;
        boolean targetSdkVersionSpecified;
        String classpath;
        boolean runtimeInit;
        String niceName;
        boolean capabilitiesSpecified;
        long permittedCapabilities;
        long effectiveCapabilities;
        boolean seInfoSpecified;
        String seInfo;
        ArrayList<int[]> rlimits;
        String invokeWith;
        String[] remainingArgs;
        int uid = 0;
        int gid = 0;
        int mountExternal = 0;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Arguments(String[] args) throws IllegalArgumentException {
            parseArgs(args);
        }

        /* JADX WARN: Code restructure failed: missing block: B:125:0x036a, code lost:
            if (r6.runtimeInit == false) goto L162;
         */
        /* JADX WARN: Code restructure failed: missing block: B:127:0x0371, code lost:
            if (r6.classpath == null) goto L162;
         */
        /* JADX WARN: Code restructure failed: missing block: B:129:0x037d, code lost:
            throw new java.lang.IllegalArgumentException("--runtime-init and -classpath are incompatible");
         */
        /* JADX WARN: Code restructure failed: missing block: B:130:0x037e, code lost:
            r6.remainingArgs = new java.lang.String[r7.length - r8];
            java.lang.System.arraycopy(r7, r8, r6.remainingArgs, 0, r6.remainingArgs.length);
         */
        /* JADX WARN: Code restructure failed: missing block: B:131:0x0398, code lost:
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void parseArgs(java.lang.String[] r7) throws java.lang.IllegalArgumentException {
            /*
                Method dump skipped, instructions count: 921
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.ZygoteConnection.Arguments.parseArgs(java.lang.String[]):void");
        }
    }

    private String[] readArgumentList() throws IOException {
        try {
            String s = this.mSocketReader.readLine();
            if (s == null) {
                return null;
            }
            int argc = Integer.parseInt(s);
            if (argc > 1024) {
                throw new IOException("max arg count exceeded");
            }
            String[] result = new String[argc];
            for (int i = 0; i < argc; i++) {
                result[i] = this.mSocketReader.readLine();
                if (result[i] == null) {
                    throw new IOException("truncated request");
                }
            }
            return result;
        } catch (NumberFormatException e) {
            Log.e(TAG, "invalid Zygote wire format: non-int at argc");
            throw new IOException("invalid wire format");
        }
    }

    private static void applyUidSecurityPolicy(Arguments args, Credentials peer, String peerSecurityContext) throws ZygoteSecurityException {
        int peerUid = peer.getUid();
        if (peerUid != 0) {
            if (peerUid == 1000) {
                String factoryTest = SystemProperties.get("ro.factorytest");
                boolean uidRestricted = (factoryTest.equals("1") || factoryTest.equals("2")) ? false : true;
                if (uidRestricted && args.uidSpecified && args.uid < 1000) {
                    throw new ZygoteSecurityException("System UID may not launch process with UID < 1000");
                }
            } else if (args.uidSpecified || args.gidSpecified || args.gids != null) {
                throw new ZygoteSecurityException("App UIDs may not specify uid's or gid's");
            }
        }
        if (args.uidSpecified || args.gidSpecified || args.gids != null) {
            boolean allowed = SELinux.checkSELinuxAccess(peerSecurityContext, peerSecurityContext, "zygote", "specifyids");
            if (!allowed) {
                throw new ZygoteSecurityException("Peer may not specify uid's or gid's");
            }
        }
        if (!args.uidSpecified) {
            args.uid = peer.getUid();
            args.uidSpecified = true;
        }
        if (!args.gidSpecified) {
            args.gid = peer.getGid();
            args.gidSpecified = true;
        }
    }

    public static void applyDebuggerSystemProperty(Arguments args) {
        if ("1".equals(SystemProperties.get("ro.debuggable"))) {
            args.debugFlags |= 1;
        }
    }

    private static void applyRlimitSecurityPolicy(Arguments args, Credentials peer, String peerSecurityContext) throws ZygoteSecurityException {
        int peerUid = peer.getUid();
        if (peerUid != 0 && peerUid != 1000 && args.rlimits != null) {
            throw new ZygoteSecurityException("This UID may not specify rlimits.");
        }
        if (args.rlimits != null) {
            boolean allowed = SELinux.checkSELinuxAccess(peerSecurityContext, peerSecurityContext, "zygote", "specifyrlimits");
            if (!allowed) {
                throw new ZygoteSecurityException("Peer may not specify rlimits");
            }
        }
    }

    private static void applyCapabilitiesSecurityPolicy(Arguments args, Credentials peer, String peerSecurityContext) throws ZygoteSecurityException {
        if (args.permittedCapabilities == 0 && args.effectiveCapabilities == 0) {
            return;
        }
        boolean allowed = SELinux.checkSELinuxAccess(peerSecurityContext, peerSecurityContext, "zygote", "specifycapabilities");
        if (!allowed) {
            throw new ZygoteSecurityException("Peer may not specify capabilities");
        }
        if (peer.getUid() == 0) {
            return;
        }
        try {
            long permittedCaps = ZygoteInit.capgetPermitted(peer.getPid());
            if (((args.permittedCapabilities ^ (-1)) & args.effectiveCapabilities) != 0) {
                throw new ZygoteSecurityException("Effective capabilities cannot be superset of  permitted capabilities");
            }
            if (((permittedCaps ^ (-1)) & args.permittedCapabilities) != 0) {
                throw new ZygoteSecurityException("Peer specified unpermitted capabilities");
            }
        } catch (IOException e) {
            throw new ZygoteSecurityException("Error retrieving peer's capabilities.");
        }
    }

    private static void applyInvokeWithSecurityPolicy(Arguments args, Credentials peer, String peerSecurityContext) throws ZygoteSecurityException {
        int peerUid = peer.getUid();
        if (args.invokeWith != null && peerUid != 0) {
            throw new ZygoteSecurityException("Peer is not permitted to specify an explicit invoke-with wrapper command");
        }
        if (args.invokeWith != null) {
            boolean allowed = SELinux.checkSELinuxAccess(peerSecurityContext, peerSecurityContext, "zygote", "specifyinvokewith");
            if (!allowed) {
                throw new ZygoteSecurityException("Peer is not permitted to specify an explicit invoke-with wrapper command");
            }
        }
    }

    private static void applyseInfoSecurityPolicy(Arguments args, Credentials peer, String peerSecurityContext) throws ZygoteSecurityException {
        int peerUid = peer.getUid();
        if (args.seInfo == null) {
            return;
        }
        if (peerUid != 0 && peerUid != 1000) {
            throw new ZygoteSecurityException("This UID may not specify SEAndroid info.");
        }
        boolean allowed = SELinux.checkSELinuxAccess(peerSecurityContext, peerSecurityContext, "zygote", "specifyseinfo");
        if (!allowed) {
            throw new ZygoteSecurityException("Peer may not specify SEAndroid info");
        }
    }

    public static void applyInvokeWithSystemProperty(Arguments args) {
        if (args.invokeWith == null && args.niceName != null && args.niceName != null) {
            String property = "wrap." + args.niceName;
            if (property.length() > 31) {
                property = property.substring(0, 31);
            }
            args.invokeWith = SystemProperties.get(property);
            if (args.invokeWith != null && args.invokeWith.length() == 0) {
                args.invokeWith = null;
            }
        }
    }

    private void handleChildProc(Arguments parsedArgs, FileDescriptor[] descriptors, FileDescriptor pipeFd, PrintStream newStderr) throws ZygoteInit.MethodAndArgsCaller {
        ClassLoader cloader;
        closeSocket();
        ZygoteInit.closeServerSocket();
        if (descriptors != null) {
            try {
                ZygoteInit.reopenStdio(descriptors[0], descriptors[1], descriptors[2]);
                for (FileDescriptor fd : descriptors) {
                    IoUtils.closeQuietly(fd);
                }
                newStderr = System.err;
            } catch (IOException ex) {
                Log.e(TAG, "Error reopening stdio", ex);
            }
        }
        if (parsedArgs.niceName != null) {
            Process.setArgV0(parsedArgs.niceName);
        }
        if (parsedArgs.runtimeInit) {
            if (parsedArgs.invokeWith != null) {
                WrapperInit.execApplication(parsedArgs.invokeWith, parsedArgs.niceName, parsedArgs.targetSdkVersion, pipeFd, parsedArgs.remainingArgs);
                return;
            } else {
                RuntimeInit.zygoteInit(parsedArgs.targetSdkVersion, parsedArgs.remainingArgs);
                return;
            }
        }
        try {
            String className = parsedArgs.remainingArgs[0];
            String[] mainArgs = new String[parsedArgs.remainingArgs.length - 1];
            System.arraycopy(parsedArgs.remainingArgs, 1, mainArgs, 0, mainArgs.length);
            if (parsedArgs.invokeWith != null) {
                WrapperInit.execStandalone(parsedArgs.invokeWith, parsedArgs.classpath, className, mainArgs);
                return;
            }
            if (parsedArgs.classpath != null) {
                cloader = new PathClassLoader(parsedArgs.classpath, ClassLoader.getSystemClassLoader());
            } else {
                cloader = ClassLoader.getSystemClassLoader();
            }
            try {
                ZygoteInit.invokeStaticMain(cloader, className, mainArgs);
            } catch (RuntimeException ex2) {
                logAndPrintError(newStderr, "Error starting.", ex2);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logAndPrintError(newStderr, "Missing required class name argument", null);
        }
    }

    private boolean handleParentProc(int pid, FileDescriptor[] descriptors, FileDescriptor pipeFd, Arguments parsedArgs) {
        int parentPid;
        if (pid > 0) {
            setChildPgid(pid);
        }
        if (descriptors != null) {
            for (FileDescriptor fd : descriptors) {
                IoUtils.closeQuietly(fd);
            }
        }
        boolean usingWrapper = false;
        if (pipeFd != null && pid > 0) {
            DataInputStream is = new DataInputStream(new FileInputStream(pipeFd));
            int innerPid = -1;
            try {
                try {
                    innerPid = is.readInt();
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                } catch (Throwable th) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                    throw th;
                }
            } catch (IOException ex) {
                Log.w(TAG, "Error reading pid from wrapped process, child may have died", ex);
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
            if (innerPid > 0) {
                int i = innerPid;
                while (true) {
                    parentPid = i;
                    if (parentPid <= 0 || parentPid == pid) {
                        break;
                    }
                    i = Process.getParentPid(parentPid);
                }
                if (parentPid > 0) {
                    Log.i(TAG, "Wrapped process has pid " + innerPid);
                    pid = innerPid;
                    usingWrapper = true;
                } else {
                    Log.w(TAG, "Wrapped process reported a pid that is not a child of the process that we forked: childPid=" + pid + " innerPid=" + innerPid);
                }
            }
        }
        try {
            this.mSocketOutStream.writeInt(pid);
            this.mSocketOutStream.writeBoolean(usingWrapper);
            return false;
        } catch (IOException ex2) {
            Log.e(TAG, "Error reading from command socket", ex2);
            return true;
        }
    }

    private void setChildPgid(int pid) {
        try {
            ZygoteInit.setpgid(pid, ZygoteInit.getpgid(this.peer.getPid()));
        } catch (IOException e) {
            Log.i(TAG, "Zygote: setpgid failed. This is normal if peer is not in our session");
        }
    }

    private static void logAndPrintError(PrintStream newStderr, String message, Throwable ex) {
        Log.e(TAG, message, ex);
        if (newStderr != null) {
            newStderr.println(message + (ex == null ? "" : ex));
        }
    }
}