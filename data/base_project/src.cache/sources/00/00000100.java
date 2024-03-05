package android.app;

import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.io.FileDescriptor;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ApplicationThreadNative.java */
/* loaded from: ApplicationThreadProxy.class */
public class ApplicationThreadProxy implements IApplicationThread {
    private final IBinder mRemote;

    public ApplicationThreadProxy(IBinder remote) {
        this.mRemote = remote;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.mRemote;
    }

    @Override // android.app.IApplicationThread
    public final void schedulePauseActivity(IBinder token, boolean finished, boolean userLeaving, int configChanges) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(finished ? 1 : 0);
        data.writeInt(userLeaving ? 1 : 0);
        data.writeInt(configChanges);
        this.mRemote.transact(1, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleStopActivity(IBinder token, boolean showWindow, int configChanges) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(showWindow ? 1 : 0);
        data.writeInt(configChanges);
        this.mRemote.transact(3, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleWindowVisibility(IBinder token, boolean showWindow) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(showWindow ? 1 : 0);
        this.mRemote.transact(4, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleSleeping(IBinder token, boolean sleeping) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(sleeping ? 1 : 0);
        this.mRemote.transact(27, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleResumeActivity(IBinder token, int procState, boolean isForward) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(procState);
        data.writeInt(isForward ? 1 : 0);
        this.mRemote.transact(5, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleSendResult(IBinder token, List<ResultInfo> results) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeTypedList(results);
        this.mRemote.transact(6, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident, ActivityInfo info, Configuration curConfig, CompatibilityInfo compatInfo, int procState, Bundle state, List<ResultInfo> pendingResults, List<Intent> pendingNewIntents, boolean notResumed, boolean isForward, String profileName, ParcelFileDescriptor profileFd, boolean autoStopProfiler) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        intent.writeToParcel(data, 0);
        data.writeStrongBinder(token);
        data.writeInt(ident);
        info.writeToParcel(data, 0);
        curConfig.writeToParcel(data, 0);
        compatInfo.writeToParcel(data, 0);
        data.writeInt(procState);
        data.writeBundle(state);
        data.writeTypedList(pendingResults);
        data.writeTypedList(pendingNewIntents);
        data.writeInt(notResumed ? 1 : 0);
        data.writeInt(isForward ? 1 : 0);
        data.writeString(profileName);
        if (profileFd != null) {
            data.writeInt(1);
            profileFd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        data.writeInt(autoStopProfiler ? 1 : 0);
        this.mRemote.transact(7, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleRelaunchActivity(IBinder token, List<ResultInfo> pendingResults, List<Intent> pendingNewIntents, int configChanges, boolean notResumed, Configuration config) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeTypedList(pendingResults);
        data.writeTypedList(pendingNewIntents);
        data.writeInt(configChanges);
        data.writeInt(notResumed ? 1 : 0);
        if (config != null) {
            data.writeInt(1);
            config.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(26, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void scheduleNewIntent(List<Intent> intents, IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeTypedList(intents);
        data.writeStrongBinder(token);
        this.mRemote.transact(8, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleDestroyActivity(IBinder token, boolean finishing, int configChanges) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(finishing ? 1 : 0);
        data.writeInt(configChanges);
        this.mRemote.transact(9, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleReceiver(Intent intent, ActivityInfo info, CompatibilityInfo compatInfo, int resultCode, String resultData, Bundle map, boolean sync, int sendingUser, int processState) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        intent.writeToParcel(data, 0);
        info.writeToParcel(data, 0);
        compatInfo.writeToParcel(data, 0);
        data.writeInt(resultCode);
        data.writeString(resultData);
        data.writeBundle(map);
        data.writeInt(sync ? 1 : 0);
        data.writeInt(sendingUser);
        data.writeInt(processState);
        this.mRemote.transact(10, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleCreateBackupAgent(ApplicationInfo app, CompatibilityInfo compatInfo, int backupMode) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        app.writeToParcel(data, 0);
        compatInfo.writeToParcel(data, 0);
        data.writeInt(backupMode);
        this.mRemote.transact(30, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleDestroyBackupAgent(ApplicationInfo app, CompatibilityInfo compatInfo) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        app.writeToParcel(data, 0);
        compatInfo.writeToParcel(data, 0);
        this.mRemote.transact(31, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleCreateService(IBinder token, ServiceInfo info, CompatibilityInfo compatInfo, int processState) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        info.writeToParcel(data, 0);
        compatInfo.writeToParcel(data, 0);
        data.writeInt(processState);
        this.mRemote.transact(11, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleBindService(IBinder token, Intent intent, boolean rebind, int processState) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        intent.writeToParcel(data, 0);
        data.writeInt(rebind ? 1 : 0);
        data.writeInt(processState);
        this.mRemote.transact(20, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleUnbindService(IBinder token, Intent intent) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        intent.writeToParcel(data, 0);
        this.mRemote.transact(21, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleServiceArgs(IBinder token, boolean taskRemoved, int startId, int flags, Intent args) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(taskRemoved ? 1 : 0);
        data.writeInt(startId);
        data.writeInt(flags);
        if (args != null) {
            data.writeInt(1);
            args.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(17, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleStopService(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(12, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void bindApplication(String packageName, ApplicationInfo info, List<ProviderInfo> providers, ComponentName testName, String profileName, ParcelFileDescriptor profileFd, boolean autoStopProfiler, Bundle testArgs, IInstrumentationWatcher testWatcher, IUiAutomationConnection uiAutomationConnection, int debugMode, boolean openGlTrace, boolean restrictedBackupMode, boolean persistent, Configuration config, CompatibilityInfo compatInfo, Map<String, IBinder> services, Bundle coreSettings) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeString(packageName);
        info.writeToParcel(data, 0);
        data.writeTypedList(providers);
        if (testName == null) {
            data.writeInt(0);
        } else {
            data.writeInt(1);
            testName.writeToParcel(data, 0);
        }
        data.writeString(profileName);
        if (profileFd != null) {
            data.writeInt(1);
            profileFd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        data.writeInt(autoStopProfiler ? 1 : 0);
        data.writeBundle(testArgs);
        data.writeStrongInterface(testWatcher);
        data.writeStrongInterface(uiAutomationConnection);
        data.writeInt(debugMode);
        data.writeInt(openGlTrace ? 1 : 0);
        data.writeInt(restrictedBackupMode ? 1 : 0);
        data.writeInt(persistent ? 1 : 0);
        config.writeToParcel(data, 0);
        compatInfo.writeToParcel(data, 0);
        data.writeMap(services);
        data.writeBundle(coreSettings);
        this.mRemote.transact(13, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleExit() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        this.mRemote.transact(14, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleSuicide() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        this.mRemote.transact(33, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void requestThumbnail(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(15, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleConfigurationChanged(Configuration config) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        config.writeToParcel(data, 0);
        this.mRemote.transact(16, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void updateTimeZone() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        this.mRemote.transact(18, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void clearDnsCache() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        this.mRemote.transact(38, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void setHttpProxy(String proxy, String port, String exclList, String pacFileUrl) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeString(proxy);
        data.writeString(port);
        data.writeString(exclList);
        data.writeString(pacFileUrl);
        this.mRemote.transact(39, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void processInBackground() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        this.mRemote.transact(19, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dumpService(FileDescriptor fd, IBinder token, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeFileDescriptor(fd);
        data.writeStrongBinder(token);
        data.writeStringArray(args);
        this.mRemote.transact(22, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dumpProvider(FileDescriptor fd, IBinder token, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeFileDescriptor(fd);
        data.writeStrongBinder(token);
        data.writeStringArray(args);
        this.mRemote.transact(45, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void scheduleRegisteredReceiver(IIntentReceiver receiver, Intent intent, int resultCode, String dataStr, Bundle extras, boolean ordered, boolean sticky, int sendingUser, int processState) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(receiver.asBinder());
        intent.writeToParcel(data, 0);
        data.writeInt(resultCode);
        data.writeString(dataStr);
        data.writeBundle(extras);
        data.writeInt(ordered ? 1 : 0);
        data.writeInt(sticky ? 1 : 0);
        data.writeInt(sendingUser);
        data.writeInt(processState);
        this.mRemote.transact(23, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleLowMemory() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        this.mRemote.transact(24, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public final void scheduleActivityConfigurationChanged(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(25, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void profilerControl(boolean start, String path, ParcelFileDescriptor fd, int profileType) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeInt(start ? 1 : 0);
        data.writeInt(profileType);
        data.writeString(path);
        if (fd != null) {
            data.writeInt(1);
            fd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(28, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void setSchedulingGroup(int group) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeInt(group);
        this.mRemote.transact(29, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dispatchPackageBroadcast(int cmd, String[] packages) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeInt(cmd);
        data.writeStringArray(packages);
        this.mRemote.transact(34, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void scheduleCrash(String msg) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeString(msg);
        this.mRemote.transact(35, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dumpHeap(boolean managed, String path, ParcelFileDescriptor fd) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeInt(managed ? 1 : 0);
        data.writeString(path);
        if (fd != null) {
            data.writeInt(1);
            fd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(36, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dumpActivity(FileDescriptor fd, IBinder token, String prefix, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeFileDescriptor(fd);
        data.writeStrongBinder(token);
        data.writeString(prefix);
        data.writeStringArray(args);
        this.mRemote.transact(37, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void setCoreSettings(Bundle coreSettings) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeBundle(coreSettings);
        this.mRemote.transact(40, data, null, 1);
    }

    @Override // android.app.IApplicationThread
    public void updatePackageCompatibilityInfo(String pkg, CompatibilityInfo info) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeString(pkg);
        info.writeToParcel(data, 0);
        this.mRemote.transact(41, data, null, 1);
    }

    @Override // android.app.IApplicationThread
    public void scheduleTrimMemory(int level) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeInt(level);
        this.mRemote.transact(42, data, null, 1);
    }

    @Override // android.app.IApplicationThread
    public void dumpMemInfo(FileDescriptor fd, Debug.MemoryInfo mem, boolean checkin, boolean dumpInfo, boolean dumpDalvik, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeFileDescriptor(fd);
        mem.writeToParcel(data, 0);
        data.writeInt(checkin ? 1 : 0);
        data.writeInt(dumpInfo ? 1 : 0);
        data.writeInt(dumpDalvik ? 1 : 0);
        data.writeStringArray(args);
        this.mRemote.transact(43, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dumpGfxInfo(FileDescriptor fd, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeFileDescriptor(fd);
        data.writeStringArray(args);
        this.mRemote.transact(44, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void dumpDbInfo(FileDescriptor fd, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeFileDescriptor(fd);
        data.writeStringArray(args);
        this.mRemote.transact(46, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void unstableProviderDied(IBinder provider) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(provider);
        this.mRemote.transact(47, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void requestAssistContextExtras(IBinder activityToken, IBinder requestToken, int requestType) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(activityToken);
        data.writeStrongBinder(requestToken);
        data.writeInt(requestType);
        this.mRemote.transact(48, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void scheduleTranslucentConversionComplete(IBinder token, boolean timeout) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(timeout ? 1 : 0);
        this.mRemote.transact(49, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void setProcessState(int state) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        data.writeInt(state);
        this.mRemote.transact(50, data, null, 1);
        data.recycle();
    }

    @Override // android.app.IApplicationThread
    public void scheduleInstallProvider(ProviderInfo provider) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IApplicationThread.descriptor);
        provider.writeToParcel(data, 0);
        this.mRemote.transact(51, data, null, 1);
        data.recycle();
    }
}