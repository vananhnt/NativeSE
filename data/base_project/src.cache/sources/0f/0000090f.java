package android.net;

import android.content.Context;
import android.net.SamplingDataTracker;
import android.os.Handler;
import android.os.Messenger;

/* loaded from: NetworkStateTracker.class */
public interface NetworkStateTracker {
    public static final int EVENT_STATE_CHANGED = 458752;
    public static final int EVENT_CONFIGURATION_CHANGED = 458753;
    public static final int EVENT_RESTORE_DEFAULT_NETWORK = 458754;
    public static final int EVENT_NETWORK_SUBTYPE_CHANGED = 458755;
    public static final int EVENT_NETWORK_CONNECTED = 458756;
    public static final int EVENT_NETWORK_DISCONNECTED = 458757;

    void startMonitoring(Context context, Handler handler);

    NetworkInfo getNetworkInfo();

    LinkProperties getLinkProperties();

    LinkCapabilities getLinkCapabilities();

    LinkQualityInfo getLinkQualityInfo();

    String getTcpBufferSizesPropName();

    boolean teardown();

    boolean reconnect();

    void captivePortalCheckComplete();

    void captivePortalCheckCompleted(boolean z);

    boolean setRadio(boolean z);

    boolean isAvailable();

    void setUserDataEnable(boolean z);

    void setPolicyDataEnable(boolean z);

    boolean isPrivateDnsRouteSet();

    void privateDnsRouteSet(boolean z);

    boolean isDefaultRouteSet();

    void defaultRouteSet(boolean z);

    boolean isTeardownRequested();

    void setTeardownRequested(boolean z);

    void setDependencyMet(boolean z);

    void addStackedLink(LinkProperties linkProperties);

    void removeStackedLink(LinkProperties linkProperties);

    void supplyMessenger(Messenger messenger);

    String getNetworkInterfaceName();

    void startSampling(SamplingDataTracker.SamplingSnapshot samplingSnapshot);

    void stopSampling(SamplingDataTracker.SamplingSnapshot samplingSnapshot);
}