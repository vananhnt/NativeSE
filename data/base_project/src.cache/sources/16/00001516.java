package android.view;

import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.IApplicationToken;
import android.view.IInputFilter;
import android.view.IMagnificationCallbacks;
import android.view.IOnKeyguardExitResult;
import android.view.IRotationWatcher;
import android.view.IWindowSession;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;

/* loaded from: IWindowManager.class */
public interface IWindowManager extends IInterface {
    boolean startViewServer(int i) throws RemoteException;

    boolean stopViewServer() throws RemoteException;

    boolean isViewServerRunning() throws RemoteException;

    IWindowSession openSession(IInputMethodClient iInputMethodClient, IInputContext iInputContext) throws RemoteException;

    boolean inputMethodClientHasFocus(IInputMethodClient iInputMethodClient) throws RemoteException;

    void getInitialDisplaySize(int i, Point point) throws RemoteException;

    void getBaseDisplaySize(int i, Point point) throws RemoteException;

    void setForcedDisplaySize(int i, int i2, int i3) throws RemoteException;

    void clearForcedDisplaySize(int i) throws RemoteException;

    int getInitialDisplayDensity(int i) throws RemoteException;

    int getBaseDisplayDensity(int i) throws RemoteException;

    void setForcedDisplayDensity(int i, int i2) throws RemoteException;

    void clearForcedDisplayDensity(int i) throws RemoteException;

    void setOverscan(int i, int i2, int i3, int i4, int i5) throws RemoteException;

    void pauseKeyDispatching(IBinder iBinder) throws RemoteException;

    void resumeKeyDispatching(IBinder iBinder) throws RemoteException;

    void setEventDispatching(boolean z) throws RemoteException;

    void addWindowToken(IBinder iBinder, int i) throws RemoteException;

    void removeWindowToken(IBinder iBinder) throws RemoteException;

    void addAppToken(int i, IApplicationToken iApplicationToken, int i2, int i3, int i4, boolean z, boolean z2, int i5) throws RemoteException;

    void setAppGroupId(IBinder iBinder, int i) throws RemoteException;

    void setAppOrientation(IApplicationToken iApplicationToken, int i) throws RemoteException;

    int getAppOrientation(IApplicationToken iApplicationToken) throws RemoteException;

    void setFocusedApp(IBinder iBinder, boolean z) throws RemoteException;

    void prepareAppTransition(int i, boolean z) throws RemoteException;

    int getPendingAppTransition() throws RemoteException;

    void overridePendingAppTransition(String str, int i, int i2, IRemoteCallback iRemoteCallback) throws RemoteException;

    void overridePendingAppTransitionScaleUp(int i, int i2, int i3, int i4) throws RemoteException;

    void overridePendingAppTransitionThumb(Bitmap bitmap, int i, int i2, IRemoteCallback iRemoteCallback, boolean z) throws RemoteException;

    void executeAppTransition() throws RemoteException;

    void setAppStartingWindow(IBinder iBinder, String str, int i, CompatibilityInfo compatibilityInfo, CharSequence charSequence, int i2, int i3, int i4, int i5, IBinder iBinder2, boolean z) throws RemoteException;

    void setAppWillBeHidden(IBinder iBinder) throws RemoteException;

    void setAppVisibility(IBinder iBinder, boolean z) throws RemoteException;

    void startAppFreezingScreen(IBinder iBinder, int i) throws RemoteException;

    void stopAppFreezingScreen(IBinder iBinder, boolean z) throws RemoteException;

    void removeAppToken(IBinder iBinder) throws RemoteException;

    Configuration updateOrientationFromAppTokens(Configuration configuration, IBinder iBinder) throws RemoteException;

    void setNewConfiguration(Configuration configuration) throws RemoteException;

    void startFreezingScreen(int i, int i2) throws RemoteException;

    void stopFreezingScreen() throws RemoteException;

    void disableKeyguard(IBinder iBinder, String str) throws RemoteException;

    void reenableKeyguard(IBinder iBinder) throws RemoteException;

    void exitKeyguardSecurely(IOnKeyguardExitResult iOnKeyguardExitResult) throws RemoteException;

    boolean isKeyguardLocked() throws RemoteException;

    boolean isKeyguardSecure() throws RemoteException;

