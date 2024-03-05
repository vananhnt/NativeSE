package java.net;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InetAddress.class */
public class InetAddress implements Serializable {
    /* JADX INFO: Access modifiers changed from: package-private */
    public InetAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public byte[] getAddress() {
        throw new RuntimeException("Stub!");
    }

    public static InetAddress[] getAllByName(String host) throws UnknownHostException {
        throw new RuntimeException("Stub!");
    }

    public static InetAddress getByName(String host) throws UnknownHostException {
        throw new RuntimeException("Stub!");
    }

    public String getHostAddress() {
        throw new RuntimeException("Stub!");
    }

    public String getHostName() {
        throw new RuntimeException("Stub!");
    }

    public String getCanonicalHostName() {
        throw new RuntimeException("Stub!");
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public boolean isAnyLocalAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean isLinkLocalAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean isLoopbackAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMCGlobal() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMCLinkLocal() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMCNodeLocal() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMCOrgLocal() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMCSiteLocal() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMulticastAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean isSiteLocalAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean isReachable(int timeout) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean isReachable(NetworkInterface networkInterface, int ttl, int timeout) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public static InetAddress getByAddress(byte[] ipAddress) throws UnknownHostException {
        throw new RuntimeException("Stub!");
    }

    public static InetAddress getByAddress(String hostName, byte[] ipAddress) throws UnknownHostException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.net.InetAddress$1  reason: invalid class name */
    /* loaded from: InetAddress$1.class */
    class AnonymousClass1 extends Thread {
        final /* synthetic */ InetAddress val$destinationAddress;
        final /* synthetic */ InetAddress val$sourceAddress;
        final /* synthetic */ int val$timeout;
        final /* synthetic */ AtomicBoolean val$isReachable;
        final /* synthetic */ CountDownLatch val$latch;

        AnonymousClass1(InetAddress inetAddress, InetAddress inetAddress2, int i, AtomicBoolean atomicBoolean, CountDownLatch countDownLatch) {
            this.val$destinationAddress = inetAddress;
            this.val$sourceAddress = inetAddress2;
            this.val$timeout = i;
            this.val$isReachable = atomicBoolean;
            this.val$latch = countDownLatch;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                if (InetAddress.access$000(InetAddress.this, this.val$destinationAddress, this.val$sourceAddress, this.val$timeout)) {
                    this.val$isReachable.set(true);
                    while (this.val$latch.getCount() > 0) {
                        this.val$latch.countDown();
                    }
                }
            } catch (IOException e) {
            }
            this.val$latch.countDown();
        }
    }
}