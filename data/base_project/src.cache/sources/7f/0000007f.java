package android.animation;

/* loaded from: IntEvaluator.class */
public class IntEvaluator implements TypeEvaluator<Integer> {
    @Override // android.animation.TypeEvaluator
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue.intValue();
        return Integer.valueOf((int) (startInt + (fraction * (endValue.intValue() - startInt))));
    }
}