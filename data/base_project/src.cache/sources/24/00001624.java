package android.view.accessibility;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pools;
import android.util.SparseLongArray;
import android.view.View;
import java.util.Collections;
import java.util.List;

/* loaded from: AccessibilityNodeInfo.class */
public class AccessibilityNodeInfo implements Parcelable {
    private static final boolean DEBUG = false;
    public static final int UNDEFINED = -1;
    public static final int ACTIVE_WINDOW_ID = -1;
    public static final int FLAG_PREFETCH_PREDECESSORS = 1;
    public static final int FLAG_PREFETCH_SIBLINGS = 2;
    public static final int FLAG_PREFETCH_DESCENDANTS = 4;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 8;
    public static final int FLAG_REPORT_VIEW_IDS = 16;
    public static final int ACTION_FOCUS = 1;
    public static final int ACTION_CLEAR_FOCUS = 2;
    public static final int ACTION_SELECT = 4;
    public static final int ACTION_CLEAR_SELECTION = 8;
    public static final int ACTION_CLICK = 16;
    public static final int ACTION_LONG_CLICK = 32;
    public static final int ACTION_ACCESSIBILITY_FOCUS = 64;
    public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128;
    public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256;
    public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512;
    public static final int ACTION_NEXT_HTML_ELEMENT = 1024;
    public static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048;
    public static final int ACTION_SCROLL_FORWARD = 4096;
    public static final int ACTION_SCROLL_BACKWARD = 8192;
    public static final int ACTION_COPY = 16384;
    public static final int ACTION_PASTE = 32768;
    public static final int ACTION_CUT = 65536;
    public static final int ACTION_SET_SELECTION = 131072;
    public static final int ACTION_EXPAND = 262144;
    public static final int ACTION_COLLAPSE = 524288;
    public static final int ACTION_DISMISS = 1048576;
    public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";
    public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING";
    public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";
    public static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT";
    public static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT";
    public static final int FOCUS_INPUT = 1;
    public static final int FOCUS_ACCESSIBILITY = 2;
    public static final int MOVEMENT_GRANULARITY_CHARACTER = 1;
    public static final int MOVEMENT_GRANULARITY_WORD = 2;
    public static final int MOVEMENT_GRANULARITY_LINE = 4;
    public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8;
    public static final int MOVEMENT_GRANULARITY_PAGE = 16;
    private static final int BOOLEAN_PROPERTY_CHECKABLE = 1;
    private static final int BOOLEAN_PROPERTY_CHECKED = 2;
    private static final int BOOLEAN_PROPERTY_FOCUSABLE = 4;
    private static final int BOOLEAN_PROPERTY_FOCUSED = 8;
    private static final int BOOLEAN_PROPERTY_SELECTED = 16;
    private static final int BOOLEAN_PROPERTY_CLICKABLE = 32;
    private static final int BOOLEAN_PROPERTY_LONG_CLICKABLE = 64;
    private static final int BOOLEAN_PROPERTY_ENABLED = 128;
    private static final int BOOLEAN_PROPERTY_PASSWORD = 256;
    private static final int BOOLEAN_PROPERTY_SCROLLABLE = 512;
    private static final int BOOLEAN_PROPERTY_ACCESSIBILITY_FOCUSED = 1024;
    private static final int BOOLEAN_PROPERTY_VISIBLE_TO_USER = 2048;
    private static final int BOOLEAN_PROPERTY_EDITABLE = 4096;
    private static final int BOOLEAN_PROPERTY_OPENS_POPUP = 8192;
    private static final int BOOLEAN_PROPERTY_DISMISSABLE = 16384;
    private static final int BOOLEAN_PROPERTY_MULTI_LINE = 32768;
    private static final int BOOLEAN_PROPERTY_CONTENT_INVALID = 65536;
    private static final long VIRTUAL_DESCENDANT_ID_MASK = -4294967296L;
    private static final int VIRTUAL_DESCENDANT_ID_SHIFT = 32;
    private static final int MAX_POOL_SIZE = 50;
    private boolean mSealed;
    private int mBooleanProperties;
    private CharSequence mPackageName;
    private CharSequence mClassName;
    private CharSequence mText;
    private CharSequence mContentDescription;
    private String mViewIdResourceName;
    private int mActions;
    private int mMovementGranularities;
    private Bundle mExtras;
    private RangeInfo mRangeInfo;
    private CollectionInfo mCollectionInfo;
    private CollectionItemInfo mCollectionItemInfo;
    public static final long ROOT_NODE_ID = makeNodeId(-1, -1);
    private static final Pools.SynchronizedPool<AccessibilityNodeInfo> sPool = new Pools.SynchronizedPool<>(50);
    public static final Parcelable.Creator<AccessibilityNodeInfo> CREATOR = new Parcelable.Creator<AccessibilityNodeInfo>() { // from class: android.view.accessibility.AccessibilityNodeInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccessibilityNodeInfo createFromParcel(Parcel parcel) {
            AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain();
            info.initFromParcel(parcel);
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccessibilityNodeInfo[] newArray(int size) {
            return new AccessibilityNodeInfo[size];
        }
    };
    private int mWindowId = -1;
    private long mSourceNodeId = ROOT_NODE_ID;
    private long mParentNodeId = ROOT_NODE_ID;
    private long mLabelForId = ROOT_NODE_ID;
    private long mLabeledById = ROOT_NODE_ID;
    private final Rect mBoundsInParent = new Rect();
    private final Rect mBoundsInScreen = new Rect();
    private final SparseLongArray mChildNodeIds = new SparseLongArray();
    private int mTextSelectionStart = -1;
    private int mTextSelectionEnd = -1;
    private int mInputType = 0;
    private int mLiveRegion = 0;
    private int mConnectionId = -1;

