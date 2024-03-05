package android.view.accessibility;

/* loaded from: AccessibilityEventSource.class */
public interface AccessibilityEventSource {
    void sendAccessibilityEvent(int i);

    void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent);
}