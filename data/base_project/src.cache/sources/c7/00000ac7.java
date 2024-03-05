package android.os;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.location.LocationManager;
import android.nfc.cardemulation.CardEmulation;
import android.provider.Telephony;
import android.telephony.SignalStrength;
import android.util.Printer;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.SurfaceControl;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

/* loaded from: BatteryStats.class */
public abstract class BatteryStats implements Parcelable {
    private static final boolean LOCAL_LOGV = false;
    public static final String SERVICE_NAME = "batterystats";
    public static final int WAKE_TYPE_PARTIAL = 0;
    public static final int WAKE_TYPE_FULL = 1;
    public static final int WAKE_TYPE_WINDOW = 2;
    public static final int SENSOR = 3;
    public static final int WIFI_RUNNING = 4;
    public static final int FULL_WIFI_LOCK = 5;
    public static final int WIFI_SCAN = 6;
    public static final int WIFI_MULTICAST_ENABLED = 7;
    public static final int AUDIO_TURNED_ON = 7;
    public static final int VIDEO_TURNED_ON = 8;
    public static final int VIBRATOR_ON = 9;
    public static final int FOREGROUND_ACTIVITY = 10;
    public static final int STATS_SINCE_CHARGED = 0;
    public static final int STATS_LAST = 1;
    public static final int STATS_CURRENT = 2;
    public static final int STATS_SINCE_UNPLUGGED = 3;
    private static final int BATTERY_STATS_CHECKIN_VERSION = 7;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final long BYTES_PER_GB = 1073741824;
    private static final String UID_DATA = "uid";
    private static final String APK_DATA = "apk";
    private static final String PROCESS_DATA = "pr";
    private static final String SENSOR_DATA = "sr";
    private static final String VIBRATOR_DATA = "vib";
    private static final String FOREGROUND_DATA = "fg";
    private static final String WAKELOCK_DATA = "wl";
    private static final String KERNEL_WAKELOCK_DATA = "kwl";
    private static final String NETWORK_DATA = "nt";
    private static final String USER_ACTIVITY_DATA = "ua";
    private static final String BATTERY_DATA = "bt";
    private static final String BATTERY_DISCHARGE_DATA = "dc";
    private static final String BATTERY_LEVEL_DATA = "lv";
    private static final String WIFI_DATA = "wfl";
    private static final String MISC_DATA = "m";
    private static final String HISTORY_DATA = "h";
    private static final String SCREEN_BRIGHTNESS_DATA = "br";
    private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
    private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
    private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
    private static final String DATA_CONNECTION_TIME_DATA = "dct";
    private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
    private final StringBuilder mFormatBuilder = new StringBuilder(32);
    private final Formatter mFormatter = new Formatter(this.mFormatBuilder);
    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
    public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
    public static final int DATA_CONNECTION_NONE = 0;
    public static final int DATA_CONNECTION_GPRS = 1;
    public static final int DATA_CONNECTION_EDGE = 2;
    public static final int DATA_CONNECTION_UMTS = 3;
    public static final int DATA_CONNECTION_CDMA = 4;
    public static final int DATA_CONNECTION_EVDO_0 = 5;
    public static final int DATA_CONNECTION_EVDO_A = 6;
    public static final int DATA_CONNECTION_1xRTT = 7;
    public static final int DATA_CONNECTION_HSDPA = 8;
    public static final int DATA_CONNECTION_HSUPA = 9;
    public static final int DATA_CONNECTION_HSPA = 10;
    public static final int DATA_CONNECTION_IDEN = 11;
    public static final int DATA_CONNECTION_EVDO_B = 12;
    public static final int DATA_CONNECTION_LTE = 13;
    public static final int DATA_CONNECTION_EHRPD = 14;
    public static final int DATA_CONNECTION_OTHER = 15;
    public static final int NUM_DATA_CONNECTION_TYPES = 16;
    public static final int NETWORK_MOBILE_RX_BYTES = 0;
    public static final int NETWORK_MOBILE_TX_BYTES = 1;
    public static final int NETWORK_WIFI_RX_BYTES = 2;
    public static final int NETWORK_WIFI_TX_BYTES = 3;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 4;
    private static final String[] STAT_NAMES = {"t", "l", FullBackup.CACHE_TREE_TOKEN, "u"};
    static final String[] SCREEN_BRIGHTNESS_NAMES = {"dark", "dim", "medium", "light", "bright"};
    static final String[] DATA_CONNECTION_NAMES = {"none", "gprs", "edge", "umts", "cdma", "evdo_0", "evdo_A", "1xrtt", "hsdpa", "hsupa", "hspa", "iden", "evdo_b", "lte", "ehrpd", CardEmulation.CATEGORY_OTHER};
    public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS = {new BitDescription(524288, BatteryManager.EXTRA_PLUGGED), new BitDescription(1048576, "screen"), new BitDescription(268435456, LocationManager.GPS_PROVIDER), new BitDescription(262144, "phone_in_call"), new BitDescription(134217728, "phone_scanning"), new BitDescription(131072, "wifi"), new BitDescription(67108864, "wifi_running"), new BitDescription(33554432, "wifi_full_lock"), new BitDescription(16777216, "wifi_scan"), new BitDescription(8388608, "wifi_multicast"), new BitDescription(65536, "bluetooth"), new BitDescription(4194304, Context.AUDIO_SERVICE), new BitDescription(2097152, "video"), new BitDescription(1073741824, "wake_lock"), new BitDescription(536870912, Context.SENSOR_SERVICE), new BitDescription(15, 0, "brightness", SCREEN_BRIGHTNESS_NAMES), new BitDescription(240, 4, "signal_strength", SignalStrength.SIGNAL_STRENGTH_NAMES), new BitDescription(HistoryItem.STATE_PHONE_STATE_MASK, 8, "phone_state", new String[]{"in", "out", "emergency", "off"}), new BitDescription(HistoryItem.STATE_DATA_CONNECTION_MASK, 12, "data_conn", DATA_CONNECTION_NAMES)};

    /* loaded from: BatteryStats$Counter.class */
    public static abstract class Counter {
        public abstract int getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    /* loaded from: BatteryStats$Timer.class */
    public static abstract class Timer {
        public abstract int getCountLocked(int i);

        public abstract long getTotalTimeLocked(long j, int i);

        public abstract void logState(Printer printer, String str);
    }

    public abstract boolean startIteratingHistoryLocked();

    public abstract boolean getNextHistoryLocked(HistoryItem historyItem);

    public abstract void finishIteratingHistoryLocked();

    public abstract boolean startIteratingOldHistoryLocked();

    public abstract boolean getNextOldHistoryLocked(HistoryItem historyItem);

    public abstract void finishIteratingOldHistoryLocked();

    public abstract long getHistoryBaseTime();

    public abstract int getStartCount();

    public abstract long getScreenOnTime(long j, int i);

    public abstract long getScreenBrightnessTime(int i, long j, int i2);

    public abstract int getInputEventCount(int i);

    public abstract long getPhoneOnTime(long j, int i);

    public abstract long getPhoneSignalStrengthTime(int i, long j, int i2);

    public abstract long getPhoneSignalScanningTime(long j, int i);

    public abstract int getPhoneSignalStrengthCount(int i, int i2);

    public abstract long getPhoneDataConnectionTime(int i, long j, int i2);

    public abstract int getPhoneDataConnectionCount(int i, int i2);

    public abstract long getWifiOnTime(long j, int i);

    public abstract long getGlobalWifiRunningTime(long j, int i);

    public abstract long getBluetoothOnTime(long j, int i);

    public abstract long getNetworkActivityCount(int i, int i2);

    public abstract boolean getIsOnBattery();

    public abstract SparseArray<? extends Uid> getUidStats();

    public abstract long getBatteryUptime(long j);

    public abstract long getRadioDataUptime();

    public abstract long getBatteryRealtime(long j);

    public abstract int getDischargeStartLevel();

    public abstract int getDischargeCurrentLevel();

    public abstract int getLowDischargeAmountSinceCharge();

    public abstract int getHighDischargeAmountSinceCharge();

    public abstract int getDischargeAmountScreenOn();

    public abstract int getDischargeAmountScreenOnSinceCharge();

    public abstract int getDischargeAmountScreenOff();

    public abstract int getDischargeAmountScreenOffSinceCharge();

    public abstract long computeBatteryUptime(long j, int i);

    public abstract long computeBatteryRealtime(long j, int i);

    public abstract long computeUptime(long j, int i);

    public abstract long computeRealtime(long j, int i);

    public abstract Map<String, ? extends Timer> getKernelWakelockStats();

    public abstract int getCpuSpeedSteps();

    /* loaded from: BatteryStats$Uid.class */
    public static abstract class Uid {
        static final String[] USER_ACTIVITY_TYPES = {CardEmulation.CATEGORY_OTHER, "button", "touch"};
        public static final int NUM_USER_ACTIVITY_TYPES = 3;

        /* loaded from: BatteryStats$Uid$Proc.class */
        public static abstract class Proc {

            /* loaded from: BatteryStats$Uid$Proc$ExcessivePower.class */
            public static class ExcessivePower {
                public static final int TYPE_WAKE = 1;
                public static final int TYPE_CPU = 2;
                public int type;
                public long overTime;
                public long usedTime;
            }

