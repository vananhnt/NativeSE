package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PrintHelperKitkat.class */
public class PrintHelperKitkat {
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    private static final String LOG_TAG = "PrintHelperKitkat";
    private static final int MAX_PRINT_SIZE = 3500;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    final Context mContext;
    BitmapFactory.Options mDecodeOptions = null;
    private final Object mLock = new Object();
    int mScaleMode = 2;
    int mColorMode = 2;
    int mOrientation = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.v4.print.PrintHelperKitkat$2  reason: invalid class name */
    /* loaded from: PrintHelperKitkat$2.class */
    public class AnonymousClass2 extends PrintDocumentAdapter {
        AsyncTask<Uri, Boolean, Bitmap> loadBitmap;
        private PrintAttributes mAttributes;
        Bitmap mBitmap = null;
        final PrintHelperKitkat this$0;
        final OnPrintFinishCallback val$callback;
        final int val$fittingMode;
        final Uri val$imageFile;
        final String val$jobName;

        /* renamed from: android.support.v4.print.PrintHelperKitkat$2$1  reason: invalid class name */
        /* loaded from: PrintHelperKitkat$2$1.class */
        class AnonymousClass1 extends AsyncTask<Uri, Boolean, Bitmap> {
            final AnonymousClass2 this$1;
            final CancellationSignal val$cancellationSignal;
            final PrintDocumentAdapter.LayoutResultCallback val$layoutResultCallback;
            final PrintAttributes val$newPrintAttributes;
            final PrintAttributes val$oldPrintAttributes;

            AnonymousClass1(AnonymousClass2 anonymousClass2, CancellationSignal cancellationSignal, PrintAttributes printAttributes, PrintAttributes printAttributes2, PrintDocumentAdapter.LayoutResultCallback layoutResultCallback) {
                this.this$1 = anonymousClass2;
                this.val$cancellationSignal = cancellationSignal;
                this.val$newPrintAttributes = printAttributes;
                this.val$oldPrintAttributes = printAttributes2;
                this.val$layoutResultCallback = layoutResultCallback;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Bitmap doInBackground(Uri... uriArr) {
                try {
                    return this.this$1.this$0.loadConstrainedBitmap(this.this$1.val$imageFile, PrintHelperKitkat.MAX_PRINT_SIZE);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onCancelled(Bitmap bitmap) {
                this.val$layoutResultCallback.onLayoutCancelled();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Bitmap bitmap) {
                super.onPostExecute((AnonymousClass1) bitmap);
                AnonymousClass2 anonymousClass2 = this.this$1;
                anonymousClass2.mBitmap = bitmap;
                if (bitmap == null) {
                    this.val$layoutResultCallback.onLayoutFailed(null);
                    return;
                }
                this.val$layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(anonymousClass2.val$jobName).setContentType(1).setPageCount(1).build(), true ^ this.val$newPrintAttributes.equals(this.val$oldPrintAttributes));
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                this.val$cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener(this) { // from class: android.support.v4.print.PrintHelperKitkat.2.1.1
                    final AnonymousClass1 this$2;

                    {
                        this.this$2 = this;
                    }

                    @Override // android.os.CancellationSignal.OnCancelListener
                    public void onCancel() {
                        this.this$2.this$1.cancelLoad();
                        this.this$2.cancel(false);
                    }
                });
            }
        }

