package android.widget;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/* loaded from: TimePicker.class */
public class TimePicker extends FrameLayout {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: android.widget.TimePicker.1
        @Override // android.widget.TimePicker.OnTimeChangedListener
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        }
    };
    private boolean mIs24HourView;
    private boolean mIsAm;
    private final NumberPicker mHourSpinner;
    private final NumberPicker mMinuteSpinner;
    private final NumberPicker mAmPmSpinner;
    private final EditText mHourSpinnerInput;
    private final EditText mMinuteSpinnerInput;
    private final EditText mAmPmSpinnerInput;
    private final TextView mDivider;
    private final Button mAmPmButton;
    private final String[] mAmPmStrings;
    private boolean mIsEnabled;
    private OnTimeChangedListener mOnTimeChangedListener;
    private Calendar mTempCalendar;
    private Locale mCurrentLocale;
    private boolean mHourWithTwoDigit;
    private char mHourFormat;

    /* loaded from: TimePicker$OnTimeChangedListener.class */
    public interface OnTimeChangedListener {
        void onTimeChanged(TimePicker timePicker, int i, int i2);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.timePickerStyle);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsEnabled = true;
        setCurrentLocale(Locale.getDefault());
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.TimePicker, defStyle, 0);
        int layoutResourceId = attributesArray.getResourceId(0, R.layout.time_picker);
        attributesArray.recycle();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, (ViewGroup) this, true);
        this.mHourSpinner = (NumberPicker) findViewById(R.id.hour);
        this.mHourSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.widget.TimePicker.2
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                TimePicker.this.updateInputState();
                if (!TimePicker.this.is24HourView() && ((oldVal == 11 && newVal == 12) || (oldVal == 12 && newVal == 11))) {
                    TimePicker.this.mIsAm = !TimePicker.this.mIsAm;
                    TimePicker.this.updateAmPmControl();
                }
                TimePicker.this.onTimeChanged();
            }
        });
        this.mHourSpinnerInput = (EditText) this.mHourSpinner.findViewById(R.id.numberpicker_input);
        this.mHourSpinnerInput.setImeOptions(5);
        this.mDivider = (TextView) findViewById(R.id.divider);
        if (this.mDivider != null) {
            setDividerText();
        }
        this.mMinuteSpinner = (NumberPicker) findViewById(R.id.minute);
        this.mMinuteSpinner.setMinValue(0);
        this.mMinuteSpinner.setMaxValue(59);
        this.mMinuteSpinner.setOnLongPressUpdateInterval(100L);
        this.mMinuteSpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
        this.mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.widget.TimePicker.3
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                TimePicker.this.updateInputState();
                int minValue = TimePicker.this.mMinuteSpinner.getMinValue();
                int maxValue = TimePicker.this.mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = TimePicker.this.mHourSpinner.getValue() + 1;
                    if (!TimePicker.this.is24HourView() && newHour == 12) {
                        TimePicker.this.mIsAm = !TimePicker.this.mIsAm;
                        TimePicker.this.updateAmPmControl();
                    }
                    TimePicker.this.mHourSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour2 = TimePicker.this.mHourSpinner.getValue() - 1;
                    if (!TimePicker.this.is24HourView() && newHour2 == 11) {
                        TimePicker.this.mIsAm = !TimePicker.this.mIsAm;
                        TimePicker.this.updateAmPmControl();
                    }
                    TimePicker.this.mHourSpinner.setValue(newHour2);
                }
                TimePicker.this.onTimeChanged();
            }
        });
        this.mMinuteSpinnerInput = (EditText) this.mMinuteSpinner.findViewById(R.id.numberpicker_input);
        this.mMinuteSpinnerInput.setImeOptions(5);
        this.mAmPmStrings = new DateFormatSymbols().getAmPmStrings();
        View amPmView = findViewById(R.id.amPm);
        if (amPmView instanceof Button) {
            this.mAmPmSpinner = null;
            this.mAmPmSpinnerInput = null;
            this.mAmPmButton = (Button) amPmView;
            this.mAmPmButton.setOnClickListener(new View.OnClickListener() { // from class: android.widget.TimePicker.4
                @Override // android.view.View.OnClickListener
                public void onClick(View button) {
                    button.requestFocus();
                    TimePicker.this.mIsAm = !TimePicker.this.mIsAm;
                    TimePicker.this.updateAmPmControl();
                    TimePicker.this.onTimeChanged();
                }
            });
        } else {
            this.mAmPmButton = null;
            this.mAmPmSpinner = (NumberPicker) amPmView;
            this.mAmPmSpinner.setMinValue(0);
            this.mAmPmSpinner.setMaxValue(1);
            this.mAmPmSpinner.setDisplayedValues(this.mAmPmStrings);
            this.mAmPmSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: android.widget.TimePicker.5
                @Override // android.widget.NumberPicker.OnValueChangeListener
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    TimePicker.this.updateInputState();
                    picker.requestFocus();
                    TimePicker.this.mIsAm = !TimePicker.this.mIsAm;
                    TimePicker.this.updateAmPmControl();
                    TimePicker.this.onTimeChanged();
                }
            });
            this.mAmPmSpinnerInput = (EditText) this.mAmPmSpinner.findViewById(R.id.numberpicker_input);
            this.mAmPmSpinnerInput.setImeOptions(6);
        }
        if (isAmPmAtStart()) {
            ViewGroup amPmParent = (ViewGroup) findViewById(R.id.timePickerLayout);
            amPmParent.removeView(amPmView);
            amPmParent.addView(amPmView, 0);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) amPmView.getLayoutParams();
            int startMargin = lp.getMarginStart();
            int endMargin = lp.getMarginEnd();
            if (startMargin != endMargin) {
                lp.setMarginStart(endMargin);
                lp.setMarginEnd(startMargin);
            }
        }
        getHourFormatData();
        updateHourControl();
        updateMinuteControl();
        updateAmPmControl();
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
        setCurrentHour(Integer.valueOf(this.mTempCalendar.get(11)));
        setCurrentMinute(Integer.valueOf(this.mTempCalendar.get(12)));
        if (!isEnabled()) {
            setEnabled(false);
        }
        setContentDescriptions();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    private void getHourFormatData() {
        Locale defaultLocale = Locale.getDefault();
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(defaultLocale, this.mIs24HourView ? "Hm" : "hm");
        int lengthPattern = bestDateTimePattern.length();
        this.mHourWithTwoDigit = false;
        for (int i = 0; i < lengthPattern; i++) {
            char c = bestDateTimePattern.charAt(i);
            if (c == 'H' || c == 'h' || c == 'K' || c == 'k') {
                this.mHourFormat = c;
                if (i + 1 < lengthPattern && c == bestDateTimePattern.charAt(i + 1)) {
                    this.mHourWithTwoDigit = true;
                    return;
                }
                return;
            }
        }
    }

    private boolean isAmPmAtStart() {
        Locale defaultLocale = Locale.getDefault();
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(defaultLocale, "hm");
        return bestDateTimePattern.startsWith(FullBackup.APK_TREE_TOKEN);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        if (this.mIsEnabled == enabled) {
            return;
        }
        super.setEnabled(enabled);
        this.mMinuteSpinner.setEnabled(enabled);
        if (this.mDivider != null) {
            this.mDivider.setEnabled(enabled);
        }
        this.mHourSpinner.setEnabled(enabled);
        if (this.mAmPmSpinner != null) {
            this.mAmPmSpinner.setEnabled(enabled);
        } else {
            this.mAmPmButton.setEnabled(enabled);
        }
        this.mIsEnabled = enabled;
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(this.mCurrentLocale)) {
            return;
        }
        this.mCurrentLocale = locale;
        this.mTempCalendar = Calendar.getInstance(locale);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TimePicker$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        private final int mHour;
        private final int mMinute;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.TimePicker.SavedState.1
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

        private SavedState(Parcelable superState, int hour, int minute) {
            super(superState);
            this.mHour = hour;
            this.mMinute = minute;
        }

        private SavedState(Parcel in) {
            super(in);
            this.mHour = in.readInt();
            this.mMinute = in.readInt();
        }

        public int getHour() {
            return this.mHour;
        }

        public int getMinute() {
            return this.mMinute;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mHour);
            dest.writeInt(this.mMinute);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getCurrentHour().intValue(), getCurrentMinute().intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentHour(Integer.valueOf(ss.getHour()));
        setCurrentMinute(Integer.valueOf(ss.getMinute()));
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mOnTimeChangedListener = onTimeChangedListener;
    }

    public Integer getCurrentHour() {
        int currentHour = this.mHourSpinner.getValue();
        if (is24HourView()) {
            return Integer.valueOf(currentHour);
        }
        if (this.mIsAm) {
            return Integer.valueOf(currentHour % 12);
        }
        return Integer.valueOf((currentHour % 12) + 12);
    }

    public void setCurrentHour(Integer currentHour) {
        setCurrentHour(currentHour, true);
    }

    private void setCurrentHour(Integer currentHour, boolean notifyTimeChanged) {
        if (currentHour == null || currentHour == getCurrentHour()) {
            return;
        }
        if (!is24HourView()) {
            if (currentHour.intValue() >= 12) {
                this.mIsAm = false;
                if (currentHour.intValue() > 12) {
                    currentHour = Integer.valueOf(currentHour.intValue() - 12);
                }
            } else {
                this.mIsAm = true;
                if (currentHour.intValue() == 0) {
                    currentHour = 12;
                }
            }
            updateAmPmControl();
        }
        this.mHourSpinner.setValue(currentHour.intValue());
        if (notifyTimeChanged) {
            onTimeChanged();
        }
    }

    public void setIs24HourView(Boolean is24HourView) {
        if (this.mIs24HourView == is24HourView.booleanValue()) {
            return;
        }
        int currentHour = getCurrentHour().intValue();
        this.mIs24HourView = is24HourView.booleanValue();
        getHourFormatData();
        updateHourControl();
        setCurrentHour(Integer.valueOf(currentHour), false);
        updateMinuteControl();
        updateAmPmControl();
    }

    public boolean is24HourView() {
        return this.mIs24HourView;
    }

    public Integer getCurrentMinute() {
        return Integer.valueOf(this.mMinuteSpinner.getValue());
    }

    public void setCurrentMinute(Integer currentMinute) {
        if (currentMinute == getCurrentMinute()) {
            return;
        }
        this.mMinuteSpinner.setValue(currentMinute.intValue());
        onTimeChanged();
    }

    private void setDividerText() {
        String separatorText;
        Locale defaultLocale = Locale.getDefault();
        String skeleton = this.mIs24HourView ? "Hm" : "hm";
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(defaultLocale, skeleton);
        int hourIndex = bestDateTimePattern.lastIndexOf(72);
        if (hourIndex == -1) {
            hourIndex = bestDateTimePattern.lastIndexOf(104);
        }
        if (hourIndex == -1) {
            separatorText = Separators.COLON;
        } else {
            int minuteIndex = bestDateTimePattern.indexOf(109, hourIndex + 1);
            if (minuteIndex == -1) {
                separatorText = Character.toString(bestDateTimePattern.charAt(hourIndex + 1));
            } else {
                separatorText = bestDateTimePattern.substring(hourIndex + 1, minuteIndex);
            }
        }
        this.mDivider.setText(separatorText);
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mHourSpinner.getBaseline();
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        int flags;
        super.onPopulateAccessibilityEvent(event);
        if (this.mIs24HourView) {
            flags = 1 | 128;
        } else {
            flags = 1 | 64;
        }
        this.mTempCalendar.set(11, getCurrentHour().intValue());
        this.mTempCalendar.set(12, getCurrentMinute().intValue());
        String selectedDateUtterance = DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), flags);
        event.getText().add(selectedDateUtterance);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TimePicker.class.getName());
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TimePicker.class.getName());
    }

    private void updateHourControl() {
        if (is24HourView()) {
            if (this.mHourFormat == 'k') {
                this.mHourSpinner.setMinValue(1);
                this.mHourSpinner.setMaxValue(24);
            } else {
                this.mHourSpinner.setMinValue(0);
                this.mHourSpinner.setMaxValue(23);
            }
        } else if (this.mHourFormat == 'K') {
            this.mHourSpinner.setMinValue(0);
            this.mHourSpinner.setMaxValue(11);
        } else {
            this.mHourSpinner.setMinValue(1);
            this.mHourSpinner.setMaxValue(12);
        }
        this.mHourSpinner.setFormatter(this.mHourWithTwoDigit ? NumberPicker.getTwoDigitFormatter() : null);
    }

    private void updateMinuteControl() {
        if (is24HourView()) {
            this.mMinuteSpinnerInput.setImeOptions(6);
        } else {
            this.mMinuteSpinnerInput.setImeOptions(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAmPmControl() {
        if (is24HourView()) {
            if (this.mAmPmSpinner != null) {
                this.mAmPmSpinner.setVisibility(8);
            } else {
                this.mAmPmButton.setVisibility(8);
            }
        } else {
            int index = this.mIsAm ? 0 : 1;
            if (this.mAmPmSpinner != null) {
                this.mAmPmSpinner.setValue(index);
                this.mAmPmSpinner.setVisibility(0);
            } else {
                this.mAmPmButton.setText(this.mAmPmStrings[index]);
                this.mAmPmButton.setVisibility(0);
            }
        }
        sendAccessibilityEvent(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        sendAccessibilityEvent(4);
        if (this.mOnTimeChangedListener != null) {
            this.mOnTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue());
        }
    }

    private void setContentDescriptions() {
        trySetContentDescription(this.mMinuteSpinner, R.id.increment, R.string.time_picker_increment_minute_button);
        trySetContentDescription(this.mMinuteSpinner, R.id.decrement, R.string.time_picker_decrement_minute_button);
        trySetContentDescription(this.mHourSpinner, R.id.increment, R.string.time_picker_increment_hour_button);
        trySetContentDescription(this.mHourSpinner, R.id.decrement, R.string.time_picker_decrement_hour_button);
        if (this.mAmPmSpinner != null) {
            trySetContentDescription(this.mAmPmSpinner, R.id.increment, R.string.time_picker_increment_set_pm_button);
            trySetContentDescription(this.mAmPmSpinner, R.id.decrement, R.string.time_picker_decrement_set_am_button);
        }
    }

    private void trySetContentDescription(View root, int viewId, int contDescResId) {
        View target = root.findViewById(viewId);
        if (target != null) {
            target.setContentDescription(this.mContext.getString(contDescResId));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInputState() {
        InputMethodManager inputMethodManager = InputMethodManager.peekInstance();
        if (inputMethodManager != null) {
            if (inputMethodManager.isActive(this.mHourSpinnerInput)) {
                this.mHourSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(this.mMinuteSpinnerInput)) {
                this.mMinuteSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(this.mAmPmSpinnerInput)) {
                this.mAmPmSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }
}