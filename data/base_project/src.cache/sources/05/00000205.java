package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import com.android.internal.R;

/* loaded from: TimePickerDialog.class */
public class TimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, TimePicker.OnTimeChangedListener {
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";
    private final TimePicker mTimePicker;
    private final OnTimeSetListener mCallback;
    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;

    /* loaded from: TimePickerDialog$OnTimeSetListener.class */
    public interface OnTimeSetListener {
        void onTimeSet(TimePicker timePicker, int i, int i2);
    }

    public TimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        this(context, 0, callBack, hourOfDay, minute, is24HourView);
    }

    public TimePickerDialog(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, theme);
        this.mCallback = callBack;
        this.mInitialHourOfDay = hourOfDay;
        this.mInitialMinute = minute;
        this.mIs24HourView = is24HourView;
        setIcon(0);
        setTitle(R.string.time_picker_dialog_title);
        Context themeContext = getContext();
        setButton(-1, themeContext.getText(R.string.date_time_done), this);
        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker_dialog, (ViewGroup) null);
        setView(view);
        this.mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
        this.mTimePicker.setIs24HourView(Boolean.valueOf(this.mIs24HourView));
        this.mTimePicker.setCurrentHour(Integer.valueOf(this.mInitialHourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mInitialMinute));
        this.mTimePicker.setOnTimeChangedListener(this);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        tryNotifyTimeSet();
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(minutOfHour));
    }

    @Override // android.widget.TimePicker.OnTimeChangedListener
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
    }

    private void tryNotifyTimeSet() {
        if (this.mCallback != null) {
            this.mTimePicker.clearFocus();
            this.mCallback.onTimeSet(this.mTimePicker, this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onStop() {
        tryNotifyTimeSet();
        super.onStop();
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, this.mTimePicker.getCurrentHour().intValue());
        state.putInt(MINUTE, this.mTimePicker.getCurrentMinute().intValue());
        state.putBoolean(IS_24_HOUR, this.mTimePicker.is24HourView());
        return state;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        this.mTimePicker.setIs24HourView(Boolean.valueOf(savedInstanceState.getBoolean(IS_24_HOUR)));
        this.mTimePicker.setCurrentHour(Integer.valueOf(hour));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(minute));
    }
}