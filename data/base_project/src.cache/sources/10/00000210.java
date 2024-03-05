package android.app;

import android.app.IWallpaperManager;
import android.app.IWallpaperManagerCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.R;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/* loaded from: WallpaperManager.class */
public class WallpaperManager {
    private float mWallpaperXStep = -1.0f;
    private float mWallpaperYStep = -1.0f;
    public static final String ACTION_CROP_AND_SET_WALLPAPER = "android.service.wallpaper.CROP_AND_SET_WALLPAPER";
    public static final String ACTION_LIVE_WALLPAPER_CHOOSER = "android.service.wallpaper.LIVE_WALLPAPER_CHOOSER";
    public static final String ACTION_CHANGE_LIVE_WALLPAPER = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";
    public static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
    public static final String WALLPAPER_PREVIEW_META_DATA = "android.wallpaper.preview";
    public static final String COMMAND_TAP = "android.wallpaper.tap";
    public static final String COMMAND_SECONDARY_TAP = "android.wallpaper.secondaryTap";
    public static final String COMMAND_DROP = "android.home.drop";
    private final Context mContext;
    private static Globals sGlobals;
    private static String TAG = "WallpaperManager";
    private static boolean DEBUG = false;
    private static final Object sSync = new Object[0];

    /* loaded from: WallpaperManager$FastBitmapDrawable.class */
    static class FastBitmapDrawable extends Drawable {
        private final Bitmap mBitmap;
        private final int mWidth;
        private final int mHeight;
        private int mDrawLeft;
        private int mDrawTop;
        private final Paint mPaint;

        private FastBitmapDrawable(Bitmap bitmap) {
            this.mBitmap = bitmap;
            this.mWidth = bitmap.getWidth();
            this.mHeight = bitmap.getHeight();
            setBounds(0, 0, this.mWidth, this.mHeight);
            this.mPaint = new Paint();
            this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, this.mDrawLeft, this.mDrawTop, this.mPaint);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -1;
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(int left, int top, int right, int bottom) {
            this.mDrawLeft = left + (((right - left) - this.mWidth) / 2);
            this.mDrawTop = top + (((bottom - top) - this.mHeight) / 2);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter cf) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setDither(boolean dither) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setFilterBitmap(boolean filter) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.mWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.mHeight;
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumWidth() {
            return this.mWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumHeight() {
            return this.mHeight;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WallpaperManager$Globals.class */
    public static class Globals extends IWallpaperManagerCallback.Stub {
        private IWallpaperManager mService;
        private Bitmap mWallpaper;
        private Bitmap mDefaultWallpaper;
        private static final int MSG_CLEAR_WALLPAPER = 1;
        private final Handler mHandler;

        Globals(Looper looper) {
            IBinder b = ServiceManager.getService(Context.WALLPAPER_SERVICE);
            this.mService = IWallpaperManager.Stub.asInterface(b);
            this.mHandler = new Handler(looper) { // from class: android.app.WallpaperManager.Globals.1
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            synchronized (this) {
                                Globals.this.mWallpaper = null;
                                Globals.this.mDefaultWallpaper = null;
                            }
                            return;
                        default:
                            return;
                    }
                }
            };
        }

        @Override // android.app.IWallpaperManagerCallback
        public void onWallpaperChanged() {
            this.mHandler.sendEmptyMessage(1);
        }

        public Bitmap peekWallpaperBitmap(Context context, boolean returnDefault) {
            synchronized (this) {
                if (this.mWallpaper != null) {
                    return this.mWallpaper;
                } else if (this.mDefaultWallpaper != null) {
                    return this.mDefaultWallpaper;
                } else {
                    this.mWallpaper = null;
                    try {
                        this.mWallpaper = getCurrentWallpaperLocked(context);
                    } catch (OutOfMemoryError e) {
                        Log.w(WallpaperManager.TAG, "No memory load current wallpaper", e);
                    }
                    if (returnDefault) {
                        if (this.mWallpaper == null) {
                            this.mDefaultWallpaper = getDefaultWallpaperLocked(context);
                            return this.mDefaultWallpaper;
                        }
                        this.mDefaultWallpaper = null;
                    }
                    return this.mWallpaper;
                }
            }
        }

        public void forgetLoadedWallpaper() {
            synchronized (this) {
                this.mWallpaper = null;
                this.mDefaultWallpaper = null;
                this.mHandler.removeMessages(1);
            }
        }

        private Bitmap getCurrentWallpaperLocked(Context context) {
            try {
                Bundle params = new Bundle();
                ParcelFileDescriptor fd = this.mService.getWallpaper(this, params);
                if (fd != null) {
                    int width = params.getInt("width", 0);
                    int height = params.getInt("height", 0);
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap bm = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
                        Bitmap generateBitmap = WallpaperManager.generateBitmap(context, bm, width, height);
                        try {
                            fd.close();
                        } catch (IOException e) {
                        }
                        return generateBitmap;
                    } catch (OutOfMemoryError e2) {
                        Log.w(WallpaperManager.TAG, "Can't decode file", e2);
                        try {
                            fd.close();
                        } catch (IOException e3) {
                        }
                    }
                }
                return null;
            } catch (RemoteException e4) {
                return null;
            }
        }

