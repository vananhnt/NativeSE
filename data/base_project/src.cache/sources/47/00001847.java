package android.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.internal.R;
import com.android.internal.widget.IRemoteViewsAdapterConnection;
import com.android.internal.widget.IRemoteViewsFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: RemoteViewsAdapter.class */
public class RemoteViewsAdapter extends BaseAdapter implements Handler.Callback {
    private static final String MULTI_USER_PERM = "android.permission.INTERACT_ACROSS_USERS_FULL";
    private static final String TAG = "RemoteViewsAdapter";
    private static final int sDefaultCacheSize = 40;
    private static final int sUnbindServiceDelay = 5000;
    private static final int sDefaultLoadingViewHeight = 50;
    private static final int sDefaultMessageType = 0;
    private static final int sUnbindServiceMessageType = 1;
    private final Context mContext;
    private final Intent mIntent;
    private final int mAppWidgetId;
    private LayoutInflater mLayoutInflater;
    private RemoteViewsAdapterServiceConnection mServiceConnection;
    private WeakReference<RemoteAdapterConnectionCallback> mCallback;
    private RemoteViews.OnClickHandler mRemoteViewsOnClickHandler;
    private FixedSizeRemoteViewsCache mCache;
    private int mVisibleWindowLowerBound;
    private int mVisibleWindowUpperBound;
    private boolean mNotifyDataSetChangedAfterOnServiceConnected = false;
    private RemoteViewsFrameLayoutRefSet mRequestedViews;
    private HandlerThread mWorkerThread;
    private Handler mWorkerQueue;
    private Handler mMainQueue;
    private static final HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> sCachedRemoteViewsCaches = new HashMap<>();
    private static final HashMap<RemoteViewsCacheKey, Runnable> sRemoteViewsCacheRemoveRunnables = new HashMap<>();
    private static HandlerThread sCacheRemovalThread;
    private static Handler sCacheRemovalQueue;
    private static final int REMOTE_VIEWS_CACHE_DURATION = 5000;
    private boolean mDataReady;
    int mUserId;

    /* loaded from: RemoteViewsAdapter$RemoteAdapterConnectionCallback.class */
    public interface RemoteAdapterConnectionCallback {
        boolean onRemoteAdapterConnected();

        void onRemoteAdapterDisconnected();

        void deferNotifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteViewsAdapter$RemoteViewsAdapterServiceConnection.class */
    public static class RemoteViewsAdapterServiceConnection extends IRemoteViewsAdapterConnection.Stub {
        private boolean mIsConnected;
        private boolean mIsConnecting;
        private WeakReference<RemoteViewsAdapter> mAdapter;
        private IRemoteViewsFactory mRemoteViewsFactory;

        public RemoteViewsAdapterServiceConnection(RemoteViewsAdapter adapter) {
            this.mAdapter = new WeakReference<>(adapter);
        }

        public synchronized void bind(Context context, int appWidgetId, Intent intent) {
            if (!this.mIsConnecting) {
                try {
                    AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    RemoteViewsAdapter adapter = this.mAdapter.get();
                    if (adapter != null) {
                        RemoteViewsAdapter.checkInteractAcrossUsersPermission(context, adapter.mUserId);
                        mgr.bindRemoteViewsService(appWidgetId, intent, asBinder(), new UserHandle(adapter.mUserId));
                    } else {
                        Slog.w(RemoteViewsAdapter.TAG, "bind: adapter was null");
                    }
                    this.mIsConnecting = true;
                } catch (Exception e) {
                    Log.e("RemoteViewsAdapterServiceConnection", "bind(): " + e.getMessage());
                    this.mIsConnecting = false;
                    this.mIsConnected = false;
                }
            }
        }

        public synchronized void unbind(Context context, int appWidgetId, Intent intent) {
            try {
                AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                RemoteViewsAdapter adapter = this.mAdapter.get();
                if (adapter != null) {
                    RemoteViewsAdapter.checkInteractAcrossUsersPermission(context, adapter.mUserId);
                    mgr.unbindRemoteViewsService(appWidgetId, intent, new UserHandle(adapter.mUserId));
                } else {
                    Slog.w(RemoteViewsAdapter.TAG, "unbind: adapter was null");
                }
                this.mIsConnecting = false;
            } catch (Exception e) {
                Log.e("RemoteViewsAdapterServiceConnection", "unbind(): " + e.getMessage());
                this.mIsConnecting = false;
                this.mIsConnected = false;
            }
        }

