package android.text.style;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.view.View;

/* loaded from: URLSpan.class */
public class URLSpan extends ClickableSpan implements ParcelableSpan {
    private final String mURL;

    public URLSpan(String url) {
        this.mURL = url;
    }

    public URLSpan(Parcel src) {
        this.mURL = src.readString();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 11;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mURL);
    }

    public String getURL() {
        return this.mURL;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View widget) {
        Uri uri = Uri.parse(getURL());
        Context context = widget.getContext();
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        context.startActivity(intent);
    }
}