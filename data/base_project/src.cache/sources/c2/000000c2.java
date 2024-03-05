package android.app;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.IActivityController;
import android.app.IActivityManager;
import android.app.IInstrumentationWatcher;
import android.app.IProcessObserver;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.IThumbnailReceiver;
import android.app.IUiAutomationConnection;
import android.app.IUserSwitchObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Singleton;
import java.util.ArrayList;
import java.util.List;

/* loaded from: ActivityManagerNative.class */
public abstract class ActivityManagerNative extends Binder implements IActivityManager {
    static boolean sSystemReady = false;
    private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() { // from class: android.app.ActivityManagerNative.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IActivityManager create() {
            IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
            IActivityManager am = ActivityManagerNative.asInterface(b);
            return am;
        }
    };

    public static IActivityManager asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IActivityManager in = (IActivityManager) obj.queryLocalInterface(IActivityManager.descriptor);
        if (in != null) {
            return in;
        }
        return new ActivityManagerProxy(obj);
    }

    public static IActivityManager getDefault() {
        return gDefault.get();
    }

    public static boolean isSystemReady() {
        if (!sSystemReady) {
            sSystemReady = getDefault().testIsSystemReady();
        }
        return sSystemReady;
    }

    public static void broadcastStickyIntent(Intent intent, String permission, int userId) {
        try {
            getDefault().broadcastIntent(null, intent, null, null, -1, null, null, null, -1, false, true, userId);
        } catch (RemoteException e) {
        }
    }

    public static void noteWakeupAlarm(PendingIntent ps) {
        try {
            getDefault().noteWakeupAlarm(ps.getTarget());
        } catch (RemoteException e) {
        }
    }

    public ActivityManagerNative() {
        attachInterface(this, IActivityManager.descriptor);
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        Intent[] requestIntents;
        String[] requestResolvedTypes;
        switch (code) {
            case 1:
                data.enforceInterface(IActivityManager.descriptor);
                String pkg = data.readString();
                String cls = data.readString();
                String action = data.readString();
                String indata = data.readString();
                startRunning(pkg, cls, action, indata);
                reply.writeNoException();
                return true;
            case 2:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder app = data.readStrongBinder();
                ApplicationErrorReport.CrashInfo ci = new ApplicationErrorReport.CrashInfo(data);
                handleApplicationCrash(app, ci);
                reply.writeNoException();
                return true;
            case 3:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b = data.readStrongBinder();
                IApplicationThread app2 = ApplicationThreadNative.asInterface(b);
                String callingPackage = data.readString();
                Intent intent = Intent.CREATOR.createFromParcel(data);
                String resolvedType = data.readString();
                IBinder resultTo = data.readStrongBinder();
                String resultWho = data.readString();
                int requestCode = data.readInt();
                int startFlags = data.readInt();
                String profileFile = data.readString();
                ParcelFileDescriptor profileFd = data.readInt() != 0 ? data.readFileDescriptor() : null;
                Bundle options = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int result = startActivity(app2, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, startFlags, profileFile, profileFd, options);
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case 4:
                data.enforceInterface(IActivityManager.descriptor);
                unhandledBack();
                reply.writeNoException();
                return true;
            case 5:
                data.enforceInterface(IActivityManager.descriptor);
                Uri uri = Uri.parse(data.readString());
                ParcelFileDescriptor pfd = openContentUri(uri);
                reply.writeNoException();
                if (pfd != null) {
                    reply.writeInt(1);
                    pfd.writeToParcel(reply, 1);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 160:
            default:
                return super.onTransact(code, data, reply, flags);
            case 11:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token = data.readStrongBinder();
                Intent resultData = null;
                int resultCode = data.readInt();
                if (data.readInt() != 0) {
                    resultData = Intent.CREATOR.createFromParcel(data);
                }
                boolean res = finishActivity(token, resultCode, resultData);
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case 12:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b2 = data.readStrongBinder();
                IApplicationThread app3 = b2 != null ? ApplicationThreadNative.asInterface(b2) : null;
                String packageName = data.readString();
                IBinder b3 = data.readStrongBinder();
                IIntentReceiver rec = b3 != null ? IIntentReceiver.Stub.asInterface(b3) : null;
                IntentFilter filter = IntentFilter.CREATOR.createFromParcel(data);
                String perm = data.readString();
                int userId = data.readInt();
                Intent intent2 = registerReceiver(app3, packageName, rec, filter, perm, userId);
                reply.writeNoException();
                if (intent2 != null) {
                    reply.writeInt(1);
                    intent2.writeToParcel(reply, 0);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 13:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b4 = data.readStrongBinder();
                if (b4 == null) {
                    return true;
                }
                IIntentReceiver rec2 = IIntentReceiver.Stub.asInterface(b4);
                unregisterReceiver(rec2);
                reply.writeNoException();
                return true;
            case 14:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b5 = data.readStrongBinder();
                IApplicationThread app4 = b5 != null ? ApplicationThreadNative.asInterface(b5) : null;
                Intent intent3 = Intent.CREATOR.createFromParcel(data);
                String resolvedType2 = data.readString();
                IBinder b6 = data.readStrongBinder();
                IIntentReceiver resultTo2 = b6 != null ? IIntentReceiver.Stub.asInterface(b6) : null;
                int resultCode2 = data.readInt();
                String resultData2 = data.readString();
                Bundle resultExtras = data.readBundle();
                String perm2 = data.readString();
                int appOp = data.readInt();
                boolean serialized = data.readInt() != 0;
                boolean sticky = data.readInt() != 0;
                int userId2 = data.readInt();
                int res2 = broadcastIntent(app4, intent3, resolvedType2, resultTo2, resultCode2, resultData2, resultExtras, perm2, appOp, serialized, sticky, userId2);
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case 15:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b7 = data.readStrongBinder();
                IApplicationThread app5 = b7 != null ? ApplicationThreadNative.asInterface(b7) : null;
                Intent intent4 = Intent.CREATOR.createFromParcel(data);
                int userId3 = data.readInt();
                unbroadcastIntent(app5, intent4, userId3);
                reply.writeNoException();
                return true;
            case 16:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder who = data.readStrongBinder();
                int resultCode3 = data.readInt();
                String resultData3 = data.readString();
                Bundle resultExtras2 = data.readBundle();
                boolean resultAbort = data.readInt() != 0;
                if (who != null) {
                    finishReceiver(who, resultCode3, resultData3, resultExtras2, resultAbort);
                }
                reply.writeNoException();
                return true;
            case 17:
                data.enforceInterface(IActivityManager.descriptor);
                IApplicationThread app6 = ApplicationThreadNative.asInterface(data.readStrongBinder());
                if (app6 != null) {
                    attachApplication(app6);
                }
                reply.writeNoException();
                return true;
            case 18:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token2 = data.readStrongBinder();
                Configuration config = null;
                if (data.readInt() != 0) {
                    config = Configuration.CREATOR.createFromParcel(data);
                }
                boolean stopProfiling = data.readInt() != 0;
                if (token2 != null) {
                    activityIdle(token2, config, stopProfiling);
                }
                reply.writeNoException();
                return true;
            case 19:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token3 = data.readStrongBinder();
                activityPaused(token3);
                reply.writeNoException();
                return true;
            case 20:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token4 = data.readStrongBinder();
                Bundle map = data.readBundle();
                Bitmap thumbnail = data.readInt() != 0 ? Bitmap.CREATOR.createFromParcel(data) : null;
                CharSequence description = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                activityStopped(token4, map, thumbnail, description);
                reply.writeNoException();
                return true;
            case 21:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token5 = data.readStrongBinder();
                String res3 = token5 != null ? getCallingPackage(token5) : null;
                reply.writeNoException();
                reply.writeString(res3);
                return true;
            case 22:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token6 = data.readStrongBinder();
                ComponentName cn = getCallingActivity(token6);
                reply.writeNoException();
                ComponentName.writeToParcel(cn, reply);
                return true;
            case 23:
                data.enforceInterface(IActivityManager.descriptor);
                int maxNum = data.readInt();
                int fl = data.readInt();
                IBinder receiverBinder = data.readStrongBinder();
                IThumbnailReceiver receiver = receiverBinder != null ? IThumbnailReceiver.Stub.asInterface(receiverBinder) : null;
                List<ActivityManager.RunningTaskInfo> list = getTasks(maxNum, fl, receiver);
                reply.writeNoException();
                int N = list != null ? list.size() : -1;
                reply.writeInt(N);
                for (int i = 0; i < N; i++) {
                    ActivityManager.RunningTaskInfo info = list.get(i);
                    info.writeToParcel(reply, 0);
                }
                return true;
            case 24:
                data.enforceInterface(IActivityManager.descriptor);
                int task = data.readInt();
                int fl2 = data.readInt();
                Bundle options2 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                moveTaskToFront(task, fl2, options2);
                reply.writeNoException();
                return true;
            case 25:
                data.enforceInterface(IActivityManager.descriptor);
                int task2 = data.readInt();
                moveTaskToBack(task2);
                reply.writeNoException();
                return true;
            case 26:
                data.enforceInterface(IActivityManager.descriptor);
                int task3 = data.readInt();
                moveTaskBackwards(task3);
                reply.writeNoException();
                return true;
            case 27:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token7 = data.readStrongBinder();
                boolean onlyRoot = data.readInt() != 0;
                int res4 = token7 != null ? getTaskForActivity(token7, onlyRoot) : -1;
                reply.writeNoException();
                reply.writeInt(res4);
                return true;
            case 28:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token8 = data.readStrongBinder();
                Bitmap thumbnail2 = data.readInt() != 0 ? Bitmap.CREATOR.createFromParcel(data) : null;
                CharSequence description2 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                reportThumbnail(token8, thumbnail2, description2);
                reply.writeNoException();
                return true;
            case 29:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b8 = data.readStrongBinder();
                IApplicationThread app7 = ApplicationThreadNative.asInterface(b8);
                String name = data.readString();
                int userId4 = data.readInt();
                boolean stable = data.readInt() != 0;
                IActivityManager.ContentProviderHolder cph = getContentProvider(app7, name, userId4, stable);
                reply.writeNoException();
                if (cph != null) {
                    reply.writeInt(1);
                    cph.writeToParcel(reply, 0);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 30:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b9 = data.readStrongBinder();
                IApplicationThread app8 = ApplicationThreadNative.asInterface(b9);
                ArrayList<IActivityManager.ContentProviderHolder> providers = data.createTypedArrayList(IActivityManager.ContentProviderHolder.CREATOR);
                publishContentProviders(app8, providers);
                reply.writeNoException();
                return true;
            case 31:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b10 = data.readStrongBinder();
                int stable2 = data.readInt();
                int unstable = data.readInt();
                boolean res5 = refContentProvider(b10, stable2, unstable);
                reply.writeNoException();
                reply.writeInt(res5 ? 1 : 0);
                return true;
            case 32:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token9 = data.readStrongBinder();
                String resultWho2 = data.readString();
                int requestCode2 = data.readInt();
                finishSubActivity(token9, resultWho2, requestCode2);
                reply.writeNoException();
                return true;
            case 33:
                data.enforceInterface(IActivityManager.descriptor);
                ComponentName comp = ComponentName.CREATOR.createFromParcel(data);
                PendingIntent pi = getRunningServiceControlPanel(comp);
                reply.writeNoException();
                PendingIntent.writePendingIntentOrNullToParcel(pi, reply);
                return true;
            case 34:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b11 = data.readStrongBinder();
                IApplicationThread app9 = ApplicationThreadNative.asInterface(b11);
                Intent service = Intent.CREATOR.createFromParcel(data);
                String resolvedType3 = data.readString();
                int userId5 = data.readInt();
                ComponentName cn2 = startService(app9, service, resolvedType3, userId5);
                reply.writeNoException();
                ComponentName.writeToParcel(cn2, reply);
                return true;
            case 35:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b12 = data.readStrongBinder();
                IApplicationThread app10 = ApplicationThreadNative.asInterface(b12);
                Intent service2 = Intent.CREATOR.createFromParcel(data);
                String resolvedType4 = data.readString();
                int userId6 = data.readInt();
                int res6 = stopService(app10, service2, resolvedType4, userId6);
                reply.writeNoException();
                reply.writeInt(res6);
                return true;
            case 36:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b13 = data.readStrongBinder();
                IApplicationThread app11 = ApplicationThreadNative.asInterface(b13);
                IBinder token10 = data.readStrongBinder();
                Intent service3 = Intent.CREATOR.createFromParcel(data);
                String resolvedType5 = data.readString();
                IBinder b14 = data.readStrongBinder();
                int fl3 = data.readInt();
                int userId7 = data.readInt();
                IServiceConnection conn = IServiceConnection.Stub.asInterface(b14);
                int res7 = bindService(app11, token10, service3, resolvedType5, conn, fl3, userId7);
                reply.writeNoException();
                reply.writeInt(res7);
                return true;
            case 37:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b15 = data.readStrongBinder();
                IServiceConnection conn2 = IServiceConnection.Stub.asInterface(b15);
                boolean res8 = unbindService(conn2);
                reply.writeNoException();
                reply.writeInt(res8 ? 1 : 0);
                return true;
            case 38:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token11 = data.readStrongBinder();
                Intent intent5 = Intent.CREATOR.createFromParcel(data);
                IBinder service4 = data.readStrongBinder();
                publishService(token11, intent5, service4);
                reply.writeNoException();
                return true;
            case 39:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token12 = data.readStrongBinder();
                activityResumed(token12);
                reply.writeNoException();
                return true;
            case 40:
                data.enforceInterface(IActivityManager.descriptor);
                goingToSleep();
                reply.writeNoException();
                return true;
            case 41:
                data.enforceInterface(IActivityManager.descriptor);
                wakingUp();
                reply.writeNoException();
                return true;
            case 42:
                data.enforceInterface(IActivityManager.descriptor);
                String pn = data.readString();
                boolean wfd = data.readInt() != 0;
                boolean per = data.readInt() != 0;
                setDebugApp(pn, wfd, per);
                reply.writeNoException();
                return true;
            case 43:
                data.enforceInterface(IActivityManager.descriptor);
                boolean enabled = data.readInt() != 0;
                setAlwaysFinish(enabled);
                reply.writeNoException();
                return true;
            case 44:
                data.enforceInterface(IActivityManager.descriptor);
                ComponentName className = ComponentName.readFromParcel(data);
                String profileFile2 = data.readString();
                int fl4 = data.readInt();
                Bundle arguments = data.readBundle();
                IBinder b16 = data.readStrongBinder();
                IInstrumentationWatcher w = IInstrumentationWatcher.Stub.asInterface(b16);
                IBinder b17 = data.readStrongBinder();
                IUiAutomationConnection c = IUiAutomationConnection.Stub.asInterface(b17);
                int userId8 = data.readInt();
                boolean res9 = startInstrumentation(className, profileFile2, fl4, arguments, w, c, userId8);
                reply.writeNoException();
                reply.writeInt(res9 ? 1 : 0);
                return true;
            case 45:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b18 = data.readStrongBinder();
                IApplicationThread app12 = ApplicationThreadNative.asInterface(b18);
                int resultCode4 = data.readInt();
                Bundle results = data.readBundle();
                finishInstrumentation(app12, resultCode4, results);
                reply.writeNoException();
                return true;
            case 46:
                data.enforceInterface(IActivityManager.descriptor);
                Configuration config2 = getConfiguration();
                reply.writeNoException();
                config2.writeToParcel(reply, 0);
                return true;
            case 47:
                data.enforceInterface(IActivityManager.descriptor);
                Configuration config3 = Configuration.CREATOR.createFromParcel(data);
                updateConfiguration(config3);
                reply.writeNoException();
                return true;
            case 48:
                data.enforceInterface(IActivityManager.descriptor);
                ComponentName className2 = ComponentName.readFromParcel(data);
                IBinder token13 = data.readStrongBinder();
                int startId = data.readInt();
                boolean res10 = stopServiceToken(className2, token13, startId);
                reply.writeNoException();
                reply.writeInt(res10 ? 1 : 0);
                return true;
            case 49:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token14 = data.readStrongBinder();
                ComponentName cn3 = getActivityClassForToken(token14);
                reply.writeNoException();
                ComponentName.writeToParcel(cn3, reply);
                return true;
            case 50:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token15 = data.readStrongBinder();
                reply.writeNoException();
                reply.writeString(getPackageForToken(token15));
                return true;
            case 51:
                data.enforceInterface(IActivityManager.descriptor);
                int max = data.readInt();
                setProcessLimit(max);
                reply.writeNoException();
                return true;
            case 52:
                data.enforceInterface(IActivityManager.descriptor);
                int limit = getProcessLimit();
                reply.writeNoException();
                reply.writeInt(limit);
                return true;
            case 53:
                data.enforceInterface(IActivityManager.descriptor);
                String perm3 = data.readString();
                int pid = data.readInt();
                int uid = data.readInt();
                int res11 = checkPermission(perm3, pid, uid);
                reply.writeNoException();
                reply.writeInt(res11);
                return true;
            case 54:
                data.enforceInterface(IActivityManager.descriptor);
                Uri uri2 = Uri.CREATOR.createFromParcel(data);
                int pid2 = data.readInt();
                int uid2 = data.readInt();
                int mode = data.readInt();
                int res12 = checkUriPermission(uri2, pid2, uid2, mode);
                reply.writeNoException();
                reply.writeInt(res12);
                return true;
            case 55:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b19 = data.readStrongBinder();
                IApplicationThread app13 = ApplicationThreadNative.asInterface(b19);
                String targetPkg = data.readString();
                Uri uri3 = Uri.CREATOR.createFromParcel(data);
                int mode2 = data.readInt();
                grantUriPermission(app13, targetPkg, uri3, mode2);
                reply.writeNoException();
                return true;
            case 56:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b20 = data.readStrongBinder();
                IApplicationThread app14 = ApplicationThreadNative.asInterface(b20);
                Uri uri4 = Uri.CREATOR.createFromParcel(data);
                int mode3 = data.readInt();
                revokeUriPermission(app14, uri4, mode3);
                reply.writeNoException();
                return true;
            case 57:
                data.enforceInterface(IActivityManager.descriptor);
                IActivityController watcher = IActivityController.Stub.asInterface(data.readStrongBinder());
                setActivityController(watcher);
                reply.writeNoException();
                return true;
            case 58:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b21 = data.readStrongBinder();
                IApplicationThread app15 = ApplicationThreadNative.asInterface(b21);
                boolean waiting = data.readInt() != 0;
                showWaitingForDebugger(app15, waiting);
                reply.writeNoException();
                return true;
            case 59:
                data.enforceInterface(IActivityManager.descriptor);
                int sig = data.readInt();
                signalPersistentProcesses(sig);
                reply.writeNoException();
                return true;
            case 60:
                data.enforceInterface(IActivityManager.descriptor);
                int maxNum2 = data.readInt();
                int fl5 = data.readInt();
                int userId9 = data.readInt();
                List<ActivityManager.RecentTaskInfo> list2 = getRecentTasks(maxNum2, fl5, userId9);
                reply.writeNoException();
                reply.writeTypedList(list2);
                return true;
            case 61:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token16 = data.readStrongBinder();
                int type = data.readInt();
                int startId2 = data.readInt();
                int res13 = data.readInt();
                serviceDoneExecuting(token16, type, startId2, res13);
                reply.writeNoException();
                return true;
            case 62:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token17 = data.readStrongBinder();
                activityDestroyed(token17);
                reply.writeNoException();
                return true;
            case 63:
                data.enforceInterface(IActivityManager.descriptor);
                int type2 = data.readInt();
                String packageName2 = data.readString();
                IBinder token18 = data.readStrongBinder();
                String resultWho3 = data.readString();
                int requestCode3 = data.readInt();
                if (data.readInt() != 0) {
                    requestIntents = (Intent[]) data.createTypedArray(Intent.CREATOR);
                    requestResolvedTypes = data.createStringArray();
                } else {
                    requestIntents = null;
                    requestResolvedTypes = null;
                }
                int fl6 = data.readInt();
                Bundle options3 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int userId10 = data.readInt();
                IIntentSender res14 = getIntentSender(type2, packageName2, token18, resultWho3, requestCode3, requestIntents, requestResolvedTypes, fl6, options3, userId10);
                reply.writeNoException();
                reply.writeStrongBinder(res14 != null ? res14.asBinder() : null);
                return true;
            case 64:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender r = IIntentSender.Stub.asInterface(data.readStrongBinder());
                cancelIntentSender(r);
                reply.writeNoException();
                return true;
            case 65:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender r2 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                String res15 = getPackageForIntentSender(r2);
                reply.writeNoException();
                reply.writeString(res15);
                return true;
            case 66:
                data.enforceInterface(IActivityManager.descriptor);
                enterSafeMode();
                reply.writeNoException();
                return true;
            case 67:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder callingActivity = data.readStrongBinder();
                Intent intent6 = Intent.CREATOR.createFromParcel(data);
                Bundle options4 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                boolean result2 = startNextMatchingActivity(callingActivity, intent6, options4);
                reply.writeNoException();
                reply.writeInt(result2 ? 1 : 0);
                return true;
            case 68:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender is = IIntentSender.Stub.asInterface(data.readStrongBinder());
                noteWakeupAlarm(is);
                reply.writeNoException();
                return true;
            case 69:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b22 = data.readStrongBinder();
                boolean stable3 = data.readInt() != 0;
                removeContentProvider(b22, stable3);
                reply.writeNoException();
                return true;
            case 70:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token19 = data.readStrongBinder();
                int requestedOrientation = data.readInt();
                setRequestedOrientation(token19, requestedOrientation);
                reply.writeNoException();
                return true;
            case 71:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token20 = data.readStrongBinder();
                int req = getRequestedOrientation(token20);
                reply.writeNoException();
                reply.writeInt(req);
                return true;
            case 72:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token21 = data.readStrongBinder();
                Intent intent7 = Intent.CREATOR.createFromParcel(data);
                boolean doRebind = data.readInt() != 0;
                unbindFinished(token21, intent7, doRebind);
                reply.writeNoException();
                return true;
            case 73:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token22 = data.readStrongBinder();
                int pid3 = data.readInt();
                boolean isForeground = data.readInt() != 0;
                setProcessForeground(token22, pid3, isForeground);
                reply.writeNoException();
                return true;
            case 74:
                data.enforceInterface(IActivityManager.descriptor);
                ComponentName className3 = ComponentName.readFromParcel(data);
                IBinder token23 = data.readStrongBinder();
                int id = data.readInt();
                Notification notification = null;
                if (data.readInt() != 0) {
                    notification = Notification.CREATOR.createFromParcel(data);
                }
                boolean removeNotification = data.readInt() != 0;
                setServiceForeground(className3, token23, id, notification, removeNotification);
                reply.writeNoException();
                return true;
            case 75:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token24 = data.readStrongBinder();
                boolean nonRoot = data.readInt() != 0;
                boolean res16 = moveActivityTaskToBack(token24, nonRoot);
                reply.writeNoException();
                reply.writeInt(res16 ? 1 : 0);
                return true;
            case 76:
                data.enforceInterface(IActivityManager.descriptor);
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                getMemoryInfo(mi);
                reply.writeNoException();
                mi.writeToParcel(reply, 0);
                return true;
            case 77:
                data.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.ProcessErrorStateInfo> list3 = getProcessesInErrorState();
                reply.writeNoException();
                reply.writeTypedList(list3);
                return true;
            case 78:
                data.enforceInterface(IActivityManager.descriptor);
                String packageName3 = data.readString();
                IPackageDataObserver observer = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                int userId11 = data.readInt();
                boolean res17 = clearApplicationUserData(packageName3, observer, userId11);
                reply.writeNoException();
                reply.writeInt(res17 ? 1 : 0);
                return true;
            case 79:
                data.enforceInterface(IActivityManager.descriptor);
                String packageName4 = data.readString();
                int userId12 = data.readInt();
                forceStopPackage(packageName4, userId12);
                reply.writeNoException();
                return true;
            case 80:
                data.enforceInterface(IActivityManager.descriptor);
                int[] pids = data.createIntArray();
                String reason = data.readString();
                boolean secure = data.readInt() != 0;
                boolean res18 = killPids(pids, reason, secure);
                reply.writeNoException();
                reply.writeInt(res18 ? 1 : 0);
                return true;
            case 81:
                data.enforceInterface(IActivityManager.descriptor);
                int maxNum3 = data.readInt();
                int fl7 = data.readInt();
                List<ActivityManager.RunningServiceInfo> list4 = getServices(maxNum3, fl7);
                reply.writeNoException();
                int N2 = list4 != null ? list4.size() : -1;
                reply.writeInt(N2);
                for (int i2 = 0; i2 < N2; i2++) {
                    ActivityManager.RunningServiceInfo info2 = list4.get(i2);
                    info2.writeToParcel(reply, 0);
                }
                return true;
            case 82:
                data.enforceInterface(IActivityManager.descriptor);
                int id2 = data.readInt();
                ActivityManager.TaskThumbnails bm = getTaskThumbnails(id2);
                reply.writeNoException();
                if (bm != null) {
                    reply.writeInt(1);
                    bm.writeToParcel(reply, 0);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 83:
                data.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.RunningAppProcessInfo> list5 = getRunningAppProcesses();
                reply.writeNoException();
                reply.writeTypedList(list5);
                return true;
            case 84:
                data.enforceInterface(IActivityManager.descriptor);
                ConfigurationInfo config4 = getDeviceConfigurationInfo();
                reply.writeNoException();
                config4.writeToParcel(reply, 0);
                return true;
            case 85:
                data.enforceInterface(IActivityManager.descriptor);
                Intent service5 = Intent.CREATOR.createFromParcel(data);
                String resolvedType6 = data.readString();
                IBinder binder = peekService(service5, resolvedType6);
                reply.writeNoException();
                reply.writeStrongBinder(binder);
                return true;
            case 86:
                data.enforceInterface(IActivityManager.descriptor);
                String process = data.readString();
                int userId13 = data.readInt();
                boolean start = data.readInt() != 0;
                int profileType = data.readInt();
                String path = data.readString();
                ParcelFileDescriptor fd = data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null;
                boolean res19 = profileControl(process, userId13, start, path, fd, profileType);
                reply.writeNoException();
                reply.writeInt(res19 ? 1 : 0);
                return true;
            case 87:
                data.enforceInterface(IActivityManager.descriptor);
                boolean res20 = shutdown(data.readInt());
                reply.writeNoException();
                reply.writeInt(res20 ? 1 : 0);
                return true;
            case 88:
                data.enforceInterface(IActivityManager.descriptor);
                stopAppSwitches();
                reply.writeNoException();
                return true;
            case 89:
                data.enforceInterface(IActivityManager.descriptor);
                resumeAppSwitches();
                reply.writeNoException();
                return true;
            case 90:
                data.enforceInterface(IActivityManager.descriptor);
                ApplicationInfo info3 = ApplicationInfo.CREATOR.createFromParcel(data);
                int backupRestoreMode = data.readInt();
                boolean success = bindBackupAgent(info3, backupRestoreMode);
                reply.writeNoException();
                reply.writeInt(success ? 1 : 0);
                return true;
            case 91:
                data.enforceInterface(IActivityManager.descriptor);
                String packageName5 = data.readString();
                IBinder agent = data.readStrongBinder();
                backupAgentCreated(packageName5, agent);
                reply.writeNoException();
                return true;
            case 92:
                data.enforceInterface(IActivityManager.descriptor);
                ApplicationInfo info4 = ApplicationInfo.CREATOR.createFromParcel(data);
                unbindBackupAgent(info4);
                reply.writeNoException();
                return true;
            case 93:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender r3 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                int res21 = getUidForIntentSender(r3);
                reply.writeNoException();
                reply.writeInt(res21);
                return true;
            case 94:
                data.enforceInterface(IActivityManager.descriptor);
                int callingPid = data.readInt();
                int callingUid = data.readInt();
                int userId14 = data.readInt();
                boolean allowAll = data.readInt() != 0;
                boolean requireFull = data.readInt() != 0;
                String name2 = data.readString();
                String callerPackage = data.readString();
                int res22 = handleIncomingUser(callingPid, callingUid, userId14, allowAll, requireFull, name2, callerPackage);
                reply.writeNoException();
                reply.writeInt(res22);
                return true;
            case 95:
                data.enforceInterface(IActivityManager.descriptor);
                int id3 = data.readInt();
                Bitmap bm2 = getTaskTopThumbnail(id3);
                reply.writeNoException();
                if (bm2 != null) {
                    reply.writeInt(1);
                    bm2.writeToParcel(reply, 0);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 96:
                data.enforceInterface(IActivityManager.descriptor);
                String pkg2 = data.readString();
                int appid = data.readInt();
                String reason2 = data.readString();
                killApplicationWithAppId(pkg2, appid, reason2);
                reply.writeNoException();
                return true;
            case 97:
                data.enforceInterface(IActivityManager.descriptor);
                String reason3 = data.readString();
                closeSystemDialogs(reason3);
                reply.writeNoException();
                return true;
            case 98:
                data.enforceInterface(IActivityManager.descriptor);
                int[] pids2 = data.createIntArray();
                Debug.MemoryInfo[] res23 = getProcessMemoryInfo(pids2);
                reply.writeNoException();
                reply.writeTypedArray(res23, 1);
                return true;
            case 99:
                data.enforceInterface(IActivityManager.descriptor);
                String processName = data.readString();
                int uid3 = data.readInt();
                killApplicationProcess(processName, uid3);
                reply.writeNoException();
                return true;
            case 100:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b23 = data.readStrongBinder();
                IApplicationThread app16 = ApplicationThreadNative.asInterface(b23);
                IntentSender intent8 = IntentSender.CREATOR.createFromParcel(data);
                Intent fillInIntent = null;
                if (data.readInt() != 0) {
                    fillInIntent = Intent.CREATOR.createFromParcel(data);
                }
                String resolvedType7 = data.readString();
                IBinder resultTo3 = data.readStrongBinder();
                String resultWho4 = data.readString();
                int requestCode4 = data.readInt();
                int flagsMask = data.readInt();
                int flagsValues = data.readInt();
                Bundle options5 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int result3 = startActivityIntentSender(app16, intent8, fillInIntent, resolvedType7, resultTo3, resultWho4, requestCode4, flagsMask, flagsValues, options5);
                reply.writeNoException();
                reply.writeInt(result3);
                return true;
            case 101:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token25 = data.readStrongBinder();
                String packageName6 = data.readString();
                int enterAnim = data.readInt();
                int exitAnim = data.readInt();
                overridePendingTransition(token25, packageName6, enterAnim, exitAnim);
                reply.writeNoException();
                return true;
            case 102:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder app17 = data.readStrongBinder();
                String tag = data.readString();
                ApplicationErrorReport.CrashInfo ci2 = new ApplicationErrorReport.CrashInfo(data);
                boolean res24 = handleApplicationWtf(app17, tag, ci2);
                reply.writeNoException();
                reply.writeInt(res24 ? 1 : 0);
                return true;
            case 103:
                data.enforceInterface(IActivityManager.descriptor);
                String packageName7 = data.readString();
                int userId15 = data.readInt();
                killBackgroundProcesses(packageName7, userId15);
                reply.writeNoException();
                return true;
            case 104:
                data.enforceInterface(IActivityManager.descriptor);
                boolean areThey = isUserAMonkey();
                reply.writeNoException();
                reply.writeInt(areThey ? 1 : 0);
                return true;
            case 105:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b24 = data.readStrongBinder();
                IApplicationThread app18 = ApplicationThreadNative.asInterface(b24);
                String callingPackage2 = data.readString();
                Intent intent9 = Intent.CREATOR.createFromParcel(data);
                String resolvedType8 = data.readString();
                IBinder resultTo4 = data.readStrongBinder();
                String resultWho5 = data.readString();
                int requestCode5 = data.readInt();
                int startFlags2 = data.readInt();
                String profileFile3 = data.readString();
                ParcelFileDescriptor profileFd2 = data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null;
                Bundle options6 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int userId16 = data.readInt();
                IActivityManager.WaitResult result4 = startActivityAndWait(app18, callingPackage2, intent9, resolvedType8, resultTo4, resultWho5, requestCode5, startFlags2, profileFile3, profileFd2, options6, userId16);
                reply.writeNoException();
                result4.writeToParcel(reply, 0);
                return true;
            case 106:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token26 = data.readStrongBinder();
                boolean res25 = willActivityBeVisible(token26);
                reply.writeNoException();
                reply.writeInt(res25 ? 1 : 0);
                return true;
            case 107:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b25 = data.readStrongBinder();
                IApplicationThread app19 = ApplicationThreadNative.asInterface(b25);
                String callingPackage3 = data.readString();
                Intent intent10 = Intent.CREATOR.createFromParcel(data);
                String resolvedType9 = data.readString();
                IBinder resultTo5 = data.readStrongBinder();
                String resultWho6 = data.readString();
                int requestCode6 = data.readInt();
                int startFlags3 = data.readInt();
                Configuration config5 = Configuration.CREATOR.createFromParcel(data);
                Bundle options7 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int userId17 = data.readInt();
                int result5 = startActivityWithConfig(app19, callingPackage3, intent10, resolvedType9, resultTo5, resultWho6, requestCode6, startFlags3, config5, options7, userId17);
                reply.writeNoException();
                reply.writeInt(result5);
                return true;
            case 108:
                data.enforceInterface(IActivityManager.descriptor);
                List<ApplicationInfo> list6 = getRunningExternalApplications();
                reply.writeNoException();
                reply.writeTypedList(list6);
                return true;
            case 109:
                data.enforceInterface(IActivityManager.descriptor);
                finishHeavyWeightApp();
                reply.writeNoException();
                return true;
            case 110:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder app20 = data.readStrongBinder();
                int violationMask = data.readInt();
                StrictMode.ViolationInfo info5 = new StrictMode.ViolationInfo(data);
                handleApplicationStrictModeViolation(app20, violationMask, info5);
                reply.writeNoException();
                return true;
            case 111:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token27 = data.readStrongBinder();
                boolean isit = isImmersive(token27);
                reply.writeNoException();
                reply.writeInt(isit ? 1 : 0);
                return true;
            case 112:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token28 = data.readStrongBinder();
                boolean imm = data.readInt() == 1;
                setImmersive(token28, imm);
                reply.writeNoException();
                return true;
            case 113:
                data.enforceInterface(IActivityManager.descriptor);
                boolean isit2 = isTopActivityImmersive();
                reply.writeNoException();
                reply.writeInt(isit2 ? 1 : 0);
                return true;
            case 114:
                data.enforceInterface(IActivityManager.descriptor);
                int uid4 = data.readInt();
                int initialPid = data.readInt();
                String packageName8 = data.readString();
                String message = data.readString();
                crashApplication(uid4, initialPid, packageName8, message);
                reply.writeNoException();
                return true;
            case 115:
                data.enforceInterface(IActivityManager.descriptor);
                Uri uri5 = Uri.CREATOR.createFromParcel(data);
                int userId18 = data.readInt();
                String type3 = getProviderMimeType(uri5, userId18);
                reply.writeNoException();
                reply.writeString(type3);
                return true;
            case 116:
                data.enforceInterface(IActivityManager.descriptor);
                String name3 = data.readString();
                IBinder perm4 = newUriPermissionOwner(name3);
                reply.writeNoException();
                reply.writeStrongBinder(perm4);
                return true;
            case 117:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder owner = data.readStrongBinder();
                int fromUid = data.readInt();
                String targetPkg2 = data.readString();
                Uri uri6 = Uri.CREATOR.createFromParcel(data);
                int mode4 = data.readInt();
                grantUriPermissionFromOwner(owner, fromUid, targetPkg2, uri6, mode4);
                reply.writeNoException();
                return true;
            case 118:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder owner2 = data.readStrongBinder();
                if (data.readInt() != 0) {
                    Uri.CREATOR.createFromParcel(data);
                }
                int mode5 = data.readInt();
                revokeUriPermissionFromOwner(owner2, null, mode5);
                reply.writeNoException();
                return true;
            case 119:
                data.enforceInterface(IActivityManager.descriptor);
                int callingUid2 = data.readInt();
                String targetPkg3 = data.readString();
                Uri uri7 = Uri.CREATOR.createFromParcel(data);
                int modeFlags = data.readInt();
                int res26 = checkGrantUriPermission(callingUid2, targetPkg3, uri7, modeFlags);
                reply.writeNoException();
                reply.writeInt(res26);
                return true;
            case 120:
                data.enforceInterface(IActivityManager.descriptor);
                String process2 = data.readString();
                int userId19 = data.readInt();
                boolean managed = data.readInt() != 0;
                String path2 = data.readString();
                ParcelFileDescriptor fd2 = data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null;
                boolean res27 = dumpHeap(process2, userId19, managed, path2, fd2);
                reply.writeNoException();
                reply.writeInt(res27 ? 1 : 0);
                return true;
            case 121:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b26 = data.readStrongBinder();
                IApplicationThread app21 = ApplicationThreadNative.asInterface(b26);
                String callingPackage4 = data.readString();
                Intent[] intents = (Intent[]) data.createTypedArray(Intent.CREATOR);
                String[] resolvedTypes = data.createStringArray();
                IBinder resultTo6 = data.readStrongBinder();
                Bundle options8 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int userId20 = data.readInt();
                int result6 = startActivities(app21, callingPackage4, intents, resolvedTypes, resultTo6, options8, userId20);
                reply.writeNoException();
                reply.writeInt(result6);
                return true;
            case 122:
                data.enforceInterface(IActivityManager.descriptor);
                int userid = data.readInt();
                boolean orStopping = data.readInt() != 0;
                boolean result7 = isUserRunning(userid, orStopping);
                reply.writeNoException();
                reply.writeInt(result7 ? 1 : 0);
                return true;
            case 123:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token29 = data.readStrongBinder();
                activitySlept(token29);
                reply.writeNoException();
                return true;
            case 124:
                data.enforceInterface(IActivityManager.descriptor);
                int mode6 = getFrontActivityScreenCompatMode();
                reply.writeNoException();
                reply.writeInt(mode6);
                return true;
            case 125:
                data.enforceInterface(IActivityManager.descriptor);
                int mode7 = data.readInt();
                setFrontActivityScreenCompatMode(mode7);
                reply.writeNoException();
                reply.writeInt(mode7);
                return true;
            case 126:
                data.enforceInterface(IActivityManager.descriptor);
                String pkg3 = data.readString();
                int mode8 = getPackageScreenCompatMode(pkg3);
                reply.writeNoException();
                reply.writeInt(mode8);
                return true;
            case 127:
                data.enforceInterface(IActivityManager.descriptor);
                String pkg4 = data.readString();
                int mode9 = data.readInt();
                setPackageScreenCompatMode(pkg4, mode9);
                reply.writeNoException();
                return true;
            case 128:
                data.enforceInterface(IActivityManager.descriptor);
                String pkg5 = data.readString();
                boolean ask = getPackageAskScreenCompat(pkg5);
                reply.writeNoException();
                reply.writeInt(ask ? 1 : 0);
                return true;
            case 129:
                data.enforceInterface(IActivityManager.descriptor);
                String pkg6 = data.readString();
                boolean ask2 = data.readInt() != 0;
                setPackageAskScreenCompat(pkg6, ask2);
                reply.writeNoException();
                return true;
            case 130:
                data.enforceInterface(IActivityManager.descriptor);
                int userid2 = data.readInt();
                boolean result8 = switchUser(userid2);
                reply.writeNoException();
                reply.writeInt(result8 ? 1 : 0);
                return true;
            case 131:
                data.enforceInterface(IActivityManager.descriptor);
                int taskId = data.readInt();
                int subTaskIndex = data.readInt();
                boolean result9 = removeSubTask(taskId, subTaskIndex);
                reply.writeNoException();
                reply.writeInt(result9 ? 1 : 0);
                return true;
            case 132:
                data.enforceInterface(IActivityManager.descriptor);
                int taskId2 = data.readInt();
                int fl8 = data.readInt();
                boolean result10 = removeTask(taskId2, fl8);
                reply.writeNoException();
                reply.writeInt(result10 ? 1 : 0);
                return true;
            case 133:
                data.enforceInterface(IActivityManager.descriptor);
                IProcessObserver observer2 = IProcessObserver.Stub.asInterface(data.readStrongBinder());
                registerProcessObserver(observer2);
                return true;
            case 134:
                data.enforceInterface(IActivityManager.descriptor);
                IProcessObserver observer3 = IProcessObserver.Stub.asInterface(data.readStrongBinder());
                unregisterProcessObserver(observer3);
                return true;
            case 135:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender r4 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                boolean res28 = isIntentSenderTargetedToPackage(r4);
                reply.writeNoException();
                reply.writeInt(res28 ? 1 : 0);
                return true;
            case 136:
                data.enforceInterface(IActivityManager.descriptor);
                Configuration config6 = Configuration.CREATOR.createFromParcel(data);
                updatePersistentConfiguration(config6);
                reply.writeNoException();
                return true;
            case 137:
                data.enforceInterface(IActivityManager.descriptor);
                int[] pids3 = data.createIntArray();
                long[] pss = getProcessPss(pids3);
                reply.writeNoException();
                reply.writeLongArray(pss);
                return true;
            case 138:
                data.enforceInterface(IActivityManager.descriptor);
                CharSequence msg = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                boolean always = data.readInt() != 0;
                showBootMessage(msg, always);
                reply.writeNoException();
                return true;
            case 139:
                data.enforceInterface(IActivityManager.descriptor);
                dismissKeyguardOnNextActivity();
                reply.writeNoException();
                return true;
            case 140:
                data.enforceInterface(IActivityManager.descriptor);
                killAllBackgroundProcesses();
                reply.writeNoException();
                return true;
            case 141:
                data.enforceInterface(IActivityManager.descriptor);
                String name4 = data.readString();
                int userId21 = data.readInt();
                IBinder token30 = data.readStrongBinder();
                IActivityManager.ContentProviderHolder cph2 = getContentProviderExternal(name4, userId21, token30);
                reply.writeNoException();
                if (cph2 != null) {
                    reply.writeInt(1);
                    cph2.writeToParcel(reply, 0);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 142:
                data.enforceInterface(IActivityManager.descriptor);
                String name5 = data.readString();
                IBinder token31 = data.readStrongBinder();
                removeContentProviderExternal(name5, token31);
                reply.writeNoException();
                return true;
            case 143:
                data.enforceInterface(IActivityManager.descriptor);
                ActivityManager.RunningAppProcessInfo info6 = new ActivityManager.RunningAppProcessInfo();
                getMyMemoryState(info6);
                reply.writeNoException();
                info6.writeToParcel(reply, 0);
                return true;
            case 144:
                data.enforceInterface(IActivityManager.descriptor);
                String reason4 = data.readString();
                boolean res29 = killProcessesBelowForeground(reason4);
                reply.writeNoException();
                reply.writeInt(res29 ? 1 : 0);
                return true;
            case 145:
                data.enforceInterface(IActivityManager.descriptor);
                UserInfo userInfo = getCurrentUser();
                reply.writeNoException();
                userInfo.writeToParcel(reply, 0);
                return true;
            case 146:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token32 = data.readStrongBinder();
                String destAffinity = data.readString();
                boolean res30 = targetTaskAffinityMatchesActivity(token32, destAffinity);
                reply.writeNoException();
                reply.writeInt(res30 ? 1 : 0);
                return true;
            case 147:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token33 = data.readStrongBinder();
                Intent target = Intent.CREATOR.createFromParcel(data);
                int resultCode5 = data.readInt();
                Intent resultData4 = null;
                if (data.readInt() != 0) {
                    resultData4 = Intent.CREATOR.createFromParcel(data);
                }
                boolean res31 = navigateUpTo(token33, target, resultCode5, resultData4);
                reply.writeNoException();
                reply.writeInt(res31 ? 1 : 0);
                return true;
            case 148:
                data.enforceInterface(IActivityManager.descriptor);
                setLockScreenShown(data.readInt() != 0);
                reply.writeNoException();
                return true;
            case 149:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token34 = data.readStrongBinder();
                boolean res32 = finishActivityAffinity(token34);
                reply.writeNoException();
                reply.writeInt(res32 ? 1 : 0);
                return true;
            case 150:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token35 = data.readStrongBinder();
                int res33 = getLaunchedFromUid(token35);
                reply.writeNoException();
                reply.writeInt(res33);
                return true;
            case 151:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b27 = data.readStrongBinder();
                unstableProviderDied(b27);
                reply.writeNoException();
                return true;
            case 152:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender r5 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                boolean res34 = isIntentSenderAnActivity(r5);
                reply.writeNoException();
                reply.writeInt(res34 ? 1 : 0);
                return true;
            case 153:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b28 = data.readStrongBinder();
                IApplicationThread app22 = ApplicationThreadNative.asInterface(b28);
                String callingPackage5 = data.readString();
                Intent intent11 = Intent.CREATOR.createFromParcel(data);
                String resolvedType10 = data.readString();
                IBinder resultTo7 = data.readStrongBinder();
                String resultWho7 = data.readString();
                int requestCode7 = data.readInt();
                int startFlags4 = data.readInt();
                String profileFile4 = data.readString();
                ParcelFileDescriptor profileFd3 = data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null;
                Bundle options9 = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                int userId22 = data.readInt();
                int result11 = startActivityAsUser(app22, callingPackage5, intent11, resolvedType10, resultTo7, resultWho7, requestCode7, startFlags4, profileFile4, profileFd3, options9, userId22);
                reply.writeNoException();
                reply.writeInt(result11);
                return true;
            case 154:
                data.enforceInterface(IActivityManager.descriptor);
                int userid3 = data.readInt();
                IStopUserCallback callback = IStopUserCallback.Stub.asInterface(data.readStrongBinder());
                int result12 = stopUser(userid3, callback);
                reply.writeNoException();
                reply.writeInt(result12);
                return true;
            case 155:
                data.enforceInterface(IActivityManager.descriptor);
                IUserSwitchObserver observer4 = IUserSwitchObserver.Stub.asInterface(data.readStrongBinder());
                registerUserSwitchObserver(observer4);
                reply.writeNoException();
                return true;
            case 156:
                data.enforceInterface(IActivityManager.descriptor);
                IUserSwitchObserver observer5 = IUserSwitchObserver.Stub.asInterface(data.readStrongBinder());
                unregisterUserSwitchObserver(observer5);
                reply.writeNoException();
                return true;
            case 157:
                data.enforceInterface(IActivityManager.descriptor);
                int[] result13 = getRunningUserIds();
                reply.writeNoException();
                reply.writeIntArray(result13);
                return true;
            case 158:
                data.enforceInterface(IActivityManager.descriptor);
                requestBugReport();
                reply.writeNoException();
                return true;
            case 159:
                data.enforceInterface(IActivityManager.descriptor);
                int pid4 = data.readInt();
                boolean aboveSystem = data.readInt() != 0;
                String reason5 = data.readString();
                long res35 = inputDispatchingTimedOut(pid4, aboveSystem, reason5);
                reply.writeNoException();
                reply.writeLong(res35);
                return true;
            case 161:
                data.enforceInterface(IActivityManager.descriptor);
                IIntentSender r6 = IIntentSender.Stub.asInterface(data.readStrongBinder());
                Intent intent12 = getIntentForIntentSender(r6);
                reply.writeNoException();
                if (intent12 != null) {
                    reply.writeInt(1);
                    intent12.writeToParcel(reply, 1);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 162:
                data.enforceInterface(IActivityManager.descriptor);
                int requestType = data.readInt();
                Bundle res36 = getAssistContextExtras(requestType);
                reply.writeNoException();
                reply.writeBundle(res36);
                return true;
            case 163:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token36 = data.readStrongBinder();
                Bundle extras = data.readBundle();
                reportAssistContextExtras(token36, extras);
                reply.writeNoException();
                return true;
            case 164:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token37 = data.readStrongBinder();
                String res37 = getLaunchedFromPackage(token37);
                reply.writeNoException();
                reply.writeString(res37);
                return true;
            case 165:
                data.enforceInterface(IActivityManager.descriptor);
                int uid5 = data.readInt();
                String reason6 = data.readString();
                killUid(uid5, reason6);
                reply.writeNoException();
                return true;
            case 166:
                data.enforceInterface(IActivityManager.descriptor);
                boolean monkey = data.readInt() == 1;
                setUserIsMonkey(monkey);
                reply.writeNoException();
                return true;
            case 167:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder who2 = data.readStrongBinder();
                boolean allowRestart = data.readInt() != 0;
                hang(who2, allowRestart);
                reply.writeNoException();
                return true;
            case 168:
                data.enforceInterface(IActivityManager.descriptor);
                int taskId3 = data.readInt();
                int relativeStackId = data.readInt();
                int position = data.readInt();
                float weight = data.readFloat();
                int res38 = createStack(taskId3, relativeStackId, position, weight);
                reply.writeNoException();
                reply.writeInt(res38);
                return true;
            case 169:
                data.enforceInterface(IActivityManager.descriptor);
                int taskId4 = data.readInt();
                int stackId = data.readInt();
                boolean toTop = data.readInt() != 0;
                moveTaskToStack(taskId4, stackId, toTop);
                reply.writeNoException();
                return true;
            case 170:
                data.enforceInterface(IActivityManager.descriptor);
                int stackBoxId = data.readInt();
                float weight2 = data.readFloat();
                resizeStackBox(stackBoxId, weight2);
                reply.writeNoException();
                return true;
            case 171:
                data.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.StackBoxInfo> list7 = getStackBoxes();
                reply.writeNoException();
                reply.writeTypedList(list7);
                return true;
            case 172:
                data.enforceInterface(IActivityManager.descriptor);
                int stackId2 = data.readInt();
                setFocusedStack(stackId2);
                reply.writeNoException();
                return true;
            case 173:
                data.enforceInterface(IActivityManager.descriptor);
                int stackBoxId2 = data.readInt();
                ActivityManager.StackBoxInfo info7 = getStackBoxInfo(stackBoxId2);
                reply.writeNoException();
                if (info7 != null) {
                    reply.writeInt(1);
                    info7.writeToParcel(reply, 0);
                    return true;
                }
                reply.writeInt(0);
                return true;
            case 174:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token38 = data.readStrongBinder();
                boolean converted = convertFromTranslucent(token38);
                reply.writeNoException();
                reply.writeInt(converted ? 1 : 0);
                return true;
            case 175:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token39 = data.readStrongBinder();
                boolean converted2 = convertToTranslucent(token39);
                reply.writeNoException();
                reply.writeInt(converted2 ? 1 : 0);
                return true;
            case 176:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token40 = data.readStrongBinder();
                notifyActivityDrawn(token40);
                reply.writeNoException();
                return true;
            case 177:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder token41 = data.readStrongBinder();
                reportActivityFullyDrawn(token41);
                reply.writeNoException();
                return true;
            case 178:
                data.enforceInterface(IActivityManager.descriptor);
                restart();
                reply.writeNoException();
                return true;
            case 179:
                data.enforceInterface(IActivityManager.descriptor);
                performIdleMaintenance();
                reply.writeNoException();
                return true;
            case 180:
                data.enforceInterface(IActivityManager.descriptor);
                Uri uri8 = Uri.CREATOR.createFromParcel(data);
                int mode10 = data.readInt();
                takePersistableUriPermission(uri8, mode10);
                reply.writeNoException();
                return true;
            case 181:
                data.enforceInterface(IActivityManager.descriptor);
                Uri uri9 = Uri.CREATOR.createFromParcel(data);
                int mode11 = data.readInt();
                releasePersistableUriPermission(uri9, mode11);
                reply.writeNoException();
                return true;
            case 182:
                data.enforceInterface(IActivityManager.descriptor);
                String packageName9 = data.readString();
                boolean incoming = data.readInt() != 0;
                ParceledListSlice<UriPermission> perms = getPersistedUriPermissions(packageName9, incoming);
                reply.writeNoException();
                perms.writeToParcel(reply, 1);
                return true;
            case 183:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder b29 = data.readStrongBinder();
                appNotRespondingViaProvider(b29);
                reply.writeNoException();
                return true;
        }
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }
}