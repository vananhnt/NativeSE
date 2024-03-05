package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.DrawableContainer;
import android.os.SystemClock;

/* loaded from: AnimationDrawable.class */
public class AnimationDrawable extends DrawableContainer implements Runnable, Animatable {
    private final AnimationState mAnimationState;
    private int mCurFrame;
    private boolean mMutated;

    public AnimationDrawable() {
        this(null, null);
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (visible) {
            if (changed || restart) {
                setFrame(0, true, true);
            }
        } else {
            unscheduleSelf(this);
        }
        return changed;
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (!isRunning()) {
            run();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(this);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mCurFrame > -1;
    }

    @Override // java.lang.Runnable
    public void run() {
        nextFrame(false);
    }

    @Override // android.graphics.drawable.Drawable
    public void unscheduleSelf(Runnable what) {
        this.mCurFrame = -1;
        super.unscheduleSelf(what);
    }

    public int getNumberOfFrames() {
        return this.mAnimationState.getChildCount();
    }

    public Drawable getFrame(int index) {
        return this.mAnimationState.getChild(index);
    }

    public int getDuration(int i) {
        return this.mAnimationState.mDurations[i];
    }

    public boolean isOneShot() {
        return this.mAnimationState.mOneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.mAnimationState.mOneShot = oneShot;
    }

    public void addFrame(Drawable frame, int duration) {
        this.mAnimationState.addFrame(frame, duration);
        if (this.mCurFrame < 0) {
            setFrame(0, true, false);
        }
    }

    private void nextFrame(boolean unschedule) {
        int next = this.mCurFrame + 1;
        int N = this.mAnimationState.getChildCount();
        if (next >= N) {
            next = 0;
        }
        setFrame(next, unschedule, !this.mAnimationState.mOneShot || next < N - 1);
    }

    private void setFrame(int frame, boolean unschedule, boolean animate) {
        if (frame >= this.mAnimationState.getChildCount()) {
            return;
        }
        this.mCurFrame = frame;
        selectDrawable(frame);
        if (unschedule) {
            unscheduleSelf(this);
        }
        if (animate) {
            this.mCurFrame = frame;
            scheduleSelf(this, SystemClock.uptimeMillis() + this.mAnimationState.mDurations[frame]);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:36:0x0133, code lost:
        setFrame(0, true, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x013a, code lost:
        return;
     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void inflate(android.content.res.Resources r7, org.xmlpull.v1.XmlPullParser r8, android.util.AttributeSet r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 315
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.AnimationDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mAnimationState.mDurations = (int[]) this.mAnimationState.mDurations.clone();
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AnimationDrawable$AnimationState.class */
    public static final class AnimationState extends DrawableContainer.DrawableContainerState {
        private int[] mDurations;
        private boolean mOneShot;

        AnimationState(AnimationState orig, AnimationDrawable owner, Resources res) {
            super(orig, owner, res);
            if (orig != null) {
                this.mDurations = orig.mDurations;
                this.mOneShot = orig.mOneShot;
                return;
            }
            this.mDurations = new int[getCapacity()];
            this.mOneShot = true;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AnimationDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new AnimationDrawable(this, res);
        }

        public void addFrame(Drawable dr, int dur) {
            int pos = super.addChild(dr);
            this.mDurations[pos] = dur;
        }

        @Override // android.graphics.drawable.DrawableContainer.DrawableContainerState
        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            int[] newDurations = new int[newSize];
            System.arraycopy(this.mDurations, 0, newDurations, 0, oldSize);
            this.mDurations = newDurations;
        }
    }

    private AnimationDrawable(AnimationState state, Resources res) {
        this.mCurFrame = -1;
        AnimationState as = new AnimationState(state, this, res);
        this.mAnimationState = as;
        setConstantState(as);
        if (state != null) {
            setFrame(0, true, false);
        }
    }
}