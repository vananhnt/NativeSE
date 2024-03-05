package android.nfc.tech;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.RemoteException;
import java.io.IOException;

/* loaded from: NfcV.class */
public final class NfcV extends BasicTagTechnology {
    public static final String EXTRA_RESP_FLAGS = "respflags";
    public static final String EXTRA_DSFID = "dsfid";
    private byte mRespFlags;
    private byte mDsfId;

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

    public static NfcV get(Tag tag) {
        if (tag.hasTech(5)) {
            try {
                return new NfcV(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NfcV(Tag tag) throws RemoteException {
        super(tag, 5);
        Bundle extras = tag.getTechExtras(5);
        this.mRespFlags = extras.getByte(EXTRA_RESP_FLAGS);
        this.mDsfId = extras.getByte(EXTRA_DSFID);
    }

    public byte getResponseFlags() {
        return this.mRespFlags;
    }

    public byte getDsfId() {
        return this.mDsfId;
    }

    public byte[] transceive(byte[] data) throws IOException {
        return transceive(data, true);
    }

    public int getMaxTransceiveLength() {
        return getMaxTransceiveLengthInternal();
    }
}