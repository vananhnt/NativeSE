package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.appcompat.R;
import android.util.TypedValue;

/* loaded from: TintManager.class */
public class TintManager {
    private static final boolean DEBUG = false;
    private final Context mContext;
    private ColorStateList mDefaultColorStateList;
    private final Resources mResources;
    private ColorStateList mSwitchThumbStateList;
    private ColorStateList mSwitchTrackStateList;
    private final TypedValue mTypedValue = new TypedValue();
    private static final String TAG = TintManager.class.getSimpleName();
    static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    private static final int[] TINT_COLOR_CONTROL_NORMAL = {R.drawable.abc_ic_ab_back_mtrl_am_alpha, R.drawable.abc_ic_go_search_api_mtrl_alpha, R.drawable.abc_ic_search_api_mtrl_alpha, R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_ic_clear_mtrl_alpha, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha, R.drawable.abc_ic_voice_search_api_mtrl_alpha, R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_ACTIVATED = {R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha};
    private static final int[] TINT_COLOR_BACKGROUND_MULTIPLY = {R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult};
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST = {R.drawable.abc_edit_text_material, R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material, R.drawable.abc_spinner_mtrl_am_alpha, R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material};
    private static final int[] CONTAINERS_WITH_TINT_CHILDREN = {R.drawable.abc_cab_background_top_material};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TintManager$ColorFilterLruCache.class */
    public static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        public ColorFilterLruCache(int i) {
            super(i);
        }

        private static int generateCacheKey(int i, PorterDuff.Mode mode) {
            return (((1 * 31) + i) * 31) + mode.hashCode();
        }

        PorterDuffColorFilter get(int i, PorterDuff.Mode mode) {
            return get(Integer.valueOf(generateCacheKey(i, mode)));
        }

        PorterDuffColorFilter put(int i, PorterDuff.Mode mode, PorterDuffColorFilter porterDuffColorFilter) {
            return put(Integer.valueOf(generateCacheKey(i, mode)), porterDuffColorFilter);
        }
    }

    public TintManager(Context context) {
        this.mContext = context;
        this.mResources = new TintResources(context.getResources(), this);
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v9, types: [int[], int[][]] */
    private ColorStateList getDefaultColorStateList() {
        if (this.mDefaultColorStateList == null) {
            int themeAttrColor = getThemeAttrColor(R.attr.colorControlNormal);
            int themeAttrColor2 = getThemeAttrColor(R.attr.colorControlActivated);
            ?? r0 = new int[7];
            int[] iArr = new int[7];
            int[] iArr2 = new int[1];
            iArr2[0] = -16842910;
            r0[0] = iArr2;
            iArr[0] = getDisabledThemeAttrColor(R.attr.colorControlNormal);
            int i = 0 + 1;
            int[] iArr3 = new int[1];
            iArr3[0] = 16842908;
            r0[i] = iArr3;
            iArr[i] = themeAttrColor2;
            int i2 = i + 1;
            int[] iArr4 = new int[1];
            iArr4[0] = 16843518;
            r0[i2] = iArr4;
            iArr[i2] = themeAttrColor2;
            int i3 = i2 + 1;
            int[] iArr5 = new int[1];
            iArr5[0] = 16842919;
            r0[i3] = iArr5;
            iArr[i3] = themeAttrColor2;
            int i4 = i3 + 1;
            int[] iArr6 = new int[1];
            iArr6[0] = 16842912;
            r0[i4] = iArr6;
            iArr[i4] = themeAttrColor2;
            int i5 = i4 + 1;
            int[] iArr7 = new int[1];
            iArr7[0] = 16842913;
            r0[i5] = iArr7;
            iArr[i5] = themeAttrColor2;
            int i6 = i5 + 1;
            r0[i6] = new int[0];
            iArr[i6] = themeAttrColor;
            this.mDefaultColorStateList = new ColorStateList(r0, iArr);
        }
        return this.mDefaultColorStateList;
    }

    public static Drawable getDrawable(Context context, int i) {
        return isInTintList(i) ? new TintManager(context).getDrawable(i) : ContextCompat.getDrawable(context, i);
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [int[], int[][]] */
    private ColorStateList getSwitchThumbColorStateList() {
        if (this.mSwitchThumbStateList == null) {
            ?? r0 = new int[3];
            int[] iArr = new int[3];
            int[] iArr2 = new int[1];
            iArr2[0] = -16842910;
            r0[0] = iArr2;
            iArr[0] = getDisabledThemeAttrColor(R.attr.colorSwitchThumbNormal);
            int i = 0 + 1;
            int[] iArr3 = new int[1];
            iArr3[0] = 16842912;
            r0[i] = iArr3;
            iArr[i] = getThemeAttrColor(R.attr.colorControlActivated);
            int i2 = i + 1;
            r0[i2] = new int[0];
            iArr[i2] = getThemeAttrColor(R.attr.colorSwitchThumbNormal);
            this.mSwitchThumbStateList = new ColorStateList(r0, iArr);
        }
        return this.mSwitchThumbStateList;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [int[], int[][]] */
    private ColorStateList getSwitchTrackColorStateList() {
        if (this.mSwitchTrackStateList == null) {
            ?? r0 = new int[3];
            int[] iArr = new int[3];
            int[] iArr2 = new int[1];
            iArr2[0] = -16842910;
            r0[0] = iArr2;
            iArr[0] = getThemeAttrColor(16842800, 0.1f);
            int i = 0 + 1;
            int[] iArr3 = new int[1];
            iArr3[0] = 16842912;
            r0[i] = iArr3;
            iArr[i] = getThemeAttrColor(R.attr.colorControlActivated, 0.3f);
            int i2 = i + 1;
            r0[i2] = new int[0];
            iArr[i2] = getThemeAttrColor(16842800, 0.3f);
            this.mSwitchTrackStateList = new ColorStateList(r0, iArr);
        }
        return this.mSwitchTrackStateList;
    }

    private static boolean isInTintList(int i) {
        return arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, i) || arrayContains(TINT_COLOR_CONTROL_NORMAL, i) || arrayContains(TINT_COLOR_CONTROL_ACTIVATED, i) || arrayContains(TINT_COLOR_CONTROL_STATE_LIST, i) || arrayContains(CONTAINERS_WITH_TINT_CHILDREN, i);
    }

    int getDisabledThemeAttrColor(int i) {
        this.mContext.getTheme().resolveAttribute(16842803, this.mTypedValue, true);
        return getThemeAttrColor(i, this.mTypedValue.getFloat());
    }

    public Drawable getDrawable(int i) {
        TintDrawableWrapper drawable = ContextCompat.getDrawable(this.mContext, i);
        if (drawable != null) {
            if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, i)) {
                drawable = new TintDrawableWrapper(drawable, getDefaultColorStateList());
            } else if (i == R.drawable.abc_switch_track_mtrl_alpha) {
                drawable = new TintDrawableWrapper(drawable, getSwitchTrackColorStateList());
            } else if (i == R.drawable.abc_switch_thumb_material) {
                drawable = new TintDrawableWrapper(drawable, getSwitchThumbColorStateList(), PorterDuff.Mode.MULTIPLY);
            } else if (arrayContains(CONTAINERS_WITH_TINT_CHILDREN, i)) {
                drawable = this.mResources.getDrawable(i);
            } else {
                tintDrawable(i, drawable);
            }
        }
        return drawable;
    }

