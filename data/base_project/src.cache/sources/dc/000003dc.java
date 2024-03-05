package android.content.res;

import android.content.res.XmlBlock;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.util.Arrays;

/* loaded from: TypedArray.class */
public class TypedArray {
    private final Resources mResources;
    XmlBlock.Parser mXml;
    int[] mRsrcs;
    int[] mData;
    int[] mIndices;
    int mLength;
    TypedValue mValue = new TypedValue();

    public int length() {
        return this.mLength;
    }

    public int getIndexCount() {
        return this.mIndices[0];
    }

    public int getIndex(int at) {
        return this.mIndices[1 + at];
    }

    public Resources getResources() {
        return this.mResources;
    }

    public CharSequence getText(int index) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index2);
        }
        TypedValue v = this.mValue;
        if (getValueAt(index2, v)) {
            Log.w("Resources", "Converting to string: " + v);
            return v.coerceToString();
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(type));
        return null;
    }

    public String getString(int index) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index2).toString();
        }
        TypedValue v = this.mValue;
        if (getValueAt(index2, v)) {
            Log.w("Resources", "Converting to string: " + v);
            CharSequence cs = v.coerceToString();
            if (cs != null) {
                return cs.toString();
            }
            return null;
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(type));
        return null;
    }

    public String getNonResourceString(int index) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 3) {
            int cookie = data[index2 + 2];
            if (cookie < 0) {
                return this.mXml.getPooledString(data[index2 + 1]).toString();
            }
            return null;
        }
        return null;
    }

    public String getNonConfigurationString(int index, int allowedChangingConfigs) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if ((data[index2 + 4] & (allowedChangingConfigs ^ (-1))) != 0 || type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index2).toString();
        }
        TypedValue v = this.mValue;
        if (getValueAt(index2, v)) {
            Log.w("Resources", "Converting to string: " + v);
            CharSequence cs = v.coerceToString();
            if (cs != null) {
                return cs.toString();
            }
            return null;
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(type));
        return null;
    }

    public boolean getBoolean(int index, boolean defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index2 + 1] != 0;
        }
        TypedValue v = this.mValue;
        if (getValueAt(index2, v)) {
            Log.w("Resources", "Converting to boolean: " + v);
            return XmlUtils.convertValueToBoolean(v.coerceToString(), defValue);
        }
        Log.w("Resources", "getBoolean of bad type: 0x" + Integer.toHexString(type));
        return defValue;
    }

    public int getInt(int index, int defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index2 + 1];
        }
        TypedValue v = this.mValue;
        if (getValueAt(index2, v)) {
            Log.w("Resources", "Converting to int: " + v);
            return XmlUtils.convertValueToInt(v.coerceToString(), defValue);
        }
        Log.w("Resources", "getInt of bad type: 0x" + Integer.toHexString(type));
        return defValue;
    }

    public float getFloat(int index, float defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 4) {
            return Float.intBitsToFloat(data[index2 + 1]);
        }
        if (type >= 16 && type <= 31) {
            return data[index2 + 1];
        }
        TypedValue v = this.mValue;
        if (getValueAt(index2, v)) {
            Log.w("Resources", "Converting to float: " + v);
            CharSequence str = v.coerceToString();
            if (str != null) {
                return Float.parseFloat(str.toString());
            }
        }
        Log.w("Resources", "getFloat of bad type: 0x" + Integer.toHexString(type));
        return defValue;
    }

    public int getColor(int index, int defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index2 + 1];
        }
        if (type == 3) {
            TypedValue value = this.mValue;
            if (getValueAt(index2, value)) {
                ColorStateList csl = this.mResources.loadColorStateList(value, value.resourceId);
                return csl.getDefaultColor();
            }
            return defValue;
        }
        throw new UnsupportedOperationException("Can't convert to color: type=0x" + Integer.toHexString(type));
    }

    public ColorStateList getColorStateList(int index) {
        TypedValue value = this.mValue;
        if (getValueAt(index * 6, value)) {
            return this.mResources.loadColorStateList(value, value.resourceId);
        }
        return null;
    }

    public int getInteger(int index, int defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index2 + 1];
        }
        throw new UnsupportedOperationException("Can't convert to integer: type=0x" + Integer.toHexString(type));
    }

    public float getDimension(int index, float defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimension(data[index2 + 1], this.mResources.mMetrics);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(type));
    }

    public int getDimensionPixelOffset(int index, int defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelOffset(data[index2 + 1], this.mResources.mMetrics);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(type));
    }

    public int getDimensionPixelSize(int index, int defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index2 + 1], this.mResources.mMetrics);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(type));
    }

    public int getLayoutDimension(int index, String name) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type >= 16 && type <= 31) {
            return data[index2 + 1];
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index2 + 1], this.mResources.mMetrics);
        }
        throw new RuntimeException(getPositionDescription() + ": You must supply a " + name + " attribute.");
    }

    public int getLayoutDimension(int index, int defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type >= 16 && type <= 31) {
            return data[index2 + 1];
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index2 + 1], this.mResources.mMetrics);
        }
        return defValue;
    }

    public float getFraction(int index, int base, int pbase, float defValue) {
        int index2 = index * 6;
        int[] data = this.mData;
        int type = data[index2 + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 6) {
            return TypedValue.complexToFraction(data[index2 + 1], base, pbase);
        }
        throw new UnsupportedOperationException("Can't convert to fraction: type=0x" + Integer.toHexString(type));
    }

    public int getResourceId(int index, int defValue) {
        int resid;
        int index2 = index * 6;
        int[] data = this.mData;
        if (data[index2 + 0] != 0 && (resid = data[index2 + 3]) != 0) {
            return resid;
        }
        return defValue;
    }

    public Drawable getDrawable(int index) {
        TypedValue value = this.mValue;
        if (getValueAt(index * 6, value)) {
            return this.mResources.loadDrawable(value, value.resourceId);
        }
        return null;
    }

    public CharSequence[] getTextArray(int index) {
        TypedValue value = this.mValue;
        if (getValueAt(index * 6, value)) {
            return this.mResources.getTextArray(value.resourceId);
        }
        return null;
    }

    public boolean getValue(int index, TypedValue outValue) {
        return getValueAt(index * 6, outValue);
    }

    public boolean hasValue(int index) {
        int[] data = this.mData;
        int type = data[(index * 6) + 0];
        return type != 0;
    }

    public TypedValue peekValue(int index) {
        TypedValue value = this.mValue;
        if (getValueAt(index * 6, value)) {
            return value;
        }
        return null;
    }

    public String getPositionDescription() {
        return this.mXml != null ? this.mXml.getPositionDescription() : "<internal>";
    }

    public void recycle() {
        synchronized (this.mResources.mAccessLock) {
            TypedArray cached = this.mResources.mCachedStyledAttributes;
            if (cached == null || cached.mData.length < this.mData.length) {
                this.mXml = null;
                this.mResources.mCachedStyledAttributes = this;
            }
        }
    }

    private boolean getValueAt(int index, TypedValue outValue) {
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return false;
        }
        outValue.type = type;
        outValue.data = data[index + 1];
        outValue.assetCookie = data[index + 2];
        outValue.resourceId = data[index + 3];
        outValue.changingConfigurations = data[index + 4];
        outValue.density = data[index + 5];
        outValue.string = type == 3 ? loadStringValueAt(index) : null;
        return true;
    }

    private CharSequence loadStringValueAt(int index) {
        int[] data = this.mData;
        int cookie = data[index + 2];
        if (cookie < 0) {
            if (this.mXml != null) {
                return this.mXml.getPooledString(data[index + 1]);
            }
            return null;
        }
        return this.mResources.mAssets.getPooledString(cookie, data[index + 1]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypedArray(Resources resources, int[] data, int[] indices, int len) {
        this.mResources = resources;
        this.mData = data;
        this.mIndices = indices;
        this.mLength = len;
    }

    public String toString() {
        return Arrays.toString(this.mData);
    }
}