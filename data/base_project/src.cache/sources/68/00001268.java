package android.support.v7.internal.widget;

import android.graphics.Outline;
import android.support.annotation.NonNull;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActionBarBackgroundDrawableV21.class */
public class ActionBarBackgroundDrawableV21 extends ActionBarBackgroundDrawable {
    public ActionBarBackgroundDrawableV21(ActionBarContainer actionBarContainer) {
        super(actionBarContainer);
    }

    public void getOutline(@NonNull Outline outline) {
        if (this.mContainer.mIsSplit) {
            if (this.mContainer.mSplitBackground != null) {
                this.mContainer.mSplitBackground.getOutline(outline);
            }
        } else if (this.mContainer.mBackground != null) {
            this.mContainer.mBackground.getOutline(outline);
        }
    }
}