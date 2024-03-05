package android.animation;

/* loaded from: ArgbEvaluator.class */
public class ArgbEvaluator implements TypeEvaluator {
    @Override // android.animation.TypeEvaluator
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = ((Integer) startValue).intValue();
        int startA = (startInt >> 24) & 255;
        int startR = (startInt >> 16) & 255;
        int startG = (startInt >> 8) & 255;
        int startB = startInt & 255;
        int endInt = ((Integer) endValue).intValue();
        int endA = (endInt >> 24) & 255;
        int endR = (endInt >> 16) & 255;
        int endG = (endInt >> 8) & 255;
        int endB = endInt & 255;
        return Integer.valueOf(((startA + ((int) (fraction * (endA - startA)))) << 24) | ((startR + ((int) (fraction * (endR - startR)))) << 16) | ((startG + ((int) (fraction * (endG - startG)))) << 8) | (startB + ((int) (fraction * (endB - startB)))));
    }
}