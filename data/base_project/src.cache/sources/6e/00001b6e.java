package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/* loaded from: FaceUnlockView.class */
public class FaceUnlockView extends RelativeLayout {
    private static final String TAG = "FaceUnlockView";

    public FaceUnlockView(Context context) {
        this(context, null);
    }

    public FaceUnlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result;
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case Integer.MIN_VALUE:
                result = Math.max(specSize, desired);
                break;
            case 0:
                result = desired;
                break;
            case 1073741824:
            default:
                result = specSize;
                break;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.RelativeLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
        int chosenSize = Math.min(viewWidth, viewHeight);
        int newWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(chosenSize, Integer.MIN_VALUE);
        int newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(chosenSize, Integer.MIN_VALUE);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
    }
}