package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: BatchedScanResult.class */
public class BatchedScanResult implements Parcelable {
    private static final String TAG = "BatchedScanResult";
    public boolean truncated;
    public final List<ScanResult> scanResults = new ArrayList();
    public static final Parcelable.Creator<BatchedScanResult> CREATOR = new Parcelable.Creator<BatchedScanResult>() { // from class: android.net.wifi.BatchedScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanResult createFromParcel(Parcel in) {
            BatchedScanResult result = new BatchedScanResult();
            result.truncated = in.readInt() == 1;
            int count = in.readInt();
            while (true) {
                int i = count;
                count--;
                if (i > 0) {
                    result.scanResults.add(ScanResult.CREATOR.createFromParcel(in));
                } else {
                    return result;
                }
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanResult[] newArray(int size) {
            return new BatchedScanResult[size];
        }
    };

    public BatchedScanResult() {
    }

    public BatchedScanResult(BatchedScanResult source) {
        this.truncated = source.truncated;
        for (ScanResult s : source.scanResults) {
            this.scanResults.add(new ScanResult(s));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("BatchedScanResult: ").append("truncated: ").append(String.valueOf(this.truncated)).append("scanResults: [");
        for (ScanResult s : this.scanResults) {
            sb.append(" <").append(s.toString()).append("> ");
        }
        sb.append(" ]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.truncated ? 1 : 0);
        dest.writeInt(this.scanResults.size());
        for (ScanResult s : this.scanResults) {
            s.writeToParcel(dest, flags);
        }
    }
}