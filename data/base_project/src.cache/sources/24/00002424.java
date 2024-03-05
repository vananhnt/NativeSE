package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import libcore.icu.ICU;
import libcore.icu.NativeConverter;
import libcore.util.EmptyArray;

/* loaded from: CharsetEncoderICU.class */
final class CharsetEncoderICU extends CharsetEncoder {
    private static final Map<String, byte[]> DEFAULT_REPLACEMENTS = new HashMap();
    private static final int INPUT_OFFSET = 0;
    private static final int OUTPUT_OFFSET = 1;
    private static final int INVALID_CHARS = 2;
    private int[] data;
    private long converterHandle;
    private char[] input;
    private byte[] output;
    private char[] allocatedInput;
    private byte[] allocatedOutput;
    private int inEnd;
    private int outEnd;

    static {
        byte[] questionMark = {63};
        DEFAULT_REPLACEMENTS.put("UTF-8", questionMark);
        DEFAULT_REPLACEMENTS.put("ISO-8859-1", questionMark);
        DEFAULT_REPLACEMENTS.put("US-ASCII", questionMark);
    }

    public static CharsetEncoderICU newInstance(Charset cs, String icuCanonicalName) {
        long address = 0;
        try {
            long address2 = NativeConverter.openConverter(icuCanonicalName);
            float averageBytesPerChar = NativeConverter.getAveBytesPerChar(address2);
            float maxBytesPerChar = NativeConverter.getMaxBytesPerChar(address2);
            byte[] replacement = makeReplacement(icuCanonicalName, address2);
            CharsetEncoderICU result = new CharsetEncoderICU(cs, averageBytesPerChar, maxBytesPerChar, replacement, address2);
            address = 0;
            if (0 != 0) {
                NativeConverter.closeConverter(0L);
            }
            return result;
        } catch (Throwable th) {
            if (address != 0) {
                NativeConverter.closeConverter(address);
            }
            throw th;
        }
    }

    private static byte[] makeReplacement(String icuCanonicalName, long address) {
        byte[] replacement = DEFAULT_REPLACEMENTS.get(icuCanonicalName);
        if (replacement != null) {
            return (byte[]) replacement.clone();
        }
        return NativeConverter.getSubstitutionBytes(address);
    }

    private CharsetEncoderICU(Charset cs, float averageBytesPerChar, float maxBytesPerChar, byte[] replacement, long address) {
        super(cs, averageBytesPerChar, maxBytesPerChar, replacement, true);
        this.data = new int[3];
        this.converterHandle = 0L;
        this.input = null;
        this.output = null;
        this.allocatedInput = null;
        this.allocatedOutput = null;
        this.converterHandle = address;
        updateCallback();
    }

    @Override // java.nio.charset.CharsetEncoder
    protected void implReplaceWith(byte[] newReplacement) {
        updateCallback();
    }

    @Override // java.nio.charset.CharsetEncoder
    protected void implOnMalformedInput(CodingErrorAction newAction) {
        updateCallback();
    }

    @Override // java.nio.charset.CharsetEncoder
    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
        updateCallback();
    }

    private void updateCallback() {
        NativeConverter.setCallbackEncode(this.converterHandle, this);
    }

    @Override // java.nio.charset.CharsetEncoder
    protected void implReset() {
        NativeConverter.resetCharToByte(this.converterHandle);
        this.data[0] = 0;
        this.data[1] = 0;
        this.data[2] = 0;
        this.output = null;
        this.input = null;
        this.allocatedInput = null;
        this.allocatedOutput = null;
        this.inEnd = 0;
        this.outEnd = 0;
    }

    @Override // java.nio.charset.CharsetEncoder
    protected CoderResult implFlush(ByteBuffer out) {
        try {
            this.input = EmptyArray.CHAR;
            this.inEnd = 0;
            this.data[0] = 0;
            this.data[1] = getArray(out);
            this.data[2] = 0;
            int error = NativeConverter.encode(this.converterHandle, this.input, this.inEnd, this.output, this.outEnd, this.data, true);
            if (ICU.U_FAILURE(error)) {
                if (error == 15) {
                    CoderResult coderResult = CoderResult.OVERFLOW;
                    setPosition(out);
                    implReset();
                    return coderResult;
                } else if (error == 11 && this.data[0] > 0) {
                    CoderResult malformedForLength = CoderResult.malformedForLength(this.data[0]);
                    setPosition(out);
                    implReset();
                    return malformedForLength;
                }
            }
            CoderResult coderResult2 = CoderResult.UNDERFLOW;
            setPosition(out);
            implReset();
            return coderResult2;
        } catch (Throwable th) {
            setPosition(out);
            implReset();
            throw th;
        }
    }

    @Override // java.nio.charset.CharsetEncoder
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if (in.hasRemaining()) {
            this.data[0] = getArray(in);
            this.data[1] = getArray(out);
            this.data[2] = 0;
            try {
                int error = NativeConverter.encode(this.converterHandle, this.input, this.inEnd, this.output, this.outEnd, this.data, false);
                if (!ICU.U_FAILURE(error)) {
                    CoderResult coderResult = CoderResult.UNDERFLOW;
                    setPosition(in);
                    setPosition(out);
                    return coderResult;
                } else if (error == 15) {
                    CoderResult coderResult2 = CoderResult.OVERFLOW;
                    setPosition(in);
                    setPosition(out);
                    return coderResult2;
                } else if (error == 10) {
                    CoderResult unmappableForLength = CoderResult.unmappableForLength(this.data[2]);
                    setPosition(in);
                    setPosition(out);
                    return unmappableForLength;
                } else if (error == 12) {
                    CoderResult malformedForLength = CoderResult.malformedForLength(this.data[2]);
                    setPosition(in);
                    setPosition(out);
                    return malformedForLength;
                } else {
                    throw new AssertionError(error);
                }
            } catch (Throwable th) {
                setPosition(in);
                setPosition(out);
                throw th;
            }
        }
        return CoderResult.UNDERFLOW;
    }

    protected void finalize() throws Throwable {
        try {
            NativeConverter.closeConverter(this.converterHandle);
            this.converterHandle = 0L;
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private int getArray(ByteBuffer out) {
        if (out.hasArray()) {
            this.output = out.array();
            this.outEnd = out.arrayOffset() + out.limit();
            return out.arrayOffset() + out.position();
        }
        this.outEnd = out.remaining();
        if (this.allocatedOutput == null || this.outEnd > this.allocatedOutput.length) {
            this.allocatedOutput = new byte[this.outEnd];
        }
        this.output = this.allocatedOutput;
        return 0;
    }

    private int getArray(CharBuffer in) {
        if (in.hasArray()) {
            this.input = in.array();
            this.inEnd = in.arrayOffset() + in.limit();
            return in.arrayOffset() + in.position();
        }
        this.inEnd = in.remaining();
        if (this.allocatedInput == null || this.inEnd > this.allocatedInput.length) {
            this.allocatedInput = new char[this.inEnd];
        }
        int pos = in.position();
        in.get(this.allocatedInput, 0, this.inEnd);
        in.position(pos);
        this.input = this.allocatedInput;
        return 0;
    }

    private void setPosition(ByteBuffer out) {
        if (out.hasArray()) {
            out.position((out.position() + this.data[1]) - out.arrayOffset());
        } else {
            out.put(this.output, 0, this.data[1]);
        }
        this.output = null;
    }

    private void setPosition(CharBuffer in) {
        in.position((in.position() + this.data[0]) - this.data[2]);
        this.input = null;
    }
}