package android.app;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.ComponentName;
import android.content.ContentProviderNative;
import android.content.IContentProvider;
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
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.StrictMode;
import java.util.List;

/* loaded from: IActivityManager.class */
public interface IActivityManager extends IInterface {
    public static final String descriptor = "android.app.IActivityManager";
    public static final int START_RUNNING_TRANSACTION = 1;
    public static final int HANDLE_APPLICATION_CRASH_TRANSACTION = 2;
    public static final int START_ACTIVITY_TRANSACTION = 3;
    public static final int UNHANDLED_BACK_TRANSACTION = 4;
    public static final int OPEN_CONTENT_URI_TRANSACTION = 5;
    public static final int FINISH_ACTIVITY_TRANSACTION = 11;
    public static final int REGISTER_RECEIVER_TRANSACTION = 12;
    public static final int UNREGISTER_RECEIVER_TRANSACTION = 13;
    public static final int BROADCAST_INTENT_TRANSACTION = 14;
    public static final int UNBROADCAST_INTENT_TRANSACTION = 15;
    public static final int FINISH_RECEIVER_TRANSACTION = 16;
    public static final int ATTACH_APPLICATION_TRANSACTION = 17;
    public static final int ACTIVITY_IDLE_TRANSACTION = 18;
    public static final int ACTIVITY_PAUSED_TRANSACTION = 19;
    public static final int ACTIVITY_STOPPED_TRANSACTION = 20;
    public static final int GET_CALLING_PACKAGE_TRANSACTION = 21;
    public static final int GET_CALLING_ACTIVITY_TRANSACTION = 22;
    public static final int GET_TASKS_TRANSACTION = 23;
    public static final int MOVE_TASK_TO_FRONT_TRANSACTION = 24;
    public static final int MOVE_TASK_TO_BACK_TRANSACTION = 25;
    public static final int MOVE_TASK_BACKWARDS_TRANSACTION = 26;
    public static final int GET_TASK_FOR_ACTIVITY_TRANSACTION = 27;
    public static final int REPORT_THUMBNAIL_TRANSACTION = 28;
    public static final int GET_CONTENT_PROVIDER_TRANSACTION = 29;
    public static final int PUBLISH_CONTENT_PROVIDERS_TRANSACTION = 30;
    public static final int REF_CONTENT_PROVIDER_TRANSACTION = 31;
    public static final int FINISH_SUB_ACTIVITY_TRANSACTION = 32;
    public static final int GET_RUNNING_SERVICE_CONTROL_PANEL_TRANSACTION = 33;
    public static final int START_SERVICE_TRANSACTION = 34;
    public static final int STOP_SERVICE_TRANSACTION = 35;
    public static final int BIND_SERVICE_TRANSACTION = 36;
    public static final int UNBIND_SERVICE_TRANSACTION = 37;
    public static final int PUBLISH_SERVICE_TRANSACTION = 38;
    public static final int ACTIVITY_RESUMED_TRANSACTION = 39;
    public static final int GOING_TO_SLEEP_TRANSACTION = 40;
    public static final int WAKING_UP_TRANSACTION = 41;
    public static final int SET_DEBUG_APP_TRANSACTION = 42;
    public static final int SET_ALWAYS_FINISH_TRANSACTION = 43;
    public static final int START_INSTRUMENTATION_TRANSACTION = 44;
    public static final int FINISH_INSTRUMENTATION_TRANSACTION = 45;
    public static final int GET_CONFIGURATION_TRANSACTION = 46;
    public static final int UPDATE_CONFIGURATION_TRANSACTION = 47;
    public static final int STOP_SERVICE_TOKEN_TRANSACTION = 48;
    public static final int GET_ACTIVITY_CLASS_FOR_TOKEN_TRANSACTION = 49;
    public static final int GET_PACKAGE_FOR_TOKEN_TRANSACTION = 50;
    public static final int SET_PROCESS_LIMIT_TRANSACTION = 51;
    public static final int GET_PROCESS_LIMIT_TRANSACTION = 52;
    public static final int CHECK_PERMISSION_TRANSACTION = 53;
    public static final int CHECK_URI_PERMISSION_TRANSACTION = 54;
    public static final int GRANT_URI_PERMISSION_TRANSACTION = 55;
    public static final int REVOKE_URI_PERMISSION_TRANSACTION = 56;
    public static final int SET_ACTIVITY_CONTROLLER_TRANSACTION = 57;
    public static final int SHOW_WAITING_FOR_DEBUGGER_TRANSACTION = 58;
    public static final int SIGNAL_PERSISTENT_PROCESSES_TRANSACTION = 59;
    public static final int GET_RECENT_TASKS_TRANSACTION = 60;
    public static final int SERVICE_DONE_EXECUTING_TRANSACTION = 61;
    public static final int ACTIVITY_DESTROYED_TRANSACTION = 62;
    public static final int GET_INTENT_SENDER_TRANSACTION = 63;
    public static final int CANCEL_INTENT_SENDER_TRANSACTION = 64;
    public static final int GET_PACKAGE_FOR_INTENT_SENDER_TRANSACTION = 65;
    public static final int ENTER_SAFE_MODE_TRANSACTION = 66;
    public static final int START_NEXT_MATCHING_ACTIVITY_TRANSACTION = 67;
    public static final int NOTE_WAKEUP_ALARM_TRANSACTION = 68;
    public static final int REMOVE_CONTENT_PROVIDER_TRANSACTION = 69;
    public static final int SET_REQUESTED_ORIENTATION_TRANSACTION = 70;
    public static final int GET_REQUESTED_ORIENTATION_TRANSACTION = 71;
    public static final int UNBIND_FINISHED_TRANSACTION = 72;
    public static final int SET_PROCESS_FOREGROUND_TRANSACTION = 73;
    public static final int SET_SERVICE_FOREGROUND_TRANSACTION = 74;
    public static final int MOVE_ACTIVITY_TASK_TO_BACK_TRANSACTION = 75;
    public static final int GET_MEMORY_INFO_TRANSACTION = 76;
    public static final int GET_PROCESSES_IN_ERROR_STATE_TRANSACTION = 77;
    public static final int CLEAR_APP_DATA_TRANSACTION = 78;
    public static final int FORCE_STOP_PACKAGE_TRANSACTION = 79;
    public static final int KILL_PIDS_TRANSACTION = 80;
    public static final int GET_SERVICES_TRANSACTION = 81;
    public static final int GET_TASK_THUMBNAILS_TRANSACTION = 82;
    public static final int GET_RUNNING_APP_PROCESSES_TRANSACTION = 83;
    public static final int GET_DEVICE_CONFIGURATION_TRANSACTION = 84;
    public static final int PEEK_SERVICE_TRANSACTION = 85;
    public static final int PROFILE_CONTROL_TRANSACTION = 86;
    public static final int SHUTDOWN_TRANSACTION = 87;
    public static final int STOP_APP_SWITCHES_TRANSACTION = 88;
    public static final int RESUME_APP_SWITCHES_TRANSACTION = 89;
    public static final int START_BACKUP_AGENT_TRANSACTION = 90;
    public static final int BACKUP_AGENT_CREATED_TRANSACTION = 91;
    public static final int UNBIND_BACKUP_AGENT_TRANSACTION = 92;
    public static final int GET_UID_FOR_INTENT_SENDER_TRANSACTION = 93;
    public static final int HANDLE_INCOMING_USER_TRANSACTION = 94;
    public static final int GET_TASK_TOP_THUMBNAIL_TRANSACTION = 95;
    public static final int KILL_APPLICATION_WITH_APPID_TRANSACTION = 96;
    public static final int CLOSE_SYSTEM_DIALOGS_TRANSACTION = 97;
    public static final int GET_PROCESS_MEMORY_INFO_TRANSACTION = 98;
    public static final int KILL_APPLICATION_PROCESS_TRANSACTION = 99;
    public static final int START_ACTIVITY_INTENT_SENDER_TRANSACTION = 100;
    public static final int OVERRIDE_PENDING_TRANSITION_TRANSACTION = 101;
    public static final int HANDLE_APPLICATION_WTF_TRANSACTION = 102;
    public static final int KILL_BACKGROUND_PROCESSES_TRANSACTION = 103;
    public static final int IS_USER_A_MONKEY_TRANSACTION = 104;
    public static final int START_ACTIVITY_AND_WAIT_TRANSACTION = 105;
    public static final int WILL_ACTIVITY_BE_VISIBLE_TRANSACTION = 106;
    public static final int START_ACTIVITY_WITH_CONFIG_TRANSACTION = 107;
    public static final int GET_RUNNING_EXTERNAL_APPLICATIONS_TRANSACTION = 108;
    public static final int FINISH_HEAVY_WEIGHT_APP_TRANSACTION = 109;
    public static final int HANDLE_APPLICATION_STRICT_MODE_VIOLATION_TRANSACTION = 110;
    public static final int IS_IMMERSIVE_TRANSACTION = 111;
    public static final int SET_IMMERSIVE_TRANSACTION = 112;
    public static final int IS_TOP_ACTIVITY_IMMERSIVE_TRANSACTION = 113;
    public static final int CRASH_APPLICATION_TRANSACTION = 114;
    public static final int GET_PROVIDER_MIME_TYPE_TRANSACTION = 115;
    public static final int NEW_URI_PERMISSION_OWNER_TRANSACTION = 116;
    public static final int GRANT_URI_PERMISSION_FROM_OWNER_TRANSACTION = 117;
    public static final int REVOKE_URI_PERMISSION_FROM_OWNER_TRANSACTION = 118;
    public static final int CHECK_GRANT_URI_PERMISSION_TRANSACTION = 119;
    public static final int DUMP_HEAP_TRANSACTION = 120;
    public static final int START_ACTIVITIES_TRANSACTION = 121;
    public static final int IS_USER_RUNNING_TRANSACTION = 122;
    public static final int ACTIVITY_SLEPT_TRANSACTION = 123;
    public static final int GET_FRONT_ACTIVITY_SCREEN_COMPAT_MODE_TRANSACTION = 124;
    public static final int SET_FRONT_ACTIVITY_SCREEN_COMPAT_MODE_TRANSACTION = 125;
    public static final int GET_PACKAGE_SCREEN_COMPAT_MODE_TRANSACTION = 126;
    public static final int SET_PACKAGE_SCREEN_COMPAT_MODE_TRANSACTION = 127;
    public static final int GET_PACKAGE_ASK_SCREEN_COMPAT_TRANSACTION = 128;
    public static final int SET_PACKAGE_ASK_SCREEN_COMPAT_TRANSACTION = 129;
    public static final int SWITCH_USER_TRANSACTION = 130;
    public static final int REMOVE_SUB_TASK_TRANSACTION = 131;
    public static final int REMOVE_TASK_TRANSACTION = 132;
    public static final int REGISTER_PROCESS_OBSERVER_TRANSACTION = 133;
    public static final int UNREGISTER_PROCESS_OBSERVER_TRANSACTION = 134;
    public static final int IS_INTENT_SENDER_TARGETED_TO_PACKAGE_TRANSACTION = 135;
    public static final int UPDATE_PERSISTENT_CONFIGURATION_TRANSACTION = 136;
    public static final int GET_PROCESS_PSS_TRANSACTION = 137;
    public static final int SHOW_BOOT_MESSAGE_TRANSACTION = 138;
    public static final int DISMISS_KEYGUARD_ON_NEXT_ACTIVITY_TRANSACTION = 139;
    public static final int KILL_ALL_BACKGROUND_PROCESSES_TRANSACTION = 140;
    public static final int GET_CONTENT_PROVIDER_EXTERNAL_TRANSACTION = 141;
    public static final int REMOVE_CONTENT_PROVIDER_EXTERNAL_TRANSACTION = 142;
    public static final int GET_MY_MEMORY_STATE_TRANSACTION = 143;
    public static final int KILL_PROCESSES_BELOW_FOREGROUND_TRANSACTION = 144;
    public static final int GET_CURRENT_USER_TRANSACTION = 145;
    public static final int TARGET_TASK_AFFINITY_MATCHES_ACTIVITY_TRANSACTION = 146;
    public static final int NAVIGATE_UP_TO_TRANSACTION = 147;
    public static final int SET_LOCK_SCREEN_SHOWN_TRANSACTION = 148;
    public static final int FINISH_ACTIVITY_AFFINITY_TRANSACTION = 149;
    public static final int GET_LAUNCHED_FROM_UID_TRANSACTION = 150;
    public static final int UNSTABLE_PROVIDER_DIED_TRANSACTION = 151;
    public static final int IS_INTENT_SENDER_AN_ACTIVITY_TRANSACTION = 152;
    public static final int START_ACTIVITY_AS_USER_TRANSACTION = 153;
    public static final int STOP_USER_TRANSACTION = 154;
    public static final int REGISTER_USER_SWITCH_OBSERVER_TRANSACTION = 155;
    public static final int UNREGISTER_USER_SWITCH_OBSERVER_TRANSACTION = 156;
    public static final int GET_RUNNING_USER_IDS_TRANSACTION = 157;
    public static final int REQUEST_BUG_REPORT_TRANSACTION = 158;
    public static final int INPUT_DISPATCHING_TIMED_OUT_TRANSACTION = 159;
    public static final int CLEAR_PENDING_BACKUP_TRANSACTION = 160;
    public static final int GET_INTENT_FOR_INTENT_SENDER_TRANSACTION = 161;
    public static final int GET_ASSIST_CONTEXT_EXTRAS_TRANSACTION = 162;
    public static final int REPORT_ASSIST_CONTEXT_EXTRAS_TRANSACTION = 163;
    public static final int GET_LAUNCHED_FROM_PACKAGE_TRANSACTION = 164;
    public static final int KILL_UID_TRANSACTION = 165;
    public static final int SET_USER_IS_MONKEY_TRANSACTION = 166;
    public static final int HANG_TRANSACTION = 167;
    public static final int CREATE_STACK_TRANSACTION = 168;
    public static final int MOVE_TASK_TO_STACK_TRANSACTION = 169;
    public static final int RESIZE_STACK_TRANSACTION = 170;
    public static final int GET_STACK_BOXES_TRANSACTION = 171;
    public static final int SET_FOCUSED_STACK_TRANSACTION = 172;
    public static final int GET_STACK_BOX_INFO_TRANSACTION = 173;
    public static final int CONVERT_FROM_TRANSLUCENT_TRANSACTION = 174;
    public static final int CONVERT_TO_TRANSLUCENT_TRANSACTION = 175;
    public static final int NOTIFY_ACTIVITY_DRAWN_TRANSACTION = 176;
    public static final int REPORT_ACTIVITY_FULLY_DRAWN_TRANSACTION = 177;
    public static final int RESTART_TRANSACTION = 178;
    public static final int PERFORM_IDLE_MAINTENANCE_TRANSACTION = 179;
    public static final int TAKE_PERSISTABLE_URI_PERMISSION_TRANSACTION = 180;
    public static final int RELEASE_PERSISTABLE_URI_PERMISSION_TRANSACTION = 181;
    public static final int GET_PERSISTED_URI_PERMISSIONS_TRANSACTION = 182;
    public static final int APP_NOT_RESPONDING_VIA_PROVIDER_TRANSACTION = 183;

