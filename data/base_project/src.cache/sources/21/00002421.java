package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CharsetDecoder.class */
public abstract class CharsetDecoder {
    protected abstract CoderResult decodeLoop(ByteBuffer byteBuffer, CharBuffer charBuffer);

    /* JADX INFO: Access modifiers changed from: protected */
    public CharsetDecoder(Charset charset, float averageCharsPerByte, float maxCharsPerByte) {
        throw new RuntimeException("Stub!");
    }

    public final float averageCharsPerByte() {
        throw new RuntimeException("Stub!");
    }

    public final Charset charset() {
        throw new RuntimeException("Stub!");
    }

    public final CharBuffer decode(ByteBuffer in) throws CharacterCodingException {
        throw new RuntimeException("Stub!");
    }

    public final CoderResult decode(ByteBuffer in, CharBuffer out, boolean endOfInput) {
        throw new RuntimeException("Stub!");
    }

    public Charset detectedCharset() {
        throw new RuntimeException("Stub!");
    }

    public final CoderResult flush(CharBuffer out) {
        throw new RuntimeException("Stub!");
    }

    protected CoderResult implFlush(CharBuffer out) {
        throw new RuntimeException("Stub!");
    }

    protected void implOnMalformedInput(CodingErrorAction newAction) {
        throw new RuntimeException("Stub!");
    }

    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
        throw new RuntimeException("Stub!");
    }

    protected void implReplaceWith(String newReplacement) {
        throw new RuntimeException("Stub!");
    }

    protected void implReset() {
        throw new RuntimeException("Stub!");
    }

    public boolean isAutoDetecting() {
        throw new RuntimeException("Stub!");
    }

    public boolean isCharsetDetected() {
        throw new RuntimeException("Stub!");
    }

    public CodingErrorAction malformedInputAction() {
        throw new RuntimeException("Stub!");
    }

    public final float maxCharsPerByte() {
        throw new RuntimeException("Stub!");
    }

    public final CharsetDecoder onMalformedInput(CodingErrorAction newAction) {
        throw new RuntimeException("Stub!");
    }

    public final CharsetDecoder onUnmappableCharacter(CodingErrorAction newAction) {
        throw new RuntimeException("Stub!");
    }

    public final String replacement() {
        throw new RuntimeException("Stub!");
    }

    public final CharsetDecoder replaceWith(String replacement) {
        throw new RuntimeException("Stub!");
    }

    public final CharsetDecoder reset() {
        throw new RuntimeException("Stub!");
    }

    public CodingErrorAction unmappableCharacterAction() {
        throw new RuntimeException("Stub!");
    }
}