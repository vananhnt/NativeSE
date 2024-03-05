package com.android.server;

import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import dalvik.system.SocketTagger;
import java.io.FileDescriptor;
import java.net.SocketException;

/* loaded from: NetworkManagementSocketTagger.class */
public final class NetworkManagementSocketTagger extends SocketTagger {
    private static final String TAG = "NetworkManagementSocketTagger";
    private static final boolean LOGD = false;
    public static final String PROP_QTAGUID_ENABLED = "net.qtaguid_enabled";
    private static ThreadLocal<SocketTags> threadSocketTags = new ThreadLocal<SocketTags>() { // from class: com.android.server.NetworkManagementSocketTagger.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public SocketTags initialValue() {
            return new SocketTags();
        }
    };

    /* loaded from: NetworkManagementSocketTagger$SocketTags.class */
    public static class SocketTags {
        public int statsTag = -1;
        public int statsUid = -1;
    }

    private static native int native_tagSocketFd(FileDescriptor fileDescriptor, int i, int i2);

    private static native int native_untagSocketFd(FileDescriptor fileDescriptor);

    private static native int native_setCounterSet(int i, int i2);

    private static native int native_deleteTagData(int i, int i2);

    public static void install() {
        SocketTagger.set(new NetworkManagementSocketTagger());
    }

    public static void setThreadSocketStatsTag(int tag) {
        threadSocketTags.get().statsTag = tag;
    }

    public static int getThreadSocketStatsTag() {
        return threadSocketTags.get().statsTag;
    }

    public static void setThreadSocketStatsUid(int uid) {
        threadSocketTags.get().statsUid = uid;
    }

    public void tag(FileDescriptor fd) throws SocketException {
        SocketTags options = threadSocketTags.get();
        tagSocketFd(fd, options.statsTag, options.statsUid);
    }

    private void tagSocketFd(FileDescriptor fd, int tag, int uid) {
        int errno;
        if ((tag != -1 || uid != -1) && SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false) && (errno = native_tagSocketFd(fd, tag, uid)) < 0) {
            Log.i(TAG, "tagSocketFd(" + fd.getInt$() + ", " + tag + ", " + uid + ") failed with errno" + errno);
        }
    }

    public void untag(FileDescriptor fd) throws SocketException {
        unTagSocketFd(fd);
    }

    private void unTagSocketFd(FileDescriptor fd) {
        int errno;
        SocketTags options = threadSocketTags.get();
        if ((options.statsTag != -1 || options.statsUid != -1) && SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false) && (errno = native_untagSocketFd(fd)) < 0) {
            Log.w(TAG, "untagSocket(" + fd.getInt$() + ") failed with errno " + errno);
        }
    }

    public static void setKernelCounterSet(int uid, int counterSet) {
        int errno;
        if (SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false) && (errno = native_setCounterSet(counterSet, uid)) < 0) {
            Log.w(TAG, "setKernelCountSet(" + uid + ", " + counterSet + ") failed with errno " + errno);
        }
    }

    public static void resetKernelUidStats(int uid) {
        int errno;
        if (SystemProperties.getBoolean(PROP_QTAGUID_ENABLED, false) && (errno = native_deleteTagData(0, uid)) < 0) {
            Slog.w(TAG, "problem clearing counters for uid " + uid + " : errno " + errno);
        }
    }

    public static int kernelToTag(String string) {
        int length = string.length();
        if (length > 10) {
            return Long.decode(string.substring(0, length - 8)).intValue();
        }
        return 0;
    }
}