    public static int getAccessibilityViewId(long accessibilityNodeId) {
        return (int) accessibilityNodeId;
    }

    public static int getVirtualDescendantId(long accessibilityNodeId) {
        return (int) ((accessibilityNodeId & VIRTUAL_DESCENDANT_ID_MASK) >> 32);
    }

    public static long makeNodeId(int accessibilityViewId, int virtualDescendantId) {
        return (virtualDescendantId << 32) | accessibilityViewId;
    }

    private AccessibilityNodeInfo() {
    }

    public void setSource(View source) {
        setSource(source, -1);
    }

    public void setSource(View root, int virtualDescendantId) {
        enforceNotSealed();
        this.mWindowId = root != null ? root.getAccessibilityWindowId() : -1;
        int rootAccessibilityViewId = root != null ? root.getAccessibilityViewId() : -1;
        this.mSourceNodeId = makeNodeId(rootAccessibilityViewId, virtualDescendantId);
    }

    public AccessibilityNodeInfo findFocus(int focus) {
        enforceSealed();
        enforceValidFocusType(focus);
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().findFocus(this.mConnectionId, this.mWindowId, this.mSourceNodeId, focus);
    }

    public AccessibilityNodeInfo focusSearch(int direction) {
        enforceSealed();
        enforceValidFocusDirection(direction);
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().focusSearch(this.mConnectionId, this.mWindowId, this.mSourceNodeId, direction);
    }

    public int getWindowId() {
        return this.mWindowId;
    }

