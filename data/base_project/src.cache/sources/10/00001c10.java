package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Binder;
import android.provider.Settings;
import gov.nist.javax.sip.header.ParameterNames;
import java.io.File;

/* loaded from: CertBlacklister.class */
public class CertBlacklister extends Binder {
    private static final String TAG = "CertBlacklister";
    private static final String BLACKLIST_ROOT = System.getenv("ANDROID_DATA") + "/misc/keychain/";
    public static final String PUBKEY_PATH = BLACKLIST_ROOT + "pubkey_blacklist.txt";
    public static final String SERIAL_PATH = BLACKLIST_ROOT + "serial_blacklist.txt";
    public static final String PUBKEY_BLACKLIST_KEY = "pubkey_blacklist";
    public static final String SERIAL_BLACKLIST_KEY = "serial_blacklist";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CertBlacklister$BlacklistObserver.class */
    public static class BlacklistObserver extends ContentObserver {
        private final String mKey;
        private final String mName;
        private final String mPath;
        private final File mTmpDir;
        private final ContentResolver mContentResolver;

        static /* synthetic */ File access$000(BlacklistObserver x0) {
            return x0.mTmpDir;
        }

        static /* synthetic */ String access$100(BlacklistObserver x0) {
            return x0.mPath;
        }

        public BlacklistObserver(String key, String name, String path, ContentResolver cr) {
            super(null);
            this.mKey = key;
            this.mName = name;
            this.mPath = path;
            this.mTmpDir = new File(this.mPath).getParentFile();
            this.mContentResolver = cr;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            writeBlacklist();
        }

        public String getValue() {
            return Settings.Secure.getString(this.mContentResolver, this.mKey);
        }

        private void writeBlacklist() {
            new Thread("BlacklistUpdater") { // from class: com.android.server.CertBlacklister.BlacklistObserver.1
                /*  JADX ERROR: Method load error
                    jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.CertBlacklister.BlacklistObserver.1.run():void, file: CertBlacklister$BlacklistObserver$1.class
                    	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                    	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                    	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                    Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                    	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                    	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                    	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                    	... 2 more
                    */
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    /*
                    // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.CertBlacklister.BlacklistObserver.1.run():void, file: CertBlacklister$BlacklistObserver$1.class
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.CertBlacklister.BlacklistObserver.AnonymousClass1.run():void");
                }
            }.start();
        }
    }

    public CertBlacklister(Context context) {
        registerObservers(context.getContentResolver());
    }

    private BlacklistObserver buildPubkeyObserver(ContentResolver cr) {
        return new BlacklistObserver(PUBKEY_BLACKLIST_KEY, ParameterNames.PUBKEY, PUBKEY_PATH, cr);
    }

    private BlacklistObserver buildSerialObserver(ContentResolver cr) {
        return new BlacklistObserver(SERIAL_BLACKLIST_KEY, Context.SERIAL_SERVICE, SERIAL_PATH, cr);
    }

    private void registerObservers(ContentResolver cr) {
        cr.registerContentObserver(Settings.Secure.getUriFor(PUBKEY_BLACKLIST_KEY), true, buildPubkeyObserver(cr));
        cr.registerContentObserver(Settings.Secure.getUriFor(SERIAL_BLACKLIST_KEY), true, buildSerialObserver(cr));
    }
}