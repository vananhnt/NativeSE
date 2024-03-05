package android.media;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioRoutesObserver;
import android.media.IRemoteControlClient;
import android.media.IRemoteControlDisplay;
import android.media.IRemoteVolumeObserver;
import android.media.IRingtonePlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.KeyEvent;

/* loaded from: IAudioService.class */
public interface IAudioService extends IInterface {
    void adjustVolume(int i, int i2, String str) throws RemoteException;

    boolean isLocalOrRemoteMusicActive() throws RemoteException;

    void adjustLocalOrRemoteStreamVolume(int i, int i2, String str) throws RemoteException;

    void adjustSuggestedStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void adjustStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void adjustMasterVolume(int i, int i2, String str) throws RemoteException;

    void setStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void setRemoteStreamVolume(int i) throws RemoteException;

    void setMasterVolume(int i, int i2, String str) throws RemoteException;

    void setStreamSolo(int i, boolean z, IBinder iBinder) throws RemoteException;

    void setStreamMute(int i, boolean z, IBinder iBinder) throws RemoteException;

    boolean isStreamMute(int i) throws RemoteException;

    void setMasterMute(boolean z, int i, IBinder iBinder) throws RemoteException;

    boolean isMasterMute() throws RemoteException;

    int getStreamVolume(int i) throws RemoteException;

    int getMasterVolume() throws RemoteException;

    int getStreamMaxVolume(int i) throws RemoteException;

    int getMasterMaxVolume() throws RemoteException;

    int getLastAudibleStreamVolume(int i) throws RemoteException;

    int getLastAudibleMasterVolume() throws RemoteException;

    void setRingerMode(int i) throws RemoteException;

    int getRingerMode() throws RemoteException;

    void setVibrateSetting(int i, int i2) throws RemoteException;

    int getVibrateSetting(int i) throws RemoteException;

    boolean shouldVibrate(int i) throws RemoteException;

    void setMode(int i, IBinder iBinder) throws RemoteException;

    int getMode() throws RemoteException;

    void playSoundEffect(int i) throws RemoteException;

    void playSoundEffectVolume(int i, float f) throws RemoteException;

    boolean loadSoundEffects() throws RemoteException;

    void unloadSoundEffects() throws RemoteException;

    void reloadAudioSettings() throws RemoteException;

    void avrcpSupportsAbsoluteVolume(String str, boolean z) throws RemoteException;

    void setSpeakerphoneOn(boolean z) throws RemoteException;

    boolean isSpeakerphoneOn() throws RemoteException;

    void setBluetoothScoOn(boolean z) throws RemoteException;

    boolean isBluetoothScoOn() throws RemoteException;

    void setBluetoothA2dpOn(boolean z) throws RemoteException;

    boolean isBluetoothA2dpOn() throws RemoteException;

    int requestAudioFocus(int i, int i2, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2) throws RemoteException;

