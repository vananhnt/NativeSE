package com.android.server.updates;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Base64;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import libcore.io.ErrnoException;
import libcore.io.Libcore;

/* loaded from: SELinuxPolicyInstallReceiver.class */
public class SELinuxPolicyInstallReceiver extends ConfigUpdateInstallReceiver {
    private static final String TAG = "SELinuxPolicyInstallReceiver";
    private static final String sepolicyPath = "sepolicy";
    private static final String fileContextsPath = "file_contexts";
    private static final String propertyContextsPath = "property_contexts";
    private static final String seappContextsPath = "seapp_contexts";

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.updates.SELinuxPolicyInstallReceiver.unpackBundle():void, file: SELinuxPolicyInstallReceiver.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void unpackBundle() throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.updates.SELinuxPolicyInstallReceiver.unpackBundle():void, file: SELinuxPolicyInstallReceiver.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.updates.SELinuxPolicyInstallReceiver.unpackBundle():void");
    }

    public SELinuxPolicyInstallReceiver() {
        super("/data/security/bundle", "sepolicy_bundle", "metadata/", "version");
    }

    private void backupContexts(File contexts) {
        new File(contexts, seappContextsPath).renameTo(new File(contexts, "seapp_contexts_backup"));
        new File(contexts, propertyContextsPath).renameTo(new File(contexts, "property_contexts_backup"));
        new File(contexts, fileContextsPath).renameTo(new File(contexts, "file_contexts_backup"));
        new File(contexts, sepolicyPath).renameTo(new File(contexts, "sepolicy_backup"));
    }

    private void copyUpdate(File contexts) {
        new File(this.updateDir, seappContextsPath).renameTo(new File(contexts, seappContextsPath));
        new File(this.updateDir, propertyContextsPath).renameTo(new File(contexts, propertyContextsPath));
        new File(this.updateDir, fileContextsPath).renameTo(new File(contexts, fileContextsPath));
        new File(this.updateDir, sepolicyPath).renameTo(new File(contexts, sepolicyPath));
    }

    private int readInt(BufferedInputStream reader) throws IOException {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value = (value << 8) | reader.read();
        }
        return value;
    }

    private int[] readChunkLengths(BufferedInputStream bundle) throws IOException {
        int[] chunks = {readInt(bundle), readInt(bundle), readInt(bundle), readInt(bundle)};
        return chunks;
    }

    private void installFile(File destination, BufferedInputStream stream, int length) throws IOException {
        byte[] chunk = new byte[length];
        stream.read(chunk, 0, length);
        writeUpdate(this.updateDir, destination, Base64.decode(chunk, 0));
    }

    private void applyUpdate() throws IOException, ErrnoException {
        Slog.i(TAG, "Applying SELinux policy");
        File contexts = new File(this.updateDir.getParentFile(), "contexts");
        File current = new File(this.updateDir.getParentFile(), Telephony.Carriers.CURRENT);
        File update = new File(this.updateDir.getParentFile(), "update");
        File tmp = new File(this.updateDir.getParentFile(), "tmp");
        if (current.exists()) {
            Libcore.os.symlink(this.updateDir.getPath(), update.getPath());
            Libcore.os.rename(update.getPath(), current.getPath());
        } else {
            Libcore.os.symlink(this.updateDir.getPath(), current.getPath());
        }
        contexts.mkdirs();
        backupContexts(contexts);
        copyUpdate(contexts);
        Libcore.os.symlink(contexts.getPath(), tmp.getPath());
        Libcore.os.rename(tmp.getPath(), current.getPath());
        SystemProperties.set("selinux.reload_policy", "1");
    }

    private void setEnforcingMode(Context context) {
        String mode = Settings.Global.getString(context.getContentResolver(), Settings.Global.SELINUX_STATUS);
        if ("1".equals(mode)) {
            Slog.i(TAG, "Setting enforcing mode");
            SystemProperties.set("persist.selinux.enforcing", mode);
        } else if ("0".equals(mode)) {
            Slog.i(TAG, "Tried to set permissive mode, ignoring");
        } else {
            Slog.e(TAG, "Got invalid enforcing mode: " + mode);
        }
    }

    @Override // com.android.server.updates.ConfigUpdateInstallReceiver
    protected void postInstall(Context context, Intent intent) {
        try {
            unpackBundle();
            applyUpdate();
            setEnforcingMode(context);
        } catch (IOException e) {
            Slog.e(TAG, "Could not update selinux policy: ", e);
        } catch (IllegalArgumentException e2) {
            Slog.e(TAG, "SELinux policy update malformed: ", e2);
        } catch (ErrnoException e3) {
            Slog.e(TAG, "Could not update selinux policy: ", e3);
        }
    }
}