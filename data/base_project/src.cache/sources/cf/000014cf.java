package android.view;

import android.content.res.CompatibilityInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import gov.nist.core.Separators;
import libcore.util.Objects;

/* loaded from: DisplayInfo.class */
public final class DisplayInfo implements Parcelable {
    public int layerStack;
    public int flags;
    public int type;
    public String address;
    public String name;
    public int appWidth;
    public int appHeight;
    public int smallestNominalAppWidth;
    public int smallestNominalAppHeight;
    public int largestNominalAppWidth;
    public int largestNominalAppHeight;
    public int logicalWidth;
    public int logicalHeight;
    public int overscanLeft;
    public int overscanTop;
    public int overscanRight;
    public int overscanBottom;
    public int rotation;
    public float refreshRate;
    public int logicalDensityDpi;
    public float physicalXDpi;
    public float physicalYDpi;
    public int ownerUid;
    public String ownerPackageName;
    public static final Parcelable.Creator<DisplayInfo> CREATOR = new Parcelable.Creator<DisplayInfo>() { // from class: android.view.DisplayInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DisplayInfo createFromParcel(Parcel source) {
            return new DisplayInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DisplayInfo[] newArray(int size) {
            return new DisplayInfo[size];
        }
    };

    public DisplayInfo() {
    }

    public DisplayInfo(DisplayInfo other) {
        copyFrom(other);
    }

    private DisplayInfo(Parcel source) {
        readFromParcel(source);
    }

    public boolean equals(Object o) {
        return (o instanceof DisplayInfo) && equals((DisplayInfo) o);
    }

    public boolean equals(DisplayInfo other) {
        return other != null && this.layerStack == other.layerStack && this.flags == other.flags && this.type == other.type && Objects.equal(this.address, other.address) && Objects.equal(this.name, other.name) && this.appWidth == other.appWidth && this.appHeight == other.appHeight && this.smallestNominalAppWidth == other.smallestNominalAppWidth && this.smallestNominalAppHeight == other.smallestNominalAppHeight && this.largestNominalAppWidth == other.largestNominalAppWidth && this.largestNominalAppHeight == other.largestNominalAppHeight && this.logicalWidth == other.logicalWidth && this.logicalHeight == other.logicalHeight && this.overscanLeft == other.overscanLeft && this.overscanTop == other.overscanTop && this.overscanRight == other.overscanRight && this.overscanBottom == other.overscanBottom && this.rotation == other.rotation && this.refreshRate == other.refreshRate && this.logicalDensityDpi == other.logicalDensityDpi && this.physicalXDpi == other.physicalXDpi && this.physicalYDpi == other.physicalYDpi && this.ownerUid == other.ownerUid && Objects.equal(this.ownerPackageName, other.ownerPackageName);
    }

    public int hashCode() {
        return 0;
    }

    public void copyFrom(DisplayInfo other) {
        this.layerStack = other.layerStack;
        this.flags = other.flags;
        this.type = other.type;
        this.address = other.address;
        this.name = other.name;
        this.appWidth = other.appWidth;
        this.appHeight = other.appHeight;
        this.smallestNominalAppWidth = other.smallestNominalAppWidth;
        this.smallestNominalAppHeight = other.smallestNominalAppHeight;
        this.largestNominalAppWidth = other.largestNominalAppWidth;
        this.largestNominalAppHeight = other.largestNominalAppHeight;
        this.logicalWidth = other.logicalWidth;
        this.logicalHeight = other.logicalHeight;
        this.overscanLeft = other.overscanLeft;
        this.overscanTop = other.overscanTop;
        this.overscanRight = other.overscanRight;
        this.overscanBottom = other.overscanBottom;
        this.rotation = other.rotation;
        this.refreshRate = other.refreshRate;
        this.logicalDensityDpi = other.logicalDensityDpi;
        this.physicalXDpi = other.physicalXDpi;
        this.physicalYDpi = other.physicalYDpi;
        this.ownerUid = other.ownerUid;
        this.ownerPackageName = other.ownerPackageName;
    }