    int startActivity(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, String str4, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle) throws RemoteException;

    int startActivityAsUser(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, String str4, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle, int i3) throws RemoteException;

    WaitResult startActivityAndWait(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, String str4, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle, int i3) throws RemoteException;

    int startActivityWithConfig(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, Configuration configuration, Bundle bundle, int i3) throws RemoteException;

    int startActivityIntentSender(IApplicationThread iApplicationThread, IntentSender intentSender, Intent intent, String str, IBinder iBinder, String str2, int i, int i2, int i3, Bundle bundle) throws RemoteException;

    boolean startNextMatchingActivity(IBinder iBinder, Intent intent, Bundle bundle) throws RemoteException;

    boolean finishActivity(IBinder iBinder, int i, Intent intent) throws RemoteException;

    void finishSubActivity(IBinder iBinder, String str, int i) throws RemoteException;

    boolean finishActivityAffinity(IBinder iBinder) throws RemoteException;

    boolean willActivityBeVisible(IBinder iBinder) throws RemoteException;

    Intent registerReceiver(IApplicationThread iApplicationThread, String str, IIntentReceiver iIntentReceiver, IntentFilter intentFilter, String str2, int i) throws RemoteException;

    void unregisterReceiver(IIntentReceiver iIntentReceiver) throws RemoteException;

