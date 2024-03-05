package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;

/* loaded from: ClipDescription.class */
public class ClipDescription implements Parcelable {
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_TEXT_HTML = "text/html";
    public static final String MIMETYPE_TEXT_URILIST = "text/uri-list";
    public static final String MIMETYPE_TEXT_INTENT = "text/vnd.android.intent";
    final CharSequence mLabel;
    final String[] mMimeTypes;
    public static final Parcelable.Creator<ClipDescription> CREATOR = new Parcelable.Creator<ClipDescription>() { // from class: android.content.ClipDescription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClipDescription createFromParcel(Parcel source) {
            return new ClipDescription(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClipDescription[] newArray(int size) {
            return new ClipDescription[size];
        }
    };

    public ClipDescription(CharSequence label, String[] mimeTypes) {
        if (mimeTypes == null) {
            throw new NullPointerException("mimeTypes is null");
        }
        this.mLabel = label;
        this.mMimeTypes = mimeTypes;
    }

    public ClipDescription(ClipDescription o) {
        this.mLabel = o.mLabel;
        this.mMimeTypes = o.mMimeTypes;
    }

    public static boolean compareMimeTypes(String concreteType, String desiredType) {
        int typeLength = desiredType.length();
        if (typeLength == 3 && desiredType.equals("*/*")) {
            return true;
        }
        int slashpos = desiredType.indexOf(47);
        if (slashpos > 0) {
            if (typeLength == slashpos + 2 && desiredType.charAt(slashpos + 1) == '*') {
                if (desiredType.regionMatches(0, concreteType, 0, slashpos + 1)) {
                    return true;
                }
                return false;
            } else if (desiredType.equals(concreteType)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public boolean hasMimeType(String mimeType) {
        for (int i = 0; i < this.mMimeTypes.length; i++) {
            if (compareMimeTypes(this.mMimeTypes[i], mimeType)) {
                return true;
            }
        }
        return false;
    }

    public String[] filterMimeTypes(String mimeType) {
        ArrayList<String> array = null;
        for (int i = 0; i < this.mMimeTypes.length; i++) {
            if (compareMimeTypes(this.mMimeTypes[i], mimeType)) {
                if (array == null) {
                    array = new ArrayList<>();
                }
                array.add(this.mMimeTypes[i]);
            }
        }
        if (array == null) {
            return null;
        }
        String[] rawArray = new String[array.size()];
        array.toArray(rawArray);
        return rawArray;
    }

    public int getMimeTypeCount() {
        return this.mMimeTypes.length;
    }

    public String getMimeType(int index) {
        return this.mMimeTypes[index];
    }

    public void validate() {
        if (this.mMimeTypes == null) {
            throw new NullPointerException("null mime types");
        }
        if (this.mMimeTypes.length <= 0) {
            throw new IllegalArgumentException("must have at least 1 mime type");
        }
        for (int i = 0; i < this.mMimeTypes.length; i++) {
            if (this.mMimeTypes[i] == null) {
                throw new NullPointerException("mime type at " + i + " is null");
            }
        }
    }

    public String toString() {
        StringBuilder b = new StringBuilder(128);
        b.append("ClipDescription { ");
        toShortString(b);
        b.append(" }");
        return b.toString();
    }

    public boolean toShortString(StringBuilder b) {
        boolean first = true;
        for (int i = 0; i < this.mMimeTypes.length; i++) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append(this.mMimeTypes[i]);
        }
        if (this.mLabel != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append('\"');
            b.append(this.mLabel);
            b.append('\"');
        }
        return !first;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.mLabel, dest, flags);
        dest.writeStringArray(this.mMimeTypes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClipDescription(Parcel in) {
        this.mLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mMimeTypes = in.createStringArray();
    }
}