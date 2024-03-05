package dalvik.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
/* loaded from: TestTarget.class */
public @interface TestTarget {
    String methodName() default "";

    String conceptName() default "";

    Class<?>[] methodArgs() default {};
}