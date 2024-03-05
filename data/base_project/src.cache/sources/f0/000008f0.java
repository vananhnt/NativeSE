package android.net;

import android.util.Log;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/* loaded from: LinkSocket.class */
public class LinkSocket extends Socket {
    private static final String TAG = "LinkSocket";
    private static final boolean DBG = true;

    public LinkSocket() {
        log("LinkSocket() EX");
    }

    public LinkSocket(LinkSocketNotifier notifier) {
        log("LinkSocket(notifier) EX");
    }

    public LinkSocket(LinkSocketNotifier notifier, Proxy proxy) {
        log("LinkSocket(notifier, proxy) EX");
    }

    public LinkProperties getLinkProperties() {
        log("LinkProperties() EX");
        return new LinkProperties();
    }

    public boolean setNeededCapabilities(LinkCapabilities needs) {
        log("setNeeds() EX");
        return false;
    }

    public LinkCapabilities getNeededCapabilities() {
        log("getNeeds() EX");
        return null;
    }

    public LinkCapabilities getCapabilities() {
        log("getCapabilities() EX");
        return null;
    }

    public LinkCapabilities getCapabilities(Set<Integer> capabilities) {
        log("getCapabilities(capabilities) EX");
        return new LinkCapabilities();
    }

    public void setTrackedCapabilities(Set<Integer> capabilities) {
        log("setTrackedCapabilities(capabilities) EX");
    }

    public Set<Integer> getTrackedCapabilities() {
        log("getTrackedCapabilities(capabilities) EX");
        return new HashSet();
    }

    public void connect(String dstName, int dstPort, int timeout) throws UnknownHostException, IOException, SocketTimeoutException {
        log("connect(dstName, dstPort, timeout) EX");
    }

    public void connect(String dstName, int dstPort) throws UnknownHostException, IOException {
        log("connect(dstName, dstPort, timeout) EX");
    }

    @Override // java.net.Socket
    @Deprecated
    public void connect(SocketAddress remoteAddr, int timeout) throws IOException, SocketTimeoutException {
        log("connect(remoteAddr, timeout) EX DEPRECATED");
    }

    @Override // java.net.Socket
    @Deprecated
    public void connect(SocketAddress remoteAddr) throws IOException {
        log("connect(remoteAddr) EX DEPRECATED");
    }

    public void connect(int timeout) throws IOException {
        log("connect(timeout) EX");
    }

    public void connect() throws IOException {
        log("connect() EX");
    }

    @Override // java.net.Socket
    public synchronized void close() throws IOException {
        log("close() EX");
    }

    public void requestNewLink(LinkRequestReason linkRequestReason) {
        log("requestNewLink(linkRequestReason) EX");
    }

    @Override // java.net.Socket
    @Deprecated
    public void bind(SocketAddress localAddr) throws UnsupportedOperationException {
        log("bind(localAddr) EX throws IOException");
        throw new UnsupportedOperationException("bind is deprecated for LinkSocket");
    }

    /* loaded from: LinkSocket$LinkRequestReason.class */
    public static final class LinkRequestReason {
        public static final int LINK_PROBLEM_NONE = 0;
        public static final int LINK_PROBLEM_UNKNOWN = 1;

        private LinkRequestReason() {
        }
    }

    protected static void log(String s) {
        Log.d(TAG, s);
    }
}