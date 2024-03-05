package java.util.prefs;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PreferenceChangeListener.class */
public interface PreferenceChangeListener extends EventListener {
    void preferenceChange(PreferenceChangeEvent preferenceChangeEvent);
}