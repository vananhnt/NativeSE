package android.widget;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/* loaded from: Filter.class */
public abstract class Filter {
    private static final String LOG_TAG = "Filter";
    private static final String THREAD_NAME = "Filter";
    private static final int FILTER_TOKEN = -791613427;
    private static final int FINISH_TOKEN = -559038737;
    private Handler mThreadHandler;
    private Delayer mDelayer;
    private final Object mLock = new Object();
    private Handler mResultHandler = new ResultsHandler();

    /* loaded from: Filter$Delayer.class */
    public interface Delayer {
        long getPostingDelay(CharSequence charSequence);
    }

    /* loaded from: Filter$FilterListener.class */
    public interface FilterListener {
        void onFilterComplete(int i);
    }

    /* loaded from: Filter$FilterResults.class */
    protected static class FilterResults {
        public Object values;
        public int count;
    }

    protected abstract FilterResults performFiltering(CharSequence charSequence);

    protected abstract void publishResults(CharSequence charSequence, FilterResults filterResults);

    public void setDelayer(Delayer delayer) {
        synchronized (this.mLock) {
            this.mDelayer = delayer;
        }
    }

    public final void filter(CharSequence constraint) {
        filter(constraint, null);
    }

    public final void filter(CharSequence constraint, FilterListener listener) {
        synchronized (this.mLock) {
            if (this.mThreadHandler == null) {
                HandlerThread thread = new HandlerThread("Filter", 10);
                thread.start();
                this.mThreadHandler = new RequestHandler(thread.getLooper());
            }
            long delay = this.mDelayer == null ? 0L : this.mDelayer.getPostingDelay(constraint);
            Message message = this.mThreadHandler.obtainMessage(FILTER_TOKEN);
            RequestArguments args = new RequestArguments();
            args.constraint = constraint != null ? constraint.toString() : null;
            args.listener = listener;
            message.obj = args;
            this.mThreadHandler.removeMessages(FILTER_TOKEN);
            this.mThreadHandler.removeMessages(FINISH_TOKEN);
            this.mThreadHandler.sendMessageDelayed(message, delay);
        }
    }

    public CharSequence convertResultToString(Object resultValue) {
        return resultValue == null ? "" : resultValue.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Filter$RequestHandler.class */
    public class RequestHandler extends Handler {
        public RequestHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case Filter.FILTER_TOKEN /* -791613427 */:
                    RequestArguments args = (RequestArguments) msg.obj;
                    try {
                        try {
                            args.results = Filter.this.performFiltering(args.constraint);
                            Message message = Filter.this.mResultHandler.obtainMessage(what);
                            message.obj = args;
                            message.sendToTarget();
                        } catch (Exception e) {
                            args.results = new FilterResults();
                            Log.w("Filter", "An exception occured during performFiltering()!", e);
                            Message message2 = Filter.this.mResultHandler.obtainMessage(what);
                            message2.obj = args;
                            message2.sendToTarget();
                        }
                        synchronized (Filter.this.mLock) {
                            if (Filter.this.mThreadHandler != null) {
                                Message finishMessage = Filter.this.mThreadHandler.obtainMessage(Filter.FINISH_TOKEN);
                                Filter.this.mThreadHandler.sendMessageDelayed(finishMessage, 3000L);
                            }
                        }
                        return;
                    } catch (Throwable th) {
                        Message message3 = Filter.this.mResultHandler.obtainMessage(what);
                        message3.obj = args;
                        message3.sendToTarget();
                        throw th;
                    }
                case Filter.FINISH_TOKEN /* -559038737 */:
                    synchronized (Filter.this.mLock) {
                        if (Filter.this.mThreadHandler != null) {
                            Filter.this.mThreadHandler.getLooper().quit();
                            Filter.this.mThreadHandler = null;
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: Filter$ResultsHandler.class */
    private class ResultsHandler extends Handler {
        private ResultsHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            RequestArguments args = (RequestArguments) msg.obj;
            Filter.this.publishResults(args.constraint, args.results);
            if (args.listener != null) {
                int count = args.results != null ? args.results.count : -1;
                args.listener.onFilterComplete(count);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Filter$RequestArguments.class */
    public static class RequestArguments {
        CharSequence constraint;
        FilterListener listener;
        FilterResults results;

        private RequestArguments() {
        }
    }
}