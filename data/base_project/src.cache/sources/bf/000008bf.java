package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: DhcpResults.class */
public class DhcpResults implements Parcelable {
    private static final String TAG = "DhcpResults";
    public final LinkProperties linkProperties;
    public InetAddress serverAddress;
    public String vendorInfo;
    public int leaseDuration;
    public static final Parcelable.Creator<DhcpResults> CREATOR = new Parcelable.Creator<DhcpResults>() { // from class: android.net.DhcpResults.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpResults createFromParcel(Parcel in) {
            DhcpResults prop = new DhcpResults((LinkProperties) in.readParcelable(null));
            prop.leaseDuration = in.readInt();
            if (in.readByte() == 1) {
                try {
                    prop.serverAddress = InetAddress.getByAddress(in.createByteArray());
                } catch (UnknownHostException e) {
                }
            }
            prop.vendorInfo = in.readString();
            return prop;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpResults[] newArray(int size) {
            return new DhcpResults[size];
        }
    };

    public DhcpResults() {
        this.linkProperties = new LinkProperties();
    }

    public DhcpResults(DhcpResults source) {
        if (source != null) {
            this.linkProperties = new LinkProperties(source.linkProperties);
            this.serverAddress = source.serverAddress;
            this.leaseDuration = source.leaseDuration;
            this.vendorInfo = source.vendorInfo;
            return;
        }
        this.linkProperties = new LinkProperties();
    }

    public DhcpResults(LinkProperties lp) {
        this.linkProperties = new LinkProperties(lp);
    }

    public void updateFromDhcpRequest(DhcpResults orig) {
        if (orig == null || orig.linkProperties == null) {
            return;
        }
        if (this.linkProperties.getRoutes().size() == 0) {
            for (RouteInfo r : orig.linkProperties.getRoutes()) {
                this.linkProperties.addRoute(r);
            }
        }
        if (this.linkProperties.getDnses().size() == 0) {
            for (InetAddress d : orig.linkProperties.getDnses()) {
                this.linkProperties.addDns(d);
            }
        }
    }

    public boolean hasMeteredHint() {
        if (this.vendorInfo != null) {
            return this.vendorInfo.contains("ANDROID_METERED");
        }
        return false;
    }

    public void clear() {
        this.linkProperties.clear();
        this.serverAddress = null;
        this.vendorInfo = null;
        this.leaseDuration = 0;
    }

    public String toString() {
        StringBuffer str = new StringBuffer(this.linkProperties.toString());
        str.append(" DHCP server ").append(this.serverAddress);
        str.append(" Vendor info ").append(this.vendorInfo);
        str.append(" lease ").append(this.leaseDuration).append(" seconds");
        return str.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DhcpResults) {
            DhcpResults target = (DhcpResults) obj;
            if (this.linkProperties == null) {
                if (target.linkProperties != null) {
                    return false;
                }
            } else if (!this.linkProperties.equals(target.linkProperties)) {
                return false;
            }
            if (this.serverAddress == null) {
                if (target.serverAddress != null) {
                    return false;
                }
            } else if (!this.serverAddress.equals(target.serverAddress)) {
                return false;
            }
            if (this.vendorInfo == null) {
                if (target.vendorInfo != null) {
                    return false;
                }
            } else if (!this.vendorInfo.equals(target.vendorInfo)) {
                return false;
            }
            return this.leaseDuration == target.leaseDuration;
        }
        return false;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.linkProperties.writeToParcel(dest, flags);
        dest.writeInt(this.leaseDuration);
        if (this.serverAddress != null) {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.serverAddress.getAddress());
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeString(this.vendorInfo);
    }

    public void setInterfaceName(String interfaceName) {
        this.linkProperties.setInterfaceName(interfaceName);
    }

    public boolean addLinkAddress(String addrString, int prefixLength) {
        try {
            InetAddress addr = NetworkUtils.numericToInetAddress(addrString);
            LinkAddress linkAddress = new LinkAddress(addr, prefixLength);
            this.linkProperties.addLinkAddress(linkAddress);
            RouteInfo routeInfo = new RouteInfo(linkAddress);
            this.linkProperties.addRoute(routeInfo);
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "addLinkAddress failed with addrString " + addrString);
            return true;
        }
    }

    public boolean addGateway(String addrString) {
        try {
            this.linkProperties.addRoute(new RouteInfo(NetworkUtils.numericToInetAddress(addrString)));
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "addGateway failed with addrString " + addrString);
            return true;
        }
    }

    public boolean addDns(String addrString) {
        if (!TextUtils.isEmpty(addrString)) {
            try {
                this.linkProperties.addDns(NetworkUtils.numericToInetAddress(addrString));
                return false;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "addDns failed with addrString " + addrString);
                return true;
            }
        }
        return false;
    }

    public boolean setServerAddress(String addrString) {
        try {
            this.serverAddress = NetworkUtils.numericToInetAddress(addrString);
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "setServerAddress failed with addrString " + addrString);
            return true;
        }
    }

    public void setLeaseDuration(int duration) {
        this.leaseDuration = duration;
    }

    public void setVendorInfo(String info) {
        this.vendorInfo = info;
    }

    public void setDomains(String domains) {
        this.linkProperties.setDomains(domains);
    }
}