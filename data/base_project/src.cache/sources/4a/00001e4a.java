package com.android.server.display;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.util.DumpUtils;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: OverlayDisplayWindow.class */
public final class OverlayDisplayWindow implements DumpUtils.Dump {
    private static final String TAG = "OverlayDisplayWindow";
    private static final boolean DEBUG = false;
    private final Context mContext;
    private final String mName;
    private final int mWidth;
    private final int mHeight;
    private final int mDensityDpi;
    private final int mGravity;
    private final boolean mSecure;
    private final Listener mListener;
    private String mTitle;
    private final DisplayManager mDisplayManager;
    private final WindowManager mWindowManager;
    private final Display mDefaultDisplay;
    private View mWindowContent;
    private WindowManager.LayoutParams mWindowParams;
    private TextureView mTextureView;
    private TextView mTitleTextView;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mWindowVisible;
    private int mWindowX;
    private int mWindowY;
    private float mWindowScale;
    private float mLiveTranslationX;
    private float mLiveTranslationY;
    private final float INITIAL_SCALE = 0.5f;
    private final float MIN_SCALE = 0.3f;
    private final float MAX_SCALE = 1.0f;
    private final float WINDOW_ALPHA = 0.8f;
    private final boolean DISABLE_MOVE_AND_RESIZE = false;
    private final DisplayInfo mDefaultDisplayInfo = new DisplayInfo();
    private float mLiveScale = 1.0f;
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.server.display.OverlayDisplayWindow.1
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int displayId) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int displayId) {
            if (displayId == OverlayDisplayWindow.this.mDefaultDisplay.getDisplayId()) {
                if (OverlayDisplayWindow.this.updateDefaultDisplayInfo()) {
                    OverlayDisplayWindow.this.relayout();
                } else {
                    OverlayDisplayWindow.this.dismiss();
                }
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int displayId) {
            if (displayId == OverlayDisplayWindow.this.mDefaultDisplay.getDisplayId()) {
                OverlayDisplayWindow.this.dismiss();
            }
        }
    };
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.server.display.OverlayDisplayWindow.2
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            OverlayDisplayWindow.this.mListener.onWindowCreated(surfaceTexture, OverlayDisplayWindow.this.mDefaultDisplayInfo.refreshRate);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            OverlayDisplayWindow.this.mListener.onWindowDestroyed();
            return true;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };
    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() { // from class: com.android.server.display.OverlayDisplayWindow.3
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent event) {
            float oldX = event.getX();
            float oldY = event.getY();
            event.setLocation(event.getRawX(), event.getRawY());
            OverlayDisplayWindow.this.mGestureDetector.onTouchEvent(event);
            OverlayDisplayWindow.this.mScaleGestureDetector.onTouchEvent(event);
            switch (event.getActionMasked()) {
                case 1:
                case 3:
                    OverlayDisplayWindow.this.saveWindowParams();
                    break;
            }
            event.setLocation(oldX, oldY);
            return true;
        }
    };
    private final GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.android.server.display.OverlayDisplayWindow.4
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            OverlayDisplayWindow.access$724(OverlayDisplayWindow.this, distanceX);
            OverlayDisplayWindow.access$824(OverlayDisplayWindow.this, distanceY);
            OverlayDisplayWindow.this.relayout();
            return true;
        }
    };
    private final ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.android.server.display.OverlayDisplayWindow.5
        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            OverlayDisplayWindow.access$932(OverlayDisplayWindow.this, detector.getScaleFactor());
            OverlayDisplayWindow.this.relayout();
            return true;
        }
    };

    /* loaded from: OverlayDisplayWindow$Listener.class */
    public interface Listener {
        void onWindowCreated(SurfaceTexture surfaceTexture, float f);

        void onWindowDestroyed();
    }

    static /* synthetic */ float access$724(OverlayDisplayWindow x0, float x1) {
        float f = x0.mLiveTranslationX - x1;
        x0.mLiveTranslationX = f;
        return f;
    }

    static /* synthetic */ float access$824(OverlayDisplayWindow x0, float x1) {
        float f = x0.mLiveTranslationY - x1;
        x0.mLiveTranslationY = f;
        return f;
    }

    static /* synthetic */ float access$932(OverlayDisplayWindow x0, float x1) {
        float f = x0.mLiveScale * x1;
        x0.mLiveScale = f;
        return f;
    }

    public OverlayDisplayWindow(Context context, String name, int width, int height, int densityDpi, int gravity, boolean secure, Listener listener) {
        this.mContext = context;
        this.mName = name;
        this.mWidth = width;
        this.mHeight = height;
        this.mDensityDpi = densityDpi;
        this.mGravity = gravity;
        this.mSecure = secure;
        this.mListener = listener;
        this.mTitle = context.getResources().getString(R.string.display_manager_overlay_display_title, this.mName, Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mDensityDpi));
        if (secure) {
            this.mTitle += context.getResources().getString(R.string.display_manager_overlay_display_secure_suffix);
        }
        this.mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mDefaultDisplay = this.mWindowManager.getDefaultDisplay();
        updateDefaultDisplayInfo();
        createWindow();
    }

    public void show() {
        if (!this.mWindowVisible) {
            this.mDisplayManager.registerDisplayListener(this.mDisplayListener, null);
            if (!updateDefaultDisplayInfo()) {
                this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
                return;
            }
            clearLiveState();
            updateWindowParams();
            this.mWindowManager.addView(this.mWindowContent, this.mWindowParams);
            this.mWindowVisible = true;
        }
    }

    public void dismiss() {
        if (this.mWindowVisible) {
            this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
            this.mWindowManager.removeView(this.mWindowContent);
            this.mWindowVisible = false;
        }
    }

    public void relayout() {
        if (this.mWindowVisible) {
            updateWindowParams();
            this.mWindowManager.updateViewLayout(this.mWindowContent, this.mWindowParams);
        }
    }

    @Override // com.android.internal.util.DumpUtils.Dump
    public void dump(PrintWriter pw) {
        pw.println("mWindowVisible=" + this.mWindowVisible);
        pw.println("mWindowX=" + this.mWindowX);
        pw.println("mWindowY=" + this.mWindowY);
        pw.println("mWindowScale=" + this.mWindowScale);
        pw.println("mWindowParams=" + this.mWindowParams);
        if (this.mTextureView != null) {
            pw.println("mTextureView.getScaleX()=" + this.mTextureView.getScaleX());
            pw.println("mTextureView.getScaleY()=" + this.mTextureView.getScaleY());
        }
        pw.println("mLiveTranslationX=" + this.mLiveTranslationX);
        pw.println("mLiveTranslationY=" + this.mLiveTranslationY);
        pw.println("mLiveScale=" + this.mLiveScale);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateDefaultDisplayInfo() {
        if (!this.mDefaultDisplay.getDisplayInfo(this.mDefaultDisplayInfo)) {
            Slog.w(TAG, "Cannot show overlay display because there is no default display upon which to show it.");
            return false;
        }
        return true;
    }

    private void createWindow() {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        this.mWindowContent = inflater.inflate(R.layout.overlay_display_window, (ViewGroup) null);
        this.mWindowContent.setOnTouchListener(this.mOnTouchListener);
        this.mTextureView = (TextureView) this.mWindowContent.findViewById(R.id.overlay_display_window_texture);
        this.mTextureView.setPivotX(0.0f);
        this.mTextureView.setPivotY(0.0f);
        this.mTextureView.getLayoutParams().width = this.mWidth;
        this.mTextureView.getLayoutParams().height = this.mHeight;
        this.mTextureView.setOpaque(false);
        this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
        this.mTitleTextView = (TextView) this.mWindowContent.findViewById(R.id.overlay_display_window_title);
        this.mTitleTextView.setText(this.mTitle);
        this.mWindowParams = new WindowManager.LayoutParams((int) WindowManager.LayoutParams.TYPE_DISPLAY_OVERLAY);
        this.mWindowParams.flags |= 16778024;
        if (this.mSecure) {
            this.mWindowParams.flags |= 8192;
        }
        this.mWindowParams.privateFlags |= 2;
        this.mWindowParams.alpha = 0.8f;
        this.mWindowParams.gravity = 51;
        this.mWindowParams.setTitle(this.mTitle);
        this.mGestureDetector = new GestureDetector(this.mContext, this.mOnGestureListener);
        this.mScaleGestureDetector = new ScaleGestureDetector(this.mContext, this.mOnScaleGestureListener);
        this.mWindowX = (this.mGravity & 3) == 3 ? 0 : this.mDefaultDisplayInfo.logicalWidth;
        this.mWindowY = (this.mGravity & 48) == 48 ? 0 : this.mDefaultDisplayInfo.logicalHeight;
        this.mWindowScale = 0.5f;
    }

    private void updateWindowParams() {
        float scale = Math.max(0.3f, Math.min(1.0f, Math.min(Math.min(this.mWindowScale * this.mLiveScale, this.mDefaultDisplayInfo.logicalWidth / this.mWidth), this.mDefaultDisplayInfo.logicalHeight / this.mHeight)));
        float offsetScale = ((scale / this.mWindowScale) - 1.0f) * 0.5f;
        int width = (int) (this.mWidth * scale);
        int height = (int) (this.mHeight * scale);
        int x = (int) ((this.mWindowX + this.mLiveTranslationX) - (width * offsetScale));
        int y = (int) ((this.mWindowY + this.mLiveTranslationY) - (height * offsetScale));
        int x2 = Math.max(0, Math.min(x, this.mDefaultDisplayInfo.logicalWidth - width));
        int y2 = Math.max(0, Math.min(y, this.mDefaultDisplayInfo.logicalHeight - height));
        this.mTextureView.setScaleX(scale);
        this.mTextureView.setScaleY(scale);
        this.mWindowParams.x = x2;
        this.mWindowParams.y = y2;
        this.mWindowParams.width = width;
        this.mWindowParams.height = height;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveWindowParams() {
        this.mWindowX = this.mWindowParams.x;
        this.mWindowY = this.mWindowParams.y;
        this.mWindowScale = this.mTextureView.getScaleX();
        clearLiveState();
    }

    private void clearLiveState() {
        this.mLiveTranslationX = 0.0f;
        this.mLiveTranslationY = 0.0f;
        this.mLiveScale = 1.0f;
    }
}