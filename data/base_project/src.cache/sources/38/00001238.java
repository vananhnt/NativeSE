package android.support.v7.internal.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuItemWrapperICS;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: SupportMenuInflater.class */
public class SupportMenuInflater extends MenuInflater {
    private static final String LOG_TAG = "SupportMenuInflater";
    private static final int NO_ID = 0;
    private static final String XML_GROUP = "group";
    private static final String XML_ITEM = "item";
    private static final String XML_MENU = "menu";
    private final Object[] mActionProviderConstructorArguments;
    private final Object[] mActionViewConstructorArguments;
    private Context mContext;
    private Object mRealOwner;
    private static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = {Context.class};
    private static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SupportMenuInflater$InflatedOnMenuItemClickListener.class */
    public static class InflatedOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        private static final Class<?>[] PARAM_TYPES = {MenuItem.class};
        private Method mMethod;
        private Object mRealOwner;

        public InflatedOnMenuItemClickListener(Object obj, String str) {
            this.mRealOwner = obj;
            Class<?> cls = obj.getClass();
            try {
                this.mMethod = cls.getMethod(str, PARAM_TYPES);
            } catch (Exception e) {
                InflateException inflateException = new InflateException("Couldn't resolve menu item onClick handler " + str + " in class " + cls.getName());
                inflateException.initCause(e);
                throw inflateException;
            }
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem menuItem) {
            try {
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    return ((Boolean) this.mMethod.invoke(this.mRealOwner, menuItem)).booleanValue();
                }
                this.mMethod.invoke(this.mRealOwner, menuItem);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SupportMenuInflater$MenuState.class */
    public class MenuState {
        private static final int defaultGroupId = 0;
        private static final int defaultItemCategory = 0;
        private static final int defaultItemCheckable = 0;
        private static final boolean defaultItemChecked = false;
        private static final boolean defaultItemEnabled = true;
        private static final int defaultItemId = 0;
        private static final int defaultItemOrder = 0;
        private static final boolean defaultItemVisible = true;
        private int groupCategory;
        private int groupCheckable;
        private boolean groupEnabled;
        private int groupId;
        private int groupOrder;
        private boolean groupVisible;
        private ActionProvider itemActionProvider;
        private String itemActionProviderClassName;
        private String itemActionViewClassName;
        private int itemActionViewLayout;
        private boolean itemAdded;
        private char itemAlphabeticShortcut;
        private int itemCategoryOrder;
        private int itemCheckable;
        private boolean itemChecked;
        private boolean itemEnabled;
        private int itemIconResId;
        private int itemId;
        private String itemListenerMethodName;
        private char itemNumericShortcut;
        private int itemShowAsAction;
        private CharSequence itemTitle;
        private CharSequence itemTitleCondensed;
        private boolean itemVisible;
        private Menu menu;
        final SupportMenuInflater this$0;

        public MenuState(SupportMenuInflater supportMenuInflater, Menu menu) {
            this.this$0 = supportMenuInflater;
            this.menu = menu;
            resetGroup();
        }

        private char getShortcut(String str) {
            if (str == null) {
                return (char) 0;
            }
            return str.charAt(0);
        }

        private <T> T newInstance(String str, Class<?>[] clsArr, Object[] objArr) {
            try {
                return (T) this.this$0.mContext.getClassLoader().loadClass(str).getConstructor(clsArr).newInstance(objArr);
            } catch (Exception e) {
                Log.w(SupportMenuInflater.LOG_TAG, "Cannot instantiate class: " + str, e);
                return null;
            }
        }

        private void setItem(MenuItem menuItem) {
            menuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled).setCheckable(this.itemCheckable >= 1).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId).setAlphabeticShortcut(this.itemAlphabeticShortcut).setNumericShortcut(this.itemNumericShortcut);
            int i = this.itemShowAsAction;
            if (i >= 0) {
                MenuItemCompat.setShowAsAction(menuItem, i);
            }
            if (this.itemListenerMethodName != null) {
                if (this.this$0.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                menuItem.setOnMenuItemClickListener(new InflatedOnMenuItemClickListener(this.this$0.getRealOwner(), this.itemListenerMethodName));
            }
            if (menuItem instanceof MenuItemImpl) {
                MenuItemImpl menuItemImpl = (MenuItemImpl) menuItem;
            }
            if (this.itemCheckable >= 2) {
                if (menuItem instanceof MenuItemImpl) {
                    ((MenuItemImpl) menuItem).setExclusiveCheckable(true);
                } else if (menuItem instanceof MenuItemWrapperICS) {
                    ((MenuItemWrapperICS) menuItem).setExclusiveCheckable(true);
                }
            }
            boolean z = false;
            String str = this.itemActionViewClassName;
            if (str != null) {
                MenuItemCompat.setActionView(menuItem, (View) newInstance(str, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, this.this$0.mActionViewConstructorArguments));
                z = true;
            }
            int i2 = this.itemActionViewLayout;
            if (i2 > 0) {
                if (z) {
                    Log.w(SupportMenuInflater.LOG_TAG, "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                } else {
                    MenuItemCompat.setActionView(menuItem, i2);
                }
            }
            ActionProvider actionProvider = this.itemActionProvider;
            if (actionProvider != null) {
                MenuItemCompat.setActionProvider(menuItem, actionProvider);
            }
        }

        public void addItem() {
            this.itemAdded = true;
            setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
        }

        public SubMenu addSubMenuItem() {
            this.itemAdded = true;
            SubMenu addSubMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            setItem(addSubMenu.getItem());
            return addSubMenu;
        }

        public boolean hasAddedItem() {
            return this.itemAdded;
        }

        public void readGroup(AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = this.this$0.mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuGroup);
            this.groupId = obtainStyledAttributes.getResourceId(R.styleable.MenuGroup_android_id, 0);
            this.groupCategory = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_menuCategory, 0);
            this.groupOrder = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_orderInCategory, 0);
            this.groupCheckable = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_checkableBehavior, 0);
            this.groupVisible = obtainStyledAttributes.getBoolean(R.styleable.MenuGroup_android_visible, true);
            this.groupEnabled = obtainStyledAttributes.getBoolean(R.styleable.MenuGroup_android_enabled, true);
            obtainStyledAttributes.recycle();
        }

        public void readItem(AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = this.this$0.mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuItem);
            this.itemId = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_android_id, 0);
            this.itemCategoryOrder = ((-65536) & obtainStyledAttributes.getInt(R.styleable.MenuItem_android_menuCategory, this.groupCategory)) | (65535 & obtainStyledAttributes.getInt(R.styleable.MenuItem_android_orderInCategory, this.groupOrder));
            this.itemTitle = obtainStyledAttributes.getText(R.styleable.MenuItem_android_title);
            this.itemTitleCondensed = obtainStyledAttributes.getText(R.styleable.MenuItem_android_titleCondensed);
            this.itemIconResId = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_android_icon, 0);
            this.itemAlphabeticShortcut = getShortcut(obtainStyledAttributes.getString(R.styleable.MenuItem_android_alphabeticShortcut));
            this.itemNumericShortcut = getShortcut(obtainStyledAttributes.getString(R.styleable.MenuItem_android_numericShortcut));
            if (obtainStyledAttributes.hasValue(R.styleable.MenuItem_android_checkable)) {
                this.itemCheckable = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_checkable, false) ? 1 : 0;
            } else {
                this.itemCheckable = this.groupCheckable;
            }
            this.itemChecked = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_checked, false);
            this.itemVisible = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_visible, this.groupVisible);
            this.itemEnabled = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_enabled, this.groupEnabled);
            this.itemShowAsAction = obtainStyledAttributes.getInt(R.styleable.MenuItem_showAsAction, -1);
            this.itemListenerMethodName = obtainStyledAttributes.getString(R.styleable.MenuItem_android_onClick);
            this.itemActionViewLayout = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_actionLayout, 0);
            this.itemActionViewClassName = obtainStyledAttributes.getString(R.styleable.MenuItem_actionViewClass);
            this.itemActionProviderClassName = obtainStyledAttributes.getString(R.styleable.MenuItem_actionProviderClass);
            byte b = this.itemActionProviderClassName != null ? (byte) 1 : (byte) 0;
            if (b != 0 && this.itemActionViewLayout == 0 && this.itemActionViewClassName == null) {
                this.itemActionProvider = (ActionProvider) newInstance(this.itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, this.this$0.mActionProviderConstructorArguments);
            } else {
                if (b != 0) {
                    Log.w(SupportMenuInflater.LOG_TAG, "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                }
                this.itemActionProvider = null;
            }
            obtainStyledAttributes.recycle();
            this.itemAdded = false;
        }

        public void resetGroup() {
            this.groupId = 0;
            this.groupCategory = 0;
            this.groupOrder = 0;
            this.groupCheckable = 0;
            this.groupVisible = true;
            this.groupEnabled = true;
        }
    }

    public SupportMenuInflater(Context context) {
        super(context);
        this.mContext = context;
        this.mActionViewConstructorArguments = new Object[]{context};
        this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    }

    private Object findRealOwner(Object obj) {
        if (!(obj instanceof Activity) && (obj instanceof ContextWrapper)) {
            return findRealOwner(((ContextWrapper) obj).getBaseContext());
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object getRealOwner() {
        if (this.mRealOwner == null) {
            this.mRealOwner = findRealOwner(this.mContext);
        }
        return this.mRealOwner;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x006b, code lost:
        r13 = false;
        r14 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0074, code lost:
        if (r13 != false) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0079, code lost:
        switch(r14) {
            case 1: goto L63;
            case 2: goto L45;
            case 3: goto L15;
            default: goto L12;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0094, code lost:
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x009b, code lost:
        r0 = r6.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x00a5, code lost:
        if (r11 == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00ae, code lost:
        if (r0.equals(r8) == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00b1, code lost:
        r10 = false;
        r8 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00c0, code lost:
        if (r0.equals("group") == false) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00c3, code lost:
        r0.resetGroup();
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00d6, code lost:
        if (r0.equals(android.support.v7.internal.view.SupportMenuInflater.XML_ITEM) == false) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00de, code lost:
        if (r0.hasAddedItem() != false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00e6, code lost:
        if (r0.itemActionProvider == null) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00f1, code lost:
        if (r0.itemActionProvider.hasSubMenu() == false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00f4, code lost:
        r0.addSubMenuItem();
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0101, code lost:
        r0.addItem();
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x010d, code lost:
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x011b, code lost:
        if (r0.equals(android.support.v7.internal.view.SupportMenuInflater.XML_MENU) == false) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x011e, code lost:
        r13 = true;
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0128, code lost:
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0131, code lost:
        if (r11 == false) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0134, code lost:
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x013b, code lost:
        r0 = r6.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x014a, code lost:
        if (r0.equals("group") == false) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x014d, code lost:
        r0.readGroup(r7);
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0161, code lost:
        if (r0.equals(android.support.v7.internal.view.SupportMenuInflater.XML_ITEM) == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0164, code lost:
        r0.readItem(r7);
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0178, code lost:
        if (r0.equals(android.support.v7.internal.view.SupportMenuInflater.XML_MENU) == false) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x017b, code lost:
        parseMenu(r6, r7, r0.addSubMenuItem());
        r10 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x018d, code lost:
        r10 = true;
        r8 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x019f, code lost:
        throw new java.lang.RuntimeException("Unexpected end of document");
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x01a0, code lost:
        r14 = r6.next();
        r11 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x01af, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseMenu(org.xmlpull.v1.XmlPullParser r6, android.util.AttributeSet r7, android.view.Menu r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 435
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.internal.view.SupportMenuInflater.parseMenu(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.Menu):void");
    }

    @Override // android.view.MenuInflater
    public void inflate(int i, Menu menu) {
        if (!(menu instanceof SupportMenu)) {
            super.inflate(i, menu);
            return;
        }
        XmlResourceParser xmlResourceParser = null;
        XmlResourceParser xmlResourceParser2 = null;
        XmlResourceParser xmlResourceParser3 = null;
        try {
            try {
                try {
                    XmlResourceParser layout = this.mContext.getResources().getLayout(i);
                    xmlResourceParser3 = layout;
                    xmlResourceParser = layout;
                    xmlResourceParser2 = layout;
                    parseMenu(layout, Xml.asAttributeSet(layout), menu);
                    if (layout != null) {
                        layout.close();
                    }
                } catch (IOException e) {
                    XmlResourceParser xmlResourceParser4 = xmlResourceParser;
                    XmlResourceParser xmlResourceParser5 = xmlResourceParser;
                    throw new InflateException("Error inflating menu XML", e);
                }
            } catch (XmlPullParserException e2) {
                XmlResourceParser xmlResourceParser6 = xmlResourceParser2;
                XmlResourceParser xmlResourceParser7 = xmlResourceParser2;
                throw new InflateException("Error inflating menu XML", e2);
            }
        } catch (Throwable th) {
            if (xmlResourceParser3 != null) {
                xmlResourceParser3.close();
            }
            throw th;
        }
    }
}