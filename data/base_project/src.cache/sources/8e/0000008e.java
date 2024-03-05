package android.animation;

import android.animation.ValueAnimator;
import android.util.Property;
import gov.nist.core.Separators;

/* loaded from: ObjectAnimator.class */
public final class ObjectAnimator extends ValueAnimator {
    private static final boolean DBG = false;
    private Object mTarget;
    private String mPropertyName;
    private Property mProperty;
    private boolean mAutoCancel = false;

    public void setPropertyName(String propertyName) {
        if (this.mValues != null) {
            PropertyValuesHolder valuesHolder = this.mValues[0];
            String oldName = valuesHolder.getPropertyName();
            valuesHolder.setPropertyName(propertyName);
            this.mValuesMap.remove(oldName);
            this.mValuesMap.put(propertyName, valuesHolder);
        }
        this.mPropertyName = propertyName;
        this.mInitialized = false;
    }

    public void setProperty(Property property) {
        if (this.mValues != null) {
            PropertyValuesHolder valuesHolder = this.mValues[0];
            String oldName = valuesHolder.getPropertyName();
            valuesHolder.setProperty(property);
            this.mValuesMap.remove(oldName);
            this.mValuesMap.put(this.mPropertyName, valuesHolder);
        }
        if (this.mProperty != null) {
            this.mPropertyName = property.getName();
        }
        this.mProperty = property;
        this.mInitialized = false;
    }

    public String getPropertyName() {
        String propertyName;
        String propertyName2 = null;
        if (this.mPropertyName != null) {
            propertyName2 = this.mPropertyName;
        } else if (this.mProperty != null) {
            propertyName2 = this.mProperty.getName();
        } else if (this.mValues != null && this.mValues.length > 0) {
            for (int i = 0; i < this.mValues.length; i++) {
                if (i == 0) {
                    propertyName = "";
                } else {
                    propertyName = propertyName2 + Separators.COMMA;
                }
                propertyName2 = propertyName + this.mValues[i].getPropertyName();
            }
        }
        return propertyName2;
    }

    @Override // android.animation.ValueAnimator
    String getNameForTrace() {
        return "animator:" + getPropertyName();
    }

    public ObjectAnimator() {
    }

    private ObjectAnimator(Object target, String propertyName) {
        this.mTarget = target;
        setPropertyName(propertyName);
    }

    private <T> ObjectAnimator(T target, Property<T, ?> property) {
        this.mTarget = target;
        setProperty(property);
    }

