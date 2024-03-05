package android.os;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import com.android.internal.R;
import java.util.List;

/* loaded from: UserManager.class */
public class UserManager {
    private final IUserManager mService;
    private final Context mContext;
    public static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts";
    public static final String DISALLOW_CONFIG_WIFI = "no_config_wifi";
    public static final String DISALLOW_INSTALL_APPS = "no_install_apps";
    public static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps";
    public static final String DISALLOW_SHARE_LOCATION = "no_share_location";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
    public static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth";
    public static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer";
    public static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials";
    public static final String DISALLOW_REMOVE_USER = "no_remove_user";
    public static final int PIN_VERIFICATION_FAILED_INCORRECT = -3;
    public static final int PIN_VERIFICATION_FAILED_NOT_SET = -2;
    public static final int PIN_VERIFICATION_SUCCESS = -1;
    private static String TAG = "UserManager";
    private static UserManager sInstance = null;

    public static synchronized UserManager get(Context context) {
        if (sInstance == null) {
            sInstance = (UserManager) context.getSystemService("user");
        }
        return sInstance;
    }

    public UserManager(Context context, IUserManager service) {
        this.mService = service;
        this.mContext = context;
    }

    public static boolean supportsMultipleUsers() {
        return getMaxSupportedUsers() > 1;
    }

    public int getUserHandle() {
        return UserHandle.myUserId();
    }

    public String getUserName() {
        try {
            return this.mService.getUserInfo(getUserHandle()).name;
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user name", re);
            return "";
        }
    }

    public boolean isUserAGoat() {
        return false;
    }

    public boolean isLinkedUser() {
        try {
            return this.mService.isRestricted();
        } catch (RemoteException re) {
            Log.w(TAG, "Could not check if user is limited ", re);
            return false;
        }
    }

    public boolean isUserRunning(UserHandle user) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(user.getIdentifier(), false);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isUserRunningOrStopping(UserHandle user) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(user.getIdentifier(), true);
        } catch (RemoteException e) {
            return false;
        }
    }

    public UserInfo getUserInfo(int userHandle) {
        try {
            return this.mService.getUserInfo(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user info", re);
            return null;
        }
    }

    public Bundle getUserRestrictions() {
        return getUserRestrictions(Process.myUserHandle());
    }

    public Bundle getUserRestrictions(UserHandle userHandle) {
        try {
            return this.mService.getUserRestrictions(userHandle.getIdentifier());
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user restrictions", re);
            return Bundle.EMPTY;
        }
    }

    public void setUserRestrictions(Bundle restrictions) {
        setUserRestrictions(restrictions, Process.myUserHandle());
    }

    public void setUserRestrictions(Bundle restrictions, UserHandle userHandle) {
        try {
            this.mService.setUserRestrictions(restrictions, userHandle.getIdentifier());
        } catch (RemoteException re) {
            Log.w(TAG, "Could not set user restrictions", re);
        }
    }

    public void setUserRestriction(String key, boolean value) {
        Bundle bundle = getUserRestrictions();
        bundle.putBoolean(key, value);
        setUserRestrictions(bundle);
    }

    public void setUserRestriction(String key, boolean value, UserHandle userHandle) {
        Bundle bundle = getUserRestrictions(userHandle);
        bundle.putBoolean(key, value);
        setUserRestrictions(bundle, userHandle);
    }

    public boolean hasUserRestriction(String restrictionKey) {
        return hasUserRestriction(restrictionKey, Process.myUserHandle());
    }

    public boolean hasUserRestriction(String restrictionKey, UserHandle userHandle) {
        return getUserRestrictions(userHandle).getBoolean(restrictionKey, false);
    }

    public long getSerialNumberForUser(UserHandle user) {
        return getUserSerialNumber(user.getIdentifier());
    }

    public UserHandle getUserForSerialNumber(long serialNumber) {
        int ident = getUserHandle((int) serialNumber);
        if (ident >= 0) {
            return new UserHandle(ident);
        }
        return null;
    }

    public UserInfo createUser(String name, int flags) {
        try {
            return this.mService.createUser(name, flags);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not create a user", re);
            return null;
        }
    }

    public int getUserCount() {
        List<UserInfo> users = getUsers();
        if (users != null) {
            return users.size();
        }
        return 1;
    }

    public List<UserInfo> getUsers() {
        try {
            return this.mService.getUsers(false);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user list", re);
            return null;
        }
    }

    public List<UserInfo> getUsers(boolean excludeDying) {
        try {
            return this.mService.getUsers(excludeDying);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user list", re);
            return null;
        }
    }

    public boolean removeUser(int userHandle) {
        try {
            return this.mService.removeUser(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not remove user ", re);
            return false;
        }
    }

    public void setUserName(int userHandle, String name) {
        try {
            this.mService.setUserName(userHandle, name);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not set the user name ", re);
        }
    }

    public void setUserIcon(int userHandle, Bitmap icon) {
        try {
            this.mService.setUserIcon(userHandle, icon);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not set the user icon ", re);
        }
    }

    public Bitmap getUserIcon(int userHandle) {
        try {
            return this.mService.getUserIcon(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get the user icon ", re);
            return null;
        }
    }

    public void setGuestEnabled(boolean enable) {
        try {
            this.mService.setGuestEnabled(enable);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change guest account availability to " + enable);
        }
    }

    public boolean isGuestEnabled() {
        try {
            return this.mService.isGuestEnabled();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not retrieve guest enabled state");
            return false;
        }
    }

    public void wipeUser(int userHandle) {
        try {
            this.mService.wipeUser(userHandle);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not wipe user " + userHandle);
        }
    }

    public static int getMaxSupportedUsers() {
        if (Build.ID.startsWith("JVP")) {
            return 1;
        }
        return SystemProperties.getInt("fw.max_users", Resources.getSystem().getInteger(R.integer.config_multiuserMaximumUsers));
    }

    public int getUserSerialNumber(int userHandle) {
        try {
            return this.mService.getUserSerialNumber(userHandle);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get serial number for user " + userHandle);
            return -1;
        }
    }

    public int getUserHandle(int userSerialNumber) {
        try {
            return this.mService.getUserHandle(userSerialNumber);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get userHandle for user " + userSerialNumber);
            return -1;
        }
    }

    public Bundle getApplicationRestrictions(String packageName) {
        try {
            return this.mService.getApplicationRestrictions(packageName);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get application restrictions for package " + packageName);
            return null;
        }
    }

    public Bundle getApplicationRestrictions(String packageName, UserHandle user) {
        try {
            return this.mService.getApplicationRestrictionsForUser(packageName, user.getIdentifier());
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get application restrictions for user " + user.getIdentifier());
            return null;
        }
    }

    public void setApplicationRestrictions(String packageName, Bundle restrictions, UserHandle user) {
        try {
            this.mService.setApplicationRestrictions(packageName, restrictions, user.getIdentifier());
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set application restrictions for user " + user.getIdentifier());
        }
    }

    public boolean setRestrictionsChallenge(String newPin) {
        try {
            return this.mService.setRestrictionsChallenge(newPin);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change restrictions pin");
            return false;
        }
    }

    public int checkRestrictionsChallenge(String pin) {
        try {
            return this.mService.checkRestrictionsChallenge(pin);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not check restrictions pin");
            return -3;
        }
    }

    public boolean hasRestrictionsChallenge() {
        try {
            return this.mService.hasRestrictionsChallenge();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change restrictions pin");
            return false;
        }
    }

    public void removeRestrictions() {
        try {
            this.mService.removeRestrictions();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change restrictions pin");
        }
    }
}