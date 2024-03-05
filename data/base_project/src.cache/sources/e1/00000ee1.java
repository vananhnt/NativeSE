package android.support.v4.accessibilityservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import com.android.internal.telephony.IccCardConstants;

/* loaded from: AccessibilityServiceInfoCompat.class */
public class AccessibilityServiceInfoCompat {
    public static final int CAPABILITY_CAN_FILTER_KEY_EVENTS = 8;
    public static final int CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 4;
    public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2;
    public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1;
    public static final int DEFAULT = 1;
    public static final int FEEDBACK_ALL_MASK = -1;
    public static final int FEEDBACK_BRAILLE = 32;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
    public static final int FLAG_REPORT_VIEW_IDS = 16;
    public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 8;
    public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32;
    public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4;
    private static final AccessibilityServiceInfoVersionImpl IMPL;

    /* loaded from: AccessibilityServiceInfoCompat$AccessibilityServiceInfoIcsImpl.class */
    static class AccessibilityServiceInfoIcsImpl extends AccessibilityServiceInfoStubImpl {
        AccessibilityServiceInfoIcsImpl() {
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public boolean getCanRetrieveWindowContent(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceInfoCompatIcs.getCanRetrieveWindowContent(accessibilityServiceInfo);
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public int getCapabilities(AccessibilityServiceInfo accessibilityServiceInfo) {
            return getCanRetrieveWindowContent(accessibilityServiceInfo) ? 1 : 0;
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public String getDescription(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceInfoCompatIcs.getDescription(accessibilityServiceInfo);
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public String getId(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceInfoCompatIcs.getId(accessibilityServiceInfo);
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public ResolveInfo getResolveInfo(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceInfoCompatIcs.getResolveInfo(accessibilityServiceInfo);
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public String getSettingsActivityName(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceInfoCompatIcs.getSettingsActivityName(accessibilityServiceInfo);
        }
    }

    /* loaded from: AccessibilityServiceInfoCompat$AccessibilityServiceInfoJellyBeanMr2.class */
    static class AccessibilityServiceInfoJellyBeanMr2 extends AccessibilityServiceInfoIcsImpl {
        AccessibilityServiceInfoJellyBeanMr2() {
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoIcsImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoStubImpl, android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public int getCapabilities(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceInfoCompatJellyBeanMr2.getCapabilities(accessibilityServiceInfo);
        }
    }

    /* loaded from: AccessibilityServiceInfoCompat$AccessibilityServiceInfoStubImpl.class */
    static class AccessibilityServiceInfoStubImpl implements AccessibilityServiceInfoVersionImpl {
        AccessibilityServiceInfoStubImpl() {
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public boolean getCanRetrieveWindowContent(AccessibilityServiceInfo accessibilityServiceInfo) {
            return false;
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public int getCapabilities(AccessibilityServiceInfo accessibilityServiceInfo) {
            return 0;
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public String getDescription(AccessibilityServiceInfo accessibilityServiceInfo) {
            return null;
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public String getId(AccessibilityServiceInfo accessibilityServiceInfo) {
            return null;
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public ResolveInfo getResolveInfo(AccessibilityServiceInfo accessibilityServiceInfo) {
            return null;
        }

        @Override // android.support.v4.accessibilityservice.AccessibilityServiceInfoCompat.AccessibilityServiceInfoVersionImpl
        public String getSettingsActivityName(AccessibilityServiceInfo accessibilityServiceInfo) {
            return null;
        }
    }

    /* loaded from: AccessibilityServiceInfoCompat$AccessibilityServiceInfoVersionImpl.class */
    interface AccessibilityServiceInfoVersionImpl {
        boolean getCanRetrieveWindowContent(AccessibilityServiceInfo accessibilityServiceInfo);

        int getCapabilities(AccessibilityServiceInfo accessibilityServiceInfo);

        String getDescription(AccessibilityServiceInfo accessibilityServiceInfo);

        String getId(AccessibilityServiceInfo accessibilityServiceInfo);

        ResolveInfo getResolveInfo(AccessibilityServiceInfo accessibilityServiceInfo);

        String getSettingsActivityName(AccessibilityServiceInfo accessibilityServiceInfo);
    }

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            IMPL = new AccessibilityServiceInfoJellyBeanMr2();
        } else if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new AccessibilityServiceInfoIcsImpl();
        } else {
            IMPL = new AccessibilityServiceInfoStubImpl();
        }
    }

    private AccessibilityServiceInfoCompat() {
    }

    public static String capabilityToString(int i) {
        if (i != 4) {
            if (i != 8) {
                switch (i) {
                    case 1:
                        return "CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT";
                    case 2:
                        return "CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION";
                    default:
                        return IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
                }
            }
            return "CAPABILITY_CAN_FILTER_KEY_EVENTS";
        }
        return "CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
    }

    public static String feedbackTypeToString(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while (i > 0) {
            int numberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            i &= numberOfTrailingZeros ^ (-1);
            if (sb.length() > 1) {
                sb.append(", ");
            }
            if (numberOfTrailingZeros == 4) {
                sb.append("FEEDBACK_AUDIBLE");
            } else if (numberOfTrailingZeros == 8) {
                sb.append("FEEDBACK_VISUAL");
            } else if (numberOfTrailingZeros != 16) {
                switch (numberOfTrailingZeros) {
                    case 1:
                        sb.append("FEEDBACK_SPOKEN");
                        continue;
                    case 2:
                        sb.append("FEEDBACK_HAPTIC");
                        continue;
                }
            } else {
                sb.append("FEEDBACK_GENERIC");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String flagToString(int i) {
        if (i != 4) {
            if (i != 8) {
                if (i != 16) {
                    if (i != 32) {
                        switch (i) {
                            case 1:
                                return "DEFAULT";
                            case 2:
                                return "FLAG_INCLUDE_NOT_IMPORTANT_VIEWS";
                            default:
                                return null;
                        }
                    }
                    return "FLAG_REQUEST_FILTER_KEY_EVENTS";
                }
                return "FLAG_REPORT_VIEW_IDS";
            }
            return "FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
        }
        return "FLAG_REQUEST_TOUCH_EXPLORATION_MODE";
    }

    public static boolean getCanRetrieveWindowContent(AccessibilityServiceInfo accessibilityServiceInfo) {
        return IMPL.getCanRetrieveWindowContent(accessibilityServiceInfo);
    }

    public static int getCapabilities(AccessibilityServiceInfo accessibilityServiceInfo) {
        return IMPL.getCapabilities(accessibilityServiceInfo);
    }

    public static String getDescription(AccessibilityServiceInfo accessibilityServiceInfo) {
        return IMPL.getDescription(accessibilityServiceInfo);
    }

    public static String getId(AccessibilityServiceInfo accessibilityServiceInfo) {
        return IMPL.getId(accessibilityServiceInfo);
    }

    public static ResolveInfo getResolveInfo(AccessibilityServiceInfo accessibilityServiceInfo) {
        return IMPL.getResolveInfo(accessibilityServiceInfo);
    }

    public static String getSettingsActivityName(AccessibilityServiceInfo accessibilityServiceInfo) {
        return IMPL.getSettingsActivityName(accessibilityServiceInfo);
    }
}