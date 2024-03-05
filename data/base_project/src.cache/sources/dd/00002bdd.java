package org.ccil.cowan.tagsoup;

import java.io.InputStream;
import java.io.Reader;

/* loaded from: AutoDetector.class */
public interface AutoDetector {
    Reader autoDetectingReader(InputStream inputStream);
}