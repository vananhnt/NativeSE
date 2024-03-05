package android.app;

import android.app.IInstrumentationWatcher;
import android.app.IUiAutomationConnection;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/* loaded from: ApplicationThreadNative.class */
public abstract class ApplicationThreadNative extends Binder implements IApplicationThread {
    public static IApplicationThread asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IApplicationThread in = (IApplicationThread) obj.queryLocalInterface(IApplicationThread.descriptor);
        if (in != null) {
            return in;
        }
        return new ApplicationThreadProxy(obj);
    }

    public ApplicationThreadNative() {
        attachInterface(this, IApplicationThread.descriptor);
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        ParcelFileDescriptor fd;
        Intent args;
        switch (code) {
            case 1:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b = data.readStrongBinder();
                boolean finished = data.readInt() != 0;
                boolean userLeaving = data.readInt() != 0;
                int configChanges = data.readInt();
                schedulePauseActivity(b, finished, userLeaving, configChanges);
                return true;
            case 2:
            case 32:
            default:
                return super.onTransact(code, data, reply, flags);
            case 3:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b2 = data.readStrongBinder();
                boolean show = data.readInt() != 0;
                int configChanges2 = data.readInt();
                scheduleStopActivity(b2, show, configChanges2);
                return true;
            case 4:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b3 = data.readStrongBinder();
                boolean show2 = data.readInt() != 0;
                scheduleWindowVisibility(b3, show2);
                return true;
            case 5:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b4 = data.readStrongBinder();
                int procState = data.readInt();
                boolean isForward = data.readInt() != 0;
                scheduleResumeActivity(b4, procState, isForward);
                return true;
            case 6:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b5 = data.readStrongBinder();
                List<ResultInfo> ri = data.createTypedArrayList(ResultInfo.CREATOR);
                scheduleSendResult(b5, ri);
                return true;
            case 7:
                data.enforceInterface(IApplicationThread.descriptor);
                Intent intent = Intent.CREATOR.createFromParcel(data);
                IBinder b6 = data.readStrongBinder();
                int ident = data.readInt();
                ActivityInfo info = ActivityInfo.CREATOR.createFromParcel(data);
                Configuration curConfig = Configuration.CREATOR.createFromParcel(data);
                CompatibilityInfo compatInfo = CompatibilityInfo.CREATOR.createFromParcel(data);
                int procState2 = data.readInt();
                Bundle state = data.readBundle();
                List<ResultInfo> ri2 = data.createTypedArrayList(ResultInfo.CREATOR);
                List<Intent> pi = data.createTypedArrayList(Intent.CREATOR);
                boolean notResumed = data.readInt() != 0;
                boolean isForward2 = data.readInt() != 0;
                String profileName = data.readString();
                ParcelFileDescriptor profileFd = data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null;
                boolean autoStopProfiler = data.readInt() != 0;
                scheduleLaunchActivity(intent, b6, ident, info, curConfig, compatInfo, procState2, state, ri2, pi, notResumed, isForward2, profileName, profileFd, autoStopProfiler);
                return true;
            case 8:
                data.enforceInterface(IApplicationThread.descriptor);
                List<Intent> pi2 = data.createTypedArrayList(Intent.CREATOR);
                IBinder b7 = data.readStrongBinder();
                scheduleNewIntent(pi2, b7);
                return true;
            case 9:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b8 = data.readStrongBinder();
                boolean finishing = data.readInt() != 0;
                int configChanges3 = data.readInt();
                scheduleDestroyActivity(b8, finishing, configChanges3);
                return true;
            case 10:
                data.enforceInterface(IApplicationThread.descriptor);
                Intent intent2 = Intent.CREATOR.createFromParcel(data);
                ActivityInfo info2 = ActivityInfo.CREATOR.createFromParcel(data);
                CompatibilityInfo compatInfo2 = CompatibilityInfo.CREATOR.createFromParcel(data);
                int resultCode = data.readInt();
                String resultData = data.readString();
                Bundle resultExtras = data.readBundle();
                boolean sync = data.readInt() != 0;
                int sendingUser = data.readInt();
                int processState = data.readInt();
                scheduleReceiver(intent2, info2, compatInfo2, resultCode, resultData, resultExtras, sync, sendingUser, processState);
                return true;
            case 11:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder token = data.readStrongBinder();
                ServiceInfo info3 = ServiceInfo.CREATOR.createFromParcel(data);
                CompatibilityInfo compatInfo3 = CompatibilityInfo.CREATOR.createFromParcel(data);
                int processState2 = data.readInt();
                scheduleCreateService(token, info3, compatInfo3, processState2);
                return true;
            case 12:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder token2 = data.readStrongBinder();
                scheduleStopService(token2);
                return true;
            case 13:
                data.enforceInterface(IApplicationThread.descriptor);
                String packageName = data.readString();
                ApplicationInfo info4 = ApplicationInfo.CREATOR.createFromParcel(data);
                List<ProviderInfo> providers = data.createTypedArrayList(ProviderInfo.CREATOR);
                ComponentName testName = data.readInt() != 0 ? new ComponentName(data) : null;
                String profileName2 = data.readString();
                ParcelFileDescriptor profileFd2 = data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null;
                boolean autoStopProfiler2 = data.readInt() != 0;
                Bundle testArgs = data.readBundle();
                IBinder binder = data.readStrongBinder();
                IInstrumentationWatcher testWatcher = IInstrumentationWatcher.Stub.asInterface(binder);
                IBinder binder2 = data.readStrongBinder();
                IUiAutomationConnection uiAutomationConnection = IUiAutomationConnection.Stub.asInterface(binder2);
                int testMode = data.readInt();
                boolean openGlTrace = data.readInt() != 0;
                boolean restrictedBackupMode = data.readInt() != 0;
                boolean persistent = data.readInt() != 0;
                Configuration config = Configuration.CREATOR.createFromParcel(data);
                CompatibilityInfo compatInfo4 = CompatibilityInfo.CREATOR.createFromParcel(data);
                HashMap<String, IBinder> services = data.readHashMap(null);
                Bundle coreSettings = data.readBundle();
                bindApplication(packageName, info4, providers, testName, profileName2, profileFd2, autoStopProfiler2, testArgs, testWatcher, uiAutomationConnection, testMode, openGlTrace, restrictedBackupMode, persistent, config, compatInfo4, services, coreSettings);
                return true;
            case 14:
                data.enforceInterface(IApplicationThread.descriptor);
                scheduleExit();
                return true;
            case 15:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b9 = data.readStrongBinder();
                requestThumbnail(b9);
                return true;
            case 16:
                data.enforceInterface(IApplicationThread.descriptor);
                Configuration config2 = Configuration.CREATOR.createFromParcel(data);
                scheduleConfigurationChanged(config2);
                return true;
            case 17:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder token3 = data.readStrongBinder();
                boolean taskRemoved = data.readInt() != 0;
                int startId = data.readInt();
                int fl = data.readInt();
                if (data.readInt() != 0) {
                    args = Intent.CREATOR.createFromParcel(data);
                } else {
                    args = null;
                }
                scheduleServiceArgs(token3, taskRemoved, startId, fl, args);
                return true;
            case 18:
                data.enforceInterface(IApplicationThread.descriptor);
                updateTimeZone();
                return true;
            case 19:
                data.enforceInterface(IApplicationThread.descriptor);
                processInBackground();
                return true;
            case 20:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder token4 = data.readStrongBinder();
                Intent intent3 = Intent.CREATOR.createFromParcel(data);
                boolean rebind = data.readInt() != 0;
                int processState3 = data.readInt();
                scheduleBindService(token4, intent3, rebind, processState3);
                return true;
            case 21:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder token5 = data.readStrongBinder();
                Intent intent4 = Intent.CREATOR.createFromParcel(data);
                scheduleUnbindService(token5, intent4);
                return true;
            case 22:
                data.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fd2 = data.readFileDescriptor();
                IBinder service = data.readStrongBinder();
                String[] args2 = data.readStringArray();
                if (fd2 != null) {
                    dumpService(fd2.getFileDescriptor(), service, args2);
                    try {
                        fd2.close();
                        return true;
                    } catch (IOException e) {
                        return true;
                    }
                }
                return true;
            case 23:
                data.enforceInterface(IApplicationThread.descriptor);
                IIntentReceiver receiver = IIntentReceiver.Stub.asInterface(data.readStrongBinder());
                Intent intent5 = Intent.CREATOR.createFromParcel(data);
                int resultCode2 = data.readInt();
                String dataStr = data.readString();
                Bundle extras = data.readBundle();
                boolean ordered = data.readInt() != 0;
                boolean sticky = data.readInt() != 0;
                int sendingUser2 = data.readInt();
                int processState4 = data.readInt();
                scheduleRegisteredReceiver(receiver, intent5, resultCode2, dataStr, extras, ordered, sticky, sendingUser2, processState4);
                return true;
            case 24:
                data.enforceInterface(IApplicationThread.descriptor);
                scheduleLowMemory();
                return true;
            case 25:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b10 = data.readStrongBinder();
                scheduleActivityConfigurationChanged(b10);
                return true;
            case 26:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b11 = data.readStrongBinder();
                List<ResultInfo> ri3 = data.createTypedArrayList(ResultInfo.CREATOR);
                List<Intent> pi3 = data.createTypedArrayList(Intent.CREATOR);
                int configChanges4 = data.readInt();
                boolean notResumed2 = data.readInt() != 0;
                Configuration config3 = null;
                if (data.readInt() != 0) {
                    config3 = Configuration.CREATOR.createFromParcel(data);
                }
                scheduleRelaunchActivity(b11, ri3, pi3, configChanges4, notResumed2, config3);
                return true;
            case 27:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder b12 = data.readStrongBinder();
                boolean sleeping = data.readInt() != 0;
                scheduleSleeping(b12, sleeping);
                return true;
            case 28:
                data.enforceInterface(IApplicationThread.descriptor);
                boolean start = data.readInt() != 0;
                int profileType = data.readInt();
                String path = data.readString();
                profilerControl(start, path, data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null, profileType);
                return true;
            case 29:
                data.enforceInterface(IApplicationThread.descriptor);
                int group = data.readInt();
                setSchedulingGroup(group);
                return true;
            case 30:
                data.enforceInterface(IApplicationThread.descriptor);
                ApplicationInfo appInfo = ApplicationInfo.CREATOR.createFromParcel(data);
                CompatibilityInfo compatInfo5 = CompatibilityInfo.CREATOR.createFromParcel(data);
                int backupMode = data.readInt();
                scheduleCreateBackupAgent(appInfo, compatInfo5, backupMode);
                return true;
            case 31:
                data.enforceInterface(IApplicationThread.descriptor);
                ApplicationInfo appInfo2 = ApplicationInfo.CREATOR.createFromParcel(data);
                CompatibilityInfo compatInfo6 = CompatibilityInfo.CREATOR.createFromParcel(data);
                scheduleDestroyBackupAgent(appInfo2, compatInfo6);
                return true;
            case 33:
                data.enforceInterface(IApplicationThread.descriptor);
                scheduleSuicide();
                return true;
            case 34:
                data.enforceInterface(IApplicationThread.descriptor);
                int cmd = data.readInt();
                String[] packages = data.readStringArray();
                dispatchPackageBroadcast(cmd, packages);
                return true;
            case 35:
                data.enforceInterface(IApplicationThread.descriptor);
                String msg = data.readString();
                scheduleCrash(msg);
                return true;
            case 36:
                data.enforceInterface(IApplicationThread.descriptor);
                boolean managed = data.readInt() != 0;
                String path2 = data.readString();
                dumpHeap(managed, path2, data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null);
                return true;
            case 37:
                data.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fd3 = data.readFileDescriptor();
                IBinder activity = data.readStrongBinder();
                String prefix = data.readString();
                String[] args3 = data.readStringArray();
                if (fd3 != null) {
                    dumpActivity(fd3.getFileDescriptor(), activity, prefix, args3);
                    try {
                        fd3.close();
                        return true;
                    } catch (IOException e2) {
                        return true;
                    }
                }
                return true;
            case 38:
                data.enforceInterface(IApplicationThread.descriptor);
                clearDnsCache();
                return true;
            case 39:
                data.enforceInterface(IApplicationThread.descriptor);
                String proxy = data.readString();
                String port = data.readString();
                String exclList = data.readString();
                String pacFileUrl = data.readString();
                setHttpProxy(proxy, port, exclList, pacFileUrl);
                return true;
            case 40:
                data.enforceInterface(IApplicationThread.descriptor);
                Bundle settings = data.readBundle();
                setCoreSettings(settings);
                return true;
            case 41:
                data.enforceInterface(IApplicationThread.descriptor);
                String pkg = data.readString();
                CompatibilityInfo compat = CompatibilityInfo.CREATOR.createFromParcel(data);
                updatePackageCompatibilityInfo(pkg, compat);
                return true;
            case 42:
                data.enforceInterface(IApplicationThread.descriptor);
                int level = data.readInt();
                scheduleTrimMemory(level);
                return true;
            case 43:
                data.enforceInterface(IApplicationThread.descriptor);
                fd = data.readFileDescriptor();
                Debug.MemoryInfo mi = Debug.MemoryInfo.CREATOR.createFromParcel(data);
                boolean checkin = data.readInt() != 0;
                boolean dumpInfo = data.readInt() != 0;
                boolean dumpDalvik = data.readInt() != 0;
                String[] args4 = data.readStringArray();
                if (fd != null) {
                    try {
                        dumpMemInfo(fd.getFileDescriptor(), mi, checkin, dumpInfo, dumpDalvik, args4);
                    } finally {
                        try {
                            fd.close();
                        } catch (IOException e3) {
                        }
                    }
                }
                reply.writeNoException();
                return true;
            case 44:
                data.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fd4 = data.readFileDescriptor();
                String[] args5 = data.readStringArray();
                if (fd4 != null) {
                    try {
                        dumpGfxInfo(fd4.getFileDescriptor(), args5);
                        try {
                            fd4.close();
                        } catch (IOException e4) {
                        }
                    } finally {
                        try {
                            fd4.close();
                        } catch (IOException e5) {
                        }
                    }
                }
                reply.writeNoException();
                return true;
            case 45:
                data.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fd5 = data.readFileDescriptor();
                IBinder service2 = data.readStrongBinder();
                String[] args6 = data.readStringArray();
                if (fd5 != null) {
                    dumpProvider(fd5.getFileDescriptor(), service2, args6);
                    try {
                        fd5.close();
                        return true;
                    } catch (IOException e6) {
                        return true;
                    }
                }
                return true;
            case 46:
                data.enforceInterface(IApplicationThread.descriptor);
                fd = data.readFileDescriptor();
                String[] args7 = data.readStringArray();
                if (fd != null) {
                    try {
                        dumpDbInfo(fd.getFileDescriptor(), args7);
                        try {
                            fd.close();
                        } catch (IOException e7) {
                        }
                    } finally {
                        try {
                            fd.close();
                        } catch (IOException e8) {
                        }
                    }
                }
                reply.writeNoException();
                return true;
            case 47:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder provider = data.readStrongBinder();
                unstableProviderDied(provider);
                reply.writeNoException();
                return true;
            case 48:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder activityToken = data.readStrongBinder();
                IBinder requestToken = data.readStrongBinder();
                int requestType = data.readInt();
                requestAssistContextExtras(activityToken, requestToken, requestType);
                reply.writeNoException();
                return true;
            case 49:
                data.enforceInterface(IApplicationThread.descriptor);
                IBinder token6 = data.readStrongBinder();
                boolean timeout = data.readInt() == 1;
                scheduleTranslucentConversionComplete(token6, timeout);
                reply.writeNoException();
                return true;
            case 50:
                data.enforceInterface(IApplicationThread.descriptor);
                int state2 = data.readInt();
                setProcessState(state2);
                reply.writeNoException();
                return true;
            case 51:
                data.enforceInterface(IApplicationThread.descriptor);
                ProviderInfo provider2 = ProviderInfo.CREATOR.createFromParcel(data);
                scheduleInstallProvider(provider2);
                reply.writeNoException();
                return true;
        }
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }
}