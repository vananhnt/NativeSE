package android.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;

/* loaded from: ContextThemeWrapper.class */
public class ContextThemeWrapper extends ContextWrapper {
    private Context mBase;
    private int mThemeResource;
    private Resources.Theme mTheme;
    private LayoutInflater mInflater;
    private Configuration mOverrideConfiguration;
    private Resources mResources;

    public ContextThemeWrapper() {
        super(null);
    }

    public ContextThemeWrapper(Context base, int themeres) {
        super(base);
        this.mBase = base;
        this.mThemeResource = themeres;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.content.ContextWrapper
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        this.mBase = newBase;
    }

    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (this.mResources != null) {
            throw new IllegalStateException("getResources() has already been called");
        }
        if (this.mOverrideConfiguration != null) {
            throw new IllegalStateException("Override configuration has already been set");
        }
        this.mOverrideConfiguration = new Configuration(overrideConfiguration);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        if (this.mResources != null) {
            return this.mResources;
        }
        if (this.mOverrideConfiguration == null) {
            this.mResources = super.getResources();
            return this.mResources;
        }
        Context resc = createConfigurationContext(this.mOverrideConfiguration);
        this.mResources = resc.getResources();
        return this.mResources;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void setTheme(int resid) {
        this.mThemeResource = resid;
        initializeTheme();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public int getThemeResId() {
        return this.mThemeResource;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources.Theme getTheme() {
        if (this.mTheme != null) {
            return this.mTheme;
        }
        this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getApplicationInfo().targetSdkVersion);
        initializeTheme();
        return this.mTheme;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (this.mInflater == null) {
                this.mInflater = LayoutInflater.from(this.mBase).cloneInContext(this);
            }
            return this.mInflater;
        }
        return this.mBase.getSystemService(name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        theme.applyStyle(resid, true);
    }

    private void initializeTheme() {
        boolean first = this.mTheme == null;
        if (first) {
            this.mTheme = getResources().newTheme();
            Resources.Theme theme = this.mBase.getTheme();
            if (theme != null) {
                this.mTheme.setTo(theme);
            }
        }
        onApplyThemeResource(this.mTheme, this.mThemeResource, first);
    }
}