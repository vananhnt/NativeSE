package android.support.v4.content.res;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/* loaded from: ResourcesCompatApi21.class */
class ResourcesCompatApi21 {
    ResourcesCompatApi21() {
    }

    public static Drawable getDrawable(Resources resources, int i, Resources.Theme theme) {
        return resources.getDrawable(i, theme);
    }
}