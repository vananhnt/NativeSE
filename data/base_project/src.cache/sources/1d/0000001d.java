package android.accessibilityservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import com.android.internal.telephony.IccCardConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: AccessibilityServiceInfo.class */
public class AccessibilityServiceInfo implements Parcelable {
    private static final String TAG_ACCESSIBILITY_SERVICE = "accessibility-service";
    public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1;
    public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2;
    public static final int CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 4;
    public static final int CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS = 8;
    private static final SparseArray<CapabilityInfo> sAvailableCapabilityInfos = new SparseArray<>();
    public static final int FEEDBACK_SPOKEN = 1;
    public static final int FEEDBACK_HAPTIC = 2;
    public static final int FEEDBACK_AUDIBLE = 4;
    public static final int FEEDBACK_VISUAL = 8;
    public static final int FEEDBACK_GENERIC = 16;
    public static final int FEEDBACK_BRAILLE = 32;
    public static final int FEEDBACK_ALL_MASK = -1;
    public static final int DEFAULT = 1;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
    public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4;
    public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 8;
    public static final int FLAG_REPORT_VIEW_IDS = 16;
    public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32;
    public int eventTypes;
    public String[] packageNames;
    public int feedbackType;
    public long notificationTimeout;
    public int flags;
    private String mId;
    private ResolveInfo mResolveInfo;
    private String mSettingsActivityName;
    private int mCapabilities;
    private int mDescriptionResId;
    private String mNonLocalizedDescription;
    public static final Parcelable.Creator<AccessibilityServiceInfo> CREATOR;

    static {
        sAvailableCapabilityInfos.put(1, new CapabilityInfo(1, R.string.capability_title_canRetrieveWindowContent, R.string.capability_desc_canRetrieveWindowContent));
        sAvailableCapabilityInfos.put(2, new CapabilityInfo(2, R.string.capability_title_canRequestTouchExploration, R.string.capability_desc_canRequestTouchExploration));
        sAvailableCapabilityInfos.put(4, new CapabilityInfo(4, R.string.capability_title_canRequestEnhancedWebAccessibility, R.string.capability_desc_canRequestEnhancedWebAccessibility));
        sAvailableCapabilityInfos.put(8, new CapabilityInfo(8, R.string.capability_title_canRequestFilterKeyEvents, R.string.capability_desc_canRequestFilterKeyEvents));
        CREATOR = new Parcelable.Creator<AccessibilityServiceInfo>() { // from class: android.accessibilityservice.AccessibilityServiceInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AccessibilityServiceInfo createFromParcel(Parcel parcel) {
                AccessibilityServiceInfo info = new AccessibilityServiceInfo();
                info.initFromParcel(parcel);
                return info;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AccessibilityServiceInfo[] newArray(int size) {
                return new AccessibilityServiceInfo[size];
            }
        };
    }

    public AccessibilityServiceInfo() {
    }