    boolean inKeyguardRestrictedInputMode() throws RemoteException;

    void dismissKeyguard() throws RemoteException;

    void closeSystemDialogs(String str) throws RemoteException;

    float getAnimationScale(int i) throws RemoteException;

    float[] getAnimationScales() throws RemoteException;

    void setAnimationScale(int i, float f) throws RemoteException;

    void setAnimationScales(float[] fArr) throws RemoteException;

    void setInTouchMode(boolean z) throws RemoteException;

    void showStrictModeViolation(boolean z) throws RemoteException;

    void setStrictModeVisualIndicatorPreference(String str) throws RemoteException;

    void updateRotation(boolean z, boolean z2) throws RemoteException;

    int getRotation() throws RemoteException;

    int watchRotation(IRotationWatcher iRotationWatcher) throws RemoteException;

    void removeRotationWatcher(IRotationWatcher iRotationWatcher) throws RemoteException;

    int getPreferredOptionsPanelGravity() throws RemoteException;

    void freezeRotation(int i) throws RemoteException;

    void thawRotation() throws RemoteException;

    boolean isRotationFrozen() throws RemoteException;

    Bitmap screenshotApplications(IBinder iBinder, int i, int i2, int i3, boolean z) throws RemoteException;

    void statusBarVisibilityChanged(int i) throws RemoteException;

    boolean waitForWindowDrawn(IBinder iBinder, IRemoteCallback iRemoteCallback) throws RemoteException;

    boolean hasNavigationBar() throws RemoteException;

    void lockNow(Bundle bundle) throws RemoteException;

    IBinder getFocusedWindowToken() throws RemoteException;

    void setInputFilter(IInputFilter iInputFilter) throws RemoteException;

    void getWindowFrame(IBinder iBinder, Rect rect) throws RemoteException;

    boolean isSafeModeEnabled() throws RemoteException;

    void setMagnificationCallbacks(IMagnificationCallbacks iMagnificationCallbacks) throws RemoteException;

    void setMagnificationSpec(MagnificationSpec magnificationSpec) throws RemoteException;

    MagnificationSpec getCompatibleMagnificationSpecForWindow(IBinder iBinder) throws RemoteException;

    void setTouchExplorationEnabled(boolean z) throws RemoteException;