        private Bitmap getDefaultWallpaperLocked(Context context) {
            try {
                InputStream is = context.getResources().openRawResource(R.drawable.default_wallpaper);
                if (is != null) {
                    int width = this.mService.getWidthHint();
                    int height = this.mService.getHeightHint();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap bm = BitmapFactory.decodeStream(is, null, options);
                        Bitmap generateBitmap = WallpaperManager.generateBitmap(context, bm, width, height);
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                        return generateBitmap;
                    } catch (OutOfMemoryError e2) {
                        Log.w(WallpaperManager.TAG, "Can't decode stream", e2);
                        try {
                            is.close();
                        } catch (IOException e3) {
                        }
                    }
                }
                return null;
            } catch (RemoteException e4) {
                return null;
            }
        }
    }

    static void initGlobals(Looper looper) {
        synchronized (sSync) {
            if (sGlobals == null) {
                sGlobals = new Globals(looper);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WallpaperManager(Context context, Handler handler) {
        this.mContext = context;
        initGlobals(context.getMainLooper());
    }

    public static WallpaperManager getInstance(Context context) {
        return (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
    }

    public IWallpaperManager getIWallpaperManager() {
        return sGlobals.mService;
    }

    public Drawable getDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, true);
        if (bm != null) {
            Drawable dr = new BitmapDrawable(this.mContext.getResources(), bm);
            dr.setDither(false);
            return dr;
        }
        return null;
    }

    public Drawable getBuiltInDrawable() {
        return getBuiltInDrawable(0, 0, false, 0.0f, 0.0f);
    }

    public Drawable getBuiltInDrawable(int outWidth, int outHeight, boolean scaleToFit, float horizontalAlignment, float verticalAlignment) {
        RectF cropRectF;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return null;
        }
        Resources resources = this.mContext.getResources();
        float horizontalAlignment2 = Math.max(0.0f, Math.min(1.0f, horizontalAlignment));
        float verticalAlignment2 = Math.max(0.0f, Math.min(1.0f, verticalAlignment));
        InputStream is = new BufferedInputStream(resources.openRawResource(R.drawable.default_wallpaper));
        if (is == null) {
            Log.e(TAG, "default wallpaper input stream is null");
            return null;
        } else if (outWidth <= 0 || outHeight <= 0) {
            Bitmap fullSize = BitmapFactory.decodeStream(is, null, null);
            return new BitmapDrawable(resources, fullSize);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            if (options.outWidth != 0 && options.outHeight != 0) {
                int inWidth = options.outWidth;
                int inHeight = options.outHeight;
                InputStream is2 = new BufferedInputStream(resources.openRawResource(R.drawable.default_wallpaper));
                int outWidth2 = Math.min(inWidth, outWidth);
                int outHeight2 = Math.min(inHeight, outHeight);
                if (scaleToFit) {
                    cropRectF = getMaxCropRect(inWidth, inHeight, outWidth2, outHeight2, horizontalAlignment2, verticalAlignment2);
                } else {
                    float left = (inWidth - outWidth2) * horizontalAlignment2;
                    float right = left + outWidth2;
                    float top = (inHeight - outHeight2) * verticalAlignment2;
                    float bottom = top + outHeight2;
                    cropRectF = new RectF(left, top, right, bottom);
                }
                Rect roundedTrueCrop = new Rect();
                cropRectF.roundOut(roundedTrueCrop);
                if (roundedTrueCrop.width() <= 0 || roundedTrueCrop.height() <= 0) {
                    Log.w(TAG, "crop has bad values for full size image");
                    return null;
                }
                int scaleDownSampleSize = Math.min(roundedTrueCrop.width() / outWidth2, roundedTrueCrop.height() / outHeight2);
                BitmapRegionDecoder decoder = null;
                try {
                    decoder = BitmapRegionDecoder.newInstance(is2, true);
                } catch (IOException e) {
                    Log.w(TAG, "cannot open region decoder for default wallpaper");
                }
                Bitmap crop = null;
                if (decoder != null) {
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    if (scaleDownSampleSize > 1) {
                        options2.inSampleSize = scaleDownSampleSize;
                    }
                    crop = decoder.decodeRegion(roundedTrueCrop, options2);
                    decoder.recycle();
                }
                if (crop == null) {
                    InputStream is3 = new BufferedInputStream(resources.openRawResource(R.drawable.default_wallpaper));
                    Bitmap fullSize2 = null;
                    if (is3 != null) {
                        BitmapFactory.Options options3 = new BitmapFactory.Options();
                        if (scaleDownSampleSize > 1) {
                            options3.inSampleSize = scaleDownSampleSize;
                        }
                        fullSize2 = BitmapFactory.decodeStream(is3, null, options3);
                    }
                    if (fullSize2 != null) {
                        crop = Bitmap.createBitmap(fullSize2, roundedTrueCrop.left, roundedTrueCrop.top, roundedTrueCrop.width(), roundedTrueCrop.height());
                    }
                }
                if (crop == null) {
                    Log.w(TAG, "cannot decode default wallpaper");
                    return null;
                }
                if (outWidth2 > 0 && outHeight2 > 0 && (crop.getWidth() != outWidth2 || crop.getHeight() != outHeight2)) {
                    Matrix m = new Matrix();
                    RectF cropRect = new RectF(0.0f, 0.0f, crop.getWidth(), crop.getHeight());
                    RectF returnRect = new RectF(0.0f, 0.0f, outWidth2, outHeight2);
                    m.setRectToRect(cropRect, returnRect, Matrix.ScaleToFit.FILL);
                    Bitmap tmp = Bitmap.createBitmap((int) returnRect.width(), (int) returnRect.height(), Bitmap.Config.ARGB_8888);
                    if (tmp != null) {
                        Canvas c = new Canvas(tmp);
                        Paint p = new Paint();
                        p.setFilterBitmap(true);
                        c.drawBitmap(crop, m, p);
                        crop = tmp;
                    }
                }
                return new BitmapDrawable(resources, crop);
            }
            Log.e(TAG, "default wallpaper dimensions are 0");
            return null;
        }
    }

    private static RectF getMaxCropRect(int inWidth, int inHeight, int outWidth, int outHeight, float horizontalAlignment, float verticalAlignment) {
        RectF cropRect = new RectF();
        if (inWidth / inHeight > outWidth / outHeight) {
            cropRect.top = 0.0f;
            cropRect.bottom = inHeight;
            float cropWidth = outWidth * (inHeight / outHeight);
            cropRect.left = (inWidth - cropWidth) * horizontalAlignment;
            cropRect.right = cropRect.left + cropWidth;
        } else {
            cropRect.left = 0.0f;
            cropRect.right = inWidth;
            float cropHeight = outHeight * (inWidth / outWidth);
            cropRect.top = (inHeight - cropHeight) * verticalAlignment;
            cropRect.bottom = cropRect.top + cropHeight;
        }
        return cropRect;
    }

    public Drawable peekDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, false);
        if (bm != null) {
            Drawable dr = new BitmapDrawable(this.mContext.getResources(), bm);
            dr.setDither(false);
            return dr;
        }
        return null;
    }

    public Drawable getFastDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, true);
        if (bm != null) {
            return new FastBitmapDrawable(bm);
        }
        return null;
    }

    public Drawable peekFastDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, false);
        if (bm != null) {
            return new FastBitmapDrawable(bm);
        }
        return null;
    }

    public Bitmap getBitmap() {
        return sGlobals.peekWallpaperBitmap(this.mContext, true);
    }

    public void forgetLoadedWallpaper() {
        sGlobals.forgetLoadedWallpaper();
    }

    public WallpaperInfo getWallpaperInfo() {
        try {
            if (sGlobals.mService != null) {
                return sGlobals.mService.getWallpaperInfo();
            }
            Log.w(TAG, "WallpaperService not running");
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public Intent getCropAndSetWallpaperIntent(Uri imageUri) {
        if (!"content".equals(imageUri.getScheme())) {
            throw new IllegalArgumentException("Image URI must be of the content scheme type");
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent cropAndSetWallpaperIntent = new Intent(ACTION_CROP_AND_SET_WALLPAPER, imageUri);
        cropAndSetWallpaperIntent.addFlags(1);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolvedHome = packageManager.resolveActivity(homeIntent, 65536);
        if (resolvedHome != null) {
            cropAndSetWallpaperIntent.setPackage(resolvedHome.activityInfo.packageName);
            List<ResolveInfo> cropAppList = packageManager.queryIntentActivities(cropAndSetWallpaperIntent, 0);
            if (cropAppList.size() > 0) {
                return cropAndSetWallpaperIntent;
            }
        }
        cropAndSetWallpaperIntent.setPackage("com.android.wallpapercropper");
        List<ResolveInfo> cropAppList2 = packageManager.queryIntentActivities(cropAndSetWallpaperIntent, 0);
        if (cropAppList2.size() > 0) {
            return cropAndSetWallpaperIntent;
        }
        throw new IllegalArgumentException("Cannot use passed URI to set wallpaper; check that the type returned by ContentProvider matches image/*");
    }

    public void setResource(int resid) throws IOException {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return;
        }
        try {
            Resources resources = this.mContext.getResources();
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper("res:" + resources.getResourceName(resid));
            if (fd != null) {
                FileOutputStream fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                setWallpaper(resources.openRawResource(resid), fos);
                if (fos != null) {
                    fos.close();
                }
            }
        } catch (RemoteException e) {
        }
    }

    public void setBitmap(Bitmap bitmap) throws IOException {
        if (sGlobals.mService != null) {
            try {
                ParcelFileDescriptor fd = sGlobals.mService.setWallpaper(null);
                if (fd == null) {
                    return;
                }
                FileOutputStream fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                if (fos != null) {
                    fos.close();
                }
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        Log.w(TAG, "WallpaperService not running");
    }

    public void setStream(InputStream data) throws IOException {
        if (sGlobals.mService != null) {
            try {
                ParcelFileDescriptor fd = sGlobals.mService.setWallpaper(null);
                if (fd == null) {
                    return;
                }
                FileOutputStream fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                setWallpaper(data, fos);
                if (fos != null) {
                    fos.close();
                }
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        Log.w(TAG, "WallpaperService not running");
    }

    private void setWallpaper(InputStream data, FileOutputStream fos) throws IOException {
        byte[] buffer = new byte[32768];
        while (true) {
            int amt = data.read(buffer);
            if (amt > 0) {
                fos.write(buffer, 0, amt);
            } else {
                return;
            }
        }
    }

    public boolean hasResourceWallpaper(int resid) {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return false;
        }
        try {
            Resources resources = this.mContext.getResources();
            String name = "res:" + resources.getResourceName(resid);
            return sGlobals.mService.hasNamedWallpaper(name);
        } catch (RemoteException e) {
            return false;
        }
    }

    public int getDesiredMinimumWidth() {
        if (sGlobals.mService != null) {
            try {
                return sGlobals.mService.getWidthHint();
            } catch (RemoteException e) {
                return 0;
            }
        }
        Log.w(TAG, "WallpaperService not running");
        return 0;
    }

    public int getDesiredMinimumHeight() {
        if (sGlobals.mService != null) {
            try {
                return sGlobals.mService.getHeightHint();
            } catch (RemoteException e) {
                return 0;
            }
        }
        Log.w(TAG, "WallpaperService not running");
        return 0;
    }

    public void suggestDesiredDimensions(int minimumWidth, int minimumHeight) {
        try {
            if (sGlobals.mService != null) {
                sGlobals.mService.setDimensionHints(minimumWidth, minimumHeight);
            } else {
                Log.w(TAG, "WallpaperService not running");
            }
        } catch (RemoteException e) {
        }
    }

    public void setWallpaperOffsets(IBinder windowToken, float xOffset, float yOffset) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(windowToken, xOffset, yOffset, this.mWallpaperXStep, this.mWallpaperYStep);
        } catch (RemoteException e) {
        }
    }

    public void setWallpaperOffsetSteps(float xStep, float yStep) {
        this.mWallpaperXStep = xStep;
        this.mWallpaperYStep = yStep;
    }

    public void sendWallpaperCommand(IBinder windowToken, String action, int x, int y, int z, Bundle extras) {
        try {
            WindowManagerGlobal.getWindowSession().sendWallpaperCommand(windowToken, action, x, y, z, extras, false);
        } catch (RemoteException e) {
        }
    }

    public void clearWallpaperOffsets(IBinder windowToken) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(windowToken, -1.0f, -1.0f, -1.0f, -1.0f);
        } catch (RemoteException e) {
        }
    }

    public void clear() throws IOException {
        setResource(R.drawable.default_wallpaper);
    }

    static Bitmap generateBitmap(Context context, Bitmap bm, int width, int height) {
        float scale;
        if (bm == null) {
            return null;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        bm.setDensity(metrics.noncompatDensityDpi);
        if (width <= 0 || height <= 0 || (bm.getWidth() == width && bm.getHeight() == height)) {
            return bm;
        }
        try {
            Bitmap newbm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            newbm.setDensity(metrics.noncompatDensityDpi);
            Canvas c = new Canvas(newbm);
            Rect targetRect = new Rect();
            targetRect.right = bm.getWidth();
            targetRect.bottom = bm.getHeight();
            int deltaw = width - targetRect.right;
            int deltah = height - targetRect.bottom;
            if (deltaw > 0 || deltah > 0) {
                if (deltaw > deltah) {
                    scale = width / targetRect.right;
                } else {
                    scale = height / targetRect.bottom;
                }
                targetRect.right = (int) (targetRect.right * scale);
                targetRect.bottom = (int) (targetRect.bottom * scale);
                deltaw = width - targetRect.right;
                deltah = height - targetRect.bottom;
            }
            targetRect.offset(deltaw / 2, deltah / 2);
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            c.drawBitmap(bm, (Rect) null, targetRect, paint);
            bm.recycle();
            c.setBitmap(null);
            return newbm;
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Can't generate default bitmap", e);
            return bm;
        }
    }
}