    int getThemeAttrColor(int i) {
        if (this.mContext.getTheme().resolveAttribute(i, this.mTypedValue, true)) {
            if (this.mTypedValue.type < 16 || this.mTypedValue.type > 31) {
                if (this.mTypedValue.type == 3) {
                    return this.mResources.getColor(this.mTypedValue.resourceId);
                }
                return 0;
            }
            return this.mTypedValue.data;
        }
        return 0;
    }

    int getThemeAttrColor(int i, float f) {
        int themeAttrColor = getThemeAttrColor(i);
        return (16777215 & themeAttrColor) | (Math.round(Color.alpha(themeAttrColor) * f) << 24);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tintDrawable(int i, Drawable drawable) {
        boolean z;
        PorterDuffColorFilter porterDuffColorFilter;
        PorterDuff.Mode mode = null;
        int i2 = 0;
        int i3 = -1;
        if (arrayContains(TINT_COLOR_CONTROL_NORMAL, i)) {
            i2 = R.attr.colorControlNormal;
            z = true;
        } else if (arrayContains(TINT_COLOR_CONTROL_ACTIVATED, i)) {
            i2 = R.attr.colorControlActivated;
            z = true;
        } else if (arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, i)) {
            i2 = 16842801;
            z = true;
            mode = PorterDuff.Mode.MULTIPLY;
        } else if (i == R.drawable.abc_list_divider_mtrl_alpha) {
            i2 = 16842800;
            z = true;
            i3 = Math.round(40.8f);
        } else {
            z = false;
        }
        if (z) {
            if (mode == null) {
                mode = DEFAULT_MODE;
            }
            int themeAttrColor = getThemeAttrColor(i2);
            PorterDuffColorFilter porterDuffColorFilter2 = COLOR_FILTER_CACHE.get(themeAttrColor, mode);
            if (porterDuffColorFilter2 == null) {
                PorterDuffColorFilter porterDuffColorFilter3 = new PorterDuffColorFilter(themeAttrColor, mode);
                COLOR_FILTER_CACHE.put(themeAttrColor, mode, porterDuffColorFilter3);
                porterDuffColorFilter = porterDuffColorFilter3;
            } else {
                porterDuffColorFilter = porterDuffColorFilter2;
            }
            drawable.setColorFilter(porterDuffColorFilter);
            if (i3 != -1) {
                drawable.setAlpha(i3);
            }
        }
    }
}