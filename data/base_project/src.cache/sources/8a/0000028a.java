package android.bluetooth;

import android.net.LocalSocket;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.UUID;

/* loaded from: BluetoothSocket.class */
public final class BluetoothSocket implements Closeable {
    private static final String TAG = "BluetoothSocket";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final int MAX_RFCOMM_CHANNEL = 30;
    static final int TYPE_RFCOMM = 1;
    static final int TYPE_SCO = 2;
    static final int TYPE_L2CAP = 3;
    static final int EBADFD = 77;
    static final int EADDRINUSE = 98;
    static final int SEC_FLAG_ENCRYPT = 1;
    static final int SEC_FLAG_AUTH = 2;
    private final int mType;
    private BluetoothDevice mDevice;
    private String mAddress;
    private final boolean mAuth;
    private final boolean mEncrypt;
    private final BluetoothInputStream mInputStream;
    private final BluetoothOutputStream mOutputStream;
    private final ParcelUuid mUuid;
    private ParcelFileDescriptor mPfd;
    private LocalSocket mSocket;
    private InputStream mSocketIS;
    private OutputStream mSocketOS;
    private int mPort;
    private int mFd;
    private String mServiceName;
    private static int PROXY_CONNECTION_TIMEOUT = BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED;
    private static int SOCK_SIGNAL_SIZE = 16;
    private volatile SocketState mSocketState;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BluetoothSocket$SocketState.class */
    public enum SocketState {
        INIT,
        CONNECTED,
        LISTENING,
        CLOSED
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothSocket(int type, int fd, boolean auth, boolean encrypt, BluetoothDevice device, int port, ParcelUuid uuid) throws IOException {
        if (type == 1 && uuid == null && fd == -1 && (port < 1 || port > 30)) {
            throw new IOException("Invalid RFCOMM channel: " + port);
        }
        if (uuid != null) {
            this.mUuid = uuid;
        } else {
            this.mUuid = new ParcelUuid(new UUID(0L, 0L));
        }
        this.mType = type;
        this.mAuth = auth;
        this.mEncrypt = encrypt;
        this.mDevice = device;
        this.mPort = port;
        this.mFd = fd;
        this.mSocketState = SocketState.INIT;
        if (device == null) {
            this.mAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
        } else {
            this.mAddress = device.getAddress();
        }
        this.mInputStream = new BluetoothInputStream(this);
        this.mOutputStream = new BluetoothOutputStream(this);
    }

    private BluetoothSocket(BluetoothSocket s) {
        this.mUuid = s.mUuid;
        this.mType = s.mType;
        this.mAuth = s.mAuth;
        this.mEncrypt = s.mEncrypt;
        this.mPort = s.mPort;
        this.mInputStream = new BluetoothInputStream(this);
        this.mOutputStream = new BluetoothOutputStream(this);
        this.mServiceName = s.mServiceName;
    }

    private BluetoothSocket acceptSocket(String RemoteAddr) throws IOException {
        BluetoothSocket as = new BluetoothSocket(this);
        as.mSocketState = SocketState.CONNECTED;
        FileDescriptor[] fds = this.mSocket.getAncillaryFileDescriptors();
        if (fds == null || fds.length != 1) {
            Log.e(TAG, "socket fd passed from stack failed, fds: " + fds);
            as.close();
            throw new IOException("bt socket acept failed");
        }
        as.mSocket = new LocalSocket(fds[0]);
        as.mSocketIS = as.mSocket.getInputStream();
        as.mSocketOS = as.mSocket.getOutputStream();
        as.mAddress = RemoteAddr;
        as.mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(RemoteAddr);
        return as;
    }

    private BluetoothSocket(int type, int fd, boolean auth, boolean encrypt, String address, int port) throws IOException {
        this(type, fd, auth, encrypt, new BluetoothDevice(address), port, null);
    }

