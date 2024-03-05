package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

/* loaded from: LinkProperties.class */
public class LinkProperties implements Parcelable {
    private String mIfaceName;
    private String mDomains;
    private ProxyProperties mHttpProxy;
    private int mMtu;
    public static final Parcelable.Creator<LinkProperties> CREATOR = new Parcelable.Creator<LinkProperties>() { // from class: android.net.LinkProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkProperties createFromParcel(Parcel in) {
            LinkProperties netProp = new LinkProperties();
            String iface = in.readString();
            if (iface != null) {
                netProp.setInterfaceName(iface);
            }
            int addressCount = in.readInt();
            for (int i = 0; i < addressCount; i++) {
                netProp.addLinkAddress((LinkAddress) in.readParcelable(null));
            }
            int addressCount2 = in.readInt();
            for (int i2 = 0; i2 < addressCount2; i2++) {
                try {
                    netProp.addDns(InetAddress.getByAddress(in.createByteArray()));
                } catch (UnknownHostException e) {
                }
            }
            netProp.setDomains(in.readString());
            netProp.setMtu(in.readInt());
            int addressCount3 = in.readInt();
            for (int i3 = 0; i3 < addressCount3; i3++) {
                netProp.addRoute((RouteInfo) in.readParcelable(null));
            }
            if (in.readByte() == 1) {
                netProp.setHttpProxy((ProxyProperties) in.readParcelable(null));
            }
            ArrayList<LinkProperties> stackedLinks = new ArrayList<>();
            in.readList(stackedLinks, LinkProperties.class.getClassLoader());
            Iterator i$ = stackedLinks.iterator();
            while (i$.hasNext()) {
                LinkProperties stackedLink = i$.next();
                netProp.addStackedLink(stackedLink);
            }
            return netProp;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkProperties[] newArray(int size) {
            return new LinkProperties[size];
        }
    };
    private Collection<LinkAddress> mLinkAddresses = new ArrayList();
    private Collection<InetAddress> mDnses = new ArrayList();
    private Collection<RouteInfo> mRoutes = new ArrayList();
    private Hashtable<String, LinkProperties> mStackedLinks = new Hashtable<>();

    /* loaded from: LinkProperties$CompareResult.class */
    public static class CompareResult<T> {
        public Collection<T> removed = new ArrayList();
        public Collection<T> added = new ArrayList();

        public String toString() {
            String retVal = "removed=[";
            for (T addr : this.removed) {
                retVal = retVal + addr.toString() + Separators.COMMA;
            }
            String retVal2 = retVal + "] added=[";
            for (T addr2 : this.added) {
                retVal2 = retVal2 + addr2.toString() + Separators.COMMA;
            }
            return retVal2 + "]";
        }
    }

    public LinkProperties() {
        clear();
    }

    public LinkProperties(LinkProperties source) {
        if (source != null) {
            this.mIfaceName = source.getInterfaceName();
            for (LinkAddress l : source.getLinkAddresses()) {
                this.mLinkAddresses.add(l);
            }
            for (InetAddress i : source.getDnses()) {
                this.mDnses.add(i);
            }
            this.mDomains = source.getDomains();
            for (RouteInfo r : source.getRoutes()) {
                this.mRoutes.add(r);
            }
            this.mHttpProxy = source.getHttpProxy() == null ? null : new ProxyProperties(source.getHttpProxy());
            for (LinkProperties l2 : source.mStackedLinks.values()) {
                addStackedLink(l2);
            }
            setMtu(source.getMtu());
        }
    }

    public void setInterfaceName(String iface) {
        this.mIfaceName = iface;
        ArrayList<RouteInfo> newRoutes = new ArrayList<>(this.mRoutes.size());
        for (RouteInfo route : this.mRoutes) {
            newRoutes.add(routeWithInterface(route));
        }
        this.mRoutes = newRoutes;
    }

    public String getInterfaceName() {
        return this.mIfaceName;
    }

