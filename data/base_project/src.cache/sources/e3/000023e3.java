package java.nio;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelectionKey;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UnsafeArrayList;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import libcore.io.StructPollfd;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SelectorImpl.class */
public final class SelectorImpl extends AbstractSelector {
    final Object keysLock;
    private final Set<SelectionKeyImpl> mutableKeys;
    private final Set<SelectionKey> unmodifiableKeys;
    private final Set<SelectionKey> mutableSelectedKeys;
    private final Set<SelectionKey> selectedKeys;
    private final FileDescriptor wakeupIn;
    private final FileDescriptor wakeupOut;
    private final UnsafeArrayList<StructPollfd> pollFds;

    public SelectorImpl(SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.keysLock = new Object();
        this.mutableKeys = new HashSet();
        this.unmodifiableKeys = Collections.unmodifiableSet(this.mutableKeys);
        this.mutableSelectedKeys = new HashSet();
        this.selectedKeys = new UnaddableSet(this.mutableSelectedKeys);
        this.pollFds = new UnsafeArrayList<>(StructPollfd.class, 8);
        try {
            FileDescriptor[] pipeFds = Libcore.os.pipe();
            this.wakeupIn = pipeFds[0];
            this.wakeupOut = pipeFds[1];
            IoUtils.setBlocking(this.wakeupIn, false);
            this.pollFds.add(new StructPollfd());
            setPollFd(0, this.wakeupIn, OsConstants.POLLIN, null);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    @Override // java.nio.channels.spi.AbstractSelector
    protected void implCloseSelector() throws IOException {
        wakeup();
        synchronized (this) {
            synchronized (this.unmodifiableKeys) {
                synchronized (this.selectedKeys) {
                    IoUtils.close(this.wakeupIn);
                    IoUtils.close(this.wakeupOut);
                    doCancel();
                    for (SelectionKeyImpl sk : this.mutableKeys) {
                        deregister(sk);
                    }
                }
            }
        }
    }

    @Override // java.nio.channels.spi.AbstractSelector
    protected SelectionKey register(AbstractSelectableChannel channel, int operations, Object attachment) {
        SelectionKeyImpl selectionKey;
        if (!provider().equals(channel.provider())) {
            throw new IllegalSelectorException();
        }
        synchronized (this) {
            synchronized (this.unmodifiableKeys) {
                selectionKey = new SelectionKeyImpl(channel, operations, attachment, this);
                this.mutableKeys.add(selectionKey);
                ensurePollFdsCapacity();
            }
        }
        return selectionKey;
    }

    @Override // java.nio.channels.Selector
    public synchronized Set<SelectionKey> keys() {
        checkClosed();
        return this.unmodifiableKeys;
    }

    private void checkClosed() {
        if (!isOpen()) {
            throw new ClosedSelectorException();
        }
    }

    @Override // java.nio.channels.Selector
    public int select() throws IOException {
        return selectInternal(-1L);
    }

    @Override // java.nio.channels.Selector
    public int select(long timeout) throws IOException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0: " + timeout);
        }
        return selectInternal(timeout == 0 ? -1L : timeout);
    }

    @Override // java.nio.channels.Selector
    public int selectNow() throws IOException {
        return selectInternal(0L);
    }

    /* JADX WARN: Finally extract failed */
    private int selectInternal(long timeout) throws IOException {
        int readyCount;
        checkClosed();
        synchronized (this) {
            synchronized (this.unmodifiableKeys) {
                synchronized (this.selectedKeys) {
                    doCancel();
                    boolean isBlocking = timeout != 0;
                    synchronized (this.keysLock) {
                        preparePollFds();
                    }
                    int rc = -1;
                    if (isBlocking) {
                        try {
                            begin();
                        } catch (Throwable th) {
                            if (isBlocking) {
                                end();
                            }
                            throw th;
                        }
                    }
                    try {
                        rc = Libcore.os.poll(this.pollFds.array(), (int) timeout);
                    } catch (ErrnoException errnoException) {
                        if (errnoException.errno != OsConstants.EINTR) {
                            throw errnoException.rethrowAsIOException();
                        }
                    }
                    if (isBlocking) {
                        end();
                    }
                    int readyCount2 = rc > 0 ? processPollFds() : 0;
                    readyCount = readyCount2 - doCancel();
                }
            }
        }
        return readyCount;
    }

    private void setPollFd(int i, FileDescriptor fd, int events, Object object) {
        StructPollfd pollFd = this.pollFds.get(i);
        pollFd.fd = fd;
        pollFd.events = (short) events;
        pollFd.userData = object;
    }

    private void preparePollFds() {
        int i = 1;
        for (SelectionKeyImpl key : this.mutableKeys) {
            int interestOps = key.interestOpsNoCheck();
            short eventMask = 0;
            if ((17 & interestOps) != 0) {
                eventMask = (short) (0 | OsConstants.POLLIN);
            }
            if ((12 & interestOps) != 0) {
                eventMask = (short) (eventMask | OsConstants.POLLOUT);
            }
            if (eventMask != 0) {
                int i2 = i;
                i++;
                setPollFd(i2, ((FileDescriptorChannel) key.channel()).getFD(), eventMask, key);
            }
        }
    }

    private void ensurePollFdsCapacity() {
        while (this.pollFds.size() < this.mutableKeys.size() + 1) {
            this.pollFds.add(new StructPollfd());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0038  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int processPollFds() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 284
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.SelectorImpl.processPollFds():int");
    }

    @Override // java.nio.channels.Selector
    public synchronized Set<SelectionKey> selectedKeys() {
        checkClosed();
        return this.selectedKeys;
    }

    private int doCancel() {
        int deselected = 0;
        Set<SelectionKey> cancelledKeys = cancelledKeys();
        synchronized (cancelledKeys) {
            if (cancelledKeys.size() > 0) {
                for (SelectionKey currentKey : cancelledKeys) {
                    this.mutableKeys.remove(currentKey);
                    deregister((AbstractSelectionKey) currentKey);
                    if (this.mutableSelectedKeys.remove(currentKey)) {
                        deselected++;
                    }
                }
                cancelledKeys.clear();
            }
        }
        return deselected;
    }

    @Override // java.nio.channels.Selector
    public Selector wakeup() {
        try {
            Libcore.os.write(this.wakeupOut, new byte[]{1}, 0, 1);
        } catch (ErrnoException e) {
        }
        return this;
    }

    /* loaded from: SelectorImpl$UnaddableSet.class */
    private static class UnaddableSet<E> implements Set<E> {
        private final Set<E> set;

        UnaddableSet(Set<E> set) {
            this.set = set;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean equals(Object object) {
            return this.set.equals(object);
        }

        @Override // java.util.Set, java.util.Collection
        public int hashCode() {
            return this.set.hashCode();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean add(E object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public void clear() {
            this.set.clear();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean contains(Object object) {
            return this.set.contains(object);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return this.set.containsAll(c);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return this.set.iterator();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean remove(Object object) {
            return this.set.remove(object);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            return this.set.removeAll(c);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            return this.set.retainAll(c);
        }

        @Override // java.util.Set, java.util.Collection, java.util.List
        public int size() {
            return this.set.size();
        }

        @Override // java.util.Set, java.util.Collection
        public Object[] toArray() {
            return this.set.toArray();
        }

        @Override // java.util.Set, java.util.Collection
        public <T> T[] toArray(T[] a) {
            return (T[]) this.set.toArray(a);
        }
    }
}