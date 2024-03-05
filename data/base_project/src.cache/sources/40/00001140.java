package android.support.v4.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;
import java.util.List;

/* loaded from: AccessibilityManagerCompatIcs.class */
class AccessibilityManagerCompatIcs {

    /* loaded from: AccessibilityManagerCompatIcs$AccessibilityStateChangeListenerBridge.class */
    interface AccessibilityStateChangeListenerBridge {
        void onAccessibilityStateChanged(boolean z);
    }

    AccessibilityManagerCompatIcs() {
    }

    public static boolean addAccessibilityStateChangeListener(AccessibilityManager accessibilityManager, Object obj) {
        return accessibilityManager.addAccessibilityStateChangeListener((AccessibilityManager.AccessibilityStateChangeListener) obj);
    }

    public static List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(AccessibilityManager accessibilityManager, int i) {
        return accessibilityManager.getEnabledAccessibilityServiceList(i);
    }

    public static List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(AccessibilityManager accessibilityManager) {
        return accessibilityManager.getInstalledAccessibilityServiceList();
    }

    public static boolean isTouchExplorationEnabled(AccessibilityManager accessibilityManager) {
        return accessibilityManager.isTouchExplorationEnabled();
    }

    public static Object newAccessibilityStateChangeListener(AccessibilityStateChangeListenerBridge accessibilityStateChangeListenerBridge) {
        return new AccessibilityManager.AccessibilityStateChangeListener(accessibilityStateChangeListenerBridge) { // from class: android.support.v4.view.accessibility.AccessibilityManagerCompatIcs.1
            final AccessibilityStateChangeListenerBridge val$bridge;

            {
                this.val$bridge = accessibilityStateChangeListenerBridge;
            }

            @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
            public void onAccessibilityStateChanged(boolean z) {
                this.val$bridge.onAccessibilityStateChanged(z);
            }
        };
    }

    public static boolean removeAccessibilityStateChangeListener(AccessibilityManager accessibilityManager, Object obj) {
        return accessibilityManager.removeAccessibilityStateChangeListener((AccessibilityManager.AccessibilityStateChangeListener) obj);
    }
}