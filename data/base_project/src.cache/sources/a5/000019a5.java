package com.android.internal.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioSystem;
import android.media.videoeditor.MediaProperties;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.AllCapsTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: PlatLogoActivity.class */
public class PlatLogoActivity extends Activity {
    FrameLayout mContent;
    int mCount;
    final Handler mHandler = new Handler();
    static final int BGCOLOR = -1237724;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Typeface bold = Typeface.create("sans-serif", 1);
        Typeface light = Typeface.create("sans-serif-light", 0);
        this.mContent = new FrameLayout(this);
        this.mContent.setBackgroundColor(AudioSystem.DEVICE_IN_DEFAULT);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = 17;
        final ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.platlogo);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        logo.setVisibility(4);
        final View bg = new View(this);
        bg.setBackgroundColor(BGCOLOR);
        bg.setAlpha(0.0f);
        final TextView letter = new TextView(this);
        letter.setTypeface(bold);
        letter.setTextSize(300.0f);
        letter.setTextColor(-1);
        letter.setGravity(17);
        letter.setText(String.valueOf(Build.ID).substring(0, 1));
        int p = (int) (4.0f * metrics.density);
        final TextView tv = new TextView(this);
        if (light != null) {
            tv.setTypeface(light);
        }
        tv.setTextSize(30.0f);
        tv.setPadding(p, p, p, p);
        tv.setTextColor(-1);
        tv.setGravity(17);
        tv.setTransformationMethod(new AllCapsTransformationMethod(this));
        tv.setText("Android " + Build.VERSION.RELEASE);
        tv.setVisibility(4);
        this.mContent.addView(bg);
        this.mContent.addView(letter, lp);
        this.mContent.addView(logo, lp);
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(lp);
        lp2.gravity = 81;
        lp2.bottomMargin = 10 * p;
        this.mContent.addView(tv, lp2);
        this.mContent.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.PlatLogoActivity.1
            int clicks;

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                this.clicks++;
                if (this.clicks >= 6) {
                    PlatLogoActivity.this.mContent.performLongClick();
                    return;
                }
                letter.animate().cancel();
                float offset = ((int) letter.getRotation()) % MediaProperties.HEIGHT_360;
                letter.animate().rotationBy((Math.random() > 0.5d ? MediaProperties.HEIGHT_360 : -360) - offset).setInterpolator(new DecelerateInterpolator()).setDuration(700L).start();
            }
        });
        this.mContent.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.internal.app.PlatLogoActivity.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                if (logo.getVisibility() != 0) {
                    bg.setScaleX(0.01f);
                    bg.animate().alpha(1.0f).scaleX(1.0f).setStartDelay(500L).start();
                    letter.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f).rotationBy(360.0f).setInterpolator(new AccelerateInterpolator()).setDuration(1000L).start();
                    logo.setAlpha(0.0f);
                    logo.setVisibility(0);
                    logo.setScaleX(0.5f);
                    logo.setScaleY(0.5f);
                    logo.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(1000L).setStartDelay(500L).setInterpolator(new AnticipateOvershootInterpolator()).start();
                    tv.setAlpha(0.0f);
                    tv.setVisibility(0);
                    tv.animate().alpha(1.0f).setDuration(1000L).setStartDelay(1000L).start();
                    return true;
                }
                return false;
            }
        });
        logo.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.internal.app.PlatLogoActivity.3
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                if (Settings.System.getLong(PlatLogoActivity.this.getContentResolver(), Settings.System.EGG_MODE, 0L) == 0) {
                    Settings.System.putLong(PlatLogoActivity.this.getContentResolver(), Settings.System.EGG_MODE, System.currentTimeMillis());
                }
                try {
                    PlatLogoActivity.this.startActivity(new Intent(Intent.ACTION_MAIN).setFlags(276856832).addCategory("com.android.internal.category.PLATLOGO"));
                } catch (ActivityNotFoundException e) {
                    Log.e("PlatLogoActivity", "Couldn't catch a break.");
                }
                PlatLogoActivity.this.finish();
                return true;
            }
        });
        setContentView(this.mContent);
    }
}