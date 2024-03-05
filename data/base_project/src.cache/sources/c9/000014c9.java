package android.view;

import android.graphics.drawable.Drawable;

/* loaded from: ContextMenu.class */
public interface ContextMenu extends Menu {

    /* loaded from: ContextMenu$ContextMenuInfo.class */
    public interface ContextMenuInfo {
    }

    ContextMenu setHeaderTitle(int i);

    ContextMenu setHeaderTitle(CharSequence charSequence);

    ContextMenu setHeaderIcon(int i);

    ContextMenu setHeaderIcon(Drawable drawable);

    ContextMenu setHeaderView(View view);

    void clearHeader();
}