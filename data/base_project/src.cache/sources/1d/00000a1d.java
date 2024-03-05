package android.net.wifi.p2p;

import gov.nist.core.Separators;

/* loaded from: WifiP2pProvDiscEvent.class */
public class WifiP2pProvDiscEvent {
    private static final String TAG = "WifiP2pProvDiscEvent";
    public static final int PBC_REQ = 1;
    public static final int PBC_RSP = 2;
    public static final int ENTER_PIN = 3;
    public static final int SHOW_PIN = 4;
    public int event;
    public WifiP2pDevice device;
    public String pin;

    public WifiP2pProvDiscEvent() {
        this.device = new WifiP2pDevice();
    }

    public WifiP2pProvDiscEvent(String string) throws IllegalArgumentException {
        String[] tokens = string.split(Separators.SP);
        if (tokens.length < 2) {
            throw new IllegalArgumentException("Malformed event " + string);
        }
        if (tokens[0].endsWith("PBC-REQ")) {
            this.event = 1;
        } else if (tokens[0].endsWith("PBC-RESP")) {
            this.event = 2;
        } else if (tokens[0].endsWith("ENTER-PIN")) {
            this.event = 3;
        } else if (!tokens[0].endsWith("SHOW-PIN")) {
            throw new IllegalArgumentException("Malformed event " + string);
        } else {
            this.event = 4;
        }
        this.device = new WifiP2pDevice();
        this.device.deviceAddress = tokens[1];
        if (this.event == 4) {
            this.pin = tokens[2];
        }
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(this.device);
        sbuf.append("\n event: ").append(this.event);
        sbuf.append("\n pin: ").append(this.pin);
        return sbuf.toString();
    }
}