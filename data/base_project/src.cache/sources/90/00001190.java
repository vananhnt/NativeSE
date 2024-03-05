package android.support.v4.widget;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

/* loaded from: DrawerLayoutCompatApi21.class */
class DrawerLayoutCompatApi21 {

    /* loaded from: DrawerLayoutCompatApi21$InsetsListener.class */
    static class InsetsListener implements View.OnApplyWindowInsetsListener {
        InsetsListener() {
        }

        @Override // android.view.View.OnApplyWindowInsetsListener
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            ((DrawerLayoutImpl) view).setChildInsets(windowInsets, windowInsets.getSystemWindowInsetTop() > 0);
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    DrawerLayoutCompatApi21() {
    }

    public static void applyMarginInsets(ViewGroup.MarginLayoutParams marginLayoutParams, Object obj, int i) {
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
        } else if (i == 5) {
            windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        marginLayoutParams.leftMargin = windowInsets.getSystemWindowInsetLeft();
        marginLayoutParams.topMargin = windowInsets.getSystemWindowInsetTop();
        marginLayoutParams.rightMargin = windowInsets.getSystemWindowInsetRight();
        marginLayoutParams.bottomMargin = windowInsets.getSystemWindowInsetBottom();
    }

    public static void configureApplyInsets(View view) {
        if (view instanceof DrawerLayoutImpl) {
            view.setOnApplyWindowInsetsListener(new InsetsListener());
            view.setSystemUiVisibility(1280);
        }
    }

    public static void dispatchChildInsets(View view, Object obj, int i) {
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
        } else if (i == 5) {
            windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        view.dispatchApplyWindowInsets(windowInsets);
    }

    public static int getTopInset(Object obj) {
        return obj != null ? ((WindowInsets) obj).getSystemWindowInsetTop() : 0;
    }
}