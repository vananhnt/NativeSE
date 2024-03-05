package android.animation;

/* loaded from: FloatEvaluator.class */
public class FloatEvaluator implements TypeEvaluator<Number> {
    @Override // android.animation.TypeEvaluator
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return Float.valueOf(startFloat + (fraction * (endValue.floatValue() - startFloat)));
    }
}