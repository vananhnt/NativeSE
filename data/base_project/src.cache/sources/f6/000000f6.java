package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Printer;
import com.android.internal.util.FastPrintWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/* loaded from: ApplicationErrorReport.class */
public class ApplicationErrorReport implements Parcelable {
    static final String SYSTEM_APPS_ERROR_RECEIVER_PROPERTY = "ro.error.receiver.system.apps";
    static final String DEFAULT_ERROR_RECEIVER_PROPERTY = "ro.error.receiver.default";
    public static final int TYPE_NONE = 0;
    public static final int TYPE_CRASH = 1;
    public static final int TYPE_ANR = 2;
    public static final int TYPE_BATTERY = 3;
    public static final int TYPE_RUNNING_SERVICE = 5;
    public int type;
    public String packageName;
    public String installerPackageName;
    public String processName;
    public long time;
    public boolean systemApp;
    public CrashInfo crashInfo;
    public AnrInfo anrInfo;
    public BatteryInfo batteryInfo;
    public RunningServiceInfo runningServiceInfo;
    public static final Parcelable.Creator<ApplicationErrorReport> CREATOR = new Parcelable.Creator<ApplicationErrorReport>() { // from class: android.app.ApplicationErrorReport.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApplicationErrorReport createFromParcel(Parcel source) {
            return new ApplicationErrorReport(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApplicationErrorReport[] newArray(int size) {
            return new ApplicationErrorReport[size];
        }
    };

    public ApplicationErrorReport() {
    }

    ApplicationErrorReport(Parcel in) {
        readFromParcel(in);
    }

    public static ComponentName getErrorReportReceiver(Context context, String packageName, int appFlags) {
        int enabled = Settings.Global.getInt(context.getContentResolver(), Settings.Global.SEND_ACTION_APP_ERROR, 0);
        if (enabled == 0) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        String candidate = pm.getInstallerPackageName(packageName);
        ComponentName result = getErrorReportReceiver(pm, packageName, candidate);
        if (result != null) {
            return result;
        }
        if ((appFlags & 1) != 0) {
            String candidate2 = SystemProperties.get(SYSTEM_APPS_ERROR_RECEIVER_PROPERTY);
            ComponentName result2 = getErrorReportReceiver(pm, packageName, candidate2);
            if (result2 != null) {
                return result2;
            }
        }
        String candidate3 = SystemProperties.get(DEFAULT_ERROR_RECEIVER_PROPERTY);
        return getErrorReportReceiver(pm, packageName, candidate3);
    }

    static ComponentName getErrorReportReceiver(PackageManager pm, String errorPackage, String receiverPackage) {
        if (receiverPackage == null || receiverPackage.length() == 0 || receiverPackage.equals(errorPackage)) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_APP_ERROR);
        intent.setPackage(receiverPackage);
        ResolveInfo info = pm.resolveActivity(intent, 0);
        if (info == null || info.activityInfo == null) {
            return null;
        }
        return new ComponentName(receiverPackage, info.activityInfo.name);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.packageName);
        dest.writeString(this.installerPackageName);
        dest.writeString(this.processName);
        dest.writeLong(this.time);
        dest.writeInt(this.systemApp ? 1 : 0);
        switch (this.type) {
            case 1:
                this.crashInfo.writeToParcel(dest, flags);
                return;
            case 2:
                this.anrInfo.writeToParcel(dest, flags);
                return;
            case 3:
                this.batteryInfo.writeToParcel(dest, flags);
                return;
            case 4:
            default:
                return;
            case 5:
                this.runningServiceInfo.writeToParcel(dest, flags);
                return;
        }
    }

    public void readFromParcel(Parcel in) {
        this.type = in.readInt();
        this.packageName = in.readString();
        this.installerPackageName = in.readString();
        this.processName = in.readString();
        this.time = in.readLong();
        this.systemApp = in.readInt() == 1;
        switch (this.type) {
            case 1:
                this.crashInfo = new CrashInfo(in);
                this.anrInfo = null;
                this.batteryInfo = null;
                this.runningServiceInfo = null;
                return;
            case 2:
                this.anrInfo = new AnrInfo(in);
                this.crashInfo = null;
                this.batteryInfo = null;
                this.runningServiceInfo = null;
                return;
            case 3:
                this.batteryInfo = new BatteryInfo(in);
                this.anrInfo = null;
                this.crashInfo = null;
                this.runningServiceInfo = null;
                return;
            case 4:
            default:
                return;
            case 5:
                this.batteryInfo = null;
                this.anrInfo = null;
                this.crashInfo = null;
                this.runningServiceInfo = new RunningServiceInfo(in);
                return;
        }
    }

    /* loaded from: ApplicationErrorReport$CrashInfo.class */
    public static class CrashInfo {
        public String exceptionClassName;
        public String exceptionMessage;
        public String throwFileName;
        public String throwClassName;
        public String throwMethodName;
        public int throwLineNumber;
        public String stackTrace;

        public CrashInfo() {
        }

        public CrashInfo(Throwable tr) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new FastPrintWriter((Writer) sw, false, 256);
            tr.printStackTrace(pw);
            pw.flush();
            this.stackTrace = sw.toString();
            this.exceptionMessage = tr.getMessage();
            Throwable rootTr = tr;
            while (tr.getCause() != null) {
                tr = tr.getCause();
                if (tr.getStackTrace() != null && tr.getStackTrace().length > 0) {
                    rootTr = tr;
                }
                String msg = tr.getMessage();
                if (msg != null && msg.length() > 0) {
                    this.exceptionMessage = msg;
                }
            }
            this.exceptionClassName = rootTr.getClass().getName();
            if (rootTr.getStackTrace().length > 0) {
                StackTraceElement trace = rootTr.getStackTrace()[0];
                this.throwFileName = trace.getFileName();
                this.throwClassName = trace.getClassName();
                this.throwMethodName = trace.getMethodName();
                this.throwLineNumber = trace.getLineNumber();
                return;
            }
            this.throwFileName = "unknown";
            this.throwClassName = "unknown";
            this.throwMethodName = "unknown";
            this.throwLineNumber = 0;
        }

        public CrashInfo(Parcel in) {
            this.exceptionClassName = in.readString();
            this.exceptionMessage = in.readString();
            this.throwFileName = in.readString();
            this.throwClassName = in.readString();
            this.throwMethodName = in.readString();
            this.throwLineNumber = in.readInt();
            this.stackTrace = in.readString();
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.exceptionClassName);
            dest.writeString(this.exceptionMessage);
            dest.writeString(this.throwFileName);
            dest.writeString(this.throwClassName);
            dest.writeString(this.throwMethodName);
            dest.writeInt(this.throwLineNumber);
            dest.writeString(this.stackTrace);
        }

        public void dump(Printer pw, String prefix) {
            pw.println(prefix + "exceptionClassName: " + this.exceptionClassName);
            pw.println(prefix + "exceptionMessage: " + this.exceptionMessage);
            pw.println(prefix + "throwFileName: " + this.throwFileName);
            pw.println(prefix + "throwClassName: " + this.throwClassName);
            pw.println(prefix + "throwMethodName: " + this.throwMethodName);
            pw.println(prefix + "throwLineNumber: " + this.throwLineNumber);
            pw.println(prefix + "stackTrace: " + this.stackTrace);
        }
    }

    /* loaded from: ApplicationErrorReport$AnrInfo.class */
    public static class AnrInfo {
        public String activity;
        public String cause;
        public String info;

        public AnrInfo() {
        }

        public AnrInfo(Parcel in) {
            this.activity = in.readString();
            this.cause = in.readString();
            this.info = in.readString();
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.activity);
            dest.writeString(this.cause);
            dest.writeString(this.info);
        }

        public void dump(Printer pw, String prefix) {
            pw.println(prefix + "activity: " + this.activity);
            pw.println(prefix + "cause: " + this.cause);
            pw.println(prefix + "info: " + this.info);
        }
    }

    /* loaded from: ApplicationErrorReport$BatteryInfo.class */
    public static class BatteryInfo {
        public int usagePercent;
        public long durationMicros;
        public String usageDetails;
        public String checkinDetails;

        public BatteryInfo() {
        }

        public BatteryInfo(Parcel in) {
            this.usagePercent = in.readInt();
            this.durationMicros = in.readLong();
            this.usageDetails = in.readString();
            this.checkinDetails = in.readString();
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.usagePercent);
            dest.writeLong(this.durationMicros);
            dest.writeString(this.usageDetails);
            dest.writeString(this.checkinDetails);
        }

        public void dump(Printer pw, String prefix) {
            pw.println(prefix + "usagePercent: " + this.usagePercent);
            pw.println(prefix + "durationMicros: " + this.durationMicros);
            pw.println(prefix + "usageDetails: " + this.usageDetails);
            pw.println(prefix + "checkinDetails: " + this.checkinDetails);
        }
    }

    /* loaded from: ApplicationErrorReport$RunningServiceInfo.class */
    public static class RunningServiceInfo {
        public long durationMillis;
        public String serviceDetails;

        public RunningServiceInfo() {
        }

        public RunningServiceInfo(Parcel in) {
            this.durationMillis = in.readLong();
            this.serviceDetails = in.readString();
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.durationMillis);
            dest.writeString(this.serviceDetails);
        }

        public void dump(Printer pw, String prefix) {
            pw.println(prefix + "durationMillis: " + this.durationMillis);
            pw.println(prefix + "serviceDetails: " + this.serviceDetails);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + "type: " + this.type);
        pw.println(prefix + "packageName: " + this.packageName);
        pw.println(prefix + "installerPackageName: " + this.installerPackageName);
        pw.println(prefix + "processName: " + this.processName);
        pw.println(prefix + "time: " + this.time);
        pw.println(prefix + "systemApp: " + this.systemApp);
        switch (this.type) {
            case 1:
                this.crashInfo.dump(pw, prefix);
                return;
            case 2:
                this.anrInfo.dump(pw, prefix);
                return;
            case 3:
                this.batteryInfo.dump(pw, prefix);
                return;
            case 4:
            default:
                return;
            case 5:
                this.runningServiceInfo.dump(pw, prefix);
                return;
        }
    }
}