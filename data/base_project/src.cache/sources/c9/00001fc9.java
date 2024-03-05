package com.android.server.updates;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Base64;
import android.util.EventLog;
import android.util.Slog;
import com.android.server.EventLogTags;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import libcore.io.IoUtils;

/* loaded from: ConfigUpdateInstallReceiver.class */
public class ConfigUpdateInstallReceiver extends BroadcastReceiver {
    private static final String TAG = "ConfigUpdateInstallReceiver";
    private static final String EXTRA_CONTENT_PATH = "CONTENT_PATH";
    private static final String EXTRA_REQUIRED_HASH = "REQUIRED_HASH";
    private static final String EXTRA_SIGNATURE = "SIGNATURE";
    private static final String EXTRA_VERSION_NUMBER = "VERSION";
    private static final String UPDATE_CERTIFICATE_KEY = "config_update_certificate";
    protected final File updateDir;
    protected final File updateContent;
    protected final File updateVersion;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.updates.ConfigUpdateInstallReceiver.writeUpdate(java.io.File, java.io.File, byte[]):void, file: ConfigUpdateInstallReceiver.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    protected void writeUpdate(java.io.File r1, java.io.File r2, byte[] r3) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.updates.ConfigUpdateInstallReceiver.writeUpdate(java.io.File, java.io.File, byte[]):void, file: ConfigUpdateInstallReceiver.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.updates.ConfigUpdateInstallReceiver.writeUpdate(java.io.File, java.io.File, byte[]):void");
    }

    public ConfigUpdateInstallReceiver(String updateDir, String updateContentPath, String updateMetadataPath, String updateVersionPath) {
        this.updateDir = new File(updateDir);
        this.updateContent = new File(updateDir, updateContentPath);
        File updateMetadataDir = new File(updateDir, updateMetadataPath);
        this.updateVersion = new File(updateMetadataDir, updateVersionPath);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        new Thread() { // from class: com.android.server.updates.ConfigUpdateInstallReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    X509Certificate cert = ConfigUpdateInstallReceiver.this.getCert(context.getContentResolver());
                    byte[] altContent = ConfigUpdateInstallReceiver.this.getAltContent(intent);
                    int altVersion = ConfigUpdateInstallReceiver.this.getVersionFromIntent(intent);
                    String altRequiredHash = ConfigUpdateInstallReceiver.this.getRequiredHashFromIntent(intent);
                    String altSig = ConfigUpdateInstallReceiver.this.getSignatureFromIntent(intent);
                    int currentVersion = ConfigUpdateInstallReceiver.this.getCurrentVersion();
                    String currentHash = ConfigUpdateInstallReceiver.getCurrentHash(ConfigUpdateInstallReceiver.this.getCurrentContent());
                    if (ConfigUpdateInstallReceiver.this.verifyVersion(currentVersion, altVersion)) {
                        if (ConfigUpdateInstallReceiver.this.verifyPreviousHash(currentHash, altRequiredHash)) {
                            if (!ConfigUpdateInstallReceiver.this.verifySignature(altContent, altVersion, altRequiredHash, altSig, cert)) {
                                EventLog.writeEvent((int) EventLogTags.CONFIG_INSTALL_FAILED, "Signature did not verify");
                            } else {
                                Slog.i(ConfigUpdateInstallReceiver.TAG, "Found new update, installing...");
                                ConfigUpdateInstallReceiver.this.install(altContent, altVersion);
                                Slog.i(ConfigUpdateInstallReceiver.TAG, "Installation successful");
                                ConfigUpdateInstallReceiver.this.postInstall(context, intent);
                            }
                        } else {
                            EventLog.writeEvent((int) EventLogTags.CONFIG_INSTALL_FAILED, "Current hash did not match required value");
                        }
                    } else {
                        Slog.i(ConfigUpdateInstallReceiver.TAG, "Not installing, new version is <= current version");
                    }
                } catch (Exception e) {
                    Slog.e(ConfigUpdateInstallReceiver.TAG, "Could not update content!", e);
                    String errMsg = e.toString();
                    if (errMsg.length() > 100) {
                        errMsg = errMsg.substring(0, 99);
                    }
                    EventLog.writeEvent((int) EventLogTags.CONFIG_INSTALL_FAILED, errMsg);
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public X509Certificate getCert(ContentResolver cr) {
        String cert = Settings.Secure.getString(cr, UPDATE_CERTIFICATE_KEY);
        try {
            byte[] derCert = Base64.decode(cert.getBytes(), 0);
            InputStream istream = new ByteArrayInputStream(derCert);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(istream);
        } catch (CertificateException e) {
            throw new IllegalStateException("Got malformed certificate from settings, ignoring");
        }
    }

    private String getContentFromIntent(Intent i) {
        String extraValue = i.getStringExtra(EXTRA_CONTENT_PATH);
        if (extraValue == null) {
            throw new IllegalStateException("Missing required content path, ignoring.");
        }
        return extraValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getVersionFromIntent(Intent i) throws NumberFormatException {
        String extraValue = i.getStringExtra(EXTRA_VERSION_NUMBER);
        if (extraValue == null) {
            throw new IllegalStateException("Missing required version number, ignoring.");
        }
        return Integer.parseInt(extraValue.trim());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getRequiredHashFromIntent(Intent i) {
        String extraValue = i.getStringExtra(EXTRA_REQUIRED_HASH);
        if (extraValue == null) {
            throw new IllegalStateException("Missing required previous hash, ignoring.");
        }
        return extraValue.trim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getSignatureFromIntent(Intent i) {
        String extraValue = i.getStringExtra(EXTRA_SIGNATURE);
        if (extraValue == null) {
            throw new IllegalStateException("Missing required signature, ignoring.");
        }
        return extraValue.trim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCurrentVersion() throws NumberFormatException {
        try {
            String strVersion = IoUtils.readFileAsString(this.updateVersion.getCanonicalPath()).trim();
            return Integer.parseInt(strVersion);
        } catch (IOException e) {
            Slog.i(TAG, "Couldn't find current metadata, assuming first update");
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] getAltContent(Intent i) throws IOException {
        return IoUtils.readFileAsByteArray(getContentFromIntent(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] getCurrentContent() {
        try {
            return IoUtils.readFileAsByteArray(this.updateContent.getCanonicalPath());
        } catch (IOException e) {
            Slog.i(TAG, "Failed to read current content, assuming first update!");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getCurrentHash(byte[] content) {
        if (content == null) {
            return "0";
        }
        try {
            MessageDigest dgst = MessageDigest.getInstance("SHA512");
            byte[] fingerprint = dgst.digest(content);
            return IntegralToString.bytesToHexString(fingerprint, false);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean verifyVersion(int current, int alternative) {
        return current < alternative;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean verifyPreviousHash(String current, String required) {
        if (required.equals("NONE")) {
            return true;
        }
        return current.equals(required);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean verifySignature(byte[] content, int version, String requiredPrevious, String signature, X509Certificate cert) throws Exception {
        Signature signer = Signature.getInstance("SHA512withRSA");
        signer.initVerify(cert);
        signer.update(content);
        signer.update(Long.toString(version).getBytes());
        signer.update(requiredPrevious.getBytes());
        return signer.verify(Base64.decode(signature.getBytes(), 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void install(byte[] content, int version) throws IOException {
        writeUpdate(this.updateDir, this.updateContent, content);
        writeUpdate(this.updateDir, this.updateVersion, Long.toString(version).getBytes());
    }

    protected void postInstall(Context context, Intent intent) {
    }
}