package android.net.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/* loaded from: DnsSdTxtRecord.class */
public class DnsSdTxtRecord implements Parcelable {
    private static final byte mSeperator = 61;
    private byte[] mData;
    public static final Parcelable.Creator<DnsSdTxtRecord> CREATOR = new Parcelable.Creator<DnsSdTxtRecord>() { // from class: android.net.nsd.DnsSdTxtRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DnsSdTxtRecord createFromParcel(Parcel in) {
            DnsSdTxtRecord info = new DnsSdTxtRecord();
            in.readByteArray(info.mData);
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DnsSdTxtRecord[] newArray(int size) {
            return new DnsSdTxtRecord[size];
        }
    };

    public DnsSdTxtRecord() {
        this.mData = new byte[0];
    }

    public DnsSdTxtRecord(byte[] data) {
        this.mData = (byte[]) data.clone();
    }

    public DnsSdTxtRecord(DnsSdTxtRecord src) {
        if (src != null && src.mData != null) {
            this.mData = (byte[]) src.mData.clone();
        }
    }

    public void set(String key, String value) {
        byte[] valBytes;
        int valLen;
        if (value != null) {
            valBytes = value.getBytes();
            valLen = valBytes.length;
        } else {
            valBytes = null;
            valLen = 0;
        }
        try {
            byte[] keyBytes = key.getBytes("US-ASCII");
            for (byte b : keyBytes) {
                if (b == 61) {
                    throw new IllegalArgumentException("= is not a valid character in key");
                }
            }
            if (keyBytes.length + valLen >= 255) {
                throw new IllegalArgumentException("Key and Value length cannot exceed 255 bytes");
            }
            int currentLoc = remove(key);
            if (currentLoc == -1) {
                currentLoc = keyCount();
            }
            insert(keyBytes, valBytes, currentLoc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("key should be US-ASCII");
        }
    }

    public String get(String key) {
        byte[] val = getValue(key);
        if (val != null) {
            return new String(val);
        }
        return null;
    }

    public int remove(String key) {
        int avStart = 0;
        int i = 0;
        while (avStart < this.mData.length) {
            byte b = this.mData[avStart];
            if (key.length() <= b && (key.length() == b || this.mData[avStart + key.length() + 1] == 61)) {
                String s = new String(this.mData, avStart + 1, key.length());
                if (0 == key.compareToIgnoreCase(s)) {
                    byte[] oldBytes = this.mData;
                    this.mData = new byte[(oldBytes.length - b) - 1];
                    System.arraycopy(oldBytes, 0, this.mData, 0, avStart);
                    System.arraycopy(oldBytes, avStart + b + 1, this.mData, avStart, ((oldBytes.length - avStart) - b) - 1);
                    return i;
                }
            }
            avStart += 255 & (b + 1);
            i++;
        }
        return -1;
    }

    public int keyCount() {
        int count = 0;
        int nextKey = 0;
        while (nextKey < this.mData.length) {
            nextKey += 255 & (this.mData[nextKey] + 1);
            count++;
        }
        return count;
    }

    public boolean contains(String key) {
        int i = 0;
        while (true) {
            String s = getKey(i);
            if (null != s) {
                if (0 == key.compareToIgnoreCase(s)) {
                    return true;
                }
                i++;
            } else {
                return false;
            }
        }
    }

    public int size() {
        return this.mData.length;
    }

    public byte[] getRawData() {
        return (byte[]) this.mData.clone();
    }

    private void insert(byte[] keyBytes, byte[] value, int index) {
        byte[] oldBytes = this.mData;
        int valLen = value != null ? value.length : 0;
        int insertion = 0;
        for (int i = 0; i < index && insertion < this.mData.length; i++) {
            insertion += 255 & (this.mData[insertion] + 1);
        }
        int avLen = keyBytes.length + valLen + (value != null ? 1 : 0);
        int newLen = avLen + oldBytes.length + 1;
        this.mData = new byte[newLen];
        System.arraycopy(oldBytes, 0, this.mData, 0, insertion);
        int secondHalfLen = oldBytes.length - insertion;
        System.arraycopy(oldBytes, insertion, this.mData, newLen - secondHalfLen, secondHalfLen);
        this.mData[insertion] = (byte) avLen;
        System.arraycopy(keyBytes, 0, this.mData, insertion + 1, keyBytes.length);
        if (value != null) {
            this.mData[insertion + 1 + keyBytes.length] = 61;
            System.arraycopy(value, 0, this.mData, insertion + keyBytes.length + 2, valLen);
        }
    }

    private String getKey(int index) {
        int avStart = 0;
        for (int i = 0; i < index && avStart < this.mData.length; i++) {
            avStart += this.mData[avStart] + 1;
        }
        if (avStart < this.mData.length) {
            byte b = this.mData[avStart];
            int aLen = 0;
            while (aLen < b && this.mData[avStart + aLen + 1] != 61) {
                aLen++;
            }
            return new String(this.mData, avStart + 1, aLen);
        }
        return null;
    }

    private byte[] getValue(int index) {
        int avStart = 0;
        byte[] value = null;
        for (int i = 0; i < index && avStart < this.mData.length; i++) {
            avStart += this.mData[avStart] + 1;
        }
        if (avStart < this.mData.length) {
            byte b = this.mData[avStart];
            int aLen = 0;
            while (true) {
                if (aLen < b) {
                    if (this.mData[avStart + aLen + 1] != 61) {
                        aLen++;
                    } else {
                        value = new byte[(b - aLen) - 1];
                        System.arraycopy(this.mData, avStart + aLen + 2, value, 0, (b - aLen) - 1);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return value;
    }

    private String getValueAsString(int index) {
        byte[] value = getValue(index);
        if (value != null) {
            return new String(value);
        }
        return null;
    }

    private byte[] getValue(String forKey) {
        int i = 0;
        while (true) {
            String s = getKey(i);
            if (null != s) {
                if (0 != forKey.compareToIgnoreCase(s)) {
                    i++;
                } else {
                    return getValue(i);
                }
            } else {
                return null;
            }
        }
    }

    public String toString() {
        String av;
        String str;
        String result = null;
        int i = 0;
        while (true) {
            String a = getKey(i);
            if (null == a) {
                break;
            }
            String av2 = "{" + a;
            String val = getValueAsString(i);
            if (val != null) {
                av = av2 + Separators.EQUALS + val + "}";
            } else {
                av = av2 + "}";
            }
            if (result == null) {
                str = av;
            } else {
                str = result + ", " + av;
            }
            result = str;
            i++;
        }
        return result != null ? result : "";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DnsSdTxtRecord)) {
            return false;
        }
        DnsSdTxtRecord record = (DnsSdTxtRecord) o;
        return Arrays.equals(record.mData, this.mData);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mData);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.mData);
    }
}