    public Collection<String> getAllInterfaceNames() {
        Collection interfaceNames = new ArrayList(this.mStackedLinks.size() + 1);
        if (this.mIfaceName != null) {
            interfaceNames.add(new String(this.mIfaceName));
        }
        for (LinkProperties stacked : this.mStackedLinks.values()) {
            interfaceNames.addAll(stacked.getAllInterfaceNames());
        }
        return interfaceNames;
    }

    public Collection<InetAddress> getAddresses() {
        Collection<InetAddress> addresses = new ArrayList<>();
        for (LinkAddress linkAddress : this.mLinkAddresses) {
            addresses.add(linkAddress.getAddress());
        }
        return Collections.unmodifiableCollection(addresses);
    }

    public Collection<InetAddress> getAllAddresses() {
        Collection<InetAddress> addresses = new ArrayList<>();
        for (LinkAddress linkAddress : this.mLinkAddresses) {
            addresses.add(linkAddress.getAddress());
        }
        for (LinkProperties stacked : this.mStackedLinks.values()) {
            addresses.addAll(stacked.getAllAddresses());
        }
        return addresses;
    }

    public boolean addLinkAddress(LinkAddress address) {
        if (address != null && !this.mLinkAddresses.contains(address)) {
            this.mLinkAddresses.add(address);
            return true;
        }
        return false;
    }

    public boolean removeLinkAddress(LinkAddress toRemove) {
        return this.mLinkAddresses.remove(toRemove);
    }

    public Collection<LinkAddress> getLinkAddresses() {
        return Collections.unmodifiableCollection(this.mLinkAddresses);
    }

    public Collection<LinkAddress> getAllLinkAddresses() {
        Collection<LinkAddress> addresses = new ArrayList<>();
        addresses.addAll(this.mLinkAddresses);
        for (LinkProperties stacked : this.mStackedLinks.values()) {
            addresses.addAll(stacked.getAllLinkAddresses());
        }
        return addresses;
    }

    public void setLinkAddresses(Collection<LinkAddress> addresses) {
        this.mLinkAddresses.clear();
        for (LinkAddress address : addresses) {
            addLinkAddress(address);
        }
    }

    public void addDns(InetAddress dns) {
        if (dns != null) {
            this.mDnses.add(dns);
        }
    }

    public Collection<InetAddress> getDnses() {
        return Collections.unmodifiableCollection(this.mDnses);
    }

    public String getDomains() {
        return this.mDomains;
    }

    public void setDomains(String domains) {
        this.mDomains = domains;
    }

    public void setMtu(int mtu) {
        this.mMtu = mtu;
    }

    public int getMtu() {
        return this.mMtu;
    }

    private RouteInfo routeWithInterface(RouteInfo route) {
        return new RouteInfo(route.getDestination(), route.getGateway(), this.mIfaceName);
    }

    public void addRoute(RouteInfo route) {
        if (route != null) {
            String routeIface = route.getInterface();
            if (routeIface != null && !routeIface.equals(this.mIfaceName)) {
                throw new IllegalArgumentException("Route added with non-matching interface: " + routeIface + " vs. " + this.mIfaceName);
            }
            this.mRoutes.add(routeWithInterface(route));
        }
    }

    public Collection<RouteInfo> getRoutes() {
        return Collections.unmodifiableCollection(this.mRoutes);
    }

    public Collection<RouteInfo> getAllRoutes() {
        Collection<RouteInfo> routes = new ArrayList<>();
        routes.addAll(this.mRoutes);
        for (LinkProperties stacked : this.mStackedLinks.values()) {
            routes.addAll(stacked.getAllRoutes());
        }
        return routes;
    }

    public void setHttpProxy(ProxyProperties proxy) {
        this.mHttpProxy = proxy;
    }

    public ProxyProperties getHttpProxy() {
        return this.mHttpProxy;
    }

    public boolean addStackedLink(LinkProperties link) {
        if (link != null && link.getInterfaceName() != null) {
            this.mStackedLinks.put(link.getInterfaceName(), link);
            return true;
        }
        return false;
    }

