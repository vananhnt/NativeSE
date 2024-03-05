package android.nfc.cardemulation;

import android.content.ComponentName;
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
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: ApduServiceInfo.class */
public final class ApduServiceInfo implements Parcelable {
    static final String TAG = "ApduServiceInfo";
    final ResolveInfo mService;
    final String mDescription;
    final ArrayList<String> mAids;
    final boolean mOnHost;
    final ArrayList<AidGroup> mAidGroups;
    final HashMap<String, AidGroup> mCategoryToGroup;
    final boolean mRequiresDeviceUnlock;
    final int mBannerResourceId;
    public static final Parcelable.Creator<ApduServiceInfo> CREATOR = new Parcelable.Creator<ApduServiceInfo>() { // from class: android.nfc.cardemulation.ApduServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduServiceInfo createFromParcel(Parcel source) {
            ResolveInfo info = ResolveInfo.CREATOR.createFromParcel(source);
            String description = source.readString();
            boolean onHost = source.readInt() != 0;
            ArrayList<AidGroup> aidGroups = new ArrayList<>();
            int numGroups = source.readInt();
            if (numGroups > 0) {
                source.readTypedList(aidGroups, AidGroup.CREATOR);
            }
            boolean requiresUnlock = source.readInt() != 0;
            int bannerResource = source.readInt();
            return new ApduServiceInfo(info, onHost, description, aidGroups, requiresUnlock, bannerResource);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduServiceInfo[] newArray(int size) {
            return new ApduServiceInfo[size];
        }
    };

    public ApduServiceInfo(ResolveInfo info, boolean onHost, String description, ArrayList<AidGroup> aidGroups, boolean requiresUnlock, int bannerResource) {
        this.mService = info;
        this.mDescription = description;
        this.mAidGroups = aidGroups;
        this.mAids = new ArrayList<>();
        this.mCategoryToGroup = new HashMap<>();
        this.mOnHost = onHost;
        this.mRequiresDeviceUnlock = requiresUnlock;
        Iterator i$ = aidGroups.iterator();
        while (i$.hasNext()) {
            AidGroup aidGroup = i$.next();
            this.mCategoryToGroup.put(aidGroup.category, aidGroup);
            this.mAids.addAll(aidGroup.aids);
        }
        this.mBannerResourceId = bannerResource;
    }