    int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str) throws RemoteException;

    void unregisterAudioFocusClient(String str) throws RemoteException;

    int getCurrentAudioFocus() throws RemoteException;

    void dispatchMediaKeyEvent(KeyEvent keyEvent) throws RemoteException;

    void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) throws RemoteException;

    void registerMediaButtonIntent(PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) throws RemoteException;

    void unregisterMediaButtonIntent(PendingIntent pendingIntent) throws RemoteException;

    void registerMediaButtonEventReceiverForCalls(ComponentName componentName) throws RemoteException;

    void unregisterMediaButtonEventReceiverForCalls() throws RemoteException;

    boolean registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) throws RemoteException;

    boolean registerRemoteController(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2, ComponentName componentName) throws RemoteException;

    void unregisterRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) throws RemoteException;

    void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) throws RemoteException;

    void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) throws RemoteException;

    void setRemoteControlClientPlaybackPosition(int i, long j) throws RemoteException;

    void updateRemoteControlClientMetadata(int i, int i2, Rating rating) throws RemoteException;

    int registerRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient, String str) throws RemoteException;

    void unregisterRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient) throws RemoteException;

    void setPlaybackInfoForRcc(int i, int i2, int i3) throws RemoteException;

    void setPlaybackStateForRcc(int i, int i2, long j, float f) throws RemoteException;

    int getRemoteStreamMaxVolume() throws RemoteException;

    int getRemoteStreamVolume() throws RemoteException;

    void registerRemoteVolumeObserverForRcc(int i, IRemoteVolumeObserver iRemoteVolumeObserver) throws RemoteException;

    void startBluetoothSco(IBinder iBinder, int i) throws RemoteException;

    void stopBluetoothSco(IBinder iBinder) throws RemoteException;

    void forceVolumeControlStream(int i, IBinder iBinder) throws RemoteException;

    void setRingtonePlayer(IRingtonePlayer iRingtonePlayer) throws RemoteException;

    IRingtonePlayer getRingtonePlayer() throws RemoteException;

    int getMasterStreamType() throws RemoteException;

    void setWiredDeviceConnectionState(int i, int i2, String str) throws RemoteException;

    int setBluetoothA2dpDeviceConnectionState(BluetoothDevice bluetoothDevice, int i) throws RemoteException;

    AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) throws RemoteException;

    boolean isCameraSoundForced() throws RemoteException;

    /* loaded from: IAudioService$Stub.class */
    public static abstract class Stub extends Binder implements IAudioService {
        private static final String DESCRIPTOR = "android.media.IAudioService";
        static final int TRANSACTION_adjustVolume = 1;
        static final int TRANSACTION_isLocalOrRemoteMusicActive = 2;
        static final int TRANSACTION_adjustLocalOrRemoteStreamVolume = 3;
        static final int TRANSACTION_adjustSuggestedStreamVolume = 4;
        static final int TRANSACTION_adjustStreamVolume = 5;
        static final int TRANSACTION_adjustMasterVolume = 6;
        static final int TRANSACTION_setStreamVolume = 7;
        static final int TRANSACTION_setRemoteStreamVolume = 8;
        static final int TRANSACTION_setMasterVolume = 9;
        static final int TRANSACTION_setStreamSolo = 10;
        static final int TRANSACTION_setStreamMute = 11;
        static final int TRANSACTION_isStreamMute = 12;
        static final int TRANSACTION_setMasterMute = 13;
        static final int TRANSACTION_isMasterMute = 14;
        static final int TRANSACTION_getStreamVolume = 15;
        static final int TRANSACTION_getMasterVolume = 16;
        static final int TRANSACTION_getStreamMaxVolume = 17;
        static final int TRANSACTION_getMasterMaxVolume = 18;
        static final int TRANSACTION_getLastAudibleStreamVolume = 19;
        static final int TRANSACTION_getLastAudibleMasterVolume = 20;
        static final int TRANSACTION_setRingerMode = 21;
        static final int TRANSACTION_getRingerMode = 22;
        static final int TRANSACTION_setVibrateSetting = 23;
        static final int TRANSACTION_getVibrateSetting = 24;
        static final int TRANSACTION_shouldVibrate = 25;
        static final int TRANSACTION_setMode = 26;
        static final int TRANSACTION_getMode = 27;
        static final int TRANSACTION_playSoundEffect = 28;
        static final int TRANSACTION_playSoundEffectVolume = 29;
        static final int TRANSACTION_loadSoundEffects = 30;
        static final int TRANSACTION_unloadSoundEffects = 31;
        static final int TRANSACTION_reloadAudioSettings = 32;
        static final int TRANSACTION_avrcpSupportsAbsoluteVolume = 33;
        static final int TRANSACTION_setSpeakerphoneOn = 34;
        static final int TRANSACTION_isSpeakerphoneOn = 35;
        static final int TRANSACTION_setBluetoothScoOn = 36;
        static final int TRANSACTION_isBluetoothScoOn = 37;
        static final int TRANSACTION_setBluetoothA2dpOn = 38;
        static final int TRANSACTION_isBluetoothA2dpOn = 39;
        static final int TRANSACTION_requestAudioFocus = 40;
        static final int TRANSACTION_abandonAudioFocus = 41;
        static final int TRANSACTION_unregisterAudioFocusClient = 42;
        static final int TRANSACTION_getCurrentAudioFocus = 43;
        static final int TRANSACTION_dispatchMediaKeyEvent = 44;
        static final int TRANSACTION_dispatchMediaKeyEventUnderWakelock = 45;
        static final int TRANSACTION_registerMediaButtonIntent = 46;
        static final int TRANSACTION_unregisterMediaButtonIntent = 47;
        static final int TRANSACTION_registerMediaButtonEventReceiverForCalls = 48;
        static final int TRANSACTION_unregisterMediaButtonEventReceiverForCalls = 49;
        static final int TRANSACTION_registerRemoteControlDisplay = 50;
        static final int TRANSACTION_registerRemoteController = 51;
        static final int TRANSACTION_unregisterRemoteControlDisplay = 52;
        static final int TRANSACTION_remoteControlDisplayUsesBitmapSize = 53;
        static final int TRANSACTION_remoteControlDisplayWantsPlaybackPositionSync = 54;
        static final int TRANSACTION_setRemoteControlClientPlaybackPosition = 55;
        static final int TRANSACTION_updateRemoteControlClientMetadata = 56;
        static final int TRANSACTION_registerRemoteControlClient = 57;
        static final int TRANSACTION_unregisterRemoteControlClient = 58;
        static final int TRANSACTION_setPlaybackInfoForRcc = 59;
        static final int TRANSACTION_setPlaybackStateForRcc = 60;
        static final int TRANSACTION_getRemoteStreamMaxVolume = 61;
        static final int TRANSACTION_getRemoteStreamVolume = 62;
        static final int TRANSACTION_registerRemoteVolumeObserverForRcc = 63;
        static final int TRANSACTION_startBluetoothSco = 64;
        static final int TRANSACTION_stopBluetoothSco = 65;
        static final int TRANSACTION_forceVolumeControlStream = 66;
        static final int TRANSACTION_setRingtonePlayer = 67;
        static final int TRANSACTION_getRingtonePlayer = 68;
        static final int TRANSACTION_getMasterStreamType = 69;
        static final int TRANSACTION_setWiredDeviceConnectionState = 70;
        static final int TRANSACTION_setBluetoothA2dpDeviceConnectionState = 71;
        static final int TRANSACTION_startWatchingRoutes = 72;
        static final int TRANSACTION_isCameraSoundForced = 73;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAudioService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioService)) {
                return (IAudioService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            BluetoothDevice _arg0;
            PendingIntent _arg02;
            PendingIntent _arg03;
            Rating _arg2;
            ComponentName _arg3;
            ComponentName _arg04;
            PendingIntent _arg05;
            PendingIntent _arg06;
            ComponentName _arg1;
            KeyEvent _arg07;
            KeyEvent _arg08;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    int _arg12 = data.readInt();
                    String _arg22 = data.readString();
                    adjustVolume(_arg09, _arg12, _arg22);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = isLocalOrRemoteMusicActive();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg13 = data.readInt();
                    String _arg23 = data.readString();
                    adjustLocalOrRemoteStreamVolume(_arg010, _arg13, _arg23);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _arg14 = data.readInt();
                    int _arg24 = data.readInt();
                    String _arg32 = data.readString();
                    adjustSuggestedStreamVolume(_arg011, _arg14, _arg24, _arg32);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _arg15 = data.readInt();
                    int _arg25 = data.readInt();
                    String _arg33 = data.readString();
                    adjustStreamVolume(_arg012, _arg15, _arg25, _arg33);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    int _arg16 = data.readInt();
                    String _arg26 = data.readString();
                    adjustMasterVolume(_arg013, _arg16, _arg26);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    int _arg17 = data.readInt();
                    int _arg27 = data.readInt();
                    String _arg34 = data.readString();
                    setStreamVolume(_arg014, _arg17, _arg27, _arg34);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    setRemoteStreamVolume(_arg015);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    int _arg18 = data.readInt();
                    String _arg28 = data.readString();
                    setMasterVolume(_arg016, _arg18, _arg28);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    boolean _arg19 = 0 != data.readInt();
                    IBinder _arg29 = data.readStrongBinder();
                    setStreamSolo(_arg017, _arg19, _arg29);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    boolean _arg110 = 0 != data.readInt();
                    IBinder _arg210 = data.readStrongBinder();
                    setStreamMute(_arg018, _arg110, _arg210);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    boolean _result2 = isStreamMute(_arg019);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg020 = 0 != data.readInt();
                    int _arg111 = data.readInt();
                    IBinder _arg211 = data.readStrongBinder();
                    setMasterMute(_arg020, _arg111, _arg211);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = isMasterMute();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    int _result4 = getStreamVolume(_arg021);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _result5 = getMasterVolume();
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    int _result6 = getStreamMaxVolume(_arg022);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = getMasterMaxVolume();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    int _result8 = getLastAudibleStreamVolume(_arg023);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    int _result9 = getLastAudibleMasterVolume();
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg024 = data.readInt();
                    setRingerMode(_arg024);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    int _result10 = getRingerMode();
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    int _arg112 = data.readInt();
                    setVibrateSetting(_arg025, _arg112);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    int _result11 = getVibrateSetting(_arg026);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    boolean _result12 = shouldVibrate(_arg027);
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    IBinder _arg113 = data.readStrongBinder();
                    setMode(_arg028, _arg113);
                    reply.writeNoException();
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    int _result13 = getMode();
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    playSoundEffect(_arg029);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg030 = data.readInt();
                    float _arg114 = data.readFloat();
                    playSoundEffectVolume(_arg030, _arg114);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result14 = loadSoundEffects();
                    reply.writeNoException();
                    reply.writeInt(_result14 ? 1 : 0);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    unloadSoundEffects();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    reloadAudioSettings();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg031 = data.readString();
                    boolean _arg115 = 0 != data.readInt();
                    avrcpSupportsAbsoluteVolume(_arg031, _arg115);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg032 = 0 != data.readInt();
                    setSpeakerphoneOn(_arg032);
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result15 = isSpeakerphoneOn();
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg033 = 0 != data.readInt();
                    setBluetoothScoOn(_arg033);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result16 = isBluetoothScoOn();
                    reply.writeNoException();
                    reply.writeInt(_result16 ? 1 : 0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg034 = 0 != data.readInt();
                    setBluetoothA2dpOn(_arg034);
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result17 = isBluetoothA2dpOn();
                    reply.writeNoException();
                    reply.writeInt(_result17 ? 1 : 0);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg035 = data.readInt();
                    int _arg116 = data.readInt();
                    IBinder _arg212 = data.readStrongBinder();
                    IAudioFocusDispatcher _arg35 = IAudioFocusDispatcher.Stub.asInterface(data.readStrongBinder());
                    String _arg4 = data.readString();
                    String _arg5 = data.readString();
                    int _result18 = requestAudioFocus(_arg035, _arg116, _arg212, _arg35, _arg4, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    IAudioFocusDispatcher _arg036 = IAudioFocusDispatcher.Stub.asInterface(data.readStrongBinder());
                    String _arg117 = data.readString();
                    int _result19 = abandonAudioFocus(_arg036, _arg117);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg037 = data.readString();
                    unregisterAudioFocusClient(_arg037);
                    reply.writeNoException();
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    int _result20 = getCurrentAudioFocus();
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = KeyEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    dispatchMediaKeyEvent(_arg08);
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = KeyEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    dispatchMediaKeyEventUnderWakelock(_arg07);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    IBinder _arg213 = data.readStrongBinder();
                    registerMediaButtonIntent(_arg06, _arg1, _arg213);
                    reply.writeNoException();
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    unregisterMediaButtonIntent(_arg05);
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    registerMediaButtonEventReceiverForCalls(_arg04);
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterMediaButtonEventReceiverForCalls();
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    IRemoteControlDisplay _arg038 = IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder());
                    int _arg118 = data.readInt();
                    int _arg214 = data.readInt();
                    boolean _result21 = registerRemoteControlDisplay(_arg038, _arg118, _arg214);
                    reply.writeNoException();
                    reply.writeInt(_result21 ? 1 : 0);
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    IRemoteControlDisplay _arg039 = IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder());
                    int _arg119 = data.readInt();
                    int _arg215 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    boolean _result22 = registerRemoteController(_arg039, _arg119, _arg215, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result22 ? 1 : 0);
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    IRemoteControlDisplay _arg040 = IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder());
                    unregisterRemoteControlDisplay(_arg040);
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    IRemoteControlDisplay _arg041 = IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder());
                    int _arg120 = data.readInt();
                    int _arg216 = data.readInt();
                    remoteControlDisplayUsesBitmapSize(_arg041, _arg120, _arg216);
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    IRemoteControlDisplay _arg042 = IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder());
                    boolean _arg121 = 0 != data.readInt();
                    remoteControlDisplayWantsPlaybackPositionSync(_arg042, _arg121);
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    long _arg122 = data.readLong();
                    setRemoteControlClientPlaybackPosition(_arg043, _arg122);
                    reply.writeNoException();
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg044 = data.readInt();
                    int _arg123 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg2 = Rating.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    updateRemoteControlClientMetadata(_arg044, _arg123, _arg2);
                    reply.writeNoException();
                    return true;
                case 57:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    IRemoteControlClient _arg124 = IRemoteControlClient.Stub.asInterface(data.readStrongBinder());
                    String _arg217 = data.readString();
                    int _result23 = registerRemoteControlClient(_arg03, _arg124, _arg217);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    return true;
                case 58:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    IRemoteControlClient _arg125 = IRemoteControlClient.Stub.asInterface(data.readStrongBinder());
                    unregisterRemoteControlClient(_arg02, _arg125);
                    return true;
                case 59:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg045 = data.readInt();
                    int _arg126 = data.readInt();
                    int _arg218 = data.readInt();
                    setPlaybackInfoForRcc(_arg045, _arg126, _arg218);
                    return true;
                case 60:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg046 = data.readInt();
                    int _arg127 = data.readInt();
                    long _arg219 = data.readLong();
                    float _arg36 = data.readFloat();
                    setPlaybackStateForRcc(_arg046, _arg127, _arg219, _arg36);
                    reply.writeNoException();
                    return true;
                case 61:
                    data.enforceInterface(DESCRIPTOR);
                    int _result24 = getRemoteStreamMaxVolume();
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    return true;
                case 62:
                    data.enforceInterface(DESCRIPTOR);
                    int _result25 = getRemoteStreamVolume();
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    return true;
                case 63:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg047 = data.readInt();
                    IRemoteVolumeObserver _arg128 = IRemoteVolumeObserver.Stub.asInterface(data.readStrongBinder());
                    registerRemoteVolumeObserverForRcc(_arg047, _arg128);
                    return true;
                case 64:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg048 = data.readStrongBinder();
                    int _arg129 = data.readInt();
                    startBluetoothSco(_arg048, _arg129);
                    reply.writeNoException();
                    return true;
                case 65:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg049 = data.readStrongBinder();
                    stopBluetoothSco(_arg049);
                    reply.writeNoException();
                    return true;
                case 66:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg050 = data.readInt();
                    IBinder _arg130 = data.readStrongBinder();
                    forceVolumeControlStream(_arg050, _arg130);
                    reply.writeNoException();
                    return true;
                case 67:
                    data.enforceInterface(DESCRIPTOR);
                    IRingtonePlayer _arg051 = IRingtonePlayer.Stub.asInterface(data.readStrongBinder());
                    setRingtonePlayer(_arg051);
                    reply.writeNoException();
                    return true;
                case 68:
                    data.enforceInterface(DESCRIPTOR);
                    IRingtonePlayer _result26 = getRingtonePlayer();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result26 != null ? _result26.asBinder() : null);
                    return true;
                case 69:
                    data.enforceInterface(DESCRIPTOR);
                    int _result27 = getMasterStreamType();
                    reply.writeNoException();
                    reply.writeInt(_result27);
                    return true;
                case 70:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg052 = data.readInt();
                    int _arg131 = data.readInt();
                    String _arg220 = data.readString();
                    setWiredDeviceConnectionState(_arg052, _arg131, _arg220);
                    reply.writeNoException();
                    return true;
                case 71:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg132 = data.readInt();
                    int _result28 = setBluetoothA2dpDeviceConnectionState(_arg0, _arg132);
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    return true;
                case 72:
                    data.enforceInterface(DESCRIPTOR);
                    IAudioRoutesObserver _arg053 = IAudioRoutesObserver.Stub.asInterface(data.readStrongBinder());
                    AudioRoutesInfo _result29 = startWatchingRoutes(_arg053);
                    reply.writeNoException();
                    if (_result29 != null) {
                        reply.writeInt(1);
                        _result29.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 73:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result30 = isCameraSoundForced();
                    reply.writeNoException();
                    reply.writeInt(_result30 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IAudioService$Stub$Proxy.class */
        public static class Proxy implements IAudioService {
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

            @Override // android.media.IAudioService
            public void adjustVolume(int direction, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public boolean isLocalOrRemoteMusicActive() throws RemoteException {
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

            @Override // android.media.IAudioService
            public void adjustLocalOrRemoteStreamVolume(int streamType, int direction, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    _data.writeInt(suggestedStreamType);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void adjustMasterVolume(int direction, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setStreamVolume(int streamType, int index, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(index);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setRemoteStreamVolume(int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    this.mRemote.transact(8, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setMasterVolume(int index, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
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

            @Override // android.media.IAudioService
            public void setStreamSolo(int streamType, boolean state, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(state ? 1 : 0);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setStreamMute(int streamType, boolean state, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(state ? 1 : 0);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public boolean isStreamMute(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMasterMute(boolean state, int flags, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state ? 1 : 0);
                    _data.writeInt(flags);
                    _data.writeStrongBinder(cb);
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

            @Override // android.media.IAudioService
            public boolean isMasterMute() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(15, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int getMasterVolume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int getStreamMaxVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(17, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int getMasterMaxVolume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int getLastAudibleStreamVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(19, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int getLastAudibleMasterVolume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void setRingerMode(int ringerMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ringerMode);
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

            @Override // android.media.IAudioService
            public int getRingerMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void setVibrateSetting(int vibrateType, int vibrateSetting) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vibrateType);
                    _data.writeInt(vibrateSetting);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public int getVibrateSetting(int vibrateType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vibrateType);
                    this.mRemote.transact(24, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public boolean shouldVibrate(int vibrateType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vibrateType);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMode(int mode, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public int getMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void playSoundEffect(int effectType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(effectType);
                    this.mRemote.transact(28, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void playSoundEffectVolume(int effectType, float volume) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(effectType);
                    _data.writeFloat(volume);
                    this.mRemote.transact(29, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public boolean loadSoundEffects() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unloadSoundEffects() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void reloadAudioSettings() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void avrcpSupportsAbsoluteVolume(String address, boolean support) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(support ? 1 : 0);
                    this.mRemote.transact(33, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setSpeakerphoneOn(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(on ? 1 : 0);
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

            @Override // android.media.IAudioService
            public boolean isSpeakerphoneOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothScoOn(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(on ? 1 : 0);
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

            @Override // android.media.IAudioService
            public boolean isBluetoothScoOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothA2dpOn(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(on ? 1 : 0);
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

            @Override // android.media.IAudioService
            public boolean isBluetoothA2dpOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int requestAudioFocus(int mainStreamType, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mainStreamType);
                    _data.writeInt(durationHint);
                    _data.writeStrongBinder(cb);
                    _data.writeStrongBinder(fd != null ? fd.asBinder() : null);
                    _data.writeString(clientId);
                    _data.writeString(callingPackageName);
                    this.mRemote.transact(40, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(fd != null ? fd.asBinder() : null);
                    _data.writeString(clientId);
                    this.mRemote.transact(41, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void unregisterAudioFocusClient(String clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(clientId);
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

            @Override // android.media.IAudioService
            public int getCurrentAudioFocus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void dispatchMediaKeyEvent(KeyEvent keyEvent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyEvent != null) {
                        _data.writeInt(1);
                        keyEvent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(44, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyEvent != null) {
                        _data.writeInt(1);
                        keyEvent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void registerMediaButtonIntent(PendingIntent pi, ComponentName c, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pi != null) {
                        _data.writeInt(1);
                        pi.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (c != null) {
                        _data.writeInt(1);
                        c.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void unregisterMediaButtonIntent(PendingIntent pi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pi != null) {
                        _data.writeInt(1);
                        pi.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(47, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void registerMediaButtonEventReceiverForCalls(ComponentName c) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (c != null) {
                        _data.writeInt(1);
                        c.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(48, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void unregisterMediaButtonEventReceiverForCalls() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public boolean registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                    _data.writeInt(w);
                    _data.writeInt(h);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean registerRemoteController(IRemoteControlDisplay rcd, int w, int h, ComponentName listenerComp) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                    _data.writeInt(w);
                    _data.writeInt(h);
                    if (listenerComp != null) {
                        _data.writeInt(1);
                        listenerComp.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                    this.mRemote.transact(52, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                    _data.writeInt(w);
                    _data.writeInt(h);
                    this.mRemote.transact(53, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                    _data.writeInt(wantsSync ? 1 : 0);
                    this.mRemote.transact(54, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setRemoteControlClientPlaybackPosition(int generationId, long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(generationId);
                    _data.writeLong(timeMs);
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

            @Override // android.media.IAudioService
            public void updateRemoteControlClientMetadata(int generationId, int key, Rating value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(generationId);
                    _data.writeInt(key);
                    if (value != null) {
                        _data.writeInt(1);
                        value.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.media.IAudioService
            public int registerRemoteControlClient(PendingIntent mediaIntent, IRemoteControlClient rcClient, String callingPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaIntent != null) {
                        _data.writeInt(1);
                        mediaIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(rcClient != null ? rcClient.asBinder() : null);
                    _data.writeString(callingPackageName);
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

            @Override // android.media.IAudioService
            public void unregisterRemoteControlClient(PendingIntent mediaIntent, IRemoteControlClient rcClient) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaIntent != null) {
                        _data.writeInt(1);
                        mediaIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(rcClient != null ? rcClient.asBinder() : null);
                    this.mRemote.transact(58, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setPlaybackInfoForRcc(int rccId, int what, int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rccId);
                    _data.writeInt(what);
                    _data.writeInt(value);
                    this.mRemote.transact(59, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setPlaybackStateForRcc(int rccId, int state, long timeMs, float speed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rccId);
                    _data.writeInt(state);
                    _data.writeLong(timeMs);
                    _data.writeFloat(speed);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public int getRemoteStreamMaxVolume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(61, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public int getRemoteStreamVolume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void registerRemoteVolumeObserverForRcc(int rccId, IRemoteVolumeObserver rvo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rccId);
                    _data.writeStrongBinder(rvo != null ? rvo.asBinder() : null);
                    this.mRemote.transact(63, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void startBluetoothSco(IBinder cb, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void stopBluetoothSco(IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
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

            @Override // android.media.IAudioService
            public void forceVolumeControlStream(int streamType, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public void setRingtonePlayer(IRingtonePlayer player) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(player != null ? player.asBinder() : null);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public IRingtonePlayer getRingtonePlayer() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    IRingtonePlayer _result = IRingtonePlayer.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.media.IAudioService
            public int getMasterStreamType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(69, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public void setWiredDeviceConnectionState(int device, int state, String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(device);
                    _data.writeInt(state);
                    _data.writeString(name);
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

            @Override // android.media.IAudioService
            public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(state);
                    this.mRemote.transact(71, _data, _reply, 0);
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

            @Override // android.media.IAudioService
            public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) throws RemoteException {
                AudioRoutesInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = AudioRoutesInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isCameraSoundForced() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}