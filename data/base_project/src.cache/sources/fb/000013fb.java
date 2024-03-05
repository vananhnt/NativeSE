package android.text.style;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import java.io.InputStream;

/* loaded from: ImageSpan.class */
public class ImageSpan extends DynamicDrawableSpan {
    private Drawable mDrawable;
    private Uri mContentUri;
    private int mResourceId;
    private Context mContext;
    private String mSource;

    @Deprecated
    public ImageSpan(Bitmap b) {
        this((Context) null, b, 0);
    }

    @Deprecated
    public ImageSpan(Bitmap b, int verticalAlignment) {
        this((Context) null, b, verticalAlignment);
    }

    public ImageSpan(Context context, Bitmap b) {
        this(context, b, 0);
    }

    public ImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(verticalAlignment);
        this.mContext = context;
        this.mDrawable = context != null ? new BitmapDrawable(context.getResources(), b) : new BitmapDrawable(b);
        int width = this.mDrawable.getIntrinsicWidth();
        int height = this.mDrawable.getIntrinsicHeight();
        this.mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
    }

    public ImageSpan(Drawable d) {
        this(d, 0);
    }

    public ImageSpan(Drawable d, int verticalAlignment) {
        super(verticalAlignment);
        this.mDrawable = d;
    }

    public ImageSpan(Drawable d, String source) {
        this(d, source, 0);
    }

    public ImageSpan(Drawable d, String source, int verticalAlignment) {
        super(verticalAlignment);
        this.mDrawable = d;
        this.mSource = source;
    }

    public ImageSpan(Context context, Uri uri) {
        this(context, uri, 0);
    }

    public ImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(verticalAlignment);
        this.mContext = context;
        this.mContentUri = uri;
        this.mSource = uri.toString();
    }

    public ImageSpan(Context context, int resourceId) {
        this(context, resourceId, 0);
    }

    public ImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(verticalAlignment);
        this.mContext = context;
        this.mResourceId = resourceId;
    }

    @Override // android.text.style.DynamicDrawableSpan
    public Drawable getDrawable() {
        Drawable drawable = null;
        if (this.mDrawable != null) {
            drawable = this.mDrawable;
        } else if (this.mContentUri != null) {
            try {
                InputStream is = this.mContext.getContentResolver().openInputStream(this.mContentUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(this.mContext.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                is.close();
            } catch (Exception e) {
                Log.e("sms", "Failed to loaded content " + this.mContentUri, e);
            }
        } else {
            try {
                drawable = this.mContext.getResources().getDrawable(this.mResourceId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            } catch (Exception e2) {
                Log.e("sms", "Unable to find resource: " + this.mResourceId);
            }
        }
        return drawable;
    }

    public String getSource() {
        return this.mSource;
    }
}