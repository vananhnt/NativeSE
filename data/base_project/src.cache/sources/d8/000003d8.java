package android.content.res;

import android.os.IBinder;

/* loaded from: ResourcesKey.class */
public final class ResourcesKey {
    final String mResDir;
    final float mScale;
    private final int mHash;
    private final IBinder mToken;
    public final int mDisplayId;
    public final Configuration mOverrideConfiguration = new Configuration();

    public ResourcesKey(String resDir, int displayId, Configuration overrideConfiguration, float scale, IBinder token) {
        this.mResDir = resDir;
        this.mDisplayId = displayId;
        if (overrideConfiguration != null) {
            this.mOverrideConfiguration.setTo(overrideConfiguration);
        }
        this.mScale = scale;
        this.mToken = token;
        int hash = (31 * 17) + (this.mResDir == null ? 0 : this.mResDir.hashCode());
        this.mHash = (31 * ((31 * ((31 * hash) + this.mDisplayId)) + (this.mOverrideConfiguration != null ? this.mOverrideConfiguration.hashCode() : 0))) + Float.floatToIntBits(this.mScale);
    }

    public boolean hasOverrideConfiguration() {
        return !Configuration.EMPTY.equals(this.mOverrideConfiguration);
    }

    public int hashCode() {
        return this.mHash;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ResourcesKey)) {
            return false;
        }
        ResourcesKey peer = (ResourcesKey) obj;
        if (!this.mResDir.equals(peer.mResDir) || this.mDisplayId != peer.mDisplayId) {
            return false;
        }
        if ((this.mOverrideConfiguration != peer.mOverrideConfiguration && (this.mOverrideConfiguration == null || peer.mOverrideConfiguration == null || !this.mOverrideConfiguration.equals(peer.mOverrideConfiguration))) || this.mScale != peer.mScale) {
            return false;
        }
        return true;
    }

    public String toString() {
        return Integer.toHexString(this.mHash);
    }
}