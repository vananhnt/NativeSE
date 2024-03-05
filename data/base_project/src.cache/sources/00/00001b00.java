package com.android.internal.util;

/* loaded from: CharSequences.class */
public class CharSequences {
    public static CharSequence forAsciiBytes(final byte[] bytes) {
        return new CharSequence() { // from class: com.android.internal.util.CharSequences.1
            @Override // java.lang.CharSequence
            public char charAt(int index) {
                return (char) bytes[index];
            }

            @Override // java.lang.CharSequence
            public int length() {
                return bytes.length;
            }

            @Override // java.lang.CharSequence
            public CharSequence subSequence(int start, int end) {
                return CharSequences.forAsciiBytes(bytes, start, end);
            }

            @Override // java.lang.CharSequence
            public String toString() {
                return new String(bytes);
            }
        };
    }

    public static CharSequence forAsciiBytes(final byte[] bytes, final int start, final int end) {
        validate(start, end, bytes.length);
        return new CharSequence() { // from class: com.android.internal.util.CharSequences.2
            @Override // java.lang.CharSequence
            public char charAt(int index) {
                return (char) bytes[index + start];
            }

            @Override // java.lang.CharSequence
            public int length() {
                return end - start;
            }

            @Override // java.lang.CharSequence
            public CharSequence subSequence(int newStart, int newEnd) {
                int newStart2 = newStart - start;
                int newEnd2 = newEnd - start;
                CharSequences.validate(newStart2, newEnd2, length());
                return CharSequences.forAsciiBytes(bytes, newStart2, newEnd2);
            }

            @Override // java.lang.CharSequence
            public String toString() {
                return new String(bytes, start, length());
            }
        };
    }

    static void validate(int start, int end, int length) {
        if (start < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (end < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (end > length) {
            throw new IndexOutOfBoundsException();
        }
        if (start > end) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a.length() != b.length()) {
            return false;
        }
        int length = a.length();
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static int compareToIgnoreCase(CharSequence me, CharSequence another) {
        int myLen = me.length();
        int anotherLen = another.length();
        int myPos = 0;
        int anotherPos = 0;
        int end = myLen < anotherLen ? myLen : anotherLen;
        while (myPos < end) {
            int i = myPos;
            myPos++;
            int i2 = anotherPos;
            anotherPos++;
            int result = Character.toLowerCase(me.charAt(i)) - Character.toLowerCase(another.charAt(i2));
            if (result != 0) {
                return result;
            }
        }
        return myLen - anotherLen;
    }
}