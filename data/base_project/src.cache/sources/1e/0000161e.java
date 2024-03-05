package android.view.accessibility;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: AccessibilityInteractionClient.class */
public final class AccessibilityInteractionClient extends IAccessibilityInteractionConnectionCallback.Stub {
    public static final int NO_ID = -1;
    private static final String LOG_TAG = "AccessibilityInteractionClient";
    private static final boolean DEBUG = false;
    private static final boolean CHECK_INTEGRITY = true;
    private static final long TIMEOUT_INTERACTION_MILLIS = 5000;
    private final AtomicInteger mInteractionIdCounter = new AtomicInteger();
    private final Object mInstanceLock = new Object();
    private volatile int mInteractionId = -1;
    private AccessibilityNodeInfo mFindAccessibilityNodeInfoResult;
    private List<AccessibilityNodeInfo> mFindAccessibilityNodeInfosResult;
    private boolean mPerformAccessibilityActionResult;
    private Message mSameThreadMessage;
    private static final Object sStaticLock = new Object();
    private static final LongSparseArray<AccessibilityInteractionClient> sClients = new LongSparseArray<>();
    private static final SparseArray<IAccessibilityServiceConnection> sConnectionCache = new SparseArray<>();
    private static final AccessibilityNodeInfoCache sAccessibilityNodeInfoCache = new AccessibilityNodeInfoCache();

    public static AccessibilityInteractionClient getInstance() {
        long threadId = Thread.currentThread().getId();
        return getInstanceForThread(threadId);
    }

    public static AccessibilityInteractionClient getInstanceForThread(long threadId) {
        AccessibilityInteractionClient accessibilityInteractionClient;
        synchronized (sStaticLock) {
            AccessibilityInteractionClient client = sClients.get(threadId);
            if (client == null) {
                client = new AccessibilityInteractionClient();
                sClients.put(threadId, client);
            }
            accessibilityInteractionClient = client;
        }
        return accessibilityInteractionClient;
    }

    private AccessibilityInteractionClient() {
    }

    public void setSameThreadMessage(Message message) {
        synchronized (this.mInstanceLock) {
            this.mSameThreadMessage = message;
            this.mInstanceLock.notifyAll();
        }
    }

    public AccessibilityNodeInfo getRootInActiveWindow(int connectionId) {
        return findAccessibilityNodeInfoByAccessibilityId(connectionId, -1, AccessibilityNodeInfo.ROOT_NODE_ID, false, 4);
    }

