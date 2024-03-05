package android.support.v4.content.res;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

/* loaded from: ResourcesCompat.class */
public class ResourcesCompat {
    public Drawable getDrawable(Resources resources, int i, Resources.Theme theme) throws Resources.NotFoundException {
        return Build.VERSION.SDK_INT >= 21 ? ResourcesCompatApi21.getDrawable(resources, i, theme) : resources.getDrawable(i);
    }
}