package java.lang.annotation;

@Target({ElementType.ANNOTATION_TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: Target.class */
public @interface Target {
    ElementType[] value();
}