package com.android.server.am;

import android.content.ComponentName;
import java.util.ArrayList;

/* loaded from: ActivityManagerService$ItemMatcher.class */
class ActivityManagerService$ItemMatcher {
    ArrayList<ComponentName> components;
    ArrayList<String> strings;
    ArrayList<Integer> objects;
    boolean all = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void build(String name) {
        ComponentName componentName = ComponentName.unflattenFromString(name);
        if (componentName != null) {
            if (this.components == null) {
                this.components = new ArrayList<>();
            }
            this.components.add(componentName);
            this.all = false;
            return;
        }
        try {
            int objectId = Integer.parseInt(name, 16);
            if (this.objects == null) {
                this.objects = new ArrayList<>();
            }
            this.objects.add(Integer.valueOf(objectId));
            this.all = false;
        } catch (RuntimeException e) {
            if (this.strings == null) {
                this.strings = new ArrayList<>();
            }
            this.strings.add(name);
            this.all = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int build(String[] args, int opti) {
        while (opti < args.length) {
            String name = args[opti];
            if ("--".equals(name)) {
                return opti + 1;
            }
            build(name);
            opti++;
        }
        return opti;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean match(Object object, ComponentName comp) {
        if (this.all) {
            return true;
        }
        if (this.components != null) {
            for (int i = 0; i < this.components.size(); i++) {
                if (this.components.get(i).equals(comp)) {
                    return true;
                }
            }
        }
        if (this.objects != null) {
            for (int i2 = 0; i2 < this.objects.size(); i2++) {
                if (System.identityHashCode(object) == this.objects.get(i2).intValue()) {
                    return true;
                }
            }
        }
        if (this.strings != null) {
            String flat = comp.flattenToString();
            for (int i3 = 0; i3 < this.strings.size(); i3++) {
                if (flat.contains(this.strings.get(i3))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}