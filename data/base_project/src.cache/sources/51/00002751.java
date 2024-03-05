package java.util.prefs;

import gov.nist.core.Separators;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/* loaded from: FilePreferencesImpl.class */
class FilePreferencesImpl extends AbstractPreferences {
    private static final String PREFS_FILE_NAME = "prefs.xml";
    private static String USER_HOME = System.getProperty("user.home") + "/.java/.userPrefs";
    private static String SYSTEM_HOME = System.getProperty("java.home") + "/.systemPrefs";
    private String path;
    private Properties prefs;
    private File prefsFile;
    private File dir;
    private Set<String> removed;
    private Set<String> updated;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilePreferencesImpl(boolean userNode) {
        super(null, "");
        this.removed = new HashSet();
        this.updated = new HashSet();
        this.userNode = userNode;
        this.path = userNode ? USER_HOME : SYSTEM_HOME;
        initPrefs();
    }

    private FilePreferencesImpl(AbstractPreferences parent, String name) {
        super(parent, name);
        this.removed = new HashSet();
        this.updated = new HashSet();
        this.path = ((FilePreferencesImpl) parent).path + File.separator + name;
        initPrefs();
    }

    private void initPrefs() {
        this.dir = new File(this.path);
        this.newNode = !this.dir.exists();
        this.prefsFile = new File(this.path + File.separator + PREFS_FILE_NAME);
        this.prefs = XMLParser.readXmlPreferences(this.prefsFile);
    }

    @Override // java.util.prefs.AbstractPreferences
    protected String[] childrenNamesSpi() throws BackingStoreException {
        String[] names = this.dir.list(new FilenameFilter() { // from class: java.util.prefs.FilePreferencesImpl.1
            @Override // java.io.FilenameFilter
            public boolean accept(File parent, String name) {
                return new File(FilePreferencesImpl.this.path + File.separator + name).isDirectory();
            }
        });
        if (names == null) {
            throw new BackingStoreException("Cannot get child names for " + toString() + " (path is " + this.path + Separators.RPAREN);
        }
        return names;
    }

    @Override // java.util.prefs.AbstractPreferences
    protected AbstractPreferences childSpi(String name) {
        FilePreferencesImpl child = new FilePreferencesImpl(this, name);
        return child;
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void flushSpi() throws BackingStoreException {
        try {
            if (isRemoved()) {
                return;
            }
            Properties currentPrefs = XMLParser.readXmlPreferences(this.prefsFile);
            for (String str : this.removed) {
                currentPrefs.remove(str);
            }
            this.removed.clear();
            for (String str2 : this.updated) {
                currentPrefs.put(str2, this.prefs.get(str2));
            }
            this.updated.clear();
            this.prefs = currentPrefs;
            XMLParser.writeXmlPreferences(this.prefsFile, this.prefs);
        } catch (Exception e) {
            throw new BackingStoreException(e);
        }
    }

    @Override // java.util.prefs.AbstractPreferences
    protected String getSpi(String key) {
        try {
            if (this.prefs == null) {
                this.prefs = XMLParser.readXmlPreferences(this.prefsFile);
            }
            return this.prefs.getProperty(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override // java.util.prefs.AbstractPreferences
    protected String[] keysSpi() throws BackingStoreException {
        Set<Object> ks = this.prefs.keySet();
        return (String[]) ks.toArray(new String[ks.size()]);
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void putSpi(String name, String value) {
        this.prefs.setProperty(name, value);
        this.updated.add(name);
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void removeNodeSpi() throws BackingStoreException {
        this.prefsFile.delete();
        boolean removeSucceed = this.dir.delete();
        if (!removeSucceed) {
            throw new BackingStoreException("Cannot remove " + toString());
        }
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void removeSpi(String key) {
        this.prefs.remove(key);
        this.updated.remove(key);
        this.removed.add(key);
    }

    @Override // java.util.prefs.AbstractPreferences
    protected void syncSpi() throws BackingStoreException {
        flushSpi();
    }
}