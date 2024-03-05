package android.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: DnsPinger.class */
public final class DnsPinger extends Handler {
    private static final boolean DBG = false;
    private static final int RECEIVE_POLL_INTERVAL_MS = 200;
    private static final int DNS_PORT = 53;
    private static final int SOCKET_TIMEOUT_MS = 1;
    private ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final int mConnectionType;
    private final Handler mTarget;
    private final ArrayList<InetAddress> mDefaultDns;
    private String TAG;
    private AtomicInteger mCurrentToken;
    private static final int BASE = 327680;
    public static final int DNS_PING_RESULT = 327680;
    public static final int TIMEOUT = -1;
    public static final int SOCKET_EXCEPTION = -2;
    private static final int ACTION_PING_DNS = 327681;
    private static final int ACTION_LISTEN_FOR_RESPONSE = 327682;
    private static final int ACTION_CANCEL_ALL_PINGS = 327683;
    private List<ActivePing> mActivePings;
    private int mEventCounter;
    private static final Random sRandom = new Random();
    private static final AtomicInteger sCounter = new AtomicInteger();
    private static final byte[] mDnsQuery = {0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3, 119, 119, 119, 6, 103, 111, 111, 103, 108, 101, 3, 99, 111, 109, 0, 0, 1, 0, 1};

    /* loaded from: DnsPinger$ActivePing.class */
    private class ActivePing {
        DatagramSocket socket;
        int internalId;
        short packetId;
        int timeout;
        Integer result;
        long start;

        private ActivePing() {
            this.start = SystemClock.elapsedRealtime();
        }
    }

    /* loaded from: DnsPinger$DnsArg.class */
    private class DnsArg {
        InetAddress dns;
        int seq;

        DnsArg(InetAddress d, int s) {
            this.dns = d;
            this.seq = s;
        }
    }

