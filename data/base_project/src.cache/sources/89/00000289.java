package android.bluetooth;

import android.os.Handler;
import android.os.ParcelUuid;
import java.io.Closeable;
import java.io.IOException;

/* loaded from: BluetoothServerSocket.class */
public final class BluetoothServerSocket implements Closeable {
    final BluetoothSocket mSocket;
    private Handler mHandler;
    private int mMessage;
    private final int mChannel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothServerSocket(int type, boolean auth, boolean encrypt, int port) throws IOException {
        this.mChannel = port;
        this.mSocket = new BluetoothSocket(type, -1, auth, encrypt, null, port, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothServerSocket(int type, boolean auth, boolean encrypt, ParcelUuid uuid) throws IOException {
        this.mSocket = new BluetoothSocket(type, -1, auth, encrypt, null, -1, uuid);
        this.mChannel = this.mSocket.getPort();
    }

    public BluetoothSocket accept() throws IOException {
        return accept(-1);
    }

    public BluetoothSocket accept(int timeout) throws IOException {
        return this.mSocket.accept(timeout);
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        synchronized (this) {
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(this.mMessage).sendToTarget();
            }
        }
        this.mSocket.close();
    }

    synchronized void setCloseHandler(Handler handler, int message) {
        this.mHandler = handler;
        this.mMessage = message;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setServiceName(String ServiceName) {
        this.mSocket.setServiceName(ServiceName);
    }

    public int getChannel() {
        return this.mChannel;
    }
}