package java.util.prefs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractPreferences.class */
public abstract class AbstractPreferences extends Preferences {
    protected final Object lock;
    protected boolean newNode;

    protected abstract void flushSpi() throws BackingStoreException;

    protected abstract String[] childrenNamesSpi() throws BackingStoreException;

    protected abstract AbstractPreferences childSpi(String str);

    protected abstract void putSpi(String str, String str2);

    protected abstract String getSpi(String str);

    protected abstract String[] keysSpi() throws BackingStoreException;

    protected abstract void removeNodeSpi() throws BackingStoreException;

    protected abstract void removeSpi(String str);

    protected abstract void syncSpi() throws BackingStoreException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractPreferences(AbstractPreferences parent, String name) {
        throw new RuntimeException("Stub!");
    }

    protected final AbstractPreferences[] cachedChildren() {
        throw new RuntimeException("Stub!");
    }

    protected AbstractPreferences getChild(String name) throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isRemoved() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public String absolutePath() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public String[] childrenNames() throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void clear() throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void exportNode(OutputStream ostream) throws IOException, BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void exportSubtree(OutputStream ostream) throws IOException, BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void flush() throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public String get(String key, String deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public boolean getBoolean(String key, boolean deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public byte[] getByteArray(String key, byte[] deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public double getDouble(String key, double deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public float getFloat(String key, float deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public int getInt(String key, int deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public long getLong(String key, long deflt) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public boolean isUserNode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public String[] keys() throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public String name() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public Preferences node(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public boolean nodeExists(String name) throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public Preferences parent() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void put(String key, String value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void putBoolean(String key, boolean value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void putByteArray(String key, byte[] value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void putDouble(String key, double value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void putFloat(String key, float value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void putInt(String key, int value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void putLong(String key, long value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void remove(String key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void removeNode() throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void addNodeChangeListener(NodeChangeListener ncl) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public void sync() throws BackingStoreException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.prefs.Preferences
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.prefs.AbstractPreferences$1  reason: invalid class name */
    /* loaded from: AbstractPreferences$1.class */
    static class AnonymousClass1 extends Thread {
        AnonymousClass1() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Preferences uroot = Preferences.userRoot();
            Preferences sroot = Preferences.systemRoot();
            try {
                uroot.flush();
            } catch (BackingStoreException e) {
            }
            try {
                sroot.flush();
            } catch (BackingStoreException e2) {
            }
        }
    }

    /* loaded from: AbstractPreferences$EventDispatcher.class */
    private static class EventDispatcher extends Thread {
        EventDispatcher(String name) {
            super(name);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    EventObject event = getEventObject();
                    AbstractPreferences pref = (AbstractPreferences) event.getSource();
                    if (event instanceof NodeAddEvent) {
                        dispatchNodeAdd((NodeChangeEvent) event, AbstractPreferences.access$000(pref));
                    } else if (event instanceof NodeRemoveEvent) {
                        dispatchNodeRemove((NodeChangeEvent) event, AbstractPreferences.access$000(pref));
                    } else if (event instanceof PreferenceChangeEvent) {
                        dispatchPrefChange((PreferenceChangeEvent) event, AbstractPreferences.access$100(pref));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private EventObject getEventObject() throws InterruptedException {
            EventObject event;
            synchronized (AbstractPreferences.access$200()) {
                if (AbstractPreferences.access$200().isEmpty()) {
                    AbstractPreferences.access$200().wait();
                }
                event = (EventObject) AbstractPreferences.access$200().get(0);
                AbstractPreferences.access$200().remove(0);
            }
            return event;
        }

        private void dispatchPrefChange(PreferenceChangeEvent event, List<EventListener> preferenceChangeListeners) {
            synchronized (preferenceChangeListeners) {
                for (EventListener preferenceChangeListener : preferenceChangeListeners) {
                    ((PreferenceChangeListener) preferenceChangeListener).preferenceChange(event);
                }
            }
        }

        private void dispatchNodeRemove(NodeChangeEvent event, List<EventListener> nodeChangeListeners) {
            synchronized (nodeChangeListeners) {
                for (EventListener nodeChangeListener : nodeChangeListeners) {
                    ((NodeChangeListener) nodeChangeListener).childRemoved(event);
                }
            }
        }

        private void dispatchNodeAdd(NodeChangeEvent event, List<EventListener> nodeChangeListeners) {
            synchronized (nodeChangeListeners) {
                for (EventListener nodeChangeListener : nodeChangeListeners) {
                    NodeChangeListener ncl = (NodeChangeListener) nodeChangeListener;
                    ncl.childAdded(event);
                }
            }
        }
    }

    /* loaded from: AbstractPreferences$NodeAddEvent.class */
    private static class NodeAddEvent extends NodeChangeEvent {
        private static final long serialVersionUID = 1;

        public NodeAddEvent(Preferences p, Preferences c) {
            super(p, c);
        }
    }

    /* loaded from: AbstractPreferences$NodeRemoveEvent.class */
    private static class NodeRemoveEvent extends NodeChangeEvent {
        private static final long serialVersionUID = 1;

        public NodeRemoveEvent(Preferences p, Preferences c) {
            super(p, c);
        }
    }
}