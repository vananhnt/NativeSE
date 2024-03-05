package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import com.android.internal.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import libcore.icu.LocaleData;

/* loaded from: CalendarView.class */
public class CalendarView extends FrameLayout {
    private static final String LOG_TAG = CalendarView.class.getSimpleName();
    private static final boolean DEFAULT_SHOW_WEEK_NUMBER = true;
    private static final long MILLIS_IN_DAY = 86400000;
    private static final int DAYS_PER_WEEK = 7;
    private static final long MILLIS_IN_WEEK = 604800000;
    private static final int SCROLL_HYST_WEEKS = 2;
    private static final int GOTO_SCROLL_DURATION = 1000;
    private static final int ADJUSTMENT_SCROLL_DURATION = 500;
    private static final int SCROLL_CHANGE_DELAY = 40;
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DEFAULT_MIN_DATE = "01/01/1900";
    private static final String DEFAULT_MAX_DATE = "01/01/2100";
    private static final int DEFAULT_SHOWN_WEEK_COUNT = 6;
    private static final int DEFAULT_DATE_TEXT_SIZE = 14;
    private static final int UNSCALED_SELECTED_DATE_VERTICAL_BAR_WIDTH = 6;
    private static final int UNSCALED_WEEK_MIN_VISIBLE_HEIGHT = 12;
    private static final int UNSCALED_LIST_SCROLL_TOP_OFFSET = 2;
    private static final int UNSCALED_BOTTOM_BUFFER = 20;
    private static final int UNSCALED_WEEK_SEPARATOR_LINE_WIDTH = 1;
    private static final int DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID = -1;
    private final int mWeekSeperatorLineWidth;
    private int mDateTextSize;
    private Drawable mSelectedDateVerticalBar;
    private final int mSelectedDateVerticalBarWidth;
    private int mSelectedWeekBackgroundColor;
    private int mFocusedMonthDateColor;
    private int mUnfocusedMonthDateColor;
    private int mWeekSeparatorLineColor;
    private int mWeekNumberColor;
    private int mWeekDayTextAppearanceResId;
    private int mDateTextAppearanceResId;
    private int mListScrollTopOffset;
    private int mWeekMinVisibleHeight;
    private int mBottomBuffer;
    private int mShownWeekCount;
    private boolean mShowWeekNumber;
    private int mDaysPerWeek;
    private float mFriction;
    private float mVelocityScale;
    private WeeksAdapter mAdapter;
    private ListView mListView;
    private TextView mMonthName;
    private ViewGroup mDayNamesHeader;
    private String[] mDayLabels;
    private int mFirstDayOfWeek;
    private int mCurrentMonthDisplayed;
    private long mPreviousScrollPosition;
    private boolean mIsScrollingUp;
    private int mPreviousScrollState;
    private int mCurrentScrollState;
    private OnDateChangeListener mOnDateChangeListener;
    private ScrollStateRunnable mScrollStateChangedRunnable;
    private Calendar mTempDate;
    private Calendar mFirstDayOfMonth;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private final DateFormat mDateFormat;
    private Locale mCurrentLocale;

