package android.net.wifi;

/* loaded from: StateChangeResult.class */
public class StateChangeResult {
    int networkId;
    WifiSsid wifiSsid;
    String BSSID;
    SupplicantState state;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StateChangeResult(int networkId, WifiSsid wifiSsid, String BSSID, SupplicantState state) {
        this.state = state;
        this.wifiSsid = wifiSsid;
        this.BSSID = BSSID;
        this.networkId = networkId;
    }
}