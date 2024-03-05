package android.view.accessibility;

import android.os.Bundle;
import java.util.List;

/* loaded from: AccessibilityNodeProvider.class */
public abstract class AccessibilityNodeProvider {
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
        return null;
    }

    public boolean performAction(int virtualViewId, int action, Bundle arguments) {
        return false;
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
        return null;
    }

    public AccessibilityNodeInfo findFocus(int focus) {
        return null;
    }
}