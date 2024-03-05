package android.location;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

/* loaded from: GeocoderParams.class */
public class GeocoderParams implements Parcelable {
    private Locale mLocale;
    private String mPackageName;
    public static final Parcelable.Creator<GeocoderParams> CREATOR = new Parcelable.Creator<GeocoderParams>() { // from class: android.location.GeocoderParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GeocoderParams createFromParcel(Parcel in) {
            GeocoderParams gp = new GeocoderParams();
            String language = in.readString();
            String country = in.readString();
            String variant = in.readString();
            gp.mLocale = new Locale(language, country, variant);
            gp.mPackageName = in.readString();
            return gp;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GeocoderParams[] newArray(int size) {
            return new GeocoderParams[size];
        }
    };

    private GeocoderParams() {
    }

    public GeocoderParams(Context context, Locale locale) {
        this.mLocale = locale;
        this.mPackageName = context.getPackageName();
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public String getClientPackage() {
        return this.mPackageName;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mLocale.getLanguage());
        parcel.writeString(this.mLocale.getCountry());
        parcel.writeString(this.mLocale.getVariant());
        parcel.writeString(this.mPackageName);
    }
}