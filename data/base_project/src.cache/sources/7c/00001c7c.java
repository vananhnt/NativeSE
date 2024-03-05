package com.android.server;

import android.Manifest;
import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.LockPatternUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

/* loaded from: LockSettingsService.class */
public class LockSettingsService extends ILockSettings.Stub {
    private static final String PERMISSION = "android.permission.ACCESS_KEYGUARD_SECURE_STORAGE";
    private final DatabaseHelper mOpenHelper;
    private static final String TAG = "LockSettingsService";
    private static final String TABLE = "locksettings";
    private static final String COLUMN_KEY = "name";
    private static final String COLUMN_USERID = "user";
    private static final String COLUMN_VALUE = "value";
    private static final String SYSTEM_DIRECTORY = "/system/";
    private static final String LOCK_PATTERN_FILE = "gesture.key";
    private static final String LOCK_PASSWORD_FILE = "password.key";
    private final Context mContext;
    private LockPatternUtils mLockPatternUtils;
    private static final String[] COLUMNS_FOR_QUERY = {"value"};
    private static final String[] VALID_SETTINGS = {LockPatternUtils.LOCKOUT_PERMANENT_KEY, LockPatternUtils.LOCKOUT_ATTEMPT_DEADLINE, LockPatternUtils.PATTERN_EVER_CHOSEN_KEY, LockPatternUtils.PASSWORD_TYPE_KEY, LockPatternUtils.PASSWORD_TYPE_ALTERNATE_KEY, LockPatternUtils.LOCK_PASSWORD_SALT_KEY, "lockscreen.disabled", LockPatternUtils.LOCKSCREEN_OPTIONS, LockPatternUtils.LOCKSCREEN_BIOMETRIC_WEAK_FALLBACK, LockPatternUtils.BIOMETRIC_WEAK_EVER_CHOSEN_KEY, LockPatternUtils.LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS, LockPatternUtils.PASSWORD_HISTORY_KEY, "lock_pattern_autolock", Settings.Secure.LOCK_BIOMETRIC_WEAK_FLAGS, "lock_pattern_visible_pattern", "lock_pattern_tactile_feedback_enabled"};
    private static final String[] READ_PROFILE_PROTECTED_SETTINGS = {Settings.Secure.LOCK_SCREEN_OWNER_INFO_ENABLED, Settings.Secure.LOCK_SCREEN_OWNER_INFO};

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LockSettingsService.removeUser(int):void, file: LockSettingsService.class
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
    @Override // com.android.internal.widget.ILockSettings
    public void removeUser(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LockSettingsService.removeUser(int):void, file: LockSettingsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LockSettingsService.removeUser(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LockSettingsService.writeToDb(android.database.sqlite.SQLiteDatabase, java.lang.String, java.lang.String, int):void, file: LockSettingsService.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public void writeToDb(android.database.sqlite.SQLiteDatabase r1, java.lang.String r2, java.lang.String r3, int r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LockSettingsService.writeToDb(android.database.sqlite.SQLiteDatabase, java.lang.String, java.lang.String, int):void, file: LockSettingsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LockSettingsService.writeToDb(android.database.sqlite.SQLiteDatabase, java.lang.String, java.lang.String, int):void");
    }

    public LockSettingsService(Context context) {
        this.mContext = context;
        this.mOpenHelper = new DatabaseHelper(this.mContext);
        this.mLockPatternUtils = new LockPatternUtils(context);
    }

    public void systemReady() {
        migrateOldData();
    }

    private void migrateOldData() {
        try {
            if (getString("migrated", null, 0) == null) {
                ContentResolver cr = this.mContext.getContentResolver();
                String[] arr$ = VALID_SETTINGS;
                for (String validSetting : arr$) {
                    String value = Settings.Secure.getString(cr, validSetting);
                    if (value != null) {
                        setString(validSetting, value, 0);
                    }
                }
                setString("migrated", "true", 0);
                Slog.i(TAG, "Migrated lock settings to new location");
            }
            if (getString("migrated_user_specific", null, 0) == null) {
                UserManager um = (UserManager) this.mContext.getSystemService("user");
                ContentResolver cr2 = this.mContext.getContentResolver();
                List<UserInfo> users = um.getUsers();
                for (int user = 0; user < users.size(); user++) {
                    int userId = users.get(user).id;
                    String ownerInfo = Settings.Secure.getStringForUser(cr2, Settings.Secure.LOCK_SCREEN_OWNER_INFO, userId);
                    if (ownerInfo != null) {
                        setString(Settings.Secure.LOCK_SCREEN_OWNER_INFO, ownerInfo, userId);
                        Settings.Secure.putStringForUser(cr2, ownerInfo, "", userId);
                    }
                    try {
                        int ivalue = Settings.Secure.getIntForUser(cr2, Settings.Secure.LOCK_SCREEN_OWNER_INFO_ENABLED, userId);
                        boolean enabled = ivalue != 0;
                        setLong(Settings.Secure.LOCK_SCREEN_OWNER_INFO_ENABLED, enabled ? 1L : 0L, userId);
                    } catch (Settings.SettingNotFoundException e) {
                        if (!TextUtils.isEmpty(ownerInfo)) {
                            setLong(Settings.Secure.LOCK_SCREEN_OWNER_INFO_ENABLED, 1L, userId);
                        }
                    }
                    Settings.Secure.putIntForUser(cr2, Settings.Secure.LOCK_SCREEN_OWNER_INFO_ENABLED, 0, userId);
                }
                setString("migrated_user_specific", "true", 0);
                Slog.i(TAG, "Migrated per-user lock settings to new location");
            }
        } catch (RemoteException re) {
            Slog.e(TAG, "Unable to migrate old data", re);
        }
    }

    private final void checkWritePermission(int userId) {
        this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE");
    }

    private final void checkPasswordReadPermission(int userId) {
        this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE");
    }

    private final void checkReadPermission(String requestedKey, int userId) {
        int callingUid = Binder.getCallingUid();
        for (int i = 0; i < READ_PROFILE_PROTECTED_SETTINGS.length; i++) {
            String key = READ_PROFILE_PROTECTED_SETTINGS[i];
            if (key.equals(requestedKey) && this.mContext.checkCallingOrSelfPermission(Manifest.permission.READ_PROFILE) != 0) {
                throw new SecurityException("uid=" + callingUid + " needs permission " + Manifest.permission.READ_PROFILE + " to read " + requestedKey + " for user " + userId);
            }
        }
    }

    @Override // com.android.internal.widget.ILockSettings
    public void setBoolean(String key, boolean value, int userId) throws RemoteException {
        checkWritePermission(userId);
        writeToDb(key, value ? "1" : "0", userId);
    }

    @Override // com.android.internal.widget.ILockSettings
    public void setLong(String key, long value, int userId) throws RemoteException {
        checkWritePermission(userId);
        writeToDb(key, Long.toString(value), userId);
    }

    @Override // com.android.internal.widget.ILockSettings
    public void setString(String key, String value, int userId) throws RemoteException {
        checkWritePermission(userId);
        writeToDb(key, value, userId);
    }

    @Override // com.android.internal.widget.ILockSettings
    public boolean getBoolean(String key, boolean defaultValue, int userId) throws RemoteException {
        checkReadPermission(key, userId);
        String value = readFromDb(key, null, userId);
        return TextUtils.isEmpty(value) ? defaultValue : value.equals("1") || value.equals("true");
    }

    @Override // com.android.internal.widget.ILockSettings
    public long getLong(String key, long defaultValue, int userId) throws RemoteException {
        checkReadPermission(key, userId);
        String value = readFromDb(key, null, userId);
        return TextUtils.isEmpty(value) ? defaultValue : Long.parseLong(value);
    }

    @Override // com.android.internal.widget.ILockSettings
    public String getString(String key, String defaultValue, int userId) throws RemoteException {
        checkReadPermission(key, userId);
        return readFromDb(key, defaultValue, userId);
    }

    private String getLockPatternFilename(int userId) {
        String dataSystemDirectory = Environment.getDataDirectory().getAbsolutePath() + SYSTEM_DIRECTORY;
        if (userId == 0) {
            return dataSystemDirectory + LOCK_PATTERN_FILE;
        }
        return new File(Environment.getUserSystemDirectory(userId), LOCK_PATTERN_FILE).getAbsolutePath();
    }

    private String getLockPasswordFilename(int userId) {
        String dataSystemDirectory = Environment.getDataDirectory().getAbsolutePath() + SYSTEM_DIRECTORY;
        if (userId == 0) {
            return dataSystemDirectory + LOCK_PASSWORD_FILE;
        }
        return new File(Environment.getUserSystemDirectory(userId), LOCK_PASSWORD_FILE).getAbsolutePath();
    }

    @Override // com.android.internal.widget.ILockSettings
    public boolean havePassword(int userId) throws RemoteException {
        return new File(getLockPasswordFilename(userId)).length() > 0;
    }

    @Override // com.android.internal.widget.ILockSettings
    public boolean havePattern(int userId) throws RemoteException {
        return new File(getLockPatternFilename(userId)).length() > 0;
    }

    private void maybeUpdateKeystore(String password, int userId) {
        if (userId == 0) {
            KeyStore keyStore = KeyStore.getInstance();
            if (TextUtils.isEmpty(password) && keyStore.isEmpty()) {
                keyStore.reset();
            } else {
                keyStore.password(password);
            }
        }
    }

    @Override // com.android.internal.widget.ILockSettings
    public void setLockPattern(String pattern, int userId) throws RemoteException {
        checkWritePermission(userId);
        maybeUpdateKeystore(pattern, userId);
        byte[] hash = LockPatternUtils.patternToHash(LockPatternUtils.stringToPattern(pattern));
        writeFile(getLockPatternFilename(userId), hash);
    }

    @Override // com.android.internal.widget.ILockSettings
    public void setLockPassword(String password, int userId) throws RemoteException {
        checkWritePermission(userId);
        maybeUpdateKeystore(password, userId);
        writeFile(getLockPasswordFilename(userId), this.mLockPatternUtils.passwordToHash(password));
    }

    @Override // com.android.internal.widget.ILockSettings
    public boolean checkPattern(String pattern, int userId) throws RemoteException {
        checkPasswordReadPermission(userId);
        try {
            RandomAccessFile raf = new RandomAccessFile(getLockPatternFilename(userId), FullBackup.ROOT_TREE_TOKEN);
            byte[] stored = new byte[(int) raf.length()];
            int got = raf.read(stored, 0, stored.length);
            raf.close();
            if (got <= 0) {
                return true;
            }
            byte[] hash = LockPatternUtils.patternToHash(LockPatternUtils.stringToPattern(pattern));
            boolean matched = Arrays.equals(stored, hash);
            if (matched && !TextUtils.isEmpty(pattern)) {
                maybeUpdateKeystore(pattern, userId);
            }
            return matched;
        } catch (FileNotFoundException fnfe) {
            Slog.e(TAG, "Cannot read file " + fnfe);
            return true;
        } catch (IOException ioe) {
            Slog.e(TAG, "Cannot read file " + ioe);
            return true;
        }
    }

    @Override // com.android.internal.widget.ILockSettings
    public boolean checkPassword(String password, int userId) throws RemoteException {
        checkPasswordReadPermission(userId);
        try {
            RandomAccessFile raf = new RandomAccessFile(getLockPasswordFilename(userId), FullBackup.ROOT_TREE_TOKEN);
            byte[] stored = new byte[(int) raf.length()];
            int got = raf.read(stored, 0, stored.length);
            raf.close();
            if (got <= 0) {
                return true;
            }
            byte[] hash = this.mLockPatternUtils.passwordToHash(password);
            boolean matched = Arrays.equals(stored, hash);
            if (matched && !TextUtils.isEmpty(password)) {
                maybeUpdateKeystore(password, userId);
            }
            return matched;
        } catch (FileNotFoundException fnfe) {
            Slog.e(TAG, "Cannot read file " + fnfe);
            return true;
        } catch (IOException ioe) {
            Slog.e(TAG, "Cannot read file " + ioe);
            return true;
        }
    }

    private void writeFile(String name, byte[] hash) {
        try {
            RandomAccessFile raf = new RandomAccessFile(name, "rw");
            if (hash == null || hash.length == 0) {
                raf.setLength(0L);
            } else {
                raf.write(hash, 0, hash.length);
            }
            raf.close();
        } catch (IOException ioe) {
            Slog.e(TAG, "Error writing to file " + ioe);
        }
    }

    private void writeToDb(String key, String value, int userId) {
        writeToDb(this.mOpenHelper.getWritableDatabase(), key, value, userId);
    }

    private String readFromDb(String key, String defaultValue, int userId) {
        String result = defaultValue;
        SQLiteDatabase db = this.mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE, COLUMNS_FOR_QUERY, "user=? AND name=?", new String[]{Integer.toString(userId), key}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(0);
            }
            cursor.close();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LockSettingsService$DatabaseHelper.class */
    public class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "LockSettingsDB";
        private static final String DATABASE_NAME = "locksettings.db";
        private static final int DATABASE_VERSION = 2;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LockSettingsService.DatabaseHelper.loadSetting(android.database.sqlite.SQLiteDatabase, java.lang.String, int, boolean):void, file: LockSettingsService$DatabaseHelper.class
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
        private void loadSetting(android.database.sqlite.SQLiteDatabase r1, java.lang.String r2, int r3, boolean r4) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LockSettingsService.DatabaseHelper.loadSetting(android.database.sqlite.SQLiteDatabase, java.lang.String, int, boolean):void, file: LockSettingsService$DatabaseHelper.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.LockSettingsService.DatabaseHelper.loadSetting(android.database.sqlite.SQLiteDatabase, java.lang.String, int, boolean):void");
        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 2);
            setWriteAheadLoggingEnabled(true);
        }

