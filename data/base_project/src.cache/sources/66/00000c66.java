package android.printservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: PrintServiceInfo.class */
public final class PrintServiceInfo implements Parcelable {
    private static final String TAG_PRINT_SERVICE = "print-service";
    private final String mId;
    private final ResolveInfo mResolveInfo;
    private final String mSettingsActivityName;
    private final String mAddPrintersActivityName;
    private static final String LOG_TAG = PrintServiceInfo.class.getSimpleName();
    public static final Parcelable.Creator<PrintServiceInfo> CREATOR = new Parcelable.Creator<PrintServiceInfo>() { // from class: android.printservice.PrintServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintServiceInfo createFromParcel(Parcel parcel) {
            return new PrintServiceInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintServiceInfo[] newArray(int size) {
            return new PrintServiceInfo[size];
        }
    };

    public PrintServiceInfo(Parcel parcel) {
        this.mId = parcel.readString();
        this.mResolveInfo = (ResolveInfo) parcel.readParcelable(null);
        this.mSettingsActivityName = parcel.readString();
        this.mAddPrintersActivityName = parcel.readString();
    }

    public PrintServiceInfo(ResolveInfo resolveInfo, String settingsActivityName, String addPrintersActivityName) {
        this.mId = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name).flattenToString();
        this.mResolveInfo = resolveInfo;
        this.mSettingsActivityName = settingsActivityName;
        this.mAddPrintersActivityName = addPrintersActivityName;
    }

    public static PrintServiceInfo create(ResolveInfo resolveInfo, Context context) {
        String settingsActivityName = null;
        String addPrintersActivityName = null;
        PackageManager packageManager = context.getPackageManager();
        XmlResourceParser parser = resolveInfo.serviceInfo.loadXmlMetaData(packageManager, PrintService.SERVICE_META_DATA);
        try {
            if (parser != null) {
                for (int type = 0; type != 1 && type != 2; type = parser.next()) {
                    try {
                        try {
                            try {
                            } catch (IOException ioe) {
                                Log.w(LOG_TAG, "Error reading meta-data:" + ioe);
                                if (parser != null) {
                                    parser.close();
                                }
                            }
                        } catch (XmlPullParserException xppe) {
                            Log.w(LOG_TAG, "Error reading meta-data:" + xppe);
                            if (parser != null) {
                                parser.close();
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(LOG_TAG, "Unable to load resources for: " + resolveInfo.serviceInfo.packageName);
                        if (parser != null) {
                            parser.close();
                        }
                    }
                }
                String nodeName = parser.getName();
                if (TAG_PRINT_SERVICE.equals(nodeName)) {
                    Resources resources = packageManager.getResourcesForApplication(resolveInfo.serviceInfo.applicationInfo);
                    AttributeSet allAttributes = Xml.asAttributeSet(parser);
                    TypedArray attributes = resources.obtainAttributes(allAttributes, R.styleable.PrintService);
                    settingsActivityName = attributes.getString(0);
                    addPrintersActivityName = attributes.getString(1);
                    attributes.recycle();
                } else {
                    Log.e(LOG_TAG, "Ignoring meta-data that does not start with print-service tag");
                }
                if (parser != null) {
                    parser.close();
                }
            }
            return new PrintServiceInfo(resolveInfo, settingsActivityName, addPrintersActivityName);
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
            throw th;
        }
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

    public String getAddPrintersActivityName() {
        return this.mAddPrintersActivityName;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flagz) {
        parcel.writeString(this.mId);
        parcel.writeParcelable(this.mResolveInfo, 0);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeString(this.mAddPrintersActivityName);
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
        PrintServiceInfo other = (PrintServiceInfo) obj;
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
        StringBuilder builder = new StringBuilder();
        builder.append("PrintServiceInfo{");
        builder.append("id=").append(this.mId);
        builder.append(", resolveInfo=").append(this.mResolveInfo);
        builder.append(", settingsActivityName=").append(this.mSettingsActivityName);
        builder.append(", addPrintersActivityName=").append(this.mAddPrintersActivityName);
        builder.append("}");
        return builder.toString();
    }
}