package android.app;

import android.app.IThumbnailRetriever;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.app.IUsageStats;
import com.android.internal.app.ProcessStats;
import com.android.internal.os.PkgUsageStats;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.FastPrintWriter;
import com.android.server.am.UsageStatsService;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: ActivityManager.class */
public class ActivityManager {
    private static String TAG = "ActivityManager";
    private static boolean localLOGV = false;
    private final Context mContext;
    private final Handler mHandler;
    public static final String META_HOME_ALTERNATE = "android.app.home.alternate";
    public static final int START_CANCELED = -6;
    public static final int START_NOT_ACTIVITY = -5;
    public static final int START_PERMISSION_DENIED = -4;
    public static final int START_FORWARD_AND_REQUEST_CONFLICT = -3;
    public static final int START_CLASS_NOT_FOUND = -2;
    public static final int START_INTENT_NOT_RESOLVED = -1;
    public static final int START_SUCCESS = 0;
    public static final int START_RETURN_INTENT_TO_CALLER = 1;
    public static final int START_TASK_TO_FRONT = 2;
    public static final int START_DELIVERED_TO_TOP = 3;
    public static final int START_SWITCHES_CANCELED = 4;
    public static final int START_FLAG_ONLY_IF_NEEDED = 1;
    public static final int START_FLAG_DEBUG = 2;
    public static final int START_FLAG_OPENGL_TRACES = 4;
    public static final int START_FLAG_AUTO_STOP_PROFILER = 8;
    public static final int BROADCAST_SUCCESS = 0;
    public static final int BROADCAST_STICKY_CANT_HAVE_PERMISSION = -1;
    public static final int INTENT_SENDER_BROADCAST = 1;
    public static final int INTENT_SENDER_ACTIVITY = 2;
    public static final int INTENT_SENDER_ACTIVITY_RESULT = 3;
    public static final int INTENT_SENDER_SERVICE = 4;
    public static final int USER_OP_SUCCESS = 0;
    public static final int USER_OP_UNKNOWN_USER = -1;
    public static final int USER_OP_IS_CURRENT = -2;
    public static final int PROCESS_STATE_PERSISTENT = 0;
    public static final int PROCESS_STATE_PERSISTENT_UI = 1;
    public static final int PROCESS_STATE_TOP = 2;
    public static final int PROCESS_STATE_IMPORTANT_FOREGROUND = 3;
    public static final int PROCESS_STATE_IMPORTANT_BACKGROUND = 4;
    public static final int PROCESS_STATE_BACKUP = 5;
    public static final int PROCESS_STATE_HEAVY_WEIGHT = 6;
    public static final int PROCESS_STATE_SERVICE = 7;
    public static final int PROCESS_STATE_RECEIVER = 8;
    public static final int PROCESS_STATE_HOME = 9;
    public static final int PROCESS_STATE_LAST_ACTIVITY = 10;
    public static final int PROCESS_STATE_CACHED_ACTIVITY = 11;
    public static final int PROCESS_STATE_CACHED_ACTIVITY_CLIENT = 12;
    public static final int PROCESS_STATE_CACHED_EMPTY = 13;
    public static final int COMPAT_MODE_ALWAYS = -1;
    public static final int COMPAT_MODE_NEVER = -2;
    public static final int COMPAT_MODE_UNKNOWN = -3;
    public static final int COMPAT_MODE_DISABLED = 0;
    public static final int COMPAT_MODE_ENABLED = 1;
    public static final int COMPAT_MODE_TOGGLE = 2;
    public static final int RECENT_WITH_EXCLUDED = 1;
    public static final int RECENT_IGNORE_UNAVAILABLE = 2;
    public static final int REMOVE_TASK_KILL_PROCESS = 1;
    public static final int MOVE_TASK_WITH_HOME = 1;
    public static final int MOVE_TASK_NO_USER_ACTION = 2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public int getFrontActivityScreenCompatMode() {
        try {
            return ActivityManagerNative.getDefault().getFrontActivityScreenCompatMode();
        } catch (RemoteException e) {
            return 0;
        }
    }

    public void setFrontActivityScreenCompatMode(int mode) {
        try {
            ActivityManagerNative.getDefault().setFrontActivityScreenCompatMode(mode);
        } catch (RemoteException e) {
        }
    }

    public int getPackageScreenCompatMode(String packageName) {
        try {
            return ActivityManagerNative.getDefault().getPackageScreenCompatMode(packageName);
        } catch (RemoteException e) {
            return 0;
        }
    }

