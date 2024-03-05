package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: LinkCapabilities.class */
public class LinkCapabilities implements Parcelable {
    private static final String TAG = "LinkCapabilities";
    private static final boolean DBG = false;
    private HashMap<Integer, String> mCapabilities;
    public static final Parcelable.Creator<LinkCapabilities> CREATOR = new Parcelable.Creator<LinkCapabilities>() { // from class: android.net.LinkCapabilities.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkCapabilities createFromParcel(Parcel in) {
            LinkCapabilities capabilities = new LinkCapabilities();
            int size = in.readInt();
            while (true) {
                int i = size;
                size--;
                if (i != 0) {
                    int key = in.readInt();
                    String value = in.readString();
                    capabilities.mCapabilities.put(Integer.valueOf(key), value);
                } else {
                    return capabilities;
                }
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkCapabilities[] newArray(int size) {
            return new LinkCapabilities[size];
        }
    };

    /* loaded from: LinkCapabilities$Key.class */
    public static final class Key {
        public static final int RO_NETWORK_TYPE = 1;
        public static final int RW_DESIRED_FWD_BW = 2;
        public static final int RW_REQUIRED_FWD_BW = 3;
        public static final int RO_AVAILABLE_FWD_BW = 4;
        public static final int RW_DESIRED_REV_BW = 5;
        public static final int RW_REQUIRED_REV_BW = 6;
        public static final int RO_AVAILABLE_REV_BW = 7;
        public static final int RW_MAX_ALLOWED_LATENCY = 8;
        public static final int RO_BOUND_INTERFACE = 9;
        public static final int RO_PHYSICAL_INTERFACE = 10;

        private Key() {
        }
    }

    /* loaded from: LinkCapabilities$Role.class */
    public static final class Role {
        public static final String DEFAULT = "default";
        public static final String BULK_DOWNLOAD = "bulk.download";
        public static final String BULK_UPLOAD = "bulk.upload";
        public static final String VOIP_24KBPS = "voip.24k";
        public static final String VOIP_32KBPS = "voip.32k";
        public static final String VIDEO_STREAMING_480P = "video.streaming.480p";
        public static final String VIDEO_STREAMING_720I = "video.streaming.720i";
        public static final String VIDEO_CHAT_360P = "video.chat.360p";
        public static final String VIDEO_CHAT_480P = "video.chat.480i";

        private Role() {
        }
    }

    public LinkCapabilities() {
        this.mCapabilities = new HashMap<>();
    }

    public LinkCapabilities(LinkCapabilities source) {
        if (source != null) {
            this.mCapabilities = new HashMap<>(source.mCapabilities);
        } else {
            this.mCapabilities = new HashMap<>();
        }
    }

    public static LinkCapabilities createNeedsMap(String applicationRole) {
        return new LinkCapabilities();
    }

    public void clear() {
        this.mCapabilities.clear();
    }

    public boolean isEmpty() {
        return this.mCapabilities.isEmpty();
    }

    public int size() {
        return this.mCapabilities.size();
    }

    public String get(int key) {
        return this.mCapabilities.get(Integer.valueOf(key));
    }

    public void put(int key, String value) {
        this.mCapabilities.put(Integer.valueOf(key), value);
    }

    public boolean containsKey(int key) {
        return this.mCapabilities.containsKey(Integer.valueOf(key));
    }

    public boolean containsValue(String value) {
        return this.mCapabilities.containsValue(value);
    }

    public Set<Map.Entry<Integer, String>> entrySet() {
        return this.mCapabilities.entrySet();
    }

    public Set<Integer> keySet() {
        return this.mCapabilities.keySet();
    }

    public Collection<String> values() {
        return this.mCapabilities.values();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean firstTime = true;
        for (Map.Entry<Integer, String> entry : this.mCapabilities.entrySet()) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(Separators.COMMA);
            }
            sb.append(entry.getKey());
            sb.append(":\"");
            sb.append(entry.getValue());
            sb.append(Separators.DOUBLE_QUOTE);
        }
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCapabilities.size());
        for (Map.Entry<Integer, String> entry : this.mCapabilities.entrySet()) {
            dest.writeInt(entry.getKey().intValue());
            dest.writeString(entry.getValue());
        }
    }

    protected static void log(String s) {
        Log.d(TAG, s);
    }
}