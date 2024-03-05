package android.content;

import java.util.Iterator;

/* loaded from: EntityIterator.class */
public interface EntityIterator extends Iterator<Entity> {
    void reset();

    void close();
}