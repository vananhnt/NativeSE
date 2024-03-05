package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: BatchedScanSettings.class */
public class BatchedScanSettings implements Parcelable {
    private static final String TAG = "BatchedScanSettings";
    public static final int UNSPECIFIED = Integer.MAX_VALUE;
    public static final int MIN_SCANS_PER_BATCH = 2;
    public static final int MAX_SCANS_PER_BATCH = 255;
    public static final int DEFAULT_SCANS_PER_BATCH = 255;
    public static final int MIN_AP_PER_SCAN = 2;
    public static final int MAX_AP_PER_SCAN = 255;
    public static final int DEFAULT_AP_PER_SCAN = 16;
    public static final int MIN_INTERVAL_SEC = 0;
    public static final int MAX_INTERVAL_SEC = 3600;
    public static final int DEFAULT_INTERVAL_SEC = 30;
    public static final int MIN_AP_FOR_DISTANCE = 0;
    public static final int MAX_AP_FOR_DISTANCE = 255;
    public static final int DEFAULT_AP_FOR_DISTANCE = 0;
    public static final int MAX_WIFI_CHANNEL = 196;
    public int maxScansPerBatch;
    public int maxApPerScan;
    public Collection<String> channelSet;
    public int scanIntervalSec;
    public int maxApForDistance;
    public static final Parcelable.Creator<BatchedScanSettings> CREATOR = new Parcelable.Creator<BatchedScanSettings>() { // from class: android.net.wifi.BatchedScanSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanSettings createFromParcel(Parcel in) {
            BatchedScanSettings settings = new BatchedScanSettings();
            settings.maxScansPerBatch = in.readInt();
            settings.maxApPerScan = in.readInt();
            settings.scanIntervalSec = in.readInt();
            settings.maxApForDistance = in.readInt();
            int channelCount = in.readInt();
            if (channelCount > 0) {
                settings.channelSet = new ArrayList(channelCount);
                while (true) {
                    int i = channelCount;
                    channelCount--;
                    if (i <= 0) {
                        break;
                    }
                    settings.channelSet.add(in.readString());
                }
            }
            return settings;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanSettings[] newArray(int size) {
            return new BatchedScanSettings[size];
        }
    };

    public BatchedScanSettings() {
        clear();
    }

    public void clear() {
        this.maxScansPerBatch = Integer.MAX_VALUE;
        this.maxApPerScan = Integer.MAX_VALUE;
        this.channelSet = null;
        this.scanIntervalSec = Integer.MAX_VALUE;
        this.maxApForDistance = Integer.MAX_VALUE;
    }

    public BatchedScanSettings(BatchedScanSettings source) {
        this.maxScansPerBatch = source.maxScansPerBatch;
        this.maxApPerScan = source.maxApPerScan;
        if (source.channelSet != null) {
            this.channelSet = new ArrayList(source.channelSet);
        }
        this.scanIntervalSec = source.scanIntervalSec;
        this.maxApForDistance = source.maxApForDistance;
    }

    private boolean channelSetIsValid() {
        int i;
        if (this.channelSet == null || this.channelSet.isEmpty()) {
            return true;
        }
        for (String channel : this.channelSet) {
            try {
                i = Integer.parseInt(channel);
            } catch (NumberFormatException e) {
            }
            if (i > 0 && i <= 196) {
            }
            if (!channel.equals("A") && !channel.equals("B")) {
                return false;
            }
        }
        return true;
    }

    public boolean isInvalid() {
        if (this.maxScansPerBatch != Integer.MAX_VALUE && (this.maxScansPerBatch < 2 || this.maxScansPerBatch > 255)) {
            return true;
        }
        if ((this.maxApPerScan == Integer.MAX_VALUE || (this.maxApPerScan >= 2 && this.maxApPerScan <= 255)) && channelSetIsValid()) {
            if (this.scanIntervalSec != Integer.MAX_VALUE && (this.scanIntervalSec < 0 || this.scanIntervalSec > 3600)) {
                return true;
            }
            if (this.maxApForDistance != Integer.MAX_VALUE) {
                if (this.maxApForDistance < 0 || this.maxApForDistance > 255) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return true;
    }

    public void constrain() {
        if (this.scanIntervalSec == Integer.MAX_VALUE) {
            this.scanIntervalSec = 30;
        } else if (this.scanIntervalSec < 0) {
            this.scanIntervalSec = 0;
        } else if (this.scanIntervalSec > 3600) {
            this.scanIntervalSec = MAX_INTERVAL_SEC;
        }
        if (this.maxScansPerBatch == Integer.MAX_VALUE) {
            this.maxScansPerBatch = 255;
        } else if (this.maxScansPerBatch < 2) {
            this.maxScansPerBatch = 2;
        } else if (this.maxScansPerBatch > 255) {
            this.maxScansPerBatch = 255;
        }
        if (this.maxApPerScan == Integer.MAX_VALUE) {
            this.maxApPerScan = 16;
        } else if (this.maxApPerScan < 2) {
            this.maxApPerScan = 2;
        } else if (this.maxApPerScan > 255) {
            this.maxApPerScan = 255;
        }
        if (this.maxApForDistance == Integer.MAX_VALUE) {
            this.maxApForDistance = 0;
        } else if (this.maxApForDistance < 0) {
            this.maxApForDistance = 0;
        } else if (this.maxApForDistance > 255) {
            this.maxApForDistance = 255;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof BatchedScanSettings) {
            BatchedScanSettings o = (BatchedScanSettings) obj;
            if (this.maxScansPerBatch != o.maxScansPerBatch || this.maxApPerScan != o.maxApPerScan || this.scanIntervalSec != o.scanIntervalSec || this.maxApForDistance != o.maxApForDistance) {
                return false;
            }
            if (this.channelSet == null) {
                return o.channelSet == null;
            }
            return this.channelSet.equals(o.channelSet);
        }
        return false;
    }

    public int hashCode() {
        return this.maxScansPerBatch + (this.maxApPerScan * 3) + (this.scanIntervalSec * 5) + (this.maxApForDistance * 7) + (this.channelSet.hashCode() * 11);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("BatchScanSettings [maxScansPerBatch: ").append(this.maxScansPerBatch == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(this.maxScansPerBatch)).append(", maxApPerScan: ").append(this.maxApPerScan == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(this.maxApPerScan)).append(", scanIntervalSec: ").append(this.scanIntervalSec == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(this.scanIntervalSec)).append(", maxApForDistance: ").append(this.maxApForDistance == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(this.maxApForDistance)).append(", channelSet: ");
        if (this.channelSet == null) {
            sb.append("ALL");
        } else {
            sb.append(Separators.LESS_THAN);
            for (String channel : this.channelSet) {
                sb.append(Separators.SP + channel);
            }
            sb.append(Separators.GREATER_THAN);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxScansPerBatch);
        dest.writeInt(this.maxApPerScan);
        dest.writeInt(this.scanIntervalSec);
        dest.writeInt(this.maxApForDistance);
        dest.writeInt(this.channelSet == null ? 0 : this.channelSet.size());
        if (this.channelSet != null) {
            for (String channel : this.channelSet) {
                dest.writeString(channel);
            }
        }
    }
}