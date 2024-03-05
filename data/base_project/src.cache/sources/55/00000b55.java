package android.os;

import android.os.Parcelable;

/* loaded from: PatternMatcher.class */
public class PatternMatcher implements Parcelable {
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    private final String mPattern;
    private final int mType;
    public static final Parcelable.Creator<PatternMatcher> CREATOR = new Parcelable.Creator<PatternMatcher>() { // from class: android.os.PatternMatcher.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher createFromParcel(Parcel source) {
            return new PatternMatcher(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher[] newArray(int size) {
            return new PatternMatcher[size];
        }
    };

    public PatternMatcher(String pattern, int type) {
        this.mPattern = pattern;
        this.mType = type;
    }

    public final String getPath() {
        return this.mPattern;
    }

    public final int getType() {
        return this.mType;
    }

    public boolean match(String str) {
        return matchPattern(this.mPattern, str, this.mType);
    }

    public String toString() {
        String type = "? ";
        switch (this.mType) {
            case 0:
                type = "LITERAL: ";
                break;
            case 1:
                type = "PREFIX: ";
                break;
            case 2:
                type = "GLOB: ";
                break;
        }
        return "PatternMatcher{" + type + this.mPattern + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPattern);
        dest.writeInt(this.mType);
    }

    public PatternMatcher(Parcel src) {
        this.mPattern = src.readString();
        this.mType = src.readInt();
    }

    static boolean matchPattern(String pattern, String match, int type) {
        if (match == null) {
            return false;
        }
        if (type == 0) {
            return pattern.equals(match);
        }
        if (type == 1) {
            return match.startsWith(pattern);
        }
        if (type != 2) {
            return false;
        }
        int NP = pattern.length();
        if (NP <= 0) {
            return match.length() <= 0;
        }
        int NM = match.length();
        int ip = 0;
        int im = 0;
        char nextChar = pattern.charAt(0);
        while (ip < NP && im < NM) {
            char c = nextChar;
            ip++;
            nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
            boolean escaped = c == '\\';
            if (escaped) {
                c = nextChar;
                ip++;
                nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
            }
            if (nextChar == '*') {
                if (!escaped && c == '.') {
                    if (ip >= NP - 1) {
                        return true;
                    }
                    int ip2 = ip + 1;
                    char nextChar2 = pattern.charAt(ip2);
                    if (nextChar2 == '\\') {
                        ip2++;
                        nextChar2 = ip2 < NP ? pattern.charAt(ip2) : (char) 0;
                    }
                    while (match.charAt(im) != nextChar2) {
                        im++;
                        if (im >= NM) {
                            break;
                        }
                    }
                    if (im == NM) {
                        return false;
                    }
                    ip = ip2 + 1;
                    nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
                    im++;
                } else {
                    while (match.charAt(im) == c) {
                        im++;
                        if (im >= NM) {
                            break;
                        }
                    }
                    ip++;
                    nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
                }
            } else if (c != '.' && match.charAt(im) != c) {
                return false;
            } else {
                im++;
            }
        }
        if (ip >= NP && im >= NM) {
            return true;
        }
        if (ip == NP - 2 && pattern.charAt(ip) == '.' && pattern.charAt(ip + 1) == '*') {
            return true;
        }
        return false;
    }
}