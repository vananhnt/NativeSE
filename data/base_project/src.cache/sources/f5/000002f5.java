package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: ContentValues.class */
public final class ContentValues implements Parcelable {
    public static final String TAG = "ContentValues";
    private HashMap<String, Object> mValues;
    public static final Parcelable.Creator<ContentValues> CREATOR = new Parcelable.Creator<ContentValues>() { // from class: android.content.ContentValues.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ContentValues createFromParcel(Parcel in) {
            HashMap<String, Object> values = in.readHashMap(null);
            return new ContentValues(values);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ContentValues[] newArray(int size) {
            return new ContentValues[size];
        }
    };

    public ContentValues() {
        this.mValues = new HashMap<>(8);
    }

    public ContentValues(int size) {
        this.mValues = new HashMap<>(size, 1.0f);
    }

    public ContentValues(ContentValues from) {
        this.mValues = new HashMap<>(from.mValues);
    }

    private ContentValues(HashMap<String, Object> values) {
        this.mValues = values;
    }

    public boolean equals(Object object) {
        if (!(object instanceof ContentValues)) {
            return false;
        }
        return this.mValues.equals(((ContentValues) object).mValues);
    }

    public int hashCode() {
        return this.mValues.hashCode();
    }

    public void put(String key, String value) {
        this.mValues.put(key, value);
    }

    public void putAll(ContentValues other) {
        this.mValues.putAll(other.mValues);
    }

    public void put(String key, Byte value) {
        this.mValues.put(key, value);
    }

    public void put(String key, Short value) {
        this.mValues.put(key, value);
    }

    public void put(String key, Integer value) {
        this.mValues.put(key, value);
    }

    public void put(String key, Long value) {
        this.mValues.put(key, value);
    }

    public void put(String key, Float value) {
        this.mValues.put(key, value);
    }

    public void put(String key, Double value) {
        this.mValues.put(key, value);
    }

    public void put(String key, Boolean value) {
        this.mValues.put(key, value);
    }

    public void put(String key, byte[] value) {
        this.mValues.put(key, value);
    }

    public void putNull(String key) {
        this.mValues.put(key, null);
    }

    public int size() {
        return this.mValues.size();
    }

    public void remove(String key) {
        this.mValues.remove(key);
    }

    public void clear() {
        this.mValues.clear();
    }

    public boolean containsKey(String key) {
        return this.mValues.containsKey(key);
    }

    public Object get(String key) {
        return this.mValues.get(key);
    }

    public String getAsString(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public Long getAsLong(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            try {
                return Long.valueOf(((Number) value).longValue());
            } catch (ClassCastException e) {
                if (value instanceof CharSequence) {
                    try {
                        return Long.valueOf(value.toString());
                    } catch (NumberFormatException e2) {
                        Log.e(TAG, "Cannot parse Long value for " + value + " at key " + key);
                        return null;
                    }
                }
                Log.e(TAG, "Cannot cast value for " + key + " to a Long: " + value, e);
                return null;
            }
        }
        return null;
    }

    public Integer getAsInteger(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            try {
                return Integer.valueOf(((Number) value).intValue());
            } catch (ClassCastException e) {
                if (value instanceof CharSequence) {
                    try {
                        return Integer.valueOf(value.toString());
                    } catch (NumberFormatException e2) {
                        Log.e(TAG, "Cannot parse Integer value for " + value + " at key " + key);
                        return null;
                    }
                }
                Log.e(TAG, "Cannot cast value for " + key + " to a Integer: " + value, e);
                return null;
            }
        }
        return null;
    }

    public Short getAsShort(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            try {
                return Short.valueOf(((Number) value).shortValue());
            } catch (ClassCastException e) {
                if (value instanceof CharSequence) {
                    try {
                        return Short.valueOf(value.toString());
                    } catch (NumberFormatException e2) {
                        Log.e(TAG, "Cannot parse Short value for " + value + " at key " + key);
                        return null;
                    }
                }
                Log.e(TAG, "Cannot cast value for " + key + " to a Short: " + value, e);
                return null;
            }
        }
        return null;
    }

    public Byte getAsByte(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            try {
                return Byte.valueOf(((Number) value).byteValue());
            } catch (ClassCastException e) {
                if (value instanceof CharSequence) {
                    try {
                        return Byte.valueOf(value.toString());
                    } catch (NumberFormatException e2) {
                        Log.e(TAG, "Cannot parse Byte value for " + value + " at key " + key);
                        return null;
                    }
                }
                Log.e(TAG, "Cannot cast value for " + key + " to a Byte: " + value, e);
                return null;
            }
        }
        return null;
    }

    public Double getAsDouble(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            try {
                return Double.valueOf(((Number) value).doubleValue());
            } catch (ClassCastException e) {
                if (value instanceof CharSequence) {
                    try {
                        return Double.valueOf(value.toString());
                    } catch (NumberFormatException e2) {
                        Log.e(TAG, "Cannot parse Double value for " + value + " at key " + key);
                        return null;
                    }
                }
                Log.e(TAG, "Cannot cast value for " + key + " to a Double: " + value, e);
                return null;
            }
        }
        return null;
    }

    public Float getAsFloat(String key) {
        Object value = this.mValues.get(key);
        if (value != null) {
            try {
                return Float.valueOf(((Number) value).floatValue());
            } catch (ClassCastException e) {
                if (value instanceof CharSequence) {
                    try {
                        return Float.valueOf(value.toString());
                    } catch (NumberFormatException e2) {
                        Log.e(TAG, "Cannot parse Float value for " + value + " at key " + key);
                        return null;
                    }
                }
                Log.e(TAG, "Cannot cast value for " + key + " to a Float: " + value, e);
                return null;
            }
        }
        return null;
    }

    public Boolean getAsBoolean(String key) {
        Object value = this.mValues.get(key);
        try {
            return (Boolean) value;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                return Boolean.valueOf(value.toString());
            }
            if (value instanceof Number) {
                return Boolean.valueOf(((Number) value).intValue() != 0);
            }
            Log.e(TAG, "Cannot cast value for " + key + " to a Boolean: " + value, e);
            return null;
        }
    }

    public byte[] getAsByteArray(String key) {
        Object value = this.mValues.get(key);
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return null;
    }

    public Set<Map.Entry<String, Object>> valueSet() {
        return this.mValues.entrySet();
    }

    public Set<String> keySet() {
        return this.mValues.keySet();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeMap(this.mValues);
    }

    @Deprecated
    public void putStringArrayList(String key, ArrayList<String> value) {
        this.mValues.put(key, value);
    }

    @Deprecated
    public ArrayList<String> getStringArrayList(String key) {
        return (ArrayList) this.mValues.get(key);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : this.mValues.keySet()) {
            String value = getAsString(name);
            if (sb.length() > 0) {
                sb.append(Separators.SP);
            }
            sb.append(name + Separators.EQUALS + value);
        }
        return sb.toString();
    }
}