    public DnsPinger(Context context, String TAG, Looper looper, Handler target, int connectionType) {
        super(looper);
        this.mConnectivityManager = null;
        this.mCurrentToken = new AtomicInteger();
        this.mActivePings = new ArrayList();
        this.TAG = TAG;
        this.mContext = context;
        this.mTarget = target;
        this.mConnectionType = connectionType;
        if (!ConnectivityManager.isNetworkTypeValid(connectionType)) {
            throw new IllegalArgumentException("Invalid connectionType in constructor: " + connectionType);
        }
        this.mDefaultDns = new ArrayList<>();
        this.mDefaultDns.add(getDefaultDns());
        this.mEventCounter = 0;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ACTION_PING_DNS /* 327681 */:
                DnsArg dnsArg = (DnsArg) msg.obj;
                if (dnsArg.seq == this.mCurrentToken.get()) {
                    try {
                        ActivePing newActivePing = new ActivePing();
                        InetAddress dnsAddress = dnsArg.dns;
                        newActivePing.internalId = msg.arg1;
                        newActivePing.timeout = msg.arg2;
                        newActivePing.socket = new DatagramSocket();
                        newActivePing.socket.setSoTimeout(1);
                        try {
                            newActivePing.socket.setNetworkInterface(NetworkInterface.getByName(getCurrentLinkProperties().getInterfaceName()));
                        } catch (Exception e) {
                            loge("sendDnsPing::Error binding to socket " + e);
                        }
                        newActivePing.packetId = (short) sRandom.nextInt();
                        byte[] buf = (byte[]) mDnsQuery.clone();
                        buf[0] = (byte) (newActivePing.packetId >> 8);
                        buf[1] = (byte) newActivePing.packetId;
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, dnsAddress, 53);
                        newActivePing.socket.send(packet);
                        this.mActivePings.add(newActivePing);
                        this.mEventCounter++;
                        sendMessageDelayed(obtainMessage(ACTION_LISTEN_FOR_RESPONSE, this.mEventCounter, 0), 200L);
                        return;
                    } catch (IOException e2) {
                        sendResponse(msg.arg1, -9999, -2);
                        return;
                    }
                }
                return;
            case ACTION_LISTEN_FOR_RESPONSE /* 327682 */:
                if (msg.arg1 == this.mEventCounter) {
                    for (ActivePing curPing : this.mActivePings) {
                        try {
                            byte[] responseBuf = new byte[2];
                            DatagramPacket replyPacket = new DatagramPacket(responseBuf, 2);
                            curPing.socket.receive(replyPacket);
                            if (responseBuf[0] == ((byte) (curPing.packetId >> 8)) && responseBuf[1] == ((byte) curPing.packetId)) {
                                curPing.result = Integer.valueOf((int) (SystemClock.elapsedRealtime() - curPing.start));
                            }
                        } catch (SocketTimeoutException e3) {
                        } catch (Exception e4) {
                            curPing.result = -2;
                        }
                    }
                    Iterator<ActivePing> iter = this.mActivePings.iterator();
                    while (iter.hasNext()) {
                        ActivePing curPing2 = iter.next();
                        if (curPing2.result != null) {
                            sendResponse(curPing2.internalId, curPing2.packetId, curPing2.result.intValue());
                            curPing2.socket.close();
                            iter.remove();
                        } else if (SystemClock.elapsedRealtime() > curPing2.start + curPing2.timeout) {
                            sendResponse(curPing2.internalId, curPing2.packetId, -1);
                            curPing2.socket.close();
                            iter.remove();
                        }
                    }
                    if (!this.mActivePings.isEmpty()) {
                        sendMessageDelayed(obtainMessage(ACTION_LISTEN_FOR_RESPONSE, this.mEventCounter, 0), 200L);
                        return;
                    }
                    return;
                }
                return;
            case ACTION_CANCEL_ALL_PINGS /* 327683 */:
                for (ActivePing activePing : this.mActivePings) {
                    activePing.socket.close();
                }
                this.mActivePings.clear();
                return;
            default:
                return;
        }
    }

    public List<InetAddress> getDnsList() {
        LinkProperties curLinkProps = getCurrentLinkProperties();
        if (curLinkProps == null) {
            loge("getCurLinkProperties:: LP for type" + this.mConnectionType + " is null!");
            return this.mDefaultDns;
        }
        Collection<InetAddress> dnses = curLinkProps.getDnses();
        if (dnses == null || dnses.size() == 0) {
            loge("getDns::LinkProps has null dns - returning default");
            return this.mDefaultDns;
        }
        return new ArrayList(dnses);
    }

    public int pingDnsAsync(InetAddress dns, int timeout, int delay) {
        int id = sCounter.incrementAndGet();
        sendMessageDelayed(obtainMessage(ACTION_PING_DNS, id, timeout, new DnsArg(dns, this.mCurrentToken.get())), delay);
        return id;
    }

    public void cancelPings() {
        this.mCurrentToken.incrementAndGet();
        obtainMessage(ACTION_CANCEL_ALL_PINGS).sendToTarget();
    }

    private void sendResponse(int internalId, int externalId, int responseVal) {
        this.mTarget.sendMessage(obtainMessage(327680, internalId, responseVal));
    }

    private LinkProperties getCurrentLinkProperties() {
        if (this.mConnectivityManager == null) {
            this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return this.mConnectivityManager.getLinkProperties(this.mConnectionType);
    }

    private InetAddress getDefaultDns() {
        String dns = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.DEFAULT_DNS_SERVER);
        if (dns == null || dns.length() == 0) {
            dns = this.mContext.getResources().getString(R.string.config_default_dns_server);
        }
        try {
            return NetworkUtils.numericToInetAddress(dns);
        } catch (IllegalArgumentException e) {
            loge("getDefaultDns::malformed default dns address");
            return null;
        }
    }

    private void log(String s) {
        Log.d(this.TAG, s);
    }

    private void loge(String s) {
        Log.e(this.TAG, s);
    }
}