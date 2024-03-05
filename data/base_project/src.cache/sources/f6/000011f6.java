package android.support.v7.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: ActionBarActivity.class */
public class ActionBarActivity extends FragmentActivity implements ActionBar.Callback, TaskStackBuilder.SupportParentable, ActionBarDrawerToggle.DelegateProvider, ActionBarDrawerToggle.TmpDelegateProvider {
    private ActionBarActivityDelegate mDelegate;

    private ActionBarActivityDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = ActionBarActivityDelegate.createDelegate(this);
        }
        return this.mDelegate;
    }

    @Override // android.app.Activity
    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        getDelegate().addContentView(view, layoutParams);
    }

    @Override // android.support.v4.app.ActionBarDrawerToggle.DelegateProvider
    public final ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return getDelegate().getDrawerToggleDelegate();
    }

    @Override // android.app.Activity
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    @Override // android.support.v4.app.TaskStackBuilder.SupportParentable
    public Intent getSupportParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    @Override // android.support.v7.app.ActionBarDrawerToggle.TmpDelegateProvider
    @Nullable
    public ActionBarDrawerToggle.Delegate getV7DrawerToggleDelegate() {
        return getDelegate().getV7DrawerToggleDelegate();
    }

    @Override // android.app.Activity
    public void invalidateOptionsMenu() {
        getDelegate().supportInvalidateOptionsMenu();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (getDelegate().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        getDelegate().onConfigurationChanged(configuration);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public final void onContentChanged() {
        getDelegate().onContentChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getDelegate().onCreate(bundle);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return getDelegate().onCreatePanelMenu(i, menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public View onCreatePanelView(int i) {
        return i == 0 ? getDelegate().onCreatePanelView(i) : super.onCreatePanelView(i);
    }

    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder taskStackBuilder) {
        taskStackBuilder.addParentStack(this);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.LayoutInflater.Factory
    public View onCreateView(String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        View onCreateView = super.onCreateView(str, context, attributeSet);
        return onCreateView != null ? onCreateView : getDelegate().createView(str, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        getDelegate().destroy();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (super.onKeyDown(i, keyEvent)) {
            return true;
        }
        return getDelegate().onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        return getDelegate().onKeyShortcut(i, keyEvent);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public final boolean onMenuItemSelected(int i, MenuItem menuItem) {
        if (super.onMenuItemSelected(i, menuItem)) {
            return true;
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (menuItem.getItemId() != 16908332 || supportActionBar == null || (supportActionBar.getDisplayOptions() & 4) == 0) {
            return false;
        }
        return onSupportNavigateUp();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int i, Menu menu) {
        return getDelegate().onMenuOpened(i, menu);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public void onPanelClosed(int i, Menu menu) {
        getDelegate().onPanelClosed(i, menu);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity
    public boolean onPrepareOptionsPanel(View view, Menu menu) {
        return getDelegate().onPrepareOptionsPanel(view, menu);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.view.Window.Callback
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return getDelegate().onPreparePanel(i, view, menu);
    }

    public void onPrepareSupportNavigateUpTaskStack(TaskStackBuilder taskStackBuilder) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    public void onSupportActionModeFinished(ActionMode actionMode) {
    }

    public void onSupportActionModeStarted(ActionMode actionMode) {
    }

    public void onSupportContentChanged() {
    }

    public boolean onSupportNavigateUp() {
        Intent supportParentActivityIntent = getSupportParentActivityIntent();
        if (supportParentActivityIntent != null) {
            if (!supportShouldUpRecreateTask(supportParentActivityIntent)) {
                supportNavigateUpTo(supportParentActivityIntent);
                return true;
            }
            TaskStackBuilder create = TaskStackBuilder.create(this);
            onCreateSupportNavigateUpTaskStack(create);
            onPrepareSupportNavigateUpTaskStack(create);
            create.startActivities();
            try {
                ActivityCompat.finishAffinity(this);
                return true;
            } catch (IllegalStateException e) {
                finish();
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        getDelegate().onTitleChanged(charSequence);
    }

    @Override // android.app.Activity
    public void setContentView(int i) {
        getDelegate().setContentView(i);
    }

    @Override // android.app.Activity
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override // android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        getDelegate().setContentView(view, layoutParams);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    public void setSupportProgress(int i) {
        getDelegate().setSupportProgress(i);
    }

    public void setSupportProgressBarIndeterminate(boolean z) {
        getDelegate().setSupportProgressBarIndeterminate(z);
    }

    public void setSupportProgressBarIndeterminateVisibility(boolean z) {
        getDelegate().setSupportProgressBarIndeterminateVisibility(z);
    }

    public void setSupportProgressBarVisibility(boolean z) {
        getDelegate().setSupportProgressBarVisibility(z);
    }

    public ActionMode startSupportActionMode(ActionMode.Callback callback) {
        return getDelegate().startSupportActionMode(callback);
    }

    void superAddContentView(View view, ViewGroup.LayoutParams layoutParams) {
        super.addContentView(view, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean superOnCreatePanelMenu(int i, Menu menu) {
        return super.onCreatePanelMenu(i, menu);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean superOnMenuOpened(int i, Menu menu) {
        return super.onMenuOpened(i, menu);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void superOnPanelClosed(int i, Menu menu) {
        super.onPanelClosed(i, menu);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean superOnPrepareOptionsPanel(View view, Menu menu) {
        return super.onPrepareOptionsPanel(view, menu);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean superOnPreparePanel(int i, View view, Menu menu) {
        return super.onPreparePanel(i, view, menu);
    }

    void superSetContentView(int i) {
        super.setContentView(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void superSetContentView(View view) {
        super.setContentView(view);
    }

    void superSetContentView(View view, ViewGroup.LayoutParams layoutParams) {
        super.setContentView(view, layoutParams);
    }

    @Override // android.support.v4.app.FragmentActivity
    public void supportInvalidateOptionsMenu() {
        getDelegate().supportInvalidateOptionsMenu();
    }

    public void supportNavigateUpTo(Intent intent) {
        NavUtils.navigateUpTo(this, intent);
    }

    public boolean supportRequestWindowFeature(int i) {
        return getDelegate().supportRequestWindowFeature(i);
    }

    public boolean supportShouldUpRecreateTask(Intent intent) {
        return NavUtils.shouldUpRecreateTask(this, intent);
    }
}