package android.content;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: RestrictionEntry.class */
public class RestrictionEntry implements Parcelable {
    public static final int TYPE_NULL = 0;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_CHOICE = 2;
    public static final int TYPE_CHOICE_LEVEL = 3;
    public static final int TYPE_MULTI_SELECT = 4;
    private int type;
    private String key;
    private String title;
    private String description;
    private String[] choices;
    private String[] values;
    private String currentValue;
    private String[] currentValues;
    public static final Parcelable.Creator<RestrictionEntry> CREATOR = new Parcelable.Creator<RestrictionEntry>() { // from class: android.content.RestrictionEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RestrictionEntry createFromParcel(Parcel source) {
            return new RestrictionEntry(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RestrictionEntry[] newArray(int size) {
            return new RestrictionEntry[size];
        }
    };

    public RestrictionEntry(String key, String selectedString) {
        this.key = key;
        this.type = 2;
        this.currentValue = selectedString;
    }

    public RestrictionEntry(String key, boolean selectedState) {
        this.key = key;
        this.type = 1;
        setSelectedState(selectedState);
    }

    public RestrictionEntry(String key, String[] selectedStrings) {
        this.key = key;
        this.type = 4;
        this.currentValues = selectedStrings;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public String getSelectedString() {
        return this.currentValue;
    }

    public String[] getAllSelectedStrings() {
        return this.currentValues;
    }

    public boolean getSelectedState() {
        return Boolean.parseBoolean(this.currentValue);
    }

    public void setSelectedString(String selectedString) {
        this.currentValue = selectedString;
    }

    public void setSelectedState(boolean state) {
        this.currentValue = Boolean.toString(state);
    }

    public void setAllSelectedStrings(String[] allSelectedStrings) {
        this.currentValues = allSelectedStrings;
    }

    public void setChoiceValues(String[] choiceValues) {
        this.values = choiceValues;
    }

    public void setChoiceValues(Context context, int stringArrayResId) {
        this.values = context.getResources().getStringArray(stringArrayResId);
    }

    public String[] getChoiceValues() {
        return this.values;
    }

    public void setChoiceEntries(String[] choiceEntries) {
        this.choices = choiceEntries;
    }

    public void setChoiceEntries(Context context, int stringArrayResId) {
        this.choices = context.getResources().getStringArray(stringArrayResId);
    }

    public String[] getChoiceEntries() {
        return this.choices;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private boolean equalArrays(String[] one, String[] other) {
        if (one.length != other.length) {
            return false;
        }
        for (int i = 0; i < one.length; i++) {
            if (!one[i].equals(other[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RestrictionEntry) {
            RestrictionEntry other = (RestrictionEntry) o;
            return this.type == other.type && this.key.equals(other.key) && ((this.currentValues == null && other.currentValues == null && this.currentValue != null && this.currentValue.equals(other.currentValue)) || (this.currentValue == null && other.currentValue == null && this.currentValues != null && equalArrays(this.currentValues, other.currentValues)));
        }
        return false;
    }

    public int hashCode() {
        int result = (31 * 17) + this.key.hashCode();
        if (this.currentValue != null) {
            result = (31 * result) + this.currentValue.hashCode();
        } else if (this.currentValues != null) {
            String[] arr$ = this.currentValues;
            for (String value : arr$) {
                if (value != null) {
                    result = (31 * result) + value.hashCode();
                }
            }
        }
        return result;
    }

    private String[] readArray(Parcel in) {
        int count = in.readInt();
        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = in.readString();
        }
        return values;
    }

    public RestrictionEntry(Parcel in) {
        this.type = in.readInt();
        this.key = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.choices = readArray(in);
        this.values = readArray(in);
        this.currentValue = in.readString();
        this.currentValues = readArray(in);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private void writeArray(Parcel dest, String[] values) {
        if (values == null) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(values.length);
        for (String str : values) {
            dest.writeString(str);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.key);
        dest.writeString(this.title);
        dest.writeString(this.description);
        writeArray(dest, this.choices);
        writeArray(dest, this.values);
        dest.writeString(this.currentValue);
        writeArray(dest, this.currentValues);
    }

    public String toString() {
        return "RestrictionsEntry {type=" + this.type + ", key=" + this.key + ", value=" + this.currentValue + "}";
    }
}