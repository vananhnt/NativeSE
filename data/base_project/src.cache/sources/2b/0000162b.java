package android.view.accessibility;

import android.os.Parcelable;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/* loaded from: AccessibilityRecord.class */
public class AccessibilityRecord {
    private static final int UNDEFINED = -1;
    private static final int PROPERTY_CHECKED = 1;
    private static final int PROPERTY_ENABLED = 2;
    private static final int PROPERTY_PASSWORD = 4;
    private static final int PROPERTY_FULL_SCREEN = 128;
    private static final int PROPERTY_SCROLLABLE = 256;
    private static final int PROPERTY_IMPORTANT_FOR_ACCESSIBILITY = 512;
    private static final int GET_SOURCE_PREFETCH_FLAGS = 7;
    private static final int MAX_POOL_SIZE = 10;
    private static final Object sPoolLock = new Object();
    private static AccessibilityRecord sPool;
    private static int sPoolSize;
    private AccessibilityRecord mNext;
    private boolean mIsInPool;
    boolean mSealed;
    CharSequence mClassName;
    CharSequence mContentDescription;
    CharSequence mBeforeText;
    Parcelable mParcelableData;
    int mBooleanProperties = 512;
    int mCurrentItemIndex = -1;
    int mItemCount = -1;
    int mFromIndex = -1;
    int mToIndex = -1;
    int mScrollX = -1;
    int mScrollY = -1;
    int mMaxScrollX = -1;
    int mMaxScrollY = -1;
    int mAddedCount = -1;
    int mRemovedCount = -1;
    long mSourceNodeId = AccessibilityNodeInfo.makeNodeId(-1, -1);
    int mSourceWindowId = -1;
    final List<CharSequence> mText = new ArrayList();
    int mConnectionId = -1;

    public void setSource(View source) {
        setSource(source, -1);
    }

    public void setSource(View root, int virtualDescendantId) {
        boolean important;
        enforceNotSealed();
        if (virtualDescendantId == -1) {
            important = root != null ? root.isImportantForAccessibility() : true;
        } else {
            important = true;
        }
        setBooleanProperty(512, important);
        this.mSourceWindowId = root != null ? root.getAccessibilityWindowId() : -1;
        int rootViewId = root != null ? root.getAccessibilityViewId() : -1;
        this.mSourceNodeId = AccessibilityNodeInfo.makeNodeId(rootViewId, virtualDescendantId);
    }

