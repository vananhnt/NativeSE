package android.os;

import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.android.internal.os.IDropBoxManagerService;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/* loaded from: DropBoxManager.class */
public class DropBoxManager {
    private static final String TAG = "DropBoxManager";
    private final IDropBoxManagerService mService;
    public static final int IS_EMPTY = 1;
    public static final int IS_TEXT = 2;
    public static final int IS_GZIPPED = 4;
    private static final int HAS_BYTE_ARRAY = 8;
    public static final String ACTION_DROPBOX_ENTRY_ADDED = "android.intent.action.DROPBOX_ENTRY_ADDED";
    public static final String EXTRA_TAG = "tag";
    public static final String EXTRA_TIME = "time";

    /* loaded from: DropBoxManager$Entry.class */
    public static class Entry implements Parcelable, Closeable {
        private final String mTag;
        private final long mTimeMillis;
        private final byte[] mData;
        private final ParcelFileDescriptor mFileDescriptor;
        private final int mFlags;
        public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator() { // from class: android.os.DropBoxManager.Entry.1
            @Override // android.os.Parcelable.Creator
            public Entry[] newArray(int size) {
                return new Entry[size];
            }

            @Override // android.os.Parcelable.Creator
            public Entry createFromParcel(Parcel in) {
                String tag = in.readString();
                long millis = in.readLong();
                int flags = in.readInt();
                if ((flags & 8) != 0) {
                    return new Entry(tag, millis, in.createByteArray(), flags & (-9));
                }
                return new Entry(tag, millis, in.readFileDescriptor(), flags);
            }
        };

        public Entry(String tag, long millis) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = null;
            this.mFileDescriptor = null;
            this.mFlags = 1;
        }

        public Entry(String tag, long millis, String text) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if (text == null) {
                throw new NullPointerException("text == null");
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = text.getBytes();
            this.mFileDescriptor = null;
            this.mFlags = 2;
        }

        public Entry(String tag, long millis, byte[] data, int flags) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if (((flags & 1) != 0) != (data == null)) {
                throw new IllegalArgumentException("Bad flags: " + flags);
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = data;
            this.mFileDescriptor = null;
            this.mFlags = flags;
        }

        public Entry(String tag, long millis, ParcelFileDescriptor data, int flags) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if (((flags & 1) != 0) != (data == null)) {
                throw new IllegalArgumentException("Bad flags: " + flags);
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = null;
            this.mFileDescriptor = data;
            this.mFlags = flags;
        }

        public Entry(String tag, long millis, File data, int flags) throws IOException {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if ((flags & 1) != 0) {
                throw new IllegalArgumentException("Bad flags: " + flags);
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = null;
            this.mFileDescriptor = ParcelFileDescriptor.open(data, 268435456);
            this.mFlags = flags;
        }

        @Override // java.io.Closeable
        public void close() {
            try {
                if (this.mFileDescriptor != null) {
                    this.mFileDescriptor.close();
                }
            } catch (IOException e) {
            }
        }

        public String getTag() {
            return this.mTag;
        }

        public long getTimeMillis() {
            return this.mTimeMillis;
        }

        public int getFlags() {
            return this.mFlags & (-5);
        }

        public String getText(int maxBytes) {
            if ((this.mFlags & 2) == 0) {
                return null;
            }
            if (this.mData != null) {
                return new String(this.mData, 0, Math.min(maxBytes, this.mData.length));
            }
            InputStream is = null;
            try {
                is = getInputStream();
                if (is != null) {
                    byte[] buf = new byte[maxBytes];
                    int readBytes = 0;
                    int n = 0;
                    while (n >= 0) {
                        int i = readBytes + n;
                        readBytes = i;
                        if (i >= maxBytes) {
                            break;
                        }
                        n = is.read(buf, readBytes, maxBytes - readBytes);
                    }
                    String str = new String(buf, 0, readBytes);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                    return str;
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                }
                return null;
            } catch (IOException e3) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e4) {
                        return null;
                    }
                }
                return null;
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e5) {
                        throw th;
                    }
                }
                throw th;
            }
        }

        public InputStream getInputStream() throws IOException {
            InputStream is;
            if (this.mData != null) {
                is = new ByteArrayInputStream(this.mData);
            } else if (this.mFileDescriptor != null) {
                is = new ParcelFileDescriptor.AutoCloseInputStream(this.mFileDescriptor);
            } else {
                return null;
            }
            return (this.mFlags & 4) != 0 ? new GZIPInputStream(is) : is;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return this.mFileDescriptor != null ? 1 : 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this.mTag);
            out.writeLong(this.mTimeMillis);
            if (this.mFileDescriptor != null) {
                out.writeInt(this.mFlags & (-9));
                this.mFileDescriptor.writeToParcel(out, flags);
                return;
            }
            out.writeInt(this.mFlags | 8);
            out.writeByteArray(this.mData);
        }
    }

    public DropBoxManager(IDropBoxManagerService service) {
        this.mService = service;
    }

    protected DropBoxManager() {
        this.mService = null;
    }

    public void addText(String tag, String data) {
        try {
            this.mService.add(new Entry(tag, 0L, data));
        } catch (RemoteException e) {
        }
    }

    public void addData(String tag, byte[] data, int flags) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        try {
            this.mService.add(new Entry(tag, 0L, data, flags));
        } catch (RemoteException e) {
        }
    }

    public void addFile(String tag, File file, int flags) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Entry entry = new Entry(tag, 0L, file, flags);
        try {
            this.mService.add(entry);
            entry.close();
        } catch (RemoteException e) {
            entry.close();
        } catch (Throwable th) {
            entry.close();
            throw th;
        }
    }

    public boolean isTagEnabled(String tag) {
        try {
            return this.mService.isTagEnabled(tag);
        } catch (RemoteException e) {
            return false;
        }
    }

    public Entry getNextEntry(String tag, long msec) {
        try {
            return this.mService.getNextEntry(tag, msec);
        } catch (RemoteException e) {
            return null;
        }
    }
}