    public AccessibilityServiceInfo(ResolveInfo resolveInfo, Context context) throws XmlPullParserException, IOException {
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        this.mId = new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString();
        this.mResolveInfo = resolveInfo;
        XmlResourceParser parser = null;
        try {
            try {
                PackageManager packageManager = context.getPackageManager();
                parser = serviceInfo.loadXmlMetaData(packageManager, AccessibilityService.SERVICE_META_DATA);
                if (parser != null) {
                    for (int type = 0; type != 1 && type != 2; type = parser.next()) {
                    }
                    String nodeName = parser.getName();
                    if (!TAG_ACCESSIBILITY_SERVICE.equals(nodeName)) {
                        throw new XmlPullParserException("Meta-data does not start withaccessibility-service tag");
                    }
                    AttributeSet allAttributes = Xml.asAttributeSet(parser);
                    Resources resources = packageManager.getResourcesForApplication(serviceInfo.applicationInfo);
                    TypedArray asAttributes = resources.obtainAttributes(allAttributes, R.styleable.AccessibilityService);
                    this.eventTypes = asAttributes.getInt(2, 0);
                    String packageNamez = asAttributes.getString(3);
                    if (packageNamez != null) {
                        this.packageNames = packageNamez.split("(\\s)*,(\\s)*");
                    }
                    this.feedbackType = asAttributes.getInt(4, 0);
                    this.notificationTimeout = asAttributes.getInt(5, 0);
                    this.flags = asAttributes.getInt(6, 0);
                    this.mSettingsActivityName = asAttributes.getString(1);
                    if (asAttributes.getBoolean(7, false)) {
                        this.mCapabilities |= 1;
                    }
                    if (asAttributes.getBoolean(8, false)) {
                        this.mCapabilities |= 2;
                    }
                    if (asAttributes.getBoolean(9, false)) {
                        this.mCapabilities |= 4;
                    }
                    if (asAttributes.getBoolean(10, false)) {
                        this.mCapabilities |= 8;
                    }
                    TypedValue peekedValue = asAttributes.peekValue(0);
                    if (peekedValue != null) {
                        this.mDescriptionResId = peekedValue.resourceId;
                        CharSequence nonLocalizedDescription = peekedValue.coerceToString();
                        if (nonLocalizedDescription != null) {
                            this.mNonLocalizedDescription = nonLocalizedDescription.toString().trim();
                        }
                    }
                    asAttributes.recycle();
                    if (parser != null) {
                        parser.close();
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to create context for: " + serviceInfo.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    public void updateDynamicallyConfigurableProperties(AccessibilityServiceInfo other) {
        this.eventTypes = other.eventTypes;
        this.packageNames = other.packageNames;
        this.feedbackType = other.feedbackType;
        this.notificationTimeout = other.notificationTimeout;
        this.flags = other.flags;
    }

    public void setComponentName(ComponentName component) {
        this.mId = component.flattenToShortString();
    }

    public String getId() {
        return this.mId;
    }

    public ResolveInfo getResolveInfo() {
        return this.mResolveInfo;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public boolean getCanRetrieveWindowContent() {
        return (this.mCapabilities & 1) != 0;
    }

    public int getCapabilities() {
        return this.mCapabilities;
    }

    public void setCapabilities(int capabilities) {
        this.mCapabilities = capabilities;
    }

    public String getDescription() {
        return this.mNonLocalizedDescription;
    }

    public String loadDescription(PackageManager packageManager) {
        if (this.mDescriptionResId == 0) {
            return this.mNonLocalizedDescription;
        }
        ServiceInfo serviceInfo = this.mResolveInfo.serviceInfo;
        CharSequence description = packageManager.getText(serviceInfo.packageName, this.mDescriptionResId, serviceInfo.applicationInfo);
        if (description != null) {
            return description.toString().trim();
        }
        return null;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flagz) {
        parcel.writeInt(this.eventTypes);
        parcel.writeStringArray(this.packageNames);
        parcel.writeInt(this.feedbackType);
        parcel.writeLong(this.notificationTimeout);
        parcel.writeInt(this.flags);
        parcel.writeString(this.mId);
        parcel.writeParcelable(this.mResolveInfo, 0);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeInt(this.mCapabilities);
        parcel.writeInt(this.mDescriptionResId);
        parcel.writeString(this.mNonLocalizedDescription);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.eventTypes = parcel.readInt();
        this.packageNames = parcel.readStringArray();
        this.feedbackType = parcel.readInt();
        this.notificationTimeout = parcel.readLong();
        this.flags = parcel.readInt();
        this.mId = parcel.readString();
        this.mResolveInfo = (ResolveInfo) parcel.readParcelable(null);
        this.mSettingsActivityName = parcel.readString();
        this.mCapabilities = parcel.readInt();
        this.mDescriptionResId = parcel.readInt();
        this.mNonLocalizedDescription = parcel.readString();
    }

    public int hashCode() {
        return 31 + (this.mId == null ? 0 : this.mId.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityServiceInfo other = (AccessibilityServiceInfo) obj;
        if (this.mId == null) {
            if (other.mId != null) {
                return false;
            }
            return true;
        } else if (!this.mId.equals(other.mId)) {
            return false;
        } else {
            return true;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        appendEventTypes(stringBuilder, this.eventTypes);
        stringBuilder.append(", ");
        appendPackageNames(stringBuilder, this.packageNames);
        stringBuilder.append(", ");
        appendFeedbackTypes(stringBuilder, this.feedbackType);
        stringBuilder.append(", ");
        stringBuilder.append("notificationTimeout: ").append(this.notificationTimeout);
        stringBuilder.append(", ");
        appendFlags(stringBuilder, this.flags);
        stringBuilder.append(", ");
        stringBuilder.append("id: ").append(this.mId);
        stringBuilder.append(", ");
        stringBuilder.append("resolveInfo: ").append(this.mResolveInfo);
        stringBuilder.append(", ");
        stringBuilder.append("settingsActivityName: ").append(this.mSettingsActivityName);
        stringBuilder.append(", ");
        appendCapabilities(stringBuilder, this.mCapabilities);
        return stringBuilder.toString();
    }

    private static void appendFeedbackTypes(StringBuilder stringBuilder, int feedbackTypes) {
        stringBuilder.append("feedbackTypes:");
        stringBuilder.append("[");
        while (feedbackTypes != 0) {
            int feedbackTypeBit = 1 << Integer.numberOfTrailingZeros(feedbackTypes);
            stringBuilder.append(feedbackTypeToString(feedbackTypeBit));
            feedbackTypes &= feedbackTypeBit ^ (-1);
            if (feedbackTypes != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    private static void appendPackageNames(StringBuilder stringBuilder, String[] packageNames) {
        stringBuilder.append("packageNames:");
        stringBuilder.append("[");
        if (packageNames != null) {
            int packageNameCount = packageNames.length;
            for (int i = 0; i < packageNameCount; i++) {
                stringBuilder.append(packageNames[i]);
                if (i < packageNameCount - 1) {
                    stringBuilder.append(", ");
                }
            }
        }
        stringBuilder.append("]");
    }

    private static void appendEventTypes(StringBuilder stringBuilder, int eventTypes) {
        stringBuilder.append("eventTypes:");
        stringBuilder.append("[");
        while (eventTypes != 0) {
            int eventTypeBit = 1 << Integer.numberOfTrailingZeros(eventTypes);
            stringBuilder.append(AccessibilityEvent.eventTypeToString(eventTypeBit));
            eventTypes &= eventTypeBit ^ (-1);
            if (eventTypes != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    private static void appendFlags(StringBuilder stringBuilder, int flags) {
        stringBuilder.append("flags:");
        stringBuilder.append("[");
        while (flags != 0) {
            int flagBit = 1 << Integer.numberOfTrailingZeros(flags);
            stringBuilder.append(flagToString(flagBit));
            flags &= flagBit ^ (-1);
            if (flags != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    private static void appendCapabilities(StringBuilder stringBuilder, int capabilities) {
        stringBuilder.append("capabilities:");
        stringBuilder.append("[");
        while (capabilities != 0) {
            int capabilityBit = 1 << Integer.numberOfTrailingZeros(capabilities);
            stringBuilder.append(capabilityToString(capabilityBit));
            capabilities &= capabilityBit ^ (-1);
            if (capabilities != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    public static String feedbackTypeToString(int feedbackType) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        while (feedbackType != 0) {
            int feedbackTypeFlag = 1 << Integer.numberOfTrailingZeros(feedbackType);
            feedbackType &= feedbackTypeFlag ^ (-1);
            switch (feedbackTypeFlag) {
                case 1:
                    if (builder.length() > 1) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_SPOKEN");
                    break;
                case 2:
                    if (builder.length() > 1) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_HAPTIC");
                    break;
                case 4:
                    if (builder.length() > 1) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_AUDIBLE");
                    break;
                case 8:
                    if (builder.length() > 1) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_VISUAL");
                    break;
                case 16:
                    if (builder.length() > 1) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_GENERIC");
                    break;
                case 32:
                    if (builder.length() > 1) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_BRAILLE");
                    break;
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static String flagToString(int flag) {
        switch (flag) {
            case 1:
                return "DEFAULT";
            case 2:
                return "FLAG_INCLUDE_NOT_IMPORTANT_VIEWS";
            case 4:
                return "FLAG_REQUEST_TOUCH_EXPLORATION_MODE";
            case 8:
                return "FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
            case 16:
                return "FLAG_REPORT_VIEW_IDS";
            case 32:
                return "FLAG_REQUEST_FILTER_KEY_EVENTS";
            default:
                return null;
        }
    }

    public static String capabilityToString(int capability) {
        switch (capability) {
            case 1:
                return "CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT";
            case 2:
                return "CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION";
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                return IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
            case 4:
                return "CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
            case 8:
                return "CAPABILITY_CAN_FILTER_KEY_EVENTS";
        }
    }

    public List<CapabilityInfo> getCapabilityInfos() {
        if (this.mCapabilities == 0) {
            return Collections.emptyList();
        }
        int capabilities = this.mCapabilities;
        List<CapabilityInfo> capabilityInfos = new ArrayList<>();
        while (capabilities != 0) {
            int capabilityBit = 1 << Integer.numberOfTrailingZeros(capabilities);
            capabilities &= capabilityBit ^ (-1);
            CapabilityInfo capabilityInfo = sAvailableCapabilityInfos.get(capabilityBit);
            if (capabilityInfo != null) {
                capabilityInfos.add(capabilityInfo);
            }
        }
        return capabilityInfos;
    }

    /* loaded from: AccessibilityServiceInfo$CapabilityInfo.class */
    public static final class CapabilityInfo {
        public final int capability;
        public final int titleResId;
        public final int descResId;

        public CapabilityInfo(int capability, int titleResId, int descResId) {
            this.capability = capability;
            this.titleResId = titleResId;
            this.descResId = descResId;
        }
    }
}