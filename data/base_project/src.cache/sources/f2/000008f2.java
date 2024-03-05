package android.net;

/* loaded from: LinkSocketNotifier.class */
public interface LinkSocketNotifier {
    boolean onBetterLinkAvailable(LinkSocket linkSocket, LinkSocket linkSocket2);

    void onLinkLost(LinkSocket linkSocket);

    void onNewLinkUnavailable(LinkSocket linkSocket);

    void onCapabilitiesChanged(LinkSocket linkSocket, LinkCapabilities linkCapabilities);
}