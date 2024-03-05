package android.media;

import android.content.Context;
import android.media.SubtitleTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.accessibility.CaptioningManager;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/* loaded from: SubtitleController.class */
public class SubtitleController {
    private MediaTimeProvider mTimeProvider;
    private SubtitleTrack mSelectedTrack;
    private CaptioningManager mCaptioningManager;
    private Handler mHandler;
    private static final int WHAT_SHOW = 1;
    private static final int WHAT_HIDE = 2;
    private static final int WHAT_SELECT_TRACK = 3;
    private static final int WHAT_SELECT_DEFAULT_TRACK = 4;
    private Anchor mAnchor;
    private Listener mListener;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final Handler.Callback mCallback = new Handler.Callback() { // from class: android.media.SubtitleController.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SubtitleController.this.doShow();
                    return true;
                case 2:
                    SubtitleController.this.doHide();
                    return true;
                case 3:
                    SubtitleController.this.doSelectTrack((SubtitleTrack) msg.obj);
                    return true;
                case 4:
                    SubtitleController.this.doSelectDefaultTrack();
                    return true;
                default:
                    return false;
            }
        }
    };
    private CaptioningManager.CaptioningChangeListener mCaptioningChangeListener = new CaptioningManager.CaptioningChangeListener() { // from class: android.media.SubtitleController.2
        @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
        public void onEnabledChanged(boolean enabled) {
            SubtitleController.this.selectDefaultTrack();
        }

        @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
        public void onLocaleChanged(Locale locale) {
            SubtitleController.this.selectDefaultTrack();
        }
    };
    private boolean mTrackIsExplicit = false;
    private boolean mVisibilityIsExplicit = false;
    private Vector<Renderer> mRenderers = new Vector<>();
    private boolean mShowing = false;
    private Vector<SubtitleTrack> mTracks = new Vector<>();

    /* loaded from: SubtitleController$Anchor.class */
    public interface Anchor {
        void setSubtitleWidget(SubtitleTrack.RenderingWidget renderingWidget);

        Looper getSubtitleLooper();
    }

    /* loaded from: SubtitleController$Listener.class */
    public interface Listener {
        void onSubtitleTrackSelected(SubtitleTrack subtitleTrack);
    }

    /* loaded from: SubtitleController$Renderer.class */
    public static abstract class Renderer {
        public abstract boolean supports(MediaFormat mediaFormat);

        public abstract SubtitleTrack createTrack(MediaFormat mediaFormat);
    }

    static {
        $assertionsDisabled = !SubtitleController.class.desiredAssertionStatus();
    }

    public SubtitleController(Context context, MediaTimeProvider timeProvider, Listener listener) {
        this.mTimeProvider = timeProvider;
        this.mListener = listener;
        this.mCaptioningManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
    }

    protected void finalize() throws Throwable {
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
        super.finalize();
    }

    public SubtitleTrack[] getTracks() {
        SubtitleTrack[] tracks;
        synchronized (this.mTracks) {
            tracks = new SubtitleTrack[this.mTracks.size()];
            this.mTracks.toArray(tracks);
        }
        return tracks;
    }

    public SubtitleTrack getSelectedTrack() {
        return this.mSelectedTrack;
    }

    private SubtitleTrack.RenderingWidget getRenderingWidget() {
        if (this.mSelectedTrack == null) {
            return null;
        }
        return this.mSelectedTrack.getRenderingWidget();
    }

    public boolean selectTrack(SubtitleTrack track) {
        if (track != null && !this.mTracks.contains(track)) {
            return false;
        }
        processOnAnchor(this.mHandler.obtainMessage(3, track));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSelectTrack(SubtitleTrack track) {
        this.mTrackIsExplicit = true;
        if (this.mSelectedTrack == track) {
            return;
        }
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.hide();
            this.mSelectedTrack.setTimeProvider(null);
        }
        this.mSelectedTrack = track;
        if (this.mAnchor != null) {
            this.mAnchor.setSubtitleWidget(getRenderingWidget());
        }
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.setTimeProvider(this.mTimeProvider);
            this.mSelectedTrack.show();
        }
        if (this.mListener != null) {
            this.mListener.onSubtitleTrackSelected(track);
        }
    }

    public SubtitleTrack getDefaultTrack() {
        SubtitleTrack bestTrack = null;
        int bestScore = -1;
        Locale selectedLocale = this.mCaptioningManager.getLocale();
        Locale locale = selectedLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        boolean selectForced = !this.mCaptioningManager.isEnabled();
        synchronized (this.mTracks) {
            Iterator i$ = this.mTracks.iterator();
            while (i$.hasNext()) {
                SubtitleTrack track = i$.next();
                MediaFormat format = track.getFormat();
                String language = format.getString("language");
                boolean forced = format.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, 0) != 0;
                boolean autoselect = format.getInteger(MediaFormat.KEY_IS_AUTOSELECT, 1) != 0;
                boolean is_default = format.getInteger(MediaFormat.KEY_IS_DEFAULT, 0) != 0;
                boolean languageMatches = locale == null || locale.getLanguage().equals("") || locale.getISO3Language().equals(language) || locale.getLanguage().equals(language);
                int score = (forced ? 0 : 8) + ((selectedLocale == null && is_default) ? 4 : 0) + (autoselect ? 0 : 2) + (languageMatches ? 1 : 0);
                if (!selectForced || forced) {
                    if (((selectedLocale == null && is_default) || (languageMatches && (autoselect || forced || selectedLocale != null))) && score > bestScore) {
                        bestScore = score;
                        bestTrack = track;
                    }
                }
            }
        }
        return bestTrack;
    }

    public void selectDefaultTrack() {
        processOnAnchor(this.mHandler.obtainMessage(4));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSelectDefaultTrack() {
        if (this.mTrackIsExplicit) {
            if (!this.mVisibilityIsExplicit) {
                if (this.mCaptioningManager.isEnabled() || (this.mSelectedTrack != null && this.mSelectedTrack.getFormat().getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, 0) != 0)) {
                    show();
                } else {
                    hide();
                }
                this.mVisibilityIsExplicit = false;
                return;
            }
            return;
        }
        SubtitleTrack track = getDefaultTrack();
        if (track != null) {
            selectTrack(track);
            this.mTrackIsExplicit = false;
            if (!this.mVisibilityIsExplicit) {
                show();
                this.mVisibilityIsExplicit = false;
            }
        }
    }

    public void reset() {
        checkAnchorLooper();
        hide();
        selectTrack(null);
        this.mTracks.clear();
        this.mTrackIsExplicit = false;
        this.mVisibilityIsExplicit = false;
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
    }

    public SubtitleTrack addTrack(MediaFormat format) {
        SubtitleTrack track;
        synchronized (this.mRenderers) {
            Iterator i$ = this.mRenderers.iterator();
            while (i$.hasNext()) {
                Renderer renderer = i$.next();
                if (renderer.supports(format) && (track = renderer.createTrack(format)) != null) {
                    synchronized (this.mTracks) {
                        if (this.mTracks.size() == 0) {
                            this.mCaptioningManager.addCaptioningChangeListener(this.mCaptioningChangeListener);
                        }
                        this.mTracks.add(track);
                    }
                    return track;
                }
            }
            return null;
        }
    }

    public void show() {
        processOnAnchor(this.mHandler.obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShow() {
        this.mShowing = true;
        this.mVisibilityIsExplicit = true;
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.show();
        }
    }

    public void hide() {
        processOnAnchor(this.mHandler.obtainMessage(2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doHide() {
        this.mVisibilityIsExplicit = true;
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.hide();
        }
        this.mShowing = false;
    }

    public void registerRenderer(Renderer renderer) {
        synchronized (this.mRenderers) {
            if (!this.mRenderers.contains(renderer)) {
                this.mRenderers.add(renderer);
            }
        }
    }

    public void setAnchor(Anchor anchor) {
        if (this.mAnchor == anchor) {
            return;
        }
        if (this.mAnchor != null) {
            checkAnchorLooper();
            this.mAnchor.setSubtitleWidget(null);
        }
        this.mAnchor = anchor;
        this.mHandler = null;
        if (this.mAnchor != null) {
            this.mHandler = new Handler(this.mAnchor.getSubtitleLooper(), this.mCallback);
            checkAnchorLooper();
            this.mAnchor.setSubtitleWidget(getRenderingWidget());
        }
    }

    private void checkAnchorLooper() {
        if (!$assertionsDisabled && this.mHandler == null) {
            throw new AssertionError("Should have a looper already");
        }
        if (!$assertionsDisabled && Looper.myLooper() != this.mHandler.getLooper()) {
            throw new AssertionError("Must be called from the anchor's looper");
        }
    }

    private void processOnAnchor(Message m) {
        if (!$assertionsDisabled && this.mHandler == null) {
            throw new AssertionError("Should have a looper already");
        }
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            this.mHandler.dispatchMessage(m);
        } else {
            this.mHandler.sendMessage(m);
        }
    }
}