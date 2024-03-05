package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import com.android.internal.R;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import libcore.icu.ICU;

/* loaded from: DatePicker.class */
public class DatePicker extends FrameLayout {
    private static final String LOG_TAG = DatePicker.class.getSimpleName();
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final boolean DEFAULT_CALENDAR_VIEW_SHOWN = true;
    private static final boolean DEFAULT_SPINNERS_SHOWN = true;
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private final LinearLayout mSpinners;
    private final NumberPicker mDaySpinner;
    private final NumberPicker mMonthSpinner;
    private final NumberPicker mYearSpinner;
    private final EditText mDaySpinnerInput;
    private final EditText mMonthSpinnerInput;
    private final EditText mYearSpinnerInput;
    private final CalendarView mCalendarView;
    private Locale mCurrentLocale;
    private OnDateChangedListener mOnDateChangedListener;
    private String[] mShortMonths;
    private final DateFormat mDateFormat;
    private int mNumberOfMonths;
    private Calendar mTempDate;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private Calendar mCurrentDate;
    private boolean mIsEnabled;

    /* loaded from: DatePicker$OnDateChangedListener.class */
    public interface OnDateChangedListener {
        void onDateChanged(DatePicker datePicker, int i, int i2, int i3);
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 16843612);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        this.mIsEnabled = true;
        setCurrentLocale(Locale.getDefault());
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.DatePicker, defStyle, 0);
        boolean spinnersShown = attributesArray.getBoolean(4, true);
        boolean calendarViewShown = attributesArray.getBoolean(5, true);
        int startYear = attributesArray.getInt(0, DEFAULT_START_YEAR);
        int endYear = attributesArray.getInt(1, 2100);
        String minDate = attributesArray.getString(2);
        String maxDate = attributesArray.getString(3);
        int layoutResourceId = attributesArray.getResourceId(6, R.layout.date_picker);
        attributesArray.recycle();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, (ViewGroup) this, true);
        NumberPicker.OnValueChangeListener onChangeListener = new NumberPicker.OnValueChangeListener() { // from class: android.widget.DatePicker.1
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                DatePicker.this.updateInputState();
                DatePicker.this.mTempDate.setTimeInMillis(DatePicker.this.mCurrentDate.getTimeInMillis());
                if (picker == DatePicker.this.mDaySpinner) {
                    int maxDayOfMonth = DatePicker.this.mTempDate.getActualMaximum(5);
                    if (oldVal == maxDayOfMonth && newVal == 1) {
                        DatePicker.this.mTempDate.add(5, 1);
                    } else if (oldVal != 1 || newVal != maxDayOfMonth) {
                        DatePicker.this.mTempDate.add(5, newVal - oldVal);
                    } else {
                        DatePicker.this.mTempDate.add(5, -1);
                    }
                } else if (picker == DatePicker.this.mMonthSpinner) {
                    if (oldVal == 11 && newVal == 0) {
                        DatePicker.this.mTempDate.add(2, 1);
                    } else if (oldVal != 0 || newVal != 11) {
                        DatePicker.this.mTempDate.add(2, newVal - oldVal);
                    } else {
                        DatePicker.this.mTempDate.add(2, -1);
                    }
                } else if (picker == DatePicker.this.mYearSpinner) {
                    DatePicker.this.mTempDate.set(1, newVal);
                } else {
                    throw new IllegalArgumentException();
                }
                DatePicker.this.setDate(DatePicker.this.mTempDate.get(1), DatePicker.this.mTempDate.get(2), DatePicker.this.mTempDate.get(5));
                DatePicker.this.updateSpinners();
                DatePicker.this.updateCalendarView();
                DatePicker.this.notifyDateChanged();
            }
        };
        this.mSpinners = (LinearLayout) findViewById(R.id.pickers);
        this.mCalendarView = (CalendarView) findViewById(R.id.calendar_view);
        this.mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() { // from class: android.widget.DatePicker.2
            @Override // android.widget.CalendarView.OnDateChangeListener
            public void onSelectedDayChange(CalendarView view, int year, int month, int monthDay) {
                DatePicker.this.setDate(year, month, monthDay);
                DatePicker.this.updateSpinners();
                DatePicker.this.notifyDateChanged();
            }
        });
        this.mDaySpinner = (NumberPicker) findViewById(R.id.day);
        this.mDaySpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
        this.mDaySpinner.setOnLongPressUpdateInterval(100L);
        this.mDaySpinner.setOnValueChangedListener(onChangeListener);
        this.mDaySpinnerInput = (EditText) this.mDaySpinner.findViewById(R.id.numberpicker_input);
        this.mMonthSpinner = (NumberPicker) findViewById(R.id.month);
        this.mMonthSpinner.setMinValue(0);
        this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
        this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
        this.mMonthSpinner.setOnLongPressUpdateInterval(200L);
        this.mMonthSpinner.setOnValueChangedListener(onChangeListener);
        this.mMonthSpinnerInput = (EditText) this.mMonthSpinner.findViewById(R.id.numberpicker_input);
        this.mYearSpinner = (NumberPicker) findViewById(R.id.year);
        this.mYearSpinner.setOnLongPressUpdateInterval(100L);
        this.mYearSpinner.setOnValueChangedListener(onChangeListener);
        this.mYearSpinnerInput = (EditText) this.mYearSpinner.findViewById(R.id.numberpicker_input);
        if (!spinnersShown && !calendarViewShown) {
            setSpinnersShown(true);
        } else {
            setSpinnersShown(spinnersShown);
            setCalendarViewShown(calendarViewShown);
        }
        this.mTempDate.clear();
        if (!TextUtils.isEmpty(minDate)) {
            if (!parseDate(minDate, this.mTempDate)) {
                this.mTempDate.set(startYear, 0, 1);
            }
        } else {
            this.mTempDate.set(startYear, 0, 1);
        }
        setMinDate(this.mTempDate.getTimeInMillis());
        this.mTempDate.clear();
        if (!TextUtils.isEmpty(maxDate)) {
            if (!parseDate(maxDate, this.mTempDate)) {
                this.mTempDate.set(endYear, 11, 31);
            }
        } else {
            this.mTempDate.set(endYear, 11, 31);
        }
        setMaxDate(this.mTempDate.getTimeInMillis());
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), null);
        reorderSpinners();
        setContentDescriptions();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    public long getMinDate() {
        return this.mCalendarView.getMinDate();
    }

    public void setMinDate(long minDate) {
        this.mTempDate.setTimeInMillis(minDate);
        if (this.mTempDate.get(1) == this.mMinDate.get(1) && this.mTempDate.get(6) != this.mMinDate.get(6)) {
            return;
        }
        this.mMinDate.setTimeInMillis(minDate);
        this.mCalendarView.setMinDate(minDate);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
            updateCalendarView();
        }
        updateSpinners();
    }

    public long getMaxDate() {
        return this.mCalendarView.getMaxDate();
    }

    public void setMaxDate(long maxDate) {
        this.mTempDate.setTimeInMillis(maxDate);
        if (this.mTempDate.get(1) == this.mMaxDate.get(1) && this.mTempDate.get(6) != this.mMaxDate.get(6)) {
            return;
        }
        this.mMaxDate.setTimeInMillis(maxDate);
        this.mCalendarView.setMaxDate(maxDate);
        if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
            updateCalendarView();
        }
        updateSpinners();
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        if (this.mIsEnabled == enabled) {
            return;
        }
        super.setEnabled(enabled);
        this.mDaySpinner.setEnabled(enabled);
        this.mMonthSpinner.setEnabled(enabled);
        this.mYearSpinner.setEnabled(enabled);
        this.mCalendarView.setEnabled(enabled);
        this.mIsEnabled = enabled;
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        String selectedDateUtterance = DateUtils.formatDateTime(this.mContext, this.mCurrentDate.getTimeInMillis(), 20);
        event.getText().add(selectedDateUtterance);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(DatePicker.class.getName());
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(DatePicker.class.getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    public boolean getCalendarViewShown() {
        return this.mCalendarView.getVisibility() == 0;
    }

    public CalendarView getCalendarView() {
        return this.mCalendarView;
    }

    public void setCalendarViewShown(boolean shown) {
        this.mCalendarView.setVisibility(shown ? 0 : 8);
    }

    public boolean getSpinnersShown() {
        return this.mSpinners.isShown();
    }

    public void setSpinnersShown(boolean shown) {
        this.mSpinners.setVisibility(shown ? 0 : 8);
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(this.mCurrentLocale)) {
            return;
        }
        this.mCurrentLocale = locale;
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
        this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, locale);
        this.mNumberOfMonths = this.mTempDate.getActualMaximum(2) + 1;
        this.mShortMonths = new DateFormatSymbols().getShortMonths();
        if (usingNumericMonths()) {
            this.mShortMonths = new String[this.mNumberOfMonths];
            for (int i = 0; i < this.mNumberOfMonths; i++) {
                this.mShortMonths[i] = String.format("%d", Integer.valueOf(i + 1));
            }
        }
    }

    private boolean usingNumericMonths() {
        return Character.isDigit(this.mShortMonths[0].charAt(0));
    }

    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        }
        long currentTimeMillis = oldCalendar.getTimeInMillis();
        Calendar newCalendar = Calendar.getInstance(locale);
        newCalendar.setTimeInMillis(currentTimeMillis);
        return newCalendar;
    }

    private void reorderSpinners() {
        this.mSpinners.removeAllViews();
        String pattern = ICU.getBestDateTimePattern("yyyyMMMdd", Locale.getDefault().toString());
        char[] order = ICU.getDateFormatOrder(pattern);
        int spinnerCount = order.length;
        for (int i = 0; i < spinnerCount; i++) {
            switch (order[i]) {
                case 'M':
                    this.mSpinners.addView(this.mMonthSpinner);
                    setImeOptions(this.mMonthSpinner, spinnerCount, i);
                    break;
                case 'd':
                    this.mSpinners.addView(this.mDaySpinner);
                    setImeOptions(this.mDaySpinner, spinnerCount, i);
                    break;
                case 'y':
                    this.mSpinners.addView(this.mYearSpinner);
                    setImeOptions(this.mYearSpinner, spinnerCount, i);
                    break;
                default:
                    throw new IllegalArgumentException(Arrays.toString(order));
            }
        }
    }

    public void updateDate(int year, int month, int dayOfMonth) {
        if (!isNewDate(year, month, dayOfMonth)) {
            return;
        }
        setDate(year, month, dayOfMonth);
        updateSpinners();
        updateCalendarView();
        notifyDateChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getYear(), getMonth(), getDayOfMonth());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setDate(ss.mYear, ss.mMonth, ss.mDay);
        updateSpinners();
        updateCalendarView();
    }

    public void init(int year, int monthOfYear, int dayOfMonth, OnDateChangedListener onDateChangedListener) {
        setDate(year, monthOfYear, dayOfMonth);
        updateSpinners();
        updateCalendarView();
        this.mOnDateChangedListener = onDateChangedListener;
    }

    private boolean parseDate(String date, Calendar outDate) {
        try {
            outDate.setTime(this.mDateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            Log.w(LOG_TAG, "Date: " + date + " not in format: " + DATE_FORMAT);
            return false;
        }
    }

    private boolean isNewDate(int year, int month, int dayOfMonth) {
        return (this.mCurrentDate.get(1) == year && this.mCurrentDate.get(2) == dayOfMonth && this.mCurrentDate.get(5) == month) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDate(int year, int month, int dayOfMonth) {
        this.mCurrentDate.set(year, month, dayOfMonth);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        } else if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSpinners() {
        if (this.mCurrentDate.equals(this.mMinDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.getActualMaximum(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else if (this.mCurrentDate.equals(this.mMaxDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.getActualMinimum(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.getActualMinimum(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else {
            this.mDaySpinner.setMinValue(1);
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(true);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(0);
            this.mMonthSpinner.setMaxValue(11);
            this.mMonthSpinner.setWrapSelectorWheel(true);
        }
        String[] displayedValues = (String[]) Arrays.copyOfRange(this.mShortMonths, this.mMonthSpinner.getMinValue(), this.mMonthSpinner.getMaxValue() + 1);
        this.mMonthSpinner.setDisplayedValues(displayedValues);
        this.mYearSpinner.setMinValue(this.mMinDate.get(1));
        this.mYearSpinner.setMaxValue(this.mMaxDate.get(1));
        this.mYearSpinner.setWrapSelectorWheel(false);
        this.mYearSpinner.setValue(this.mCurrentDate.get(1));
        this.mMonthSpinner.setValue(this.mCurrentDate.get(2));
        this.mDaySpinner.setValue(this.mCurrentDate.get(5));
        if (usingNumericMonths()) {
            this.mMonthSpinnerInput.setRawInputType(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCalendarView() {
        this.mCalendarView.setDate(this.mCurrentDate.getTimeInMillis(), false, false);
    }

    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    public int getMonth() {
        return this.mCurrentDate.get(2);
    }

    public int getDayOfMonth() {
        return this.mCurrentDate.get(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDateChanged() {
        sendAccessibilityEvent(4);
        if (this.mOnDateChangedListener != null) {
            this.mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(), getDayOfMonth());
        }
    }

    private void setImeOptions(NumberPicker spinner, int spinnerCount, int spinnerIndex) {
        int imeOptions;
        if (spinnerIndex < spinnerCount - 1) {
            imeOptions = 5;
        } else {
            imeOptions = 6;
        }
        TextView input = (TextView) spinner.findViewById(R.id.numberpicker_input);
        input.setImeOptions(imeOptions);
    }

    private void setContentDescriptions() {
        trySetContentDescription(this.mDaySpinner, R.id.increment, R.string.date_picker_increment_day_button);
        trySetContentDescription(this.mDaySpinner, R.id.decrement, R.string.date_picker_decrement_day_button);
        trySetContentDescription(this.mMonthSpinner, R.id.increment, R.string.date_picker_increment_month_button);
        trySetContentDescription(this.mMonthSpinner, R.id.decrement, R.string.date_picker_decrement_month_button);
        trySetContentDescription(this.mYearSpinner, R.id.increment, R.string.date_picker_increment_year_button);
        trySetContentDescription(this.mYearSpinner, R.id.decrement, R.string.date_picker_decrement_year_button);
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
            if (inputMethodManager.isActive(this.mYearSpinnerInput)) {
                this.mYearSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(this.mMonthSpinnerInput)) {
                this.mMonthSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(this.mDaySpinnerInput)) {
                this.mDaySpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DatePicker$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        private final int mYear;
        private final int mMonth;
        private final int mDay;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.DatePicker.SavedState.1
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

        private SavedState(Parcelable superState, int year, int month, int day) {
            super(superState);
            this.mYear = year;
            this.mMonth = month;
            this.mDay = day;
        }

        private SavedState(Parcel in) {
            super(in);
            this.mYear = in.readInt();
            this.mMonth = in.readInt();
            this.mDay = in.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mYear);
            dest.writeInt(this.mMonth);
            dest.writeInt(this.mDay);
        }
    }
}