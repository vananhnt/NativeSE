package android.net;

import android.util.Log;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Locale;

/* loaded from: NetworkUtils.class */
public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    public static final int RESET_IPV4_ADDRESSES = 1;
    public static final int RESET_IPV6_ADDRESSES = 2;
    public static final int RESET_ALL_ADDRESSES = 3;

    public static native int enableInterface(String str);

    public static native int disableInterface(String str);

    public static native int resetConnections(String str, int i);

    public static native boolean runDhcp(String str, DhcpResults dhcpResults);

    public static native boolean runDhcpRenew(String str, DhcpResults dhcpResults);

    public static native boolean stopDhcp(String str);

    public static native boolean releaseDhcpLease(String str);

    public static native String getDhcpError();

    public static native void markSocket(int i, int i2);

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (255 & hostAddress), (byte) (255 & (hostAddress >> 8)), (byte) (255 & (hostAddress >> 16)), (byte) (255 & (hostAddress >> 24))};
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    public static int inetAddressToInt(Inet4Address inetAddr) throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        return ((addr[3] & 255) << 24) | ((addr[2] & 255) << 16) | ((addr[1] & 255) << 8) | (addr[0] & 255);
    }

    public static int prefixLengthToNetmaskInt(int prefixLength) throws IllegalArgumentException {
        if (prefixLength < 0 || prefixLength > 32) {
            throw new IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)");
        }
        int value = (-1) << (32 - prefixLength);
        return Integer.reverseBytes(value);
    }

    public static int netmaskIntToPrefixLength(int netmask) {
        return Integer.bitCount(netmask);
    }

    public static InetAddress numericToInetAddress(String addrString) throws IllegalArgumentException {
        return InetAddress.parseNumericAddress(addrString);
    }

    public static InetAddress getNetworkPart(InetAddress address, int prefixLength) {
        if (address == null) {
            throw new RuntimeException("getNetworkPart doesn't accept null address");
        }
        byte[] array = address.getAddress();
        if (prefixLength < 0 || prefixLength > array.length * 8) {
            throw new RuntimeException("getNetworkPart - bad prefixLength");
        }
        int offset = prefixLength / 8;
        int reminder = prefixLength % 8;
        byte mask = (byte) (255 << (8 - reminder));
        if (offset < array.length) {
            array[offset] = (byte) (array[offset] & mask);
        }
        while (true) {
            offset++;
            if (offset < array.length) {
                array[offset] = 0;
            } else {
                try {
                    InetAddress netPart = InetAddress.getByAddress(array);
                    return netPart;
                } catch (UnknownHostException e) {
                    throw new RuntimeException("getNetworkPart error - " + e.toString());
                }
            }
        }
    }

    public static boolean addressTypeMatches(InetAddress left, InetAddress right) {
        return ((left instanceof Inet4Address) && (right instanceof Inet4Address)) || ((left instanceof Inet6Address) && (right instanceof Inet6Address));
    }

    public static InetAddress hexToInet6Address(String addrHexString) throws IllegalArgumentException {
        try {
            return numericToInetAddress(String.format(Locale.US, "%s:%s:%s:%s:%s:%s:%s:%s", addrHexString.substring(0, 4), addrHexString.substring(4, 8), addrHexString.substring(8, 12), addrHexString.substring(12, 16), addrHexString.substring(16, 20), addrHexString.substring(20, 24), addrHexString.substring(24, 28), addrHexString.substring(28, 32)));
        } catch (Exception e) {
            Log.e(TAG, "error in hexToInet6Address(" + addrHexString + "): " + e);
            throw new IllegalArgumentException(e);
        }
    }

    public static String[] makeStrings(Collection<InetAddress> addrs) {
        String[] result = new String[addrs.size()];
        int i = 0;
        for (InetAddress addr : addrs) {
            int i2 = i;
            i++;
            result[i2] = addr.getHostAddress();
        }
        return result;
    }

    public static String trimV4AddrZeros(String addr) {
        if (addr == null) {
            return null;
        }
        String[] octets = addr.split("\\.");
        if (octets.length != 4) {
            return addr;
        }
        StringBuilder builder = new StringBuilder(16);
        for (int i = 0; i < 4; i++) {
            try {
                if (octets[i].length() > 3) {
                    return addr;
                }
                builder.append(Integer.parseInt(octets[i]));
                if (i < 3) {
                    builder.append('.');
                }
            } catch (NumberFormatException e) {
                return addr;
            }
        }
        String result = builder.toString();
        return result;
    }
}