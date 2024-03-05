package java.text;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AttributedCharacterIterator.class */
public interface AttributedCharacterIterator extends CharacterIterator {
    Set<Attribute> getAllAttributeKeys();

    Object getAttribute(Attribute attribute);

    Map<Attribute, Object> getAttributes();

    int getRunLimit();

    int getRunLimit(Attribute attribute);

    int getRunLimit(Set<? extends Attribute> set);

    int getRunStart();

    int getRunStart(Attribute attribute);

    int getRunStart(Set<? extends Attribute> set);

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: AttributedCharacterIterator$Attribute.class */
    public static class Attribute implements Serializable {
        public static final Attribute INPUT_METHOD_SEGMENT = null;
        public static final Attribute LANGUAGE = null;
        public static final Attribute READING = null;

        /* JADX INFO: Access modifiers changed from: protected */
        public Attribute(String name) {
            throw new RuntimeException("Stub!");
        }

        public final boolean equals(Object object) {
            throw new RuntimeException("Stub!");
        }

        protected String getName() {
            throw new RuntimeException("Stub!");
        }

        public final int hashCode() {
            throw new RuntimeException("Stub!");
        }

        protected Object readResolve() throws InvalidObjectException {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }
}