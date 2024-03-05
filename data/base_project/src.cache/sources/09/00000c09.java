package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

/* loaded from: TwoStatePreference.class */
public abstract class TwoStatePreference extends Preference {
    private CharSequence mSummaryOn;
    private CharSequence mSummaryOff;
    boolean mChecked;
    private boolean mCheckedSet;
    private boolean mSendClickAccessibilityEvent;
    private boolean mDisableDependentsState;

    public TwoStatePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TwoStatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoStatePreference(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onClick() {
        super.onClick();
        boolean newValue = !isChecked();
        this.mSendClickAccessibilityEvent = true;
        if (!callChangeListener(Boolean.valueOf(newValue))) {
            return;
        }
        setChecked(newValue);
    }

    public void setChecked(boolean checked) {
        boolean changed = this.mChecked != checked;
        if (changed || !this.mCheckedSet) {
            this.mChecked = checked;
            this.mCheckedSet = true;
            persistBoolean(checked);
            if (changed) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.preference.Preference
    public boolean shouldDisableDependents() {
        boolean shouldDisable = this.mDisableDependentsState ? this.mChecked : !this.mChecked;
        return shouldDisable || super.shouldDisableDependents();
    }

    public void setSummaryOn(CharSequence summary) {
        this.mSummaryOn = summary;
        if (isChecked()) {
            notifyChanged();
        }
    }

    public void setSummaryOn(int summaryResId) {
        setSummaryOn(getContext().getString(summaryResId));
    }

    public CharSequence getSummaryOn() {
        return this.mSummaryOn;
    }

    public void setSummaryOff(CharSequence summary) {
        this.mSummaryOff = summary;
        if (!isChecked()) {
            notifyChanged();
        }
    }

    public void setSummaryOff(int summaryResId) {
        setSummaryOff(getContext().getString(summaryResId));
    }

    public CharSequence getSummaryOff() {
        return this.mSummaryOff;
    }

    public boolean getDisableDependentsState() {
        return this.mDisableDependentsState;
    }

    public void setDisableDependentsState(boolean disableDependentsState) {
        this.mDisableDependentsState = disableDependentsState;
    }

    @Override // android.preference.Preference
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return Boolean.valueOf(a.getBoolean(index, false));
    }

    @Override // android.preference.Preference
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setChecked(restoreValue ? getPersistedBoolean(this.mChecked) : ((Boolean) defaultValue).booleanValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendAccessibilityEvent(View view) {
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(getContext());
        if (this.mSendClickAccessibilityEvent && accessibilityManager.isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(1);
            view.onInitializeAccessibilityEvent(event);
            view.dispatchPopulateAccessibilityEvent(event);
            accessibilityManager.sendAccessibilityEvent(event);
        }
        this.mSendClickAccessibilityEvent = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void syncSummaryView(View view) {
        TextView summaryView = (TextView) view.findViewById(16908304);
        if (summaryView != null) {
            boolean useDefaultSummary = true;
            if (this.mChecked && !TextUtils.isEmpty(this.mSummaryOn)) {
                summaryView.setText(this.mSummaryOn);
                useDefaultSummary = false;
            } else if (!this.mChecked && !TextUtils.isEmpty(this.mSummaryOff)) {
                summaryView.setText(this.mSummaryOff);
                useDefaultSummary = false;
            }
            if (useDefaultSummary) {
                CharSequence summary = getSummary();
                if (!TextUtils.isEmpty(summary)) {
                    summaryView.setText(summary);
                    useDefaultSummary = false;
                }
            }
            int newVisibility = 8;
            if (!useDefaultSummary) {
                newVisibility = 0;
            }
            if (newVisibility != summaryView.getVisibility()) {
                summaryView.setVisibility(newVisibility);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.checked = isChecked();
        return myState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setChecked(myState.checked);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TwoStatePreference$SavedState.class */
    public static class SavedState extends Preference.BaseSavedState {
        boolean checked;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.preference.TwoStatePreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            super(source);
            this.checked = source.readInt() == 1;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.checked ? 1 : 0);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }
}