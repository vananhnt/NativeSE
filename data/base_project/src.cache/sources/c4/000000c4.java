package android.app;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.IActivityManager;
import android.content.ComponentName;
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
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.StrictMode;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ActivityManagerNative.java */
/* loaded from: ActivityManagerProxy.class */
public class ActivityManagerProxy implements IActivityManager {
    private IBinder mRemote;

    public ActivityManagerProxy(IBinder remote) {
        this.mRemote = remote;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this.mRemote;
    }

    @Override // android.app.IActivityManager
    public int startActivity(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, String profileFile, ParcelFileDescriptor profileFd, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(callingPackage);
        intent.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        data.writeInt(startFlags);
        data.writeString(profileFile);
        if (profileFd != null) {
            data.writeInt(1);
            profileFd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(3, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, String profileFile, ParcelFileDescriptor profileFd, Bundle options, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(callingPackage);
        intent.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        data.writeInt(startFlags);
        data.writeString(profileFile);
        if (profileFd != null) {
            data.writeInt(1);
            profileFd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(userId);
        this.mRemote.transact(153, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public IActivityManager.WaitResult startActivityAndWait(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, String profileFile, ParcelFileDescriptor profileFd, Bundle options, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(callingPackage);
        intent.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        data.writeInt(startFlags);
        data.writeString(profileFile);
        if (profileFd != null) {
            data.writeInt(1);
            profileFd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(userId);
        this.mRemote.transact(105, data, reply, 0);
        reply.readException();
        IActivityManager.WaitResult result = IActivityManager.WaitResult.CREATOR.createFromParcel(reply);
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int startActivityWithConfig(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, Configuration config, Bundle options, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(callingPackage);
        intent.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        data.writeInt(startFlags);
        config.writeToParcel(data, 0);
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(userId);
        this.mRemote.transact(3, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int startActivityIntentSender(IApplicationThread caller, IntentSender intent, Intent fillInIntent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flagsMask, int flagsValues, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        intent.writeToParcel(data, 0);
        if (fillInIntent != null) {
            data.writeInt(1);
            fillInIntent.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        data.writeInt(flagsMask);
        data.writeInt(flagsValues);
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(100, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public boolean startNextMatchingActivity(IBinder callingActivity, Intent intent, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(callingActivity);
        intent.writeToParcel(data, 0);
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(67, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result != 0;
    }

    @Override // android.app.IActivityManager
    public boolean finishActivity(IBinder token, int resultCode, Intent resultData) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(resultCode);
        if (resultData != null) {
            data.writeInt(1);
            resultData.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(11, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void finishSubActivity(IBinder token, String resultWho, int requestCode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        this.mRemote.transact(32, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean finishActivityAffinity(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(149, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean willActivityBeVisible(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(106, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public Intent registerReceiver(IApplicationThread caller, String packageName, IIntentReceiver receiver, IntentFilter filter, String perm, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(packageName);
        data.writeStrongBinder(receiver != null ? receiver.asBinder() : null);
        filter.writeToParcel(data, 0);
        data.writeString(perm);
        data.writeInt(userId);
        this.mRemote.transact(12, data, reply, 0);
        reply.readException();
        Intent intent = null;
        int haveIntent = reply.readInt();
        if (haveIntent != 0) {
            intent = Intent.CREATOR.createFromParcel(reply);
        }
        reply.recycle();
        data.recycle();
        return intent;
    }

    @Override // android.app.IActivityManager
    public void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(receiver.asBinder());
        this.mRemote.transact(13, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int broadcastIntent(IApplicationThread caller, Intent intent, String resolvedType, IIntentReceiver resultTo, int resultCode, String resultData, Bundle map, String requiredPermission, int appOp, boolean serialized, boolean sticky, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        intent.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo != null ? resultTo.asBinder() : null);
        data.writeInt(resultCode);
        data.writeString(resultData);
        data.writeBundle(map);
        data.writeString(requiredPermission);
        data.writeInt(appOp);
        data.writeInt(serialized ? 1 : 0);
        data.writeInt(sticky ? 1 : 0);
        data.writeInt(userId);
        this.mRemote.transact(14, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void unbroadcastIntent(IApplicationThread caller, Intent intent, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        intent.writeToParcel(data, 0);
        data.writeInt(userId);
        this.mRemote.transact(15, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void finishReceiver(IBinder who, int resultCode, String resultData, Bundle map, boolean abortBroadcast) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(who);
        data.writeInt(resultCode);
        data.writeString(resultData);
        data.writeBundle(map);
        data.writeInt(abortBroadcast ? 1 : 0);
        this.mRemote.transact(16, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void attachApplication(IApplicationThread app) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(app.asBinder());
        this.mRemote.transact(17, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityIdle(IBinder token, Configuration config, boolean stopProfiling) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        if (config != null) {
            data.writeInt(1);
            config.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(stopProfiling ? 1 : 0);
        this.mRemote.transact(18, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityResumed(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(39, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityPaused(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(19, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityStopped(IBinder token, Bundle state, Bitmap thumbnail, CharSequence description) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeBundle(state);
        if (thumbnail != null) {
            data.writeInt(1);
            thumbnail.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        TextUtils.writeToParcel(description, data, 0);
        this.mRemote.transact(20, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void activitySlept(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(123, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityDestroyed(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(62, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public String getCallingPackage(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(21, data, reply, 0);
        reply.readException();
        String res = reply.readString();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public ComponentName getCallingActivity(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(22, data, reply, 0);
        reply.readException();
        ComponentName res = ComponentName.readFromParcel(reply);
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public List getTasks(int maxNum, int flags, IThumbnailReceiver receiver) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(maxNum);
        data.writeInt(flags);
        data.writeStrongBinder(receiver != null ? receiver.asBinder() : null);
        this.mRemote.transact(23, data, reply, 0);
        reply.readException();
        ArrayList list = null;
        int N = reply.readInt();
        if (N >= 0) {
            list = new ArrayList();
            while (N > 0) {
                ActivityManager.RunningTaskInfo info = ActivityManager.RunningTaskInfo.CREATOR.createFromParcel(reply);
                list.add(info);
                N--;
            }
        }
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum, int flags, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(maxNum);
        data.writeInt(flags);
        data.writeInt(userId);
        this.mRemote.transact(60, data, reply, 0);
        reply.readException();
        ArrayList<ActivityManager.RecentTaskInfo> list = reply.createTypedArrayList(ActivityManager.RecentTaskInfo.CREATOR);
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public ActivityManager.TaskThumbnails getTaskThumbnails(int id) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(id);
        this.mRemote.transact(82, data, reply, 0);
        reply.readException();
        ActivityManager.TaskThumbnails bm = null;
        if (reply.readInt() != 0) {
            bm = ActivityManager.TaskThumbnails.CREATOR.createFromParcel(reply);
        }
        data.recycle();
        reply.recycle();
        return bm;
    }

    @Override // android.app.IActivityManager
    public Bitmap getTaskTopThumbnail(int id) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(id);
        this.mRemote.transact(95, data, reply, 0);
        reply.readException();
        Bitmap bm = null;
        if (reply.readInt() != 0) {
            bm = Bitmap.CREATOR.createFromParcel(reply);
        }
        data.recycle();
        reply.recycle();
        return bm;
    }

    @Override // android.app.IActivityManager
    public List getServices(int maxNum, int flags) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(maxNum);
        data.writeInt(flags);
        this.mRemote.transact(81, data, reply, 0);
        reply.readException();
        ArrayList list = null;
        int N = reply.readInt();
        if (N >= 0) {
            list = new ArrayList();
            while (N > 0) {
                ActivityManager.RunningServiceInfo info = ActivityManager.RunningServiceInfo.CREATOR.createFromParcel(reply);
                list.add(info);
                N--;
            }
        }
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(77, data, reply, 0);
        reply.readException();
        ArrayList<ActivityManager.ProcessErrorStateInfo> list = reply.createTypedArrayList(ActivityManager.ProcessErrorStateInfo.CREATOR);
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(83, data, reply, 0);
        reply.readException();
        ArrayList<ActivityManager.RunningAppProcessInfo> list = reply.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public List<ApplicationInfo> getRunningExternalApplications() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(108, data, reply, 0);
        reply.readException();
        ArrayList<ApplicationInfo> list = reply.createTypedArrayList(ApplicationInfo.CREATOR);
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public void moveTaskToFront(int task, int flags, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(task);
        data.writeInt(flags);
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(24, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void moveTaskToBack(int task) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(task);
        this.mRemote.transact(25, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean moveActivityTaskToBack(IBinder token, boolean nonRoot) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(nonRoot ? 1 : 0);
        this.mRemote.transact(75, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void moveTaskBackwards(int task) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(task);
        this.mRemote.transact(26, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int createStack(int taskId, int relativeStackBoxId, int position, float weight) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(taskId);
        data.writeInt(relativeStackBoxId);
        data.writeInt(position);
        data.writeFloat(weight);
        this.mRemote.transact(168, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void moveTaskToStack(int taskId, int stackId, boolean toTop) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(taskId);
        data.writeInt(stackId);
        data.writeInt(toTop ? 1 : 0);
        this.mRemote.transact(169, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void resizeStackBox(int stackBoxId, float weight) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(stackBoxId);
        data.writeFloat(weight);
        this.mRemote.transact(170, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.StackBoxInfo> getStackBoxes() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(171, data, reply, 0);
        reply.readException();
        ArrayList<ActivityManager.StackBoxInfo> list = reply.createTypedArrayList(ActivityManager.StackBoxInfo.CREATOR);
        data.recycle();
        reply.recycle();
        return list;
    }

    @Override // android.app.IActivityManager
    public ActivityManager.StackBoxInfo getStackBoxInfo(int stackBoxId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(stackBoxId);
        this.mRemote.transact(173, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        ActivityManager.StackBoxInfo info = null;
        if (res != 0) {
            info = ActivityManager.StackBoxInfo.CREATOR.createFromParcel(reply);
        }
        data.recycle();
        reply.recycle();
        return info;
    }

    @Override // android.app.IActivityManager
    public void setFocusedStack(int stackId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(stackId);
        this.mRemote.transact(172, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int getTaskForActivity(IBinder token, boolean onlyRoot) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(onlyRoot ? 1 : 0);
        this.mRemote.transact(27, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void reportThumbnail(IBinder token, Bitmap thumbnail, CharSequence description) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        if (thumbnail != null) {
            data.writeInt(1);
            thumbnail.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        TextUtils.writeToParcel(description, data, 0);
        this.mRemote.transact(28, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public IActivityManager.ContentProviderHolder getContentProvider(IApplicationThread caller, String name, int userId, boolean stable) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(name);
        data.writeInt(userId);
        data.writeInt(stable ? 1 : 0);
        this.mRemote.transact(29, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        IActivityManager.ContentProviderHolder cph = null;
        if (res != 0) {
            cph = IActivityManager.ContentProviderHolder.CREATOR.createFromParcel(reply);
        }
        data.recycle();
        reply.recycle();
        return cph;
    }

    @Override // android.app.IActivityManager
    public IActivityManager.ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(name);
        data.writeInt(userId);
        data.writeStrongBinder(token);
        this.mRemote.transact(141, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        IActivityManager.ContentProviderHolder cph = null;
        if (res != 0) {
            cph = IActivityManager.ContentProviderHolder.CREATOR.createFromParcel(reply);
        }
        data.recycle();
        reply.recycle();
        return cph;
    }

    @Override // android.app.IActivityManager
    public void publishContentProviders(IApplicationThread caller, List<IActivityManager.ContentProviderHolder> providers) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeTypedList(providers);
        this.mRemote.transact(30, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean refContentProvider(IBinder connection, int stable, int unstable) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(connection);
        data.writeInt(stable);
        data.writeInt(unstable);
        this.mRemote.transact(31, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void unstableProviderDied(IBinder connection) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(connection);
        this.mRemote.transact(151, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void appNotRespondingViaProvider(IBinder connection) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(connection);
        this.mRemote.transact(183, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void removeContentProvider(IBinder connection, boolean stable) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(connection);
        data.writeInt(stable ? 1 : 0);
        this.mRemote.transact(69, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void removeContentProviderExternal(String name, IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(name);
        data.writeStrongBinder(token);
        this.mRemote.transact(142, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public PendingIntent getRunningServiceControlPanel(ComponentName service) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        service.writeToParcel(data, 0);
        this.mRemote.transact(33, data, reply, 0);
        reply.readException();
        PendingIntent res = PendingIntent.readPendingIntentOrNullFromParcel(reply);
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public ComponentName startService(IApplicationThread caller, Intent service, String resolvedType, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        service.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeInt(userId);
        this.mRemote.transact(34, data, reply, 0);
        reply.readException();
        ComponentName res = ComponentName.readFromParcel(reply);
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public int stopService(IApplicationThread caller, Intent service, String resolvedType, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        service.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeInt(userId);
        this.mRemote.transact(35, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean stopServiceToken(ComponentName className, IBinder token, int startId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        ComponentName.writeToParcel(className, data);
        data.writeStrongBinder(token);
        data.writeInt(startId);
        this.mRemote.transact(48, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void setServiceForeground(ComponentName className, IBinder token, int id, Notification notification, boolean removeNotification) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        ComponentName.writeToParcel(className, data);
        data.writeStrongBinder(token);
        data.writeInt(id);
        if (notification != null) {
            data.writeInt(1);
            notification.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(removeNotification ? 1 : 0);
        this.mRemote.transact(74, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int bindService(IApplicationThread caller, IBinder token, Intent service, String resolvedType, IServiceConnection connection, int flags, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeStrongBinder(token);
        service.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(connection.asBinder());
        data.writeInt(flags);
        data.writeInt(userId);
        this.mRemote.transact(36, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean unbindService(IServiceConnection connection) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(connection.asBinder());
        this.mRemote.transact(37, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void publishService(IBinder token, Intent intent, IBinder service) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        intent.writeToParcel(data, 0);
        data.writeStrongBinder(service);
        this.mRemote.transact(38, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void unbindFinished(IBinder token, Intent intent, boolean doRebind) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        intent.writeToParcel(data, 0);
        data.writeInt(doRebind ? 1 : 0);
        this.mRemote.transact(72, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void serviceDoneExecuting(IBinder token, int type, int startId, int res) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(type);
        data.writeInt(startId);
        data.writeInt(res);
        this.mRemote.transact(61, data, reply, 1);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public IBinder peekService(Intent service, String resolvedType) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        service.writeToParcel(data, 0);
        data.writeString(resolvedType);
        this.mRemote.transact(85, data, reply, 0);
        reply.readException();
        IBinder binder = reply.readStrongBinder();
        reply.recycle();
        data.recycle();
        return binder;
    }

    @Override // android.app.IActivityManager
    public boolean bindBackupAgent(ApplicationInfo app, int backupRestoreMode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        app.writeToParcel(data, 0);
        data.writeInt(backupRestoreMode);
        this.mRemote.transact(90, data, reply, 0);
        reply.readException();
        boolean success = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return success;
    }

    @Override // android.app.IActivityManager
    public void clearPendingBackup() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(160, data, reply, 0);
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public void backupAgentCreated(String packageName, IBinder agent) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeStrongBinder(agent);
        this.mRemote.transact(91, data, reply, 0);
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public void unbindBackupAgent(ApplicationInfo app) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        app.writeToParcel(data, 0);
        this.mRemote.transact(92, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean startInstrumentation(ComponentName className, String profileFile, int flags, Bundle arguments, IInstrumentationWatcher watcher, IUiAutomationConnection connection, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        ComponentName.writeToParcel(className, data);
        data.writeString(profileFile);
        data.writeInt(flags);
        data.writeBundle(arguments);
        data.writeStrongBinder(watcher != null ? watcher.asBinder() : null);
        data.writeStrongBinder(connection != null ? connection.asBinder() : null);
        data.writeInt(userId);
        this.mRemote.transact(44, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void finishInstrumentation(IApplicationThread target, int resultCode, Bundle results) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(target != null ? target.asBinder() : null);
        data.writeInt(resultCode);
        data.writeBundle(results);
        this.mRemote.transact(45, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public Configuration getConfiguration() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(46, data, reply, 0);
        reply.readException();
        Configuration res = Configuration.CREATOR.createFromParcel(reply);
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void updateConfiguration(Configuration values) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        values.writeToParcel(data, 0);
        this.mRemote.transact(47, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void setRequestedOrientation(IBinder token, int requestedOrientation) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(requestedOrientation);
        this.mRemote.transact(70, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int getRequestedOrientation(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(71, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public ComponentName getActivityClassForToken(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(49, data, reply, 0);
        reply.readException();
        ComponentName res = ComponentName.readFromParcel(reply);
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public String getPackageForToken(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(50, data, reply, 0);
        reply.readException();
        String res = reply.readString();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public IIntentSender getIntentSender(int type, String packageName, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle options, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(type);
        data.writeString(packageName);
        data.writeStrongBinder(token);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        if (intents != null) {
            data.writeInt(1);
            data.writeTypedArray(intents, 0);
            data.writeStringArray(resolvedTypes);
        } else {
            data.writeInt(0);
        }
        data.writeInt(flags);
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(userId);
        this.mRemote.transact(63, data, reply, 0);
        reply.readException();
        IIntentSender res = IIntentSender.Stub.asInterface(reply.readStrongBinder());
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void cancelIntentSender(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(sender.asBinder());
        this.mRemote.transact(64, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public String getPackageForIntentSender(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(sender.asBinder());
        this.mRemote.transact(65, data, reply, 0);
        reply.readException();
        String res = reply.readString();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public int getUidForIntentSender(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(sender.asBinder());
        this.mRemote.transact(93, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll, boolean requireFull, String name, String callerPackage) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(callingPid);
        data.writeInt(callingUid);
        data.writeInt(userId);
        data.writeInt(allowAll ? 1 : 0);
        data.writeInt(requireFull ? 1 : 0);
        data.writeString(name);
        data.writeString(callerPackage);
        this.mRemote.transact(94, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void setProcessLimit(int max) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(max);
        this.mRemote.transact(51, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int getProcessLimit() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(52, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void setProcessForeground(IBinder token, int pid, boolean isForeground) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(pid);
        data.writeInt(isForeground ? 1 : 0);
        this.mRemote.transact(73, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int checkPermission(String permission, int pid, int uid) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(permission);
        data.writeInt(pid);
        data.writeInt(uid);
        this.mRemote.transact(53, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean clearApplicationUserData(String packageName, IPackageDataObserver observer, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeStrongBinder(observer != null ? observer.asBinder() : null);
        data.writeInt(userId);
        this.mRemote.transact(78, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public int checkUriPermission(Uri uri, int pid, int uid, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(data, 0);
        data.writeInt(pid);
        data.writeInt(uid);
        data.writeInt(mode);
        this.mRemote.transact(54, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void grantUriPermission(IApplicationThread caller, String targetPkg, Uri uri, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller.asBinder());
        data.writeString(targetPkg);
        uri.writeToParcel(data, 0);
        data.writeInt(mode);
        this.mRemote.transact(55, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void revokeUriPermission(IApplicationThread caller, Uri uri, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller.asBinder());
        uri.writeToParcel(data, 0);
        data.writeInt(mode);
        this.mRemote.transact(56, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void takePersistableUriPermission(Uri uri, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(data, 0);
        data.writeInt(mode);
        this.mRemote.transact(180, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void releasePersistableUriPermission(Uri uri, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(data, 0);
        data.writeInt(mode);
        this.mRemote.transact(181, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public ParceledListSlice<UriPermission> getPersistedUriPermissions(String packageName, boolean incoming) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeInt(incoming ? 1 : 0);
        this.mRemote.transact(182, data, reply, 0);
        reply.readException();
        ParceledListSlice<UriPermission> perms = ParceledListSlice.CREATOR.createFromParcel(reply);
        data.recycle();
        reply.recycle();
        return perms;
    }

    @Override // android.app.IActivityManager
    public void showWaitingForDebugger(IApplicationThread who, boolean waiting) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(who.asBinder());
        data.writeInt(waiting ? 1 : 0);
        this.mRemote.transact(58, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void getMemoryInfo(ActivityManager.MemoryInfo outInfo) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(76, data, reply, 0);
        reply.readException();
        outInfo.readFromParcel(reply);
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void unhandledBack() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(4, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public ParcelFileDescriptor openContentUri(Uri uri) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(5, data, reply, 0);
        reply.readException();
        ParcelFileDescriptor pfd = null;
        if (reply.readInt() != 0) {
            pfd = ParcelFileDescriptor.CREATOR.createFromParcel(reply);
        }
        data.recycle();
        reply.recycle();
        return pfd;
    }

    @Override // android.app.IActivityManager
    public void goingToSleep() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(40, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void wakingUp() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(41, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void setLockScreenShown(boolean shown) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(shown ? 1 : 0);
        this.mRemote.transact(148, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void setDebugApp(String packageName, boolean waitForDebugger, boolean persistent) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeInt(waitForDebugger ? 1 : 0);
        data.writeInt(persistent ? 1 : 0);
        this.mRemote.transact(42, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void setAlwaysFinish(boolean enabled) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(enabled ? 1 : 0);
        this.mRemote.transact(43, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void setActivityController(IActivityController watcher) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(watcher != null ? watcher.asBinder() : null);
        this.mRemote.transact(57, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void enterSafeMode() throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(66, data, null, 0);
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public void noteWakeupAlarm(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        data.writeStrongBinder(sender.asBinder());
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(68, data, null, 0);
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean killPids(int[] pids, String reason, boolean secure) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeIntArray(pids);
        data.writeString(reason);
        data.writeInt(secure ? 1 : 0);
        this.mRemote.transact(80, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean killProcessesBelowForeground(String reason) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(reason);
        this.mRemote.transact(144, data, reply, 0);
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void startRunning(String pkg, String cls, String action, String indata) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(pkg);
        data.writeString(cls);
        data.writeString(action);
        data.writeString(indata);
        this.mRemote.transact(1, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean testIsSystemReady() {
        return true;
    }

    @Override // android.app.IActivityManager
    public void handleApplicationCrash(IBinder app, ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(app);
        crashInfo.writeToParcel(data, 0);
        this.mRemote.transact(2, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean handleApplicationWtf(IBinder app, String tag, ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(app);
        data.writeString(tag);
        crashInfo.writeToParcel(data, 0);
        this.mRemote.transact(102, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void handleApplicationStrictModeViolation(IBinder app, int violationMask, StrictMode.ViolationInfo info) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(app);
        data.writeInt(violationMask);
        info.writeToParcel(data, 0);
        this.mRemote.transact(110, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public void signalPersistentProcesses(int sig) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(sig);
        this.mRemote.transact(59, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void killBackgroundProcesses(String packageName, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeInt(userId);
        this.mRemote.transact(103, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void killAllBackgroundProcesses() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(140, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void forceStopPackage(String packageName, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeInt(userId);
        this.mRemote.transact(79, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void getMyMemoryState(ActivityManager.RunningAppProcessInfo outInfo) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(143, data, reply, 0);
        reply.readException();
        outInfo.readFromParcel(reply);
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(84, data, reply, 0);
        reply.readException();
        ConfigurationInfo res = ConfigurationInfo.CREATOR.createFromParcel(reply);
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean profileControl(String process, int userId, boolean start, String path, ParcelFileDescriptor fd, int profileType) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(process);
        data.writeInt(userId);
        data.writeInt(start ? 1 : 0);
        data.writeInt(profileType);
        data.writeString(path);
        if (fd != null) {
            data.writeInt(1);
            fd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(86, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean shutdown(int timeout) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(timeout);
        this.mRemote.transact(87, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void stopAppSwitches() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(88, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public void resumeAppSwitches() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(89, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public void killApplicationWithAppId(String pkg, int appid, String reason) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(pkg);
        data.writeInt(appid);
        data.writeString(reason);
        this.mRemote.transact(96, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void closeSystemDialogs(String reason) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(reason);
        this.mRemote.transact(97, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeIntArray(pids);
        this.mRemote.transact(98, data, reply, 0);
        reply.readException();
        Debug.MemoryInfo[] res = (Debug.MemoryInfo[]) reply.createTypedArray(Debug.MemoryInfo.CREATOR);
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void killApplicationProcess(String processName, int uid) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(processName);
        data.writeInt(uid);
        this.mRemote.transact(99, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void overridePendingTransition(IBinder token, String packageName, int enterAnim, int exitAnim) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeString(packageName);
        data.writeInt(enterAnim);
        data.writeInt(exitAnim);
        this.mRemote.transact(101, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean isUserAMonkey() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(104, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void setUserIsMonkey(boolean monkey) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(monkey ? 1 : 0);
        this.mRemote.transact(166, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void finishHeavyWeightApp() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(109, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean convertFromTranslucent(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(174, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean convertToTranslucent(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(175, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void setImmersive(IBinder token, boolean immersive) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeInt(immersive ? 1 : 0);
        this.mRemote.transact(112, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean isImmersive(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(111, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() == 1;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean isTopActivityImmersive() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(113, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() == 1;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void crashApplication(int uid, int initialPid, String packageName, String message) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(uid);
        data.writeInt(initialPid);
        data.writeString(packageName);
        data.writeString(message);
        this.mRemote.transact(114, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public String getProviderMimeType(Uri uri, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(data, 0);
        data.writeInt(userId);
        this.mRemote.transact(115, data, reply, 0);
        reply.readException();
        String res = reply.readString();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public IBinder newUriPermissionOwner(String name) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(name);
        this.mRemote.transact(116, data, reply, 0);
        reply.readException();
        IBinder res = reply.readStrongBinder();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void grantUriPermissionFromOwner(IBinder owner, int fromUid, String targetPkg, Uri uri, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(owner);
        data.writeInt(fromUid);
        data.writeString(targetPkg);
        uri.writeToParcel(data, 0);
        data.writeInt(mode);
        this.mRemote.transact(55, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void revokeUriPermissionFromOwner(IBinder owner, Uri uri, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(owner);
        if (uri != null) {
            data.writeInt(1);
            uri.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(mode);
        this.mRemote.transact(56, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public int checkGrantUriPermission(int callingUid, String targetPkg, Uri uri, int modeFlags) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(callingUid);
        data.writeString(targetPkg);
        uri.writeToParcel(data, 0);
        data.writeInt(modeFlags);
        this.mRemote.transact(119, data, reply, 0);
        reply.readException();
        int res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean dumpHeap(String process, int userId, boolean managed, String path, ParcelFileDescriptor fd) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(process);
        data.writeInt(userId);
        data.writeInt(managed ? 1 : 0);
        data.writeString(path);
        if (fd != null) {
            data.writeInt(1);
            fd.writeToParcel(data, 1);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(120, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public int startActivities(IApplicationThread caller, String callingPackage, Intent[] intents, String[] resolvedTypes, IBinder resultTo, Bundle options, int userId) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(callingPackage);
        data.writeTypedArray(intents, 0);
        data.writeStringArray(resolvedTypes);
        data.writeStrongBinder(resultTo);
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        data.writeInt(userId);
        this.mRemote.transact(121, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int getFrontActivityScreenCompatMode() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(124, data, reply, 0);
        reply.readException();
        int mode = reply.readInt();
        reply.recycle();
        data.recycle();
        return mode;
    }

    @Override // android.app.IActivityManager
    public void setFrontActivityScreenCompatMode(int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(mode);
        this.mRemote.transact(125, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public int getPackageScreenCompatMode(String packageName) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        this.mRemote.transact(126, data, reply, 0);
        reply.readException();
        int mode = reply.readInt();
        reply.recycle();
        data.recycle();
        return mode;
    }

    @Override // android.app.IActivityManager
    public void setPackageScreenCompatMode(String packageName, int mode) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeInt(mode);
        this.mRemote.transact(127, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean getPackageAskScreenCompat(String packageName) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        this.mRemote.transact(128, data, reply, 0);
        reply.readException();
        boolean ask = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return ask;
    }

    @Override // android.app.IActivityManager
    public void setPackageAskScreenCompat(String packageName, boolean ask) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeString(packageName);
        data.writeInt(ask ? 1 : 0);
        this.mRemote.transact(129, data, reply, 0);
        reply.readException();
        reply.recycle();
        data.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean switchUser(int userid) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(userid);
        this.mRemote.transact(130, data, reply, 0);
        reply.readException();
        boolean result = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int stopUser(int userid, IStopUserCallback callback) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(userid);
        data.writeStrongInterface(callback);
        this.mRemote.transact(154, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public UserInfo getCurrentUser() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(145, data, reply, 0);
        reply.readException();
        UserInfo userInfo = UserInfo.CREATOR.createFromParcel(reply);
        reply.recycle();
        data.recycle();
        return userInfo;
    }

    @Override // android.app.IActivityManager
    public boolean isUserRunning(int userid, boolean orStopping) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(userid);
        data.writeInt(orStopping ? 1 : 0);
        this.mRemote.transact(122, data, reply, 0);
        reply.readException();
        boolean result = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int[] getRunningUserIds() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(157, data, reply, 0);
        reply.readException();
        int[] result = reply.createIntArray();
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public boolean removeSubTask(int taskId, int subTaskIndex) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(taskId);
        data.writeInt(subTaskIndex);
        this.mRemote.transact(131, data, reply, 0);
        reply.readException();
        boolean result = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public boolean removeTask(int taskId, int flags) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(taskId);
        data.writeInt(flags);
        this.mRemote.transact(132, data, reply, 0);
        reply.readException();
        boolean result = reply.readInt() != 0;
        reply.recycle();
        data.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public void registerProcessObserver(IProcessObserver observer) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(observer != null ? observer.asBinder() : null);
        this.mRemote.transact(133, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void unregisterProcessObserver(IProcessObserver observer) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(observer != null ? observer.asBinder() : null);
        this.mRemote.transact(134, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean isIntentSenderTargetedToPackage(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(sender.asBinder());
        this.mRemote.transact(135, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public boolean isIntentSenderAnActivity(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(sender.asBinder());
        this.mRemote.transact(152, data, reply, 0);
        reply.readException();
        boolean res = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public Intent getIntentForIntentSender(IIntentSender sender) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(sender.asBinder());
        this.mRemote.transact(161, data, reply, 0);
        reply.readException();
        Intent res = reply.readInt() != 0 ? Intent.CREATOR.createFromParcel(reply) : null;
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void updatePersistentConfiguration(Configuration values) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        values.writeToParcel(data, 0);
        this.mRemote.transact(136, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public long[] getProcessPss(int[] pids) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeIntArray(pids);
        this.mRemote.transact(137, data, reply, 0);
        reply.readException();
        long[] res = reply.createLongArray();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void showBootMessage(CharSequence msg, boolean always) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        TextUtils.writeToParcel(msg, data, 0);
        data.writeInt(always ? 1 : 0);
        this.mRemote.transact(138, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void dismissKeyguardOnNextActivity() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(139, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean targetTaskAffinityMatchesActivity(IBinder token, String destAffinity) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeString(destAffinity);
        this.mRemote.transact(146, data, reply, 0);
        reply.readException();
        boolean result = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public boolean navigateUpTo(IBinder token, Intent target, int resultCode, Intent resultData) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        target.writeToParcel(data, 0);
        data.writeInt(resultCode);
        if (resultData != null) {
            data.writeInt(1);
            resultData.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        this.mRemote.transact(147, data, reply, 0);
        reply.readException();
        boolean result = reply.readInt() != 0;
        data.recycle();
        reply.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public int getLaunchedFromUid(IBinder activityToken) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(activityToken);
        this.mRemote.transact(150, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        data.recycle();
        reply.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public String getLaunchedFromPackage(IBinder activityToken) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(activityToken);
        this.mRemote.transact(164, data, reply, 0);
        reply.readException();
        String result = reply.readString();
        data.recycle();
        reply.recycle();
        return result;
    }

    @Override // android.app.IActivityManager
    public void registerUserSwitchObserver(IUserSwitchObserver observer) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(observer != null ? observer.asBinder() : null);
        this.mRemote.transact(155, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void unregisterUserSwitchObserver(IUserSwitchObserver observer) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(observer != null ? observer.asBinder() : null);
        this.mRemote.transact(156, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void requestBugReport() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(158, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public long inputDispatchingTimedOut(int pid, boolean aboveSystem, String reason) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(pid);
        data.writeInt(aboveSystem ? 1 : 0);
        data.writeString(reason);
        this.mRemote.transact(159, data, reply, 0);
        reply.readException();
        long res = reply.readInt();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public Bundle getAssistContextExtras(int requestType) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(requestType);
        this.mRemote.transact(162, data, reply, 0);
        reply.readException();
        Bundle res = reply.readBundle();
        data.recycle();
        reply.recycle();
        return res;
    }

    @Override // android.app.IActivityManager
    public void reportAssistContextExtras(IBinder token, Bundle extras) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        data.writeBundle(extras);
        this.mRemote.transact(163, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void killUid(int uid, String reason) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeInt(uid);
        data.writeString(reason);
        this.mRemote.transact(165, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void hang(IBinder who, boolean allowRestart) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(who);
        data.writeInt(allowRestart ? 1 : 0);
        this.mRemote.transact(167, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void reportActivityFullyDrawn(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(177, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void notifyActivityDrawn(IBinder token) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        this.mRemote.transact(176, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void restart() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(178, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

    @Override // android.app.IActivityManager
    public void performIdleMaintenance() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(179, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }
}