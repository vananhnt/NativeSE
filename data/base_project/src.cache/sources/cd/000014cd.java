package android.view;

import android.content.res.CompatibilityInfo;
import android.os.IBinder;
import com.android.internal.util.Objects;

/* loaded from: DisplayAdjustments.class */
public class DisplayAdjustments {
    public static final boolean DEVELOPMENT_RESOURCES_DEPEND_ON_ACTIVITY_TOKEN = false;
    public static final DisplayAdjustments DEFAULT_DISPLAY_ADJUSTMENTS = new DisplayAdjustments();
    private volatile CompatibilityInfo mCompatInfo;
    private volatile IBinder mActivityToken;

    public DisplayAdjustments() {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
    }

    public DisplayAdjustments(IBinder token) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mActivityToken = token;
    }

    public DisplayAdjustments(DisplayAdjustments daj) {
        this(daj.getCompatibilityInfo(), daj.getActivityToken());
    }

    public DisplayAdjustments(CompatibilityInfo compatInfo, IBinder token) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        setCompatibilityInfo(compatInfo);
        this.mActivityToken = token;
    }

    public void setCompatibilityInfo(CompatibilityInfo compatInfo) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setCompatbilityInfo: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        if (compatInfo != null && (compatInfo.isScalingRequired() || !compatInfo.supportsScreen())) {
            this.mCompatInfo = compatInfo;
        } else {
            this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        }
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatInfo;
    }

    public void setActivityToken(IBinder token) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setActivityToken: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        this.mActivityToken = token;
    }

    public IBinder getActivityToken() {
        return this.mActivityToken;
    }

    public int hashCode() {
        int hash = (17 * 31) + this.mCompatInfo.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DisplayAdjustments)) {
            return false;
        }
        DisplayAdjustments daj = (DisplayAdjustments) o;
        return Objects.equal(daj.mCompatInfo, this.mCompatInfo) && Objects.equal(daj.mActivityToken, this.mActivityToken);
    }
}