        @Override // com.android.internal.widget.IRemoteViewsAdapterConnection
        public synchronized void onServiceConnected(IBinder service) {
            this.mRemoteViewsFactory = IRemoteViewsFactory.Stub.asInterface(service);
            final RemoteViewsAdapter adapter = this.mAdapter.get();
            if (adapter == null) {
                return;
            }
            adapter.mWorkerQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.RemoteViewsAdapterServiceConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    if (adapter.mNotifyDataSetChangedAfterOnServiceConnected) {
                        adapter.onNotifyDataSetChanged();
                    } else {
                        IRemoteViewsFactory factory = adapter.mServiceConnection.getRemoteViewsFactory();
                        try {
                            if (!factory.isCreated()) {
                                factory.onDataSetChanged();
                            }
                        } catch (RemoteException e) {
                            Log.e(RemoteViewsAdapter.TAG, "Error notifying factory of data set changed in onServiceConnected(): " + e.getMessage());
                            return;
                        } catch (RuntimeException e2) {
                            Log.e(RemoteViewsAdapter.TAG, "Error notifying factory of data set changed in onServiceConnected(): " + e2.getMessage());
                        }
                        adapter.updateTemporaryMetaData();
                        adapter.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.RemoteViewsAdapterServiceConnection.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                synchronized (adapter.mCache) {
                                    adapter.mCache.commitTemporaryMetaData();
                                }
                                RemoteAdapterConnectionCallback callback = (RemoteAdapterConnectionCallback) adapter.mCallback.get();
                                if (callback != null) {
                                    callback.onRemoteAdapterConnected();
                                }
                            }
                        });
                    }
                    adapter.enqueueDeferredUnbindServiceMessage();
                    RemoteViewsAdapterServiceConnection.this.mIsConnected = true;
                    RemoteViewsAdapterServiceConnection.this.mIsConnecting = false;
                }
            });
        }

        @Override // com.android.internal.widget.IRemoteViewsAdapterConnection
        public synchronized void onServiceDisconnected() {
            this.mIsConnected = false;
            this.mIsConnecting = false;
            this.mRemoteViewsFactory = null;
            final RemoteViewsAdapter adapter = this.mAdapter.get();
            if (adapter == null) {
                return;
            }
            adapter.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.RemoteViewsAdapterServiceConnection.2
                @Override // java.lang.Runnable
                public void run() {
                    adapter.mMainQueue.removeMessages(1);
                    RemoteAdapterConnectionCallback callback = (RemoteAdapterConnectionCallback) adapter.mCallback.get();
                    if (callback != null) {
                        callback.onRemoteAdapterDisconnected();
                    }
                }
            });
        }

        public synchronized IRemoteViewsFactory getRemoteViewsFactory() {
            return this.mRemoteViewsFactory;
        }

        public synchronized boolean isConnected() {
            return this.mIsConnected;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteViewsAdapter$RemoteViewsFrameLayout.class */
    public static class RemoteViewsFrameLayout extends FrameLayout {
        public RemoteViewsFrameLayout(Context context) {
            super(context);
        }

        public void onRemoteViewsLoaded(RemoteViews view, RemoteViews.OnClickHandler handler) {
            try {
                removeAllViews();
                addView(view.apply(getContext(), this, handler));
            } catch (Exception e) {
                Log.e(RemoteViewsAdapter.TAG, "Failed to apply RemoteViews.");
            }
        }
    }

    /* loaded from: RemoteViewsAdapter$RemoteViewsFrameLayoutRefSet.class */
    private class RemoteViewsFrameLayoutRefSet {
        private HashMap<Integer, LinkedList<RemoteViewsFrameLayout>> mReferences = new HashMap<>();
        private HashMap<RemoteViewsFrameLayout, LinkedList<RemoteViewsFrameLayout>> mViewToLinkedList = new HashMap<>();

        public RemoteViewsFrameLayoutRefSet() {
        }

        public void add(int position, RemoteViewsFrameLayout layout) {
            LinkedList<RemoteViewsFrameLayout> refs;
            Integer pos = Integer.valueOf(position);
            if (this.mReferences.containsKey(pos)) {
                refs = this.mReferences.get(pos);
            } else {
                refs = new LinkedList<>();
                this.mReferences.put(pos, refs);
            }
            this.mViewToLinkedList.put(layout, refs);
            refs.add(layout);
        }

        public void notifyOnRemoteViewsLoaded(int position, RemoteViews view) {
            if (view == null) {
                return;
            }
            Integer pos = Integer.valueOf(position);
            if (this.mReferences.containsKey(pos)) {
                LinkedList<RemoteViewsFrameLayout> refs = this.mReferences.get(pos);
                Iterator i$ = refs.iterator();
                while (i$.hasNext()) {
                    RemoteViewsFrameLayout ref = i$.next();
                    ref.onRemoteViewsLoaded(view, RemoteViewsAdapter.this.mRemoteViewsOnClickHandler);
                    if (this.mViewToLinkedList.containsKey(ref)) {
                        this.mViewToLinkedList.remove(ref);
                    }
                }
                refs.clear();
                this.mReferences.remove(pos);
            }
        }

        public void removeView(RemoteViewsFrameLayout rvfl) {
            if (this.mViewToLinkedList.containsKey(rvfl)) {
                this.mViewToLinkedList.get(rvfl).remove(rvfl);
                this.mViewToLinkedList.remove(rvfl);
            }
        }

        public void clear() {
            this.mReferences.clear();
            this.mViewToLinkedList.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteViewsAdapter$RemoteViewsMetaData.class */
    public static class RemoteViewsMetaData {
        int count;
        int viewTypeCount;
        boolean hasStableIds;
        RemoteViews mUserLoadingView;
        RemoteViews mFirstView;
        int mFirstViewHeight;
        private final HashMap<Integer, Integer> mTypeIdIndexMap = new HashMap<>();

        public RemoteViewsMetaData() {
            reset();
        }

        public void set(RemoteViewsMetaData d) {
            synchronized (d) {
                this.count = d.count;
                this.viewTypeCount = d.viewTypeCount;
                this.hasStableIds = d.hasStableIds;
                setLoadingViewTemplates(d.mUserLoadingView, d.mFirstView);
            }
        }

        public void reset() {
            this.count = 0;
            this.viewTypeCount = 1;
            this.hasStableIds = true;
            this.mUserLoadingView = null;
            this.mFirstView = null;
            this.mFirstViewHeight = 0;
            this.mTypeIdIndexMap.clear();
        }

        public void setLoadingViewTemplates(RemoteViews loadingView, RemoteViews firstView) {
            this.mUserLoadingView = loadingView;
            if (firstView != null) {
                this.mFirstView = firstView;
                this.mFirstViewHeight = -1;
            }
        }

        public int getMappedViewType(int typeId) {
            if (this.mTypeIdIndexMap.containsKey(Integer.valueOf(typeId))) {
                return this.mTypeIdIndexMap.get(Integer.valueOf(typeId)).intValue();
            }
            int incrementalTypeId = this.mTypeIdIndexMap.size() + 1;
            this.mTypeIdIndexMap.put(Integer.valueOf(typeId), Integer.valueOf(incrementalTypeId));
            return incrementalTypeId;
        }

        public boolean isViewTypeInRange(int typeId) {
            int mappedType = getMappedViewType(typeId);
            if (mappedType >= this.viewTypeCount) {
                return false;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViewsFrameLayout createLoadingView(int position, View convertView, ViewGroup parent, Object lock, LayoutInflater layoutInflater, RemoteViews.OnClickHandler handler) {
            Context context = parent.getContext();
            RemoteViewsFrameLayout layout = new RemoteViewsFrameLayout(context);
            synchronized (lock) {
                boolean customLoadingViewAvailable = false;
                if (this.mUserLoadingView != null) {
                    try {
                        View loadingView = this.mUserLoadingView.apply(parent.getContext(), parent, handler);
                        loadingView.setTagInternal(R.id.rowTypeId, new Integer(0));
                        layout.addView(loadingView);
                        customLoadingViewAvailable = true;
                    } catch (Exception e) {
                        Log.w(RemoteViewsAdapter.TAG, "Error inflating custom loading view, using default loadingview instead", e);
                    }
                }
                if (!customLoadingViewAvailable) {
                    if (this.mFirstViewHeight < 0) {
                        try {
                            View firstView = this.mFirstView.apply(parent.getContext(), parent, handler);
                            firstView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                            this.mFirstViewHeight = firstView.getMeasuredHeight();
                            this.mFirstView = null;
                        } catch (Exception e2) {
                            float density = context.getResources().getDisplayMetrics().density;
                            this.mFirstViewHeight = Math.round(50.0f * density);
                            this.mFirstView = null;
                            Log.w(RemoteViewsAdapter.TAG, "Error inflating first RemoteViews" + e2);
                        }
                    }
                    TextView loadingTextView = (TextView) layoutInflater.inflate(R.layout.remote_views_adapter_default_loading_view, (ViewGroup) layout, false);
                    loadingTextView.setHeight(this.mFirstViewHeight);
                    loadingTextView.setTag(new Integer(0));
                    layout.addView(loadingTextView);
                }
            }
            return layout;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteViewsAdapter$RemoteViewsIndexMetaData.class */
    public static class RemoteViewsIndexMetaData {
        int typeId;
        long itemId;

        public RemoteViewsIndexMetaData(RemoteViews v, long itemId) {
            set(v, itemId);
        }

        public void set(RemoteViews v, long id) {
            this.itemId = id;
            if (v != null) {
                this.typeId = v.getLayoutId();
            } else {
                this.typeId = 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteViewsAdapter$FixedSizeRemoteViewsCache.class */
    public static class FixedSizeRemoteViewsCache {
        private static final String TAG = "FixedSizeRemoteViewsCache";
        private int mMaxCount;
        private int mMaxCountSlack;
        private static final float sMaxCountSlackPercent = 0.75f;
        private static final int sMaxMemoryLimitInBytes = 2097152;
        private int mPreloadLowerBound = 0;
        private int mPreloadUpperBound = -1;
        private final RemoteViewsMetaData mMetaData = new RemoteViewsMetaData();
        private final RemoteViewsMetaData mTemporaryMetaData = new RemoteViewsMetaData();
        private HashMap<Integer, RemoteViewsIndexMetaData> mIndexMetaData = new HashMap<>();
        private HashMap<Integer, RemoteViews> mIndexRemoteViews = new HashMap<>();
        private HashSet<Integer> mRequestedIndices = new HashSet<>();
        private int mLastRequestedIndex = -1;
        private HashSet<Integer> mLoadIndices = new HashSet<>();

        public FixedSizeRemoteViewsCache(int maxCacheSize) {
            this.mMaxCount = maxCacheSize;
            this.mMaxCountSlack = Math.round(sMaxCountSlackPercent * (this.mMaxCount / 2));
        }

        public void insert(int position, RemoteViews v, long itemId, ArrayList<Integer> visibleWindow) {
            if (this.mIndexRemoteViews.size() >= this.mMaxCount) {
                this.mIndexRemoteViews.remove(Integer.valueOf(getFarthestPositionFrom(position, visibleWindow)));
            }
            int pruneFromPosition = this.mLastRequestedIndex > -1 ? this.mLastRequestedIndex : position;
            while (getRemoteViewsBitmapMemoryUsage() >= 2097152) {
                this.mIndexRemoteViews.remove(Integer.valueOf(getFarthestPositionFrom(pruneFromPosition, visibleWindow)));
            }
            if (this.mIndexMetaData.containsKey(Integer.valueOf(position))) {
                RemoteViewsIndexMetaData metaData = this.mIndexMetaData.get(Integer.valueOf(position));
                metaData.set(v, itemId);
            } else {
                this.mIndexMetaData.put(Integer.valueOf(position), new RemoteViewsIndexMetaData(v, itemId));
            }
            this.mIndexRemoteViews.put(Integer.valueOf(position), v);
        }

        public RemoteViewsMetaData getMetaData() {
            return this.mMetaData;
        }

        public RemoteViewsMetaData getTemporaryMetaData() {
            return this.mTemporaryMetaData;
        }

        public RemoteViews getRemoteViewsAt(int position) {
            if (this.mIndexRemoteViews.containsKey(Integer.valueOf(position))) {
                return this.mIndexRemoteViews.get(Integer.valueOf(position));
            }
            return null;
        }

        public RemoteViewsIndexMetaData getMetaDataAt(int position) {
            if (this.mIndexMetaData.containsKey(Integer.valueOf(position))) {
                return this.mIndexMetaData.get(Integer.valueOf(position));
            }
            return null;
        }

        public void commitTemporaryMetaData() {
            synchronized (this.mTemporaryMetaData) {
                synchronized (this.mMetaData) {
                    this.mMetaData.set(this.mTemporaryMetaData);
                }
            }
        }

        private int getRemoteViewsBitmapMemoryUsage() {
            int mem = 0;
            for (Integer i : this.mIndexRemoteViews.keySet()) {
                RemoteViews v = this.mIndexRemoteViews.get(i);
                if (v != null) {
                    mem += v.estimateMemoryUsage();
                }
            }
            return mem;
        }

        private int getFarthestPositionFrom(int pos, ArrayList<Integer> visibleWindow) {
            int maxDist = 0;
            int maxDistIndex = -1;
            int maxDistNotVisible = 0;
            int maxDistIndexNotVisible = -1;
            for (Integer num : this.mIndexRemoteViews.keySet()) {
                int i = num.intValue();
                int dist = Math.abs(i - pos);
                if (dist > maxDistNotVisible && !visibleWindow.contains(Integer.valueOf(i))) {
                    maxDistIndexNotVisible = i;
                    maxDistNotVisible = dist;
                }
                if (dist >= maxDist) {
                    maxDistIndex = i;
                    maxDist = dist;
                }
            }
            if (maxDistIndexNotVisible > -1) {
                return maxDistIndexNotVisible;
            }
            return maxDistIndex;
        }

        public void queueRequestedPositionToLoad(int position) {
            this.mLastRequestedIndex = position;
            synchronized (this.mLoadIndices) {
                this.mRequestedIndices.add(Integer.valueOf(position));
                this.mLoadIndices.add(Integer.valueOf(position));
            }
        }

        public boolean queuePositionsToBePreloadedFromRequestedPosition(int position) {
            int count;
            if (this.mPreloadLowerBound <= position && position <= this.mPreloadUpperBound) {
                int center = (this.mPreloadUpperBound + this.mPreloadLowerBound) / 2;
                if (Math.abs(position - center) < this.mMaxCountSlack) {
                    return false;
                }
            }
            synchronized (this.mMetaData) {
                count = this.mMetaData.count;
            }
            synchronized (this.mLoadIndices) {
                this.mLoadIndices.clear();
                this.mLoadIndices.addAll(this.mRequestedIndices);
                int halfMaxCount = this.mMaxCount / 2;
                this.mPreloadLowerBound = position - halfMaxCount;
                this.mPreloadUpperBound = position + halfMaxCount;
                int effectiveLowerBound = Math.max(0, this.mPreloadLowerBound);
                int effectiveUpperBound = Math.min(this.mPreloadUpperBound, count - 1);
                for (int i = effectiveLowerBound; i <= effectiveUpperBound; i++) {
                    this.mLoadIndices.add(Integer.valueOf(i));
                }
                this.mLoadIndices.removeAll(this.mIndexRemoteViews.keySet());
            }
            return true;
        }

        public int[] getNextIndexToLoad() {
            synchronized (this.mLoadIndices) {
                if (!this.mRequestedIndices.isEmpty()) {
                    Integer i = this.mRequestedIndices.iterator().next();
                    this.mRequestedIndices.remove(i);
                    this.mLoadIndices.remove(i);
                    return new int[]{i.intValue(), 1};
                } else if (this.mLoadIndices.isEmpty()) {
                    return new int[]{-1, 0};
                } else {
                    Integer i2 = this.mLoadIndices.iterator().next();
                    this.mLoadIndices.remove(i2);
                    return new int[]{i2.intValue(), 0};
                }
            }
        }

        public boolean containsRemoteViewAt(int position) {
            return this.mIndexRemoteViews.containsKey(Integer.valueOf(position));
        }

        public boolean containsMetaDataAt(int position) {
            return this.mIndexMetaData.containsKey(Integer.valueOf(position));
        }

        public void reset() {
            this.mPreloadLowerBound = 0;
            this.mPreloadUpperBound = -1;
            this.mLastRequestedIndex = -1;
            this.mIndexRemoteViews.clear();
            this.mIndexMetaData.clear();
            synchronized (this.mLoadIndices) {
                this.mRequestedIndices.clear();
                this.mLoadIndices.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: RemoteViewsAdapter$RemoteViewsCacheKey.class */
    public static class RemoteViewsCacheKey {
        final Intent.FilterComparison filter;
        final int widgetId;
        final int userId;

        RemoteViewsCacheKey(Intent.FilterComparison filter, int widgetId, int userId) {
            this.filter = filter;
            this.widgetId = widgetId;
            this.userId = userId;
        }

        public boolean equals(Object o) {
            if (!(o instanceof RemoteViewsCacheKey)) {
                return false;
            }
            RemoteViewsCacheKey other = (RemoteViewsCacheKey) o;
            return other.filter.equals(this.filter) && other.widgetId == this.widgetId && other.userId == this.userId;
        }

        public int hashCode() {
            return ((this.filter == null ? 0 : this.filter.hashCode()) ^ (this.widgetId << 2)) ^ (this.userId << 10);
        }
    }

    public RemoteViewsAdapter(Context context, Intent intent, RemoteAdapterConnectionCallback callback) {
        this.mDataReady = false;
        this.mContext = context;
        this.mIntent = intent;
        this.mAppWidgetId = intent.getIntExtra("remoteAdapterAppWidgetId", -1);
        this.mLayoutInflater = LayoutInflater.from(context);
        if (this.mIntent == null) {
            throw new IllegalArgumentException("Non-null Intent must be specified.");
        }
        this.mRequestedViews = new RemoteViewsFrameLayoutRefSet();
        checkInteractAcrossUsersPermission(context, UserHandle.myUserId());
        this.mUserId = context.getUserId();
        if (intent.hasExtra("remoteAdapterAppWidgetId")) {
            intent.removeExtra("remoteAdapterAppWidgetId");
        }
        this.mWorkerThread = new HandlerThread("RemoteViewsCache-loader");
        this.mWorkerThread.start();
        this.mWorkerQueue = new Handler(this.mWorkerThread.getLooper());
        this.mMainQueue = new Handler(Looper.myLooper(), this);
        if (sCacheRemovalThread == null) {
            sCacheRemovalThread = new HandlerThread("RemoteViewsAdapter-cachePruner");
            sCacheRemovalThread.start();
            sCacheRemovalQueue = new Handler(sCacheRemovalThread.getLooper());
        }
        this.mCallback = new WeakReference<>(callback);
        this.mServiceConnection = new RemoteViewsAdapterServiceConnection(this);
        RemoteViewsCacheKey key = new RemoteViewsCacheKey(new Intent.FilterComparison(this.mIntent), this.mAppWidgetId, this.mUserId);
        synchronized (sCachedRemoteViewsCaches) {
            if (sCachedRemoteViewsCaches.containsKey(key)) {
                this.mCache = sCachedRemoteViewsCaches.get(key);
                synchronized (this.mCache.mMetaData) {
                    if (this.mCache.mMetaData.count > 0) {
                        this.mDataReady = true;
                    }
                }
            } else {
                this.mCache = new FixedSizeRemoteViewsCache(40);
            }
            if (!this.mDataReady) {
                requestBindService();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkInteractAcrossUsersPermission(Context context, int userId) {
        if (context.getUserId() != userId && context.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) {
            throw new SecurityException("Must have permission android.permission.INTERACT_ACROSS_USERS_FULL to inflate another user's widget");
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mWorkerThread != null) {
                this.mWorkerThread.quit();
            }
        } finally {
            super.finalize();
        }
    }

    public boolean isDataReady() {
        return this.mDataReady;
    }

    public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler handler) {
        this.mRemoteViewsOnClickHandler = handler;
    }

    public void saveRemoteViewsCache() {
        int metaDataCount;
        int numRemoteViewsCached;
        final RemoteViewsCacheKey key = new RemoteViewsCacheKey(new Intent.FilterComparison(this.mIntent), this.mAppWidgetId, this.mUserId);
        synchronized (sCachedRemoteViewsCaches) {
            if (sRemoteViewsCacheRemoveRunnables.containsKey(key)) {
                sCacheRemovalQueue.removeCallbacks(sRemoteViewsCacheRemoveRunnables.get(key));
                sRemoteViewsCacheRemoveRunnables.remove(key);
            }
            synchronized (this.mCache.mMetaData) {
                metaDataCount = this.mCache.mMetaData.count;
            }
            synchronized (this.mCache) {
                numRemoteViewsCached = this.mCache.mIndexRemoteViews.size();
            }
            if (metaDataCount > 0 && numRemoteViewsCached > 0) {
                sCachedRemoteViewsCaches.put(key, this.mCache);
            }
            Runnable r = new Runnable() { // from class: android.widget.RemoteViewsAdapter.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (RemoteViewsAdapter.sCachedRemoteViewsCaches) {
                        if (RemoteViewsAdapter.sCachedRemoteViewsCaches.containsKey(key)) {
                            RemoteViewsAdapter.sCachedRemoteViewsCaches.remove(key);
                        }
                        if (RemoteViewsAdapter.sRemoteViewsCacheRemoveRunnables.containsKey(key)) {
                            RemoteViewsAdapter.sRemoteViewsCacheRemoveRunnables.remove(key);
                        }
                    }
                }
            };
            sRemoteViewsCacheRemoveRunnables.put(key, r);
            sCacheRemovalQueue.postDelayed(r, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadNextIndexInBackground() {
        this.mWorkerQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                int position;
                if (RemoteViewsAdapter.this.mServiceConnection.isConnected()) {
                    synchronized (RemoteViewsAdapter.this.mCache) {
                        int[] res = RemoteViewsAdapter.this.mCache.getNextIndexToLoad();
                        position = res[0];
                    }
                    if (position > -1) {
                        RemoteViewsAdapter.this.updateRemoteViews(position, true);
                        RemoteViewsAdapter.this.loadNextIndexInBackground();
                        return;
                    }
                    RemoteViewsAdapter.this.enqueueDeferredUnbindServiceMessage();
                }
            }
        });
    }

    private void processException(String method, Exception e) {
        Log.e(TAG, "Error in " + method + ": " + e.getMessage());
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            metaData.reset();
        }
        synchronized (this.mCache) {
            this.mCache.reset();
        }
        this.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.3
            @Override // java.lang.Runnable
            public void run() {
                RemoteViewsAdapter.this.superNotifyDataSetChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTemporaryMetaData() {
        IRemoteViewsFactory factory = this.mServiceConnection.getRemoteViewsFactory();
        try {
            boolean hasStableIds = factory.hasStableIds();
            int viewTypeCount = factory.getViewTypeCount();
            int count = factory.getCount();
            RemoteViews loadingView = factory.getLoadingView();
            RemoteViews firstView = null;
            if (count > 0 && loadingView == null) {
                firstView = factory.getViewAt(0);
            }
            RemoteViewsMetaData tmpMetaData = this.mCache.getTemporaryMetaData();
            synchronized (tmpMetaData) {
                tmpMetaData.hasStableIds = hasStableIds;
                tmpMetaData.viewTypeCount = viewTypeCount + 1;
                tmpMetaData.count = count;
                tmpMetaData.setLoadingViewTemplates(loadingView, firstView);
            }
        } catch (RemoteException e) {
            processException("updateMetaData", e);
        } catch (RuntimeException e2) {
            processException("updateMetaData", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRemoteViews(final int position, boolean notifyWhenLoaded) {
        boolean viewTypeInRange;
        int cacheCount;
        IRemoteViewsFactory factory = this.mServiceConnection.getRemoteViewsFactory();
        try {
            final RemoteViews remoteViews = factory.getViewAt(position);
            remoteViews.setUser(new UserHandle(this.mUserId));
            long itemId = factory.getItemId(position);
            if (remoteViews == null) {
                Log.e(TAG, "Error in updateRemoteViews(" + position + "):  null RemoteViews returned from RemoteViewsFactory.");
                return;
            }
            int layoutId = remoteViews.getLayoutId();
            RemoteViewsMetaData metaData = this.mCache.getMetaData();
            synchronized (metaData) {
                viewTypeInRange = metaData.isViewTypeInRange(layoutId);
                cacheCount = this.mCache.mMetaData.count;
            }
            synchronized (this.mCache) {
                if (viewTypeInRange) {
                    ArrayList<Integer> visibleWindow = getVisibleWindow(this.mVisibleWindowLowerBound, this.mVisibleWindowUpperBound, cacheCount);
                    this.mCache.insert(position, remoteViews, itemId, visibleWindow);
                    if (notifyWhenLoaded) {
                        this.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.4
                            @Override // java.lang.Runnable
                            public void run() {
                                RemoteViewsAdapter.this.mRequestedViews.notifyOnRemoteViewsLoaded(position, remoteViews);
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Error: widget's RemoteViewsFactory returns more view types than  indicated by getViewTypeCount() ");
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error in updateRemoteViews(" + position + "): " + e.getMessage());
        } catch (RuntimeException e2) {
            Log.e(TAG, "Error in updateRemoteViews(" + position + "): " + e2.getMessage());
        }
    }

    public Intent getRemoteViewsServiceIntent() {
        return this.mIntent;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int i;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            i = metaData.count;
        }
        return i;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        synchronized (this.mCache) {
            if (this.mCache.containsMetaDataAt(position)) {
                return this.mCache.getMetaDataAt(position).itemId;
            }
            return 0L;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        int mappedViewType;
        synchronized (this.mCache) {
            if (this.mCache.containsMetaDataAt(position)) {
                int typeId = this.mCache.getMetaDataAt(position).typeId;
                RemoteViewsMetaData metaData = this.mCache.getMetaData();
                synchronized (metaData) {
                    mappedViewType = metaData.getMappedViewType(typeId);
                }
                return mappedViewType;
            }
            return 0;
        }
    }

    private int getConvertViewTypeId(View convertView) {
        Object tag;
        int typeId = -1;
        if (convertView != null && (tag = convertView.getTag(R.id.rowTypeId)) != null) {
            typeId = ((Integer) tag).intValue();
        }
        return typeId;
    }

    public void setVisibleRangeHint(int lowerBound, int upperBound) {
        this.mVisibleWindowLowerBound = lowerBound;
        this.mVisibleWindowUpperBound = upperBound;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        RemoteViewsFrameLayout loadingView;
        synchronized (this.mCache) {
            boolean isInCache = this.mCache.containsRemoteViewAt(position);
            boolean isConnected = this.mServiceConnection.isConnected();
            boolean hasNewItems = false;
            if (convertView != null && (convertView instanceof RemoteViewsFrameLayout)) {
                this.mRequestedViews.removeView((RemoteViewsFrameLayout) convertView);
            }
            if (!isInCache && !isConnected) {
                requestBindService();
            } else {
                hasNewItems = this.mCache.queuePositionsToBePreloadedFromRequestedPosition(position);
            }
            if (isInCache) {
                View convertViewChild = null;
                int convertViewTypeId = 0;
                RemoteViewsFrameLayout layout = null;
                if (convertView instanceof RemoteViewsFrameLayout) {
                    layout = (RemoteViewsFrameLayout) convertView;
                    convertViewChild = layout.getChildAt(0);
                    convertViewTypeId = getConvertViewTypeId(convertViewChild);
                }
                Context context = parent.getContext();
                RemoteViews rv = this.mCache.getRemoteViewsAt(position);
                RemoteViewsIndexMetaData indexMetaData = this.mCache.getMetaDataAt(position);
                int typeId = indexMetaData.typeId;
                try {
                    if (layout != null) {
                        if (convertViewTypeId == typeId) {
                            rv.reapply(context, convertViewChild, this.mRemoteViewsOnClickHandler);
                            RemoteViewsFrameLayout remoteViewsFrameLayout = layout;
                            if (hasNewItems) {
                                loadNextIndexInBackground();
                            }
                            return remoteViewsFrameLayout;
                        }
                        layout.removeAllViews();
                    } else {
                        layout = new RemoteViewsFrameLayout(context);
                    }
                    View newView = rv.apply(context, parent, this.mRemoteViewsOnClickHandler);
                    newView.setTagInternal(R.id.rowTypeId, new Integer(typeId));
                    layout.addView(newView);
                    RemoteViewsFrameLayout remoteViewsFrameLayout2 = layout;
                    if (hasNewItems) {
                        loadNextIndexInBackground();
                    }
                    return remoteViewsFrameLayout2;
                } catch (Exception e) {
                    Log.w(TAG, "Error inflating RemoteViews at position: " + position + ", usingloading view instead" + e);
                    RemoteViewsMetaData metaData = this.mCache.getMetaData();
                    synchronized (metaData) {
                        RemoteViewsFrameLayout loadingView2 = metaData.createLoadingView(position, convertView, parent, this.mCache, this.mLayoutInflater, this.mRemoteViewsOnClickHandler);
                        if (hasNewItems) {
                            loadNextIndexInBackground();
                        }
                        return loadingView2;
                    }
                }
            }
            RemoteViewsMetaData metaData2 = this.mCache.getMetaData();
            synchronized (metaData2) {
                loadingView = metaData2.createLoadingView(position, convertView, parent, this.mCache, this.mLayoutInflater, this.mRemoteViewsOnClickHandler);
            }
            this.mRequestedViews.add(position, loadingView);
            this.mCache.queueRequestedPositionToLoad(position);
            loadNextIndexInBackground();
            return loadingView;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        int i;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            i = metaData.viewTypeCount;
        }
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        boolean z;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            z = metaData.hasStableIds;
        }
        return z;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean isEmpty() {
        return getCount() <= 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNotifyDataSetChanged() {
        int newCount;
        ArrayList<Integer> visibleWindow;
        IRemoteViewsFactory factory = this.mServiceConnection.getRemoteViewsFactory();
        try {
            factory.onDataSetChanged();
            synchronized (this.mCache) {
                this.mCache.reset();
            }
            updateTemporaryMetaData();
            synchronized (this.mCache.getTemporaryMetaData()) {
                newCount = this.mCache.getTemporaryMetaData().count;
                visibleWindow = getVisibleWindow(this.mVisibleWindowLowerBound, this.mVisibleWindowUpperBound, newCount);
            }
            Iterator i$ = visibleWindow.iterator();
            while (i$.hasNext()) {
                int i = i$.next().intValue();
                if (i < newCount) {
                    updateRemoteViews(i, false);
                }
            }
            this.mMainQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.5
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (RemoteViewsAdapter.this.mCache) {
                        RemoteViewsAdapter.this.mCache.commitTemporaryMetaData();
                    }
                    RemoteViewsAdapter.this.superNotifyDataSetChanged();
                    RemoteViewsAdapter.this.enqueueDeferredUnbindServiceMessage();
                }
            });
            this.mNotifyDataSetChangedAfterOnServiceConnected = false;
        } catch (RemoteException e) {
            Log.e(TAG, "Error in updateNotifyDataSetChanged(): " + e.getMessage());
        } catch (RuntimeException e2) {
            Log.e(TAG, "Error in updateNotifyDataSetChanged(): " + e2.getMessage());
        }
    }

    private ArrayList<Integer> getVisibleWindow(int lower, int upper, int count) {
        ArrayList<Integer> window = new ArrayList<>();
        if ((lower == 0 && upper == 0) || lower < 0 || upper < 0) {
            return window;
        }
        if (lower <= upper) {
            for (int i = lower; i <= upper; i++) {
                window.add(Integer.valueOf(i));
            }
        } else {
            for (int i2 = lower; i2 < count; i2++) {
                window.add(Integer.valueOf(i2));
            }
            for (int i3 = 0; i3 <= upper; i3++) {
                window.add(Integer.valueOf(i3));
            }
        }
        return window;
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        this.mMainQueue.removeMessages(1);
        if (!this.mServiceConnection.isConnected()) {
            if (this.mNotifyDataSetChangedAfterOnServiceConnected) {
                return;
            }
            this.mNotifyDataSetChangedAfterOnServiceConnected = true;
            requestBindService();
            return;
        }
        this.mWorkerQueue.post(new Runnable() { // from class: android.widget.RemoteViewsAdapter.6
            @Override // java.lang.Runnable
            public void run() {
                RemoteViewsAdapter.this.onNotifyDataSetChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        boolean result = false;
        switch (msg.what) {
            case 1:
                if (this.mServiceConnection.isConnected()) {
                    this.mServiceConnection.unbind(this.mContext, this.mAppWidgetId, this.mIntent);
                }
                result = true;
                break;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enqueueDeferredUnbindServiceMessage() {
        this.mMainQueue.removeMessages(1);
        this.mMainQueue.sendEmptyMessageDelayed(1, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
    }

    private boolean requestBindService() {
        if (!this.mServiceConnection.isConnected()) {
            this.mServiceConnection.bind(this.mContext, this.mAppWidgetId, this.mIntent);
        }
        this.mMainQueue.removeMessages(1);
        return this.mServiceConnection.isConnected();
    }
}