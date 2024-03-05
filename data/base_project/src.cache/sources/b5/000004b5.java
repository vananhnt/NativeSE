package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.os.Handler;
import android.os.Looper;

/* loaded from: CallbackFilter.class */
public class CallbackFilter extends Filter {
    @GenerateFieldPort(name = "listener", hasDefault = true)
    private FilterContext.OnFrameReceivedListener mListener;
    @GenerateFieldPort(name = "userData", hasDefault = true)
    private Object mUserData;
    @GenerateFinalPort(name = "callUiThread", hasDefault = true)
    private boolean mCallbacksOnUiThread;
    private Handler mUiThreadHandler;

    /* loaded from: CallbackFilter$CallbackRunnable.class */
    private class CallbackRunnable implements Runnable {
        private Filter mFilter;
        private Frame mFrame;
        private Object mUserData;
        private FilterContext.OnFrameReceivedListener mListener;

        public CallbackRunnable(FilterContext.OnFrameReceivedListener listener, Filter filter, Frame frame, Object userData) {
            this.mListener = listener;
            this.mFilter = filter;
            this.mFrame = frame;
            this.mUserData = userData;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mListener.onFrameReceived(this.mFilter, this.mFrame, this.mUserData);
            this.mFrame.release();
        }
    }

    public CallbackFilter(String name) {
        super(name);
        this.mCallbacksOnUiThread = true;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addInputPort("frame");
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        if (this.mCallbacksOnUiThread) {
            this.mUiThreadHandler = new Handler(Looper.getMainLooper());
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("frame");
        if (this.mListener != null) {
            if (this.mCallbacksOnUiThread) {
                input.retain();
                CallbackRunnable uiRunnable = new CallbackRunnable(this.mListener, this, input, this.mUserData);
                if (!this.mUiThreadHandler.post(uiRunnable)) {
                    throw new RuntimeException("Unable to send callback to UI thread!");
                }
                return;
            }
            this.mListener.onFrameReceived(this, input, this.mUserData);
            return;
        }
        throw new RuntimeException("CallbackFilter received frame, but no listener set!");
    }
}