package java.util.prefs;

/* loaded from: FilePreferencesFactoryImpl.class */
class FilePreferencesFactoryImpl implements PreferencesFactory {
    private static final Preferences USER_ROOT = new FilePreferencesImpl(true);
    private static final Preferences SYSTEM_ROOT = new FilePreferencesImpl(false);

    @Override // java.util.prefs.PreferencesFactory
    public Preferences userRoot() {
        return USER_ROOT;
    }

    @Override // java.util.prefs.PreferencesFactory
    public Preferences systemRoot() {
        return SYSTEM_ROOT;
    }
}