    protected void finalize() throws Throwable {
        try {
            close();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private int getSecurityFlags() {
        int flags = 0;
        if (this.mAuth) {
            flags = 0 | 2;
        }
        if (this.mEncrypt) {
            flags |= 1;
        }
        return flags;
    }

    public BluetoothDevice getRemoteDevice() {
        return this.mDevice;
    }

    public InputStream getInputStream() throws IOException {
        return this.mInputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        return this.mOutputStream;
    }

    public boolean isConnected() {
        return this.mSocketState == SocketState.CONNECTED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setServiceName(String name) {
        this.mServiceName = name;
    }

    public void connect() throws IOException {
        if (this.mDevice == null) {
            throw new IOException("Connect is called on null device");
        }
        try {
            if (this.mSocketState == SocketState.CLOSED) {
                throw new IOException("socket closed");
            }
            IBluetooth bluetoothProxy = BluetoothAdapter.getDefaultAdapter().getBluetoothService(null);
            if (bluetoothProxy == null) {
                throw new IOException("Bluetooth is off");
            }
            this.mPfd = bluetoothProxy.connectSocket(this.mDevice, this.mType, this.mUuid, this.mPort, getSecurityFlags());
            synchronized (this) {
                Log.d(TAG, "connect(), SocketState: " + this.mSocketState + ", mPfd: " + this.mPfd);
                if (this.mSocketState == SocketState.CLOSED) {
                    throw new IOException("socket closed");
                }
                if (this.mPfd == null) {
                    throw new IOException("bt socket connect failed");
                }
                FileDescriptor fd = this.mPfd.getFileDescriptor();
                this.mSocket = new LocalSocket(fd);
                this.mSocketIS = this.mSocket.getInputStream();
                this.mSocketOS = this.mSocket.getOutputStream();
            }
            int channel = readInt(this.mSocketIS);
            if (channel <= 0) {
                throw new IOException("bt socket connect failed");
            }
            this.mPort = channel;
            waitSocketSignal(this.mSocketIS);
            synchronized (this) {
                if (this.mSocketState == SocketState.CLOSED) {
                    throw new IOException("bt socket closed");
                }
                this.mSocketState = SocketState.CONNECTED;
            }
        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int bindListen() {
        if (this.mSocketState == SocketState.CLOSED) {
            return 77;
        }
        IBluetooth bluetoothProxy = BluetoothAdapter.getDefaultAdapter().getBluetoothService(null);
        if (bluetoothProxy == null) {
            Log.e(TAG, "bindListen fail, reason: bluetooth is off");
            return -1;
        }
        try {
            this.mPfd = bluetoothProxy.createSocketChannel(this.mType, this.mServiceName, this.mUuid, this.mPort, getSecurityFlags());
            try {
                synchronized (this) {
                    if (this.mSocketState != SocketState.INIT) {
                        return 77;
                    }
                    if (this.mPfd == null) {
                        return -1;
                    }
                    FileDescriptor fd = this.mPfd.getFileDescriptor();
                    this.mSocket = new LocalSocket(fd);
                    this.mSocketIS = this.mSocket.getInputStream();
                    this.mSocketOS = this.mSocket.getOutputStream();
                    int channel = readInt(this.mSocketIS);
                    synchronized (this) {
                        if (this.mSocketState == SocketState.INIT) {
                            this.mSocketState = SocketState.LISTENING;
                        }
                    }
                    if (this.mPort == -1) {
                        this.mPort = channel;
                    }
                    return 0;
                }
            } catch (IOException e) {
                Log.e(TAG, "bindListen, fail to get port number, exception: " + e);
                return -1;
            }
        } catch (RemoteException e2) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothSocket accept(int timeout) throws IOException {
        BluetoothSocket acceptedSocket;
        if (this.mSocketState != SocketState.LISTENING) {
            throw new IOException("bt socket is not in listen state");
        }
        if (timeout > 0) {
            Log.d(TAG, "accept() set timeout (ms):" + timeout);
            this.mSocket.setSoTimeout(timeout);
        }
        String RemoteAddr = waitSocketSignal(this.mSocketIS);
        if (timeout > 0) {
            this.mSocket.setSoTimeout(0);
        }
        synchronized (this) {
            if (this.mSocketState != SocketState.LISTENING) {
                throw new IOException("bt socket is not in listen state");
            }
            acceptedSocket = acceptSocket(RemoteAddr);
        }
        return acceptedSocket;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int available() throws IOException {
        return this.mSocketIS.available();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void flush() throws IOException {
        this.mSocketOS.flush();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int read(byte[] b, int offset, int length) throws IOException {
        int ret = this.mSocketIS.read(b, offset, length);
        if (ret < 0) {
            throw new IOException("bt socket closed, read return: " + ret);
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int write(byte[] b, int offset, int length) throws IOException {
        this.mSocketOS.write(b, offset, length);
        return length;
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        if (this.mSocketState == SocketState.CLOSED) {
            return;
        }
        synchronized (this) {
            if (this.mSocketState == SocketState.CLOSED) {
                return;
            }
            this.mSocketState = SocketState.CLOSED;
            if (this.mSocket != null) {
                this.mSocket.shutdownInput();
                this.mSocket.shutdownOutput();
                this.mSocket.close();
                this.mSocket = null;
            }
            if (this.mPfd != null) {
                this.mPfd.detachFd();
            }
        }
    }

    void removeChannel() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPort() {
        return this.mPort;
    }

    private String convertAddr(byte[] addr) {
        return String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", Byte.valueOf(addr[0]), Byte.valueOf(addr[1]), Byte.valueOf(addr[2]), Byte.valueOf(addr[3]), Byte.valueOf(addr[4]), Byte.valueOf(addr[5]));
    }

    private String waitSocketSignal(InputStream is) throws IOException {
        byte[] sig = new byte[SOCK_SIGNAL_SIZE];
        readAll(is, sig);
        ByteBuffer bb = ByteBuffer.wrap(sig);
        bb.order(ByteOrder.nativeOrder());
        int size = bb.getShort();
        if (size != SOCK_SIGNAL_SIZE) {
            throw new IOException("Connection failure, wrong signal size: " + size);
        }
        byte[] addr = new byte[6];
        bb.get(addr);
        bb.getInt();
        int status = bb.getInt();
        String RemoteAddr = convertAddr(addr);
        if (status != 0) {
            throw new IOException("Connection failure, status: " + status);
        }
        return RemoteAddr;
    }

    private int readAll(InputStream is, byte[] b) throws IOException {
        int left = b.length;
        while (left > 0) {
            int ret = is.read(b, b.length - left, left);
            if (ret <= 0) {
                throw new IOException("read failed, socket might closed or timeout, read ret: " + ret);
            }
            left -= ret;
            if (left != 0) {
                Log.w(TAG, "readAll() looping, read partial size: " + (b.length - left) + ", expect size: " + b.length);
            }
        }
        return b.length;
    }

    private int readInt(InputStream is) throws IOException {
        byte[] ibytes = new byte[4];
        readAll(is, ibytes);
        ByteBuffer bb = ByteBuffer.wrap(ibytes);
        bb.order(ByteOrder.nativeOrder());
        return bb.getInt();
    }
}