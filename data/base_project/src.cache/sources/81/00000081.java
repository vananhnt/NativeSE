package android.animation;

/* loaded from: Keyframe.class */
public abstract class Keyframe implements Cloneable {
    float mFraction;
    Class mValueType;
    private TimeInterpolator mInterpolator = null;
    boolean mHasValue = false;

    public abstract Object getValue();

    public abstract void setValue(Object obj);

    @Override // 
    /* renamed from: clone */
    public abstract Keyframe mo9clone();

    public static Keyframe ofInt(float fraction, int value) {
        return new IntKeyframe(fraction, value);
    }

    public static Keyframe ofInt(float fraction) {
        return new IntKeyframe(fraction);
    }

    public static Keyframe ofFloat(float fraction, float value) {
        return new FloatKeyframe(fraction, value);
    }

    public static Keyframe ofFloat(float fraction) {
        return new FloatKeyframe(fraction);
    }

    public static Keyframe ofObject(float fraction, Object value) {
        return new ObjectKeyframe(fraction, value);
    }

    public static Keyframe ofObject(float fraction) {
        return new ObjectKeyframe(fraction, null);
    }

    public boolean hasValue() {
        return this.mHasValue;
    }

    public float getFraction() {
        return this.mFraction;
    }

    public void setFraction(float fraction) {
        this.mFraction = fraction;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public Class getType() {
        return this.mValueType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Keyframe$ObjectKeyframe.class */
    public static class ObjectKeyframe extends Keyframe {
        Object mValue;

        ObjectKeyframe(float fraction, Object value) {
            this.mFraction = fraction;
            this.mValue = value;
            this.mHasValue = value != null;
            this.mValueType = this.mHasValue ? value.getClass() : Object.class;
        }

        @Override // android.animation.Keyframe
        public Object getValue() {
            return this.mValue;
        }

        @Override // android.animation.Keyframe
        public void setValue(Object value) {
            this.mValue = value;
            this.mHasValue = value != null;
        }

        @Override // android.animation.Keyframe
        /* renamed from: clone */
        public ObjectKeyframe mo9clone() {
            ObjectKeyframe kfClone = new ObjectKeyframe(getFraction(), this.mHasValue ? this.mValue : null);
            kfClone.setInterpolator(getInterpolator());
            return kfClone;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Keyframe$IntKeyframe.class */
    public static class IntKeyframe extends Keyframe {
        int mValue;

        IntKeyframe(float fraction, int value) {
            this.mFraction = fraction;
            this.mValue = value;
            this.mValueType = Integer.TYPE;
            this.mHasValue = true;
        }

        IntKeyframe(float fraction) {
            this.mFraction = fraction;
            this.mValueType = Integer.TYPE;
        }

        public int getIntValue() {
            return this.mValue;
        }

        @Override // android.animation.Keyframe
        public Object getValue() {
            return Integer.valueOf(this.mValue);
        }

        @Override // android.animation.Keyframe
        public void setValue(Object value) {
            if (value != null && value.getClass() == Integer.class) {
                this.mValue = ((Integer) value).intValue();
                this.mHasValue = true;
            }
        }

        @Override // android.animation.Keyframe
        /* renamed from: clone */
        public IntKeyframe mo9clone() {
            IntKeyframe kfClone = this.mHasValue ? new IntKeyframe(getFraction(), this.mValue) : new IntKeyframe(getFraction());
            kfClone.setInterpolator(getInterpolator());
            return kfClone;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Keyframe$FloatKeyframe.class */
    public static class FloatKeyframe extends Keyframe {
        float mValue;

        FloatKeyframe(float fraction, float value) {
            this.mFraction = fraction;
            this.mValue = value;
            this.mValueType = Float.TYPE;
            this.mHasValue = true;
        }

        FloatKeyframe(float fraction) {
            this.mFraction = fraction;
            this.mValueType = Float.TYPE;
        }

        public float getFloatValue() {
            return this.mValue;
        }

        @Override // android.animation.Keyframe
        public Object getValue() {
            return Float.valueOf(this.mValue);
        }

        @Override // android.animation.Keyframe
        public void setValue(Object value) {
            if (value != null && value.getClass() == Float.class) {
                this.mValue = ((Float) value).floatValue();
                this.mHasValue = true;
            }
        }

        @Override // android.animation.Keyframe
        /* renamed from: clone */
        public FloatKeyframe mo9clone() {
            FloatKeyframe kfClone = this.mHasValue ? new FloatKeyframe(getFraction(), this.mValue) : new FloatKeyframe(getFraction());
            kfClone.setInterpolator(getInterpolator());
            return kfClone;
        }
    }
}