package android.inputmethodservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.internal.R;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuPopupHelper;

/* loaded from: ExtractEditLayout.class */
public class ExtractEditLayout extends LinearLayout {
    ExtractActionMode mActionMode;
    Button mExtractActionButton;
    Button mEditButton;

    public ExtractEditLayout(Context context) {
        super(context);
    }

    public ExtractEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ActionMode startActionModeForChild(View sourceView, ActionMode.Callback cb) {
        ExtractActionMode mode = new ExtractActionMode(cb);
        if (mode.dispatchOnCreate()) {
            mode.invalidate();
            this.mExtractActionButton.setVisibility(4);
            this.mEditButton.setVisibility(0);
            this.mActionMode = mode;
            sendAccessibilityEvent(32);
            return mode;
        }
        return null;
    }

    public boolean isActionModeStarted() {
        return this.mActionMode != null;
    }

    public void finishActionMode() {
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mExtractActionButton = (Button) findViewById(R.id.inputExtractAction);
        this.mEditButton = (Button) findViewById(R.id.inputExtractEditButton);
        this.mEditButton.setOnClickListener(new View.OnClickListener() { // from class: android.inputmethodservice.ExtractEditLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View clicked) {
                if (ExtractEditLayout.this.mActionMode != null) {
                    new MenuPopupHelper(ExtractEditLayout.this.getContext(), ExtractEditLayout.this.mActionMode.mMenu, clicked).show();
                }
            }
        });
    }

    /* loaded from: ExtractEditLayout$ExtractActionMode.class */
    private class ExtractActionMode extends ActionMode implements MenuBuilder.Callback {
        private ActionMode.Callback mCallback;
        MenuBuilder mMenu;

        public ExtractActionMode(ActionMode.Callback cb) {
            this.mMenu = new MenuBuilder(ExtractEditLayout.this.getContext());
            this.mMenu.setCallback(this);
            this.mCallback = cb;
        }

        @Override // android.view.ActionMode
        public void setTitle(CharSequence title) {
        }

        @Override // android.view.ActionMode
        public void setTitle(int resId) {
        }

        @Override // android.view.ActionMode
        public void setSubtitle(CharSequence subtitle) {
        }

        @Override // android.view.ActionMode
        public void setSubtitle(int resId) {
        }

        @Override // android.view.ActionMode
        public boolean isTitleOptional() {
            return true;
        }

        @Override // android.view.ActionMode
        public void setCustomView(View view) {
        }

        @Override // android.view.ActionMode
        public void invalidate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                this.mCallback.onPrepareActionMode(this, this.mMenu);
                this.mMenu.startDispatchingItemsChanged();
            } catch (Throwable th) {
                this.mMenu.startDispatchingItemsChanged();
                throw th;
            }
        }

        public boolean dispatchOnCreate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                boolean onCreateActionMode = this.mCallback.onCreateActionMode(this, this.mMenu);
                this.mMenu.startDispatchingItemsChanged();
                return onCreateActionMode;
            } catch (Throwable th) {
                this.mMenu.startDispatchingItemsChanged();
                throw th;
            }
        }

        @Override // android.view.ActionMode
        public void finish() {
            if (ExtractEditLayout.this.mActionMode != this) {
                return;
            }
            this.mCallback.onDestroyActionMode(this);
            this.mCallback = null;
            ExtractEditLayout.this.mExtractActionButton.setVisibility(0);
            ExtractEditLayout.this.mEditButton.setVisibility(4);
            ExtractEditLayout.this.sendAccessibilityEvent(32);
            ExtractEditLayout.this.mActionMode = null;
        }

        @Override // android.view.ActionMode
        public Menu getMenu() {
            return this.mMenu;
        }

        @Override // android.view.ActionMode
        public CharSequence getTitle() {
            return null;
        }

        @Override // android.view.ActionMode
        public CharSequence getSubtitle() {
            return null;
        }

        @Override // android.view.ActionMode
        public View getCustomView() {
            return null;
        }

        @Override // android.view.ActionMode
        public MenuInflater getMenuInflater() {
            return new MenuInflater(ExtractEditLayout.this.getContext());
        }

        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            if (this.mCallback != null) {
                return this.mCallback.onActionItemClicked(this, item);
            }
            return false;
        }

        public void onMenuModeChange(MenuBuilder menu) {
        }
    }
}