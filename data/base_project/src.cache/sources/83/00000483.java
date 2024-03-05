package android.filterfw.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: GenerateFinalPort.class */
public @interface GenerateFinalPort {
    String name() default "";

    boolean hasDefault() default false;
}