package android.net;

import gov.nist.core.Separators;
import java.util.Locale;

/* loaded from: NetworkConfig.class */
public class NetworkConfig {
    public String name;
    public int type;
    public int radio;
    public int priority;
    public boolean dependencyMet;
    public int restoreTime;

    public NetworkConfig(String init) {
        String[] fragments = init.split(Separators.COMMA);
        this.name = fragments[0].trim().toLowerCase(Locale.ROOT);
        this.type = Integer.parseInt(fragments[1]);
        this.radio = Integer.parseInt(fragments[2]);
        this.priority = Integer.parseInt(fragments[3]);
        this.restoreTime = Integer.parseInt(fragments[4]);
        this.dependencyMet = Boolean.parseBoolean(fragments[5]);
    }

    public boolean isDefault() {
        return this.type == this.radio;
    }
}