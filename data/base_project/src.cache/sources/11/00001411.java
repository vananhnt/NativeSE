package android.text.style;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: SuggestionSpan.class */
public class SuggestionSpan extends CharacterStyle implements ParcelableSpan {
    private static final String TAG = "SuggestionSpan";
    public static final int FLAG_EASY_CORRECT = 1;
    public static final int FLAG_MISSPELLED = 2;
    public static final int FLAG_AUTO_CORRECTION = 4;
    public static final String ACTION_SUGGESTION_PICKED = "android.text.style.SUGGESTION_PICKED";
    public static final String SUGGESTION_SPAN_PICKED_AFTER = "after";
    public static final String SUGGESTION_SPAN_PICKED_BEFORE = "before";
    public static final String SUGGESTION_SPAN_PICKED_HASHCODE = "hashcode";
    public static final int SUGGESTIONS_MAX_SIZE = 5;
    private int mFlags;
    private final String[] mSuggestions;
    private final String mLocaleString;
    private final String mNotificationTargetClassName;
    private final String mNotificationTargetPackageName;
    private final int mHashCode;
    private float mEasyCorrectUnderlineThickness;
    private int mEasyCorrectUnderlineColor;
    private float mMisspelledUnderlineThickness;
    private int mMisspelledUnderlineColor;
    private float mAutoCorrectionUnderlineThickness;
    private int mAutoCorrectionUnderlineColor;
    public static final Parcelable.Creator<SuggestionSpan> CREATOR = new Parcelable.Creator<SuggestionSpan>() { // from class: android.text.style.SuggestionSpan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SuggestionSpan createFromParcel(Parcel source) {
            return new SuggestionSpan(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SuggestionSpan[] newArray(int size) {
            return new SuggestionSpan[size];
        }
    };

    public SuggestionSpan(Context context, String[] suggestions, int flags) {
        this(context, null, suggestions, flags, null);
    }

    public SuggestionSpan(Locale locale, String[] suggestions, int flags) {
        this(null, locale, suggestions, flags, null);
    }

    public SuggestionSpan(Context context, Locale locale, String[] suggestions, int flags, Class<?> notificationTargetClass) {
        int N = Math.min(5, suggestions.length);
        this.mSuggestions = (String[]) Arrays.copyOf(suggestions, N);
        this.mFlags = flags;
        if (locale != null) {
            this.mLocaleString = locale.toString();
        } else if (context != null) {
            this.mLocaleString = context.getResources().getConfiguration().locale.toString();
        } else {
            Log.e(TAG, "No locale or context specified in SuggestionSpan constructor");
            this.mLocaleString = "";
        }
        if (context != null) {
            this.mNotificationTargetPackageName = context.getPackageName();
        } else {
            this.mNotificationTargetPackageName = null;
        }
        if (notificationTargetClass != null) {
            this.mNotificationTargetClassName = notificationTargetClass.getCanonicalName();
        } else {
            this.mNotificationTargetClassName = "";
        }
        this.mHashCode = hashCodeInternal(this.mSuggestions, this.mLocaleString, this.mNotificationTargetClassName);
        initStyle(context);
    }

    private void initStyle(Context context) {
        if (context == null) {
            this.mMisspelledUnderlineThickness = 0.0f;
            this.mEasyCorrectUnderlineThickness = 0.0f;
            this.mAutoCorrectionUnderlineThickness = 0.0f;
            this.mMisspelledUnderlineColor = -16777216;
            this.mEasyCorrectUnderlineColor = -16777216;
            this.mAutoCorrectionUnderlineColor = -16777216;
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, R.attr.textAppearanceMisspelledSuggestion, 0);
        this.mMisspelledUnderlineThickness = typedArray.getDimension(1, 0.0f);
        this.mMisspelledUnderlineColor = typedArray.getColor(0, -16777216);
        TypedArray typedArray2 = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, R.attr.textAppearanceEasyCorrectSuggestion, 0);
        this.mEasyCorrectUnderlineThickness = typedArray2.getDimension(1, 0.0f);
        this.mEasyCorrectUnderlineColor = typedArray2.getColor(0, -16777216);
        TypedArray typedArray3 = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, R.attr.textAppearanceAutoCorrectionSuggestion, 0);
        this.mAutoCorrectionUnderlineThickness = typedArray3.getDimension(1, 0.0f);
        this.mAutoCorrectionUnderlineColor = typedArray3.getColor(0, -16777216);
    }

    public SuggestionSpan(Parcel src) {
        this.mSuggestions = src.readStringArray();
        this.mFlags = src.readInt();
        this.mLocaleString = src.readString();
        this.mNotificationTargetClassName = src.readString();
        this.mNotificationTargetPackageName = src.readString();
        this.mHashCode = src.readInt();
        this.mEasyCorrectUnderlineColor = src.readInt();
        this.mEasyCorrectUnderlineThickness = src.readFloat();
        this.mMisspelledUnderlineColor = src.readInt();
        this.mMisspelledUnderlineThickness = src.readFloat();
        this.mAutoCorrectionUnderlineColor = src.readInt();
        this.mAutoCorrectionUnderlineThickness = src.readFloat();
    }

    public String[] getSuggestions() {
        return this.mSuggestions;
    }

    public String getLocale() {
        return this.mLocaleString;
    }

    public String getNotificationTargetClassName() {
        return this.mNotificationTargetClassName;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.mSuggestions);
        dest.writeInt(this.mFlags);
        dest.writeString(this.mLocaleString);
        dest.writeString(this.mNotificationTargetClassName);
        dest.writeString(this.mNotificationTargetPackageName);
        dest.writeInt(this.mHashCode);
        dest.writeInt(this.mEasyCorrectUnderlineColor);
        dest.writeFloat(this.mEasyCorrectUnderlineThickness);
        dest.writeInt(this.mMisspelledUnderlineColor);
        dest.writeFloat(this.mMisspelledUnderlineThickness);
        dest.writeInt(this.mAutoCorrectionUnderlineColor);
        dest.writeFloat(this.mAutoCorrectionUnderlineThickness);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 19;
    }

    public boolean equals(Object o) {
        return (o instanceof SuggestionSpan) && ((SuggestionSpan) o).hashCode() == this.mHashCode;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    private static int hashCodeInternal(String[] suggestions, String locale, String notificationTargetClassName) {
        return Arrays.hashCode(new Object[]{Long.valueOf(SystemClock.uptimeMillis()), suggestions, locale, notificationTargetClassName});
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint tp) {
        boolean misspelled = (this.mFlags & 2) != 0;
        boolean easy = (this.mFlags & 1) != 0;
        boolean autoCorrection = (this.mFlags & 4) != 0;
        if (easy) {
            if (!misspelled) {
                tp.setUnderlineText(this.mEasyCorrectUnderlineColor, this.mEasyCorrectUnderlineThickness);
            } else if (tp.underlineColor == 0) {
                tp.setUnderlineText(this.mMisspelledUnderlineColor, this.mMisspelledUnderlineThickness);
            }
        } else if (autoCorrection) {
            tp.setUnderlineText(this.mAutoCorrectionUnderlineColor, this.mAutoCorrectionUnderlineThickness);
        }
    }

    public int getUnderlineColor() {
        boolean misspelled = (this.mFlags & 2) != 0;
        boolean easy = (this.mFlags & 1) != 0;
        boolean autoCorrection = (this.mFlags & 4) != 0;
        if (easy) {
            if (!misspelled) {
                return this.mEasyCorrectUnderlineColor;
            }
            return this.mMisspelledUnderlineColor;
        } else if (autoCorrection) {
            return this.mAutoCorrectionUnderlineColor;
        } else {
            return 0;
        }
    }

    public void notifySelection(Context context, String original, int index) {
        Intent intent = new Intent();
        if (context == null || this.mNotificationTargetClassName == null) {
            return;
        }
        if (this.mSuggestions == null || index < 0 || index >= this.mSuggestions.length) {
            Log.w(TAG, "Unable to notify the suggestion as the index is out of range index=" + index + " length=" + this.mSuggestions.length);
        } else if (this.mNotificationTargetPackageName != null) {
            intent.setClassName(this.mNotificationTargetPackageName, this.mNotificationTargetClassName);
            intent.setAction(ACTION_SUGGESTION_PICKED);
            intent.putExtra(SUGGESTION_SPAN_PICKED_BEFORE, original);
            intent.putExtra(SUGGESTION_SPAN_PICKED_AFTER, this.mSuggestions[index]);
            intent.putExtra(SUGGESTION_SPAN_PICKED_HASHCODE, hashCode());
            context.sendBroadcast(intent);
        } else {
            InputMethodManager imm = InputMethodManager.peekInstance();
            if (imm != null) {
                imm.notifySuggestionPicked(this, original, index);
            }
        }
    }
}