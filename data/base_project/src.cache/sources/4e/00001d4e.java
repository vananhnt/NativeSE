package com.android.server.accounts;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.GrantCredentialsPermissionActivity;
import android.accounts.IAccountAuthenticator;
import android.accounts.IAccountAuthenticatorResponse;
import android.accounts.IAccountManager;
import android.accounts.IAccountManagerResponse;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.UserInfo;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Telephony;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.R;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.FgThread;
import com.google.android.collect.Lists;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: AccountManagerService.class */
public class AccountManagerService extends IAccountManager.Stub implements RegisteredServicesCacheListener<AuthenticatorDescription> {
    private static final String TAG = "AccountManagerService";
    private static final int TIMEOUT_DELAY_MS = 60000;
    private static final String DATABASE_NAME = "accounts.db";
    private static final int DATABASE_VERSION = 5;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private UserManager mUserManager;
    private final MessageHandler mMessageHandler;
    private static final int MESSAGE_TIMED_OUT = 3;
    private static final int MESSAGE_COPY_SHARED_ACCOUNT = 4;
    private final IAccountAuthenticatorCache mAuthenticatorCache;
    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String ACCOUNTS_ID = "_id";
    private static final String ACCOUNTS_NAME = "name";
    private static final String ACCOUNTS_TYPE = "type";
    private static final String ACCOUNTS_PASSWORD = "password";
    private static final String TABLE_AUTHTOKENS = "authtokens";
    private static final String AUTHTOKENS_ID = "_id";
    private static final String AUTHTOKENS_ACCOUNTS_ID = "accounts_id";
    private static final String AUTHTOKENS_TYPE = "type";
    private static final String AUTHTOKENS_AUTHTOKEN = "authtoken";
    private static final String TABLE_GRANTS = "grants";
    private static final String GRANTS_ACCOUNTS_ID = "accounts_id";
    private static final String GRANTS_AUTH_TOKEN_TYPE = "auth_token_type";
    private static final String GRANTS_GRANTEE_UID = "uid";
    private static final String TABLE_EXTRAS = "extras";
    private static final String EXTRAS_ID = "_id";
    private static final String EXTRAS_ACCOUNTS_ID = "accounts_id";
    private static final String EXTRAS_KEY = "key";
    private static final String EXTRAS_VALUE = "value";
    private static final String TABLE_META = "meta";
    private static final String META_KEY = "key";
    private static final String META_VALUE = "value";
    private static final String TABLE_SHARED_ACCOUNTS = "shared_accounts";
    private static final String COUNT_OF_MATCHING_GRANTS = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND auth_token_type=? AND name=? AND type=?";
    private static final String SELECTION_AUTHTOKENS_BY_ACCOUNT = "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)";
    private static final String SELECTION_USERDATA_BY_ACCOUNT = "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)";
    private final LinkedHashMap<String, Session> mSessions;
    private final AtomicInteger mNotificationIds;
    private final SparseArray<UserAccounts> mUsers;
    private static final String ACCOUNTS_TYPE_COUNT = "count(type)";
    private static final String[] ACCOUNT_TYPE_COUNT_PROJECTION = {"type", ACCOUNTS_TYPE_COUNT};
    private static final String[] COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN = {"type", "authtoken"};
    private static final String[] COLUMNS_EXTRAS_KEY_AND_VALUE = {"key", "value"};
    private static AtomicReference<AccountManagerService> sThis = new AtomicReference<>();
    private static final Account[] EMPTY_ACCOUNT_ARRAY = new Account[0];
    private static final Intent ACCOUNTS_CHANGED_INTENT = new Intent(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.purgeOldGrants(com.android.server.accounts.AccountManagerService$UserAccounts):void, file: AccountManagerService.class
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
    private void purgeOldGrants(com.android.server.accounts.AccountManagerService.UserAccounts r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.purgeOldGrants(com.android.server.accounts.AccountManagerService$UserAccounts):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.purgeOldGrants(com.android.server.accounts.AccountManagerService$UserAccounts):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.validateAccountsInternal(com.android.server.accounts.AccountManagerService$UserAccounts, boolean):void, file: AccountManagerService.class
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
    private void validateAccountsInternal(com.android.server.accounts.AccountManagerService.UserAccounts r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.validateAccountsInternal(com.android.server.accounts.AccountManagerService$UserAccounts, boolean):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.validateAccountsInternal(com.android.server.accounts.AccountManagerService$UserAccounts, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getPassword(android.accounts.Account):java.lang.String, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public java.lang.String getPassword(android.accounts.Account r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getPassword(android.accounts.Account):java.lang.String, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getPassword(android.accounts.Account):java.lang.String");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.readPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):java.lang.String, file: AccountManagerService.class
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
    private java.lang.String readPasswordInternal(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.readPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):java.lang.String, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.readPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):java.lang.String");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getUserData(android.accounts.Account, java.lang.String):java.lang.String, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public java.lang.String getUserData(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getUserData(android.accounts.Account, java.lang.String):java.lang.String, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getUserData(android.accounts.Account, java.lang.String):java.lang.String");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAuthenticatorTypes():android.accounts.AuthenticatorDescription[], file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public android.accounts.AuthenticatorDescription[] getAuthenticatorTypes() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAuthenticatorTypes():android.accounts.AuthenticatorDescription[], file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAuthenticatorTypes():android.accounts.AuthenticatorDescription[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.addAccountExplicitly(android.accounts.Account, java.lang.String, android.os.Bundle):boolean, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public boolean addAccountExplicitly(android.accounts.Account r1, java.lang.String r2, android.os.Bundle r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.addAccountExplicitly(android.accounts.Account, java.lang.String, android.os.Bundle):boolean, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.addAccountExplicitly(android.accounts.Account, java.lang.String, android.os.Bundle):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.copyAccountToUser(android.accounts.Account, int, int):boolean, file: AccountManagerService.class
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
    public boolean copyAccountToUser(android.accounts.Account r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.copyAccountToUser(android.accounts.Account, int, int):boolean, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.copyAccountToUser(android.accounts.Account, int, int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.completeCloningAccount(android.os.Bundle, android.accounts.Account, com.android.server.accounts.AccountManagerService$UserAccounts):void, file: AccountManagerService.class
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
    void completeCloningAccount(android.os.Bundle r1, android.accounts.Account r2, com.android.server.accounts.AccountManagerService.UserAccounts r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.completeCloningAccount(android.os.Bundle, android.accounts.Account, com.android.server.accounts.AccountManagerService$UserAccounts):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.completeCloningAccount(android.os.Bundle, android.accounts.Account, com.android.server.accounts.AccountManagerService$UserAccounts):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.addAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, android.os.Bundle, boolean):boolean, file: AccountManagerService.class
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
    private boolean addAccountInternal(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2, java.lang.String r3, android.os.Bundle r4, boolean r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.addAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, android.os.Bundle, boolean):boolean, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.addAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, android.os.Bundle, boolean):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.hasFeatures(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String[]):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void hasFeatures(android.accounts.IAccountManagerResponse r1, android.accounts.Account r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.hasFeatures(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String[]):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.hasFeatures(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.removeAccount(android.accounts.IAccountManagerResponse, android.accounts.Account):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void removeAccount(android.accounts.IAccountManagerResponse r1, android.accounts.Account r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.removeAccount(android.accounts.IAccountManagerResponse, android.accounts.Account):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.removeAccount(android.accounts.IAccountManagerResponse, android.accounts.Account):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.removeAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):void, file: AccountManagerService.class
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
    public void removeAccountInternal(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.removeAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.removeAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.invalidateAuthToken(java.lang.String, java.lang.String):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void invalidateAuthToken(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.invalidateAuthToken(java.lang.String, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.invalidateAuthToken(java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.invalidateAuthTokenLocked(com.android.server.accounts.AccountManagerService$UserAccounts, android.database.sqlite.SQLiteDatabase, java.lang.String, java.lang.String):void, file: AccountManagerService.class
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
    private void invalidateAuthTokenLocked(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.database.sqlite.SQLiteDatabase r2, java.lang.String r3, java.lang.String r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.invalidateAuthTokenLocked(com.android.server.accounts.AccountManagerService$UserAccounts, android.database.sqlite.SQLiteDatabase, java.lang.String, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.invalidateAuthTokenLocked(com.android.server.accounts.AccountManagerService$UserAccounts, android.database.sqlite.SQLiteDatabase, java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.saveAuthTokenToDatabase(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):boolean, file: AccountManagerService.class
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
    public boolean saveAuthTokenToDatabase(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2, java.lang.String r3, java.lang.String r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.saveAuthTokenToDatabase(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):boolean, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.saveAuthTokenToDatabase(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.peekAuthToken(android.accounts.Account, java.lang.String):java.lang.String, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public java.lang.String peekAuthToken(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.peekAuthToken(android.accounts.Account, java.lang.String):java.lang.String, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.peekAuthToken(android.accounts.Account, java.lang.String):java.lang.String");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setAuthToken(android.accounts.Account, java.lang.String, java.lang.String):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void setAuthToken(android.accounts.Account r1, java.lang.String r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setAuthToken(android.accounts.Account, java.lang.String, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.setAuthToken(android.accounts.Account, java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setPassword(android.accounts.Account, java.lang.String):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void setPassword(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setPassword(android.accounts.Account, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.setPassword(android.accounts.Account, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String):void, file: AccountManagerService.class
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
    private void setPasswordInternal(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.setPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.clearPassword(android.accounts.Account):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void clearPassword(android.accounts.Account r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.clearPassword(android.accounts.Account):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.clearPassword(android.accounts.Account):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setUserData(android.accounts.Account, java.lang.String, java.lang.String):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void setUserData(android.accounts.Account r1, java.lang.String r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setUserData(android.accounts.Account, java.lang.String, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.setUserData(android.accounts.Account, java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setUserdataInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):void, file: AccountManagerService.class
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
    private void setUserdataInternal(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2, java.lang.String r3, java.lang.String r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.setUserdataInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.setUserdataInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAuthTokenLabel(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void getAuthTokenLabel(android.accounts.IAccountManagerResponse r1, java.lang.String r2, java.lang.String r3) throws android.os.RemoteException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAuthTokenLabel(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAuthTokenLabel(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAuthToken(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String, boolean, boolean, android.os.Bundle):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void getAuthToken(android.accounts.IAccountManagerResponse r1, android.accounts.Account r2, java.lang.String r3, boolean r4, boolean r5, android.os.Bundle r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAuthToken(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String, boolean, boolean, android.os.Bundle):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAuthToken(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String, boolean, boolean, android.os.Bundle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.addAccount(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String, java.lang.String[], boolean, android.os.Bundle):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void addAccount(android.accounts.IAccountManagerResponse r1, java.lang.String r2, java.lang.String r3, java.lang.String[] r4, boolean r5, android.os.Bundle r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.addAccount(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String, java.lang.String[], boolean, android.os.Bundle):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.addAccount(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String, java.lang.String[], boolean, android.os.Bundle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.confirmCredentialsAsUser(android.accounts.IAccountManagerResponse, android.accounts.Account, android.os.Bundle, boolean, int):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void confirmCredentialsAsUser(android.accounts.IAccountManagerResponse r1, android.accounts.Account r2, android.os.Bundle r3, boolean r4, int r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.confirmCredentialsAsUser(android.accounts.IAccountManagerResponse, android.accounts.Account, android.os.Bundle, boolean, int):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.confirmCredentialsAsUser(android.accounts.IAccountManagerResponse, android.accounts.Account, android.os.Bundle, boolean, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.updateCredentials(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String, boolean, android.os.Bundle):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void updateCredentials(android.accounts.IAccountManagerResponse r1, android.accounts.Account r2, java.lang.String r3, boolean r4, android.os.Bundle r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.updateCredentials(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String, boolean, android.os.Bundle):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.updateCredentials(android.accounts.IAccountManagerResponse, android.accounts.Account, java.lang.String, boolean, android.os.Bundle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.editProperties(android.accounts.IAccountManagerResponse, java.lang.String, boolean):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void editProperties(android.accounts.IAccountManagerResponse r1, java.lang.String r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.editProperties(android.accounts.IAccountManagerResponse, java.lang.String, boolean):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.editProperties(android.accounts.IAccountManagerResponse, java.lang.String, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccounts(int):android.accounts.Account[], file: AccountManagerService.class
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
    public android.accounts.Account[] getAccounts(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccounts(int):android.accounts.Account[], file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAccounts(int):android.accounts.Account[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccountsAsUser(java.lang.String, int, java.lang.String, int):android.accounts.Account[], file: AccountManagerService.class
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
    private android.accounts.Account[] getAccountsAsUser(java.lang.String r1, int r2, java.lang.String r3, int r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccountsAsUser(java.lang.String, int, java.lang.String, int):android.accounts.Account[], file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAccountsAsUser(java.lang.String, int, java.lang.String, int):android.accounts.Account[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getSharedAccountsAsUser(int):android.accounts.Account[], file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public android.accounts.Account[] getSharedAccountsAsUser(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getSharedAccountsAsUser(int):android.accounts.Account[], file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getSharedAccountsAsUser(int):android.accounts.Account[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccountsByFeatures(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String[]):void, file: AccountManagerService.class
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
    @Override // android.accounts.IAccountManager
    public void getAccountsByFeatures(android.accounts.IAccountManagerResponse r1, java.lang.String r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccountsByFeatures(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String[]):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAccountsByFeatures(android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccountIdLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):long, file: AccountManagerService.class
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
    private long getAccountIdLocked(android.database.sqlite.SQLiteDatabase r1, android.accounts.Account r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getAccountIdLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):long, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getAccountIdLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):long");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getExtrasIdLocked(android.database.sqlite.SQLiteDatabase, long, java.lang.String):long, file: AccountManagerService.class
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
    private long getExtrasIdLocked(android.database.sqlite.SQLiteDatabase r1, long r2, java.lang.String r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.getExtrasIdLocked(android.database.sqlite.SQLiteDatabase, long, java.lang.String):long, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.getExtrasIdLocked(android.database.sqlite.SQLiteDatabase, long, java.lang.String):long");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.dumpUser(com.android.server.accounts.AccountManagerService$UserAccounts, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], boolean):void, file: AccountManagerService.class
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
    private void dumpUser(com.android.server.accounts.AccountManagerService.UserAccounts r1, java.io.FileDescriptor r2, java.io.PrintWriter r3, java.lang.String[] r4, boolean r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.dumpUser(com.android.server.accounts.AccountManagerService$UserAccounts, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], boolean):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.dumpUser(com.android.server.accounts.AccountManagerService$UserAccounts, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.doNotification(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.CharSequence, android.content.Intent, int):void, file: AccountManagerService.class
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
    public void doNotification(com.android.server.accounts.AccountManagerService.UserAccounts r1, android.accounts.Account r2, java.lang.CharSequence r3, android.content.Intent r4, int r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.doNotification(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.CharSequence, android.content.Intent, int):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.doNotification(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.CharSequence, android.content.Intent, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.cancelNotification(int, android.os.UserHandle):void, file: AccountManagerService.class
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
    protected void cancelNotification(int r1, android.os.UserHandle r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.cancelNotification(int, android.os.UserHandle):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.cancelNotification(int, android.os.UserHandle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.grantAppPermission(android.accounts.Account, java.lang.String, int):void, file: AccountManagerService.class
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
    private void grantAppPermission(android.accounts.Account r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.grantAppPermission(android.accounts.Account, java.lang.String, int):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.grantAppPermission(android.accounts.Account, java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.revokeAppPermission(android.accounts.Account, java.lang.String, int):void, file: AccountManagerService.class
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
    private void revokeAppPermission(android.accounts.Account r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.revokeAppPermission(android.accounts.Account, java.lang.String, int):void, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.revokeAppPermission(android.accounts.Account, java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.readUserDataForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):java.util.HashMap<java.lang.String, java.lang.String>, file: AccountManagerService.class
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
    protected java.util.HashMap<java.lang.String, java.lang.String> readUserDataForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase r1, android.accounts.Account r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.readUserDataForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):java.util.HashMap<java.lang.String, java.lang.String>, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.readUserDataForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):java.util.HashMap");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.readAuthTokensForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):java.util.HashMap<java.lang.String, java.lang.String>, file: AccountManagerService.class
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
    protected java.util.HashMap<java.lang.String, java.lang.String> readAuthTokensForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase r1, android.accounts.Account r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.readAuthTokensForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):java.util.HashMap<java.lang.String, java.lang.String>, file: AccountManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.readAuthTokensForAccountFromDatabaseLocked(android.database.sqlite.SQLiteDatabase, android.accounts.Account):java.util.HashMap");
    }

    static /* synthetic */ Context access$1600(AccountManagerService x0) {
        return x0.mContext;
    }

    static {
        ACCOUNTS_CHANGED_INTENT.setFlags(67108864);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AccountManagerService$UserAccounts.class */
    public static class UserAccounts {
        private final int userId;
        private final DatabaseHelper openHelper;
        private final HashMap<Pair<Pair<Account, String>, Integer>, Integer> credentialsPermissionNotificationIds = new HashMap<>();
        private final HashMap<Account, Integer> signinRequiredNotificationIds = new HashMap<>();
        private final Object cacheLock = new Object();
        private final HashMap<String, Account[]> accountCache = new LinkedHashMap();
        private HashMap<Account, HashMap<String, String>> userDataCache = new HashMap<>();
        private HashMap<Account, HashMap<String, String>> authTokenCache = new HashMap<>();

        static /* synthetic */ Object access$300(UserAccounts x0) {
            return x0.cacheLock;
        }

        static /* synthetic */ DatabaseHelper access$400(UserAccounts x0) {
            return x0.openHelper;
        }

        static /* synthetic */ int access$500(UserAccounts x0) {
            return x0.userId;
        }

        static /* synthetic */ HashMap access$600(UserAccounts x0) {
            return x0.accountCache;
        }

        static /* synthetic */ HashMap access$700(UserAccounts x0) {
            return x0.userDataCache;
        }

        static /* synthetic */ HashMap access$800(UserAccounts x0) {
            return x0.authTokenCache;
        }

        static /* synthetic */ HashMap access$900(UserAccounts x0) {
            return x0.credentialsPermissionNotificationIds;
        }

        UserAccounts(Context context, int userId) {
            this.userId = userId;
            synchronized (this.cacheLock) {
                this.openHelper = new DatabaseHelper(context, userId);
            }
        }
    }

    public static AccountManagerService getSingleton() {
        return sThis.get();
    }

    public AccountManagerService(Context context) {
        this(context, context.getPackageManager(), new AccountAuthenticatorCache(context));
    }

    public AccountManagerService(Context context, PackageManager packageManager, IAccountAuthenticatorCache authenticatorCache) {
        this.mSessions = new LinkedHashMap<>();
        this.mNotificationIds = new AtomicInteger(1);
        this.mUsers = new SparseArray<>();
        this.mContext = context;
        this.mPackageManager = packageManager;
        this.mMessageHandler = new MessageHandler(FgThread.get().getLooper());
        this.mAuthenticatorCache = authenticatorCache;
        this.mAuthenticatorCache.setListener(this, null);
        sThis.set(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.accounts.AccountManagerService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context1, Intent intent) {
                AccountManagerService.this.purgeOldGrantsAll();
            }
        }, intentFilter);
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        userFilter.addAction(Intent.ACTION_USER_STARTED);
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.accounts.AccountManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_REMOVED.equals(action)) {
                    AccountManagerService.this.onUserRemoved(intent);
                } else if (Intent.ACTION_USER_STARTED.equals(action)) {
                    AccountManagerService.this.onUserStarted(intent);
                }
            }
        }, UserHandle.ALL, userFilter, null, null);
    }

    @Override // android.accounts.IAccountManager.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Account Manager Crash", e);
            }
            throw e;
        }
    }

    public void systemReady() {
    }

    private UserManager getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = UserManager.get(this.mContext);
        }
        return this.mUserManager;
    }

    private UserAccounts initUserLocked(int userId) {
        UserAccounts accounts = this.mUsers.get(userId);
        if (accounts == null) {
            accounts = new UserAccounts(this.mContext, userId);
            this.mUsers.append(userId, accounts);
            purgeOldGrants(accounts);
            validateAccountsInternal(accounts, true);
        }
        return accounts;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void purgeOldGrantsAll() {
        synchronized (this.mUsers) {
            for (int i = 0; i < this.mUsers.size(); i++) {
                purgeOldGrants(this.mUsers.valueAt(i));
            }
        }
    }

    public void validateAccounts(int userId) {
        UserAccounts accounts = getUserAccounts(userId);
        validateAccountsInternal(accounts, true);
    }

    private UserAccounts getUserAccountsForCaller() {
        return getUserAccounts(UserHandle.getCallingUserId());
    }

    protected UserAccounts getUserAccounts(int userId) {
        UserAccounts userAccounts;
        synchronized (this.mUsers) {
            UserAccounts accounts = this.mUsers.get(userId);
            if (accounts == null) {
                accounts = initUserLocked(userId);
                this.mUsers.append(userId, accounts);
            }
            userAccounts = accounts;
        }
        return userAccounts;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserRemoved(Intent intent) {
        UserAccounts accounts;
        int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
        if (userId < 1) {
            return;
        }
        synchronized (this.mUsers) {
            accounts = this.mUsers.get(userId);
            this.mUsers.remove(userId);
        }
        if (accounts != null) {
            synchronized (accounts.cacheLock) {
                accounts.openHelper.close();
                File dbFile = new File(getDatabaseName(userId));
                dbFile.delete();
            }
            return;
        }
        File dbFile2 = new File(getDatabaseName(userId));
        dbFile2.delete();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserStarted(Intent intent) {
        Account[] sharedAccounts;
        int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
        if (userId < 1 || (sharedAccounts = getSharedAccountsAsUser(userId)) == null || sharedAccounts.length == 0) {
            return;
        }
        Account[] accounts = getAccountsAsUser(null, userId);
        for (Account sa : sharedAccounts) {
            if (!ArrayUtils.contains(accounts, sa)) {
                copyAccountToUser(sa, 0, userId);
            }
        }
    }

    @Override // android.content.pm.RegisteredServicesCacheListener
    public void onServiceChanged(AuthenticatorDescription desc, int userId, boolean removed) {
        validateAccountsInternal(getUserAccounts(userId), false);
    }

    private void addAccountToLimitedUsers(Account account) {
        List<UserInfo> users = getUserManager().getUsers();
        for (UserInfo user : users) {
            if (user.isRestricted()) {
                addSharedAccountAsUser(account, user.id);
                try {
                    if (ActivityManagerNative.getDefault().isUserRunning(user.id, false)) {
                        this.mMessageHandler.sendMessage(this.mMessageHandler.obtainMessage(4, 0, user.id, account));
                    }
                } catch (RemoteException e) {
                }
            }
        }
    }

    private long insertExtraLocked(SQLiteDatabase db, long accountId, String key, String value) {
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("accounts_id", Long.valueOf(accountId));
        values.put("value", value);
        return db.insert(TABLE_EXTRAS, "key", values);
    }

    /* loaded from: AccountManagerService$TestFeaturesSession.class */
    private class TestFeaturesSession extends Session {
        private final String[] mFeatures;
        private final Account mAccount;

        public TestFeaturesSession(UserAccounts accounts, IAccountManagerResponse response, Account account, String[] features) {
            super(accounts, response, account.type, false, true);
            this.mFeatures = features;
            this.mAccount = account;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        public void run() throws RemoteException {
            try {
                this.mAuthenticator.hasFeatures(this, this.mAccount, this.mFeatures);
            } catch (RemoteException e) {
                onError(1, "remote exception");
            }
        }

        @Override // com.android.server.accounts.AccountManagerService.Session, android.accounts.IAccountAuthenticatorResponse
        public void onResult(Bundle result) {
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    if (result == null) {
                        response.onError(5, "null bundle");
                        return;
                    }
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                    }
                    Bundle newResult = new Bundle();
                    newResult.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT, false));
                    response.onResult(newResult);
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "failure while notifying response", e);
                    }
                }
            }
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        protected String toDebugString(long now) {
            return super.toDebugString(now) + ", hasFeatures, " + this.mAccount + ", " + (this.mFeatures != null ? TextUtils.join(Separators.COMMA, this.mFeatures) : null);
        }
    }

    /* loaded from: AccountManagerService$RemoveAccountSession.class */
    private class RemoveAccountSession extends Session {
        final Account mAccount;

        public RemoveAccountSession(UserAccounts accounts, IAccountManagerResponse response, Account account) {
            super(accounts, response, account.type, false, true);
            this.mAccount = account;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        protected String toDebugString(long now) {
            return super.toDebugString(now) + ", removeAccount, account " + this.mAccount;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        public void run() throws RemoteException {
            this.mAuthenticator.getAccountRemovalAllowed(this, this.mAccount);
        }

        @Override // com.android.server.accounts.AccountManagerService.Session, android.accounts.IAccountAuthenticatorResponse
        public void onResult(Bundle result) {
            if (result != null && result.containsKey(AccountManager.KEY_BOOLEAN_RESULT) && !result.containsKey("intent")) {
                boolean removalAllowed = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);
                if (removalAllowed) {
                    AccountManagerService.this.removeAccountInternal(this.mAccounts, this.mAccount);
                }
                IAccountManagerResponse response = getResponseAndClose();
                if (response != null) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                    }
                    Bundle result2 = new Bundle();
                    result2.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, removalAllowed);
                    try {
                        response.onResult(result2);
                    } catch (RemoteException e) {
                    }
                }
            }
            super.onResult(result);
        }
    }

    protected void removeAccountInternal(Account account) {
        removeAccountInternal(getUserAccountsForCaller(), account);
    }

    private void sendAccountsChangedBroadcast(int userId) {
        Log.i(TAG, "the accounts changed, sending broadcast of " + ACCOUNTS_CHANGED_INTENT.getAction());
        this.mContext.sendBroadcastAsUser(ACCOUNTS_CHANGED_INTENT, new UserHandle(userId));
    }

    private void onResult(IAccountManagerResponse response, Bundle result) {
        if (result == null) {
            Log.e(TAG, "the result is unexpectedly null", new Exception());
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
        }
        try {
            response.onResult(result);
        } catch (RemoteException e) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "failure while notifying response", e);
            }
        }
    }

    /* renamed from: com.android.server.accounts.AccountManagerService$6  reason: invalid class name */
    /* loaded from: AccountManagerService$6.class */
    class AnonymousClass6 extends Session {
        final /* synthetic */ Bundle val$loginOptions;
        final /* synthetic */ Account val$account;
        final /* synthetic */ String val$authTokenType;
        final /* synthetic */ boolean val$notifyOnAuthFailure;
        final /* synthetic */ boolean val$permissionGranted;
        final /* synthetic */ int val$callerUid;
        final /* synthetic */ boolean val$customTokens;
        final /* synthetic */ UserAccounts val$accounts;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass6(UserAccounts x0, IAccountManagerResponse x1, String x2, boolean x3, boolean x4, Bundle bundle, Account account, String str, boolean z, boolean z2, int i, boolean z3, UserAccounts userAccounts) {
            super(x0, x1, x2, x3, x4);
            this.val$loginOptions = bundle;
            this.val$account = account;
            this.val$authTokenType = str;
            this.val$notifyOnAuthFailure = z;
            this.val$permissionGranted = z2;
            this.val$callerUid = i;
            this.val$customTokens = z3;
            this.val$accounts = userAccounts;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        protected String toDebugString(long now) {
            if (this.val$loginOptions != null) {
                this.val$loginOptions.keySet();
            }
            return super.toDebugString(now) + ", getAuthToken, " + this.val$account + ", authTokenType " + this.val$authTokenType + ", loginOptions " + this.val$loginOptions + ", notifyOnAuthFailure " + this.val$notifyOnAuthFailure;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        public void run() throws RemoteException {
            if (!this.val$permissionGranted) {
                this.mAuthenticator.getAuthTokenLabel(this, this.val$authTokenType);
            } else {
                this.mAuthenticator.getAuthToken(this, this.val$account, this.val$authTokenType, this.val$loginOptions);
            }
        }

        @Override // com.android.server.accounts.AccountManagerService.Session, android.accounts.IAccountAuthenticatorResponse
        public void onResult(Bundle result) {
            if (result != null) {
                if (result.containsKey(AccountManager.KEY_AUTH_TOKEN_LABEL)) {
                    Intent intent = AccountManagerService.this.newGrantCredentialsPermissionIntent(this.val$account, this.val$callerUid, new AccountAuthenticatorResponse(this), this.val$authTokenType, result.getString(AccountManager.KEY_AUTH_TOKEN_LABEL));
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("intent", intent);
                    onResult(bundle);
                    return;
                }
                String authToken = result.getString("authtoken");
                if (authToken != null) {
                    String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    String type = result.getString("accountType");
                    if (TextUtils.isEmpty(type) || TextUtils.isEmpty(name)) {
                        onError(5, "the type and name should not be empty");
                        return;
                    } else if (!this.val$customTokens) {
                        AccountManagerService.this.saveAuthTokenToDatabase(this.mAccounts, new Account(name, type), this.val$authTokenType, authToken);
                    }
                }
                Intent intent2 = (Intent) result.getParcelable("intent");
                if (intent2 != null && this.val$notifyOnAuthFailure && !this.val$customTokens) {
                    AccountManagerService.this.doNotification(this.mAccounts, this.val$account, result.getString(AccountManager.KEY_AUTH_FAILED_MESSAGE), intent2, this.val$accounts.userId);
                }
            }
            super.onResult(result);
        }
    }

    private void createNoCredentialsPermissionNotification(Account account, Intent intent, int userId) {
        int uid = intent.getIntExtra("uid", -1);
        String authTokenType = intent.getStringExtra("authTokenType");
        intent.getStringExtra(GrantCredentialsPermissionActivity.EXTRAS_AUTH_TOKEN_LABEL);
        Notification n = new Notification(17301642, null, 0L);
        String titleAndSubtitle = this.mContext.getString(R.string.permission_request_notification_with_subtitle, account.name);
        int index = titleAndSubtitle.indexOf(10);
        String title = titleAndSubtitle;
        String subtitle = "";
        if (index > 0) {
            title = titleAndSubtitle.substring(0, index);
            subtitle = titleAndSubtitle.substring(index + 1);
        }
        UserHandle user = new UserHandle(userId);
        n.setLatestEventInfo(this.mContext, title, subtitle, PendingIntent.getActivityAsUser(this.mContext, 0, intent, 268435456, null, user));
        installNotification(getCredentialPermissionNotificationId(account, authTokenType, uid).intValue(), n, user);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Intent newGrantCredentialsPermissionIntent(Account account, int uid, AccountAuthenticatorResponse response, String authTokenType, String authTokenLabel) {
        Intent intent = new Intent(this.mContext, GrantCredentialsPermissionActivity.class);
        intent.setFlags(268435456);
        intent.addCategory(String.valueOf(getCredentialPermissionNotificationId(account, authTokenType, uid)));
        intent.putExtra("account", account);
        intent.putExtra("authTokenType", authTokenType);
        intent.putExtra("response", response);
        intent.putExtra("uid", uid);
        return intent;
    }

    private Integer getCredentialPermissionNotificationId(Account account, String authTokenType, int uid) {
        Integer id;
        UserAccounts accounts = getUserAccounts(UserHandle.getUserId(uid));
        synchronized (accounts.credentialsPermissionNotificationIds) {
            Pair<Pair<Account, String>, Integer> key = new Pair<>(new Pair(account, authTokenType), Integer.valueOf(uid));
            id = (Integer) accounts.credentialsPermissionNotificationIds.get(key);
            if (id == null) {
                id = Integer.valueOf(this.mNotificationIds.incrementAndGet());
                accounts.credentialsPermissionNotificationIds.put(key, id);
            }
        }
        return id;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Integer getSigninRequiredNotificationId(UserAccounts accounts, Account account) {
        Integer id;
        synchronized (accounts.signinRequiredNotificationIds) {
            id = (Integer) accounts.signinRequiredNotificationIds.get(account);
            if (id == null) {
                id = Integer.valueOf(this.mNotificationIds.incrementAndGet());
                accounts.signinRequiredNotificationIds.put(account, id);
            }
        }
        return id;
    }

    /* renamed from: com.android.server.accounts.AccountManagerService$7  reason: invalid class name */
    /* loaded from: AccountManagerService$7.class */
    class AnonymousClass7 extends Session {
        final /* synthetic */ String val$authTokenType;
        final /* synthetic */ String[] val$requiredFeatures;
        final /* synthetic */ Bundle val$options;
        final /* synthetic */ String val$accountType;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass7(UserAccounts x0, IAccountManagerResponse x1, String x2, boolean x3, boolean x4, String str, String[] strArr, Bundle bundle, String str2) {
            super(x0, x1, x2, x3, x4);
            this.val$authTokenType = str;
            this.val$requiredFeatures = strArr;
            this.val$options = bundle;
            this.val$accountType = str2;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        public void run() throws RemoteException {
            this.mAuthenticator.addAccount(this, this.mAccountType, this.val$authTokenType, this.val$requiredFeatures, this.val$options);
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        protected String toDebugString(long now) {
            return super.toDebugString(now) + ", addAccount, accountType " + this.val$accountType + ", requiredFeatures " + (this.val$requiredFeatures != null ? TextUtils.join(Separators.COMMA, this.val$requiredFeatures) : null);
        }
    }

    /* loaded from: AccountManagerService$GetAccountsByTypeAndFeatureSession.class */
    private class GetAccountsByTypeAndFeatureSession extends Session {
        private final String[] mFeatures;
        private volatile Account[] mAccountsOfType;
        private volatile ArrayList<Account> mAccountsWithFeatures;
        private volatile int mCurrentAccount;
        private int mCallingUid;

        public GetAccountsByTypeAndFeatureSession(UserAccounts accounts, IAccountManagerResponse response, String type, String[] features, int callingUid) {
            super(accounts, response, type, false, true);
            this.mAccountsOfType = null;
            this.mAccountsWithFeatures = null;
            this.mCurrentAccount = 0;
            this.mCallingUid = callingUid;
            this.mFeatures = features;
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        public void run() throws RemoteException {
            synchronized (this.mAccounts.cacheLock) {
                this.mAccountsOfType = AccountManagerService.this.getAccountsFromCacheLocked(this.mAccounts, this.mAccountType, this.mCallingUid, null);
            }
            this.mAccountsWithFeatures = new ArrayList<>(this.mAccountsOfType.length);
            this.mCurrentAccount = 0;
            checkAccount();
        }

        public void checkAccount() {
            if (this.mCurrentAccount >= this.mAccountsOfType.length) {
                sendResult();
                return;
            }
            IAccountAuthenticator accountAuthenticator = this.mAuthenticator;
            if (accountAuthenticator == null) {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, "checkAccount: aborting session since we are no longer connected to the authenticator, " + toDebugString());
                    return;
                }
                return;
            }
            try {
                accountAuthenticator.hasFeatures(this, this.mAccountsOfType[this.mCurrentAccount], this.mFeatures);
            } catch (RemoteException e) {
                onError(1, "remote exception");
            }
        }

        @Override // com.android.server.accounts.AccountManagerService.Session, android.accounts.IAccountAuthenticatorResponse
        public void onResult(Bundle result) {
            this.mNumResults++;
            if (result == null) {
                onError(5, "null bundle");
                return;
            }
            if (result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)) {
                this.mAccountsWithFeatures.add(this.mAccountsOfType[this.mCurrentAccount]);
            }
            this.mCurrentAccount++;
            checkAccount();
        }

        public void sendResult() {
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    Account[] accounts = new Account[this.mAccountsWithFeatures.size()];
                    for (int i = 0; i < accounts.length; i++) {
                        accounts[i] = this.mAccountsWithFeatures.get(i);
                    }
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                    }
                    Bundle result = new Bundle();
                    result.putParcelableArray("accounts", accounts);
                    response.onResult(result);
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "failure while notifying response", e);
                    }
                }
            }
        }

        @Override // com.android.server.accounts.AccountManagerService.Session
        protected String toDebugString(long now) {
            return super.toDebugString(now) + ", getAccountsByTypeAndFeatures, " + (this.mFeatures != null ? TextUtils.join(Separators.COMMA, this.mFeatures) : null);
        }
    }

    public AccountAndUser[] getRunningAccounts() {
        try {
            int[] runningUserIds = ActivityManagerNative.getDefault().getRunningUserIds();
            return getAccounts(runningUserIds);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountAndUser[] getAllAccounts() {
        List<UserInfo> users = getUserManager().getUsers();
        int[] userIds = new int[users.size()];
        for (int i = 0; i < userIds.length; i++) {
            userIds[i] = users.get(i).id;
        }
        return getAccounts(userIds);
    }

    private AccountAndUser[] getAccounts(int[] userIds) {
        ArrayList<AccountAndUser> runningAccounts = Lists.newArrayList();
        for (int userId : userIds) {
            UserAccounts userAccounts = getUserAccounts(userId);
            if (userAccounts != null) {
                synchronized (userAccounts.cacheLock) {
                    Account[] accounts = getAccountsFromCacheLocked(userAccounts, null, Binder.getCallingUid(), null);
                    for (Account account : accounts) {
                        runningAccounts.add(new AccountAndUser(account, userId));
                    }
                }
            }
        }
        AccountAndUser[] accountsArray = new AccountAndUser[runningAccounts.size()];
        return (AccountAndUser[]) runningAccounts.toArray(accountsArray);
    }

    @Override // android.accounts.IAccountManager
    public Account[] getAccountsAsUser(String type, int userId) {
        return getAccountsAsUser(type, userId, null, -1);
    }

    @Override // android.accounts.IAccountManager
    public boolean addSharedAccountAsUser(Account account, int userId) {
        SQLiteDatabase db = getUserAccounts(handleIncomingUser(userId)).openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", account.name);
        values.put("type", account.type);
        db.delete(TABLE_SHARED_ACCOUNTS, "name=? AND type=?", new String[]{account.name, account.type});
        long accountId = db.insert(TABLE_SHARED_ACCOUNTS, "name", values);
        if (accountId < 0) {
            Log.w(TAG, "insertAccountIntoDatabase: " + account + ", skipping the DB insert failed");
            return false;
        }
        return true;
    }

    @Override // android.accounts.IAccountManager
    public boolean removeSharedAccountAsUser(Account account, int userId) {
        UserAccounts accounts = getUserAccounts(handleIncomingUser(userId));
        SQLiteDatabase db = accounts.openHelper.getWritableDatabase();
        int r = db.delete(TABLE_SHARED_ACCOUNTS, "name=? AND type=?", new String[]{account.name, account.type});
        if (r > 0) {
            removeAccountInternal(accounts, account);
        }
        return r > 0;
    }

    @Override // android.accounts.IAccountManager
    public Account[] getAccounts(String type) {
        return getAccountsAsUser(type, UserHandle.getCallingUserId());
    }

    @Override // android.accounts.IAccountManager
    public Account[] getAccountsForPackage(String packageName, int uid) {
        int callingUid = Binder.getCallingUid();
        if (!UserHandle.isSameApp(callingUid, Process.myUid())) {
            throw new SecurityException("getAccountsForPackage() called from unauthorized uid " + callingUid + " with uid=" + uid);
        }
        return getAccountsAsUser(null, UserHandle.getCallingUserId(), packageName, uid);
    }

    @Override // android.accounts.IAccountManager
    public Account[] getAccountsByTypeForPackage(String type, String packageName) {
        checkBinderPermission(Manifest.permission.INTERACT_ACROSS_USERS);
        try {
            int packageUid = AppGlobals.getPackageManager().getPackageUid(packageName, UserHandle.getCallingUserId());
            return getAccountsAsUser(type, UserHandle.getCallingUserId(), packageName, packageUid);
        } catch (RemoteException re) {
            Slog.e(TAG, "Couldn't determine the packageUid for " + packageName + re);
            return new Account[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccountManagerService$Session.class */
    public abstract class Session extends IAccountAuthenticatorResponse.Stub implements IBinder.DeathRecipient, ServiceConnection {
        IAccountManagerResponse mResponse;
        final String mAccountType;
        final boolean mExpectActivityLaunch;
        final long mCreationTime;
        public int mNumResults = 0;
        private int mNumRequestContinued = 0;
        private int mNumErrors = 0;
        IAccountAuthenticator mAuthenticator = null;
        private final boolean mStripAuthTokenFromResult;
        protected final UserAccounts mAccounts;

        public abstract void run() throws RemoteException;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.Session.onResult(android.os.Bundle):void, file: AccountManagerService$Session.class
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
        public void onResult(android.os.Bundle r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountManagerService.Session.onResult(android.os.Bundle):void, file: AccountManagerService$Session.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.Session.onResult(android.os.Bundle):void");
        }

        public Session(UserAccounts accounts, IAccountManagerResponse response, String accountType, boolean expectActivityLaunch, boolean stripAuthTokenFromResult) {
            if (accountType == null) {
                throw new IllegalArgumentException("accountType is null");
            }
            this.mAccounts = accounts;
            this.mStripAuthTokenFromResult = stripAuthTokenFromResult;
            this.mResponse = response;
            this.mAccountType = accountType;
            this.mExpectActivityLaunch = expectActivityLaunch;
            this.mCreationTime = SystemClock.elapsedRealtime();
            synchronized (AccountManagerService.this.mSessions) {
                AccountManagerService.this.mSessions.put(toString(), this);
            }
            if (response != null) {
                try {
                    response.asBinder().linkToDeath(this, 0);
                } catch (RemoteException e) {
                    this.mResponse = null;
                    binderDied();
                }
            }
        }

        IAccountManagerResponse getResponseAndClose() {
            if (this.mResponse == null) {
                return null;
            }
            IAccountManagerResponse response = this.mResponse;
            close();
            return response;
        }

        private void close() {
            synchronized (AccountManagerService.this.mSessions) {
                if (AccountManagerService.this.mSessions.remove(toString()) == 0) {
                    return;
                }
                if (this.mResponse != null) {
                    this.mResponse.asBinder().unlinkToDeath(this, 0);
                    this.mResponse = null;
                }
                cancelTimeout();
                unbind();
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            this.mResponse = null;
            close();
        }

        protected String toDebugString() {
            return toDebugString(SystemClock.elapsedRealtime());
        }

        protected String toDebugString(long now) {
            return "Session: expectLaunch " + this.mExpectActivityLaunch + ", connected " + (this.mAuthenticator != null) + ", stats (" + this.mNumResults + Separators.SLASH + this.mNumRequestContinued + Separators.SLASH + this.mNumErrors + Separators.RPAREN + ", lifetime " + ((now - this.mCreationTime) / 1000.0d);
        }

        void bind() {
            if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "initiating bind to authenticator type " + this.mAccountType);
            }
            if (!bindToAuthenticator(this.mAccountType)) {
                Log.d(AccountManagerService.TAG, "bind attempt failed for " + toDebugString());
                onError(1, "bind failure");
            }
        }

        private void unbind() {
            if (this.mAuthenticator != null) {
                this.mAuthenticator = null;
                AccountManagerService.this.mContext.unbindService(this);
            }
        }

        public void scheduleTimeout() {
            AccountManagerService.this.mMessageHandler.sendMessageDelayed(AccountManagerService.this.mMessageHandler.obtainMessage(3, this), DateUtils.MINUTE_IN_MILLIS);
        }

        public void cancelTimeout() {
            AccountManagerService.this.mMessageHandler.removeMessages(3, this);
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mAuthenticator = IAccountAuthenticator.Stub.asInterface(service);
            try {
                run();
            } catch (RemoteException e) {
                onError(1, "remote exception");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            this.mAuthenticator = null;
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    response.onError(1, "disconnected");
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "Session.onServiceDisconnected: caught RemoteException while responding", e);
                    }
                }
            }
        }

        public void onTimedOut() {
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    response.onError(1, GpsNetInitiatedHandler.NI_INTENT_KEY_TIMEOUT);
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "Session.onTimedOut: caught RemoteException while responding", e);
                    }
                }
            }
        }

        @Override // android.accounts.IAccountAuthenticatorResponse
        public void onRequestContinued() {
            this.mNumRequestContinued++;
        }

        public void onError(int errorCode, String errorMessage) {
            this.mNumErrors++;
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onError() on response " + response);
                }
                try {
                    response.onError(errorCode, errorMessage);
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "Session.onError: caught RemoteException while responding", e);
                    }
                }
            } else if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "Session.onError: already closed");
            }
        }

        private boolean bindToAuthenticator(String authenticatorType) {
            RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> authenticatorInfo = AccountManagerService.this.mAuthenticatorCache.getServiceInfo(AuthenticatorDescription.newKey(authenticatorType), this.mAccounts.userId);
            if (authenticatorInfo == null) {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, "there is no authenticator for " + authenticatorType + ", bailing out");
                    return false;
                }
                return false;
            }
            Intent intent = new Intent();
            intent.setAction("android.accounts.AccountAuthenticator");
            intent.setComponent(authenticatorInfo.componentName);
            if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "performing bindService to " + authenticatorInfo.componentName);
            }
            if (!AccountManagerService.this.mContext.bindServiceAsUser(intent, this, 1, new UserHandle(this.mAccounts.userId))) {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, "bindService to " + authenticatorInfo.componentName + " failed");
                    return false;
                }
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccountManagerService$MessageHandler.class */
    public class MessageHandler extends Handler {
        MessageHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    Session session = (Session) msg.obj;
                    session.onTimedOut();
                    return;
                case 4:
                    AccountManagerService.this.copyAccountToUser((Account) msg.obj, msg.arg1, msg.arg2);
                    return;
                default:
                    throw new IllegalStateException("unhandled message: " + msg.what);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getDatabaseName(int userId) {
        File systemDir = Environment.getSystemSecureDirectory();
        File databaseFile = new File(Environment.getUserSystemDirectory(userId), DATABASE_NAME);
        if (userId == 0) {
            File oldFile = new File(systemDir, DATABASE_NAME);
            if (oldFile.exists() && !databaseFile.exists()) {
                File userDir = Environment.getUserSystemDirectory(userId);
                if (!userDir.exists() && !userDir.mkdirs()) {
                    throw new IllegalStateException("User dir cannot be created: " + userDir);
                }
                if (!oldFile.renameTo(databaseFile)) {
                    throw new IllegalStateException("User dir cannot be migrated: " + databaseFile);
                }
            }
        }
        return databaseFile.getPath();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AccountManagerService$DatabaseHelper.class */
    public static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, int userId) {
            super(context, AccountManagerService.getDatabaseName(userId), null, 5);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, password TEXT, UNIQUE(name,type))");
            db.execSQL("CREATE TABLE authtokens (  _id INTEGER PRIMARY KEY AUTOINCREMENT,  accounts_id INTEGER NOT NULL, type TEXT NOT NULL,  authtoken TEXT,  UNIQUE (accounts_id,type))");
            createGrantsTable(db);
            db.execSQL("CREATE TABLE extras ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accounts_id INTEGER, key TEXT NOT NULL, value TEXT, UNIQUE(accounts_id,key))");
            db.execSQL("CREATE TABLE meta ( key TEXT PRIMARY KEY NOT NULL, value TEXT)");
            createSharedAccountsTable(db);
            createAccountsDeletionTrigger(db);
        }

        private void createSharedAccountsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE shared_accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, UNIQUE(name,type))");
        }

        private void createAccountsDeletionTrigger(SQLiteDatabase db) {
            db.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM authtokens     WHERE accounts_id=OLD._id ;   DELETE FROM extras     WHERE accounts_id=OLD._id ;   DELETE FROM grants     WHERE accounts_id=OLD._id ; END");
        }

        private void createGrantsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE grants (  accounts_id INTEGER NOT NULL, auth_token_type STRING NOT NULL,  uid INTEGER NOT NULL,  UNIQUE (accounts_id,auth_token_type,uid))");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(AccountManagerService.TAG, "upgrade from version " + oldVersion + " to version " + newVersion);
            if (oldVersion == 1) {
                oldVersion++;
            }
            if (oldVersion == 2) {
                createGrantsTable(db);
                db.execSQL("DROP TRIGGER accountsDelete");
                createAccountsDeletionTrigger(db);
                oldVersion++;
            }
            if (oldVersion == 3) {
                db.execSQL("UPDATE accounts SET type = 'com.google' WHERE type == 'com.google.GAIA'");
                oldVersion++;
            }
            if (oldVersion == 4) {
                createSharedAccountsTable(db);
                oldVersion++;
            }
            if (oldVersion != newVersion) {
                Log.e(AccountManagerService.TAG, "failed to upgrade version " + oldVersion + " to version " + newVersion);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onOpen(SQLiteDatabase db) {
            if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "opened database accounts.db");
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return asBinder();
    }

    private static boolean scanArgs(String[] args, String value) {
        if (args != null) {
            for (String arg : args) {
                if (value.equals(arg)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            fout.println("Permission Denial: can't dump AccountsManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
            return;
        }
        boolean isCheckinRequest = scanArgs(args, "--checkin") || scanArgs(args, "-c");
        IndentingPrintWriter ipw = new IndentingPrintWriter(fout, "  ");
        List<UserInfo> users = getUserManager().getUsers();
        for (UserInfo user : users) {
            ipw.println("User " + user + Separators.COLON);
            ipw.increaseIndent();
            dumpUser(getUserAccounts(user.id), fd, ipw, args, isCheckinRequest);
            ipw.println();
            ipw.decreaseIndent();
        }
    }

    protected void installNotification(int notificationId, Notification n, UserHandle user) {
        ((NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notifyAsUser(null, notificationId, n, user);
    }

    private void checkBinderPermission(String... permissions) {
        int uid = Binder.getCallingUid();
        for (String perm : permissions) {
            if (this.mContext.checkCallingOrSelfPermission(perm) == 0) {
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, "  caller uid " + uid + " has " + perm);
                    return;
                } else {
                    return;
                }
            }
        }
        String msg = "caller uid " + uid + " lacks any of " + TextUtils.join(Separators.COMMA, permissions);
        Log.w(TAG, "  " + msg);
        throw new SecurityException(msg);
    }

    private int handleIncomingUser(int userId) {
        try {
            return ActivityManagerNative.getDefault().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, true, "", null);
        } catch (RemoteException e) {
            return userId;
        }
    }

    private boolean isPrivileged(int callingUid) {
        int callingUserId = UserHandle.getUserId(callingUid);
        try {
            PackageManager userPackageManager = this.mContext.createPackageContextAsUser("android", 0, new UserHandle(callingUserId)).getPackageManager();
            String[] packages = userPackageManager.getPackagesForUid(callingUid);
            for (String name : packages) {
                try {
                    PackageInfo packageInfo = userPackageManager.getPackageInfo(name, 0);
                    if (packageInfo != null && (packageInfo.applicationInfo.flags & 1073741824) != 0) {
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return false;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e2) {
            return false;
        }
    }

    private boolean permissionIsGranted(Account account, String authTokenType, int callerUid) {
        boolean isPrivileged = isPrivileged(callerUid);
        boolean fromAuthenticator = account != null && hasAuthenticatorUid(account.type, callerUid);
        boolean hasExplicitGrants = account != null && hasExplicitlyGrantedPermission(account, authTokenType, callerUid);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "checkGrantsOrCallingUidAgainstAuthenticator: caller uid " + callerUid + ", " + account + ": is authenticator? " + fromAuthenticator + ", has explicit permission? " + hasExplicitGrants);
        }
        return fromAuthenticator || hasExplicitGrants || isPrivileged;
    }

    private boolean hasAuthenticatorUid(String accountType, int callingUid) {
        int callingUserId = UserHandle.getUserId(callingUid);
        for (RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo : this.mAuthenticatorCache.getAllServices(callingUserId)) {
            if (serviceInfo.type.type.equals(accountType)) {
                return serviceInfo.uid == callingUid || this.mPackageManager.checkSignatures(serviceInfo.uid, callingUid) == 0;
            }
        }
        return false;
    }

    private boolean hasExplicitlyGrantedPermission(Account account, String authTokenType, int callerUid) {
        if (callerUid == 1000) {
            return true;
        }
        UserAccounts accounts = getUserAccountsForCaller();
        synchronized (accounts.cacheLock) {
            SQLiteDatabase db = accounts.openHelper.getReadableDatabase();
            String[] args = {String.valueOf(callerUid), authTokenType, account.name, account.type};
            boolean permissionGranted = DatabaseUtils.longForQuery(db, COUNT_OF_MATCHING_GRANTS, args) != 0;
            if (!permissionGranted && ActivityManager.isRunningInTestHarness()) {
                Log.d(TAG, "no credentials permission for usage of " + account + ", " + authTokenType + " by uid " + callerUid + " but ignoring since device is in test harness.");
                return true;
            }
            return permissionGranted;
        }
    }

    private void checkCallingUidAgainstAuthenticator(Account account) {
        int uid = Binder.getCallingUid();
        if (account == null || !hasAuthenticatorUid(account.type, uid)) {
            String msg = "caller uid " + uid + " is different than the authenticator's uid";
            Log.w(TAG, msg);
            throw new SecurityException(msg);
        } else if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "caller uid " + uid + " is the same as the authenticator's uid");
        }
    }

    private void checkAuthenticateAccountsPermission(Account account) {
        checkBinderPermission(Manifest.permission.AUTHENTICATE_ACCOUNTS);
        checkCallingUidAgainstAuthenticator(account);
    }

    private void checkReadAccountsPermission() {
        checkBinderPermission(Manifest.permission.GET_ACCOUNTS);
    }

    private void checkManageAccountsPermission() {
        checkBinderPermission(Manifest.permission.MANAGE_ACCOUNTS);
    }

    private void checkManageAccountsOrUseCredentialsPermissions() {
        checkBinderPermission(Manifest.permission.MANAGE_ACCOUNTS, Manifest.permission.USE_CREDENTIALS);
    }

    private boolean canUserModifyAccounts(int callingUid) {
        if (callingUid != Process.myUid() && getUserManager().getUserRestrictions(new UserHandle(UserHandle.getUserId(callingUid))).getBoolean(UserManager.DISALLOW_MODIFY_ACCOUNTS)) {
            return false;
        }
        return true;
    }

    @Override // android.accounts.IAccountManager
    public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) throws RemoteException {
        int callingUid = getCallingUid();
        if (callingUid != 1000) {
            throw new SecurityException();
        }
        if (value) {
            grantAppPermission(account, authTokenType, uid);
        } else {
            revokeAppPermission(account, authTokenType, uid);
        }
    }

    private static final String stringArrayToString(String[] value) {
        if (value != null) {
            return "[" + TextUtils.join(Separators.COMMA, value) + "]";
        }
        return null;
    }

    private void removeAccountFromCacheLocked(UserAccounts accounts, Account account) {
        Account[] oldAccountsForType = (Account[]) accounts.accountCache.get(account.type);
        if (oldAccountsForType != null) {
            ArrayList<Account> newAccountsList = new ArrayList<>();
            for (Account curAccount : oldAccountsForType) {
                if (!curAccount.equals(account)) {
                    newAccountsList.add(curAccount);
                }
            }
            if (newAccountsList.isEmpty()) {
                accounts.accountCache.remove(account.type);
            } else {
                Account[] newAccountsForType = new Account[newAccountsList.size()];
                accounts.accountCache.put(account.type, (Account[]) newAccountsList.toArray(newAccountsForType));
            }
        }
        accounts.userDataCache.remove(account);
        accounts.authTokenCache.remove(account);
    }

    private void insertAccountIntoCacheLocked(UserAccounts accounts, Account account) {
        Account[] accountsForType = (Account[]) accounts.accountCache.get(account.type);
        int oldLength = accountsForType != null ? accountsForType.length : 0;
        Account[] newAccountsForType = new Account[oldLength + 1];
        if (accountsForType != null) {
            System.arraycopy(accountsForType, 0, newAccountsForType, 0, oldLength);
        }
        newAccountsForType[oldLength] = account;
        accounts.accountCache.put(account.type, newAccountsForType);
    }

    private Account[] filterSharedAccounts(UserAccounts userAccounts, Account[] unfiltered, int callingUid, String callingPackage) {
        if (getUserManager() != null && userAccounts != null && userAccounts.userId >= 0 && callingUid != Process.myUid()) {
            UserInfo user = this.mUserManager.getUserInfo(userAccounts.userId);
            if (user != null && user.isRestricted()) {
                String[] packages = this.mPackageManager.getPackagesForUid(callingUid);
                String whiteList = this.mContext.getResources().getString(R.string.config_appsAuthorizedForSharedAccounts);
                for (String packageName : packages) {
                    if (whiteList.contains(Separators.SEMICOLON + packageName + Separators.SEMICOLON)) {
                        return unfiltered;
                    }
                }
                ArrayList<Account> allowed = new ArrayList<>();
                Account[] sharedAccounts = getSharedAccountsAsUser(userAccounts.userId);
                if (sharedAccounts == null || sharedAccounts.length == 0) {
                    return unfiltered;
                }
                String requiredAccountType = "";
                try {
                    if (callingPackage != null) {
                        PackageInfo pi = this.mPackageManager.getPackageInfo(callingPackage, 0);
                        if (pi != null && pi.restrictedAccountType != null) {
                            requiredAccountType = pi.restrictedAccountType;
                        }
                    } else {
                        int len$ = packages.length;
                        int i$ = 0;
                        while (true) {
                            if (i$ >= len$) {
                                break;
                            }
                            String packageName2 = packages[i$];
                            PackageInfo pi2 = this.mPackageManager.getPackageInfo(packageName2, 0);
                            if (pi2 == null || pi2.restrictedAccountType == null) {
                                i$++;
                            } else {
                                requiredAccountType = pi2.restrictedAccountType;
                                break;
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                }
                for (Account account : unfiltered) {
                    if (account.type.equals(requiredAccountType)) {
                        allowed.add(account);
                    } else {
                        boolean found = false;
                        int len$2 = sharedAccounts.length;
                        int i$2 = 0;
                        while (true) {
                            if (i$2 >= len$2) {
                                break;
                            }
                            Account shared = sharedAccounts[i$2];
                            if (!shared.equals(account)) {
                                i$2++;
                            } else {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            allowed.add(account);
                        }
                    }
                }
                Account[] filtered = new Account[allowed.size()];
                allowed.toArray(filtered);
                return filtered;
            }
            return unfiltered;
        }
        return unfiltered;
    }

    protected Account[] getAccountsFromCacheLocked(UserAccounts userAccounts, String accountType, int callingUid, String callingPackage) {
        if (accountType != null) {
            Account[] accounts = (Account[]) userAccounts.accountCache.get(accountType);
            if (accounts == null) {
                return EMPTY_ACCOUNT_ARRAY;
            }
            return filterSharedAccounts(userAccounts, (Account[]) Arrays.copyOf(accounts, accounts.length), callingUid, callingPackage);
        }
        int totalLength = 0;
        for (Account[] accounts2 : userAccounts.accountCache.values()) {
            totalLength += accounts2.length;
        }
        if (totalLength == 0) {
            return EMPTY_ACCOUNT_ARRAY;
        }
        Account[] accounts3 = new Account[totalLength];
        int totalLength2 = 0;
        for (Account[] accountsOfType : userAccounts.accountCache.values()) {
            System.arraycopy(accountsOfType, 0, accounts3, totalLength2, accountsOfType.length);
            totalLength2 += accountsOfType.length;
        }
        return filterSharedAccounts(userAccounts, accounts3, callingUid, callingPackage);
    }

    protected void writeUserDataIntoCacheLocked(UserAccounts accounts, SQLiteDatabase db, Account account, String key, String value) {
        HashMap<String, String> userDataForAccount = (HashMap) accounts.userDataCache.get(account);
        if (userDataForAccount == null) {
            userDataForAccount = readUserDataForAccountFromDatabaseLocked(db, account);
            accounts.userDataCache.put(account, userDataForAccount);
        }
        if (value == null) {
            userDataForAccount.remove(key);
        } else {
            userDataForAccount.put(key, value);
        }
    }

    protected void writeAuthTokenIntoCacheLocked(UserAccounts accounts, SQLiteDatabase db, Account account, String key, String value) {
        HashMap<String, String> authTokensForAccount = (HashMap) accounts.authTokenCache.get(account);
        if (authTokensForAccount == null) {
            authTokensForAccount = readAuthTokensForAccountFromDatabaseLocked(db, account);
            accounts.authTokenCache.put(account, authTokensForAccount);
        }
        if (value == null) {
            authTokensForAccount.remove(key);
        } else {
            authTokensForAccount.put(key, value);
        }
    }

    protected String readAuthTokenInternal(UserAccounts accounts, Account account, String authTokenType) {
        String str;
        synchronized (accounts.cacheLock) {
            HashMap<String, String> authTokensForAccount = (HashMap) accounts.authTokenCache.get(account);
            if (authTokensForAccount == null) {
                SQLiteDatabase db = accounts.openHelper.getReadableDatabase();
                authTokensForAccount = readAuthTokensForAccountFromDatabaseLocked(db, account);
                accounts.authTokenCache.put(account, authTokensForAccount);
            }
            str = authTokensForAccount.get(authTokenType);
        }
        return str;
    }

    protected String readUserDataInternal(UserAccounts accounts, Account account, String key) {
        String str;
        synchronized (accounts.cacheLock) {
            HashMap<String, String> userDataForAccount = (HashMap) accounts.userDataCache.get(account);
            if (userDataForAccount == null) {
                SQLiteDatabase db = accounts.openHelper.getReadableDatabase();
                userDataForAccount = readUserDataForAccountFromDatabaseLocked(db, account);
                accounts.userDataCache.put(account, userDataForAccount);
            }
            str = userDataForAccount.get(key);
        }
        return str;
    }
}