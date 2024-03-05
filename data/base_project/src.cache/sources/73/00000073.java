package android.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.animation.AnimationUtils;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: AnimatorInflater.class */
public class AnimatorInflater {
    private static final int TOGETHER = 0;
    private static final int SEQUENTIALLY = 1;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_COLOR = 4;
    private static final int VALUE_TYPE_CUSTOM = 5;

    public static Animator loadAnimator(Context context, int id) throws Resources.NotFoundException {
        XmlResourceParser parser = null;
        try {
            try {
                try {
                    parser = context.getResources().getAnimation(id);
                    Animator createAnimatorFromXml = createAnimatorFromXml(context, parser);
                    if (parser != null) {
                        parser.close();
                    }
                    return createAnimatorFromXml;
                } catch (XmlPullParserException ex) {
                    Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                    rnf.initCause(ex);
                    throw rnf;
                }
            } catch (IOException ex2) {
                Resources.NotFoundException rnf2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                rnf2.initCause(ex2);
                throw rnf2;
            }
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
            throw th;
        }
    }

    private static Animator createAnimatorFromXml(Context c, XmlPullParser parser) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(c, parser, Xml.asAttributeSet(parser), null, 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x00e0, code lost:
        if (r9 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00e5, code lost:
        if (r12 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00e8, code lost:
        r0 = new android.animation.Animator[r12.size()];
        r16 = 0;
        r0 = r12.iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0103, code lost:
        if (r0.hasNext() == false) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0106, code lost:
        r1 = r16;
        r16 = r16 + 1;
        r0[r1] = r0.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0121, code lost:
        if (r10 != 0) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0124, code lost:
        r9.playTogether(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x012d, code lost:
        r9.playSequentially(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0135, code lost:
        return r11;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static android.animation.Animator createAnimatorFromXml(android.content.Context r6, org.xmlpull.v1.XmlPullParser r7, android.util.AttributeSet r8, android.animation.AnimatorSet r9, int r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 310
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorInflater.createAnimatorFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.animation.AnimatorSet, int):android.animation.Animator");
    }

    private static ObjectAnimator loadObjectAnimator(Context context, AttributeSet attrs) throws Resources.NotFoundException {
        ObjectAnimator anim = new ObjectAnimator();
        loadAnimator(context, attrs, anim);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PropertyAnimator);
        String propertyName = a.getString(0);
        anim.setPropertyName(propertyName);
        a.recycle();
        return anim;
    }

    private static ValueAnimator loadAnimator(Context context, AttributeSet attrs, ValueAnimator anim) throws Resources.NotFoundException {
        int valueTo;
        int valueFrom;
        int valueTo2;
        float valueTo3;
        float valueFrom2;
        float valueTo4;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Animator);
        long duration = a.getInt(1, 300);
        long startDelay = a.getInt(2, 0);
        int valueType = a.getInt(7, 0);
        if (anim == null) {
            anim = new ValueAnimator();
        }
        boolean getFloats = valueType == 0;
        TypedValue tvFrom = a.peekValue(5);
        boolean hasFrom = tvFrom != null;
        int fromType = hasFrom ? tvFrom.type : 0;
        TypedValue tvTo = a.peekValue(6);
        boolean hasTo = tvTo != null;
        int toType = hasTo ? tvTo.type : 0;
        if ((hasFrom && fromType >= 28 && fromType <= 31) || (hasTo && toType >= 28 && toType <= 31)) {
            getFloats = false;
            anim.setEvaluator(new ArgbEvaluator());
        }
        if (getFloats) {
            if (hasFrom) {
                if (fromType == 5) {
                    valueFrom2 = a.getDimension(5, 0.0f);
                } else {
                    valueFrom2 = a.getFloat(5, 0.0f);
                }
                if (hasTo) {
                    if (toType == 5) {
                        valueTo4 = a.getDimension(6, 0.0f);
                    } else {
                        valueTo4 = a.getFloat(6, 0.0f);
                    }
                    anim.setFloatValues(valueFrom2, valueTo4);
                } else {
                    anim.setFloatValues(valueFrom2);
                }
            } else {
                if (toType == 5) {
                    valueTo3 = a.getDimension(6, 0.0f);
                } else {
                    valueTo3 = a.getFloat(6, 0.0f);
                }
                anim.setFloatValues(valueTo3);
            }
        } else if (hasFrom) {
            if (fromType == 5) {
                valueFrom = (int) a.getDimension(5, 0.0f);
            } else if (fromType >= 28 && fromType <= 31) {
                valueFrom = a.getColor(5, 0);
            } else {
                valueFrom = a.getInt(5, 0);
            }
            if (hasTo) {
                if (toType == 5) {
                    valueTo2 = (int) a.getDimension(6, 0.0f);
                } else if (toType >= 28 && toType <= 31) {
                    valueTo2 = a.getColor(6, 0);
                } else {
                    valueTo2 = a.getInt(6, 0);
                }
                anim.setIntValues(valueFrom, valueTo2);
            } else {
                anim.setIntValues(valueFrom);
            }
        } else if (hasTo) {
            if (toType == 5) {
                valueTo = (int) a.getDimension(6, 0.0f);
            } else if (toType >= 28 && toType <= 31) {
                valueTo = a.getColor(6, 0);
            } else {
                valueTo = a.getInt(6, 0);
            }
            anim.setIntValues(valueTo);
        }
        anim.setDuration(duration);
        anim.setStartDelay(startDelay);
        if (a.hasValue(3)) {
            anim.setRepeatCount(a.getInt(3, 0));
        }
        if (a.hasValue(4)) {
            anim.setRepeatMode(a.getInt(4, 1));
        }
        if (0 != 0) {
            anim.setEvaluator(null);
        }
        int resID = a.getResourceId(0, 0);
        if (resID > 0) {
            anim.setInterpolator(AnimationUtils.loadInterpolator(context, resID));
        }
        a.recycle();
        return anim;
    }
}