        private void createTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE locksettings (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,user INTEGER,value TEXT);");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            createTable(db);
            initializeDefaults(db);
        }

        private void initializeDefaults(SQLiteDatabase db) {
            boolean lockScreenDisable = SystemProperties.getBoolean("ro.lockscreen.disable.default", false);
            if (lockScreenDisable) {
                LockSettingsService.this.writeToDb(db, "lockscreen.disabled", "1", 0);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            int upgradeVersion = oldVersion;
            if (upgradeVersion == 1) {
                maybeEnableWidgetSettingForUsers(db);
                upgradeVersion = 2;
            }
            if (upgradeVersion != 2) {
                Log.w(TAG, "Failed to upgrade database!");
            }
        }

        private void maybeEnableWidgetSettingForUsers(SQLiteDatabase db) {
            UserManager um = (UserManager) LockSettingsService.this.mContext.getSystemService("user");
            LockSettingsService.this.mContext.getContentResolver();
            List<UserInfo> users = um.getUsers();
            for (int i = 0; i < users.size(); i++) {
                int userId = users.get(i).id;
                boolean enabled = LockSettingsService.this.mLockPatternUtils.hasWidgetsEnabledInKeyguard(userId);
                Log.v(TAG, "Widget upgrade uid=" + userId + ", enabled=" + enabled + ", w[]=" + LockSettingsService.this.mLockPatternUtils.getAppWidgets());
                loadSetting(db, LockPatternUtils.LOCKSCREEN_WIDGETS_ENABLED, userId, enabled);
            }
        }
    }
}