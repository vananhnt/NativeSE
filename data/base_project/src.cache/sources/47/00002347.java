package java.lang.reflect;

/* loaded from: Member.class */
public interface Member {
    public static final int PUBLIC = 0;
    public static final int DECLARED = 1;

    Class<?> getDeclaringClass();

    int getModifiers();

    String getName();

    boolean isSynthetic();
}