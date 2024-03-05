package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import com.android.internal.R;

/* loaded from: CheckBoxPreference.class */
public class CheckBoxPreference extends TwoStatePreference {
    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxPreference, defStyle, 0);
        setSummaryOn(a.getString(0));
        setSummaryOff(a.getString(1));
        setDisableDependentsState(a.getBoolean(2, false));
        a.recycle();
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842895);
    }

    public CheckBoxPreference(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        View checkboxView = view.findViewById(16908289);
        if (checkboxView != null && (checkboxView instanceof Checkable)) {
            ((Checkable) checkboxView).setChecked(this.mChecked);
            sendAccessibilityEvent(checkboxView);
        }
        syncSummaryView(view);
    }
}