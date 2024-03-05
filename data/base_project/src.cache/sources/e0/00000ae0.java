package android.os;

import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: Bundle.class */
public final class Bundle implements Parcelable, Cloneable {
    private static final String TAG = "Bundle";
    static final boolean DEBUG = false;
    public static final Bundle EMPTY = new Bundle();
    static final int BUNDLE_MAGIC = 1279544898;
    ArrayMap<String, Object> mMap;
    Parcel mParcelledData;
    private boolean mHasFds;
    private boolean mFdsKnown;
    private boolean mAllowFds;
    private ClassLoader mClassLoader;
    public static final Parcelable.Creator<Bundle> CREATOR;

    static {
        EMPTY.mMap = ArrayMap.EMPTY;
        CREATOR = new Parcelable.Creator<Bundle>() { // from class: android.os.Bundle.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Bundle createFromParcel(Parcel in) {
                return in.readBundle();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Bundle[] newArray(int size) {
                return new Bundle[size];
            }
        };
    }

    public Bundle() {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        this.mMap = new ArrayMap<>();
        this.mClassLoader = getClass().getClassLoader();
    }

    Bundle(Parcel parcelledData) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        readFromParcel(parcelledData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bundle(Parcel parcelledData, int length) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        readFromParcelInner(parcelledData, length);
    }

