package android.view;

import android.graphics.Rect;
import android.graphics.Region;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: ViewTreeObserver.class */
public final class ViewTreeObserver {
    private CopyOnWriteArrayList<OnWindowFocusChangeListener> mOnWindowFocusListeners;
    private CopyOnWriteArrayList<OnWindowAttachListener> mOnWindowAttachListeners;
    private CopyOnWriteArrayList<OnGlobalFocusChangeListener> mOnGlobalFocusListeners;
    private CopyOnWriteArrayList<OnTouchModeChangeListener> mOnTouchModeChangeListeners;
    private CopyOnWriteArray<OnGlobalLayoutListener> mOnGlobalLayoutListeners;
    private CopyOnWriteArray<OnComputeInternalInsetsListener> mOnComputeInternalInsetsListeners;
    private CopyOnWriteArray<OnScrollChangedListener> mOnScrollChangedListeners;
    private CopyOnWriteArray<OnPreDrawListener> mOnPreDrawListeners;
    private ArrayList<OnDrawListener> mOnDrawListeners;
    private boolean mAlive = true;

    /* loaded from: ViewTreeObserver$OnComputeInternalInsetsListener.class */
    public interface OnComputeInternalInsetsListener {
        void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo);
    }

    /* loaded from: ViewTreeObserver$OnDrawListener.class */
    public interface OnDrawListener {
        void onDraw();
    }

    /* loaded from: ViewTreeObserver$OnGlobalFocusChangeListener.class */
    public interface OnGlobalFocusChangeListener {
        void onGlobalFocusChanged(View view, View view2);
    }

    /* loaded from: ViewTreeObserver$OnGlobalLayoutListener.class */
    public interface OnGlobalLayoutListener {
        void onGlobalLayout();
    }

    /* loaded from: ViewTreeObserver$OnPreDrawListener.class */
    public interface OnPreDrawListener {
        boolean onPreDraw();
    }

    /* loaded from: ViewTreeObserver$OnScrollChangedListener.class */
    public interface OnScrollChangedListener {
        void onScrollChanged();
    }

    /* loaded from: ViewTreeObserver$OnTouchModeChangeListener.class */
    public interface OnTouchModeChangeListener {
        void onTouchModeChanged(boolean z);
    }

    /* loaded from: ViewTreeObserver$OnWindowAttachListener.class */
    public interface OnWindowAttachListener {
        void onWindowAttached();

        void onWindowDetached();
    }

    /* loaded from: ViewTreeObserver$OnWindowFocusChangeListener.class */
    public interface OnWindowFocusChangeListener {
        void onWindowFocusChanged(boolean z);
    }

    /* loaded from: ViewTreeObserver$InternalInsetsInfo.class */
    public static final class InternalInsetsInfo {
        public final Rect contentInsets = new Rect();
        public final Rect visibleInsets = new Rect();
        public final Region touchableRegion = new Region();
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_VISIBLE = 2;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        int mTouchableInsets;

        public void setTouchableInsets(int val) {
            this.mTouchableInsets = val;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void reset() {
            this.contentInsets.setEmpty();
            this.visibleInsets.setEmpty();
            this.touchableRegion.setEmpty();
            this.mTouchableInsets = 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isEmpty() {
            return this.contentInsets.isEmpty() && this.visibleInsets.isEmpty() && this.touchableRegion.isEmpty() && this.mTouchableInsets == 0;
        }

        public int hashCode() {
            int result = this.contentInsets.hashCode();
            return (31 * ((31 * ((31 * result) + this.visibleInsets.hashCode())) + this.touchableRegion.hashCode())) + this.mTouchableInsets;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            InternalInsetsInfo other = (InternalInsetsInfo) o;
            return this.mTouchableInsets == other.mTouchableInsets && this.contentInsets.equals(other.contentInsets) && this.visibleInsets.equals(other.visibleInsets) && this.touchableRegion.equals(other.touchableRegion);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void set(InternalInsetsInfo other) {
            this.contentInsets.set(other.contentInsets);
            this.visibleInsets.set(other.visibleInsets);
            this.touchableRegion.set(other.touchableRegion);
            this.mTouchableInsets = other.mTouchableInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void merge(ViewTreeObserver observer) {
        if (observer.mOnWindowAttachListeners != null) {
            if (this.mOnWindowAttachListeners != null) {
                this.mOnWindowAttachListeners.addAll(observer.mOnWindowAttachListeners);
            } else {
                this.mOnWindowAttachListeners = observer.mOnWindowAttachListeners;
            }
        }
        if (observer.mOnWindowFocusListeners != null) {
            if (this.mOnWindowFocusListeners != null) {
                this.mOnWindowFocusListeners.addAll(observer.mOnWindowFocusListeners);
            } else {
                this.mOnWindowFocusListeners = observer.mOnWindowFocusListeners;
            }
        }
        if (observer.mOnGlobalFocusListeners != null) {
            if (this.mOnGlobalFocusListeners != null) {
                this.mOnGlobalFocusListeners.addAll(observer.mOnGlobalFocusListeners);
            } else {
                this.mOnGlobalFocusListeners = observer.mOnGlobalFocusListeners;
            }
        }
        if (observer.mOnGlobalLayoutListeners != null) {
            if (this.mOnGlobalLayoutListeners != null) {
                this.mOnGlobalLayoutListeners.addAll(observer.mOnGlobalLayoutListeners);
            } else {
                this.mOnGlobalLayoutListeners = observer.mOnGlobalLayoutListeners;
            }
        }
        if (observer.mOnPreDrawListeners != null) {
            if (this.mOnPreDrawListeners != null) {
                this.mOnPreDrawListeners.addAll(observer.mOnPreDrawListeners);
            } else {
                this.mOnPreDrawListeners = observer.mOnPreDrawListeners;
            }
        }
        if (observer.mOnTouchModeChangeListeners != null) {
            if (this.mOnTouchModeChangeListeners != null) {
                this.mOnTouchModeChangeListeners.addAll(observer.mOnTouchModeChangeListeners);
            } else {
                this.mOnTouchModeChangeListeners = observer.mOnTouchModeChangeListeners;
            }
        }
        if (observer.mOnComputeInternalInsetsListeners != null) {
            if (this.mOnComputeInternalInsetsListeners != null) {
                this.mOnComputeInternalInsetsListeners.addAll(observer.mOnComputeInternalInsetsListeners);
            } else {
                this.mOnComputeInternalInsetsListeners = observer.mOnComputeInternalInsetsListeners;
            }
        }
        if (observer.mOnScrollChangedListeners != null) {
            if (this.mOnScrollChangedListeners != null) {
                this.mOnScrollChangedListeners.addAll(observer.mOnScrollChangedListeners);
            } else {
                this.mOnScrollChangedListeners = observer.mOnScrollChangedListeners;
            }
        }
        observer.kill();
    }

    public void addOnWindowAttachListener(OnWindowAttachListener listener) {
        checkIsAlive();
        if (this.mOnWindowAttachListeners == null) {
            this.mOnWindowAttachListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowAttachListeners.add(listener);
    }

    public void removeOnWindowAttachListener(OnWindowAttachListener victim) {
        checkIsAlive();
        if (this.mOnWindowAttachListeners == null) {
            return;
        }
        this.mOnWindowAttachListeners.remove(victim);
    }

    public void addOnWindowFocusChangeListener(OnWindowFocusChangeListener listener) {
        checkIsAlive();
        if (this.mOnWindowFocusListeners == null) {
            this.mOnWindowFocusListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowFocusListeners.add(listener);
    }

    public void removeOnWindowFocusChangeListener(OnWindowFocusChangeListener victim) {
        checkIsAlive();
        if (this.mOnWindowFocusListeners == null) {
            return;
        }
        this.mOnWindowFocusListeners.remove(victim);
    }

    public void addOnGlobalFocusChangeListener(OnGlobalFocusChangeListener listener) {
        checkIsAlive();
        if (this.mOnGlobalFocusListeners == null) {
            this.mOnGlobalFocusListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnGlobalFocusListeners.add(listener);
    }

    public void removeOnGlobalFocusChangeListener(OnGlobalFocusChangeListener victim) {
        checkIsAlive();
        if (this.mOnGlobalFocusListeners == null) {
            return;
        }
        this.mOnGlobalFocusListeners.remove(victim);
    }

    public void addOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        checkIsAlive();
        if (this.mOnGlobalLayoutListeners == null) {
            this.mOnGlobalLayoutListeners = new CopyOnWriteArray<>();
        }
        this.mOnGlobalLayoutListeners.add(listener);
    }

    @Deprecated
    public void removeGlobalOnLayoutListener(OnGlobalLayoutListener victim) {
        removeOnGlobalLayoutListener(victim);
    }

    public void removeOnGlobalLayoutListener(OnGlobalLayoutListener victim) {
        checkIsAlive();
        if (this.mOnGlobalLayoutListeners == null) {
            return;
        }
        this.mOnGlobalLayoutListeners.remove(victim);
    }

    public void addOnPreDrawListener(OnPreDrawListener listener) {
        checkIsAlive();
        if (this.mOnPreDrawListeners == null) {
            this.mOnPreDrawListeners = new CopyOnWriteArray<>();
        }
        this.mOnPreDrawListeners.add(listener);
    }

    public void removeOnPreDrawListener(OnPreDrawListener victim) {
        checkIsAlive();
        if (this.mOnPreDrawListeners == null) {
            return;
        }
        this.mOnPreDrawListeners.remove(victim);
    }

    public void addOnDrawListener(OnDrawListener listener) {
        checkIsAlive();
        if (this.mOnDrawListeners == null) {
            this.mOnDrawListeners = new ArrayList<>();
        }
        this.mOnDrawListeners.add(listener);
    }

    public void removeOnDrawListener(OnDrawListener victim) {
        checkIsAlive();
        if (this.mOnDrawListeners == null) {
            return;
        }
        this.mOnDrawListeners.remove(victim);
    }

    public void addOnScrollChangedListener(OnScrollChangedListener listener) {
        checkIsAlive();
        if (this.mOnScrollChangedListeners == null) {
            this.mOnScrollChangedListeners = new CopyOnWriteArray<>();
        }
        this.mOnScrollChangedListeners.add(listener);
    }

    public void removeOnScrollChangedListener(OnScrollChangedListener victim) {
        checkIsAlive();
        if (this.mOnScrollChangedListeners == null) {
            return;
        }
        this.mOnScrollChangedListeners.remove(victim);
    }

    public void addOnTouchModeChangeListener(OnTouchModeChangeListener listener) {
        checkIsAlive();
        if (this.mOnTouchModeChangeListeners == null) {
            this.mOnTouchModeChangeListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnTouchModeChangeListeners.add(listener);
    }

    public void removeOnTouchModeChangeListener(OnTouchModeChangeListener victim) {
        checkIsAlive();
        if (this.mOnTouchModeChangeListeners == null) {
            return;
        }
        this.mOnTouchModeChangeListeners.remove(victim);
    }

    public void addOnComputeInternalInsetsListener(OnComputeInternalInsetsListener listener) {
        checkIsAlive();
        if (this.mOnComputeInternalInsetsListeners == null) {
            this.mOnComputeInternalInsetsListeners = new CopyOnWriteArray<>();
        }
        this.mOnComputeInternalInsetsListeners.add(listener);
    }

    public void removeOnComputeInternalInsetsListener(OnComputeInternalInsetsListener victim) {
        checkIsAlive();
        if (this.mOnComputeInternalInsetsListeners == null) {
            return;
        }
        this.mOnComputeInternalInsetsListeners.remove(victim);
    }

    private void checkIsAlive() {
        if (!this.mAlive) {
            throw new IllegalStateException("This ViewTreeObserver is not alive, call getViewTreeObserver() again");
        }
    }

    public boolean isAlive() {
        return this.mAlive;
    }

    private void kill() {
        this.mAlive = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnWindowAttachedChange(boolean attached) {
        CopyOnWriteArrayList<OnWindowAttachListener> listeners = this.mOnWindowAttachListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                OnWindowAttachListener listener = i$.next();
                if (attached) {
                    listener.onWindowAttached();
                } else {
                    listener.onWindowDetached();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnWindowFocusChange(boolean hasFocus) {
        CopyOnWriteArrayList<OnWindowFocusChangeListener> listeners = this.mOnWindowFocusListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                OnWindowFocusChangeListener listener = i$.next();
                listener.onWindowFocusChanged(hasFocus);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnGlobalFocusChange(View oldFocus, View newFocus) {
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> listeners = this.mOnGlobalFocusListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                OnGlobalFocusChangeListener listener = i$.next();
                listener.onGlobalFocusChanged(oldFocus, newFocus);
            }
        }
    }

    public final void dispatchOnGlobalLayout() {
        CopyOnWriteArray<OnGlobalLayoutListener> listeners = this.mOnGlobalLayoutListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnGlobalLayoutListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onGlobalLayout();
                }
            } finally {
                listeners.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasOnPreDrawListeners() {
        return this.mOnPreDrawListeners != null && this.mOnPreDrawListeners.size() > 0;
    }

    public final boolean dispatchOnPreDraw() {
        boolean cancelDraw = false;
        CopyOnWriteArray<OnPreDrawListener> listeners = this.mOnPreDrawListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnPreDrawListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    cancelDraw |= !access.get(i).onPreDraw();
                }
            } finally {
                listeners.end();
            }
        }
        return cancelDraw;
    }

    public final void dispatchOnDraw() {
        if (this.mOnDrawListeners != null) {
            ArrayList<OnDrawListener> listeners = this.mOnDrawListeners;
            int numListeners = listeners.size();
            for (int i = 0; i < numListeners; i++) {
                listeners.get(i).onDraw();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnTouchModeChanged(boolean inTouchMode) {
        CopyOnWriteArrayList<OnTouchModeChangeListener> listeners = this.mOnTouchModeChangeListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                OnTouchModeChangeListener listener = i$.next();
                listener.onTouchModeChanged(inTouchMode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnScrollChanged() {
        CopyOnWriteArray<OnScrollChangedListener> listeners = this.mOnScrollChangedListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnScrollChangedListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onScrollChanged();
                }
            } finally {
                listeners.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasComputeInternalInsetsListeners() {
        CopyOnWriteArray<OnComputeInternalInsetsListener> listeners = this.mOnComputeInternalInsetsListeners;
        return listeners != null && listeners.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnComputeInternalInsets(InternalInsetsInfo inoutInfo) {
        CopyOnWriteArray<OnComputeInternalInsetsListener> listeners = this.mOnComputeInternalInsetsListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnComputeInternalInsetsListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onComputeInternalInsets(inoutInfo);
                }
            } finally {
                listeners.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewTreeObserver$CopyOnWriteArray.class */
    public static class CopyOnWriteArray<T> {
        private ArrayList<T> mDataCopy;
        private boolean mStart;
        private ArrayList<T> mData = new ArrayList<>();
        private final Access<T> mAccess = new Access<>();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ViewTreeObserver$CopyOnWriteArray$Access.class */
        public static class Access<T> {
            private ArrayList<T> mData;
            private int mSize;

            Access() {
            }

            T get(int index) {
                return this.mData.get(index);
            }

            int size() {
                return this.mSize;
            }
        }

        CopyOnWriteArray() {
        }

        private ArrayList<T> getArray() {
            if (this.mStart) {
                if (this.mDataCopy == null) {
                    this.mDataCopy = new ArrayList<>(this.mData);
                }
                return this.mDataCopy;
            }
            return this.mData;
        }

        Access<T> start() {
            if (this.mStart) {
                throw new IllegalStateException("Iteration already started");
            }
            this.mStart = true;
            this.mDataCopy = null;
            ((Access) this.mAccess).mData = this.mData;
            ((Access) this.mAccess).mSize = this.mData.size();
            return this.mAccess;
        }

        void end() {
            if (!this.mStart) {
                throw new IllegalStateException("Iteration not started");
            }
            this.mStart = false;
            if (this.mDataCopy != null) {
                this.mData = this.mDataCopy;
                ((Access) this.mAccess).mData.clear();
                ((Access) this.mAccess).mSize = 0;
            }
            this.mDataCopy = null;
        }

        int size() {
            return getArray().size();
        }

        void add(T item) {
            getArray().add(item);
        }

        void addAll(CopyOnWriteArray<T> array) {
            getArray().addAll(array.mData);
        }

        void remove(T item) {
            getArray().remove(item);
        }

        void clear() {
            getArray().clear();
        }
    }
}