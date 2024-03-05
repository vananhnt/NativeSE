package android.telephony;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MediaStore;
import com.android.internal.R;
import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.TelephonyProperties;
import gov.nist.core.Separators;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: TelephonyManager.class */
public class TelephonyManager {
    private static final String TAG = "TelephonyManager";
    private static ITelephonyRegistry sRegistry;
    private final Context mContext;
    public static final String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    public static final String ACTION_RESPOND_VIA_MESSAGE = "android.intent.action.RESPOND_VIA_MESSAGE";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_INCOMING_NUMBER = "incoming_number";
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_SIP = 3;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_CLASS_2_G = 1;
    public static final int NETWORK_CLASS_3_G = 2;
    public static final int NETWORK_CLASS_4_G = 3;
    public static final int SIM_STATE_UNKNOWN = 0;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_READY = 5;
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_RINGING = 1;
    public static final int CALL_STATE_OFFHOOK = 2;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DATA_ACTIVITY_INOUT = 3;
    public static final int DATA_ACTIVITY_DORMANT = 4;
    public static final int DATA_UNKNOWN = -1;
    public static final int DATA_DISCONNECTED = 0;
    public static final int DATA_CONNECTING = 1;
    public static final int DATA_CONNECTED = 2;
    public static final int DATA_SUSPENDED = 3;
    private static TelephonyManager sInstance = new TelephonyManager();
    public static final String EXTRA_STATE_IDLE = PhoneConstants.State.IDLE.toString();
    public static final String EXTRA_STATE_RINGING = PhoneConstants.State.RINGING.toString();
    public static final String EXTRA_STATE_OFFHOOK = PhoneConstants.State.OFFHOOK.toString();
    private static final String sKernelCmdLine = getProcCmdLine();
    private static final Pattern sProductTypePattern = Pattern.compile("\\sproduct_type\\s*=\\s*(\\w+)");
    private static final String sLteOnCdmaProductType = SystemProperties.get(TelephonyProperties.PROPERTY_LTE_ON_CDMA_PRODUCT_TYPE, "");

