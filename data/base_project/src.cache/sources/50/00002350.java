package java.lang.reflect;

/* loaded from: WildcardType.class */
public interface WildcardType extends Type {
    Type[] getUpperBounds();

    Type[] getLowerBounds();
}