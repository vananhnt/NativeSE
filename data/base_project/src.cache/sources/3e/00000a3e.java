package android.net.wifi.p2p.nsd;

import gov.nist.core.Separators;

/* loaded from: WifiP2pDnsSdServiceRequest.class */
public class WifiP2pDnsSdServiceRequest extends WifiP2pServiceRequest {
    private WifiP2pDnsSdServiceRequest(String query) {
        super(1, query);
    }

    private WifiP2pDnsSdServiceRequest() {
        super(1, null);
    }

    private WifiP2pDnsSdServiceRequest(String dnsQuery, int dnsType, int version) {
        super(1, WifiP2pDnsSdServiceInfo.createRequest(dnsQuery, dnsType, version));
    }

    public static WifiP2pDnsSdServiceRequest newInstance() {
        return new WifiP2pDnsSdServiceRequest();
    }

    public static WifiP2pDnsSdServiceRequest newInstance(String serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("service type cannot be null");
        }
        return new WifiP2pDnsSdServiceRequest(serviceType + ".local.", 12, 1);
    }

    public static WifiP2pDnsSdServiceRequest newInstance(String instanceName, String serviceType) {
        if (instanceName == null || serviceType == null) {
            throw new IllegalArgumentException("instance name or service type cannot be null");
        }
        String fullDomainName = instanceName + Separators.DOT + serviceType + ".local.";
        return new WifiP2pDnsSdServiceRequest(fullDomainName, 16, 1);
    }
}