package javax.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SealedObject.class */
public class SealedObject implements Serializable {
    private static final long serialVersionUID = 4482838265551344752L;
    protected byte[] encodedParams;
    private byte[] encryptedContent;
    private String sealAlg;
    private String paramsAlg;

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.encodedParams = (byte[]) s.readUnshared();
        this.encryptedContent = (byte[]) s.readUnshared();
        this.sealAlg = (String) s.readObject();
        this.paramsAlg = (String) s.readObject();
    }

    public SealedObject(Serializable object, Cipher c) throws IOException, IllegalBlockSizeException {
        if (c == null) {
            throw new NullPointerException("c == null");
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            AlgorithmParameters ap = c.getParameters();
            this.encodedParams = ap == null ? null : ap.getEncoded();
            this.paramsAlg = ap == null ? null : ap.getAlgorithm();
            this.sealAlg = c.getAlgorithm();
            this.encryptedContent = c.doFinal(bos.toByteArray());
        } catch (BadPaddingException e) {
            throw new IOException(e.toString());
        }
    }

    protected SealedObject(SealedObject so) {
        if (so == null) {
            throw new NullPointerException("so == null");
        }
        this.encryptedContent = so.encryptedContent;
        this.encodedParams = so.encodedParams;
        this.sealAlg = so.sealAlg;
        this.paramsAlg = so.paramsAlg;
    }

    public final String getAlgorithm() {
        return this.sealAlg;
    }

    public final Object getObject(Key key) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        try {
            Cipher cipher = Cipher.getInstance(this.sealAlg);
            if (this.paramsAlg != null && this.paramsAlg.length() != 0) {
                AlgorithmParameters params = AlgorithmParameters.getInstance(this.paramsAlg);
                params.init(this.encodedParams);
                cipher.init(2, key, params);
            } else {
                cipher.init(2, key);
            }
            byte[] serialized = cipher.doFinal(this.encryptedContent);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));
            return ois.readObject();
        } catch (IllegalStateException e) {
            throw new NoSuchAlgorithmException(e.toString());
        } catch (InvalidAlgorithmParameterException e2) {
            throw new NoSuchAlgorithmException(e2.toString());
        } catch (BadPaddingException e3) {
            throw new NoSuchAlgorithmException(e3.toString());
        } catch (IllegalBlockSizeException e4) {
            throw new NoSuchAlgorithmException(e4.toString());
        } catch (NoSuchPaddingException e5) {
            throw new NoSuchAlgorithmException(e5.toString());
        }
    }

    public final Object getObject(Cipher c) throws IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        if (c == null) {
            throw new NullPointerException("c == null");
        }
        byte[] serialized = c.doFinal(this.encryptedContent);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));
        return ois.readObject();
    }

    public final Object getObject(Key key, String provider) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("provider name empty or null");
        }
        try {
            Cipher cipher = Cipher.getInstance(this.sealAlg, provider);
            if (this.paramsAlg != null && this.paramsAlg.length() != 0) {
                AlgorithmParameters params = AlgorithmParameters.getInstance(this.paramsAlg);
                params.init(this.encodedParams);
                cipher.init(2, key, params);
            } else {
                cipher.init(2, key);
            }
            byte[] serialized = cipher.doFinal(this.encryptedContent);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));
            return ois.readObject();
        } catch (IllegalStateException e) {
            throw new NoSuchAlgorithmException(e.toString());
        } catch (InvalidAlgorithmParameterException e2) {
            throw new NoSuchAlgorithmException(e2.toString());
        } catch (BadPaddingException e3) {
            throw new NoSuchAlgorithmException(e3.toString());
        } catch (IllegalBlockSizeException e4) {
            throw new NoSuchAlgorithmException(e4.toString());
        } catch (NoSuchPaddingException e5) {
            throw new NoSuchAlgorithmException(e5.toString());
        }
    }
}