    public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setIntValues(values);
        return anim;
    }

    public static <T> ObjectAnimator ofInt(T target, Property<T, Integer> property, int... values) {
        ObjectAnimator anim = new ObjectAnimator(target, property);
        anim.setIntValues(values);
        return anim;
    }

    public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setFloatValues(values);
        return anim;
    }

    public static <T> ObjectAnimator ofFloat(T target, Property<T, Float> property, float... values) {
        ObjectAnimator anim = new ObjectAnimator(target, property);
        anim.setFloatValues(values);
        return anim;
    }

    public static ObjectAnimator ofObject(Object target, String propertyName, TypeEvaluator evaluator, Object... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }

    public static <T, V> ObjectAnimator ofObject(T target, Property<T, V> property, TypeEvaluator<V> evaluator, V... values) {
        ObjectAnimator anim = new ObjectAnimator(target, property);
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object target, PropertyValuesHolder... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.mTarget = target;
        anim.setValues(values);
        return anim;
    }

    @Override // android.animation.ValueAnimator
    public void setIntValues(int... values) {
        if (this.mValues == null || this.mValues.length == 0) {
            if (this.mProperty != null) {
                setValues(PropertyValuesHolder.ofInt(this.mProperty, values));
                return;
            } else {
                setValues(PropertyValuesHolder.ofInt(this.mPropertyName, values));
                return;
            }
        }
        super.setIntValues(values);
    }

    @Override // android.animation.ValueAnimator
    public void setFloatValues(float... values) {
        if (this.mValues == null || this.mValues.length == 0) {
            if (this.mProperty != null) {
                setValues(PropertyValuesHolder.ofFloat(this.mProperty, values));
                return;
            } else {
                setValues(PropertyValuesHolder.ofFloat(this.mPropertyName, values));
                return;
            }
        }
        super.setFloatValues(values);
    }

    @Override // android.animation.ValueAnimator
    public void setObjectValues(Object... values) {
        if (this.mValues == null || this.mValues.length == 0) {
            if (this.mProperty != null) {
                setValues(PropertyValuesHolder.ofObject(this.mProperty, (TypeEvaluator) null, values));
                return;
            } else {
                setValues(PropertyValuesHolder.ofObject(this.mPropertyName, (TypeEvaluator) null, values));
                return;
            }
        }
        super.setObjectValues(values);
    }

    public void setAutoCancel(boolean cancel) {
        this.mAutoCancel = cancel;
    }

    private boolean hasSameTargetAndProperties(Animator anim) {
        if (anim instanceof ObjectAnimator) {
            PropertyValuesHolder[] theirValues = ((ObjectAnimator) anim).getValues();
            if (((ObjectAnimator) anim).getTarget() == this.mTarget && this.mValues.length == theirValues.length) {
                for (int i = 0; i < this.mValues.length; i++) {
                    PropertyValuesHolder pvhMine = this.mValues[i];
                    PropertyValuesHolder pvhTheirs = theirValues[i];
                    if (pvhMine.getPropertyName() == null || !pvhMine.getPropertyName().equals(pvhTheirs.getPropertyName())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void start() {
        ValueAnimator.AnimationHandler handler = sAnimationHandler.get();
        if (handler != null) {
            int numAnims = handler.mAnimations.size();
            for (int i = numAnims - 1; i >= 0; i--) {
                if (handler.mAnimations.get(i) instanceof ObjectAnimator) {
                    ObjectAnimator anim = (ObjectAnimator) handler.mAnimations.get(i);
                    if (anim.mAutoCancel && hasSameTargetAndProperties(anim)) {
                        anim.cancel();
                    }
                }
            }
            int numAnims2 = handler.mPendingAnimations.size();
            for (int i2 = numAnims2 - 1; i2 >= 0; i2--) {
                if (handler.mPendingAnimations.get(i2) instanceof ObjectAnimator) {
                    ObjectAnimator anim2 = (ObjectAnimator) handler.mPendingAnimations.get(i2);
                    if (anim2.mAutoCancel && hasSameTargetAndProperties(anim2)) {
                        anim2.cancel();
                    }
                }
            }
            int numAnims3 = handler.mDelayedAnims.size();
            for (int i3 = numAnims3 - 1; i3 >= 0; i3--) {
                if (handler.mDelayedAnims.get(i3) instanceof ObjectAnimator) {
                    ObjectAnimator anim3 = (ObjectAnimator) handler.mDelayedAnims.get(i3);
                    if (anim3.mAutoCancel && hasSameTargetAndProperties(anim3)) {
                        anim3.cancel();
                    }
                }
            }
        }
        super.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator
    public void initAnimation() {
        if (!this.mInitialized) {
            int numValues = this.mValues.length;
            for (int i = 0; i < numValues; i++) {
                this.mValues[i].setupSetterAndGetter(this.mTarget);
            }
            super.initAnimation();
        }
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public ObjectAnimator setDuration(long duration) {
        super.setDuration(duration);
        return this;
    }

    public Object getTarget() {
        return this.mTarget;
    }

    @Override // android.animation.Animator
    public void setTarget(Object target) {
        if (this.mTarget != target) {
            Object oldTarget = this.mTarget;
            this.mTarget = target;
            if (oldTarget != null && target != null && oldTarget.getClass() == target.getClass()) {
                return;
            }
            this.mInitialized = false;
        }
    }

    @Override // android.animation.Animator
    public void setupStartValues() {
        initAnimation();
        int numValues = this.mValues.length;
        for (int i = 0; i < numValues; i++) {
            this.mValues[i].setupStartValue(this.mTarget);
        }
    }

    @Override // android.animation.Animator
    public void setupEndValues() {
        initAnimation();
        int numValues = this.mValues.length;
        for (int i = 0; i < numValues; i++) {
            this.mValues[i].setupEndValue(this.mTarget);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator
    public void animateValue(float fraction) {
        super.animateValue(fraction);
        int numValues = this.mValues.length;
        for (int i = 0; i < numValues; i++) {
            this.mValues[i].setAnimatedValue(this.mTarget);
        }
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    /* renamed from: clone */
    public ObjectAnimator mo6clone() {
        ObjectAnimator anim = (ObjectAnimator) super.mo6clone();
        return anim;
    }

    @Override // android.animation.ValueAnimator
    public String toString() {
        String returnVal = "ObjectAnimator@" + Integer.toHexString(hashCode()) + ", target " + this.mTarget;
        if (this.mValues != null) {
            for (int i = 0; i < this.mValues.length; i++) {
                returnVal = returnVal + "\n    " + this.mValues[i].toString();
            }
        }
        return returnVal;
    }
}