    public void setPackageScreenCompatMode(String packageName, int mode) {
        try {
            ActivityManagerNative.getDefault().setPackageScreenCompatMode(packageName, mode);
        } catch (RemoteException e) {
        }
    }

    public boolean getPackageAskScreenCompat(String packageName) {
        try {
            return ActivityManagerNative.getDefault().getPackageAskScreenCompat(packageName);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setPackageAskScreenCompat(String packageName, boolean ask) {
        try {
            ActivityManagerNative.getDefault().setPackageAskScreenCompat(packageName, ask);
        } catch (RemoteException e) {
        }
    }

    public int getMemoryClass() {
        return staticGetMemoryClass();
    }

    public static int staticGetMemoryClass() {
        String vmHeapSize = SystemProperties.get("dalvik.vm.heapgrowthlimit", "");
        if (vmHeapSize != null && !"".equals(vmHeapSize)) {
            return Integer.parseInt(vmHeapSize.substring(0, vmHeapSize.length() - 1));
        }
        return staticGetLargeMemoryClass();
    }

    public int getLargeMemoryClass() {
        return staticGetLargeMemoryClass();
    }

    public static int staticGetLargeMemoryClass() {
        String vmHeapSize = SystemProperties.get("dalvik.vm.heapsize", "16m");
        return Integer.parseInt(vmHeapSize.substring(0, vmHeapSize.length() - 1));
    }

    public boolean isLowRamDevice() {
        return isLowRamDeviceStatic();
    }

    public static boolean isLowRamDeviceStatic() {
        return "true".equals(SystemProperties.get("ro.config.low_ram", "false"));
    }

    public static boolean isHighEndGfx() {
        return (isLowRamDeviceStatic() || Resources.getSystem().getBoolean(R.bool.config_avoidGfxAccel)) ? false : true;
    }

    /* loaded from: ActivityManager$RecentTaskInfo.class */
    public static class RecentTaskInfo implements Parcelable {
        public int id;
        public int persistentId;
        public Intent baseIntent;
        public ComponentName origActivity;
        public CharSequence description;
        public int stackId;
        public static final Parcelable.Creator<RecentTaskInfo> CREATOR = new Parcelable.Creator<RecentTaskInfo>() { // from class: android.app.ActivityManager.RecentTaskInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecentTaskInfo createFromParcel(Parcel source) {
                return new RecentTaskInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecentTaskInfo[] newArray(int size) {
                return new RecentTaskInfo[size];
            }
        };

        public RecentTaskInfo() {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.persistentId);
            if (this.baseIntent != null) {
                dest.writeInt(1);
                this.baseIntent.writeToParcel(dest, 0);
            } else {
                dest.writeInt(0);
            }
            ComponentName.writeToParcel(this.origActivity, dest);
            TextUtils.writeToParcel(this.description, dest, 1);
            dest.writeInt(this.stackId);
        }

        public void readFromParcel(Parcel source) {
            this.id = source.readInt();
            this.persistentId = source.readInt();
            if (source.readInt() != 0) {
                this.baseIntent = Intent.CREATOR.createFromParcel(source);
            } else {
                this.baseIntent = null;
            }
            this.origActivity = ComponentName.readFromParcel(source);
            this.description = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            this.stackId = source.readInt();
        }

        private RecentTaskInfo(Parcel source) {
            readFromParcel(source);
        }
    }

    public List<RecentTaskInfo> getRecentTasks(int maxNum, int flags) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getRecentTasks(maxNum, flags, UserHandle.myUserId());
        } catch (RemoteException e) {
            return null;
        }
    }

    public List<RecentTaskInfo> getRecentTasksForUser(int maxNum, int flags, int userId) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getRecentTasks(maxNum, flags, userId);
        } catch (RemoteException e) {
            return null;
        }
    }

    /* loaded from: ActivityManager$RunningTaskInfo.class */
    public static class RunningTaskInfo implements Parcelable {
        public int id;
        public ComponentName baseActivity;
        public ComponentName topActivity;
        public Bitmap thumbnail;
        public CharSequence description;
        public int numActivities;
        public int numRunning;
        public long lastActiveTime;
        public static final Parcelable.Creator<RunningTaskInfo> CREATOR = new Parcelable.Creator<RunningTaskInfo>() { // from class: android.app.ActivityManager.RunningTaskInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningTaskInfo createFromParcel(Parcel source) {
                return new RunningTaskInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningTaskInfo[] newArray(int size) {
                return new RunningTaskInfo[size];
            }
        };

        public RunningTaskInfo() {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            ComponentName.writeToParcel(this.baseActivity, dest);
            ComponentName.writeToParcel(this.topActivity, dest);
            if (this.thumbnail != null) {
                dest.writeInt(1);
                this.thumbnail.writeToParcel(dest, 0);
            } else {
                dest.writeInt(0);
            }
            TextUtils.writeToParcel(this.description, dest, 1);
            dest.writeInt(this.numActivities);
            dest.writeInt(this.numRunning);
        }

        public void readFromParcel(Parcel source) {
            this.id = source.readInt();
            this.baseActivity = ComponentName.readFromParcel(source);
            this.topActivity = ComponentName.readFromParcel(source);
            if (source.readInt() != 0) {
                this.thumbnail = Bitmap.CREATOR.createFromParcel(source);
            } else {
                this.thumbnail = null;
            }
            this.description = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            this.numActivities = source.readInt();
            this.numRunning = source.readInt();
        }

        private RunningTaskInfo(Parcel source) {
            readFromParcel(source);
        }
    }

    public List<RunningTaskInfo> getRunningTasks(int maxNum, int flags, IThumbnailReceiver receiver) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getTasks(maxNum, flags, receiver);
        } catch (RemoteException e) {
            return null;
        }
    }

    public List<RunningTaskInfo> getRunningTasks(int maxNum) throws SecurityException {
        return getRunningTasks(maxNum, 0, null);
    }

    public boolean removeSubTask(int taskId, int subTaskIndex) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().removeSubTask(taskId, subTaskIndex);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean removeTask(int taskId, int flags) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().removeTask(taskId, flags);
        } catch (RemoteException e) {
            return false;
        }
    }

    /* loaded from: ActivityManager$TaskThumbnails.class */
    public static class TaskThumbnails implements Parcelable {
        public Bitmap mainThumbnail;
        public int numSubThumbbails;
        public IThumbnailRetriever retriever;
        public static final Parcelable.Creator<TaskThumbnails> CREATOR = new Parcelable.Creator<TaskThumbnails>() { // from class: android.app.ActivityManager.TaskThumbnails.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TaskThumbnails createFromParcel(Parcel source) {
                return new TaskThumbnails(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TaskThumbnails[] newArray(int size) {
                return new TaskThumbnails[size];
            }
        };

        public TaskThumbnails() {
        }

        public Bitmap getSubThumbnail(int index) {
            try {
                return this.retriever.getThumbnail(index);
            } catch (RemoteException e) {
                return null;
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            if (this.mainThumbnail != null) {
                dest.writeInt(1);
                this.mainThumbnail.writeToParcel(dest, 0);
            } else {
                dest.writeInt(0);
            }
            dest.writeInt(this.numSubThumbbails);
            dest.writeStrongInterface(this.retriever);
        }

        public void readFromParcel(Parcel source) {
            if (source.readInt() != 0) {
                this.mainThumbnail = Bitmap.CREATOR.createFromParcel(source);
            } else {
                this.mainThumbnail = null;
            }
            this.numSubThumbbails = source.readInt();
            this.retriever = IThumbnailRetriever.Stub.asInterface(source.readStrongBinder());
        }

        private TaskThumbnails(Parcel source) {
            readFromParcel(source);
        }
    }

    public TaskThumbnails getTaskThumbnails(int id) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getTaskThumbnails(id);
        } catch (RemoteException e) {
            return null;
        }
    }

    public Bitmap getTaskTopThumbnail(int id) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getTaskTopThumbnail(id);
        } catch (RemoteException e) {
            return null;
        }
    }

    public void moveTaskToFront(int taskId, int flags) {
        moveTaskToFront(taskId, flags, null);
    }

    public void moveTaskToFront(int taskId, int flags, Bundle options) {
        try {
            ActivityManagerNative.getDefault().moveTaskToFront(taskId, flags, options);
        } catch (RemoteException e) {
        }
    }

    /* loaded from: ActivityManager$RunningServiceInfo.class */
    public static class RunningServiceInfo implements Parcelable {
        public ComponentName service;
        public int pid;
        public int uid;
        public String process;
        public boolean foreground;
        public long activeSince;
        public boolean started;
        public int clientCount;
        public int crashCount;
        public long lastActivityTime;
        public long restarting;
        public static final int FLAG_STARTED = 1;
        public static final int FLAG_FOREGROUND = 2;
        public static final int FLAG_SYSTEM_PROCESS = 4;
        public static final int FLAG_PERSISTENT_PROCESS = 8;
        public int flags;
        public String clientPackage;
        public int clientLabel;
        public static final Parcelable.Creator<RunningServiceInfo> CREATOR = new Parcelable.Creator<RunningServiceInfo>() { // from class: android.app.ActivityManager.RunningServiceInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningServiceInfo createFromParcel(Parcel source) {
                return new RunningServiceInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningServiceInfo[] newArray(int size) {
                return new RunningServiceInfo[size];
            }
        };

        public RunningServiceInfo() {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            ComponentName.writeToParcel(this.service, dest);
            dest.writeInt(this.pid);
            dest.writeInt(this.uid);
            dest.writeString(this.process);
            dest.writeInt(this.foreground ? 1 : 0);
            dest.writeLong(this.activeSince);
            dest.writeInt(this.started ? 1 : 0);
            dest.writeInt(this.clientCount);
            dest.writeInt(this.crashCount);
            dest.writeLong(this.lastActivityTime);
            dest.writeLong(this.restarting);
            dest.writeInt(this.flags);
            dest.writeString(this.clientPackage);
            dest.writeInt(this.clientLabel);
        }

        public void readFromParcel(Parcel source) {
            this.service = ComponentName.readFromParcel(source);
            this.pid = source.readInt();
            this.uid = source.readInt();
            this.process = source.readString();
            this.foreground = source.readInt() != 0;
            this.activeSince = source.readLong();
            this.started = source.readInt() != 0;
            this.clientCount = source.readInt();
            this.crashCount = source.readInt();
            this.lastActivityTime = source.readLong();
            this.restarting = source.readLong();
            this.flags = source.readInt();
            this.clientPackage = source.readString();
            this.clientLabel = source.readInt();
        }

        private RunningServiceInfo(Parcel source) {
            readFromParcel(source);
        }
    }

    public List<RunningServiceInfo> getRunningServices(int maxNum) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getServices(maxNum, 0);
        } catch (RemoteException e) {
            return null;
        }
    }

    public PendingIntent getRunningServiceControlPanel(ComponentName service) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().getRunningServiceControlPanel(service);
        } catch (RemoteException e) {
            return null;
        }
    }

    /* loaded from: ActivityManager$MemoryInfo.class */
    public static class MemoryInfo implements Parcelable {
        public long availMem;
        public long totalMem;
        public long threshold;
        public boolean lowMemory;
        public long hiddenAppThreshold;
        public long secondaryServerThreshold;
        public long visibleAppThreshold;
        public long foregroundAppThreshold;
        public static final Parcelable.Creator<MemoryInfo> CREATOR = new Parcelable.Creator<MemoryInfo>() { // from class: android.app.ActivityManager.MemoryInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MemoryInfo createFromParcel(Parcel source) {
                return new MemoryInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MemoryInfo[] newArray(int size) {
                return new MemoryInfo[size];
            }
        };

        public MemoryInfo() {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.availMem);
            dest.writeLong(this.totalMem);
            dest.writeLong(this.threshold);
            dest.writeInt(this.lowMemory ? 1 : 0);
            dest.writeLong(this.hiddenAppThreshold);
            dest.writeLong(this.secondaryServerThreshold);
            dest.writeLong(this.visibleAppThreshold);
            dest.writeLong(this.foregroundAppThreshold);
        }

        public void readFromParcel(Parcel source) {
            this.availMem = source.readLong();
            this.totalMem = source.readLong();
            this.threshold = source.readLong();
            this.lowMemory = source.readInt() != 0;
            this.hiddenAppThreshold = source.readLong();
            this.secondaryServerThreshold = source.readLong();
            this.visibleAppThreshold = source.readLong();
            this.foregroundAppThreshold = source.readLong();
        }

        private MemoryInfo(Parcel source) {
            readFromParcel(source);
        }
    }

    public void getMemoryInfo(MemoryInfo outInfo) {
        try {
            ActivityManagerNative.getDefault().getMemoryInfo(outInfo);
        } catch (RemoteException e) {
        }
    }

    /* loaded from: ActivityManager$StackBoxInfo.class */
    public static class StackBoxInfo implements Parcelable {
        public int stackBoxId;
        public float weight;
        public boolean vertical;
        public Rect bounds;
        public StackBoxInfo[] children;
        public int stackId;
        public StackInfo stack;
        public static final Parcelable.Creator<StackBoxInfo> CREATOR = new Parcelable.Creator<StackBoxInfo>() { // from class: android.app.ActivityManager.StackBoxInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackBoxInfo createFromParcel(Parcel source) {
                return new StackBoxInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackBoxInfo[] newArray(int size) {
                return new StackBoxInfo[size];
            }
        };

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.stackBoxId);
            dest.writeFloat(this.weight);
            dest.writeInt(this.vertical ? 1 : 0);
            this.bounds.writeToParcel(dest, flags);
            dest.writeInt(this.stackId);
            if (this.children != null) {
                this.children[0].writeToParcel(dest, flags);
                this.children[1].writeToParcel(dest, flags);
                return;
            }
            this.stack.writeToParcel(dest, flags);
        }

        public void readFromParcel(Parcel source) {
            this.stackBoxId = source.readInt();
            this.weight = source.readFloat();
            this.vertical = source.readInt() == 1;
            this.bounds = Rect.CREATOR.createFromParcel(source);
            this.stackId = source.readInt();
            if (this.stackId == -1) {
                this.children = new StackBoxInfo[2];
                this.children[0] = CREATOR.createFromParcel(source);
                this.children[1] = CREATOR.createFromParcel(source);
                return;
            }
            this.stack = StackInfo.CREATOR.createFromParcel(source);
        }

        public StackBoxInfo() {
        }

        public StackBoxInfo(Parcel source) {
            readFromParcel(source);
        }

        public String toString(String prefix) {
            StringBuilder sb = new StringBuilder(256);
            sb.append(prefix);
            sb.append("Box id=" + this.stackBoxId);
            sb.append(" weight=" + this.weight);
            sb.append(" vertical=" + this.vertical);
            sb.append(" bounds=" + this.bounds.toShortString());
            sb.append(Separators.RETURN);
            if (this.children != null) {
                sb.append(prefix);
                sb.append("First child=\n");
                sb.append(this.children[0].toString(prefix + "  "));
                sb.append(prefix);
                sb.append("Second child=\n");
                sb.append(this.children[1].toString(prefix + "  "));
            } else {
                sb.append(prefix);
                sb.append("Stack=\n");
                sb.append(this.stack.toString(prefix + "  "));
            }
            return sb.toString();
        }

        public String toString() {
            return toString("");
        }
    }

    /* loaded from: ActivityManager$StackInfo.class */
    public static class StackInfo implements Parcelable {
        public int stackId;
        public Rect bounds;
        public int[] taskIds;
        public String[] taskNames;
        public static final Parcelable.Creator<StackInfo> CREATOR = new Parcelable.Creator<StackInfo>() { // from class: android.app.ActivityManager.StackInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackInfo createFromParcel(Parcel source) {
                return new StackInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StackInfo[] newArray(int size) {
                return new StackInfo[size];
            }
        };

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.stackId);
            dest.writeInt(this.bounds.left);
            dest.writeInt(this.bounds.top);
            dest.writeInt(this.bounds.right);
            dest.writeInt(this.bounds.bottom);
            dest.writeIntArray(this.taskIds);
            dest.writeStringArray(this.taskNames);
        }

        public void readFromParcel(Parcel source) {
            this.stackId = source.readInt();
            this.bounds = new Rect(source.readInt(), source.readInt(), source.readInt(), source.readInt());
            this.taskIds = source.createIntArray();
            this.taskNames = source.createStringArray();
        }

        public StackInfo() {
        }

        private StackInfo(Parcel source) {
            readFromParcel(source);
        }

        public String toString(String prefix) {
            StringBuilder sb = new StringBuilder(256);
            sb.append(prefix);
            sb.append("Stack id=");
            sb.append(this.stackId);
            sb.append(" bounds=");
            sb.append(this.bounds.toShortString());
            sb.append(Separators.RETURN);
            String prefix2 = prefix + "  ";
            for (int i = 0; i < this.taskIds.length; i++) {
                sb.append(prefix2);
                sb.append("taskId=");
                sb.append(this.taskIds[i]);
                sb.append(": ");
                sb.append(this.taskNames[i]);
                sb.append(Separators.RETURN);
            }
            return sb.toString();
        }

        public String toString() {
            return toString("");
        }
    }

    public boolean clearApplicationUserData(String packageName, IPackageDataObserver observer) {
        try {
            return ActivityManagerNative.getDefault().clearApplicationUserData(packageName, observer, UserHandle.myUserId());
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean clearApplicationUserData() {
        return clearApplicationUserData(this.mContext.getPackageName(), null);
    }

    /* loaded from: ActivityManager$ProcessErrorStateInfo.class */
    public static class ProcessErrorStateInfo implements Parcelable {
        public static final int NO_ERROR = 0;
        public static final int CRASHED = 1;
        public static final int NOT_RESPONDING = 2;
        public int condition;
        public String processName;
        public int pid;
        public int uid;
        public String tag;
        public String shortMsg;
        public String longMsg;
        public String stackTrace;
        public byte[] crashData;
        public static final Parcelable.Creator<ProcessErrorStateInfo> CREATOR = new Parcelable.Creator<ProcessErrorStateInfo>() { // from class: android.app.ActivityManager.ProcessErrorStateInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ProcessErrorStateInfo createFromParcel(Parcel source) {
                return new ProcessErrorStateInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ProcessErrorStateInfo[] newArray(int size) {
                return new ProcessErrorStateInfo[size];
            }
        };

        public ProcessErrorStateInfo() {
            this.crashData = null;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.condition);
            dest.writeString(this.processName);
            dest.writeInt(this.pid);
            dest.writeInt(this.uid);
            dest.writeString(this.tag);
            dest.writeString(this.shortMsg);
            dest.writeString(this.longMsg);
            dest.writeString(this.stackTrace);
        }

        public void readFromParcel(Parcel source) {
            this.condition = source.readInt();
            this.processName = source.readString();
            this.pid = source.readInt();
            this.uid = source.readInt();
            this.tag = source.readString();
            this.shortMsg = source.readString();
            this.longMsg = source.readString();
            this.stackTrace = source.readString();
        }

        private ProcessErrorStateInfo(Parcel source) {
            this.crashData = null;
            readFromParcel(source);
        }
    }

    public List<ProcessErrorStateInfo> getProcessesInErrorState() {
        try {
            return ActivityManagerNative.getDefault().getProcessesInErrorState();
        } catch (RemoteException e) {
            return null;
        }
    }

    /* loaded from: ActivityManager$RunningAppProcessInfo.class */
    public static class RunningAppProcessInfo implements Parcelable {
        public String processName;
        public int pid;
        public int uid;
        public String[] pkgList;
        public static final int FLAG_CANT_SAVE_STATE = 1;
        public static final int FLAG_PERSISTENT = 2;
        public static final int FLAG_HAS_ACTIVITIES = 4;
        public int flags;
        public int lastTrimLevel;
        public static final int IMPORTANCE_PERSISTENT = 50;
        public static final int IMPORTANCE_FOREGROUND = 100;
        public static final int IMPORTANCE_VISIBLE = 200;
        public static final int IMPORTANCE_PERCEPTIBLE = 130;
        public static final int IMPORTANCE_CANT_SAVE_STATE = 170;
        public static final int IMPORTANCE_SERVICE = 300;
        public static final int IMPORTANCE_BACKGROUND = 400;
        public static final int IMPORTANCE_EMPTY = 500;
        public int importance;
        public int lru;
        public static final int REASON_UNKNOWN = 0;
        public static final int REASON_PROVIDER_IN_USE = 1;
        public static final int REASON_SERVICE_IN_USE = 2;
        public int importanceReasonCode;
        public int importanceReasonPid;
        public ComponentName importanceReasonComponent;
        public int importanceReasonImportance;
        public static final Parcelable.Creator<RunningAppProcessInfo> CREATOR = new Parcelable.Creator<RunningAppProcessInfo>() { // from class: android.app.ActivityManager.RunningAppProcessInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningAppProcessInfo createFromParcel(Parcel source) {
                return new RunningAppProcessInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RunningAppProcessInfo[] newArray(int size) {
                return new RunningAppProcessInfo[size];
            }
        };

        public RunningAppProcessInfo() {
            this.importance = 100;
            this.importanceReasonCode = 0;
        }

        public RunningAppProcessInfo(String pProcessName, int pPid, String[] pArr) {
            this.processName = pProcessName;
            this.pid = pPid;
            this.pkgList = pArr;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.processName);
            dest.writeInt(this.pid);
            dest.writeInt(this.uid);
            dest.writeStringArray(this.pkgList);
            dest.writeInt(this.flags);
            dest.writeInt(this.lastTrimLevel);
            dest.writeInt(this.importance);
            dest.writeInt(this.lru);
            dest.writeInt(this.importanceReasonCode);
            dest.writeInt(this.importanceReasonPid);
            ComponentName.writeToParcel(this.importanceReasonComponent, dest);
            dest.writeInt(this.importanceReasonImportance);
        }

        public void readFromParcel(Parcel source) {
            this.processName = source.readString();
            this.pid = source.readInt();
            this.uid = source.readInt();
            this.pkgList = source.readStringArray();
            this.flags = source.readInt();
            this.lastTrimLevel = source.readInt();
            this.importance = source.readInt();
            this.lru = source.readInt();
            this.importanceReasonCode = source.readInt();
            this.importanceReasonPid = source.readInt();
            this.importanceReasonComponent = ComponentName.readFromParcel(source);
            this.importanceReasonImportance = source.readInt();
        }

        private RunningAppProcessInfo(Parcel source) {
            readFromParcel(source);
        }
    }

    public List<ApplicationInfo> getRunningExternalApplications() {
        try {
            return ActivityManagerNative.getDefault().getRunningExternalApplications();
        } catch (RemoteException e) {
            return null;
        }
    }

    public List<RunningAppProcessInfo> getRunningAppProcesses() {
        try {
            return ActivityManagerNative.getDefault().getRunningAppProcesses();
        } catch (RemoteException e) {
            return null;
        }
    }

    public static void getMyMemoryState(RunningAppProcessInfo outState) {
        try {
            ActivityManagerNative.getDefault().getMyMemoryState(outState);
        } catch (RemoteException e) {
        }
    }

    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) {
        try {
            return ActivityManagerNative.getDefault().getProcessMemoryInfo(pids);
        } catch (RemoteException e) {
            return null;
        }
    }

    @Deprecated
    public void restartPackage(String packageName) {
        killBackgroundProcesses(packageName);
    }

    public void killBackgroundProcesses(String packageName) {
        try {
            ActivityManagerNative.getDefault().killBackgroundProcesses(packageName, UserHandle.myUserId());
        } catch (RemoteException e) {
        }
    }

    public void forceStopPackage(String packageName) {
        try {
            ActivityManagerNative.getDefault().forceStopPackage(packageName, UserHandle.myUserId());
        } catch (RemoteException e) {
        }
    }

    public ConfigurationInfo getDeviceConfigurationInfo() {
        try {
            return ActivityManagerNative.getDefault().getDeviceConfigurationInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public int getLauncherLargeIconDensity() {
        Resources res = this.mContext.getResources();
        int density = res.getDisplayMetrics().densityDpi;
        int sw = res.getConfiguration().smallestScreenWidthDp;
        if (sw < 600) {
            return density;
        }
        switch (density) {
            case 120:
                return 160;
            case 160:
                return 240;
            case 213:
                return 320;
            case 240:
                return 320;
            case 320:
                return 480;
            case 480:
                return DisplayMetrics.DENSITY_XXXHIGH;
            default:
                return (int) ((density * 1.5f) + 0.5f);
        }
    }

    public int getLauncherLargeIconSize() {
        Resources res = this.mContext.getResources();
        int size = res.getDimensionPixelSize(17104896);
        int sw = res.getConfiguration().smallestScreenWidthDp;
        if (sw < 600) {
            return size;
        }
        int density = res.getDisplayMetrics().densityDpi;
        switch (density) {
            case 120:
                return (size * 160) / 120;
            case 160:
                return (size * 240) / 160;
            case 213:
                return (size * 320) / 240;
            case 240:
                return (size * 320) / 240;
            case 320:
                return (size * 480) / 320;
            case 480:
                return ((size * 320) * 2) / 480;
            default:
                return (int) ((size * 1.5f) + 0.5f);
        }
    }

    public static boolean isUserAMonkey() {
        try {
            return ActivityManagerNative.getDefault().isUserAMonkey();
        } catch (RemoteException e) {
            return false;
        }
    }

    public static boolean isRunningInTestHarness() {
        return SystemProperties.getBoolean("ro.test_harness", false);
    }

    public Map<String, Integer> getAllPackageLaunchCounts() {
        try {
            IUsageStats usageStatsService = IUsageStats.Stub.asInterface(ServiceManager.getService(UsageStatsService.SERVICE_NAME));
            if (usageStatsService == null) {
                return new HashMap();
            }
            PkgUsageStats[] allPkgUsageStats = usageStatsService.getAllPkgUsageStats();
            if (allPkgUsageStats == null) {
                return new HashMap();
            }
            Map<String, Integer> launchCounts = new HashMap<>();
            for (PkgUsageStats pkgUsageStats : allPkgUsageStats) {
                launchCounts.put(pkgUsageStats.packageName, Integer.valueOf(pkgUsageStats.launchCount));
            }
            return launchCounts;
        } catch (RemoteException e) {
            Log.w(TAG, "Could not query launch counts", e);
            return new HashMap();
        }
    }

    public static int checkComponentPermission(String permission, int uid, int owningUid, boolean exported) {
        if (uid == 0 || uid == 1000) {
            return 0;
        }
        if (UserHandle.isIsolated(uid)) {
            return -1;
        }
        if (owningUid >= 0 && UserHandle.isSameApp(uid, owningUid)) {
            return 0;
        }
        if (!exported) {
            return -1;
        }
        if (permission == null) {
            return 0;
        }
        try {
            return AppGlobals.getPackageManager().checkUidPermission(permission, uid);
        } catch (RemoteException e) {
            Slog.e(TAG, "PackageManager is dead?!?", e);
            return -1;
        }
    }

    public static int checkUidPermission(String permission, int uid) {
        try {
            return AppGlobals.getPackageManager().checkUidPermission(permission, uid);
        } catch (RemoteException e) {
            Slog.e(TAG, "PackageManager is dead?!?", e);
            return -1;
        }
    }

    public static int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll, boolean requireFull, String name, String callerPackage) {
        if (UserHandle.getUserId(callingUid) == userId) {
            return userId;
        }
        try {
            return ActivityManagerNative.getDefault().handleIncomingUser(callingPid, callingUid, userId, allowAll, requireFull, name, callerPackage);
        } catch (RemoteException e) {
            throw new SecurityException("Failed calling activity manager", e);
        }
    }

    public static int getCurrentUser() {
        try {
            UserInfo ui = ActivityManagerNative.getDefault().getCurrentUser();
            if (ui != null) {
                return ui.id;
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public PkgUsageStats[] getAllPackageUsageStats() {
        try {
            IUsageStats usageStatsService = IUsageStats.Stub.asInterface(ServiceManager.getService(UsageStatsService.SERVICE_NAME));
            if (usageStatsService != null) {
                return usageStatsService.getAllPkgUsageStats();
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Could not query usage stats", e);
        }
        return new PkgUsageStats[0];
    }

    public boolean switchUser(int userid) {
        try {
            return ActivityManagerNative.getDefault().switchUser(userid);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isUserRunning(int userid) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(userid, false);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void dumpPackageState(FileDescriptor fd, String packageName) {
        dumpPackageStateStatic(fd, packageName);
    }

    public static void dumpPackageStateStatic(FileDescriptor fd, String packageName) {
        FileOutputStream fout = new FileOutputStream(fd);
        PrintWriter pw = new FastPrintWriter(fout);
        dumpService(pw, fd, Context.ACTIVITY_SERVICE, new String[]{Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName});
        pw.println();
        dumpService(pw, fd, ProcessStats.SERVICE_NAME, new String[]{packageName});
        pw.println();
        dumpService(pw, fd, UsageStatsService.SERVICE_NAME, new String[]{"--packages", packageName});
        pw.println();
        dumpService(pw, fd, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, new String[]{packageName});
        pw.println();
        dumpService(pw, fd, BatteryStats.SERVICE_NAME, new String[]{packageName});
        pw.flush();
    }

    private static void dumpService(PrintWriter pw, FileDescriptor fd, String name, String[] args) {
        pw.print("DUMP OF SERVICE ");
        pw.print(name);
        pw.println(Separators.COLON);
        IBinder service = ServiceManager.checkService(name);
        if (service == null) {
            pw.println("  (Service not found)");
            return;
        }
        TransferPipe tp = null;
        try {
            pw.flush();
            tp = new TransferPipe();
            tp.setBufferPrefix("  ");
            service.dump(tp.getWriteFd().getFileDescriptor(), args);
            tp.go(fd);
        } catch (Throwable e) {
            if (tp != null) {
                tp.kill();
            }
            pw.println("Failure dumping service:");
            e.printStackTrace(pw);
        }
    }
}