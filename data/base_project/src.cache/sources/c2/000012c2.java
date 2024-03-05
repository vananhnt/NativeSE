package android.support.v7.internal.widget;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: ViewUtils.class */
public class ViewUtils {
    private static final String TAG = "ViewUtils";
    private static Method sComputeFitSystemWindowsMethod;

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                sComputeFitSystemWindowsMethod = View.class.getDeclaredMethod("computeFitSystemWindows", Rect.class, Rect.class);
                if (sComputeFitSystemWindowsMethod.isAccessible()) {
                    return;
                }
                sComputeFitSystemWindowsMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "Could not find method computeFitSystemWindows. Oh well.");
            }
        }
    }

    private ViewUtils() {
    }

    public static int combineMeasuredStates(int i, int i2) {
        return i | i2;
    }

    public static void computeFitSystemWindows(View view, Rect rect, Rect rect2) {
        Method method = sComputeFitSystemWindowsMethod;
        if (method != null) {
            try {
                method.invoke(view, rect, rect2);
            } catch (Exception e) {
                Log.d(TAG, "Could not invoke computeFitSystemWindows", e);
            }
        }
    }

    public static boolean isLayoutRtl(View view) {
        boolean z = true;
        if (ViewCompat.getLayoutDirection(view) != 1) {
            z = false;
        }
        return z;
    }

    public static void makeOptionalFitsSystemWindows(View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                Method method = view.getClass().getMethod("makeOptionalFitsSystemWindows", new Class[0]);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(view, new Object[0]);
            } catch (IllegalAccessException e) {
                Log.d(TAG, "Could not invoke makeOptionalFitsSystemWindows", e);
            } catch (NoSuchMethodException e2) {
                Log.d(TAG, "Could not find method makeOptionalFitsSystemWindows. Oh well...");
            } catch (InvocationTargetException e3) {
                Log.d(TAG, "Could not invoke makeOptionalFitsSystemWindows", e3);
            }
        }
    }
}