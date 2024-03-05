package android.nfc.tech;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;

/* loaded from: NfcF.class */
public final class NfcF extends BasicTagTechnology {
    private static final String TAG = "NFC";
    public static final String EXTRA_SC = "systemcode";
    public static final String EXTRA_PMM = "pmm";
    private byte[] mSystemCode;
    private byte[] mManufacturer;

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology, java.io.Closeable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ void reconnect() throws IOException {
        super.reconnect();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ void connect() throws IOException {
        super.connect();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ boolean isConnected() {
        return super.isConnected();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ Tag getTag() {
        return super.getTag();
    }

    public static NfcF get(Tag tag) {
        if (tag.hasTech(4)) {
            try {
                return new NfcF(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NfcF(Tag tag) throws RemoteException {
        super(tag, 4);
        this.mSystemCode = null;
        this.mManufacturer = null;
        Bundle extras = tag.getTechExtras(4);
        if (extras != null) {
            this.mSystemCode = extras.getByteArray(EXTRA_SC);
            this.mManufacturer = extras.getByteArray(EXTRA_PMM);
        }
    }

    public byte[] getSystemCode() {
        return this.mSystemCode;
    }

    public byte[] getManufacturer() {
        return this.mManufacturer;
    }

    public byte[] transceive(byte[] data) throws IOException {
        return transceive(data, true);
    }

    public int getMaxTransceiveLength() {
        return getMaxTransceiveLengthInternal();
    }

    public void setTimeout(int timeout) {
        try {
            int err = this.mTag.getTagService().setTimeout(4, timeout);
            if (err != 0) {
                throw new IllegalArgumentException("The supplied timeout is not valid");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "NFC service dead", e);
        }
    }

    public int getTimeout() {
        try {
            return this.mTag.getTagService().getTimeout(4);
        } catch (RemoteException e) {
            Log.e(TAG, "NFC service dead", e);
            return 0;
        }
    }
}