    public AccessibilityNodeInfo getSource() {
        enforceSealed();
        if (this.mConnectionId == -1 || this.mSourceWindowId == -1 || AccessibilityNodeInfo.getAccessibilityViewId(this.mSourceNodeId) == -1) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mSourceWindowId, this.mSourceNodeId, false, 7);
    }

    public void setWindowId(int windowId) {
        this.mSourceWindowId = windowId;
    }

    public int getWindowId() {
        return this.mSourceWindowId;
    }

    public boolean isChecked() {
        return getBooleanProperty(1);
    }

    public void setChecked(boolean isChecked) {
        enforceNotSealed();
        setBooleanProperty(1, isChecked);
    }

    public boolean isEnabled() {
        return getBooleanProperty(2);
    }

    public void setEnabled(boolean isEnabled) {
        enforceNotSealed();
        setBooleanProperty(2, isEnabled);
    }

    public boolean isPassword() {
        return getBooleanProperty(4);
    }

    public void setPassword(boolean isPassword) {
        enforceNotSealed();
        setBooleanProperty(4, isPassword);
    }

    public boolean isFullScreen() {
        return getBooleanProperty(128);
    }

    public void setFullScreen(boolean isFullScreen) {
        enforceNotSealed();
        setBooleanProperty(128, isFullScreen);
    }

    public boolean isScrollable() {
        return getBooleanProperty(256);
    }

    public void setScrollable(boolean scrollable) {
        enforceNotSealed();
        setBooleanProperty(256, scrollable);
    }

    public boolean isImportantForAccessibility() {
        return getBooleanProperty(512);
    }

    public int getItemCount() {
        return this.mItemCount;
    }

    public void setItemCount(int itemCount) {
        enforceNotSealed();
        this.mItemCount = itemCount;
    }

    public int getCurrentItemIndex() {
        return this.mCurrentItemIndex;
    }

    public void setCurrentItemIndex(int currentItemIndex) {
        enforceNotSealed();
        this.mCurrentItemIndex = currentItemIndex;
    }

    public int getFromIndex() {
        return this.mFromIndex;
    }

    public void setFromIndex(int fromIndex) {
        enforceNotSealed();
        this.mFromIndex = fromIndex;
    }

    public int getToIndex() {
        return this.mToIndex;
    }

    public void setToIndex(int toIndex) {
        enforceNotSealed();
        this.mToIndex = toIndex;
    }

    public int getScrollX() {
        return this.mScrollX;
    }

    public void setScrollX(int scrollX) {
        enforceNotSealed();
        this.mScrollX = scrollX;
    }

    public int getScrollY() {
        return this.mScrollY;
    }

    public void setScrollY(int scrollY) {
        enforceNotSealed();
        this.mScrollY = scrollY;
    }

    public int getMaxScrollX() {
        return this.mMaxScrollX;
    }

    public void setMaxScrollX(int maxScrollX) {
        enforceNotSealed();
        this.mMaxScrollX = maxScrollX;
    }

    public int getMaxScrollY() {
        return this.mMaxScrollY;
    }

    public void setMaxScrollY(int maxScrollY) {
        enforceNotSealed();
        this.mMaxScrollY = maxScrollY;
    }

    public int getAddedCount() {
        return this.mAddedCount;
    }

    public void setAddedCount(int addedCount) {
        enforceNotSealed();
        this.mAddedCount = addedCount;
    }

    public int getRemovedCount() {
        return this.mRemovedCount;
    }

    public void setRemovedCount(int removedCount) {
        enforceNotSealed();
        this.mRemovedCount = removedCount;
    }

    public CharSequence getClassName() {
        return this.mClassName;
    }

    public void setClassName(CharSequence className) {
        enforceNotSealed();
        this.mClassName = className;
    }

    public List<CharSequence> getText() {
        return this.mText;
    }

    public CharSequence getBeforeText() {
        return this.mBeforeText;
    }

    public void setBeforeText(CharSequence beforeText) {
        enforceNotSealed();
        this.mBeforeText = beforeText;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public void setContentDescription(CharSequence contentDescription) {
        enforceNotSealed();
        this.mContentDescription = contentDescription;
    }

    public Parcelable getParcelableData() {
        return this.mParcelableData;
    }

    public void setParcelableData(Parcelable parcelableData) {
        enforceNotSealed();
        this.mParcelableData = parcelableData;
    }

    public long getSourceNodeId() {
        return this.mSourceNodeId;
    }

    public void setConnectionId(int connectionId) {
        enforceNotSealed();
        this.mConnectionId = connectionId;
    }

    public void setSealed(boolean sealed) {
        this.mSealed = sealed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSealed() {
        return this.mSealed;
    }

    void enforceSealed() {
        if (!isSealed()) {
            throw new IllegalStateException("Cannot perform this action on a not sealed instance.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enforceNotSealed() {
        if (isSealed()) {
            throw new IllegalStateException("Cannot perform this action on a sealed instance.");
        }
    }

    private boolean getBooleanProperty(int property) {
        return (this.mBooleanProperties & property) == property;
    }

    private void setBooleanProperty(int property, boolean value) {
        if (value) {
            this.mBooleanProperties |= property;
        } else {
            this.mBooleanProperties &= property ^ (-1);
        }
    }

    public static AccessibilityRecord obtain(AccessibilityRecord record) {
        AccessibilityRecord clone = obtain();
        clone.init(record);
        return clone;
    }

    public static AccessibilityRecord obtain() {
        synchronized (sPoolLock) {
            if (sPool != null) {
                AccessibilityRecord record = sPool;
                sPool = sPool.mNext;
                sPoolSize--;
                record.mNext = null;
                record.mIsInPool = false;
                return record;
            }
            return new AccessibilityRecord();
        }
    }

    public void recycle() {
        if (this.mIsInPool) {
            throw new IllegalStateException("Record already recycled!");
        }
        clear();
        synchronized (sPoolLock) {
            if (sPoolSize <= 10) {
                this.mNext = sPool;
                sPool = this;
                this.mIsInPool = true;
                sPoolSize++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(AccessibilityRecord record) {
        this.mSealed = record.mSealed;
        this.mBooleanProperties = record.mBooleanProperties;
        this.mCurrentItemIndex = record.mCurrentItemIndex;
        this.mItemCount = record.mItemCount;
        this.mFromIndex = record.mFromIndex;
        this.mToIndex = record.mToIndex;
        this.mScrollX = record.mScrollX;
        this.mScrollY = record.mScrollY;
        this.mMaxScrollX = record.mMaxScrollX;
        this.mMaxScrollY = record.mMaxScrollY;
        this.mAddedCount = record.mAddedCount;
        this.mRemovedCount = record.mRemovedCount;
        this.mClassName = record.mClassName;
        this.mContentDescription = record.mContentDescription;
        this.mBeforeText = record.mBeforeText;
        this.mParcelableData = record.mParcelableData;
        this.mText.addAll(record.mText);
        this.mSourceWindowId = record.mSourceWindowId;
        this.mSourceNodeId = record.mSourceNodeId;
        this.mConnectionId = record.mConnectionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.mSealed = false;
        this.mBooleanProperties = 512;
        this.mCurrentItemIndex = -1;
        this.mItemCount = -1;
        this.mFromIndex = -1;
        this.mToIndex = -1;
        this.mScrollX = -1;
        this.mScrollY = -1;
        this.mMaxScrollX = -1;
        this.mMaxScrollY = -1;
        this.mAddedCount = -1;
        this.mRemovedCount = -1;
        this.mClassName = null;
        this.mContentDescription = null;
        this.mBeforeText = null;
        this.mParcelableData = null;
        this.mText.clear();
        this.mSourceNodeId = AccessibilityNodeInfo.makeNodeId(-1, -1);
        this.mSourceWindowId = -1;
        this.mConnectionId = -1;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" [ ClassName: " + ((Object) this.mClassName));
        builder.append("; Text: " + this.mText);
        builder.append("; ContentDescription: " + ((Object) this.mContentDescription));
        builder.append("; ItemCount: " + this.mItemCount);
        builder.append("; CurrentItemIndex: " + this.mCurrentItemIndex);
        builder.append("; IsEnabled: " + getBooleanProperty(2));
        builder.append("; IsPassword: " + getBooleanProperty(4));
        builder.append("; IsChecked: " + getBooleanProperty(1));
        builder.append("; IsFullScreen: " + getBooleanProperty(128));
        builder.append("; Scrollable: " + getBooleanProperty(256));
        builder.append("; BeforeText: " + ((Object) this.mBeforeText));
        builder.append("; FromIndex: " + this.mFromIndex);
        builder.append("; ToIndex: " + this.mToIndex);
        builder.append("; ScrollX: " + this.mScrollX);
        builder.append("; ScrollY: " + this.mScrollY);
        builder.append("; MaxScrollX: " + this.mMaxScrollX);
        builder.append("; MaxScrollY: " + this.mMaxScrollY);
        builder.append("; AddedCount: " + this.mAddedCount);
        builder.append("; RemovedCount: " + this.mRemovedCount);
        builder.append("; ParcelableData: " + this.mParcelableData);
        builder.append(" ]");
        return builder.toString();
    }
}