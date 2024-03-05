package com.android.server.am;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.internal.R;

/* loaded from: CompatModeDialog.class */
public final class CompatModeDialog extends Dialog {
    final ActivityManagerService mService;
    final ApplicationInfo mAppInfo;
    final Switch mCompatEnabled;
    final CheckBox mAlwaysShow;
    final View mHint;

    public CompatModeDialog(ActivityManagerService service, Context context, ApplicationInfo appInfo) {
        super(context, 16973936);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().requestFeature(1);
        getWindow().setType(2002);
        getWindow().setGravity(81);
        this.mService = service;
        this.mAppInfo = appInfo;
        setContentView(R.layout.am_compat_mode_dialog);
        this.mCompatEnabled = (Switch) findViewById(R.id.compat_checkbox);
        this.mCompatEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.server.am.CompatModeDialog.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (CompatModeDialog.this.mService) {
                    CompatModeDialog.this.mService.mCompatModePackages.setPackageScreenCompatModeLocked(CompatModeDialog.this.mAppInfo.packageName, CompatModeDialog.this.mCompatEnabled.isChecked() ? 1 : 0);
                    CompatModeDialog.this.updateControls();
                }
            }
        });
        this.mAlwaysShow = (CheckBox) findViewById(R.id.ask_checkbox);
        this.mAlwaysShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.server.am.CompatModeDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (CompatModeDialog.this.mService) {
                    CompatModeDialog.this.mService.mCompatModePackages.setPackageAskCompatModeLocked(CompatModeDialog.this.mAppInfo.packageName, CompatModeDialog.this.mAlwaysShow.isChecked());
                    CompatModeDialog.this.updateControls();
                }
            }
        });
        this.mHint = findViewById(R.id.reask_hint);
        updateControls();
    }

    void updateControls() {
        synchronized (this.mService) {
            int mode = this.mService.mCompatModePackages.computeCompatModeLocked(this.mAppInfo);
            this.mCompatEnabled.setChecked(mode == 1);
            boolean ask = this.mService.mCompatModePackages.getPackageAskCompatModeLocked(this.mAppInfo.packageName);
            this.mAlwaysShow.setChecked(ask);
            this.mHint.setVisibility(ask ? 4 : 0);
        }
    }
}