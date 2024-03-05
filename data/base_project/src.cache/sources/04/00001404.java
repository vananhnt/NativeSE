package android.text.style;

import android.text.TextPaint;

/* loaded from: MetricAffectingSpan.class */
public abstract class MetricAffectingSpan extends CharacterStyle implements UpdateLayout {
    public abstract void updateMeasureState(TextPaint textPaint);

    @Override // android.text.style.CharacterStyle
    public MetricAffectingSpan getUnderlying() {
        return this;
    }

    /* loaded from: MetricAffectingSpan$Passthrough.class */
    static class Passthrough extends MetricAffectingSpan {
        private MetricAffectingSpan mStyle;

        public Passthrough(MetricAffectingSpan cs) {
            this.mStyle = cs;
        }

        @Override // android.text.style.CharacterStyle
        public void updateDrawState(TextPaint tp) {
            this.mStyle.updateDrawState(tp);
        }

        @Override // android.text.style.MetricAffectingSpan
        public void updateMeasureState(TextPaint tp) {
            this.mStyle.updateMeasureState(tp);
        }

        @Override // android.text.style.MetricAffectingSpan, android.text.style.CharacterStyle
        public MetricAffectingSpan getUnderlying() {
            return this.mStyle.getUnderlying();
        }
    }
}