    public AccessibilityNodeInfo findAccessibilityNodeInfoByAccessibilityId(int connectionId, int accessibilityWindowId, long accessibilityNodeId, boolean bypassCache, int prefetchFlags) {
        AccessibilityNodeInfo cachedInfo;
        try {
            IAccessibilityServiceConnection connection = getConnection(connectionId);
            if (connection != null) {
                if (!bypassCache && (cachedInfo = sAccessibilityNodeInfoCache.get(accessibilityNodeId)) != null) {
                    return cachedInfo;
                }
                int interactionId = this.mInteractionIdCounter.getAndIncrement();
                boolean success = connection.findAccessibilityNodeInfoByAccessibilityId(accessibilityWindowId, accessibilityNodeId, interactionId, this, prefetchFlags, Thread.currentThread().getId());
                if (success) {
                    List<AccessibilityNodeInfo> infos = getFindAccessibilityNodeInfosResultAndClear(interactionId);
                    finalizeAndCacheAccessibilityNodeInfos(infos, connectionId);
                    if (infos != null && !infos.isEmpty()) {
                        return infos.get(0);
                    }
                    return null;
                }
                return null;
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(int connectionId, int accessibilityWindowId, long accessibilityNodeId, String viewId) {
        List<AccessibilityNodeInfo> infos;
        try {
            IAccessibilityServiceConnection connection = getConnection(connectionId);
            if (connection != null) {
                int interactionId = this.mInteractionIdCounter.getAndIncrement();
                boolean success = connection.findAccessibilityNodeInfosByViewId(accessibilityWindowId, accessibilityNodeId, viewId, interactionId, this, Thread.currentThread().getId());
                if (success && (infos = getFindAccessibilityNodeInfosResultAndClear(interactionId)) != null) {
                    finalizeAndCacheAccessibilityNodeInfos(infos, connectionId);
                    return infos;
                }
            }
        } catch (RemoteException e) {
        }
        return Collections.emptyList();
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(int connectionId, int accessibilityWindowId, long accessibilityNodeId, String text) {
        List<AccessibilityNodeInfo> infos;
        try {
            IAccessibilityServiceConnection connection = getConnection(connectionId);
            if (connection != null) {
                int interactionId = this.mInteractionIdCounter.getAndIncrement();
                boolean success = connection.findAccessibilityNodeInfosByText(accessibilityWindowId, accessibilityNodeId, text, interactionId, this, Thread.currentThread().getId());
                if (success && (infos = getFindAccessibilityNodeInfosResultAndClear(interactionId)) != null) {
                    finalizeAndCacheAccessibilityNodeInfos(infos, connectionId);
                    return infos;
                }
            }
        } catch (RemoteException e) {
        }
        return Collections.emptyList();
    }

    public AccessibilityNodeInfo findFocus(int connectionId, int accessibilityWindowId, long accessibilityNodeId, int focusType) {
        try {
            IAccessibilityServiceConnection connection = getConnection(connectionId);
            if (connection != null) {
                int interactionId = this.mInteractionIdCounter.getAndIncrement();
                boolean success = connection.findFocus(accessibilityWindowId, accessibilityNodeId, focusType, interactionId, this, Thread.currentThread().getId());
                if (success) {
                    AccessibilityNodeInfo info = getFindAccessibilityNodeInfoResultAndClear(interactionId);
                    finalizeAndCacheAccessibilityNodeInfo(info, connectionId);
                    return info;
                }
                return null;
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public AccessibilityNodeInfo focusSearch(int connectionId, int accessibilityWindowId, long accessibilityNodeId, int direction) {
        try {
            IAccessibilityServiceConnection connection = getConnection(connectionId);
            if (connection != null) {
                int interactionId = this.mInteractionIdCounter.getAndIncrement();
                boolean success = connection.focusSearch(accessibilityWindowId, accessibilityNodeId, direction, interactionId, this, Thread.currentThread().getId());
                if (success) {
                    AccessibilityNodeInfo info = getFindAccessibilityNodeInfoResultAndClear(interactionId);
                    finalizeAndCacheAccessibilityNodeInfo(info, connectionId);
                    return info;
                }
                return null;
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean performAccessibilityAction(int connectionId, int accessibilityWindowId, long accessibilityNodeId, int action, Bundle arguments) {
        try {
            IAccessibilityServiceConnection connection = getConnection(connectionId);
            if (connection != null) {
                int interactionId = this.mInteractionIdCounter.getAndIncrement();
                boolean success = connection.performAccessibilityAction(accessibilityWindowId, accessibilityNodeId, action, arguments, interactionId, this, Thread.currentThread().getId());
                if (success) {
                    return getPerformAccessibilityActionResultAndClear(interactionId);
                }
                return false;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void clearCache() {
        sAccessibilityNodeInfoCache.clear();
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        sAccessibilityNodeInfoCache.onAccessibilityEvent(event);
    }

    private AccessibilityNodeInfo getFindAccessibilityNodeInfoResultAndClear(int interactionId) {
        AccessibilityNodeInfo result;
        synchronized (this.mInstanceLock) {
            boolean success = waitForResultTimedLocked(interactionId);
            result = success ? this.mFindAccessibilityNodeInfoResult : null;
            clearResultLocked();
        }
        return result;
    }

    @Override // android.view.accessibility.IAccessibilityInteractionConnectionCallback
    public void setFindAccessibilityNodeInfoResult(AccessibilityNodeInfo info, int interactionId) {
        synchronized (this.mInstanceLock) {
            if (interactionId > this.mInteractionId) {
                this.mFindAccessibilityNodeInfoResult = info;
                this.mInteractionId = interactionId;
            }
            this.mInstanceLock.notifyAll();
        }
    }

    private List<AccessibilityNodeInfo> getFindAccessibilityNodeInfosResultAndClear(int interactionId) {
        List<AccessibilityNodeInfo> result;
        List<AccessibilityNodeInfo> list;
        synchronized (this.mInstanceLock) {
            boolean success = waitForResultTimedLocked(interactionId);
            if (success) {
                result = this.mFindAccessibilityNodeInfosResult;
            } else {
                result = Collections.emptyList();
            }
            clearResultLocked();
            if (Build.IS_DEBUGGABLE) {
                checkFindAccessibilityNodeInfoResultIntegrity(result);
            }
            list = result;
        }
        return list;
    }

    @Override // android.view.accessibility.IAccessibilityInteractionConnectionCallback
    public void setFindAccessibilityNodeInfosResult(List<AccessibilityNodeInfo> infos, int interactionId) {
        synchronized (this.mInstanceLock) {
            if (interactionId > this.mInteractionId) {
                if (infos != null) {
                    boolean isIpcCall = Binder.getCallingPid() != Process.myPid();
                    if (!isIpcCall) {
                        this.mFindAccessibilityNodeInfosResult = new ArrayList(infos);
                    } else {
                        this.mFindAccessibilityNodeInfosResult = infos;
                    }
                } else {
                    this.mFindAccessibilityNodeInfosResult = Collections.emptyList();
                }
                this.mInteractionId = interactionId;
            }
            this.mInstanceLock.notifyAll();
        }
    }

    private boolean getPerformAccessibilityActionResultAndClear(int interactionId) {
        boolean result;
        synchronized (this.mInstanceLock) {
            boolean success = waitForResultTimedLocked(interactionId);
            result = success ? this.mPerformAccessibilityActionResult : false;
            clearResultLocked();
        }
        return result;
    }

    @Override // android.view.accessibility.IAccessibilityInteractionConnectionCallback
    public void setPerformAccessibilityActionResult(boolean succeeded, int interactionId) {
        synchronized (this.mInstanceLock) {
            if (interactionId > this.mInteractionId) {
                this.mPerformAccessibilityActionResult = succeeded;
                this.mInteractionId = interactionId;
            }
            this.mInstanceLock.notifyAll();
        }
    }

    private void clearResultLocked() {
        this.mInteractionId = -1;
        this.mFindAccessibilityNodeInfoResult = null;
        this.mFindAccessibilityNodeInfosResult = null;
        this.mPerformAccessibilityActionResult = false;
    }

    private boolean waitForResultTimedLocked(int interactionId) {
        long startTimeMillis = SystemClock.uptimeMillis();
        while (true) {
            try {
                Message sameProcessMessage = getSameProcessMessageAndClear();
                if (sameProcessMessage != null) {
                    sameProcessMessage.getTarget().handleMessage(sameProcessMessage);
                }
            } catch (InterruptedException e) {
            }
            if (this.mInteractionId == interactionId) {
                return true;
            }
            if (this.mInteractionId > interactionId) {
                return false;
            }
            long elapsedTimeMillis = SystemClock.uptimeMillis() - startTimeMillis;
            long waitTimeMillis = 5000 - elapsedTimeMillis;
            if (waitTimeMillis <= 0) {
                return false;
            }
            this.mInstanceLock.wait(waitTimeMillis);
        }
    }

    private void finalizeAndCacheAccessibilityNodeInfo(AccessibilityNodeInfo info, int connectionId) {
        if (info != null) {
            info.setConnectionId(connectionId);
            info.setSealed(true);
            sAccessibilityNodeInfoCache.add(info);
        }
    }

    private void finalizeAndCacheAccessibilityNodeInfos(List<AccessibilityNodeInfo> infos, int connectionId) {
        if (infos != null) {
            int infosCount = infos.size();
            for (int i = 0; i < infosCount; i++) {
                AccessibilityNodeInfo info = infos.get(i);
                finalizeAndCacheAccessibilityNodeInfo(info, connectionId);
            }
        }
    }

    private Message getSameProcessMessageAndClear() {
        Message result;
        synchronized (this.mInstanceLock) {
            result = this.mSameThreadMessage;
            this.mSameThreadMessage = null;
        }
        return result;
    }

    public IAccessibilityServiceConnection getConnection(int connectionId) {
        IAccessibilityServiceConnection iAccessibilityServiceConnection;
        synchronized (sConnectionCache) {
            iAccessibilityServiceConnection = sConnectionCache.get(connectionId);
        }
        return iAccessibilityServiceConnection;
    }

    public void addConnection(int connectionId, IAccessibilityServiceConnection connection) {
        synchronized (sConnectionCache) {
            sConnectionCache.put(connectionId, connection);
        }
    }

    public void removeConnection(int connectionId) {
        synchronized (sConnectionCache) {
            sConnectionCache.remove(connectionId);
        }
    }

    private void checkFindAccessibilityNodeInfoResultIntegrity(List<AccessibilityNodeInfo> infos) {
        if (infos.size() == 0) {
            return;
        }
        AccessibilityNodeInfo root = infos.get(0);
        int infoCount = infos.size();
        for (int i = 1; i < infoCount; i++) {
            int j = i;
            while (true) {
                if (j < infoCount) {
                    AccessibilityNodeInfo candidate = infos.get(j);
                    if (root.getParentNodeId() != candidate.getSourceNodeId()) {
                        j++;
                    } else {
                        root = candidate;
                        break;
                    }
                }
            }
        }
        if (root == null) {
            Log.e(LOG_TAG, "No root.");
        }
        HashSet<AccessibilityNodeInfo> seen = new HashSet<>();
        Queue<AccessibilityNodeInfo> fringe = new LinkedList<>();
        fringe.add(root);
        while (!fringe.isEmpty()) {
            AccessibilityNodeInfo current = fringe.poll();
            if (!seen.add(current)) {
                Log.e(LOG_TAG, "Duplicate node.");
                return;
            }
            SparseLongArray childIds = current.getChildNodeIds();
            int childCount = childIds.size();
            for (int i2 = 0; i2 < childCount; i2++) {
                long childId = childIds.valueAt(i2);
                for (int j2 = 0; j2 < infoCount; j2++) {
                    AccessibilityNodeInfo child = infos.get(j2);
                    if (child.getSourceNodeId() == childId) {
                        fringe.add(child);
                    }
                }
            }
        }
        int disconnectedCount = infos.size() - seen.size();
        if (disconnectedCount > 0) {
            Log.e(LOG_TAG, disconnectedCount + " Disconnected nodes.");
        }
    }
}