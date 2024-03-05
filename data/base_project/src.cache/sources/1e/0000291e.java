package libcore.reflect;

import gov.nist.core.Separators;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/* loaded from: WildcardTypeImpl.class */
public final class WildcardTypeImpl implements WildcardType {
    private final ListOfTypes extendsBound;
    private final ListOfTypes superBound;

    public WildcardTypeImpl(ListOfTypes extendsBound, ListOfTypes superBound) {
        this.extendsBound = extendsBound;
        this.superBound = superBound;
    }

    @Override // java.lang.reflect.WildcardType
    public Type[] getLowerBounds() throws TypeNotPresentException, MalformedParameterizedTypeException {
        return (Type[]) this.superBound.getResolvedTypes().clone();
    }

    @Override // java.lang.reflect.WildcardType
    public Type[] getUpperBounds() throws TypeNotPresentException, MalformedParameterizedTypeException {
        return (Type[]) this.extendsBound.getResolvedTypes().clone();
    }

    public boolean equals(Object o) {
        if (!(o instanceof WildcardType)) {
            return false;
        }
        WildcardType that = (WildcardType) o;
        return Arrays.equals(getLowerBounds(), that.getLowerBounds()) && Arrays.equals(getUpperBounds(), that.getUpperBounds());
    }

    public int hashCode() {
        return (31 * Arrays.hashCode(getLowerBounds())) + Arrays.hashCode(getUpperBounds());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(Separators.QUESTION);
        if ((this.extendsBound.length() == 1 && this.extendsBound.getResolvedTypes()[0] != Object.class) || this.extendsBound.length() > 1) {
            sb.append(" extends ").append(this.extendsBound);
        } else if (this.superBound.length() > 0) {
            sb.append(" super ").append(this.superBound);
        }
        return sb.toString();
    }
}