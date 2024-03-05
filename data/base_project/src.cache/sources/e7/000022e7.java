package java.lang;

/* loaded from: EnumConstantNotPresentException.class */
public class EnumConstantNotPresentException extends RuntimeException {
    public EnumConstantNotPresentException(Class<? extends Enum> enumType, String constantName) {
        throw new RuntimeException("Stub!");
    }

    public Class<? extends Enum> enumType() {
        throw new RuntimeException("Stub!");
    }

    public String constantName() {
        throw new RuntimeException("Stub!");
    }
}