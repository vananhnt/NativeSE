package android.animation;

import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.Property;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: PropertyValuesHolder.class */
public class PropertyValuesHolder implements Cloneable {
    String mPropertyName;
    protected Property mProperty;
    Method mSetter;
    private Method mGetter;
    Class mValueType;
    KeyframeSet mKeyframeSet;
    private static final TypeEvaluator sIntEvaluator = new IntEvaluator();
    private static final TypeEvaluator sFloatEvaluator = new FloatEvaluator();
    private static Class[] FLOAT_VARIANTS = {Float.TYPE, Float.class, Double.TYPE, Integer.TYPE, Double.class, Integer.class};
    private static Class[] INTEGER_VARIANTS = {Integer.TYPE, Integer.class, Float.TYPE, Double.TYPE, Float.class, Double.class};
    private static Class[] DOUBLE_VARIANTS = {Double.TYPE, Double.class, Float.TYPE, Integer.TYPE, Float.class, Integer.class};
    private static final HashMap<Class, HashMap<String, Method>> sSetterPropertyMap = new HashMap<>();
    private static final HashMap<Class, HashMap<String, Method>> sGetterPropertyMap = new HashMap<>();
    final ReentrantReadWriteLock mPropertyMapLock;
    final Object[] mTmpValueArray;
    private TypeEvaluator mEvaluator;
    private Object mAnimatedValue;

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nGetIntMethod(Class cls, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nGetFloatMethod(Class cls, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallIntMethod(Object obj, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nCallFloatMethod(Object obj, int i, float f);

    private PropertyValuesHolder(String propertyName) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframeSet = null;
        this.mPropertyMapLock = new ReentrantReadWriteLock();
        this.mTmpValueArray = new Object[1];
        this.mPropertyName = propertyName;
    }

    private PropertyValuesHolder(Property property) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframeSet = null;
        this.mPropertyMapLock = new ReentrantReadWriteLock();
        this.mTmpValueArray = new Object[1];
        this.mProperty = property;
        if (property != null) {
            this.mPropertyName = property.getName();
        }
    }

    public static PropertyValuesHolder ofInt(String propertyName, int... values) {
        return new IntPropertyValuesHolder(propertyName, values);
    }

    public static PropertyValuesHolder ofInt(Property<?, Integer> property, int... values) {
        return new IntPropertyValuesHolder(property, values);
    }

    public static PropertyValuesHolder ofFloat(String propertyName, float... values) {
        return new FloatPropertyValuesHolder(propertyName, values);
    }

    public static PropertyValuesHolder ofFloat(Property<?, Float> property, float... values) {
        return new FloatPropertyValuesHolder(property, values);
    }

    public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator, Object... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    public static <V> PropertyValuesHolder ofObject(Property property, TypeEvaluator<V> evaluator, V... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values) {
        KeyframeSet keyframeSet = KeyframeSet.ofKeyframe(values);
        if (keyframeSet instanceof IntKeyframeSet) {
            return new IntPropertyValuesHolder(propertyName, (IntKeyframeSet) keyframeSet);
        }
        if (keyframeSet instanceof FloatKeyframeSet) {
            return new FloatPropertyValuesHolder(propertyName, (FloatKeyframeSet) keyframeSet);
        }
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mKeyframeSet = keyframeSet;
        pvh.mValueType = values[0].getType();
        return pvh;
    }

    public static PropertyValuesHolder ofKeyframe(Property property, Keyframe... values) {
        KeyframeSet keyframeSet = KeyframeSet.ofKeyframe(values);
        if (keyframeSet instanceof IntKeyframeSet) {
            return new IntPropertyValuesHolder(property, (IntKeyframeSet) keyframeSet);
        }
        if (keyframeSet instanceof FloatKeyframeSet) {
            return new FloatPropertyValuesHolder(property, (FloatKeyframeSet) keyframeSet);
        }
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.mKeyframeSet = keyframeSet;
        pvh.mValueType = values[0].getType();
        return pvh;
    }

    public void setIntValues(int... values) {
        this.mValueType = Integer.TYPE;
        this.mKeyframeSet = KeyframeSet.ofInt(values);
    }

    public void setFloatValues(float... values) {
        this.mValueType = Float.TYPE;
        this.mKeyframeSet = KeyframeSet.ofFloat(values);
    }

    public void setKeyframes(Keyframe... values) {
        int numKeyframes = values.length;
        Keyframe[] keyframes = new Keyframe[Math.max(numKeyframes, 2)];
        this.mValueType = values[0].getType();
        for (int i = 0; i < numKeyframes; i++) {
            keyframes[i] = values[i];
        }
        this.mKeyframeSet = new KeyframeSet(keyframes);
    }

    public void setObjectValues(Object... values) {
        this.mValueType = values[0].getClass();
        this.mKeyframeSet = KeyframeSet.ofObject(values);
    }

    private Method getPropertyFunction(Class targetClass, String prefix, Class valueType) {
        Class[] typeVariants;
        Method returnVal = null;
        String methodName = getMethodName(prefix, this.mPropertyName);
        if (valueType == null) {
            try {
                returnVal = targetClass.getMethod(methodName, null);
            } catch (NoSuchMethodException e) {
            }
        } else {
            Class[] args = new Class[1];
            if (this.mValueType.equals(Float.class)) {
                typeVariants = FLOAT_VARIANTS;
            } else if (this.mValueType.equals(Integer.class)) {
                typeVariants = INTEGER_VARIANTS;
            } else {
                typeVariants = this.mValueType.equals(Double.class) ? DOUBLE_VARIANTS : new Class[]{this.mValueType};
            }
            Class[] arr$ = typeVariants;
            for (Class typeVariant : arr$) {
                args[0] = typeVariant;
                try {
                    returnVal = targetClass.getMethod(methodName, args);
                    this.mValueType = typeVariant;
                    return returnVal;
                } catch (NoSuchMethodException e2) {
                }
            }
        }
        if (returnVal == null) {
            Log.w("PropertyValuesHolder", "Method " + getMethodName(prefix, this.mPropertyName) + "() with type " + this.mValueType + " not found on target class " + targetClass);
        }
        return returnVal;
    }

    private Method setupSetterOrGetter(Class targetClass, HashMap<Class, HashMap<String, Method>> propertyMapMap, String prefix, Class valueType) {
        Method setterOrGetter = null;
        try {
            this.mPropertyMapLock.writeLock().lock();
            HashMap<String, Method> propertyMap = propertyMapMap.get(targetClass);
            if (propertyMap != null) {
                setterOrGetter = propertyMap.get(this.mPropertyName);
            }
            if (setterOrGetter == null) {
                setterOrGetter = getPropertyFunction(targetClass, prefix, valueType);
                if (propertyMap == null) {
                    propertyMap = new HashMap<>();
                    propertyMapMap.put(targetClass, propertyMap);
                }
                propertyMap.put(this.mPropertyName, setterOrGetter);
            }
            return setterOrGetter;
        } finally {
            this.mPropertyMapLock.writeLock().unlock();
        }
    }

    void setupSetter(Class targetClass) {
        this.mSetter = setupSetterOrGetter(targetClass, sSetterPropertyMap, "set", this.mValueType);
    }

    private void setupGetter(Class targetClass) {
        this.mGetter = setupSetterOrGetter(targetClass, sGetterPropertyMap, "get", null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupSetterAndGetter(Object target) {
        if (this.mProperty != null) {
            try {
                this.mProperty.get(target);
                Iterator i$ = this.mKeyframeSet.mKeyframes.iterator();
                while (i$.hasNext()) {
                    Keyframe kf = i$.next();
                    if (!kf.hasValue()) {
                        kf.setValue(this.mProperty.get(target));
                    }
                }
                return;
            } catch (ClassCastException e) {
                Log.w("PropertyValuesHolder", "No such property (" + this.mProperty.getName() + ") on target object " + target + ". Trying reflection instead");
                this.mProperty = null;
            }
        }
        Class targetClass = target.getClass();
        if (this.mSetter == null) {
            setupSetter(targetClass);
        }
        Iterator i$2 = this.mKeyframeSet.mKeyframes.iterator();
        while (i$2.hasNext()) {
            Keyframe kf2 = i$2.next();
            if (!kf2.hasValue()) {
                if (this.mGetter == null) {
                    setupGetter(targetClass);
                    if (this.mGetter == null) {
                        return;
                    }
                }
                try {
                    kf2.setValue(this.mGetter.invoke(target, new Object[0]));
                } catch (IllegalAccessException e2) {
                    Log.e("PropertyValuesHolder", e2.toString());
                } catch (InvocationTargetException e3) {
                    Log.e("PropertyValuesHolder", e3.toString());
                }
            }
        }
    }

    private void setupValue(Object target, Keyframe kf) {
        if (this.mProperty != null) {
            kf.setValue(this.mProperty.get(target));
        }
        try {
            if (this.mGetter == null) {
                Class targetClass = target.getClass();
                setupGetter(targetClass);
                if (this.mGetter == null) {
                    return;
                }
            }
            kf.setValue(this.mGetter.invoke(target, new Object[0]));
        } catch (IllegalAccessException e) {
            Log.e("PropertyValuesHolder", e.toString());
        } catch (InvocationTargetException e2) {
            Log.e("PropertyValuesHolder", e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupStartValue(Object target) {
        setupValue(target, this.mKeyframeSet.mKeyframes.get(0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupEndValue(Object target) {
        setupValue(target, this.mKeyframeSet.mKeyframes.get(this.mKeyframeSet.mKeyframes.size() - 1));
    }

    @Override // 
    /* renamed from: clone */
    public PropertyValuesHolder mo12clone() {
        try {
            PropertyValuesHolder newPVH = (PropertyValuesHolder) super.clone();
            newPVH.mPropertyName = this.mPropertyName;
            newPVH.mProperty = this.mProperty;
            newPVH.mKeyframeSet = this.mKeyframeSet.mo8clone();
            newPVH.mEvaluator = this.mEvaluator;
            return newPVH;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAnimatedValue(Object target) {
        if (this.mProperty != null) {
            this.mProperty.set(target, getAnimatedValue());
        }
        if (this.mSetter != null) {
            try {
                this.mTmpValueArray[0] = getAnimatedValue();
                this.mSetter.invoke(target, this.mTmpValueArray);
            } catch (IllegalAccessException e) {
                Log.e("PropertyValuesHolder", e.toString());
            } catch (InvocationTargetException e2) {
                Log.e("PropertyValuesHolder", e2.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init() {
        if (this.mEvaluator == null) {
            this.mEvaluator = this.mValueType == Integer.class ? sIntEvaluator : this.mValueType == Float.class ? sFloatEvaluator : null;
        }
        if (this.mEvaluator != null) {
            this.mKeyframeSet.setEvaluator(this.mEvaluator);
        }
    }

    public void setEvaluator(TypeEvaluator evaluator) {
        this.mEvaluator = evaluator;
        this.mKeyframeSet.setEvaluator(evaluator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void calculateValue(float fraction) {
        this.mAnimatedValue = this.mKeyframeSet.getValue(fraction);
    }

    public void setPropertyName(String propertyName) {
        this.mPropertyName = propertyName;
    }

    public void setProperty(Property property) {
        this.mProperty = property;
    }

    public String getPropertyName() {
        return this.mPropertyName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getAnimatedValue() {
        return this.mAnimatedValue;
    }

    public String toString() {
        return this.mPropertyName + ": " + this.mKeyframeSet.toString();
    }

    static String getMethodName(String prefix, String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            return prefix;
        }
        char firstLetter = Character.toUpperCase(propertyName.charAt(0));
        String theRest = propertyName.substring(1);
        return prefix + firstLetter + theRest;
    }

    /* loaded from: PropertyValuesHolder$IntPropertyValuesHolder.class */
    static class IntPropertyValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Integer>> sJNISetterPropertyMap = new HashMap<>();
        int mJniSetter;
        private IntProperty mIntProperty;
        IntKeyframeSet mIntKeyframeSet;
        int mIntAnimatedValue;

        public IntPropertyValuesHolder(String propertyName, IntKeyframeSet keyframeSet) {
            super(propertyName);
            this.mValueType = Integer.TYPE;
            this.mKeyframeSet = keyframeSet;
            this.mIntKeyframeSet = (IntKeyframeSet) this.mKeyframeSet;
        }

        public IntPropertyValuesHolder(Property property, IntKeyframeSet keyframeSet) {
            super(property);
            this.mValueType = Integer.TYPE;
            this.mKeyframeSet = keyframeSet;
            this.mIntKeyframeSet = (IntKeyframeSet) this.mKeyframeSet;
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) this.mProperty;
            }
        }

        public IntPropertyValuesHolder(String propertyName, int... values) {
            super(propertyName);
            setIntValues(values);
        }

        public IntPropertyValuesHolder(Property property, int... values) {
            super(property);
            setIntValues(values);
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) this.mProperty;
            }
        }

        @Override // android.animation.PropertyValuesHolder
        public void setIntValues(int... values) {
            super.setIntValues(values);
            this.mIntKeyframeSet = (IntKeyframeSet) this.mKeyframeSet;
        }

        @Override // android.animation.PropertyValuesHolder
        void calculateValue(float fraction) {
            this.mIntAnimatedValue = this.mIntKeyframeSet.getIntValue(fraction);
        }

        @Override // android.animation.PropertyValuesHolder
        Object getAnimatedValue() {
            return Integer.valueOf(this.mIntAnimatedValue);
        }

        @Override // android.animation.PropertyValuesHolder
        /* renamed from: clone */
        public IntPropertyValuesHolder mo12clone() {
            IntPropertyValuesHolder newPVH = (IntPropertyValuesHolder) super.mo12clone();
            newPVH.mIntKeyframeSet = (IntKeyframeSet) newPVH.mKeyframeSet;
            return newPVH;
        }

        @Override // android.animation.PropertyValuesHolder
        void setAnimatedValue(Object target) {
            if (this.mIntProperty != null) {
                this.mIntProperty.setValue(target, this.mIntAnimatedValue);
            } else if (this.mProperty != null) {
                this.mProperty.set(target, Integer.valueOf(this.mIntAnimatedValue));
            } else if (this.mJniSetter != 0) {
                PropertyValuesHolder.nCallIntMethod(target, this.mJniSetter, this.mIntAnimatedValue);
            } else if (this.mSetter != null) {
                try {
                    this.mTmpValueArray[0] = Integer.valueOf(this.mIntAnimatedValue);
                    this.mSetter.invoke(target, this.mTmpValueArray);
                } catch (IllegalAccessException e) {
                    Log.e("PropertyValuesHolder", e.toString());
                } catch (InvocationTargetException e2) {
                    Log.e("PropertyValuesHolder", e2.toString());
                }
            }
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetter(Class targetClass) {
            Integer mJniSetterInteger;
            if (this.mProperty != null) {
                return;
            }
            try {
                this.mPropertyMapLock.writeLock().lock();
                HashMap<String, Integer> propertyMap = sJNISetterPropertyMap.get(targetClass);
                if (propertyMap != null && (mJniSetterInteger = propertyMap.get(this.mPropertyName)) != null) {
                    this.mJniSetter = mJniSetterInteger.intValue();
                }
                if (this.mJniSetter == 0) {
                    String methodName = getMethodName("set", this.mPropertyName);
                    this.mJniSetter = PropertyValuesHolder.nGetIntMethod(targetClass, methodName);
                    if (this.mJniSetter != 0) {
                        if (propertyMap == null) {
                            propertyMap = new HashMap<>();
                            sJNISetterPropertyMap.put(targetClass, propertyMap);
                        }
                        propertyMap.put(this.mPropertyName, Integer.valueOf(this.mJniSetter));
                    }
                }
                this.mPropertyMapLock.writeLock().unlock();
            } catch (NoSuchMethodError e) {
                this.mPropertyMapLock.writeLock().unlock();
            } catch (Throwable th) {
                this.mPropertyMapLock.writeLock().unlock();
                throw th;
            }
            if (this.mJniSetter == 0) {
                super.setupSetter(targetClass);
            }
        }
    }

    /* loaded from: PropertyValuesHolder$FloatPropertyValuesHolder.class */
    static class FloatPropertyValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Integer>> sJNISetterPropertyMap = new HashMap<>();
        int mJniSetter;
        private FloatProperty mFloatProperty;
        FloatKeyframeSet mFloatKeyframeSet;
        float mFloatAnimatedValue;

        public FloatPropertyValuesHolder(String propertyName, FloatKeyframeSet keyframeSet) {
            super(propertyName);
            this.mValueType = Float.TYPE;
            this.mKeyframeSet = keyframeSet;
            this.mFloatKeyframeSet = (FloatKeyframeSet) this.mKeyframeSet;
        }

        public FloatPropertyValuesHolder(Property property, FloatKeyframeSet keyframeSet) {
            super(property);
            this.mValueType = Float.TYPE;
            this.mKeyframeSet = keyframeSet;
            this.mFloatKeyframeSet = (FloatKeyframeSet) this.mKeyframeSet;
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) this.mProperty;
            }
        }

        public FloatPropertyValuesHolder(String propertyName, float... values) {
            super(propertyName);
            setFloatValues(values);
        }

        public FloatPropertyValuesHolder(Property property, float... values) {
            super(property);
            setFloatValues(values);
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) this.mProperty;
            }
        }

        @Override // android.animation.PropertyValuesHolder
        public void setFloatValues(float... values) {
            super.setFloatValues(values);
            this.mFloatKeyframeSet = (FloatKeyframeSet) this.mKeyframeSet;
        }

        @Override // android.animation.PropertyValuesHolder
        void calculateValue(float fraction) {
            this.mFloatAnimatedValue = this.mFloatKeyframeSet.getFloatValue(fraction);
        }

        @Override // android.animation.PropertyValuesHolder
        Object getAnimatedValue() {
            return Float.valueOf(this.mFloatAnimatedValue);
        }

        @Override // android.animation.PropertyValuesHolder
        /* renamed from: clone */
        public FloatPropertyValuesHolder mo12clone() {
            FloatPropertyValuesHolder newPVH = (FloatPropertyValuesHolder) super.mo12clone();
            newPVH.mFloatKeyframeSet = (FloatKeyframeSet) newPVH.mKeyframeSet;
            return newPVH;
        }

        @Override // android.animation.PropertyValuesHolder
        void setAnimatedValue(Object target) {
            if (this.mFloatProperty != null) {
                this.mFloatProperty.setValue(target, this.mFloatAnimatedValue);
            } else if (this.mProperty != null) {
                this.mProperty.set(target, Float.valueOf(this.mFloatAnimatedValue));
            } else if (this.mJniSetter != 0) {
                PropertyValuesHolder.nCallFloatMethod(target, this.mJniSetter, this.mFloatAnimatedValue);
            } else if (this.mSetter != null) {
                try {
                    this.mTmpValueArray[0] = Float.valueOf(this.mFloatAnimatedValue);
                    this.mSetter.invoke(target, this.mTmpValueArray);
                } catch (IllegalAccessException e) {
                    Log.e("PropertyValuesHolder", e.toString());
                } catch (InvocationTargetException e2) {
                    Log.e("PropertyValuesHolder", e2.toString());
                }
            }
        }

        @Override // android.animation.PropertyValuesHolder
        void setupSetter(Class targetClass) {
            Integer mJniSetterInteger;
            if (this.mProperty != null) {
                return;
            }
            try {
                this.mPropertyMapLock.writeLock().lock();
                HashMap<String, Integer> propertyMap = sJNISetterPropertyMap.get(targetClass);
                if (propertyMap != null && (mJniSetterInteger = propertyMap.get(this.mPropertyName)) != null) {
                    this.mJniSetter = mJniSetterInteger.intValue();
                }
                if (this.mJniSetter == 0) {
                    String methodName = getMethodName("set", this.mPropertyName);
                    this.mJniSetter = PropertyValuesHolder.nGetFloatMethod(targetClass, methodName);
                    if (this.mJniSetter != 0) {
                        if (propertyMap == null) {
                            propertyMap = new HashMap<>();
                            sJNISetterPropertyMap.put(targetClass, propertyMap);
                        }
                        propertyMap.put(this.mPropertyName, Integer.valueOf(this.mJniSetter));
                    }
                }
                this.mPropertyMapLock.writeLock().unlock();
            } catch (NoSuchMethodError e) {
                this.mPropertyMapLock.writeLock().unlock();
            } catch (Throwable th) {
                this.mPropertyMapLock.writeLock().unlock();
                throw th;
            }
            if (this.mJniSetter == 0) {
                super.setupSetter(targetClass);
            }
        }
    }
}