    public TelephonyManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        if (sRegistry == null) {
            sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
        }
    }

    private TelephonyManager() {
        this.mContext = null;
    }

    public static TelephonyManager getDefault() {
        return sInstance;
    }

    public static TelephonyManager from(Context context) {
        return (TelephonyManager) context.getSystemService("phone");
    }

    public String getDeviceSoftwareVersion() {
        try {
            return getSubscriberInfo().getDeviceSvn();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getDeviceId() {
        try {
            return getSubscriberInfo().getDeviceId();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public CellLocation getCellLocation() {
        try {
            Bundle bundle = getITelephony().getCellLocation();
            if (bundle.isEmpty()) {
                return null;
            }
            CellLocation cl = CellLocation.newFromBundle(bundle);
            if (cl.isEmpty()) {
                return null;
            }
            return cl;
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public void enableLocationUpdates() {
        try {
            getITelephony().enableLocationUpdates();
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public void disableLocationUpdates() {
        try {
            getITelephony().disableLocationUpdates();
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public List<NeighboringCellInfo> getNeighboringCellInfo() {
        try {
            return getITelephony().getNeighboringCellInfo(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public int getCurrentPhoneType() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getActivePhoneType();
            }
            return getPhoneTypeFromProperty();
        } catch (RemoteException e) {
            return getPhoneTypeFromProperty();
        } catch (NullPointerException e2) {
            return getPhoneTypeFromProperty();
        }
    }

    public int getPhoneType() {
        if (!isVoiceCapable()) {
            return 0;
        }
        return getCurrentPhoneType();
    }

    private int getPhoneTypeFromProperty() {
        int type = SystemProperties.getInt(TelephonyProperties.CURRENT_ACTIVE_PHONE, getPhoneTypeFromNetworkType());
        return type;
    }

    private int getPhoneTypeFromNetworkType() {
        int mode = SystemProperties.getInt("ro.telephony.default_network", -1);
        if (mode == -1) {
            return 0;
        }
        return getPhoneType(mode);
    }

    public static int getPhoneType(int networkMode) {
        switch (networkMode) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 9:
            case 10:
            case 12:
                return 1;
            case 4:
            case 5:
            case 6:
                return 2;
            case 7:
            case 8:
                return 2;
            case 11:
                if (getLteOnCdmaModeStatic() == 1) {
                    return 2;
                }
                return 1;
            default:
                return 1;
        }
    }

    private static String getProcCmdLine() {
        String cmdline = "";
        FileInputStream is = null;
        try {
            try {
                is = new FileInputStream("/proc/cmdline");
                byte[] buffer = new byte[2048];
                int count = is.read(buffer);
                if (count > 0) {
                    cmdline = new String(buffer, 0, count);
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e2) {
                Rlog.d(TAG, "No /proc/cmdline exception=" + e2);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                    }
                }
            }
            Rlog.d(TAG, "/proc/cmdline=" + cmdline);
            return cmdline;
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    public static int getLteOnCdmaModeStatic() {
        String productType = "";
        int curVal = SystemProperties.getInt(TelephonyProperties.PROPERTY_LTE_ON_CDMA_DEVICE, -1);
        int retVal = curVal;
        if (retVal == -1) {
            Matcher matcher = sProductTypePattern.matcher(sKernelCmdLine);
            if (matcher.find()) {
                productType = matcher.group(1);
                if (sLteOnCdmaProductType.equals(productType)) {
                    retVal = 1;
                } else {
                    retVal = 0;
                }
            } else {
                retVal = 0;
            }
        }
        Rlog.d(TAG, "getLteOnCdmaMode=" + retVal + " curVal=" + curVal + " product_type='" + productType + "' lteOnCdmaProductType='" + sLteOnCdmaProductType + Separators.QUOTE);
        return retVal;
    }

    public String getNetworkOperatorName() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_ALPHA);
    }

    public String getNetworkOperator() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_NUMERIC);
    }

    public boolean isNetworkRoaming() {
        return "true".equals(SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_ISROAMING));
    }

    public String getNetworkCountryIso() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_ISO_COUNTRY);
    }

    public int getNetworkType() {
        return getDataNetworkType();
    }

    public int getDataNetworkType() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getDataNetworkType();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    public int getVoiceNetworkType() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getVoiceNetworkType();
            }
            return 0;
        } catch (RemoteException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return 1;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return 2;
            case 13:
                return 3;
            default:
                return 0;
        }
    }

    public String getNetworkTypeName() {
        return getNetworkTypeName(getNetworkType());
    }

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            default:
                return IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
        }
    }

    public boolean hasIccCard() {
        try {
            return getITelephony().hasIccCard();
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e2) {
            return false;
        }
    }

    public int getSimState() {
        String prop = SystemProperties.get(TelephonyProperties.PROPERTY_SIM_STATE);
        if (IccCardConstants.INTENT_VALUE_ICC_ABSENT.equals(prop)) {
            return 1;
        }
        if ("PIN_REQUIRED".equals(prop)) {
            return 2;
        }
        if ("PUK_REQUIRED".equals(prop)) {
            return 3;
        }
        if ("NETWORK_LOCKED".equals(prop)) {
            return 4;
        }
        if (IccCardConstants.INTENT_VALUE_ICC_READY.equals(prop)) {
            return 5;
        }
        return 0;
    }

    public String getSimOperator() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_ICC_OPERATOR_NUMERIC);
    }

    public String getSimOperatorName() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_ICC_OPERATOR_ALPHA);
    }

    public String getSimCountryIso() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_ICC_OPERATOR_ISO_COUNTRY);
    }

    public String getSimSerialNumber() {
        try {
            return getSubscriberInfo().getIccSerialNumber();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public int getLteOnCdmaMode() {
        try {
            return getITelephony().getLteOnCdmaMode();
        } catch (RemoteException e) {
            return -1;
        } catch (NullPointerException e2) {
            return -1;
        }
    }

    public String getSubscriberId() {
        try {
            return getSubscriberInfo().getSubscriberId();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getGroupIdLevel1() {
        try {
            return getSubscriberInfo().getGroupIdLevel1();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getLine1Number() {
        try {
            return getSubscriberInfo().getLine1Number();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getLine1AlphaTag() {
        try {
            return getSubscriberInfo().getLine1AlphaTag();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getMsisdn() {
        try {
            return getSubscriberInfo().getMsisdn();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getVoiceMailNumber() {
        try {
            return getSubscriberInfo().getVoiceMailNumber();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getCompleteVoiceMailNumber() {
        try {
            return getSubscriberInfo().getCompleteVoiceMailNumber();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public int getVoiceMessageCount() {
        try {
            return getITelephony().getVoiceMessageCount();
        } catch (RemoteException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    public String getVoiceMailAlphaTag() {
        try {
            return getSubscriberInfo().getVoiceMailAlphaTag();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getIsimImpi() {
        try {
            return getSubscriberInfo().getIsimImpi();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String getIsimDomain() {
        try {
            return getSubscriberInfo().getIsimDomain();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public String[] getIsimImpu() {
        try {
            return getSubscriberInfo().getIsimImpu();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    private IPhoneSubInfo getSubscriberInfo() {
        return IPhoneSubInfo.Stub.asInterface(ServiceManager.getService("iphonesubinfo"));
    }

    public int getCallState() {
        try {
            return getITelephony().getCallState();
        } catch (RemoteException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    public int getDataActivity() {
        try {
            return getITelephony().getDataActivity();
        } catch (RemoteException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    public int getDataState() {
        try {
            return getITelephony().getDataState();
        } catch (RemoteException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    private ITelephony getITelephony() {
        return ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
    }

    public void listen(PhoneStateListener listener, int events) {
        String pkgForDebug = this.mContext != null ? this.mContext.getPackageName() : MediaStore.UNKNOWN_STRING;
        try {
            Boolean notifyNow = true;
            sRegistry.listen(pkgForDebug, listener.callback, events, notifyNow.booleanValue());
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public int getCdmaEriIconIndex() {
        try {
            return getITelephony().getCdmaEriIconIndex();
        } catch (RemoteException e) {
            return -1;
        } catch (NullPointerException e2) {
            return -1;
        }
    }

    public int getCdmaEriIconMode() {
        try {
            return getITelephony().getCdmaEriIconMode();
        } catch (RemoteException e) {
            return -1;
        } catch (NullPointerException e2) {
            return -1;
        }
    }

    public String getCdmaEriText() {
        try {
            return getITelephony().getCdmaEriText();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public boolean isVoiceCapable() {
        if (this.mContext == null) {
            return true;
        }
        return this.mContext.getResources().getBoolean(R.bool.config_voice_capable);
    }

    public boolean isSmsCapable() {
        if (this.mContext == null) {
            return true;
        }
        return this.mContext.getResources().getBoolean(R.bool.config_sms_capable);
    }

    public List<CellInfo> getAllCellInfo() {
        try {
            return getITelephony().getAllCellInfo();
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public void setCellInfoListRate(int rateInMillis) {
        try {
            getITelephony().setCellInfoListRate(rateInMillis);
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public String getMmsUserAgent() {
        if (this.mContext == null) {
            return null;
        }
        return this.mContext.getResources().getString(R.string.config_mms_user_agent);
    }

    public String getMmsUAProfUrl() {
        if (this.mContext == null) {
            return null;
        }
        return this.mContext.getResources().getString(R.string.config_mms_user_agent_profile_url);
    }
}