    /* loaded from: IWindowManager$Stub.class */
    public static abstract class Stub extends Binder implements IWindowManager {
        private static final String DESCRIPTOR = "android.view.IWindowManager";
        static final int TRANSACTION_startViewServer = 1;
        static final int TRANSACTION_stopViewServer = 2;
        static final int TRANSACTION_isViewServerRunning = 3;
        static final int TRANSACTION_openSession = 4;
        static final int TRANSACTION_inputMethodClientHasFocus = 5;
        static final int TRANSACTION_getInitialDisplaySize = 6;
        static final int TRANSACTION_getBaseDisplaySize = 7;
        static final int TRANSACTION_setForcedDisplaySize = 8;
        static final int TRANSACTION_clearForcedDisplaySize = 9;
        static final int TRANSACTION_getInitialDisplayDensity = 10;
        static final int TRANSACTION_getBaseDisplayDensity = 11;
        static final int TRANSACTION_setForcedDisplayDensity = 12;
        static final int TRANSACTION_clearForcedDisplayDensity = 13;
        static final int TRANSACTION_setOverscan = 14;
        static final int TRANSACTION_pauseKeyDispatching = 15;
        static final int TRANSACTION_resumeKeyDispatching = 16;
        static final int TRANSACTION_setEventDispatching = 17;
        static final int TRANSACTION_addWindowToken = 18;
        static final int TRANSACTION_removeWindowToken = 19;
        static final int TRANSACTION_addAppToken = 20;
        static final int TRANSACTION_setAppGroupId = 21;
        static final int TRANSACTION_setAppOrientation = 22;
        static final int TRANSACTION_getAppOrientation = 23;
        static final int TRANSACTION_setFocusedApp = 24;
        static final int TRANSACTION_prepareAppTransition = 25;
        static final int TRANSACTION_getPendingAppTransition = 26;
        static final int TRANSACTION_overridePendingAppTransition = 27;
        static final int TRANSACTION_overridePendingAppTransitionScaleUp = 28;
        static final int TRANSACTION_overridePendingAppTransitionThumb = 29;
        static final int TRANSACTION_executeAppTransition = 30;
        static final int TRANSACTION_setAppStartingWindow = 31;
        static final int TRANSACTION_setAppWillBeHidden = 32;
        static final int TRANSACTION_setAppVisibility = 33;
        static final int TRANSACTION_startAppFreezingScreen = 34;
        static final int TRANSACTION_stopAppFreezingScreen = 35;
        static final int TRANSACTION_removeAppToken = 36;
        static final int TRANSACTION_updateOrientationFromAppTokens = 37;
        static final int TRANSACTION_setNewConfiguration = 38;
        static final int TRANSACTION_startFreezingScreen = 39;
        static final int TRANSACTION_stopFreezingScreen = 40;
        static final int TRANSACTION_disableKeyguard = 41;
        static final int TRANSACTION_reenableKeyguard = 42;
        static final int TRANSACTION_exitKeyguardSecurely = 43;
        static final int TRANSACTION_isKeyguardLocked = 44;
        static final int TRANSACTION_isKeyguardSecure = 45;
        static final int TRANSACTION_inKeyguardRestrictedInputMode = 46;
        static final int TRANSACTION_dismissKeyguard = 47;
        static final int TRANSACTION_closeSystemDialogs = 48;
        static final int TRANSACTION_getAnimationScale = 49;
        static final int TRANSACTION_getAnimationScales = 50;
        static final int TRANSACTION_setAnimationScale = 51;
        static final int TRANSACTION_setAnimationScales = 52;
        static final int TRANSACTION_setInTouchMode = 53;
        static final int TRANSACTION_showStrictModeViolation = 54;
        static final int TRANSACTION_setStrictModeVisualIndicatorPreference = 55;
        static final int TRANSACTION_updateRotation = 56;
        static final int TRANSACTION_getRotation = 57;
        static final int TRANSACTION_watchRotation = 58;
        static final int TRANSACTION_removeRotationWatcher = 59;
        static final int TRANSACTION_getPreferredOptionsPanelGravity = 60;
        static final int TRANSACTION_freezeRotation = 61;
        static final int TRANSACTION_thawRotation = 62;
        static final int TRANSACTION_isRotationFrozen = 63;
        static final int TRANSACTION_screenshotApplications = 64;
        static final int TRANSACTION_statusBarVisibilityChanged = 65;
        static final int TRANSACTION_waitForWindowDrawn = 66;
        static final int TRANSACTION_hasNavigationBar = 67;
        static final int TRANSACTION_lockNow = 68;
        static final int TRANSACTION_getFocusedWindowToken = 69;
        static final int TRANSACTION_setInputFilter = 70;
        static final int TRANSACTION_getWindowFrame = 71;
        static final int TRANSACTION_isSafeModeEnabled = 72;
        static final int TRANSACTION_setMagnificationCallbacks = 73;
        static final int TRANSACTION_setMagnificationSpec = 74;
        static final int TRANSACTION_getCompatibleMagnificationSpecForWindow = 75;
        static final int TRANSACTION_setTouchExplorationEnabled = 76;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWindowManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowManager)) {
                return (IWindowManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            MagnificationSpec _arg0;
            Bundle _arg02;
            Configuration _arg03;
            Configuration _arg04;
            CompatibilityInfo _arg3;
            CharSequence _arg4;
            Bitmap _arg05;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    boolean _result = startViewServer(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result2 = stopViewServer();
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = isViewServerRunning();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IInputMethodClient _arg07 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                    IWindowSession _result4 = openSession(_arg07, IInputContext.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeStrongBinder(_result4 != null ? _result4.asBinder() : null);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    IInputMethodClient _arg08 = IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                    boolean _result5 = inputMethodClientHasFocus(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    Point _arg1 = new Point();
                    getInitialDisplaySize(_arg09, _arg1);
                    reply.writeNoException();
                    if (_arg1 != null) {
                        reply.writeInt(1);
                        _arg1.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    Point _arg12 = new Point();
                    getBaseDisplaySize(_arg010, _arg12);
                    reply.writeNoException();
                    if (_arg12 != null) {
                        reply.writeInt(1);
                        _arg12.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _arg13 = data.readInt();
                    int _arg2 = data.readInt();
                    setForcedDisplaySize(_arg011, _arg13, _arg2);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    clearForcedDisplaySize(_arg012);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    int _result6 = getInitialDisplayDensity(_arg013);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    int _result7 = getBaseDisplayDensity(_arg014);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    setForcedDisplayDensity(_arg015, data.readInt());
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    clearForcedDisplayDensity(_arg016);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    int _arg14 = data.readInt();
                    int _arg22 = data.readInt();
                    int _arg32 = data.readInt();
                    int _arg42 = data.readInt();
                    setOverscan(_arg017, _arg14, _arg22, _arg32, _arg42);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg018 = data.readStrongBinder();
                    pauseKeyDispatching(_arg018);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg019 = data.readStrongBinder();
                    resumeKeyDispatching(_arg019);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg020 = 0 != data.readInt();
                    setEventDispatching(_arg020);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg021 = data.readStrongBinder();
                    addWindowToken(_arg021, data.readInt());
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg022 = data.readStrongBinder();
                    removeWindowToken(_arg022);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    IApplicationToken _arg15 = IApplicationToken.Stub.asInterface(data.readStrongBinder());
                    int _arg23 = data.readInt();
                    int _arg33 = data.readInt();
                    int _arg43 = data.readInt();
                    boolean _arg5 = 0 != data.readInt();
                    boolean _arg6 = 0 != data.readInt();
                    int _arg7 = data.readInt();
                    addAppToken(_arg023, _arg15, _arg23, _arg33, _arg43, _arg5, _arg6, _arg7);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg024 = data.readStrongBinder();
                    setAppGroupId(_arg024, data.readInt());
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    IApplicationToken _arg025 = IApplicationToken.Stub.asInterface(data.readStrongBinder());
                    setAppOrientation(_arg025, data.readInt());
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    IApplicationToken _arg026 = IApplicationToken.Stub.asInterface(data.readStrongBinder());
                    int _result8 = getAppOrientation(_arg026);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg027 = data.readStrongBinder();
                    setFocusedApp(_arg027, 0 != data.readInt());
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    prepareAppTransition(_arg028, 0 != data.readInt());
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    int _result9 = getPendingAppTransition();
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg029 = data.readString();
                    int _arg16 = data.readInt();
                    int _arg24 = data.readInt();
                    IRemoteCallback _arg34 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                    overridePendingAppTransition(_arg029, _arg16, _arg24, _arg34);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg030 = data.readInt();
                    int _arg17 = data.readInt();
                    int _arg25 = data.readInt();
                    int _arg35 = data.readInt();
                    overridePendingAppTransitionScaleUp(_arg030, _arg17, _arg25, _arg35);
                    reply.writeNoException();
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = Bitmap.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _arg18 = data.readInt();
                    int _arg26 = data.readInt();
                    IRemoteCallback _arg36 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                    boolean _arg44 = 0 != data.readInt();
                    overridePendingAppTransitionThumb(_arg05, _arg18, _arg26, _arg36, _arg44);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    executeAppTransition();
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg031 = data.readStrongBinder();
                    String _arg19 = data.readString();
                    int _arg27 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = CompatibilityInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg4 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    int _arg52 = data.readInt();
                    int _arg62 = data.readInt();
                    int _arg72 = data.readInt();
                    int _arg8 = data.readInt();
                    IBinder _arg9 = data.readStrongBinder();
                    boolean _arg10 = 0 != data.readInt();
                    setAppStartingWindow(_arg031, _arg19, _arg27, _arg3, _arg4, _arg52, _arg62, _arg72, _arg8, _arg9, _arg10);
                    reply.writeNoException();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg032 = data.readStrongBinder();
                    setAppWillBeHidden(_arg032);
                    reply.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg033 = data.readStrongBinder();
                    setAppVisibility(_arg033, 0 != data.readInt());
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg034 = data.readStrongBinder();
                    startAppFreezingScreen(_arg034, data.readInt());
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg035 = data.readStrongBinder();
                    stopAppFreezingScreen(_arg035, 0 != data.readInt());
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg036 = data.readStrongBinder();
                    removeAppToken(_arg036);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = Configuration.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    Configuration _result10 = updateOrientationFromAppTokens(_arg04, data.readStrongBinder());
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(1);
                        _result10.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = Configuration.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    setNewConfiguration(_arg03);
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg037 = data.readInt();
                    startFreezingScreen(_arg037, data.readInt());
                    reply.writeNoException();
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    stopFreezingScreen();
                    reply.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg038 = data.readStrongBinder();
                    disableKeyguard(_arg038, data.readString());
                    reply.writeNoException();
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg039 = data.readStrongBinder();
                    reenableKeyguard(_arg039);
                    reply.writeNoException();
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    IOnKeyguardExitResult _arg040 = IOnKeyguardExitResult.Stub.asInterface(data.readStrongBinder());
                    exitKeyguardSecurely(_arg040);
                    reply.writeNoException();
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result11 = isKeyguardLocked();
                    reply.writeNoException();
                    reply.writeInt(_result11 ? 1 : 0);
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result12 = isKeyguardSecure();
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result13 = inKeyguardRestrictedInputMode();
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    dismissKeyguard();
                    reply.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg041 = data.readString();
                    closeSystemDialogs(_arg041);
                    reply.writeNoException();
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg042 = data.readInt();
                    float _result14 = getAnimationScale(_arg042);
                    reply.writeNoException();
                    reply.writeFloat(_result14);
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    float[] _result15 = getAnimationScales();
                    reply.writeNoException();
                    reply.writeFloatArray(_result15);
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    setAnimationScale(_arg043, data.readFloat());
                    reply.writeNoException();
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    float[] _arg044 = data.createFloatArray();
                    setAnimationScales(_arg044);
                    reply.writeNoException();
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg045 = 0 != data.readInt();
                    setInTouchMode(_arg045);
                    reply.writeNoException();
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg046 = 0 != data.readInt();
                    showStrictModeViolation(_arg046);
                    reply.writeNoException();
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg047 = data.readString();
                    setStrictModeVisualIndicatorPreference(_arg047);
                    reply.writeNoException();
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg048 = 0 != data.readInt();
                    updateRotation(_arg048, 0 != data.readInt());
                    reply.writeNoException();
                    return true;
                case 57:
                    data.enforceInterface(DESCRIPTOR);
                    int _result16 = getRotation();
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    return true;
                case 58:
                    data.enforceInterface(DESCRIPTOR);
                    IRotationWatcher _arg049 = IRotationWatcher.Stub.asInterface(data.readStrongBinder());
                    int _result17 = watchRotation(_arg049);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    return true;
                case 59:
                    data.enforceInterface(DESCRIPTOR);
                    IRotationWatcher _arg050 = IRotationWatcher.Stub.asInterface(data.readStrongBinder());
                    removeRotationWatcher(_arg050);
                    reply.writeNoException();
                    return true;
                case 60:
                    data.enforceInterface(DESCRIPTOR);
                    int _result18 = getPreferredOptionsPanelGravity();
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    return true;
                case 61:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg051 = data.readInt();
                    freezeRotation(_arg051);
                    reply.writeNoException();
                    return true;
                case 62:
                    data.enforceInterface(DESCRIPTOR);
                    thawRotation();
                    reply.writeNoException();
                    return true;
                case 63:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result19 = isRotationFrozen();
                    reply.writeNoException();
                    reply.writeInt(_result19 ? 1 : 0);
                    return true;
                case 64:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg052 = data.readStrongBinder();
                    int _arg110 = data.readInt();
                    int _arg28 = data.readInt();
                    int _arg37 = data.readInt();
                    boolean _arg45 = 0 != data.readInt();
                    Bitmap _result20 = screenshotApplications(_arg052, _arg110, _arg28, _arg37, _arg45);
                    reply.writeNoException();
                    if (_result20 != null) {
                        reply.writeInt(1);
                        _result20.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 65:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg053 = data.readInt();
                    statusBarVisibilityChanged(_arg053);
                    reply.writeNoException();
                    return true;
                case 66:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg054 = data.readStrongBinder();
                    boolean _result21 = waitForWindowDrawn(_arg054, IRemoteCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeInt(_result21 ? 1 : 0);
                    return true;
                case 67:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result22 = hasNavigationBar();
                    reply.writeNoException();
                    reply.writeInt(_result22 ? 1 : 0);
                    return true;
                case 68:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    lockNow(_arg02);
                    reply.writeNoException();
                    return true;
                case 69:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _result23 = getFocusedWindowToken();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result23);
                    return true;
                case 70:
                    data.enforceInterface(DESCRIPTOR);
                    IInputFilter _arg055 = IInputFilter.Stub.asInterface(data.readStrongBinder());
                    setInputFilter(_arg055);
                    reply.writeNoException();
                    return true;
                case 71:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg056 = data.readStrongBinder();
                    Rect _arg111 = new Rect();
                    getWindowFrame(_arg056, _arg111);
                    reply.writeNoException();
                    if (_arg111 != null) {
                        reply.writeInt(1);
                        _arg111.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 72:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result24 = isSafeModeEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result24 ? 1 : 0);
                    return true;
                case 73:
                    data.enforceInterface(DESCRIPTOR);
                    IMagnificationCallbacks _arg057 = IMagnificationCallbacks.Stub.asInterface(data.readStrongBinder());
                    setMagnificationCallbacks(_arg057);
                    reply.writeNoException();
                    return true;
                case 74:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = MagnificationSpec.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setMagnificationSpec(_arg0);
                    reply.writeNoException();
                    return true;
                case 75:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg058 = data.readStrongBinder();
                    MagnificationSpec _result25 = getCompatibleMagnificationSpecForWindow(_arg058);
                    reply.writeNoException();
                    if (_result25 != null) {
                        reply.writeInt(1);
                        _result25.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 76:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg059 = 0 != data.readInt();
                    setTouchExplorationEnabled(_arg059);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IWindowManager$Stub$Proxy.class */
        private static class Proxy implements IWindowManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.view.IWindowManager
            public boolean startViewServer(int port) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(port);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean stopViewServer() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isViewServerRunning() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public IWindowSession openSession(IInputMethodClient client, IInputContext inputContext) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeStrongBinder(inputContext != null ? inputContext.asBinder() : null);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    IWindowSession _result = IWindowSession.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public boolean inputMethodClientHasFocus(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void getInitialDisplaySize(int displayId, Point size) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        size.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void getBaseDisplaySize(int displayId, Point size) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        size.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setForcedDisplaySize(int displayId, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void clearForcedDisplaySize(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int getInitialDisplayDensity(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int getBaseDisplayDensity(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setForcedDisplayDensity(int displayId, int density) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(density);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void clearForcedDisplayDensity(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setOverscan(int displayId, int left, int top, int right, int bottom) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(left);
                    _data.writeInt(top);
                    _data.writeInt(right);
                    _data.writeInt(bottom);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void pauseKeyDispatching(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void resumeKeyDispatching(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setEventDispatching(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void addWindowToken(IBinder token, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(type);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void removeWindowToken(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void addAppToken(int addPos, IApplicationToken token, int groupId, int stackId, int requestedOrientation, boolean fullscreen, boolean showWhenLocked, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(addPos);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    _data.writeInt(groupId);
                    _data.writeInt(stackId);
                    _data.writeInt(requestedOrientation);
                    _data.writeInt(fullscreen ? 1 : 0);
                    _data.writeInt(showWhenLocked ? 1 : 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAppGroupId(IBinder token, int groupId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(groupId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAppOrientation(IApplicationToken token, int requestedOrientation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    _data.writeInt(requestedOrientation);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int getAppOrientation(IApplicationToken token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setFocusedApp(IBinder token, boolean moveFocusNow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(moveFocusNow ? 1 : 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void prepareAppTransition(int transit, boolean alwaysKeepCurrent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(transit);
                    _data.writeInt(alwaysKeepCurrent ? 1 : 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int getPendingAppTransition() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void overridePendingAppTransition(String packageName, int enterAnim, int exitAnim, IRemoteCallback startedCallback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(enterAnim);
                    _data.writeInt(exitAnim);
                    _data.writeStrongBinder(startedCallback != null ? startedCallback.asBinder() : null);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void overridePendingAppTransitionScaleUp(int startX, int startY, int startWidth, int startHeight) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startX);
                    _data.writeInt(startY);
                    _data.writeInt(startWidth);
                    _data.writeInt(startHeight);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void overridePendingAppTransitionThumb(Bitmap srcThumb, int startX, int startY, IRemoteCallback startedCallback, boolean scaleUp) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (srcThumb != null) {
                        _data.writeInt(1);
                        srcThumb.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(startX);
                    _data.writeInt(startY);
                    _data.writeStrongBinder(startedCallback != null ? startedCallback.asBinder() : null);
                    _data.writeInt(scaleUp ? 1 : 0);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void executeAppTransition() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAppStartingWindow(IBinder token, String pkg, int theme, CompatibilityInfo compatInfo, CharSequence nonLocalizedLabel, int labelRes, int icon, int logo, int windowFlags, IBinder transferFrom, boolean createIfNeeded) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(pkg);
                    _data.writeInt(theme);
                    if (compatInfo != null) {
                        _data.writeInt(1);
                        compatInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (nonLocalizedLabel != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(nonLocalizedLabel, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(labelRes);
                    _data.writeInt(icon);
                    _data.writeInt(logo);
                    _data.writeInt(windowFlags);
                    _data.writeStrongBinder(transferFrom);
                    _data.writeInt(createIfNeeded ? 1 : 0);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAppWillBeHidden(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAppVisibility(IBinder token, boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(visible ? 1 : 0);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void startAppFreezingScreen(IBinder token, int configChanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(configChanges);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void stopAppFreezingScreen(IBinder token, boolean force) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(force ? 1 : 0);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void removeAppToken(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public Configuration updateOrientationFromAppTokens(Configuration currentConfig, IBinder freezeThisOneIfNeeded) throws RemoteException {
                Configuration _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (currentConfig != null) {
                        _data.writeInt(1);
                        currentConfig.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(freezeThisOneIfNeeded);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Configuration.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setNewConfiguration(Configuration config) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (config != null) {
                        _data.writeInt(1);
                        config.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void startFreezingScreen(int exitAnim, int enterAnim) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(exitAnim);
                    _data.writeInt(enterAnim);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void stopFreezingScreen() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void disableKeyguard(IBinder token, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(tag);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void reenableKeyguard(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void exitKeyguardSecurely(IOnKeyguardExitResult callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public boolean isKeyguardLocked() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isKeyguardSecure() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean inKeyguardRestrictedInputMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void dismissKeyguard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void closeSystemDialogs(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public float getAnimationScale(int which) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public float[] getAnimationScales() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    float[] _result = _reply.createFloatArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAnimationScale(int which, float scale) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    _data.writeFloat(scale);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setAnimationScales(float[] scales) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloatArray(scales);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setInTouchMode(boolean showFocus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showFocus ? 1 : 0);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void showStrictModeViolation(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(on ? 1 : 0);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setStrictModeVisualIndicatorPreference(String enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(enabled);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void updateRotation(boolean alwaysSendConfiguration, boolean forceRelayout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(alwaysSendConfiguration ? 1 : 0);
                    _data.writeInt(forceRelayout ? 1 : 0);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int getRotation() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int watchRotation(IRotationWatcher watcher) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(watcher != null ? watcher.asBinder() : null);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void removeRotationWatcher(IRotationWatcher watcher) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(watcher != null ? watcher.asBinder() : null);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public int getPreferredOptionsPanelGravity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void freezeRotation(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void thawRotation() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public boolean isRotationFrozen() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public Bitmap screenshotApplications(IBinder appToken, int displayId, int maxWidth, int maxHeight, boolean force565) throws RemoteException {
                Bitmap _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(appToken);
                    _data.writeInt(displayId);
                    _data.writeInt(maxWidth);
                    _data.writeInt(maxHeight);
                    _data.writeInt(force565 ? 1 : 0);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bitmap.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void statusBarVisibilityChanged(int visibility) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visibility);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public boolean waitForWindowDrawn(IBinder token, IRemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean hasNavigationBar() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void lockNow(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public IBinder getFocusedWindowToken() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setInputFilter(IInputFilter filter) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(filter != null ? filter.asBinder() : null);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void getWindowFrame(IBinder token, Rect outFrame) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        outFrame.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isSafeModeEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setMagnificationCallbacks(IMagnificationCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public void setMagnificationSpec(MagnificationSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (spec != null) {
                        _data.writeInt(1);
                        spec.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowManager
            public MagnificationSpec getCompatibleMagnificationSpecForWindow(IBinder windowToken) throws RemoteException {
                MagnificationSpec _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = MagnificationSpec.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setTouchExplorationEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}