        AnonymousClass2(PrintHelperKitkat printHelperKitkat, String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback, int i) {
            this.this$0 = printHelperKitkat;
            this.val$jobName = str;
            this.val$imageFile = uri;
            this.val$callback = onPrintFinishCallback;
            this.val$fittingMode = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancelLoad() {
            synchronized (this.this$0.mLock) {
                if (this.this$0.mDecodeOptions != null) {
                    this.this$0.mDecodeOptions.requestCancelDecode();
                    this.this$0.mDecodeOptions = null;
                }
            }
        }

        @Override // android.print.PrintDocumentAdapter
        public void onFinish() {
            super.onFinish();
            cancelLoad();
            this.loadBitmap.cancel(true);
            OnPrintFinishCallback onPrintFinishCallback = this.val$callback;
            if (onPrintFinishCallback != null) {
                onPrintFinishCallback.onFinish();
            }
        }

        @Override // android.print.PrintDocumentAdapter
        public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {
            if (cancellationSignal.isCanceled()) {
                layoutResultCallback.onLayoutCancelled();
                this.mAttributes = printAttributes2;
            } else if (this.mBitmap != null) {
                layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(this.val$jobName).setContentType(1).setPageCount(1).build(), true ^ printAttributes2.equals(printAttributes));
            } else {
                this.loadBitmap = new AnonymousClass1(this, cancellationSignal, printAttributes2, printAttributes, layoutResultCallback);
                this.loadBitmap.execute(new Uri[0]);
                this.mAttributes = printAttributes2;
            }
        }

