package android.speech;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.speech.IRecognitionService;
import android.util.Log;

/* loaded from: RecognitionService.class */
public abstract class RecognitionService extends Service {
    public static final String SERVICE_INTERFACE = "android.speech.RecognitionService";
    public static final String SERVICE_META_DATA = "android.speech";
    private static final String TAG = "RecognitionService";
    private static final boolean DBG = false;
    private static final int MSG_START_LISTENING = 1;
    private static final int MSG_STOP_LISTENING = 2;
    private static final int MSG_CANCEL = 3;
    private static final int MSG_RESET = 4;
    private RecognitionServiceBinder mBinder = new RecognitionServiceBinder(this);
    private Callback mCurrentCallback = null;
    private final Handler mHandler = new Handler() { // from class: android.speech.RecognitionService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    StartListeningArgs args = (StartListeningArgs) msg.obj;
                    RecognitionService.this.dispatchStartListening(args.mIntent, args.mListener);
                    return;
                case 2:
                    RecognitionService.this.dispatchStopListening((IRecognitionListener) msg.obj);
                    return;
                case 3:
                    RecognitionService.this.dispatchCancel((IRecognitionListener) msg.obj);
                    return;
                case 4:
                    RecognitionService.this.dispatchClearCallback();
                    return;
                default:
                    return;
            }
        }
    };

    protected abstract void onStartListening(Intent intent, Callback callback);

    protected abstract void onCancel(Callback callback);

    protected abstract void onStopListening(Callback callback);

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchStartListening(Intent intent, IRecognitionListener listener) {
        if (this.mCurrentCallback == null) {
            this.mCurrentCallback = new Callback(listener);
            onStartListening(intent, this.mCurrentCallback);
            return;
        }
        try {
            listener.onError(8);
        } catch (RemoteException e) {
            Log.d(TAG, "onError call from startListening failed");
        }
        Log.i(TAG, "concurrent startListening received - ignoring this call");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchStopListening(IRecognitionListener listener) {
        try {
            if (this.mCurrentCallback == null) {
                listener.onError(5);
                Log.w(TAG, "stopListening called with no preceding startListening - ignoring");
            } else if (this.mCurrentCallback.mListener.asBinder() != listener.asBinder()) {
                listener.onError(8);
                Log.w(TAG, "stopListening called by other caller than startListening - ignoring");
            } else {
                onStopListening(this.mCurrentCallback);
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onError call from stopListening failed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchCancel(IRecognitionListener listener) {
        if (this.mCurrentCallback == null) {
            return;
        }
        if (this.mCurrentCallback.mListener.asBinder() != listener.asBinder()) {
            Log.w(TAG, "cancel called by client who did not call startListening - ignoring");
            return;
        }
        onCancel(this.mCurrentCallback);
        this.mCurrentCallback = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchClearCallback() {
        this.mCurrentCallback = null;
    }

    /* loaded from: RecognitionService$StartListeningArgs.class */
    private class StartListeningArgs {
        public final Intent mIntent;
        public final IRecognitionListener mListener;

        public StartListeningArgs(Intent intent, IRecognitionListener listener) {
            this.mIntent = intent;
            this.mListener = listener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkPermissions(IRecognitionListener listener) {
        if (checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) == 0) {
            return true;
        }
        try {
            Log.e(TAG, "call for recognition service without RECORD_AUDIO permissions");
            listener.onError(9);
            return false;
        } catch (RemoteException re) {
            Log.e(TAG, "sending ERROR_INSUFFICIENT_PERMISSIONS message failed", re);
            return false;
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mCurrentCallback = null;
        this.mBinder.clearReference();
        super.onDestroy();
    }

    /* loaded from: RecognitionService$Callback.class */
    public class Callback {
        private final IRecognitionListener mListener;

        private Callback(IRecognitionListener listener) {
            this.mListener = listener;
        }

        public void beginningOfSpeech() throws RemoteException {
            this.mListener.onBeginningOfSpeech();
        }

        public void bufferReceived(byte[] buffer) throws RemoteException {
            this.mListener.onBufferReceived(buffer);
        }

        public void endOfSpeech() throws RemoteException {
            this.mListener.onEndOfSpeech();
        }

        public void error(int error) throws RemoteException {
            Message.obtain(RecognitionService.this.mHandler, 4).sendToTarget();
            this.mListener.onError(error);
        }

        public void partialResults(Bundle partialResults) throws RemoteException {
            this.mListener.onPartialResults(partialResults);
        }

        public void readyForSpeech(Bundle params) throws RemoteException {
            this.mListener.onReadyForSpeech(params);
        }

        public void results(Bundle results) throws RemoteException {
            Message.obtain(RecognitionService.this.mHandler, 4).sendToTarget();
            this.mListener.onResults(results);
        }

        public void rmsChanged(float rmsdB) throws RemoteException {
            this.mListener.onRmsChanged(rmsdB);
        }
    }

    /* loaded from: RecognitionService$RecognitionServiceBinder.class */
    private static class RecognitionServiceBinder extends IRecognitionService.Stub {
        private RecognitionService mInternalService;

        public RecognitionServiceBinder(RecognitionService service) {
            this.mInternalService = service;
        }

        @Override // android.speech.IRecognitionService
        public void startListening(Intent recognizerIntent, IRecognitionListener listener) {
            if (this.mInternalService != null && this.mInternalService.checkPermissions(listener)) {
                Handler handler = this.mInternalService.mHandler;
                Handler handler2 = this.mInternalService.mHandler;
                RecognitionService recognitionService = this.mInternalService;
                recognitionService.getClass();
                handler.sendMessage(Message.obtain(handler2, 1, new StartListeningArgs(recognizerIntent, listener)));
            }
        }

        @Override // android.speech.IRecognitionService
        public void stopListening(IRecognitionListener listener) {
            if (this.mInternalService != null && this.mInternalService.checkPermissions(listener)) {
                this.mInternalService.mHandler.sendMessage(Message.obtain(this.mInternalService.mHandler, 2, listener));
            }
        }

        @Override // android.speech.IRecognitionService
        public void cancel(IRecognitionListener listener) {
            if (this.mInternalService != null && this.mInternalService.checkPermissions(listener)) {
                this.mInternalService.mHandler.sendMessage(Message.obtain(this.mInternalService.mHandler, 3, listener));
            }
        }

        public void clearReference() {
            this.mInternalService = null;
        }
    }
}