package java.lang;

import java.io.IOException;
import java.nio.CharBuffer;

/* loaded from: Readable.class */
public interface Readable {
    int read(CharBuffer charBuffer) throws IOException;
}