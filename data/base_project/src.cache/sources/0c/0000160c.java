package android.view;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewDebug;
import android.view.ViewGroup;
import gov.nist.core.Separators;

/* loaded from: WindowManager.class */
public interface WindowManager extends ViewManager {
    Display getDefaultDisplay();

    void removeViewImmediate(View view);

    /* loaded from: WindowManager$BadTokenException.class */
    public static class BadTokenException extends RuntimeException {
        public BadTokenException() {
        }

        public BadTokenException(String name) {
            super(name);
        }
    }

    /* loaded from: WindowManager$InvalidDisplayException.class */
    public static class InvalidDisplayException extends RuntimeException {
        public InvalidDisplayException() {
        }

        public InvalidDisplayException(String name) {
            super(name);
        }
    }

    /* loaded from: WindowManager$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.LayoutParams implements Parcelable {
        @ViewDebug.ExportedProperty
        public int x;
        @ViewDebug.ExportedProperty
        public int y;
        @ViewDebug.ExportedProperty
        public float horizontalWeight;
        @ViewDebug.ExportedProperty
        public float verticalWeight;
        @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 1, to = "TYPE_BASE_APPLICATION"), @ViewDebug.IntToString(from = 2, to = "TYPE_APPLICATION"), @ViewDebug.IntToString(from = 3, to = "TYPE_APPLICATION_STARTING"), @ViewDebug.IntToString(from = 1000, to = "TYPE_APPLICATION_PANEL"), @ViewDebug.IntToString(from = 1001, to = "TYPE_APPLICATION_MEDIA"), @ViewDebug.IntToString(from = 1002, to = "TYPE_APPLICATION_SUB_PANEL"), @ViewDebug.IntToString(from = 1003, to = "TYPE_APPLICATION_ATTACHED_DIALOG"), @ViewDebug.IntToString(from = 1004, to = "TYPE_APPLICATION_MEDIA_OVERLAY"), @ViewDebug.IntToString(from = 2000, to = "TYPE_STATUS_BAR"), @ViewDebug.IntToString(from = 2001, to = "TYPE_SEARCH_BAR"), @ViewDebug.IntToString(from = 2002, to = "TYPE_PHONE"), @ViewDebug.IntToString(from = 2003, to = "TYPE_SYSTEM_ALERT"), @ViewDebug.IntToString(from = 2004, to = "TYPE_KEYGUARD"), @ViewDebug.IntToString(from = 2005, to = "TYPE_TOAST"), @ViewDebug.IntToString(from = 2006, to = "TYPE_SYSTEM_OVERLAY"), @ViewDebug.IntToString(from = 2007, to = "TYPE_PRIORITY_PHONE"), @ViewDebug.IntToString(from = 2008, to = "TYPE_SYSTEM_DIALOG"), @ViewDebug.IntToString(from = 2009, to = "TYPE_KEYGUARD_DIALOG"), @ViewDebug.IntToString(from = TYPE_SYSTEM_ERROR, to = "TYPE_SYSTEM_ERROR"), @ViewDebug.IntToString(from = 2011, to = "TYPE_INPUT_METHOD"), @ViewDebug.IntToString(from = TYPE_INPUT_METHOD_DIALOG, to = "TYPE_INPUT_METHOD_DIALOG"), @ViewDebug.IntToString(from = TYPE_WALLPAPER, to = "TYPE_WALLPAPER"), @ViewDebug.IntToString(from = TYPE_STATUS_BAR_PANEL, to = "TYPE_STATUS_BAR_PANEL"), @ViewDebug.IntToString(from = TYPE_SECURE_SYSTEM_OVERLAY, to = "TYPE_SECURE_SYSTEM_OVERLAY"), @ViewDebug.IntToString(from = TYPE_DRAG, to = "TYPE_DRAG"), @ViewDebug.IntToString(from = TYPE_STATUS_BAR_SUB_PANEL, to = "TYPE_STATUS_BAR_SUB_PANEL"), @ViewDebug.IntToString(from = TYPE_POINTER, to = "TYPE_POINTER"), @ViewDebug.IntToString(from = TYPE_NAVIGATION_BAR, to = "TYPE_NAVIGATION_BAR"), @ViewDebug.IntToString(from = TYPE_VOLUME_OVERLAY, to = "TYPE_VOLUME_OVERLAY"), @ViewDebug.IntToString(from = TYPE_BOOT_PROGRESS, to = "TYPE_BOOT_PROGRESS"), @ViewDebug.IntToString(from = TYPE_HIDDEN_NAV_CONSUMER, to = "TYPE_HIDDEN_NAV_CONSUMER"), @ViewDebug.IntToString(from = TYPE_DREAM, to = "TYPE_DREAM"), @ViewDebug.IntToString(from = TYPE_NAVIGATION_BAR_PANEL, to = "TYPE_NAVIGATION_BAR_PANEL"), @ViewDebug.IntToString(from = TYPE_DISPLAY_OVERLAY, to = "TYPE_DISPLAY_OVERLAY"), @ViewDebug.IntToString(from = TYPE_MAGNIFICATION_OVERLAY, to = "TYPE_MAGNIFICATION_OVERLAY"), @ViewDebug.IntToString(from = TYPE_PRIVATE_PRESENTATION, to = "TYPE_PRIVATE_PRESENTATION")})
        public int type;
        public static final int FIRST_APPLICATION_WINDOW = 1;
        public static final int TYPE_BASE_APPLICATION = 1;
        public static final int TYPE_APPLICATION = 2;
        public static final int TYPE_APPLICATION_STARTING = 3;
        public static final int LAST_APPLICATION_WINDOW = 99;
        public static final int FIRST_SUB_WINDOW = 1000;
        public static final int TYPE_APPLICATION_PANEL = 1000;
        public static final int TYPE_APPLICATION_MEDIA = 1001;
        public static final int TYPE_APPLICATION_SUB_PANEL = 1002;
        public static final int TYPE_APPLICATION_ATTACHED_DIALOG = 1003;
        public static final int TYPE_APPLICATION_MEDIA_OVERLAY = 1004;
        public static final int LAST_SUB_WINDOW = 1999;
        public static final int FIRST_SYSTEM_WINDOW = 2000;
        public static final int TYPE_STATUS_BAR = 2000;
        public static final int TYPE_SEARCH_BAR = 2001;
        public static final int TYPE_PHONE = 2002;
        public static final int TYPE_SYSTEM_ALERT = 2003;
        public static final int TYPE_KEYGUARD = 2004;
        public static final int TYPE_TOAST = 2005;
        public static final int TYPE_SYSTEM_OVERLAY = 2006;
        public static final int TYPE_PRIORITY_PHONE = 2007;
        public static final int TYPE_SYSTEM_DIALOG = 2008;
        public static final int TYPE_KEYGUARD_DIALOG = 2009;
        public static final int TYPE_SYSTEM_ERROR = 2010;
        public static final int TYPE_INPUT_METHOD = 2011;
        public static final int TYPE_INPUT_METHOD_DIALOG = 2012;
        public static final int TYPE_WALLPAPER = 2013;
        public static final int TYPE_STATUS_BAR_PANEL = 2014;
        public static final int TYPE_SECURE_SYSTEM_OVERLAY = 2015;
        public static final int TYPE_DRAG = 2016;
        public static final int TYPE_STATUS_BAR_SUB_PANEL = 2017;
        public static final int TYPE_POINTER = 2018;
        public static final int TYPE_NAVIGATION_BAR = 2019;
        public static final int TYPE_VOLUME_OVERLAY = 2020;
        public static final int TYPE_BOOT_PROGRESS = 2021;
        public static final int TYPE_HIDDEN_NAV_CONSUMER = 2022;
        public static final int TYPE_DREAM = 2023;
        public static final int TYPE_NAVIGATION_BAR_PANEL = 2024;
        public static final int TYPE_UNIVERSE_BACKGROUND = 2025;
        public static final int TYPE_DISPLAY_OVERLAY = 2026;
        public static final int TYPE_MAGNIFICATION_OVERLAY = 2027;
        public static final int TYPE_RECENTS_OVERLAY = 2028;
        public static final int TYPE_KEYGUARD_SCRIM = 2029;
        public static final int TYPE_PRIVATE_PRESENTATION = 2030;
        public static final int LAST_SYSTEM_WINDOW = 2999;
        @Deprecated
        public static final int MEMORY_TYPE_NORMAL = 0;
        @Deprecated
        public static final int MEMORY_TYPE_HARDWARE = 1;
        @Deprecated
        public static final int MEMORY_TYPE_GPU = 2;
        @Deprecated
        public static final int MEMORY_TYPE_PUSH_BUFFERS = 3;
        @Deprecated
        public int memoryType;
        public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 1;
        public static final int FLAG_DIM_BEHIND = 2;
        @Deprecated
        public static final int FLAG_BLUR_BEHIND = 4;
        public static final int FLAG_NOT_FOCUSABLE = 8;
        public static final int FLAG_NOT_TOUCHABLE = 16;
        public static final int FLAG_NOT_TOUCH_MODAL = 32;
        public static final int FLAG_TOUCHABLE_WHEN_WAKING = 64;
        public static final int FLAG_KEEP_SCREEN_ON = 128;
        public static final int FLAG_LAYOUT_IN_SCREEN = 256;
        public static final int FLAG_LAYOUT_NO_LIMITS = 512;
        public static final int FLAG_FULLSCREEN = 1024;
        public static final int FLAG_FORCE_NOT_FULLSCREEN = 2048;
        @Deprecated
        public static final int FLAG_DITHER = 4096;
        public static final int FLAG_SECURE = 8192;
        public static final int FLAG_SCALED = 16384;
        public static final int FLAG_IGNORE_CHEEK_PRESSES = 32768;
        public static final int FLAG_LAYOUT_INSET_DECOR = 65536;
        public static final int FLAG_ALT_FOCUSABLE_IM = 131072;
        public static final int FLAG_WATCH_OUTSIDE_TOUCH = 262144;
        public static final int FLAG_SHOW_WHEN_LOCKED = 524288;
        public static final int FLAG_SHOW_WALLPAPER = 1048576;
        public static final int FLAG_TURN_SCREEN_ON = 2097152;
        public static final int FLAG_DISMISS_KEYGUARD = 4194304;
        public static final int FLAG_SPLIT_TOUCH = 8388608;
        public static final int FLAG_HARDWARE_ACCELERATED = 16777216;
        public static final int FLAG_LAYOUT_IN_OVERSCAN = 33554432;
        public static final int FLAG_TRANSLUCENT_STATUS = 67108864;
        public static final int FLAG_TRANSLUCENT_NAVIGATION = 134217728;
        public static final int FLAG_LOCAL_FOCUS_MODE = 268435456;
        public static final int FLAG_SLIPPERY = 536870912;
        public static final int FLAG_NEEDS_MENU_KEY = 1073741824;
        @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(mask = 1, equals = 1, name = "FLAG_ALLOW_LOCK_WHILE_SCREEN_ON"), @ViewDebug.FlagToString(mask = 2, equals = 2, name = "FLAG_DIM_BEHIND"), @ViewDebug.FlagToString(mask = 4, equals = 4, name = "FLAG_BLUR_BEHIND"), @ViewDebug.FlagToString(mask = 8, equals = 8, name = "FLAG_NOT_FOCUSABLE"), @ViewDebug.FlagToString(mask = 16, equals = 16, name = "FLAG_NOT_TOUCHABLE"), @ViewDebug.FlagToString(mask = 32, equals = 32, name = "FLAG_NOT_TOUCH_MODAL"), @ViewDebug.FlagToString(mask = 64, equals = 64, name = "FLAG_TOUCHABLE_WHEN_WAKING"), @ViewDebug.FlagToString(mask = 128, equals = 128, name = "FLAG_KEEP_SCREEN_ON"), @ViewDebug.FlagToString(mask = 256, equals = 256, name = "FLAG_LAYOUT_IN_SCREEN"), @ViewDebug.FlagToString(mask = 512, equals = 512, name = "FLAG_LAYOUT_NO_LIMITS"), @ViewDebug.FlagToString(mask = 1024, equals = 1024, name = "FLAG_FULLSCREEN"), @ViewDebug.FlagToString(mask = 2048, equals = 2048, name = "FLAG_FORCE_NOT_FULLSCREEN"), @ViewDebug.FlagToString(mask = 4096, equals = 4096, name = "FLAG_DITHER"), @ViewDebug.FlagToString(mask = 8192, equals = 8192, name = "FLAG_SECURE"), @ViewDebug.FlagToString(mask = 16384, equals = 16384, name = "FLAG_SCALED"), @ViewDebug.FlagToString(mask = 32768, equals = 32768, name = "FLAG_IGNORE_CHEEK_PRESSES"), @ViewDebug.FlagToString(mask = 65536, equals = 65536, name = "FLAG_LAYOUT_INSET_DECOR"), @ViewDebug.FlagToString(mask = 131072, equals = 131072, name = "FLAG_ALT_FOCUSABLE_IM"), @ViewDebug.FlagToString(mask = 262144, equals = 262144, name = "FLAG_WATCH_OUTSIDE_TOUCH"), @ViewDebug.FlagToString(mask = 524288, equals = 524288, name = "FLAG_SHOW_WHEN_LOCKED"), @ViewDebug.FlagToString(mask = 1048576, equals = 1048576, name = "FLAG_SHOW_WALLPAPER"), @ViewDebug.FlagToString(mask = 2097152, equals = 2097152, name = "FLAG_TURN_SCREEN_ON"), @ViewDebug.FlagToString(mask = 4194304, equals = 4194304, name = "FLAG_DISMISS_KEYGUARD"), @ViewDebug.FlagToString(mask = 8388608, equals = 8388608, name = "FLAG_SPLIT_TOUCH"), @ViewDebug.FlagToString(mask = 16777216, equals = 16777216, name = "FLAG_HARDWARE_ACCELERATED"), @ViewDebug.FlagToString(mask = 268435456, equals = 268435456, name = "FLAG_LOCAL_FOCUS_MODE"), @ViewDebug.FlagToString(mask = 67108864, equals = 67108864, name = "FLAG_TRANSLUCENT_STATUS"), @ViewDebug.FlagToString(mask = 134217728, equals = 134217728, name = "FLAG_TRANSLUCENT_NAVIGATION")})
        public int flags;
        public static final int PRIVATE_FLAG_FAKE_HARDWARE_ACCELERATED = 1;
        public static final int PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED = 2;
        public static final int PRIVATE_FLAG_WANTS_OFFSET_NOTIFICATIONS = 4;
        public static final int PRIVATE_FLAG_SET_NEEDS_MENU_KEY = 8;
        public static final int PRIVATE_FLAG_SHOW_FOR_ALL_USERS = 16;
        public static final int PRIVATE_FLAG_FORCE_SHOW_NAV_BAR = 32;
        public static final int PRIVATE_FLAG_NO_MOVE_ANIMATION = 64;
        public static final int PRIVATE_FLAG_COMPATIBLE_WINDOW = 128;
        public static final int PRIVATE_FLAG_SYSTEM_ERROR = 256;
        public static final int PRIVATE_FLAG_INHERIT_TRANSLUCENT_DECOR = 512;
        public int privateFlags;
        public static final int SOFT_INPUT_MASK_STATE = 15;
        public static final int SOFT_INPUT_STATE_UNSPECIFIED = 0;
        public static final int SOFT_INPUT_STATE_UNCHANGED = 1;
        public static final int SOFT_INPUT_STATE_HIDDEN = 2;
        public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
        public static final int SOFT_INPUT_STATE_VISIBLE = 4;
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        public static final int SOFT_INPUT_MASK_ADJUST = 240;
        public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0;
        public static final int SOFT_INPUT_ADJUST_RESIZE = 16;
        public static final int SOFT_INPUT_ADJUST_PAN = 32;
        public static final int SOFT_INPUT_ADJUST_NOTHING = 48;
        public static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 256;
        public int softInputMode;
        public int gravity;
        public float horizontalMargin;
        public float verticalMargin;
        public int format;
        public int windowAnimations;
        public float alpha;
        public float dimAmount;
        public static final float BRIGHTNESS_OVERRIDE_NONE = -1.0f;
        public static final float BRIGHTNESS_OVERRIDE_OFF = 0.0f;
        public static final float BRIGHTNESS_OVERRIDE_FULL = 1.0f;
        public float screenBrightness;
        public float buttonBrightness;
        public static final int ROTATION_ANIMATION_ROTATE = 0;
        public static final int ROTATION_ANIMATION_CROSSFADE = 1;
        public static final int ROTATION_ANIMATION_JUMPCUT = 2;
        public int rotationAnimation;
        public IBinder token;
        public String packageName;
        public int screenOrientation;
        public int systemUiVisibility;
        public int subtreeSystemUiVisibility;
        public boolean hasSystemUiListeners;
        public static final int INPUT_FEATURE_DISABLE_POINTER_GESTURES = 1;
        public static final int INPUT_FEATURE_NO_INPUT_CHANNEL = 2;
        public static final int INPUT_FEATURE_DISABLE_USER_ACTIVITY = 4;
        public int inputFeatures;
        public long userActivityTimeout;
        public static final Parcelable.Creator<LayoutParams> CREATOR = new Parcelable.Creator<LayoutParams>() { // from class: android.view.WindowManager.LayoutParams.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LayoutParams createFromParcel(Parcel in) {
                return new LayoutParams(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LayoutParams[] newArray(int size) {
                return new LayoutParams[size];
            }
        };
        public static final int LAYOUT_CHANGED = 1;
        public static final int TYPE_CHANGED = 2;
        public static final int FLAGS_CHANGED = 4;
        public static final int FORMAT_CHANGED = 8;
        public static final int ANIMATION_CHANGED = 16;
        public static final int DIM_AMOUNT_CHANGED = 32;
        public static final int TITLE_CHANGED = 64;
        public static final int ALPHA_CHANGED = 128;
        public static final int MEMORY_TYPE_CHANGED = 256;
        public static final int SOFT_INPUT_MODE_CHANGED = 512;
        public static final int SCREEN_ORIENTATION_CHANGED = 1024;
        public static final int SCREEN_BRIGHTNESS_CHANGED = 2048;
        public static final int ROTATION_ANIMATION_CHANGED = 4096;
        public static final int BUTTON_BRIGHTNESS_CHANGED = 8192;
        public static final int SYSTEM_UI_VISIBILITY_CHANGED = 16384;
        public static final int SYSTEM_UI_LISTENER_CHANGED = 32768;
        public static final int INPUT_FEATURES_CHANGED = 65536;
        public static final int PRIVATE_FLAGS_CHANGED = 131072;
        public static final int USER_ACTIVITY_TIMEOUT_CHANGED = 262144;
        public static final int TRANSLUCENT_FLAGS_CHANGED = 524288;
        public static final int EVERYTHING_CHANGED = -1;
        private int[] mCompatibilityParamsBackup;
        private CharSequence mTitle;

        public static boolean mayUseInputMethod(int flags) {
            switch (flags & 131080) {
                case 0:
                case 131080:
                    return true;
                default:
                    return false;
            }
        }

        public LayoutParams() {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = 2;
            this.format = -1;
        }

        public LayoutParams(int _type) {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = _type;
            this.format = -1;
        }

        public LayoutParams(int _type, int _flags) {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = _type;
            this.flags = _flags;
            this.format = -1;
        }

        public LayoutParams(int _type, int _flags, int _format) {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = _type;
            this.flags = _flags;
            this.format = _format;
        }

        public LayoutParams(int w, int h, int _type, int _flags, int _format) {
            super(w, h);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = _type;
            this.flags = _flags;
            this.format = _format;
        }

        public LayoutParams(int w, int h, int xpos, int ypos, int _type, int _flags, int _format) {
            super(w, h);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.x = xpos;
            this.y = ypos;
            this.type = _type;
            this.flags = _flags;
            this.format = _format;
        }

        public final void setTitle(CharSequence title) {
            if (null == title) {
                title = "";
            }
            this.mTitle = TextUtils.stringOrSpannedString(title);
        }

        public final CharSequence getTitle() {
            return this.mTitle;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int parcelableFlags) {
            out.writeInt(this.width);
            out.writeInt(this.height);
            out.writeInt(this.x);
            out.writeInt(this.y);
            out.writeInt(this.type);
            out.writeInt(this.flags);
            out.writeInt(this.privateFlags);
            out.writeInt(this.softInputMode);
            out.writeInt(this.gravity);
            out.writeFloat(this.horizontalMargin);
            out.writeFloat(this.verticalMargin);
            out.writeInt(this.format);
            out.writeInt(this.windowAnimations);
            out.writeFloat(this.alpha);
            out.writeFloat(this.dimAmount);
            out.writeFloat(this.screenBrightness);
            out.writeFloat(this.buttonBrightness);
            out.writeInt(this.rotationAnimation);
            out.writeStrongBinder(this.token);
            out.writeString(this.packageName);
            TextUtils.writeToParcel(this.mTitle, out, parcelableFlags);
            out.writeInt(this.screenOrientation);
            out.writeInt(this.systemUiVisibility);
            out.writeInt(this.subtreeSystemUiVisibility);
            out.writeInt(this.hasSystemUiListeners ? 1 : 0);
            out.writeInt(this.inputFeatures);
            out.writeLong(this.userActivityTimeout);
        }

        public LayoutParams(Parcel in) {
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.width = in.readInt();
            this.height = in.readInt();
            this.x = in.readInt();
            this.y = in.readInt();
            this.type = in.readInt();
            this.flags = in.readInt();
            this.privateFlags = in.readInt();
            this.softInputMode = in.readInt();
            this.gravity = in.readInt();
            this.horizontalMargin = in.readFloat();
            this.verticalMargin = in.readFloat();
            this.format = in.readInt();
            this.windowAnimations = in.readInt();
            this.alpha = in.readFloat();
            this.dimAmount = in.readFloat();
            this.screenBrightness = in.readFloat();
            this.buttonBrightness = in.readFloat();
            this.rotationAnimation = in.readInt();
            this.token = in.readStrongBinder();
            this.packageName = in.readString();
            this.mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.screenOrientation = in.readInt();
            this.systemUiVisibility = in.readInt();
            this.subtreeSystemUiVisibility = in.readInt();
            this.hasSystemUiListeners = in.readInt() != 0;
            this.inputFeatures = in.readInt();
            this.userActivityTimeout = in.readLong();
        }

        public final int copyFrom(LayoutParams o) {
            int changes = 0;
            if (this.width != o.width) {
                this.width = o.width;
                changes = 0 | 1;
            }
            if (this.height != o.height) {
                this.height = o.height;
                changes |= 1;
            }
            if (this.x != o.x) {
                this.x = o.x;
                changes |= 1;
            }
            if (this.y != o.y) {
                this.y = o.y;
                changes |= 1;
            }
            if (this.horizontalWeight != o.horizontalWeight) {
                this.horizontalWeight = o.horizontalWeight;
                changes |= 1;
            }
            if (this.verticalWeight != o.verticalWeight) {
                this.verticalWeight = o.verticalWeight;
                changes |= 1;
            }
            if (this.horizontalMargin != o.horizontalMargin) {
                this.horizontalMargin = o.horizontalMargin;
                changes |= 1;
            }
            if (this.verticalMargin != o.verticalMargin) {
                this.verticalMargin = o.verticalMargin;
                changes |= 1;
            }
            if (this.type != o.type) {
                this.type = o.type;
                changes |= 2;
            }
            if (this.flags != o.flags) {
                int diff = this.flags ^ o.flags;
                if ((diff & 201326592) != 0) {
                    changes |= 524288;
                }
                this.flags = o.flags;
                changes |= 4;
            }
            if (this.privateFlags != o.privateFlags) {
                this.privateFlags = o.privateFlags;
                changes |= 131072;
            }
            if (this.softInputMode != o.softInputMode) {
                this.softInputMode = o.softInputMode;
                changes |= 512;
            }
            if (this.gravity != o.gravity) {
                this.gravity = o.gravity;
                changes |= 1;
            }
            if (this.format != o.format) {
                this.format = o.format;
                changes |= 8;
            }
            if (this.windowAnimations != o.windowAnimations) {
                this.windowAnimations = o.windowAnimations;
                changes |= 16;
            }
            if (this.token == null) {
                this.token = o.token;
            }
            if (this.packageName == null) {
                this.packageName = o.packageName;
            }
            if (!this.mTitle.equals(o.mTitle)) {
                this.mTitle = o.mTitle;
                changes |= 64;
            }
            if (this.alpha != o.alpha) {
                this.alpha = o.alpha;
                changes |= 128;
            }
            if (this.dimAmount != o.dimAmount) {
                this.dimAmount = o.dimAmount;
                changes |= 32;
            }
            if (this.screenBrightness != o.screenBrightness) {
                this.screenBrightness = o.screenBrightness;
                changes |= 2048;
            }
            if (this.buttonBrightness != o.buttonBrightness) {
                this.buttonBrightness = o.buttonBrightness;
                changes |= 8192;
            }
            if (this.rotationAnimation != o.rotationAnimation) {
                this.rotationAnimation = o.rotationAnimation;
                changes |= 4096;
            }
            if (this.screenOrientation != o.screenOrientation) {
                this.screenOrientation = o.screenOrientation;
                changes |= 1024;
            }
            if (this.systemUiVisibility != o.systemUiVisibility || this.subtreeSystemUiVisibility != o.subtreeSystemUiVisibility) {
                this.systemUiVisibility = o.systemUiVisibility;
                this.subtreeSystemUiVisibility = o.subtreeSystemUiVisibility;
                changes |= 16384;
            }
            if (this.hasSystemUiListeners != o.hasSystemUiListeners) {
                this.hasSystemUiListeners = o.hasSystemUiListeners;
                changes |= 32768;
            }
            if (this.inputFeatures != o.inputFeatures) {
                this.inputFeatures = o.inputFeatures;
                changes |= 65536;
            }
            if (this.userActivityTimeout != o.userActivityTimeout) {
                this.userActivityTimeout = o.userActivityTimeout;
                changes |= 262144;
            }
            return changes;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String output) {
            Log.d("Debug", output + "Contents of " + this + Separators.COLON);
            String output2 = super.debug("");
            Log.d("Debug", output2);
            Log.d("Debug", "");
            Log.d("Debug", "WindowManager.LayoutParams={title=" + ((Object) this.mTitle) + "}");
            return "";
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(256);
            sb.append("WM.LayoutParams{");
            sb.append(Separators.LPAREN);
            sb.append(this.x);
            sb.append(',');
            sb.append(this.y);
            sb.append(")(");
            sb.append(this.width == -1 ? "fill" : this.width == -2 ? "wrap" : Integer.valueOf(this.width));
            sb.append('x');
            sb.append(this.height == -1 ? "fill" : this.height == -2 ? "wrap" : Integer.valueOf(this.height));
            sb.append(Separators.RPAREN);
            if (this.horizontalMargin != 0.0f) {
                sb.append(" hm=");
                sb.append(this.horizontalMargin);
            }
            if (this.verticalMargin != 0.0f) {
                sb.append(" vm=");
                sb.append(this.verticalMargin);
            }
            if (this.gravity != 0) {
                sb.append(" gr=#");
                sb.append(Integer.toHexString(this.gravity));
            }
            if (this.softInputMode != 0) {
                sb.append(" sim=#");
                sb.append(Integer.toHexString(this.softInputMode));
            }
            sb.append(" ty=");
            sb.append(this.type);
            sb.append(" fl=#");
            sb.append(Integer.toHexString(this.flags));
            if (this.privateFlags != 0) {
                if ((this.privateFlags & 128) != 0) {
                    sb.append(" compatible=true");
                }
                sb.append(" pfl=0x").append(Integer.toHexString(this.privateFlags));
            }
            if (this.format != -1) {
                sb.append(" fmt=");
                sb.append(this.format);
            }
            if (this.windowAnimations != 0) {
                sb.append(" wanim=0x");
                sb.append(Integer.toHexString(this.windowAnimations));
            }
            if (this.screenOrientation != -1) {
                sb.append(" or=");
                sb.append(this.screenOrientation);
            }
            if (this.alpha != 1.0f) {
                sb.append(" alpha=");
                sb.append(this.alpha);
            }
            if (this.screenBrightness != -1.0f) {
                sb.append(" sbrt=");
                sb.append(this.screenBrightness);
            }
            if (this.buttonBrightness != -1.0f) {
                sb.append(" bbrt=");
                sb.append(this.buttonBrightness);
            }
            if (this.rotationAnimation != 0) {
                sb.append(" rotAnim=");
                sb.append(this.rotationAnimation);
            }
            if (this.systemUiVisibility != 0) {
                sb.append(" sysui=0x");
                sb.append(Integer.toHexString(this.systemUiVisibility));
            }
            if (this.subtreeSystemUiVisibility != 0) {
                sb.append(" vsysui=0x");
                sb.append(Integer.toHexString(this.subtreeSystemUiVisibility));
            }
            if (this.hasSystemUiListeners) {
                sb.append(" sysuil=");
                sb.append(this.hasSystemUiListeners);
            }
            if (this.inputFeatures != 0) {
                sb.append(" if=0x").append(Integer.toHexString(this.inputFeatures));
            }
            if (this.userActivityTimeout >= 0) {
                sb.append(" userActivityTimeout=").append(this.userActivityTimeout);
            }
            sb.append('}');
            return sb.toString();
        }

        public void scale(float scale) {
            this.x = (int) ((this.x * scale) + 0.5f);
            this.y = (int) ((this.y * scale) + 0.5f);
            if (this.width > 0) {
                this.width = (int) ((this.width * scale) + 0.5f);
            }
            if (this.height > 0) {
                this.height = (int) ((this.height * scale) + 0.5f);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void backup() {
            int[] backup = this.mCompatibilityParamsBackup;
            if (backup == null) {
                int[] iArr = new int[4];
                this.mCompatibilityParamsBackup = iArr;
                backup = iArr;
            }
            backup[0] = this.x;
            backup[1] = this.y;
            backup[2] = this.width;
            backup[3] = this.height;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void restore() {
            int[] backup = this.mCompatibilityParamsBackup;
            if (backup != null) {
                this.x = backup[0];
                this.y = backup[1];
                this.width = backup[2];
                this.height = backup[3];
            }
        }
    }
}