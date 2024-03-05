package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.DrawableContainer;
import android.util.AttributeSet;
import android.util.StateSet;
import com.android.internal.R;
import java.io.IOException;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: StateListDrawable.class */
public class StateListDrawable extends DrawableContainer {
    private static final boolean DEBUG = false;
    private static final String TAG = "StateListDrawable";
    private static final boolean DEFAULT_DITHER = true;
    private final StateListState mStateListState;
    private boolean mMutated;

    public StateListDrawable() {
        this(null, null);
    }

    public void addState(int[] stateSet, Drawable drawable) {
        if (drawable != null) {
            this.mStateListState.addStateSet(stateSet, drawable);
            onStateChange(getState());
        }
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] stateSet) {
        int idx = this.mStateListState.indexOfStateSet(stateSet);
        if (idx < 0) {
            idx = this.mStateListState.indexOfStateSet(StateSet.WILD_CARD);
        }
        if (selectDrawable(idx)) {
            return true;
        }
        return super.onStateChange(stateSet);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        int depth;
        int type;
        Drawable createFromXmlInner;
        TypedArray a = r.obtainAttributes(attrs, R.styleable.StateListDrawable);
        super.inflateWithAttributes(r, parser, a, 1);
        this.mStateListState.setVariablePadding(a.getBoolean(2, false));
        this.mStateListState.setConstantSize(a.getBoolean(3, false));
        this.mStateListState.setEnterFadeDuration(a.getInt(4, 0));
        this.mStateListState.setExitFadeDuration(a.getInt(5, 0));
        setDither(a.getBoolean(0, true));
        setAutoMirrored(a.getBoolean(6, false));
        a.recycle();
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || ((depth = parser.getDepth()) < innerDepth && type2 == 3)) {
                break;
            } else if (type2 == 2 && depth <= innerDepth && parser.getName().equals("item")) {
                int drawableRes = 0;
                int j = 0;
                int numAttrs = attrs.getAttributeCount();
                int[] states = new int[numAttrs];
                for (int i = 0; i < numAttrs; i++) {
                    int stateResId = attrs.getAttributeNameResource(i);
                    if (stateResId == 0) {
                        break;
                    }
                    if (stateResId == 16843161) {
                        drawableRes = attrs.getAttributeResourceValue(i, 0);
                    } else {
                        int i2 = j;
                        j++;
                        states[i2] = attrs.getAttributeBooleanValue(i, false) ? stateResId : -stateResId;
                    }
                }
                int[] states2 = StateSet.trimStateSet(states, j);
                if (drawableRes != 0) {
                    createFromXmlInner = r.getDrawable(drawableRes);
                } else {
                    do {
                        type = parser.next();
                    } while (type == 4);
                    if (type != 2) {
                        throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
                    }
                    createFromXmlInner = Drawable.createFromXmlInner(r, parser, attrs);
                }
                Drawable dr = createFromXmlInner;
                this.mStateListState.addStateSet(states2, dr);
            }
        }
        onStateChange(getState());
    }

    StateListState getStateListState() {
        return this.mStateListState;
    }

    public int getStateCount() {
        return this.mStateListState.getChildCount();
    }

    public int[] getStateSet(int index) {
        return this.mStateListState.mStateSets[index];
    }

    public Drawable getStateDrawable(int index) {
        return this.mStateListState.getChild(index);
    }

    public int getStateDrawableIndex(int[] stateSet) {
        return this.mStateListState.indexOfStateSet(stateSet);
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [int[], int[][]] */
    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            int[][] sets = this.mStateListState.mStateSets;
            int count = sets.length;
            this.mStateListState.mStateSets = new int[count];
            for (int i = 0; i < count; i++) {
                int[] set = sets[i];
                if (set != null) {
                    this.mStateListState.mStateSets[i] = (int[]) set.clone();
                }
            }
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int layoutDirection) {
        super.setLayoutDirection(layoutDirection);
        this.mStateListState.setLayoutDirection(layoutDirection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: StateListDrawable$StateListState.class */
    public static final class StateListState extends DrawableContainer.DrawableContainerState {
        int[][] mStateSets;

        /* JADX WARN: Type inference failed for: r1v3, types: [int[], int[][]] */
        StateListState(StateListState orig, StateListDrawable owner, Resources res) {
            super(orig, owner, res);
            if (orig != null) {
                this.mStateSets = (int[][]) Arrays.copyOf(orig.mStateSets, orig.mStateSets.length);
            } else {
                this.mStateSets = new int[getCapacity()];
            }
        }

        int addStateSet(int[] stateSet, Drawable drawable) {
            int pos = addChild(drawable);
            this.mStateSets[pos] = stateSet;
            return pos;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int indexOfStateSet(int[] stateSet) {
            int[][] stateSets = this.mStateSets;
            int N = getChildCount();
            for (int i = 0; i < N; i++) {
                if (StateSet.stateSetMatches(stateSets[i], stateSet)) {
                    return i;
                }
            }
            return -1;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new StateListDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new StateListDrawable(this, res);
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [int[], int[][], java.lang.Object] */
        @Override // android.graphics.drawable.DrawableContainer.DrawableContainerState
        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            ?? r0 = new int[newSize];
            System.arraycopy(this.mStateSets, 0, r0, 0, oldSize);
            this.mStateSets = r0;
        }
    }

    private StateListDrawable(StateListState state, Resources res) {
        StateListState as = new StateListState(state, this, res);
        this.mStateListState = as;
        setConstantState(as);
        onStateChange(getState());
    }
}