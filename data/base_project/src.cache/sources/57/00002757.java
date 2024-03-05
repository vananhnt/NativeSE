package java.util.prefs;

import java.io.Serializable;
import java.util.EventObject;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PreferenceChangeEvent.class */
public class PreferenceChangeEvent extends EventObject implements Serializable {
    public PreferenceChangeEvent(Preferences p, String k, String v) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public String getKey() {
        throw new RuntimeException("Stub!");
    }

    public String getNewValue() {
        throw new RuntimeException("Stub!");
    }

    public Preferences getNode() {
        throw new RuntimeException("Stub!");
    }
}