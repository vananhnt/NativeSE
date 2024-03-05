package android.net.wifi.p2p.nsd;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.nsd.WifiP2pServiceResponse;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;

/* loaded from: WifiP2pUpnpServiceResponse.class */
public class WifiP2pUpnpServiceResponse extends WifiP2pServiceResponse {
    private int mVersion;
    private List<String> mUniqueServiceNames;

    public int getVersion() {
        return this.mVersion;
    }

    public List<String> getUniqueServiceNames() {
        return this.mUniqueServiceNames;
    }

    protected WifiP2pUpnpServiceResponse(int status, int transId, WifiP2pDevice dev, byte[] data) {
        super(2, status, transId, dev, data);
        if (!parse()) {
            throw new IllegalArgumentException("Malformed upnp service response");
        }
    }

    private boolean parse() {
        if (this.mData == null) {
            return true;
        }
        if (this.mData.length < 1) {
            return false;
        }
        this.mVersion = this.mData[0] & 255;
        String[] names = new String(this.mData, 1, this.mData.length - 1).split(Separators.COMMA);
        this.mUniqueServiceNames = new ArrayList();
        for (String name : names) {
            this.mUniqueServiceNames.add(name);
        }
        return true;
    }

    @Override // android.net.wifi.p2p.nsd.WifiP2pServiceResponse
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("serviceType:UPnP(").append(this.mServiceType).append(Separators.RPAREN);
        sbuf.append(" status:").append(WifiP2pServiceResponse.Status.toString(this.mStatus));
        sbuf.append(" srcAddr:").append(this.mDevice.deviceAddress);
        sbuf.append(" version:").append(String.format("%02x", Integer.valueOf(this.mVersion)));
        if (this.mUniqueServiceNames != null) {
            for (String name : this.mUniqueServiceNames) {
                sbuf.append(" usn:").append(name);
            }
        }
        return sbuf.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WifiP2pUpnpServiceResponse newInstance(int status, int transId, WifiP2pDevice device, byte[] data) {
        if (status != 0) {
            return new WifiP2pUpnpServiceResponse(status, transId, device, null);
        }
        try {
            return new WifiP2pUpnpServiceResponse(status, transId, device, data);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}