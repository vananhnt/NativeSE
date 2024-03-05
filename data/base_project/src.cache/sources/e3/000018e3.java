package android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.Metadata;
import android.media.SubtitleController;
import android.media.SubtitleTrack;
import android.media.WebVttRenderer;
import android.net.Uri;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MediaController;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/* loaded from: VideoView.class */
public class VideoView extends SurfaceView implements MediaController.MediaPlayerControl, SubtitleController.Anchor {
    private String TAG;
    private Uri mUri;
    private Map<String, String> mHeaders;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private int mCurrentState;
    private int mTargetState;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private int mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private MediaController mMediaController;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;
    private SubtitleTrack.RenderingWidget mSubtitleWidget;
    private SubtitleTrack.RenderingWidget.OnChangedListener mSubtitlesChangedListener;
    private Vector<Pair<InputStream, MediaFormat>> mPendingSubtitleTracks;
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener;
    MediaPlayer.OnPreparedListener mPreparedListener;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private MediaPlayer.OnInfoListener mInfoListener;
    private MediaPlayer.OnErrorListener mErrorListener;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    SurfaceHolder.Callback mSHCallback;

    public VideoView(Context context) {
        super(context);
        this.TAG = "VideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: android.widget.VideoView.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                VideoView.this.mVideoWidth = mp.getVideoWidth();
                VideoView.this.mVideoHeight = mp.getVideoHeight();
                if (VideoView.this.mVideoWidth != 0 && VideoView.this.mVideoHeight != 0) {
                    VideoView.this.getHolder().setFixedSize(VideoView.this.mVideoWidth, VideoView.this.mVideoHeight);
                    VideoView.this.requestLayout();
                }
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: android.widget.VideoView.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                VideoView.this.mCurrentState = 2;
                Metadata data = mp.getMetadata(false, false);
                if (data == null) {
                    VideoView.this.mCanPause = VideoView.this.mCanSeekBack = VideoView.this.mCanSeekForward = true;
                } else {
                    VideoView.this.mCanPause = !data.has(1) || data.getBoolean(1);
                    VideoView.this.mCanSeekBack = !data.has(2) || data.getBoolean(2);
                    VideoView.this.mCanSeekForward = !data.has(3) || data.getBoolean(3);
                }
                if (VideoView.this.mOnPreparedListener != null) {
                    VideoView.this.mOnPreparedListener.onPrepared(VideoView.this.mMediaPlayer);
                }
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.setEnabled(true);
                }
                VideoView.this.mVideoWidth = mp.getVideoWidth();
                VideoView.this.mVideoHeight = mp.getVideoHeight();
                int seekToPosition = VideoView.this.mSeekWhenPrepared;
                if (seekToPosition != 0) {
                    VideoView.this.seekTo(seekToPosition);
                }
                if (VideoView.this.mVideoWidth == 0 || VideoView.this.mVideoHeight == 0) {
                    if (VideoView.this.mTargetState == 3) {
                        VideoView.this.start();
                        return;
                    }
                    return;
                }
                VideoView.this.getHolder().setFixedSize(VideoView.this.mVideoWidth, VideoView.this.mVideoHeight);
                if (VideoView.this.mSurfaceWidth == VideoView.this.mVideoWidth && VideoView.this.mSurfaceHeight == VideoView.this.mVideoHeight) {
                    if (VideoView.this.mTargetState == 3) {
                        VideoView.this.start();
                        if (VideoView.this.mMediaController != null) {
                            VideoView.this.mMediaController.show();
                        }
                    } else if (!VideoView.this.isPlaying()) {
                        if ((seekToPosition != 0 || VideoView.this.getCurrentPosition() > 0) && VideoView.this.mMediaController != null) {
                            VideoView.this.mMediaController.show(0);
                        }
                    }
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: android.widget.VideoView.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                VideoView.this.mCurrentState = 5;
                VideoView.this.mTargetState = 5;
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.hide();
                }
                if (VideoView.this.mOnCompletionListener != null) {
                    VideoView.this.mOnCompletionListener.onCompletion(VideoView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() { // from class: android.widget.VideoView.4
            @Override // android.media.MediaPlayer.OnInfoListener
            public boolean onInfo(MediaPlayer mp, int arg1, int arg2) {
                if (VideoView.this.mOnInfoListener != null) {
                    VideoView.this.mOnInfoListener.onInfo(mp, arg1, arg2);
                    return true;
                }
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: android.widget.VideoView.5
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                int messageId;
                Log.d(VideoView.this.TAG, "Error: " + framework_err + Separators.COMMA + impl_err);
                VideoView.this.mCurrentState = -1;
                VideoView.this.mTargetState = -1;
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.hide();
                }
                if ((VideoView.this.mOnErrorListener == null || !VideoView.this.mOnErrorListener.onError(VideoView.this.mMediaPlayer, framework_err, impl_err)) && VideoView.this.getWindowToken() != null) {
                    VideoView.this.mContext.getResources();
                    if (framework_err == 200) {
                        messageId = 17039381;
                    } else {
                        messageId = 17039377;
                    }
                    new AlertDialog.Builder(VideoView.this.mContext).setMessage(messageId).setPositiveButton(17039376, new DialogInterface.OnClickListener() { // from class: android.widget.VideoView.5.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (VideoView.this.mOnCompletionListener != null) {
                                VideoView.this.mOnCompletionListener.onCompletion(VideoView.this.mMediaPlayer);
                            }
                        }
                    }).setCancelable(false).show();
                    return true;
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: android.widget.VideoView.6
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                VideoView.this.mCurrentBufferPercentage = percent;
            }
        };
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: android.widget.VideoView.7
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                VideoView.this.mSurfaceWidth = w;
                VideoView.this.mSurfaceHeight = h;
                boolean isValidState = VideoView.this.mTargetState == 3;
                boolean hasValidSize = VideoView.this.mVideoWidth == w && VideoView.this.mVideoHeight == h;
                if (VideoView.this.mMediaPlayer != null && isValidState && hasValidSize) {
                    if (VideoView.this.mSeekWhenPrepared != 0) {
                        VideoView.this.seekTo(VideoView.this.mSeekWhenPrepared);
                    }
                    VideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                VideoView.this.mSurfaceHolder = holder;
                VideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) {
                VideoView.this.mSurfaceHolder = null;
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.hide();
                }
                VideoView.this.release(true);
            }
        };
        initVideoView();
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.TAG = "VideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: android.widget.VideoView.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                VideoView.this.mVideoWidth = mp.getVideoWidth();
                VideoView.this.mVideoHeight = mp.getVideoHeight();
                if (VideoView.this.mVideoWidth != 0 && VideoView.this.mVideoHeight != 0) {
                    VideoView.this.getHolder().setFixedSize(VideoView.this.mVideoWidth, VideoView.this.mVideoHeight);
                    VideoView.this.requestLayout();
                }
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: android.widget.VideoView.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                VideoView.this.mCurrentState = 2;
                Metadata data = mp.getMetadata(false, false);
                if (data == null) {
                    VideoView.this.mCanPause = VideoView.this.mCanSeekBack = VideoView.this.mCanSeekForward = true;
                } else {
                    VideoView.this.mCanPause = !data.has(1) || data.getBoolean(1);
                    VideoView.this.mCanSeekBack = !data.has(2) || data.getBoolean(2);
                    VideoView.this.mCanSeekForward = !data.has(3) || data.getBoolean(3);
                }
                if (VideoView.this.mOnPreparedListener != null) {
                    VideoView.this.mOnPreparedListener.onPrepared(VideoView.this.mMediaPlayer);
                }
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.setEnabled(true);
                }
                VideoView.this.mVideoWidth = mp.getVideoWidth();
                VideoView.this.mVideoHeight = mp.getVideoHeight();
                int seekToPosition = VideoView.this.mSeekWhenPrepared;
                if (seekToPosition != 0) {
                    VideoView.this.seekTo(seekToPosition);
                }
                if (VideoView.this.mVideoWidth == 0 || VideoView.this.mVideoHeight == 0) {
                    if (VideoView.this.mTargetState == 3) {
                        VideoView.this.start();
                        return;
                    }
                    return;
                }
                VideoView.this.getHolder().setFixedSize(VideoView.this.mVideoWidth, VideoView.this.mVideoHeight);
                if (VideoView.this.mSurfaceWidth == VideoView.this.mVideoWidth && VideoView.this.mSurfaceHeight == VideoView.this.mVideoHeight) {
                    if (VideoView.this.mTargetState == 3) {
                        VideoView.this.start();
                        if (VideoView.this.mMediaController != null) {
                            VideoView.this.mMediaController.show();
                        }
                    } else if (!VideoView.this.isPlaying()) {
                        if ((seekToPosition != 0 || VideoView.this.getCurrentPosition() > 0) && VideoView.this.mMediaController != null) {
                            VideoView.this.mMediaController.show(0);
                        }
                    }
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: android.widget.VideoView.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                VideoView.this.mCurrentState = 5;
                VideoView.this.mTargetState = 5;
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.hide();
                }
                if (VideoView.this.mOnCompletionListener != null) {
                    VideoView.this.mOnCompletionListener.onCompletion(VideoView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() { // from class: android.widget.VideoView.4
            @Override // android.media.MediaPlayer.OnInfoListener
            public boolean onInfo(MediaPlayer mp, int arg1, int arg2) {
                if (VideoView.this.mOnInfoListener != null) {
                    VideoView.this.mOnInfoListener.onInfo(mp, arg1, arg2);
                    return true;
                }
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: android.widget.VideoView.5
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                int messageId;
                Log.d(VideoView.this.TAG, "Error: " + framework_err + Separators.COMMA + impl_err);
                VideoView.this.mCurrentState = -1;
                VideoView.this.mTargetState = -1;
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.hide();
                }
                if ((VideoView.this.mOnErrorListener == null || !VideoView.this.mOnErrorListener.onError(VideoView.this.mMediaPlayer, framework_err, impl_err)) && VideoView.this.getWindowToken() != null) {
                    VideoView.this.mContext.getResources();
                    if (framework_err == 200) {
                        messageId = 17039381;
                    } else {
                        messageId = 17039377;
                    }
                    new AlertDialog.Builder(VideoView.this.mContext).setMessage(messageId).setPositiveButton(17039376, new DialogInterface.OnClickListener() { // from class: android.widget.VideoView.5.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (VideoView.this.mOnCompletionListener != null) {
                                VideoView.this.mOnCompletionListener.onCompletion(VideoView.this.mMediaPlayer);
                            }
                        }
                    }).setCancelable(false).show();
                    return true;
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: android.widget.VideoView.6
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                VideoView.this.mCurrentBufferPercentage = percent;
            }
        };
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: android.widget.VideoView.7
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                VideoView.this.mSurfaceWidth = w;
                VideoView.this.mSurfaceHeight = h;
                boolean isValidState = VideoView.this.mTargetState == 3;
                boolean hasValidSize = VideoView.this.mVideoWidth == w && VideoView.this.mVideoHeight == h;
                if (VideoView.this.mMediaPlayer != null && isValidState && hasValidSize) {
                    if (VideoView.this.mSeekWhenPrepared != 0) {
                        VideoView.this.seekTo(VideoView.this.mSeekWhenPrepared);
                    }
                    VideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                VideoView.this.mSurfaceHolder = holder;
                VideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) {
                VideoView.this.mSurfaceHolder = null;
                if (VideoView.this.mMediaController != null) {
                    VideoView.this.mMediaController.hide();
                }
                VideoView.this.release(true);
            }
        };
        initVideoView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.SurfaceView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(this.mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(this.mVideoHeight, heightMeasureSpec);
        if (this.mVideoWidth > 0 && this.mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (widthSpecMode == 1073741824 && heightSpecMode == 1073741824) {
                width = widthSpecSize;
                height = heightSpecSize;
                if (this.mVideoWidth * height < width * this.mVideoHeight) {
                    width = (height * this.mVideoWidth) / this.mVideoHeight;
                } else if (this.mVideoWidth * height > width * this.mVideoHeight) {
                    height = (width * this.mVideoHeight) / this.mVideoWidth;
                }
            } else if (widthSpecMode == 1073741824) {
                width = widthSpecSize;
                height = (width * this.mVideoHeight) / this.mVideoWidth;
                if (heightSpecMode == Integer.MIN_VALUE && height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == 1073741824) {
                height = heightSpecSize;
                width = (height * this.mVideoWidth) / this.mVideoHeight;
                if (widthSpecMode == Integer.MIN_VALUE && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                width = this.mVideoWidth;
                height = this.mVideoHeight;
                if (heightSpecMode == Integer.MIN_VALUE && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = (height * this.mVideoWidth) / this.mVideoHeight;
                }
                if (widthSpecMode == Integer.MIN_VALUE && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = (width * this.mVideoHeight) / this.mVideoWidth;
                }
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(VideoView.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(VideoView.class.getName());
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        return getDefaultSize(desiredSize, measureSpec);
    }

    private void initVideoView() {
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        getHolder().addCallback(this.mSHCallback);
        getHolder().setType(3);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        this.mPendingSubtitleTracks = new Vector<>();
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        this.mUri = uri;
        this.mHeaders = headers;
        this.mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void addSubtitleSource(InputStream is, MediaFormat format) {
        if (this.mMediaPlayer == null) {
            this.mPendingSubtitleTracks.add(Pair.create(is, format));
            return;
        }
        try {
            this.mMediaPlayer.addSubtitleSource(is, format);
        } catch (IllegalStateException e) {
            this.mInfoListener.onInfo(this.mMediaPlayer, MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE, 0);
        }
    }

    public void stopPlayback() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            this.mTargetState = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openVideo() {
        if (this.mUri == null || this.mSurfaceHolder == null) {
            return;
        }
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        this.mContext.sendBroadcast(i);
        release(false);
        try {
            try {
                this.mMediaPlayer = new MediaPlayer();
                Context context = getContext();
                SubtitleController controller = new SubtitleController(context, this.mMediaPlayer.getMediaTimeProvider(), this.mMediaPlayer);
                controller.registerRenderer(new WebVttRenderer(context));
                this.mMediaPlayer.setSubtitleAnchor(controller, this);
                if (this.mAudioSession != 0) {
                    this.mMediaPlayer.setAudioSessionId(this.mAudioSession);
                } else {
                    this.mAudioSession = this.mMediaPlayer.getAudioSessionId();
                }
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setOnInfoListener(this.mInfoListener);
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
                this.mCurrentBufferPercentage = 0;
                this.mMediaPlayer.setDataSource(this.mContext, this.mUri, this.mHeaders);
                this.mMediaPlayer.setDisplay(this.mSurfaceHolder);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                this.mMediaPlayer.prepareAsync();
                Iterator i$ = this.mPendingSubtitleTracks.iterator();
                while (i$.hasNext()) {
                    Pair<InputStream, MediaFormat> pending = i$.next();
                    try {
                        this.mMediaPlayer.addSubtitleSource(pending.first, pending.second);
                    } catch (IllegalStateException e) {
                        this.mInfoListener.onInfo(this.mMediaPlayer, MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE, 0);
                    }
                }
                this.mCurrentState = 1;
                attachMediaController();
                this.mPendingSubtitleTracks.clear();
            } catch (IOException ex) {
                Log.w(this.TAG, "Unable to open content: " + this.mUri, ex);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
                this.mPendingSubtitleTracks.clear();
            } catch (IllegalArgumentException ex2) {
                Log.w(this.TAG, "Unable to open content: " + this.mUri, ex2);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
                this.mPendingSubtitleTracks.clear();
            }
        } catch (Throwable th) {
            this.mPendingSubtitleTracks.clear();
            throw th;
        }
    }

    public void setMediaController(MediaController controller) {
        if (this.mMediaController != null) {
            this.mMediaController.hide();
        }
        this.mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (this.mMediaPlayer != null && this.mMediaController != null) {
            this.mMediaController.setMediaPlayer(this);
            View anchorView = getParent() instanceof View ? (View) getParent() : this;
            this.mMediaController.setAnchorView(anchorView);
            this.mMediaController.setEnabled(isInPlaybackState());
        }
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener l) {
        this.mOnInfoListener = l;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void release(boolean cleartargetstate) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mPendingSubtitleTracks.clear();
            this.mCurrentState = 0;
            if (cleartargetstate) {
                this.mTargetState = 0;
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
            return false;
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
            return false;
        }
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = (keyCode == 4 || keyCode == 24 || keyCode == 25 || keyCode == 164 || keyCode == 82 || keyCode == 5 || keyCode == 6) ? false : true;
        if (isInPlaybackState() && isKeyCodeSupported && this.mMediaController != null) {
            if (keyCode == 79 || keyCode == 85) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                    return true;
                }
                start();
                this.mMediaController.hide();
                return true;
            } else if (keyCode == 126) {
                if (!this.mMediaPlayer.isPlaying()) {
                    start();
                    this.mMediaController.hide();
                    return true;
                }
                return true;
            } else if (keyCode == 86 || keyCode == 127) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                    return true;
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (this.mMediaController.isShowing()) {
            this.mMediaController.hide();
        } else {
            this.mMediaController.show();
        }
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void start() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
        }
        this.mTargetState = 3;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
        }
        this.mTargetState = 4;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getDuration() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            this.mMediaPlayer.seekTo(msec);
            this.mSeekWhenPrepared = 0;
            return;
        }
        this.mSeekWhenPrepared = msec;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getBufferPercentage() {
        if (this.mMediaPlayer != null) {
            return this.mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (this.mMediaPlayer == null || this.mCurrentState == -1 || this.mCurrentState == 0 || this.mCurrentState == 1) ? false : true;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canPause() {
        return this.mCanPause;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canSeekBackward() {
        return this.mCanSeekBack;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canSeekForward() {
        return this.mCanSeekForward;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getAudioSessionId() {
        if (this.mAudioSession == 0) {
            MediaPlayer foo = new MediaPlayer();
            this.mAudioSession = foo.getAudioSessionId();
            foo.release();
        }
        return this.mAudioSession;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.SurfaceView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mSubtitleWidget != null) {
            this.mSubtitleWidget.onAttachedToWindow();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.SurfaceView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mSubtitleWidget != null) {
            this.mSubtitleWidget.onDetachedFromWindow();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mSubtitleWidget != null) {
            measureAndLayoutSubtitleWidget();
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mSubtitleWidget != null) {
            int saveCount = canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            this.mSubtitleWidget.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    private void measureAndLayoutSubtitleWidget() {
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        this.mSubtitleWidget.setSize(width, height);
    }

    @Override // android.media.SubtitleController.Anchor
    public void setSubtitleWidget(SubtitleTrack.RenderingWidget subtitleWidget) {
        if (this.mSubtitleWidget == subtitleWidget) {
            return;
        }
        boolean attachedToWindow = isAttachedToWindow();
        if (this.mSubtitleWidget != null) {
            if (attachedToWindow) {
                this.mSubtitleWidget.onDetachedFromWindow();
            }
            this.mSubtitleWidget.setOnChangedListener(null);
        }
        this.mSubtitleWidget = subtitleWidget;
        if (subtitleWidget != null) {
            if (this.mSubtitlesChangedListener == null) {
                this.mSubtitlesChangedListener = new SubtitleTrack.RenderingWidget.OnChangedListener() { // from class: android.widget.VideoView.8
                    @Override // android.media.SubtitleTrack.RenderingWidget.OnChangedListener
                    public void onChanged(SubtitleTrack.RenderingWidget renderingWidget) {
                        VideoView.this.invalidate();
                    }
                };
            }
            setWillNotDraw(false);
            subtitleWidget.setOnChangedListener(this.mSubtitlesChangedListener);
            if (attachedToWindow) {
                subtitleWidget.onAttachedToWindow();
                requestLayout();
            }
        } else {
            setWillNotDraw(true);
        }
        invalidate();
    }

    @Override // android.media.SubtitleController.Anchor
    public Looper getSubtitleLooper() {
        return Looper.getMainLooper();
    }
}