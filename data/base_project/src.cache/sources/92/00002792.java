package javax.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: MacSpi.class */
public abstract class MacSpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int engineGetMacLength();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineUpdate(byte b);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineUpdate(byte[] bArr, int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract byte[] engineDoFinal();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineReset();

    /* JADX INFO: Access modifiers changed from: protected */
    public void engineUpdate(ByteBuffer input) {
        if (!input.hasRemaining()) {
            return;
        }
        if (input.hasArray()) {
            byte[] bInput = input.array();
            int offset = input.arrayOffset();
            int position = input.position();
            int limit = input.limit();
            engineUpdate(bInput, offset + position, limit - position);
            input.position(limit);
            return;
        }
        byte[] bInput2 = new byte[input.limit() - input.position()];
        input.get(bInput2);
        engineUpdate(bInput2, 0, bInput2.length);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}