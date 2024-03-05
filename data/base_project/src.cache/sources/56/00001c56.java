package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.AppGlobals;
import android.app.IUserSwitchObserver;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.style.SuggestionSpan;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.LruCache;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.internal.os.HandlerCaller;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethod;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.IInputSessionCallback;
import com.android.internal.view.InputBindResult;
import com.android.server.wm.WindowManagerService;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: InputMethodManagerService.class */
public class InputMethodManagerService extends IInputMethodManager.Stub implements ServiceConnection, Handler.Callback {
    static final boolean DEBUG = false;
    static final String TAG = "InputMethodManagerService";
    static final int MSG_SHOW_IM_PICKER = 1;
    static final int MSG_SHOW_IM_SUBTYPE_PICKER = 2;
    static final int MSG_SHOW_IM_SUBTYPE_ENABLER = 3;
    static final int MSG_SHOW_IM_CONFIG = 4;
    static final int MSG_UNBIND_INPUT = 1000;
    static final int MSG_BIND_INPUT = 1010;
    static final int MSG_SHOW_SOFT_INPUT = 1020;
    static final int MSG_HIDE_SOFT_INPUT = 1030;
    static final int MSG_ATTACH_TOKEN = 1040;
    static final int MSG_CREATE_SESSION = 1050;
    static final int MSG_START_INPUT = 2000;
    static final int MSG_RESTART_INPUT = 2010;
    static final int MSG_UNBIND_METHOD = 3000;
    static final int MSG_BIND_METHOD = 3010;
    static final int MSG_SET_ACTIVE = 3020;
    static final int MSG_HARD_KEYBOARD_SWITCH_CHANGED = 4000;
    static final long TIME_TO_RECONNECT = 10000;
    static final int SECURE_SUGGESTION_SPANS_MAX_SIZE = 20;
    private static final int NOT_A_SUBTYPE_ID = -1;
    private static final String TAG_TRY_SUPPRESSING_IME_SWITCHER = "TrySuppressingImeSwitcher";
    final Context mContext;
    final Resources mRes;
    final InputMethodUtils.InputMethodSettings mSettings;
    final SettingsObserver mSettingsObserver;
    final HandlerCaller mCaller;
    final boolean mHasFeature;
    private InputMethodFileManager mFileManager;
    private InputMethodAndSubtypeListManager mImListManager;
    private final WindowManagerService mWindowManagerService;
    private NotificationManager mNotificationManager;
    private KeyguardManager mKeyguardManager;
    private StatusBarManagerService mStatusBar;
    private PendingIntent mImeSwitchPendingIntent;
    private boolean mShowOngoingImeSwitcherForPhones;
    private boolean mNotificationShown;
    private final boolean mImeSelectedOnBoot;
    boolean mSystemReady;
    String mCurMethodId;
    int mCurSeq;
    ClientState mCurClient;
    IBinder mCurFocusedWindow;
    IInputContext mCurInputContext;
    EditorInfo mCurAttribute;
    String mCurId;
    private InputMethodSubtype mCurrentSubtype;
    boolean mHaveConnection;
    boolean mShowRequested;
    boolean mShowExplicitlyRequested;
    boolean mShowForced;
    boolean mInputShown;
    Intent mCurIntent;
    IBinder mCurToken;
    IInputMethod mCurMethod;
    long mLastBindTime;
    boolean mBoundToMethod;
    SessionState mEnabledSession;
    int mImeWindowVis;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mSwitchingDialog;
    private View mSwitchingDialogTitleView;
    private InputMethodInfo[] mIms;
    private int[] mSubtypeIds;
    private Locale mLastSystemLocale;
    private boolean mInputBoundToKeyguard;
    final InputBindResult mNoBinding = new InputBindResult(null, null, null, -1);
    final ArrayList<InputMethodInfo> mMethodList = new ArrayList<>();
    final HashMap<String, InputMethodInfo> mMethodMap = new HashMap<>();
    private final LruCache<SuggestionSpan, InputMethodInfo> mSecureSuggestionSpans = new LruCache<>(20);
    final ServiceConnection mVisibleConnection = new ServiceConnection() { // from class: com.android.server.InputMethodManagerService.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    boolean mVisibleBound = false;
    final HashMap<IBinder, ClientState> mClients = new HashMap<>();
    private final HashMap<InputMethodInfo, ArrayList<InputMethodSubtype>> mShortcutInputMethodsAndSubtypes = new HashMap<>();
    boolean mScreenOn = true;
    int mBackDisposition = 0;
    private final MyPackageMonitor mMyPackageMonitor = new MyPackageMonitor();
    private final IPackageManager mIPackageManager = AppGlobals.getPackageManager();
    final Handler mHandler = new Handler(this);
    final IWindowManager mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
    private final HardKeyboardListener mHardKeyboardListener = new HardKeyboardListener();
    private Notification mImeSwitcherNotification = new Notification();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.startInput(com.android.internal.view.IInputMethodClient, com.android.internal.view.IInputContext, android.view.inputmethod.EditorInfo, int):com.android.internal.view.InputBindResult, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public com.android.internal.view.InputBindResult startInput(com.android.internal.view.IInputMethodClient r1, com.android.internal.view.IInputContext r2, android.view.inputmethod.EditorInfo r3, int r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.startInput(com.android.internal.view.IInputMethodClient, com.android.internal.view.IInputContext, android.view.inputmethod.EditorInfo, int):com.android.internal.view.InputBindResult, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.startInput(com.android.internal.view.IInputMethodClient, com.android.internal.view.IInputContext, android.view.inputmethod.EditorInfo, int):com.android.internal.view.InputBindResult");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.updateStatusIcon(android.os.IBinder, java.lang.String, int):void, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public void updateStatusIcon(android.os.IBinder r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.updateStatusIcon(android.os.IBinder, java.lang.String, int):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.updateStatusIcon(android.os.IBinder, java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setImeWindowStatus(android.os.IBinder, int, int):void, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public void setImeWindowStatus(android.os.IBinder r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setImeWindowStatus(android.os.IBinder, int, int):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.setImeWindowStatus(android.os.IBinder, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.notifySuggestionPicked(android.text.style.SuggestionSpan, java.lang.String, int):boolean, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public boolean notifySuggestionPicked(android.text.style.SuggestionSpan r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.notifySuggestionPicked(android.text.style.SuggestionSpan, java.lang.String, int):boolean, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.notifySuggestionPicked(android.text.style.SuggestionSpan, java.lang.String, int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setInputMethodLocked(java.lang.String, int):void, file: InputMethodManagerService.class
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
    void setInputMethodLocked(java.lang.String r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setInputMethodLocked(java.lang.String, int):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.setInputMethodLocked(java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.showSoftInput(com.android.internal.view.IInputMethodClient, int, android.os.ResultReceiver):boolean, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public boolean showSoftInput(com.android.internal.view.IInputMethodClient r1, int r2, android.os.ResultReceiver r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.showSoftInput(com.android.internal.view.IInputMethodClient, int, android.os.ResultReceiver):boolean, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.showSoftInput(com.android.internal.view.IInputMethodClient, int, android.os.ResultReceiver):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.hideSoftInput(com.android.internal.view.IInputMethodClient, int, android.os.ResultReceiver):boolean, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public boolean hideSoftInput(com.android.internal.view.IInputMethodClient r1, int r2, android.os.ResultReceiver r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.hideSoftInput(com.android.internal.view.IInputMethodClient, int, android.os.ResultReceiver):boolean, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.hideSoftInput(com.android.internal.view.IInputMethodClient, int, android.os.ResultReceiver):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.windowGainedFocus(com.android.internal.view.IInputMethodClient, android.os.IBinder, int, int, int, android.view.inputmethod.EditorInfo, com.android.internal.view.IInputContext):com.android.internal.view.InputBindResult, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public com.android.internal.view.InputBindResult windowGainedFocus(com.android.internal.view.IInputMethodClient r1, android.os.IBinder r2, int r3, int r4, int r5, android.view.inputmethod.EditorInfo r6, com.android.internal.view.IInputContext r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.windowGainedFocus(com.android.internal.view.IInputMethodClient, android.os.IBinder, int, int, int, android.view.inputmethod.EditorInfo, com.android.internal.view.IInputContext):com.android.internal.view.InputBindResult, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.windowGainedFocus(com.android.internal.view.IInputMethodClient, android.os.IBinder, int, int, int, android.view.inputmethod.EditorInfo, com.android.internal.view.IInputContext):com.android.internal.view.InputBindResult");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setAdditionalInputMethodSubtypes(java.lang.String, android.view.inputmethod.InputMethodSubtype[]):void, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public void setAdditionalInputMethodSubtypes(java.lang.String r1, android.view.inputmethod.InputMethodSubtype[] r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setAdditionalInputMethodSubtypes(java.lang.String, android.view.inputmethod.InputMethodSubtype[]):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.setAdditionalInputMethodSubtypes(java.lang.String, android.view.inputmethod.InputMethodSubtype[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setInputMethodWithSubtypeId(android.os.IBinder, java.lang.String, int):void, file: InputMethodManagerService.class
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
    private void setInputMethodWithSubtypeId(android.os.IBinder r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setInputMethodWithSubtypeId(android.os.IBinder, java.lang.String, int):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.setInputMethodWithSubtypeId(android.os.IBinder, java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.hideMySoftInput(android.os.IBinder, int):void, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public void hideMySoftInput(android.os.IBinder r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.hideMySoftInput(android.os.IBinder, int):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.hideMySoftInput(android.os.IBinder, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.showMySoftInput(android.os.IBinder, int):void, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public void showMySoftInput(android.os.IBinder r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.showMySoftInput(android.os.IBinder, int):void, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.showMySoftInput(android.os.IBinder, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.handleMessage(android.os.Message):boolean, file: InputMethodManagerService.class
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
    @Override // android.os.Handler.Callback
    public boolean handleMessage(android.os.Message r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.handleMessage(android.os.Message):boolean, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.handleMessage(android.os.Message):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setInputMethodEnabled(java.lang.String, boolean):boolean, file: InputMethodManagerService.class
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
    @Override // com.android.internal.view.IInputMethodManager
    public boolean setInputMethodEnabled(java.lang.String r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.setInputMethodEnabled(java.lang.String, boolean):boolean, file: InputMethodManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.setInputMethodEnabled(java.lang.String, boolean):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: InputMethodManagerService$SessionState.class */
    public class SessionState {
        final ClientState client;
        final IInputMethod method;
        IInputMethodSession session;
        InputChannel channel;

        public String toString() {
            return "SessionState{uid " + this.client.uid + " pid " + this.client.pid + " method " + Integer.toHexString(System.identityHashCode(this.method)) + " session " + Integer.toHexString(System.identityHashCode(this.session)) + " channel " + this.channel + "}";
        }

        SessionState(ClientState _client, IInputMethod _method, IInputMethodSession _session, InputChannel _channel) {
            this.client = _client;
            this.method = _method;
            this.session = _session;
            this.channel = _channel;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: InputMethodManagerService$ClientState.class */
    public static final class ClientState {
        final IInputMethodClient client;
        final IInputContext inputContext;
        final int uid;
        final int pid;
        final InputBinding binding;
        boolean sessionRequested;
        SessionState curSession;

        public String toString() {
            return "ClientState{" + Integer.toHexString(System.identityHashCode(this)) + " uid " + this.uid + " pid " + this.pid + "}";
        }

        ClientState(IInputMethodClient _client, IInputContext _inputContext, int _uid, int _pid) {
            this.client = _client;
            this.inputContext = _inputContext;
            this.uid = _uid;
            this.pid = _pid;
            this.binding = new InputBinding(null, this.inputContext.asBinder(), this.uid, this.pid);
        }
    }

    /* loaded from: InputMethodManagerService$SettingsObserver.class */
    class SettingsObserver extends ContentObserver {
        String mLastEnabled;

        SettingsObserver(Handler handler) {
            super(handler);
            this.mLastEnabled = "";
            ContentResolver resolver = InputMethodManagerService.this.mContext.getContentResolver();
            resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.DEFAULT_INPUT_METHOD), false, this);
            resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ENABLED_INPUT_METHODS), false, this);
            resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            synchronized (InputMethodManagerService.this.mMethodMap) {
                boolean enabledChanged = false;
                String newEnabled = InputMethodManagerService.this.mSettings.getEnabledInputMethodsStr();
                if (!this.mLastEnabled.equals(newEnabled)) {
                    this.mLastEnabled = newEnabled;
                    enabledChanged = true;
                }
                InputMethodManagerService.this.updateFromSettingsLocked(enabledChanged);
            }
        }
    }

    /* loaded from: InputMethodManagerService$ImmsBroadcastReceiver.class */
    class ImmsBroadcastReceiver extends BroadcastReceiver {
        ImmsBroadcastReceiver() {
        }

        private void updateActive() {
            if (InputMethodManagerService.this.mCurClient != null && InputMethodManagerService.this.mCurClient.client != null) {
                InputMethodManagerService.this.executeOrSendMessage(InputMethodManagerService.this.mCurClient.client, InputMethodManagerService.this.mCaller.obtainMessageIO(InputMethodManagerService.MSG_SET_ACTIVE, InputMethodManagerService.this.mScreenOn ? 1 : 0, InputMethodManagerService.this.mCurClient));
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                InputMethodManagerService.this.mScreenOn = true;
                InputMethodManagerService.this.refreshImeWindowVisibilityLocked();
                updateActive();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                InputMethodManagerService.this.mScreenOn = false;
                InputMethodManagerService.this.setImeWindowVisibilityStatusHiddenLocked();
                updateActive();
            } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                InputMethodManagerService.this.hideInputMethodMenu();
            } else {
                Slog.w(InputMethodManagerService.TAG, "Unexpected intent " + intent);
            }
        }
    }

    /* loaded from: InputMethodManagerService$MyPackageMonitor.class */
    class MyPackageMonitor extends PackageMonitor {
        MyPackageMonitor() {
        }

        private boolean isChangingPackagesOfCurrentUser() {
            int userId = getChangingUserId();
            boolean retval = userId == InputMethodManagerService.this.mSettings.getCurrentUserId();
            return retval;
        }

        @Override // com.android.internal.content.PackageMonitor
        public boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
            if (!isChangingPackagesOfCurrentUser()) {
                return false;
            }
            synchronized (InputMethodManagerService.this.mMethodMap) {
                String curInputMethodId = InputMethodManagerService.this.mSettings.getSelectedInputMethod();
                int N = InputMethodManagerService.this.mMethodList.size();
                if (curInputMethodId != null) {
                    for (int i = 0; i < N; i++) {
                        InputMethodInfo imi = InputMethodManagerService.this.mMethodList.get(i);
                        if (imi.getId().equals(curInputMethodId)) {
                            for (String pkg : packages) {
                                if (imi.getPackageName().equals(pkg)) {
                                    if (doit) {
                                        InputMethodManagerService.this.resetSelectedInputMethodAndSubtypeLocked("");
                                        InputMethodManagerService.this.chooseNewDefaultIMELocked();
                                        return true;
                                    } else {
                                        return true;
                                    }
                                }
                            }
                            continue;
                        }
                    }
                }
                return false;
            }
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            int change;
            if (!isChangingPackagesOfCurrentUser()) {
                return;
            }
            synchronized (InputMethodManagerService.this.mMethodMap) {
                InputMethodInfo curIm = null;
                String curInputMethodId = InputMethodManagerService.this.mSettings.getSelectedInputMethod();
                int N = InputMethodManagerService.this.mMethodList.size();
                if (curInputMethodId != null) {
                    for (int i = 0; i < N; i++) {
                        InputMethodInfo imi = InputMethodManagerService.this.mMethodList.get(i);
                        String imiId = imi.getId();
                        if (imiId.equals(curInputMethodId)) {
                            curIm = imi;
                        }
                        int change2 = isPackageDisappearing(imi.getPackageName());
                        if (isPackageModified(imi.getPackageName())) {
                            InputMethodManagerService.this.mFileManager.deleteAllInputMethodSubtypes(imiId);
                        }
                        if (change2 == 2 || change2 == 3) {
                            Slog.i(InputMethodManagerService.TAG, "Input method uninstalled, disabling: " + imi.getComponent());
                            InputMethodManagerService.this.setInputMethodEnabledLocked(imi.getId(), false);
                        }
                    }
                }
                InputMethodManagerService.this.buildInputMethodListLocked(InputMethodManagerService.this.mMethodList, InputMethodManagerService.this.mMethodMap, false);
                boolean changed = false;
                if (curIm != null && ((change = isPackageDisappearing(curIm.getPackageName())) == 2 || change == 3)) {
                    ServiceInfo si = null;
                    try {
                        si = InputMethodManagerService.this.mIPackageManager.getServiceInfo(curIm.getComponent(), 0, InputMethodManagerService.this.mSettings.getCurrentUserId());
                    } catch (RemoteException e) {
                    }
                    if (si == null) {
                        Slog.i(InputMethodManagerService.TAG, "Current input method removed: " + curInputMethodId);
                        InputMethodManagerService.this.setImeWindowVisibilityStatusHiddenLocked();
                        if (!InputMethodManagerService.this.chooseNewDefaultIMELocked()) {
                            changed = true;
                            curIm = null;
                            Slog.i(InputMethodManagerService.TAG, "Unsetting current input method");
                            InputMethodManagerService.this.resetSelectedInputMethodAndSubtypeLocked("");
                        }
                    }
                }
                if (curIm == null) {
                    changed = InputMethodManagerService.this.chooseNewDefaultIMELocked();
                }
                if (changed) {
                    InputMethodManagerService.this.updateFromSettingsLocked(false);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManagerService$MethodCallback.class */
    public static final class MethodCallback extends IInputSessionCallback.Stub {
        private final InputMethodManagerService mParentIMMS;
        private final IInputMethod mMethod;
        private final InputChannel mChannel;

        MethodCallback(InputMethodManagerService imms, IInputMethod method, InputChannel channel) {
            this.mParentIMMS = imms;
            this.mMethod = method;
            this.mChannel = channel;
        }

        @Override // com.android.internal.view.IInputSessionCallback
        public void sessionCreated(IInputMethodSession session) {
            this.mParentIMMS.onSessionCreated(this.mMethod, session, this.mChannel);
        }
    }

    /* loaded from: InputMethodManagerService$HardKeyboardListener.class */
    private class HardKeyboardListener implements WindowManagerService.OnHardKeyboardStatusChangeListener {
        private HardKeyboardListener() {
        }

        @Override // com.android.server.wm.WindowManagerService.OnHardKeyboardStatusChangeListener
        public void onHardKeyboardStatusChange(boolean available, boolean enabled) {
            InputMethodManagerService.this.mHandler.sendMessage(InputMethodManagerService.this.mHandler.obtainMessage(InputMethodManagerService.MSG_HARD_KEYBOARD_SWITCH_CHANGED, available ? 1 : 0, enabled ? 1 : 0));
        }

        public void handleHardKeyboardStatusChange(boolean available, boolean enabled) {
            synchronized (InputMethodManagerService.this.mMethodMap) {
                if (InputMethodManagerService.this.mSwitchingDialog != null && InputMethodManagerService.this.mSwitchingDialogTitleView != null && InputMethodManagerService.this.mSwitchingDialog.isShowing()) {
                    InputMethodManagerService.this.mSwitchingDialogTitleView.findViewById(R.id.hard_keyboard_section).setVisibility(available ? 0 : 8);
                }
            }
        }
    }

    public InputMethodManagerService(Context context, WindowManagerService windowManager) {
        this.mContext = context;
        this.mRes = context.getResources();
        this.mCaller = new HandlerCaller(context, null, new HandlerCaller.Callback() { // from class: com.android.server.InputMethodManagerService.2
            @Override // com.android.internal.os.HandlerCaller.Callback
            public void executeMessage(Message msg) {
                InputMethodManagerService.this.handleMessage(msg);
            }
        }, true);
        this.mWindowManagerService = windowManager;
        this.mHasFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_INPUT_METHODS);
        this.mImeSwitcherNotification.icon = R.drawable.ic_notification_ime_default;
        this.mImeSwitcherNotification.when = 0L;
        this.mImeSwitcherNotification.flags = 2;
        this.mImeSwitcherNotification.tickerText = null;
        this.mImeSwitcherNotification.defaults = 0;
        this.mImeSwitcherNotification.sound = null;
        this.mImeSwitcherNotification.vibrate = null;
        this.mImeSwitcherNotification.kind = new String[]{"android.system.imeswitcher"};
        Intent intent = new Intent(Settings.ACTION_SHOW_INPUT_METHOD_PICKER);
        this.mImeSwitchPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
        this.mShowOngoingImeSwitcherForPhones = false;
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(Intent.ACTION_SCREEN_ON);
        broadcastFilter.addAction(Intent.ACTION_SCREEN_OFF);
        broadcastFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        this.mContext.registerReceiver(new ImmsBroadcastReceiver(), broadcastFilter);
        this.mNotificationShown = false;
        int userId = 0;
        try {
            ActivityManagerNative.getDefault().registerUserSwitchObserver(new IUserSwitchObserver.Stub() { // from class: com.android.server.InputMethodManagerService.3
                @Override // android.app.IUserSwitchObserver
                public void onUserSwitching(int newUserId, IRemoteCallback reply) {
                    synchronized (InputMethodManagerService.this.mMethodMap) {
                        InputMethodManagerService.this.switchUserLocked(newUserId);
                    }
                    if (reply != null) {
                        try {
                            reply.sendResult(null);
                        } catch (RemoteException e) {
                        }
                    }
                }

                @Override // android.app.IUserSwitchObserver
                public void onUserSwitchComplete(int newUserId) throws RemoteException {
                }
            });
            userId = ActivityManagerNative.getDefault().getCurrentUser().id;
        } catch (RemoteException e) {
            Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", e);
        }
        this.mMyPackageMonitor.register(this.mContext, null, UserHandle.ALL, true);
        this.mSettings = new InputMethodUtils.InputMethodSettings(this.mRes, context.getContentResolver(), this.mMethodMap, this.mMethodList, userId);
        this.mFileManager = new InputMethodFileManager(this.mMethodMap, userId);
        this.mImListManager = new InputMethodAndSubtypeListManager(context, this);
        String defaultImiId = this.mSettings.getSelectedInputMethod();
        this.mImeSelectedOnBoot = !TextUtils.isEmpty(defaultImiId);
        buildInputMethodListLocked(this.mMethodList, this.mMethodMap, !this.mImeSelectedOnBoot);
        this.mSettings.enableAllIMEsIfThereIsNoEnabledIME();
        if (!this.mImeSelectedOnBoot) {
            Slog.w(TAG, "No IME selected. Choose the most applicable IME.");
            resetDefaultImeLocked(context);
        }
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        updateFromSettingsLocked(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.InputMethodManagerService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent2) {
                synchronized (InputMethodManagerService.this.mMethodMap) {
                    InputMethodManagerService.this.resetStateIfCurrentLocaleChangedLocked();
                }
            }
        }, filter);
    }

    private void resetDefaultImeLocked(Context context) {
        if (this.mCurMethodId != null && !InputMethodUtils.isSystemIme(this.mMethodMap.get(this.mCurMethodId))) {
            return;
        }
        InputMethodInfo defIm = null;
        Iterator i$ = this.mMethodList.iterator();
        while (i$.hasNext()) {
            InputMethodInfo imi = i$.next();
            if (defIm == null && InputMethodUtils.isValidSystemDefaultIme(this.mSystemReady, imi, context)) {
                defIm = imi;
                Slog.i(TAG, "Selected default: " + imi.getId());
            }
        }
        if (defIm == null && this.mMethodList.size() > 0) {
            defIm = InputMethodUtils.getMostApplicableDefaultIME(this.mSettings.getEnabledInputMethodListLocked());
            Slog.i(TAG, "No default found, using " + defIm.getId());
        }
        if (defIm != null) {
            setSelectedInputMethodAndSubtypeLocked(defIm, -1, false);
        }
    }

    private void resetAllInternalStateLocked(boolean updateOnlyWhenLocaleChanged, boolean resetDefaultEnabledIme) {
        if (!this.mSystemReady) {
            return;
        }
        Locale newLocale = this.mRes.getConfiguration().locale;
        if (!updateOnlyWhenLocaleChanged || (newLocale != null && !newLocale.equals(this.mLastSystemLocale))) {
            if (!updateOnlyWhenLocaleChanged) {
                hideCurrentInputLocked(0, null);
                this.mCurMethodId = null;
                unbindCurrentMethodLocked(true, false);
            }
            this.mImListManager = new InputMethodAndSubtypeListManager(this.mContext, this);
            buildInputMethodListLocked(this.mMethodList, this.mMethodMap, resetDefaultEnabledIme);
            if (!updateOnlyWhenLocaleChanged) {
                String selectedImiId = this.mSettings.getSelectedInputMethod();
                if (TextUtils.isEmpty(selectedImiId)) {
                    resetDefaultImeLocked(this.mContext);
                }
            } else {
                resetDefaultImeLocked(this.mContext);
            }
            updateFromSettingsLocked(true);
            this.mLastSystemLocale = newLocale;
            if (!updateOnlyWhenLocaleChanged) {
                try {
                    startInputInnerLocked();
                } catch (RuntimeException e) {
                    Slog.w(TAG, "Unexpected exception", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetStateIfCurrentLocaleChangedLocked() {
        resetAllInternalStateLocked(true, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchUserLocked(int newUserId) {
        this.mSettings.setCurrentUserId(newUserId);
        this.mFileManager = new InputMethodFileManager(this.mMethodMap, newUserId);
        String defaultImiId = this.mSettings.getSelectedInputMethod();
        boolean initialUserSwitch = TextUtils.isEmpty(defaultImiId);
        resetAllInternalStateLocked(false, initialUserSwitch);
        if (initialUserSwitch) {
            InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mContext.getPackageManager(), this.mSettings.getEnabledInputMethodListLocked());
        }
    }

    @Override // com.android.internal.view.IInputMethodManager.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Input Method Manager Crash", e);
            }
            throw e;
        }
    }

    public void systemRunning(StatusBarManagerService statusBar) {
        synchronized (this.mMethodMap) {
            if (!this.mSystemReady) {
                this.mSystemReady = true;
                this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService(Context.KEYGUARD_SERVICE);
                this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                this.mStatusBar = statusBar;
                statusBar.setIconVisibility("ime", false);
                updateImeWindowStatusLocked();
                this.mShowOngoingImeSwitcherForPhones = this.mRes.getBoolean(R.bool.show_ongoing_ime_switcher);
                if (this.mShowOngoingImeSwitcherForPhones) {
                    this.mWindowManagerService.setOnHardKeyboardStatusChangeListener(this.mHardKeyboardListener);
                }
                buildInputMethodListLocked(this.mMethodList, this.mMethodMap, !this.mImeSelectedOnBoot);
                if (!this.mImeSelectedOnBoot) {
                    Slog.w(TAG, "Reset the default IME as \"Resource\" is ready here.");
                    resetStateIfCurrentLocaleChangedLocked();
                    InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mContext.getPackageManager(), this.mSettings.getEnabledInputMethodListLocked());
                }
                this.mLastSystemLocale = this.mRes.getConfiguration().locale;
                try {
                    startInputInnerLocked();
                } catch (RuntimeException e) {
                    Slog.w(TAG, "Unexpected exception", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImeWindowVisibilityStatusHiddenLocked() {
        this.mImeWindowVis = 0;
        updateImeWindowStatusLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshImeWindowVisibilityLocked() {
        Configuration conf = this.mRes.getConfiguration();
        boolean haveHardKeyboard = conf.keyboard != 1;
        boolean hardKeyShown = haveHardKeyboard && conf.hardKeyboardHidden != 2;
        boolean isScreenLocked = this.mKeyguardManager != null && this.mKeyguardManager.isKeyguardLocked();
        boolean isScreenSecurelyLocked = isScreenLocked && this.mKeyguardManager.isKeyguardSecure();
        boolean inputShown = this.mInputShown && (!isScreenLocked || this.mInputBoundToKeyguard);
        this.mImeWindowVis = (isScreenSecurelyLocked || !(inputShown || hardKeyShown)) ? 0 : 3;
        updateImeWindowStatusLocked();
    }

    private void updateImeWindowStatusLocked() {
        setImeWindowStatus(this.mCurToken, this.mImeWindowVis, this.mBackDisposition);
    }

    private boolean calledFromValidUser() {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        if (uid == 1000 || userId == this.mSettings.getCurrentUserId() || this.mContext.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL) == 0) {
            return true;
        }
        Slog.w(TAG, "--- IPC called from background users. Ignore. \n" + InputMethodUtils.getStackTrace());
        return false;
    }

    private boolean bindCurrentInputMethodService(Intent service, ServiceConnection conn, int flags) {
        if (service == null || conn == null) {
            Slog.e(TAG, "--- bind failed: service = " + service + ", conn = " + conn);
            return false;
        }
        return this.mContext.bindServiceAsUser(service, conn, flags, new UserHandle(this.mSettings.getCurrentUserId()));
    }

    @Override // com.android.internal.view.IInputMethodManager
    public List<InputMethodInfo> getInputMethodList() {
        ArrayList arrayList;
        if (!calledFromValidUser()) {
            return Collections.emptyList();
        }
        synchronized (this.mMethodMap) {
            arrayList = new ArrayList(this.mMethodList);
        }
        return arrayList;
    }

    @Override // com.android.internal.view.IInputMethodManager
    public List<InputMethodInfo> getEnabledInputMethodList() {
        List<InputMethodInfo> enabledInputMethodListLocked;
        if (!calledFromValidUser()) {
            return Collections.emptyList();
        }
        synchronized (this.mMethodMap) {
            enabledInputMethodListLocked = this.mSettings.getEnabledInputMethodListLocked();
        }
        return enabledInputMethodListLocked;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HashMap<InputMethodInfo, List<InputMethodSubtype>> getExplicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked() {
        HashMap<InputMethodInfo, List<InputMethodSubtype>> enabledInputMethodAndSubtypes = new HashMap<>();
        for (InputMethodInfo imi : this.mSettings.getEnabledInputMethodListLocked()) {
            enabledInputMethodAndSubtypes.put(imi, this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imi, true));
        }
        return enabledInputMethodAndSubtypes;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0042 A[Catch: all -> 0x0059, TryCatch #0 {, blocks: (B:10:0x0016, B:12:0x001d, B:16:0x0042, B:17:0x0046, B:19:0x0048, B:20:0x0057, B:13:0x0030), top: B:27:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0048 A[Catch: all -> 0x0059, TRY_ENTER, TryCatch #0 {, blocks: (B:10:0x0016, B:12:0x001d, B:16:0x0042, B:17:0x0046, B:19:0x0048, B:20:0x0057, B:13:0x0030), top: B:27:0x0016 }] */
    @Override // com.android.internal.view.IInputMethodManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<android.view.inputmethod.InputMethodSubtype> getEnabledInputMethodSubtypeList(java.lang.String r6, boolean r7) {
        /*
            r5 = this;
            r0 = r5
            boolean r0 = r0.calledFromValidUser()
            if (r0 != 0) goto Lb
            java.util.List r0 = java.util.Collections.emptyList()
            return r0
        Lb:
            r0 = r5
            java.util.HashMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r0.mMethodMap
            r1 = r0
            r8 = r1
            monitor-enter(r0)
            r0 = r6
            if (r0 != 0) goto L30
            r0 = r5
            java.lang.String r0 = r0.mCurMethodId     // Catch: java.lang.Throwable -> L59
            if (r0 == 0) goto L30
            r0 = r5
            java.util.HashMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r0.mMethodMap     // Catch: java.lang.Throwable -> L59
            r1 = r5
            java.lang.String r1 = r1.mCurMethodId     // Catch: java.lang.Throwable -> L59
            java.lang.Object r0 = r0.get(r1)     // Catch: java.lang.Throwable -> L59
            android.view.inputmethod.InputMethodInfo r0 = (android.view.inputmethod.InputMethodInfo) r0     // Catch: java.lang.Throwable -> L59
            r9 = r0
            goto L3d
        L30:
            r0 = r5
            java.util.HashMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r0.mMethodMap     // Catch: java.lang.Throwable -> L59
            r1 = r6
            java.lang.Object r0 = r0.get(r1)     // Catch: java.lang.Throwable -> L59
            android.view.inputmethod.InputMethodInfo r0 = (android.view.inputmethod.InputMethodInfo) r0     // Catch: java.lang.Throwable -> L59
            r9 = r0
        L3d:
            r0 = r9
            if (r0 != 0) goto L48
            java.util.List r0 = java.util.Collections.emptyList()     // Catch: java.lang.Throwable -> L59
            r1 = r8
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L59
            return r0
        L48:
            r0 = r5
            com.android.internal.inputmethod.InputMethodUtils$InputMethodSettings r0 = r0.mSettings     // Catch: java.lang.Throwable -> L59
            r1 = r5
            android.content.Context r1 = r1.mContext     // Catch: java.lang.Throwable -> L59
            r2 = r9
            r3 = r7
            java.util.List r0 = r0.getEnabledInputMethodSubtypeListLocked(r1, r2, r3)     // Catch: java.lang.Throwable -> L59
            r1 = r8
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L59
            return r0
        L59:
            r10 = move-exception
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L59
            r0 = r10
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.getEnabledInputMethodSubtypeList(java.lang.String, boolean):java.util.List");
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void addClient(IInputMethodClient client, IInputContext inputContext, int uid, int pid) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mMethodMap) {
            this.mClients.put(client.asBinder(), new ClientState(client, inputContext, uid, pid));
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void removeClient(IInputMethodClient client) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mMethodMap) {
            ClientState cs = this.mClients.remove(client.asBinder());
            if (cs != null) {
                clearClientSessionLocked(cs);
            }
        }
    }

    void executeOrSendMessage(IInterface target, Message msg) {
        if (target.asBinder() instanceof Binder) {
            this.mCaller.sendMessage(msg);
            return;
        }
        handleMessage(msg);
        msg.recycle();
    }

    void unbindCurrentClientLocked() {
        if (this.mCurClient != null) {
            if (this.mBoundToMethod) {
                this.mBoundToMethod = false;
                if (this.mCurMethod != null) {
                    executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageO(1000, this.mCurMethod));
                }
            }
            executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIO(MSG_SET_ACTIVE, 0, this.mCurClient));
            executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIO(3000, this.mCurSeq, this.mCurClient.client));
            this.mCurClient.sessionRequested = false;
            this.mCurClient = null;
            hideInputMethodMenuLocked();
        }
    }

    private int getImeShowFlags() {
        int flags = 0;
        if (this.mShowForced) {
            flags = 0 | 3;
        } else if (this.mShowExplicitlyRequested) {
            flags = 0 | 1;
        }
        return flags;
    }

    private int getAppShowFlags() {
        int flags = 0;
        if (this.mShowForced) {
            flags = 0 | 2;
        } else if (!this.mShowExplicitlyRequested) {
            flags = 0 | 1;
        }
        return flags;
    }

    InputBindResult attachNewInputLocked(boolean initial) {
        if (!this.mBoundToMethod) {
            executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOO(1010, this.mCurMethod, this.mCurClient.binding));
            this.mBoundToMethod = true;
        }
        SessionState session = this.mCurClient.curSession;
        if (initial) {
            executeOrSendMessage(session.method, this.mCaller.obtainMessageOOO(2000, session, this.mCurInputContext, this.mCurAttribute));
        } else {
            executeOrSendMessage(session.method, this.mCaller.obtainMessageOOO(2010, session, this.mCurInputContext, this.mCurAttribute));
        }
        if (this.mShowRequested) {
            showCurrentInputLocked(getAppShowFlags(), null);
        }
        return new InputBindResult(session.session, session.channel != null ? session.channel.dup() : null, this.mCurId, this.mCurSeq);
    }

    InputBindResult startInputLocked(IInputMethodClient client, IInputContext inputContext, EditorInfo attribute, int controlFlags) {
        if (this.mCurMethodId == null) {
            return this.mNoBinding;
        }
        ClientState cs = this.mClients.get(client.asBinder());
        if (cs == null) {
            throw new IllegalArgumentException("unknown client " + client.asBinder());
        }
        try {
            if (!this.mIWindowManager.inputMethodClientHasFocus(cs.client)) {
                Slog.w(TAG, "Starting input on non-focused client " + cs.client + " (uid=" + cs.uid + " pid=" + cs.pid + Separators.RPAREN);
                return null;
            }
        } catch (RemoteException e) {
        }
        return startInputUncheckedLocked(cs, inputContext, attribute, controlFlags);
    }

    InputBindResult startInputUncheckedLocked(ClientState cs, IInputContext inputContext, EditorInfo attribute, int controlFlags) {
        if (this.mCurMethodId == null) {
            return this.mNoBinding;
        }
        if (this.mCurClient == null) {
            this.mInputBoundToKeyguard = this.mKeyguardManager != null && this.mKeyguardManager.isKeyguardLocked();
        }
        if (this.mCurClient != cs) {
            unbindCurrentClientLocked();
            if (this.mScreenOn) {
                executeOrSendMessage(cs.client, this.mCaller.obtainMessageIO(MSG_SET_ACTIVE, this.mScreenOn ? 1 : 0, cs));
            }
        }
        this.mCurSeq++;
        if (this.mCurSeq <= 0) {
            this.mCurSeq = 1;
        }
        this.mCurClient = cs;
        this.mCurInputContext = inputContext;
        this.mCurAttribute = attribute;
        if (this.mCurId != null && this.mCurId.equals(this.mCurMethodId)) {
            if (cs.curSession != null) {
                return attachNewInputLocked((controlFlags & 256) != 0);
            } else if (this.mHaveConnection) {
                if (this.mCurMethod != null) {
                    requestClientSessionLocked(cs);
                    return new InputBindResult(null, null, this.mCurId, this.mCurSeq);
                } else if (SystemClock.uptimeMillis() < this.mLastBindTime + TIME_TO_RECONNECT) {
                    return new InputBindResult(null, null, this.mCurId, this.mCurSeq);
                } else {
                    EventLog.writeEvent(32000, this.mCurMethodId, Long.valueOf(SystemClock.uptimeMillis() - this.mLastBindTime), 0);
                }
            }
        }
        return startInputInnerLocked();
    }

    InputBindResult startInputInnerLocked() {
        if (this.mCurMethodId == null) {
            return this.mNoBinding;
        }
        if (!this.mSystemReady) {
            return new InputBindResult(null, null, this.mCurMethodId, this.mCurSeq);
        }
        InputMethodInfo info = this.mMethodMap.get(this.mCurMethodId);
        if (info == null) {
            throw new IllegalArgumentException("Unknown id: " + this.mCurMethodId);
        }
        unbindCurrentMethodLocked(false, true);
        this.mCurIntent = new Intent(InputMethod.SERVICE_INTERFACE);
        this.mCurIntent.setComponent(info.getComponent());
        this.mCurIntent.putExtra(Intent.EXTRA_CLIENT_LABEL, R.string.input_method_binding_label);
        this.mCurIntent.putExtra(Intent.EXTRA_CLIENT_INTENT, PendingIntent.getActivity(this.mContext, 0, new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0));
        if (bindCurrentInputMethodService(this.mCurIntent, this, 1610612737)) {
            this.mLastBindTime = SystemClock.uptimeMillis();
            this.mHaveConnection = true;
            this.mCurId = info.getId();
            this.mCurToken = new Binder();
            try {
                Slog.v(TAG, "Adding window token: " + this.mCurToken);
                this.mIWindowManager.addWindowToken(this.mCurToken, 2011);
            } catch (RemoteException e) {
            }
            return new InputBindResult(null, null, this.mCurId, this.mCurSeq);
        }
        this.mCurIntent = null;
        Slog.w(TAG, "Failure connecting to input method service: " + this.mCurIntent);
        return null;
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void finishInput(IInputMethodClient client) {
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        synchronized (this.mMethodMap) {
            if (this.mCurIntent != null && name.equals(this.mCurIntent.getComponent())) {
                this.mCurMethod = IInputMethod.Stub.asInterface(service);
                if (this.mCurToken == null) {
                    Slog.w(TAG, "Service connected without a token!");
                    unbindCurrentMethodLocked(false, false);
                    return;
                }
                executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOO(1040, this.mCurMethod, this.mCurToken));
                if (this.mCurClient != null) {
                    clearClientSessionLocked(this.mCurClient);
                    requestClientSessionLocked(this.mCurClient);
                }
            }
        }
    }

    void onSessionCreated(IInputMethod method, IInputMethodSession session, InputChannel channel) {
        synchronized (this.mMethodMap) {
            if (this.mCurMethod != null && method != null && this.mCurMethod.asBinder() == method.asBinder() && this.mCurClient != null) {
                clearClientSessionLocked(this.mCurClient);
                this.mCurClient.curSession = new SessionState(this.mCurClient, method, session, channel);
                InputBindResult res = attachNewInputLocked(true);
                if (res.method != null) {
                    executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageOO(3010, this.mCurClient.client, res));
                }
                return;
            }
            channel.dispose();
        }
    }

    void unbindCurrentMethodLocked(boolean reportToClient, boolean savePosition) {
        if (this.mVisibleBound) {
            this.mContext.unbindService(this.mVisibleConnection);
            this.mVisibleBound = false;
        }
        if (this.mHaveConnection) {
            this.mContext.unbindService(this);
            this.mHaveConnection = false;
        }
        if (this.mCurToken != null) {
            try {
                if ((this.mImeWindowVis & 1) != 0 && savePosition) {
                    this.mWindowManagerService.saveLastInputMethodWindowForTransition();
                }
                this.mIWindowManager.removeWindowToken(this.mCurToken);
            } catch (RemoteException e) {
            }
            this.mCurToken = null;
        }
        this.mCurId = null;
        clearCurMethodLocked();
        if (reportToClient && this.mCurClient != null) {
            executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIO(3000, this.mCurSeq, this.mCurClient.client));
        }
    }

    void requestClientSessionLocked(ClientState cs) {
        if (!cs.sessionRequested) {
            InputChannel[] channels = InputChannel.openInputChannelPair(cs.toString());
            cs.sessionRequested = true;
            executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOOO(MSG_CREATE_SESSION, this.mCurMethod, channels[1], new MethodCallback(this, this.mCurMethod, channels[0])));
        }
    }

    void clearClientSessionLocked(ClientState cs) {
        finishSessionLocked(cs.curSession);
        cs.curSession = null;
        cs.sessionRequested = false;
    }

    private void finishSessionLocked(SessionState sessionState) {
        if (sessionState != null) {
            if (sessionState.session != null) {
                try {
                    sessionState.session.finishSession();
                } catch (RemoteException e) {
                    Slog.w(TAG, "Session failed to close due to remote exception", e);
                    setImeWindowVisibilityStatusHiddenLocked();
                }
                sessionState.session = null;
            }
            if (sessionState.channel != null) {
                sessionState.channel.dispose();
                sessionState.channel = null;
            }
        }
    }

    void clearCurMethodLocked() {
        if (this.mCurMethod != null) {
            for (ClientState cs : this.mClients.values()) {
                clearClientSessionLocked(cs);
            }
            finishSessionLocked(this.mEnabledSession);
            this.mEnabledSession = null;
            this.mCurMethod = null;
        }
        if (this.mStatusBar != null) {
            this.mStatusBar.setIconVisibility("ime", false);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        synchronized (this.mMethodMap) {
            if (this.mCurMethod != null && this.mCurIntent != null && name.equals(this.mCurIntent.getComponent())) {
                clearCurMethodLocked();
                this.mLastBindTime = SystemClock.uptimeMillis();
                this.mShowRequested = this.mInputShown;
                this.mInputShown = false;
                if (this.mCurClient != null) {
                    executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIO(3000, this.mCurSeq, this.mCurClient.client));
                }
            }
        }
    }

    private boolean needsToShowImeSwitchOngoingNotification() {
        if (this.mShowOngoingImeSwitcherForPhones && !isScreenLocked()) {
            synchronized (this.mMethodMap) {
                List<InputMethodInfo> imis = this.mSettings.getEnabledInputMethodListLocked();
                int N = imis.size();
                if (N > 2) {
                    return true;
                }
                if (N < 1) {
                    return false;
                }
                int nonAuxCount = 0;
                int auxCount = 0;
                InputMethodSubtype nonAuxSubtype = null;
                InputMethodSubtype auxSubtype = null;
                for (int i = 0; i < N; i++) {
                    InputMethodInfo imi = imis.get(i);
                    List<InputMethodSubtype> subtypes = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imi, true);
                    int subtypeCount = subtypes.size();
                    if (subtypeCount == 0) {
                        nonAuxCount++;
                    } else {
                        for (int j = 0; j < subtypeCount; j++) {
                            InputMethodSubtype subtype = subtypes.get(j);
                            if (!subtype.isAuxiliary()) {
                                nonAuxCount++;
                                nonAuxSubtype = subtype;
                            } else {
                                auxCount++;
                                auxSubtype = subtype;
                            }
                        }
                    }
                }
                if (nonAuxCount > 1 || auxCount > 1) {
                    return true;
                }
                if (nonAuxCount == 1 && auxCount == 1) {
                    if (nonAuxSubtype != null && auxSubtype != null && ((nonAuxSubtype.getLocale().equals(auxSubtype.getLocale()) || auxSubtype.overridesImplicitlyEnabledSubtype() || nonAuxSubtype.overridesImplicitlyEnabledSubtype()) && nonAuxSubtype.containsExtraValueKey(TAG_TRY_SUPPRESSING_IME_SWITCHER))) {
                        return false;
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void registerSuggestionSpansForNotification(SuggestionSpan[] spans) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mMethodMap) {
            InputMethodInfo currentImi = this.mMethodMap.get(this.mCurMethodId);
            for (SuggestionSpan ss : spans) {
                if (!TextUtils.isEmpty(ss.getNotificationTargetClassName())) {
                    this.mSecureSuggestionSpans.put(ss, currentImi);
                }
            }
        }
    }

    void updateFromSettingsLocked(boolean enabledMayChange) {
        if (enabledMayChange) {
            List<InputMethodInfo> enabled = this.mSettings.getEnabledInputMethodListLocked();
            for (int i = 0; i < enabled.size(); i++) {
                InputMethodInfo imm = enabled.get(i);
                try {
                    ApplicationInfo ai = this.mIPackageManager.getApplicationInfo(imm.getPackageName(), 32768, this.mSettings.getCurrentUserId());
                    if (ai != null && ai.enabledSetting == 4) {
                        this.mIPackageManager.setApplicationEnabledSetting(imm.getPackageName(), 0, 1, this.mSettings.getCurrentUserId(), this.mContext.getBasePackageName());
                    }
                } catch (RemoteException e) {
                }
            }
        }
        String id = this.mSettings.getSelectedInputMethod();
        if (TextUtils.isEmpty(id) && chooseNewDefaultIMELocked()) {
            id = this.mSettings.getSelectedInputMethod();
        }
        if (!TextUtils.isEmpty(id)) {
            try {
                setInputMethodLocked(id, this.mSettings.getSelectedInputMethodSubtypeId(id));
            } catch (IllegalArgumentException e2) {
                Slog.w(TAG, "Unknown input method from prefs: " + id, e2);
                this.mCurMethodId = null;
                unbindCurrentMethodLocked(true, false);
            }
            this.mShortcutInputMethodsAndSubtypes.clear();
            return;
        }
        this.mCurMethodId = null;
        unbindCurrentMethodLocked(true, false);
    }

    boolean showCurrentInputLocked(int flags, ResultReceiver resultReceiver) {
        this.mShowRequested = true;
        if ((flags & 1) == 0) {
            this.mShowExplicitlyRequested = true;
        }
        if ((flags & 2) != 0) {
            this.mShowExplicitlyRequested = true;
            this.mShowForced = true;
        }
        if (!this.mSystemReady) {
            return false;
        }
        boolean res = false;
        if (this.mCurMethod != null) {
            executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageIOO(1020, getImeShowFlags(), this.mCurMethod, resultReceiver));
            this.mInputShown = true;
            if (this.mHaveConnection && !this.mVisibleBound) {
                bindCurrentInputMethodService(this.mCurIntent, this.mVisibleConnection, 1);
                this.mVisibleBound = true;
            }
            res = true;
        } else if (this.mHaveConnection && SystemClock.uptimeMillis() >= this.mLastBindTime + TIME_TO_RECONNECT) {
            EventLog.writeEvent(32000, this.mCurMethodId, Long.valueOf(SystemClock.uptimeMillis() - this.mLastBindTime), 1);
            Slog.w(TAG, "Force disconnect/connect to the IME in showCurrentInputLocked()");
            this.mContext.unbindService(this);
            bindCurrentInputMethodService(this.mCurIntent, this, 1073741825);
        }
        return res;
    }

    boolean hideCurrentInputLocked(int flags, ResultReceiver resultReceiver) {
        boolean res;
        if ((flags & 1) != 0 && (this.mShowExplicitlyRequested || this.mShowForced)) {
            return false;
        }
        if (this.mShowForced && (flags & 2) != 0) {
            return false;
        }
        if (this.mInputShown && this.mCurMethod != null) {
            executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageOO(1030, this.mCurMethod, resultReceiver));
            res = true;
        } else {
            res = false;
        }
        if (this.mHaveConnection && this.mVisibleBound) {
            this.mContext.unbindService(this.mVisibleConnection);
            this.mVisibleBound = false;
        }
        this.mInputShown = false;
        this.mShowRequested = false;
        this.mShowExplicitlyRequested = false;
        this.mShowForced = false;
        return res;
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void showInputMethodPickerFromClient(IInputMethodClient client) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mMethodMap) {
            if (this.mCurClient == null || client == null || this.mCurClient.client.asBinder() != client.asBinder()) {
                Slog.w(TAG, "Ignoring showInputMethodPickerFromClient of uid " + Binder.getCallingUid() + ": " + client);
            }
            this.mHandler.sendEmptyMessage(2);
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void setInputMethod(IBinder token, String id) {
        if (!calledFromValidUser()) {
            return;
        }
        setInputMethodWithSubtypeId(token, id, -1);
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void setInputMethodAndSubtype(IBinder token, String id, InputMethodSubtype subtype) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mMethodMap) {
            if (subtype != null) {
                setInputMethodWithSubtypeId(token, id, InputMethodUtils.getSubtypeIdFromHashCode(this.mMethodMap.get(id), subtype.hashCode()));
            } else {
                setInputMethod(token, id);
            }
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public void showInputMethodAndSubtypeEnablerFromClient(IInputMethodClient client, String inputMethodId) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mMethodMap) {
            if (this.mCurClient == null || client == null || this.mCurClient.client.asBinder() != client.asBinder()) {
                Slog.w(TAG, "Ignoring showInputMethodAndSubtypeEnablerFromClient of: " + client);
            }
            executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageO(3, inputMethodId));
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public boolean switchToLastInputMethod(IBinder token) {
        InputMethodInfo lastImi;
        List<InputMethodInfo> enabled;
        InputMethodSubtype keyboardSubtype;
        if (!calledFromValidUser()) {
            return false;
        }
        synchronized (this.mMethodMap) {
            Pair<String, String> lastIme = this.mSettings.getLastInputMethodAndSubtypeLocked();
            if (lastIme != null) {
                lastImi = this.mMethodMap.get(lastIme.first);
            } else {
                lastImi = null;
            }
            String targetLastImiId = null;
            int subtypeId = -1;
            if (lastIme != null && lastImi != null) {
                boolean imiIdIsSame = lastImi.getId().equals(this.mCurMethodId);
                int lastSubtypeHash = Integer.valueOf(lastIme.second).intValue();
                int currentSubtypeHash = this.mCurrentSubtype == null ? -1 : this.mCurrentSubtype.hashCode();
                if (!imiIdIsSame || lastSubtypeHash != currentSubtypeHash) {
                    targetLastImiId = lastIme.first;
                    subtypeId = InputMethodUtils.getSubtypeIdFromHashCode(lastImi, lastSubtypeHash);
                }
            }
            if (TextUtils.isEmpty(targetLastImiId) && !InputMethodUtils.canAddToLastInputMethod(this.mCurrentSubtype) && (enabled = this.mSettings.getEnabledInputMethodListLocked()) != null) {
                int N = enabled.size();
                String locale = this.mCurrentSubtype == null ? this.mRes.getConfiguration().locale.toString() : this.mCurrentSubtype.getLocale();
                for (int i = 0; i < N; i++) {
                    InputMethodInfo imi = enabled.get(i);
                    if (imi.getSubtypeCount() > 0 && InputMethodUtils.isSystemIme(imi) && (keyboardSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, InputMethodUtils.getSubtypes(imi), InputMethodUtils.SUBTYPE_MODE_KEYBOARD, locale, true)) != null) {
                        targetLastImiId = imi.getId();
                        subtypeId = InputMethodUtils.getSubtypeIdFromHashCode(imi, keyboardSubtype.hashCode());
                        if (keyboardSubtype.getLocale().equals(locale)) {
                            break;
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(targetLastImiId)) {
                setInputMethodWithSubtypeId(token, targetLastImiId, subtypeId);
                return true;
            }
            return false;
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public boolean switchToNextInputMethod(IBinder token, boolean onlyCurrentIme) {
        if (!calledFromValidUser()) {
            return false;
        }
        synchronized (this.mMethodMap) {
            ImeSubtypeListItem nextSubtype = this.mImListManager.getNextInputMethod(onlyCurrentIme, this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype);
            if (nextSubtype == null) {
                return false;
            }
            setInputMethodWithSubtypeId(token, nextSubtype.mImi.getId(), nextSubtype.mSubtypeId);
            return true;
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public boolean shouldOfferSwitchingToNextInputMethod(IBinder token) {
        if (!calledFromValidUser()) {
            return false;
        }
        synchronized (this.mMethodMap) {
            ImeSubtypeListItem nextSubtype = this.mImListManager.getNextInputMethod(false, this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype);
            if (nextSubtype == null) {
                return false;
            }
            return true;
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public InputMethodSubtype getLastInputMethodSubtype() {
        if (!calledFromValidUser()) {
            return null;
        }
        synchronized (this.mMethodMap) {
            Pair<String, String> lastIme = this.mSettings.getLastInputMethodAndSubtypeLocked();
            if (lastIme == null || TextUtils.isEmpty(lastIme.first) || TextUtils.isEmpty(lastIme.second)) {
                return null;
            }
            InputMethodInfo lastImi = this.mMethodMap.get(lastIme.first);
            if (lastImi == null) {
                return null;
            }
            try {
                int lastSubtypeHash = Integer.valueOf(lastIme.second).intValue();
                int lastSubtypeId = InputMethodUtils.getSubtypeIdFromHashCode(lastImi, lastSubtypeHash);
                if (lastSubtypeId < 0 || lastSubtypeId >= lastImi.getSubtypeCount()) {
                    return null;
                }
                return lastImi.getSubtypeAt(lastSubtypeId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    void setEnabledSessionInMainThread(SessionState session) {
        if (this.mEnabledSession != session) {
            if (this.mEnabledSession != null) {
                try {
                    this.mEnabledSession.method.setSessionEnabled(this.mEnabledSession.session, false);
                } catch (RemoteException e) {
                }
            }
            this.mEnabledSession = session;
            try {
                session.method.setSessionEnabled(session.session, true);
            } catch (RemoteException e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean chooseNewDefaultIMELocked() {
        InputMethodInfo imi = InputMethodUtils.getMostApplicableDefaultIME(this.mSettings.getEnabledInputMethodListLocked());
        if (imi != null) {
            resetSelectedInputMethodAndSubtypeLocked(imi.getId());
            return true;
        }
        return false;
    }

    void buildInputMethodListLocked(ArrayList<InputMethodInfo> list, HashMap<String, InputMethodInfo> map, boolean resetDefaultEnabledIme) {
        list.clear();
        map.clear();
        PackageManager pm = this.mContext.getPackageManager();
        String disabledSysImes = this.mSettings.getDisabledSystemInputMethods();
        if (disabledSysImes == null) {
        }
        List<ResolveInfo> services = pm.queryIntentServicesAsUser(new Intent(InputMethod.SERVICE_INTERFACE), 32896, this.mSettings.getCurrentUserId());
        HashMap<String, List<InputMethodSubtype>> additionalSubtypes = this.mFileManager.getAllAdditionalInputMethodSubtypes();
        for (int i = 0; i < services.size(); i++) {
            ResolveInfo ri = services.get(i);
            ServiceInfo si = ri.serviceInfo;
            ComponentName compName = new ComponentName(si.packageName, si.name);
            if (!Manifest.permission.BIND_INPUT_METHOD.equals(si.permission)) {
                Slog.w(TAG, "Skipping input method " + compName + ": it does not require the permission " + Manifest.permission.BIND_INPUT_METHOD);
            } else {
                try {
                    InputMethodInfo p = new InputMethodInfo(this.mContext, ri, additionalSubtypes);
                    list.add(p);
                    String id = p.getId();
                    map.put(id, p);
                } catch (IOException e) {
                    Slog.w(TAG, "Unable to load input method " + compName, e);
                } catch (XmlPullParserException e2) {
                    Slog.w(TAG, "Unable to load input method " + compName, e2);
                }
            }
        }
        if (resetDefaultEnabledIme) {
            ArrayList<InputMethodInfo> defaultEnabledIme = InputMethodUtils.getDefaultEnabledImes(this.mContext, this.mSystemReady, list);
            for (int i2 = 0; i2 < defaultEnabledIme.size(); i2++) {
                InputMethodInfo imi = defaultEnabledIme.get(i2);
                setInputMethodEnabledLocked(imi.getId(), true);
            }
        }
        String defaultImiId = this.mSettings.getSelectedInputMethod();
        if (!TextUtils.isEmpty(defaultImiId)) {
            if (!map.containsKey(defaultImiId)) {
                Slog.w(TAG, "Default IME is uninstalled. Choose new default IME.");
                if (chooseNewDefaultIMELocked()) {
                    updateFromSettingsLocked(true);
                    return;
                }
                return;
            }
            setInputMethodEnabledLocked(defaultImiId, true);
        }
    }

    private void showInputMethodMenu() {
        showInputMethodMenuInternal(false);
    }

    private void showInputMethodSubtypeMenu() {
        showInputMethodMenuInternal(true);
    }

    private void showInputMethodAndSubtypeEnabler(String inputMethodId) {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
        intent.setFlags(337641472);
        if (!TextUtils.isEmpty(inputMethodId)) {
            intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, inputMethodId);
        }
        this.mContext.startActivityAsUser(intent, null, UserHandle.CURRENT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConfigureInputMethods() {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.setFlags(337641472);
        this.mContext.startActivityAsUser(intent, null, UserHandle.CURRENT);
    }

    private boolean isScreenLocked() {
        return this.mKeyguardManager != null && this.mKeyguardManager.isKeyguardLocked() && this.mKeyguardManager.isKeyguardSecure();
    }

    private void showInputMethodMenuInternal(boolean showSubtypes) {
        int subtypeId;
        InputMethodSubtype currentSubtype;
        Context context = this.mContext;
        boolean isScreenLocked = isScreenLocked();
        String lastInputMethodId = this.mSettings.getSelectedInputMethod();
        int lastInputMethodSubtypeId = this.mSettings.getSelectedInputMethodSubtypeId(lastInputMethodId);
        synchronized (this.mMethodMap) {
            HashMap<InputMethodInfo, List<InputMethodSubtype>> immis = getExplicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked();
            if (immis == null || immis.size() == 0) {
                return;
            }
            hideInputMethodMenuLocked();
            List<ImeSubtypeListItem> imList = this.mImListManager.getSortedInputMethodAndSubtypeList(showSubtypes, this.mInputShown, isScreenLocked);
            if (lastInputMethodSubtypeId == -1 && (currentSubtype = getCurrentInputMethodSubtypeLocked()) != null) {
                InputMethodInfo currentImi = this.mMethodMap.get(this.mCurMethodId);
                lastInputMethodSubtypeId = InputMethodUtils.getSubtypeIdFromHashCode(currentImi, currentSubtype.hashCode());
            }
            int N = imList.size();
            this.mIms = new InputMethodInfo[N];
            this.mSubtypeIds = new int[N];
            int checkedItem = 0;
            for (int i = 0; i < N; i++) {
                ImeSubtypeListItem item = imList.get(i);
                this.mIms[i] = item.mImi;
                this.mSubtypeIds[i] = item.mSubtypeId;
                if (this.mIms[i].getId().equals(lastInputMethodId) && ((subtypeId = this.mSubtypeIds[i]) == -1 || ((lastInputMethodSubtypeId == -1 && subtypeId == 0) || subtypeId == lastInputMethodSubtypeId))) {
                    checkedItem = i;
                }
            }
            TypedArray a = context.obtainStyledAttributes(null, R.styleable.DialogPreference, 16842845, 0);
            this.mDialogBuilder = new AlertDialog.Builder(context).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.server.InputMethodManagerService.5
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialog) {
                    InputMethodManagerService.this.hideInputMethodMenu();
                }
            }).setIcon(a.getDrawable(0));
            a.recycle();
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tv = inflater.inflate(R.layout.input_method_switch_dialog_title, (ViewGroup) null);
            this.mDialogBuilder.setCustomTitle(tv);
            this.mSwitchingDialogTitleView = tv;
            this.mSwitchingDialogTitleView.findViewById(R.id.hard_keyboard_section).setVisibility(this.mWindowManagerService.isHardKeyboardAvailable() ? 0 : 8);
            Switch hardKeySwitch = (Switch) this.mSwitchingDialogTitleView.findViewById(R.id.hard_keyboard_switch);
            hardKeySwitch.setChecked(this.mWindowManagerService.isHardKeyboardEnabled());
            hardKeySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.server.InputMethodManagerService.6
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    InputMethodManagerService.this.mWindowManagerService.setHardKeyboardEnabled(isChecked);
                    InputMethodManagerService.this.hideInputMethodMenu();
                }
            });
            final ImeSubtypeListAdapter adapter = new ImeSubtypeListAdapter(context, R.layout.simple_list_item_2_single_choice, imList, checkedItem);
            this.mDialogBuilder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() { // from class: com.android.server.InputMethodManagerService.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    synchronized (InputMethodManagerService.this.mMethodMap) {
                        if (InputMethodManagerService.this.mIms == null || InputMethodManagerService.this.mIms.length <= which || InputMethodManagerService.this.mSubtypeIds == null || InputMethodManagerService.this.mSubtypeIds.length <= which) {
                            return;
                        }
                        InputMethodInfo im = InputMethodManagerService.this.mIms[which];
                        int subtypeId2 = InputMethodManagerService.this.mSubtypeIds[which];
                        adapter.mCheckedItem = which;
                        adapter.notifyDataSetChanged();
                        InputMethodManagerService.this.hideInputMethodMenu();
                        if (im != null) {
                            if (subtypeId2 < 0 || subtypeId2 >= im.getSubtypeCount()) {
                                subtypeId2 = -1;
                            }
                            InputMethodManagerService.this.setInputMethodLocked(im.getId(), subtypeId2);
                        }
                    }
                }
            });
            if (showSubtypes && !isScreenLocked) {
                this.mDialogBuilder.setPositiveButton(R.string.configure_input_methods, new DialogInterface.OnClickListener() { // from class: com.android.server.InputMethodManagerService.8
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                        InputMethodManagerService.this.showConfigureInputMethods();
                    }
                });
            }
            this.mSwitchingDialog = this.mDialogBuilder.create();
            this.mSwitchingDialog.setCanceledOnTouchOutside(true);
            this.mSwitchingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_INPUT_METHOD_DIALOG);
            this.mSwitchingDialog.getWindow().getAttributes().privateFlags |= 16;
            this.mSwitchingDialog.getWindow().getAttributes().setTitle("Select input method");
            this.mSwitchingDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManagerService$ImeSubtypeListItem.class */
    public static class ImeSubtypeListItem implements Comparable<ImeSubtypeListItem> {
        public final CharSequence mImeName;
        public final CharSequence mSubtypeName;
        public final InputMethodInfo mImi;
        public final int mSubtypeId;
        private final boolean mIsSystemLocale;
        private final boolean mIsSystemLanguage;

        public ImeSubtypeListItem(CharSequence imeName, CharSequence subtypeName, InputMethodInfo imi, int subtypeId, String subtypeLocale, String systemLocale) {
            this.mImeName = imeName;
            this.mSubtypeName = subtypeName;
            this.mImi = imi;
            this.mSubtypeId = subtypeId;
            if (TextUtils.isEmpty(subtypeLocale)) {
                this.mIsSystemLocale = false;
                this.mIsSystemLanguage = false;
                return;
            }
            this.mIsSystemLocale = subtypeLocale.equals(systemLocale);
            this.mIsSystemLanguage = this.mIsSystemLocale || subtypeLocale.startsWith(systemLocale.substring(0, 2));
        }

        @Override // java.lang.Comparable
        public int compareTo(ImeSubtypeListItem other) {
            if (TextUtils.isEmpty(this.mImeName)) {
                return 1;
            }
            if (TextUtils.isEmpty(other.mImeName)) {
                return -1;
            }
            if (!TextUtils.equals(this.mImeName, other.mImeName)) {
                return this.mImeName.toString().compareTo(other.mImeName.toString());
            }
            if (TextUtils.equals(this.mSubtypeName, other.mSubtypeName)) {
                return 0;
            }
            if (this.mIsSystemLocale) {
                return -1;
            }
            if (other.mIsSystemLocale) {
                return 1;
            }
            if (this.mIsSystemLanguage) {
                return -1;
            }
            if (other.mIsSystemLanguage || TextUtils.isEmpty(this.mSubtypeName)) {
                return 1;
            }
            if (TextUtils.isEmpty(other.mSubtypeName)) {
                return -1;
            }
            return this.mSubtypeName.toString().compareTo(other.mSubtypeName.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManagerService$ImeSubtypeListAdapter.class */
    public static class ImeSubtypeListAdapter extends ArrayAdapter<ImeSubtypeListItem> {
        private final LayoutInflater mInflater;
        private final int mTextViewResourceId;
        private final List<ImeSubtypeListItem> mItemsList;
        public int mCheckedItem;

        public ImeSubtypeListAdapter(Context context, int textViewResourceId, List<ImeSubtypeListItem> itemsList, int checkedItem) {
            super(context, textViewResourceId, itemsList);
            this.mTextViewResourceId = textViewResourceId;
            this.mItemsList = itemsList;
            this.mCheckedItem = checkedItem;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView != null ? convertView : this.mInflater.inflate(this.mTextViewResourceId, (ViewGroup) null);
            if (position < 0 || position >= this.mItemsList.size()) {
                return view;
            }
            ImeSubtypeListItem item = this.mItemsList.get(position);
            CharSequence imeName = item.mImeName;
            CharSequence subtypeName = item.mSubtypeName;
            TextView firstTextView = (TextView) view.findViewById(16908308);
            TextView secondTextView = (TextView) view.findViewById(16908309);
            if (TextUtils.isEmpty(subtypeName)) {
                firstTextView.setText(imeName);
                secondTextView.setVisibility(8);
            } else {
                firstTextView.setText(subtypeName);
                secondTextView.setText(imeName);
                secondTextView.setVisibility(0);
            }
            RadioButton radioButton = (RadioButton) view.findViewById(R.id.radio);
            radioButton.setChecked(position == this.mCheckedItem);
            return view;
        }
    }

    void hideInputMethodMenu() {
        synchronized (this.mMethodMap) {
            hideInputMethodMenuLocked();
        }
    }

    void hideInputMethodMenuLocked() {
        if (this.mSwitchingDialog != null) {
            this.mSwitchingDialog.dismiss();
            this.mSwitchingDialog = null;
        }
        this.mDialogBuilder = null;
        this.mIms = null;
    }

    boolean setInputMethodEnabledLocked(String id, boolean enabled) {
        InputMethodInfo imm = this.mMethodMap.get(id);
        if (imm == null) {
            throw new IllegalArgumentException("Unknown id: " + this.mCurMethodId);
        }
        List<Pair<String, ArrayList<String>>> enabledInputMethodsList = this.mSettings.getEnabledInputMethodsAndSubtypeListLocked();
        if (enabled) {
            for (Pair<String, ArrayList<String>> pair : enabledInputMethodsList) {
                if (pair.first.equals(id)) {
                    return true;
                }
            }
            this.mSettings.appendAndPutEnabledInputMethodLocked(id, false);
            return false;
        }
        StringBuilder builder = new StringBuilder();
        if (this.mSettings.buildAndPutEnabledInputMethodsStrRemovingIdLocked(builder, enabledInputMethodsList, id)) {
            String selId = this.mSettings.getSelectedInputMethod();
            if (id.equals(selId) && !chooseNewDefaultIMELocked()) {
                Slog.i(TAG, "Can't find new IME, unsetting the current input method.");
                resetSelectedInputMethodAndSubtypeLocked("");
                return true;
            }
            return true;
        }
        return false;
    }

    private void setSelectedInputMethodAndSubtypeLocked(InputMethodInfo imi, int subtypeId, boolean setSubtypeOnly) {
        this.mSettings.saveCurrentInputMethodAndSubtypeToHistory(this.mCurMethodId, this.mCurrentSubtype);
        if (imi == null || subtypeId < 0) {
            this.mSettings.putSelectedSubtype(-1);
            this.mCurrentSubtype = null;
        } else if (subtypeId < imi.getSubtypeCount()) {
            InputMethodSubtype subtype = imi.getSubtypeAt(subtypeId);
            this.mSettings.putSelectedSubtype(subtype.hashCode());
            this.mCurrentSubtype = subtype;
        } else {
            this.mSettings.putSelectedSubtype(-1);
            this.mCurrentSubtype = getCurrentInputMethodSubtypeLocked();
        }
        if (this.mSystemReady && !setSubtypeOnly) {
            this.mSettings.putSelectedInputMethod(imi != null ? imi.getId() : "");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetSelectedInputMethodAndSubtypeLocked(String newDefaultIme) {
        String subtypeHashCode;
        InputMethodInfo imi = this.mMethodMap.get(newDefaultIme);
        int lastSubtypeId = -1;
        if (imi != null && !TextUtils.isEmpty(newDefaultIme) && (subtypeHashCode = this.mSettings.getLastSubtypeForInputMethodLocked(newDefaultIme)) != null) {
            try {
                lastSubtypeId = InputMethodUtils.getSubtypeIdFromHashCode(imi, Integer.valueOf(subtypeHashCode).intValue());
            } catch (NumberFormatException e) {
                Slog.w(TAG, "HashCode for subtype looks broken: " + subtypeHashCode, e);
            }
        }
        setSelectedInputMethodAndSubtypeLocked(imi, lastSubtypeId, false);
    }

    private Pair<InputMethodInfo, InputMethodSubtype> findLastResortApplicableShortcutInputMethodAndSubtypeLocked(String mode) {
        List<InputMethodInfo> imis = this.mSettings.getEnabledInputMethodListLocked();
        InputMethodInfo mostApplicableIMI = null;
        InputMethodSubtype mostApplicableSubtype = null;
        boolean foundInSystemIME = false;
        Iterator i$ = imis.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            InputMethodInfo imi = i$.next();
            String imiId = imi.getId();
            if (!foundInSystemIME || imiId.equals(this.mCurMethodId)) {
                InputMethodSubtype subtype = null;
                List<InputMethodSubtype> enabledSubtypes = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imi, true);
                if (this.mCurrentSubtype != null) {
                    subtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, enabledSubtypes, mode, this.mCurrentSubtype.getLocale(), false);
                }
                if (subtype == null) {
                    subtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, enabledSubtypes, mode, null, true);
                }
                ArrayList<InputMethodSubtype> overridingImplicitlyEnabledSubtypes = InputMethodUtils.getOverridingImplicitlyEnabledSubtypes(imi, mode);
                ArrayList<InputMethodSubtype> subtypesForSearch = overridingImplicitlyEnabledSubtypes.isEmpty() ? InputMethodUtils.getSubtypes(imi) : overridingImplicitlyEnabledSubtypes;
                if (subtype == null && this.mCurrentSubtype != null) {
                    subtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, subtypesForSearch, mode, this.mCurrentSubtype.getLocale(), false);
                }
                if (subtype == null) {
                    subtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, subtypesForSearch, mode, null, true);
                }
                if (subtype == null) {
                    continue;
                } else if (imiId.equals(this.mCurMethodId)) {
                    mostApplicableIMI = imi;
                    mostApplicableSubtype = subtype;
                    break;
                } else if (!foundInSystemIME) {
                    mostApplicableIMI = imi;
                    mostApplicableSubtype = subtype;
                    if ((imi.getServiceInfo().applicationInfo.flags & 1) != 0) {
                        foundInSystemIME = true;
                    }
                }
            }
        }
        if (mostApplicableIMI != null) {
            return new Pair<>(mostApplicableIMI, mostApplicableSubtype);
        }
        return null;
    }

    @Override // com.android.internal.view.IInputMethodManager
    public InputMethodSubtype getCurrentInputMethodSubtype() {
        InputMethodSubtype currentInputMethodSubtypeLocked;
        if (!calledFromValidUser()) {
            return null;
        }
        synchronized (this.mMethodMap) {
            currentInputMethodSubtypeLocked = getCurrentInputMethodSubtypeLocked();
        }
        return currentInputMethodSubtypeLocked;
    }

    private InputMethodSubtype getCurrentInputMethodSubtypeLocked() {
        if (this.mCurMethodId == null) {
            return null;
        }
        boolean subtypeIsSelected = this.mSettings.isSubtypeSelected();
        InputMethodInfo imi = this.mMethodMap.get(this.mCurMethodId);
        if (imi == null || imi.getSubtypeCount() == 0) {
            return null;
        }
        if (!subtypeIsSelected || this.mCurrentSubtype == null || !InputMethodUtils.isValidSubtypeId(imi, this.mCurrentSubtype.hashCode())) {
            int subtypeId = this.mSettings.getSelectedInputMethodSubtypeId(this.mCurMethodId);
            if (subtypeId == -1) {
                List<InputMethodSubtype> explicitlyOrImplicitlyEnabledSubtypes = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imi, true);
                if (explicitlyOrImplicitlyEnabledSubtypes.size() == 1) {
                    this.mCurrentSubtype = explicitlyOrImplicitlyEnabledSubtypes.get(0);
                } else if (explicitlyOrImplicitlyEnabledSubtypes.size() > 1) {
                    this.mCurrentSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, explicitlyOrImplicitlyEnabledSubtypes, InputMethodUtils.SUBTYPE_MODE_KEYBOARD, null, true);
                    if (this.mCurrentSubtype == null) {
                        this.mCurrentSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, explicitlyOrImplicitlyEnabledSubtypes, null, null, true);
                    }
                }
            } else {
                this.mCurrentSubtype = InputMethodUtils.getSubtypes(imi).get(subtypeId);
            }
        }
        return this.mCurrentSubtype;
    }

    private void addShortcutInputMethodAndSubtypes(InputMethodInfo imi, InputMethodSubtype subtype) {
        if (this.mShortcutInputMethodsAndSubtypes.containsKey(imi)) {
            this.mShortcutInputMethodsAndSubtypes.get(imi).add(subtype);
            return;
        }
        ArrayList<InputMethodSubtype> subtypes = new ArrayList<>();
        subtypes.add(subtype);
        this.mShortcutInputMethodsAndSubtypes.put(imi, subtypes);
    }

    @Override // com.android.internal.view.IInputMethodManager
    public List getShortcutInputMethodsAndSubtypes() {
        synchronized (this.mMethodMap) {
            ArrayList<Object> ret = new ArrayList<>();
            if (this.mShortcutInputMethodsAndSubtypes.size() == 0) {
                Pair<InputMethodInfo, InputMethodSubtype> info = findLastResortApplicableShortcutInputMethodAndSubtypeLocked(InputMethodUtils.SUBTYPE_MODE_VOICE);
                if (info != null) {
                    ret.add(info.first);
                    ret.add(info.second);
                }
                return ret;
            }
            for (InputMethodInfo imi : this.mShortcutInputMethodsAndSubtypes.keySet()) {
                ret.add(imi);
                Iterator i$ = this.mShortcutInputMethodsAndSubtypes.get(imi).iterator();
                while (i$.hasNext()) {
                    InputMethodSubtype subtype = (InputMethodSubtype) i$.next();
                    ret.add(subtype);
                }
            }
            return ret;
        }
    }

    @Override // com.android.internal.view.IInputMethodManager
    public boolean setCurrentInputMethodSubtype(InputMethodSubtype subtype) {
        if (!calledFromValidUser()) {
            return false;
        }
        synchronized (this.mMethodMap) {
            if (subtype != null) {
                if (this.mCurMethodId != null) {
                    InputMethodInfo imi = this.mMethodMap.get(this.mCurMethodId);
                    int subtypeId = InputMethodUtils.getSubtypeIdFromHashCode(imi, subtype.hashCode());
                    if (subtypeId != -1) {
                        setInputMethodLocked(this.mCurMethodId, subtypeId);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManagerService$InputMethodAndSubtypeListManager.class */
    public static class InputMethodAndSubtypeListManager {
        private final Context mContext;
        private final PackageManager mPm;
        private final InputMethodManagerService mImms;
        private final String mSystemLocaleStr;
        private final TreeMap<InputMethodInfo, List<InputMethodSubtype>> mSortedImmis = new TreeMap<>(new Comparator<InputMethodInfo>() { // from class: com.android.server.InputMethodManagerService.InputMethodAndSubtypeListManager.1
            @Override // java.util.Comparator
            public int compare(InputMethodInfo imi1, InputMethodInfo imi2) {
                if (imi2 == null) {
                    return 0;
                }
                if (imi1 == null) {
                    return 1;
                }
                if (InputMethodAndSubtypeListManager.this.mPm != null) {
                    CharSequence imiId1 = ((Object) imi1.loadLabel(InputMethodAndSubtypeListManager.this.mPm)) + Separators.SLASH + imi1.getId();
                    CharSequence imiId2 = ((Object) imi2.loadLabel(InputMethodAndSubtypeListManager.this.mPm)) + Separators.SLASH + imi2.getId();
                    return imiId1.toString().compareTo(imiId2.toString());
                }
                return imi1.getId().compareTo(imi2.getId());
            }
        });

        public InputMethodAndSubtypeListManager(Context context, InputMethodManagerService imms) {
            this.mContext = context;
            this.mPm = context.getPackageManager();
            this.mImms = imms;
            Locale locale = context.getResources().getConfiguration().locale;
            this.mSystemLocaleStr = locale != null ? locale.toString() : "";
        }

        public ImeSubtypeListItem getNextInputMethod(boolean onlyCurrentIme, InputMethodInfo imi, InputMethodSubtype subtype) {
            if (imi == null) {
                return null;
            }
            List<ImeSubtypeListItem> imList = getSortedInputMethodAndSubtypeList();
            if (imList.size() <= 1) {
                return null;
            }
            int N = imList.size();
            int currentSubtypeId = subtype != null ? InputMethodUtils.getSubtypeIdFromHashCode(imi, subtype.hashCode()) : -1;
            for (int i = 0; i < N; i++) {
                ImeSubtypeListItem isli = imList.get(i);
                if (isli.mImi.equals(imi) && isli.mSubtypeId == currentSubtypeId) {
                    if (!onlyCurrentIme) {
                        return imList.get((i + 1) % N);
                    } else {
                        for (int j = 0; j < N - 1; j++) {
                            ImeSubtypeListItem candidate = imList.get(((i + j) + 1) % N);
                            if (candidate.mImi.equals(imi)) {
                                return candidate;
                            }
                        }
                        return null;
                    }
                }
            }
            return null;
        }

        public List<ImeSubtypeListItem> getSortedInputMethodAndSubtypeList() {
            return getSortedInputMethodAndSubtypeList(true, false, false);
        }

        public List<ImeSubtypeListItem> getSortedInputMethodAndSubtypeList(boolean showSubtypes, boolean inputShown, boolean isScreenLocked) {
            ArrayList<ImeSubtypeListItem> imList = new ArrayList<>();
            HashMap<InputMethodInfo, List<InputMethodSubtype>> immis = this.mImms.getExplicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked();
            if (immis == null || immis.size() == 0) {
                return Collections.emptyList();
            }
            this.mSortedImmis.clear();
            this.mSortedImmis.putAll(immis);
            for (InputMethodInfo imi : this.mSortedImmis.keySet()) {
                if (imi != null) {
                    List<InputMethodSubtype> explicitlyOrImplicitlyEnabledSubtypeList = immis.get(imi);
                    HashSet<String> enabledSubtypeSet = new HashSet<>();
                    for (InputMethodSubtype subtype : explicitlyOrImplicitlyEnabledSubtypeList) {
                        enabledSubtypeSet.add(String.valueOf(subtype.hashCode()));
                    }
                    CharSequence imeLabel = imi.loadLabel(this.mPm);
                    if (showSubtypes && enabledSubtypeSet.size() > 0) {
                        int subtypeCount = imi.getSubtypeCount();
                        for (int j = 0; j < subtypeCount; j++) {
                            InputMethodSubtype subtype2 = imi.getSubtypeAt(j);
                            String subtypeHashCode = String.valueOf(subtype2.hashCode());
                            if (enabledSubtypeSet.contains(subtypeHashCode) && ((inputShown && !isScreenLocked) || !subtype2.isAuxiliary())) {
                                CharSequence subtypeLabel = subtype2.overridesImplicitlyEnabledSubtype() ? null : subtype2.getDisplayName(this.mContext, imi.getPackageName(), imi.getServiceInfo().applicationInfo);
                                imList.add(new ImeSubtypeListItem(imeLabel, subtypeLabel, imi, j, subtype2.getLocale(), this.mSystemLocaleStr));
                                enabledSubtypeSet.remove(subtypeHashCode);
                            }
                        }
                    } else {
                        imList.add(new ImeSubtypeListItem(imeLabel, null, imi, -1, null, this.mSystemLocaleStr));
                    }
                }
            }
            Collections.sort(imList);
            return imList;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManagerService$InputMethodFileManager.class */
    public static class InputMethodFileManager {
        private static final String SYSTEM_PATH = "system";
        private static final String INPUT_METHOD_PATH = "inputmethod";
        private static final String ADDITIONAL_SUBTYPES_FILE_NAME = "subtypes.xml";
        private static final String NODE_SUBTYPES = "subtypes";
        private static final String NODE_SUBTYPE = "subtype";
        private static final String NODE_IMI = "imi";
        private static final String ATTR_ID = "id";
        private static final String ATTR_LABEL = "label";
        private static final String ATTR_ICON = "icon";
        private static final String ATTR_IME_SUBTYPE_LOCALE = "imeSubtypeLocale";
        private static final String ATTR_IME_SUBTYPE_MODE = "imeSubtypeMode";
        private static final String ATTR_IME_SUBTYPE_EXTRA_VALUE = "imeSubtypeExtraValue";
        private static final String ATTR_IS_AUXILIARY = "isAuxiliary";
        private final AtomicFile mAdditionalInputMethodSubtypeFile;
        private final HashMap<String, InputMethodInfo> mMethodMap;
        private final HashMap<String, List<InputMethodSubtype>> mAdditionalSubtypesMap = new HashMap<>();

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.InputMethodFileManager.readAdditionalInputMethodSubtypes(java.util.HashMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>>, android.util.AtomicFile):void, file: InputMethodManagerService$InputMethodFileManager.class
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
        private static void readAdditionalInputMethodSubtypes(java.util.HashMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r0, android.util.AtomicFile r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.InputMethodManagerService.InputMethodFileManager.readAdditionalInputMethodSubtypes(java.util.HashMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>>, android.util.AtomicFile):void, file: InputMethodManagerService$InputMethodFileManager.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.InputMethodManagerService.InputMethodFileManager.readAdditionalInputMethodSubtypes(java.util.HashMap, android.util.AtomicFile):void");
        }

        public InputMethodFileManager(HashMap<String, InputMethodInfo> methodMap, int userId) {
            if (methodMap == null) {
                throw new NullPointerException("methodMap is null");
            }
            this.mMethodMap = methodMap;
            File systemDir = userId == 0 ? new File(Environment.getDataDirectory(), SYSTEM_PATH) : Environment.getUserSystemDirectory(userId);
            File inputMethodDir = new File(systemDir, INPUT_METHOD_PATH);
            if (!inputMethodDir.mkdirs()) {
                Slog.w(InputMethodManagerService.TAG, "Couldn't create dir.: " + inputMethodDir.getAbsolutePath());
            }
            File subtypeFile = new File(inputMethodDir, ADDITIONAL_SUBTYPES_FILE_NAME);
            this.mAdditionalInputMethodSubtypeFile = new AtomicFile(subtypeFile);
            if (!subtypeFile.exists()) {
                writeAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile, methodMap);
            } else {
                readAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void deleteAllInputMethodSubtypes(String imiId) {
            synchronized (this.mMethodMap) {
                this.mAdditionalSubtypesMap.remove(imiId);
                writeAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile, this.mMethodMap);
            }
        }

        public void addInputMethodSubtypes(InputMethodInfo imi, InputMethodSubtype[] additionalSubtypes) {
            synchronized (this.mMethodMap) {
                ArrayList<InputMethodSubtype> subtypes = new ArrayList<>();
                for (InputMethodSubtype subtype : additionalSubtypes) {
                    if (!subtypes.contains(subtype)) {
                        subtypes.add(subtype);
                    } else {
                        Slog.w(InputMethodManagerService.TAG, "Duplicated subtype definition found: " + subtype.getLocale() + ", " + subtype.getMode());
                    }
                }
                this.mAdditionalSubtypesMap.put(imi.getId(), subtypes);
                writeAdditionalInputMethodSubtypes(this.mAdditionalSubtypesMap, this.mAdditionalInputMethodSubtypeFile, this.mMethodMap);
            }
        }

        public HashMap<String, List<InputMethodSubtype>> getAllAdditionalInputMethodSubtypes() {
            HashMap<String, List<InputMethodSubtype>> hashMap;
            synchronized (this.mMethodMap) {
                hashMap = this.mAdditionalSubtypesMap;
            }
            return hashMap;
        }

        private static void writeAdditionalInputMethodSubtypes(HashMap<String, List<InputMethodSubtype>> allSubtypes, AtomicFile subtypesFile, HashMap<String, InputMethodInfo> methodMap) {
            boolean isSetMethodMap = methodMap != null && methodMap.size() > 0;
            FileOutputStream fos = null;
            try {
                fos = subtypesFile.startWrite();
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(fos, "utf-8");
                out.startDocument(null, true);
                out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                out.startTag(null, NODE_SUBTYPES);
                for (String imiId : allSubtypes.keySet()) {
                    if (isSetMethodMap && !methodMap.containsKey(imiId)) {
                        Slog.w(InputMethodManagerService.TAG, "IME uninstalled or not valid.: " + imiId);
                    } else {
                        out.startTag(null, NODE_IMI);
                        out.attribute(null, "id", imiId);
                        List<InputMethodSubtype> subtypesList = allSubtypes.get(imiId);
                        int N = subtypesList.size();
                        for (int i = 0; i < N; i++) {
                            InputMethodSubtype subtype = subtypesList.get(i);
                            out.startTag(null, NODE_SUBTYPE);
                            out.attribute(null, "icon", String.valueOf(subtype.getIconResId()));
                            out.attribute(null, "label", String.valueOf(subtype.getNameResId()));
                            out.attribute(null, ATTR_IME_SUBTYPE_LOCALE, subtype.getLocale());
                            out.attribute(null, ATTR_IME_SUBTYPE_MODE, subtype.getMode());
                            out.attribute(null, ATTR_IME_SUBTYPE_EXTRA_VALUE, subtype.getExtraValue());
                            out.attribute(null, ATTR_IS_AUXILIARY, String.valueOf(subtype.isAuxiliary() ? 1 : 0));
                            out.endTag(null, NODE_SUBTYPE);
                        }
                        out.endTag(null, NODE_IMI);
                    }
                }
                out.endTag(null, NODE_SUBTYPES);
                out.endDocument();
                subtypesFile.finishWrite(fos);
            } catch (IOException e) {
                Slog.w(InputMethodManagerService.TAG, "Error writing subtypes", e);
                if (fos != null) {
                    subtypesFile.failWrite(fos);
                }
            }
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        ClientState client;
        IInputMethod method;
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump InputMethodManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        Printer p = new PrintWriterPrinter(pw);
        synchronized (this.mMethodMap) {
            p.println("Current Input Method Manager state:");
            int N = this.mMethodList.size();
            p.println("  Input Methods:");
            for (int i = 0; i < N; i++) {
                InputMethodInfo info = this.mMethodList.get(i);
                p.println("  InputMethod #" + i + Separators.COLON);
                info.dump(p, "    ");
            }
            p.println("  Clients:");
            for (ClientState ci : this.mClients.values()) {
                p.println("  Client " + ci + Separators.COLON);
                p.println("    client=" + ci.client);
                p.println("    inputContext=" + ci.inputContext);
                p.println("    sessionRequested=" + ci.sessionRequested);
                p.println("    curSession=" + ci.curSession);
            }
            p.println("  mCurMethodId=" + this.mCurMethodId);
            client = this.mCurClient;
            p.println("  mCurClient=" + client + " mCurSeq=" + this.mCurSeq);
            p.println("  mCurFocusedWindow=" + this.mCurFocusedWindow);
            p.println("  mCurId=" + this.mCurId + " mHaveConnect=" + this.mHaveConnection + " mBoundToMethod=" + this.mBoundToMethod);
            p.println("  mCurToken=" + this.mCurToken);
            p.println("  mCurIntent=" + this.mCurIntent);
            method = this.mCurMethod;
            p.println("  mCurMethod=" + this.mCurMethod);
            p.println("  mEnabledSession=" + this.mEnabledSession);
            p.println("  mShowRequested=" + this.mShowRequested + " mShowExplicitlyRequested=" + this.mShowExplicitlyRequested + " mShowForced=" + this.mShowForced + " mInputShown=" + this.mInputShown);
            p.println("  mSystemReady=" + this.mSystemReady + " mScreenOn=" + this.mScreenOn);
        }
        p.println(Separators.SP);
        if (client != null) {
            pw.flush();
            try {
                client.client.asBinder().dump(fd, args);
            } catch (RemoteException e) {
                p.println("Input method client dead: " + e);
            }
        } else {
            p.println("No input method client.");
        }
        p.println(Separators.SP);
        if (method != null) {
            pw.flush();
            try {
                method.asBinder().dump(fd, args);
                return;
            } catch (RemoteException e2) {
                p.println("Input method service dead: " + e2);
                return;
            }
        }
        p.println("No input method service.");
    }
}