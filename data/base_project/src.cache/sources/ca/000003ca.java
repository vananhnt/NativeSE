package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.StateSet;
import android.util.Xml;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: ColorStateList.class */
public class ColorStateList implements Parcelable {
    private int[][] mStateSpecs;
    private int[] mColors;
    private int mDefaultColor;
    private static final int[][] EMPTY = {new int[0]};
    private static final SparseArray<WeakReference<ColorStateList>> sCache = new SparseArray<>();
    public static final Parcelable.Creator<ColorStateList> CREATOR = new Parcelable.Creator<ColorStateList>() { // from class: android.content.res.ColorStateList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ColorStateList[] newArray(int size) {
            return new ColorStateList[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        /* JADX WARN: Type inference failed for: r0v3, types: [int[], int[][]] */
        @Override // android.os.Parcelable.Creator
        public ColorStateList createFromParcel(Parcel source) {
            int N = source.readInt();
            ?? r0 = new int[N];
            for (int i = 0; i < N; i++) {
                r0[i] = source.createIntArray();
            }
            int[] colors = source.createIntArray();
            return new ColorStateList(r0, colors);
        }
    };

    private ColorStateList() {
        this.mDefaultColor = -65536;
    }

    public ColorStateList(int[][] states, int[] colors) {
        this.mDefaultColor = -65536;
        this.mStateSpecs = states;
        this.mColors = colors;
        if (states.length > 0) {
            this.mDefaultColor = colors[0];
            for (int i = 0; i < states.length; i++) {
                if (states[i].length == 0) {
                    this.mDefaultColor = colors[i];
                }
            }
        }
    }

    public static ColorStateList valueOf(int color) {
        synchronized (sCache) {
            WeakReference<ColorStateList> ref = sCache.get(color);
            ColorStateList csl = ref != null ? ref.get() : null;
            if (csl != null) {
                return csl;
            }
            ColorStateList csl2 = new ColorStateList(EMPTY, new int[]{color});
            sCache.put(color, new WeakReference<>(csl2));
            return csl2;
        }
    }

    public static ColorStateList createFromXml(Resources r, XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(r, parser, attrs);
    }

    private static ColorStateList createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (name.equals("selector")) {
            ColorStateList colorStateList = new ColorStateList();
            colorStateList.inflate(r, parser, attrs);
            return colorStateList;
        }
        throw new XmlPullParserException(parser.getPositionDescription() + ": invalid drawable tag " + name);
    }

    public ColorStateList withAlpha(int alpha) {
        int[] colors = new int[this.mColors.length];
        int len = colors.length;
        for (int i = 0; i < len; i++) {
            colors[i] = (this.mColors[i] & 16777215) | (alpha << 24);
        }
        return new ColorStateList(this.mStateSpecs, colors);
    }

    /* JADX WARN: Code restructure failed: missing block: B:50:0x0182, code lost:
        r6.mColors = new int[r14];
        r6.mStateSpecs = new int[r14];
        java.lang.System.arraycopy(r15, 0, r6.mColors, 0, r14);
        java.lang.System.arraycopy(r16, 0, r6.mStateSpecs, 0, r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x01ad, code lost:
        return;
     */
    /* JADX WARN: Type inference failed for: r1v6, types: [int[], int[][]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void inflate(android.content.res.Resources r7, org.xmlpull.v1.XmlPullParser r8, android.util.AttributeSet r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 430
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.res.ColorStateList.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    public boolean isStateful() {
        return this.mStateSpecs.length > 1;
    }

    public int getColorForState(int[] stateSet, int defaultColor) {
        int setLength = this.mStateSpecs.length;
        for (int i = 0; i < setLength; i++) {
            int[] stateSpec = this.mStateSpecs[i];
            if (StateSet.stateSetMatches(stateSpec, stateSet)) {
                return this.mColors[i];
            }
        }
        return defaultColor;
    }

    public int getDefaultColor() {
        return this.mDefaultColor;
    }

    public String toString() {
        return "ColorStateList{mStateSpecs=" + Arrays.deepToString(this.mStateSpecs) + "mColors=" + Arrays.toString(this.mColors) + "mDefaultColor=" + this.mDefaultColor + '}';
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int N = this.mStateSpecs.length;
        dest.writeInt(N);
        for (int i = 0; i < N; i++) {
            dest.writeIntArray(this.mStateSpecs[i]);
        }
        dest.writeIntArray(this.mColors);
    }
}