package org.apache.harmony.security.asn1;

import java.util.Arrays;

/* loaded from: ObjectIdentifier.class */
public final class ObjectIdentifier {
    private final int[] oid;
    private String soid;

    public ObjectIdentifier(int[] oid) {
        validate(oid);
        this.oid = oid;
    }

    public ObjectIdentifier(String strOid) {
        this.oid = toIntArray(strOid);
        this.soid = strOid;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Arrays.equals(this.oid, ((ObjectIdentifier) o).oid);
    }

    public String toString() {
        if (this.soid == null) {
            this.soid = toString(this.oid);
        }
        return this.soid;
    }

    public int hashCode() {
        int intHash = 0;
        for (int i = 0; i < this.oid.length && i < 4; i++) {
            intHash += this.oid[i] << (8 * i);
        }
        return intHash & Integer.MAX_VALUE;
    }

    public static void validate(int[] oid) {
        if (oid == null) {
            throw new IllegalArgumentException("oid == null");
        }
        if (oid.length < 2) {
            throw new IllegalArgumentException("OID MUST have at least 2 subidentifiers");
        }
        if (oid[0] > 2) {
            throw new IllegalArgumentException("Valid values for first subidentifier are 0, 1 and 2");
        }
        if (oid[0] != 2 && oid[1] > 39) {
            throw new IllegalArgumentException("If the first subidentifier has 0 or 1 value the second subidentifier value MUST be less than 40");
        }
        for (int anOid : oid) {
            if (anOid < 0) {
                throw new IllegalArgumentException("Subidentifier MUST have positive value");
            }
        }
    }

    public static String toString(int[] oid) {
        StringBuilder sb = new StringBuilder(3 * oid.length);
        for (int i = 0; i < oid.length - 1; i++) {
            sb.append(oid[i]);
            sb.append('.');
        }
        sb.append(oid[oid.length - 1]);
        return sb.toString();
    }

    public static int[] toIntArray(String str) {
        return toIntArray(str, true);
    }

    public static boolean isOID(String str) {
        return toIntArray(str, false) != null;
    }

    private static int[] toIntArray(String str, boolean shouldThrow) {
        if (str == null) {
            if (!shouldThrow) {
                return null;
            }
            throw new IllegalArgumentException("str == null");
        }
        int length = str.length();
        if (length == 0) {
            if (!shouldThrow) {
                return null;
            }
            throw new IllegalArgumentException("Incorrect syntax");
        }
        int count = 1;
        boolean wasDot = true;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c == '.') {
                if (wasDot) {
                    if (!shouldThrow) {
                        return null;
                    }
                    throw new IllegalArgumentException("Incorrect syntax");
                }
                wasDot = true;
                count++;
            } else if (c >= '0' && c <= '9') {
                wasDot = false;
            } else if (!shouldThrow) {
                return null;
            } else {
                throw new IllegalArgumentException("Incorrect syntax");
            }
        }
        if (wasDot) {
            if (!shouldThrow) {
                return null;
            }
            throw new IllegalArgumentException("Incorrect syntax");
        } else if (count < 2) {
            if (!shouldThrow) {
                return null;
            }
            throw new IllegalArgumentException("Incorrect syntax");
        } else {
            int[] oid = new int[count];
            int j = 0;
            for (int i2 = 0; i2 < length; i2++) {
                char c2 = str.charAt(i2);
                if (c2 == '.') {
                    j++;
                } else {
                    oid[j] = ((oid[j] * 10) + c2) - 48;
                }
            }
            if (oid[0] > 2) {
                if (!shouldThrow) {
                    return null;
                }
                throw new IllegalArgumentException("Incorrect syntax");
            } else if (oid[0] != 2 && oid[1] > 39) {
                if (!shouldThrow) {
                    return null;
                }
                throw new IllegalArgumentException("Incorrect syntax");
            } else {
                return oid;
            }
        }
    }
}