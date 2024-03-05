package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.internal.R;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: LayoutInflater.class */
public abstract class LayoutInflater {
    private final boolean DEBUG = false;
    protected final Context mContext;
    private boolean mFactorySet;
    private Factory mFactory;
    private Factory2 mFactory2;
    private Factory2 mPrivateFactory;
    private Filter mFilter;
    final Object[] mConstructorArgs;
    static final Class<?>[] mConstructorSignature = {Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();
    private HashMap<String, Boolean> mFilterMap;
    private static final String TAG_MERGE = "merge";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_1995 = "blink";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";

    /* loaded from: LayoutInflater$Factory.class */
    public interface Factory {
        View onCreateView(String str, Context context, AttributeSet attributeSet);
    }

    /* loaded from: LayoutInflater$Factory2.class */
    public interface Factory2 extends Factory {
        View onCreateView(View view, String str, Context context, AttributeSet attributeSet);
    }

    /* loaded from: LayoutInflater$Filter.class */
    public interface Filter {
        boolean onLoadClass(Class cls);
    }

    public abstract LayoutInflater cloneInContext(Context context);

    /* loaded from: LayoutInflater$FactoryMerger.class */
    private static class FactoryMerger implements Factory2 {
        private final Factory mF1;
        private final Factory mF2;
        private final Factory2 mF12;
        private final Factory2 mF22;

        FactoryMerger(Factory f1, Factory2 f12, Factory f2, Factory2 f22) {
            this.mF1 = f1;
            this.mF2 = f2;
            this.mF12 = f12;
            this.mF22 = f22;
        }

        @Override // android.view.LayoutInflater.Factory
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View v = this.mF1.onCreateView(name, context, attrs);
            return v != null ? v : this.mF2.onCreateView(name, context, attrs);
        }

        @Override // android.view.LayoutInflater.Factory2
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            View v = this.mF12 != null ? this.mF12.onCreateView(parent, name, context, attrs) : this.mF1.onCreateView(name, context, attrs);
            return v != null ? v : this.mF22 != null ? this.mF22.onCreateView(parent, name, context, attrs) : this.mF2.onCreateView(name, context, attrs);
        }
    }

    protected LayoutInflater(Context context) {
        this.DEBUG = false;
        this.mConstructorArgs = new Object[2];
        this.mContext = context;
    }

    protected LayoutInflater(LayoutInflater original, Context newContext) {
        this.DEBUG = false;
        this.mConstructorArgs = new Object[2];
        this.mContext = newContext;
        this.mFactory = original.mFactory;
        this.mFactory2 = original.mFactory2;
        this.mPrivateFactory = original.mPrivateFactory;
        this.mFilter = original.mFilter;
    }

    public static LayoutInflater from(Context context) {
        LayoutInflater LayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }

    public Context getContext() {
        return this.mContext;
    }

    public final Factory getFactory() {
        return this.mFactory;
    }

    public final Factory2 getFactory2() {
        return this.mFactory2;
    }

    public void setFactory(Factory factory) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        this.mFactorySet = true;
        if (this.mFactory == null) {
            this.mFactory = factory;
        } else {
            this.mFactory = new FactoryMerger(factory, null, this.mFactory, this.mFactory2);
        }
    }

    public void setFactory2(Factory2 factory) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        this.mFactorySet = true;
        if (this.mFactory == null) {
            this.mFactory2 = factory;
            this.mFactory = factory;
            return;
        }
        this.mFactory = new FactoryMerger(factory, factory, this.mFactory, this.mFactory2);
    }

    public void setPrivateFactory(Factory2 factory) {
        this.mPrivateFactory = factory;
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public void setFilter(Filter filter) {
        this.mFilter = filter;
        if (filter != null) {
            this.mFilterMap = new HashMap<>();
        }
    }

    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        XmlResourceParser parser = getContext().getResources().getLayout(resource);
        try {
            View inflate = inflate(parser, root, attachToRoot);
            parser.close();
            return inflate;
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        int type;
        View temp;
        View view;
        synchronized (this.mConstructorArgs) {
            Trace.traceBegin(8L, "inflate");
            AttributeSet attrs = Xml.asAttributeSet(parser);
            Context lastContext = (Context) this.mConstructorArgs[0];
            this.mConstructorArgs[0] = this.mContext;
            View result = root;
            while (true) {
                try {
                    type = parser.next();
                    if (type == 2 || type == 1) {
                        break;
                    }
                } catch (IOException e) {
                    InflateException ex = new InflateException(parser.getPositionDescription() + ": " + e.getMessage());
                    ex.initCause(e);
                    throw ex;
                } catch (XmlPullParserException e2) {
                    InflateException ex2 = new InflateException(e2.getMessage());
                    ex2.initCause(e2);
                    throw ex2;
                }
            }
            if (type != 2) {
                throw new InflateException(parser.getPositionDescription() + ": No start tag found!");
            }
            String name = parser.getName();
            if (TAG_MERGE.equals(name)) {
                if (root == null || !attachToRoot) {
                    throw new InflateException("<merge /> can be used only with a valid ViewGroup root and attachToRoot=true");
                }
                rInflate(parser, root, attrs, false);
            } else {
                if (TAG_1995.equals(name)) {
                    temp = new BlinkLayout(this.mContext, attrs);
                } else {
                    temp = createViewFromTag(root, name, attrs);
                }
                ViewGroup.LayoutParams params = null;
                if (root != null) {
                    params = root.generateLayoutParams(attrs);
                    if (!attachToRoot) {
                        temp.setLayoutParams(params);
                    }
                }
                rInflate(parser, temp, attrs, true);
                if (root != null && attachToRoot) {
                    root.addView(temp, params);
                }
                if (root == null || !attachToRoot) {
                    result = temp;
                }
            }
            this.mConstructorArgs[0] = lastContext;
            this.mConstructorArgs[1] = null;
            Trace.traceEnd(8L);
            view = result;
        }
        return view;
    }

    public final View createView(String name, String prefix, AttributeSet attrs) throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        Class cls = null;
        try {
            try {
                try {
                    try {
                        Trace.traceBegin(8L, name);
                        if (constructor == null) {
                            cls = this.mContext.getClassLoader().loadClass(prefix != null ? prefix + name : name).asSubclass(View.class);
                            if (this.mFilter != null && cls != null && !this.mFilter.onLoadClass(cls)) {
                                failNotAllowed(name, prefix, attrs);
                            }
                            constructor = cls.getConstructor(mConstructorSignature);
                            sConstructorMap.put(name, constructor);
                        } else if (this.mFilter != null) {
                            Boolean allowedState = this.mFilterMap.get(name);
                            if (allowedState == null) {
                                cls = this.mContext.getClassLoader().loadClass(prefix != null ? prefix + name : name).asSubclass(View.class);
                                boolean allowed = cls != null && this.mFilter.onLoadClass(cls);
                                this.mFilterMap.put(name, Boolean.valueOf(allowed));
                                if (!allowed) {
                                    failNotAllowed(name, prefix, attrs);
                                }
                            } else if (allowedState.equals(Boolean.FALSE)) {
                                failNotAllowed(name, prefix, attrs);
                            }
                        }
                        Object[] args = this.mConstructorArgs;
                        args[1] = attrs;
                        View view = constructor.newInstance(args);
                        if (view instanceof ViewStub) {
                            ViewStub viewStub = (ViewStub) view;
                            viewStub.setLayoutInflater(this);
                        }
                        return view;
                    } catch (ClassCastException e) {
                        InflateException ie = new InflateException(attrs.getPositionDescription() + ": Class is not a View " + (prefix != null ? prefix + name : name));
                        ie.initCause(e);
                        throw ie;
                    } catch (NoSuchMethodException e2) {
                        InflateException ie2 = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + (prefix != null ? prefix + name : name));
                        ie2.initCause(e2);
                        throw ie2;
                    }
                } catch (Exception e3) {
                    InflateException ie3 = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + (cls == null ? MediaStore.UNKNOWN_STRING : cls.getName()));
                    ie3.initCause(e3);
                    throw ie3;
                }
            } catch (ClassNotFoundException e4) {
                throw e4;
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private void failNotAllowed(String name, String prefix, AttributeSet attrs) {
        throw new InflateException(attrs.getPositionDescription() + ": Class not allowed to be inflated " + (prefix != null ? prefix + name : name));
    }

    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        return createView(name, "android.view.", attrs);
    }

    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return onCreateView(name, attrs);
    }

    View createViewFromTag(View parent, String name, AttributeSet attrs) {
        View view;
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            if (this.mFactory2 != null) {
                view = this.mFactory2.onCreateView(parent, name, this.mContext, attrs);
            } else {
                view = this.mFactory != null ? this.mFactory.onCreateView(name, this.mContext, attrs) : null;
            }
            if (view == null && this.mPrivateFactory != null) {
                view = this.mPrivateFactory.onCreateView(parent, name, this.mContext, attrs);
            }
            if (view == null) {
                if (-1 == name.indexOf(46)) {
                    view = onCreateView(parent, name, attrs);
                } else {
                    view = createView(name, null, attrs);
                }
            }
            return view;
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
            ie.initCause(e2);
            throw ie;
        } catch (Exception e3) {
            InflateException ie2 = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
            ie2.initCause(e3);
            throw ie2;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x00ef, code lost:
        if (r10 == false) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00f2, code lost:
        r8.onFinishInflate();
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00f6, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void rInflate(org.xmlpull.v1.XmlPullParser r7, android.view.View r8, android.util.AttributeSet r9, boolean r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 247
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.LayoutInflater.rInflate(org.xmlpull.v1.XmlPullParser, android.view.View, android.util.AttributeSet, boolean):void");
    }

    private void parseRequestFocus(XmlPullParser parser, View parent) throws XmlPullParserException, IOException {
        int type;
        parent.requestFocus();
        int currentDepth = parser.getDepth();
        do {
            type = parser.next();
            if (type == 3 && parser.getDepth() <= currentDepth) {
                return;
            }
        } while (type != 1);
    }

    private void parseInclude(XmlPullParser parser, View parent, AttributeSet attrs) throws XmlPullParserException, IOException {
        int type;
        int type2;
        if (parent instanceof ViewGroup) {
            int layout = attrs.getAttributeResourceValue(null, "layout", 0);
            if (layout == 0) {
                String value = attrs.getAttributeValue(null, "layout");
                if (value == null) {
                    throw new InflateException("You must specifiy a layout in the include tag: <include layout=\"@layout/layoutID\" />");
                }
                throw new InflateException("You must specifiy a valid layout reference. The layout ID " + value + " is not valid.");
            }
            XmlResourceParser childParser = getContext().getResources().getLayout(layout);
            try {
                AttributeSet childAttrs = Xml.asAttributeSet(childParser);
                while (true) {
                    type = childParser.next();
                    if (type == 2 || type == 1) {
                        break;
                    }
                }
                if (type != 2) {
                    throw new InflateException(childParser.getPositionDescription() + ": No start tag found!");
                }
                String childName = childParser.getName();
                if (TAG_MERGE.equals(childName)) {
                    rInflate(childParser, parent, childAttrs, false);
                } else {
                    View view = createViewFromTag(parent, childName, childAttrs);
                    ViewGroup group = (ViewGroup) parent;
                    try {
                        ViewGroup.LayoutParams params = group.generateLayoutParams(attrs);
                        if (params != null) {
                            view.setLayoutParams(params);
                        }
                    } catch (RuntimeException e) {
                        ViewGroup.LayoutParams params2 = group.generateLayoutParams(childAttrs);
                        if (params2 != null) {
                            view.setLayoutParams(params2);
                        }
                    }
                    rInflate(childParser, view, childAttrs, true);
                    TypedArray a = this.mContext.obtainStyledAttributes(attrs, R.styleable.View, 0, 0);
                    int id = a.getResourceId(8, -1);
                    int visibility = a.getInt(20, -1);
                    a.recycle();
                    if (id != -1) {
                        view.setId(id);
                    }
                    switch (visibility) {
                        case 0:
                            view.setVisibility(0);
                            break;
                        case 1:
                            view.setVisibility(4);
                            break;
                        case 2:
                            view.setVisibility(8);
                            break;
                    }
                    group.addView(view);
                }
                int currentDepth = parser.getDepth();
                do {
                    type2 = parser.next();
                    if (type2 == 3 && parser.getDepth() <= currentDepth) {
                        return;
                    }
                } while (type2 != 1);
                return;
            } finally {
                childParser.close();
            }
        }
        throw new InflateException("<include /> can only be used inside of a ViewGroup");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LayoutInflater$BlinkLayout.class */
    public static class BlinkLayout extends FrameLayout {
        private static final int MESSAGE_BLINK = 66;
        private static final int BLINK_DELAY = 500;
        private boolean mBlink;
        private boolean mBlinkState;
        private final Handler mHandler;

        public BlinkLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mHandler = new Handler(new Handler.Callback() { // from class: android.view.LayoutInflater.BlinkLayout.1
                @Override // android.os.Handler.Callback
                public boolean handleMessage(Message msg) {
                    if (msg.what == 66) {
                        if (BlinkLayout.this.mBlink) {
                            BlinkLayout.this.mBlinkState = !BlinkLayout.this.mBlinkState;
                            BlinkLayout.this.makeBlink();
                        }
                        BlinkLayout.this.invalidate();
                        return true;
                    }
                    return false;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void makeBlink() {
            Message message = this.mHandler.obtainMessage(66);
            this.mHandler.sendMessageDelayed(message, 500L);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.mBlink = true;
            this.mBlinkState = true;
            makeBlink();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.mBlink = false;
            this.mBlinkState = true;
            this.mHandler.removeMessages(66);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            if (this.mBlinkState) {
                super.dispatchDraw(canvas);
            }
        }
    }
}