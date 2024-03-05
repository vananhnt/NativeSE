package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import com.android.internal.R;
import java.util.Calendar;

/* loaded from: DatePickerDialog.class */
public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private final DatePicker mDatePicker;
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;
    private boolean mTitleNeedsUpdate;

    /* loaded from: DatePickerDialog$OnDateSetListener.class */
    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int i, int i2, int i3);
    }

    public DatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme);
        this.mTitleNeedsUpdate = true;
        this.mCallBack = callBack;
        this.mCalendar = Calendar.getInstance();
        Context themeContext = getContext();
        setButton(-1, themeContext.getText(R.string.date_time_done), this);
        setIcon(0);
        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_picker_dialog, (ViewGroup) null);
        setView(view);
        this.mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        this.mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        updateTitle(year, monthOfYear, dayOfMonth);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDateSet();
    }

    @Override // android.widget.DatePicker.OnDateChangedListener
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        this.mDatePicker.init(year, month, day, this);
        updateTitle(year, month, day);
    }

    public DatePicker getDatePicker() {
        return this.mDatePicker;
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        this.mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    private void tryNotifyDateSet() {
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            this.mCallBack.onDateSet(this.mDatePicker, this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onStop() {
        tryNotifyDateSet();
        super.onStop();
    }

    private void updateTitle(int year, int month, int day) {
        if (!this.mDatePicker.getCalendarViewShown()) {
            this.mCalendar.set(1, year);
            this.mCalendar.set(2, month);
            this.mCalendar.set(5, day);
            String title = DateUtils.formatDateTime(this.mContext, this.mCalendar.getTimeInMillis(), 98326);
            setTitle(title);
            this.mTitleNeedsUpdate = true;
        } else if (this.mTitleNeedsUpdate) {
            this.mTitleNeedsUpdate = false;
            setTitle(R.string.date_picker_dialog_title);
        }
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("year", this.mDatePicker.getYear());
        state.putInt(MONTH, this.mDatePicker.getMonth());
        state.putInt(DAY, this.mDatePicker.getDayOfMonth());
        return state;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt("year");
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        this.mDatePicker.init(year, month, day, this);
    }
}