    public void readFromParcel(Parcel source) {
        this.layerStack = source.readInt();
        this.flags = source.readInt();
        this.type = source.readInt();
        this.address = source.readString();
        this.name = source.readString();
        this.appWidth = source.readInt();
        this.appHeight = source.readInt();
        this.smallestNominalAppWidth = source.readInt();
        this.smallestNominalAppHeight = source.readInt();
        this.largestNominalAppWidth = source.readInt();
        this.largestNominalAppHeight = source.readInt();
        this.logicalWidth = source.readInt();
        this.logicalHeight = source.readInt();
        this.overscanLeft = source.readInt();
        this.overscanTop = source.readInt();
        this.overscanRight = source.readInt();
        this.overscanBottom = source.readInt();
        this.rotation = source.readInt();
        this.refreshRate = source.readFloat();
        this.logicalDensityDpi = source.readInt();
        this.physicalXDpi = source.readFloat();
        this.physicalYDpi = source.readFloat();
        this.ownerUid = source.readInt();
        this.ownerPackageName = source.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.layerStack);
        dest.writeInt(this.flags);
        dest.writeInt(this.type);
        dest.writeString(this.address);
        dest.writeString(this.name);
        dest.writeInt(this.appWidth);
        dest.writeInt(this.appHeight);
        dest.writeInt(this.smallestNominalAppWidth);
        dest.writeInt(this.smallestNominalAppHeight);
        dest.writeInt(this.largestNominalAppWidth);
        dest.writeInt(this.largestNominalAppHeight);
        dest.writeInt(this.logicalWidth);
        dest.writeInt(this.logicalHeight);
        dest.writeInt(this.overscanLeft);
        dest.writeInt(this.overscanTop);
        dest.writeInt(this.overscanRight);
        dest.writeInt(this.overscanBottom);
        dest.writeInt(this.rotation);
        dest.writeFloat(this.refreshRate);
        dest.writeInt(this.logicalDensityDpi);
        dest.writeFloat(this.physicalXDpi);
        dest.writeFloat(this.physicalYDpi);
        dest.writeInt(this.ownerUid);
        dest.writeString(this.ownerPackageName);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void getAppMetrics(DisplayMetrics outMetrics) {
        getAppMetrics(outMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
    }

    public void getAppMetrics(DisplayMetrics outMetrics, DisplayAdjustments displayAdjustments) {
        getMetricsWithSize(outMetrics, displayAdjustments.getCompatibilityInfo(), displayAdjustments.getActivityToken(), this.appWidth, this.appHeight);
    }

    public void getAppMetrics(DisplayMetrics outMetrics, CompatibilityInfo ci, IBinder token) {
        getMetricsWithSize(outMetrics, ci, token, this.appWidth, this.appHeight);
    }

    public void getLogicalMetrics(DisplayMetrics outMetrics, CompatibilityInfo compatInfo, IBinder token) {
        getMetricsWithSize(outMetrics, compatInfo, token, this.logicalWidth, this.logicalHeight);
    }

    public int getNaturalWidth() {
        return (this.rotation == 0 || this.rotation == 2) ? this.logicalWidth : this.logicalHeight;
    }

    public int getNaturalHeight() {
        return (this.rotation == 0 || this.rotation == 2) ? this.logicalHeight : this.logicalWidth;
    }

    public boolean hasAccess(int uid) {
        return Display.hasAccess(uid, this.flags, this.ownerUid);
    }

    private void getMetricsWithSize(DisplayMetrics outMetrics, CompatibilityInfo compatInfo, IBinder token, int width, int height) {
        int i = this.logicalDensityDpi;
        outMetrics.noncompatDensityDpi = i;
        outMetrics.densityDpi = i;
        outMetrics.widthPixels = width;
        outMetrics.noncompatWidthPixels = width;
        outMetrics.heightPixels = height;
        outMetrics.noncompatHeightPixels = height;
        float f = this.logicalDensityDpi * 0.00625f;
        outMetrics.noncompatDensity = f;
        outMetrics.density = f;
        float f2 = outMetrics.density;
        outMetrics.noncompatScaledDensity = f2;
        outMetrics.scaledDensity = f2;
        float f3 = this.physicalXDpi;
        outMetrics.noncompatXdpi = f3;
        outMetrics.xdpi = f3;
        float f4 = this.physicalYDpi;
        outMetrics.noncompatYdpi = f4;
        outMetrics.ydpi = f4;
        if (!compatInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
            compatInfo.applyToDisplayMetrics(outMetrics);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DisplayInfo{\"");
        sb.append(this.name);
        sb.append("\", app ");
        sb.append(this.appWidth);
        sb.append(" x ");
        sb.append(this.appHeight);
        sb.append(", real ");
        sb.append(this.logicalWidth);
        sb.append(" x ");
        sb.append(this.logicalHeight);
        if (this.overscanLeft != 0 || this.overscanTop != 0 || this.overscanRight != 0 || this.overscanBottom != 0) {
            sb.append(", overscan (");
            sb.append(this.overscanLeft);
            sb.append(Separators.COMMA);
            sb.append(this.overscanTop);
            sb.append(Separators.COMMA);
            sb.append(this.overscanRight);
            sb.append(Separators.COMMA);
            sb.append(this.overscanBottom);
            sb.append(Separators.RPAREN);
        }
        sb.append(", largest app ");
        sb.append(this.largestNominalAppWidth);
        sb.append(" x ");
        sb.append(this.largestNominalAppHeight);
        sb.append(", smallest app ");
        sb.append(this.smallestNominalAppWidth);
        sb.append(" x ");
        sb.append(this.smallestNominalAppHeight);
        sb.append(", ");
        sb.append(this.refreshRate);
        sb.append(" fps, rotation");
        sb.append(this.rotation);
        sb.append(", density ");
        sb.append(this.logicalDensityDpi);
        sb.append(" (");
        sb.append(this.physicalXDpi);
        sb.append(" x ");
        sb.append(this.physicalYDpi);
        sb.append(") dpi, layerStack ");
        sb.append(this.layerStack);
        sb.append(", type ");
        sb.append(Display.typeToString(this.type));
        if (this.address != null) {
            sb.append(", address ").append(this.address);
        }
        if (this.ownerUid != 0 || this.ownerPackageName != null) {
            sb.append(", owner ").append(this.ownerPackageName);
            sb.append(" (uid ").append(this.ownerUid).append(Separators.RPAREN);
        }
        sb.append(flagsToString(this.flags));
        sb.append("}");
        return sb.toString();
    }

    private static String flagsToString(int flags) {
        StringBuilder result = new StringBuilder();
        if ((flags & 2) != 0) {
            result.append(", FLAG_SECURE");
        }
        if ((flags & 1) != 0) {
            result.append(", FLAG_SUPPORTS_PROTECTED_BUFFERS");
        }
        if ((flags & 4) != 0) {
            result.append(", FLAG_PRIVATE");
        }
        if ((flags & 8) != 0) {
            result.append(", FLAG_PRESENTATION");
        }
        return result.toString();
    }
}