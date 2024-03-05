package android.support.v4.view.accessibility;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompatJellyBean;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: AccessibilityNodeProviderCompat.class */
public class AccessibilityNodeProviderCompat {
    private static final AccessibilityNodeProviderImpl IMPL;
    private final Object mProvider;

    /* loaded from: AccessibilityNodeProviderCompat$AccessibilityNodeProviderImpl.class */
    interface AccessibilityNodeProviderImpl {
        Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat accessibilityNodeProviderCompat);
    }

    /* loaded from: AccessibilityNodeProviderCompat$AccessibilityNodeProviderJellyBeanImpl.class */
    static class AccessibilityNodeProviderJellyBeanImpl extends AccessibilityNodeProviderStubImpl {
        AccessibilityNodeProviderJellyBeanImpl() {
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderStubImpl, android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderImpl
        public Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat accessibilityNodeProviderCompat) {
            return AccessibilityNodeProviderCompatJellyBean.newAccessibilityNodeProviderBridge(new AccessibilityNodeProviderCompatJellyBean.AccessibilityNodeInfoBridge(this, accessibilityNodeProviderCompat) { // from class: android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderJellyBeanImpl.1
                final AccessibilityNodeProviderJellyBeanImpl this$0;
                final AccessibilityNodeProviderCompat val$compat;

                {
                    this.this$0 = this;
                    this.val$compat = accessibilityNodeProviderCompat;
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatJellyBean.AccessibilityNodeInfoBridge
                public Object createAccessibilityNodeInfo(int i) {
                    AccessibilityNodeInfoCompat createAccessibilityNodeInfo = this.val$compat.createAccessibilityNodeInfo(i);
                    if (createAccessibilityNodeInfo == null) {
                        return null;
                    }
                    return createAccessibilityNodeInfo.getInfo();
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatJellyBean.AccessibilityNodeInfoBridge
                public List<Object> findAccessibilityNodeInfosByText(String str, int i) {
                    List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText = this.val$compat.findAccessibilityNodeInfosByText(str, i);
                    ArrayList arrayList = new ArrayList();
                    int size = findAccessibilityNodeInfosByText.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        arrayList.add(findAccessibilityNodeInfosByText.get(i2).getInfo());
                    }
                    return arrayList;
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatJellyBean.AccessibilityNodeInfoBridge
                public boolean performAction(int i, int i2, Bundle bundle) {
                    return this.val$compat.performAction(i, i2, bundle);
                }
            });
        }
    }

    /* loaded from: AccessibilityNodeProviderCompat$AccessibilityNodeProviderKitKatImpl.class */
    static class AccessibilityNodeProviderKitKatImpl extends AccessibilityNodeProviderStubImpl {
        AccessibilityNodeProviderKitKatImpl() {
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderStubImpl, android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderImpl
        public Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat accessibilityNodeProviderCompat) {
            return AccessibilityNodeProviderCompatKitKat.newAccessibilityNodeProviderBridge(new AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge(this, accessibilityNodeProviderCompat) { // from class: android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderKitKatImpl.1
                final AccessibilityNodeProviderKitKatImpl this$0;
                final AccessibilityNodeProviderCompat val$compat;

                {
                    this.this$0 = this;
                    this.val$compat = accessibilityNodeProviderCompat;
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge
                public Object createAccessibilityNodeInfo(int i) {
                    AccessibilityNodeInfoCompat createAccessibilityNodeInfo = this.val$compat.createAccessibilityNodeInfo(i);
                    if (createAccessibilityNodeInfo == null) {
                        return null;
                    }
                    return createAccessibilityNodeInfo.getInfo();
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge
                public List<Object> findAccessibilityNodeInfosByText(String str, int i) {
                    List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText = this.val$compat.findAccessibilityNodeInfosByText(str, i);
                    ArrayList arrayList = new ArrayList();
                    int size = findAccessibilityNodeInfosByText.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        arrayList.add(findAccessibilityNodeInfosByText.get(i2).getInfo());
                    }
                    return arrayList;
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge
                public Object findFocus(int i) {
                    AccessibilityNodeInfoCompat findFocus = this.val$compat.findFocus(i);
                    if (findFocus == null) {
                        return null;
                    }
                    return findFocus.getInfo();
                }

                @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge
                public boolean performAction(int i, int i2, Bundle bundle) {
                    return this.val$compat.performAction(i, i2, bundle);
                }
            });
        }
    }

    /* loaded from: AccessibilityNodeProviderCompat$AccessibilityNodeProviderStubImpl.class */
    static class AccessibilityNodeProviderStubImpl implements AccessibilityNodeProviderImpl {
        AccessibilityNodeProviderStubImpl() {
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderImpl
        public Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat accessibilityNodeProviderCompat) {
            return null;
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new AccessibilityNodeProviderKitKatImpl();
        } else if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityNodeProviderJellyBeanImpl();
        } else {
            IMPL = new AccessibilityNodeProviderStubImpl();
        }
    }

    public AccessibilityNodeProviderCompat() {
        this.mProvider = IMPL.newAccessibilityNodeProviderBridge(this);
    }

    public AccessibilityNodeProviderCompat(Object obj) {
        this.mProvider = obj;
    }

    public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int i) {
        return null;
    }

    public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(String str, int i) {
        return null;
    }

    public AccessibilityNodeInfoCompat findFocus(int i) {
        return null;
    }

    public Object getProvider() {
        return this.mProvider;
    }

    public boolean performAction(int i, int i2, Bundle bundle) {
        return false;
    }
}