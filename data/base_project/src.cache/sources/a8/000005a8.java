package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.DrawableContainer;
import android.util.AttributeSet;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: LevelListDrawable.class */
public class LevelListDrawable extends DrawableContainer {
    private final LevelListState mLevelListState;
    private boolean mMutated;

    public LevelListDrawable() {
        this(null, null);
    }

    public void addLevel(int low, int high, Drawable drawable) {
        if (drawable != null) {
            this.mLevelListState.addLevel(low, high, drawable);
            onLevelChange(getLevel());
        }
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        int idx = this.mLevelListState.indexOfLevel(level);
        if (selectDrawable(idx)) {
            return true;
        }
        return super.onLevelChange(level);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        int depth;
        int type;
        Drawable createFromXmlInner;
        super.inflate(r, parser, attrs);
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || ((depth = parser.getDepth()) < innerDepth && type2 == 3)) {
                break;
            } else if (type2 == 2 && depth <= innerDepth && parser.getName().equals("item")) {
                TypedArray a = r.obtainAttributes(attrs, R.styleable.LevelListDrawableItem);
                int low = a.getInt(1, 0);
                int high = a.getInt(2, 0);
                int drawableRes = a.getResourceId(0, 0);
                a.recycle();
                if (high < 0) {
                    throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'maxLevel' attribute");
                }
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
                this.mLevelListState.addLevel(low, high, dr);
            }
        }
        onLevelChange(getLevel());
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mLevelListState.mLows = (int[]) this.mLevelListState.mLows.clone();
            this.mLevelListState.mHighs = (int[]) this.mLevelListState.mHighs.clone();
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LevelListDrawable$LevelListState.class */
    public static final class LevelListState extends DrawableContainer.DrawableContainerState {
        private int[] mLows;
        private int[] mHighs;

        LevelListState(LevelListState orig, LevelListDrawable owner, Resources res) {
            super(orig, owner, res);
            if (orig != null) {
                this.mLows = orig.mLows;
                this.mHighs = orig.mHighs;
                return;
            }
            this.mLows = new int[getCapacity()];
            this.mHighs = new int[getCapacity()];
        }

        public void addLevel(int low, int high, Drawable drawable) {
            int pos = addChild(drawable);
            this.mLows[pos] = low;
            this.mHighs[pos] = high;
        }

        public int indexOfLevel(int level) {
            int[] lows = this.mLows;
            int[] highs = this.mHighs;
            int N = getChildCount();
            for (int i = 0; i < N; i++) {
                if (level >= lows[i] && level <= highs[i]) {
                    return i;
                }
            }
            return -1;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new LevelListDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new LevelListDrawable(this, res);
        }

        @Override // android.graphics.drawable.DrawableContainer.DrawableContainerState
        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            int[] newInts = new int[newSize];
            System.arraycopy(this.mLows, 0, newInts, 0, oldSize);
            this.mLows = newInts;
            int[] newInts2 = new int[newSize];
            System.arraycopy(this.mHighs, 0, newInts2, 0, oldSize);
            this.mHighs = newInts2;
        }
    }

    private LevelListDrawable(LevelListState state, Resources res) {
        LevelListState as = new LevelListState(state, this, res);
        this.mLevelListState = as;
        setConstantState(as);
        onLevelChange(getLevel());
    }
}