    public boolean refresh(boolean bypassCache) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return false;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        AccessibilityNodeInfo refreshedInfo = client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mSourceNodeId, bypassCache, 0);
        if (refreshedInfo == null) {
            return false;
        }
        init(refreshedInfo);
        refreshedInfo.recycle();
        return true;
    }

    public boolean refresh() {
        return refresh(false);
    }

    public SparseLongArray getChildNodeIds() {
        return this.mChildNodeIds;
    }

    public int getChildCount() {
        return this.mChildNodeIds.size();
    }

    public AccessibilityNodeInfo getChild(int index) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return null;
        }
        long childId = this.mChildNodeIds.get(index);
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, childId, false, 4);
    }

    public void addChild(View child) {
        addChild(child, -1);
    }

    public void addChild(View root, int virtualDescendantId) {
        enforceNotSealed();
        int index = this.mChildNodeIds.size();
        int rootAccessibilityViewId = root != null ? root.getAccessibilityViewId() : -1;
        long childNodeId = makeNodeId(rootAccessibilityViewId, virtualDescendantId);
        this.mChildNodeIds.put(index, childNodeId);
    }

    public int getActions() {
        return this.mActions;
    }

    public void addAction(int action) {
        enforceNotSealed();
        this.mActions |= action;
    }

    public void setMovementGranularities(int granularities) {
        enforceNotSealed();
        this.mMovementGranularities = granularities;
    }

    public int getMovementGranularities() {
        return this.mMovementGranularities;
    }

    public boolean performAction(int action) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return false;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.performAccessibilityAction(this.mConnectionId, this.mWindowId, this.mSourceNodeId, action, null);
    }

    public boolean performAction(int action, Bundle arguments) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return false;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.performAccessibilityAction(this.mConnectionId, this.mWindowId, this.mSourceNodeId, action, arguments);
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return Collections.emptyList();
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfosByText(this.mConnectionId, this.mWindowId, this.mSourceNodeId, text);
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(String viewId) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return Collections.emptyList();
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfosByViewId(this.mConnectionId, this.mWindowId, this.mSourceNodeId, viewId);
    }

    public AccessibilityNodeInfo getParent() {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mParentNodeId)) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mParentNodeId, false, 6);
    }

    public long getParentNodeId() {
        return this.mParentNodeId;
    }

    public void setParent(View parent) {
        setParent(parent, -1);
    }

    public void setParent(View root, int virtualDescendantId) {
        enforceNotSealed();
        int rootAccessibilityViewId = root != null ? root.getAccessibilityViewId() : -1;
        this.mParentNodeId = makeNodeId(rootAccessibilityViewId, virtualDescendantId);
    }

    public void getBoundsInParent(Rect outBounds) {
        outBounds.set(this.mBoundsInParent.left, this.mBoundsInParent.top, this.mBoundsInParent.right, this.mBoundsInParent.bottom);
    }

    public void setBoundsInParent(Rect bounds) {
        enforceNotSealed();
        this.mBoundsInParent.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public void getBoundsInScreen(Rect outBounds) {
        outBounds.set(this.mBoundsInScreen.left, this.mBoundsInScreen.top, this.mBoundsInScreen.right, this.mBoundsInScreen.bottom);
    }

    public void setBoundsInScreen(Rect bounds) {
        enforceNotSealed();
        this.mBoundsInScreen.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public boolean isCheckable() {
        return getBooleanProperty(1);
    }

    public void setCheckable(boolean checkable) {
        setBooleanProperty(1, checkable);
    }

    public boolean isChecked() {
        return getBooleanProperty(2);
    }

    public void setChecked(boolean checked) {
        setBooleanProperty(2, checked);
    }

    public boolean isFocusable() {
        return getBooleanProperty(4);
    }

    public void setFocusable(boolean focusable) {
        setBooleanProperty(4, focusable);
    }

    public boolean isFocused() {
        return getBooleanProperty(8);
    }

    public void setFocused(boolean focused) {
        setBooleanProperty(8, focused);
    }

    public boolean isVisibleToUser() {
        return getBooleanProperty(2048);
    }

    public void setVisibleToUser(boolean visibleToUser) {
        setBooleanProperty(2048, visibleToUser);
    }

    public boolean isAccessibilityFocused() {
        return getBooleanProperty(1024);
    }

    public void setAccessibilityFocused(boolean focused) {
        setBooleanProperty(1024, focused);
    }

    public boolean isSelected() {
        return getBooleanProperty(16);
    }

    public void setSelected(boolean selected) {
        setBooleanProperty(16, selected);
    }

    public boolean isClickable() {
        return getBooleanProperty(32);
    }

    public void setClickable(boolean clickable) {
        setBooleanProperty(32, clickable);
    }

    public boolean isLongClickable() {
        return getBooleanProperty(64);
    }

    public void setLongClickable(boolean longClickable) {
        setBooleanProperty(64, longClickable);
    }

    public boolean isEnabled() {
        return getBooleanProperty(128);
    }

    public void setEnabled(boolean enabled) {
        setBooleanProperty(128, enabled);
    }

    public boolean isPassword() {
        return getBooleanProperty(256);
    }

    public void setPassword(boolean password) {
        setBooleanProperty(256, password);
    }

    public boolean isScrollable() {
        return getBooleanProperty(512);
    }

    public void setScrollable(boolean scrollable) {
        setBooleanProperty(512, scrollable);
    }

    public boolean isEditable() {
        return getBooleanProperty(4096);
    }

    public void setEditable(boolean editable) {
        setBooleanProperty(4096, editable);
    }

    public CollectionInfo getCollectionInfo() {
        return this.mCollectionInfo;
    }

    public void setCollectionInfo(CollectionInfo collectionInfo) {
        enforceNotSealed();
        this.mCollectionInfo = collectionInfo;
    }

    public CollectionItemInfo getCollectionItemInfo() {
        return this.mCollectionItemInfo;
    }

    public void setCollectionItemInfo(CollectionItemInfo collectionItemInfo) {
        enforceNotSealed();
        this.mCollectionItemInfo = collectionItemInfo;
    }

    public RangeInfo getRangeInfo() {
        return this.mRangeInfo;
    }

    public void setRangeInfo(RangeInfo rangeInfo) {
        enforceNotSealed();
        this.mRangeInfo = rangeInfo;
    }

    public boolean isContentInvalid() {
        return getBooleanProperty(65536);
    }

    public void setContentInvalid(boolean contentInvalid) {
        setBooleanProperty(65536, contentInvalid);
    }

    public int getLiveRegion() {
        return this.mLiveRegion;
    }

    public void setLiveRegion(int mode) {
        enforceNotSealed();
        this.mLiveRegion = mode;
    }

    public boolean isMultiLine() {
        return getBooleanProperty(32768);
    }

    public void setMultiLine(boolean multiLine) {
        setBooleanProperty(32768, multiLine);
    }

    public boolean canOpenPopup() {
        return getBooleanProperty(8192);
    }

    public void setCanOpenPopup(boolean opensPopup) {
        enforceNotSealed();
        setBooleanProperty(8192, opensPopup);
    }

    public boolean isDismissable() {
        return getBooleanProperty(16384);
    }

    public void setDismissable(boolean dismissable) {
        setBooleanProperty(16384, dismissable);
    }

    public CharSequence getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(CharSequence packageName) {
        enforceNotSealed();
        this.mPackageName = packageName;
    }

    public CharSequence getClassName() {
        return this.mClassName;
    }

    public void setClassName(CharSequence className) {
        enforceNotSealed();
        this.mClassName = className;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public void setText(CharSequence text) {
        enforceNotSealed();
        this.mText = text;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public void setContentDescription(CharSequence contentDescription) {
        enforceNotSealed();
        this.mContentDescription = contentDescription;
    }

    public void setLabelFor(View labeled) {
        setLabelFor(labeled, -1);
    }

    public void setLabelFor(View root, int virtualDescendantId) {
        enforceNotSealed();
        int rootAccessibilityViewId = root != null ? root.getAccessibilityViewId() : -1;
        this.mLabelForId = makeNodeId(rootAccessibilityViewId, virtualDescendantId);
    }

    public AccessibilityNodeInfo getLabelFor() {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mLabelForId)) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mLabelForId, false, 6);
    }

    public void setLabeledBy(View label) {
        setLabeledBy(label, -1);
    }

    public void setLabeledBy(View root, int virtualDescendantId) {
        enforceNotSealed();
        int rootAccessibilityViewId = root != null ? root.getAccessibilityViewId() : -1;
        this.mLabeledById = makeNodeId(rootAccessibilityViewId, virtualDescendantId);
    }

    public AccessibilityNodeInfo getLabeledBy() {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mLabeledById)) {
            return null;
        }
        AccessibilityInteractionClient client = AccessibilityInteractionClient.getInstance();
        return client.findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mLabeledById, false, 6);
    }

    public void setViewIdResourceName(String viewIdResName) {
        enforceNotSealed();
        this.mViewIdResourceName = viewIdResName;
    }

    public String getViewIdResourceName() {
        return this.mViewIdResourceName;
    }

    public int getTextSelectionStart() {
        return this.mTextSelectionStart;
    }

    public int getTextSelectionEnd() {
        return this.mTextSelectionEnd;
    }

    public void setTextSelection(int start, int end) {
        enforceNotSealed();
        this.mTextSelectionStart = start;
        this.mTextSelectionEnd = end;
    }

    public int getInputType() {
        return this.mInputType;
    }

    public void setInputType(int inputType) {
        enforceNotSealed();
        this.mInputType = inputType;
    }

    public Bundle getExtras() {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        return this.mExtras;
    }

    private boolean getBooleanProperty(int property) {
        return (this.mBooleanProperties & property) != 0;
    }

    private void setBooleanProperty(int property, boolean value) {
        enforceNotSealed();
        if (value) {
            this.mBooleanProperties |= property;
        } else {
            this.mBooleanProperties &= property ^ (-1);
        }
    }

    public void setConnectionId(int connectionId) {
        enforceNotSealed();
        this.mConnectionId = connectionId;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public long getSourceNodeId() {
        return this.mSourceNodeId;
    }

    public void setSealed(boolean sealed) {
        this.mSealed = sealed;
    }

    public boolean isSealed() {
        return this.mSealed;
    }

    protected void enforceSealed() {
        if (!isSealed()) {
            throw new IllegalStateException("Cannot perform this action on a not sealed instance.");
        }
    }

    private void enforceValidFocusDirection(int direction) {
        switch (direction) {
            case 1:
            case 2:
            case 17:
            case 33:
            case 66:
            case 130:
                return;
            default:
                throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }

    private void enforceValidFocusType(int focusType) {
        switch (focusType) {
            case 1:
            case 2:
                return;
            default:
                throw new IllegalArgumentException("Unknown focus type: " + focusType);
        }
    }

    protected void enforceNotSealed() {
        if (isSealed()) {
            throw new IllegalStateException("Cannot perform this action on a sealed instance.");
        }
    }

    public static AccessibilityNodeInfo obtain(View source) {
        AccessibilityNodeInfo info = obtain();
        info.setSource(source);
        return info;
    }

    public static AccessibilityNodeInfo obtain(View root, int virtualDescendantId) {
        AccessibilityNodeInfo info = obtain();
        info.setSource(root, virtualDescendantId);
        return info;
    }

    public static AccessibilityNodeInfo obtain() {
        AccessibilityNodeInfo info = sPool.acquire();
        return info != null ? info : new AccessibilityNodeInfo();
    }

    public static AccessibilityNodeInfo obtain(AccessibilityNodeInfo info) {
        AccessibilityNodeInfo infoClone = obtain();
        infoClone.init(info);
        return infoClone;
    }

    public void recycle() {
        clear();
        sPool.release(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(isSealed() ? 1 : 0);
        parcel.writeLong(this.mSourceNodeId);
        parcel.writeInt(this.mWindowId);
        parcel.writeLong(this.mParentNodeId);
        parcel.writeLong(this.mLabelForId);
        parcel.writeLong(this.mLabeledById);
        parcel.writeInt(this.mConnectionId);
        SparseLongArray childIds = this.mChildNodeIds;
        int childIdsSize = childIds.size();
        parcel.writeInt(childIdsSize);
        for (int i = 0; i < childIdsSize; i++) {
            parcel.writeLong(childIds.valueAt(i));
        }
        parcel.writeInt(this.mBoundsInParent.top);
        parcel.writeInt(this.mBoundsInParent.bottom);
        parcel.writeInt(this.mBoundsInParent.left);
        parcel.writeInt(this.mBoundsInParent.right);
        parcel.writeInt(this.mBoundsInScreen.top);
        parcel.writeInt(this.mBoundsInScreen.bottom);
        parcel.writeInt(this.mBoundsInScreen.left);
        parcel.writeInt(this.mBoundsInScreen.right);
        parcel.writeInt(this.mActions);
        parcel.writeInt(this.mMovementGranularities);
        parcel.writeInt(this.mBooleanProperties);
        parcel.writeCharSequence(this.mPackageName);
        parcel.writeCharSequence(this.mClassName);
        parcel.writeCharSequence(this.mText);
        parcel.writeCharSequence(this.mContentDescription);
        parcel.writeString(this.mViewIdResourceName);
        parcel.writeInt(this.mTextSelectionStart);
        parcel.writeInt(this.mTextSelectionEnd);
        parcel.writeInt(this.mInputType);
        parcel.writeInt(this.mLiveRegion);
        if (this.mExtras != null) {
            parcel.writeInt(1);
            parcel.writeBundle(this.mExtras);
        } else {
            parcel.writeInt(0);
        }
        if (this.mRangeInfo != null) {
            parcel.writeInt(1);
            parcel.writeInt(this.mRangeInfo.getType());
            parcel.writeFloat(this.mRangeInfo.getMin());
            parcel.writeFloat(this.mRangeInfo.getMax());
            parcel.writeFloat(this.mRangeInfo.getCurrent());
        } else {
            parcel.writeInt(0);
        }
        if (this.mCollectionInfo != null) {
            parcel.writeInt(1);
            parcel.writeInt(this.mCollectionInfo.getRowCount());
            parcel.writeInt(this.mCollectionInfo.getColumnCount());
            parcel.writeInt(this.mCollectionInfo.isHierarchical() ? 1 : 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.mCollectionItemInfo != null) {
            parcel.writeInt(1);
            parcel.writeInt(this.mCollectionItemInfo.getColumnIndex());
            parcel.writeInt(this.mCollectionItemInfo.getColumnSpan());
            parcel.writeInt(this.mCollectionItemInfo.getRowIndex());
            parcel.writeInt(this.mCollectionItemInfo.getRowSpan());
            parcel.writeInt(this.mCollectionItemInfo.isHeading() ? 1 : 0);
        } else {
            parcel.writeInt(0);
        }
        recycle();
    }

    private void init(AccessibilityNodeInfo other) {
        this.mSealed = other.mSealed;
        this.mSourceNodeId = other.mSourceNodeId;
        this.mParentNodeId = other.mParentNodeId;
        this.mLabelForId = other.mLabelForId;
        this.mLabeledById = other.mLabeledById;
        this.mWindowId = other.mWindowId;
        this.mConnectionId = other.mConnectionId;
        this.mBoundsInParent.set(other.mBoundsInParent);
        this.mBoundsInScreen.set(other.mBoundsInScreen);
        this.mPackageName = other.mPackageName;
        this.mClassName = other.mClassName;
        this.mText = other.mText;
        this.mContentDescription = other.mContentDescription;
        this.mViewIdResourceName = other.mViewIdResourceName;
        this.mActions = other.mActions;
        this.mBooleanProperties = other.mBooleanProperties;
        this.mMovementGranularities = other.mMovementGranularities;
        int otherChildIdCount = other.mChildNodeIds.size();
        for (int i = 0; i < otherChildIdCount; i++) {
            this.mChildNodeIds.put(i, other.mChildNodeIds.valueAt(i));
        }
        this.mTextSelectionStart = other.mTextSelectionStart;
        this.mTextSelectionEnd = other.mTextSelectionEnd;
        this.mInputType = other.mInputType;
        this.mLiveRegion = other.mLiveRegion;
        if (other.mExtras != null && !other.mExtras.isEmpty()) {
            getExtras().putAll(other.mExtras);
        }
        this.mRangeInfo = other.mRangeInfo != null ? RangeInfo.obtain(other.mRangeInfo) : null;
        this.mCollectionInfo = other.mCollectionInfo != null ? CollectionInfo.obtain(other.mCollectionInfo) : null;
        this.mCollectionItemInfo = other.mCollectionItemInfo != null ? CollectionItemInfo.obtain(other.mCollectionItemInfo) : null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.mSealed = parcel.readInt() == 1;
        this.mSourceNodeId = parcel.readLong();
        this.mWindowId = parcel.readInt();
        this.mParentNodeId = parcel.readLong();
        this.mLabelForId = parcel.readLong();
        this.mLabeledById = parcel.readLong();
        this.mConnectionId = parcel.readInt();
        SparseLongArray childIds = this.mChildNodeIds;
        int childrenSize = parcel.readInt();
        for (int i = 0; i < childrenSize; i++) {
            long childId = parcel.readLong();
            childIds.put(i, childId);
        }
        this.mBoundsInParent.top = parcel.readInt();
        this.mBoundsInParent.bottom = parcel.readInt();
        this.mBoundsInParent.left = parcel.readInt();
        this.mBoundsInParent.right = parcel.readInt();
        this.mBoundsInScreen.top = parcel.readInt();
        this.mBoundsInScreen.bottom = parcel.readInt();
        this.mBoundsInScreen.left = parcel.readInt();
        this.mBoundsInScreen.right = parcel.readInt();
        this.mActions = parcel.readInt();
        this.mMovementGranularities = parcel.readInt();
        this.mBooleanProperties = parcel.readInt();
        this.mPackageName = parcel.readCharSequence();
        this.mClassName = parcel.readCharSequence();
        this.mText = parcel.readCharSequence();
        this.mContentDescription = parcel.readCharSequence();
        this.mViewIdResourceName = parcel.readString();
        this.mTextSelectionStart = parcel.readInt();
        this.mTextSelectionEnd = parcel.readInt();
        this.mInputType = parcel.readInt();
        this.mLiveRegion = parcel.readInt();
        if (parcel.readInt() == 1) {
            getExtras().putAll(parcel.readBundle());
        }
        if (parcel.readInt() == 1) {
            this.mRangeInfo = RangeInfo.obtain(parcel.readInt(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat());
        }
        if (parcel.readInt() == 1) {
            this.mCollectionInfo = CollectionInfo.obtain(parcel.readInt(), parcel.readInt(), parcel.readInt() == 1);
        }
        if (parcel.readInt() == 1) {
            this.mCollectionItemInfo = CollectionItemInfo.obtain(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() == 1);
        }
    }

    private void clear() {
        this.mSealed = false;
        this.mSourceNodeId = ROOT_NODE_ID;
        this.mParentNodeId = ROOT_NODE_ID;
        this.mLabelForId = ROOT_NODE_ID;
        this.mLabeledById = ROOT_NODE_ID;
        this.mWindowId = -1;
        this.mConnectionId = -1;
        this.mMovementGranularities = 0;
        this.mChildNodeIds.clear();
        this.mBoundsInParent.set(0, 0, 0, 0);
        this.mBoundsInScreen.set(0, 0, 0, 0);
        this.mBooleanProperties = 0;
        this.mPackageName = null;
        this.mClassName = null;
        this.mText = null;
        this.mContentDescription = null;
        this.mViewIdResourceName = null;
        this.mActions = 0;
        this.mTextSelectionStart = -1;
        this.mTextSelectionEnd = -1;
        this.mInputType = 0;
        this.mLiveRegion = 0;
        if (this.mExtras != null) {
            this.mExtras.clear();
        }
        if (this.mRangeInfo != null) {
            this.mRangeInfo.recycle();
            this.mRangeInfo = null;
        }
        if (this.mCollectionInfo != null) {
            this.mCollectionInfo.recycle();
            this.mCollectionInfo = null;
        }
        if (this.mCollectionItemInfo != null) {
            this.mCollectionItemInfo.recycle();
            this.mCollectionItemInfo = null;
        }
    }

    private static String getActionSymbolicName(int action) {
        switch (action) {
            case 1:
                return "ACTION_FOCUS";
            case 2:
                return "ACTION_CLEAR_FOCUS";
            case 4:
                return "ACTION_SELECT";
            case 8:
                return "ACTION_CLEAR_SELECTION";
            case 16:
                return "ACTION_CLICK";
            case 32:
                return "ACTION_LONG_CLICK";
            case 64:
                return "ACTION_ACCESSIBILITY_FOCUS";
            case 128:
                return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
            case 256:
                return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
            case 512:
                return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
            case 1024:
                return "ACTION_NEXT_HTML_ELEMENT";
            case 2048:
                return "ACTION_PREVIOUS_HTML_ELEMENT";
            case 4096:
                return "ACTION_SCROLL_FORWARD";
            case 8192:
                return "ACTION_SCROLL_BACKWARD";
            case 16384:
                return "ACTION_COPY";
            case 32768:
                return "ACTION_PASTE";
            case 65536:
                return "ACTION_CUT";
            case 131072:
                return "ACTION_SET_SELECTION";
            default:
                return "ACTION_UNKNOWN";
        }
    }

    private static String getMovementGranularitySymbolicName(int granularity) {
        switch (granularity) {
            case 1:
                return "MOVEMENT_GRANULARITY_CHARACTER";
            case 2:
                return "MOVEMENT_GRANULARITY_WORD";
            case 4:
                return "MOVEMENT_GRANULARITY_LINE";
            case 8:
                return "MOVEMENT_GRANULARITY_PARAGRAPH";
            case 16:
                return "MOVEMENT_GRANULARITY_PAGE";
            default:
                throw new IllegalArgumentException("Unknown movement granularity: " + granularity);
        }
    }

    private boolean canPerformRequestOverConnection(long accessibilityNodeId) {
        return (this.mWindowId == -1 || getAccessibilityViewId(accessibilityNodeId) == -1 || this.mConnectionId == -1) ? false : true;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AccessibilityNodeInfo other = (AccessibilityNodeInfo) object;
        if (this.mSourceNodeId != other.mSourceNodeId || this.mWindowId != other.mWindowId) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + getAccessibilityViewId(this.mSourceNodeId);
        return (31 * ((31 * result) + getVirtualDescendantId(this.mSourceNodeId))) + this.mWindowId;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("; boundsInParent: " + this.mBoundsInParent);
        builder.append("; boundsInScreen: " + this.mBoundsInScreen);
        builder.append("; packageName: ").append(this.mPackageName);
        builder.append("; className: ").append(this.mClassName);
        builder.append("; text: ").append(this.mText);
        builder.append("; contentDescription: ").append(this.mContentDescription);
        builder.append("; viewIdResName: ").append(this.mViewIdResourceName);
        builder.append("; checkable: ").append(isCheckable());
        builder.append("; checked: ").append(isChecked());
        builder.append("; focusable: ").append(isFocusable());
        builder.append("; focused: ").append(isFocused());
        builder.append("; selected: ").append(isSelected());
        builder.append("; clickable: ").append(isClickable());
        builder.append("; longClickable: ").append(isLongClickable());
        builder.append("; enabled: ").append(isEnabled());
        builder.append("; password: ").append(isPassword());
        builder.append("; scrollable: " + isScrollable());
        builder.append("; [");
        int actionBits = this.mActions;
        while (actionBits != 0) {
            int action = 1 << Integer.numberOfTrailingZeros(actionBits);
            actionBits &= action ^ (-1);
            builder.append(getActionSymbolicName(action));
            if (actionBits != 0) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /* loaded from: AccessibilityNodeInfo$RangeInfo.class */
    public static final class RangeInfo {
        private static final int MAX_POOL_SIZE = 10;
        public static final int RANGE_TYPE_INT = 0;
        public static final int RANGE_TYPE_FLOAT = 1;
        public static final int RANGE_TYPE_PERCENT = 2;
        private static final Pools.SynchronizedPool<RangeInfo> sPool = new Pools.SynchronizedPool<>(10);
        private int mType;
        private float mMin;
        private float mMax;
        private float mCurrent;

        public static RangeInfo obtain(RangeInfo other) {
            return obtain(other.mType, other.mMin, other.mMax, other.mCurrent);
        }

        public static RangeInfo obtain(int type, float min, float max, float current) {
            RangeInfo info = sPool.acquire();
            return info != null ? info : new RangeInfo(type, min, max, current);
        }

        private RangeInfo(int type, float min, float max, float current) {
            this.mType = type;
            this.mMin = min;
            this.mMax = max;
            this.mCurrent = current;
        }

        public int getType() {
            return this.mType;
        }

        public float getMin() {
            return this.mMin;
        }

        public float getMax() {
            return this.mMax;
        }

        public float getCurrent() {
            return this.mCurrent;
        }

        void recycle() {
            clear();
            sPool.release(this);
        }

        private void clear() {
            this.mType = 0;
            this.mMin = 0.0f;
            this.mMax = 0.0f;
            this.mCurrent = 0.0f;
        }
    }

    /* loaded from: AccessibilityNodeInfo$CollectionInfo.class */
    public static final class CollectionInfo {
        private static final int MAX_POOL_SIZE = 20;
        private static final Pools.SynchronizedPool<CollectionInfo> sPool = new Pools.SynchronizedPool<>(20);
        private int mRowCount;
        private int mColumnCount;
        private boolean mHierarchical;

        public static CollectionInfo obtain(CollectionInfo other) {
            return obtain(other.mRowCount, other.mColumnCount, other.mHierarchical);
        }

        public static CollectionInfo obtain(int rowCount, int columnCount, boolean hierarchical) {
            CollectionInfo info = sPool.acquire();
            return info != null ? info : new CollectionInfo(rowCount, columnCount, hierarchical);
        }

        private CollectionInfo(int rowCount, int columnCount, boolean hierarchical) {
            this.mRowCount = rowCount;
            this.mColumnCount = columnCount;
            this.mHierarchical = hierarchical;
        }

        public int getRowCount() {
            return this.mRowCount;
        }

        public int getColumnCount() {
            return this.mColumnCount;
        }

        public boolean isHierarchical() {
            return this.mHierarchical;
        }

        void recycle() {
            clear();
            sPool.release(this);
        }

        private void clear() {
            this.mRowCount = 0;
            this.mColumnCount = 0;
            this.mHierarchical = false;
        }
    }

    /* loaded from: AccessibilityNodeInfo$CollectionItemInfo.class */
    public static final class CollectionItemInfo {
        private static final int MAX_POOL_SIZE = 20;
        private static final Pools.SynchronizedPool<CollectionItemInfo> sPool = new Pools.SynchronizedPool<>(20);
        private boolean mHeading;
        private int mColumnIndex;
        private int mRowIndex;
        private int mColumnSpan;
        private int mRowSpan;

        public static CollectionItemInfo obtain(CollectionItemInfo other) {
            return obtain(other.mRowIndex, other.mRowSpan, other.mColumnIndex, other.mColumnSpan, other.mHeading);
        }

        public static CollectionItemInfo obtain(int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading) {
            CollectionItemInfo info = sPool.acquire();
            return info != null ? info : new CollectionItemInfo(rowIndex, rowSpan, columnIndex, columnSpan, heading);
        }

        private CollectionItemInfo(int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading) {
            this.mRowIndex = rowIndex;
            this.mRowSpan = rowSpan;
            this.mColumnIndex = columnIndex;
            this.mColumnSpan = columnSpan;
            this.mHeading = heading;
        }

        public int getColumnIndex() {
            return this.mColumnIndex;
        }

        public int getRowIndex() {
            return this.mRowIndex;
        }

        public int getColumnSpan() {
            return this.mColumnSpan;
        }

        public int getRowSpan() {
            return this.mRowSpan;
        }

        public boolean isHeading() {
            return this.mHeading;
        }

        void recycle() {
            clear();
            sPool.release(this);
        }

        private void clear() {
            this.mColumnIndex = 0;
            this.mColumnSpan = 0;
            this.mRowIndex = 0;
            this.mRowSpan = 0;
            this.mHeading = false;
        }
    }
}