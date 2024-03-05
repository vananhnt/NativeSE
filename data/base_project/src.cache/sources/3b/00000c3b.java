package android.print;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PrintDocumentAdapter;
import android.util.Log;
import com.android.internal.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;

/* loaded from: PrintFileDocumentAdapter.class */
public class PrintFileDocumentAdapter extends PrintDocumentAdapter {
    private static final String LOG_TAG = "PrintedFileDocumentAdapter";
    private final Context mContext;
    private final File mFile;
    private final PrintDocumentInfo mDocumentInfo;
    private WriteFileAsyncTask mWriteFileAsyncTask;

    public PrintFileDocumentAdapter(Context context, File file, PrintDocumentInfo documentInfo) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null!");
        }
        if (documentInfo == null) {
            throw new IllegalArgumentException("documentInfo cannot be null!");
        }
        this.mContext = context;
        this.mFile = file;
        this.mDocumentInfo = documentInfo;
    }

    @Override // android.print.PrintDocumentAdapter
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback callback, Bundle metadata) {
        callback.onLayoutFinished(this.mDocumentInfo, false);
    }

    @Override // android.print.PrintDocumentAdapter
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback callback) {
        this.mWriteFileAsyncTask = new WriteFileAsyncTask(destination, cancellationSignal, callback);
        this.mWriteFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /* loaded from: PrintFileDocumentAdapter$WriteFileAsyncTask.class */
    private final class WriteFileAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ParcelFileDescriptor mDestination;
        private final PrintDocumentAdapter.WriteResultCallback mResultCallback;
        private final CancellationSignal mCancellationSignal;

        public WriteFileAsyncTask(ParcelFileDescriptor destination, CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback callback) {
            this.mDestination = destination;
            this.mResultCallback = callback;
            this.mCancellationSignal = cancellationSignal;
            this.mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: android.print.PrintFileDocumentAdapter.WriteFileAsyncTask.1
                @Override // android.os.CancellationSignal.OnCancelListener
                public void onCancel() {
                    WriteFileAsyncTask.this.cancel(true);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v1, types: [java.io.OutputStream, java.io.FileOutputStream, java.lang.AutoCloseable] */
        /* JADX WARN: Type inference failed for: r7v0 */
        /* JADX WARN: Type inference failed for: r7v1 */
        /* JADX WARN: Type inference failed for: r7v2 */
        /* JADX WARN: Type inference failed for: r7v3, types: [java.lang.AutoCloseable, java.io.InputStream] */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... params) {
            int readByteCount;
            ?? r7 = 0;
            ?? fileOutputStream = new FileOutputStream(this.mDestination.getFileDescriptor());
            byte[] buffer = new byte[8192];
            try {
                try {
                    r7 = new FileInputStream(PrintFileDocumentAdapter.this.mFile);
                    while (!isCancelled() && (readByteCount = r7.read(buffer)) >= 0) {
                        fileOutputStream.write(buffer, 0, readByteCount);
                    }
                    IoUtils.closeQuietly((AutoCloseable) r7);
                    IoUtils.closeQuietly((AutoCloseable) fileOutputStream);
                    return null;
                } catch (IOException ioe) {
                    Log.e(PrintFileDocumentAdapter.LOG_TAG, "Error writing data!", ioe);
                    this.mResultCallback.onWriteFailed(PrintFileDocumentAdapter.this.mContext.getString(R.string.write_fail_reason_cannot_write));
                    IoUtils.closeQuietly((AutoCloseable) r7);
                    IoUtils.closeQuietly((AutoCloseable) fileOutputStream);
                    return null;
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) r7);
                IoUtils.closeQuietly((AutoCloseable) fileOutputStream);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            this.mResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onCancelled(Void result) {
            this.mResultCallback.onWriteFailed(PrintFileDocumentAdapter.this.mContext.getString(R.string.write_fail_reason_cancelled));
        }
    }
}