    int broadcastIntent(IApplicationThread iApplicationThread, Intent intent, String str, IIntentReceiver iIntentReceiver, int i, String str2, Bundle bundle, String str3, int i2, boolean z, boolean z2, int i3) throws RemoteException;

    void unbroadcastIntent(IApplicationThread iApplicationThread, Intent intent, int i) throws RemoteException;

    void finishReceiver(IBinder iBinder, int i, String str, Bundle bundle, boolean z) throws RemoteException;

    void attachApplication(IApplicationThread iApplicationThread) throws RemoteException;

    void activityResumed(IBinder iBinder) throws RemoteException;

    void activityIdle(IBinder iBinder, Configuration configuration, boolean z) throws RemoteException;

    void activityPaused(IBinder iBinder) throws RemoteException;

    void activityStopped(IBinder iBinder, Bundle bundle, Bitmap bitmap, CharSequence charSequence) throws RemoteException;

    void activitySlept(IBinder iBinder) throws RemoteException;

    void activityDestroyed(IBinder iBinder) throws RemoteException;

    String getCallingPackage(IBinder iBinder) throws RemoteException;

    ComponentName getCallingActivity(IBinder iBinder) throws RemoteException;

    List<ActivityManager.RunningTaskInfo> getTasks(int i, int i2, IThumbnailReceiver iThumbnailReceiver) throws RemoteException;

