package com.android.server.pm;

import android.content.pm.PackageStats;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Slog;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: Installer.class */
public final class Installer {
    private static final String TAG = "Installer";
    private static final boolean LOCAL_DEBUG = false;
    InputStream mIn;
    OutputStream mOut;
    LocalSocket mSocket;
    byte[] buf = new byte[1024];
    int buflen = 0;

    private boolean connect() {
        if (this.mSocket != null) {
            return true;
        }
        Slog.i(TAG, "connecting...");
        try {
            this.mSocket = new LocalSocket();
            LocalSocketAddress address = new LocalSocketAddress("installd", LocalSocketAddress.Namespace.RESERVED);
            this.mSocket.connect(address);
            this.mIn = this.mSocket.getInputStream();
            this.mOut = this.mSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            disconnect();
            return false;
        }
    }

    private void disconnect() {
        Slog.i(TAG, "disconnecting...");
        try {
            if (this.mSocket != null) {
                this.mSocket.close();
            }
        } catch (IOException e) {
        }
        try {
            if (this.mIn != null) {
                this.mIn.close();
            }
        } catch (IOException e2) {
        }
        try {
            if (this.mOut != null) {
                this.mOut.close();
            }
        } catch (IOException e3) {
        }
        this.mSocket = null;
        this.mIn = null;
        this.mOut = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0020, code lost:
        android.util.Slog.e(com.android.server.pm.Installer.TAG, "read error " + r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean readBytes(byte[] r7, int r8) {
        /*
            r6 = this;
            r0 = 0
            r9 = r0
            r0 = r8
            if (r0 >= 0) goto L8
            r0 = 0
            return r0
        L8:
            r0 = r9
            r1 = r8
            if (r0 == r1) goto L52
            r0 = r6
            java.io.InputStream r0 = r0.mIn     // Catch: java.io.IOException -> L45
            r1 = r7
            r2 = r9
            r3 = r8
            r4 = r9
            int r3 = r3 - r4
            int r0 = r0.read(r1, r2, r3)     // Catch: java.io.IOException -> L45
            r10 = r0
            r0 = r10
            if (r0 > 0) goto L3d
            java.lang.String r0 = "Installer"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.io.IOException -> L45
            r2 = r1
            r2.<init>()     // Catch: java.io.IOException -> L45
            java.lang.String r2 = "read error "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> L45
            r2 = r10
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> L45
            java.lang.String r1 = r1.toString()     // Catch: java.io.IOException -> L45
            int r0 = android.util.Slog.e(r0, r1)     // Catch: java.io.IOException -> L45
            goto L52
        L3d:
            r0 = r9
            r1 = r10
            int r0 = r0 + r1
            r9 = r0
            goto L8
        L45:
            r11 = move-exception
            java.lang.String r0 = "Installer"
            java.lang.String r1 = "read exception"
            int r0 = android.util.Slog.e(r0, r1)
            goto L52
        L52:
            r0 = r9
            r1 = r8
            if (r0 != r1) goto L59
            r0 = 1
            return r0
        L59:
            r0 = r6
            r0.disconnect()
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Installer.readBytes(byte[], int):boolean");
    }

    private boolean readReply() {
        this.buflen = 0;
        if (!readBytes(this.buf, 2)) {
            return false;
        }
        int len = (this.buf[0] & 255) | ((this.buf[1] & 255) << 8);
        if (len < 1 || len > 1024) {
            Slog.e(TAG, "invalid reply length (" + len + Separators.RPAREN);
            disconnect();
            return false;
        } else if (!readBytes(this.buf, len)) {
            return false;
        } else {
            this.buflen = len;
            return true;
        }
    }

    private boolean writeCommand(String _cmd) {
        byte[] cmd = _cmd.getBytes();
        int len = cmd.length;
        if (len < 1 || len > 1024) {
            return false;
        }
        this.buf[0] = (byte) (len & 255);
        this.buf[1] = (byte) ((len >> 8) & 255);
        try {
            this.mOut.write(this.buf, 0, 2);
            this.mOut.write(cmd, 0, len);
            return true;
        } catch (IOException e) {
            Slog.e(TAG, "write error");
            disconnect();
            return false;
        }
    }

    private synchronized String transaction(String cmd) {
        if (!connect()) {
            Slog.e(TAG, "connection failed");
            return "-1";
        }
        if (!writeCommand(cmd)) {
            Slog.e(TAG, "write command failed? reconnect!");
            if (!connect() || !writeCommand(cmd)) {
                return "-1";
            }
        }
        if (readReply()) {
            String s = new String(this.buf, 0, this.buflen);
            return s;
        }
        return "-1";
    }

    private int execute(String cmd) {
        String res = transaction(cmd);
        try {
            return Integer.parseInt(res);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int install(String name, int uid, int gid, String seinfo) {
        StringBuilder builder = new StringBuilder("install");
        builder.append(' ');
        builder.append(name);
        builder.append(' ');
        builder.append(uid);
        builder.append(' ');
        builder.append(gid);
        builder.append(' ');
        builder.append(seinfo != null ? seinfo : "!");
        return execute(builder.toString());
    }

    public int dexopt(String apkPath, int uid, boolean isPublic) {
        StringBuilder builder = new StringBuilder("dexopt");
        builder.append(' ');
        builder.append(apkPath);
        builder.append(' ');
        builder.append(uid);
        builder.append(isPublic ? " 1" : " 0");
        return execute(builder.toString());
    }

    public int movedex(String srcPath, String dstPath) {
        return execute("movedex " + srcPath + ' ' + dstPath);
    }

    public int rmdex(String codePath) {
        return execute("rmdex " + codePath);
    }

    public int remove(String name, int userId) {
        return execute("remove " + name + ' ' + userId);
    }

    public int rename(String oldname, String newname) {
        return execute("rename " + oldname + ' ' + newname);
    }

    public int fixUid(String name, int uid, int gid) {
        return execute("fixuid " + name + ' ' + uid + ' ' + gid);
    }

    public int deleteCacheFiles(String name, int userId) {
        return execute("rmcache " + name + ' ' + userId);
    }

    public int createUserData(String name, int uid, int userId) {
        return execute("mkuserdata " + name + ' ' + uid + ' ' + userId);
    }

    public int removeUserDataDirs(int userId) {
        return execute("rmuser " + userId);
    }

    public int clearUserData(String name, int userId) {
        return execute("rmuserdata " + name + ' ' + userId);
    }

    public boolean ping() {
        if (execute("ping") < 0) {
            return false;
        }
        return true;
    }

    public int freeCache(long freeStorageSize) {
        return execute("freecache " + String.valueOf(freeStorageSize));
    }

    public int getSizeInfo(String pkgName, int persona, String apkPath, String libDirPath, String fwdLockApkPath, String asecPath, PackageStats pStats) {
        StringBuilder builder = new StringBuilder("getsize");
        builder.append(' ');
        builder.append(pkgName);
        builder.append(' ');
        builder.append(persona);
        builder.append(' ');
        builder.append(apkPath);
        builder.append(' ');
        builder.append(libDirPath != null ? libDirPath : "!");
        builder.append(' ');
        builder.append(fwdLockApkPath != null ? fwdLockApkPath : "!");
        builder.append(' ');
        builder.append(asecPath != null ? asecPath : "!");
        String s = transaction(builder.toString());
        String[] res = s.split(Separators.SP);
        if (res == null || res.length != 5) {
            return -1;
        }
        try {
            pStats.codeSize = Long.parseLong(res[1]);
            pStats.dataSize = Long.parseLong(res[2]);
            pStats.cacheSize = Long.parseLong(res[3]);
            pStats.externalCodeSize = Long.parseLong(res[4]);
            return Integer.parseInt(res[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int moveFiles() {
        return execute("movefiles");
    }

    public int linkNativeLibraryDirectory(String dataPath, String nativeLibPath, int userId) {
        if (dataPath == null) {
            Slog.e(TAG, "linkNativeLibraryDirectory dataPath is null");
            return -1;
        } else if (nativeLibPath == null) {
            Slog.e(TAG, "linkNativeLibraryDirectory nativeLibPath is null");
            return -1;
        } else {
            return execute("linklib " + dataPath + ' ' + nativeLibPath + ' ' + userId);
        }
    }
}