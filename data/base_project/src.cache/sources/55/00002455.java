package java.security;

import java.io.ObjectStreamException;
import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyRep.class */
public class KeyRep implements Serializable {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyRep$Type.class */
    public enum Type {
        PRIVATE,
        PUBLIC,
        SECRET
    }

    public KeyRep(Type type, String algorithm, String format, byte[] encoded) {
        throw new RuntimeException("Stub!");
    }

    protected Object readResolve() throws ObjectStreamException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.security.KeyRep$1  reason: invalid class name */
    /* loaded from: KeyRep$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$security$KeyRep$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$java$security$KeyRep$Type[Type.SECRET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$security$KeyRep$Type[Type.PUBLIC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$security$KeyRep$Type[Type.PRIVATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}