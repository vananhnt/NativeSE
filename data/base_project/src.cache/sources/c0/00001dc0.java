package com.android.server.am;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: LaunchWarningWindow.class */
public final class LaunchWarningWindow extends Dialog {
    public LaunchWarningWindow(Context context, ActivityRecord cur, ActivityRecord next) {
        super(context, R.style.Theme_Toast);
        requestWindowFeature(3);
        getWindow().setType(2003);
        getWindow().addFlags(24);
        setContentView(R.layout.launch_warning);
        setTitle(context.getText(R.string.launch_warning_title));
        TypedValue out = new TypedValue();
        getContext().getTheme().resolveAttribute(16843605, out, true);
        getWindow().setFeatureDrawableResource(3, out.resourceId);
        ImageView icon = (ImageView) findViewById(R.id.replace_app_icon);
        icon.setImageDrawable(next.info.applicationInfo.loadIcon(context.getPackageManager()));
        TextView text = (TextView) findViewById(R.id.replace_message);
        text.setText(context.getResources().getString(R.string.launch_warning_replace, next.info.applicationInfo.loadLabel(context.getPackageManager()).toString()));
        ImageView icon2 = (ImageView) findViewById(R.id.original_app_icon);
        icon2.setImageDrawable(cur.info.applicationInfo.loadIcon(context.getPackageManager()));
        TextView text2 = (TextView) findViewById(R.id.original_message);
        text2.setText(context.getResources().getString(R.string.launch_warning_original, cur.info.applicationInfo.loadLabel(context.getPackageManager()).toString()));
    }
}