    /* loaded from: CalendarView$OnDateChangeListener.class */
    public interface OnDateChangeListener {
        void onSelectedDayChange(CalendarView calendarView, int i, int i2, int i3);
    }

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, 0);
        this.mListScrollTopOffset = 2;
        this.mWeekMinVisibleHeight = 12;
        this.mBottomBuffer = 20;
        this.mDaysPerWeek = 7;
        this.mFriction = 0.05f;
        this.mVelocityScale = 0.333f;
        this.mCurrentMonthDisplayed = -1;
        this.mIsScrollingUp = false;
        this.mPreviousScrollState = 0;
        this.mCurrentScrollState = 0;
        this.mScrollStateChangedRunnable = new ScrollStateRunnable();
        this.mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        setCurrentLocale(Locale.getDefault());
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView, 16843613, 0);
        this.mShowWeekNumber = attributesArray.getBoolean(1, true);
        this.mFirstDayOfWeek = attributesArray.getInt(0, LocaleData.get(Locale.getDefault()).firstDayOfWeek.intValue());
        String minDate = attributesArray.getString(2);
        if (TextUtils.isEmpty(minDate) || !parseDate(minDate, this.mMinDate)) {
            parseDate(DEFAULT_MIN_DATE, this.mMinDate);
        }
        String maxDate = attributesArray.getString(3);
        if (TextUtils.isEmpty(maxDate) || !parseDate(maxDate, this.mMaxDate)) {
            parseDate(DEFAULT_MAX_DATE, this.mMaxDate);
        }
        if (this.mMaxDate.before(this.mMinDate)) {
            throw new IllegalArgumentException("Max date cannot be before min date.");
        }
        this.mShownWeekCount = attributesArray.getInt(4, 6);
        this.mSelectedWeekBackgroundColor = attributesArray.getColor(5, 0);
        this.mFocusedMonthDateColor = attributesArray.getColor(6, 0);
        this.mUnfocusedMonthDateColor = attributesArray.getColor(7, 0);
        this.mWeekSeparatorLineColor = attributesArray.getColor(9, 0);
        this.mWeekNumberColor = attributesArray.getColor(8, 0);
        this.mSelectedDateVerticalBar = attributesArray.getDrawable(10);
        this.mDateTextAppearanceResId = attributesArray.getResourceId(12, 16973894);
        updateDateTextSize();
        this.mWeekDayTextAppearanceResId = attributesArray.getResourceId(11, -1);
        attributesArray.recycle();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mWeekMinVisibleHeight = (int) TypedValue.applyDimension(1, 12.0f, displayMetrics);
        this.mListScrollTopOffset = (int) TypedValue.applyDimension(1, 2.0f, displayMetrics);
        this.mBottomBuffer = (int) TypedValue.applyDimension(1, 20.0f, displayMetrics);
        this.mSelectedDateVerticalBarWidth = (int) TypedValue.applyDimension(1, 6.0f, displayMetrics);
        this.mWeekSeperatorLineWidth = (int) TypedValue.applyDimension(1, 1.0f, displayMetrics);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = layoutInflater.inflate(R.layout.calendar_view, (ViewGroup) null, false);
        addView(content);
        this.mListView = (ListView) findViewById(16908298);
        this.mDayNamesHeader = (ViewGroup) content.findViewById(R.id.day_names);
        this.mMonthName = (TextView) content.findViewById(R.id.month_name);
        setUpHeader();
        setUpListView();
        setUpAdapter();
        this.mTempDate.setTimeInMillis(System.currentTimeMillis());
        if (this.mTempDate.before(this.mMinDate)) {
            goTo(this.mMinDate, false, true, true);
        } else if (this.mMaxDate.before(this.mTempDate)) {
            goTo(this.mMaxDate, false, true, true);
        } else {
            goTo(this.mTempDate, false, true, true);
        }
        invalidate();
    }

    public void setShownWeekCount(int count) {
        if (this.mShownWeekCount != count) {
            this.mShownWeekCount = count;
            invalidate();
        }
    }

    public int getShownWeekCount() {
        return this.mShownWeekCount;
    }

    public void setSelectedWeekBackgroundColor(int color) {
        if (this.mSelectedWeekBackgroundColor != color) {
            this.mSelectedWeekBackgroundColor = color;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasSelectedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    public int getSelectedWeekBackgroundColor() {
        return this.mSelectedWeekBackgroundColor;
    }

    public void setFocusedMonthDateColor(int color) {
        if (this.mFocusedMonthDateColor != color) {
            this.mFocusedMonthDateColor = color;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasFocusedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    public int getFocusedMonthDateColor() {
        return this.mFocusedMonthDateColor;
    }

    public void setUnfocusedMonthDateColor(int color) {
        if (this.mUnfocusedMonthDateColor != color) {
            this.mUnfocusedMonthDateColor = color;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasUnfocusedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    public int getUnfocusedMonthDateColor() {
        return this.mFocusedMonthDateColor;
    }

    public void setWeekNumberColor(int color) {
        if (this.mWeekNumberColor != color) {
            this.mWeekNumberColor = color;
            if (this.mShowWeekNumber) {
                invalidateAllWeekViews();
            }
        }
    }

    public int getWeekNumberColor() {
        return this.mWeekNumberColor;
    }

    public void setWeekSeparatorLineColor(int color) {
        if (this.mWeekSeparatorLineColor != color) {
            this.mWeekSeparatorLineColor = color;
            invalidateAllWeekViews();
        }
    }

    public int getWeekSeparatorLineColor() {
        return this.mWeekSeparatorLineColor;
    }

    public void setSelectedDateVerticalBar(int resourceId) {
        Drawable drawable = getResources().getDrawable(resourceId);
        setSelectedDateVerticalBar(drawable);
    }

    public void setSelectedDateVerticalBar(Drawable drawable) {
        if (this.mSelectedDateVerticalBar != drawable) {
            this.mSelectedDateVerticalBar = drawable;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasSelectedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    public Drawable getSelectedDateVerticalBar() {
        return this.mSelectedDateVerticalBar;
    }

    public void setWeekDayTextAppearance(int resourceId) {
        if (this.mWeekDayTextAppearanceResId != resourceId) {
            this.mWeekDayTextAppearanceResId = resourceId;
            setUpHeader();
        }
    }

    public int getWeekDayTextAppearance() {
        return this.mWeekDayTextAppearanceResId;
    }

    public void setDateTextAppearance(int resourceId) {
        if (this.mDateTextAppearanceResId != resourceId) {
            this.mDateTextAppearanceResId = resourceId;
            updateDateTextSize();
            invalidateAllWeekViews();
        }
    }

    public int getDateTextAppearance() {
        return this.mDateTextAppearanceResId;
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        this.mListView.setEnabled(enabled);
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mListView.isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CalendarView.class.getName());
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CalendarView.class.getName());
    }

    public long getMinDate() {
        return this.mMinDate.getTimeInMillis();
    }

    public void setMinDate(long minDate) {
        this.mTempDate.setTimeInMillis(minDate);
        if (isSameDate(this.mTempDate, this.mMinDate)) {
            return;
        }
        this.mMinDate.setTimeInMillis(minDate);
        Calendar date = this.mAdapter.mSelectedDate;
        if (date.before(this.mMinDate)) {
            this.mAdapter.setSelectedDay(this.mMinDate);
        }
        this.mAdapter.init();
        if (date.before(this.mMinDate)) {
            setDate(this.mTempDate.getTimeInMillis());
        } else {
            goTo(date, false, true, false);
        }
    }

    public long getMaxDate() {
        return this.mMaxDate.getTimeInMillis();
    }

    public void setMaxDate(long maxDate) {
        this.mTempDate.setTimeInMillis(maxDate);
        if (isSameDate(this.mTempDate, this.mMaxDate)) {
            return;
        }
        this.mMaxDate.setTimeInMillis(maxDate);
        this.mAdapter.init();
        Calendar date = this.mAdapter.mSelectedDate;
        if (date.after(this.mMaxDate)) {
            setDate(this.mMaxDate.getTimeInMillis());
        } else {
            goTo(date, false, true, false);
        }
    }

    public void setShowWeekNumber(boolean showWeekNumber) {
        if (this.mShowWeekNumber == showWeekNumber) {
            return;
        }
        this.mShowWeekNumber = showWeekNumber;
        this.mAdapter.notifyDataSetChanged();
        setUpHeader();
    }

    public boolean getShowWeekNumber() {
        return this.mShowWeekNumber;
    }

    public int getFirstDayOfWeek() {
        return this.mFirstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (this.mFirstDayOfWeek == firstDayOfWeek) {
            return;
        }
        this.mFirstDayOfWeek = firstDayOfWeek;
        this.mAdapter.init();
        setUpHeader();
    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        this.mOnDateChangeListener = listener;
    }

    public long getDate() {
        return this.mAdapter.mSelectedDate.getTimeInMillis();
    }

    public void setDate(long date) {
        setDate(date, false, false);
    }

    public void setDate(long date, boolean animate, boolean center) {
        this.mTempDate.setTimeInMillis(date);
        if (isSameDate(this.mTempDate, this.mAdapter.mSelectedDate)) {
            return;
        }
        goTo(this.mTempDate, animate, true, center);
    }

    private void updateDateTextSize() {
        TypedArray dateTextAppearance = this.mContext.obtainStyledAttributes(this.mDateTextAppearanceResId, R.styleable.TextAppearance);
        this.mDateTextSize = dateTextAppearance.getDimensionPixelSize(0, 14);
        dateTextAppearance.recycle();
    }

    private void invalidateAllWeekViews() {
        int childCount = this.mListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = this.mListView.getChildAt(i);
            view.invalidate();
        }
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(this.mCurrentLocale)) {
            return;
        }
        this.mCurrentLocale = locale;
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mFirstDayOfMonth = getCalendarForLocale(this.mFirstDayOfMonth, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
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

    private boolean isSameDate(Calendar firstDate, Calendar secondDate) {
        return firstDate.get(6) == secondDate.get(6) && firstDate.get(1) == secondDate.get(1);
    }

    private void setUpAdapter() {
        if (this.mAdapter == null) {
            this.mAdapter = new WeeksAdapter();
            this.mAdapter.registerDataSetObserver(new DataSetObserver() { // from class: android.widget.CalendarView.1
                @Override // android.database.DataSetObserver
                public void onChanged() {
                    if (CalendarView.this.mOnDateChangeListener != null) {
                        Calendar selectedDay = CalendarView.this.mAdapter.getSelectedDay();
                        CalendarView.this.mOnDateChangeListener.onSelectedDayChange(CalendarView.this, selectedDay.get(1), selectedDay.get(2), selectedDay.get(5));
                    }
                }
            });
            this.mListView.setAdapter((ListAdapter) this.mAdapter);
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private void setUpHeader() {
        String[] tinyWeekdayNames = LocaleData.get(Locale.getDefault()).tinyWeekdayNames;
        this.mDayLabels = new String[this.mDaysPerWeek];
        for (int i = 0; i < this.mDaysPerWeek; i++) {
            int j = i + this.mFirstDayOfWeek;
            int calendarDay = j > 7 ? j - 7 : j;
            this.mDayLabels[i] = tinyWeekdayNames[calendarDay];
        }
        TextView label = (TextView) this.mDayNamesHeader.getChildAt(0);
        if (this.mShowWeekNumber) {
            label.setVisibility(0);
        } else {
            label.setVisibility(8);
        }
        int count = this.mDayNamesHeader.getChildCount();
        for (int i2 = 0; i2 < count - 1; i2++) {
            TextView label2 = (TextView) this.mDayNamesHeader.getChildAt(i2 + 1);
            if (this.mWeekDayTextAppearanceResId > -1) {
                label2.setTextAppearance(this.mContext, this.mWeekDayTextAppearanceResId);
            }
            if (i2 < this.mDaysPerWeek) {
                label2.setText(this.mDayLabels[i2]);
                label2.setVisibility(0);
            } else {
                label2.setVisibility(8);
            }
        }
        this.mDayNamesHeader.invalidate();
    }

    private void setUpListView() {
        this.mListView.setDivider(null);
        this.mListView.setItemsCanFocus(true);
        this.mListView.setVerticalScrollBarEnabled(false);
        this.mListView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: android.widget.CalendarView.2
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                CalendarView.this.onScrollStateChanged(view, scrollState);
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                CalendarView.this.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
        this.mListView.setFriction(this.mFriction);
        this.mListView.setVelocityScale(this.mVelocityScale);
    }

    private void goTo(Calendar date, boolean animate, boolean setSelected, boolean forceScroll) {
        int position;
        if (date.before(this.mMinDate) || date.after(this.mMaxDate)) {
            throw new IllegalArgumentException("Time not between " + this.mMinDate.getTime() + " and " + this.mMaxDate.getTime());
        }
        int firstFullyVisiblePosition = this.mListView.getFirstVisiblePosition();
        View firstChild = this.mListView.getChildAt(0);
        if (firstChild != null && firstChild.getTop() < 0) {
            firstFullyVisiblePosition++;
        }
        int lastFullyVisiblePosition = (firstFullyVisiblePosition + this.mShownWeekCount) - 1;
        if (firstChild != null && firstChild.getTop() > this.mBottomBuffer) {
            lastFullyVisiblePosition--;
        }
        if (setSelected) {
            this.mAdapter.setSelectedDay(date);
        }
        int position2 = getWeeksSinceMinDate(date);
        if (position2 < firstFullyVisiblePosition || position2 > lastFullyVisiblePosition || forceScroll) {
            this.mFirstDayOfMonth.setTimeInMillis(date.getTimeInMillis());
            this.mFirstDayOfMonth.set(5, 1);
            setMonthDisplayed(this.mFirstDayOfMonth);
            if (this.mFirstDayOfMonth.before(this.mMinDate)) {
                position = 0;
            } else {
                position = getWeeksSinceMinDate(this.mFirstDayOfMonth);
            }
            this.mPreviousScrollState = 2;
            if (animate) {
                this.mListView.smoothScrollToPositionFromTop(position, this.mListScrollTopOffset, 1000);
                return;
            }
            this.mListView.setSelectionFromTop(position, this.mListScrollTopOffset);
            onScrollStateChanged(this.mListView, 0);
        } else if (setSelected) {
            setMonthDisplayed(date);
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int month;
        int monthDiff;
        WeekView child = (WeekView) view.getChildAt(0);
        if (child == null) {
            return;
        }
        long currScroll = (view.getFirstVisiblePosition() * child.getHeight()) - child.getBottom();
        if (currScroll < this.mPreviousScrollPosition) {
            this.mIsScrollingUp = true;
        } else if (currScroll > this.mPreviousScrollPosition) {
            this.mIsScrollingUp = false;
        } else {
            return;
        }
        int offset = child.getBottom() < this.mWeekMinVisibleHeight ? 1 : 0;
        if (this.mIsScrollingUp) {
            child = (WeekView) view.getChildAt(2 + offset);
        } else if (offset != 0) {
            child = (WeekView) view.getChildAt(offset);
        }
        if (this.mIsScrollingUp) {
            month = child.getMonthOfFirstWeekDay();
        } else {
            month = child.getMonthOfLastWeekDay();
        }
        if (this.mCurrentMonthDisplayed == 11 && month == 0) {
            monthDiff = 1;
        } else if (this.mCurrentMonthDisplayed == 0 && month == 11) {
            monthDiff = -1;
        } else {
            monthDiff = month - this.mCurrentMonthDisplayed;
        }
        if ((!this.mIsScrollingUp && monthDiff > 0) || (this.mIsScrollingUp && monthDiff < 0)) {
            Calendar firstDay = child.getFirstDay();
            if (this.mIsScrollingUp) {
                firstDay.add(5, -7);
            } else {
                firstDay.add(5, 7);
            }
            setMonthDisplayed(firstDay);
        }
        this.mPreviousScrollPosition = currScroll;
        this.mPreviousScrollState = this.mCurrentScrollState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMonthDisplayed(Calendar calendar) {
        this.mCurrentMonthDisplayed = calendar.get(2);
        this.mAdapter.setFocusMonth(this.mCurrentMonthDisplayed);
        long millis = calendar.getTimeInMillis();
        String newMonthName = DateUtils.formatDateRange(this.mContext, millis, millis, 52);
        this.mMonthName.setText(newMonthName);
        this.mMonthName.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWeeksSinceMinDate(Calendar date) {
        if (date.before(this.mMinDate)) {
            throw new IllegalArgumentException("fromDate: " + this.mMinDate.getTime() + " does not precede toDate: " + date.getTime());
        }
        long endTimeMillis = date.getTimeInMillis() + date.getTimeZone().getOffset(date.getTimeInMillis());
        long startTimeMillis = this.mMinDate.getTimeInMillis() + this.mMinDate.getTimeZone().getOffset(this.mMinDate.getTimeInMillis());
        long dayOffsetMillis = (this.mMinDate.get(7) - this.mFirstDayOfWeek) * 86400000;
        return (int) (((endTimeMillis - startTimeMillis) + dayOffsetMillis) / 604800000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CalendarView$ScrollStateRunnable.class */
    public class ScrollStateRunnable implements Runnable {
        private AbsListView mView;
        private int mNewState;

        private ScrollStateRunnable() {
        }

        public void doScrollStateChange(AbsListView view, int scrollState) {
            this.mView = view;
            this.mNewState = scrollState;
            CalendarView.this.removeCallbacks(this);
            CalendarView.this.postDelayed(this, 40L);
        }

        @Override // java.lang.Runnable
        public void run() {
            CalendarView.this.mCurrentScrollState = this.mNewState;
            if (this.mNewState == 0 && CalendarView.this.mPreviousScrollState != 0) {
                View child = this.mView.getChildAt(0);
                if (child != null) {
                    int dist = child.getBottom() - CalendarView.this.mListScrollTopOffset;
                    if (dist > CalendarView.this.mListScrollTopOffset) {
                        if (CalendarView.this.mIsScrollingUp) {
                            this.mView.smoothScrollBy(dist - child.getHeight(), 500);
                        } else {
                            this.mView.smoothScrollBy(dist, 500);
                        }
                    }
                } else {
                    return;
                }
            }
            CalendarView.this.mPreviousScrollState = this.mNewState;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CalendarView$WeeksAdapter.class */
    public class WeeksAdapter extends BaseAdapter implements View.OnTouchListener {
        private final Calendar mSelectedDate = Calendar.getInstance();
        private final GestureDetector mGestureDetector;
        private int mSelectedWeek;
        private int mFocusedMonth;
        private int mTotalWeekCount;

        public WeeksAdapter() {
            this.mGestureDetector = new GestureDetector(CalendarView.this.mContext, new CalendarGestureListener());
            init();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void init() {
            this.mSelectedWeek = CalendarView.this.getWeeksSinceMinDate(this.mSelectedDate);
            this.mTotalWeekCount = CalendarView.this.getWeeksSinceMinDate(CalendarView.this.mMaxDate);
            if (CalendarView.this.mMinDate.get(7) != CalendarView.this.mFirstDayOfWeek || CalendarView.this.mMaxDate.get(7) != CalendarView.this.mFirstDayOfWeek) {
                this.mTotalWeekCount++;
            }
            notifyDataSetChanged();
        }

        public void setSelectedDay(Calendar selectedDay) {
            if (selectedDay.get(6) == this.mSelectedDate.get(6) && selectedDay.get(1) == this.mSelectedDate.get(1)) {
                return;
            }
            this.mSelectedDate.setTimeInMillis(selectedDay.getTimeInMillis());
            this.mSelectedWeek = CalendarView.this.getWeeksSinceMinDate(this.mSelectedDate);
            this.mFocusedMonth = this.mSelectedDate.get(2);
            notifyDataSetChanged();
        }

        public Calendar getSelectedDay() {
            return this.mSelectedDate;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mTotalWeekCount;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            WeekView weekView;
            if (convertView != null) {
                weekView = (WeekView) convertView;
            } else {
                weekView = new WeekView(CalendarView.this.mContext);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(-2, -2);
                weekView.setLayoutParams(params);
                weekView.setClickable(true);
                weekView.setOnTouchListener(this);
            }
            int selectedWeekDay = this.mSelectedWeek == position ? this.mSelectedDate.get(7) : -1;
            weekView.init(position, selectedWeekDay, this.mFocusedMonth);
            return weekView;
        }

        public void setFocusMonth(int month) {
            if (this.mFocusedMonth == month) {
                return;
            }
            this.mFocusedMonth = month;
            notifyDataSetChanged();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (CalendarView.this.mListView.isEnabled() && this.mGestureDetector.onTouchEvent(event)) {
                WeekView weekView = (WeekView) v;
                if (!weekView.getDayFromLocation(event.getX(), CalendarView.this.mTempDate) || CalendarView.this.mTempDate.before(CalendarView.this.mMinDate) || CalendarView.this.mTempDate.after(CalendarView.this.mMaxDate)) {
                    return true;
                }
                onDateTapped(CalendarView.this.mTempDate);
                return true;
            }
            return false;
        }

        private void onDateTapped(Calendar day) {
            setSelectedDay(day);
            CalendarView.this.setMonthDisplayed(day);
        }

        /* loaded from: CalendarView$WeeksAdapter$CalendarGestureListener.class */
        class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
            CalendarGestureListener() {
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CalendarView$WeekView.class */
    public class WeekView extends View {
        private final Rect mTempRect;
        private final Paint mDrawPaint;
        private final Paint mMonthNumDrawPaint;
        private String[] mDayNumbers;
        private boolean[] mFocusDay;
        private boolean mHasFocusedDay;
        private boolean mHasUnfocusedDay;
        private Calendar mFirstDay;
        private int mMonthOfFirstWeekDay;
        private int mLastWeekDayMonth;
        private int mWeek;
        private int mWidth;
        private int mHeight;
        private boolean mHasSelectedDay;
        private int mSelectedDay;
        private int mNumCells;
        private int mSelectedLeft;
        private int mSelectedRight;

        public WeekView(Context context) {
            super(context);
            this.mTempRect = new Rect();
            this.mDrawPaint = new Paint();
            this.mMonthNumDrawPaint = new Paint();
            this.mMonthOfFirstWeekDay = -1;
            this.mLastWeekDayMonth = -1;
            this.mWeek = -1;
            this.mHasSelectedDay = false;
            this.mSelectedDay = -1;
            this.mSelectedLeft = -1;
            this.mSelectedRight = -1;
            initilaizePaints();
        }

        public void init(int weekNumber, int selectedWeekDay, int focusedMonth) {
            this.mSelectedDay = selectedWeekDay;
            this.mHasSelectedDay = this.mSelectedDay != -1;
            this.mNumCells = CalendarView.this.mShowWeekNumber ? CalendarView.this.mDaysPerWeek + 1 : CalendarView.this.mDaysPerWeek;
            this.mWeek = weekNumber;
            CalendarView.this.mTempDate.setTimeInMillis(CalendarView.this.mMinDate.getTimeInMillis());
            CalendarView.this.mTempDate.add(3, this.mWeek);
            CalendarView.this.mTempDate.setFirstDayOfWeek(CalendarView.this.mFirstDayOfWeek);
            this.mDayNumbers = new String[this.mNumCells];
            this.mFocusDay = new boolean[this.mNumCells];
            int i = 0;
            if (CalendarView.this.mShowWeekNumber) {
                this.mDayNumbers[0] = String.format(Locale.getDefault(), "%d", Integer.valueOf(CalendarView.this.mTempDate.get(3)));
                i = 0 + 1;
            }
            int diff = CalendarView.this.mFirstDayOfWeek - CalendarView.this.mTempDate.get(7);
            CalendarView.this.mTempDate.add(5, diff);
            this.mFirstDay = (Calendar) CalendarView.this.mTempDate.clone();
            this.mMonthOfFirstWeekDay = CalendarView.this.mTempDate.get(2);
            this.mHasUnfocusedDay = true;
            while (i < this.mNumCells) {
                boolean isFocusedDay = CalendarView.this.mTempDate.get(2) == focusedMonth;
                this.mFocusDay[i] = isFocusedDay;
                this.mHasFocusedDay |= isFocusedDay;
                this.mHasUnfocusedDay &= !isFocusedDay;
                if (CalendarView.this.mTempDate.before(CalendarView.this.mMinDate) || CalendarView.this.mTempDate.after(CalendarView.this.mMaxDate)) {
                    this.mDayNumbers[i] = "";
                } else {
                    this.mDayNumbers[i] = String.format(Locale.getDefault(), "%d", Integer.valueOf(CalendarView.this.mTempDate.get(5)));
                }
                CalendarView.this.mTempDate.add(5, 1);
                i++;
            }
            if (CalendarView.this.mTempDate.get(5) == 1) {
                CalendarView.this.mTempDate.add(5, -1);
            }
            this.mLastWeekDayMonth = CalendarView.this.mTempDate.get(2);
            updateSelectionPositions();
        }

        private void initilaizePaints() {
            this.mDrawPaint.setFakeBoldText(false);
            this.mDrawPaint.setAntiAlias(true);
            this.mDrawPaint.setStyle(Paint.Style.FILL);
            this.mMonthNumDrawPaint.setFakeBoldText(true);
            this.mMonthNumDrawPaint.setAntiAlias(true);
            this.mMonthNumDrawPaint.setStyle(Paint.Style.FILL);
            this.mMonthNumDrawPaint.setTextAlign(Paint.Align.CENTER);
            this.mMonthNumDrawPaint.setTextSize(CalendarView.this.mDateTextSize);
        }

        public int getMonthOfFirstWeekDay() {
            return this.mMonthOfFirstWeekDay;
        }

        public int getMonthOfLastWeekDay() {
            return this.mLastWeekDayMonth;
        }

        public Calendar getFirstDay() {
            return this.mFirstDay;
        }

        public boolean getDayFromLocation(float x, Calendar outCalendar) {
            int start;
            int end;
            boolean isLayoutRtl = isLayoutRtl();
            if (!isLayoutRtl) {
                start = CalendarView.this.mShowWeekNumber ? this.mWidth / this.mNumCells : 0;
                end = this.mWidth;
            } else {
                start = 0;
                end = CalendarView.this.mShowWeekNumber ? this.mWidth - (this.mWidth / this.mNumCells) : this.mWidth;
            }
            if (x >= start && x <= end) {
                int dayPosition = (int) (((x - start) * CalendarView.this.mDaysPerWeek) / (end - start));
                if (isLayoutRtl) {
                    dayPosition = (CalendarView.this.mDaysPerWeek - 1) - dayPosition;
                }
                outCalendar.setTimeInMillis(this.mFirstDay.getTimeInMillis());
                outCalendar.add(5, dayPosition);
                return true;
            }
            outCalendar.clear();
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            drawBackground(canvas);
            drawWeekNumbersAndDates(canvas);
            drawWeekSeparators(canvas);
            drawSelectedDateVerticalBars(canvas);
        }

        private void drawBackground(Canvas canvas) {
            if (this.mHasSelectedDay) {
                this.mDrawPaint.setColor(CalendarView.this.mSelectedWeekBackgroundColor);
                this.mTempRect.top = CalendarView.this.mWeekSeperatorLineWidth;
                this.mTempRect.bottom = this.mHeight;
                boolean isLayoutRtl = isLayoutRtl();
                if (isLayoutRtl) {
                    this.mTempRect.left = 0;
                    this.mTempRect.right = this.mSelectedLeft - 2;
                } else {
                    this.mTempRect.left = CalendarView.this.mShowWeekNumber ? this.mWidth / this.mNumCells : 0;
                    this.mTempRect.right = this.mSelectedLeft - 2;
                }
                canvas.drawRect(this.mTempRect, this.mDrawPaint);
                if (isLayoutRtl) {
                    this.mTempRect.left = this.mSelectedRight + 3;
                    this.mTempRect.right = CalendarView.this.mShowWeekNumber ? this.mWidth - (this.mWidth / this.mNumCells) : this.mWidth;
                } else {
                    this.mTempRect.left = this.mSelectedRight + 3;
                    this.mTempRect.right = this.mWidth;
                }
                canvas.drawRect(this.mTempRect, this.mDrawPaint);
            }
        }

        private void drawWeekNumbersAndDates(Canvas canvas) {
            float textHeight = this.mDrawPaint.getTextSize();
            int y = ((int) ((this.mHeight + textHeight) / 2.0f)) - CalendarView.this.mWeekSeperatorLineWidth;
            int nDays = this.mNumCells;
            int divisor = 2 * nDays;
            this.mDrawPaint.setTextAlign(Paint.Align.CENTER);
            this.mDrawPaint.setTextSize(CalendarView.this.mDateTextSize);
            int i = 0;
            if (!isLayoutRtl()) {
                if (CalendarView.this.mShowWeekNumber) {
                    this.mDrawPaint.setColor(CalendarView.this.mWeekNumberColor);
                    int x = this.mWidth / divisor;
                    canvas.drawText(this.mDayNumbers[0], x, y, this.mDrawPaint);
                    i = 0 + 1;
                }
                while (i < nDays) {
                    this.mMonthNumDrawPaint.setColor(this.mFocusDay[i] ? CalendarView.this.mFocusedMonthDateColor : CalendarView.this.mUnfocusedMonthDateColor);
                    int x2 = (((2 * i) + 1) * this.mWidth) / divisor;
                    canvas.drawText(this.mDayNumbers[i], x2, y, this.mMonthNumDrawPaint);
                    i++;
                }
                return;
            }
            while (i < nDays - 1) {
                this.mMonthNumDrawPaint.setColor(this.mFocusDay[i] ? CalendarView.this.mFocusedMonthDateColor : CalendarView.this.mUnfocusedMonthDateColor);
                int x3 = (((2 * i) + 1) * this.mWidth) / divisor;
                canvas.drawText(this.mDayNumbers[(nDays - 1) - i], x3, y, this.mMonthNumDrawPaint);
                i++;
            }
            if (CalendarView.this.mShowWeekNumber) {
                this.mDrawPaint.setColor(CalendarView.this.mWeekNumberColor);
                int x4 = this.mWidth - (this.mWidth / divisor);
                canvas.drawText(this.mDayNumbers[0], x4, y, this.mDrawPaint);
            }
        }

        private void drawWeekSeparators(Canvas canvas) {
            float startX;
            float stopX;
            int firstFullyVisiblePosition = CalendarView.this.mListView.getFirstVisiblePosition();
            if (CalendarView.this.mListView.getChildAt(0).getTop() < 0) {
                firstFullyVisiblePosition++;
            }
            if (firstFullyVisiblePosition != this.mWeek) {
                this.mDrawPaint.setColor(CalendarView.this.mWeekSeparatorLineColor);
                this.mDrawPaint.setStrokeWidth(CalendarView.this.mWeekSeperatorLineWidth);
                if (!isLayoutRtl()) {
                    startX = CalendarView.this.mShowWeekNumber ? this.mWidth / this.mNumCells : 0.0f;
                    stopX = this.mWidth;
                } else {
                    startX = 0.0f;
                    stopX = CalendarView.this.mShowWeekNumber ? this.mWidth - (this.mWidth / this.mNumCells) : this.mWidth;
                }
                canvas.drawLine(startX, 0.0f, stopX, 0.0f, this.mDrawPaint);
            }
        }

        private void drawSelectedDateVerticalBars(Canvas canvas) {
            if (this.mHasSelectedDay) {
                CalendarView.this.mSelectedDateVerticalBar.setBounds(this.mSelectedLeft - (CalendarView.this.mSelectedDateVerticalBarWidth / 2), CalendarView.this.mWeekSeperatorLineWidth, this.mSelectedLeft + (CalendarView.this.mSelectedDateVerticalBarWidth / 2), this.mHeight);
                CalendarView.this.mSelectedDateVerticalBar.draw(canvas);
                CalendarView.this.mSelectedDateVerticalBar.setBounds(this.mSelectedRight - (CalendarView.this.mSelectedDateVerticalBarWidth / 2), CalendarView.this.mWeekSeperatorLineWidth, this.mSelectedRight + (CalendarView.this.mSelectedDateVerticalBarWidth / 2), this.mHeight);
                CalendarView.this.mSelectedDateVerticalBar.draw(canvas);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            this.mWidth = w;
            updateSelectionPositions();
        }

        private void updateSelectionPositions() {
            if (this.mHasSelectedDay) {
                boolean isLayoutRtl = isLayoutRtl();
                int selectedPosition = this.mSelectedDay - CalendarView.this.mFirstDayOfWeek;
                if (selectedPosition < 0) {
                    selectedPosition += 7;
                }
                if (CalendarView.this.mShowWeekNumber && !isLayoutRtl) {
                    selectedPosition++;
                }
                if (isLayoutRtl) {
                    this.mSelectedLeft = (((CalendarView.this.mDaysPerWeek - 1) - selectedPosition) * this.mWidth) / this.mNumCells;
                } else {
                    this.mSelectedLeft = (selectedPosition * this.mWidth) / this.mNumCells;
                }
                this.mSelectedRight = this.mSelectedLeft + (this.mWidth / this.mNumCells);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.mHeight = ((CalendarView.this.mListView.getHeight() - CalendarView.this.mListView.getPaddingTop()) - CalendarView.this.mListView.getPaddingBottom()) / CalendarView.this.mShownWeekCount;
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), this.mHeight);
        }
    }
}