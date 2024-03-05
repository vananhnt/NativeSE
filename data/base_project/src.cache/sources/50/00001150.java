package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

/* loaded from: AccessibilityNodeInfoCompatApi21.class */
class AccessibilityNodeInfoCompatApi21 {

    /* loaded from: AccessibilityNodeInfoCompatApi21$AccessibilityAction.class */
    static class AccessibilityAction {
        AccessibilityAction() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getId(Object obj) {
            return ((AccessibilityNodeInfo.AccessibilityAction) obj).getId();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static CharSequence getLabel(Object obj) {
            return ((AccessibilityNodeInfo.AccessibilityAction) obj).getLabel();
        }
    }

    /* loaded from: AccessibilityNodeInfoCompatApi21$CollectionItemInfo.class */
    static class CollectionItemInfo {
        CollectionItemInfo() {
        }

        public static boolean isSelected(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).isSelected();
        }
    }

    AccessibilityNodeInfoCompatApi21() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addAction(Object obj, int i, CharSequence charSequence) {
        ((AccessibilityNodeInfo) obj).addAction(new AccessibilityNodeInfo.AccessibilityAction(i, charSequence));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<Object> getActionList(Object obj) {
        return ((AccessibilityNodeInfo) obj).getActionList();
    }

    public static Object obtainCollectionInfo(int i, int i2, boolean z, int i3) {
        return AccessibilityNodeInfo.CollectionInfo.obtain(i, i2, z, i3);
    }

    public static Object obtainCollectionItemInfo(int i, int i2, int i3, int i4, boolean z, boolean z2) {
        return AccessibilityNodeInfo.CollectionItemInfo.obtain(i, i2, i3, i4, z, z2);
    }
}