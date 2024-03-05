package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.R;

/* loaded from: EdgeEffect.class */
public class EdgeEffect {
    private static final String TAG = "EdgeEffect";
    private static final int RECEDE_TIME = 1000;
    private static final int PULL_TIME = 167;
    private static final int PULL_DECAY_TIME = 1000;
    private static final float MAX_ALPHA = 1.0f;
    private static final float HELD_EDGE_SCALE_Y = 0.5f;
    private static final float MAX_GLOW_HEIGHT = 4.0f;
    private static final float PULL_GLOW_BEGIN = 1.0f;
    private static final float PULL_EDGE_BEGIN = 0.6f;
    private static final int MIN_VELOCITY = 100;
    private static final int MAX_VELOCITY = 10000;
    private static final float EPSILON = 0.001f;
    private final Drawable mEdge;
    private final Drawable mGlow;
    private int mWidth;
    private int mHeight;
    private int mX;
    private int mY;
    private static final int MIN_WIDTH = 300;
    private final int mMinWidth;
    private float mEdgeAlpha;
    private float mEdgeScaleY;
    private float mGlowAlpha;
    private float mGlowScaleY;
    private float mEdgeAlphaStart;
    private float mEdgeAlphaFinish;
    private float mEdgeScaleYStart;
    private float mEdgeScaleYFinish;
    private float mGlowAlphaStart;
    private float mGlowAlphaFinish;
    private float mGlowScaleYStart;
    private float mGlowScaleYFinish;
    private long mStartTime;
    private float mDuration;
    private final Interpolator mInterpolator;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PULL = 1;
    private static final int STATE_ABSORB = 2;
    private static final int STATE_RECEDE = 3;
    private static final int STATE_PULL_DECAY = 4;
    private static final int PULL_DISTANCE_EDGE_FACTOR = 7;
    private static final int PULL_DISTANCE_GLOW_FACTOR = 7;
    private static final float PULL_DISTANCE_ALPHA_GLOW_FACTOR = 1.1f;
    private static final int VELOCITY_EDGE_FACTOR = 8;
    private static final int VELOCITY_GLOW_FACTOR = 12;
    private float mPullDistance;
    private final int mEdgeHeight;
    private final int mGlowHeight;
    private final int mGlowWidth;
    private final int mMaxEffectHeight;
    private int mState = 0;
    private final Rect mBounds = new Rect();

