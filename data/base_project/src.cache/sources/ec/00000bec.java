package android.preference;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: PreferenceCategory.class */
public class PreferenceCategory extends PreferenceGroup {
    private static final String TAG = "PreferenceCategory";

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, 16842892);
    }

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.PreferenceGroup
    public boolean onPrepareAddPreference(Preference preference) {
        if (preference instanceof PreferenceCategory) {
            throw new IllegalArgumentException("Cannot add a PreferenceCategory directly to a PreferenceCategory");
        }
        return super.onPrepareAddPreference(preference);
    }

    @Override // android.preference.Preference
    public boolean isEnabled() {
        return false;
    }

    @Override // android.preference.Preference
    public boolean shouldDisableDependents() {
        return !super.isEnabled();
    }
}