    public boolean removeStackedLink(LinkProperties link) {
        if (link != null && link.getInterfaceName() != null) {
            LinkProperties removed = this.mStackedLinks.remove(link.getInterfaceName());
            return removed != null;
        }
        return false;
    }

    public Collection<LinkProperties> getStackedLinks() {
        Collection<LinkProperties> stacked = new ArrayList<>();
        for (LinkProperties link : this.mStackedLinks.values()) {
            stacked.add(new LinkProperties(link));
        }
        return Collections.unmodifiableCollection(stacked);
    }

    public void clear() {
        this.mIfaceName = null;
        this.mLinkAddresses.clear();
        this.mDnses.clear();
        this.mDomains = null;
        this.mRoutes.clear();
        this.mHttpProxy = null;
        this.mStackedLinks.clear();
        this.mMtu = 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        String ifaceName = this.mIfaceName == null ? "" : "InterfaceName: " + this.mIfaceName + Separators.SP;
        String linkAddresses = "LinkAddresses: [";
        for (LinkAddress addr : this.mLinkAddresses) {
            linkAddresses = linkAddresses + addr.toString() + Separators.COMMA;
        }
        String linkAddresses2 = linkAddresses + "] ";
        String dns = "DnsAddresses: [";
        for (InetAddress addr2 : this.mDnses) {
            dns = dns + addr2.getHostAddress() + Separators.COMMA;
        }
        String dns2 = dns + "] ";
        String domainName = "Domains: " + this.mDomains;
        String mtu = "MTU: " + this.mMtu;
        String routes = " Routes: [";
        for (RouteInfo route : this.mRoutes) {
            routes = routes + route.toString() + Separators.COMMA;
        }
        String routes2 = routes + "] ";
        String proxy = this.mHttpProxy == null ? "" : "HttpProxy: " + this.mHttpProxy.toString() + Separators.SP;
        String stacked = "";
        if (this.mStackedLinks.values().size() > 0) {
            String stacked2 = stacked + " Stacked: [";
            for (LinkProperties link : this.mStackedLinks.values()) {
                stacked2 = stacked2 + " [" + link.toString() + " ],";
            }
            stacked = stacked2 + "] ";
        }
        return "{" + ifaceName + linkAddresses2 + routes2 + dns2 + domainName + mtu + proxy + stacked + "}";
    }

    public boolean hasIPv4Address() {
        for (LinkAddress address : this.mLinkAddresses) {
            if (address.getAddress() instanceof Inet4Address) {
                return true;
            }
        }
        return false;
    }

    public boolean hasIPv6Address() {
        for (LinkAddress address : this.mLinkAddresses) {
            if (address.getAddress() instanceof Inet6Address) {
                return true;
            }
        }
        return false;
    }

    public boolean isIdenticalInterfaceName(LinkProperties target) {
        return TextUtils.equals(getInterfaceName(), target.getInterfaceName());
    }

    public boolean isIdenticalAddresses(LinkProperties target) {
        Collection<InetAddress> targetAddresses = target.getAddresses();
        Collection<InetAddress> sourceAddresses = getAddresses();
        if (sourceAddresses.size() == targetAddresses.size()) {
            return sourceAddresses.containsAll(targetAddresses);
        }
        return false;
    }

    public boolean isIdenticalDnses(LinkProperties target) {
        Collection<InetAddress> targetDnses = target.getDnses();
        String targetDomains = target.getDomains();
        if (this.mDomains == null) {
            if (targetDomains != null) {
                return false;
            }
        } else if (!this.mDomains.equals(targetDomains)) {
            return false;
        }
        if (this.mDnses.size() == targetDnses.size()) {
            return this.mDnses.containsAll(targetDnses);
        }
        return false;
    }

    public boolean isIdenticalRoutes(LinkProperties target) {
        Collection<RouteInfo> targetRoutes = target.getRoutes();
        if (this.mRoutes.size() == targetRoutes.size()) {
            return this.mRoutes.containsAll(targetRoutes);
        }
        return false;
    }