    public Bundle(ClassLoader loader) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        this.mMap = new ArrayMap<>();
        this.mClassLoader = loader;
    }

    public Bundle(int capacity) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        this.mMap = new ArrayMap<>(capacity);
        this.mClassLoader = getClass().getClassLoader();
    }

    public Bundle(Bundle b) {
        this.mMap = null;
        this.mParcelledData = null;
        this.mHasFds = false;
        this.mFdsKnown = true;
        this.mAllowFds = true;
        if (b.mParcelledData != null) {
            this.mParcelledData = Parcel.obtain();
            this.mParcelledData.appendFrom(b.mParcelledData, 0, b.mParcelledData.dataSize());
            this.mParcelledData.setDataPosition(0);
        } else {
            this.mParcelledData = null;
        }
        if (b.mMap != null) {
            this.mMap = new ArrayMap<>(b.mMap);
        } else {
            this.mMap = null;
        }
        this.mHasFds = b.mHasFds;
        this.mFdsKnown = b.mFdsKnown;
        this.mClassLoader = b.mClassLoader;
    }

    public static Bundle forPair(String key, String value) {
        Bundle b = new Bundle(1);
        b.putString(key, value);
        return b;
    }

    public String getPairValue() {
        unparcel();
        int size = this.mMap.size();
        if (size > 1) {
            Log.w(TAG, "getPairValue() used on Bundle with multiple pairs.");
        }
        if (size == 0) {
            return null;
        }
        Object o = this.mMap.valueAt(0);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            typeWarning("getPairValue()", o, "String", e);
            return null;
        }
    }

    public void setClassLoader(ClassLoader loader) {
        this.mClassLoader = loader;
    }

    public ClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    public boolean setAllowFds(boolean allowFds) {
        boolean orig = this.mAllowFds;
        this.mAllowFds = allowFds;
        return orig;
    }

    public Object clone() {
        return new Bundle(this);
    }

    synchronized void unparcel() {
        int N;
        if (this.mParcelledData == null || (N = this.mParcelledData.readInt()) < 0) {
            return;
        }
        if (this.mMap == null) {
            this.mMap = new ArrayMap<>(N);
        } else {
            this.mMap.erase();
            this.mMap.ensureCapacity(N);
        }
        this.mParcelledData.readArrayMapInternal(this.mMap, N, this.mClassLoader);
        this.mParcelledData.recycle();
        this.mParcelledData = null;
    }

    public boolean isParcelled() {
        return this.mParcelledData != null;
    }

    public int size() {
        unparcel();
        return this.mMap.size();
    }

    public boolean isEmpty() {
        unparcel();
        return this.mMap.isEmpty();
    }

    public void clear() {
        unparcel();
        this.mMap.clear();
        this.mHasFds = false;
        this.mFdsKnown = true;
    }

    public boolean containsKey(String key) {
        unparcel();
        return this.mMap.containsKey(key);
    }

    public Object get(String key) {
        unparcel();
        return this.mMap.get(key);
    }

    public void remove(String key) {
        unparcel();
        this.mMap.remove(key);
    }

    public void putAll(Bundle map) {
        unparcel();
        map.unparcel();
        this.mMap.putAll((ArrayMap<? extends String, ? extends Object>) map.mMap);
        this.mHasFds |= map.mHasFds;
        this.mFdsKnown = this.mFdsKnown && map.mFdsKnown;
    }

    public Set<String> keySet() {
        unparcel();
        return this.mMap.keySet();
    }

    public boolean hasFileDescriptors() {
        if (!this.mFdsKnown) {
            boolean fdFound = false;
            if (this.mParcelledData != null) {
                if (this.mParcelledData.hasFileDescriptors()) {
                    fdFound = true;
                }
            } else {
                int i = this.mMap.size() - 1;
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    Object obj = this.mMap.valueAt(i);
                    if (obj instanceof Parcelable) {
                        if ((((Parcelable) obj).describeContents() & 1) != 0) {
                            fdFound = true;
                            break;
                        }
                    } else if (obj instanceof Parcelable[]) {
                        Parcelable[] array = (Parcelable[]) obj;
                        int n = array.length - 1;
                        while (true) {
                            if (n < 0) {
                                break;
                            } else if ((array[n].describeContents() & 1) == 0) {
                                n--;
                            } else {
                                fdFound = true;
                                break;
                            }
                        }
                    } else if (obj instanceof SparseArray) {
                        SparseArray<? extends Parcelable> array2 = (SparseArray) obj;
                        int n2 = array2.size() - 1;
                        while (true) {
                            if (n2 < 0) {
                                break;
                            } else if ((((Parcelable) array2.get(n2)).describeContents() & 1) == 0) {
                                n2--;
                            } else {
                                fdFound = true;
                                break;
                            }
                        }
                    } else if (obj instanceof ArrayList) {
                        ArrayList array3 = (ArrayList) obj;
                        if (array3.size() > 0 && (array3.get(0) instanceof Parcelable)) {
                            int n3 = array3.size() - 1;
                            while (true) {
                                if (n3 >= 0) {
                                    Parcelable p = (Parcelable) array3.get(n3);
                                    if (p == null || (p.describeContents() & 1) == 0) {
                                        n3--;
                                    } else {
                                        fdFound = true;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    i--;
                }
            }
            this.mHasFds = fdFound;
            this.mFdsKnown = true;
        }
        return this.mHasFds;
    }

    public void putBoolean(String key, boolean value) {
        unparcel();
        this.mMap.put(key, Boolean.valueOf(value));
    }

    public void putByte(String key, byte value) {
        unparcel();
        this.mMap.put(key, Byte.valueOf(value));
    }

    public void putChar(String key, char value) {
        unparcel();
        this.mMap.put(key, Character.valueOf(value));
    }

    public void putShort(String key, short value) {
        unparcel();
        this.mMap.put(key, Short.valueOf(value));
    }

    public void putInt(String key, int value) {
        unparcel();
        this.mMap.put(key, Integer.valueOf(value));
    }

    public void putLong(String key, long value) {
        unparcel();
        this.mMap.put(key, Long.valueOf(value));
    }

    public void putFloat(String key, float value) {
        unparcel();
        this.mMap.put(key, Float.valueOf(value));
    }

    public void putDouble(String key, double value) {
        unparcel();
        this.mMap.put(key, Double.valueOf(value));
    }

    public void putString(String key, String value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putCharSequence(String key, CharSequence value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putParcelable(String key, Parcelable value) {
        unparcel();
        this.mMap.put(key, value);
        this.mFdsKnown = false;
    }

    public void putParcelableArray(String key, Parcelable[] value) {
        unparcel();
        this.mMap.put(key, value);
        this.mFdsKnown = false;
    }

    public void putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        unparcel();
        this.mMap.put(key, value);
        this.mFdsKnown = false;
    }

    public void putParcelableList(String key, List<? extends Parcelable> value) {
        unparcel();
        this.mMap.put(key, value);
        this.mFdsKnown = false;
    }

    public void putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
        unparcel();
        this.mMap.put(key, value);
        this.mFdsKnown = false;
    }

    public void putIntegerArrayList(String key, ArrayList<Integer> value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putStringArrayList(String key, ArrayList<String> value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putSerializable(String key, Serializable value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putBooleanArray(String key, boolean[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putByteArray(String key, byte[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putShortArray(String key, short[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putCharArray(String key, char[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putIntArray(String key, int[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putLongArray(String key, long[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putFloatArray(String key, float[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putDoubleArray(String key, double[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putStringArray(String key, String[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putCharSequenceArray(String key, CharSequence[] value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putBundle(String key, Bundle value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public void putBinder(String key, IBinder value) {
        unparcel();
        this.mMap.put(key, value);
    }

    @Deprecated
    public void putIBinder(String key, IBinder value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public boolean getBoolean(String key) {
        unparcel();
        return getBoolean(key, false);
    }

    private void typeWarning(String key, Object value, String className, Object defaultValue, ClassCastException e) {
        Log.w(TAG, "Key " + key + " expected " + className + " but value was a " + value.getClass().getName() + ".  The default value " + defaultValue + " was returned.");
        Log.w(TAG, "Attempt to cast generated internal exception:", e);
    }

    private void typeWarning(String key, Object value, String className, ClassCastException e) {
        typeWarning(key, value, className, "<null>", e);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Boolean) o).booleanValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Boolean", Boolean.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public byte getByte(String key) {
        unparcel();
        return getByte(key, (byte) 0).byteValue();
    }

    public Byte getByte(String key, byte defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return Byte.valueOf(defaultValue);
        }
        try {
            return (Byte) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Byte", Byte.valueOf(defaultValue), e);
            return Byte.valueOf(defaultValue);
        }
    }

    public char getChar(String key) {
        unparcel();
        return getChar(key, (char) 0);
    }

    public char getChar(String key, char defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Character) o).charValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Character", Character.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public short getShort(String key) {
        unparcel();
        return getShort(key, (short) 0);
    }

    public short getShort(String key, short defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Short) o).shortValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Short", Short.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public int getInt(String key) {
        unparcel();
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Integer) o).intValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Integer", Integer.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public long getLong(String key) {
        unparcel();
        return getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Long) o).longValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Long", Long.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public float getFloat(String key) {
        unparcel();
        return getFloat(key, 0.0f);
    }

    public float getFloat(String key, float defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Float) o).floatValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Float", Float.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public double getDouble(String key) {
        unparcel();
        return getDouble(key, 0.0d);
    }

    public double getDouble(String key, double defaultValue) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return ((Double) o).doubleValue();
        } catch (ClassCastException e) {
            typeWarning(key, o, "Double", Double.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public String getString(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String", e);
            return null;
        }
    }

    public String getString(String key, String defaultValue) {
        String s = getString(key);
        return s == null ? defaultValue : s;
    }

    public CharSequence getCharSequence(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        try {
            return (CharSequence) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "CharSequence", e);
            return null;
        }
    }

    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        CharSequence cs = getCharSequence(key);
        return cs == null ? defaultValue : cs;
    }

    public Bundle getBundle(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (Bundle) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, TAG, e);
            return null;
        }
    }

    public <T extends Parcelable> T getParcelable(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (T) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Parcelable", e);
            return null;
        }
    }

    public Parcelable[] getParcelableArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (Parcelable[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Parcelable[]", e);
            return null;
        }
    }

    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList", e);
            return null;
        }
    }

    public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (SparseArray) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "SparseArray", e);
            return null;
        }
    }

    public Serializable getSerializable(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (Serializable) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Serializable", e);
            return null;
        }
    }

    public ArrayList<Integer> getIntegerArrayList(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<Integer>", e);
            return null;
        }
    }

    public ArrayList<String> getStringArrayList(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<String>", e);
            return null;
        }
    }

    public ArrayList<CharSequence> getCharSequenceArrayList(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<CharSequence>", e);
            return null;
        }
    }

    public boolean[] getBooleanArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (boolean[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    public byte[] getByteArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (byte[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    public short[] getShortArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (short[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "short[]", e);
            return null;
        }
    }

    public char[] getCharArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (char[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "char[]", e);
            return null;
        }
    }

    public int[] getIntArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (int[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "int[]", e);
            return null;
        }
    }

    public long[] getLongArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (long[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "long[]", e);
            return null;
        }
    }

    public float[] getFloatArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (float[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "float[]", e);
            return null;
        }
    }

    public double[] getDoubleArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (double[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "double[]", e);
            return null;
        }
    }

    public String[] getStringArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (String[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String[]", e);
            return null;
        }
    }

    public CharSequence[] getCharSequenceArray(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (CharSequence[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "CharSequence[]", e);
            return null;
        }
    }

    public IBinder getBinder(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (IBinder) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "IBinder", e);
            return null;
        }
    }

    @Deprecated
    public IBinder getIBinder(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (IBinder) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "IBinder", e);
            return null;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        int mask = 0;
        if (hasFileDescriptors()) {
            mask = 0 | 1;
        }
        return mask;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        boolean oldAllowFds = parcel.pushAllowFds(this.mAllowFds);
        try {
            if (this.mParcelledData != null) {
                int length = this.mParcelledData.dataSize();
                parcel.writeInt(length);
                parcel.writeInt(BUNDLE_MAGIC);
                parcel.appendFrom(this.mParcelledData, 0, length);
            } else {
                int lengthPos = parcel.dataPosition();
                parcel.writeInt(-1);
                parcel.writeInt(BUNDLE_MAGIC);
                int startPos = parcel.dataPosition();
                parcel.writeArrayMapInternal(this.mMap);
                int endPos = parcel.dataPosition();
                parcel.setDataPosition(lengthPos);
                parcel.writeInt(endPos - startPos);
                parcel.setDataPosition(endPos);
            }
        } finally {
            parcel.restoreAllowFds(oldAllowFds);
        }
    }

    public void readFromParcel(Parcel parcel) {
        int length = parcel.readInt();
        if (length < 0) {
            throw new RuntimeException("Bad length in parcel: " + length);
        }
        readFromParcelInner(parcel, length);
    }

    void readFromParcelInner(Parcel parcel, int length) {
        int magic = parcel.readInt();
        if (magic != BUNDLE_MAGIC) {
            throw new IllegalStateException("Bad magic number for Bundle: 0x" + Integer.toHexString(magic));
        }
        int offset = parcel.dataPosition();
        parcel.setDataPosition(offset + length);
        Parcel p = Parcel.obtain();
        p.setDataPosition(0);
        p.appendFrom(parcel, offset, length);
        p.setDataPosition(0);
        this.mParcelledData = p;
        this.mHasFds = p.hasFileDescriptors();
        this.mFdsKnown = true;
    }

    public synchronized String toString() {
        if (this.mParcelledData != null) {
            return "Bundle[mParcelledData.dataSize=" + this.mParcelledData.dataSize() + "]";
        }
        return "Bundle[" + this.mMap.toString() + "]";
    }
}