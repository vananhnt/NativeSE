package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/* loaded from: RouteInfo.class */
public class RouteInfo implements Parcelable {
    private final LinkAddress mDestination;
    private final InetAddress mGateway;
    private final String mInterface;
    private final boolean mIsDefault;
    private final boolean mIsHost;
    private final boolean mHasGateway;
    public static final Parcelable.Creator<RouteInfo> CREATOR = new Parcelable.Creator<RouteInfo>() { // from class: android.net.RouteInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RouteInfo createFromParcel(Parcel in) {
            InetAddress destAddr = null;
            int prefix = 0;
            InetAddress gateway = null;
            if (in.readByte() == 1) {
                byte[] addr = in.createByteArray();
                prefix = in.readInt();
                try {
                    destAddr = InetAddress.getByAddress(addr);
                } catch (UnknownHostException e) {
                }
            }
            if (in.readByte() == 1) {
                byte[] addr2 = in.createByteArray();
                try {
                    gateway = InetAddress.getByAddress(addr2);
                } catch (UnknownHostException e2) {
                }
            }
            String iface = in.readString();
            LinkAddress dest = null;
            if (destAddr != null) {
                dest = new LinkAddress(destAddr, prefix);
            }
            return new RouteInfo(dest, gateway, iface);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RouteInfo[] newArray(int size) {
            return new RouteInfo[size];
        }
    };

    public RouteInfo(LinkAddress destination, InetAddress gateway, String iface) {
        if (destination == null) {
            if (gateway != null) {
                if (gateway instanceof Inet4Address) {
                    destination = new LinkAddress(Inet4Address.ANY, 0);
                } else {
                    destination = new LinkAddress(Inet6Address.ANY, 0);
                }
            } else {
                throw new IllegalArgumentException("Invalid arguments passed in: " + gateway + Separators.COMMA + destination);
            }
        }
        if (gateway == null) {
            if (destination.getAddress() instanceof Inet4Address) {
                gateway = Inet4Address.ANY;
            } else {
                gateway = Inet6Address.ANY;
            }
        }
        this.mHasGateway = !gateway.isAnyLocalAddress();
        this.mDestination = new LinkAddress(NetworkUtils.getNetworkPart(destination.getAddress(), destination.getNetworkPrefixLength()), destination.getNetworkPrefixLength());
        this.mGateway = gateway;
        this.mInterface = iface;
        this.mIsDefault = isDefault();
        this.mIsHost = isHost();
    }

    public RouteInfo(LinkAddress destination, InetAddress gateway) {
        this(destination, gateway, null);
    }

    public RouteInfo(InetAddress gateway) {
        this(null, gateway, null);
    }

    public RouteInfo(LinkAddress host) {
        this(host, null, null);
    }

    public static RouteInfo makeHostRoute(InetAddress host, String iface) {
        return makeHostRoute(host, null, iface);
    }

    public static RouteInfo makeHostRoute(InetAddress host, InetAddress gateway, String iface) {
        if (host == null) {
            return null;
        }
        if (host instanceof Inet4Address) {
            return new RouteInfo(new LinkAddress(host, 32), gateway, iface);
        }
        return new RouteInfo(new LinkAddress(host, 128), gateway, iface);
    }

    private boolean isHost() {
        return ((this.mDestination.getAddress() instanceof Inet4Address) && this.mDestination.getNetworkPrefixLength() == 32) || ((this.mDestination.getAddress() instanceof Inet6Address) && this.mDestination.getNetworkPrefixLength() == 128);
    }

    private boolean isDefault() {
        boolean val = false;
        if (this.mGateway != null) {
            if (this.mGateway instanceof Inet4Address) {
                val = this.mDestination == null || this.mDestination.getNetworkPrefixLength() == 0;
            } else {
                val = this.mDestination == null || this.mDestination.getNetworkPrefixLength() == 0;
            }
        }
        return val;
    }

    public LinkAddress getDestination() {
        return this.mDestination;
    }

    public InetAddress getGateway() {
        return this.mGateway;
    }

    public String getInterface() {
        return this.mInterface;
    }

    public boolean isDefaultRoute() {
        return this.mIsDefault;
    }

    public boolean isHostRoute() {
        return this.mIsHost;
    }

    public boolean hasGateway() {
        return this.mHasGateway;
    }

    public String toString() {
        String val = this.mDestination != null ? this.mDestination.toString() : "";
        if (this.mGateway != null) {
            val = val + " -> " + this.mGateway.getHostAddress();
        }
        return val;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mDestination == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.mDestination.getAddress().getAddress());
            dest.writeInt(this.mDestination.getNetworkPrefixLength());
        }
        if (this.mGateway == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.mGateway.getAddress());
        }
        dest.writeString(this.mInterface);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RouteInfo) {
            RouteInfo target = (RouteInfo) obj;
            boolean sameDestination = this.mDestination == null ? target.getDestination() == null : this.mDestination.equals(target.getDestination());
            boolean sameAddress = this.mGateway == null ? target.getGateway() == null : this.mGateway.equals(target.getGateway());
            boolean sameInterface = this.mInterface == null ? target.getInterface() == null : this.mInterface.equals(target.getInterface());
            return sameDestination && sameAddress && sameInterface && this.mIsDefault == target.mIsDefault;
        }
        return false;
    }

    public int hashCode() {
        return (this.mDestination == null ? 0 : this.mDestination.hashCode() * 41) + (this.mGateway == null ? 0 : this.mGateway.hashCode() * 47) + (this.mInterface == null ? 0 : this.mInterface.hashCode() * 67) + (this.mIsDefault ? 3 : 7);
    }

    protected boolean matches(InetAddress destination) {
        if (destination == null) {
            return false;
        }
        InetAddress dstNet = NetworkUtils.getNetworkPart(destination, this.mDestination.getNetworkPrefixLength());
        return this.mDestination.getAddress().equals(dstNet);
    }

    public static RouteInfo selectBestRoute(Collection<RouteInfo> routes, InetAddress dest) {
        if (routes == null || dest == null) {
            return null;
        }
        RouteInfo bestRoute = null;
        for (RouteInfo route : routes) {
            if (NetworkUtils.addressTypeMatches(route.mDestination.getAddress(), dest) && (bestRoute == null || bestRoute.mDestination.getNetworkPrefixLength() < route.mDestination.getNetworkPrefixLength())) {
                if (route.matches(dest)) {
                    bestRoute = route;
                }
            }
        }
        return bestRoute;
    }
}