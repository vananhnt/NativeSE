package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: AccessibilityNodeInfoCompatKitKat.class */
class AccessibilityNodeInfoCompatKitKat {

    /* loaded from: AccessibilityNodeInfoCompatKitKat$CollectionInfo.class */
    static class CollectionInfo {
        CollectionInfo() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getColumnCount(Object obj) {
            return ((AccessibilityNodeInfo.CollectionInfo) obj).getColumnCount();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getRowCount(Object obj) {
            return ((AccessibilityNodeInfo.CollectionInfo) obj).getRowCount();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static boolean isHierarchical(Object obj) {
            return ((AccessibilityNodeInfo.CollectionInfo) obj).isHierarchical();
        }
    }

    /* loaded from: AccessibilityNodeInfoCompatKitKat$CollectionItemInfo.class */
    static class CollectionItemInfo {
        CollectionItemInfo() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getColumnIndex(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).getColumnIndex();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getColumnSpan(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).getColumnSpan();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getRowIndex(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).getRowIndex();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getRowSpan(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).getRowSpan();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static boolean isHeading(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).isHeading();
        }
    }

    /* loaded from: AccessibilityNodeInfoCompatKitKat$RangeInfo.class */
    static class RangeInfo {
        RangeInfo() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static float getCurrent(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getCurrent();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static float getMax(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getMax();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static float getMin(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getMin();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getType(Object obj) {
            return ((AccessibilityNodeInfo.RangeInfo) obj).getType();
        }
    }

    AccessibilityNodeInfoCompatKitKat() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object getCollectionInfo(Object obj) {
        return ((AccessibilityNodeInfo) obj).getCollectionInfo();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object getCollectionItemInfo(Object obj) {
        return ((AccessibilityNodeInfo) obj).getCollectionItemInfo();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getLiveRegion(Object obj) {
        return ((AccessibilityNodeInfo) obj).getLiveRegion();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object getRangeInfo(Object obj) {
        return ((AccessibilityNodeInfo) obj).getRangeInfo();
    }

    public static Object obtainCollectionInfo(int i, int i2, boolean z, int i3) {
        return AccessibilityNodeInfo.CollectionInfo.obtain(i, i2, z);
    }

    public static Object obtainCollectionItemInfo(int i, int i2, int i3, int i4, boolean z) {
        return AccessibilityNodeInfo.CollectionItemInfo.obtain(i, i2, i3, i4, z);
    }

    public static void setCollectionInfo(Object obj, Object obj2) {
        ((AccessibilityNodeInfo) obj).setCollectionInfo((AccessibilityNodeInfo.CollectionInfo) obj2);
    }

    public static void setCollectionItemInfo(Object obj, Object obj2) {
        ((AccessibilityNodeInfo) obj).setCollectionItemInfo((AccessibilityNodeInfo.CollectionItemInfo) obj2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setLiveRegion(Object obj, int i) {
        ((AccessibilityNodeInfo) obj).setLiveRegion(i);
    }
}