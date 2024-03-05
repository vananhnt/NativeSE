package com.android.server.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WifiTrafficPoller.class */
public final class WifiTrafficPoller {
    private static final int POLL_TRAFFIC_STATS_INTERVAL_MSECS = 1000;
    private static final int ENABLE_TRAFFIC_STATS_POLL = 1;
    private static final int TRAFFIC_STATS_POLL = 2;
    private static final int ADD_CLIENT = 3;
    private static final int REMOVE_CLIENT = 4;
    private long mTxPkts;
    private long mRxPkts;
    private int mDataActivity;
    private NetworkInfo mNetworkInfo;
    private final String mInterface;
    private boolean mEnableTrafficStatsPoll = false;
    private int mTrafficStatsPollToken = 0;
    private final List<Messenger> mClients = new ArrayList();
    private AtomicBoolean mScreenOn = new AtomicBoolean(true);
    private final TrafficHandler mTrafficHandler = new TrafficHandler();

    static /* synthetic */ int access$508(WifiTrafficPoller x0) {
        int i = x0.mTrafficStatsPollToken;
        x0.mTrafficStatsPollToken = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiTrafficPoller(Context context, String iface) {
        this.mInterface = iface;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.wifi.WifiTrafficPoller.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    WifiTrafficPoller.this.mNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    WifiTrafficPoller.this.mScreenOn.set(false);
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    WifiTrafficPoller.this.mScreenOn.set(true);
                }
                WifiTrafficPoller.this.evaluateTrafficStatsPolling();
            }
        }, filter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addClient(Messenger client) {
        Message.obtain(this.mTrafficHandler, 3, client).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeClient(Messenger client) {
        Message.obtain(this.mTrafficHandler, 4, client).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiTrafficPoller$TrafficHandler.class */
    public class TrafficHandler extends Handler {
        private TrafficHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WifiTrafficPoller.this.mEnableTrafficStatsPoll = msg.arg1 == 1;
                    WifiTrafficPoller.access$508(WifiTrafficPoller.this);
                    if (WifiTrafficPoller.this.mEnableTrafficStatsPoll) {
                        WifiTrafficPoller.this.notifyOnDataActivity();
                        sendMessageDelayed(Message.obtain(this, 2, WifiTrafficPoller.this.mTrafficStatsPollToken, 0), 1000L);
                        return;
                    }
                    return;
                case 2:
                    if (msg.arg1 == WifiTrafficPoller.this.mTrafficStatsPollToken) {
                        WifiTrafficPoller.this.notifyOnDataActivity();
                        sendMessageDelayed(Message.obtain(this, 2, WifiTrafficPoller.this.mTrafficStatsPollToken, 0), 1000L);
                        return;
                    }
                    return;
                case 3:
                    WifiTrafficPoller.this.mClients.add((Messenger) msg.obj);
                    return;
                case 4:
                    WifiTrafficPoller.this.mClients.remove(msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void evaluateTrafficStatsPolling() {
        Message msg;
        if (this.mNetworkInfo == null) {
            return;
        }
        if (this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED && this.mScreenOn.get()) {
            msg = Message.obtain(this.mTrafficHandler, 1, 1, 0);
        } else {
            msg = Message.obtain(this.mTrafficHandler, 1, 0, 0);
        }
        msg.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOnDataActivity() {
        long preTxPkts = this.mTxPkts;
        long preRxPkts = this.mRxPkts;
        int dataActivity = 0;
        this.mTxPkts = TrafficStats.getTxPackets(this.mInterface);
        this.mRxPkts = TrafficStats.getRxPackets(this.mInterface);
        if (preTxPkts > 0 || preRxPkts > 0) {
            long sent = this.mTxPkts - preTxPkts;
            long received = this.mRxPkts - preRxPkts;
            if (sent > 0) {
                dataActivity = 0 | 2;
            }
            if (received > 0) {
                dataActivity |= 1;
            }
            if (dataActivity != this.mDataActivity && this.mScreenOn.get()) {
                this.mDataActivity = dataActivity;
                for (Messenger client : this.mClients) {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.arg1 = this.mDataActivity;
                    try {
                        client.send(msg);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("mEnableTrafficStatsPoll " + this.mEnableTrafficStatsPoll);
        pw.println("mTrafficStatsPollToken " + this.mTrafficStatsPollToken);
        pw.println("mTxPkts " + this.mTxPkts);
        pw.println("mRxPkts " + this.mRxPkts);
        pw.println("mDataActivity " + this.mDataActivity);
    }
}