package android.view;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Interpolator;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManagerGlobal;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.LongSparseLongArray;
import android.util.Pools;
import android.util.Property;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.util.TypedValue;
import android.view.AccessibilityIterators;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ScrollBarDrawable;
import com.android.internal.R;
import com.android.internal.util.Predicate;
import com.android.internal.view.menu.MenuBuilder;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ims.AuthorizationHeaderIms;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: View.class */
public class View implements Drawable.Callback, KeyEvent.Callback, AccessibilityEventSource {
    private static final boolean DBG = false;
    protected static final String VIEW_LOG_TAG = "View";
    public static final String DEBUG_LAYOUT_PROPERTY = "debug.layout";
    public static final int NO_ID = -1;
    private static final int NOT_FOCUSABLE = 0;
    private static final int FOCUSABLE = 1;
    private static final int FOCUSABLE_MASK = 1;
    private static final int FITS_SYSTEM_WINDOWS = 2;
    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 4;
    public static final int GONE = 8;
    static final int VISIBILITY_MASK = 12;
    static final int ENABLED = 0;
    static final int DISABLED = 32;
    static final int ENABLED_MASK = 32;
    static final int WILL_NOT_DRAW = 128;
    static final int DRAW_MASK = 128;
    static final int SCROLLBARS_NONE = 0;
    static final int SCROLLBARS_HORIZONTAL = 256;
    static final int SCROLLBARS_VERTICAL = 512;
    static final int SCROLLBARS_MASK = 768;
    static final int FILTER_TOUCHES_WHEN_OBSCURED = 1024;
    static final int OPTIONAL_FITS_SYSTEM_WINDOWS = 2048;
    static final int FADING_EDGE_NONE = 0;
    static final int FADING_EDGE_HORIZONTAL = 4096;
    static final int FADING_EDGE_VERTICAL = 8192;
    static final int FADING_EDGE_MASK = 12288;
    static final int CLICKABLE = 16384;
    static final int DRAWING_CACHE_ENABLED = 32768;
    static final int SAVE_DISABLED = 65536;
    static final int SAVE_DISABLED_MASK = 65536;
    static final int WILL_NOT_CACHE_DRAWING = 131072;
    static final int FOCUSABLE_IN_TOUCH_MODE = 262144;
    public static final int DRAWING_CACHE_QUALITY_LOW = 524288;
    public static final int DRAWING_CACHE_QUALITY_HIGH = 1048576;
    public static final int DRAWING_CACHE_QUALITY_AUTO = 0;
    static final int DRAWING_CACHE_QUALITY_MASK = 1572864;
    static final int LONG_CLICKABLE = 2097152;
    static final int DUPLICATE_PARENT_STATE = 4194304;
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    public static final int SCROLLBARS_INSIDE_INSET = 16777216;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432;
    public static final int SCROLLBARS_OUTSIDE_INSET = 50331648;
    static final int SCROLLBARS_INSET_MASK = 16777216;
    static final int SCROLLBARS_OUTSIDE_MASK = 33554432;
    static final int SCROLLBARS_STYLE_MASK = 50331648;
    public static final int KEEP_SCREEN_ON = 67108864;
    public static final int SOUND_EFFECTS_ENABLED = 134217728;
    public static final int HAPTIC_FEEDBACK_ENABLED = 268435456;
    static final int PARENT_SAVE_DISABLED = 536870912;
    static final int PARENT_SAVE_DISABLED_MASK = 536870912;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 1;
    public static final int FOCUS_BACKWARD = 1;
    public static final int FOCUS_FORWARD = 2;
    public static final int FOCUS_LEFT = 17;
    public static final int FOCUS_UP = 33;
    public static final int FOCUS_RIGHT = 66;
    public static final int FOCUS_DOWN = 130;
    public static final int MEASURED_SIZE_MASK = 16777215;
    public static final int MEASURED_STATE_MASK = -16777216;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_STATE_TOO_SMALL = 16777216;
    protected static final int[] EMPTY_STATE_SET;
    protected static final int[] ENABLED_STATE_SET;
    protected static final int[] FOCUSED_STATE_SET;
    protected static final int[] SELECTED_STATE_SET;
    protected static final int[] PRESSED_STATE_SET;
    protected static final int[] WINDOW_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_SELECTED_STATE_SET;
    protected static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] FOCUSED_SELECTED_STATE_SET;
    protected static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    private static final int[][] VIEW_STATE_SETS;
    static final int VIEW_STATE_WINDOW_FOCUSED = 1;
    static final int VIEW_STATE_SELECTED = 2;
    static final int VIEW_STATE_FOCUSED = 4;
    static final int VIEW_STATE_ENABLED = 8;
    static final int VIEW_STATE_PRESSED = 16;
    static final int VIEW_STATE_ACTIVATED = 32;
    static final int VIEW_STATE_ACCELERATED = 64;
    static final int VIEW_STATE_HOVERED = 128;
    static final int VIEW_STATE_DRAG_CAN_ACCEPT = 256;
    static final int VIEW_STATE_DRAG_HOVERED = 512;
    private static final int POPULATING_ACCESSIBILITY_EVENT_TYPES = 172479;
    static final ThreadLocal<Rect> sThreadLocal;
    private SparseArray<Object> mKeyedTags;
    private static int sNextAccessibilityViewId;
    protected Animation mCurrentAnimation;
    @ViewDebug.ExportedProperty(category = "measurement")
    int mMeasuredWidth;
    @ViewDebug.ExportedProperty(category = "measurement")
    int mMeasuredHeight;
    boolean mRecreateDisplayList;
    @ViewDebug.ExportedProperty(resolveId = true)
    int mID;
    int mAccessibilityViewId;
    private int mAccessibilityCursorPosition;
    SendViewStateChangedAccessibilityEvent mSendViewStateChangedAccessibilityEvent;
    protected Object mTag;
    static final int PFLAG_WANTS_FOCUS = 1;
    static final int PFLAG_FOCUSED = 2;
    static final int PFLAG_SELECTED = 4;
    static final int PFLAG_IS_ROOT_NAMESPACE = 8;
    static final int PFLAG_HAS_BOUNDS = 16;
    static final int PFLAG_DRAWN = 32;
    static final int PFLAG_DRAW_ANIMATION = 64;
    static final int PFLAG_SKIP_DRAW = 128;
    static final int PFLAG_ONLY_DRAWS_BACKGROUND = 256;
    static final int PFLAG_REQUEST_TRANSPARENT_REGIONS = 512;
    static final int PFLAG_DRAWABLE_STATE_DIRTY = 1024;
    static final int PFLAG_MEASURED_DIMENSION_SET = 2048;
    static final int PFLAG_FORCE_LAYOUT = 4096;
    static final int PFLAG_LAYOUT_REQUIRED = 8192;
    private static final int PFLAG_PRESSED = 16384;
    static final int PFLAG_DRAWING_CACHE_VALID = 32768;
    static final int PFLAG_ANIMATION_STARTED = 65536;
    private static final int PFLAG_SAVE_STATE_CALLED = 131072;
    static final int PFLAG_ALPHA_SET = 262144;
    static final int PFLAG_SCROLL_CONTAINER = 524288;
    static final int PFLAG_SCROLL_CONTAINER_ADDED = 1048576;
    static final int PFLAG_DIRTY = 2097152;
    static final int PFLAG_DIRTY_OPAQUE = 4194304;
    static final int PFLAG_DIRTY_MASK = 6291456;
    static final int PFLAG_OPAQUE_BACKGROUND = 8388608;
    static final int PFLAG_OPAQUE_SCROLLBARS = 16777216;
    static final int PFLAG_OPAQUE_MASK = 25165824;
    private static final int PFLAG_PREPRESSED = 33554432;
    static final int PFLAG_CANCEL_NEXT_UP_EVENT = 67108864;
    private static final int PFLAG_AWAKEN_SCROLL_BARS_ON_ATTACH = 134217728;
    private static final int PFLAG_HOVERED = 268435456;
    private static final int PFLAG_PIVOT_EXPLICITLY_SET = 536870912;
    static final int PFLAG_ACTIVATED = 1073741824;
    static final int PFLAG_INVALIDATED = Integer.MIN_VALUE;
    static final int PFLAG2_DRAG_CAN_ACCEPT = 1;
    static final int PFLAG2_DRAG_HOVERED = 2;
    public static final int LAYOUT_DIRECTION_LTR = 0;
    public static final int LAYOUT_DIRECTION_RTL = 1;
    public static final int LAYOUT_DIRECTION_INHERIT = 2;
    public static final int LAYOUT_DIRECTION_LOCALE = 3;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT = 2;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK = 12;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_RTL = 16;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED = 32;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK = 48;
    private static final int[] LAYOUT_DIRECTION_FLAGS;
    private static final int LAYOUT_DIRECTION_DEFAULT = 2;
    static final int LAYOUT_DIRECTION_RESOLVED_DEFAULT = 0;
    public static final int TEXT_DIRECTION_INHERIT = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG = 1;
    public static final int TEXT_DIRECTION_ANY_RTL = 2;
    public static final int TEXT_DIRECTION_LTR = 3;
    public static final int TEXT_DIRECTION_RTL = 4;
    public static final int TEXT_DIRECTION_LOCALE = 5;
    private static final int TEXT_DIRECTION_DEFAULT = 0;
    static final int TEXT_DIRECTION_RESOLVED_DEFAULT = 1;
    static final int PFLAG2_TEXT_DIRECTION_MASK_SHIFT = 6;
    static final int PFLAG2_TEXT_DIRECTION_MASK = 448;
    private static final int[] PFLAG2_TEXT_DIRECTION_FLAGS;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED = 512;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT = 10;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK = 7168;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_DEFAULT = 1024;
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    public static final int TEXT_ALIGNMENT_GRAVITY = 1;
    public static final int TEXT_ALIGNMENT_TEXT_START = 2;
    public static final int TEXT_ALIGNMENT_TEXT_END = 3;
    public static final int TEXT_ALIGNMENT_CENTER = 4;
    public static final int TEXT_ALIGNMENT_VIEW_START = 5;
    public static final int TEXT_ALIGNMENT_VIEW_END = 6;
    private static final int TEXT_ALIGNMENT_DEFAULT = 1;
    static final int TEXT_ALIGNMENT_RESOLVED_DEFAULT = 1;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT = 13;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK = 57344;
    private static final int[] PFLAG2_TEXT_ALIGNMENT_FLAGS;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED = 65536;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT = 17;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK = 917504;
    private static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_DEFAULT = 131072;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT = 20;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
    static final int IMPORTANT_FOR_ACCESSIBILITY_DEFAULT = 0;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK = 7340032;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT = 23;
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
    static final int ACCESSIBILITY_LIVE_REGION_DEFAULT = 0;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_MASK = 25165824;
    static final int PFLAG2_ACCESSIBILITY_FOCUSED = 67108864;
    static final int PFLAG2_SUBTREE_ACCESSIBILITY_STATE_CHANGED = 134217728;
    static final int PFLAG2_VIEW_QUICK_REJECTED = 268435456;
    static final int PFLAG2_PADDING_RESOLVED = 536870912;
    static final int PFLAG2_DRAWABLE_RESOLVED = 1073741824;
    static final int PFLAG2_HAS_TRANSIENT_STATE = Integer.MIN_VALUE;
    static final int ALL_RTL_PROPERTIES_RESOLVED = 1610678816;
    static final int PFLAG3_VIEW_IS_ANIMATING_TRANSFORM = 1;
    static final int PFLAG3_VIEW_IS_ANIMATING_ALPHA = 2;
    static final int PFLAG3_IS_LAID_OUT = 4;
    static final int PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT = 8;
    static final int PFLAG3_CALLED_SUPER = 16;
    static final int DRAG_MASK = 3;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    public static final int SYSTEM_UI_FLAG_VISIBLE = 0;
    public static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
    public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
    public static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
    public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
    public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE = 2048;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 4096;
    public static final int STATUS_BAR_HIDDEN = 1;
    public static final int STATUS_BAR_VISIBLE = 0;
    public static final int STATUS_BAR_DISABLE_EXPAND = 65536;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ICONS = 131072;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ALERTS = 262144;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_TICKER = 524288;
    public static final int STATUS_BAR_DISABLE_SYSTEM_INFO = 1048576;
    public static final int STATUS_BAR_DISABLE_HOME = 2097152;
    public static final int STATUS_BAR_DISABLE_BACK = 4194304;
    public static final int STATUS_BAR_DISABLE_CLOCK = 8388608;
    public static final int STATUS_BAR_DISABLE_RECENT = 16777216;
    public static final int STATUS_BAR_DISABLE_SEARCH = 33554432;
    public static final int STATUS_BAR_TRANSIENT = 67108864;
    public static final int NAVIGATION_BAR_TRANSIENT = 134217728;
    public static final int STATUS_BAR_UNHIDE = 268435456;
    public static final int NAVIGATION_BAR_UNHIDE = 536870912;
    public static final int STATUS_BAR_TRANSLUCENT = 1073741824;
    public static final int NAVIGATION_BAR_TRANSLUCENT = Integer.MIN_VALUE;
    public static final int PUBLIC_STATUS_BAR_VISIBILITY_MASK = 65535;
    public static final int SYSTEM_UI_CLEARABLE_FLAGS = 7;
    public static final int SYSTEM_UI_LAYOUT_FLAGS = 1536;
    public static final int FIND_VIEWS_WITH_TEXT = 1;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;
    public static final int FIND_VIEWS_WITH_ACCESSIBILITY_NODE_PROVIDERS = 4;
    public static final int ACCESSIBILITY_CURSOR_POSITION_UNDEFINED = -1;
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 1;
    private int mOverScrollMode;
    protected ViewParent mParent;
    AttachInfo mAttachInfo;
    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(mask = 4096, equals = 4096, name = "FORCE_LAYOUT"), @ViewDebug.FlagToString(mask = 8192, equals = 8192, name = "LAYOUT_REQUIRED"), @ViewDebug.FlagToString(mask = 32768, equals = 32768, name = "DRAWING_CACHE_INVALID", outputIf = false), @ViewDebug.FlagToString(mask = 32, equals = 32, name = "DRAWN", outputIf = true), @ViewDebug.FlagToString(mask = 32, equals = 32, name = "NOT_DRAWN", outputIf = false), @ViewDebug.FlagToString(mask = 6291456, equals = 4194304, name = "DIRTY_OPAQUE"), @ViewDebug.FlagToString(mask = 6291456, equals = 2097152, name = "DIRTY")})
    int mPrivateFlags;
    int mPrivateFlags2;
    int mPrivateFlags3;
    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(mask = 1, equals = 1, name = "SYSTEM_UI_FLAG_LOW_PROFILE", outputIf = true), @ViewDebug.FlagToString(mask = 2, equals = 2, name = "SYSTEM_UI_FLAG_HIDE_NAVIGATION", outputIf = true), @ViewDebug.FlagToString(mask = 65535, equals = 0, name = "SYSTEM_UI_FLAG_VISIBLE", outputIf = true)})
    int mSystemUiVisibility;
    int mTransientStateCount;
    int mWindowAttachCount;
    protected ViewGroup.LayoutParams mLayoutParams;
    @ViewDebug.ExportedProperty
    int mViewFlags;
    TransformationInfo mTransformationInfo;
    private Rect mClipBounds;
    private boolean mLastIsOpaque;
    private static final float NONZERO_EPSILON = 0.001f;
    @ViewDebug.ExportedProperty(category = "layout")
    protected int mLeft;
    @ViewDebug.ExportedProperty(category = "layout")
    protected int mRight;
    @ViewDebug.ExportedProperty(category = "layout")
    protected int mTop;
    @ViewDebug.ExportedProperty(category = "layout")
    protected int mBottom;
    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollX;
    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollY;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingLeft;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingRight;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingTop;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingBottom;
    private Insets mLayoutInsets;
    private CharSequence mContentDescription;
    private int mLabelForId;
    private MatchLabelForPredicate mMatchLabelForPredicate;
    private MatchIdPredicate mMatchIdPredicate;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingRight;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingBottom;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingLeft;
    @ViewDebug.ExportedProperty(category = "padding")
    int mUserPaddingStart;
    @ViewDebug.ExportedProperty(category = "padding")
    int mUserPaddingEnd;
    int mUserPaddingLeftInitial;
    int mUserPaddingRightInitial;
    private static final int UNDEFINED_PADDING = Integer.MIN_VALUE;
    int mOldWidthMeasureSpec;
    int mOldHeightMeasureSpec;
    private LongSparseLongArray mMeasureCache;
    @ViewDebug.ExportedProperty(deepExport = true, prefix = "bg_")
    private Drawable mBackground;
    private int mBackgroundResource;
    private boolean mBackgroundSizeChanged;
    ListenerInfo mListenerInfo;
    protected Context mContext;
    private final Resources mResources;
    private ScrollabilityCache mScrollCache;
    private int[] mDrawableState;
    private int mNextFocusLeftId;
    private int mNextFocusRightId;
    private int mNextFocusUpId;
    private int mNextFocusDownId;
    int mNextFocusForwardId;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private PerformClick mPerformClick;
    private SendViewScrolledAccessibilityEvent mSendViewScrolledAccessibilityEvent;
    private UnsetPressedState mUnsetPressedState;
    private boolean mHasPerformedLongPress;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mMinHeight;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mMinWidth;
    private TouchDelegate mTouchDelegate;
    private int mDrawingCacheBackgroundColor;
    private ViewTreeObserver mFloatingTreeObserver;
    private int mTouchSlop;
    private ViewPropertyAnimator mAnimator;
    public static final int DRAG_FLAG_GLOBAL = 1;
    private float mVerticalScrollFactor;
    private int mVerticalScrollbarPosition;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 1;
    public static final int SCROLLBAR_POSITION_RIGHT = 2;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;
    @ViewDebug.ExportedProperty(category = "drawing", mapping = {@ViewDebug.IntToString(from = 0, to = "NONE"), @ViewDebug.IntToString(from = 1, to = "SOFTWARE"), @ViewDebug.IntToString(from = 2, to = "HARDWARE")})
    int mLayerType;
    Paint mLayerPaint;
    Rect mLocalDirtyRect;
    private HardwareLayer mHardwareLayer;
    public boolean mCachingFailed;
    private Bitmap mDrawingCache;
    private Bitmap mUnscaledDrawingCache;
    DisplayList mDisplayList;
    private boolean mSendingHoverAccessibilityEvents;
    AccessibilityDelegate mAccessibilityDelegate;
    ViewOverlay mOverlay;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private static final AtomicInteger sNextGeneratedId;
    public static final Property<View, Float> ALPHA;
    public static final Property<View, Float> TRANSLATION_X;
    public static final Property<View, Float> TRANSLATION_Y;
    public static final Property<View, Float> X;
    public static final Property<View, Float> Y;
    public static final Property<View, Float> ROTATION;
    public static final Property<View, Float> ROTATION_X;
    public static final Property<View, Float> ROTATION_Y;
    public static final Property<View, Float> SCALE_X;
    public static final Property<View, Float> SCALE_Y;
    private static boolean sCompatibilityDone = false;
    private static boolean sUseBrokenMakeMeasureSpec = false;
    private static boolean sIgnoreMeasureCache = false;
    private static final int[] VISIBILITY_FLAGS = {0, 4, 8};
    private static final int[] DRAWING_CACHE_QUALITY_FLAGS = {0, 524288, 1048576};
    static final int[] VIEW_STATE_IDS = {16842909, 1, 16842913, 2, 16842908, 4, 16842910, 8, 16842919, 16, 16843518, 32, 16843547, 64, 16843623, 128, 16843624, 256, 16843625, 512};

    /* loaded from: View$OnAttachStateChangeListener.class */
    public interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View view);

        void onViewDetachedFromWindow(View view);
    }

    /* loaded from: View$OnClickListener.class */
    public interface OnClickListener {
        void onClick(View view);
    }

    /* loaded from: View$OnCreateContextMenuListener.class */
    public interface OnCreateContextMenuListener {
        void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo);
    }

    /* loaded from: View$OnDragListener.class */
    public interface OnDragListener {
        boolean onDrag(View view, DragEvent dragEvent);
    }

    /* loaded from: View$OnFocusChangeListener.class */
    public interface OnFocusChangeListener {
        void onFocusChange(View view, boolean z);
    }

    /* loaded from: View$OnGenericMotionListener.class */
    public interface OnGenericMotionListener {
        boolean onGenericMotion(View view, MotionEvent motionEvent);
    }

    /* loaded from: View$OnHoverListener.class */
    public interface OnHoverListener {
        boolean onHover(View view, MotionEvent motionEvent);
    }

    /* loaded from: View$OnKeyListener.class */
    public interface OnKeyListener {
        boolean onKey(View view, int i, KeyEvent keyEvent);
    }

    /* loaded from: View$OnLayoutChangeListener.class */
    public interface OnLayoutChangeListener {
        void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);
    }

    /* loaded from: View$OnLongClickListener.class */
    public interface OnLongClickListener {
        boolean onLongClick(View view);
    }

    /* loaded from: View$OnSystemUiVisibilityChangeListener.class */
    public interface OnSystemUiVisibilityChangeListener {
        void onSystemUiVisibilityChange(int i);
    }

    /* loaded from: View$OnTouchListener.class */
    public interface OnTouchListener {
        boolean onTouch(View view, MotionEvent motionEvent);
    }

    /* JADX WARN: Type inference failed for: r0v22, types: [int[], int[][]] */
    static {
        if (VIEW_STATE_IDS.length / 2 != R.styleable.ViewDrawableStates.length) {
            throw new IllegalStateException("VIEW_STATE_IDs array length does not match ViewDrawableStates style array");
        }
        int[] orderedIds = new int[VIEW_STATE_IDS.length];
        for (int i = 0; i < R.styleable.ViewDrawableStates.length; i++) {
            int viewState = R.styleable.ViewDrawableStates[i];
            for (int j = 0; j < VIEW_STATE_IDS.length; j += 2) {
                if (VIEW_STATE_IDS[j] == viewState) {
                    orderedIds[i * 2] = viewState;
                    orderedIds[(i * 2) + 1] = VIEW_STATE_IDS[j + 1];
                }
            }
        }
        int NUM_BITS = VIEW_STATE_IDS.length / 2;
        VIEW_STATE_SETS = new int[1 << NUM_BITS];
        for (int i2 = 0; i2 < VIEW_STATE_SETS.length; i2++) {
            int numBits = Integer.bitCount(i2);
            int[] set = new int[numBits];
            int pos = 0;
            for (int j2 = 0; j2 < orderedIds.length; j2 += 2) {
                if ((i2 & orderedIds[j2 + 1]) != 0) {
                    int i3 = pos;
                    pos++;
                    set[i3] = orderedIds[j2];
                }
            }
            VIEW_STATE_SETS[i2] = set;
        }
        EMPTY_STATE_SET = VIEW_STATE_SETS[0];
        WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[1];
        SELECTED_STATE_SET = VIEW_STATE_SETS[2];
        SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[3];
        FOCUSED_STATE_SET = VIEW_STATE_SETS[4];
        FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[5];
        FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[6];
        FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[7];
        ENABLED_STATE_SET = VIEW_STATE_SETS[8];
        ENABLED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[9];
        ENABLED_SELECTED_STATE_SET = VIEW_STATE_SETS[10];
        ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[11];
        ENABLED_FOCUSED_STATE_SET = VIEW_STATE_SETS[12];
        ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[13];
        ENABLED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[14];
        ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[15];
        PRESSED_STATE_SET = VIEW_STATE_SETS[16];
        PRESSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[17];
        PRESSED_SELECTED_STATE_SET = VIEW_STATE_SETS[18];
        PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[19];
        PRESSED_FOCUSED_STATE_SET = VIEW_STATE_SETS[20];
        PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[21];
        PRESSED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[22];
        PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[23];
        PRESSED_ENABLED_STATE_SET = VIEW_STATE_SETS[24];
        PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[25];
        PRESSED_ENABLED_SELECTED_STATE_SET = VIEW_STATE_SETS[26];
        PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[27];
        PRESSED_ENABLED_FOCUSED_STATE_SET = VIEW_STATE_SETS[28];
        PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[29];
        PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[30];
        PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[31];
        sThreadLocal = new ThreadLocal<>();
        LAYOUT_DIRECTION_FLAGS = new int[]{0, 1, 2, 3};
        PFLAG2_TEXT_DIRECTION_FLAGS = new int[]{0, 64, 128, 192, 256, 320};
        PFLAG2_TEXT_ALIGNMENT_FLAGS = new int[]{0, 8192, 16384, AudioSystem.DEVICE_OUT_ALL_USB, 32768, 40960, 49152};
        sNextGeneratedId = new AtomicInteger(1);
        ALPHA = new FloatProperty<View>("alpha") { // from class: android.view.View.3
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setAlpha(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getAlpha());
            }
        };
        TRANSLATION_X = new FloatProperty<View>("translationX") { // from class: android.view.View.4
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setTranslationX(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getTranslationX());
            }
        };
        TRANSLATION_Y = new FloatProperty<View>("translationY") { // from class: android.view.View.5
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setTranslationY(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getTranslationY());
            }
        };
        X = new FloatProperty<View>("x") { // from class: android.view.View.6
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setX(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getX());
            }
        };
        Y = new FloatProperty<View>("y") { // from class: android.view.View.7
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setY(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getY());
            }
        };
        ROTATION = new FloatProperty<View>("rotation") { // from class: android.view.View.8
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setRotation(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getRotation());
            }
        };
        ROTATION_X = new FloatProperty<View>("rotationX") { // from class: android.view.View.9
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setRotationX(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getRotationX());
            }
        };
        ROTATION_Y = new FloatProperty<View>("rotationY") { // from class: android.view.View.10
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setRotationY(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getRotationY());
            }
        };
        SCALE_X = new FloatProperty<View>("scaleX") { // from class: android.view.View.11
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setScaleX(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getScaleX());
            }
        };
        SCALE_Y = new FloatProperty<View>("scaleY") { // from class: android.view.View.12
            @Override // android.util.FloatProperty
            public void setValue(View object, float value) {
                object.setScaleY(value);
            }

            @Override // android.util.Property
            public Float get(View object) {
                return Float.valueOf(object.getScaleY());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: View$TransformationInfo.class */
    public static class TransformationInfo {
        private Matrix mInverseMatrix;
        private final Matrix mMatrix = new Matrix();
        boolean mMatrixDirty = false;
        private boolean mInverseMatrixDirty = true;
        private boolean mMatrixIsIdentity = true;
        private Camera mCamera = null;
        private Matrix matrix3D = null;
        private int mPrevWidth = -1;
        private int mPrevHeight = -1;
        @ViewDebug.ExportedProperty
        float mRotationY = 0.0f;
        @ViewDebug.ExportedProperty
        float mRotationX = 0.0f;
        @ViewDebug.ExportedProperty
        float mRotation = 0.0f;
        @ViewDebug.ExportedProperty
        float mTranslationX = 0.0f;
        @ViewDebug.ExportedProperty
        float mTranslationY = 0.0f;
        @ViewDebug.ExportedProperty
        float mScaleX = 1.0f;
        @ViewDebug.ExportedProperty
        float mScaleY = 1.0f;
        @ViewDebug.ExportedProperty
        float mPivotX = 0.0f;
        @ViewDebug.ExportedProperty
        float mPivotY = 0.0f;
        @ViewDebug.ExportedProperty
        float mAlpha = 1.0f;
        float mTransitionAlpha = 1.0f;

        TransformationInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: View$ListenerInfo.class */
    public static class ListenerInfo {
        protected OnFocusChangeListener mOnFocusChangeListener;
        private ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
        private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;
        public OnClickListener mOnClickListener;
        protected OnLongClickListener mOnLongClickListener;
        protected OnCreateContextMenuListener mOnCreateContextMenuListener;
        private OnKeyListener mOnKeyListener;
        private OnTouchListener mOnTouchListener;
        private OnHoverListener mOnHoverListener;
        private OnGenericMotionListener mOnGenericMotionListener;
        private OnDragListener mOnDragListener;
        private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;

        ListenerInfo() {
        }
    }

    public View(Context context) {
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = false;
        this.mID = -1;
        this.mAccessibilityViewId = -1;
        this.mAccessibilityCursorPosition = -1;
        this.mTransientStateCount = 0;
        this.mClipBounds = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mLabelForId = -1;
        this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
        this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
        this.mDrawableState = null;
        this.mNextFocusLeftId = -1;
        this.mNextFocusRightId = -1;
        this.mNextFocusUpId = -1;
        this.mNextFocusDownId = -1;
        this.mNextFocusForwardId = -1;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mDrawingCacheBackgroundColor = 0;
        this.mAnimator = null;
        this.mLayerType = 0;
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mContext = context;
        this.mResources = context != null ? context.getResources() : null;
        this.mViewFlags = 402653184;
        this.mPrivateFlags2 = 140296;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOverScrollMode(1);
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        if (!sCompatibilityDone && context != null) {
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            sUseBrokenMakeMeasureSpec = targetSdkVersion <= 17;
            sIgnoreMeasureCache = targetSdkVersion < 19;
            sCompatibilityDone = true;
        }
    }

    public View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public View(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View, defStyleAttr, 0);
        Drawable background = null;
        int leftPadding = -1;
        int topPadding = -1;
        int rightPadding = -1;
        int bottomPadding = -1;
        int startPadding = Integer.MIN_VALUE;
        int endPadding = Integer.MIN_VALUE;
        int padding = -1;
        int viewFlagValues = 0;
        int viewFlagMasks = 0;
        boolean setScrollContainer = false;
        int x = 0;
        int y = 0;
        float tx = 0.0f;
        float ty = 0.0f;
        float rotation = 0.0f;
        float rotationX = 0.0f;
        float rotationY = 0.0f;
        float sx = 1.0f;
        float sy = 1.0f;
        boolean transformSet = false;
        int scrollbarStyle = 0;
        int overScrollMode = this.mOverScrollMode;
        boolean initializeScrollbars = false;
        boolean leftPaddingDefined = false;
        boolean rightPaddingDefined = false;
        boolean startPaddingDefined = false;
        boolean endPaddingDefined = false;
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 7:
                    scrollbarStyle = a.getInt(attr, 0);
                    if (scrollbarStyle != 0) {
                        viewFlagValues |= scrollbarStyle & 50331648;
                        viewFlagMasks |= 50331648;
                    } else {
                        continue;
                    }
                case 8:
                    this.mID = a.getResourceId(attr, -1);
                    continue;
                case 9:
                    this.mTag = a.getText(attr);
                    continue;
                case 10:
                    x = a.getDimensionPixelOffset(attr, 0);
                    continue;
                case 11:
                    y = a.getDimensionPixelOffset(attr, 0);
                    continue;
                case 12:
                    background = a.getDrawable(attr);
                    continue;
                case 13:
                    padding = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingLeftInitial = padding;
                    this.mUserPaddingRightInitial = padding;
                    leftPaddingDefined = true;
                    rightPaddingDefined = true;
                    continue;
                case 14:
                    leftPadding = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingLeftInitial = leftPadding;
                    leftPaddingDefined = true;
                    continue;
                case 15:
                    topPadding = a.getDimensionPixelSize(attr, -1);
                    continue;
                case 16:
                    rightPadding = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingRightInitial = rightPadding;
                    rightPaddingDefined = true;
                    continue;
                case 17:
                    bottomPadding = a.getDimensionPixelSize(attr, -1);
                    continue;
                case 18:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 1;
                        viewFlagMasks |= 1;
                    } else {
                        continue;
                    }
                case 19:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 262145;
                        viewFlagMasks |= 262145;
                    } else {
                        continue;
                    }
                case 20:
                    int visibility = a.getInt(attr, 0);
                    if (visibility != 0) {
                        viewFlagValues |= VISIBILITY_FLAGS[visibility];
                        viewFlagMasks |= 12;
                    } else {
                        continue;
                    }
                case 21:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 2;
                        viewFlagMasks |= 2;
                    } else {
                        continue;
                    }
                case 22:
                    int scrollbars = a.getInt(attr, 0);
                    if (scrollbars != 0) {
                        viewFlagValues |= scrollbars;
                        viewFlagMasks |= 768;
                        initializeScrollbars = true;
                    } else {
                        continue;
                    }
                case 23:
                    if (targetSdkVersion >= 14) {
                        continue;
                    }
                    break;
                case 24:
                case 42:
                case 43:
                case 44:
                default:
                case 25:
                    this.mNextFocusLeftId = a.getResourceId(attr, -1);
                    continue;
                case 26:
                    this.mNextFocusRightId = a.getResourceId(attr, -1);
                    continue;
                case 27:
                    this.mNextFocusUpId = a.getResourceId(attr, -1);
                    continue;
                case 28:
                    this.mNextFocusDownId = a.getResourceId(attr, -1);
                    continue;
                case 29:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 16384;
                        viewFlagMasks |= 16384;
                    } else {
                        continue;
                    }
                case 30:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 2097152;
                        viewFlagMasks |= 2097152;
                    } else {
                        continue;
                    }
                case 31:
                    if (a.getBoolean(attr, true)) {
                        continue;
                    } else {
                        viewFlagValues |= 65536;
                        viewFlagMasks |= 65536;
                    }
                case 32:
                    int cacheQuality = a.getInt(attr, 0);
                    if (cacheQuality != 0) {
                        viewFlagValues |= DRAWING_CACHE_QUALITY_FLAGS[cacheQuality];
                        viewFlagMasks |= DRAWING_CACHE_QUALITY_MASK;
                    } else {
                        continue;
                    }
                case 33:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 4194304;
                        viewFlagMasks |= 4194304;
                    } else {
                        continue;
                    }
                case 34:
                    this.mMinWidth = a.getDimensionPixelSize(attr, 0);
                    continue;
                case 35:
                    this.mMinHeight = a.getDimensionPixelSize(attr, 0);
                    continue;
                case 36:
                    if (a.getBoolean(attr, true)) {
                        continue;
                    } else {
                        viewFlagValues &= -134217729;
                        viewFlagMasks |= 134217728;
                    }
                case 37:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 67108864;
                        viewFlagMasks |= 67108864;
                    } else {
                        continue;
                    }
                case 38:
                    setScrollContainer = true;
                    if (a.getBoolean(attr, false)) {
                        setScrollContainer(true);
                    } else {
                        continue;
                    }
                case 39:
                    if (a.getBoolean(attr, true)) {
                        continue;
                    } else {
                        viewFlagValues &= -268435457;
                        viewFlagMasks |= 268435456;
                    }
                case 40:
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                    }
                    final String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnClickListener(new OnClickListener() { // from class: android.view.View.1
                            private Method mHandler;

                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                if (this.mHandler == null) {
                                    try {
                                        this.mHandler = View.this.getContext().getClass().getMethod(handlerName, View.class);
                                    } catch (NoSuchMethodException e) {
                                        int id = View.this.getId();
                                        String idText = id == -1 ? "" : " with id '" + View.this.getContext().getResources().getResourceEntryName(id) + Separators.QUOTE;
                                        throw new IllegalStateException("Could not find a method " + handlerName + "(View) in the activity " + View.this.getContext().getClass() + " for onClick handler on view " + View.this.getClass() + idText, e);
                                    }
                                }
                                try {
                                    this.mHandler.invoke(View.this.getContext(), View.this);
                                } catch (IllegalAccessException e2) {
                                    throw new IllegalStateException("Could not execute non public method of the activity", e2);
                                } catch (InvocationTargetException e3) {
                                    throw new IllegalStateException("Could not execute method of the activity", e3);
                                }
                            }
                        });
                    } else {
                        continue;
                    }
                case 41:
                    setContentDescription(a.getString(attr));
                    continue;
                case 45:
                    overScrollMode = a.getInt(attr, 1);
                    continue;
                case 46:
                    if (a.getBoolean(attr, false)) {
                        viewFlagValues |= 1024;
                        viewFlagMasks |= 1024;
                    } else {
                        continue;
                    }
                case 47:
                    setAlpha(a.getFloat(attr, 1.0f));
                    continue;
                case 48:
                    setPivotX(a.getDimensionPixelOffset(attr, 0));
                    continue;
                case 49:
                    setPivotY(a.getDimensionPixelOffset(attr, 0));
                    continue;
                case 50:
                    tx = a.getDimensionPixelOffset(attr, 0);
                    transformSet = true;
                    continue;
                case 51:
                    ty = a.getDimensionPixelOffset(attr, 0);
                    transformSet = true;
                    continue;
                case 52:
                    sx = a.getFloat(attr, 1.0f);
                    transformSet = true;
                    continue;
                case 53:
                    sy = a.getFloat(attr, 1.0f);
                    transformSet = true;
                    continue;
                case 54:
                    rotation = a.getFloat(attr, 0.0f);
                    transformSet = true;
                    continue;
                case 55:
                    rotationX = a.getFloat(attr, 0.0f);
                    transformSet = true;
                    continue;
                case 56:
                    rotationY = a.getFloat(attr, 0.0f);
                    transformSet = true;
                    continue;
                case 57:
                    this.mVerticalScrollbarPosition = a.getInt(attr, 0);
                    continue;
                case 58:
                    this.mNextFocusForwardId = a.getResourceId(attr, -1);
                    continue;
                case 59:
                    setLayerType(a.getInt(attr, 0), null);
                    continue;
                case 60:
                    break;
                case 61:
                    setImportantForAccessibility(a.getInt(attr, 0));
                    continue;
                case 62:
                    this.mPrivateFlags2 &= -449;
                    int textDirection = a.getInt(attr, -1);
                    if (textDirection != -1) {
                        this.mPrivateFlags2 |= PFLAG2_TEXT_DIRECTION_FLAGS[textDirection];
                    } else {
                        continue;
                    }
                case 63:
                    this.mPrivateFlags2 &= -57345;
                    int textAlignment = a.getInt(attr, 1);
                    this.mPrivateFlags2 |= PFLAG2_TEXT_ALIGNMENT_FLAGS[textAlignment];
                    continue;
                case 64:
                    this.mPrivateFlags2 &= -61;
                    int layoutDirection = a.getInt(attr, -1);
                    int value = layoutDirection != -1 ? LAYOUT_DIRECTION_FLAGS[layoutDirection] : 2;
                    this.mPrivateFlags2 |= value << 2;
                    continue;
                case 65:
                    startPadding = a.getDimensionPixelSize(attr, Integer.MIN_VALUE);
                    startPaddingDefined = startPadding != Integer.MIN_VALUE;
                    continue;
                case 66:
                    endPadding = a.getDimensionPixelSize(attr, Integer.MIN_VALUE);
                    endPaddingDefined = endPadding != Integer.MIN_VALUE;
                    continue;
                case 67:
                    setLabelFor(a.getResourceId(attr, -1));
                    continue;
                case 68:
                    setAccessibilityLiveRegion(a.getInt(attr, 0));
                    continue;
            }
            int fadingEdge = a.getInt(attr, 0);
            if (fadingEdge != 0) {
                viewFlagValues |= fadingEdge;
                viewFlagMasks |= 12288;
                initializeFadingEdge(a);
            }
        }
        setOverScrollMode(overScrollMode);
        this.mUserPaddingStart = startPadding;
        this.mUserPaddingEnd = endPadding;
        if (background != null) {
            setBackground(background);
        }
        if (padding >= 0) {
            leftPadding = padding;
            topPadding = padding;
            rightPadding = padding;
            bottomPadding = padding;
            this.mUserPaddingLeftInitial = padding;
            this.mUserPaddingRightInitial = padding;
        }
        if (isRtlCompatibilityMode()) {
            if (!leftPaddingDefined && startPaddingDefined) {
                leftPadding = startPadding;
            }
            this.mUserPaddingLeftInitial = leftPadding >= 0 ? leftPadding : this.mUserPaddingLeftInitial;
            if (!rightPaddingDefined && endPaddingDefined) {
                rightPadding = endPadding;
            }
            this.mUserPaddingRightInitial = rightPadding >= 0 ? rightPadding : this.mUserPaddingRightInitial;
        } else {
            boolean hasRelativePadding = startPaddingDefined || endPaddingDefined;
            if (leftPaddingDefined && !hasRelativePadding) {
                this.mUserPaddingLeftInitial = leftPadding;
            }
            if (rightPaddingDefined && !hasRelativePadding) {
                this.mUserPaddingRightInitial = rightPadding;
            }
        }
        internalSetPadding(this.mUserPaddingLeftInitial, topPadding >= 0 ? topPadding : this.mPaddingTop, this.mUserPaddingRightInitial, bottomPadding >= 0 ? bottomPadding : this.mPaddingBottom);
        if (viewFlagMasks != 0) {
            setFlags(viewFlagValues, viewFlagMasks);
        }
        if (initializeScrollbars) {
            initializeScrollbars(a);
        }
        a.recycle();
        if (scrollbarStyle != 0) {
            recomputePadding();
        }
        if (x != 0 || y != 0) {
            scrollTo(x, y);
        }
        if (transformSet) {
            setTranslationX(tx);
            setTranslationY(ty);
            setRotation(rotation);
            setRotationX(rotationX);
            setRotationY(rotationY);
            setScaleX(sx);
            setScaleY(sy);
        }
        if (!setScrollContainer && (viewFlagValues & 512) != 0) {
            setScrollContainer(true);
        }
        computeOpaqueFlags();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View() {
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = false;
        this.mID = -1;
        this.mAccessibilityViewId = -1;
        this.mAccessibilityCursorPosition = -1;
        this.mTransientStateCount = 0;
        this.mClipBounds = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mLabelForId = -1;
        this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
        this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
        this.mDrawableState = null;
        this.mNextFocusLeftId = -1;
        this.mNextFocusRightId = -1;
        this.mNextFocusUpId = -1;
        this.mNextFocusDownId = -1;
        this.mNextFocusForwardId = -1;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mDrawingCacheBackgroundColor = 0;
        this.mAnimator = null;
        this.mLayerType = 0;
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mResources = null;
    }

    public String toString() {
        String pkgname;
        StringBuilder out = new StringBuilder(128);
        out.append(getClass().getName());
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(this)));
        out.append(' ');
        switch (this.mViewFlags & 12) {
            case 0:
                out.append('V');
                break;
            case 4:
                out.append('I');
                break;
            case 8:
                out.append('G');
                break;
            default:
                out.append('.');
                break;
        }
        out.append((this.mViewFlags & 1) == 1 ? 'F' : '.');
        out.append((this.mViewFlags & 32) == 0 ? 'E' : '.');
        out.append((this.mViewFlags & 128) == 128 ? '.' : 'D');
        out.append((this.mViewFlags & 256) != 0 ? 'H' : '.');
        out.append((this.mViewFlags & 512) != 0 ? 'V' : '.');
        out.append((this.mViewFlags & 16384) != 0 ? 'C' : '.');
        out.append((this.mViewFlags & 2097152) != 0 ? 'L' : '.');
        out.append(' ');
        out.append((this.mPrivateFlags & 8) != 0 ? 'R' : '.');
        out.append((this.mPrivateFlags & 2) != 0 ? 'F' : '.');
        out.append((this.mPrivateFlags & 4) != 0 ? 'S' : '.');
        if ((this.mPrivateFlags & 33554432) != 0) {
            out.append('p');
        } else {
            out.append((this.mPrivateFlags & 16384) != 0 ? 'P' : '.');
        }
        out.append((this.mPrivateFlags & 268435456) != 0 ? 'H' : '.');
        out.append((this.mPrivateFlags & 1073741824) != 0 ? 'A' : '.');
        out.append((this.mPrivateFlags & Integer.MIN_VALUE) != 0 ? 'I' : '.');
        out.append((this.mPrivateFlags & 6291456) != 0 ? 'D' : '.');
        out.append(' ');
        out.append(this.mLeft);
        out.append(',');
        out.append(this.mTop);
        out.append('-');
        out.append(this.mRight);
        out.append(',');
        out.append(this.mBottom);
        int id = getId();
        if (id != -1) {
            out.append(" #");
            out.append(Integer.toHexString(id));
            Resources r = this.mResources;
            if (id != 0 && r != null) {
                try {
                    switch (id & (-16777216)) {
                        case 16777216:
                            pkgname = "android";
                            break;
                        case 2130706432:
                            pkgname = "app";
                            break;
                        default:
                            pkgname = r.getResourcePackageName(id);
                            break;
                    }
                    String typename = r.getResourceTypeName(id);
                    String entryname = r.getResourceEntryName(id);
                    out.append(Separators.SP);
                    out.append(pkgname);
                    out.append(Separators.COLON);
                    out.append(typename);
                    out.append(Separators.SLASH);
                    out.append(entryname);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
        out.append("}");
        return out.toString();
    }

    protected void initializeFadingEdge(TypedArray a) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = a.getDimensionPixelSize(24, ViewConfiguration.get(this.mContext).getScaledFadingEdgeLength());
    }

    public int getVerticalFadingEdgeLength() {
        ScrollabilityCache cache;
        if (isVerticalFadingEdgeEnabled() && (cache = this.mScrollCache) != null) {
            return cache.fadingEdgeLength;
        }
        return 0;
    }

    public void setFadingEdgeLength(int length) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = length;
    }

    public int getHorizontalFadingEdgeLength() {
        ScrollabilityCache cache;
        if (isHorizontalFadingEdgeEnabled() && (cache = this.mScrollCache) != null) {
            return cache.fadingEdgeLength;
        }
        return 0;
    }

    public int getVerticalScrollbarWidth() {
        ScrollBarDrawable scrollBar;
        ScrollabilityCache cache = this.mScrollCache;
        if (cache != null && (scrollBar = cache.scrollBar) != null) {
            int size = scrollBar.getSize(true);
            if (size <= 0) {
                size = cache.scrollBarSize;
            }
            return size;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getHorizontalScrollbarHeight() {
        ScrollBarDrawable scrollBar;
        ScrollabilityCache cache = this.mScrollCache;
        if (cache != null && (scrollBar = cache.scrollBar) != null) {
            int size = scrollBar.getSize(false);
            if (size <= 0) {
                size = cache.scrollBarSize;
            }
            return size;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initializeScrollbars(TypedArray a) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache.scrollBar == null) {
            scrollabilityCache.scrollBar = new ScrollBarDrawable();
        }
        boolean fadeScrollbars = a.getBoolean(44, true);
        if (!fadeScrollbars) {
            scrollabilityCache.state = 1;
        }
        scrollabilityCache.fadeScrollBars = fadeScrollbars;
        scrollabilityCache.scrollBarFadeDuration = a.getInt(42, ViewConfiguration.getScrollBarFadeDuration());
        scrollabilityCache.scrollBarDefaultDelayBeforeFade = a.getInt(43, ViewConfiguration.getScrollDefaultDelay());
        scrollabilityCache.scrollBarSize = a.getDimensionPixelSize(0, ViewConfiguration.get(this.mContext).getScaledScrollBarSize());
        scrollabilityCache.scrollBar.setHorizontalTrackDrawable(a.getDrawable(3));
        Drawable thumb = a.getDrawable(1);
        if (thumb != null) {
            scrollabilityCache.scrollBar.setHorizontalThumbDrawable(thumb);
        }
        boolean alwaysDraw = a.getBoolean(5, false);
        if (alwaysDraw) {
            scrollabilityCache.scrollBar.setAlwaysDrawHorizontalTrack(true);
        }
        Drawable track = a.getDrawable(4);
        scrollabilityCache.scrollBar.setVerticalTrackDrawable(track);
        Drawable thumb2 = a.getDrawable(2);
        if (thumb2 != null) {
            scrollabilityCache.scrollBar.setVerticalThumbDrawable(thumb2);
        }
        boolean alwaysDraw2 = a.getBoolean(6, false);
        if (alwaysDraw2) {
            scrollabilityCache.scrollBar.setAlwaysDrawVerticalTrack(true);
        }
        int layoutDirection = getLayoutDirection();
        if (track != null) {
            track.setLayoutDirection(layoutDirection);
        }
        if (thumb2 != null) {
            thumb2.setLayoutDirection(layoutDirection);
        }
        resolvePadding();
    }

    private void initScrollCache() {
        if (this.mScrollCache == null) {
            this.mScrollCache = new ScrollabilityCache(ViewConfiguration.get(this.mContext), this);
        }
    }

    private ScrollabilityCache getScrollCache() {
        initScrollCache();
        return this.mScrollCache;
    }

    public void setVerticalScrollbarPosition(int position) {
        if (this.mVerticalScrollbarPosition != position) {
            this.mVerticalScrollbarPosition = position;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public int getVerticalScrollbarPosition() {
        return this.mVerticalScrollbarPosition;
    }

    ListenerInfo getListenerInfo() {
        if (this.mListenerInfo != null) {
            return this.mListenerInfo;
        }
        this.mListenerInfo = new ListenerInfo();
        return this.mListenerInfo;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        getListenerInfo().mOnFocusChangeListener = l;
    }

    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        ListenerInfo li = getListenerInfo();
        if (li.mOnLayoutChangeListeners == null) {
            li.mOnLayoutChangeListeners = new ArrayList();
        }
        if (!li.mOnLayoutChangeListeners.contains(listener)) {
            li.mOnLayoutChangeListeners.add(listener);
        }
    }

    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnLayoutChangeListeners != null) {
            li.mOnLayoutChangeListeners.remove(listener);
        }
    }

    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        ListenerInfo li = getListenerInfo();
        if (li.mOnAttachStateChangeListeners == null) {
            li.mOnAttachStateChangeListeners = new CopyOnWriteArrayList();
        }
        li.mOnAttachStateChangeListeners.add(listener);
    }

    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnAttachStateChangeListeners != null) {
            li.mOnAttachStateChangeListeners.remove(listener);
        }
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        ListenerInfo li = this.mListenerInfo;
        if (li != null) {
            return li.mOnFocusChangeListener;
        }
        return null;
    }

    public void setOnClickListener(OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        getListenerInfo().mOnClickListener = l;
    }

    public boolean hasOnClickListeners() {
        ListenerInfo li = this.mListenerInfo;
        return (li == null || li.mOnClickListener == null) ? false : true;
    }

    public void setOnLongClickListener(OnLongClickListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnLongClickListener = l;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnCreateContextMenuListener = l;
    }

    public boolean performClick() {
        sendAccessibilityEvent(1);
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnClickListener != null) {
            playSoundEffect(0);
            li.mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    public boolean callOnClick() {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnClickListener != null) {
            li.mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    public boolean performLongClick() {
        sendAccessibilityEvent(2);
        boolean handled = false;
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnLongClickListener != null) {
            handled = li.mOnLongClickListener.onLongClick(this);
        }
        if (!handled) {
            handled = showContextMenu();
        }
        if (handled) {
            performHapticFeedback(0);
        }
        return handled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean performButtonActionOnTouchDown(MotionEvent event) {
        if ((event.getButtonState() & 2) != 0 && showContextMenu(event.getX(), event.getY(), event.getMetaState())) {
            return true;
        }
        return false;
    }

    public boolean showContextMenu() {
        return getParent().showContextMenuForChild(this);
    }

    public boolean showContextMenu(float x, float y, int metaState) {
        return showContextMenu();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        return parent.startActionModeForChild(this, callback);
    }

    public void setOnKeyListener(OnKeyListener l) {
        getListenerInfo().mOnKeyListener = l;
    }

    public void setOnTouchListener(OnTouchListener l) {
        getListenerInfo().mOnTouchListener = l;
    }

    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        getListenerInfo().mOnGenericMotionListener = l;
    }

    public void setOnHoverListener(OnHoverListener l) {
        getListenerInfo().mOnHoverListener = l;
    }

    public void setOnDragListener(OnDragListener l) {
        getListenerInfo().mOnDragListener = l;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleFocusGainInternal(int direction, Rect previouslyFocusedRect) {
        if ((this.mPrivateFlags & 2) == 0) {
            this.mPrivateFlags |= 2;
            View oldFocus = this.mAttachInfo != null ? getRootView().findFocus() : null;
            if (this.mParent != null) {
                this.mParent.requestChildFocus(this, this);
            }
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, this);
            }
            onFocusChanged(true, direction, previouslyFocusedRect);
            refreshDrawableState();
        }
    }

    public boolean requestRectangleOnScreen(Rect rectangle) {
        return requestRectangleOnScreen(rectangle, false);
    }

    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        if (this.mParent == null) {
            return false;
        }
        View child = this;
        RectF position = this.mAttachInfo != null ? this.mAttachInfo.mTmpTransformRect : new RectF();
        position.set(rectangle);
        ViewParent parent = this.mParent;
        boolean scrolled = false;
        while (parent != null) {
            rectangle.set((int) position.left, (int) position.top, (int) position.right, (int) position.bottom);
            scrolled |= parent.requestChildRectangleOnScreen(child, rectangle, immediate);
            if (!child.hasIdentityMatrix()) {
                child.getMatrix().mapRect(position);
            }
            position.offset(child.mLeft, child.mTop);
            if (!(parent instanceof View)) {
                break;
            }
            View parentView = (View) parent;
            position.offset(-parentView.getScrollX(), -parentView.getScrollY());
            child = parentView;
            parent = child.getParent();
        }
        return scrolled;
    }

    public void clearFocus() {
        clearFocusInternal(true, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearFocusInternal(boolean propagate, boolean refocus) {
        if ((this.mPrivateFlags & 2) != 0) {
            this.mPrivateFlags &= -3;
            if (propagate && this.mParent != null) {
                this.mParent.clearChildFocus(this);
            }
            onFocusChanged(false, 0, null);
            refreshDrawableState();
            if (propagate) {
                if (!refocus || !rootViewRequestFocus()) {
                    notifyGlobalFocusCleared(this);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyGlobalFocusCleared(View oldFocus) {
        if (oldFocus != null && this.mAttachInfo != null) {
            this.mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean rootViewRequestFocus() {
        View root = getRootView();
        return root != null && root.requestFocus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unFocus() {
        clearFocusInternal(false, false);
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public boolean hasFocus() {
        return (this.mPrivateFlags & 2) != 0;
    }

    public boolean hasFocusable() {
        return (this.mViewFlags & 12) == 0 && isFocusable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            sendAccessibilityEvent(8);
        } else {
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (!gainFocus) {
            if (isPressed()) {
                setPressed(false);
            }
            if (imm != null && this.mAttachInfo != null && this.mAttachInfo.mHasWindowFocus) {
                imm.focusOut(this);
            }
            onFocusLost();
        } else if (imm != null && this.mAttachInfo != null && this.mAttachInfo.mHasWindowFocus) {
            imm.focusIn(this);
        }
        invalidate(true);
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnFocusChangeListener != null) {
            li.mOnFocusChangeListener.onFocusChange(this, gainFocus);
        }
        if (this.mAttachInfo != null) {
            this.mAttachInfo.mKeyDispatchState.reset(this);
        }
    }

    @Override // android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int eventType) {
        if (!includeForAccessibility()) {
            return;
        }
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.sendAccessibilityEvent(this, eventType);
        } else {
            sendAccessibilityEventInternal(eventType);
        }
    }

    public void announceForAccessibility(CharSequence text) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && this.mParent != null) {
            AccessibilityEvent event = AccessibilityEvent.obtain(16384);
            onInitializeAccessibilityEvent(event);
            event.getText().add(text);
            event.setContentDescription(null);
            this.mParent.requestSendAccessibilityEvent(this, event);
        }
    }

    void sendAccessibilityEventInternal(int eventType) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            sendAccessibilityEventUnchecked(AccessibilityEvent.obtain(eventType));
        }
    }

    @Override // android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.sendAccessibilityEventUnchecked(this, event);
        } else {
            sendAccessibilityEventUncheckedInternal(event);
        }
    }

    void sendAccessibilityEventUncheckedInternal(AccessibilityEvent event) {
        if (!isShown()) {
            return;
        }
        onInitializeAccessibilityEvent(event);
        if ((event.getEventType() & POPULATING_ACCESSIBILITY_EVENT_TYPES) != 0) {
            dispatchPopulateAccessibilityEvent(event);
        }
        getParent().requestSendAccessibilityEvent(this, event);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.dispatchPopulateAccessibilityEvent(this, event);
        }
        return dispatchPopulateAccessibilityEventInternal(event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return false;
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.onPopulateAccessibilityEvent(this, event);
        } else {
            onPopulateAccessibilityEventInternal(event);
        }
    }

    void onPopulateAccessibilityEventInternal(AccessibilityEvent event) {
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.onInitializeAccessibilityEvent(this, event);
        } else {
            onInitializeAccessibilityEventInternal(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        event.setSource(this);
        event.setClassName(View.class.getName());
        event.setPackageName(getContext().getPackageName());
        event.setEnabled(isEnabled());
        event.setContentDescription(this.mContentDescription);
        switch (event.getEventType()) {
            case 8:
                ArrayList<View> focusablesTempList = this.mAttachInfo != null ? this.mAttachInfo.mTempArrayList : new ArrayList<>();
                getRootView().addFocusables(focusablesTempList, 2, 0);
                event.setItemCount(focusablesTempList.size());
                event.setCurrentItemIndex(focusablesTempList.indexOf(this));
                if (this.mAttachInfo != null) {
                    focusablesTempList.clear();
                    return;
                }
                return;
            case 8192:
                CharSequence text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    event.setFromIndex(getAccessibilitySelectionStart());
                    event.setToIndex(getAccessibilitySelectionEnd());
                    event.setItemCount(text.length());
                    return;
                }
                return;
            default:
                return;
        }
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.createAccessibilityNodeInfo(this);
        }
        return createAccessibilityNodeInfoInternal();
    }

    AccessibilityNodeInfo createAccessibilityNodeInfoInternal() {
        AccessibilityNodeProvider provider = getAccessibilityNodeProvider();
        if (provider != null) {
            return provider.createAccessibilityNodeInfo(-1);
        }
        AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain(this);
        onInitializeAccessibilityNodeInfo(info);
        return info;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.onInitializeAccessibilityNodeInfo(this, info);
        } else {
            onInitializeAccessibilityNodeInfoInternal(info);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getBoundsOnScreen(Rect outRect) {
        ViewParent parent;
        if (this.mAttachInfo == null) {
            return;
        }
        RectF position = this.mAttachInfo.mTmpTransformRect;
        position.set(0.0f, 0.0f, this.mRight - this.mLeft, this.mBottom - this.mTop);
        if (!hasIdentityMatrix()) {
            getMatrix().mapRect(position);
        }
        position.offset(this.mLeft, this.mTop);
        ViewParent viewParent = this.mParent;
        while (true) {
            parent = viewParent;
            if (!(parent instanceof View)) {
                break;
            }
            View parentView = (View) parent;
            position.offset(-parentView.mScrollX, -parentView.mScrollY);
            if (!parentView.hasIdentityMatrix()) {
                parentView.getMatrix().mapRect(position);
            }
            position.offset(parentView.mLeft, parentView.mTop);
            viewParent = parentView.mParent;
        }
        if (parent instanceof ViewRootImpl) {
            ViewRootImpl viewRootImpl = (ViewRootImpl) parent;
            position.offset(0.0f, -viewRootImpl.mCurScrollY);
        }
        position.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
        outRect.set((int) (position.left + 0.5f), (int) (position.top + 0.5f), (int) (position.right + 0.5f), (int) (position.bottom + 0.5f));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        Rect bounds = this.mAttachInfo.mTmpInvalRect;
        getDrawingRect(bounds);
        info.setBoundsInParent(bounds);
        getBoundsOnScreen(bounds);
        info.setBoundsInScreen(bounds);
        ViewParent parent = getParentForAccessibility();
        if (parent instanceof View) {
            info.setParent((View) parent);
        }
        if (this.mID != -1) {
            View rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            View label = rootView.findLabelForView(this, this.mID);
            if (label != null) {
                info.setLabeledBy(label);
            }
            if ((this.mAttachInfo.mAccessibilityFetchFlags & 16) != 0 && Resources.resourceHasPackage(this.mID)) {
                try {
                    String viewId = getResources().getResourceName(this.mID);
                    info.setViewIdResourceName(viewId);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
        if (this.mLabelForId != -1) {
            View rootView2 = getRootView();
            if (rootView2 == null) {
                rootView2 = this;
            }
            View labeled = rootView2.findViewInsideOutShouldExist(this, this.mLabelForId);
            if (labeled != null) {
                info.setLabelFor(labeled);
            }
        }
        info.setVisibleToUser(isVisibleToUser());
        info.setPackageName(this.mContext.getPackageName());
        info.setClassName(View.class.getName());
        info.setContentDescription(getContentDescription());
        info.setEnabled(isEnabled());
        info.setClickable(isClickable());
        info.setFocusable(isFocusable());
        info.setFocused(isFocused());
        info.setAccessibilityFocused(isAccessibilityFocused());
        info.setSelected(isSelected());
        info.setLongClickable(isLongClickable());
        info.setLiveRegion(getAccessibilityLiveRegion());
        info.addAction(4);
        info.addAction(8);
        if (isFocusable()) {
            if (isFocused()) {
                info.addAction(2);
            } else {
                info.addAction(1);
            }
        }
        if (!isAccessibilityFocused()) {
            info.addAction(64);
        } else {
            info.addAction(128);
        }
        if (isClickable() && isEnabled()) {
            info.addAction(16);
        }
        if (isLongClickable() && isEnabled()) {
            info.addAction(32);
        }
        CharSequence text = getIterableTextForAccessibility();
        if (text != null && text.length() > 0) {
            info.setTextSelection(getAccessibilitySelectionStart(), getAccessibilitySelectionEnd());
            info.addAction(131072);
            info.addAction(256);
            info.addAction(512);
            info.setMovementGranularities(11);
        }
    }

    private View findLabelForView(View view, int labeledId) {
        if (this.mMatchLabelForPredicate == null) {
            this.mMatchLabelForPredicate = new MatchLabelForPredicate();
        }
        this.mMatchLabelForPredicate.mLabeledId = labeledId;
        return findViewByPredicateInsideOut(view, this.mMatchLabelForPredicate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isVisibleToUser() {
        return isVisibleToUser(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isVisibleToUser(Rect boundInView) {
        if (this.mAttachInfo == null || this.mAttachInfo.mWindowVisibility != 0) {
            return false;
        }
        Object obj = this;
        while (true) {
            Object current = obj;
            if (current instanceof View) {
                View view = (View) current;
                if (view.getAlpha() <= 0.0f || view.getTransitionAlpha() <= 0.0f || view.getVisibility() != 0) {
                    return false;
                }
                obj = view.mParent;
            } else {
                Rect visibleRect = this.mAttachInfo.mTmpInvalRect;
                Point offset = this.mAttachInfo.mPoint;
                if (!getGlobalVisibleRect(visibleRect, offset)) {
                    return false;
                }
                if (boundInView != null) {
                    visibleRect.offset(-offset.x, -offset.y);
                    return boundInView.intersect(visibleRect);
                }
                return true;
            }
        }
    }

    public AccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
        this.mAccessibilityDelegate = delegate;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.getAccessibilityNodeProvider(this);
        }
        return null;
    }

    public int getAccessibilityViewId() {
        if (this.mAccessibilityViewId == -1) {
            int i = sNextAccessibilityViewId;
            sNextAccessibilityViewId = i + 1;
            this.mAccessibilityViewId = i;
        }
        return this.mAccessibilityViewId;
    }

    public int getAccessibilityWindowId() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mAccessibilityWindowId;
        }
        return -1;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @RemotableViewMethod
    public void setContentDescription(CharSequence contentDescription) {
        if (this.mContentDescription == null) {
            if (contentDescription == null) {
                return;
            }
        } else if (this.mContentDescription.equals(contentDescription)) {
            return;
        }
        this.mContentDescription = contentDescription;
        boolean nonEmptyDesc = contentDescription != null && contentDescription.length() > 0;
        if (nonEmptyDesc && getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
            notifySubtreeAccessibilityStateChangedIfNeeded();
            return;
        }
        notifyViewAccessibilityStateChangedIfNeeded(4);
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public int getLabelFor() {
        return this.mLabelForId;
    }

    @RemotableViewMethod
    public void setLabelFor(int id) {
        this.mLabelForId = id;
        if (this.mLabelForId != -1 && this.mID == -1) {
            this.mID = generateViewId();
        }
    }

    protected void onFocusLost() {
        resetPressedState();
    }

    private void resetPressedState() {
        if ((this.mViewFlags & 32) != 32 && isPressed()) {
            setPressed(false);
            if (!this.mHasPerformedLongPress) {
                removeLongPressCallback();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public boolean isFocused() {
        return (this.mPrivateFlags & 2) != 0;
    }

    public View findFocus() {
        if ((this.mPrivateFlags & 2) != 0) {
            return this;
        }
        return null;
    }

    public boolean isScrollContainer() {
        return (this.mPrivateFlags & 1048576) != 0;
    }

    public void setScrollContainer(boolean isScrollContainer) {
        if (isScrollContainer) {
            if (this.mAttachInfo != null && (this.mPrivateFlags & 1048576) == 0) {
                this.mAttachInfo.mScrollContainers.add(this);
                this.mPrivateFlags |= 1048576;
            }
            this.mPrivateFlags |= 524288;
            return;
        }
        if ((this.mPrivateFlags & 1048576) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
        }
        this.mPrivateFlags &= -1572865;
    }

    public int getDrawingCacheQuality() {
        return this.mViewFlags & DRAWING_CACHE_QUALITY_MASK;
    }

    public void setDrawingCacheQuality(int quality) {
        setFlags(quality, DRAWING_CACHE_QUALITY_MASK);
    }

    public boolean getKeepScreenOn() {
        return (this.mViewFlags & 67108864) != 0;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        setFlags(keepScreenOn ? 67108864 : 0, 67108864);
    }

    public int getNextFocusLeftId() {
        return this.mNextFocusLeftId;
    }

    public void setNextFocusLeftId(int nextFocusLeftId) {
        this.mNextFocusLeftId = nextFocusLeftId;
    }

    public int getNextFocusRightId() {
        return this.mNextFocusRightId;
    }

    public void setNextFocusRightId(int nextFocusRightId) {
        this.mNextFocusRightId = nextFocusRightId;
    }

    public int getNextFocusUpId() {
        return this.mNextFocusUpId;
    }

    public void setNextFocusUpId(int nextFocusUpId) {
        this.mNextFocusUpId = nextFocusUpId;
    }

    public int getNextFocusDownId() {
        return this.mNextFocusDownId;
    }

    public void setNextFocusDownId(int nextFocusDownId) {
        this.mNextFocusDownId = nextFocusDownId;
    }

    public int getNextFocusForwardId() {
        return this.mNextFocusForwardId;
    }

    public void setNextFocusForwardId(int nextFocusForwardId) {
        this.mNextFocusForwardId = nextFocusForwardId;
    }

    public boolean isShown() {
        ViewParent parent;
        View current = this;
        while ((current.mViewFlags & 12) == 0 && (parent = current.mParent) != null) {
            if (!(parent instanceof View)) {
                return true;
            }
            current = (View) parent;
            if (current == null) {
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean fitSystemWindows(Rect insets) {
        if ((this.mViewFlags & 2) == 2) {
            this.mUserPaddingStart = Integer.MIN_VALUE;
            this.mUserPaddingEnd = Integer.MIN_VALUE;
            Rect localInsets = sThreadLocal.get();
            if (localInsets == null) {
                localInsets = new Rect();
                sThreadLocal.set(localInsets);
            }
            boolean res = computeFitSystemWindows(insets, localInsets);
            internalSetPadding(localInsets.left, localInsets.top, localInsets.right, localInsets.bottom);
            return res;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean computeFitSystemWindows(Rect inoutInsets, Rect outLocalInsets) {
        if ((this.mViewFlags & 2048) == 0 || this.mAttachInfo == null || ((this.mAttachInfo.mSystemUiVisibility & 1536) == 0 && !this.mAttachInfo.mOverscanRequested)) {
            outLocalInsets.set(inoutInsets);
            inoutInsets.set(0, 0, 0, 0);
            return true;
        }
        Rect overscan = this.mAttachInfo.mOverscanInsets;
        outLocalInsets.set(overscan);
        inoutInsets.left -= overscan.left;
        inoutInsets.top -= overscan.top;
        inoutInsets.right -= overscan.right;
        inoutInsets.bottom -= overscan.bottom;
        return false;
    }

    public void setFitsSystemWindows(boolean fitSystemWindows) {
        setFlags(fitSystemWindows ? 2 : 0, 2);
    }

    public boolean getFitsSystemWindows() {
        return (this.mViewFlags & 2) == 2;
    }

    public boolean fitsSystemWindows() {
        return getFitsSystemWindows();
    }

    public void requestFitSystemWindows() {
        if (this.mParent != null) {
            this.mParent.requestFitSystemWindows();
        }
    }

    public void makeOptionalFitsSystemWindows() {
        setFlags(2048, 2048);
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, to = "VISIBLE"), @ViewDebug.IntToString(from = 4, to = "INVISIBLE"), @ViewDebug.IntToString(from = 8, to = "GONE")})
    public int getVisibility() {
        return this.mViewFlags & 12;
    }

    @RemotableViewMethod
    public void setVisibility(int visibility) {
        setFlags(visibility, 12);
        if (this.mBackground != null) {
            this.mBackground.setVisible(visibility == 0, false);
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isEnabled() {
        return (this.mViewFlags & 32) == 0;
    }

    @RemotableViewMethod
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        setFlags(enabled ? 0 : 32, 32);
        refreshDrawableState();
        invalidate(true);
        if (!enabled) {
            cancelPendingInputEvents();
        }
    }

    public void setFocusable(boolean focusable) {
        if (!focusable) {
            setFlags(0, 262144);
        }
        setFlags(focusable ? 1 : 0, 1);
    }

    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        setFlags(focusableInTouchMode ? 262144 : 0, 262144);
        if (focusableInTouchMode) {
            setFlags(1, 1);
        }
    }

    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        setFlags(soundEffectsEnabled ? 134217728 : 0, 134217728);
    }

    @ViewDebug.ExportedProperty
    public boolean isSoundEffectsEnabled() {
        return 134217728 == (this.mViewFlags & 134217728);
    }

    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        setFlags(hapticFeedbackEnabled ? 268435456 : 0, 268435456);
    }

    @ViewDebug.ExportedProperty
    public boolean isHapticFeedbackEnabled() {
        return 268435456 == (this.mViewFlags & 268435456);
    }

    @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = 0, to = "LTR"), @ViewDebug.IntToString(from = 1, to = "RTL"), @ViewDebug.IntToString(from = 2, to = "INHERIT"), @ViewDebug.IntToString(from = 3, to = "LOCALE")})
    public int getRawLayoutDirection() {
        return (this.mPrivateFlags2 & 12) >> 2;
    }

    @RemotableViewMethod
    public void setLayoutDirection(int layoutDirection) {
        if (getRawLayoutDirection() != layoutDirection) {
            this.mPrivateFlags2 &= -13;
            resetRtlProperties();
            this.mPrivateFlags2 |= (layoutDirection << 2) & 12;
            resolveRtlPropertiesIfNeeded();
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = 0, to = "RESOLVED_DIRECTION_LTR"), @ViewDebug.IntToString(from = 1, to = "RESOLVED_DIRECTION_RTL")})
    public int getLayoutDirection() {
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        if (targetSdkVersion >= 17) {
            return (this.mPrivateFlags2 & 16) == 16 ? 1 : 0;
        }
        this.mPrivateFlags2 |= 32;
        return 0;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public boolean hasTransientState() {
        return (this.mPrivateFlags2 & Integer.MIN_VALUE) == Integer.MIN_VALUE;
    }

    public void setHasTransientState(boolean hasTransientState) {
        this.mTransientStateCount = hasTransientState ? this.mTransientStateCount + 1 : this.mTransientStateCount - 1;
        if (this.mTransientStateCount < 0) {
            this.mTransientStateCount = 0;
            Log.e(VIEW_LOG_TAG, "hasTransientState decremented below 0: unmatched pair of setHasTransientState calls");
        } else if ((hasTransientState && this.mTransientStateCount == 1) || (!hasTransientState && this.mTransientStateCount == 0)) {
            this.mPrivateFlags2 = (this.mPrivateFlags2 & Integer.MAX_VALUE) | (hasTransientState ? Integer.MIN_VALUE : 0);
            if (this.mParent != null) {
                try {
                    this.mParent.childHasTransientStateChanged(this, hasTransientState);
                } catch (AbstractMethodError e) {
                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        }
    }

    public boolean isAttachedToWindow() {
        return this.mAttachInfo != null;
    }

    public boolean isLaidOut() {
        return (this.mPrivateFlags3 & 4) == 4;
    }

    public void setWillNotDraw(boolean willNotDraw) {
        setFlags(willNotDraw ? 128 : 0, 128);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean willNotDraw() {
        return (this.mViewFlags & 128) == 128;
    }

    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        setFlags(willNotCacheDrawing ? 131072 : 0, 131072);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean willNotCacheDrawing() {
        return (this.mViewFlags & 131072) == 131072;
    }

    @ViewDebug.ExportedProperty
    public boolean isClickable() {
        return (this.mViewFlags & 16384) == 16384;
    }

    public void setClickable(boolean clickable) {
        setFlags(clickable ? 16384 : 0, 16384);
    }

    public boolean isLongClickable() {
        return (this.mViewFlags & 2097152) == 2097152;
    }

    public void setLongClickable(boolean longClickable) {
        setFlags(longClickable ? 2097152 : 0, 2097152);
    }

    public void setPressed(boolean pressed) {
        boolean needsRefresh = pressed != ((this.mPrivateFlags & 16384) == 16384);
        if (pressed) {
            this.mPrivateFlags |= 16384;
        } else {
            this.mPrivateFlags &= -16385;
        }
        if (needsRefresh) {
            refreshDrawableState();
        }
        dispatchSetPressed(pressed);
    }

    protected void dispatchSetPressed(boolean pressed) {
    }

    public boolean isPressed() {
        return (this.mPrivateFlags & 16384) == 16384;
    }

    public boolean isSaveEnabled() {
        return (this.mViewFlags & 65536) != 65536;
    }

    public void setSaveEnabled(boolean enabled) {
        setFlags(enabled ? 0 : 65536, 65536);
    }

    @ViewDebug.ExportedProperty
    public boolean getFilterTouchesWhenObscured() {
        return (this.mViewFlags & 1024) != 0;
    }

    public void setFilterTouchesWhenObscured(boolean enabled) {
        setFlags(enabled ? 0 : 1024, 1024);
    }

    public boolean isSaveFromParentEnabled() {
        return (this.mViewFlags & 536870912) != 536870912;
    }

    public void setSaveFromParentEnabled(boolean enabled) {
        setFlags(enabled ? 0 : 536870912, 536870912);
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean isFocusable() {
        return 1 == (this.mViewFlags & 1);
    }

    @ViewDebug.ExportedProperty
    public final boolean isFocusableInTouchMode() {
        return 262144 == (this.mViewFlags & 262144);
    }

    public View focusSearch(int direction) {
        if (this.mParent != null) {
            return this.mParent.focusSearch(this, direction);
        }
        return null;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View findUserSetNextFocus(View root, int direction) {
        switch (direction) {
            case 1:
                if (this.mID == -1) {
                    return null;
                }
                final int id = this.mID;
                return root.findViewByPredicateInsideOut(this, new Predicate<View>() { // from class: android.view.View.2
                    @Override // com.android.internal.util.Predicate
                    public boolean apply(View t) {
                        return t.mNextFocusForwardId == id;
                    }
                });
            case 2:
                if (this.mNextFocusForwardId == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, this.mNextFocusForwardId);
            case 17:
                if (this.mNextFocusLeftId == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, this.mNextFocusLeftId);
            case 33:
                if (this.mNextFocusUpId == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, this.mNextFocusUpId);
            case 66:
                if (this.mNextFocusRightId == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, this.mNextFocusRightId);
            case 130:
                if (this.mNextFocusDownId == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, this.mNextFocusDownId);
            default:
                return null;
        }
    }

    private View findViewInsideOutShouldExist(View root, int id) {
        if (this.mMatchIdPredicate == null) {
            this.mMatchIdPredicate = new MatchIdPredicate();
        }
        this.mMatchIdPredicate.mId = id;
        View result = root.findViewByPredicateInsideOut(this, this.mMatchIdPredicate);
        if (result == null) {
            Log.w(VIEW_LOG_TAG, "couldn't find view with id " + id);
        }
        return result;
    }

    public ArrayList<View> getFocusables(int direction) {
        ArrayList<View> result = new ArrayList<>(24);
        addFocusables(result, direction);
        return result;
    }

    public void addFocusables(ArrayList<View> views, int direction) {
        addFocusables(views, direction, 1);
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null || !isFocusable()) {
            return;
        }
        if ((focusableMode & 1) == 1 && isInTouchMode() && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        if (getAccessibilityNodeProvider() != null) {
            if ((flags & 4) != 0) {
                outViews.add(this);
            }
        } else if ((flags & 2) != 0 && searched != null && searched.length() > 0 && this.mContentDescription != null && this.mContentDescription.length() > 0) {
            String searchedLowerCase = searched.toString().toLowerCase();
            String contentDescriptionLowerCase = this.mContentDescription.toString().toLowerCase();
            if (contentDescriptionLowerCase.contains(searchedLowerCase)) {
                outViews.add(this);
            }
        }
    }

    public ArrayList<View> getTouchables() {
        ArrayList<View> result = new ArrayList<>();
        addTouchables(result);
        return result;
    }

    public void addTouchables(ArrayList<View> views) {
        int viewFlags = this.mViewFlags;
        if (((viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152) && (viewFlags & 32) == 0) {
            views.add(this);
        }
    }

    public boolean isAccessibilityFocused() {
        return (this.mPrivateFlags2 & 67108864) != 0;
    }

    public boolean requestAccessibilityFocus() {
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mContext);
        if (manager.isEnabled() && manager.isTouchExplorationEnabled() && (this.mViewFlags & 12) == 0 && (this.mPrivateFlags2 & 67108864) == 0) {
            this.mPrivateFlags2 |= 67108864;
            ViewRootImpl viewRootImpl = getViewRootImpl();
            if (viewRootImpl != null) {
                viewRootImpl.setAccessibilityFocus(this, null);
            }
            invalidate();
            sendAccessibilityEvent(32768);
            return true;
        }
        return false;
    }

    public void clearAccessibilityFocus() {
        View focusHost;
        clearAccessibilityFocusNoCallbacks();
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null && (focusHost = viewRootImpl.getAccessibilityFocusedHost()) != null && ViewRootImpl.isViewDescendantOf(focusHost, this)) {
            viewRootImpl.setAccessibilityFocus(null, null);
        }
    }

    private void sendAccessibilityHoverEvent(int eventType) {
        View view = this;
        while (true) {
            View source = view;
            if (source.includeForAccessibility()) {
                source.sendAccessibilityEvent(eventType);
                return;
            }
            ViewParent parent = source.getParent();
            if (parent instanceof View) {
                view = (View) parent;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearAccessibilityFocusNoCallbacks() {
        if ((this.mPrivateFlags2 & 67108864) != 0) {
            this.mPrivateFlags2 &= -67108865;
            invalidate();
            sendAccessibilityEvent(65536);
        }
    }

    public final boolean requestFocus() {
        return requestFocus(130);
    }

    public final boolean requestFocus(int direction) {
        return requestFocus(direction, null);
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return requestFocusNoSearch(direction, previouslyFocusedRect);
    }

    private boolean requestFocusNoSearch(int direction, Rect previouslyFocusedRect) {
        if ((this.mViewFlags & 1) != 1 || (this.mViewFlags & 12) != 0) {
            return false;
        }
        if ((isInTouchMode() && 262144 != (this.mViewFlags & 262144)) || hasAncestorThatBlocksDescendantFocus()) {
            return false;
        }
        handleFocusGainInternal(direction, previouslyFocusedRect);
        return true;
    }

    public final boolean requestFocusFromTouch() {
        ViewRootImpl viewRoot;
        if (isInTouchMode() && (viewRoot = getViewRootImpl()) != null) {
            viewRoot.ensureTouchMode(false);
        }
        return requestFocus(130);
    }

    private boolean hasAncestorThatBlocksDescendantFocus() {
        ViewParent viewParent = this.mParent;
        while (true) {
            ViewParent ancestor = viewParent;
            if (ancestor instanceof ViewGroup) {
                ViewGroup vgAncestor = (ViewGroup) ancestor;
                if (vgAncestor.getDescendantFocusability() == 393216) {
                    return true;
                }
                viewParent = vgAncestor.getParent();
            } else {
                return false;
            }
        }
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE, mapping = {@ViewDebug.IntToString(from = 0, to = "auto"), @ViewDebug.IntToString(from = 1, to = AuthorizationHeaderIms.YES), @ViewDebug.IntToString(from = 2, to = AuthorizationHeaderIms.NO), @ViewDebug.IntToString(from = 4, to = "noHideDescendants")})
    public int getImportantForAccessibility() {
        return (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> 20;
    }

    public void setAccessibilityLiveRegion(int mode) {
        if (mode != getAccessibilityLiveRegion()) {
            this.mPrivateFlags2 &= -25165825;
            this.mPrivateFlags2 |= (mode << 23) & 25165824;
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public int getAccessibilityLiveRegion() {
        return (this.mPrivateFlags2 & 25165824) >> 23;
    }

    public void setImportantForAccessibility(int mode) {
        int oldMode = getImportantForAccessibility();
        if (mode != oldMode) {
            boolean maySkipNotify = oldMode == 0 || mode == 0;
            boolean oldIncludeForAccessibility = maySkipNotify && includeForAccessibility();
            this.mPrivateFlags2 &= -7340033;
            this.mPrivateFlags2 |= (mode << 20) & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK;
            if (!maySkipNotify || oldIncludeForAccessibility != includeForAccessibility()) {
                notifySubtreeAccessibilityStateChangedIfNeeded();
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    public boolean isImportantForAccessibility() {
        int mode = (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> 20;
        if (mode == 2 || mode == 4) {
            return false;
        }
        ViewParent viewParent = this.mParent;
        while (true) {
            ViewParent parent = viewParent;
            if (!(parent instanceof View)) {
                return mode == 1 || isActionableForAccessibility() || hasListenersForAccessibility() || getAccessibilityNodeProvider() != null || getAccessibilityLiveRegion() != 0;
            } else if (((View) parent).getImportantForAccessibility() == 4) {
                return false;
            } else {
                viewParent = parent.getParent();
            }
        }
    }

    public ViewParent getParentForAccessibility() {
        if (this.mParent instanceof View) {
            View parentView = (View) this.mParent;
            if (parentView.includeForAccessibility()) {
                return this.mParent;
            }
            return this.mParent.getParentForAccessibility();
        }
        return null;
    }

    public void addChildrenForAccessibility(ArrayList<View> children) {
        if (includeForAccessibility()) {
            children.add(this);
        }
    }

    public boolean includeForAccessibility() {
        if (this.mAttachInfo != null) {
            return (this.mAttachInfo.mAccessibilityFetchFlags & 8) != 0 || isImportantForAccessibility();
        }
        return false;
    }

    public boolean isActionableForAccessibility() {
        return isClickable() || isLongClickable() || isFocusable();
    }

    private boolean hasListenersForAccessibility() {
        ListenerInfo info = getListenerInfo();
        return (this.mTouchDelegate == null && info.mOnKeyListener == null && info.mOnTouchListener == null && info.mOnGenericMotionListener == null && info.mOnHoverListener == null && info.mOnDragListener == null) ? false : true;
    }

    public void notifyViewAccessibilityStateChangedIfNeeded(int changeType) {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            return;
        }
        if (this.mSendViewStateChangedAccessibilityEvent == null) {
            this.mSendViewStateChangedAccessibilityEvent = new SendViewStateChangedAccessibilityEvent();
        }
        this.mSendViewStateChangedAccessibilityEvent.runOrPost(changeType);
    }

    public void notifySubtreeAccessibilityStateChangedIfNeeded() {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && (this.mPrivateFlags2 & 134217728) == 0) {
            this.mPrivateFlags2 |= 134217728;
            if (this.mParent != null) {
                try {
                    this.mParent.notifySubtreeAccessibilityStateChanged(this, this, 1);
                } catch (AbstractMethodError e) {
                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetSubtreeAccessibilityStateChanged() {
        this.mPrivateFlags2 &= -134217729;
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.performAccessibilityAction(this, action, arguments);
        }
        return performAccessibilityActionInternal(action, arguments);
    }

    boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        switch (action) {
            case 1:
                if (!hasFocus()) {
                    getViewRootImpl().ensureTouchMode(false);
                    return requestFocus();
                }
                return false;
            case 2:
                if (hasFocus()) {
                    clearFocus();
                    return !isFocused();
                }
                return false;
            case 4:
                if (!isSelected()) {
                    setSelected(true);
                    return isSelected();
                }
                return false;
            case 8:
                if (isSelected()) {
                    setSelected(false);
                    return !isSelected();
                }
                return false;
            case 16:
                if (isClickable()) {
                    performClick();
                    return true;
                }
                return false;
            case 32:
                if (isLongClickable()) {
                    performLongClick();
                    return true;
                }
                return false;
            case 64:
                if (!isAccessibilityFocused()) {
                    return requestAccessibilityFocus();
                }
                return false;
            case 128:
                if (isAccessibilityFocused()) {
                    clearAccessibilityFocus();
                    return true;
                }
                return false;
            case 256:
                if (arguments != null) {
                    int granularity = arguments.getInt("ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT");
                    boolean extendSelection = arguments.getBoolean("ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN");
                    return traverseAtGranularity(granularity, true, extendSelection);
                }
                return false;
            case 512:
                if (arguments != null) {
                    int granularity2 = arguments.getInt("ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT");
                    boolean extendSelection2 = arguments.getBoolean("ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN");
                    return traverseAtGranularity(granularity2, false, extendSelection2);
                }
                return false;
            case 131072:
                CharSequence text = getIterableTextForAccessibility();
                if (text == null) {
                    return false;
                }
                int start = arguments != null ? arguments.getInt("ACTION_ARGUMENT_SELECTION_START_INT", -1) : -1;
                int end = arguments != null ? arguments.getInt("ACTION_ARGUMENT_SELECTION_END_INT", -1) : -1;
                if ((getAccessibilitySelectionStart() != start || getAccessibilitySelectionEnd() != end) && start == end) {
                    setAccessibilitySelection(start, end);
                    notifyViewAccessibilityStateChangedIfNeeded(0);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private boolean traverseAtGranularity(int granularity, boolean forward, boolean extendSelection) {
        AccessibilityIterators.TextSegmentIterator iterator;
        int selectionEnd;
        int selectionStart;
        CharSequence text = getIterableTextForAccessibility();
        if (text == null || text.length() == 0 || (iterator = getIteratorForGranularity(granularity)) == null) {
            return false;
        }
        int current = getAccessibilitySelectionEnd();
        if (current == -1) {
            current = forward ? 0 : text.length();
        }
        int[] range = forward ? iterator.following(current) : iterator.preceding(current);
        if (range == null) {
            return false;
        }
        int segmentStart = range[0];
        int segmentEnd = range[1];
        if (extendSelection && isAccessibilitySelectionExtendable()) {
            selectionStart = getAccessibilitySelectionStart();
            if (selectionStart == -1) {
                selectionStart = forward ? segmentStart : segmentEnd;
            }
            selectionEnd = forward ? segmentEnd : segmentStart;
        } else {
            int i = forward ? segmentEnd : segmentStart;
            selectionEnd = i;
            selectionStart = i;
        }
        setAccessibilitySelection(selectionStart, selectionEnd);
        int action = forward ? 256 : 512;
        sendViewTextTraversedAtGranularityEvent(action, granularity, segmentStart, segmentEnd);
        return true;
    }

    public CharSequence getIterableTextForAccessibility() {
        return getContentDescription();
    }

    public boolean isAccessibilitySelectionExtendable() {
        return false;
    }

    public int getAccessibilitySelectionStart() {
        return this.mAccessibilityCursorPosition;
    }

    public int getAccessibilitySelectionEnd() {
        return getAccessibilitySelectionStart();
    }

    public void setAccessibilitySelection(int start, int end) {
        if (start == end && end == this.mAccessibilityCursorPosition) {
            return;
        }
        if (start >= 0 && start == end && end <= getIterableTextForAccessibility().length()) {
            this.mAccessibilityCursorPosition = start;
        } else {
            this.mAccessibilityCursorPosition = -1;
        }
        sendAccessibilityEvent(8192);
    }

    private void sendViewTextTraversedAtGranularityEvent(int action, int granularity, int fromIndex, int toIndex) {
        if (this.mParent == null) {
            return;
        }
        AccessibilityEvent event = AccessibilityEvent.obtain(131072);
        onInitializeAccessibilityEvent(event);
        onPopulateAccessibilityEvent(event);
        event.setFromIndex(fromIndex);
        event.setToIndex(toIndex);
        event.setAction(action);
        event.setMovementGranularity(granularity);
        this.mParent.requestSendAccessibilityEvent(this, event);
    }

    public AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int granularity) {
        switch (granularity) {
            case 1:
                CharSequence text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    AccessibilityIterators.CharacterTextSegmentIterator iterator = AccessibilityIterators.CharacterTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
                    iterator.initialize(text.toString());
                    return iterator;
                }
                return null;
            case 2:
                CharSequence text2 = getIterableTextForAccessibility();
                if (text2 != null && text2.length() > 0) {
                    AccessibilityIterators.WordTextSegmentIterator iterator2 = AccessibilityIterators.WordTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
                    iterator2.initialize(text2.toString());
                    return iterator2;
                }
                return null;
            case 8:
                CharSequence text3 = getIterableTextForAccessibility();
                if (text3 != null && text3.length() > 0) {
                    AccessibilityIterators.ParagraphTextSegmentIterator iterator3 = AccessibilityIterators.ParagraphTextSegmentIterator.getInstance();
                    iterator3.initialize(text3.toString());
                    return iterator3;
                }
                return null;
            default:
                return null;
        }
    }

    public void dispatchStartTemporaryDetach() {
        clearDisplayList();
        onStartTemporaryDetach();
    }

    public void onStartTemporaryDetach() {
        removeUnsetPressCallback();
        this.mPrivateFlags |= 67108864;
    }

    public void dispatchFinishTemporaryDetach() {
        onFinishTemporaryDetach();
    }

    public void onFinishTemporaryDetach() {
    }

    public KeyEvent.DispatcherState getKeyDispatcherState() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mKeyDispatchState;
        }
        return null;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return onKeyPreIme(event.getKeyCode(), event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onKeyEvent(event, 0);
        }
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnKeyListener != null && (this.mViewFlags & 32) == 0 && li.mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        if (event.dispatch(this, this.mAttachInfo != null ? this.mAttachInfo.mKeyDispatchState : null, this)) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, 0);
            return false;
        }
        return false;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return onKeyShortcut(event.getKeyCode(), event);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        ListenerInfo li;
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onTouchEvent(event, 0);
        }
        if (onFilterTouchEventForSecurity(event) && (((li = this.mListenerInfo) != null && li.mOnTouchListener != null && (this.mViewFlags & 32) == 0 && li.mOnTouchListener.onTouch(this, event)) || onTouchEvent(event))) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, 0);
            return false;
        }
        return false;
    }

    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if ((this.mViewFlags & 1024) != 0 && (event.getFlags() & 1) != 0) {
            return false;
        }
        return true;
    }

    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onTrackballEvent(event, 0);
        }
        return onTrackballEvent(event);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onGenericMotionEvent(event, 0);
        }
        int source = event.getSource();
        if ((source & 2) != 0) {
            int action = event.getAction();
            if (action == 9 || action == 7 || action == 10) {
                if (dispatchHoverEvent(event)) {
                    return true;
                }
            } else if (dispatchGenericPointerEvent(event)) {
                return true;
            }
        } else if (dispatchGenericFocusedEvent(event)) {
            return true;
        }
        if (dispatchGenericMotionEventInternal(event)) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, 0);
            return false;
        }
        return false;
    }

    private boolean dispatchGenericMotionEventInternal(MotionEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if ((li != null && li.mOnGenericMotionListener != null && (this.mViewFlags & 32) == 0 && li.mOnGenericMotionListener.onGenericMotion(this, event)) || onGenericMotionEvent(event)) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, 0);
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean dispatchHoverEvent(MotionEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnHoverListener != null && (this.mViewFlags & 32) == 0 && li.mOnHoverListener.onHover(this, event)) {
            return true;
        }
        return onHoverEvent(event);
    }

    protected boolean hasHoveredChild() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean dispatchGenericPointerEvent(MotionEvent event) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean dispatchGenericFocusedEvent(MotionEvent event) {
        return false;
    }

    public final boolean dispatchPointerEvent(MotionEvent event) {
        if (event.isTouchEvent()) {
            return dispatchTouchEvent(event);
        }
        return dispatchGenericMotionEvent(event);
    }

    public void dispatchWindowFocusChanged(boolean hasFocus) {
        onWindowFocusChanged(hasFocus);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (!hasWindowFocus) {
            if (isPressed()) {
                setPressed(false);
            }
            if (imm != null && (this.mPrivateFlags & 2) != 0) {
                imm.focusOut(this);
            }
            removeLongPressCallback();
            removeTapCallback();
            onFocusLost();
        } else if (imm != null && (this.mPrivateFlags & 2) != 0) {
            imm.focusIn(this);
        }
        refreshDrawableState();
    }

    public boolean hasWindowFocus() {
        return this.mAttachInfo != null && this.mAttachInfo.mHasWindowFocus;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchVisibilityChanged(View changedView, int visibility) {
        onVisibilityChanged(changedView, visibility);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onVisibilityChanged(View changedView, int visibility) {
        if (visibility == 0) {
            if (this.mAttachInfo != null) {
                initialAwakenScrollBars();
            } else {
                this.mPrivateFlags |= 134217728;
            }
        }
    }

    public void dispatchDisplayHint(int hint) {
        onDisplayHint(hint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDisplayHint(int hint) {
    }

    public void dispatchWindowVisibilityChanged(int visibility) {
        onWindowVisibilityChanged(visibility);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int visibility) {
        if (visibility == 0) {
            initialAwakenScrollBars();
        }
    }

    public int getWindowVisibility() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mWindowVisibility;
        }
        return 8;
    }

    public void getWindowVisibleDisplayFrame(Rect outRect) {
        if (this.mAttachInfo != null) {
            try {
                this.mAttachInfo.mSession.getDisplayFrame(this.mAttachInfo.mWindow, outRect);
                Rect insets = this.mAttachInfo.mVisibleInsets;
                outRect.left += insets.left;
                outRect.top += insets.top;
                outRect.right -= insets.right;
                outRect.bottom -= insets.bottom;
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        Display d = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        d.getRectSize(outRect);
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged(newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchCollectViewAttributes(AttachInfo attachInfo, int visibility) {
        performCollectViewAttributes(attachInfo, visibility);
    }

    void performCollectViewAttributes(AttachInfo attachInfo, int visibility) {
        if ((visibility & 12) == 0) {
            if ((this.mViewFlags & 67108864) == 67108864) {
                attachInfo.mKeepScreenOn = true;
            }
            attachInfo.mSystemUiVisibility |= this.mSystemUiVisibility;
            ListenerInfo li = this.mListenerInfo;
            if (li != null && li.mOnSystemUiVisibilityChangeListener != null) {
                attachInfo.mHasSystemUiListeners = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void needGlobalAttributesUpdate(boolean force) {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null && !ai.mRecomputeGlobalAttributes) {
            if (force || ai.mKeepScreenOn || ai.mSystemUiVisibility != 0 || ai.mHasSystemUiListeners) {
                ai.mRecomputeGlobalAttributes = true;
            }
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isInTouchMode() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mInTouchMode;
        }
        return ViewRootImpl.isInTouchMode();
    }

    @ViewDebug.CapturedViewProperty
    public final Context getContext() {
        return this.mContext;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            if ((this.mViewFlags & 32) == 32) {
                return true;
            }
            if (((this.mViewFlags & 16384) == 16384 || (this.mViewFlags & 2097152) == 2097152) && event.getRepeatCount() == 0) {
                setPressed(true);
                checkForLongClick(0);
                return true;
            }
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            if ((this.mViewFlags & 32) == 32) {
                return true;
            }
            if ((this.mViewFlags & 16384) == 16384 && isPressed()) {
                setPressed(false);
                if (!this.mHasPerformedLongPress) {
                    removeLongPressCallback();
                    return performClick();
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onCheckIsTextEditor() {
        return false;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return null;
    }

    public boolean checkInputConnectionProxy(View view) {
        return false;
    }

    public void createContextMenu(ContextMenu menu) {
        ContextMenu.ContextMenuInfo menuInfo = getContextMenuInfo();
        ((MenuBuilder) menu).setCurrentMenuInfo(menuInfo);
        onCreateContextMenu(menu);
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnCreateContextMenuListener != null) {
            li.mOnCreateContextMenuListener.onCreateContextMenu(menu, this, menuInfo);
        }
        ((MenuBuilder) menu).setCurrentMenuInfo((ContextMenu.ContextMenuInfo) null);
        if (this.mParent != null) {
            this.mParent.createContextMenu(menu);
        }
    }

    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return null;
    }

    protected void onCreateContextMenu(ContextMenu menu) {
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    public boolean onHoverEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (!this.mSendingHoverAccessibilityEvents) {
            if ((action == 9 || action == 7) && !hasHoveredChild() && pointInView(event.getX(), event.getY())) {
                sendAccessibilityHoverEvent(128);
                this.mSendingHoverAccessibilityEvents = true;
            }
        } else if (action == 10 || (action == 2 && !pointInView(event.getX(), event.getY()))) {
            this.mSendingHoverAccessibilityEvents = false;
            sendAccessibilityHoverEvent(256);
            if (this.mAttachInfo != null && !this.mAttachInfo.mHasWindowFocus) {
                getViewRootImpl().setAccessibilityFocus(null, null);
            }
        }
        if (isHoverable()) {
            switch (action) {
                case 9:
                    setHovered(true);
                    break;
                case 10:
                    setHovered(false);
                    break;
            }
            dispatchGenericMotionEventInternal(event);
            return true;
        }
        return false;
    }

    private boolean isHoverable() {
        int viewFlags = this.mViewFlags;
        if ((viewFlags & 32) == 32) {
            return false;
        }
        return (viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152;
    }

    @ViewDebug.ExportedProperty
    public boolean isHovered() {
        return (this.mPrivateFlags & 268435456) != 0;
    }

    public void setHovered(boolean hovered) {
        if (hovered) {
            if ((this.mPrivateFlags & 268435456) == 0) {
                this.mPrivateFlags |= 268435456;
                refreshDrawableState();
                onHoverChanged(true);
            }
        } else if ((this.mPrivateFlags & 268435456) != 0) {
            this.mPrivateFlags &= -268435457;
            refreshDrawableState();
            onHoverChanged(false);
        }
    }

    public void onHoverChanged(boolean hovered) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        int viewFlags = this.mViewFlags;
        if ((viewFlags & 32) == 32) {
            if (event.getAction() == 1 && (this.mPrivateFlags & 16384) != 0) {
                setPressed(false);
            }
            return (viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152;
        } else if (this.mTouchDelegate != null && this.mTouchDelegate.onTouchEvent(event)) {
            return true;
        } else {
            if ((viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152) {
                switch (event.getAction()) {
                    case 0:
                        this.mHasPerformedLongPress = false;
                        if (!performButtonActionOnTouchDown(event)) {
                            boolean isInScrollingContainer = isInScrollingContainer();
                            if (isInScrollingContainer) {
                                this.mPrivateFlags |= 33554432;
                                if (this.mPendingCheckForTap == null) {
                                    this.mPendingCheckForTap = new CheckForTap();
                                }
                                postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                                return true;
                            }
                            setPressed(true);
                            checkForLongClick(0);
                            return true;
                        }
                        return true;
                    case 1:
                        boolean prepressed = (this.mPrivateFlags & 33554432) != 0;
                        if ((this.mPrivateFlags & 16384) != 0 || prepressed) {
                            boolean focusTaken = false;
                            if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
                                focusTaken = requestFocus();
                            }
                            if (prepressed) {
                                setPressed(true);
                            }
                            if (!this.mHasPerformedLongPress) {
                                removeLongPressCallback();
                                if (!focusTaken) {
                                    if (this.mPerformClick == null) {
                                        this.mPerformClick = new PerformClick();
                                    }
                                    if (!post(this.mPerformClick)) {
                                        performClick();
                                    }
                                }
                            }
                            if (this.mUnsetPressedState == null) {
                                this.mUnsetPressedState = new UnsetPressedState();
                            }
                            if (prepressed) {
                                postDelayed(this.mUnsetPressedState, ViewConfiguration.getPressedStateDuration());
                            } else if (!post(this.mUnsetPressedState)) {
                                this.mUnsetPressedState.run();
                            }
                            removeTapCallback();
                            return true;
                        }
                        return true;
                    case 2:
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        if (!pointInView(x, y, this.mTouchSlop)) {
                            removeTapCallback();
                            if ((this.mPrivateFlags & 16384) != 0) {
                                removeLongPressCallback();
                                setPressed(false);
                                return true;
                            }
                            return true;
                        }
                        return true;
                    case 3:
                        setPressed(false);
                        removeTapCallback();
                        removeLongPressCallback();
                        return true;
                    default:
                        return true;
                }
            }
            return false;
        }
    }

    public boolean isInScrollingContainer() {
        ViewParent parent = getParent();
        while (true) {
            ViewParent p = parent;
            if (p != null && (p instanceof ViewGroup)) {
                if (((ViewGroup) p).shouldDelayChildPressedState()) {
                    return true;
                }
                parent = p.getParent();
            } else {
                return false;
            }
        }
    }

    private void removeLongPressCallback() {
        if (this.mPendingCheckForLongPress != null) {
            removeCallbacks(this.mPendingCheckForLongPress);
        }
    }

    private void removePerformClickCallback() {
        if (this.mPerformClick != null) {
            removeCallbacks(this.mPerformClick);
        }
    }

    private void removeUnsetPressCallback() {
        if ((this.mPrivateFlags & 16384) != 0 && this.mUnsetPressedState != null) {
            setPressed(false);
            removeCallbacks(this.mUnsetPressedState);
        }
    }

    private void removeTapCallback() {
        if (this.mPendingCheckForTap != null) {
            this.mPrivateFlags &= -33554433;
            removeCallbacks(this.mPendingCheckForTap);
        }
    }

    public void cancelLongPress() {
        removeLongPressCallback();
        removeTapCallback();
    }

    private void removeSendViewScrolledAccessibilityEventCallback() {
        if (this.mSendViewScrolledAccessibilityEvent != null) {
            removeCallbacks(this.mSendViewScrolledAccessibilityEvent);
            this.mSendViewScrolledAccessibilityEvent.mIsPending = false;
        }
    }

    public void setTouchDelegate(TouchDelegate delegate) {
        this.mTouchDelegate = delegate;
    }

    public TouchDelegate getTouchDelegate() {
        return this.mTouchDelegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlags(int flags, int mask) {
        boolean accessibilityEnabled = AccessibilityManager.getInstance(this.mContext).isEnabled();
        boolean oldIncludeForAccessibility = accessibilityEnabled && includeForAccessibility();
        int old = this.mViewFlags;
        this.mViewFlags = (this.mViewFlags & (mask ^ (-1))) | (flags & mask);
        int changed = this.mViewFlags ^ old;
        if (changed == 0) {
            return;
        }
        int privateFlags = this.mPrivateFlags;
        if ((changed & 1) != 0 && (privateFlags & 16) != 0) {
            if ((old & 1) == 1 && (privateFlags & 2) != 0) {
                clearFocus();
            } else if ((old & 1) == 0 && (privateFlags & 2) == 0 && this.mParent != null) {
                this.mParent.focusableViewAvailable(this);
            }
        }
        int newVisibility = flags & 12;
        if (newVisibility == 0 && (changed & 12) != 0) {
            this.mPrivateFlags |= 32;
            invalidate(true);
            needGlobalAttributesUpdate(true);
            if (this.mParent != null && this.mBottom > this.mTop && this.mRight > this.mLeft) {
                this.mParent.focusableViewAvailable(this);
            }
        }
        if ((changed & 8) != 0) {
            needGlobalAttributesUpdate(false);
            requestLayout();
            if ((this.mViewFlags & 12) == 8) {
                if (hasFocus()) {
                    clearFocus();
                }
                clearAccessibilityFocus();
                destroyDrawingCache();
                if (this.mParent instanceof View) {
                    ((View) this.mParent).invalidate(true);
                }
                this.mPrivateFlags |= 32;
            }
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mViewVisibilityChanged = true;
            }
        }
        if ((changed & 4) != 0) {
            needGlobalAttributesUpdate(false);
            this.mPrivateFlags |= 32;
            if ((this.mViewFlags & 12) == 4 && getRootView() != this) {
                if (hasFocus()) {
                    clearFocus();
                }
                clearAccessibilityFocus();
            }
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mViewVisibilityChanged = true;
            }
        }
        if ((changed & 12) != 0) {
            if (newVisibility != 0) {
                cleanupDraw();
            }
            if (this.mParent instanceof ViewGroup) {
                ((ViewGroup) this.mParent).onChildVisibilityChanged(this, changed & 12, newVisibility);
                ((View) this.mParent).invalidate(true);
            } else if (this.mParent != null) {
                this.mParent.invalidateChild(this, null);
            }
            dispatchVisibilityChanged(this, newVisibility);
        }
        if ((changed & 131072) != 0) {
            destroyDrawingCache();
        }
        if ((changed & 32768) != 0) {
            destroyDrawingCache();
            this.mPrivateFlags &= -32769;
            invalidateParentCaches();
        }
        if ((changed & DRAWING_CACHE_QUALITY_MASK) != 0) {
            destroyDrawingCache();
            this.mPrivateFlags &= -32769;
        }
        if ((changed & 128) != 0) {
            if ((this.mViewFlags & 128) != 0) {
                if (this.mBackground != null) {
                    this.mPrivateFlags &= -129;
                    this.mPrivateFlags |= 256;
                } else {
                    this.mPrivateFlags |= 128;
                }
            } else {
                this.mPrivateFlags &= -129;
            }
            requestLayout();
            invalidate(true);
        }
        if ((changed & 67108864) != 0 && this.mParent != null && this.mAttachInfo != null && !this.mAttachInfo.mRecomputeGlobalAttributes) {
            this.mParent.recomputeViewAttributes(this);
        }
        if (accessibilityEnabled) {
            if ((changed & 1) != 0 || (changed & 12) != 0 || (changed & 16384) != 0 || (changed & 2097152) != 0) {
                if (oldIncludeForAccessibility != includeForAccessibility()) {
                    notifySubtreeAccessibilityStateChangedIfNeeded();
                } else {
                    notifyViewAccessibilityStateChangedIfNeeded(0);
                }
            } else if ((changed & 32) != 0) {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    public void bringToFront() {
        if (this.mParent != null) {
            this.mParent.bringChildToFront(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            postSendViewScrolledAccessibilityEventCallback();
        }
        this.mBackgroundSizeChanged = true;
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            ai.mViewScrollChanged = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
    }

    public final ViewParent getParent() {
        return this.mParent;
    }

    public void setScrollX(int value) {
        scrollTo(value, this.mScrollY);
    }

    public void setScrollY(int value) {
        scrollTo(this.mScrollX, value);
    }

    public final int getScrollX() {
        return this.mScrollX;
    }

    public final int getScrollY() {
        return this.mScrollY;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public final int getWidth() {
        return this.mRight - this.mLeft;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public final int getHeight() {
        return this.mBottom - this.mTop;
    }

    public void getDrawingRect(Rect outRect) {
        outRect.left = this.mScrollX;
        outRect.top = this.mScrollY;
        outRect.right = this.mScrollX + (this.mRight - this.mLeft);
        outRect.bottom = this.mScrollY + (this.mBottom - this.mTop);
    }

    public final int getMeasuredWidth() {
        return this.mMeasuredWidth & 16777215;
    }

    public final int getMeasuredWidthAndState() {
        return this.mMeasuredWidth;
    }

    public final int getMeasuredHeight() {
        return this.mMeasuredHeight & 16777215;
    }

    public final int getMeasuredHeightAndState() {
        return this.mMeasuredHeight;
    }

    public final int getMeasuredState() {
        return (this.mMeasuredWidth & (-16777216)) | ((this.mMeasuredHeight >> 16) & (-256));
    }

    public Matrix getMatrix() {
        if (this.mTransformationInfo != null) {
            updateMatrix();
            return this.mTransformationInfo.mMatrix;
        }
        return Matrix.IDENTITY_MATRIX;
    }

    private static boolean nonzero(float value) {
        return value < -0.001f || value > 0.001f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasIdentityMatrix() {
        if (this.mTransformationInfo != null) {
            updateMatrix();
            return this.mTransformationInfo.mMatrixIsIdentity;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void ensureTransformationInfo() {
        if (this.mTransformationInfo == null) {
            this.mTransformationInfo = new TransformationInfo();
        }
    }

    private void updateMatrix() {
        TransformationInfo info = this.mTransformationInfo;
        if (info != null && info.mMatrixDirty) {
            if ((this.mPrivateFlags & 536870912) == 0 && (this.mRight - this.mLeft != info.mPrevWidth || this.mBottom - this.mTop != info.mPrevHeight)) {
                info.mPrevWidth = this.mRight - this.mLeft;
                info.mPrevHeight = this.mBottom - this.mTop;
                info.mPivotX = info.mPrevWidth / 2.0f;
                info.mPivotY = info.mPrevHeight / 2.0f;
            }
            info.mMatrix.reset();
            if (nonzero(info.mRotationX) || nonzero(info.mRotationY)) {
                if (info.mCamera == null) {
                    info.mCamera = new Camera();
                    info.matrix3D = new Matrix();
                }
                info.mCamera.save();
                info.mMatrix.preScale(info.mScaleX, info.mScaleY, info.mPivotX, info.mPivotY);
                info.mCamera.rotate(info.mRotationX, info.mRotationY, -info.mRotation);
                info.mCamera.getMatrix(info.matrix3D);
                info.matrix3D.preTranslate(-info.mPivotX, -info.mPivotY);
                info.matrix3D.postTranslate(info.mPivotX + info.mTranslationX, info.mPivotY + info.mTranslationY);
                info.mMatrix.postConcat(info.matrix3D);
                info.mCamera.restore();
            } else {
                info.mMatrix.setTranslate(info.mTranslationX, info.mTranslationY);
                info.mMatrix.preRotate(info.mRotation, info.mPivotX, info.mPivotY);
                info.mMatrix.preScale(info.mScaleX, info.mScaleY, info.mPivotX, info.mPivotY);
            }
            info.mMatrixDirty = false;
            info.mMatrixIsIdentity = info.mMatrix.isIdentity();
            info.mInverseMatrixDirty = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Matrix getInverseMatrix() {
        TransformationInfo info = this.mTransformationInfo;
        if (info != null) {
            updateMatrix();
            if (info.mInverseMatrixDirty) {
                if (info.mInverseMatrix == null) {
                    info.mInverseMatrix = new Matrix();
                }
                info.mMatrix.invert(info.mInverseMatrix);
                info.mInverseMatrixDirty = false;
            }
            return info.mInverseMatrix;
        }
        return Matrix.IDENTITY_MATRIX;
    }

    public float getCameraDistance() {
        ensureTransformationInfo();
        float dpi = this.mResources.getDisplayMetrics().densityDpi;
        TransformationInfo info = this.mTransformationInfo;
        if (info.mCamera == null) {
            info.mCamera = new Camera();
            info.matrix3D = new Matrix();
        }
        return -(info.mCamera.getLocationZ() * dpi);
    }

    public void setCameraDistance(float distance) {
        invalidateViewProperty(true, false);
        ensureTransformationInfo();
        float dpi = this.mResources.getDisplayMetrics().densityDpi;
        TransformationInfo info = this.mTransformationInfo;
        if (info.mCamera == null) {
            info.mCamera = new Camera();
            info.matrix3D = new Matrix();
        }
        info.mCamera.setLocation(0.0f, 0.0f, (-Math.abs(distance)) / dpi);
        info.mMatrixDirty = true;
        invalidateViewProperty(false, false);
        if (this.mDisplayList != null) {
            this.mDisplayList.setCameraDistance((-Math.abs(distance)) / dpi);
        }
        if ((this.mPrivateFlags2 & 268435456) == 268435456) {
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotation() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mRotation;
        }
        return 0.0f;
    }

    public void setRotation(float rotation) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mRotation != rotation) {
            invalidateViewProperty(true, false);
            info.mRotation = rotation;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setRotation(rotation);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotationY() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mRotationY;
        }
        return 0.0f;
    }

    public void setRotationY(float rotationY) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mRotationY != rotationY) {
            invalidateViewProperty(true, false);
            info.mRotationY = rotationY;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setRotationY(rotationY);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotationX() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mRotationX;
        }
        return 0.0f;
    }

    public void setRotationX(float rotationX) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mRotationX != rotationX) {
            invalidateViewProperty(true, false);
            info.mRotationX = rotationX;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setRotationX(rotationX);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getScaleX() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mScaleX;
        }
        return 1.0f;
    }

    public void setScaleX(float scaleX) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mScaleX != scaleX) {
            invalidateViewProperty(true, false);
            info.mScaleX = scaleX;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setScaleX(scaleX);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getScaleY() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mScaleY;
        }
        return 1.0f;
    }

    public void setScaleY(float scaleY) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mScaleY != scaleY) {
            invalidateViewProperty(true, false);
            info.mScaleY = scaleY;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setScaleY(scaleY);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getPivotX() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mPivotX;
        }
        return 0.0f;
    }

    public void setPivotX(float pivotX) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        boolean pivotSet = (this.mPrivateFlags & 536870912) == 536870912;
        if (info.mPivotX != pivotX || !pivotSet) {
            this.mPrivateFlags |= 536870912;
            invalidateViewProperty(true, false);
            info.mPivotX = pivotX;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setPivotX(pivotX);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getPivotY() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mPivotY;
        }
        return 0.0f;
    }

    public void setPivotY(float pivotY) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        boolean pivotSet = (this.mPrivateFlags & 536870912) == 536870912;
        if (info.mPivotY != pivotY || !pivotSet) {
            this.mPrivateFlags |= 536870912;
            invalidateViewProperty(true, false);
            info.mPivotY = pivotY;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setPivotY(pivotY);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getAlpha() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mAlpha;
        }
        return 1.0f;
    }

    public boolean hasOverlappingRendering() {
        return true;
    }

    public void setAlpha(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != alpha) {
            this.mTransformationInfo.mAlpha = alpha;
            if (onSetAlpha((int) (alpha * 255.0f))) {
                this.mPrivateFlags |= 262144;
                invalidateParentCaches();
                invalidate(true);
                return;
            }
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, false);
            if (this.mDisplayList != null) {
                this.mDisplayList.setAlpha(getFinalAlpha());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setAlphaNoInvalidation(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != alpha) {
            this.mTransformationInfo.mAlpha = alpha;
            boolean subclassHandlesAlpha = onSetAlpha((int) (alpha * 255.0f));
            if (subclassHandlesAlpha) {
                this.mPrivateFlags |= 262144;
                return true;
            }
            this.mPrivateFlags &= -262145;
            if (this.mDisplayList != null) {
                this.mDisplayList.setAlpha(getFinalAlpha());
                return false;
            }
            return false;
        }
        return false;
    }

    public void setTransitionAlpha(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mTransitionAlpha != alpha) {
            this.mTransformationInfo.mTransitionAlpha = alpha;
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, false);
            if (this.mDisplayList != null) {
                this.mDisplayList.setAlpha(getFinalAlpha());
            }
        }
    }

    private float getFinalAlpha() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mAlpha * this.mTransformationInfo.mTransitionAlpha;
        }
        return 1.0f;
    }

    public float getTransitionAlpha() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mTransitionAlpha;
        }
        return 1.0f;
    }

    @ViewDebug.CapturedViewProperty
    public final int getTop() {
        return this.mTop;
    }

    public final void setTop(int top) {
        int minTop;
        int yLoc;
        if (top != this.mTop) {
            updateMatrix();
            boolean matrixIsIdentity = this.mTransformationInfo == null || this.mTransformationInfo.mMatrixIsIdentity;
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    if (top < this.mTop) {
                        minTop = top;
                        yLoc = top - this.mTop;
                    } else {
                        minTop = this.mTop;
                        yLoc = 0;
                    }
                    invalidate(0, yLoc, this.mRight - this.mLeft, this.mBottom - minTop);
                }
            } else {
                invalidate(true);
            }
            int width = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            this.mTop = top;
            if (this.mDisplayList != null) {
                this.mDisplayList.setTop(this.mTop);
            }
            sizeChange(width, this.mBottom - this.mTop, width, oldHeight);
            if (!matrixIsIdentity) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getBottom() {
        return this.mBottom;
    }

    public boolean isDirty() {
        return (this.mPrivateFlags & 6291456) != 0;
    }

    public final void setBottom(int bottom) {
        int maxBottom;
        if (bottom != this.mBottom) {
            updateMatrix();
            boolean matrixIsIdentity = this.mTransformationInfo == null || this.mTransformationInfo.mMatrixIsIdentity;
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    if (bottom < this.mBottom) {
                        maxBottom = this.mBottom;
                    } else {
                        maxBottom = bottom;
                    }
                    invalidate(0, 0, this.mRight - this.mLeft, maxBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int width = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            this.mBottom = bottom;
            if (this.mDisplayList != null) {
                this.mDisplayList.setBottom(this.mBottom);
            }
            sizeChange(width, this.mBottom - this.mTop, width, oldHeight);
            if (!matrixIsIdentity) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getLeft() {
        return this.mLeft;
    }

    public final void setLeft(int left) {
        int minLeft;
        int xLoc;
        if (left != this.mLeft) {
            updateMatrix();
            boolean matrixIsIdentity = this.mTransformationInfo == null || this.mTransformationInfo.mMatrixIsIdentity;
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    if (left < this.mLeft) {
                        minLeft = left;
                        xLoc = left - this.mLeft;
                    } else {
                        minLeft = this.mLeft;
                        xLoc = 0;
                    }
                    invalidate(xLoc, 0, this.mRight - minLeft, this.mBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int oldWidth = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            this.mLeft = left;
            if (this.mDisplayList != null) {
                this.mDisplayList.setLeft(left);
            }
            sizeChange(this.mRight - this.mLeft, height, oldWidth, height);
            if (!matrixIsIdentity) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getRight() {
        return this.mRight;
    }

    public final void setRight(int right) {
        int maxRight;
        if (right != this.mRight) {
            updateMatrix();
            boolean matrixIsIdentity = this.mTransformationInfo == null || this.mTransformationInfo.mMatrixIsIdentity;
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    if (right < this.mRight) {
                        maxRight = this.mRight;
                    } else {
                        maxRight = right;
                    }
                    invalidate(0, 0, maxRight - this.mLeft, this.mBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int oldWidth = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            this.mRight = right;
            if (this.mDisplayList != null) {
                this.mDisplayList.setRight(this.mRight);
            }
            sizeChange(this.mRight - this.mLeft, height, oldWidth, height);
            if (!matrixIsIdentity) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getX() {
        return this.mLeft + (this.mTransformationInfo != null ? this.mTransformationInfo.mTranslationX : 0.0f);
    }

    public void setX(float x) {
        setTranslationX(x - this.mLeft);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getY() {
        return this.mTop + (this.mTransformationInfo != null ? this.mTransformationInfo.mTranslationY : 0.0f);
    }

    public void setY(float y) {
        setTranslationY(y - this.mTop);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationX() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mTranslationX;
        }
        return 0.0f;
    }

    public void setTranslationX(float translationX) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mTranslationX != translationX) {
            invalidateViewProperty(true, false);
            info.mTranslationX = translationX;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setTranslationX(translationX);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationY() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mTranslationY;
        }
        return 0.0f;
    }

    public void setTranslationY(float translationY) {
        ensureTransformationInfo();
        TransformationInfo info = this.mTransformationInfo;
        if (info.mTranslationY != translationY) {
            invalidateViewProperty(true, false);
            info.mTranslationY = translationY;
            info.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            if (this.mDisplayList != null) {
                this.mDisplayList.setTranslationY(translationY);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    public void getHitRect(Rect outRect) {
        updateMatrix();
        TransformationInfo info = this.mTransformationInfo;
        if (info == null || info.mMatrixIsIdentity || this.mAttachInfo == null) {
            outRect.set(this.mLeft, this.mTop, this.mRight, this.mBottom);
            return;
        }
        RectF tmpRect = this.mAttachInfo.mTmpTransformRect;
        tmpRect.set(0.0f, 0.0f, getWidth(), getHeight());
        info.mMatrix.mapRect(tmpRect);
        outRect.set(((int) tmpRect.left) + this.mLeft, ((int) tmpRect.top) + this.mTop, ((int) tmpRect.right) + this.mLeft, ((int) tmpRect.bottom) + this.mTop);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean pointInView(float localX, float localY) {
        return localX >= 0.0f && localX < ((float) (this.mRight - this.mLeft)) && localY >= 0.0f && localY < ((float) (this.mBottom - this.mTop));
    }

    public boolean pointInView(float localX, float localY, float slop) {
        return localX >= (-slop) && localY >= (-slop) && localX < ((float) (this.mRight - this.mLeft)) + slop && localY < ((float) (this.mBottom - this.mTop)) + slop;
    }

    public void getFocusedRect(Rect r) {
        getDrawingRect(r);
    }

    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        if (width > 0 && height > 0) {
            r.set(0, 0, width, height);
            if (globalOffset != null) {
                globalOffset.set(-this.mScrollX, -this.mScrollY);
            }
            return this.mParent == null || this.mParent.getChildVisibleRect(this, r, globalOffset);
        }
        return false;
    }

    public final boolean getGlobalVisibleRect(Rect r) {
        return getGlobalVisibleRect(r, null);
    }

    public final boolean getLocalVisibleRect(Rect r) {
        Point offset = this.mAttachInfo != null ? this.mAttachInfo.mPoint : new Point();
        if (getGlobalVisibleRect(r, offset)) {
            r.offset(-offset.x, -offset.y);
            return true;
        }
        return false;
    }

    public void offsetTopAndBottom(int offset) {
        int minTop;
        int maxBottom;
        int yLoc;
        if (offset != 0) {
            updateMatrix();
            boolean matrixIsIdentity = this.mTransformationInfo == null || this.mTransformationInfo.mMatrixIsIdentity;
            if (matrixIsIdentity) {
                if (this.mDisplayList != null) {
                    invalidateViewProperty(false, false);
                } else {
                    ViewParent p = this.mParent;
                    if (p != null && this.mAttachInfo != null) {
                        Rect r = this.mAttachInfo.mTmpInvalRect;
                        if (offset < 0) {
                            minTop = this.mTop + offset;
                            maxBottom = this.mBottom;
                            yLoc = offset;
                        } else {
                            minTop = this.mTop;
                            maxBottom = this.mBottom + offset;
                            yLoc = 0;
                        }
                        r.set(0, yLoc, this.mRight - this.mLeft, maxBottom - minTop);
                        p.invalidateChild(this, r);
                    }
                }
            } else {
                invalidateViewProperty(false, false);
            }
            this.mTop += offset;
            this.mBottom += offset;
            if (this.mDisplayList != null) {
                this.mDisplayList.offsetTopAndBottom(offset);
                invalidateViewProperty(false, false);
                return;
            }
            if (!matrixIsIdentity) {
                invalidateViewProperty(false, true);
            }
            invalidateParentIfNeeded();
        }
    }

    public void offsetLeftAndRight(int offset) {
        int minLeft;
        int maxRight;
        if (offset != 0) {
            updateMatrix();
            boolean matrixIsIdentity = this.mTransformationInfo == null || this.mTransformationInfo.mMatrixIsIdentity;
            if (matrixIsIdentity) {
                if (this.mDisplayList != null) {
                    invalidateViewProperty(false, false);
                } else {
                    ViewParent p = this.mParent;
                    if (p != null && this.mAttachInfo != null) {
                        Rect r = this.mAttachInfo.mTmpInvalRect;
                        if (offset < 0) {
                            minLeft = this.mLeft + offset;
                            maxRight = this.mRight;
                        } else {
                            minLeft = this.mLeft;
                            maxRight = this.mRight + offset;
                        }
                        r.set(0, 0, maxRight - minLeft, this.mBottom - this.mTop);
                        p.invalidateChild(this, r);
                    }
                }
            } else {
                invalidateViewProperty(false, false);
            }
            this.mLeft += offset;
            this.mRight += offset;
            if (this.mDisplayList != null) {
                this.mDisplayList.offsetLeftAndRight(offset);
                invalidateViewProperty(false, false);
                return;
            }
            if (!matrixIsIdentity) {
                invalidateViewProperty(false, true);
            }
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(deepExport = true, prefix = "layout_")
    public ViewGroup.LayoutParams getLayoutParams() {
        return this.mLayoutParams;
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new NullPointerException("Layout parameters cannot be null");
        }
        this.mLayoutParams = params;
        resolveLayoutParams();
        if (this.mParent instanceof ViewGroup) {
            ((ViewGroup) this.mParent).onSetLayoutParams(this, params);
        }
        requestLayout();
    }

    public void resolveLayoutParams() {
        if (this.mLayoutParams != null) {
            this.mLayoutParams.resolveLayoutDirection(getLayoutDirection());
        }
    }

    public void scrollTo(int x, int y) {
        if (this.mScrollX != x || this.mScrollY != y) {
            int oldX = this.mScrollX;
            int oldY = this.mScrollY;
            this.mScrollX = x;
            this.mScrollY = y;
            invalidateParentCaches();
            onScrollChanged(this.mScrollX, this.mScrollY, oldX, oldY);
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
        }
    }

    public void scrollBy(int x, int y) {
        scrollTo(this.mScrollX + x, this.mScrollY + y);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars() {
        return this.mScrollCache != null && awakenScrollBars(this.mScrollCache.scrollBarDefaultDelayBeforeFade, true);
    }

    private boolean initialAwakenScrollBars() {
        return this.mScrollCache != null && awakenScrollBars(this.mScrollCache.scrollBarDefaultDelayBeforeFade * 4, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars(int startDelay) {
        return awakenScrollBars(startDelay, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        ScrollabilityCache scrollCache = this.mScrollCache;
        if (scrollCache == null || !scrollCache.fadeScrollBars) {
            return false;
        }
        if (scrollCache.scrollBar == null) {
            scrollCache.scrollBar = new ScrollBarDrawable();
        }
        if (isHorizontalScrollBarEnabled() || isVerticalScrollBarEnabled()) {
            if (invalidate) {
                postInvalidateOnAnimation();
            }
            if (scrollCache.state == 0) {
                startDelay = Math.max(750, startDelay);
            }
            long fadeStartTime = AnimationUtils.currentAnimationTimeMillis() + startDelay;
            scrollCache.fadeStartTime = fadeStartTime;
            scrollCache.state = 1;
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mHandler.removeCallbacks(scrollCache);
                this.mAttachInfo.mHandler.postAtTime(scrollCache, fadeStartTime);
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean skipInvalidate() {
        return ((this.mViewFlags & 12) == 0 || this.mCurrentAnimation != null || ((this.mParent instanceof ViewGroup) && ((ViewGroup) this.mParent).isViewTransitioning(this))) ? false : true;
    }

    public void invalidate(Rect dirty) {
        if (skipInvalidate()) {
            return;
        }
        if ((this.mPrivateFlags & 48) == 48 || (this.mPrivateFlags & 32768) == 32768 || (this.mPrivateFlags & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            this.mPrivateFlags &= -32769;
            this.mPrivateFlags |= Integer.MIN_VALUE;
            this.mPrivateFlags |= 2097152;
            ViewParent p = this.mParent;
            AttachInfo ai = this.mAttachInfo;
            if (p != null && ai != null) {
                int scrollX = this.mScrollX;
                int scrollY = this.mScrollY;
                Rect r = ai.mTmpInvalRect;
                r.set(dirty.left - scrollX, dirty.top - scrollY, dirty.right - scrollX, dirty.bottom - scrollY);
                this.mParent.invalidateChild(this, r);
            }
        }
    }

    public void invalidate(int l, int t, int r, int b) {
        if (skipInvalidate()) {
            return;
        }
        if ((this.mPrivateFlags & 48) == 48 || (this.mPrivateFlags & 32768) == 32768 || (this.mPrivateFlags & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            this.mPrivateFlags &= -32769;
            this.mPrivateFlags |= Integer.MIN_VALUE;
            this.mPrivateFlags |= 2097152;
            ViewParent p = this.mParent;
            AttachInfo ai = this.mAttachInfo;
            if (p != null && ai != null && l < r && t < b) {
                int scrollX = this.mScrollX;
                int scrollY = this.mScrollY;
                Rect tmpr = ai.mTmpInvalRect;
                tmpr.set(l - scrollX, t - scrollY, r - scrollX, b - scrollY);
                p.invalidateChild(this, tmpr);
            }
        }
    }

    public void invalidate() {
        invalidate(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate(boolean invalidateCache) {
        if (skipInvalidate()) {
            return;
        }
        if ((this.mPrivateFlags & 48) == 48 || ((invalidateCache && (this.mPrivateFlags & 32768) == 32768) || (this.mPrivateFlags & Integer.MIN_VALUE) != Integer.MIN_VALUE || isOpaque() != this.mLastIsOpaque)) {
            this.mLastIsOpaque = isOpaque();
            this.mPrivateFlags &= -33;
            this.mPrivateFlags |= 2097152;
            if (invalidateCache) {
                this.mPrivateFlags |= Integer.MIN_VALUE;
                this.mPrivateFlags &= -32769;
            }
            AttachInfo ai = this.mAttachInfo;
            ViewParent p = this.mParent;
            if (p != null && ai != null) {
                Rect r = ai.mTmpInvalRect;
                r.set(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
                p.invalidateChild(this, r);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateViewProperty(boolean invalidateParent, boolean forceRedraw) {
        if (this.mDisplayList == null || (this.mPrivateFlags & 64) == 64) {
            if (invalidateParent) {
                invalidateParentCaches();
            }
            if (forceRedraw) {
                this.mPrivateFlags |= 32;
            }
            invalidate(false);
            return;
        }
        AttachInfo ai = this.mAttachInfo;
        ViewParent p = this.mParent;
        if (p != null && ai != null) {
            Rect r = ai.mTmpInvalRect;
            r.set(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            if (this.mParent instanceof ViewGroup) {
                ((ViewGroup) this.mParent).invalidateChildFast(this, r);
            } else {
                this.mParent.invalidateChild(this, r);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void transformRect(Rect rect) {
        if (!getMatrix().isIdentity()) {
            RectF boundingRect = this.mAttachInfo.mTmpTransformRect;
            boundingRect.set(rect);
            getMatrix().mapRect(boundingRect);
            rect.set((int) Math.floor(boundingRect.left), (int) Math.floor(boundingRect.top), (int) Math.ceil(boundingRect.right), (int) Math.ceil(boundingRect.bottom));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidateParentCaches() {
        if (this.mParent instanceof View) {
            ((View) this.mParent).mPrivateFlags |= Integer.MIN_VALUE;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidateParentIfNeeded() {
        if (isHardwareAccelerated() && (this.mParent instanceof View)) {
            ((View) this.mParent).invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isOpaque() {
        return (this.mPrivateFlags & 25165824) == 25165824 && getFinalAlpha() >= 1.0f;
    }

    protected void computeOpaqueFlags() {
        if (this.mBackground != null && this.mBackground.getOpacity() == -1) {
            this.mPrivateFlags |= 8388608;
        } else {
            this.mPrivateFlags &= -8388609;
        }
        int flags = this.mViewFlags;
        if (((flags & 512) == 0 && (flags & 256) == 0) || (flags & 50331648) == 0 || (flags & 50331648) == 33554432) {
            this.mPrivateFlags |= 16777216;
        } else {
            this.mPrivateFlags &= -16777217;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasOpaqueScrollbars() {
        return (this.mPrivateFlags & 16777216) == 16777216;
    }

    public Handler getHandler() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler;
        }
        return null;
    }

    public ViewRootImpl getViewRootImpl() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mViewRootImpl;
        }
        return null;
    }

    public boolean post(Runnable action) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.post(action);
        }
        ViewRootImpl.getRunQueue().post(action);
        return true;
    }

    public boolean postDelayed(Runnable action, long delayMillis) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.postDelayed(action, delayMillis);
        }
        ViewRootImpl.getRunQueue().postDelayed(action, delayMillis);
        return true;
    }

    public void postOnAnimation(Runnable action) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallback(1, action, null);
        } else {
            ViewRootImpl.getRunQueue().post(action);
        }
    }

    public void postOnAnimationDelayed(Runnable action, long delayMillis) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, action, null, delayMillis);
        } else {
            ViewRootImpl.getRunQueue().postDelayed(action, delayMillis);
        }
    }

    public boolean removeCallbacks(Runnable action) {
        if (action != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mHandler.removeCallbacks(action);
                attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, action, null);
                return true;
            }
            ViewRootImpl.getRunQueue().removeCallbacks(action);
            return true;
        }
        return true;
    }

    public void postInvalidate() {
        postInvalidateDelayed(0L);
    }

    public void postInvalidate(int left, int top, int right, int bottom) {
        postInvalidateDelayed(0L, left, top, right, bottom);
    }

    public void postInvalidateDelayed(long delayMilliseconds) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateDelayed(this, delayMilliseconds);
        }
    }

    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            AttachInfo.InvalidateInfo info = AttachInfo.InvalidateInfo.obtain();
            info.target = this;
            info.left = left;
            info.top = top;
            info.right = right;
            info.bottom = bottom;
            attachInfo.mViewRootImpl.dispatchInvalidateRectDelayed(info, delayMilliseconds);
        }
    }

    public void postInvalidateOnAnimation() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateOnAnimation(this);
        }
    }

    public void postInvalidateOnAnimation(int left, int top, int right, int bottom) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            AttachInfo.InvalidateInfo info = AttachInfo.InvalidateInfo.obtain();
            info.target = this;
            info.left = left;
            info.top = top;
            info.right = right;
            info.bottom = bottom;
            attachInfo.mViewRootImpl.dispatchInvalidateRectOnAnimation(info);
        }
    }

    private void postSendViewScrolledAccessibilityEventCallback() {
        if (this.mSendViewScrolledAccessibilityEvent == null) {
            this.mSendViewScrolledAccessibilityEvent = new SendViewScrolledAccessibilityEvent();
        }
        if (!this.mSendViewScrolledAccessibilityEvent.mIsPending) {
            this.mSendViewScrolledAccessibilityEvent.mIsPending = true;
            postDelayed(this.mSendViewScrolledAccessibilityEvent, ViewConfiguration.getSendRecurringAccessibilityEventsInterval());
        }
    }

    public void computeScroll() {
    }

    public boolean isHorizontalFadingEdgeEnabled() {
        return (this.mViewFlags & 4096) == 4096;
    }

    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        if (isHorizontalFadingEdgeEnabled() != horizontalFadingEdgeEnabled) {
            if (horizontalFadingEdgeEnabled) {
                initScrollCache();
            }
            this.mViewFlags ^= 4096;
        }
    }

    public boolean isVerticalFadingEdgeEnabled() {
        return (this.mViewFlags & 8192) == 8192;
    }

    public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
        if (isVerticalFadingEdgeEnabled() != verticalFadingEdgeEnabled) {
            if (verticalFadingEdgeEnabled) {
                initScrollCache();
            }
            this.mViewFlags ^= 8192;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getTopFadingEdgeStrength() {
        return computeVerticalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        return computeVerticalScrollOffset() + computeVerticalScrollExtent() < computeVerticalScrollRange() ? 1.0f : 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getLeftFadingEdgeStrength() {
        return computeHorizontalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getRightFadingEdgeStrength() {
        return computeHorizontalScrollOffset() + computeHorizontalScrollExtent() < computeHorizontalScrollRange() ? 1.0f : 0.0f;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return (this.mViewFlags & 256) == 256;
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        if (isHorizontalScrollBarEnabled() != horizontalScrollBarEnabled) {
            this.mViewFlags ^= 256;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public boolean isVerticalScrollBarEnabled() {
        return (this.mViewFlags & 512) == 512;
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (isVerticalScrollBarEnabled() != verticalScrollBarEnabled) {
            this.mViewFlags ^= 512;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    protected void recomputePadding() {
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
    }

    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        scrollabilityCache.fadeScrollBars = fadeScrollbars;
        if (fadeScrollbars) {
            scrollabilityCache.state = 0;
        } else {
            scrollabilityCache.state = 1;
        }
    }

    public boolean isScrollbarFadingEnabled() {
        return this.mScrollCache != null && this.mScrollCache.fadeScrollBars;
    }

    public int getScrollBarDefaultDelayBeforeFade() {
        return this.mScrollCache == null ? ViewConfiguration.getScrollDefaultDelay() : this.mScrollCache.scrollBarDefaultDelayBeforeFade;
    }

    public void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) {
        getScrollCache().scrollBarDefaultDelayBeforeFade = scrollBarDefaultDelayBeforeFade;
    }

    public int getScrollBarFadeDuration() {
        return this.mScrollCache == null ? ViewConfiguration.getScrollBarFadeDuration() : this.mScrollCache.scrollBarFadeDuration;
    }

    public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
        getScrollCache().scrollBarFadeDuration = scrollBarFadeDuration;
    }

    public int getScrollBarSize() {
        return this.mScrollCache == null ? ViewConfiguration.get(this.mContext).getScaledScrollBarSize() : this.mScrollCache.scrollBarSize;
    }

    public void setScrollBarSize(int scrollBarSize) {
        getScrollCache().scrollBarSize = scrollBarSize;
    }

    public void setScrollBarStyle(int style) {
        if (style != (this.mViewFlags & 50331648)) {
            this.mViewFlags = (this.mViewFlags & (-50331649)) | (style & 50331648);
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, to = "INSIDE_OVERLAY"), @ViewDebug.IntToString(from = 16777216, to = "INSIDE_INSET"), @ViewDebug.IntToString(from = 33554432, to = "OUTSIDE_OVERLAY"), @ViewDebug.IntToString(from = 50331648, to = "OUTSIDE_INSET")})
    public int getScrollBarStyle() {
        return this.mViewFlags & 50331648;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeHorizontalScrollRange() {
        return getWidth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeHorizontalScrollOffset() {
        return this.mScrollX;
    }

    protected int computeHorizontalScrollExtent() {
        return getWidth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeVerticalScrollRange() {
        return getHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeVerticalScrollOffset() {
        return this.mScrollY;
    }

    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    public boolean canScrollHorizontally(int direction) {
        int offset = computeHorizontalScrollOffset();
        int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) {
            return false;
        }
        return direction < 0 ? offset > 0 : offset < range - 1;
    }

    public boolean canScrollVertically(int direction) {
        int offset = computeVerticalScrollOffset();
        int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) {
            return false;
        }
        return direction < 0 ? offset > 0 : offset < range - 1;
    }

    protected final void onDrawScrollBars(Canvas canvas) {
        int state;
        int left;
        ScrollabilityCache cache = this.mScrollCache;
        if (cache == null || (state = cache.state) == 0) {
            return;
        }
        boolean invalidate = false;
        if (state == 2) {
            if (cache.interpolatorValues == null) {
                cache.interpolatorValues = new float[1];
            }
            float[] values = cache.interpolatorValues;
            if (cache.scrollBarInterpolator.timeToValues(values) == Interpolator.Result.FREEZE_END) {
                cache.state = 0;
            } else {
                cache.scrollBar.setAlpha(Math.round(values[0]));
            }
            invalidate = true;
        } else {
            cache.scrollBar.setAlpha(255);
        }
        int viewFlags = this.mViewFlags;
        boolean drawHorizontalScrollBar = (viewFlags & 256) == 256;
        boolean drawVerticalScrollBar = (viewFlags & 512) == 512 && !isVerticalScrollBarHidden();
        if (drawVerticalScrollBar || drawHorizontalScrollBar) {
            int width = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            ScrollBarDrawable scrollBar = cache.scrollBar;
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            int inside = (viewFlags & 33554432) == 0 ? -1 : 0;
            if (drawHorizontalScrollBar) {
                int size = scrollBar.getSize(false);
                if (size <= 0) {
                    size = cache.scrollBarSize;
                }
                scrollBar.setParameters(computeHorizontalScrollRange(), computeHorizontalScrollOffset(), computeHorizontalScrollExtent(), false);
                int verticalScrollBarGap = drawVerticalScrollBar ? getVerticalScrollbarWidth() : 0;
                int top = ((scrollY + height) - size) - (this.mUserPaddingBottom & inside);
                int left2 = scrollX + (this.mPaddingLeft & inside);
                int right = ((scrollX + width) - (this.mUserPaddingRight & inside)) - verticalScrollBarGap;
                int bottom = top + size;
                onDrawHorizontalScrollBar(canvas, scrollBar, left2, top, right, bottom);
                if (invalidate) {
                    invalidate(left2, top, right, bottom);
                }
            }
            if (drawVerticalScrollBar) {
                int size2 = scrollBar.getSize(true);
                if (size2 <= 0) {
                    size2 = cache.scrollBarSize;
                }
                scrollBar.setParameters(computeVerticalScrollRange(), computeVerticalScrollOffset(), computeVerticalScrollExtent(), true);
                int verticalScrollbarPosition = this.mVerticalScrollbarPosition;
                if (verticalScrollbarPosition == 0) {
                    verticalScrollbarPosition = isLayoutRtl() ? 1 : 2;
                }
                switch (verticalScrollbarPosition) {
                    case 1:
                        left = scrollX + (this.mUserPaddingLeft & inside);
                        break;
                    case 2:
                    default:
                        left = ((scrollX + width) - size2) - (this.mUserPaddingRight & inside);
                        break;
                }
                int top2 = scrollY + (this.mPaddingTop & inside);
                int right2 = left + size2;
                int bottom2 = (scrollY + height) - (this.mUserPaddingBottom & inside);
                onDrawVerticalScrollBar(canvas, scrollBar, left, top2, right2, bottom2);
                if (invalidate) {
                    invalidate(left, top2, right2, bottom2);
                }
            }
        }
    }

    protected boolean isVerticalScrollBarHidden() {
        return false;
    }

    protected void onDrawHorizontalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void assignParent(ViewParent parent) {
        if (this.mParent == null) {
            this.mParent = parent;
        } else if (parent == null) {
            this.mParent = null;
        } else {
            throw new RuntimeException("view " + this + " being added, but it already has a parent");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAttachedToWindow() {
        if ((this.mPrivateFlags & 512) != 0) {
            this.mParent.requestTransparentRegion(this);
        }
        if ((this.mPrivateFlags & 134217728) != 0) {
            initialAwakenScrollBars();
            this.mPrivateFlags &= -134217729;
        }
        this.mPrivateFlags3 &= -5;
        jumpDrawablesToCurrentState();
        resetSubtreeAccessibilityStateChanged();
        if (isFocused()) {
            InputMethodManager imm = InputMethodManager.peekInstance();
            imm.focusIn(this);
        }
        if (this.mDisplayList != null) {
            this.mDisplayList.clearDirty();
        }
    }

    public boolean resolveRtlPropertiesIfNeeded() {
        if (needRtlPropertiesResolution()) {
            if (!isLayoutDirectionResolved()) {
                resolveLayoutDirection();
                resolveLayoutParams();
            }
            if (!isTextDirectionResolved()) {
                resolveTextDirection();
            }
            if (!isTextAlignmentResolved()) {
                resolveTextAlignment();
            }
            if (!isPaddingResolved()) {
                resolvePadding();
            }
            if (!isDrawablesResolved()) {
                resolveDrawables();
            }
            onRtlPropertiesChanged(getLayoutDirection());
            return true;
        }
        return false;
    }

    public void resetRtlProperties() {
        resetResolvedLayoutDirection();
        resetResolvedTextDirection();
        resetResolvedTextAlignment();
        resetResolvedPadding();
        resetResolvedDrawables();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchScreenStateChanged(int screenState) {
        onScreenStateChanged(screenState);
    }

    public void onScreenStateChanged(int screenState) {
    }

    private boolean hasRtlSupport() {
        return this.mContext.getApplicationInfo().hasRtlSupport();
    }

    private boolean isRtlCompatibilityMode() {
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        return targetSdkVersion < 17 || !hasRtlSupport();
    }

    private boolean needRtlPropertiesResolution() {
        return (this.mPrivateFlags2 & ALL_RTL_PROPERTIES_RESOLVED) != ALL_RTL_PROPERTIES_RESOLVED;
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
    }

    public boolean resolveLayoutDirection() {
        this.mPrivateFlags2 &= -49;
        if (hasRtlSupport()) {
            switch ((this.mPrivateFlags2 & 12) >> 2) {
                case 1:
                    this.mPrivateFlags2 |= 16;
                    break;
                case 2:
                    if (canResolveLayoutDirection()) {
                        try {
                            if (this.mParent.isLayoutDirectionResolved()) {
                                if (this.mParent.getLayoutDirection() == 1) {
                                    this.mPrivateFlags2 |= 16;
                                }
                                break;
                            } else {
                                return false;
                            }
                        } catch (AbstractMethodError e) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            break;
                        }
                    } else {
                        return false;
                    }
                case 3:
                    if (1 == TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())) {
                        this.mPrivateFlags2 |= 16;
                        break;
                    }
                    break;
            }
        }
        this.mPrivateFlags2 |= 32;
        return true;
    }

    public boolean canResolveLayoutDirection() {
        switch (getRawLayoutDirection()) {
            case 2:
                if (this.mParent != null) {
                    try {
                        return this.mParent.canResolveLayoutDirection();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        return false;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void resetResolvedLayoutDirection() {
        this.mPrivateFlags2 &= -49;
    }

    public boolean isLayoutDirectionInherited() {
        return getRawLayoutDirection() == 2;
    }

    public boolean isLayoutDirectionResolved() {
        return (this.mPrivateFlags2 & 32) == 32;
    }

    boolean isPaddingResolved() {
        return (this.mPrivateFlags2 & 536870912) == 536870912;
    }

    public void resolvePadding() {
        int resolvedLayoutDirection = getLayoutDirection();
        if (!isRtlCompatibilityMode()) {
            switch (resolvedLayoutDirection) {
                case 0:
                default:
                    if (this.mUserPaddingStart != Integer.MIN_VALUE) {
                        this.mUserPaddingLeft = this.mUserPaddingStart;
                    } else {
                        this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                    }
                    if (this.mUserPaddingEnd != Integer.MIN_VALUE) {
                        this.mUserPaddingRight = this.mUserPaddingEnd;
                        break;
                    } else {
                        this.mUserPaddingRight = this.mUserPaddingRightInitial;
                        break;
                    }
                case 1:
                    if (this.mUserPaddingStart != Integer.MIN_VALUE) {
                        this.mUserPaddingRight = this.mUserPaddingStart;
                    } else {
                        this.mUserPaddingRight = this.mUserPaddingRightInitial;
                    }
                    if (this.mUserPaddingEnd != Integer.MIN_VALUE) {
                        this.mUserPaddingLeft = this.mUserPaddingEnd;
                        break;
                    } else {
                        this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                        break;
                    }
            }
            this.mUserPaddingBottom = this.mUserPaddingBottom >= 0 ? this.mUserPaddingBottom : this.mPaddingBottom;
        }
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
        onRtlPropertiesChanged(resolvedLayoutDirection);
        this.mPrivateFlags2 |= 536870912;
    }

    public void resetResolvedPadding() {
        this.mPrivateFlags2 &= -536870913;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mPrivateFlags &= -67108865;
        this.mPrivateFlags3 &= -5;
        removeUnsetPressCallback();
        removeLongPressCallback();
        removePerformClickCallback();
        removeSendViewScrolledAccessibilityEventCallback();
        destroyDrawingCache();
        destroyLayer(false);
        cleanupDraw();
        this.mCurrentAnimation = null;
    }

    private void cleanupDraw() {
        if (this.mAttachInfo != null) {
            if (this.mDisplayList != null) {
                this.mDisplayList.markDirty();
                this.mAttachInfo.mViewRootImpl.enqueueDisplayList(this.mDisplayList);
            }
            this.mAttachInfo.mViewRootImpl.cancelInvalidate(this);
            return;
        }
        resetDisplayList();
    }

    public boolean executeHardwareAction(Runnable action) {
        if (this.mAttachInfo != null && this.mAttachInfo.mHardwareRenderer != null) {
            return this.mAttachInfo.mHardwareRenderer.safelyRun(action);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateInheritedLayoutMode(int layoutModeOfRoot) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getWindowAttachCount() {
        return this.mWindowAttachCount;
    }

    public IBinder getWindowToken() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mWindowToken;
        }
        return null;
    }

    public WindowId getWindowId() {
        if (this.mAttachInfo == null) {
            return null;
        }
        if (this.mAttachInfo.mWindowId == null) {
            try {
                this.mAttachInfo.mIWindowId = this.mAttachInfo.mSession.getWindowId(this.mAttachInfo.mWindowToken);
                this.mAttachInfo.mWindowId = new WindowId(this.mAttachInfo.mIWindowId);
            } catch (RemoteException e) {
            }
        }
        return this.mAttachInfo.mWindowId;
    }

    public IBinder getApplicationWindowToken() {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            IBinder appWindowToken = ai.mPanelParentWindowToken;
            if (appWindowToken == null) {
                appWindowToken = ai.mWindowToken;
            }
            return appWindowToken;
        }
        return null;
    }

    public Display getDisplay() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mDisplay;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IWindowSession getWindowSession() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mSession;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchAttachedToWindow(AttachInfo info, int visibility) {
        this.mAttachInfo = info;
        if (this.mOverlay != null) {
            this.mOverlay.getOverlayView().dispatchAttachedToWindow(info, visibility);
        }
        this.mWindowAttachCount++;
        this.mPrivateFlags |= 1024;
        if (this.mFloatingTreeObserver != null) {
            info.mTreeObserver.merge(this.mFloatingTreeObserver);
            this.mFloatingTreeObserver = null;
        }
        if ((this.mPrivateFlags & 524288) != 0) {
            this.mAttachInfo.mScrollContainers.add(this);
            this.mPrivateFlags |= 1048576;
        }
        performCollectViewAttributes(this.mAttachInfo, visibility);
        onAttachedToWindow();
        ListenerInfo li = this.mListenerInfo;
        CopyOnWriteArrayList<OnAttachStateChangeListener> listeners = li != null ? li.mOnAttachStateChangeListeners : null;
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                OnAttachStateChangeListener listener = i$.next();
                listener.onViewAttachedToWindow(this);
            }
        }
        int vis = info.mWindowVisibility;
        if (vis != 8) {
            onWindowVisibilityChanged(vis);
        }
        if ((this.mPrivateFlags & 1024) != 0) {
            refreshDrawableState();
        }
        needGlobalAttributesUpdate(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchDetachedFromWindow() {
        AttachInfo info = this.mAttachInfo;
        if (info != null) {
            int vis = info.mWindowVisibility;
            if (vis != 8) {
                onWindowVisibilityChanged(8);
            }
        }
        onDetachedFromWindow();
        ListenerInfo li = this.mListenerInfo;
        CopyOnWriteArrayList<OnAttachStateChangeListener> listeners = li != null ? li.mOnAttachStateChangeListeners : null;
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                OnAttachStateChangeListener listener = i$.next();
                listener.onViewDetachedFromWindow(this);
            }
        }
        if ((this.mPrivateFlags & 1048576) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
            this.mPrivateFlags &= -1048577;
        }
        this.mAttachInfo = null;
        if (this.mOverlay != null) {
            this.mOverlay.getOverlayView().dispatchDetachedFromWindow();
        }
    }

    public final void cancelPendingInputEvents() {
        dispatchCancelPendingInputEvents();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchCancelPendingInputEvents() {
        this.mPrivateFlags3 &= -17;
        onCancelPendingInputEvents();
        if ((this.mPrivateFlags3 & 16) != 16) {
            throw new SuperNotCalledException("View " + getClass().getSimpleName() + " did not call through to super.onCancelPendingInputEvents()");
        }
    }

    public void onCancelPendingInputEvents() {
        removePerformClickCallback();
        cancelLongPress();
        this.mPrivateFlags3 |= 16;
    }

    public void saveHierarchyState(SparseArray<Parcelable> container) {
        dispatchSaveInstanceState(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        if (this.mID != -1 && (this.mViewFlags & 65536) == 0) {
            this.mPrivateFlags &= -131073;
            Parcelable state = onSaveInstanceState();
            if ((this.mPrivateFlags & 131072) == 0) {
                throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
            }
            if (state != null) {
                container.put(this.mID, state);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        this.mPrivateFlags |= 131072;
        return BaseSavedState.EMPTY_STATE;
    }

    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        dispatchRestoreInstanceState(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        Parcelable state;
        if (this.mID != -1 && (state = container.get(this.mID)) != null) {
            this.mPrivateFlags &= -131073;
            onRestoreInstanceState(state);
            if ((this.mPrivateFlags & 131072) == 0) {
                throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        this.mPrivateFlags |= 131072;
        if (state != BaseSavedState.EMPTY_STATE && state != null) {
            throw new IllegalArgumentException("Wrong state class, expecting View State but received " + state.getClass().toString() + " instead. This usually happens when two views of different type have the same id in the same hierarchy. This view's id is " + ViewDebug.resolveId(this.mContext, getId()) + ". Make sure other views do not use the same id.");
        }
    }

    public long getDrawingTime() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mDrawingTime;
        }
        return 0L;
    }

    public void setDuplicateParentStateEnabled(boolean enabled) {
        setFlags(enabled ? 4194304 : 0, 4194304);
    }

    public boolean isDuplicateParentStateEnabled() {
        return (this.mViewFlags & 4194304) == 4194304;
    }

    public void setLayerType(int layerType, Paint paint) {
        if (layerType < 0 || layerType > 2) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        }
        if (layerType == this.mLayerType) {
            if (layerType != 0 && paint != this.mLayerPaint) {
                this.mLayerPaint = paint == null ? new Paint() : paint;
                invalidateParentCaches();
                invalidate(true);
                return;
            }
            return;
        }
        switch (this.mLayerType) {
            case 2:
                destroyLayer(false);
            case 1:
                destroyDrawingCache();
                break;
        }
        this.mLayerType = layerType;
        boolean layerDisabled = this.mLayerType == 0;
        this.mLayerPaint = layerDisabled ? null : paint == null ? new Paint() : paint;
        this.mLocalDirtyRect = layerDisabled ? null : new Rect();
        invalidateParentCaches();
        invalidate(true);
    }

    public void setLayerPaint(Paint paint) {
        int layerType = getLayerType();
        if (layerType != 0) {
            this.mLayerPaint = paint == null ? new Paint() : paint;
            if (layerType == 2) {
                HardwareLayer layer = getHardwareLayer();
                if (layer != null) {
                    layer.setLayerPaint(paint);
                }
                invalidateViewProperty(false, false);
                return;
            }
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasStaticLayer() {
        return true;
    }

    public int getLayerType() {
        return this.mLayerType;
    }

    public void buildLayer() {
        if (this.mLayerType == 0) {
            return;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            throw new IllegalStateException("This view must be attached to a window first");
        }
        switch (this.mLayerType) {
            case 1:
                buildDrawingCache(true);
                return;
            case 2:
                if (attachInfo.mHardwareRenderer != null && attachInfo.mHardwareRenderer.isEnabled() && attachInfo.mHardwareRenderer.validate()) {
                    getHardwareLayer();
                    if (!attachInfo.mTreeObserver.hasOnPreDrawListeners()) {
                        attachInfo.mViewRootImpl.dispatchFlushHardwareLayerUpdates();
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    HardwareLayer getHardwareLayer() {
        if (this.mAttachInfo == null || this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled() || !this.mAttachInfo.mHardwareRenderer.validate()) {
            return null;
        }
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        if (width == 0 || height == 0) {
            return null;
        }
        if ((this.mPrivateFlags & 32768) == 0 || this.mHardwareLayer == null) {
            if (this.mHardwareLayer == null) {
                this.mHardwareLayer = this.mAttachInfo.mHardwareRenderer.createHardwareLayer(width, height, isOpaque());
                this.mLocalDirtyRect.set(0, 0, width, height);
            } else {
                if ((this.mHardwareLayer.getWidth() != width || this.mHardwareLayer.getHeight() != height) && this.mHardwareLayer.resize(width, height)) {
                    this.mLocalDirtyRect.set(0, 0, width, height);
                }
                computeOpaqueFlags();
                boolean opaque = isOpaque();
                if (this.mHardwareLayer.isValid() && this.mHardwareLayer.isOpaque() != opaque) {
                    this.mHardwareLayer.setOpaque(opaque);
                    this.mLocalDirtyRect.set(0, 0, width, height);
                }
            }
            if (!this.mHardwareLayer.isValid()) {
                return null;
            }
            this.mHardwareLayer.setLayerPaint(this.mLayerPaint);
            this.mHardwareLayer.redrawLater(getHardwareLayerDisplayList(this.mHardwareLayer), this.mLocalDirtyRect);
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot != null) {
                viewRoot.pushHardwareLayerUpdate(this.mHardwareLayer);
            }
            this.mLocalDirtyRect.setEmpty();
        }
        return this.mHardwareLayer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean destroyLayer(boolean valid) {
        if (this.mHardwareLayer != null) {
            AttachInfo info = this.mAttachInfo;
            if (info == null || info.mHardwareRenderer == null || !info.mHardwareRenderer.isEnabled()) {
                return true;
            }
            if (valid || info.mHardwareRenderer.validate()) {
                info.mHardwareRenderer.cancelLayerUpdate(this.mHardwareLayer);
                this.mHardwareLayer.destroy();
                this.mHardwareLayer = null;
                invalidate(true);
                invalidateParentCaches();
                return true;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void destroyHardwareResources() {
        resetDisplayList();
        destroyLayer(true);
    }

    public void setDrawingCacheEnabled(boolean enabled) {
        this.mCachingFailed = false;
        setFlags(enabled ? 32768 : 0, 32768);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isDrawingCacheEnabled() {
        return (this.mViewFlags & 32768) == 32768;
    }

    public void outputDirtyFlags(String indent, boolean clear, int clearMask) {
        Log.d(VIEW_LOG_TAG, indent + this + "             DIRTY(" + (this.mPrivateFlags & 6291456) + ") DRAWN(" + (this.mPrivateFlags & 32) + Separators.RPAREN + " CACHE_VALID(" + (this.mPrivateFlags & 32768) + ") INVALIDATED(" + (this.mPrivateFlags & Integer.MIN_VALUE) + Separators.RPAREN);
        if (clear) {
            this.mPrivateFlags &= clearMask;
        }
        if (this instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) this;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                child.outputDirtyFlags(indent + "  ", clear, clearMask);
            }
        }
    }

    protected void dispatchGetDisplayList() {
    }

    public boolean canHaveDisplayList() {
        return (this.mAttachInfo == null || this.mAttachInfo.mHardwareRenderer == null) ? false : true;
    }

    public HardwareRenderer getHardwareRenderer() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mHardwareRenderer;
        }
        return null;
    }

    /* JADX WARN: Finally extract failed */
    private DisplayList getDisplayList(DisplayList displayList, boolean isLayer) {
        if (!canHaveDisplayList()) {
            return null;
        }
        if ((this.mPrivateFlags & 32768) == 0 || displayList == null || !displayList.isValid() || (!isLayer && this.mRecreateDisplayList)) {
            if (displayList != null && displayList.isValid() && !isLayer && !this.mRecreateDisplayList) {
                this.mPrivateFlags |= 32800;
                this.mPrivateFlags &= -6291457;
                dispatchGetDisplayList();
                return displayList;
            }
            if (!isLayer) {
                this.mRecreateDisplayList = true;
            }
            if (displayList == null) {
                displayList = this.mAttachInfo.mHardwareRenderer.createDisplayList(getClass().getName());
                invalidateParentCaches();
            }
            boolean caching = false;
            int width = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            int layerType = getLayerType();
            HardwareCanvas canvas = displayList.start(width, height);
            try {
                if (!isLayer && layerType != 0) {
                    if (layerType == 2) {
                        HardwareLayer layer = getHardwareLayer();
                        if (layer != null && layer.isValid()) {
                            canvas.drawHardwareLayer(layer, 0.0f, 0.0f, this.mLayerPaint);
                        } else {
                            canvas.saveLayer(0.0f, 0.0f, this.mRight - this.mLeft, this.mBottom - this.mTop, this.mLayerPaint, 20);
                        }
                        caching = true;
                    } else {
                        buildDrawingCache(true);
                        Bitmap cache = getDrawingCache(true);
                        if (cache != null) {
                            canvas.drawBitmap(cache, 0.0f, 0.0f, this.mLayerPaint);
                            caching = true;
                        }
                    }
                } else {
                    computeScroll();
                    canvas.translate(-this.mScrollX, -this.mScrollY);
                    if (!isLayer) {
                        this.mPrivateFlags |= 32800;
                        this.mPrivateFlags &= -6291457;
                    }
                    if ((this.mPrivateFlags & 128) == 128) {
                        dispatchDraw(canvas);
                        if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
                            this.mOverlay.getOverlayView().draw(canvas);
                        }
                    } else {
                        draw(canvas);
                    }
                }
                displayList.end();
                displayList.setCaching(caching);
                if (isLayer) {
                    displayList.setLeftTopRightBottom(0, 0, width, height);
                } else {
                    setDisplayListProperties(displayList);
                }
            } catch (Throwable th) {
                displayList.end();
                displayList.setCaching(false);
                if (isLayer) {
                    displayList.setLeftTopRightBottom(0, 0, width, height);
                } else {
                    setDisplayListProperties(displayList);
                }
                throw th;
            }
        } else if (!isLayer) {
            this.mPrivateFlags |= 32800;
            this.mPrivateFlags &= -6291457;
        }
        return displayList;
    }

    private DisplayList getHardwareLayerDisplayList(HardwareLayer layer) {
        DisplayList displayList = getDisplayList(layer.getDisplayList(), true);
        layer.setDisplayList(displayList);
        return displayList;
    }

    public DisplayList getDisplayList() {
        this.mDisplayList = getDisplayList(this.mDisplayList, false);
        return this.mDisplayList;
    }

    private void clearDisplayList() {
        if (this.mDisplayList != null) {
            this.mDisplayList.clear();
        }
    }

    private void resetDisplayList() {
        if (this.mDisplayList != null) {
            this.mDisplayList.reset();
        }
    }

    public Bitmap getDrawingCache() {
        return getDrawingCache(false);
    }

    public Bitmap getDrawingCache(boolean autoScale) {
        if ((this.mViewFlags & 131072) == 131072) {
            return null;
        }
        if ((this.mViewFlags & 32768) == 32768) {
            buildDrawingCache(autoScale);
        }
        return autoScale ? this.mDrawingCache : this.mUnscaledDrawingCache;
    }

    public void destroyDrawingCache() {
        if (this.mDrawingCache != null) {
            this.mDrawingCache.recycle();
            this.mDrawingCache = null;
        }
        if (this.mUnscaledDrawingCache != null) {
            this.mUnscaledDrawingCache.recycle();
            this.mUnscaledDrawingCache = null;
        }
    }

    public void setDrawingCacheBackgroundColor(int color) {
        if (color != this.mDrawingCacheBackgroundColor) {
            this.mDrawingCacheBackgroundColor = color;
            this.mPrivateFlags &= -32769;
        }
    }

    public int getDrawingCacheBackgroundColor() {
        return this.mDrawingCacheBackgroundColor;
    }

    public void buildDrawingCache() {
        buildDrawingCache(false);
    }

    public void buildDrawingCache(boolean autoScale) {
        Bitmap.Config quality;
        Canvas canvas;
        if ((this.mPrivateFlags & 32768) != 0) {
            if (autoScale) {
                if (this.mDrawingCache != null) {
                    return;
                }
            } else if (this.mUnscaledDrawingCache != null) {
                return;
            }
        }
        this.mCachingFailed = false;
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        boolean scalingRequired = attachInfo != null && attachInfo.mScalingRequired;
        if (autoScale && scalingRequired) {
            width = (int) ((width * attachInfo.mApplicationScale) + 0.5f);
            height = (int) ((height * attachInfo.mApplicationScale) + 0.5f);
        }
        int drawingCacheBackgroundColor = this.mDrawingCacheBackgroundColor;
        boolean opaque = drawingCacheBackgroundColor != 0 || isOpaque();
        boolean use32BitCache = attachInfo != null && attachInfo.mUse32BitDrawingCache;
        long projectedBitmapSize = width * height * ((!opaque || use32BitCache) ? 4 : 2);
        long drawingCacheSize = ViewConfiguration.get(this.mContext).getScaledMaximumDrawingCacheSize();
        if (width <= 0 || height <= 0 || projectedBitmapSize > drawingCacheSize) {
            if (width > 0 && height > 0) {
                Log.w(VIEW_LOG_TAG, "View too large to fit into drawing cache, needs " + projectedBitmapSize + " bytes, only " + drawingCacheSize + " available");
            }
            destroyDrawingCache();
            this.mCachingFailed = true;
            return;
        }
        boolean clear = true;
        Bitmap bitmap = autoScale ? this.mDrawingCache : this.mUnscaledDrawingCache;
        if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
            if (!opaque) {
                switch (this.mViewFlags & DRAWING_CACHE_QUALITY_MASK) {
                    case 0:
                    case 524288:
                    case 1048576:
                    default:
                        quality = Bitmap.Config.ARGB_8888;
                        break;
                }
            } else {
                quality = use32BitCache ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
            try {
                bitmap = Bitmap.createBitmap(this.mResources.getDisplayMetrics(), width, height, quality);
                bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                if (autoScale) {
                    this.mDrawingCache = bitmap;
                } else {
                    this.mUnscaledDrawingCache = bitmap;
                }
                if (opaque && use32BitCache) {
                    bitmap.setHasAlpha(false);
                }
                clear = drawingCacheBackgroundColor != 0;
            } catch (OutOfMemoryError e) {
                if (autoScale) {
                    this.mDrawingCache = null;
                } else {
                    this.mUnscaledDrawingCache = null;
                }
                this.mCachingFailed = true;
                return;
            }
        }
        if (attachInfo != null) {
            canvas = attachInfo.mCanvas;
            if (canvas == null) {
                canvas = new Canvas();
            }
            canvas.setBitmap(bitmap);
            attachInfo.mCanvas = null;
        } else {
            canvas = new Canvas(bitmap);
        }
        if (clear) {
            bitmap.eraseColor(drawingCacheBackgroundColor);
        }
        computeScroll();
        int restoreCount = canvas.save();
        if (autoScale && scalingRequired) {
            float scale = attachInfo.mApplicationScale;
            canvas.scale(scale, scale);
        }
        canvas.translate(-this.mScrollX, -this.mScrollY);
        this.mPrivateFlags |= 32;
        if (this.mAttachInfo == null || !this.mAttachInfo.mHardwareAccelerated || this.mLayerType != 0) {
            this.mPrivateFlags |= 32768;
        }
        if ((this.mPrivateFlags & 128) == 128) {
            this.mPrivateFlags &= -6291457;
            dispatchDraw(canvas);
            if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().draw(canvas);
            }
        } else {
            draw(canvas);
        }
        canvas.restoreToCount(restoreCount);
        canvas.setBitmap(null);
        if (attachInfo != null) {
            attachInfo.mCanvas = canvas;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap createSnapshot(Bitmap.Config quality, int backgroundColor, boolean skipChildren) {
        Canvas canvas;
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        float scale = attachInfo != null ? attachInfo.mApplicationScale : 1.0f;
        int width2 = (int) ((width * scale) + 0.5f);
        int height2 = (int) ((height * scale) + 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(this.mResources.getDisplayMetrics(), width2 > 0 ? width2 : 1, height2 > 0 ? height2 : 1, quality);
        if (bitmap == null) {
            throw new OutOfMemoryError();
        }
        Resources resources = getResources();
        if (resources != null) {
            bitmap.setDensity(resources.getDisplayMetrics().densityDpi);
        }
        if (attachInfo != null) {
            canvas = attachInfo.mCanvas;
            if (canvas == null) {
                canvas = new Canvas();
            }
            canvas.setBitmap(bitmap);
            attachInfo.mCanvas = null;
        } else {
            canvas = new Canvas(bitmap);
        }
        if ((backgroundColor & (-16777216)) != 0) {
            bitmap.eraseColor(backgroundColor);
        }
        computeScroll();
        int restoreCount = canvas.save();
        canvas.scale(scale, scale);
        canvas.translate(-this.mScrollX, -this.mScrollY);
        int flags = this.mPrivateFlags;
        this.mPrivateFlags &= -6291457;
        if ((this.mPrivateFlags & 128) == 128) {
            dispatchDraw(canvas);
            if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().draw(canvas);
            }
        } else {
            draw(canvas);
        }
        this.mPrivateFlags = flags;
        canvas.restoreToCount(restoreCount);
        canvas.setBitmap(null);
        if (attachInfo != null) {
            attachInfo.mCanvas = canvas;
        }
        return bitmap;
    }

    public boolean isInEditMode() {
        return false;
    }

    protected boolean isPaddingOffsetRequired() {
        return false;
    }

    protected int getLeftPaddingOffset() {
        return 0;
    }

    protected int getRightPaddingOffset() {
        return 0;
    }

    protected int getTopPaddingOffset() {
        return 0;
    }

    protected int getBottomPaddingOffset() {
        return 0;
    }

    protected int getFadeTop(boolean offsetRequired) {
        int top = this.mPaddingTop;
        if (offsetRequired) {
            top += getTopPaddingOffset();
        }
        return top;
    }

    protected int getFadeHeight(boolean offsetRequired) {
        int padding = this.mPaddingTop;
        if (offsetRequired) {
            padding += getTopPaddingOffset();
        }
        return ((this.mBottom - this.mTop) - this.mPaddingBottom) - padding;
    }

    public boolean isHardwareAccelerated() {
        return this.mAttachInfo != null && this.mAttachInfo.mHardwareAccelerated;
    }

    public void setClipBounds(Rect clipBounds) {
        if (clipBounds != null) {
            if (clipBounds.equals(this.mClipBounds)) {
                return;
            }
            if (this.mClipBounds == null) {
                invalidate();
                this.mClipBounds = new Rect(clipBounds);
                return;
            }
            invalidate(Math.min(this.mClipBounds.left, clipBounds.left), Math.min(this.mClipBounds.top, clipBounds.top), Math.max(this.mClipBounds.right, clipBounds.right), Math.max(this.mClipBounds.bottom, clipBounds.bottom));
            this.mClipBounds.set(clipBounds);
        } else if (this.mClipBounds != null) {
            invalidate();
            this.mClipBounds = null;
        }
    }

    public Rect getClipBounds() {
        if (this.mClipBounds != null) {
            return new Rect(this.mClipBounds);
        }
        return null;
    }

    private boolean drawAnimation(ViewGroup parent, long drawingTime, Animation a, boolean scalingRequired) {
        Transformation invalidationTransform;
        int flags = parent.mGroupFlags;
        boolean initialized = a.isInitialized();
        if (!initialized) {
            a.initialize(this.mRight - this.mLeft, this.mBottom - this.mTop, parent.getWidth(), parent.getHeight());
            a.initializeInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            if (this.mAttachInfo != null) {
                a.setListenerHandler(this.mAttachInfo.mHandler);
            }
            onAnimationStart();
        }
        Transformation t = parent.getChildTransformation();
        boolean more = a.getTransformation(drawingTime, t, 1.0f);
        if (scalingRequired && this.mAttachInfo.mApplicationScale != 1.0f) {
            if (parent.mInvalidationTransformation == null) {
                parent.mInvalidationTransformation = new Transformation();
            }
            invalidationTransform = parent.mInvalidationTransformation;
            a.getTransformation(drawingTime, invalidationTransform, 1.0f);
        } else {
            invalidationTransform = t;
        }
        if (more) {
            if (!a.willChangeBounds()) {
                if ((flags & 144) == 128) {
                    parent.mGroupFlags |= 4;
                } else if ((flags & 4) == 0) {
                    parent.mPrivateFlags |= 64;
                    parent.invalidate(this.mLeft, this.mTop, this.mRight, this.mBottom);
                }
            } else {
                if (parent.mInvalidateRegion == null) {
                    parent.mInvalidateRegion = new RectF();
                }
                RectF region = parent.mInvalidateRegion;
                a.getInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop, region, invalidationTransform);
                parent.mPrivateFlags |= 64;
                int left = this.mLeft + ((int) region.left);
                int top = this.mTop + ((int) region.top);
                parent.invalidate(left, top, left + ((int) (region.width() + 0.5f)), top + ((int) (region.height() + 0.5f)));
            }
        }
        return more;
    }

    void setDisplayListProperties(DisplayList displayList) {
        int transformType;
        if (displayList != null) {
            displayList.setLeftTopRightBottom(this.mLeft, this.mTop, this.mRight, this.mBottom);
            displayList.setHasOverlappingRendering(hasOverlappingRendering());
            if (this.mParent instanceof ViewGroup) {
                displayList.setClipToBounds((((ViewGroup) this.mParent).mGroupFlags & 1) != 0);
            }
            float alpha = 1.0f;
            if ((this.mParent instanceof ViewGroup) && (((ViewGroup) this.mParent).mGroupFlags & 2048) != 0) {
                ViewGroup parentVG = (ViewGroup) this.mParent;
                Transformation t = parentVG.getChildTransformation();
                if (parentVG.getChildStaticTransformation(this, t) && (transformType = t.getTransformationType()) != 0) {
                    if ((transformType & 1) != 0) {
                        alpha = t.getAlpha();
                    }
                    if ((transformType & 2) != 0) {
                        displayList.setMatrix(t.getMatrix());
                    }
                }
            }
            if (this.mTransformationInfo != null) {
                float alpha2 = alpha * getFinalAlpha();
                if (alpha2 < 1.0f) {
                    int multipliedAlpha = (int) (255.0f * alpha2);
                    if (onSetAlpha(multipliedAlpha)) {
                        alpha2 = 1.0f;
                    }
                }
                displayList.setTransformationInfo(alpha2, this.mTransformationInfo.mTranslationX, this.mTransformationInfo.mTranslationY, this.mTransformationInfo.mRotation, this.mTransformationInfo.mRotationX, this.mTransformationInfo.mRotationY, this.mTransformationInfo.mScaleX, this.mTransformationInfo.mScaleY);
                if (this.mTransformationInfo.mCamera == null) {
                    this.mTransformationInfo.mCamera = new Camera();
                    this.mTransformationInfo.matrix3D = new Matrix();
                }
                displayList.setCameraDistance(this.mTransformationInfo.mCamera.getLocationZ());
                if ((this.mPrivateFlags & 536870912) == 536870912) {
                    displayList.setPivotX(getPivotX());
                    displayList.setPivotY(getPivotY());
                }
            } else if (alpha < 1.0f) {
                displayList.setAlpha(alpha);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean draw(Canvas canvas, ViewGroup parent, long drawingTime) {
        boolean caching;
        Paint cachePaint;
        boolean useDisplayListProperties = this.mAttachInfo != null && this.mAttachInfo.mHardwareAccelerated;
        boolean more = false;
        boolean childHasIdentityMatrix = hasIdentityMatrix();
        int flags = parent.mGroupFlags;
        if ((flags & 256) == 256) {
            parent.getChildTransformation().clear();
            parent.mGroupFlags &= -257;
        }
        Transformation transformToApply = null;
        boolean concatMatrix = false;
        boolean scalingRequired = false;
        int layerType = getLayerType();
        boolean hardwareAccelerated = canvas.isHardwareAccelerated();
        if ((flags & 32768) != 0 || (flags & 16384) != 0) {
            caching = true;
            if (this.mAttachInfo != null) {
                scalingRequired = this.mAttachInfo.mScalingRequired;
            }
        } else {
            caching = layerType != 0 || hardwareAccelerated;
        }
        Animation a = getAnimation();
        if (a != null) {
            more = drawAnimation(parent, drawingTime, a, scalingRequired);
            concatMatrix = a.willChangeTransformationMatrix();
            if (concatMatrix) {
                this.mPrivateFlags3 |= 1;
            }
            transformToApply = parent.getChildTransformation();
        } else {
            if ((this.mPrivateFlags3 & 1) == 1 && this.mDisplayList != null) {
                this.mDisplayList.setAnimationMatrix(null);
                this.mPrivateFlags3 &= -2;
            }
            if (!useDisplayListProperties && (flags & 2048) != 0) {
                Transformation t = parent.getChildTransformation();
                boolean hasTransform = parent.getChildStaticTransformation(this, t);
                if (hasTransform) {
                    int transformType = t.getTransformationType();
                    transformToApply = transformType != 0 ? t : null;
                    concatMatrix = (transformType & 2) != 0;
                }
            }
        }
        boolean concatMatrix2 = concatMatrix | (!childHasIdentityMatrix);
        this.mPrivateFlags |= 32;
        if (!concatMatrix2 && (flags & 2049) == 1 && canvas.quickReject(this.mLeft, this.mTop, this.mRight, this.mBottom, Canvas.EdgeType.BW) && (this.mPrivateFlags & 64) == 0) {
            this.mPrivateFlags2 |= 268435456;
            return more;
        }
        this.mPrivateFlags2 &= -268435457;
        if (hardwareAccelerated) {
            this.mRecreateDisplayList = (this.mPrivateFlags & Integer.MIN_VALUE) == Integer.MIN_VALUE;
            this.mPrivateFlags &= Integer.MAX_VALUE;
        }
        DisplayList displayList = null;
        Bitmap cache = null;
        boolean hasDisplayList = false;
        if (caching) {
            if (!hardwareAccelerated) {
                if (layerType != 0) {
                    layerType = 1;
                    buildDrawingCache(true);
                }
                cache = getDrawingCache(true);
            } else {
                switch (layerType) {
                    case 0:
                        hasDisplayList = canHaveDisplayList();
                        break;
                    case 1:
                        if (useDisplayListProperties) {
                            hasDisplayList = canHaveDisplayList();
                            break;
                        } else {
                            buildDrawingCache(true);
                            cache = getDrawingCache(true);
                            break;
                        }
                    case 2:
                        if (useDisplayListProperties) {
                            hasDisplayList = canHaveDisplayList();
                            break;
                        }
                        break;
                }
            }
        }
        boolean useDisplayListProperties2 = useDisplayListProperties & hasDisplayList;
        if (useDisplayListProperties2) {
            displayList = getDisplayList();
            if (!displayList.isValid()) {
                displayList = null;
                hasDisplayList = false;
                useDisplayListProperties2 = false;
            }
        }
        int sx = 0;
        int sy = 0;
        if (!hasDisplayList) {
            computeScroll();
            sx = this.mScrollX;
            sy = this.mScrollY;
        }
        boolean hasNoCache = cache == null || hasDisplayList;
        boolean offsetForScroll = (cache != null || hasDisplayList || layerType == 2) ? false : true;
        int restoreTo = -1;
        if (!useDisplayListProperties2 || transformToApply != null) {
            restoreTo = canvas.save();
        }
        if (offsetForScroll) {
            canvas.translate(this.mLeft - sx, this.mTop - sy);
        } else {
            if (!useDisplayListProperties2) {
                canvas.translate(this.mLeft, this.mTop);
            }
            if (scalingRequired) {
                if (useDisplayListProperties2) {
                    restoreTo = canvas.save();
                }
                float scale = 1.0f / this.mAttachInfo.mApplicationScale;
                canvas.scale(scale, scale);
            }
        }
        float alpha = useDisplayListProperties2 ? 1.0f : getAlpha() * getTransitionAlpha();
        if (transformToApply != null || alpha < 1.0f || !hasIdentityMatrix() || (this.mPrivateFlags3 & 2) == 2) {
            if (transformToApply != null || !childHasIdentityMatrix) {
                int transX = 0;
                int transY = 0;
                if (offsetForScroll) {
                    transX = -sx;
                    transY = -sy;
                }
                if (transformToApply != null) {
                    if (concatMatrix2) {
                        if (useDisplayListProperties2) {
                            displayList.setAnimationMatrix(transformToApply.getMatrix());
                        } else {
                            canvas.translate(-transX, -transY);
                            canvas.concat(transformToApply.getMatrix());
                            canvas.translate(transX, transY);
                        }
                        parent.mGroupFlags |= 256;
                    }
                    float transformAlpha = transformToApply.getAlpha();
                    if (transformAlpha < 1.0f) {
                        alpha *= transformAlpha;
                        parent.mGroupFlags |= 256;
                    }
                }
                if (!childHasIdentityMatrix && !useDisplayListProperties2) {
                    canvas.translate(-transX, -transY);
                    canvas.concat(getMatrix());
                    canvas.translate(transX, transY);
                }
            }
            if (alpha < 1.0f || (this.mPrivateFlags3 & 2) == 2) {
                if (alpha < 1.0f) {
                    this.mPrivateFlags3 |= 2;
                } else {
                    this.mPrivateFlags3 &= -3;
                }
                parent.mGroupFlags |= 256;
                if (hasNoCache) {
                    int multipliedAlpha = (int) (255.0f * alpha);
                    if (!onSetAlpha(multipliedAlpha)) {
                        int layerFlags = 4;
                        if ((flags & 1) != 0 || layerType != 0) {
                            layerFlags = 4 | 16;
                        }
                        if (useDisplayListProperties2) {
                            displayList.setAlpha(alpha * getAlpha() * getTransitionAlpha());
                        } else if (layerType == 0) {
                            int scrollX = hasDisplayList ? 0 : sx;
                            int scrollY = hasDisplayList ? 0 : sy;
                            canvas.saveLayerAlpha(scrollX, scrollY, (scrollX + this.mRight) - this.mLeft, (scrollY + this.mBottom) - this.mTop, multipliedAlpha, layerFlags);
                        }
                    } else {
                        this.mPrivateFlags |= 262144;
                    }
                }
            }
        } else if ((this.mPrivateFlags & 262144) == 262144) {
            onSetAlpha(255);
            this.mPrivateFlags &= -262145;
        }
        if ((flags & 1) == 1 && !useDisplayListProperties2 && cache == null) {
            if (offsetForScroll) {
                canvas.clipRect(sx, sy, sx + (this.mRight - this.mLeft), sy + (this.mBottom - this.mTop));
            } else if (!scalingRequired || cache == null) {
                canvas.clipRect(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            } else {
                canvas.clipRect(0, 0, cache.getWidth(), cache.getHeight());
            }
        }
        if (!useDisplayListProperties2 && hasDisplayList) {
            displayList = getDisplayList();
            if (!displayList.isValid()) {
                displayList = null;
                hasDisplayList = false;
            }
        }
        if (hasNoCache) {
            boolean layerRendered = false;
            if (layerType == 2 && !useDisplayListProperties2) {
                HardwareLayer layer = getHardwareLayer();
                if (layer != null && layer.isValid()) {
                    this.mLayerPaint.setAlpha((int) (alpha * 255.0f));
                    ((HardwareCanvas) canvas).drawHardwareLayer(layer, 0.0f, 0.0f, this.mLayerPaint);
                    layerRendered = true;
                } else {
                    int scrollX2 = hasDisplayList ? 0 : sx;
                    int scrollY2 = hasDisplayList ? 0 : sy;
                    canvas.saveLayer(scrollX2, scrollY2, (scrollX2 + this.mRight) - this.mLeft, (scrollY2 + this.mBottom) - this.mTop, this.mLayerPaint, 20);
                }
            }
            if (!layerRendered) {
                if (!hasDisplayList) {
                    if ((this.mPrivateFlags & 128) == 128) {
                        this.mPrivateFlags &= -6291457;
                        dispatchDraw(canvas);
                    } else {
                        draw(canvas);
                    }
                } else {
                    this.mPrivateFlags &= -6291457;
                    ((HardwareCanvas) canvas).drawDisplayList(displayList, null, flags);
                }
            }
        } else if (cache != null) {
            this.mPrivateFlags &= -6291457;
            if (layerType == 0) {
                cachePaint = parent.mCachePaint;
                if (cachePaint == null) {
                    cachePaint = new Paint();
                    cachePaint.setDither(false);
                    parent.mCachePaint = cachePaint;
                }
                if (alpha < 1.0f) {
                    cachePaint.setAlpha((int) (alpha * 255.0f));
                    parent.mGroupFlags |= 4096;
                } else if ((flags & 4096) != 0) {
                    cachePaint.setAlpha(255);
                    parent.mGroupFlags &= -4097;
                }
            } else {
                cachePaint = this.mLayerPaint;
                cachePaint.setAlpha((int) (alpha * 255.0f));
            }
            canvas.drawBitmap(cache, 0.0f, 0.0f, cachePaint);
        }
        if (restoreTo >= 0) {
            canvas.restoreToCount(restoreTo);
        }
        if (a != null && !more) {
            if (!hardwareAccelerated && !a.getFillAfter()) {
                onSetAlpha(255);
            }
            parent.finishAnimatingView(this, a);
        }
        if (more && hardwareAccelerated && a.hasAlpha() && (this.mPrivateFlags & 262144) == 262144) {
            invalidate(true);
        }
        this.mRecreateDisplayList = false;
        return more;
    }

    public void draw(Canvas canvas) {
        Drawable background;
        if (this.mClipBounds != null) {
            canvas.clipRect(this.mClipBounds);
        }
        int privateFlags = this.mPrivateFlags;
        boolean dirtyOpaque = (privateFlags & 6291456) == 4194304 && (this.mAttachInfo == null || !this.mAttachInfo.mIgnoreDirtyState);
        this.mPrivateFlags = (privateFlags & (-6291457)) | 32;
        if (!dirtyOpaque && (background = this.mBackground) != null) {
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            if (this.mBackgroundSizeChanged) {
                background.setBounds(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
                this.mBackgroundSizeChanged = false;
            }
            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
        int viewFlags = this.mViewFlags;
        boolean horizontalEdges = (viewFlags & 4096) != 0;
        boolean verticalEdges = (viewFlags & 8192) != 0;
        if (!verticalEdges && !horizontalEdges) {
            if (!dirtyOpaque) {
                onDraw(canvas);
            }
            dispatchDraw(canvas);
            onDrawScrollBars(canvas);
            if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().dispatchDraw(canvas);
                return;
            }
            return;
        }
        boolean drawTop = false;
        boolean drawBottom = false;
        boolean drawLeft = false;
        boolean drawRight = false;
        float topFadeStrength = 0.0f;
        float bottomFadeStrength = 0.0f;
        float leftFadeStrength = 0.0f;
        float rightFadeStrength = 0.0f;
        int paddingLeft = this.mPaddingLeft;
        boolean offsetRequired = isPaddingOffsetRequired();
        if (offsetRequired) {
            paddingLeft += getLeftPaddingOffset();
        }
        int left = this.mScrollX + paddingLeft;
        int right = (((left + this.mRight) - this.mLeft) - this.mPaddingRight) - paddingLeft;
        int top = this.mScrollY + getFadeTop(offsetRequired);
        int bottom = top + getFadeHeight(offsetRequired);
        if (offsetRequired) {
            right += getRightPaddingOffset();
            bottom += getBottomPaddingOffset();
        }
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        float fadeHeight = scrollabilityCache.fadingEdgeLength;
        int length = (int) fadeHeight;
        if (verticalEdges && top + length > bottom - length) {
            length = (bottom - top) / 2;
        }
        if (horizontalEdges && left + length > right - length) {
            length = (right - left) / 2;
        }
        if (verticalEdges) {
            topFadeStrength = Math.max(0.0f, Math.min(1.0f, getTopFadingEdgeStrength()));
            drawTop = topFadeStrength * fadeHeight > 1.0f;
            bottomFadeStrength = Math.max(0.0f, Math.min(1.0f, getBottomFadingEdgeStrength()));
            drawBottom = bottomFadeStrength * fadeHeight > 1.0f;
        }
        if (horizontalEdges) {
            leftFadeStrength = Math.max(0.0f, Math.min(1.0f, getLeftFadingEdgeStrength()));
            drawLeft = leftFadeStrength * fadeHeight > 1.0f;
            rightFadeStrength = Math.max(0.0f, Math.min(1.0f, getRightFadingEdgeStrength()));
            drawRight = rightFadeStrength * fadeHeight > 1.0f;
        }
        int saveCount = canvas.getSaveCount();
        int solidColor = getSolidColor();
        if (solidColor == 0) {
            if (drawTop) {
                canvas.saveLayer(left, top, right, top + length, null, 4);
            }
            if (drawBottom) {
                canvas.saveLayer(left, bottom - length, right, bottom, null, 4);
            }
            if (drawLeft) {
                canvas.saveLayer(left, top, left + length, bottom, null, 4);
            }
            if (drawRight) {
                canvas.saveLayer(right - length, top, right, bottom, null, 4);
            }
        } else {
            scrollabilityCache.setFadeColor(solidColor);
        }
        if (!dirtyOpaque) {
            onDraw(canvas);
        }
        dispatchDraw(canvas);
        Paint p = scrollabilityCache.paint;
        Matrix matrix = scrollabilityCache.matrix;
        Shader fade = scrollabilityCache.shader;
        if (drawTop) {
            matrix.setScale(1.0f, fadeHeight * topFadeStrength);
            matrix.postTranslate(left, top);
            fade.setLocalMatrix(matrix);
            canvas.drawRect(left, top, right, top + length, p);
        }
        if (drawBottom) {
            matrix.setScale(1.0f, fadeHeight * bottomFadeStrength);
            matrix.postRotate(180.0f);
            matrix.postTranslate(left, bottom);
            fade.setLocalMatrix(matrix);
            canvas.drawRect(left, bottom - length, right, bottom, p);
        }
        if (drawLeft) {
            matrix.setScale(1.0f, fadeHeight * leftFadeStrength);
            matrix.postRotate(-90.0f);
            matrix.postTranslate(left, top);
            fade.setLocalMatrix(matrix);
            canvas.drawRect(left, top, left + length, bottom, p);
        }
        if (drawRight) {
            matrix.setScale(1.0f, fadeHeight * rightFadeStrength);
            matrix.postRotate(90.0f);
            matrix.postTranslate(right, top);
            fade.setLocalMatrix(matrix);
            canvas.drawRect(right - length, top, right, bottom, p);
        }
        canvas.restoreToCount(saveCount);
        onDrawScrollBars(canvas);
        if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
            this.mOverlay.getOverlayView().dispatchDraw(canvas);
        }
    }

    public ViewOverlay getOverlay() {
        if (this.mOverlay == null) {
            this.mOverlay = new ViewOverlay(this.mContext, this);
        }
        return this.mOverlay;
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public int getSolidColor() {
        return 0;
    }

    private static String printFlags(int flags) {
        String output = "";
        int numFlags = 0;
        if ((flags & 1) == 1) {
            output = output + "TAKES_FOCUS";
            numFlags = 0 + 1;
        }
        switch (flags & 12) {
            case 4:
                if (numFlags > 0) {
                    output = output + Separators.SP;
                }
                output = output + "INVISIBLE";
                break;
            case 8:
                if (numFlags > 0) {
                    output = output + Separators.SP;
                }
                output = output + "GONE";
                break;
        }
        return output;
    }

    private static String printPrivateFlags(int privateFlags) {
        String output = "";
        int numFlags = 0;
        if ((privateFlags & 1) == 1) {
            output = output + "WANTS_FOCUS";
            numFlags = 0 + 1;
        }
        if ((privateFlags & 2) == 2) {
            if (numFlags > 0) {
                output = output + Separators.SP;
            }
            output = output + "FOCUSED";
            numFlags++;
        }
        if ((privateFlags & 4) == 4) {
            if (numFlags > 0) {
                output = output + Separators.SP;
            }
            output = output + "SELECTED";
            numFlags++;
        }
        if ((privateFlags & 8) == 8) {
            if (numFlags > 0) {
                output = output + Separators.SP;
            }
            output = output + "IS_ROOT_NAMESPACE";
            numFlags++;
        }
        if ((privateFlags & 16) == 16) {
            if (numFlags > 0) {
                output = output + Separators.SP;
            }
            output = output + "HAS_BOUNDS";
            numFlags++;
        }
        if ((privateFlags & 32) == 32) {
            if (numFlags > 0) {
                output = output + Separators.SP;
            }
            output = output + "DRAWN";
        }
        return output;
    }

    public boolean isLayoutRequested() {
        return (this.mPrivateFlags & 4096) == 4096;
    }

    public static boolean isLayoutModeOptical(Object o) {
        return (o instanceof ViewGroup) && ((ViewGroup) o).isLayoutModeOptical();
    }

    private boolean setOpticalFrame(int left, int top, int right, int bottom) {
        Insets parentInsets = this.mParent instanceof View ? ((View) this.mParent).getOpticalInsets() : Insets.NONE;
        Insets childInsets = getOpticalInsets();
        return setFrame((left + parentInsets.left) - childInsets.left, (top + parentInsets.top) - childInsets.top, right + parentInsets.left + childInsets.right, bottom + parentInsets.top + childInsets.bottom);
    }

    public void layout(int l, int t, int r, int b) {
        if ((this.mPrivateFlags3 & 8) != 0) {
            onMeasure(this.mOldWidthMeasureSpec, this.mOldHeightMeasureSpec);
            this.mPrivateFlags3 &= -9;
        }
        int oldL = this.mLeft;
        int oldT = this.mTop;
        int oldB = this.mBottom;
        int oldR = this.mRight;
        boolean changed = isLayoutModeOptical(this.mParent) ? setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);
        if (changed || (this.mPrivateFlags & 8192) == 8192) {
            onLayout(changed, l, t, r, b);
            this.mPrivateFlags &= -8193;
            ListenerInfo li = this.mListenerInfo;
            if (li != null && li.mOnLayoutChangeListeners != null) {
                ArrayList<OnLayoutChangeListener> listenersCopy = (ArrayList) li.mOnLayoutChangeListeners.clone();
                int numListeners = listenersCopy.size();
                for (int i = 0; i < numListeners; i++) {
                    listenersCopy.get(i).onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
                }
            }
        }
        this.mPrivateFlags &= -4097;
        this.mPrivateFlags3 |= 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = false;
        if (this.mLeft != left || this.mRight != right || this.mTop != top || this.mBottom != bottom) {
            changed = true;
            int drawn = this.mPrivateFlags & 32;
            int oldWidth = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            int newWidth = right - left;
            int newHeight = bottom - top;
            boolean sizeChanged = (newWidth == oldWidth && newHeight == oldHeight) ? false : true;
            invalidate(sizeChanged);
            this.mLeft = left;
            this.mTop = top;
            this.mRight = right;
            this.mBottom = bottom;
            if (this.mDisplayList != null) {
                this.mDisplayList.setLeftTopRightBottom(this.mLeft, this.mTop, this.mRight, this.mBottom);
            }
            this.mPrivateFlags |= 16;
            if (sizeChanged) {
                if ((this.mPrivateFlags & 536870912) == 0 && this.mTransformationInfo != null) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                sizeChange(newWidth, newHeight, oldWidth, oldHeight);
            }
            if ((this.mViewFlags & 12) == 0) {
                this.mPrivateFlags |= 32;
                invalidate(sizeChanged);
                invalidateParentCaches();
            }
            this.mPrivateFlags |= drawn;
            this.mBackgroundSizeChanged = true;
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
        return changed;
    }

    private void sizeChange(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        if (this.mOverlay != null) {
            this.mOverlay.getOverlayView().setRight(newWidth);
            this.mOverlay.getOverlayView().setBottom(newHeight);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFinishInflate() {
    }

    public Resources getResources() {
        return this.mResources;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (verifyDrawable(drawable)) {
            Rect dirty = drawable.getBounds();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            invalidate(dirty.left + scrollX, dirty.top + scrollY, dirty.right + scrollX, dirty.bottom + scrollY);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (verifyDrawable(who) && what != null) {
            long delay = when - SystemClock.uptimeMillis();
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, what, who, Choreographer.subtractFrameDelay(delay));
            } else {
                ViewRootImpl.getRunQueue().postDelayed(what, delay);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (verifyDrawable(who) && what != null) {
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, what, who);
            } else {
                ViewRootImpl.getRunQueue().removeCallbacks(what);
            }
        }
    }

    public void unscheduleDrawable(Drawable who) {
        if (this.mAttachInfo != null && who != null) {
            this.mAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, null, who);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resolveDrawables() {
        if (!isLayoutDirectionResolved() && getRawLayoutDirection() == 2) {
            return;
        }
        int layoutDirection = isLayoutDirectionResolved() ? getLayoutDirection() : getRawLayoutDirection();
        if (this.mBackground != null) {
            this.mBackground.setLayoutDirection(layoutDirection);
        }
        this.mPrivateFlags2 |= 1073741824;
        onResolveDrawables(layoutDirection);
    }

    public void onResolveDrawables(int layoutDirection) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetResolvedDrawables() {
        this.mPrivateFlags2 &= -1073741825;
    }

    private boolean isDrawablesResolved() {
        return (this.mPrivateFlags2 & 1073741824) == 1073741824;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return who == this.mBackground;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawableStateChanged() {
        Drawable d = this.mBackground;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }

    public void refreshDrawableState() {
        this.mPrivateFlags |= 1024;
        drawableStateChanged();
        ViewParent parent = this.mParent;
        if (parent != null) {
            parent.childDrawableStateChanged(this);
        }
    }

    public final int[] getDrawableState() {
        if (this.mDrawableState != null && (this.mPrivateFlags & 1024) == 0) {
            return this.mDrawableState;
        }
        this.mDrawableState = onCreateDrawableState(0);
        this.mPrivateFlags &= -1025;
        return this.mDrawableState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int[] onCreateDrawableState(int extraSpace) {
        int[] fullState;
        if ((this.mViewFlags & 4194304) == 4194304 && (this.mParent instanceof View)) {
            return ((View) this.mParent).onCreateDrawableState(extraSpace);
        }
        int privateFlags = this.mPrivateFlags;
        int viewStateIndex = 0;
        if ((privateFlags & 16384) != 0) {
            viewStateIndex = 0 | 16;
        }
        if ((this.mViewFlags & 32) == 0) {
            viewStateIndex |= 8;
        }
        if (isFocused()) {
            viewStateIndex |= 4;
        }
        if ((privateFlags & 4) != 0) {
            viewStateIndex |= 2;
        }
        if (hasWindowFocus()) {
            viewStateIndex |= 1;
        }
        if ((privateFlags & 1073741824) != 0) {
            viewStateIndex |= 32;
        }
        if (this.mAttachInfo != null && this.mAttachInfo.mHardwareAccelerationRequested && HardwareRenderer.isAvailable()) {
            viewStateIndex |= 64;
        }
        if ((privateFlags & 268435456) != 0) {
            viewStateIndex |= 128;
        }
        int privateFlags2 = this.mPrivateFlags2;
        if ((privateFlags2 & 1) != 0) {
            viewStateIndex |= 256;
        }
        if ((privateFlags2 & 2) != 0) {
            viewStateIndex |= 512;
        }
        int[] drawableState = VIEW_STATE_SETS[viewStateIndex];
        if (extraSpace == 0) {
            return drawableState;
        }
        if (drawableState != null) {
            fullState = new int[drawableState.length + extraSpace];
            System.arraycopy(drawableState, 0, fullState, 0, drawableState.length);
        } else {
            fullState = new int[extraSpace];
        }
        return fullState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int[] mergeDrawableStates(int[] baseState, int[] additionalState) {
        int N = baseState.length;
        int i = N - 1;
        while (i >= 0 && baseState[i] == 0) {
            i--;
        }
        System.arraycopy(additionalState, 0, baseState, i + 1, additionalState.length);
        return baseState;
    }

    public void jumpDrawablesToCurrentState() {
        if (this.mBackground != null) {
            this.mBackground.jumpToCurrentState();
        }
    }

    @RemotableViewMethod
    public void setBackgroundColor(int color) {
        if (this.mBackground instanceof ColorDrawable) {
            ((ColorDrawable) this.mBackground.mutate()).setColor(color);
            computeOpaqueFlags();
            this.mBackgroundResource = 0;
            return;
        }
        setBackground(new ColorDrawable(color));
    }

    @RemotableViewMethod
    public void setBackgroundResource(int resid) {
        if (resid != 0 && resid == this.mBackgroundResource) {
            return;
        }
        Drawable d = null;
        if (resid != 0) {
            d = this.mResources.getDrawable(resid);
        }
        setBackground(d);
        this.mBackgroundResource = resid;
    }

    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        computeOpaqueFlags();
        if (background == this.mBackground) {
            return;
        }
        boolean requestLayout = false;
        this.mBackgroundResource = 0;
        if (this.mBackground != null) {
            this.mBackground.setCallback(null);
            unscheduleDrawable(this.mBackground);
        }
        if (background != null) {
            Rect padding = sThreadLocal.get();
            if (padding == null) {
                padding = new Rect();
                sThreadLocal.set(padding);
            }
            resetResolvedDrawables();
            background.setLayoutDirection(getLayoutDirection());
            if (background.getPadding(padding)) {
                resetResolvedPadding();
                switch (background.getLayoutDirection()) {
                    case 0:
                    default:
                        this.mUserPaddingLeftInitial = padding.left;
                        this.mUserPaddingRightInitial = padding.right;
                        internalSetPadding(padding.left, padding.top, padding.right, padding.bottom);
                        break;
                    case 1:
                        this.mUserPaddingLeftInitial = padding.right;
                        this.mUserPaddingRightInitial = padding.left;
                        internalSetPadding(padding.right, padding.top, padding.left, padding.bottom);
                        break;
                }
            }
            if (this.mBackground == null || this.mBackground.getMinimumHeight() != background.getMinimumHeight() || this.mBackground.getMinimumWidth() != background.getMinimumWidth()) {
                requestLayout = true;
            }
            background.setCallback(this);
            if (background.isStateful()) {
                background.setState(getDrawableState());
            }
            background.setVisible(getVisibility() == 0, false);
            this.mBackground = background;
            if ((this.mPrivateFlags & 128) != 0) {
                this.mPrivateFlags &= -129;
                this.mPrivateFlags |= 256;
                requestLayout = true;
            }
        } else {
            this.mBackground = null;
            if ((this.mPrivateFlags & 256) != 0) {
                this.mPrivateFlags &= -257;
                this.mPrivateFlags |= 128;
            }
            requestLayout = true;
        }
        computeOpaqueFlags();
        if (requestLayout) {
            requestLayout();
        }
        this.mBackgroundSizeChanged = true;
        invalidate(true);
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        resetResolvedPadding();
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        this.mUserPaddingLeftInitial = left;
        this.mUserPaddingRightInitial = right;
        internalSetPadding(left, top, right, bottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void internalSetPadding(int left, int top, int right, int bottom) {
        this.mUserPaddingLeft = left;
        this.mUserPaddingRight = right;
        this.mUserPaddingBottom = bottom;
        int viewFlags = this.mViewFlags;
        boolean changed = false;
        if ((viewFlags & 768) != 0) {
            if ((viewFlags & 512) != 0) {
                int offset = (viewFlags & 16777216) == 0 ? 0 : getVerticalScrollbarWidth();
                switch (this.mVerticalScrollbarPosition) {
                    case 0:
                        if (isLayoutRtl()) {
                            left += offset;
                            break;
                        } else {
                            right += offset;
                            break;
                        }
                    case 1:
                        left += offset;
                        break;
                    case 2:
                        right += offset;
                        break;
                }
            }
            if ((viewFlags & 256) != 0) {
                bottom += (viewFlags & 16777216) == 0 ? 0 : getHorizontalScrollbarHeight();
            }
        }
        if (this.mPaddingLeft != left) {
            changed = true;
            this.mPaddingLeft = left;
        }
        if (this.mPaddingTop != top) {
            changed = true;
            this.mPaddingTop = top;
        }
        if (this.mPaddingRight != right) {
            changed = true;
            this.mPaddingRight = right;
        }
        if (this.mPaddingBottom != bottom) {
            changed = true;
            this.mPaddingBottom = bottom;
        }
        if (changed) {
            requestLayout();
        }
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        resetResolvedPadding();
        this.mUserPaddingStart = start;
        this.mUserPaddingEnd = end;
        switch (getLayoutDirection()) {
            case 0:
            default:
                this.mUserPaddingLeftInitial = start;
                this.mUserPaddingRightInitial = end;
                internalSetPadding(start, top, end, bottom);
                return;
            case 1:
                this.mUserPaddingLeftInitial = end;
                this.mUserPaddingRightInitial = start;
                internalSetPadding(end, top, start, bottom);
                return;
        }
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public int getPaddingLeft() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingLeft;
    }

    public int getPaddingStart() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == 1 ? this.mPaddingRight : this.mPaddingLeft;
    }

    public int getPaddingRight() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingRight;
    }

    public int getPaddingEnd() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == 1 ? this.mPaddingLeft : this.mPaddingRight;
    }

    public boolean isPaddingRelative() {
        return (this.mUserPaddingStart == Integer.MIN_VALUE && this.mUserPaddingEnd == Integer.MIN_VALUE) ? false : true;
    }

    Insets computeOpticalInsets() {
        return this.mBackground == null ? Insets.NONE : this.mBackground.getOpticalInsets();
    }

    public void resetPaddingToInitialValues() {
        if (isRtlCompatibilityMode()) {
            this.mPaddingLeft = this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingRightInitial;
        } else if (isLayoutRtl()) {
            this.mPaddingLeft = this.mUserPaddingEnd >= 0 ? this.mUserPaddingEnd : this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingStart >= 0 ? this.mUserPaddingStart : this.mUserPaddingRightInitial;
        } else {
            this.mPaddingLeft = this.mUserPaddingStart >= 0 ? this.mUserPaddingStart : this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingEnd >= 0 ? this.mUserPaddingEnd : this.mUserPaddingRightInitial;
        }
    }

    public Insets getOpticalInsets() {
        if (this.mLayoutInsets == null) {
            this.mLayoutInsets = computeOpticalInsets();
        }
        return this.mLayoutInsets;
    }

    public void setSelected(boolean selected) {
        if (((this.mPrivateFlags & 4) != 0) != selected) {
            this.mPrivateFlags = (this.mPrivateFlags & (-5)) | (selected ? 4 : 0);
            if (!selected) {
                resetPressedState();
            }
            invalidate(true);
            refreshDrawableState();
            dispatchSetSelected(selected);
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    protected void dispatchSetSelected(boolean selected) {
    }

    @ViewDebug.ExportedProperty
    public boolean isSelected() {
        return (this.mPrivateFlags & 4) != 0;
    }

    public void setActivated(boolean activated) {
        if (((this.mPrivateFlags & 1073741824) != 0) != activated) {
            this.mPrivateFlags = (this.mPrivateFlags & (-1073741825)) | (activated ? 1073741824 : 0);
            invalidate(true);
            refreshDrawableState();
            dispatchSetActivated(activated);
        }
    }

    protected void dispatchSetActivated(boolean activated) {
    }

    @ViewDebug.ExportedProperty
    public boolean isActivated() {
        return (this.mPrivateFlags & 1073741824) != 0;
    }

    public ViewTreeObserver getViewTreeObserver() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mTreeObserver;
        }
        if (this.mFloatingTreeObserver == null) {
            this.mFloatingTreeObserver = new ViewTreeObserver();
        }
        return this.mFloatingTreeObserver;
    }

    public View getRootView() {
        View parent;
        View v;
        if (this.mAttachInfo != null && (v = this.mAttachInfo.mRootView) != null) {
            return v;
        }
        View view = this;
        while (true) {
            parent = view;
            if (parent.mParent == null || !(parent.mParent instanceof View)) {
                break;
            }
            view = (View) parent.mParent;
        }
        return parent;
    }

    public boolean toGlobalMotionEvent(MotionEvent ev) {
        AttachInfo info = this.mAttachInfo;
        if (info == null) {
            return false;
        }
        transformMotionEventToGlobal(ev);
        ev.offsetLocation(info.mWindowLeft, info.mWindowTop);
        return true;
    }

    public boolean toLocalMotionEvent(MotionEvent ev) {
        AttachInfo info = this.mAttachInfo;
        if (info == null) {
            return false;
        }
        ev.offsetLocation(-info.mWindowLeft, -info.mWindowTop);
        transformMotionEventToLocal(ev);
        return true;
    }

    private void transformMotionEventToLocal(MotionEvent ev) {
        ViewParent parent = this.mParent;
        if (parent instanceof View) {
            View vp = (View) parent;
            vp.transformMotionEventToLocal(ev);
            ev.offsetLocation(vp.mScrollX, vp.mScrollY);
        } else if (parent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) parent;
            ev.offsetLocation(0.0f, vr.mCurScrollY);
        }
        ev.offsetLocation(-this.mLeft, -this.mTop);
        if (!hasIdentityMatrix()) {
            ev.transform(getInverseMatrix());
        }
    }

    private void transformMotionEventToGlobal(MotionEvent ev) {
        if (!hasIdentityMatrix()) {
            ev.transform(getMatrix());
        }
        ev.offsetLocation(this.mLeft, this.mTop);
        ViewParent parent = this.mParent;
        if (parent instanceof View) {
            View vp = (View) parent;
            ev.offsetLocation(-vp.mScrollX, -vp.mScrollY);
            vp.transformMotionEventToGlobal(ev);
        } else if (parent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) parent;
            ev.offsetLocation(0.0f, -vr.mCurScrollY);
        }
    }

    public void getLocationOnScreen(int[] location) {
        getLocationInWindow(location);
        AttachInfo info = this.mAttachInfo;
        if (info != null) {
            location[0] = location[0] + info.mWindowLeft;
            location[1] = location[1] + info.mWindowTop;
        }
    }

    public void getLocationInWindow(int[] location) {
        ViewParent viewParent;
        if (location == null || location.length < 2) {
            throw new IllegalArgumentException("location must be an array of two integers");
        }
        if (this.mAttachInfo == null) {
            location[1] = 0;
            location[0] = 0;
            return;
        }
        float[] position = this.mAttachInfo.mTmpTransformLocation;
        position[1] = 0.0f;
        position[0] = 0.0f;
        if (!hasIdentityMatrix()) {
            getMatrix().mapPoints(position);
        }
        position[0] = position[0] + this.mLeft;
        position[1] = position[1] + this.mTop;
        ViewParent viewParent2 = this.mParent;
        while (true) {
            viewParent = viewParent2;
            if (!(viewParent instanceof View)) {
                break;
            }
            View view = (View) viewParent;
            position[0] = position[0] - view.mScrollX;
            position[1] = position[1] - view.mScrollY;
            if (!view.hasIdentityMatrix()) {
                view.getMatrix().mapPoints(position);
            }
            position[0] = position[0] + view.mLeft;
            position[1] = position[1] + view.mTop;
            viewParent2 = view.mParent;
        }
        if (viewParent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) viewParent;
            position[1] = position[1] - vr.mCurScrollY;
        }
        location[0] = (int) (position[0] + 0.5f);
        location[1] = (int) (position[1] + 0.5f);
    }

    protected View findViewTraversal(int id) {
        if (id == this.mID) {
            return this;
        }
        return null;
    }

    protected View findViewWithTagTraversal(Object tag) {
        if (tag != null && tag.equals(this.mTag)) {
            return this;
        }
        return null;
    }

    protected View findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
        if (predicate.apply(this)) {
            return this;
        }
        return null;
    }

    public final View findViewById(int id) {
        if (id < 0) {
            return null;
        }
        return findViewTraversal(id);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final View findViewByAccessibilityId(int accessibilityId) {
        if (accessibilityId < 0) {
            return null;
        }
        return findViewByAccessibilityIdTraversal(accessibilityId);
    }

    public View findViewByAccessibilityIdTraversal(int accessibilityId) {
        if (getAccessibilityViewId() == accessibilityId) {
            return this;
        }
        return null;
    }

    public final View findViewWithTag(Object tag) {
        if (tag == null) {
            return null;
        }
        return findViewWithTagTraversal(tag);
    }

    public final View findViewByPredicate(Predicate<View> predicate) {
        return findViewByPredicateTraversal(predicate, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0016, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.view.View findViewByPredicateInsideOut(android.view.View r5, com.android.internal.util.Predicate<android.view.View> r6) {
        /*
            r4 = this;
            r0 = 0
            r7 = r0
        L2:
            r0 = r5
            r1 = r6
            r2 = r7
            android.view.View r0 = r0.findViewByPredicateTraversal(r1, r2)
            r8 = r0
            r0 = r8
            if (r0 != 0) goto L14
            r0 = r5
            r1 = r4
            if (r0 != r1) goto L17
        L14:
            r0 = r8
            return r0
        L17:
            r0 = r5
            android.view.ViewParent r0 = r0.getParent()
            r9 = r0
            r0 = r9
            if (r0 == 0) goto L2a
            r0 = r9
            boolean r0 = r0 instanceof android.view.View
            if (r0 != 0) goto L2c
        L2a:
            r0 = 0
            return r0
        L2c:
            r0 = r5
            r7 = r0
            r0 = r9
            android.view.View r0 = (android.view.View) r0
            r5 = r0
            goto L2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.findViewByPredicateInsideOut(android.view.View, com.android.internal.util.Predicate):android.view.View");
    }

    public void setId(int id) {
        this.mID = id;
        if (this.mID == -1 && this.mLabelForId != -1) {
            this.mID = generateViewId();
        }
    }

    public void setIsRootNamespace(boolean isRoot) {
        if (isRoot) {
            this.mPrivateFlags |= 8;
        } else {
            this.mPrivateFlags &= -9;
        }
    }

    public boolean isRootNamespace() {
        return (this.mPrivateFlags & 8) != 0;
    }

    @ViewDebug.CapturedViewProperty
    public int getId() {
        return this.mID;
    }

    @ViewDebug.ExportedProperty
    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag(int key) {
        if (this.mKeyedTags != null) {
            return this.mKeyedTags.get(key);
        }
        return null;
    }

    public void setTag(int key, Object tag) {
        if ((key >>> 24) < 2) {
            throw new IllegalArgumentException("The key must be an application-specific resource id.");
        }
        setKeyedTag(key, tag);
    }

    public void setTagInternal(int key, Object tag) {
        if ((key >>> 24) != 1) {
            throw new IllegalArgumentException("The key must be a framework-specific resource id.");
        }
        setKeyedTag(key, tag);
    }

    private void setKeyedTag(int key, Object tag) {
        if (this.mKeyedTags == null) {
            this.mKeyedTags = new SparseArray<>(2);
        }
        this.mKeyedTags.put(key, tag);
    }

    public void debug() {
        debug(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void debug(int depth) {
        String output;
        String output2 = debugIndent(depth - 1) + "+ " + this;
        int id = getId();
        if (id != -1) {
            output2 = output2 + " (id=" + id + Separators.RPAREN;
        }
        Object tag = getTag();
        if (tag != null) {
            output2 = output2 + " (tag=" + tag + Separators.RPAREN;
        }
        Log.d(VIEW_LOG_TAG, output2);
        if ((this.mPrivateFlags & 2) != 0) {
            Log.d(VIEW_LOG_TAG, debugIndent(depth) + " FOCUSED");
        }
        Log.d(VIEW_LOG_TAG, debugIndent(depth) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ");
        if (this.mPaddingLeft != 0 || this.mPaddingTop != 0 || this.mPaddingRight != 0 || this.mPaddingBottom != 0) {
            Log.d(VIEW_LOG_TAG, debugIndent(depth) + "padding={" + this.mPaddingLeft + ", " + this.mPaddingTop + ", " + this.mPaddingRight + ", " + this.mPaddingBottom + "}");
        }
        Log.d(VIEW_LOG_TAG, debugIndent(depth) + "mMeasureWidth=" + this.mMeasuredWidth + " mMeasureHeight=" + this.mMeasuredHeight);
        String output3 = debugIndent(depth);
        if (this.mLayoutParams == null) {
            output = output3 + "BAD! no layout params";
        } else {
            output = this.mLayoutParams.debug(output3);
        }
        Log.d(VIEW_LOG_TAG, output);
        Log.d(VIEW_LOG_TAG, ((debugIndent(depth) + "flags={") + printFlags(this.mViewFlags)) + "}");
        Log.d(VIEW_LOG_TAG, ((debugIndent(depth) + "privateFlags={") + printPrivateFlags(this.mPrivateFlags)) + "}");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String debugIndent(int depth) {
        StringBuilder spaces = new StringBuilder(((depth * 2) + 3) * 2);
        for (int i = 0; i < (depth * 2) + 3; i++) {
            spaces.append(' ').append(' ');
        }
        return spaces.toString();
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public int getBaseline() {
        return -1;
    }

    public boolean isInLayout() {
        ViewRootImpl viewRoot = getViewRootImpl();
        return viewRoot != null && viewRoot.isInLayout();
    }

    public void requestLayout() {
        if (this.mMeasureCache != null) {
            this.mMeasureCache.clear();
        }
        if (this.mAttachInfo != null && this.mAttachInfo.mViewRequestingLayout == null) {
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot != null && viewRoot.isInLayout() && !viewRoot.requestLayoutDuringLayout(this)) {
                return;
            }
            this.mAttachInfo.mViewRequestingLayout = this;
        }
        this.mPrivateFlags |= 4096;
        this.mPrivateFlags |= Integer.MIN_VALUE;
        if (this.mParent != null && !this.mParent.isLayoutRequested()) {
            this.mParent.requestLayout();
        }
        if (this.mAttachInfo != null && this.mAttachInfo.mViewRequestingLayout == this) {
            this.mAttachInfo.mViewRequestingLayout = null;
        }
    }

    public void forceLayout() {
        if (this.mMeasureCache != null) {
            this.mMeasureCache.clear();
        }
        this.mPrivateFlags |= 4096;
        this.mPrivateFlags |= Integer.MIN_VALUE;
    }

    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean optical = isLayoutModeOptical(this);
        if (optical != isLayoutModeOptical(this.mParent)) {
            Insets insets = getOpticalInsets();
            int oWidth = insets.left + insets.right;
            int oHeight = insets.top + insets.bottom;
            widthMeasureSpec = MeasureSpec.adjust(widthMeasureSpec, optical ? -oWidth : oWidth);
            heightMeasureSpec = MeasureSpec.adjust(heightMeasureSpec, optical ? -oHeight : oHeight);
        }
        long key = (widthMeasureSpec << 32) | (heightMeasureSpec & ExpandableListView.PACKED_POSITION_VALUE_NULL);
        if (this.mMeasureCache == null) {
            this.mMeasureCache = new LongSparseLongArray(2);
        }
        if ((this.mPrivateFlags & 4096) == 4096 || widthMeasureSpec != this.mOldWidthMeasureSpec || heightMeasureSpec != this.mOldHeightMeasureSpec) {
            this.mPrivateFlags &= -2049;
            resolveRtlPropertiesIfNeeded();
            int cacheIndex = (this.mPrivateFlags & 4096) == 4096 ? -1 : this.mMeasureCache.indexOfKey(key);
            if (cacheIndex < 0 || sIgnoreMeasureCache) {
                onMeasure(widthMeasureSpec, heightMeasureSpec);
                this.mPrivateFlags3 &= -9;
            } else {
                long value = this.mMeasureCache.valueAt(cacheIndex);
                setMeasuredDimension((int) (value >> 32), (int) value);
                this.mPrivateFlags3 |= 8;
            }
            if ((this.mPrivateFlags & 2048) != 2048) {
                throw new IllegalStateException("onMeasure() did not set the measured dimension by calling setMeasuredDimension()");
            }
            this.mPrivateFlags |= 8192;
        }
        this.mOldWidthMeasureSpec = widthMeasureSpec;
        this.mOldHeightMeasureSpec = heightMeasureSpec;
        this.mMeasureCache.put(key, (this.mMeasuredWidth << 32) | (this.mMeasuredHeight & ExpandableListView.PACKED_POSITION_VALUE_NULL));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        boolean optical = isLayoutModeOptical(this);
        if (optical != isLayoutModeOptical(this.mParent)) {
            Insets insets = getOpticalInsets();
            int opticalWidth = insets.left + insets.right;
            int opticalHeight = insets.top + insets.bottom;
            measuredWidth += optical ? opticalWidth : -opticalWidth;
            measuredHeight += optical ? opticalHeight : -opticalHeight;
        }
        this.mMeasuredWidth = measuredWidth;
        this.mMeasuredHeight = measuredHeight;
        this.mPrivateFlags |= 2048;
    }

    public static int combineMeasuredStates(int curState, int newState) {
        return curState | newState;
    }

    public static int resolveSize(int size, int measureSpec) {
        return resolveSizeAndState(size, measureSpec, 0) & 16777215;
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                if (specSize < size) {
                    result = specSize | 16777216;
                    break;
                } else {
                    result = size;
                    break;
                }
            case 0:
                result = size;
                break;
            case 1073741824:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & (-16777216));
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
            case 1073741824:
                result = specSize;
                break;
            case 0:
                result = size;
                break;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSuggestedMinimumHeight() {
        return this.mBackground == null ? this.mMinHeight : Math.max(this.mMinHeight, this.mBackground.getMinimumHeight());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSuggestedMinimumWidth() {
        return this.mBackground == null ? this.mMinWidth : Math.max(this.mMinWidth, this.mBackground.getMinimumWidth());
    }

    public int getMinimumHeight() {
        return this.mMinHeight;
    }

    public void setMinimumHeight(int minHeight) {
        this.mMinHeight = minHeight;
        requestLayout();
    }

    public int getMinimumWidth() {
        return this.mMinWidth;
    }

    public void setMinimumWidth(int minWidth) {
        this.mMinWidth = minWidth;
        requestLayout();
    }

    public Animation getAnimation() {
        return this.mCurrentAnimation;
    }

    public void startAnimation(Animation animation) {
        animation.setStartTime(-1L);
        setAnimation(animation);
        invalidateParentCaches();
        invalidate(true);
    }

    public void clearAnimation() {
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.detach();
        }
        this.mCurrentAnimation = null;
        invalidateParentIfNeeded();
    }

    public void setAnimation(Animation animation) {
        this.mCurrentAnimation = animation;
        if (animation != null) {
            if (this.mAttachInfo != null && !this.mAttachInfo.mScreenOn && animation.getStartTime() == -1) {
                animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
            }
            animation.reset();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAnimationStart() {
        this.mPrivateFlags |= 65536;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAnimationEnd() {
        this.mPrivateFlags &= -65537;
    }

    protected boolean onSetAlpha(int alpha) {
        return false;
    }

    public boolean gatherTransparentRegion(Region region) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (region != null && attachInfo != null) {
            int pflags = this.mPrivateFlags;
            if ((pflags & 128) == 0) {
                int[] location = attachInfo.mTransparentLocation;
                getLocationInWindow(location);
                region.op(location[0], location[1], (location[0] + this.mRight) - this.mLeft, (location[1] + this.mBottom) - this.mTop, Region.Op.DIFFERENCE);
                return true;
            } else if ((pflags & 256) != 0 && this.mBackground != null) {
                applyDrawableToTransparentRegion(this.mBackground, region);
                return true;
            } else {
                return true;
            }
        }
        return true;
    }

    public void playSoundEffect(int soundConstant) {
        if (this.mAttachInfo == null || this.mAttachInfo.mRootCallbacks == null || !isSoundEffectsEnabled()) {
            return;
        }
        this.mAttachInfo.mRootCallbacks.playSoundEffect(soundConstant);
    }

    public boolean performHapticFeedback(int feedbackConstant) {
        return performHapticFeedback(feedbackConstant, 0);
    }

    public boolean performHapticFeedback(int feedbackConstant, int flags) {
        if (this.mAttachInfo == null) {
            return false;
        }
        if ((flags & 1) != 0 || isHapticFeedbackEnabled()) {
            return this.mAttachInfo.mRootCallbacks.performHapticFeedback(feedbackConstant, (flags & 2) != 0);
        }
        return false;
    }

    public void setSystemUiVisibility(int visibility) {
        if (visibility != this.mSystemUiVisibility) {
            this.mSystemUiVisibility = visibility;
            if (this.mParent != null && this.mAttachInfo != null && !this.mAttachInfo.mRecomputeGlobalAttributes) {
                this.mParent.recomputeViewAttributes(this);
            }
        }
    }

    public int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    public int getWindowSystemUiVisibility() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mSystemUiVisibility;
        }
        return 0;
    }

    public void onWindowSystemUiVisibilityChanged(int visible) {
    }

    public void dispatchWindowSystemUiVisiblityChanged(int visible) {
        onWindowSystemUiVisibilityChanged(visible);
    }

    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener l) {
        getListenerInfo().mOnSystemUiVisibilityChangeListener = l;
        if (this.mParent != null && this.mAttachInfo != null && !this.mAttachInfo.mRecomputeGlobalAttributes) {
            this.mParent.recomputeViewAttributes(this);
        }
    }

    public void dispatchSystemUiVisibilityChanged(int visibility) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnSystemUiVisibilityChangeListener != null) {
            li.mOnSystemUiVisibilityChangeListener.onSystemUiVisibilityChange(visibility & 65535);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateLocalSystemUiVisibility(int localValue, int localChanges) {
        int val = (this.mSystemUiVisibility & (localChanges ^ (-1))) | (localValue & localChanges);
        if (val != this.mSystemUiVisibility) {
            setSystemUiVisibility(val);
            return true;
        }
        return false;
    }

    public void setDisabledSystemUiVisibility(int flags) {
        if (this.mAttachInfo != null && this.mAttachInfo.mDisabledSystemUiVisibility != flags) {
            this.mAttachInfo.mDisabledSystemUiVisibility = flags;
            if (this.mParent != null) {
                this.mParent.recomputeViewAttributes(this);
            }
        }
    }

    /* loaded from: View$DragShadowBuilder.class */
    public static class DragShadowBuilder {
        private final WeakReference<View> mView;

        public DragShadowBuilder(View view) {
            this.mView = new WeakReference<>(view);
        }

        public DragShadowBuilder() {
            this.mView = new WeakReference<>(null);
        }

        public final View getView() {
            return this.mView.get();
        }

        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            View view = this.mView.get();
            if (view != null) {
                shadowSize.set(view.getWidth(), view.getHeight());
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
                return;
            }
            Log.e(View.VIEW_LOG_TAG, "Asked for drag thumb metrics but no view");
        }

        public void onDrawShadow(Canvas canvas) {
            View view = this.mView.get();
            if (view != null) {
                view.draw(canvas);
            } else {
                Log.e(View.VIEW_LOG_TAG, "Asked to draw drag shadow but no view");
            }
        }
    }

    public final boolean startDrag(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
        boolean okay = false;
        Point shadowSize = new Point();
        Point shadowTouchPoint = new Point();
        shadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        if (shadowSize.x < 0 || shadowSize.y < 0 || shadowTouchPoint.x < 0 || shadowTouchPoint.y < 0) {
            throw new IllegalStateException("Drag shadow dimensions must not be negative");
        }
        Surface surface = new Surface();
        try {
            IBinder token = this.mAttachInfo.mSession.prepareDrag(this.mAttachInfo.mWindow, flags, shadowSize.x, shadowSize.y, surface);
            if (token != null) {
                Canvas canvas = surface.lockCanvas(null);
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                shadowBuilder.onDrawShadow(canvas);
                surface.unlockCanvasAndPost(canvas);
                ViewRootImpl root = getViewRootImpl();
                root.setLocalDragState(myLocalState);
                root.getLastTouchPoint(shadowSize);
                okay = this.mAttachInfo.mSession.performDrag(this.mAttachInfo.mWindow, token, shadowSize.x, shadowSize.y, shadowTouchPoint.x, shadowTouchPoint.y, data);
                surface.release();
            }
        } catch (Exception e) {
            Log.e(VIEW_LOG_TAG, "Unable to initiate drag", e);
            surface.destroy();
        }
        return okay;
    }

    public boolean onDragEvent(DragEvent event) {
        return false;
    }

    public boolean dispatchDragEvent(DragEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnDragListener != null && (this.mViewFlags & 32) == 0 && li.mOnDragListener.onDrag(this, event)) {
            return true;
        }
        return onDragEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canAcceptDrag() {
        return (this.mPrivateFlags2 & 1) != 0;
    }

    public void onCloseSystemDialogs(String reason) {
    }

    public void applyDrawableToTransparentRegion(Drawable dr, Region region) {
        Region r = dr.getTransparentRegion();
        Rect db = dr.getBounds();
        AttachInfo attachInfo = this.mAttachInfo;
        if (r != null && attachInfo != null) {
            int w = getRight() - getLeft();
            int h = getBottom() - getTop();
            if (db.left > 0) {
                r.op(0, 0, db.left, h, Region.Op.UNION);
            }
            if (db.right < w) {
                r.op(db.right, 0, w, h, Region.Op.UNION);
            }
            if (db.top > 0) {
                r.op(0, 0, w, db.top, Region.Op.UNION);
            }
            if (db.bottom < h) {
                r.op(0, db.bottom, w, h, Region.Op.UNION);
            }
            int[] location = attachInfo.mTransparentLocation;
            getLocationInWindow(location);
            r.translate(location[0], location[1]);
            region.op(r, Region.Op.INTERSECT);
            return;
        }
        region.op(db, Region.Op.DIFFERENCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkForLongClick(int delayOffset) {
        if ((this.mViewFlags & 2097152) == 2097152) {
            this.mHasPerformedLongPress = false;
            if (this.mPendingCheckForLongPress == null) {
                this.mPendingCheckForLongPress = new CheckForLongPress();
            }
            this.mPendingCheckForLongPress.rememberWindowAttachCount();
            postDelayed(this.mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - delayOffset);
        }
    }

    public static View inflate(Context context, int resource, ViewGroup root) {
        LayoutInflater factory = LayoutInflater.from(context);
        return factory.inflate(resource, root);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int overScrollMode = this.mOverScrollMode;
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean overScrollHorizontal = overScrollMode == 0 || (overScrollMode == 1 && canScrollHorizontal);
        boolean overScrollVertical = overScrollMode == 0 || (overScrollMode == 1 && canScrollVertical);
        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX = 0;
        }
        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY = 0;
        }
        int left = -maxOverScrollX;
        int right = maxOverScrollX + scrollRangeX;
        int top = -maxOverScrollY;
        int bottom = maxOverScrollY + scrollRangeY;
        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }
        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);
        return clampedX || clampedY;
    }

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    }

    public int getOverScrollMode() {
        return this.mOverScrollMode;
    }

    public void setOverScrollMode(int overScrollMode) {
        if (overScrollMode != 0 && overScrollMode != 1 && overScrollMode != 2) {
            throw new IllegalArgumentException("Invalid overscroll mode " + overScrollMode);
        }
        this.mOverScrollMode = overScrollMode;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getVerticalScrollFactor() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue outValue = new TypedValue();
            if (!this.mContext.getTheme().resolveAttribute(16842829, outValue, true)) {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
            this.mVerticalScrollFactor = outValue.getDimension(this.mContext.getResources().getDisplayMetrics());
        }
        return this.mVerticalScrollFactor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getHorizontalScrollFactor() {
        return getVerticalScrollFactor();
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "FIRST_STRONG"), @ViewDebug.IntToString(from = 2, to = "ANY_RTL"), @ViewDebug.IntToString(from = 3, to = "LTR"), @ViewDebug.IntToString(from = 4, to = "RTL"), @ViewDebug.IntToString(from = 5, to = "LOCALE")})
    public int getRawTextDirection() {
        return (this.mPrivateFlags2 & 448) >> 6;
    }

    public void setTextDirection(int textDirection) {
        if (getRawTextDirection() != textDirection) {
            this.mPrivateFlags2 &= -449;
            resetResolvedTextDirection();
            this.mPrivateFlags2 |= (textDirection << 6) & 448;
            resolveTextDirection();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "FIRST_STRONG"), @ViewDebug.IntToString(from = 2, to = "ANY_RTL"), @ViewDebug.IntToString(from = 3, to = "LTR"), @ViewDebug.IntToString(from = 4, to = "RTL"), @ViewDebug.IntToString(from = 5, to = "LOCALE")})
    public int getTextDirection() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_DIRECTION_RESOLVED_MASK) >> 10;
    }

    public boolean resolveTextDirection() {
        int parentResolvedDirection;
        this.mPrivateFlags2 &= -7681;
        if (hasRtlSupport()) {
            int textDirection = getRawTextDirection();
            switch (textDirection) {
                case 0:
                    if (!canResolveTextDirection()) {
                        this.mPrivateFlags2 |= 1024;
                        return false;
                    }
                    try {
                        if (!this.mParent.isTextDirectionResolved()) {
                            this.mPrivateFlags2 |= 1024;
                            return false;
                        }
                        try {
                            parentResolvedDirection = this.mParent.getTextDirection();
                        } catch (AbstractMethodError e) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            parentResolvedDirection = 3;
                        }
                        switch (parentResolvedDirection) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                                this.mPrivateFlags2 |= parentResolvedDirection << 10;
                                break;
                            default:
                                this.mPrivateFlags2 |= 1024;
                                break;
                        }
                    } catch (AbstractMethodError e2) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                        this.mPrivateFlags2 |= 1536;
                        return true;
                    }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    this.mPrivateFlags2 |= textDirection << 10;
                    break;
                default:
                    this.mPrivateFlags2 |= 1024;
                    break;
            }
        } else {
            this.mPrivateFlags2 |= 1024;
        }
        this.mPrivateFlags2 |= 512;
        return true;
    }

    public boolean canResolveTextDirection() {
        switch (getRawTextDirection()) {
            case 0:
                if (this.mParent != null) {
                    try {
                        return this.mParent.canResolveTextDirection();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        return false;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void resetResolvedTextDirection() {
        this.mPrivateFlags2 &= -7681;
        this.mPrivateFlags2 |= 1024;
    }

    public boolean isTextDirectionInherited() {
        return getRawTextDirection() == 0;
    }

    public boolean isTextDirectionResolved() {
        return (this.mPrivateFlags2 & 512) == 512;
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "GRAVITY"), @ViewDebug.IntToString(from = 2, to = "TEXT_START"), @ViewDebug.IntToString(from = 3, to = "TEXT_END"), @ViewDebug.IntToString(from = 4, to = "CENTER"), @ViewDebug.IntToString(from = 5, to = "VIEW_START"), @ViewDebug.IntToString(from = 6, to = "VIEW_END")})
    public int getRawTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_MASK) >> 13;
    }

    public void setTextAlignment(int textAlignment) {
        if (textAlignment != getRawTextAlignment()) {
            this.mPrivateFlags2 &= -57345;
            resetResolvedTextAlignment();
            this.mPrivateFlags2 |= (textAlignment << 13) & PFLAG2_TEXT_ALIGNMENT_MASK;
            resolveTextAlignment();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "GRAVITY"), @ViewDebug.IntToString(from = 2, to = "TEXT_START"), @ViewDebug.IntToString(from = 3, to = "TEXT_END"), @ViewDebug.IntToString(from = 4, to = "CENTER"), @ViewDebug.IntToString(from = 5, to = "VIEW_START"), @ViewDebug.IntToString(from = 6, to = "VIEW_END")})
    public int getTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK) >> 17;
    }

    public boolean resolveTextAlignment() {
        int parentResolvedTextAlignment;
        this.mPrivateFlags2 &= -983041;
        if (hasRtlSupport()) {
            int textAlignment = getRawTextAlignment();
            switch (textAlignment) {
                case 0:
                    if (!canResolveTextAlignment()) {
                        this.mPrivateFlags2 |= 131072;
                        return false;
                    }
                    try {
                        if (!this.mParent.isTextAlignmentResolved()) {
                            this.mPrivateFlags2 |= 131072;
                            return false;
                        }
                        try {
                            parentResolvedTextAlignment = this.mParent.getTextAlignment();
                        } catch (AbstractMethodError e) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            parentResolvedTextAlignment = 1;
                        }
                        switch (parentResolvedTextAlignment) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.mPrivateFlags2 |= parentResolvedTextAlignment << 17;
                                break;
                            default:
                                this.mPrivateFlags2 |= 131072;
                                break;
                        }
                    } catch (AbstractMethodError e2) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                        this.mPrivateFlags2 |= 196608;
                        return true;
                    }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    this.mPrivateFlags2 |= textAlignment << 17;
                    break;
                default:
                    this.mPrivateFlags2 |= 131072;
                    break;
            }
        } else {
            this.mPrivateFlags2 |= 131072;
        }
        this.mPrivateFlags2 |= 65536;
        return true;
    }

    public boolean canResolveTextAlignment() {
        switch (getRawTextAlignment()) {
            case 0:
                if (this.mParent != null) {
                    try {
                        return this.mParent.canResolveTextAlignment();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        return false;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void resetResolvedTextAlignment() {
        this.mPrivateFlags2 &= -983041;
        this.mPrivateFlags2 |= 131072;
    }

    public boolean isTextAlignmentInherited() {
        return getRawTextAlignment() == 0;
    }

    public boolean isTextAlignmentResolved() {
        return (this.mPrivateFlags2 & 65536) == 65536;
    }

    public static int generateViewId() {
        int result;
        int newValue;
        do {
            result = sNextGeneratedId.get();
            newValue = result + 1;
            if (newValue > 16777215) {
                newValue = 1;
            }
        } while (!sNextGeneratedId.compareAndSet(result, newValue));
        return result;
    }

    /* loaded from: View$MeasureSpec.class */
    public static class MeasureSpec {
        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK = -1073741824;
        public static final int UNSPECIFIED = 0;
        public static final int EXACTLY = 1073741824;
        public static final int AT_MOST = Integer.MIN_VALUE;

        public static int makeMeasureSpec(int size, int mode) {
            if (View.sUseBrokenMakeMeasureSpec) {
                return size + mode;
            }
            return (size & 1073741823) | (mode & (-1073741824));
        }

        public static int getMode(int measureSpec) {
            return measureSpec & (-1073741824);
        }

        public static int getSize(int measureSpec) {
            return measureSpec & 1073741823;
        }

        static int adjust(int measureSpec, int delta) {
            return makeMeasureSpec(getSize(measureSpec + delta), getMode(measureSpec));
        }

        public static String toString(int measureSpec) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            StringBuilder sb = new StringBuilder("MeasureSpec: ");
            if (mode == 0) {
                sb.append("UNSPECIFIED ");
            } else if (mode == 1073741824) {
                sb.append("EXACTLY ");
            } else if (mode == Integer.MIN_VALUE) {
                sb.append("AT_MOST ");
            } else {
                sb.append(mode).append(Separators.SP);
            }
            sb.append(size);
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: View$CheckForLongPress.class */
    public class CheckForLongPress implements Runnable {
        private int mOriginalWindowAttachCount;

        CheckForLongPress() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (View.this.isPressed() && View.this.mParent != null && this.mOriginalWindowAttachCount == View.this.mWindowAttachCount && View.this.performLongClick()) {
                View.this.mHasPerformedLongPress = true;
            }
        }

        public void rememberWindowAttachCount() {
            this.mOriginalWindowAttachCount = View.this.mWindowAttachCount;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$CheckForTap.class */
    public final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.mPrivateFlags &= -33554433;
            View.this.setPressed(true);
            View.this.checkForLongClick(ViewConfiguration.getTapTimeout());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$PerformClick.class */
    public final class PerformClick implements Runnable {
        private PerformClick() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.performClick();
        }
    }

    public void hackTurnOffWindowResizeAnim(boolean off) {
        this.mAttachInfo.mTurnOffWindowResizeAnim = off;
    }

    public ViewPropertyAnimator animate() {
        if (this.mAnimator == null) {
            this.mAnimator = new ViewPropertyAnimator(this);
        }
        return this.mAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$UnsetPressedState.class */
    public final class UnsetPressedState implements Runnable {
        private UnsetPressedState() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.setPressed(false);
        }
    }

    /* loaded from: View$BaseSavedState.class */
    public static class BaseSavedState extends AbsSavedState {
        public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.Creator<BaseSavedState>() { // from class: android.view.View.BaseSavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        };

        public BaseSavedState(Parcel source) {
            super(source);
        }

        public BaseSavedState(Parcelable superState) {
            super(superState);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: View$AttachInfo.class */
    public static class AttachInfo {
        final IWindowSession mSession;
        final IWindow mWindow;
        final IBinder mWindowToken;
        final Display mDisplay;
        final Callbacks mRootCallbacks;
        HardwareCanvas mHardwareCanvas;
        IWindowId mIWindowId;
        WindowId mWindowId;
        View mRootView;
        IBinder mPanelParentWindowToken;
        Surface mSurface;
        boolean mHardwareAccelerated;
        boolean mHardwareAccelerationRequested;
        HardwareRenderer mHardwareRenderer;
        boolean mScreenOn;
        float mApplicationScale;
        boolean mScalingRequired;
        boolean mTurnOffWindowResizeAnim;
        int mWindowLeft;
        int mWindowTop;
        boolean mUse32BitDrawingCache;
        boolean mHasNonEmptyGivenInternalInsets;
        boolean mHasWindowFocus;
        int mWindowVisibility;
        long mDrawingTime;
        boolean mIgnoreDirtyState;
        boolean mInTouchMode;
        boolean mRecomputeGlobalAttributes;
        boolean mForceReportNewAttributes;
        boolean mKeepScreenOn;
        int mSystemUiVisibility;
        int mDisabledSystemUiVisibility;
        int mGlobalSystemUiVisibility;
        boolean mHasSystemUiListeners;
        boolean mOverscanRequested;
        boolean mViewVisibilityChanged;
        boolean mViewScrollChanged;
        Canvas mCanvas;
        final ViewRootImpl mViewRootImpl;
        final Handler mHandler;
        int mAccessibilityFetchFlags;
        Drawable mAccessibilityFocusDrawable;
        View mViewRequestingLayout;
        final Rect mOverscanInsets = new Rect();
        final Rect mContentInsets = new Rect();
        final Rect mVisibleInsets = new Rect();
        final ViewTreeObserver.InternalInsetsInfo mGivenInternalInsets = new ViewTreeObserver.InternalInsetsInfo();
        final ArrayList<View> mScrollContainers = new ArrayList<>();
        final KeyEvent.DispatcherState mKeyDispatchState = new KeyEvent.DispatcherState();
        boolean mSetIgnoreDirtyState = false;
        final int[] mTransparentLocation = new int[2];
        final int[] mInvalidateChildLocation = new int[2];
        final float[] mTmpTransformLocation = new float[2];
        final ViewTreeObserver mTreeObserver = new ViewTreeObserver();
        final Rect mTmpInvalRect = new Rect();
        final RectF mTmpTransformRect = new RectF();
        final Matrix mTmpMatrix = new Matrix();
        final Transformation mTmpTransformation = new Transformation();
        final ArrayList<View> mTempArrayList = new ArrayList<>(24);
        int mAccessibilityWindowId = -1;
        boolean mDebugLayout = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, false);
        final Point mPoint = new Point();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: View$AttachInfo$Callbacks.class */
        public interface Callbacks {
            void playSoundEffect(int i);

            boolean performHapticFeedback(int i, boolean z);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: View$AttachInfo$InvalidateInfo.class */
        public static class InvalidateInfo {
            private static final int POOL_LIMIT = 10;
            private static final Pools.SynchronizedPool<InvalidateInfo> sPool = new Pools.SynchronizedPool<>(10);
            View target;
            int left;
            int top;
            int right;
            int bottom;

            InvalidateInfo() {
            }

            public static InvalidateInfo obtain() {
                InvalidateInfo instance = sPool.acquire();
                return instance != null ? instance : new InvalidateInfo();
            }

            public void recycle() {
                this.target = null;
                sPool.release(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public AttachInfo(IWindowSession session, IWindow window, Display display, ViewRootImpl viewRootImpl, Handler handler, Callbacks effectPlayer) {
            this.mSession = session;
            this.mWindow = window;
            this.mWindowToken = window.asBinder();
            this.mDisplay = display;
            this.mViewRootImpl = viewRootImpl;
            this.mHandler = handler;
            this.mRootCallbacks = effectPlayer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$ScrollabilityCache.class */
    public static class ScrollabilityCache implements Runnable {
        public static final int OFF = 0;
        public static final int ON = 1;
        public static final int FADING = 2;
        public boolean fadeScrollBars;
        public int fadingEdgeLength;
        public int scrollBarSize;
        public ScrollBarDrawable scrollBar;
        public float[] interpolatorValues;
        public View host;
        private static final float[] OPAQUE = {255.0f};
        private static final float[] TRANSPARENT = {0.0f};
        public long fadeStartTime;
        private int mLastColor;
        public final Interpolator scrollBarInterpolator = new Interpolator(1, 2);
        public int state = 0;
        public int scrollBarDefaultDelayBeforeFade = ViewConfiguration.getScrollDefaultDelay();
        public int scrollBarFadeDuration = ViewConfiguration.getScrollBarFadeDuration();
        public final Paint paint = new Paint();
        public final Matrix matrix = new Matrix();
        public Shader shader = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, -16777216, 0, Shader.TileMode.CLAMP);

        public ScrollabilityCache(ViewConfiguration configuration, View host) {
            this.fadingEdgeLength = configuration.getScaledFadingEdgeLength();
            this.scrollBarSize = configuration.getScaledScrollBarSize();
            this.paint.setShader(this.shader);
            this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.host = host;
        }

        public void setFadeColor(int color) {
            if (color != this.mLastColor) {
                this.mLastColor = color;
                if (color != 0) {
                    this.shader = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, color | (-16777216), color & 16777215, Shader.TileMode.CLAMP);
                    this.paint.setShader(this.shader);
                    this.paint.setXfermode(null);
                    return;
                }
                this.shader = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, -16777216, 0, Shader.TileMode.CLAMP);
                this.paint.setShader(this.shader);
                this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            if (now >= this.fadeStartTime) {
                int nextFrame = (int) now;
                Interpolator interpolator = this.scrollBarInterpolator;
                int framesCount = 0 + 1;
                interpolator.setKeyFrame(0, nextFrame, OPAQUE);
                interpolator.setKeyFrame(framesCount, nextFrame + this.scrollBarFadeDuration, TRANSPARENT);
                this.state = 2;
                this.host.invalidate(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$SendViewScrolledAccessibilityEvent.class */
    public class SendViewScrolledAccessibilityEvent implements Runnable {
        public volatile boolean mIsPending;

        private SendViewScrolledAccessibilityEvent() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.sendAccessibilityEvent(4096);
            this.mIsPending = false;
        }
    }

    /* loaded from: View$AccessibilityDelegate.class */
    public static class AccessibilityDelegate {
        public void sendAccessibilityEvent(View host, int eventType) {
            host.sendAccessibilityEventInternal(eventType);
        }

        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            return host.performAccessibilityActionInternal(action, args);
        }

        public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
            host.sendAccessibilityEventUncheckedInternal(event);
        }

        public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            return host.dispatchPopulateAccessibilityEventInternal(event);
        }

        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            host.onPopulateAccessibilityEventInternal(event);
        }

        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            host.onInitializeAccessibilityEventInternal(event);
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            host.onInitializeAccessibilityNodeInfoInternal(info);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            return host.onRequestSendAccessibilityEventInternal(child, event);
        }

        public AccessibilityNodeProvider getAccessibilityNodeProvider(View host) {
            return null;
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(View host) {
            return host.createAccessibilityNodeInfoInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$MatchIdPredicate.class */
    public class MatchIdPredicate implements Predicate<View> {
        public int mId;

        private MatchIdPredicate() {
        }

        @Override // com.android.internal.util.Predicate
        public boolean apply(View view) {
            return view.mID == this.mId;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$MatchLabelForPredicate.class */
    public class MatchLabelForPredicate implements Predicate<View> {
        private int mLabeledId;

        private MatchLabelForPredicate() {
        }

        @Override // com.android.internal.util.Predicate
        public boolean apply(View view) {
            return view.mLabelForId == this.mLabeledId;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: View$SendViewStateChangedAccessibilityEvent.class */
    public class SendViewStateChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        private boolean mPosted;
        private boolean mPostedWithDelay;
        private long mLastEventTimeMillis;

        private SendViewStateChangedAccessibilityEvent() {
            this.mChangeTypes = 0;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mPosted = false;
            this.mPostedWithDelay = false;
            this.mLastEventTimeMillis = SystemClock.uptimeMillis();
            if (AccessibilityManager.getInstance(View.this.mContext).isEnabled()) {
                AccessibilityEvent event = AccessibilityEvent.obtain();
                event.setEventType(2048);
                event.setContentChangeTypes(this.mChangeTypes);
                View.this.sendAccessibilityEventUnchecked(event);
            }
            this.mChangeTypes = 0;
        }

        public void runOrPost(int changeType) {
            this.mChangeTypes |= changeType;
            if (View.this.inLiveRegion()) {
                if (this.mPostedWithDelay) {
                    View.this.removeCallbacks(this);
                    this.mPostedWithDelay = false;
                }
                if (!this.mPosted) {
                    View.this.post(this);
                    this.mPosted = true;
                }
            } else if (this.mPosted) {
            } else {
                long timeSinceLastMillis = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
                long minEventIntevalMillis = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
                if (timeSinceLastMillis >= minEventIntevalMillis) {
                    View.this.removeCallbacks(this);
                    run();
                    return;
                }
                View.this.postDelayed(this, minEventIntevalMillis - timeSinceLastMillis);
                this.mPosted = true;
                this.mPostedWithDelay = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean inLiveRegion() {
        if (getAccessibilityLiveRegion() != 0) {
            return true;
        }
        ViewParent parent = getParent();
        while (true) {
            ViewParent parent2 = parent;
            if (parent2 instanceof View) {
                if (((View) parent2).getAccessibilityLiveRegion() != 0) {
                    return true;
                }
                parent = parent2.getParent();
            } else {
                return false;
            }
        }
    }

    private static void dumpFlags() {
        HashMap<String, String> found = Maps.newHashMap();
        try {
            Field[] arr$ = View.class.getDeclaredFields();
            for (Field field : arr$) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    if (field.getType().equals(Integer.TYPE)) {
                        int value = field.getInt(null);
                        dumpFlag(found, field.getName(), value);
                    } else if (field.getType().equals(int[].class)) {
                        int[] values = (int[]) field.get(null);
                        for (int i = 0; i < values.length; i++) {
                            dumpFlag(found, field.getName() + "[" + i + "]", values[i]);
                        }
                    }
                }
            }
            ArrayList<String> keys = Lists.newArrayList();
            keys.addAll(found.keySet());
            Collections.sort(keys);
            Iterator i$ = keys.iterator();
            while (i$.hasNext()) {
                String key = i$.next();
                Log.d(VIEW_LOG_TAG, found.get(key));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dumpFlag(HashMap<String, String> found, String name, int value) {
        String bits = String.format("%32s", Integer.toBinaryString(value)).replace('0', ' ');
        int prefix = name.indexOf(95);
        String key = (prefix > 0 ? name.substring(0, prefix) : name) + bits + name;
        String output = bits + Separators.SP + name;
        found.put(key, output);
    }
}