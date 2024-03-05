package android.view.accessibility;

import android.os.Build;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseLongArray;
import gov.nist.core.Separators;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: AccessibilityNodeInfoCache.class */
public class AccessibilityNodeInfoCache {
    private static final String LOG_TAG = AccessibilityNodeInfoCache.class.getSimpleName();
    private static final boolean ENABLED = true;
    private static final boolean DEBUG = false;
    private static final boolean CHECK_INTEGRITY_IF_DEBUGGABLE_BUILD = true;
    private final Object mLock = new Object();
    private final LongSparseArray<AccessibilityNodeInfo> mCacheImpl = new LongSparseArray<>();
    private int mWindowId;

    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case 4:
            case 8:
            case 16:
            case 8192:
            case 32768:
            case 65536:
                refreshCachedNode(event.getSourceNodeId());
                break;
            case 32:
            case 128:
            case 256:
            case 1024:
                int windowId = event.getWindowId();
                if (this.mWindowId != windowId) {
                    this.mWindowId = windowId;
                    clear();
                    break;
                }
                break;
            case 2048:
                synchronized (this.mLock) {
                    long sourceId = event.getSourceNodeId();
                    if ((event.getContentChangeTypes() & 1) != 0) {
                        clearSubTreeLocked(sourceId);
                    } else {
                        refreshCachedNode(sourceId);
                    }
                }
                break;
            case 4096:
                synchronized (this.mLock) {
                    clearSubTreeLocked(event.getSourceNodeId());
                }
                break;
        }
        if (Build.IS_DEBUGGABLE) {
            checkIntegrity();
        }
    }

    private void refreshCachedNode(long sourceId) {
        synchronized (this.mLock) {
            AccessibilityNodeInfo cachedInfo = this.mCacheImpl.get(sourceId);
            if (cachedInfo == null) {
                return;
            }
            if (cachedInfo.refresh(true)) {
                return;
            }
            clearSubTreeLocked(sourceId);
        }
    }

    public AccessibilityNodeInfo get(long accessibilityNodeId) {
        AccessibilityNodeInfo accessibilityNodeInfo;
        synchronized (this.mLock) {
            AccessibilityNodeInfo info = this.mCacheImpl.get(accessibilityNodeId);
            if (info != null) {
                info = AccessibilityNodeInfo.obtain(info);
            }
            accessibilityNodeInfo = info;
        }
        return accessibilityNodeInfo;
    }

    public void add(AccessibilityNodeInfo info) {
        synchronized (this.mLock) {
            long sourceId = info.getSourceNodeId();
            AccessibilityNodeInfo oldInfo = this.mCacheImpl.get(sourceId);
            if (oldInfo != null) {
                SparseLongArray oldChildrenIds = oldInfo.getChildNodeIds();
                SparseLongArray newChildrenIds = info.getChildNodeIds();
                int oldChildCount = oldChildrenIds.size();
                for (int i = 0; i < oldChildCount; i++) {
                    long oldChildId = oldChildrenIds.valueAt(i);
                    if (newChildrenIds.indexOfValue(oldChildId) < 0) {
                        clearSubTreeLocked(oldChildId);
                    }
                }
                long oldParentId = oldInfo.getParentNodeId();
                if (info.getParentNodeId() != oldParentId) {
                    clearSubTreeLocked(oldParentId);
                }
            }
            AccessibilityNodeInfo clone = AccessibilityNodeInfo.obtain(info);
            this.mCacheImpl.put(sourceId, clone);
        }
    }

    public void clear() {
        synchronized (this.mLock) {
            int nodeCount = this.mCacheImpl.size();
            for (int i = 0; i < nodeCount; i++) {
                AccessibilityNodeInfo info = this.mCacheImpl.valueAt(i);
                info.recycle();
            }
            this.mCacheImpl.clear();
        }
    }

    private void clearSubTreeLocked(long rootNodeId) {
        clearSubTreeRecursiveLocked(rootNodeId);
    }

    private void clearSubTreeRecursiveLocked(long rootNodeId) {
        AccessibilityNodeInfo current = this.mCacheImpl.get(rootNodeId);
        if (current == null) {
            return;
        }
        this.mCacheImpl.remove(rootNodeId);
        SparseLongArray childNodeIds = current.getChildNodeIds();
        int childCount = childNodeIds.size();
        for (int i = 0; i < childCount; i++) {
            long childNodeId = childNodeIds.valueAt(i);
            clearSubTreeRecursiveLocked(childNodeId);
        }
    }

    private void checkIntegrity() {
        synchronized (this.mLock) {
            if (this.mCacheImpl.size() <= 0) {
                return;
            }
            AccessibilityNodeInfo root = this.mCacheImpl.valueAt(0);
            AccessibilityNodeInfo parent = root;
            while (parent != null) {
                root = parent;
                parent = this.mCacheImpl.get(parent.getParentNodeId());
            }
            int windowId = root.getWindowId();
            AccessibilityNodeInfo accessFocus = null;
            AccessibilityNodeInfo inputFocus = null;
            HashSet<AccessibilityNodeInfo> seen = new HashSet<>();
            Queue<AccessibilityNodeInfo> fringe = new LinkedList<>();
            fringe.add(root);
            while (!fringe.isEmpty()) {
                AccessibilityNodeInfo current = fringe.poll();
                if (!seen.add(current)) {
                    Log.e(LOG_TAG, "Duplicate node: " + current);
                    return;
                }
                if (current.isAccessibilityFocused()) {
                    if (accessFocus != null) {
                        Log.e(LOG_TAG, "Duplicate accessibility focus:" + current);
                    } else {
                        accessFocus = current;
                    }
                }
                if (current.isFocused()) {
                    if (inputFocus != null) {
                        Log.e(LOG_TAG, "Duplicate input focus: " + current);
                    } else {
                        inputFocus = current;
                    }
                }
                SparseLongArray childIds = current.getChildNodeIds();
                int childCount = childIds.size();
                for (int i = 0; i < childCount; i++) {
                    long childId = childIds.valueAt(i);
                    AccessibilityNodeInfo child = this.mCacheImpl.get(childId);
                    if (child != null) {
                        fringe.add(child);
                    }
                }
            }
            for (int i2 = 0; i2 < this.mCacheImpl.size(); i2++) {
                AccessibilityNodeInfo info = this.mCacheImpl.valueAt(i2);
                if (!seen.contains(info)) {
                    if (info.getWindowId() == windowId) {
                        Log.e(LOG_TAG, "Disconneced node: " + info);
                    } else {
                        Log.e(LOG_TAG, "Node from: " + info.getWindowId() + " not from:" + windowId + Separators.SP + info);
                    }
                }
            }
        }
    }
}