    public EdgeEffect(Context context) {
        Resources res = context.getResources();
        this.mEdge = res.getDrawable(R.drawable.overscroll_edge);
        this.mGlow = res.getDrawable(R.drawable.overscroll_glow);
        this.mEdgeHeight = this.mEdge.getIntrinsicHeight();
        this.mGlowHeight = this.mGlow.getIntrinsicHeight();
        this.mGlowWidth = this.mGlow.getIntrinsicWidth();
        this.mMaxEffectHeight = (int) (Math.min((((this.mGlowHeight * MAX_GLOW_HEIGHT) * this.mGlowHeight) / this.mGlowWidth) * 0.6f, this.mGlowHeight * MAX_GLOW_HEIGHT) + HELD_EDGE_SCALE_Y);
        this.mMinWidth = (int) ((res.getDisplayMetrics().density * 300.0f) + HELD_EDGE_SCALE_Y);
        this.mInterpolator = new DecelerateInterpolator();
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPosition(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public boolean isFinished() {
        return this.mState == 0;
    }

    public void finish() {
        this.mState = 0;
    }

    public void onPull(float deltaDistance) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (this.mState == 4 && ((float) (now - this.mStartTime)) < this.mDuration) {
            return;
        }
        if (this.mState != 1) {
            this.mGlowScaleY = 1.0f;
        }
        this.mState = 1;
        this.mStartTime = now;
        this.mDuration = 167.0f;
        this.mPullDistance += deltaDistance;
        float distance = Math.abs(this.mPullDistance);
        float max = Math.max(0.6f, Math.min(distance, 1.0f));
        this.mEdgeAlphaStart = max;
        this.mEdgeAlpha = max;
        float max2 = Math.max((float) HELD_EDGE_SCALE_Y, Math.min(distance * 7.0f, 1.0f));
        this.mEdgeScaleYStart = max2;
        this.mEdgeScaleY = max2;
        float min = Math.min(1.0f, this.mGlowAlpha + (Math.abs(deltaDistance) * PULL_DISTANCE_ALPHA_GLOW_FACTOR));
        this.mGlowAlphaStart = min;
        this.mGlowAlpha = min;
        float glowChange = Math.abs(deltaDistance);
        if (deltaDistance > 0.0f && this.mPullDistance < 0.0f) {
            glowChange = -glowChange;
        }
        if (this.mPullDistance == 0.0f) {
            this.mGlowScaleY = 0.0f;
        }
        float min2 = Math.min((float) MAX_GLOW_HEIGHT, Math.max(0.0f, this.mGlowScaleY + (glowChange * 7.0f)));
        this.mGlowScaleYStart = min2;
        this.mGlowScaleY = min2;
        this.mEdgeAlphaFinish = this.mEdgeAlpha;
        this.mEdgeScaleYFinish = this.mEdgeScaleY;
        this.mGlowAlphaFinish = this.mGlowAlpha;
        this.mGlowScaleYFinish = this.mGlowScaleY;
    }

    public void onRelease() {
        this.mPullDistance = 0.0f;
        if (this.mState != 1 && this.mState != 4) {
            return;
        }
        this.mState = 3;
        this.mEdgeAlphaStart = this.mEdgeAlpha;
        this.mEdgeScaleYStart = this.mEdgeScaleY;
        this.mGlowAlphaStart = this.mGlowAlpha;
        this.mGlowScaleYStart = this.mGlowScaleY;
        this.mEdgeAlphaFinish = 0.0f;
        this.mEdgeScaleYFinish = 0.0f;
        this.mGlowAlphaFinish = 0.0f;
        this.mGlowScaleYFinish = 0.0f;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDuration = 1000.0f;
    }

    public void onAbsorb(int velocity) {
        this.mState = 2;
        int velocity2 = Math.min(Math.max(100, Math.abs(velocity)), 10000);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDuration = 0.15f + (velocity2 * 0.02f);
        this.mEdgeAlphaStart = 0.0f;
        this.mEdgeScaleYStart = 0.0f;
        this.mEdgeScaleY = 0.0f;
        this.mGlowAlphaStart = 0.3f;
        this.mGlowScaleYStart = 0.0f;
        this.mEdgeAlphaFinish = Math.max(0, Math.min(velocity2 * 8, 1));
        this.mEdgeScaleYFinish = Math.max((float) HELD_EDGE_SCALE_Y, Math.min(velocity2 * 8, 1.0f));
        this.mGlowScaleYFinish = Math.min(0.025f + (velocity2 * (velocity2 / 100) * 1.5E-4f), 1.75f);
        this.mGlowAlphaFinish = Math.max(this.mGlowAlphaStart, Math.min(velocity2 * 12 * 1.0E-5f, 1.0f));
    }

    public boolean draw(Canvas canvas) {
        update();
        this.mGlow.setAlpha((int) (Math.max(0.0f, Math.min(this.mGlowAlpha, 1.0f)) * 255.0f));
        int glowBottom = (int) Math.min((((this.mGlowHeight * this.mGlowScaleY) * this.mGlowHeight) / this.mGlowWidth) * 0.6f, this.mGlowHeight * MAX_GLOW_HEIGHT);
        if (this.mWidth < this.mMinWidth) {
            int glowLeft = (this.mWidth - this.mMinWidth) / 2;
            this.mGlow.setBounds(glowLeft, 0, this.mWidth - glowLeft, glowBottom);
        } else {
            this.mGlow.setBounds(0, 0, this.mWidth, glowBottom);
        }
        this.mGlow.draw(canvas);
        this.mEdge.setAlpha((int) (Math.max(0.0f, Math.min(this.mEdgeAlpha, 1.0f)) * 255.0f));
        int edgeBottom = (int) (this.mEdgeHeight * this.mEdgeScaleY);
        if (this.mWidth < this.mMinWidth) {
            int edgeLeft = (this.mWidth - this.mMinWidth) / 2;
            this.mEdge.setBounds(edgeLeft, 0, this.mWidth - edgeLeft, edgeBottom);
        } else {
            this.mEdge.setBounds(0, 0, this.mWidth, edgeBottom);
        }
        this.mEdge.draw(canvas);
        if (this.mState == 3 && glowBottom == 0 && edgeBottom == 0) {
            this.mState = 0;
        }
        return this.mState != 0;
    }

    public Rect getBounds(boolean reverse) {
        this.mBounds.set(0, 0, this.mWidth, this.mMaxEffectHeight);
        this.mBounds.offset(this.mX, this.mY - (reverse ? this.mMaxEffectHeight : 0));
        return this.mBounds;
    }

    private void update() {
        long time = AnimationUtils.currentAnimationTimeMillis();
        float t = Math.min(((float) (time - this.mStartTime)) / this.mDuration, 1.0f);
        float interp = this.mInterpolator.getInterpolation(t);
        this.mEdgeAlpha = this.mEdgeAlphaStart + ((this.mEdgeAlphaFinish - this.mEdgeAlphaStart) * interp);
        this.mEdgeScaleY = this.mEdgeScaleYStart + ((this.mEdgeScaleYFinish - this.mEdgeScaleYStart) * interp);
        this.mGlowAlpha = this.mGlowAlphaStart + ((this.mGlowAlphaFinish - this.mGlowAlphaStart) * interp);
        this.mGlowScaleY = this.mGlowScaleYStart + ((this.mGlowScaleYFinish - this.mGlowScaleYStart) * interp);
        if (t >= 0.999f) {
            switch (this.mState) {
                case 1:
                    this.mState = 4;
                    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    this.mDuration = 1000.0f;
                    this.mEdgeAlphaStart = this.mEdgeAlpha;
                    this.mEdgeScaleYStart = this.mEdgeScaleY;
                    this.mGlowAlphaStart = this.mGlowAlpha;
                    this.mGlowScaleYStart = this.mGlowScaleY;
                    this.mEdgeAlphaFinish = 0.0f;
                    this.mEdgeScaleYFinish = 0.0f;
                    this.mGlowAlphaFinish = 0.0f;
                    this.mGlowScaleYFinish = 0.0f;
                    return;
                case 2:
                    this.mState = 3;
                    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    this.mDuration = 1000.0f;
                    this.mEdgeAlphaStart = this.mEdgeAlpha;
                    this.mEdgeScaleYStart = this.mEdgeScaleY;
                    this.mGlowAlphaStart = this.mGlowAlpha;
                    this.mGlowScaleYStart = this.mGlowScaleY;
                    this.mEdgeAlphaFinish = 0.0f;
                    this.mEdgeScaleYFinish = 0.0f;
                    this.mGlowAlphaFinish = 0.0f;
                    this.mGlowScaleYFinish = 0.0f;
                    return;
                case 3:
                    this.mState = 0;
                    return;
                case 4:
                    float factor = this.mGlowScaleYFinish != 0.0f ? 1.0f / (this.mGlowScaleYFinish * this.mGlowScaleYFinish) : Float.MAX_VALUE;
                    this.mEdgeScaleY = this.mEdgeScaleYStart + ((this.mEdgeScaleYFinish - this.mEdgeScaleYStart) * interp * factor);
                    this.mState = 3;
                    return;
                default:
                    return;
            }
        }
    }
}