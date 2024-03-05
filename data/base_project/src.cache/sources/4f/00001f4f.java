package com.android.server.pm;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.IStopUserCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IUserManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.view.Window;
import com.android.internal.R;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.am.ProcessList;
import gov.nist.core.Separators;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: UserManagerService.class */
public class UserManagerService extends IUserManager.Stub {
    private static final String LOG_TAG = "UserManagerService";
    private static final boolean DBG = false;
    private static final String TAG_NAME = "name";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_ICON_PATH = "icon";
    private static final String ATTR_ID = "id";
    private static final String ATTR_CREATION_TIME = "created";
    private static final String ATTR_LAST_LOGGED_IN_TIME = "lastLoggedIn";
    private static final String ATTR_SALT = "salt";
    private static final String ATTR_PIN_HASH = "pinHash";
    private static final String ATTR_FAILED_ATTEMPTS = "failedAttempts";
    private static final String ATTR_LAST_RETRY_MS = "lastAttemptMs";
    private static final String ATTR_SERIAL_NO = "serialNumber";
    private static final String ATTR_NEXT_SERIAL_NO = "nextSerialNumber";
    private static final String ATTR_PARTIAL = "partial";
    private static final String ATTR_USER_VERSION = "version";
    private static final String TAG_USER = "user";
    private static final String TAG_RESTRICTIONS = "restrictions";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_VALUE = "value";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_VALUE_TYPE = "type";
    private static final String ATTR_MULTIPLE = "m";
    private static final String ATTR_TYPE_STRING_ARRAY = "sa";
    private static final String ATTR_TYPE_STRING = "s";
    private static final String ATTR_TYPE_BOOLEAN = "b";
    private static final String USER_LIST_FILENAME = "userlist.xml";
    private static final String USER_PHOTO_FILENAME = "photo.png";
    private static final String RESTRICTIONS_FILE_PREFIX = "res_";
    private static final String XML_SUFFIX = ".xml";
    private static final int MIN_USER_ID = 10;
    private static final int USER_VERSION = 4;
    private static final long EPOCH_PLUS_30_YEARS = 946080000000L;
    private static final int BACKOFF_INC_INTERVAL = 5;
    private final Context mContext;
    private final PackageManagerService mPm;
    private final Object mInstallLock;
    private final Object mPackagesLock;
    private final Handler mHandler;
    private final File mUsersDir;
    private final File mUserListFile;
    private final File mBaseUserPath;
    private final SparseArray<UserInfo> mUsers;
    private final SparseArray<Bundle> mUserRestrictions;
    private final SparseArray<RestrictionsPinState> mRestrictionsPinStates;
    private final SparseBooleanArray mRemovingUserIds;
    private int[] mUserIds;
    private boolean mGuestEnabled;
    private int mNextSerialNumber;
    private int mUserVersion;
    private static UserManagerService sInstance;
    private PackageMonitor mUserPackageMonitor;
    private static final String TAG_USERS = "users";
    private static final String USER_INFO_DIR = "system" + File.separator + TAG_USERS;
    private static final int[] BACKOFF_TIMES = {0, Window.PROGRESS_SECONDARY_END, 60000, 300000, ProcessList.PSS_MAX_INTERVAL};

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.readUserListLocked():void, file: UserManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void readUserListLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.readUserListLocked():void, file: UserManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.readUserListLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.readUserLocked(int):android.content.pm.UserInfo, file: UserManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private android.content.pm.UserInfo readUserLocked(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.readUserLocked(int):android.content.pm.UserInfo, file: UserManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.readUserLocked(int):android.content.pm.UserInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.createUser(java.lang.String, int):android.content.pm.UserInfo, file: UserManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.os.IUserManager
    public android.content.pm.UserInfo createUser(java.lang.String r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.createUser(java.lang.String, int):android.content.pm.UserInfo, file: UserManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.createUser(java.lang.String, int):android.content.pm.UserInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.finishRemoveUser(int):void, file: UserManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void finishRemoveUser(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.finishRemoveUser(int):void, file: UserManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.finishRemoveUser(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.getUidForPackage(java.lang.String):int, file: UserManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private int getUidForPackage(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.getUidForPackage(java.lang.String):int, file: UserManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.getUidForPackage(java.lang.String):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.readApplicationRestrictionsLocked(java.lang.String, int):android.os.Bundle, file: UserManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private android.os.Bundle readApplicationRestrictionsLocked(java.lang.String r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.readApplicationRestrictionsLocked(java.lang.String, int):android.os.Bundle, file: UserManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.readApplicationRestrictionsLocked(java.lang.String, int):android.os.Bundle");
    }

    static /* synthetic */ PackageManagerService access$400(UserManagerService x0) {
        return x0.mPm;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: UserManagerService$RestrictionsPinState.class */
    public class RestrictionsPinState {
        long salt;
        String pinHash;
        int failedAttempts;
        long lastAttemptTime;

        RestrictionsPinState() {
        }
    }

    public static UserManagerService getInstance() {
        UserManagerService userManagerService;
        synchronized (UserManagerService.class) {
            userManagerService = sInstance;
        }
        return userManagerService;
    }

    UserManagerService(File dataDir, File baseUserPath) {
        this(null, null, new Object(), new Object(), dataDir, baseUserPath);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UserManagerService(Context context, PackageManagerService pm, Object installLock, Object packagesLock) {
        this(context, pm, installLock, packagesLock, Environment.getDataDirectory(), new File(Environment.getDataDirectory(), "user"));
    }

    private UserManagerService(Context context, PackageManagerService pm, Object installLock, Object packagesLock, File dataDir, File baseUserPath) {
        this.mUsers = new SparseArray<>();
        this.mUserRestrictions = new SparseArray<>();
        this.mRestrictionsPinStates = new SparseArray<>();
        this.mRemovingUserIds = new SparseBooleanArray();
        this.mUserVersion = 0;
        this.mUserPackageMonitor = new PackageMonitor() { // from class: com.android.server.pm.UserManagerService.5
            @Override // com.android.internal.content.PackageMonitor
            public void onPackageRemoved(String pkg, int uid) {
                int userId = getChangingUserId();
                boolean uninstalled = isPackageDisappearing(pkg) == 3;
                if (uninstalled && userId >= 0 && !UserManagerService.this.isPackageInstalled(pkg, userId)) {
                    UserManagerService.this.cleanAppRestrictionsForPackage(pkg, userId);
                }
            }
        };
        this.mContext = context;
        this.mPm = pm;
        this.mInstallLock = installLock;
        this.mPackagesLock = packagesLock;
        this.mHandler = new Handler();
        synchronized (this.mInstallLock) {
            synchronized (this.mPackagesLock) {
                this.mUsersDir = new File(dataDir, USER_INFO_DIR);
                this.mUsersDir.mkdirs();
                File userZeroDir = new File(this.mUsersDir, "0");
                userZeroDir.mkdirs();
                this.mBaseUserPath = baseUserPath;
                FileUtils.setPermissions(this.mUsersDir.toString(), 509, -1, -1);
                this.mUserListFile = new File(this.mUsersDir, USER_LIST_FILENAME);
                readUserListLocked();
                ArrayList<UserInfo> partials = new ArrayList<>();
                for (int i = 0; i < this.mUsers.size(); i++) {
                    UserInfo ui = this.mUsers.valueAt(i);
                    if (ui.partial && i != 0) {
                        partials.add(ui);
                    }
                }
                for (int i2 = 0; i2 < partials.size(); i2++) {
                    UserInfo ui2 = partials.get(i2);
                    Slog.w(LOG_TAG, "Removing partially created user #" + i2 + " (name=" + ui2.name + Separators.RPAREN);
                    removeUserStateLocked(ui2.id);
                }
                sInstance = this;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        Context context = ActivityThread.systemMain().getSystemContext();
        this.mUserPackageMonitor.register(context, null, UserHandle.ALL, false);
        userForeground(0);
    }

    @Override // android.os.IUserManager
    public List<UserInfo> getUsers(boolean excludeDying) {
        ArrayList<UserInfo> users;
        checkManageUsersPermission("query users");
        synchronized (this.mPackagesLock) {
            users = new ArrayList<>(this.mUsers.size());
            for (int i = 0; i < this.mUsers.size(); i++) {
                UserInfo ui = this.mUsers.valueAt(i);
                if (!ui.partial && (!excludeDying || !this.mRemovingUserIds.get(ui.id))) {
                    users.add(ui);
                }
            }
        }
        return users;
    }

    @Override // android.os.IUserManager
    public UserInfo getUserInfo(int userId) {
        UserInfo userInfoLocked;
        checkManageUsersPermission("query user");
        synchronized (this.mPackagesLock) {
            userInfoLocked = getUserInfoLocked(userId);
        }
        return userInfoLocked;
    }

    @Override // android.os.IUserManager
    public boolean isRestricted() {
        boolean isRestricted;
        synchronized (this.mPackagesLock) {
            isRestricted = getUserInfoLocked(UserHandle.getCallingUserId()).isRestricted();
        }
        return isRestricted;
    }

    private UserInfo getUserInfoLocked(int userId) {
        UserInfo ui = this.mUsers.get(userId);
        if (ui != null && ui.partial && !this.mRemovingUserIds.get(userId)) {
            Slog.w(LOG_TAG, "getUserInfo: unknown user #" + userId);
            return null;
        }
        return ui;
    }

    public boolean exists(int userId) {
        boolean contains;
        synchronized (this.mPackagesLock) {
            contains = ArrayUtils.contains(this.mUserIds, userId);
        }
        return contains;
    }

    @Override // android.os.IUserManager
    public void setUserName(int userId, String name) {
        checkManageUsersPermission("rename users");
        boolean changed = false;
        synchronized (this.mPackagesLock) {
            UserInfo info = this.mUsers.get(userId);
            if (info == null || info.partial) {
                Slog.w(LOG_TAG, "setUserName: unknown user #" + userId);
                return;
            }
            if (name != null && !name.equals(info.name)) {
                info.name = name;
                writeUserLocked(info);
                changed = true;
            }
            if (changed) {
                sendUserInfoChangedBroadcast(userId);
            }
        }
    }

    @Override // android.os.IUserManager
    public void setUserIcon(int userId, Bitmap bitmap) {
        checkManageUsersPermission("update users");
        synchronized (this.mPackagesLock) {
            UserInfo info = this.mUsers.get(userId);
            if (info == null || info.partial) {
                Slog.w(LOG_TAG, "setUserIcon: unknown user #" + userId);
                return;
            }
            writeBitmapLocked(info, bitmap);
            writeUserLocked(info);
            sendUserInfoChangedBroadcast(userId);
        }
    }

    private void sendUserInfoChangedBroadcast(int userId) {
        Intent changedIntent = new Intent(Intent.ACTION_USER_INFO_CHANGED);
        changedIntent.putExtra(Intent.EXTRA_USER_HANDLE, userId);
        changedIntent.addFlags(1073741824);
        this.mContext.sendBroadcastAsUser(changedIntent, UserHandle.ALL);
    }

    @Override // android.os.IUserManager
    public Bitmap getUserIcon(int userId) {
        checkManageUsersPermission("read users");
        synchronized (this.mPackagesLock) {
            UserInfo info = this.mUsers.get(userId);
            if (info == null || info.partial) {
                Slog.w(LOG_TAG, "getUserIcon: unknown user #" + userId);
                return null;
            } else if (info.iconPath == null) {
                return null;
            } else {
                return BitmapFactory.decodeFile(info.iconPath);
            }
        }
    }

    @Override // android.os.IUserManager
    public void setGuestEnabled(boolean enable) {
        checkManageUsersPermission("enable guest users");
        synchronized (this.mPackagesLock) {
            if (this.mGuestEnabled != enable) {
                this.mGuestEnabled = enable;
                for (int i = 0; i < this.mUsers.size(); i++) {
                    UserInfo user = this.mUsers.valueAt(i);
                    if (!user.partial && user.isGuest()) {
                        if (!enable) {
                            removeUser(user.id);
                        }
                        return;
                    }
                }
                if (enable) {
                    createUser("Guest", 4);
                }
            }
        }
    }

    @Override // android.os.IUserManager
    public boolean isGuestEnabled() {
        boolean z;
        synchronized (this.mPackagesLock) {
            z = this.mGuestEnabled;
        }
        return z;
    }

    @Override // android.os.IUserManager
    public void wipeUser(int userHandle) {
        checkManageUsersPermission("wipe user");
    }

    public void makeInitialized(int userId) {
        checkManageUsersPermission("makeInitialized");
        synchronized (this.mPackagesLock) {
            UserInfo info = this.mUsers.get(userId);
            if (info == null || info.partial) {
                Slog.w(LOG_TAG, "makeInitialized: unknown user #" + userId);
            }
            if ((info.flags & 16) == 0) {
                info.flags |= 16;
                writeUserLocked(info);
            }
        }
    }

    @Override // android.os.IUserManager
    public Bundle getUserRestrictions(int userId) {
        Bundle bundle;
        synchronized (this.mPackagesLock) {
            Bundle restrictions = this.mUserRestrictions.get(userId);
            bundle = restrictions != null ? restrictions : Bundle.EMPTY;
        }
        return bundle;
    }

    @Override // android.os.IUserManager
    public void setUserRestrictions(Bundle restrictions, int userId) {
        checkManageUsersPermission("setUserRestrictions");
        if (restrictions == null) {
            return;
        }
        synchronized (this.mPackagesLock) {
            this.mUserRestrictions.get(userId).clear();
            this.mUserRestrictions.get(userId).putAll(restrictions);
            writeUserLocked(this.mUsers.get(userId));
        }
    }

    private boolean isUserLimitReachedLocked() {
        int nUsers = this.mUsers.size();
        return nUsers >= UserManager.getMaxSupportedUsers();
    }

    private static final void checkManageUsersPermission(String message) {
        int uid = Binder.getCallingUid();
        if (uid != 1000 && uid != 0 && ActivityManager.checkComponentPermission(Manifest.permission.MANAGE_USERS, uid, -1, true) != 0) {
            throw new SecurityException("You need MANAGE_USERS permission to: " + message);
        }
    }

    private void writeBitmapLocked(UserInfo info, Bitmap bitmap) {
        try {
            File dir = new File(this.mUsersDir, Integer.toString(info.id));
            File file = new File(dir, USER_PHOTO_FILENAME);
            if (!dir.exists()) {
                dir.mkdir();
                FileUtils.setPermissions(dir.getPath(), 505, -1, -1);
            }
            Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
            FileOutputStream os = new FileOutputStream(file);
            if (bitmap.compress(compressFormat, 100, os)) {
                info.iconPath = file.getAbsolutePath();
            }
            try {
                os.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e2) {
            Slog.w(LOG_TAG, "Error setting photo for user ", e2);
        }
    }

    public int[] getUserIds() {
        int[] iArr;
        synchronized (this.mPackagesLock) {
            iArr = this.mUserIds;
        }
        return iArr;
    }

    int[] getUserIdsLPr() {
        return this.mUserIds;
    }

    private void upgradeIfNecessaryLocked() {
        int userVersion = this.mUserVersion;
        if (userVersion < 1) {
            UserInfo user = this.mUsers.get(0);
            if ("Primary".equals(user.name)) {
                user.name = this.mContext.getResources().getString(R.string.owner_name);
                writeUserLocked(user);
            }
            userVersion = 1;
        }
        if (userVersion < 2) {
            UserInfo user2 = this.mUsers.get(0);
            if ((user2.flags & 16) == 0) {
                user2.flags |= 16;
                writeUserLocked(user2);
            }
            userVersion = 2;
        }
        if (userVersion < 4) {
            userVersion = 4;
        }
        if (userVersion < 4) {
            Slog.w(LOG_TAG, "User version " + this.mUserVersion + " didn't upgrade as expected to 4");
            return;
        }
        this.mUserVersion = userVersion;
        writeUserListLocked();
    }

    private void fallbackToSingleUserLocked() {
        UserInfo primary = new UserInfo(0, this.mContext.getResources().getString(R.string.owner_name), null, 19);
        this.mUsers.put(0, primary);
        this.mNextSerialNumber = 10;
        this.mUserVersion = 4;
        Bundle restrictions = new Bundle();
        this.mUserRestrictions.append(0, restrictions);
        updateUserIdsLocked();
        writeUserListLocked();
        writeUserLocked(primary);
    }

    private void writeUserLocked(UserInfo userInfo) {
        FileOutputStream fos = null;
        AtomicFile userFile = new AtomicFile(new File(this.mUsersDir, userInfo.id + XML_SUFFIX));
        try {
            fos = userFile.startWrite();
            OutputStream bos = new BufferedOutputStream(fos);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(bos, "utf-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "user");
            serializer.attribute(null, "id", Integer.toString(userInfo.id));
            serializer.attribute(null, ATTR_SERIAL_NO, Integer.toString(userInfo.serialNumber));
            serializer.attribute(null, "flags", Integer.toString(userInfo.flags));
            serializer.attribute(null, "created", Long.toString(userInfo.creationTime));
            serializer.attribute(null, ATTR_LAST_LOGGED_IN_TIME, Long.toString(userInfo.lastLoggedInTime));
            RestrictionsPinState pinState = this.mRestrictionsPinStates.get(userInfo.id);
            if (pinState != null) {
                if (pinState.salt != 0) {
                    serializer.attribute(null, ATTR_SALT, Long.toString(pinState.salt));
                }
                if (pinState.pinHash != null) {
                    serializer.attribute(null, ATTR_PIN_HASH, pinState.pinHash);
                }
                if (pinState.failedAttempts != 0) {
                    serializer.attribute(null, ATTR_FAILED_ATTEMPTS, Integer.toString(pinState.failedAttempts));
                    serializer.attribute(null, ATTR_LAST_RETRY_MS, Long.toString(pinState.lastAttemptTime));
                }
            }
            if (userInfo.iconPath != null) {
                serializer.attribute(null, "icon", userInfo.iconPath);
            }
            if (userInfo.partial) {
                serializer.attribute(null, ATTR_PARTIAL, "true");
            }
            serializer.startTag(null, "name");
            serializer.text(userInfo.name);
            serializer.endTag(null, "name");
            Bundle restrictions = this.mUserRestrictions.get(userInfo.id);
            if (restrictions != null) {
                serializer.startTag(null, TAG_RESTRICTIONS);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_CONFIG_WIFI);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_MODIFY_ACCOUNTS);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_INSTALL_APPS);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_UNINSTALL_APPS);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_SHARE_LOCATION);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_CONFIG_BLUETOOTH);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_USB_FILE_TRANSFER);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_CONFIG_CREDENTIALS);
                writeBoolean(serializer, restrictions, UserManager.DISALLOW_REMOVE_USER);
                serializer.endTag(null, TAG_RESTRICTIONS);
            }
            serializer.endTag(null, "user");
            serializer.endDocument();
            userFile.finishWrite(fos);
        } catch (Exception ioe) {
            Slog.e(LOG_TAG, "Error writing user info " + userInfo.id + Separators.RETURN + ioe);
            userFile.failWrite(fos);
        }
    }

    private void writeUserListLocked() {
        FileOutputStream fos = null;
        AtomicFile userListFile = new AtomicFile(this.mUserListFile);
        try {
            fos = userListFile.startWrite();
            OutputStream bos = new BufferedOutputStream(fos);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(bos, "utf-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, TAG_USERS);
            serializer.attribute(null, ATTR_NEXT_SERIAL_NO, Integer.toString(this.mNextSerialNumber));
            serializer.attribute(null, "version", Integer.toString(this.mUserVersion));
            for (int i = 0; i < this.mUsers.size(); i++) {
                UserInfo user = this.mUsers.valueAt(i);
                serializer.startTag(null, "user");
                serializer.attribute(null, "id", Integer.toString(user.id));
                serializer.endTag(null, "user");
            }
            serializer.endTag(null, TAG_USERS);
            serializer.endDocument();
            userListFile.finishWrite(fos);
        } catch (Exception e) {
            userListFile.failWrite(fos);
            Slog.e(LOG_TAG, "Error writing user list");
        }
    }

    private void readBoolean(XmlPullParser parser, Bundle restrictions, String restrictionKey) {
        String value = parser.getAttributeValue(null, restrictionKey);
        if (value != null) {
            restrictions.putBoolean(restrictionKey, Boolean.parseBoolean(value));
        }
    }

    private void writeBoolean(XmlSerializer xml, Bundle restrictions, String restrictionKey) throws IOException {
        if (restrictions.containsKey(restrictionKey)) {
            xml.attribute(null, restrictionKey, Boolean.toString(restrictions.getBoolean(restrictionKey)));
        }
    }

    private int readIntAttribute(XmlPullParser parser, String attr, int defaultValue) {
        String valueString = parser.getAttributeValue(null, attr);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long readLongAttribute(XmlPullParser parser, String attr, long defaultValue) {
        String valueString = parser.getAttributeValue(null, attr);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPackageInstalled(String pkg, int userId) {
        ApplicationInfo info = this.mPm.getApplicationInfo(pkg, 8192, userId);
        if (info == null || (info.flags & 8388608) == 0) {
            return false;
        }
        return true;
    }

    private void cleanAppRestrictions(int userId, boolean all) {
        synchronized (this.mPackagesLock) {
            File dir = Environment.getUserSystemDirectory(userId);
            String[] files = dir.list();
            if (files == null) {
                return;
            }
            for (String fileName : files) {
                if (fileName.startsWith(RESTRICTIONS_FILE_PREFIX)) {
                    File resFile = new File(dir, fileName);
                    if (resFile.exists()) {
                        if (all) {
                            resFile.delete();
                        } else {
                            String pkg = restrictionsFileNameToPackage(fileName);
                            if (!isPackageInstalled(pkg, userId)) {
                                resFile.delete();
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanAppRestrictionsForPackage(String pkg, int userId) {
        synchronized (this.mPackagesLock) {
            File dir = Environment.getUserSystemDirectory(userId);
            File resFile = new File(dir, packageToRestrictionsFileName(pkg));
            if (resFile.exists()) {
                resFile.delete();
            }
        }
    }

    @Override // android.os.IUserManager
    public boolean removeUser(int userHandle) {
        checkManageUsersPermission("Only the system can remove users");
        synchronized (this.mPackagesLock) {
            UserInfo user = this.mUsers.get(userHandle);
            if (userHandle == 0 || user == null) {
                return false;
            }
            this.mRemovingUserIds.put(userHandle, true);
            user.partial = true;
            writeUserLocked(user);
            try {
                int res = ActivityManagerNative.getDefault().stopUser(userHandle, new IStopUserCallback.Stub() { // from class: com.android.server.pm.UserManagerService.1
                    @Override // android.app.IStopUserCallback
                    public void userStopped(int userId) {
                        UserManagerService.this.finishRemoveUser(userId);
                    }

                    @Override // android.app.IStopUserCallback
                    public void userStopAborted(int userId) {
                    }
                });
                return res == 0;
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUserStateLocked(final int userHandle) {
        this.mPm.cleanUpUserLILPw(userHandle);
        this.mUsers.remove(userHandle);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.pm.UserManagerService.3
            @Override // java.lang.Runnable
            public void run() {
                synchronized (UserManagerService.this.mPackagesLock) {
                    UserManagerService.this.mRemovingUserIds.delete(userHandle);
                }
            }
        }, DateUtils.MINUTE_IN_MILLIS);
        this.mRestrictionsPinStates.remove(userHandle);
        AtomicFile userFile = new AtomicFile(new File(this.mUsersDir, userHandle + XML_SUFFIX));
        userFile.delete();
        writeUserListLocked();
        updateUserIdsLocked();
        removeDirectoryRecursive(Environment.getUserSystemDirectory(userHandle));
    }

    private void removeDirectoryRecursive(File parent) {
        if (parent.isDirectory()) {
            String[] files = parent.list();
            for (String filename : files) {
                File child = new File(parent, filename);
                removeDirectoryRecursive(child);
            }
        }
        parent.delete();
    }

    @Override // android.os.IUserManager
    public Bundle getApplicationRestrictions(String packageName) {
        return getApplicationRestrictionsForUser(packageName, UserHandle.getCallingUserId());
    }

    @Override // android.os.IUserManager
    public Bundle getApplicationRestrictionsForUser(String packageName, int userId) {
        Bundle readApplicationRestrictionsLocked;
        if (UserHandle.getCallingUserId() != userId || !UserHandle.isSameApp(Binder.getCallingUid(), getUidForPackage(packageName))) {
            checkManageUsersPermission("Only system can get restrictions for other users/apps");
        }
        synchronized (this.mPackagesLock) {
            readApplicationRestrictionsLocked = readApplicationRestrictionsLocked(packageName, userId);
        }
        return readApplicationRestrictionsLocked;
    }

    @Override // android.os.IUserManager
    public void setApplicationRestrictions(String packageName, Bundle restrictions, int userId) {
        if (UserHandle.getCallingUserId() != userId || !UserHandle.isSameApp(Binder.getCallingUid(), getUidForPackage(packageName))) {
            checkManageUsersPermission("Only system can set restrictions for other users/apps");
        }
        synchronized (this.mPackagesLock) {
            writeApplicationRestrictionsLocked(packageName, restrictions, userId);
        }
    }

    @Override // android.os.IUserManager
    public boolean setRestrictionsChallenge(String newPin) {
        checkManageUsersPermission("Only system can modify the restrictions pin");
        int userId = UserHandle.getCallingUserId();
        synchronized (this.mPackagesLock) {
            RestrictionsPinState pinState = this.mRestrictionsPinStates.get(userId);
            if (pinState == null) {
                pinState = new RestrictionsPinState();
            }
            if (newPin == null) {
                pinState.salt = 0L;
                pinState.pinHash = null;
            } else {
                try {
                    pinState.salt = SecureRandom.getInstance("SHA1PRNG").nextLong();
                } catch (NoSuchAlgorithmException e) {
                    pinState.salt = (long) (Math.random() * 9.223372036854776E18d);
                }
                pinState.pinHash = passwordToHash(newPin, pinState.salt);
                pinState.failedAttempts = 0;
            }
            this.mRestrictionsPinStates.put(userId, pinState);
            writeUserLocked(this.mUsers.get(userId));
        }
        return true;
    }

    @Override // android.os.IUserManager
    public int checkRestrictionsChallenge(String pin) {
        checkManageUsersPermission("Only system can verify the restrictions pin");
        int userId = UserHandle.getCallingUserId();
        synchronized (this.mPackagesLock) {
            RestrictionsPinState pinState = this.mRestrictionsPinStates.get(userId);
            if (pinState == null || pinState.salt == 0 || pinState.pinHash == null) {
                return -2;
            }
            if (pin == null) {
                int waitTime = getRemainingTimeForPinAttempt(pinState);
                Slog.d(LOG_TAG, "Remaining waittime peek=" + waitTime);
                return waitTime;
            }
            int waitTime2 = getRemainingTimeForPinAttempt(pinState);
            Slog.d(LOG_TAG, "Remaining waittime=" + waitTime2);
            if (waitTime2 > 0) {
                return waitTime2;
            }
            if (passwordToHash(pin, pinState.salt).equals(pinState.pinHash)) {
                pinState.failedAttempts = 0;
                writeUserLocked(this.mUsers.get(userId));
                return -1;
            }
            pinState.failedAttempts++;
            pinState.lastAttemptTime = System.currentTimeMillis();
            writeUserLocked(this.mUsers.get(userId));
            return waitTime2;
        }
    }

    private int getRemainingTimeForPinAttempt(RestrictionsPinState pinState) {
        int backoffIndex = Math.min(pinState.failedAttempts / 5, BACKOFF_TIMES.length - 1);
        int backoffTime = pinState.failedAttempts % 5 == 0 ? BACKOFF_TIMES[backoffIndex] : 0;
        return (int) Math.max((backoffTime + pinState.lastAttemptTime) - System.currentTimeMillis(), 0L);
    }

    @Override // android.os.IUserManager
    public boolean hasRestrictionsChallenge() {
        boolean hasRestrictionsPinLocked;
        int userId = UserHandle.getCallingUserId();
        synchronized (this.mPackagesLock) {
            hasRestrictionsPinLocked = hasRestrictionsPinLocked(userId);
        }
        return hasRestrictionsPinLocked;
    }

    private boolean hasRestrictionsPinLocked(int userId) {
        RestrictionsPinState pinState = this.mRestrictionsPinStates.get(userId);
        if (pinState == null || pinState.salt == 0 || pinState.pinHash == null) {
            return false;
        }
        return true;
    }

    @Override // android.os.IUserManager
    public void removeRestrictions() {
        checkManageUsersPermission("Only system can remove restrictions");
        int userHandle = UserHandle.getCallingUserId();
        removeRestrictionsForUser(userHandle, true);
    }

    private void removeRestrictionsForUser(int userHandle, boolean unblockApps) {
        synchronized (this.mPackagesLock) {
            setUserRestrictions(new Bundle(), userHandle);
            setRestrictionsChallenge(null);
            cleanAppRestrictions(userHandle, true);
        }
        if (unblockApps) {
            unblockAllAppsForUser(userHandle);
        }
    }

    private void unblockAllAppsForUser(final int userHandle) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.UserManagerService.4
            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.4.run():void, file: UserManagerService$4.class
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                	at jadx.core.ProcessClass.process(ProcessClass.java:67)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
                Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                	... 6 more
                */
            @Override // java.lang.Runnable
            public void run() {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.UserManagerService.4.run():void, file: UserManagerService$4.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.AnonymousClass4.run():void");
            }
        });
    }

    private String passwordToHash(String password, long salt) {
        if (password == null) {
            return null;
        }
        String algo = null;
        String hashed = salt + password;
        try {
            byte[] saltedPassword = (password + salt).getBytes();
            byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(saltedPassword);
            algo = "MD5";
            byte[] md5 = MessageDigest.getInstance("MD5").digest(saltedPassword);
            hashed = toHex(sha1) + toHex(md5);
        } catch (NoSuchAlgorithmException e) {
            Log.w(LOG_TAG, "Failed to encode string because of missing algorithm: " + algo);
        }
        return hashed;
    }

    private static String toHex(byte[] ary) {
        String ret = "";
        for (int i = 0; i < ary.length; i++) {
            ret = (ret + "0123456789ABCDEF".charAt((ary[i] >> 4) & 15)) + "0123456789ABCDEF".charAt(ary[i] & 15);
        }
        return ret;
    }

    private void writeApplicationRestrictionsLocked(String packageName, Bundle restrictions, int userId) {
        FileOutputStream fos = null;
        AtomicFile restrictionsFile = new AtomicFile(new File(Environment.getUserSystemDirectory(userId), packageToRestrictionsFileName(packageName)));
        try {
            fos = restrictionsFile.startWrite();
            OutputStream bos = new BufferedOutputStream(fos);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(bos, "utf-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, TAG_RESTRICTIONS);
            for (String key : restrictions.keySet()) {
                Object value = restrictions.get(key);
                serializer.startTag(null, TAG_ENTRY);
                serializer.attribute(null, "key", key);
                if (value instanceof Boolean) {
                    serializer.attribute(null, "type", ATTR_TYPE_BOOLEAN);
                    serializer.text(value.toString());
                } else if (value == null || (value instanceof String)) {
                    serializer.attribute(null, "type", ATTR_TYPE_STRING);
                    serializer.text(value != null ? (String) value : "");
                } else {
                    serializer.attribute(null, "type", ATTR_TYPE_STRING_ARRAY);
                    String[] values = (String[]) value;
                    serializer.attribute(null, ATTR_MULTIPLE, Integer.toString(values.length));
                    int len$ = values.length;
                    for (int i$ = 0; i$ < len$; i$++) {
                        String choice = values[i$];
                        serializer.startTag(null, "value");
                        serializer.text(choice != null ? choice : "");
                        serializer.endTag(null, "value");
                    }
                }
                serializer.endTag(null, TAG_ENTRY);
            }
            serializer.endTag(null, TAG_RESTRICTIONS);
            serializer.endDocument();
            restrictionsFile.finishWrite(fos);
        } catch (Exception e) {
            restrictionsFile.failWrite(fos);
            Slog.e(LOG_TAG, "Error writing application restrictions list");
        }
    }

    @Override // android.os.IUserManager
    public int getUserSerialNumber(int userHandle) {
        synchronized (this.mPackagesLock) {
            if (exists(userHandle)) {
                return getUserInfoLocked(userHandle).serialNumber;
            }
            return -1;
        }
    }

    @Override // android.os.IUserManager
    public int getUserHandle(int userSerialNumber) {
        synchronized (this.mPackagesLock) {
            int[] arr$ = this.mUserIds;
            for (int userId : arr$) {
                if (getUserInfoLocked(userId).serialNumber == userSerialNumber) {
                    return userId;
                }
            }
            return -1;
        }
    }

    private void updateUserIdsLocked() {
        int num = 0;
        for (int i = 0; i < this.mUsers.size(); i++) {
            if (!this.mUsers.valueAt(i).partial) {
                num++;
            }
        }
        int[] newUsers = new int[num];
        int n = 0;
        for (int i2 = 0; i2 < this.mUsers.size(); i2++) {
            if (!this.mUsers.valueAt(i2).partial) {
                int i3 = n;
                n++;
                newUsers[i3] = this.mUsers.keyAt(i2);
            }
        }
        this.mUserIds = newUsers;
    }

    public void userForeground(int userId) {
        synchronized (this.mPackagesLock) {
            UserInfo user = this.mUsers.get(userId);
            long now = System.currentTimeMillis();
            if (user == null || user.partial) {
                Slog.w(LOG_TAG, "userForeground: unknown user #" + userId);
                return;
            }
            if (now > EPOCH_PLUS_30_YEARS) {
                user.lastLoggedInTime = now;
                writeUserLocked(user);
            }
            RestrictionsPinState pinState = this.mRestrictionsPinStates.get(userId);
            long salt = pinState == null ? 0L : pinState.salt;
            cleanAppRestrictions(userId, !user.isRestricted() && salt == 0);
        }
    }

    private int getNextAvailableIdLocked() {
        int i;
        synchronized (this.mPackagesLock) {
            int i2 = 10;
            while (i2 < Integer.MAX_VALUE) {
                if (this.mUsers.indexOfKey(i2) < 0 && !this.mRemovingUserIds.get(i2)) {
                    break;
                }
                i2++;
            }
            i = i2;
        }
        return i;
    }

    private String packageToRestrictionsFileName(String packageName) {
        return RESTRICTIONS_FILE_PREFIX + packageName + XML_SUFFIX;
    }

    private String restrictionsFileNameToPackage(String fileName) {
        return fileName.substring(RESTRICTIONS_FILE_PREFIX.length(), fileName.length() - XML_SUFFIX.length());
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump UserManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
            return;
        }
        long now = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        synchronized (this.mPackagesLock) {
            pw.println("Users:");
            for (int i = 0; i < this.mUsers.size(); i++) {
                UserInfo user = this.mUsers.valueAt(i);
                if (user != null) {
                    pw.print("  ");
                    pw.print(user);
                    pw.print(" serialNo=");
                    pw.print(user.serialNumber);
                    if (this.mRemovingUserIds.get(this.mUsers.keyAt(i))) {
                        pw.print(" <removing> ");
                    }
                    if (user.partial) {
                        pw.print(" <partial>");
                    }
                    pw.println();
                    pw.print("    Created: ");
                    if (user.creationTime == 0) {
                        pw.println(MediaStore.UNKNOWN_STRING);
                    } else {
                        sb.setLength(0);
                        TimeUtils.formatDuration(now - user.creationTime, sb);
                        sb.append(" ago");
                        pw.println(sb);
                    }
                    pw.print("    Last logged in: ");
                    if (user.lastLoggedInTime == 0) {
                        pw.println(MediaStore.UNKNOWN_STRING);
                    } else {
                        sb.setLength(0);
                        TimeUtils.formatDuration(now - user.lastLoggedInTime, sb);
                        sb.append(" ago");
                        pw.println(sb);
                    }
                }
            }
        }
    }
}