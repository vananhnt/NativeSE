package android.support.v7.internal.widget;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/* loaded from: TintResources.class */
class TintResources extends Resources {
    private final TintManager mTintManager;

    public TintResources(Resources resources, TintManager tintManager) {
        super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
        this.mTintManager = tintManager;
    }

    @Override // android.content.res.Resources
    public Drawable getDrawable(int i) throws Resources.NotFoundException {
        Drawable drawable = super.getDrawable(i);
        if (drawable != null) {
            this.mTintManager.tintDrawable(i, drawable);
        }
        return drawable;
    }
}