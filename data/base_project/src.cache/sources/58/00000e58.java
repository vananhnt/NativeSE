package android.speech.tts;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.speech.tts.ITextToSpeechCallback;

/* loaded from: ITextToSpeechService.class */
public interface ITextToSpeechService extends IInterface {
    int speak(IBinder iBinder, String str, int i, Bundle bundle) throws RemoteException;

    int synthesizeToFileDescriptor(IBinder iBinder, String str, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle) throws RemoteException;

    int playAudio(IBinder iBinder, Uri uri, int i, Bundle bundle) throws RemoteException;

    int playSilence(IBinder iBinder, long j, int i, Bundle bundle) throws RemoteException;

    boolean isSpeaking() throws RemoteException;

    int stop(IBinder iBinder) throws RemoteException;

    String[] getLanguage() throws RemoteException;

    String[] getClientDefaultLanguage() throws RemoteException;

    int isLanguageAvailable(String str, String str2, String str3) throws RemoteException;

    String[] getFeaturesForLanguage(String str, String str2, String str3) throws RemoteException;

    int loadLanguage(IBinder iBinder, String str, String str2, String str3) throws RemoteException;

    void setCallback(IBinder iBinder, ITextToSpeechCallback iTextToSpeechCallback) throws RemoteException;

    /* loaded from: ITextToSpeechService$Stub.class */
    public static abstract class Stub extends Binder implements ITextToSpeechService {
        private static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechService";
        static final int TRANSACTION_speak = 1;
        static final int TRANSACTION_synthesizeToFileDescriptor = 2;
        static final int TRANSACTION_playAudio = 3;
        static final int TRANSACTION_playSilence = 4;
        static final int TRANSACTION_isSpeaking = 5;
        static final int TRANSACTION_stop = 6;
        static final int TRANSACTION_getLanguage = 7;
        static final int TRANSACTION_getClientDefaultLanguage = 8;
        static final int TRANSACTION_isLanguageAvailable = 9;
        static final int TRANSACTION_getFeaturesForLanguage = 10;
        static final int TRANSACTION_loadLanguage = 11;
        static final int TRANSACTION_setCallback = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITextToSpeechService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITextToSpeechService)) {
                return (ITextToSpeechService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg3;
            Uri _arg1;
            Bundle _arg32;
            ParcelFileDescriptor _arg2;
            Bundle _arg33;
            Bundle _arg34;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg0 = data.readStrongBinder();
                    String _arg12 = data.readString();
                    int _arg22 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg34 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg34 = null;
                    }
                    int _result = speak(_arg0, _arg12, _arg22, _arg34);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg02 = data.readStrongBinder();
                    String _arg13 = data.readString();
                    if (0 != data.readInt()) {
                        _arg2 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg33 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg33 = null;
                    }
                    int _result2 = synthesizeToFileDescriptor(_arg02, _arg13, _arg2, _arg33);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg1 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg23 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg32 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg32 = null;
                    }
                    int _result3 = playAudio(_arg03, _arg1, _arg23, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg04 = data.readStrongBinder();
                    long _arg14 = data.readLong();
                    int _arg24 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    int _result4 = playSilence(_arg04, _arg14, _arg24, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = isSpeaking();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg05 = data.readStrongBinder();
                    int _result6 = stop(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result7 = getLanguage();
                    reply.writeNoException();
                    reply.writeStringArray(_result7);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result8 = getClientDefaultLanguage();
                    reply.writeNoException();
                    reply.writeStringArray(_result8);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    String _arg15 = data.readString();
                    String _arg25 = data.readString();
                    int _result9 = isLanguageAvailable(_arg06, _arg15, _arg25);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    String _arg16 = data.readString();
                    String _arg26 = data.readString();
                    String[] _result10 = getFeaturesForLanguage(_arg07, _arg16, _arg26);
                    reply.writeNoException();
                    reply.writeStringArray(_result10);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg08 = data.readStrongBinder();
                    String _arg17 = data.readString();
                    String _arg27 = data.readString();
                    String _arg35 = data.readString();
                    int _result11 = loadLanguage(_arg08, _arg17, _arg27, _arg35);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg09 = data.readStrongBinder();
                    ITextToSpeechCallback _arg18 = ITextToSpeechCallback.Stub.asInterface(data.readStrongBinder());
                    setCallback(_arg09, _arg18);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ITextToSpeechService$Stub$Proxy.class */
        private static class Proxy implements ITextToSpeechService {
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

            @Override // android.speech.tts.ITextToSpeechService
            public int speak(IBinder callingInstance, String text, int queueMode, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    _data.writeString(text);
                    _data.writeInt(queueMode);
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // android.speech.tts.ITextToSpeechService
            public int synthesizeToFileDescriptor(IBinder callingInstance, String text, ParcelFileDescriptor fileDescriptor, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    _data.writeString(text);
                    if (fileDescriptor != null) {
                        _data.writeInt(1);
                        fileDescriptor.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // android.speech.tts.ITextToSpeechService
            public int playAudio(IBinder callingInstance, Uri audioUri, int queueMode, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    if (audioUri != null) {
                        _data.writeInt(1);
                        audioUri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(queueMode);
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // android.speech.tts.ITextToSpeechService
            public int playSilence(IBinder callingInstance, long duration, int queueMode, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    _data.writeLong(duration);
                    _data.writeInt(queueMode);
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, _reply, 0);
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

            @Override // android.speech.tts.ITextToSpeechService
            public boolean isSpeaking() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int stop(IBinder callingInstance) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    this.mRemote.transact(6, _data, _reply, 0);
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

            @Override // android.speech.tts.ITextToSpeechService
            public String[] getLanguage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public String[] getClientDefaultLanguage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int isLanguageAvailable(String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
                    this.mRemote.transact(9, _data, _reply, 0);
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

            @Override // android.speech.tts.ITextToSpeechService
            public String[] getFeaturesForLanguage(String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int loadLanguage(IBinder caller, String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(caller);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
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

            @Override // android.speech.tts.ITextToSpeechService
            public void setCallback(IBinder caller, ITextToSpeechCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(caller);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
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
        }
    }
}