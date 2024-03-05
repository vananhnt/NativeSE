package com.android.internal.os;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;
import java.util.HashMap;
import java.util.Map;

/* loaded from: PkgUsageStats.class */
public class PkgUsageStats implements Parcelable {
    public String packageName;
    public int launchCount;
    public long usageTime;
    public Map<String, Long> componentResumeTimes;
    public static final Parcelable.Creator<PkgUsageStats> CREATOR = new Parcelable.Creator<PkgUsageStats>() { // from class: com.android.internal.os.PkgUsageStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PkgUsageStats createFromParcel(Parcel in) {
            return new PkgUsageStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PkgUsageStats[] newArray(int size) {
            return new PkgUsageStats[size];
        }
    };

    public String toString() {
        return "PkgUsageStats{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
    }

    public PkgUsageStats(String pkgName, int count, long time, Map<String, Long> lastResumeTimes) {
        this.packageName = pkgName;
        this.launchCount = count;
        this.usageTime = time;
        this.componentResumeTimes = new HashMap(lastResumeTimes);
    }

    public PkgUsageStats(Parcel source) {
        this.packageName = source.readString();
        this.launchCount = source.readInt();
        this.usageTime = source.readLong();
        int N = source.readInt();
        this.componentResumeTimes = new HashMap(N);
        for (int i = 0; i < N; i++) {
            String component = source.readString();
            long lastResumeTime = source.readLong();
            this.componentResumeTimes.put(component, Long.valueOf(lastResumeTime));
        }
    }

    public PkgUsageStats(PkgUsageStats pStats) {
        this.packageName = pStats.packageName;
        this.launchCount = pStats.launchCount;
        this.usageTime = pStats.usageTime;
        this.componentResumeTimes = new HashMap(pStats.componentResumeTimes);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.packageName);
        dest.writeInt(this.launchCount);
        dest.writeLong(this.usageTime);
        dest.writeInt(this.componentResumeTimes.size());
        for (Map.Entry<String, Long> ent : this.componentResumeTimes.entrySet()) {
            dest.writeString(ent.getKey());
            dest.writeLong(ent.getValue().longValue());
        }
    }
}