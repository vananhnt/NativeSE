package android.net;

import android.os.SystemClock;
import android.util.Slog;
import com.android.server.am.ProcessList;
import gov.nist.core.Separators;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/* loaded from: SamplingDataTracker.class */
public class SamplingDataTracker {
    private static final boolean DBG = false;
    private static final String TAG = "SamplingDataTracker";
    private SamplingSnapshot mBeginningSample;
    private SamplingSnapshot mEndingSample;
    private SamplingSnapshot mLastSample;
    public final Object mSamplingDataLock = new Object();
    private final int MINIMUM_SAMPLING_INTERVAL = ProcessList.PSS_MIN_TIME_FROM_STATE_CHANGE;
    private final int MINIMUM_SAMPLED_PACKETS = 30;

    /* loaded from: SamplingDataTracker$SamplingSnapshot.class */
    public static class SamplingSnapshot {
        public long mTxByteCount;
        public long mRxByteCount;
        public long mTxPacketCount;
        public long mRxPacketCount;
        public long mTxPacketErrorCount;
        public long mRxPacketErrorCount;
        public long mTimestamp;
    }

    public static void getSamplingSnapshots(Map<String, SamplingSnapshot> mapIfaceToSample) {
        BufferedReader reader = null;
        try {
            try {
                try {
                    reader = new BufferedReader(new FileReader("/proc/net/dev"));
                    reader.readLine();
                    reader.readLine();
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] tokens = line.trim().split("[ ]+");
                        if (tokens.length >= 17) {
                            String currentIface = tokens[0].split(Separators.COLON)[0];
                            if (mapIfaceToSample.containsKey(currentIface)) {
                                try {
                                    SamplingSnapshot ss = new SamplingSnapshot();
                                    ss.mTxByteCount = Long.parseLong(tokens[1]);
                                    ss.mTxPacketCount = Long.parseLong(tokens[2]);
                                    ss.mTxPacketErrorCount = Long.parseLong(tokens[3]);
                                    ss.mRxByteCount = Long.parseLong(tokens[9]);
                                    ss.mRxPacketCount = Long.parseLong(tokens[10]);
                                    ss.mRxPacketErrorCount = Long.parseLong(tokens[11]);
                                    ss.mTimestamp = SystemClock.elapsedRealtime();
                                    mapIfaceToSample.put(currentIface, ss);
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e2) {
                            Slog.e(TAG, "could not close /proc/net/dev");
                        }
                    }
                } catch (FileNotFoundException e3) {
                    Slog.e(TAG, "could not find /proc/net/dev");
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e4) {
                            Slog.e(TAG, "could not close /proc/net/dev");
                        }
                    }
                }
            } catch (IOException e5) {
                Slog.e(TAG, "could not read /proc/net/dev");
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e6) {
                        Slog.e(TAG, "could not close /proc/net/dev");
                    }
                }
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e7) {
                    Slog.e(TAG, "could not close /proc/net/dev");
                    throw th;
                }
            }
            throw th;
        }
    }

    public void startSampling(SamplingSnapshot s) {
        synchronized (this.mSamplingDataLock) {
            this.mLastSample = s;
        }
    }

    public void stopSampling(SamplingSnapshot s) {
        synchronized (this.mSamplingDataLock) {
            if (this.mLastSample != null && s.mTimestamp - this.mLastSample.mTimestamp > 15000 && getSampledPacketCount(this.mLastSample, s) > 30) {
                this.mBeginningSample = this.mLastSample;
                this.mEndingSample = s;
                this.mLastSample = null;
            }
        }
    }

    public void resetSamplingData() {
        synchronized (this.mSamplingDataLock) {
            this.mLastSample = null;
        }
    }

    public long getSampledTxByteCount() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return this.mEndingSample.mTxByteCount - this.mBeginningSample.mTxByteCount;
            }
            return Long.MAX_VALUE;
        }
    }

    public long getSampledTxPacketCount() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return this.mEndingSample.mTxPacketCount - this.mBeginningSample.mTxPacketCount;
            }
            return Long.MAX_VALUE;
        }
    }

    public long getSampledTxPacketErrorCount() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return this.mEndingSample.mTxPacketErrorCount - this.mBeginningSample.mTxPacketErrorCount;
            }
            return Long.MAX_VALUE;
        }
    }

    public long getSampledRxByteCount() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return this.mEndingSample.mRxByteCount - this.mBeginningSample.mRxByteCount;
            }
            return Long.MAX_VALUE;
        }
    }

    public long getSampledRxPacketCount() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return this.mEndingSample.mRxPacketCount - this.mBeginningSample.mRxPacketCount;
            }
            return Long.MAX_VALUE;
        }
    }

    public long getSampledPacketCount() {
        return getSampledPacketCount(this.mBeginningSample, this.mEndingSample);
    }

    public long getSampledPacketCount(SamplingSnapshot begin, SamplingSnapshot end) {
        if (begin != null && end != null) {
            long rxPacketCount = end.mRxPacketCount - begin.mRxPacketCount;
            long txPacketCount = end.mTxPacketCount - begin.mTxPacketCount;
            return rxPacketCount + txPacketCount;
        }
        return Long.MAX_VALUE;
    }

    public long getSampledPacketErrorCount() {
        if (this.mBeginningSample != null && this.mEndingSample != null) {
            long rxPacketErrorCount = getSampledRxPacketErrorCount();
            long txPacketErrorCount = getSampledTxPacketErrorCount();
            return rxPacketErrorCount + txPacketErrorCount;
        }
        return Long.MAX_VALUE;
    }

    public long getSampledRxPacketErrorCount() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return this.mEndingSample.mRxPacketErrorCount - this.mBeginningSample.mRxPacketErrorCount;
            }
            return Long.MAX_VALUE;
        }
    }

    public long getSampleTimestamp() {
        synchronized (this.mSamplingDataLock) {
            if (this.mEndingSample != null) {
                return this.mEndingSample.mTimestamp;
            }
            return Long.MAX_VALUE;
        }
    }

    public int getSampleDuration() {
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample != null && this.mEndingSample != null) {
                return (int) (this.mEndingSample.mTimestamp - this.mBeginningSample.mTimestamp);
            }
            return Integer.MAX_VALUE;
        }
    }

    public void setCommonLinkQualityInfoFields(LinkQualityInfo li) {
        synchronized (this.mSamplingDataLock) {
            li.setLastDataSampleTime(getSampleTimestamp());
            li.setDataSampleDuration(getSampleDuration());
            li.setPacketCount(getSampledPacketCount());
            li.setPacketErrorCount(getSampledPacketErrorCount());
        }
    }
}