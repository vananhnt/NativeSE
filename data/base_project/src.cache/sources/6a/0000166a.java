package android.view.inputmethod;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Printer;
import android.util.Slog;
import android.util.Xml;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: InputMethodInfo.class */
public final class InputMethodInfo implements Parcelable {
    static final String TAG = "InputMethodInfo";
    final ResolveInfo mService;
    final String mId;
    final String mSettingsActivityName;
    final int mIsDefaultResId;
    private final ArrayList<InputMethodSubtype> mSubtypes;
    private final boolean mIsAuxIme;
    private final boolean mForceDefault;
    private final boolean mSupportsSwitchingToNextInputMethod;
    public static final Parcelable.Creator<InputMethodInfo> CREATOR = new Parcelable.Creator<InputMethodInfo>() { // from class: android.view.inputmethod.InputMethodInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputMethodInfo createFromParcel(Parcel source) {
            return new InputMethodInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputMethodInfo[] newArray(int size) {
            return new InputMethodInfo[size];
        }
    };

    public InputMethodInfo(Context context, ResolveInfo service) throws XmlPullParserException, IOException {
        this(context, service, null);
    }

    public InputMethodInfo(Context context, ResolveInfo service, Map<String, List<InputMethodSubtype>> additionalSubtypesMap) throws XmlPullParserException, IOException {
        this.mSubtypes = new ArrayList<>();
        this.mService = service;
        ServiceInfo si = service.serviceInfo;
        this.mId = new ComponentName(si.packageName, si.name).flattenToShortString();
        boolean isAuxIme = true;
        this.mForceDefault = false;
        PackageManager pm = context.getPackageManager();
        XmlResourceParser parser = null;
        try {
            try {
                parser = si.loadXmlMetaData(pm, InputMethod.SERVICE_META_DATA);
                if (parser == null) {
                    throw new XmlPullParserException("No android.view.im meta-data");
                }
                Resources res = pm.getResourcesForApplication(si.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser.getName();
                if (!"input-method".equals(nodeName)) {
                    throw new XmlPullParserException("Meta-data does not start with input-method tag");
                }
                TypedArray sa = res.obtainAttributes(attrs, R.styleable.InputMethod);
                String settingsActivityComponent = sa.getString(1);
                int isDefaultResId = sa.getResourceId(0, 0);
                boolean supportsSwitchingToNextInputMethod = sa.getBoolean(2, false);
                sa.recycle();
                int depth = parser.getDepth();
                while (true) {
                    int type2 = parser.next();
                    if ((type2 != 3 || parser.getDepth() > depth) && type2 != 1) {
                        if (type2 == 2) {
                            String nodeName2 = parser.getName();
                            if (!"subtype".equals(nodeName2)) {
                                throw new XmlPullParserException("Meta-data in input-method does not start with subtype tag");
                            }
                            TypedArray a = res.obtainAttributes(attrs, R.styleable.InputMethod_Subtype);
                            InputMethodSubtype subtype = new InputMethodSubtype.InputMethodSubtypeBuilder().setSubtypeNameResId(a.getResourceId(0, 0)).setSubtypeIconResId(a.getResourceId(1, 0)).setSubtypeLocale(a.getString(2)).setSubtypeMode(a.getString(3)).setSubtypeExtraValue(a.getString(4)).setIsAuxiliary(a.getBoolean(5, false)).setOverridesImplicitlyEnabledSubtype(a.getBoolean(6, false)).setSubtypeId(a.getInt(7, 0)).setIsAsciiCapable(a.getBoolean(8, false)).build();
                            isAuxIme = subtype.isAuxiliary() ? isAuxIme : false;
                            this.mSubtypes.add(subtype);
                        }
                    }
                }
                isAuxIme = this.mSubtypes.size() == 0 ? false : isAuxIme;
                if (additionalSubtypesMap != null && additionalSubtypesMap.containsKey(this.mId)) {
                    List<InputMethodSubtype> additionalSubtypes = additionalSubtypesMap.get(this.mId);
                    int N = additionalSubtypes.size();
                    for (int i = 0; i < N; i++) {
                        InputMethodSubtype subtype2 = additionalSubtypes.get(i);
                        if (!this.mSubtypes.contains(subtype2)) {
                            this.mSubtypes.add(subtype2);
                        } else {
                            Slog.w(TAG, "Duplicated subtype definition found: " + subtype2.getLocale() + ", " + subtype2.getMode());
                        }
                    }
                }
                this.mSettingsActivityName = settingsActivityComponent;
                this.mIsDefaultResId = isDefaultResId;
                this.mIsAuxIme = isAuxIme;
                this.mSupportsSwitchingToNextInputMethod = supportsSwitchingToNextInputMethod;
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to create context for: " + si.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    InputMethodInfo(Parcel source) {
        this.mSubtypes = new ArrayList<>();
        this.mId = source.readString();
        this.mSettingsActivityName = source.readString();
        this.mIsDefaultResId = source.readInt();
        this.mIsAuxIme = source.readInt() == 1;
        this.mSupportsSwitchingToNextInputMethod = source.readInt() == 1;
        this.mService = ResolveInfo.CREATOR.createFromParcel(source);
        source.readTypedList(this.mSubtypes, InputMethodSubtype.CREATOR);
        this.mForceDefault = false;
    }

    public InputMethodInfo(String packageName, String className, CharSequence label, String settingsActivity) {
        this(buildDummyResolveInfo(packageName, className, label), false, settingsActivity, null, 0, false);
    }

    public InputMethodInfo(ResolveInfo ri, boolean isAuxIme, String settingsActivity, List<InputMethodSubtype> subtypes, int isDefaultResId, boolean forceDefault) {
        this.mSubtypes = new ArrayList<>();
        ServiceInfo si = ri.serviceInfo;
        this.mService = ri;
        this.mId = new ComponentName(si.packageName, si.name).flattenToShortString();
        this.mSettingsActivityName = settingsActivity;
        this.mIsDefaultResId = isDefaultResId;
        this.mIsAuxIme = isAuxIme;
        if (subtypes != null) {
            this.mSubtypes.addAll(subtypes);
        }
        this.mForceDefault = forceDefault;
        this.mSupportsSwitchingToNextInputMethod = true;
    }

    private static ResolveInfo buildDummyResolveInfo(String packageName, String className, CharSequence label) {
        ResolveInfo ri = new ResolveInfo();
        ServiceInfo si = new ServiceInfo();
        ApplicationInfo ai = new ApplicationInfo();
        ai.packageName = packageName;
        ai.enabled = true;
        si.applicationInfo = ai;
        si.enabled = true;
        si.packageName = packageName;
        si.name = className;
        si.exported = true;
        si.nonLocalizedLabel = label;
        ri.serviceInfo = si;
        return ri;
    }

    public String getId() {
        return this.mId;
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    public String getServiceName() {
        return this.mService.serviceInfo.name;
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public CharSequence loadLabel(PackageManager pm) {
        return this.mService.loadLabel(pm);
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public int getSubtypeCount() {
        return this.mSubtypes.size();
    }

    public InputMethodSubtype getSubtypeAt(int index) {
        return this.mSubtypes.get(index);
    }

    public int getIsDefaultResourceId() {
        return this.mIsDefaultResId;
    }

    public boolean isDefault(Context context) {
        if (this.mForceDefault) {
            return true;
        }
        try {
            Resources res = context.createPackageContext(getPackageName(), 0).getResources();
            return res.getBoolean(getIsDefaultResourceId());
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + "mId=" + this.mId + " mSettingsActivityName=" + this.mSettingsActivityName);
        pw.println(prefix + "mIsDefaultResId=0x" + Integer.toHexString(this.mIsDefaultResId));
        pw.println(prefix + "Service:");
        this.mService.dump(pw, prefix + "  ");
    }

    public String toString() {
        return "InputMethodInfo{" + this.mId + ", settings: " + this.mSettingsActivityName + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && (o instanceof InputMethodInfo)) {
            InputMethodInfo obj = (InputMethodInfo) o;
            return this.mId.equals(obj.mId);
        }
        return false;
    }

    public int hashCode() {
        return this.mId.hashCode();
    }

    public boolean isAuxiliaryIme() {
        return this.mIsAuxIme;
    }

    public boolean supportsSwitchingToNextInputMethod() {
        return this.mSupportsSwitchingToNextInputMethod;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mSettingsActivityName);
        dest.writeInt(this.mIsDefaultResId);
        dest.writeInt(this.mIsAuxIme ? 1 : 0);
        dest.writeInt(this.mSupportsSwitchingToNextInputMethod ? 1 : 0);
        this.mService.writeToParcel(dest, flags);
        dest.writeTypedList(this.mSubtypes);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}