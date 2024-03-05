package android.os;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Locale;
import libcore.io.OsConstants;

/* loaded from: CommonTimeUtils.class */
class CommonTimeUtils {
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -4;
    public static final int ERROR_DEAD_OBJECT = -7;
    private IBinder mRemote;
    private String mInterfaceDesc;

    public CommonTimeUtils(IBinder remote, String interfaceDesc) {
        this.mRemote = remote;
        this.mInterfaceDesc = interfaceDesc;
    }

    public int transactGetInt(int method_code, int error_ret_val) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(method_code, data, reply, 0);
            int res = reply.readInt();
            int ret_val = 0 == res ? reply.readInt() : error_ret_val;
            return ret_val;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public int transactSetInt(int method_code, int val) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            data.writeInt(val);
            this.mRemote.transact(method_code, data, reply, 0);
            int readInt = reply.readInt();
            reply.recycle();
            data.recycle();
            return readInt;
        } catch (RemoteException e) {
            reply.recycle();
            data.recycle();
            return -7;
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            throw th;
        }
    }

    public long transactGetLong(int method_code, long error_ret_val) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(method_code, data, reply, 0);
            int res = reply.readInt();
            long ret_val = 0 == res ? reply.readLong() : error_ret_val;
            return ret_val;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public int transactSetLong(int method_code, long val) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            data.writeLong(val);
            this.mRemote.transact(method_code, data, reply, 0);
            int readInt = reply.readInt();
            reply.recycle();
            data.recycle();
            return readInt;
        } catch (RemoteException e) {
            reply.recycle();
            data.recycle();
            return -7;
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            throw th;
        }
    }

    public String transactGetString(int method_code, String error_ret_val) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(method_code, data, reply, 0);
            int res = reply.readInt();
            String ret_val = 0 == res ? reply.readString() : error_ret_val;
            return ret_val;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public int transactSetString(int method_code, String val) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            data.writeString(val);
            this.mRemote.transact(method_code, data, reply, 0);
            int readInt = reply.readInt();
            reply.recycle();
            data.recycle();
            return readInt;
        } catch (RemoteException e) {
            reply.recycle();
            data.recycle();
            return -7;
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            throw th;
        }
    }

    public InetSocketAddress transactGetSockaddr(int method_code) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        InetSocketAddress ret_val = null;
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(method_code, data, reply, 0);
            int res = reply.readInt();
            if (0 == res) {
                int port = 0;
                String addrStr = null;
                int type = reply.readInt();
                if (OsConstants.AF_INET == type) {
                    int addr = reply.readInt();
                    port = reply.readInt();
                    addrStr = String.format(Locale.US, "%d.%d.%d.%d", Integer.valueOf((addr >> 24) & 255), Integer.valueOf((addr >> 16) & 255), Integer.valueOf((addr >> 8) & 255), Integer.valueOf(addr & 255));
                } else if (OsConstants.AF_INET6 == type) {
                    int addr1 = reply.readInt();
                    int addr2 = reply.readInt();
                    int addr3 = reply.readInt();
                    int addr4 = reply.readInt();
                    port = reply.readInt();
                    reply.readInt();
                    reply.readInt();
                    addrStr = String.format(Locale.US, "[%04X:%04X:%04X:%04X:%04X:%04X:%04X:%04X]", Integer.valueOf((addr1 >> 16) & 65535), Integer.valueOf(addr1 & 65535), Integer.valueOf((addr2 >> 16) & 65535), Integer.valueOf(addr2 & 65535), Integer.valueOf((addr3 >> 16) & 65535), Integer.valueOf(addr3 & 65535), Integer.valueOf((addr4 >> 16) & 65535), Integer.valueOf(addr4 & 65535));
                }
                if (null != addrStr) {
                    ret_val = new InetSocketAddress(addrStr, port);
                }
            }
            return ret_val;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public int transactSetSockaddr(int method_code, InetSocketAddress addr) {
        int ret_val;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            if (null == addr) {
                data.writeInt(0);
            } else {
                data.writeInt(1);
                InetAddress a = addr.getAddress();
                byte[] b = a.getAddress();
                int p = addr.getPort();
                if (a instanceof Inet4Address) {
                    int v4addr = ((b[0] & 255) << 24) | ((b[1] & 255) << 16) | ((b[2] & 255) << 8) | (b[3] & 255);
                    data.writeInt(OsConstants.AF_INET);
                    data.writeInt(v4addr);
                    data.writeInt(p);
                } else if (!(a instanceof Inet6Address)) {
                    reply.recycle();
                    data.recycle();
                    return -4;
                } else {
                    Inet6Address v6 = (Inet6Address) a;
                    data.writeInt(OsConstants.AF_INET6);
                    for (int i = 0; i < 4; i++) {
                        int aword = ((b[(i * 4) + 0] & 255) << 24) | ((b[(i * 4) + 1] & 255) << 16) | ((b[(i * 4) + 2] & 255) << 8) | (b[(i * 4) + 3] & 255);
                        data.writeInt(aword);
                    }
                    data.writeInt(p);
                    data.writeInt(0);
                    data.writeInt(v6.getScopeId());
                }
            }
            this.mRemote.transact(method_code, data, reply, 0);
            ret_val = reply.readInt();
            reply.recycle();
            data.recycle();
        } catch (RemoteException e) {
            ret_val = -7;
            reply.recycle();
            data.recycle();
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            throw th;
        }
        return ret_val;
    }
}