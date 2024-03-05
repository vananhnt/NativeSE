package com.android.server.am;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryStats;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.WorkSource;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/* loaded from: BatteryStatsService.class */
public final class BatteryStatsService extends IBatteryStats.Stub {
    static IBatteryStats sService;
    final BatteryStatsImpl mStats;
    Context mContext;
    private boolean mBluetoothPendingStats;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothProfile.ServiceListener mBluetoothProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.server.am.BatteryStatsService.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            BatteryStatsService.this.mBluetoothHeadset = (BluetoothHeadset) proxy;
            synchronized (BatteryStatsService.this.mStats) {
                if (BatteryStatsService.this.mBluetoothPendingStats) {
                    BatteryStatsService.this.mStats.noteBluetoothOnLocked();
                    BatteryStatsService.this.mStats.setBtHeadset(BatteryStatsService.this.mBluetoothHeadset);
                    BatteryStatsService.this.mBluetoothPendingStats = false;
                }
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int profile) {
            BatteryStatsService.this.mBluetoothHeadset = null;
        }
    };

    BatteryStatsService(String filename) {
        this.mStats = new BatteryStatsImpl(filename);
    }

    public void publish(Context context) {
        this.mContext = context;
        ServiceManager.addService(BatteryStats.SERVICE_NAME, asBinder());
        this.mStats.setNumSpeedSteps(new PowerProfile(this.mContext).getNumSpeedSteps());
        this.mStats.setRadioScanningTimeout(this.mContext.getResources().getInteger(R.integer.config_radioScanningTimeout) * 1000);
    }

    public void shutdown() {
        Slog.w("BatteryStats", "Writing battery stats before shutdown...");
        synchronized (this.mStats) {
            this.mStats.shutdownLocked();
        }
    }

    public static IBatteryStats getService() {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService(BatteryStats.SERVICE_NAME);
        sService = asInterface(b);
        return sService;
    }

    public BatteryStatsImpl getActiveStatistics() {
        return this.mStats;
    }

    @Override // com.android.internal.app.IBatteryStats
    public byte[] getStatistics() {
        this.mContext.enforceCallingPermission(Manifest.permission.BATTERY_STATS, null);
        Parcel out = Parcel.obtain();
        this.mStats.writeToParcel(out, 0);
        byte[] data = out.marshall();
        out.recycle();
        return data;
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStartWakelock(int uid, int pid, String name, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartWakeLocked(uid, pid, name, type);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStopWakelock(int uid, int pid, String name, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopWakeLocked(uid, pid, name, type);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStartWakelockFromSource(WorkSource ws, int pid, String name, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartWakeFromSourceLocked(ws, pid, name, type);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStopWakelockFromSource(WorkSource ws, int pid, String name, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopWakeFromSourceLocked(ws, pid, name, type);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStartSensor(int uid, int sensor) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartSensorLocked(uid, sensor);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStopSensor(int uid, int sensor) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopSensorLocked(uid, sensor);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteVibratorOn(int uid, long durationMillis) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteVibratorOnLocked(uid, durationMillis);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteVibratorOff(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteVibratorOffLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStartGps(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartGpsLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteStopGps(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopGpsLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteScreenOn() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteScreenOnLocked();
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteScreenBrightness(int brightness) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteScreenBrightnessLocked(brightness);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteScreenOff() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteScreenOffLocked();
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteInputEvent() {
        enforceCallingPermission();
        this.mStats.noteInputEventAtomic();
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteUserActivity(int uid, int event) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteUserActivityLocked(uid, event);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void notePhoneOn() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneOnLocked();
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void notePhoneOff() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneOffLocked();
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void notePhoneSignalStrength(SignalStrength signalStrength) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneSignalStrengthLocked(signalStrength);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void notePhoneDataConnectionState(int dataType, boolean hasData) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneDataConnectionStateLocked(dataType, hasData);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void notePhoneState(int state) {
        enforceCallingPermission();
        int simState = TelephonyManager.getDefault().getSimState();
        synchronized (this.mStats) {
            this.mStats.notePhoneStateLocked(state, simState);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiOn() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiOnLocked();
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiOff() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiOffLocked();
        }
    }

    public void noteStartAudio(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteAudioOnLocked(uid);
        }
    }

    public void noteStopAudio(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteAudioOffLocked(uid);
        }
    }

    public void noteStartVideo(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteVideoOnLocked(uid);
        }
    }

    public void noteStopVideo(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteVideoOffLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiRunning(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiRunningLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiRunningChanged(WorkSource oldWs, WorkSource newWs) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiRunningChangedLocked(oldWs, newWs);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiStopped(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiStoppedLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteBluetoothOn() {
        enforceCallingPermission();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, 1);
        }
        synchronized (this.mStats) {
            if (this.mBluetoothHeadset != null) {
                this.mStats.noteBluetoothOnLocked();
                this.mStats.setBtHeadset(this.mBluetoothHeadset);
            } else {
                this.mBluetoothPendingStats = true;
            }
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteBluetoothOff() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mBluetoothPendingStats = false;
            this.mStats.noteBluetoothOffLocked();
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteFullWifiLockAcquired(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockAcquiredLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteFullWifiLockReleased(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockReleasedLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiScanStarted(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStartedLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiScanStopped(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStoppedLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiMulticastEnabled(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiMulticastEnabledLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiMulticastDisabled(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiMulticastDisabledLocked(uid);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteFullWifiLockAcquiredFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockAcquiredFromSourceLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteFullWifiLockReleasedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockReleasedFromSourceLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiScanStartedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStartedFromSourceLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiScanStoppedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStoppedFromSourceLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiMulticastEnabledFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiMulticastEnabledFromSourceLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteWifiMulticastDisabledFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiMulticastDisabledFromSourceLocked(ws);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteNetworkInterfaceType(String iface, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteNetworkInterfaceTypeLocked(iface, type);
        }
    }

    @Override // com.android.internal.app.IBatteryStats
    public void noteNetworkStatsEnabled() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteNetworkStatsEnabledLocked();
        }
    }

    public boolean isOnBattery() {
        return this.mStats.isOnBattery();
    }

    @Override // com.android.internal.app.IBatteryStats
    public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt) {
        enforceCallingPermission();
        this.mStats.setBatteryState(status, health, plugType, level, temp, volt);
    }

    @Override // com.android.internal.app.IBatteryStats
    public long getAwakeTimeBattery() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BATTERY_STATS, null);
        return this.mStats.getAwakeTimeBattery();
    }

    @Override // com.android.internal.app.IBatteryStats
    public long getAwakeTimePlugged() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BATTERY_STATS, null);
        return this.mStats.getAwakeTimePlugged();
    }

    public void enforceCallingPermission() {
        if (Binder.getCallingPid() == Process.myPid()) {
            return;
        }
        this.mContext.enforcePermission(Manifest.permission.UPDATE_DEVICE_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
    }

    private void dumpHelp(PrintWriter pw) {
        pw.println("Battery stats (batterystats) dump options:");
        pw.println("  [--checkin] [-c] [--unplugged] [--reset] [--write] [-h] [<package.name>]");
        pw.println("  --checkin: format output for a checkin report.");
        pw.println("  --unplugged: only output data since last unplugged.");
        pw.println("  --reset: reset the stats, clearing all current data.");
        pw.println("  --write: force write current collected stats to disk.");
        pw.println("  -h: print this help text.");
        pw.println("  <package.name>: optional name of package to filter output by.");
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump BatteryStats from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
            return;
        }
        boolean isCheckin = false;
        boolean includeHistory = false;
        boolean isUnpluggedOnly = false;
        boolean noOutput = false;
        int reqUid = -1;
        if (args != null) {
            for (String arg : args) {
                if ("--checkin".equals(arg)) {
                    isCheckin = true;
                } else if ("-c".equals(arg)) {
                    isCheckin = true;
                    includeHistory = true;
                } else if ("--unplugged".equals(arg)) {
                    isUnpluggedOnly = true;
                } else if ("--reset".equals(arg)) {
                    synchronized (this.mStats) {
                        this.mStats.resetAllStatsLocked();
                        pw.println("Battery stats reset.");
                        noOutput = true;
                    }
                } else if ("--write".equals(arg)) {
                    synchronized (this.mStats) {
                        this.mStats.writeSyncLocked();
                        pw.println("Battery stats written.");
                        noOutput = true;
                    }
                } else if ("-h".equals(arg)) {
                    dumpHelp(pw);
                    return;
                } else if ("-a".equals(arg)) {
                    continue;
                } else if (arg.length() > 0 && arg.charAt(0) == '-') {
                    pw.println("Unknown option: " + arg);
                    dumpHelp(pw);
                    return;
                } else {
                    try {
                        reqUid = this.mContext.getPackageManager().getPackageUid(arg, UserHandle.getCallingUserId());
                    } catch (PackageManager.NameNotFoundException e) {
                        pw.println("Unknown package: " + arg);
                        dumpHelp(pw);
                        return;
                    }
                }
            }
        }
        if (noOutput) {
            return;
        }
        if (isCheckin) {
            List<ApplicationInfo> apps = this.mContext.getPackageManager().getInstalledApplications(0);
            synchronized (this.mStats) {
                this.mStats.dumpCheckinLocked(pw, apps, isUnpluggedOnly, includeHistory);
            }
            return;
        }
        synchronized (this.mStats) {
            this.mStats.dumpLocked(pw, isUnpluggedOnly, reqUid);
        }
    }
}