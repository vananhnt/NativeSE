package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import libcore.icu.ICU;
import libcore.icu.NativeConverter;
import libcore.util.EmptyArray;

/* loaded from: CharsetDecoderICU.class */
final class CharsetDecoderICU extends CharsetDecoder {
    private static final int MAX_CHARS_PER_BYTE = 2;
    private static final int INPUT_OFFSET = 0;
    private static final int OUTPUT_OFFSET = 1;
    private static final int INVALID_BYTES = 2;
    private int[] data;
    private long converterHandle;
    private byte[] input;
    private char[] output;
    private byte[] allocatedInput;
    private char[] allocatedOutput;
    private int inEnd;
    private int outEnd;

    public static CharsetDecoderICU newInstance(Charset cs, String icuCanonicalName) {
        long address = 0;
        try {
            long address2 = NativeConverter.openConverter(icuCanonicalName);
            float averageCharsPerByte = NativeConverter.getAveCharsPerByte(address2);
            CharsetDecoderICU result = new CharsetDecoderICU(cs, averageCharsPerByte, address2);
            address = 0;
            result.updateCallback();
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

    private CharsetDecoderICU(Charset cs, float averageCharsPerByte, long address) {
        super(cs, averageCharsPerByte, 2.0f);
        this.data = new int[3];
        this.converterHandle = 0L;
        this.input = null;
        this.output = null;
        this.allocatedInput = null;
        this.allocatedOutput = null;
        this.converterHandle = address;
    }

    @Override // java.nio.charset.CharsetDecoder
    protected void implReplaceWith(String newReplacement) {
        updateCallback();
    }

    @Override // java.nio.charset.CharsetDecoder
    protected final void implOnMalformedInput(CodingErrorAction newAction) {
        updateCallback();
    }

    @Override // java.nio.charset.CharsetDecoder
    protected final void implOnUnmappableCharacter(CodingErrorAction newAction) {
        updateCallback();
    }

    private void updateCallback() {
        NativeConverter.setCallbackDecode(this.converterHandle, this);
    }

    @Override // java.nio.charset.CharsetDecoder
    protected void implReset() {
        NativeConverter.resetByteToChar(this.converterHandle);
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

    @Override // java.nio.charset.CharsetDecoder
    protected final CoderResult implFlush(CharBuffer out) {
        try {
            this.input = EmptyArray.BYTE;
            this.inEnd = 0;
            this.data[0] = 0;
            this.data[1] = getArray(out);
            this.data[2] = 0;
            int error = NativeConverter.decode(this.converterHandle, this.input, this.inEnd, this.output, this.outEnd, this.data, true);
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

    @Override // java.nio.charset.CharsetDecoder
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (in.hasRemaining()) {
            this.data[0] = getArray(in);
            this.data[1] = getArray(out);
            try {
                int error = NativeConverter.decode(this.converterHandle, this.input, this.inEnd, this.output, this.outEnd, this.data, false);
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

    private int getArray(CharBuffer out) {
        if (out.hasArray()) {
            this.output = out.array();
            this.outEnd = out.arrayOffset() + out.limit();
            return out.arrayOffset() + out.position();
        }
        this.outEnd = out.remaining();
        if (this.allocatedOutput == null || this.outEnd > this.allocatedOutput.length) {
            this.allocatedOutput = new char[this.outEnd];
        }
        this.output = this.allocatedOutput;
        return 0;
    }

    private int getArray(ByteBuffer in) {
        if (in.hasArray()) {
            this.input = in.array();
            this.inEnd = in.arrayOffset() + in.limit();
            return in.arrayOffset() + in.position();
        }
        this.inEnd = in.remaining();
        if (this.allocatedInput == null || this.inEnd > this.allocatedInput.length) {
            this.allocatedInput = new byte[this.inEnd];
        }
        int pos = in.position();
        in.get(this.allocatedInput, 0, this.inEnd);
        in.position(pos);
        this.input = this.allocatedInput;
        return 0;
    }

    private void setPosition(CharBuffer out) {
        if (out.hasArray()) {
            out.position((out.position() + this.data[1]) - out.arrayOffset());
        } else {
            out.put(this.output, 0, this.data[1]);
        }
        this.output = null;
    }

    private void setPosition(ByteBuffer in) {
        in.position(in.position() + this.data[0]);
        this.input = null;
    }
}