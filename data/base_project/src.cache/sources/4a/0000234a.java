package java.lang.reflect;

/* loaded from: ParameterizedType.class */
public interface ParameterizedType extends Type {
    Type[] getActualTypeArguments();

    Type getOwnerType();

    Type getRawType();
}