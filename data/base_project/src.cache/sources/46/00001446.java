package android.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: TransitionInflater.class */
public class TransitionInflater {
    private static final ArrayMap<Context, TransitionInflater> sInflaterMap = new ArrayMap<>();
    private Context mContext;
    SparseArray<Scene> mScenes = new SparseArray<>();

    private TransitionInflater(Context context) {
        this.mContext = context;
    }

    public static TransitionInflater from(Context context) {
        TransitionInflater inflater = sInflaterMap.get(context);
        if (inflater != null) {
            return inflater;
        }
        TransitionInflater inflater2 = new TransitionInflater(context);
        sInflaterMap.put(context, inflater2);
        return inflater2;
    }

    public Transition inflateTransition(int resource) {
        XmlResourceParser parser = this.mContext.getResources().getXml(resource);
        try {
            try {
                Transition createTransitionFromXml = createTransitionFromXml(parser, Xml.asAttributeSet(parser), null);
                parser.close();
                return createTransitionFromXml;
            } catch (IOException e) {
                InflateException ex = new InflateException(parser.getPositionDescription() + ": " + e.getMessage());
                ex.initCause(e);
                throw ex;
            } catch (XmlPullParserException e2) {
                InflateException ex2 = new InflateException(e2.getMessage());
                ex2.initCause(e2);
                throw ex2;
            }
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    public TransitionManager inflateTransitionManager(int resource, ViewGroup sceneRoot) {
        XmlResourceParser parser = this.mContext.getResources().getXml(resource);
        try {
            try {
                try {
                    TransitionManager createTransitionManagerFromXml = createTransitionManagerFromXml(parser, Xml.asAttributeSet(parser), sceneRoot);
                    parser.close();
                    return createTransitionManagerFromXml;
                } catch (IOException e) {
                    InflateException ex = new InflateException(parser.getPositionDescription() + ": " + e.getMessage());
                    ex.initCause(e);
                    throw ex;
                }
            } catch (XmlPullParserException e2) {
                InflateException ex2 = new InflateException(e2.getMessage());
                ex2.initCause(e2);
                throw ex2;
            }
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:55:0x01ab, code lost:
        return r9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.transition.Transition createTransitionFromXml(org.xmlpull.v1.XmlPullParser r6, android.util.AttributeSet r7, android.transition.TransitionSet r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 428
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionInflater.createTransitionFromXml(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.transition.TransitionSet):android.transition.Transition");
    }

    private void getTargetIds(XmlPullParser parser, AttributeSet attrs, Transition transition) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        ArrayList<Integer> targetIds = new ArrayList<>();
        while (true) {
            int type = parser.next();
            if ((type != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if (name.equals("target")) {
                        TypedArray a = this.mContext.obtainStyledAttributes(attrs, R.styleable.TransitionTarget);
                        int id = a.getResourceId(0, -1);
                        if (id >= 0) {
                            targetIds.add(Integer.valueOf(id));
                        }
                    } else {
                        throw new RuntimeException("Unknown scene name: " + parser.getName());
                    }
                }
            }
        }
        int numTargets = targetIds.size();
        if (numTargets > 0) {
            for (int i = 0; i < numTargets; i++) {
                transition.addTarget(targetIds.get(i).intValue());
            }
        }
    }

    private Transition loadTransition(Transition transition, AttributeSet attrs) throws Resources.NotFoundException {
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, R.styleable.Transition);
        long duration = a.getInt(1, -1);
        if (duration >= 0) {
            transition.setDuration(duration);
        }
        long startDelay = a.getInt(2, -1);
        if (startDelay > 0) {
            transition.setStartDelay(startDelay);
        }
        int resID = a.getResourceId(0, 0);
        if (resID > 0) {
            transition.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, resID));
        }
        a.recycle();
        return transition;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x008f, code lost:
        return r11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.transition.TransitionManager createTransitionManagerFromXml(org.xmlpull.v1.XmlPullParser r6, android.util.AttributeSet r7, android.view.ViewGroup r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r5 = this;
            r0 = r6
            int r0 = r0.getDepth()
            r10 = r0
            r0 = 0
            r11 = r0
        Lb:
            r0 = r6
            int r0 = r0.next()
            r1 = r0
            r9 = r1
            r1 = 3
            if (r0 != r1) goto L23
            r0 = r6
            int r0 = r0.getDepth()
            r1 = r10
            if (r0 <= r1) goto L8d
        L23:
            r0 = r9
            r1 = 1
            if (r0 == r1) goto L8d
            r0 = r9
            r1 = 2
            if (r0 == r1) goto L32
            goto Lb
        L32:
            r0 = r6
            java.lang.String r0 = r0.getName()
            r12 = r0
            r0 = r12
            java.lang.String r1 = "transitionManager"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L50
            android.transition.TransitionManager r0 = new android.transition.TransitionManager
            r1 = r0
            r1.<init>()
            r11 = r0
            goto L8a
        L50:
            r0 = r12
            java.lang.String r1 = "transition"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L6a
            r0 = r11
            if (r0 == 0) goto L6a
            r0 = r5
            r1 = r7
            r2 = r8
            r3 = r11
            r0.loadTransition(r1, r2, r3)
            goto L8a
        L6a:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            r1 = r0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = r2
            r3.<init>()
            java.lang.String r3 = "Unknown scene name: "
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r6
            java.lang.String r3 = r3.getName()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r0
        L8a:
            goto Lb
        L8d:
            r0 = r11
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionInflater.createTransitionManagerFromXml(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.ViewGroup):android.transition.TransitionManager");
    }

    private void loadTransition(AttributeSet attrs, ViewGroup sceneRoot, TransitionManager transitionManager) throws Resources.NotFoundException {
        Transition transition;
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, R.styleable.TransitionManager);
        int transitionId = a.getResourceId(2, -1);
        Scene fromScene = null;
        Scene toScene = null;
        int fromId = a.getResourceId(0, -1);
        if (fromId >= 0) {
            fromScene = Scene.getSceneForLayout(sceneRoot, fromId, this.mContext);
        }
        int toId = a.getResourceId(1, -1);
        if (toId >= 0) {
            toScene = Scene.getSceneForLayout(sceneRoot, toId, this.mContext);
        }
        if (transitionId >= 0 && (transition = inflateTransition(transitionId)) != null) {
            if (fromScene != null) {
                if (toScene == null) {
                    throw new RuntimeException("No matching toScene for given fromScene for transition ID " + transitionId);
                }
                transitionManager.setTransition(fromScene, toScene, transition);
            } else if (toId >= 0) {
                transitionManager.setTransition(toScene, transition);
            }
        }
        a.recycle();
    }
}