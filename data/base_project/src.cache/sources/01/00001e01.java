package com.android.server.connectivity;

import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.NetworkStats;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.BaseNetworkStateTracker;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.LinkProperties;
import android.net.LocalSocket;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.server.ConnectivityService;
import com.android.server.net.BaseNetworkObserver;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoUtils;

/* loaded from: Vpn.class */
public class Vpn extends BaseNetworkStateTracker {
    private static final String TAG = "Vpn";
    private static final boolean LOGD = true;
    private final ConnectivityService.VpnCallback mCallback;
    private String mPackage;
    private String mInterface;
    private Connection mConnection;
    private LegacyVpnRunner mLegacyVpnRunner;
    private PendingIntent mStatusIntent;
    private volatile boolean mEnableNotif;
    private volatile boolean mEnableTeardown;
    private final IConnectivityManager mConnService;
    private VpnConfig mConfig;
    @GuardedBy("this")
    private SparseBooleanArray mVpnUsers;
    private BroadcastReceiver mUserIntentReceiver;
    private final int mUserId;
    private INetworkManagementEventObserver mObserver;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.prepare(java.lang.String, java.lang.String):boolean, file: Vpn.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public synchronized boolean prepare(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.prepare(java.lang.String, java.lang.String):boolean, file: Vpn.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.prepare(java.lang.String, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.protect(android.os.ParcelFileDescriptor, java.lang.String):void, file: Vpn.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void protect(android.os.ParcelFileDescriptor r1, java.lang.String r2) throws java.lang.Exception {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.protect(android.os.ParcelFileDescriptor, java.lang.String):void, file: Vpn.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.protect(android.os.ParcelFileDescriptor, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.establish(com.android.internal.net.VpnConfig):android.os.ParcelFileDescriptor, file: Vpn.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public synchronized android.os.ParcelFileDescriptor establish(com.android.internal.net.VpnConfig r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.establish(com.android.internal.net.VpnConfig):android.os.ParcelFileDescriptor, file: Vpn.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.establish(com.android.internal.net.VpnConfig):android.os.ParcelFileDescriptor");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.enforceControlPermission():void, file: Vpn.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void enforceControlPermission() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.enforceControlPermission():void, file: Vpn.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.enforceControlPermission():void");
    }

    private native int jniCreate(int i);

    private native String jniGetName(int i);

    private native int jniSetAddresses(String str, String str2);

    private native int jniSetRoutes(String str, String str2);

    private native void jniReset(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native int jniCheck(String str);

    private native void jniProtect(int i, String str);

    static /* synthetic */ String access$500(Vpn x0) {
        return x0.mInterface;
    }

    static /* synthetic */ int access$600(Vpn x0, String x1) {
        return x0.jniCheck(x1);
    }

    static /* synthetic */ SparseBooleanArray access$700(Vpn x0) {
        return x0.mVpnUsers;
    }

    static /* synthetic */ VpnConfig access$800(Vpn x0) {
        return x0.mConfig;
    }

    static /* synthetic */ ConnectivityService.VpnCallback access$900(Vpn x0) {
        return x0.mCallback;
    }

    static /* synthetic */ void access$1000(Vpn x0, int x1) {
        x0.hideNotification(x1);
    }

    static /* synthetic */ SparseBooleanArray access$702(Vpn x0, SparseBooleanArray x1) {
        x0.mVpnUsers = x1;
        return x1;
    }

    static /* synthetic */ void access$1300(Vpn x0, NetworkInfo.DetailedState x1, String x2) {
        x0.updateState(x1, x2);
    }

    static /* synthetic */ NetworkInfo access$2500(Vpn x0) {
        return x0.mNetworkInfo;
    }

    public Vpn(Context context, ConnectivityService.VpnCallback callback, INetworkManagementService netService, IConnectivityManager connService, int userId) {
        super(8);
        this.mPackage = VpnConfig.LEGACY_VPN;
        this.mEnableNotif = true;
        this.mEnableTeardown = true;
        this.mVpnUsers = null;
        this.mUserIntentReceiver = null;
        this.mObserver = new BaseNetworkObserver() { // from class: com.android.server.connectivity.Vpn.2
            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.2.interfaceRemoved(java.lang.String):void, file: Vpn$2.class
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                	at jadx.core.ProcessClass.process(ProcessClass.java:67)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
                Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                	... 6 more
                */
            @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
            public void interfaceRemoved(java.lang.String r1) {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.2.interfaceRemoved(java.lang.String):void, file: Vpn$2.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.AnonymousClass2.interfaceRemoved(java.lang.String):void");
            }

            @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
            public void interfaceStatusChanged(String interfaze, boolean up) {
                synchronized (Vpn.this) {
                    if (!up) {
                        if (Vpn.this.mLegacyVpnRunner != null) {
                            Vpn.this.mLegacyVpnRunner.check(interfaze);
                        }
                    }
                }
            }
        };
        this.mContext = context;
        this.mCallback = callback;
        this.mConnService = connService;
        this.mUserId = userId;
        try {
            netService.registerObserver(this.mObserver);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Problem registering observer", e);
        }
        if (userId == 0) {
            this.mUserIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.connectivity.Vpn.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    String action = intent.getAction();
                    int userId2 = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
                    if (userId2 == -10000) {
                        return;
                    }
                    if (Intent.ACTION_USER_ADDED.equals(action)) {
                        Vpn.this.onUserAdded(userId2);
                    } else if (Intent.ACTION_USER_REMOVED.equals(action)) {
                        Vpn.this.onUserRemoved(userId2);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_ADDED);
            intentFilter.addAction(Intent.ACTION_USER_REMOVED);
            this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.ALL, intentFilter, null, null);
        }
    }

    public void setEnableNotifications(boolean enableNotif) {
        this.mEnableNotif = enableNotif;
    }

    public void setEnableTeardown(boolean enableTeardown) {
        this.mEnableTeardown = enableTeardown;
    }

    @Override // android.net.BaseNetworkStateTracker
    protected void startMonitoringInternal() {
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        throw new UnsupportedOperationException();
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        throw new UnsupportedOperationException();
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        return BaseNetworkStateTracker.PROP_TCP_BUFFER_UNKNOWN;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState(NetworkInfo.DetailedState detailedState, String reason) {
        Log.d(TAG, "setting state=" + detailedState + ", reason=" + reason);
        this.mNetworkInfo.setDetailedState(detailedState, reason, null);
        this.mCallback.onStateChanged(new NetworkInfo(this.mNetworkInfo));
    }

    private boolean isRunningLocked() {
        return this.mVpnUsers != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addVpnUserLocked(int user) {
        enforceControlPermission();
        if (!isRunningLocked()) {
            throw new IllegalStateException("VPN is not active");
        }
        boolean forwardDns = (this.mConfig.dnsServers == null || this.mConfig.dnsServers.size() == 0) ? false : true;
        this.mCallback.addUserForwarding(this.mInterface, user, forwardDns);
        this.mVpnUsers.put(user, true);
        if (!this.mPackage.equals(VpnConfig.LEGACY_VPN)) {
            PackageManager pm = this.mContext.getPackageManager();
            try {
                ApplicationInfo app = AppGlobals.getPackageManager().getApplicationInfo(this.mPackage, 0, this.mUserId);
                String label = app.loadLabel(pm).toString();
                Drawable icon = app.loadIcon(pm);
                Bitmap bitmap = null;
                if (icon.getIntrinsicWidth() > 0 && icon.getIntrinsicHeight() > 0) {
                    int width = this.mContext.getResources().getDimensionPixelSize(17104901);
                    int height = this.mContext.getResources().getDimensionPixelSize(17104902);
                    icon.setBounds(0, 0, width, height);
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(bitmap);
                    icon.draw(c);
                    c.setBitmap(null);
                }
                showNotification(label, bitmap, user);
                return;
            } catch (RemoteException e) {
                throw new IllegalStateException("Invalid application");
            }
        }
        showNotification(null, null, user);
    }

    private void removeVpnUserLocked(int user) {
        enforceControlPermission();
        if (!isRunningLocked()) {
            throw new IllegalStateException("VPN is not active");
        }
        boolean forwardDns = (this.mConfig.dnsServers == null || this.mConfig.dnsServers.size() == 0) ? false : true;
        this.mCallback.clearUserForwarding(this.mInterface, user, forwardDns);
        this.mVpnUsers.delete(user);
        hideNotification(user);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserAdded(int userId) {
        synchronized (this) {
            UserManager mgr = UserManager.get(this.mContext);
            UserInfo user = mgr.getUserInfo(userId);
            if (user.isRestricted()) {
                try {
                    addVpnUserLocked(userId);
                } catch (Exception e) {
                    Log.wtf(TAG, "Failed to add restricted user to owner", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserRemoved(int userId) {
        synchronized (this) {
            UserManager mgr = UserManager.get(this.mContext);
            UserInfo user = mgr.getUserInfo(userId);
            if (user.isRestricted()) {
                try {
                    removeVpnUserLocked(userId);
                } catch (Exception e) {
                    Log.wtf(TAG, "Failed to remove restricted user to owner", e);
                }
            }
        }
    }

    public VpnConfig getVpnConfig() {
        enforceControlPermission();
        return this.mConfig;
    }

    @Deprecated
    public synchronized void interfaceStatusChanged(String iface, boolean up) {
        try {
            this.mObserver.interfaceStatusChanged(iface, up);
        } catch (RemoteException e) {
        }
    }

    /* loaded from: Vpn$Connection.class */
    private class Connection implements ServiceConnection {
        private IBinder mService;

        private Connection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mService = service;
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            this.mService = null;
        }
    }

    private void showNotification(String label, Bitmap icon, int user) {
        if (this.mEnableNotif) {
            this.mStatusIntent = VpnConfig.getIntentForStatusPanel(this.mContext);
            NotificationManager nm = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                String title = label == null ? this.mContext.getString(R.string.vpn_title) : this.mContext.getString(R.string.vpn_title_long, label);
                String text = this.mConfig.session == null ? this.mContext.getString(R.string.vpn_text) : this.mContext.getString(R.string.vpn_text_long, this.mConfig.session);
                Notification notification = new Notification.Builder(this.mContext).setSmallIcon(R.drawable.vpn_connected).setLargeIcon(icon).setContentTitle(title).setContentText(text).setContentIntent(this.mStatusIntent).setDefaults(0).setOngoing(true).build();
                nm.notifyAsUser(null, R.drawable.vpn_connected, notification, new UserHandle(user));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideNotification(int user) {
        if (this.mEnableNotif) {
            this.mStatusIntent = null;
            NotificationManager nm = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancelAsUser(null, R.drawable.vpn_connected, new UserHandle(user));
            }
        }
    }

    private static RouteInfo findIPv4DefaultRoute(LinkProperties prop) {
        for (RouteInfo route : prop.getAllRoutes()) {
            if (route.isDefaultRoute() && (route.getGateway() instanceof Inet4Address)) {
                return route;
            }
        }
        throw new IllegalStateException("Unable to find IPv4 default gateway");
    }

    public void startLegacyVpn(VpnProfile profile, KeyStore keyStore, LinkProperties egress) {
        enforceControlPermission();
        if (!keyStore.isUnlocked()) {
            throw new IllegalStateException("KeyStore isn't unlocked");
        }
        RouteInfo ipv4DefaultRoute = findIPv4DefaultRoute(egress);
        String gateway = ipv4DefaultRoute.getGateway().getHostAddress();
        String iface = ipv4DefaultRoute.getInterface();
        String privateKey = "";
        String userCert = "";
        String caCert = "";
        String serverCert = "";
        if (!profile.ipsecUserCert.isEmpty()) {
            privateKey = Credentials.USER_PRIVATE_KEY + profile.ipsecUserCert;
            byte[] value = keyStore.get(Credentials.USER_CERTIFICATE + profile.ipsecUserCert);
            userCert = value == null ? null : new String(value, StandardCharsets.UTF_8);
        }
        if (!profile.ipsecCaCert.isEmpty()) {
            byte[] value2 = keyStore.get(Credentials.CA_CERTIFICATE + profile.ipsecCaCert);
            caCert = value2 == null ? null : new String(value2, StandardCharsets.UTF_8);
        }
        if (!profile.ipsecServerCert.isEmpty()) {
            byte[] value3 = keyStore.get(Credentials.USER_CERTIFICATE + profile.ipsecServerCert);
            serverCert = value3 == null ? null : new String(value3, StandardCharsets.UTF_8);
        }
        if (privateKey == null || userCert == null || caCert == null || serverCert == null) {
            throw new IllegalStateException("Cannot load credentials");
        }
        String[] racoon = null;
        switch (profile.type) {
            case 1:
                racoon = new String[]{iface, profile.server, "udppsk", profile.ipsecIdentifier, profile.ipsecSecret, "1701"};
                break;
            case 2:
                racoon = new String[]{iface, profile.server, "udprsa", privateKey, userCert, caCert, serverCert, "1701"};
                break;
            case 3:
                racoon = new String[]{iface, profile.server, "xauthpsk", profile.ipsecIdentifier, profile.ipsecSecret, profile.username, profile.password, "", gateway};
                break;
            case 4:
                racoon = new String[]{iface, profile.server, "xauthrsa", privateKey, userCert, caCert, serverCert, profile.username, profile.password, "", gateway};
                break;
            case 5:
                racoon = new String[]{iface, profile.server, "hybridrsa", caCert, serverCert, profile.username, profile.password, "", gateway};
                break;
        }
        String[] mtpd = null;
        switch (profile.type) {
            case 0:
                String[] strArr = new String[20];
                strArr[0] = iface;
                strArr[1] = "pptp";
                strArr[2] = profile.server;
                strArr[3] = "1723";
                strArr[4] = "name";
                strArr[5] = profile.username;
                strArr[6] = "password";
                strArr[7] = profile.password;
                strArr[8] = "linkname";
                strArr[9] = "vpn";
                strArr[10] = "refuse-eap";
                strArr[11] = "nodefaultroute";
                strArr[12] = "usepeerdns";
                strArr[13] = "idle";
                strArr[14] = "1800";
                strArr[15] = "mtu";
                strArr[16] = "1400";
                strArr[17] = "mru";
                strArr[18] = "1400";
                strArr[19] = profile.mppe ? "+mppe" : "nomppe";
                mtpd = strArr;
                break;
            case 1:
            case 2:
                mtpd = new String[]{iface, "l2tp", profile.server, "1701", profile.l2tpSecret, "name", profile.username, "password", profile.password, "linkname", "vpn", "refuse-eap", "nodefaultroute", "usepeerdns", "idle", "1800", "mtu", "1400", "mru", "1400"};
                break;
        }
        VpnConfig config = new VpnConfig();
        config.legacy = true;
        config.user = profile.key;
        config.interfaze = iface;
        config.session = profile.name;
        config.addLegacyRoutes(profile.routes);
        if (!profile.dnsServers.isEmpty()) {
            config.dnsServers = Arrays.asList(profile.dnsServers.split(" +"));
        }
        if (!profile.searchDomains.isEmpty()) {
            config.searchDomains = Arrays.asList(profile.searchDomains.split(" +"));
        }
        startLegacyVpn(config, racoon, mtpd);
    }

    private synchronized void startLegacyVpn(VpnConfig config, String[] racoon, String[] mtpd) {
        stopLegacyVpn();
        prepare(null, VpnConfig.LEGACY_VPN);
        updateState(NetworkInfo.DetailedState.CONNECTING, "startLegacyVpn");
        this.mLegacyVpnRunner = new LegacyVpnRunner(config, racoon, mtpd);
        this.mLegacyVpnRunner.start();
    }

    public synchronized void stopLegacyVpn() {
        if (this.mLegacyVpnRunner != null) {
            this.mLegacyVpnRunner.exit();
            this.mLegacyVpnRunner = null;
            synchronized ("LegacyVpnRunner") {
            }
        }
    }

    public synchronized LegacyVpnInfo getLegacyVpnInfo() {
        enforceControlPermission();
        if (this.mLegacyVpnRunner == null) {
            return null;
        }
        LegacyVpnInfo info = new LegacyVpnInfo();
        info.key = this.mConfig.user;
        info.state = LegacyVpnInfo.stateFromNetworkInfo(this.mNetworkInfo);
        if (this.mNetworkInfo.isConnected()) {
            info.intent = this.mStatusIntent;
        }
        return info;
    }

    public VpnConfig getLegacyVpnConfig() {
        if (this.mLegacyVpnRunner != null) {
            return this.mConfig;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Vpn$LegacyVpnRunner.class */
    public class LegacyVpnRunner extends Thread {
        private static final String TAG = "LegacyVpnRunner";
        private final String[] mDaemons;
        private final String[][] mArguments;
        private final LocalSocket[] mSockets;
        private final String mOuterInterface;
        private final AtomicInteger mOuterConnection;
        private long mTimer;
        private final BroadcastReceiver mBroadcastReceiver;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.LegacyVpnRunner.execute():void, file: Vpn$LegacyVpnRunner.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        private void execute() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.LegacyVpnRunner.execute():void, file: Vpn$LegacyVpnRunner.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.LegacyVpnRunner.execute():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.LegacyVpnRunner.monitorDaemons():void, file: Vpn$LegacyVpnRunner.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        private void monitorDaemons() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.Vpn.LegacyVpnRunner.monitorDaemons():void, file: Vpn$LegacyVpnRunner.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Vpn.LegacyVpnRunner.monitorDaemons():void");
        }

        /* JADX WARN: Type inference failed for: r1v9, types: [java.lang.String[], java.lang.String[][]] */
        public LegacyVpnRunner(VpnConfig config, String[] racoon, String[] mtpd) {
            super(TAG);
            this.mOuterConnection = new AtomicInteger(-1);
            this.mTimer = -1L;
            this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.connectivity.Vpn.LegacyVpnRunner.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    NetworkInfo info;
                    if (Vpn.this.mEnableTeardown && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1) == LegacyVpnRunner.this.mOuterConnection.get() && (info = (NetworkInfo) intent.getExtra("networkInfo")) != null && !info.isConnectedOrConnecting()) {
                        try {
                            Vpn.this.mObserver.interfaceStatusChanged(LegacyVpnRunner.this.mOuterInterface, false);
                        } catch (RemoteException e) {
                        }
                    }
                }
            };
            Vpn.this.mConfig = config;
            this.mDaemons = new String[]{"racoon", "mtpd"};
            this.mArguments = new String[]{racoon, mtpd};
            this.mSockets = new LocalSocket[this.mDaemons.length];
            this.mOuterInterface = Vpn.this.mConfig.interfaze;
            try {
                this.mOuterConnection.set(Vpn.this.mConnService.findConnectionTypeForIface(this.mOuterInterface));
            } catch (Exception e) {
                this.mOuterConnection.set(-1);
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            Vpn.this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        }

        public void check(String interfaze) {
            if (interfaze.equals(this.mOuterInterface)) {
                Log.i(TAG, "Legacy VPN is going down with " + interfaze);
                exit();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void exit() {
            interrupt();
            LocalSocket[] arr$ = this.mSockets;
            for (NetworkStats networkStats : arr$) {
                IoUtils.closeQuietly(networkStats);
            }
            Vpn.this.updateState(NetworkInfo.DetailedState.DISCONNECTED, "exit");
            try {
                Vpn.this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            } catch (IllegalArgumentException e) {
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.v(TAG, "Waiting");
            synchronized (TAG) {
                Log.v(TAG, "Executing");
                execute();
                monitorDaemons();
            }
        }

        private void checkpoint(boolean yield) throws InterruptedException {
            long now = SystemClock.elapsedRealtime();
            if (this.mTimer == -1) {
                this.mTimer = now;
                Thread.sleep(1L);
            } else if (now - this.mTimer > DateUtils.MINUTE_IN_MILLIS) {
                Vpn.this.updateState(NetworkInfo.DetailedState.FAILED, "checkpoint");
                throw new IllegalStateException("Time is up");
            } else {
                Thread.sleep(yield ? 200L : 1L);
            }
        }
    }
}