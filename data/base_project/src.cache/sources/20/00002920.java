package libcore.util;

import java.lang.ref.Reference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: CollectionUtils.class */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> Iterable<T> dereferenceIterable(final Iterable<? extends Reference<T>> iterable, final boolean trim) {
        return new Iterable<T>() { // from class: libcore.util.CollectionUtils.1
            @Override // java.lang.Iterable
            public Iterator<T> iterator() {
                return new Iterator<T>() { // from class: libcore.util.CollectionUtils.1.1
                    private final Iterator<? extends Reference<T>> delegate;
                    private boolean removeIsOkay;
                    private T next;

                    {
                        this.delegate = Iterable.this.iterator();
                    }

                    private void computeNext() {
                        this.removeIsOkay = false;
                        while (this.next == null && this.delegate.hasNext()) {
                            this.next = this.delegate.next().get();
                            if (trim && this.next == null) {
                                this.delegate.remove();
                            }
                        }
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        computeNext();
                        return this.next != null;
                    }

                    @Override // java.util.Iterator
                    public T next() {
                        if (!hasNext()) {
                            throw new IllegalStateException();
                        }
                        T result = this.next;
                        this.removeIsOkay = true;
                        this.next = null;
                        return result;
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        if (!this.removeIsOkay) {
                            throw new IllegalStateException();
                        }
                        this.delegate.remove();
                    }
                };
            }
        };
    }

    public static <T> void removeDuplicates(List<T> list, Comparator<? super T> comparator) {
        Collections.sort(list, comparator);
        int j = 1;
        for (int i = 1; i < list.size(); i++) {
            if (comparator.compare(list.get(j - 1), list.get(i)) != 0) {
                T object = list.get(i);
                int i2 = j;
                j++;
                list.set(i2, object);
            }
        }
        if (j < list.size()) {
            list.subList(j, list.size()).clear();
        }
    }
}