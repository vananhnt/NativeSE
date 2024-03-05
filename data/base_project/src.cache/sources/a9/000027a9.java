package javax.crypto.spec;

import libcore.util.EmptyArray;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PSource.class */
public class PSource {
    private String pSrcName;

    private PSource() {
    }

    protected PSource(String pSrcName) {
        if (pSrcName == null) {
            throw new NullPointerException("pSrcName == null");
        }
        this.pSrcName = pSrcName;
    }

    public String getAlgorithm() {
        return this.pSrcName;
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: PSource$PSpecified.class */
    public static final class PSpecified extends PSource {
        private final byte[] p;
        public static final PSpecified DEFAULT = new PSpecified();

        private PSpecified() {
            super("PSpecified");
            this.p = EmptyArray.BYTE;
        }

        public PSpecified(byte[] p) {
            super("PSpecified");
            if (p == null) {
                throw new NullPointerException("p == null");
            }
            this.p = new byte[p.length];
            System.arraycopy(p, 0, this.p, 0, p.length);
        }

        public byte[] getValue() {
            byte[] result = new byte[this.p.length];
            System.arraycopy(this.p, 0, result, 0, this.p.length);
            return result;
        }
    }
}