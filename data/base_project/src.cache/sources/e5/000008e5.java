package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

/* loaded from: LinkAddress.class */
public class LinkAddress implements Parcelable {
    private InetAddress address;
    private int prefixLength;
    public static final Parcelable.Creator<LinkAddress> CREATOR = new Parcelable.Creator<LinkAddress>() { // from class: android.net.LinkAddress.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkAddress createFromParcel(Parcel in) {
            InetAddress address = null;
            int prefixLength = 0;
            if (in.readByte() == 1) {
                try {
                    address = InetAddress.getByAddress(in.createByteArray());
                    prefixLength = in.readInt();
                } catch (UnknownHostException e) {
                }
            }
            return new LinkAddress(address, prefixLength);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkAddress[] newArray(int size) {
            return new LinkAddress[size];
        }
    };

    private void init(InetAddress address, int prefixLength) {
        if (address == null || prefixLength < 0 || (((address instanceof Inet4Address) && prefixLength > 32) || prefixLength > 128)) {
            throw new IllegalArgumentException("Bad LinkAddress params " + address + Separators.SLASH + prefixLength);
        }
        this.address = address;
        this.prefixLength = prefixLength;
    }

    public LinkAddress(InetAddress address, int prefixLength) {
        init(address, prefixLength);
    }

    public LinkAddress(InterfaceAddress interfaceAddress) {
        init(interfaceAddress.getAddress(), interfaceAddress.getNetworkPrefixLength());
    }

    public LinkAddress(String address) {
        InetAddress inetAddress = null;
        int prefixLength = -1;
        try {
            String[] pieces = address.split(Separators.SLASH, 2);
            prefixLength = Integer.parseInt(pieces[1]);
            inetAddress = InetAddress.parseNumericAddress(pieces[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (NumberFormatException e2) {
        } catch (IllegalArgumentException e3) {
        } catch (NullPointerException e4) {
        }
        if (inetAddress == null || prefixLength == -1) {
            throw new IllegalArgumentException("Bad LinkAddress params " + address);
        }
        init(inetAddress, prefixLength);
    }

    public String toString() {
        return this.address == null ? "" : this.address.getHostAddress() + Separators.SLASH + this.prefixLength;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LinkAddress)) {
            return false;
        }
        LinkAddress linkAddress = (LinkAddress) obj;
        return this.address.equals(linkAddress.address) && this.prefixLength == linkAddress.prefixLength;
    }

    public int hashCode() {
        return (null == this.address ? 0 : this.address.hashCode()) + this.prefixLength;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getNetworkPrefixLength() {
        return this.prefixLength;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.address != null) {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.address.getAddress());
            dest.writeInt(this.prefixLength);
            return;
        }
        dest.writeByte((byte) 0);
    }
}