package android.content.pm;

import java.util.HashSet;

/* loaded from: PackageUserState.class */
public class PackageUserState {
    public boolean stopped;
    public boolean notLaunched;
    public boolean installed;
    public boolean blocked;
    public int enabled;
    public String lastDisableAppCaller;
    public HashSet<String> disabledComponents;
    public HashSet<String> enabledComponents;

    public PackageUserState() {
        this.installed = true;
        this.blocked = false;
        this.enabled = 0;
    }

    public PackageUserState(PackageUserState o) {
        this.installed = o.installed;
        this.stopped = o.stopped;
        this.notLaunched = o.notLaunched;
        this.enabled = o.enabled;
        this.blocked = o.blocked;
        this.lastDisableAppCaller = o.lastDisableAppCaller;
        this.disabledComponents = o.disabledComponents != null ? new HashSet<>(o.disabledComponents) : null;
        this.enabledComponents = o.enabledComponents != null ? new HashSet<>(o.enabledComponents) : null;
    }
}