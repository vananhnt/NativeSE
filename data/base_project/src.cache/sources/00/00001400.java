package android.text.style;

import android.graphics.Paint;
import android.text.TextPaint;

/* loaded from: LineHeightSpan.class */
public interface LineHeightSpan extends ParagraphStyle, WrapTogetherSpan {

    /* loaded from: LineHeightSpan$WithDensity.class */
    public interface WithDensity extends LineHeightSpan {
        void chooseHeight(CharSequence charSequence, int i, int i2, int i3, int i4, Paint.FontMetricsInt fontMetricsInt, TextPaint textPaint);
    }

    void chooseHeight(CharSequence charSequence, int i, int i2, int i3, int i4, Paint.FontMetricsInt fontMetricsInt);
}