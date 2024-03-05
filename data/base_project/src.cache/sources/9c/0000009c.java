package android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
/* loaded from: SdkConstant.class */
public @interface SdkConstant {

    /* loaded from: SdkConstant$SdkConstantType.class */
    public enum SdkConstantType {
        ACTIVITY_INTENT_ACTION,
        BROADCAST_INTENT_ACTION,
        SERVICE_ACTION,
        INTENT_CATEGORY,
        FEATURE
    }

    SdkConstantType value();
}