    public ApduServiceInfo(PackageManager pm, ResolveInfo info, boolean onHost) throws XmlPullParserException, IOException {
        XmlResourceParser parser;
        ServiceInfo si = info.serviceInfo;
        XmlResourceParser parser2 = null;
        try {
            try {
                if (onHost) {
                    parser = si.loadXmlMetaData(pm, HostApduService.SERVICE_META_DATA);
                    if (parser == null) {
                        throw new XmlPullParserException("No android.nfc.cardemulation.host_apdu_service meta-data");
                    }
                } else {
                    parser = si.loadXmlMetaData(pm, OffHostApduService.SERVICE_META_DATA);
                    if (parser == null) {
                        throw new XmlPullParserException("No android.nfc.cardemulation.off_host_apdu_service meta-data");
                    }
                }
                for (int eventType = parser.getEventType(); eventType != 2 && eventType != 1; eventType = parser.next()) {
                }
                String tagName = parser.getName();
                if (onHost && !"host-apdu-service".equals(tagName)) {
                    throw new XmlPullParserException("Meta-data does not start with <host-apdu-service> tag");
                }
                if (!onHost && !"offhost-apdu-service".equals(tagName)) {
                    throw new XmlPullParserException("Meta-data does not start with <offhost-apdu-service> tag");
                }
                Resources res = pm.getResourcesForApplication(si.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                if (onHost) {
                    TypedArray sa = res.obtainAttributes(attrs, R.styleable.HostApduService);
                    this.mService = info;
                    this.mDescription = sa.getString(0);
                    this.mRequiresDeviceUnlock = sa.getBoolean(1, false);
                    this.mBannerResourceId = sa.getResourceId(2, -1);
                    sa.recycle();
                } else {
                    TypedArray sa2 = res.obtainAttributes(attrs, R.styleable.OffHostApduService);
                    this.mService = info;
                    this.mDescription = sa2.getString(0);
                    this.mRequiresDeviceUnlock = false;
                    this.mBannerResourceId = sa2.getResourceId(1, -1);
                    sa2.recycle();
                }
                this.mAidGroups = new ArrayList<>();
                this.mCategoryToGroup = new HashMap<>();
                this.mAids = new ArrayList<>();
                this.mOnHost = onHost;
                int depth = parser.getDepth();
                AidGroup currentGroup = null;
                while (true) {
                    int eventType2 = parser.next();
                    if ((eventType2 != 3 || parser.getDepth() > depth) && eventType2 != 1) {
                        String tagName2 = parser.getName();
                        if (eventType2 == 2 && "aid-group".equals(tagName2) && currentGroup == null) {
                            TypedArray groupAttrs = res.obtainAttributes(attrs, R.styleable.AidGroup);
                            String groupDescription = groupAttrs.getString(0);
                            String groupCategory = groupAttrs.getString(1);
                            groupCategory = CardEmulation.CATEGORY_PAYMENT.equals(groupCategory) ? groupCategory : CardEmulation.CATEGORY_OTHER;
                            currentGroup = this.mCategoryToGroup.get(groupCategory);
                            if (currentGroup != null) {
                                if (!CardEmulation.CATEGORY_OTHER.equals(groupCategory)) {
                                    Log.e(TAG, "Not allowing multiple aid-groups in the " + groupCategory + " category");
                                    currentGroup = null;
                                }
                            } else {
                                currentGroup = new AidGroup(groupCategory, groupDescription);
                            }
                            groupAttrs.recycle();
                        } else if (eventType2 == 3 && "aid-group".equals(tagName2) && currentGroup != null) {
                            if (currentGroup.aids.size() > 0) {
                                if (!this.mCategoryToGroup.containsKey(currentGroup.category)) {
                                    this.mAidGroups.add(currentGroup);
                                    this.mCategoryToGroup.put(currentGroup.category, currentGroup);
                                }
                            } else {
                                Log.e(TAG, "Not adding <aid-group> with empty or invalid AIDs");
                            }
                            currentGroup = null;
                        } else if (eventType2 == 2 && "aid-filter".equals(tagName2) && currentGroup != null) {
                            TypedArray a = res.obtainAttributes(attrs, R.styleable.AidFilter);
                            String aid = a.getString(0).toUpperCase();
                            if (isValidAid(aid) && !currentGroup.aids.contains(aid)) {
                                currentGroup.aids.add(aid);
                                this.mAids.add(aid);
                            } else {
                                Log.e(TAG, "Ignoring invalid or duplicate aid: " + aid);
                            }
                            a.recycle();
                        }
                    }
                }
                if (parser != null) {
                    parser.close();
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to create context for: " + si.packageName);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                parser2.close();
            }
            throw th;
        }
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public ArrayList<String> getAids() {
        return this.mAids;
    }

    public ArrayList<AidGroup> getAidGroups() {
        return this.mAidGroups;
    }

    public boolean hasCategory(String category) {
        return this.mCategoryToGroup.containsKey(category);
    }

    public boolean isOnHost() {
        return this.mOnHost;
    }

    public boolean requiresUnlock() {
        return this.mRequiresDeviceUnlock;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public CharSequence loadLabel(PackageManager pm) {
        return this.mService.loadLabel(pm);
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public Drawable loadBanner(PackageManager pm) {
        try {
            Resources res = pm.getResourcesForApplication(this.mService.serviceInfo.packageName);
            Drawable banner = res.getDrawable(this.mBannerResourceId);
            return banner;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not load banner.");
            return null;
        } catch (Resources.NotFoundException e2) {
            Log.e(TAG, "Could not load banner.");
            return null;
        }
    }

    static boolean isValidAid(String aid) {
        if (aid == null) {
            return false;
        }
        int aidLength = aid.length();
        if (aidLength == 0 || aidLength % 2 != 0) {
            Log.e(TAG, "AID " + aid + " is not correctly formatted.");
            return false;
        } else if (aidLength < 10) {
            Log.e(TAG, "AID " + aid + " is shorter than 5 bytes.");
            return false;
        } else {
            return true;
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder("ApduService: ");
        out.append(getComponent());
        out.append(", description: " + this.mDescription);
        out.append(", AID Groups: ");
        Iterator i$ = this.mAidGroups.iterator();
        while (i$.hasNext()) {
            AidGroup aidGroup = i$.next();
            out.append(aidGroup.toString());
        }
        return out.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ApduServiceInfo) {
            ApduServiceInfo thatService = (ApduServiceInfo) o;
            return thatService.getComponent().equals(getComponent());
        }
        return false;
    }

    public int hashCode() {
        return getComponent().hashCode();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mService.writeToParcel(dest, flags);
        dest.writeString(this.mDescription);
        dest.writeInt(this.mOnHost ? 1 : 0);
        dest.writeInt(this.mAidGroups.size());
        if (this.mAidGroups.size() > 0) {
            dest.writeTypedList(this.mAidGroups);
        }
        dest.writeInt(this.mRequiresDeviceUnlock ? 1 : 0);
        dest.writeInt(this.mBannerResourceId);
    }

    /* loaded from: ApduServiceInfo$AidGroup.class */
    public static class AidGroup implements Parcelable {
        final ArrayList<String> aids;
        final String category;
        final String description;
        public static final Parcelable.Creator<AidGroup> CREATOR = new Parcelable.Creator<AidGroup>() { // from class: android.nfc.cardemulation.ApduServiceInfo.AidGroup.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AidGroup createFromParcel(Parcel source) {
                String category = source.readString();
                String description = source.readString();
                int listSize = source.readInt();
                ArrayList<String> aidList = new ArrayList<>();
                if (listSize > 0) {
                    source.readStringList(aidList);
                }
                return new AidGroup(aidList, category, description);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AidGroup[] newArray(int size) {
                return new AidGroup[size];
            }
        };

        AidGroup(ArrayList<String> aids, String category, String description) {
            this.aids = aids;
            this.category = category;
            this.description = description;
        }

        AidGroup(String category, String description) {
            this.aids = new ArrayList<>();
            this.category = category;
            this.description = description;
        }

        public String getCategory() {
            return this.category;
        }

        public ArrayList<String> getAids() {
            return this.aids;
        }

        public String toString() {
            StringBuilder out = new StringBuilder("Category: " + this.category + ", description: " + this.description + ", AIDs:");
            Iterator i$ = this.aids.iterator();
            while (i$.hasNext()) {
                String aid = i$.next();
                out.append(aid);
                out.append(", ");
            }
            return out.toString();
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.category);
            dest.writeString(this.description);
            dest.writeInt(this.aids.size());
            if (this.aids.size() > 0) {
                dest.writeStringList(this.aids);
            }
        }
    }
}