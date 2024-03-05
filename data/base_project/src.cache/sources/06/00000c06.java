package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.internal.R;

/* loaded from: SwitchPreference.class */
public class SwitchPreference extends TwoStatePreference {
    private CharSequence mSwitchOn;
    private CharSequence mSwitchOff;
    private final Listener mListener;

    /* loaded from: SwitchPreference$Listener.class */
    private class Listener implements CompoundButton.OnCheckedChangeListener {
        private Listener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!SwitchPreference.this.callChangeListener(Boolean.valueOf(isChecked))) {
                buttonView.setChecked(!isChecked);
            } else {
                SwitchPreference.this.setChecked(isChecked);
            }
        }
    }

    public SwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mListener = new Listener();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchPreference, defStyle, 0);
        setSummaryOn(a.getString(0));
        setSummaryOff(a.getString(1));
        setSwitchTextOn(a.getString(3));
        setSwitchTextOff(a.getString(4));
        setDisableDependentsState(a.getBoolean(2, false));
        a.recycle();
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16843629);
    }

    public SwitchPreference(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        View checkableView = view.findViewById(R.id.switchWidget);
        if (checkableView != null && (checkableView instanceof Checkable)) {
            ((Checkable) checkableView).setChecked(this.mChecked);
            sendAccessibilityEvent(checkableView);
            if (checkableView instanceof Switch) {
                Switch switchView = (Switch) checkableView;
                switchView.setTextOn(this.mSwitchOn);
                switchView.setTextOff(this.mSwitchOff);
                switchView.setOnCheckedChangeListener(this.mListener);
            }
        }
        syncSummaryView(view);
    }

    public void setSwitchTextOn(CharSequence onText) {
        this.mSwitchOn = onText;
        notifyChanged();
    }

    public void setSwitchTextOff(CharSequence offText) {
        this.mSwitchOff = offText;
        notifyChanged();
    }

    public void setSwitchTextOn(int resId) {
        setSwitchTextOn(getContext().getString(resId));
    }

    public void setSwitchTextOff(int resId) {
        setSwitchTextOff(getContext().getString(resId));
    }

    public CharSequence getSwitchTextOn() {
        return this.mSwitchOn;
    }

    public CharSequence getSwitchTextOff() {
        return this.mSwitchOff;
    }
}