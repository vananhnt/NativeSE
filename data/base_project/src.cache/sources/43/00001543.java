package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import com.android.internal.R;
import com.android.internal.view.menu.MenuItemImpl;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: MenuInflater.class */
public class MenuInflater {
    private static final String LOG_TAG = "MenuInflater";
    private static final String XML_MENU = "menu";
    private static final String XML_GROUP = "group";
    private static final String XML_ITEM = "item";
    private static final int NO_ID = 0;
    private static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = {Context.class};
    private static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
    private final Object[] mActionViewConstructorArguments;
    private final Object[] mActionProviderConstructorArguments;
    private Context mContext;
    private Object mRealOwner;

    public MenuInflater(Context context) {
        this.mContext = context;
        this.mRealOwner = context;
        this.mActionViewConstructorArguments = new Object[]{context};
        this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    }

    public MenuInflater(Context context, Object realOwner) {
        this.mContext = context;
        this.mRealOwner = realOwner;
        this.mActionViewConstructorArguments = new Object[]{context};
        this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    }

    public void inflate(int menuRes, Menu menu) {
        XmlResourceParser parser = null;
        try {
            try {
                parser = this.mContext.getResources().getLayout(menuRes);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                parseMenu(parser, attrs, menu);
                if (parser != null) {
                    parser.close();
                }
            } catch (IOException e) {
                throw new InflateException("Error inflating menu XML", e);
            } catch (XmlPullParserException e2) {
                throw new InflateException("Error inflating menu XML", e2);
            }
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0066, code lost:
        r14 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x006b, code lost:
        if (r14 != false) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0070, code lost:
        switch(r10) {
            case 1: goto L58;
            case 2: goto L12;
            case 3: goto L30;
            default: goto L62;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x008e, code lost:
        if (r12 == false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0094, code lost:
        r0 = r6.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00a3, code lost:
        if (r0.equals("group") == false) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00a6, code lost:
        r0.readGroup(r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00b6, code lost:
        if (r0.equals(android.view.MenuInflater.XML_ITEM) == false) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00b9, code lost:
        r0.readItem(r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00c9, code lost:
        if (r0.equals(android.view.MenuInflater.XML_MENU) == false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00cc, code lost:
        r0 = r0.addSubMenuItem();
        parseMenu(r6, r7, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00de, code lost:
        r12 = true;
        r13 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00e8, code lost:
        r0 = r6.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00f2, code lost:
        if (r12 == false) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00fc, code lost:
        if (r0.equals(r13) == false) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00ff, code lost:
        r12 = false;
        r13 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x010f, code lost:
        if (r0.equals("group") == false) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0112, code lost:
        r0.resetGroup();
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0121, code lost:
        if (r0.equals(android.view.MenuInflater.XML_ITEM) == false) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0129, code lost:
        if (r0.hasAddedItem() != false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0131, code lost:
        if (r0.itemActionProvider == null) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x013c, code lost:
        if (r0.itemActionProvider.hasSubMenu() == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x013f, code lost:
        r0.addSubMenuItem();
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0148, code lost:
        r0.addItem();
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0157, code lost:
        if (r0.equals(android.view.MenuInflater.XML_MENU) == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x015a, code lost:
        r14 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0169, code lost:
        throw new java.lang.RuntimeException("Unexpected end of document");
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x016a, code lost:
        r10 = r6.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0175, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseMenu(org.xmlpull.v1.XmlPullParser r6, android.util.AttributeSet r7, android.view.Menu r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 374
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.MenuInflater.parseMenu(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.Menu):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MenuInflater$InflatedOnMenuItemClickListener.class */
    public static class InflatedOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        private static final Class<?>[] PARAM_TYPES = {MenuItem.class};
        private Object mRealOwner;
        private Method mMethod;

        public InflatedOnMenuItemClickListener(Object realOwner, String methodName) {
            this.mRealOwner = realOwner;
            Class<?> c = realOwner.getClass();
            try {
                this.mMethod = c.getMethod(methodName, PARAM_TYPES);
            } catch (Exception e) {
                InflateException ex = new InflateException("Couldn't resolve menu item onClick handler " + methodName + " in class " + c.getName());
                ex.initCause(e);
                throw ex;
            }
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            try {
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    return ((Boolean) this.mMethod.invoke(this.mRealOwner, item)).booleanValue();
                }
                this.mMethod.invoke(this.mRealOwner, item);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MenuInflater$MenuState.class */
    public class MenuState {
        private Menu menu;
        private int groupId;
        private int groupCategory;
        private int groupOrder;
        private int groupCheckable;
        private boolean groupVisible;
        private boolean groupEnabled;
        private boolean itemAdded;
        private int itemId;
        private int itemCategoryOrder;
        private CharSequence itemTitle;
        private CharSequence itemTitleCondensed;
        private int itemIconResId;
        private char itemAlphabeticShortcut;
        private char itemNumericShortcut;
        private int itemCheckable;
        private boolean itemChecked;
        private boolean itemVisible;
        private boolean itemEnabled;
        private int itemShowAsAction;
        private int itemActionViewLayout;
        private String itemActionViewClassName;
        private String itemActionProviderClassName;
        private String itemListenerMethodName;
        private ActionProvider itemActionProvider;
        private static final int defaultGroupId = 0;
        private static final int defaultItemId = 0;
        private static final int defaultItemCategory = 0;
        private static final int defaultItemOrder = 0;
        private static final int defaultItemCheckable = 0;
        private static final boolean defaultItemChecked = false;
        private static final boolean defaultItemVisible = true;
        private static final boolean defaultItemEnabled = true;

        public MenuState(Menu menu) {
            this.menu = menu;
            resetGroup();
        }

        public void resetGroup() {
            this.groupId = 0;
            this.groupCategory = 0;
            this.groupOrder = 0;
            this.groupCheckable = 0;
            this.groupVisible = true;
            this.groupEnabled = true;
        }

        public void readGroup(AttributeSet attrs) {
            TypedArray a = MenuInflater.this.mContext.obtainStyledAttributes(attrs, R.styleable.MenuGroup);
            this.groupId = a.getResourceId(1, 0);
            this.groupCategory = a.getInt(3, 0);
            this.groupOrder = a.getInt(4, 0);
            this.groupCheckable = a.getInt(5, 0);
            this.groupVisible = a.getBoolean(2, true);
            this.groupEnabled = a.getBoolean(0, true);
            a.recycle();
        }

        public void readItem(AttributeSet attrs) {
            TypedArray a = MenuInflater.this.mContext.obtainStyledAttributes(attrs, R.styleable.MenuItem);
            this.itemId = a.getResourceId(2, 0);
            int category = a.getInt(5, this.groupCategory);
            int order = a.getInt(6, this.groupOrder);
            this.itemCategoryOrder = (category & (-65536)) | (order & 65535);
            this.itemTitle = a.getText(7);
            this.itemTitleCondensed = a.getText(8);
            this.itemIconResId = a.getResourceId(0, 0);
            this.itemAlphabeticShortcut = getShortcut(a.getString(9));
            this.itemNumericShortcut = getShortcut(a.getString(10));
            if (a.hasValue(11)) {
                this.itemCheckable = a.getBoolean(11, false) ? 1 : 0;
            } else {
                this.itemCheckable = this.groupCheckable;
            }
            this.itemChecked = a.getBoolean(3, false);
            this.itemVisible = a.getBoolean(4, this.groupVisible);
            this.itemEnabled = a.getBoolean(1, this.groupEnabled);
            this.itemShowAsAction = a.getInt(13, -1);
            this.itemListenerMethodName = a.getString(12);
            this.itemActionViewLayout = a.getResourceId(14, 0);
            this.itemActionViewClassName = a.getString(15);
            this.itemActionProviderClassName = a.getString(16);
            boolean hasActionProvider = this.itemActionProviderClassName != null;
            if (hasActionProvider && this.itemActionViewLayout == 0 && this.itemActionViewClassName == null) {
                this.itemActionProvider = (ActionProvider) newInstance(this.itemActionProviderClassName, MenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, MenuInflater.this.mActionProviderConstructorArguments);
            } else {
                if (hasActionProvider) {
                    Log.w(MenuInflater.LOG_TAG, "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                }
                this.itemActionProvider = null;
            }
            a.recycle();
            this.itemAdded = false;
        }

        private char getShortcut(String shortcutString) {
            if (shortcutString == null) {
                return (char) 0;
            }
            return shortcutString.charAt(0);
        }

        private void setItem(MenuItem item) {
            item.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled).setCheckable(this.itemCheckable >= 1).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId).setAlphabeticShortcut(this.itemAlphabeticShortcut).setNumericShortcut(this.itemNumericShortcut);
            if (this.itemShowAsAction >= 0) {
                item.setShowAsAction(this.itemShowAsAction);
            }
            if (this.itemListenerMethodName != null) {
                if (MenuInflater.this.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                item.setOnMenuItemClickListener(new InflatedOnMenuItemClickListener(MenuInflater.this.mRealOwner, this.itemListenerMethodName));
            }
            if (item instanceof MenuItemImpl) {
                MenuItemImpl impl = (MenuItemImpl) item;
                if (this.itemCheckable >= 2) {
                    impl.setExclusiveCheckable(true);
                }
            }
            boolean actionViewSpecified = false;
            if (this.itemActionViewClassName != null) {
                View actionView = (View) newInstance(this.itemActionViewClassName, MenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, MenuInflater.this.mActionViewConstructorArguments);
                item.setActionView(actionView);
                actionViewSpecified = true;
            }
            if (this.itemActionViewLayout > 0) {
                if (!actionViewSpecified) {
                    item.setActionView(this.itemActionViewLayout);
                } else {
                    Log.w(MenuInflater.LOG_TAG, "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                }
            }
            if (this.itemActionProvider != null) {
                item.setActionProvider(this.itemActionProvider);
            }
        }

        public void addItem() {
            this.itemAdded = true;
            setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
        }

        public SubMenu addSubMenuItem() {
            this.itemAdded = true;
            SubMenu subMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            setItem(subMenu.getItem());
            return subMenu;
        }

        public boolean hasAddedItem() {
            return this.itemAdded;
        }

        private <T> T newInstance(String className, Class<?>[] constructorSignature, Object[] arguments) {
            try {
                Class<?> clazz = MenuInflater.this.mContext.getClassLoader().loadClass(className);
                Constructor<?> constructor = clazz.getConstructor(constructorSignature);
                return (T) constructor.newInstance(arguments);
            } catch (Exception e) {
                Log.w(MenuInflater.LOG_TAG, "Cannot instantiate class: " + className, e);
                return null;
            }
        }
    }
}