        @Override // android.print.PrintDocumentAdapter
        public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
            PrintedPdfDocument printedPdfDocument = new PrintedPdfDocument(this.this$0.mContext, this.mAttributes);
            try {
                PdfDocument.Page startPage = printedPdfDocument.startPage(1);
                startPage.getCanvas().drawBitmap(this.mBitmap, this.this$0.getMatrix(this.mBitmap.getWidth(), this.mBitmap.getHeight(), new RectF(startPage.getInfo().getContentRect()), this.val$fittingMode), null);
                printedPdfDocument.finishPage(startPage);
                try {
                    printedPdfDocument.writeTo(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
                    writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                } catch (IOException e) {
                    Log.e(PrintHelperKitkat.LOG_TAG, "Error writing printed content", e);
                    writeResultCallback.onWriteFailed(null);
                }
                printedPdfDocument.close();
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Throwable th) {
                printedPdfDocument.close();
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e3) {
                    }
                }
                throw th;
            }
        }
    }

    /* loaded from: PrintHelperKitkat$OnPrintFinishCallback.class */
    public interface OnPrintFinishCallback {
        void onFinish();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrintHelperKitkat(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Matrix getMatrix(int i, int i2, RectF rectF, int i3) {
        Matrix matrix = new Matrix();
        float width = rectF.width() / i;
        float max = i3 == 2 ? Math.max(width, rectF.height() / i2) : Math.min(width, rectF.height() / i2);
        matrix.postScale(max, max);
        matrix.postTranslate((rectF.width() - (i * max)) / 2.0f, (rectF.height() - (i2 * max)) / 2.0f);
        return matrix;
    }

    private Bitmap loadBitmap(Uri uri, BitmapFactory.Options options) throws FileNotFoundException {
        Context context;
        if (uri == null || (context = this.mContext) == null) {
            throw new IllegalArgumentException("bad argument to loadBitmap");
        }
        InputStream inputStream = null;
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            inputStream = openInputStream;
            Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream, null, options);
            if (openInputStream != null) {
                try {
                    openInputStream.close();
                } catch (IOException e) {
                    Log.w(LOG_TAG, "close fail ", e);
                }
            }
            return decodeStream;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                    Log.w(LOG_TAG, "close fail ", e2);
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap loadConstrainedBitmap(Uri uri, int i) throws FileNotFoundException {
        int i2;
        BitmapFactory.Options options;
        if (i <= 0 || uri == null || this.mContext == null) {
            throw new IllegalArgumentException("bad argument to getScaledBitmap");
        }
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inJustDecodeBounds = true;
        loadBitmap(uri, options2);
        int i3 = options2.outWidth;
        int i4 = options2.outHeight;
        if (i3 <= 0 || i4 <= 0) {
            return null;
        }
        int max = Math.max(i3, i4);
        int i5 = 1;
        while (true) {
            i2 = i5;
            if (max <= i) {
                break;
            }
            max >>>= 1;
            i5 = i2 << 1;
        }
        if (i2 <= 0 || Math.min(i3, i4) / i2 <= 0) {
            return null;
        }
        synchronized (this.mLock) {
            this.mDecodeOptions = new BitmapFactory.Options();
            this.mDecodeOptions.inMutable = true;
            this.mDecodeOptions.inSampleSize = i2;
            options = this.mDecodeOptions;
        }
        try {
            Bitmap loadBitmap = loadBitmap(uri, options);
            synchronized (this.mLock) {
                this.mDecodeOptions = null;
            }
            return loadBitmap;
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mDecodeOptions = null;
                throw th;
            }
        }
    }

    public int getColorMode() {
        return this.mColorMode;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public int getScaleMode() {
        return this.mScaleMode;
    }

    public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
        if (bitmap == null) {
            return;
        }
        int i = this.mScaleMode;
        PrintManager printManager = (PrintManager) this.mContext.getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.MediaSize mediaSize = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            mediaSize = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
        }
        printManager.print(str, new PrintDocumentAdapter(this, str, bitmap, i, onPrintFinishCallback) { // from class: android.support.v4.print.PrintHelperKitkat.1
            private PrintAttributes mAttributes;
            final PrintHelperKitkat this$0;
            final Bitmap val$bitmap;
            final OnPrintFinishCallback val$callback;
            final int val$fittingMode;
            final String val$jobName;

            {
                this.this$0 = this;
                this.val$jobName = str;
                this.val$bitmap = bitmap;
                this.val$fittingMode = i;
                this.val$callback = onPrintFinishCallback;
            }

            @Override // android.print.PrintDocumentAdapter
            public void onFinish() {
                OnPrintFinishCallback onPrintFinishCallback2 = this.val$callback;
                if (onPrintFinishCallback2 != null) {
                    onPrintFinishCallback2.onFinish();
                }
            }

            @Override // android.print.PrintDocumentAdapter
            public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback layoutResultCallback, Bundle bundle) {
                this.mAttributes = printAttributes2;
                layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(this.val$jobName).setContentType(1).setPageCount(1).build(), true ^ printAttributes2.equals(printAttributes));
            }

            @Override // android.print.PrintDocumentAdapter
            public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
                PrintedPdfDocument printedPdfDocument = new PrintedPdfDocument(this.this$0.mContext, this.mAttributes);
                try {
                    PdfDocument.Page startPage = printedPdfDocument.startPage(1);
                    startPage.getCanvas().drawBitmap(this.val$bitmap, this.this$0.getMatrix(this.val$bitmap.getWidth(), this.val$bitmap.getHeight(), new RectF(startPage.getInfo().getContentRect()), this.val$fittingMode), null);
                    printedPdfDocument.finishPage(startPage);
                    try {
                        printedPdfDocument.writeTo(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
                        writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (IOException e) {
                        Log.e(PrintHelperKitkat.LOG_TAG, "Error writing printed content", e);
                        writeResultCallback.onWriteFailed(null);
                    }
                    printedPdfDocument.close();
                    if (parcelFileDescriptor != null) {
                        try {
                            parcelFileDescriptor.close();
                        } catch (IOException e2) {
                        }
                    }
                } catch (Throwable th) {
                    printedPdfDocument.close();
                    if (parcelFileDescriptor != null) {
                        try {
                            parcelFileDescriptor.close();
                        } catch (IOException e3) {
                        }
                    }
                    throw th;
                }
            }
        }, new PrintAttributes.Builder().setMediaSize(mediaSize).setColorMode(this.mColorMode).build());
    }

    public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
        AnonymousClass2 anonymousClass2 = new AnonymousClass2(this, str, uri, onPrintFinishCallback, this.mScaleMode);
        PrintManager printManager = (PrintManager) this.mContext.getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(this.mColorMode);
        int i = this.mOrientation;
        if (i == 1) {
            builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
        } else if (i == 2) {
            builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
        }
        printManager.print(str, anonymousClass2, builder.build());
    }

    public void setColorMode(int i) {
        this.mColorMode = i;
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
    }

    public void setScaleMode(int i) {
        this.mScaleMode = i;
    }
}