    List<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2, int i3) throws RemoteException;

    ActivityManager.TaskThumbnails getTaskThumbnails(int i) throws RemoteException;

    Bitmap getTaskTopThumbnail(int i) throws RemoteException;

    List<ActivityManager.RunningServiceInfo> getServices(int i, int i2) throws RemoteException;

    List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException;

    void moveTaskToFront(int i, int i2, Bundle bundle) throws RemoteException;

    void moveTaskToBack(int i) throws RemoteException;

    boolean moveActivityTaskToBack(IBinder iBinder, boolean z) throws RemoteException;

    void moveTaskBackwards(int i) throws RemoteException;

    int createStack(int i, int i2, int i3, float f) throws RemoteException;

    void moveTaskToStack(int i, int i2, boolean z) throws RemoteException;

    void resizeStackBox(int i, float f) throws RemoteException;

    List<ActivityManager.StackBoxInfo> getStackBoxes() throws RemoteException;

    ActivityManager.StackBoxInfo getStackBoxInfo(int i) throws RemoteException;

    void setFocusedStack(int i) throws RemoteException;

    int getTaskForActivity(IBinder iBinder, boolean z) throws RemoteException;

    void reportThumbnail(IBinder iBinder, Bitmap bitmap, CharSequence charSequence) throws RemoteException;

    ContentProviderHolder getContentProvider(IApplicationThread iApplicationThread, String str, int i, boolean z) throws RemoteException;

    ContentProviderHolder getContentProviderExternal(String str, int i, IBinder iBinder) throws RemoteException;

    void removeContentProvider(IBinder iBinder, boolean z) throws RemoteException;

    void removeContentProviderExternal(String str, IBinder iBinder) throws RemoteException;

    void publishContentProviders(IApplicationThread iApplicationThread, List<ContentProviderHolder> list) throws RemoteException;

    boolean refContentProvider(IBinder iBinder, int i, int i2) throws RemoteException;

    void unstableProviderDied(IBinder iBinder) throws RemoteException;

    void appNotRespondingViaProvider(IBinder iBinder) throws RemoteException;

    PendingIntent getRunningServiceControlPanel(ComponentName componentName) throws RemoteException;

    ComponentName startService(IApplicationThread iApplicationThread, Intent intent, String str, int i) throws RemoteException;

    int stopService(IApplicationThread iApplicationThread, Intent intent, String str, int i) throws RemoteException;

    boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i) throws RemoteException;

    void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, boolean z) throws RemoteException;

    int bindService(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, int i, int i2) throws RemoteException;

    boolean unbindService(IServiceConnection iServiceConnection) throws RemoteException;

    void publishService(IBinder iBinder, Intent intent, IBinder iBinder2) throws RemoteException;

    void unbindFinished(IBinder iBinder, Intent intent, boolean z) throws RemoteException;

    void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3) throws RemoteException;

    IBinder peekService(Intent intent, String str) throws RemoteException;

    boolean bindBackupAgent(ApplicationInfo applicationInfo, int i) throws RemoteException;

    void clearPendingBackup() throws RemoteException;

    void backupAgentCreated(String str, IBinder iBinder) throws RemoteException;

    void unbindBackupAgent(ApplicationInfo applicationInfo) throws RemoteException;

    void killApplicationProcess(String str, int i) throws RemoteException;

    boolean startInstrumentation(ComponentName componentName, String str, int i, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i2) throws RemoteException;

    void finishInstrumentation(IApplicationThread iApplicationThread, int i, Bundle bundle) throws RemoteException;

    Configuration getConfiguration() throws RemoteException;

    void updateConfiguration(Configuration configuration) throws RemoteException;

    void setRequestedOrientation(IBinder iBinder, int i) throws RemoteException;

    int getRequestedOrientation(IBinder iBinder) throws RemoteException;

    ComponentName getActivityClassForToken(IBinder iBinder) throws RemoteException;

    String getPackageForToken(IBinder iBinder) throws RemoteException;

    IIntentSender getIntentSender(int i, String str, IBinder iBinder, String str2, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4) throws RemoteException;

    void cancelIntentSender(IIntentSender iIntentSender) throws RemoteException;

    boolean clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver, int i) throws RemoteException;

    String getPackageForIntentSender(IIntentSender iIntentSender) throws RemoteException;

    int getUidForIntentSender(IIntentSender iIntentSender) throws RemoteException;

    int handleIncomingUser(int i, int i2, int i3, boolean z, boolean z2, String str, String str2) throws RemoteException;

    void setProcessLimit(int i) throws RemoteException;

    int getProcessLimit() throws RemoteException;

    void setProcessForeground(IBinder iBinder, int i, boolean z) throws RemoteException;

    int checkPermission(String str, int i, int i2) throws RemoteException;

    int checkUriPermission(Uri uri, int i, int i2, int i3) throws RemoteException;

    void grantUriPermission(IApplicationThread iApplicationThread, String str, Uri uri, int i) throws RemoteException;

    void revokeUriPermission(IApplicationThread iApplicationThread, Uri uri, int i) throws RemoteException;

    void takePersistableUriPermission(Uri uri, int i) throws RemoteException;

    void releasePersistableUriPermission(Uri uri, int i) throws RemoteException;

    ParceledListSlice<UriPermission> getPersistedUriPermissions(String str, boolean z) throws RemoteException;

    void showWaitingForDebugger(IApplicationThread iApplicationThread, boolean z) throws RemoteException;

    void getMemoryInfo(ActivityManager.MemoryInfo memoryInfo) throws RemoteException;

    void killBackgroundProcesses(String str, int i) throws RemoteException;

    void killAllBackgroundProcesses() throws RemoteException;

    void forceStopPackage(String str, int i) throws RemoteException;

    void goingToSleep() throws RemoteException;

    void wakingUp() throws RemoteException;

    void setLockScreenShown(boolean z) throws RemoteException;

    void unhandledBack() throws RemoteException;

    ParcelFileDescriptor openContentUri(Uri uri) throws RemoteException;

    void setDebugApp(String str, boolean z, boolean z2) throws RemoteException;

    void setAlwaysFinish(boolean z) throws RemoteException;

    void setActivityController(IActivityController iActivityController) throws RemoteException;

    void enterSafeMode() throws RemoteException;

    void noteWakeupAlarm(IIntentSender iIntentSender) throws RemoteException;

    boolean killPids(int[] iArr, String str, boolean z) throws RemoteException;

    boolean killProcessesBelowForeground(String str) throws RemoteException;

    void startRunning(String str, String str2, String str3, String str4) throws RemoteException;

    void handleApplicationCrash(IBinder iBinder, ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException;

    boolean handleApplicationWtf(IBinder iBinder, String str, ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException;

    void handleApplicationStrictModeViolation(IBinder iBinder, int i, StrictMode.ViolationInfo violationInfo) throws RemoteException;

    void signalPersistentProcesses(int i) throws RemoteException;

    List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException;

    List<ApplicationInfo> getRunningExternalApplications() throws RemoteException;

    void getMyMemoryState(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) throws RemoteException;

    ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException;

    boolean profileControl(String str, int i, boolean z, String str2, ParcelFileDescriptor parcelFileDescriptor, int i2) throws RemoteException;

    boolean shutdown(int i) throws RemoteException;

    void stopAppSwitches() throws RemoteException;

    void resumeAppSwitches() throws RemoteException;

    void killApplicationWithAppId(String str, int i, String str2) throws RemoteException;

    void closeSystemDialogs(String str) throws RemoteException;

    Debug.MemoryInfo[] getProcessMemoryInfo(int[] iArr) throws RemoteException;

    void overridePendingTransition(IBinder iBinder, String str, int i, int i2) throws RemoteException;

    boolean isUserAMonkey() throws RemoteException;

    void setUserIsMonkey(boolean z) throws RemoteException;

    void finishHeavyWeightApp() throws RemoteException;

    boolean convertFromTranslucent(IBinder iBinder) throws RemoteException;

    boolean convertToTranslucent(IBinder iBinder) throws RemoteException;

    void notifyActivityDrawn(IBinder iBinder) throws RemoteException;

    void setImmersive(IBinder iBinder, boolean z) throws RemoteException;

    boolean isImmersive(IBinder iBinder) throws RemoteException;

    boolean isTopActivityImmersive() throws RemoteException;

    void crashApplication(int i, int i2, String str, String str2) throws RemoteException;

    String getProviderMimeType(Uri uri, int i) throws RemoteException;

    IBinder newUriPermissionOwner(String str) throws RemoteException;

    void grantUriPermissionFromOwner(IBinder iBinder, int i, String str, Uri uri, int i2) throws RemoteException;

    void revokeUriPermissionFromOwner(IBinder iBinder, Uri uri, int i) throws RemoteException;

    int checkGrantUriPermission(int i, String str, Uri uri, int i2) throws RemoteException;

    boolean dumpHeap(String str, int i, boolean z, String str2, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    int startActivities(IApplicationThread iApplicationThread, String str, Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) throws RemoteException;

    int getFrontActivityScreenCompatMode() throws RemoteException;

    void setFrontActivityScreenCompatMode(int i) throws RemoteException;

    int getPackageScreenCompatMode(String str) throws RemoteException;

    void setPackageScreenCompatMode(String str, int i) throws RemoteException;

    boolean getPackageAskScreenCompat(String str) throws RemoteException;

    void setPackageAskScreenCompat(String str, boolean z) throws RemoteException;

    boolean switchUser(int i) throws RemoteException;

    int stopUser(int i, IStopUserCallback iStopUserCallback) throws RemoteException;

    UserInfo getCurrentUser() throws RemoteException;

    boolean isUserRunning(int i, boolean z) throws RemoteException;

    int[] getRunningUserIds() throws RemoteException;

    boolean removeSubTask(int i, int i2) throws RemoteException;

    boolean removeTask(int i, int i2) throws RemoteException;

    void registerProcessObserver(IProcessObserver iProcessObserver) throws RemoteException;

    void unregisterProcessObserver(IProcessObserver iProcessObserver) throws RemoteException;

    boolean isIntentSenderTargetedToPackage(IIntentSender iIntentSender) throws RemoteException;

    boolean isIntentSenderAnActivity(IIntentSender iIntentSender) throws RemoteException;

    Intent getIntentForIntentSender(IIntentSender iIntentSender) throws RemoteException;

    void updatePersistentConfiguration(Configuration configuration) throws RemoteException;

    long[] getProcessPss(int[] iArr) throws RemoteException;

    void showBootMessage(CharSequence charSequence, boolean z) throws RemoteException;

    void dismissKeyguardOnNextActivity() throws RemoteException;

    boolean targetTaskAffinityMatchesActivity(IBinder iBinder, String str) throws RemoteException;

    boolean navigateUpTo(IBinder iBinder, Intent intent, int i, Intent intent2) throws RemoteException;

    int getLaunchedFromUid(IBinder iBinder) throws RemoteException;

    String getLaunchedFromPackage(IBinder iBinder) throws RemoteException;

    void registerUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) throws RemoteException;

    void unregisterUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) throws RemoteException;

    void requestBugReport() throws RemoteException;

    long inputDispatchingTimedOut(int i, boolean z, String str) throws RemoteException;

    Bundle getAssistContextExtras(int i) throws RemoteException;

    void reportAssistContextExtras(IBinder iBinder, Bundle bundle) throws RemoteException;

    void killUid(int i, String str) throws RemoteException;

    void hang(IBinder iBinder, boolean z) throws RemoteException;

    void reportActivityFullyDrawn(IBinder iBinder) throws RemoteException;

    void restart() throws RemoteException;

    void performIdleMaintenance() throws RemoteException;

    boolean testIsSystemReady();

    /* loaded from: IActivityManager$ContentProviderHolder.class */
    public static class ContentProviderHolder implements Parcelable {
        public final ProviderInfo info;
        public IContentProvider provider;
        public IBinder connection;
        public boolean noReleaseNeeded;
        public static final Parcelable.Creator<ContentProviderHolder> CREATOR = new Parcelable.Creator<ContentProviderHolder>() { // from class: android.app.IActivityManager.ContentProviderHolder.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ContentProviderHolder createFromParcel(Parcel source) {
                return new ContentProviderHolder(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ContentProviderHolder[] newArray(int size) {
                return new ContentProviderHolder[size];
            }
        };

        public ContentProviderHolder(ProviderInfo _info) {
            this.info = _info;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            this.info.writeToParcel(dest, 0);
            if (this.provider != null) {
                dest.writeStrongBinder(this.provider.asBinder());
            } else {
                dest.writeStrongBinder(null);
            }
            dest.writeStrongBinder(this.connection);
            dest.writeInt(this.noReleaseNeeded ? 1 : 0);
        }

        private ContentProviderHolder(Parcel source) {
            this.info = ProviderInfo.CREATOR.createFromParcel(source);
            this.provider = ContentProviderNative.asInterface(source.readStrongBinder());
            this.connection = source.readStrongBinder();
            this.noReleaseNeeded = source.readInt() != 0;
        }
    }

    /* loaded from: IActivityManager$WaitResult.class */
    public static class WaitResult implements Parcelable {
        public int result;
        public boolean timeout;
        public ComponentName who;
        public long thisTime;
        public long totalTime;
        public static final Parcelable.Creator<WaitResult> CREATOR = new Parcelable.Creator<WaitResult>() { // from class: android.app.IActivityManager.WaitResult.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public WaitResult createFromParcel(Parcel source) {
                return new WaitResult(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public WaitResult[] newArray(int size) {
                return new WaitResult[size];
            }
        };

        public WaitResult() {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.result);
            dest.writeInt(this.timeout ? 1 : 0);
            ComponentName.writeToParcel(this.who, dest);
            dest.writeLong(this.thisTime);
            dest.writeLong(this.totalTime);
        }

        private WaitResult(Parcel source) {
            this.result = source.readInt();
            this.timeout = source.readInt() != 0;
            this.who = ComponentName.readFromParcel(source);
            this.thisTime = source.readLong();
            this.totalTime = source.readLong();
        }
    }
}