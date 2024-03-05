package com.android.server.display;

import android.view.Display;
import gov.nist.core.Separators;
import libcore.util.Objects;

/* loaded from: DisplayDeviceInfo.class */
final class DisplayDeviceInfo {
    public static final int FLAG_DEFAULT_DISPLAY = 1;
    public static final int FLAG_ROTATES_WITH_CONTENT = 2;
    public static final int FLAG_SECURE = 4;
    public static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 8;
    public static final int FLAG_PRIVATE = 16;
    public static final int FLAG_NEVER_BLANK = 32;
    public static final int FLAG_PRESENTATION = 64;
    public static final int TOUCH_NONE = 0;
    public static final int TOUCH_INTERNAL = 1;
    public static final int TOUCH_EXTERNAL = 2;
    public String name;
    public int width;
    public int height;
    public float refreshRate;
    public int densityDpi;
    public float xDpi;
    public float yDpi;
    public int flags;
    public int touch;
    public int rotation = 0;
    public int type;
    public String address;
    public int ownerUid;
    public String ownerPackageName;

    public void setAssumedDensityForExternalDisplay(int width, int height) {
        this.densityDpi = (Math.min(width, height) * 320) / 1080;
        this.xDpi = this.densityDpi;
        this.yDpi = this.densityDpi;
    }

    public boolean equals(Object o) {
        return (o instanceof DisplayDeviceInfo) && equals((DisplayDeviceInfo) o);
    }

    public boolean equals(DisplayDeviceInfo other) {
        return other != null && Objects.equal(this.name, other.name) && this.width == other.width && this.height == other.height && this.refreshRate == other.refreshRate && this.densityDpi == other.densityDpi && this.xDpi == other.xDpi && this.yDpi == other.yDpi && this.flags == other.flags && this.touch == other.touch && this.rotation == other.rotation && this.type == other.type && Objects.equal(this.address, other.address) && this.ownerUid == other.ownerUid && Objects.equal(this.ownerPackageName, other.ownerPackageName);
    }

    public int hashCode() {
        return 0;
    }

    public void copyFrom(DisplayDeviceInfo other) {
        this.name = other.name;
        this.width = other.width;
        this.height = other.height;
        this.refreshRate = other.refreshRate;
        this.densityDpi = other.densityDpi;
        this.xDpi = other.xDpi;
        this.yDpi = other.yDpi;
        this.flags = other.flags;
        this.touch = other.touch;
        this.rotation = other.rotation;
        this.type = other.type;
        this.address = other.address;
        this.ownerUid = other.ownerUid;
        this.ownerPackageName = other.ownerPackageName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DisplayDeviceInfo{\"");
        sb.append(this.name).append("\": ").append(this.width).append(" x ").append(this.height);
        sb.append(", ").append(this.refreshRate).append(" fps, ");
        sb.append("density ").append(this.densityDpi);
        sb.append(", ").append(this.xDpi).append(" x ").append(this.yDpi).append(" dpi");
        sb.append(", touch ").append(touchToString(this.touch));
        sb.append(", rotation ").append(this.rotation);
        sb.append(", type ").append(Display.typeToString(this.type));
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

    private static String touchToString(int touch) {
        switch (touch) {
            case 0:
                return "NONE";
            case 1:
                return "INTERNAL";
            case 2:
                return "EXTERNAL";
            default:
                return Integer.toString(touch);
        }
    }

    private static String flagsToString(int flags) {
        StringBuilder msg = new StringBuilder();
        if ((flags & 1) != 0) {
            msg.append(", FLAG_DEFAULT_DISPLAY");
        }
        if ((flags & 2) != 0) {
            msg.append(", FLAG_ROTATES_WITH_CONTENT");
        }
        if ((flags & 4) != 0) {
            msg.append(", FLAG_SECURE");
        }
        if ((flags & 8) != 0) {
            msg.append(", FLAG_SUPPORTS_PROTECTED_BUFFERS");
        }
        if ((flags & 16) != 0) {
            msg.append(", FLAG_PRIVATE");
        }
        if ((flags & 32) != 0) {
            msg.append(", FLAG_NEVER_BLANK");
        }
        if ((flags & 64) != 0) {
            msg.append(", FLAG_PRESENTATION");
        }
        return msg.toString();
    }
}