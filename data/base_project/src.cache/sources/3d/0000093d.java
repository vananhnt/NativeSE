package android.net;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.IConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.net.VpnConfig;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/* loaded from: VpnService.class */
public class VpnService extends Service {
    public static final String SERVICE_INTERFACE = "android.net.VpnService";

    static /* synthetic */ IConnectivityManager access$100() {
        return getService();
    }

    private static IConnectivityManager getService() {
        return IConnectivityManager.Stub.asInterface(ServiceManager.getService(Context.CONNECTIVITY_SERVICE));
    }

    public static Intent prepare(Context context) {
        try {
            if (getService().prepareVpn(context.getPackageName(), null)) {
                return null;
            }
        } catch (RemoteException e) {
        }
        return VpnConfig.getIntentForConfirmation();
    }

    public boolean protect(int socket) {
        ParcelFileDescriptor dup = null;
        try {
            dup = ParcelFileDescriptor.fromFd(socket);
            boolean protectVpn = getService().protectVpn(dup);
            try {
                dup.close();
            } catch (Exception e) {
            }
            return protectVpn;
        } catch (Exception e2) {
            try {
                dup.close();
            } catch (Exception e3) {
            }
            return false;
        } catch (Throwable th) {
            try {
                dup.close();
            } catch (Exception e4) {
            }
            throw th;
        }
    }

    public boolean protect(Socket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    public boolean protect(DatagramSocket socket) {
        return protect(socket.getFileDescriptor$().getInt$());
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (intent != null && "android.net.VpnService".equals(intent.getAction())) {
            return new Callback();
        }
        return null;
    }

    public void onRevoke() {
        stopSelf();
    }

    /* loaded from: VpnService$Callback.class */
    private class Callback extends Binder {
        private Callback() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            if (code == 16777215) {
                VpnService.this.onRevoke();
                return true;
            }
            return false;
        }
    }

    /* loaded from: VpnService$Builder.class */
    public class Builder {
        private final VpnConfig mConfig = new VpnConfig();
        private final List<LinkAddress> mAddresses = new ArrayList();
        private final List<RouteInfo> mRoutes = new ArrayList();

        public Builder() {
            this.mConfig.user = VpnService.this.getClass().getName();
        }

        public Builder setSession(String session) {
            this.mConfig.session = session;
            return this;
        }

        public Builder setConfigureIntent(PendingIntent intent) {
            this.mConfig.configureIntent = intent;
            return this;
        }

        public Builder setMtu(int mtu) {
            if (mtu <= 0) {
                throw new IllegalArgumentException("Bad mtu");
            }
            this.mConfig.mtu = mtu;
            return this;
        }

        private void check(InetAddress address, int prefixLength) {
            if (address.isLoopbackAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            if (address instanceof Inet4Address) {
                if (prefixLength < 0 || prefixLength > 32) {
                    throw new IllegalArgumentException("Bad prefixLength");
                }
            } else if (address instanceof Inet6Address) {
                if (prefixLength < 0 || prefixLength > 128) {
                    throw new IllegalArgumentException("Bad prefixLength");
                }
            } else {
                throw new IllegalArgumentException("Unsupported family");
            }
        }

        public Builder addAddress(InetAddress address, int prefixLength) {
            check(address, prefixLength);
            if (address.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            this.mAddresses.add(new LinkAddress(address, prefixLength));
            return this;
        }

        public Builder addAddress(String address, int prefixLength) {
            return addAddress(InetAddress.parseNumericAddress(address), prefixLength);
        }

        public Builder addRoute(InetAddress address, int prefixLength) {
            check(address, prefixLength);
            int offset = prefixLength / 8;
            byte[] bytes = address.getAddress();
            if (offset < bytes.length) {
                bytes[offset] = (byte) (bytes[offset] << (prefixLength % 8));
                while (offset < bytes.length) {
                    if (bytes[offset] == 0) {
                        offset++;
                    } else {
                        throw new IllegalArgumentException("Bad address");
                    }
                }
            }
            this.mRoutes.add(new RouteInfo(new LinkAddress(address, prefixLength), null));
            return this;
        }

        public Builder addRoute(String address, int prefixLength) {
            return addRoute(InetAddress.parseNumericAddress(address), prefixLength);
        }

        public Builder addDnsServer(InetAddress address) {
            if (address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Bad address");
            }
            if (this.mConfig.dnsServers == null) {
                this.mConfig.dnsServers = new ArrayList();
            }
            this.mConfig.dnsServers.add(address.getHostAddress());
            return this;
        }

        public Builder addDnsServer(String address) {
            return addDnsServer(InetAddress.parseNumericAddress(address));
        }

        public Builder addSearchDomain(String domain) {
            if (this.mConfig.searchDomains == null) {
                this.mConfig.searchDomains = new ArrayList();
            }
            this.mConfig.searchDomains.add(domain);
            return this;
        }

        public ParcelFileDescriptor establish() {
            this.mConfig.addresses = this.mAddresses;
            this.mConfig.routes = this.mRoutes;
            try {
                return VpnService.access$100().establishVpn(this.mConfig);
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}