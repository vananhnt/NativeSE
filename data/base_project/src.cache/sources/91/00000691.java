package android.hardware.usb;

import android.util.Log;
import java.nio.ByteBuffer;

/* loaded from: UsbRequest.class */
public class UsbRequest {
    private static final String TAG = "UsbRequest";
    private int mNativeContext;
    private UsbEndpoint mEndpoint;
    private ByteBuffer mBuffer;
    private int mLength;
    private Object mClientData;

    private native boolean native_init(UsbDeviceConnection usbDeviceConnection, int i, int i2, int i3, int i4);

    private native void native_close();

    private native boolean native_queue_array(byte[] bArr, int i, boolean z);

    private native int native_dequeue_array(byte[] bArr, int i, boolean z);

    private native boolean native_queue_direct(ByteBuffer byteBuffer, int i, boolean z);

    private native int native_dequeue_direct();

    private native boolean native_cancel();

    public boolean initialize(UsbDeviceConnection connection, UsbEndpoint endpoint) {
        this.mEndpoint = endpoint;
        return native_init(connection, endpoint.getAddress(), endpoint.getAttributes(), endpoint.getMaxPacketSize(), endpoint.getInterval());
    }

    public void close() {
        this.mEndpoint = null;
        native_close();
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mEndpoint != null) {
                Log.v(TAG, "endpoint still open in finalize(): " + this);
                close();
            }
        } finally {
            super.finalize();
        }
    }

    public UsbEndpoint getEndpoint() {
        return this.mEndpoint;
    }

    public Object getClientData() {
        return this.mClientData;
    }

    public void setClientData(Object data) {
        this.mClientData = data;
    }

    public boolean queue(ByteBuffer buffer, int length) {
        boolean result;
        boolean out = this.mEndpoint.getDirection() == 0;
        if (buffer.isDirect()) {
            result = native_queue_direct(buffer, length, out);
        } else if (buffer.hasArray()) {
            result = native_queue_array(buffer.array(), length, out);
        } else {
            throw new IllegalArgumentException("buffer is not direct and has no array");
        }
        if (result) {
            this.mBuffer = buffer;
            this.mLength = length;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dequeue() {
        int bytesRead;
        boolean out = this.mEndpoint.getDirection() == 0;
        if (this.mBuffer.isDirect()) {
            bytesRead = native_dequeue_direct();
        } else {
            bytesRead = native_dequeue_array(this.mBuffer.array(), this.mLength, out);
        }
        if (bytesRead >= 0) {
            this.mBuffer.position(Math.min(bytesRead, this.mLength));
        }
        this.mBuffer = null;
        this.mLength = 0;
    }

    public boolean cancel() {
        return native_cancel();
    }
}