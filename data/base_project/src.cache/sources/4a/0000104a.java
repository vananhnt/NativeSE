package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.print.PrintHelperKitkat;
import java.io.FileNotFoundException;

/* loaded from: PrintHelper.class */
public final class PrintHelper {
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    PrintHelperVersionImpl mImpl;

    /* loaded from: PrintHelper$OnPrintFinishCallback.class */
    public interface OnPrintFinishCallback {
        void onFinish();
    }

    /* loaded from: PrintHelper$PrintHelperKitkatImpl.class */
    private static final class PrintHelperKitkatImpl implements PrintHelperVersionImpl {
        private final PrintHelperKitkat mPrintHelper;

        PrintHelperKitkatImpl(Context context) {
            this.mPrintHelper = new PrintHelperKitkat(context);
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getColorMode() {
            return this.mPrintHelper.getColorMode();
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getOrientation() {
            return this.mPrintHelper.getOrientation();
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getScaleMode() {
            return this.mPrintHelper.getScaleMode();
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
            this.mPrintHelper.printBitmap(str, bitmap, onPrintFinishCallback != null ? new PrintHelperKitkat.OnPrintFinishCallback(this, onPrintFinishCallback) { // from class: android.support.v4.print.PrintHelper.PrintHelperKitkatImpl.1
                final PrintHelperKitkatImpl this$0;
                final OnPrintFinishCallback val$callback;

                {
                    this.this$0 = this;
                    this.val$callback = onPrintFinishCallback;
                }

                @Override // android.support.v4.print.PrintHelperKitkat.OnPrintFinishCallback
                public void onFinish() {
                    this.val$callback.onFinish();
                }
            } : null);
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
            this.mPrintHelper.printBitmap(str, uri, onPrintFinishCallback != null ? new PrintHelperKitkat.OnPrintFinishCallback(this, onPrintFinishCallback) { // from class: android.support.v4.print.PrintHelper.PrintHelperKitkatImpl.2
                final PrintHelperKitkatImpl this$0;
                final OnPrintFinishCallback val$callback;

                {
                    this.this$0 = this;
                    this.val$callback = onPrintFinishCallback;
                }

                @Override // android.support.v4.print.PrintHelperKitkat.OnPrintFinishCallback
                public void onFinish() {
                    this.val$callback.onFinish();
                }
            } : null);
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setColorMode(int i) {
            this.mPrintHelper.setColorMode(i);
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setOrientation(int i) {
            this.mPrintHelper.setOrientation(i);
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setScaleMode(int i) {
            this.mPrintHelper.setScaleMode(i);
        }
    }

    /* loaded from: PrintHelper$PrintHelperStubImpl.class */
    private static final class PrintHelperStubImpl implements PrintHelperVersionImpl {
        int mColorMode;
        int mOrientation;
        int mScaleMode;

        private PrintHelperStubImpl() {
            this.mScaleMode = 2;
            this.mColorMode = 2;
            this.mOrientation = 1;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getColorMode() {
            return this.mColorMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getOrientation() {
            return this.mOrientation;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public int getScaleMode() {
            return this.mScaleMode;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) {
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setColorMode(int i) {
            this.mColorMode = i;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setOrientation(int i) {
            this.mOrientation = i;
        }

        @Override // android.support.v4.print.PrintHelper.PrintHelperVersionImpl
        public void setScaleMode(int i) {
            this.mScaleMode = i;
        }
    }

    /* loaded from: PrintHelper$PrintHelperVersionImpl.class */
    interface PrintHelperVersionImpl {
        int getColorMode();

        int getOrientation();

        int getScaleMode();

        void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback);

        void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException;

        void setColorMode(int i);

        void setOrientation(int i);

        void setScaleMode(int i);
    }

    public PrintHelper(Context context) {
        if (systemSupportsPrint()) {
            this.mImpl = new PrintHelperKitkatImpl(context);
        } else {
            this.mImpl = new PrintHelperStubImpl();
        }
    }

    public static boolean systemSupportsPrint() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public int getColorMode() {
        return this.mImpl.getColorMode();
    }

    public int getOrientation() {
        return this.mImpl.getOrientation();
    }

    public int getScaleMode() {
        return this.mImpl.getScaleMode();
    }

    public void printBitmap(String str, Bitmap bitmap) {
        this.mImpl.printBitmap(str, bitmap, (OnPrintFinishCallback) null);
    }

    public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
        this.mImpl.printBitmap(str, bitmap, onPrintFinishCallback);
    }

    public void printBitmap(String str, Uri uri) throws FileNotFoundException {
        this.mImpl.printBitmap(str, uri, (OnPrintFinishCallback) null);
    }

    public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
        this.mImpl.printBitmap(str, uri, onPrintFinishCallback);
    }

    public void setColorMode(int i) {
        this.mImpl.setColorMode(i);
    }

    public void setOrientation(int i) {
        this.mImpl.setOrientation(i);
    }

    public void setScaleMode(int i) {
        this.mImpl.setScaleMode(i);
    }
}