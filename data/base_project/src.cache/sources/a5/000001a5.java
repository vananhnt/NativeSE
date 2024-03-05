package android.app;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

/* loaded from: IntentService.class */
public abstract class IntentService extends Service {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;
    private boolean mRedelivery;

    protected abstract void onHandleIntent(Intent intent);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: IntentService$ServiceHandler.class */
    public final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            IntentService.this.onHandleIntent((Intent) msg.obj);
            IntentService.this.stopSelf(msg.arg1);
        }
    }

    public IntentService(String name) {
        this.mName = name;
    }

    public void setIntentRedelivery(boolean enabled) {
        this.mRedelivery = enabled;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + this.mName + "]");
        thread.start();
        this.mServiceLooper = thread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        Message msg = this.mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        this.mServiceHandler.sendMessage(msg);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return this.mRedelivery ? 3 : 2;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mServiceLooper.quit();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }
}