    public boolean isIdenticalHttpProxy(LinkProperties target) {
        return getHttpProxy() == null ? target.getHttpProxy() == null : getHttpProxy().equals(target.getHttpProxy());
    }

    public boolean isIdenticalStackedLinks(LinkProperties target) {
        if (!this.mStackedLinks.keySet().equals(target.mStackedLinks.keySet())) {
            return false;
        }
        for (LinkProperties stacked : this.mStackedLinks.values()) {
            String iface = stacked.getInterfaceName();
            if (!stacked.equals(target.mStackedLinks.get(iface))) {
                return false;
            }
        }
        return true;
    }

    public boolean isIdenticalMtu(LinkProperties target) {
        return getMtu() == target.getMtu();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LinkProperties) {
            LinkProperties target = (LinkProperties) obj;
            return isIdenticalInterfaceName(target) && isIdenticalAddresses(target) && isIdenticalDnses(target) && isIdenticalRoutes(target) && isIdenticalHttpProxy(target) && isIdenticalStackedLinks(target) && isIdenticalMtu(target);
        }
        return false;
    }

    public CompareResult<LinkAddress> compareAddresses(LinkProperties target) {
        CompareResult<LinkAddress> result = new CompareResult<>();
        result.removed = new ArrayList(this.mLinkAddresses);
        result.added.clear();
        if (target != null) {
            for (LinkAddress newAddress : target.getLinkAddresses()) {
                if (!result.removed.remove(newAddress)) {
                    result.added.add(newAddress);
                }
            }
        }
        return result;
    }

    public CompareResult<InetAddress> compareDnses(LinkProperties target) {
        CompareResult<InetAddress> result = new CompareResult<>();
        result.removed = new ArrayList(this.mDnses);
        result.added.clear();
        if (target != null) {
            for (InetAddress newAddress : target.getDnses()) {
                if (!result.removed.remove(newAddress)) {
                    result.added.add(newAddress);
                }
            }
        }
        return result;
    }

    public CompareResult<RouteInfo> compareAllRoutes(LinkProperties target) {
        CompareResult<RouteInfo> result = new CompareResult<>();
        result.removed = getAllRoutes();
        result.added.clear();
        if (target != null) {
            for (RouteInfo r : target.getAllRoutes()) {
                if (!result.removed.remove(r)) {
                    result.added.add(r);
                }
            }
        }
        return result;
    }

    public int hashCode() {
        int hashCode;
        if (null == this.mIfaceName) {
            hashCode = 0;
        } else {
            hashCode = this.mIfaceName.hashCode() + (this.mLinkAddresses.size() * 31) + (this.mDnses.size() * 37) + (null == this.mDomains ? 0 : this.mDomains.hashCode()) + (this.mRoutes.size() * 41) + (null == this.mHttpProxy ? 0 : this.mHttpProxy.hashCode()) + (this.mStackedLinks.hashCode() * 47);
        }
        return hashCode + (this.mMtu * 51);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getInterfaceName());
        dest.writeInt(this.mLinkAddresses.size());
        for (LinkAddress linkAddress : this.mLinkAddresses) {
            dest.writeParcelable(linkAddress, flags);
        }
        dest.writeInt(this.mDnses.size());
        for (InetAddress d : this.mDnses) {
            dest.writeByteArray(d.getAddress());
        }
        dest.writeString(this.mDomains);
        dest.writeInt(this.mMtu);
        dest.writeInt(this.mRoutes.size());
        for (RouteInfo route : this.mRoutes) {
            dest.writeParcelable(route, flags);
        }
        if (this.mHttpProxy != null) {
            dest.writeByte((byte) 1);
            dest.writeParcelable(this.mHttpProxy, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        ArrayList<LinkProperties> stackedLinks = new ArrayList<>(this.mStackedLinks.values());
        dest.writeList(stackedLinks);
    }
}