            public abstract long getUserTime(int i);

            public abstract long getSystemTime(int i);

            public abstract int getStarts(int i);

            public abstract long getForegroundTime(int i);

            public abstract long getTimeAtCpuSpeedStep(int i, int i2);

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);
        }

        /* loaded from: BatteryStats$Uid$Sensor.class */
        public static abstract class Sensor {
            public static final int GPS = -10000;

            public abstract int getHandle();

            public abstract Timer getSensorTime();
        }

        /* loaded from: BatteryStats$Uid$Wakelock.class */
        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int i);
        }

        public abstract Map<String, ? extends Wakelock> getWakelockStats();

        public abstract Map<Integer, ? extends Sensor> getSensorStats();

        public abstract SparseArray<? extends Pid> getPidStats();

        public abstract Map<String, ? extends Proc> getProcessStats();

        public abstract Map<String, ? extends Pkg> getPackageStats();

        public abstract int getUid();

        public abstract void noteWifiRunningLocked();

        public abstract void noteWifiStoppedLocked();

        public abstract void noteFullWifiLockAcquiredLocked();

        public abstract void noteFullWifiLockReleasedLocked();

        public abstract void noteWifiScanStartedLocked();

        public abstract void noteWifiScanStoppedLocked();

        public abstract void noteWifiMulticastEnabledLocked();

        public abstract void noteWifiMulticastDisabledLocked();

        public abstract void noteAudioTurnedOnLocked();

        public abstract void noteAudioTurnedOffLocked();

        public abstract void noteVideoTurnedOnLocked();

        public abstract void noteVideoTurnedOffLocked();

        public abstract void noteActivityResumedLocked();

        public abstract void noteActivityPausedLocked();

        public abstract long getWifiRunningTime(long j, int i);

        public abstract long getFullWifiLockTime(long j, int i);

        public abstract long getWifiScanTime(long j, int i);

        public abstract long getWifiMulticastTime(long j, int i);

        public abstract long getAudioTurnedOnTime(long j, int i);

        public abstract long getVideoTurnedOnTime(long j, int i);

        public abstract Timer getForegroundActivityTimer();

        public abstract Timer getVibratorOnTimer();

        public abstract void noteUserActivityLocked(int i);

        public abstract boolean hasUserActivity();

        public abstract int getUserActivityCount(int i, int i2);

        public abstract boolean hasNetworkActivity();

        public abstract long getNetworkActivityCount(int i, int i2);

        /* loaded from: BatteryStats$Uid$Pid.class */
        public class Pid {
            public long mWakeSum;
            public long mWakeStart;

            public Pid() {
            }
        }

        /* loaded from: BatteryStats$Uid$Pkg.class */
        public static abstract class Pkg {
            public abstract int getWakeups(int i);

            public abstract Map<String, ? extends Serv> getServiceStats();

            /* loaded from: BatteryStats$Uid$Pkg$Serv.class */
            public abstract class Serv {
                public abstract long getStartTime(long j, int i);

                public abstract int getStarts(int i);

                public abstract int getLaunches(int i);

                public Serv() {
                }
            }
        }
    }

    /* loaded from: BatteryStats$HistoryItem.class */
    public static final class HistoryItem implements Parcelable {
        static final String TAG = "HistoryItem";
        static final boolean DEBUG = false;
        public HistoryItem next;
        public long time;
        public static final byte CMD_NULL = 0;
        public static final byte CMD_UPDATE = 1;
        public static final byte CMD_START = 2;
        public static final byte CMD_OVERFLOW = 3;
        public byte cmd = 0;
        public byte batteryLevel;
        public byte batteryStatus;
        public byte batteryHealth;
        public byte batteryPlugType;
        public char batteryTemperature;
        public char batteryVoltage;
        public static final int STATE_BRIGHTNESS_MASK = 15;
        public static final int STATE_BRIGHTNESS_SHIFT = 0;
        public static final int STATE_SIGNAL_STRENGTH_MASK = 240;
        public static final int STATE_SIGNAL_STRENGTH_SHIFT = 4;
        public static final int STATE_PHONE_STATE_MASK = 3840;
        public static final int STATE_PHONE_STATE_SHIFT = 8;
        public static final int STATE_DATA_CONNECTION_MASK = 61440;
        public static final int STATE_DATA_CONNECTION_SHIFT = 12;
        public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
        public static final int STATE_SENSOR_ON_FLAG = 536870912;
        public static final int STATE_GPS_ON_FLAG = 268435456;
        public static final int STATE_PHONE_SCANNING_FLAG = 134217728;
        public static final int STATE_WIFI_RUNNING_FLAG = 67108864;
        public static final int STATE_WIFI_FULL_LOCK_FLAG = 33554432;
        public static final int STATE_WIFI_SCAN_FLAG = 16777216;
        public static final int STATE_WIFI_MULTICAST_ON_FLAG = 8388608;
        public static final int STATE_AUDIO_ON_FLAG = 4194304;
        public static final int STATE_VIDEO_ON_FLAG = 2097152;
        public static final int STATE_SCREEN_ON_FLAG = 1048576;
        public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
        public static final int STATE_PHONE_IN_CALL_FLAG = 262144;
        public static final int STATE_WIFI_ON_FLAG = 131072;
        public static final int STATE_BLUETOOTH_ON_FLAG = 65536;
        public static final int MOST_INTERESTING_STATES = 270270464;
        public int states;
        static final int DELTA_TIME_MASK = 262143;
        static final int DELTA_TIME_ABS = 262141;
        static final int DELTA_TIME_INT = 262142;
        static final int DELTA_TIME_LONG = 262143;
        static final int DELTA_CMD_MASK = 3;
        static final int DELTA_CMD_SHIFT = 18;
        static final int DELTA_BATTERY_LEVEL_FLAG = 1048576;
        static final int DELTA_STATE_FLAG = 2097152;
        static final int DELTA_STATE_MASK = -4194304;

        public HistoryItem() {
        }

        public HistoryItem(long time, Parcel src) {
            this.time = time;
            readFromParcel(src);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.time);
            int bat = (this.cmd & 255) | ((this.batteryLevel << 8) & 65280) | ((this.batteryStatus << 16) & SurfaceControl.FX_SURFACE_MASK) | ((this.batteryHealth << 20) & 15728640) | ((this.batteryPlugType << 24) & 251658240);
            dest.writeInt(bat);
            int bat2 = (this.batteryTemperature & 65535) | ((this.batteryVoltage << 16) & (-65536));
            dest.writeInt(bat2);
            dest.writeInt(this.states);
        }

        private void readFromParcel(Parcel src) {
            int bat = src.readInt();
            this.cmd = (byte) (bat & 255);
            this.batteryLevel = (byte) ((bat >> 8) & 255);
            this.batteryStatus = (byte) ((bat >> 16) & 15);
            this.batteryHealth = (byte) ((bat >> 20) & 15);
            this.batteryPlugType = (byte) ((bat >> 24) & 15);
            int bat2 = src.readInt();
            this.batteryTemperature = (char) (bat2 & 65535);
            this.batteryVoltage = (char) ((bat2 >> 16) & 65535);
            this.states = src.readInt();
        }

        public void writeDelta(Parcel dest, HistoryItem last) {
            int deltaTimeToken;
            if (last == null || last.cmd != 1) {
                dest.writeInt(DELTA_TIME_ABS);
                writeToParcel(dest, 0);
                return;
            }
            long deltaTime = this.time - last.time;
            int lastBatteryLevelInt = last.buildBatteryLevelInt();
            int lastStateInt = last.buildStateInt();
            if (deltaTime < 0 || deltaTime > 2147483647L) {
                deltaTimeToken = 262143;
            } else if (deltaTime >= 262141) {
                deltaTimeToken = DELTA_TIME_INT;
            } else {
                deltaTimeToken = (int) deltaTime;
            }
            int firstToken = deltaTimeToken | (this.cmd << 18) | (this.states & DELTA_STATE_MASK);
            int batteryLevelInt = buildBatteryLevelInt();
            boolean batteryLevelIntChanged = batteryLevelInt != lastBatteryLevelInt;
            if (batteryLevelIntChanged) {
                firstToken |= 1048576;
            }
            int stateInt = buildStateInt();
            boolean stateIntChanged = stateInt != lastStateInt;
            if (stateIntChanged) {
                firstToken |= 2097152;
            }
            dest.writeInt(firstToken);
            if (deltaTimeToken >= DELTA_TIME_INT) {
                if (deltaTimeToken == DELTA_TIME_INT) {
                    dest.writeInt((int) deltaTime);
                } else {
                    dest.writeLong(deltaTime);
                }
            }
            if (batteryLevelIntChanged) {
                dest.writeInt(batteryLevelInt);
            }
            if (stateIntChanged) {
                dest.writeInt(stateInt);
            }
        }

        private int buildBatteryLevelInt() {
            return ((this.batteryLevel << 24) & (-16777216)) | ((this.batteryTemperature << 14) & 16760832) | (this.batteryVoltage & 16383);
        }

        private int buildStateInt() {
            return ((this.batteryStatus << 28) & (-268435456)) | ((this.batteryHealth << 24) & 251658240) | ((this.batteryPlugType << 22) & 12582912) | (this.states & 4194303);
        }

        public void readDelta(Parcel src) {
            int firstToken = src.readInt();
            int deltaTimeToken = firstToken & 262143;
            this.cmd = (byte) ((firstToken >> 18) & 3);
            if (deltaTimeToken < DELTA_TIME_ABS) {
                this.time += deltaTimeToken;
            } else if (deltaTimeToken == DELTA_TIME_ABS) {
                this.time = src.readLong();
                readFromParcel(src);
                return;
            } else if (deltaTimeToken == DELTA_TIME_INT) {
                int delta = src.readInt();
                this.time += delta;
            } else {
                long delta2 = src.readLong();
                this.time += delta2;
            }
            if ((firstToken & 1048576) != 0) {
                int batteryLevelInt = src.readInt();
                this.batteryLevel = (byte) ((batteryLevelInt >> 24) & 255);
                this.batteryTemperature = (char) ((batteryLevelInt >> 14) & 1023);
                this.batteryVoltage = (char) (batteryLevelInt & 16383);
            }
            if ((firstToken & 2097152) != 0) {
                int stateInt = src.readInt();
                this.states = (firstToken & DELTA_STATE_MASK) | (stateInt & 4194303);
                this.batteryStatus = (byte) ((stateInt >> 28) & 15);
                this.batteryHealth = (byte) ((stateInt >> 24) & 15);
                this.batteryPlugType = (byte) ((stateInt >> 22) & 3);
                return;
            }
            this.states = (firstToken & DELTA_STATE_MASK) | (this.states & 4194303);
        }

        public void clear() {
            this.time = 0L;
            this.cmd = (byte) 0;
            this.batteryLevel = (byte) 0;
            this.batteryStatus = (byte) 0;
            this.batteryHealth = (byte) 0;
            this.batteryPlugType = (byte) 0;
            this.batteryTemperature = (char) 0;
            this.batteryVoltage = (char) 0;
            this.states = 0;
        }

        public void setTo(HistoryItem o) {
            this.time = o.time;
            this.cmd = o.cmd;
            this.batteryLevel = o.batteryLevel;
            this.batteryStatus = o.batteryStatus;
            this.batteryHealth = o.batteryHealth;
            this.batteryPlugType = o.batteryPlugType;
            this.batteryTemperature = o.batteryTemperature;
            this.batteryVoltage = o.batteryVoltage;
            this.states = o.states;
        }

        public void setTo(long time, byte cmd, HistoryItem o) {
            this.time = time;
            this.cmd = cmd;
            this.batteryLevel = o.batteryLevel;
            this.batteryStatus = o.batteryStatus;
            this.batteryHealth = o.batteryHealth;
            this.batteryPlugType = o.batteryPlugType;
            this.batteryTemperature = o.batteryTemperature;
            this.batteryVoltage = o.batteryVoltage;
            this.states = o.states;
        }

        public boolean same(HistoryItem o) {
            return this.batteryLevel == o.batteryLevel && this.batteryStatus == o.batteryStatus && this.batteryHealth == o.batteryHealth && this.batteryPlugType == o.batteryPlugType && this.batteryTemperature == o.batteryTemperature && this.batteryVoltage == o.batteryVoltage && this.states == o.states;
        }
    }

    /* loaded from: BatteryStats$BitDescription.class */
    public static final class BitDescription {
        public final int mask;
        public final int shift;
        public final String name;
        public final String[] values;

        public BitDescription(int mask, String name) {
            this.mask = mask;
            this.shift = -1;
            this.name = name;
            this.values = null;
        }

        public BitDescription(int mask, int shift, String name, String[] values) {
            this.mask = mask;
            this.shift = shift;
            this.name = name;
            this.values = values;
        }
    }

    public long getRadioDataUptimeMs() {
        return getRadioDataUptime() / 1000;
    }

    private static final void formatTimeRaw(StringBuilder out, long seconds) {
        long days = seconds / 86400;
        if (days != 0) {
            out.append(days);
            out.append("d ");
        }
        long used = days * 60 * 60 * 24;
        long hours = (seconds - used) / 3600;
        if (hours != 0 || used != 0) {
            out.append(hours);
            out.append("h ");
        }
        long used2 = used + (hours * 60 * 60);
        long mins = (seconds - used2) / 60;
        if (mins != 0 || used2 != 0) {
            out.append(mins);
            out.append("m ");
        }
        long used3 = used2 + (mins * 60);
        if (seconds != 0 || used3 != 0) {
            out.append(seconds - used3);
            out.append("s ");
        }
    }

    private static final void formatTime(StringBuilder sb, long time) {
        long sec = time / 100;
        formatTimeRaw(sb, sec);
        sb.append((time - (sec * 100)) * 10);
        sb.append("ms ");
    }

    private static final void formatTimeMs(StringBuilder sb, long time) {
        long sec = time / 1000;
        formatTimeRaw(sb, sec);
        sb.append(time - (sec * 1000));
        sb.append("ms ");
    }

    private final String formatRatioLocked(long num, long den) {
        if (den == 0) {
            return "---%";
        }
        float perc = (((float) num) / ((float) den)) * 100.0f;
        this.mFormatBuilder.setLength(0);
        this.mFormatter.format("%.1f%%", Float.valueOf(perc));
        return this.mFormatBuilder.toString();
    }

    private final String formatBytesLocked(long bytes) {
        this.mFormatBuilder.setLength(0);
        if (bytes < 1024) {
            return bytes + "B";
        }
        if (bytes < 1048576) {
            this.mFormatter.format("%.2fKB", Double.valueOf(bytes / 1024.0d));
            return this.mFormatBuilder.toString();
        } else if (bytes < 1073741824) {
            this.mFormatter.format("%.2fMB", Double.valueOf(bytes / 1048576.0d));
            return this.mFormatBuilder.toString();
        } else {
            this.mFormatter.format("%.2fGB", Double.valueOf(bytes / 1.073741824E9d));
            return this.mFormatBuilder.toString();
        }
    }

    private static long computeWakeLock(Timer timer, long batteryRealtime, int which) {
        if (timer != null) {
            long totalTimeMicros = timer.getTotalTimeLocked(batteryRealtime, which);
            long totalTimeMillis = (totalTimeMicros + 500) / 1000;
            return totalTimeMillis;
        }
        return 0L;
    }

    private static final String printWakeLock(StringBuilder sb, Timer timer, long batteryRealtime, String name, int which, String linePrefix) {
        if (timer != null) {
            long totalTimeMillis = computeWakeLock(timer, batteryRealtime, which);
            int count = timer.getCountLocked(which);
            if (totalTimeMillis != 0) {
                sb.append(linePrefix);
                formatTimeMs(sb, totalTimeMillis);
                if (name != null) {
                    sb.append(name);
                    sb.append(' ');
                }
                sb.append('(');
                sb.append(count);
                sb.append(" times)");
                return ", ";
            }
        }
        return linePrefix;
    }

    private static final String printWakeLockCheckin(StringBuilder sb, Timer timer, long now, String name, int which, String linePrefix) {
        long totalTimeMicros = 0;
        int count = 0;
        if (timer != null) {
            totalTimeMicros = timer.getTotalTimeLocked(now, which);
            count = timer.getCountLocked(which);
        }
        sb.append(linePrefix);
        sb.append((totalTimeMicros + 500) / 1000);
        sb.append(',');
        sb.append(name != null ? name + Separators.COMMA : "");
        sb.append(count);
        return Separators.COMMA;
    }

    private static final void dumpLine(PrintWriter pw, int uid, String category, String type, Object... args) {
        pw.print(7);
        pw.print(',');
        pw.print(uid);
        pw.print(',');
        pw.print(category);
        pw.print(',');
        pw.print(type);
        for (Object arg : args) {
            pw.print(',');
            pw.print(arg);
        }
        pw.println();
    }

    public final void dumpCheckinLocked(PrintWriter pw, int which, int reqUid) {
        long rawUptime = SystemClock.uptimeMillis() * 1000;
        long rawRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryUptime = getBatteryUptime(rawUptime);
        long batteryRealtime = getBatteryRealtime(rawRealtime);
        long whichBatteryUptime = computeBatteryUptime(rawUptime, which);
        long whichBatteryRealtime = computeBatteryRealtime(rawRealtime, which);
        long totalRealtime = computeRealtime(rawRealtime, which);
        long totalUptime = computeUptime(rawUptime, which);
        long screenOnTime = getScreenOnTime(batteryRealtime, which);
        long phoneOnTime = getPhoneOnTime(batteryRealtime, which);
        long wifiOnTime = getWifiOnTime(batteryRealtime, which);
        long wifiRunningTime = getGlobalWifiRunningTime(batteryRealtime, which);
        long bluetoothOnTime = getBluetoothOnTime(batteryRealtime, which);
        StringBuilder sb = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int NU = uidStats.size();
        String category = STAT_NAMES[which];
        Object[] objArr = new Object[5];
        objArr[0] = which == 0 ? Integer.valueOf(getStartCount()) : "N/A";
        objArr[1] = Long.valueOf(whichBatteryRealtime / 1000);
        objArr[2] = Long.valueOf(whichBatteryUptime / 1000);
        objArr[3] = Long.valueOf(totalRealtime / 1000);
        objArr[4] = Long.valueOf(totalUptime / 1000);
        dumpLine(pw, 0, category, BATTERY_DATA, objArr);
        long mobileRxTotal = 0;
        long mobileTxTotal = 0;
        long wifiRxTotal = 0;
        long wifiTxTotal = 0;
        long fullWakeLockTimeTotal = 0;
        long partialWakeLockTimeTotal = 0;
        for (int iu = 0; iu < NU; iu++) {
            Uid u = uidStats.valueAt(iu);
            mobileRxTotal += u.getNetworkActivityCount(0, which);
            mobileTxTotal += u.getNetworkActivityCount(1, which);
            wifiRxTotal += u.getNetworkActivityCount(2, which);
            wifiTxTotal += u.getNetworkActivityCount(3, which);
            Map<String, ? extends Uid.Wakelock> wakelocks = u.getWakelockStats();
            if (wakelocks.size() > 0) {
                for (Map.Entry<String, ? extends Uid.Wakelock> ent : wakelocks.entrySet()) {
                    Uid.Wakelock wl = ent.getValue();
                    Timer fullWakeTimer = wl.getWakeTime(1);
                    if (fullWakeTimer != null) {
                        fullWakeLockTimeTotal += fullWakeTimer.getTotalTimeLocked(batteryRealtime, which);
                    }
                    Timer partialWakeTimer = wl.getWakeTime(0);
                    if (partialWakeTimer != null) {
                        partialWakeLockTimeTotal += partialWakeTimer.getTotalTimeLocked(batteryRealtime, which);
                    }
                }
            }
        }
        dumpLine(pw, 0, category, MISC_DATA, Long.valueOf(screenOnTime / 1000), Long.valueOf(phoneOnTime / 1000), Long.valueOf(wifiOnTime / 1000), Long.valueOf(wifiRunningTime / 1000), Long.valueOf(bluetoothOnTime / 1000), Long.valueOf(mobileRxTotal), Long.valueOf(mobileTxTotal), Long.valueOf(wifiRxTotal), Long.valueOf(wifiTxTotal), Long.valueOf(fullWakeLockTimeTotal), Long.valueOf(partialWakeLockTimeTotal), Integer.valueOf(getInputEventCount(which)));
        Object[] args = new Object[5];
        for (int i = 0; i < 5; i++) {
            args[i] = Long.valueOf(getScreenBrightnessTime(i, batteryRealtime, which) / 1000);
        }
        dumpLine(pw, 0, category, SCREEN_BRIGHTNESS_DATA, args);
        Object[] args2 = new Object[5];
        for (int i2 = 0; i2 < 5; i2++) {
            args2[i2] = Long.valueOf(getPhoneSignalStrengthTime(i2, batteryRealtime, which) / 1000);
        }
        dumpLine(pw, 0, category, SIGNAL_STRENGTH_TIME_DATA, args2);
        dumpLine(pw, 0, category, SIGNAL_SCANNING_TIME_DATA, Long.valueOf(getPhoneSignalScanningTime(batteryRealtime, which) / 1000));
        for (int i3 = 0; i3 < 5; i3++) {
            args2[i3] = Integer.valueOf(getPhoneSignalStrengthCount(i3, which));
        }
        dumpLine(pw, 0, category, SIGNAL_STRENGTH_COUNT_DATA, args2);
        Object[] args3 = new Object[16];
        for (int i4 = 0; i4 < 16; i4++) {
            args3[i4] = Long.valueOf(getPhoneDataConnectionTime(i4, batteryRealtime, which) / 1000);
        }
        dumpLine(pw, 0, category, DATA_CONNECTION_TIME_DATA, args3);
        for (int i5 = 0; i5 < 16; i5++) {
            args3[i5] = Integer.valueOf(getPhoneDataConnectionCount(i5, which));
        }
        dumpLine(pw, 0, category, DATA_CONNECTION_COUNT_DATA, args3);
        if (which == 3) {
            dumpLine(pw, 0, category, BATTERY_LEVEL_DATA, Integer.valueOf(getDischargeStartLevel()), Integer.valueOf(getDischargeCurrentLevel()));
        }
        if (which == 3) {
            dumpLine(pw, 0, category, BATTERY_DISCHARGE_DATA, Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()));
        } else {
            dumpLine(pw, 0, category, BATTERY_DISCHARGE_DATA, Integer.valueOf(getLowDischargeAmountSinceCharge()), Integer.valueOf(getHighDischargeAmountSinceCharge()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()));
        }
        if (reqUid < 0) {
            Map<String, ? extends Timer> kernelWakelocks = getKernelWakelockStats();
            if (kernelWakelocks.size() > 0) {
                for (Map.Entry<String, ? extends Timer> ent2 : kernelWakelocks.entrySet()) {
                    sb.setLength(0);
                    printWakeLockCheckin(sb, ent2.getValue(), batteryRealtime, null, which, "");
                    dumpLine(pw, 0, category, KERNEL_WAKELOCK_DATA, ent2.getKey(), sb.toString());
                }
            }
        }
        for (int iu2 = 0; iu2 < NU; iu2++) {
            int uid = uidStats.keyAt(iu2);
            if (reqUid < 0 || uid == reqUid) {
                Uid u2 = uidStats.valueAt(iu2);
                long mobileRx = u2.getNetworkActivityCount(0, which);
                long mobileTx = u2.getNetworkActivityCount(1, which);
                long wifiRx = u2.getNetworkActivityCount(2, which);
                long wifiTx = u2.getNetworkActivityCount(3, which);
                long fullWifiLockOnTime = u2.getFullWifiLockTime(batteryRealtime, which);
                long wifiScanTime = u2.getWifiScanTime(batteryRealtime, which);
                long uidWifiRunningTime = u2.getWifiRunningTime(batteryRealtime, which);
                if (mobileRx > 0 || mobileTx > 0 || wifiRx > 0 || wifiTx > 0) {
                    dumpLine(pw, uid, category, NETWORK_DATA, Long.valueOf(mobileRx), Long.valueOf(mobileTx), Long.valueOf(wifiRx), Long.valueOf(wifiTx));
                }
                if (fullWifiLockOnTime != 0 || wifiScanTime != 0 || uidWifiRunningTime != 0) {
                    dumpLine(pw, uid, category, WIFI_DATA, Long.valueOf(fullWifiLockOnTime), Long.valueOf(wifiScanTime), Long.valueOf(uidWifiRunningTime));
                }
                if (u2.hasUserActivity()) {
                    Object[] args4 = new Object[3];
                    boolean hasData = false;
                    for (int i6 = 0; i6 < 3; i6++) {
                        int val = u2.getUserActivityCount(i6, which);
                        args4[i6] = Integer.valueOf(val);
                        if (val != 0) {
                            hasData = true;
                        }
                    }
                    if (hasData) {
                        dumpLine(pw, 0, category, USER_ACTIVITY_DATA, args4);
                    }
                }
                Map<String, ? extends Uid.Wakelock> wakelocks2 = u2.getWakelockStats();
                if (wakelocks2.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Wakelock> ent3 : wakelocks2.entrySet()) {
                        Uid.Wakelock wl2 = ent3.getValue();
                        sb.setLength(0);
                        String linePrefix = printWakeLockCheckin(sb, wl2.getWakeTime(1), batteryRealtime, FullBackup.DATA_TREE_TOKEN, which, "");
                        printWakeLockCheckin(sb, wl2.getWakeTime(2), batteryRealtime, "w", which, printWakeLockCheckin(sb, wl2.getWakeTime(0), batteryRealtime, "p", which, linePrefix));
                        if (sb.length() > 0) {
                            String name = ent3.getKey();
                            if (name.indexOf(44) >= 0) {
                                name = name.replace(',', '_');
                            }
                            dumpLine(pw, uid, category, WAKELOCK_DATA, name, sb.toString());
                        }
                    }
                }
                Map<Integer, ? extends Uid.Sensor> sensors = u2.getSensorStats();
                if (sensors.size() > 0) {
                    for (Map.Entry<Integer, ? extends Uid.Sensor> ent4 : sensors.entrySet()) {
                        Uid.Sensor se = ent4.getValue();
                        int sensorNumber = ent4.getKey().intValue();
                        Timer timer = se.getSensorTime();
                        if (timer != null) {
                            long totalTime = (timer.getTotalTimeLocked(batteryRealtime, which) + 500) / 1000;
                            int count = timer.getCountLocked(which);
                            if (totalTime != 0) {
                                dumpLine(pw, uid, category, SENSOR_DATA, Integer.valueOf(sensorNumber), Long.valueOf(totalTime), Integer.valueOf(count));
                            }
                        }
                    }
                }
                Timer vibTimer = u2.getVibratorOnTimer();
                if (vibTimer != null) {
                    long totalTime2 = (vibTimer.getTotalTimeLocked(batteryRealtime, which) + 500) / 1000;
                    int count2 = vibTimer.getCountLocked(which);
                    if (totalTime2 != 0) {
                        dumpLine(pw, uid, category, VIBRATOR_DATA, Long.valueOf(totalTime2), Integer.valueOf(count2));
                    }
                }
                Timer fgTimer = u2.getForegroundActivityTimer();
                if (fgTimer != null) {
                    long totalTime3 = (fgTimer.getTotalTimeLocked(batteryRealtime, which) + 500) / 1000;
                    int count3 = fgTimer.getCountLocked(which);
                    if (totalTime3 != 0) {
                        dumpLine(pw, uid, category, FOREGROUND_DATA, Long.valueOf(totalTime3), Integer.valueOf(count3));
                    }
                }
                Map<String, ? extends Uid.Proc> processStats = u2.getProcessStats();
                if (processStats.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Proc> ent5 : processStats.entrySet()) {
                        Uid.Proc ps = ent5.getValue();
                        long userMillis = ps.getUserTime(which) * 10;
                        long systemMillis = ps.getSystemTime(which) * 10;
                        long foregroundMillis = ps.getForegroundTime(which) * 10;
                        long starts = ps.getStarts(which);
                        if (userMillis != 0 || systemMillis != 0 || foregroundMillis != 0 || starts != 0) {
                            dumpLine(pw, uid, category, PROCESS_DATA, ent5.getKey(), Long.valueOf(userMillis), Long.valueOf(systemMillis), Long.valueOf(foregroundMillis), Long.valueOf(starts));
                        }
                    }
                }
                Map<String, ? extends Uid.Pkg> packageStats = u2.getPackageStats();
                if (packageStats.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Pkg> ent6 : packageStats.entrySet()) {
                        Uid.Pkg ps2 = ent6.getValue();
                        int wakeups = ps2.getWakeups(which);
                        Map<String, ? extends Uid.Pkg.Serv> serviceStats = ps2.getServiceStats();
                        for (Map.Entry<String, ? extends Uid.Pkg.Serv> sent : serviceStats.entrySet()) {
                            Uid.Pkg.Serv ss = sent.getValue();
                            long startTime = ss.getStartTime(batteryUptime, which);
                            int starts2 = ss.getStarts(which);
                            int launches = ss.getLaunches(which);
                            if (startTime != 0 || starts2 != 0 || launches != 0) {
                                dumpLine(pw, uid, category, APK_DATA, Integer.valueOf(wakeups), ent6.getKey(), sent.getKey(), Long.valueOf(startTime / 1000), Integer.valueOf(starts2), Integer.valueOf(launches));
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BatteryStats$TimerEntry.class */
    public static final class TimerEntry {
        final String mName;
        final int mId;
        final Timer mTimer;
        final long mTime;

        TimerEntry(String name, int id, Timer timer, long time) {
            this.mName = name;
            this.mId = id;
            this.mTimer = timer;
            this.mTime = time;
        }
    }

    public final void dumpLocked(PrintWriter pw, String prefix, int which, int reqUid) {
        long rawUptime = SystemClock.uptimeMillis() * 1000;
        long rawRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryUptime = getBatteryUptime(rawUptime);
        long batteryRealtime = getBatteryRealtime(rawRealtime);
        long whichBatteryUptime = computeBatteryUptime(rawUptime, which);
        long whichBatteryRealtime = computeBatteryRealtime(rawRealtime, which);
        long totalRealtime = computeRealtime(rawRealtime, which);
        long totalUptime = computeUptime(rawUptime, which);
        StringBuilder sb = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int NU = uidStats.size();
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Time on battery: ");
        formatTimeMs(sb, whichBatteryRealtime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(whichBatteryRealtime, totalRealtime));
        sb.append(") realtime, ");
        formatTimeMs(sb, whichBatteryUptime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(whichBatteryUptime, totalRealtime));
        sb.append(") uptime");
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Total run time: ");
        formatTimeMs(sb, totalRealtime / 1000);
        sb.append("realtime, ");
        formatTimeMs(sb, totalUptime / 1000);
        sb.append("uptime, ");
        pw.println(sb.toString());
        long screenOnTime = getScreenOnTime(batteryRealtime, which);
        long phoneOnTime = getPhoneOnTime(batteryRealtime, which);
        long wifiRunningTime = getGlobalWifiRunningTime(batteryRealtime, which);
        long wifiOnTime = getWifiOnTime(batteryRealtime, which);
        long bluetoothOnTime = getBluetoothOnTime(batteryRealtime, which);
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Screen on: ");
        formatTimeMs(sb, screenOnTime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(screenOnTime, whichBatteryRealtime));
        sb.append("), Input events: ");
        sb.append(getInputEventCount(which));
        sb.append(", Active phone call: ");
        formatTimeMs(sb, phoneOnTime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(phoneOnTime, whichBatteryRealtime));
        sb.append(Separators.RPAREN);
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Screen brightnesses: ");
        boolean didOne = false;
        for (int i = 0; i < 5; i++) {
            long time = getScreenBrightnessTime(i, batteryRealtime, which);
            if (time != 0) {
                if (didOne) {
                    sb.append(", ");
                }
                didOne = true;
                sb.append(SCREEN_BRIGHTNESS_NAMES[i]);
                sb.append(Separators.SP);
                formatTimeMs(sb, time / 1000);
                sb.append(Separators.LPAREN);
                sb.append(formatRatioLocked(time, screenOnTime));
                sb.append(Separators.RPAREN);
            }
        }
        if (!didOne) {
            sb.append("No activity");
        }
        pw.println(sb.toString());
        long mobileRxTotal = 0;
        long mobileTxTotal = 0;
        long wifiRxTotal = 0;
        long wifiTxTotal = 0;
        long fullWakeLockTimeTotalMicros = 0;
        long partialWakeLockTimeTotalMicros = 0;
        Comparator<TimerEntry> timerComparator = new Comparator<TimerEntry>() { // from class: android.os.BatteryStats.1
            @Override // java.util.Comparator
            public int compare(TimerEntry lhs, TimerEntry rhs) {
                long lhsTime = lhs.mTime;
                long rhsTime = rhs.mTime;
                if (lhsTime < rhsTime) {
                    return 1;
                }
                if (lhsTime > rhsTime) {
                    return -1;
                }
                return 0;
            }
        };
        if (reqUid < 0) {
            Map<String, ? extends Timer> kernelWakelocks = getKernelWakelockStats();
            if (kernelWakelocks.size() > 0) {
                ArrayList<TimerEntry> timers = new ArrayList<>();
                for (Map.Entry<String, ? extends Timer> ent : kernelWakelocks.entrySet()) {
                    Timer timer = ent.getValue();
                    long totalTimeMillis = computeWakeLock(timer, batteryRealtime, which);
                    if (totalTimeMillis > 0) {
                        timers.add(new TimerEntry(ent.getKey(), 0, timer, totalTimeMillis));
                    }
                }
                Collections.sort(timers, timerComparator);
                for (int i2 = 0; i2 < timers.size(); i2++) {
                    TimerEntry timer2 = timers.get(i2);
                    sb.setLength(0);
                    sb.append(prefix);
                    sb.append("  Kernel Wake lock ");
                    sb.append(timer2.mName);
                    String linePrefix = printWakeLock(sb, timer2.mTimer, batteryRealtime, null, which, ": ");
                    if (!linePrefix.equals(": ")) {
                        sb.append(" realtime");
                        pw.println(sb.toString());
                    }
                }
            }
        }
        ArrayList<TimerEntry> timers2 = new ArrayList<>();
        for (int iu = 0; iu < NU; iu++) {
            Uid u = uidStats.valueAt(iu);
            mobileRxTotal += u.getNetworkActivityCount(0, which);
            mobileTxTotal += u.getNetworkActivityCount(1, which);
            wifiRxTotal += u.getNetworkActivityCount(2, which);
            wifiTxTotal += u.getNetworkActivityCount(3, which);
            Map<String, ? extends Uid.Wakelock> wakelocks = u.getWakelockStats();
            if (wakelocks.size() > 0) {
                for (Map.Entry<String, ? extends Uid.Wakelock> ent2 : wakelocks.entrySet()) {
                    Uid.Wakelock wl = ent2.getValue();
                    Timer fullWakeTimer = wl.getWakeTime(1);
                    if (fullWakeTimer != null) {
                        fullWakeLockTimeTotalMicros += fullWakeTimer.getTotalTimeLocked(batteryRealtime, which);
                    }
                    Timer partialWakeTimer = wl.getWakeTime(0);
                    if (partialWakeTimer != null) {
                        long totalTimeMicros = partialWakeTimer.getTotalTimeLocked(batteryRealtime, which);
                        if (totalTimeMicros > 0) {
                            if (reqUid < 0) {
                                timers2.add(new TimerEntry(ent2.getKey(), u.getUid(), partialWakeTimer, totalTimeMicros));
                            }
                            partialWakeLockTimeTotalMicros += totalTimeMicros;
                        }
                    }
                }
            }
        }
        pw.print(prefix);
        pw.print("  Mobile total received: ");
        pw.print(formatBytesLocked(mobileRxTotal));
        pw.print(", Total sent: ");
        pw.println(formatBytesLocked(mobileTxTotal));
        pw.print(prefix);
        pw.print("  Wi-Fi total received: ");
        pw.print(formatBytesLocked(wifiRxTotal));
        pw.print(", Total sent: ");
        pw.println(formatBytesLocked(wifiTxTotal));
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Total full wakelock time: ");
        formatTimeMs(sb, (fullWakeLockTimeTotalMicros + 500) / 1000);
        sb.append(", Total partial wakelock time: ");
        formatTimeMs(sb, (partialWakeLockTimeTotalMicros + 500) / 1000);
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Signal levels: ");
        boolean didOne2 = false;
        for (int i3 = 0; i3 < 5; i3++) {
            long time2 = getPhoneSignalStrengthTime(i3, batteryRealtime, which);
            if (time2 != 0) {
                if (didOne2) {
                    sb.append(", ");
                }
                didOne2 = true;
                sb.append(SignalStrength.SIGNAL_STRENGTH_NAMES[i3]);
                sb.append(Separators.SP);
                formatTimeMs(sb, time2 / 1000);
                sb.append(Separators.LPAREN);
                sb.append(formatRatioLocked(time2, whichBatteryRealtime));
                sb.append(") ");
                sb.append(getPhoneSignalStrengthCount(i3, which));
                sb.append("x");
            }
        }
        if (!didOne2) {
            sb.append("No activity");
        }
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Signal scanning time: ");
        formatTimeMs(sb, getPhoneSignalScanningTime(batteryRealtime, which) / 1000);
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Radio types: ");
        boolean didOne3 = false;
        for (int i4 = 0; i4 < 16; i4++) {
            long time3 = getPhoneDataConnectionTime(i4, batteryRealtime, which);
            if (time3 != 0) {
                if (didOne3) {
                    sb.append(", ");
                }
                didOne3 = true;
                sb.append(DATA_CONNECTION_NAMES[i4]);
                sb.append(Separators.SP);
                formatTimeMs(sb, time3 / 1000);
                sb.append(Separators.LPAREN);
                sb.append(formatRatioLocked(time3, whichBatteryRealtime));
                sb.append(") ");
                sb.append(getPhoneDataConnectionCount(i4, which));
                sb.append("x");
            }
        }
        if (!didOne3) {
            sb.append("No activity");
        }
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Radio data uptime when unplugged: ");
        sb.append(getRadioDataUptime() / 1000);
        sb.append(" ms");
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("  Wifi on: ");
        formatTimeMs(sb, wifiOnTime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(wifiOnTime, whichBatteryRealtime));
        sb.append("), Wifi running: ");
        formatTimeMs(sb, wifiRunningTime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(wifiRunningTime, whichBatteryRealtime));
        sb.append("), Bluetooth on: ");
        formatTimeMs(sb, bluetoothOnTime / 1000);
        sb.append(Separators.LPAREN);
        sb.append(formatRatioLocked(bluetoothOnTime, whichBatteryRealtime));
        sb.append(Separators.RPAREN);
        pw.println(sb.toString());
        pw.println(Separators.SP);
        if (which == 3) {
            if (getIsOnBattery()) {
                pw.print(prefix);
                pw.println("  Device is currently unplugged");
                pw.print(prefix);
                pw.print("    Discharge cycle start level: ");
                pw.println(getDischargeStartLevel());
                pw.print(prefix);
                pw.print("    Discharge cycle current level: ");
                pw.println(getDischargeCurrentLevel());
            } else {
                pw.print(prefix);
                pw.println("  Device is currently plugged into power");
                pw.print(prefix);
                pw.print("    Last discharge cycle start level: ");
                pw.println(getDischargeStartLevel());
                pw.print(prefix);
                pw.print("    Last discharge cycle end level: ");
                pw.println(getDischargeCurrentLevel());
            }
            pw.print(prefix);
            pw.print("    Amount discharged while screen on: ");
            pw.println(getDischargeAmountScreenOn());
            pw.print(prefix);
            pw.print("    Amount discharged while screen off: ");
            pw.println(getDischargeAmountScreenOff());
            pw.println(Separators.SP);
        } else {
            pw.print(prefix);
            pw.println("  Device battery use since last full charge");
            pw.print(prefix);
            pw.print("    Amount discharged (lower bound): ");
            pw.println(getLowDischargeAmountSinceCharge());
            pw.print(prefix);
            pw.print("    Amount discharged (upper bound): ");
            pw.println(getHighDischargeAmountSinceCharge());
            pw.print(prefix);
            pw.print("    Amount discharged while screen on: ");
            pw.println(getDischargeAmountScreenOnSinceCharge());
            pw.print(prefix);
            pw.print("    Amount discharged while screen off: ");
            pw.println(getDischargeAmountScreenOffSinceCharge());
            pw.println();
        }
        if (timers2.size() > 0) {
            Collections.sort(timers2, timerComparator);
            pw.print(prefix);
            pw.println("  All partial wake locks:");
            for (int i5 = 0; i5 < timers2.size(); i5++) {
                TimerEntry timer3 = timers2.get(i5);
                sb.setLength(0);
                sb.append("  Wake lock ");
                UserHandle.formatUid(sb, timer3.mId);
                sb.append(Separators.SP);
                sb.append(timer3.mName);
                printWakeLock(sb, timer3.mTimer, batteryRealtime, null, which, ": ");
                sb.append(" realtime");
                pw.println(sb.toString());
            }
            timers2.clear();
            pw.println();
        }
        for (int iu2 = 0; iu2 < NU; iu2++) {
            int uid = uidStats.keyAt(iu2);
            if (reqUid < 0 || uid == reqUid || uid == 1000) {
                Uid u2 = uidStats.valueAt(iu2);
                pw.print(prefix);
                pw.print("  ");
                UserHandle.formatUid(pw, uid);
                pw.println(Separators.COLON);
                boolean uidActivity = false;
                long mobileRxBytes = u2.getNetworkActivityCount(0, which);
                long mobileTxBytes = u2.getNetworkActivityCount(1, which);
                long wifiRxBytes = u2.getNetworkActivityCount(2, which);
                long wifiTxBytes = u2.getNetworkActivityCount(3, which);
                long fullWifiLockOnTime = u2.getFullWifiLockTime(batteryRealtime, which);
                long wifiScanTime = u2.getWifiScanTime(batteryRealtime, which);
                long uidWifiRunningTime = u2.getWifiRunningTime(batteryRealtime, which);
                if (mobileRxBytes > 0 || mobileTxBytes > 0) {
                    pw.print(prefix);
                    pw.print("    Mobile network: ");
                    pw.print(formatBytesLocked(mobileRxBytes));
                    pw.print(" received, ");
                    pw.print(formatBytesLocked(mobileTxBytes));
                    pw.println(" sent");
                }
                if (wifiRxBytes > 0 || wifiTxBytes > 0) {
                    pw.print(prefix);
                    pw.print("    Wi-Fi network: ");
                    pw.print(formatBytesLocked(wifiRxBytes));
                    pw.print(" received, ");
                    pw.print(formatBytesLocked(wifiTxBytes));
                    pw.println(" sent");
                }
                if (u2.hasUserActivity()) {
                    boolean hasData = false;
                    for (int i6 = 0; i6 < 3; i6++) {
                        int val = u2.getUserActivityCount(i6, which);
                        if (val != 0) {
                            if (!hasData) {
                                sb.setLength(0);
                                sb.append("    User activity: ");
                                hasData = true;
                            } else {
                                sb.append(", ");
                            }
                            sb.append(val);
                            sb.append(Separators.SP);
                            sb.append(Uid.USER_ACTIVITY_TYPES[i6]);
                        }
                    }
                    if (hasData) {
                        pw.println(sb.toString());
                    }
                }
                if (fullWifiLockOnTime != 0 || wifiScanTime != 0 || uidWifiRunningTime != 0) {
                    sb.setLength(0);
                    sb.append(prefix);
                    sb.append("    Wifi Running: ");
                    formatTimeMs(sb, uidWifiRunningTime / 1000);
                    sb.append(Separators.LPAREN);
                    sb.append(formatRatioLocked(uidWifiRunningTime, whichBatteryRealtime));
                    sb.append(")\n");
                    sb.append(prefix);
                    sb.append("    Full Wifi Lock: ");
                    formatTimeMs(sb, fullWifiLockOnTime / 1000);
                    sb.append(Separators.LPAREN);
                    sb.append(formatRatioLocked(fullWifiLockOnTime, whichBatteryRealtime));
                    sb.append(")\n");
                    sb.append(prefix);
                    sb.append("    Wifi Scan: ");
                    formatTimeMs(sb, wifiScanTime / 1000);
                    sb.append(Separators.LPAREN);
                    sb.append(formatRatioLocked(wifiScanTime, whichBatteryRealtime));
                    sb.append(Separators.RPAREN);
                    pw.println(sb.toString());
                }
                Map<String, ? extends Uid.Wakelock> wakelocks2 = u2.getWakelockStats();
                if (wakelocks2.size() > 0) {
                    long totalFull = 0;
                    long totalPartial = 0;
                    long totalWindow = 0;
                    int count = 0;
                    for (Map.Entry<String, ? extends Uid.Wakelock> ent3 : wakelocks2.entrySet()) {
                        Uid.Wakelock wl2 = ent3.getValue();
                        sb.setLength(0);
                        sb.append(prefix);
                        sb.append("    Wake lock ");
                        sb.append(ent3.getKey());
                        String linePrefix2 = printWakeLock(sb, wl2.getWakeTime(1), batteryRealtime, "full", which, ": ");
                        if (!printWakeLock(sb, wl2.getWakeTime(2), batteryRealtime, Context.WINDOW_SERVICE, which, printWakeLock(sb, wl2.getWakeTime(0), batteryRealtime, "partial", which, linePrefix2)).equals(": ")) {
                            sb.append(" realtime");
                            pw.println(sb.toString());
                            uidActivity = true;
                            count++;
                        }
                        totalFull += computeWakeLock(wl2.getWakeTime(1), batteryRealtime, which);
                        totalPartial += computeWakeLock(wl2.getWakeTime(0), batteryRealtime, which);
                        totalWindow += computeWakeLock(wl2.getWakeTime(2), batteryRealtime, which);
                    }
                    if (count > 1 && (totalFull != 0 || totalPartial != 0 || totalWindow != 0)) {
                        sb.setLength(0);
                        sb.append(prefix);
                        sb.append("    TOTAL wake: ");
                        boolean needComma = false;
                        if (totalFull != 0) {
                            needComma = true;
                            formatTimeMs(sb, totalFull);
                            sb.append("full");
                        }
                        if (totalPartial != 0) {
                            if (needComma) {
                                sb.append(", ");
                            }
                            needComma = true;
                            formatTimeMs(sb, totalPartial);
                            sb.append("partial");
                        }
                        if (totalWindow != 0) {
                            if (needComma) {
                                sb.append(", ");
                            }
                            formatTimeMs(sb, totalWindow);
                            sb.append(Context.WINDOW_SERVICE);
                        }
                        sb.append(" realtime");
                        pw.println(sb.toString());
                    }
                }
                Map<Integer, ? extends Uid.Sensor> sensors = u2.getSensorStats();
                if (sensors.size() > 0) {
                    for (Map.Entry<Integer, ? extends Uid.Sensor> ent4 : sensors.entrySet()) {
                        Uid.Sensor se = ent4.getValue();
                        ent4.getKey().intValue();
                        sb.setLength(0);
                        sb.append(prefix);
                        sb.append("    Sensor ");
                        int handle = se.getHandle();
                        if (handle == -10000) {
                            sb.append("GPS");
                        } else {
                            sb.append(handle);
                        }
                        sb.append(": ");
                        Timer timer4 = se.getSensorTime();
                        if (timer4 != null) {
                            long totalTime = (timer4.getTotalTimeLocked(batteryRealtime, which) + 500) / 1000;
                            int count2 = timer4.getCountLocked(which);
                            if (totalTime != 0) {
                                formatTimeMs(sb, totalTime);
                                sb.append("realtime (");
                                sb.append(count2);
                                sb.append(" times)");
                            } else {
                                sb.append("(not used)");
                            }
                        } else {
                            sb.append("(not used)");
                        }
                        pw.println(sb.toString());
                        uidActivity = true;
                    }
                }
                Timer vibTimer = u2.getVibratorOnTimer();
                if (vibTimer != null) {
                    long totalTime2 = (vibTimer.getTotalTimeLocked(batteryRealtime, which) + 500) / 1000;
                    int count3 = vibTimer.getCountLocked(which);
                    if (totalTime2 != 0) {
                        sb.setLength(0);
                        sb.append(prefix);
                        sb.append("    Vibrator: ");
                        formatTimeMs(sb, totalTime2);
                        sb.append("realtime (");
                        sb.append(count3);
                        sb.append(" times)");
                        pw.println(sb.toString());
                        uidActivity = true;
                    }
                }
                Timer fgTimer = u2.getForegroundActivityTimer();
                if (fgTimer != null) {
                    long totalTime3 = (fgTimer.getTotalTimeLocked(batteryRealtime, which) + 500) / 1000;
                    int count4 = fgTimer.getCountLocked(which);
                    if (totalTime3 != 0) {
                        sb.setLength(0);
                        sb.append(prefix);
                        sb.append("    Foreground activities: ");
                        formatTimeMs(sb, totalTime3);
                        sb.append("realtime (");
                        sb.append(count4);
                        sb.append(" times)");
                        pw.println(sb.toString());
                        uidActivity = true;
                    }
                }
                Map<String, ? extends Uid.Proc> processStats = u2.getProcessStats();
                if (processStats.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Proc> ent5 : processStats.entrySet()) {
                        Uid.Proc ps = ent5.getValue();
                        long userTime = ps.getUserTime(which);
                        long systemTime = ps.getSystemTime(which);
                        long foregroundTime = ps.getForegroundTime(which);
                        int starts = ps.getStarts(which);
                        int numExcessive = which == 0 ? ps.countExcessivePowers() : 0;
                        if (userTime != 0 || systemTime != 0 || foregroundTime != 0 || starts != 0 || numExcessive != 0) {
                            sb.setLength(0);
                            sb.append(prefix);
                            sb.append("    Proc ");
                            sb.append(ent5.getKey());
                            sb.append(":\n");
                            sb.append(prefix);
                            sb.append("      CPU: ");
                            formatTime(sb, userTime);
                            sb.append("usr + ");
                            formatTime(sb, systemTime);
                            sb.append("krn ; ");
                            formatTime(sb, foregroundTime);
                            sb.append(FOREGROUND_DATA);
                            if (starts != 0) {
                                sb.append(Separators.RETURN);
                                sb.append(prefix);
                                sb.append("      ");
                                sb.append(starts);
                                sb.append(" proc starts");
                            }
                            pw.println(sb.toString());
                            for (int e = 0; e < numExcessive; e++) {
                                Uid.Proc.ExcessivePower ew = ps.getExcessivePower(e);
                                if (ew != null) {
                                    pw.print(prefix);
                                    pw.print("      * Killed for ");
                                    if (ew.type == 1) {
                                        pw.print("wake lock");
                                    } else if (ew.type == 2) {
                                        pw.print("cpu");
                                    } else {
                                        pw.print("unknown");
                                    }
                                    pw.print(" use: ");
                                    TimeUtils.formatDuration(ew.usedTime, pw);
                                    pw.print(" over ");
                                    TimeUtils.formatDuration(ew.overTime, pw);
                                    pw.print(" (");
                                    pw.print((ew.usedTime * 100) / ew.overTime);
                                    pw.println("%)");
                                }
                            }
                            uidActivity = true;
                        }
                    }
                }
                Map<String, ? extends Uid.Pkg> packageStats = u2.getPackageStats();
                if (packageStats.size() > 0) {
                    for (Map.Entry<String, ? extends Uid.Pkg> ent6 : packageStats.entrySet()) {
                        pw.print(prefix);
                        pw.print("    Apk ");
                        pw.print(ent6.getKey());
                        pw.println(Separators.COLON);
                        boolean apkActivity = false;
                        Uid.Pkg ps2 = ent6.getValue();
                        int wakeups = ps2.getWakeups(which);
                        if (wakeups != 0) {
                            pw.print(prefix);
                            pw.print("      ");
                            pw.print(wakeups);
                            pw.println(" wakeup alarms");
                            apkActivity = true;
                        }
                        Map<String, ? extends Uid.Pkg.Serv> serviceStats = ps2.getServiceStats();
                        if (serviceStats.size() > 0) {
                            for (Map.Entry<String, ? extends Uid.Pkg.Serv> sent : serviceStats.entrySet()) {
                                Uid.Pkg.Serv ss = sent.getValue();
                                long startTime = ss.getStartTime(batteryUptime, which);
                                int starts2 = ss.getStarts(which);
                                int launches = ss.getLaunches(which);
                                if (startTime != 0 || starts2 != 0 || launches != 0) {
                                    sb.setLength(0);
                                    sb.append(prefix);
                                    sb.append("      Service ");
                                    sb.append(sent.getKey());
                                    sb.append(":\n");
                                    sb.append(prefix);
                                    sb.append("        Created for: ");
                                    formatTimeMs(sb, startTime / 1000);
                                    sb.append("uptime\n");
                                    sb.append(prefix);
                                    sb.append("        Starts: ");
                                    sb.append(starts2);
                                    sb.append(", launches: ");
                                    sb.append(launches);
                                    pw.println(sb.toString());
                                    apkActivity = true;
                                }
                            }
                        }
                        if (!apkActivity) {
                            pw.print(prefix);
                            pw.println("      (nothing executed)");
                        }
                        uidActivity = true;
                    }
                }
                if (!uidActivity) {
                    pw.print(prefix);
                    pw.println("    (nothing executed)");
                }
            }
        }
    }

    static void printBitDescriptions(PrintWriter pw, int oldval, int newval, BitDescription[] descriptions) {
        int diff = oldval ^ newval;
        if (diff == 0) {
            return;
        }
        for (BitDescription bd : descriptions) {
            if ((diff & bd.mask) != 0) {
                if (bd.shift < 0) {
                    pw.print((newval & bd.mask) != 0 ? " +" : " -");
                    pw.print(bd.name);
                } else {
                    pw.print(Separators.SP);
                    pw.print(bd.name);
                    pw.print(Separators.EQUALS);
                    int val = (newval & bd.mask) >> bd.shift;
                    if (bd.values != null && val >= 0 && val < bd.values.length) {
                        pw.print(bd.values[val]);
                    } else {
                        pw.print(val);
                    }
                }
            }
        }
    }

    public void prepareForDumpLocked() {
    }

    /* loaded from: BatteryStats$HistoryPrinter.class */
    public static class HistoryPrinter {
        int oldState = 0;
        int oldStatus = -1;
        int oldHealth = -1;
        int oldPlug = -1;
        int oldTemp = -1;
        int oldVolt = -1;

        public void printNextItem(PrintWriter pw, HistoryItem rec, long now) {
            pw.print("  ");
            TimeUtils.formatDuration(rec.time - now, pw, 19);
            pw.print(Separators.SP);
            if (rec.cmd == 2) {
                pw.println(" START");
            } else if (rec.cmd == 3) {
                pw.println(" *OVERFLOW*");
            } else {
                if (rec.batteryLevel < 10) {
                    pw.print("00");
                } else if (rec.batteryLevel < 100) {
                    pw.print("0");
                }
                pw.print((int) rec.batteryLevel);
                pw.print(Separators.SP);
                if (rec.states < 16) {
                    pw.print("0000000");
                } else if (rec.states < 256) {
                    pw.print("000000");
                } else if (rec.states < 4096) {
                    pw.print("00000");
                } else if (rec.states < 65536) {
                    pw.print("0000");
                } else if (rec.states < 1048576) {
                    pw.print("000");
                } else if (rec.states < 16777216) {
                    pw.print("00");
                } else if (rec.states < 268435456) {
                    pw.print("0");
                }
                pw.print(Integer.toHexString(rec.states));
                if (this.oldStatus != rec.batteryStatus) {
                    this.oldStatus = rec.batteryStatus;
                    pw.print(" status=");
                    switch (this.oldStatus) {
                        case 1:
                            pw.print("unknown");
                            break;
                        case 2:
                            pw.print("charging");
                            break;
                        case 3:
                            pw.print("discharging");
                            break;
                        case 4:
                            pw.print("not-charging");
                            break;
                        case 5:
                            pw.print("full");
                            break;
                        default:
                            pw.print(this.oldStatus);
                            break;
                    }
                }
                if (this.oldHealth != rec.batteryHealth) {
                    this.oldHealth = rec.batteryHealth;
                    pw.print(" health=");
                    switch (this.oldHealth) {
                        case 1:
                            pw.print("unknown");
                            break;
                        case 2:
                            pw.print("good");
                            break;
                        case 3:
                            pw.print("overheat");
                            break;
                        case 4:
                            pw.print("dead");
                            break;
                        case 5:
                            pw.print("over-voltage");
                            break;
                        case 6:
                            pw.print("failure");
                            break;
                        default:
                            pw.print(this.oldHealth);
                            break;
                    }
                }
                if (this.oldPlug != rec.batteryPlugType) {
                    this.oldPlug = rec.batteryPlugType;
                    pw.print(" plug=");
                    switch (this.oldPlug) {
                        case 0:
                            pw.print("none");
                            break;
                        case 1:
                            pw.print("ac");
                            break;
                        case 2:
                            pw.print(Context.USB_SERVICE);
                            break;
                        case 3:
                        default:
                            pw.print(this.oldPlug);
                            break;
                        case 4:
                            pw.print("wireless");
                            break;
                    }
                }
                if (this.oldTemp != rec.batteryTemperature) {
                    this.oldTemp = rec.batteryTemperature;
                    pw.print(" temp=");
                    pw.print(this.oldTemp);
                }
                if (this.oldVolt != rec.batteryVoltage) {
                    this.oldVolt = rec.batteryVoltage;
                    pw.print(" volt=");
                    pw.print(this.oldVolt);
                }
                BatteryStats.printBitDescriptions(pw, this.oldState, rec.states, BatteryStats.HISTORY_STATE_DESCRIPTIONS);
                pw.println();
            }
            this.oldState = rec.states;
        }

        public void printNextItemCheckin(PrintWriter pw, HistoryItem rec, long now) {
            pw.print(rec.time - now);
            pw.print(Separators.COMMA);
            if (rec.cmd == 2) {
                pw.print(Telephony.BaseMmsColumns.START);
            } else if (rec.cmd == 3) {
                pw.print("overflow");
            } else {
                pw.print((int) rec.batteryLevel);
                pw.print(Separators.COMMA);
                pw.print(rec.states);
                pw.print(Separators.COMMA);
                pw.print((int) rec.batteryStatus);
                pw.print(Separators.COMMA);
                pw.print((int) rec.batteryHealth);
                pw.print(Separators.COMMA);
                pw.print((int) rec.batteryPlugType);
                pw.print(Separators.COMMA);
                pw.print((int) rec.batteryTemperature);
                pw.print(Separators.COMMA);
                pw.print((int) rec.batteryVoltage);
            }
        }
    }

    public void dumpLocked(PrintWriter pw, boolean isUnpluggedOnly, int reqUid) {
        prepareForDumpLocked();
        long now = getHistoryBaseTime() + SystemClock.elapsedRealtime();
        HistoryItem rec = new HistoryItem();
        if (startIteratingHistoryLocked()) {
            pw.println("Battery History:");
            HistoryPrinter hprinter = new HistoryPrinter();
            while (getNextHistoryLocked(rec)) {
                hprinter.printNextItem(pw, rec, now);
            }
            finishIteratingHistoryLocked();
            pw.println("");
        }
        if (startIteratingOldHistoryLocked()) {
            pw.println("Old battery History:");
            HistoryPrinter hprinter2 = new HistoryPrinter();
            while (getNextOldHistoryLocked(rec)) {
                hprinter2.printNextItem(pw, rec, now);
            }
            finishIteratingOldHistoryLocked();
            pw.println("");
        }
        SparseArray<? extends Uid> uidStats = getUidStats();
        int NU = uidStats.size();
        boolean didPid = false;
        long nowRealtime = SystemClock.elapsedRealtime();
        for (int i = 0; i < NU; i++) {
            Uid uid = uidStats.valueAt(i);
            SparseArray<? extends Uid.Pid> pids = uid.getPidStats();
            if (pids != null) {
                for (int j = 0; j < pids.size(); j++) {
                    Uid.Pid pid = pids.valueAt(j);
                    if (!didPid) {
                        pw.println("Per-PID Stats:");
                        didPid = true;
                    }
                    long time = pid.mWakeSum + (pid.mWakeStart != 0 ? nowRealtime - pid.mWakeStart : 0L);
                    pw.print("  PID ");
                    pw.print(pids.keyAt(j));
                    pw.print(" wake time: ");
                    TimeUtils.formatDuration(time, pw);
                    pw.println("");
                }
            }
        }
        if (didPid) {
            pw.println("");
        }
        if (!isUnpluggedOnly) {
            pw.println("Statistics since last charge:");
            pw.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
            dumpLocked(pw, "", 0, reqUid);
            pw.println("");
        }
        pw.println("Statistics since last unplugged:");
        dumpLocked(pw, "", 3, reqUid);
    }

    public void dumpCheckinLocked(PrintWriter pw, List<ApplicationInfo> apps, boolean isUnpluggedOnly, boolean includeHistory) {
        prepareForDumpLocked();
        long now = getHistoryBaseTime() + SystemClock.elapsedRealtime();
        if (includeHistory) {
            HistoryItem rec = new HistoryItem();
            if (startIteratingHistoryLocked()) {
                HistoryPrinter hprinter = new HistoryPrinter();
                while (getNextHistoryLocked(rec)) {
                    pw.print(7);
                    pw.print(',');
                    pw.print(0);
                    pw.print(',');
                    pw.print(HISTORY_DATA);
                    pw.print(',');
                    hprinter.printNextItemCheckin(pw, rec, now);
                    pw.println();
                }
                finishIteratingHistoryLocked();
            }
        }
        if (apps != null) {
            SparseArray<ArrayList<String>> uids = new SparseArray<>();
            for (int i = 0; i < apps.size(); i++) {
                ApplicationInfo ai = apps.get(i);
                ArrayList<String> pkgs = uids.get(ai.uid);
                if (pkgs == null) {
                    pkgs = new ArrayList<>();
                    uids.put(ai.uid, pkgs);
                }
                pkgs.add(ai.packageName);
            }
            SparseArray<? extends Uid> uidStats = getUidStats();
            int NU = uidStats.size();
            String[] lineArgs = new String[2];
            for (int i2 = 0; i2 < NU; i2++) {
                int uid = uidStats.keyAt(i2);
                ArrayList<String> pkgs2 = uids.get(uid);
                if (pkgs2 != null) {
                    for (int j = 0; j < pkgs2.size(); j++) {
                        lineArgs[0] = Integer.toString(uid);
                        lineArgs[1] = pkgs2.get(j);
                        dumpLine(pw, 0, "i", "uid", lineArgs);
                    }
                }
            }
        }
        if (isUnpluggedOnly) {
            dumpCheckinLocked(pw, 3, -1);
            return;
        }
        dumpCheckinLocked(pw, 0, -1);
        dumpCheckinLocked(pw, 3, -1);
    }
}