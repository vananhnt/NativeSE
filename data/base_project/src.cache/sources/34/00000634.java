package android.hardware.camera2.impl;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.Face;
import android.hardware.camera2.Rational;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: CameraMetadataNative.class */
public class CameraMetadataNative extends CameraMetadata implements Parcelable {
    private static final int NATIVE_JPEG_FORMAT = 33;
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_INT32 = 1;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_DOUBLE = 4;
    public static final int TYPE_RATIONAL = 5;
    public static final int NUM_TYPES = 6;
    private long mMetadataPtr;
    private static final String TAG = "CameraMetadataJV";
    private static final boolean VERBOSE = Log.isLoggable(TAG, 2);
    public static final Parcelable.Creator<CameraMetadataNative> CREATOR = new Parcelable.Creator<CameraMetadataNative>() { // from class: android.hardware.camera2.impl.CameraMetadataNative.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CameraMetadataNative createFromParcel(Parcel in) {
            CameraMetadataNative metadata = new CameraMetadataNative();
            metadata.readFromParcel(in);
            return metadata;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CameraMetadataNative[] newArray(int size) {
            return new CameraMetadataNative[size];
        }
    };
    private static final HashMap<Class<? extends Enum>, int[]> sEnumValues = new HashMap<>();
    static HashMap<Class<?>, MetadataMarshalClass<?>> sMarshalerMap = new HashMap<>();

    private native long nativeAllocate();

    private native long nativeAllocateCopy(CameraMetadataNative cameraMetadataNative) throws NullPointerException;

    private native synchronized void nativeWriteToParcel(Parcel parcel);

    private native synchronized void nativeReadFromParcel(Parcel parcel);

    private native synchronized void nativeSwap(CameraMetadataNative cameraMetadataNative) throws NullPointerException;

    private native synchronized void nativeClose();

    private native synchronized boolean nativeIsEmpty();

    private native synchronized int nativeGetEntryCount();

    private native synchronized byte[] nativeReadValues(int i);

    private native synchronized void nativeWriteValues(int i, byte[] bArr);

    private static native int nativeGetTagFromKey(String str) throws IllegalArgumentException;

    private static native int nativeGetTypeFromTag(int i) throws IllegalArgumentException;

    private static native void nativeClassInit();

    static {
        nativeClassInit();
        if (VERBOSE) {
            Log.v(TAG, "Shall register metadata marshalers");
        }
        registerMarshaler(new MetadataMarshalRect());
        registerMarshaler(new MetadataMarshalSize());
        registerMarshaler(new MetadataMarshalString());
        if (VERBOSE) {
            Log.v(TAG, "Registered metadata marshalers");
        }
    }

    public CameraMetadataNative() {
        this.mMetadataPtr = nativeAllocate();
        if (this.mMetadataPtr == 0) {
            throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
        }
    }

    public CameraMetadataNative(CameraMetadataNative other) {
        this.mMetadataPtr = nativeAllocateCopy(other);
        if (this.mMetadataPtr == 0) {
            throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        nativeWriteToParcel(dest);
    }

    @Override // android.hardware.camera2.CameraMetadata
    public <T> T get(CameraMetadata.Key<T> key) {
        T value = (T) getOverride(key);
        if (value != null) {
            return value;
        }
        return (T) getBase(key);
    }

    public void readFromParcel(Parcel in) {
        nativeReadFromParcel(in);
    }

    public <T> void set(CameraMetadata.Key<T> key, T value) {
        if (setOverride(key, value)) {
            return;
        }
        setBase(key, value);
    }

    private void close() {
        nativeClose();
        this.mMetadataPtr = 0L;
    }

    private static int getTypeSize(int nativeType) {
        switch (nativeType) {
            case 0:
                return 1;
            case 1:
            case 2:
                return 4;
            case 3:
            case 4:
            case 5:
                return 8;
            default:
                throw new UnsupportedOperationException("Unknown type, can't get size " + nativeType);
        }
    }

    private static Class<?> getExpectedType(int nativeType) {
        switch (nativeType) {
            case 0:
                return Byte.TYPE;
            case 1:
                return Integer.TYPE;
            case 2:
                return Float.TYPE;
            case 3:
                return Long.TYPE;
            case 4:
                return Double.TYPE;
            case 5:
                return Rational.class;
            default:
                throw new UnsupportedOperationException("Unknown type, can't map to Java type " + nativeType);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v14, types: [java.lang.Byte] */
    /* JADX WARN: Type inference failed for: r0v46, types: [java.lang.Byte] */
    private static <T> int packSingleNative(T value, ByteBuffer buffer, Class<T> type, int nativeType, boolean sizeOnly) {
        if (!sizeOnly) {
            if (nativeType == 0 && type == Boolean.TYPE) {
                boolean asBool = ((Boolean) value).booleanValue();
                byte asByte = (byte) (asBool ? 1 : 0);
                value = Byte.valueOf(asByte);
            } else if (nativeType == 0 && type == Integer.TYPE) {
                int asInt = ((Integer) value).intValue();
                byte asByte2 = (byte) asInt;
                value = Byte.valueOf(asByte2);
            } else if (type != getExpectedType(nativeType)) {
                throw new UnsupportedOperationException("Tried to pack a type of " + type + " but we expected the type to be " + getExpectedType(nativeType));
            }
            if (nativeType == 0) {
                buffer.put(((Byte) value).byteValue());
            } else if (nativeType == 1) {
                buffer.putInt(((Integer) value).intValue());
            } else if (nativeType == 2) {
                buffer.putFloat(((Float) value).floatValue());
            } else if (nativeType == 3) {
                buffer.putLong(((Long) value).longValue());
            } else if (nativeType == 4) {
                buffer.putDouble(((Double) value).doubleValue());
            } else if (nativeType == 5) {
                Rational r = (Rational) value;
                buffer.putInt(r.getNumerator());
                buffer.putInt(r.getDenominator());
            }
        }
        return getTypeSize(nativeType);
    }

    private static <T> int packSingle(T value, ByteBuffer buffer, Class<T> type, int nativeType, boolean sizeOnly) {
        int size;
        if (type.isPrimitive() || type == Rational.class) {
            size = packSingleNative(value, buffer, type, nativeType, sizeOnly);
        } else if (type.isEnum()) {
            size = packEnum((Enum) value, buffer, type, nativeType, sizeOnly);
        } else if (type.isArray()) {
            size = packArray(value, buffer, type, nativeType, sizeOnly);
        } else {
            size = packClass(value, buffer, type, nativeType, sizeOnly);
        }
        return size;
    }

    private static <T extends Enum<T>> int packEnum(T value, ByteBuffer buffer, Class<T> type, int nativeType, boolean sizeOnly) {
        return packSingleNative(Integer.valueOf(getEnumValue(value)), buffer, Integer.TYPE, nativeType, sizeOnly);
    }

    private static <T> int packClass(T value, ByteBuffer buffer, Class<T> type, int nativeType, boolean sizeOnly) {
        MetadataMarshalClass<T> marshaler = getMarshaler(type, nativeType);
        if (marshaler == null) {
            throw new IllegalArgumentException(String.format("Unknown Key type: %s", type));
        }
        return marshaler.marshal(value, buffer, nativeType, sizeOnly);
    }

    private static <T> int packArray(T value, ByteBuffer buffer, Class<T> type, int nativeType, boolean sizeOnly) {
        int size = 0;
        int arrayLength = Array.getLength(value);
        Class<Object> componentType = type.getComponentType();
        for (int i = 0; i < arrayLength; i++) {
            size += packSingle(Array.get(value, i), buffer, componentType, nativeType, sizeOnly);
        }
        return size;
    }

    private static <T> T unpackSingleNative(ByteBuffer buffer, Class<T> type, int nativeType) {
        Object rational;
        if (nativeType == 0) {
            rational = Byte.valueOf(buffer.get());
        } else if (nativeType == 1) {
            rational = Integer.valueOf(buffer.getInt());
        } else if (nativeType == 2) {
            rational = Float.valueOf(buffer.getFloat());
        } else if (nativeType == 3) {
            rational = Long.valueOf(buffer.getLong());
        } else if (nativeType == 4) {
            rational = Double.valueOf(buffer.getDouble());
        } else if (nativeType == 5) {
            rational = new Rational(buffer.getInt(), buffer.getInt());
        } else {
            throw new UnsupportedOperationException("Unknown type, can't unpack a native type " + nativeType);
        }
        if (nativeType == 0 && type == Boolean.TYPE) {
            byte asByte = ((Byte) rational).byteValue();
            boolean asBool = asByte != 0;
            rational = Boolean.valueOf(asBool);
        } else if (nativeType == 0 && type == Integer.TYPE) {
            byte asByte2 = ((Byte) rational).byteValue();
            rational = Integer.valueOf(asByte2);
        } else if (type != getExpectedType(nativeType)) {
            throw new UnsupportedOperationException("Tried to unpack a type of " + type + " but we expected the type to be " + getExpectedType(nativeType));
        }
        return (T) rational;
    }

    private static <T> T unpackSingle(ByteBuffer buffer, Class<T> type, int nativeType) {
        if (type.isPrimitive() || type == Rational.class) {
            return (T) unpackSingleNative(buffer, type, nativeType);
        }
        if (type.isEnum()) {
            return (T) unpackEnum(buffer, type, nativeType);
        }
        if (type.isArray()) {
            return (T) unpackArray(buffer, type, nativeType);
        }
        T instance = (T) unpackClass(buffer, type, nativeType);
        return instance;
    }

    private static <T extends Enum<T>> T unpackEnum(ByteBuffer buffer, Class<T> type, int nativeType) {
        int ordinal = ((Integer) unpackSingleNative(buffer, Integer.TYPE, nativeType)).intValue();
        return (T) getEnumFromValue(type, ordinal);
    }

    private static <T> T unpackClass(ByteBuffer buffer, Class<T> type, int nativeType) {
        MetadataMarshalClass<T> marshaler = getMarshaler(type, nativeType);
        if (marshaler == null) {
            throw new IllegalArgumentException("Unknown class type: " + type);
        }
        return marshaler.unmarshal(buffer, nativeType);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T> T unpackArray(ByteBuffer buffer, Class<T> type, int nativeType) {
        Object array;
        Class<?> componentType = type.getComponentType();
        int elementSize = getTypeSize(nativeType);
        MetadataMarshalClass<?> marshaler = getMarshaler(componentType, nativeType);
        if (marshaler != null) {
            elementSize = marshaler.getNativeSize(nativeType);
        }
        if (elementSize != -1) {
            int remaining = buffer.remaining();
            int arraySize = remaining / elementSize;
            if (VERBOSE) {
                Log.v(TAG, String.format("Attempting to unpack array (count = %d, element size = %d, bytes remaining = %d) for type %s", Integer.valueOf(arraySize), Integer.valueOf(elementSize), Integer.valueOf(remaining), type));
            }
            array = Array.newInstance(componentType, arraySize);
            for (int i = 0; i < arraySize; i++) {
                Object elem = unpackSingle(buffer, componentType, nativeType);
                Array.set(array, i, elem);
            }
        } else {
            ArrayList<Object> arrayList = new ArrayList<>();
            int primitiveSize = getTypeSize(nativeType);
            while (buffer.remaining() >= primitiveSize) {
                Object elem2 = unpackSingle(buffer, componentType, nativeType);
                arrayList.add(elem2);
            }
            array = arrayList.toArray((Object[]) Array.newInstance(componentType, 0));
        }
        if (buffer.remaining() != 0) {
            Log.e(TAG, "Trailing bytes (" + buffer.remaining() + ") left over after unpacking " + type);
        }
        return (T) array;
    }

    private <T> T getBase(CameraMetadata.Key<T> key) {
        int tag = key.getTag();
        byte[] values = readValues(tag);
        if (values == null) {
            return null;
        }
        int nativeType = getNativeType(tag);
        ByteBuffer buffer = ByteBuffer.wrap(values).order(ByteOrder.nativeOrder());
        return (T) unpackSingle(buffer, key.getType(), nativeType);
    }

    private <T> T getOverride(CameraMetadata.Key<T> key) {
        if (key.equals(CameraCharacteristics.SCALER_AVAILABLE_FORMATS)) {
            return (T) getAvailableFormats();
        }
        if (key.equals(CaptureResult.STATISTICS_FACES)) {
            return (T) getFaces();
        }
        if (key.equals(CaptureResult.STATISTICS_FACE_RECTANGLES)) {
            return (T) fixFaceRectangles();
        }
        return null;
    }

    private int[] getAvailableFormats() {
        int[] availableFormats = (int[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS);
        for (int i = 0; i < availableFormats.length; i++) {
            if (availableFormats[i] == 33) {
                availableFormats[i] = 256;
            }
        }
        return availableFormats;
    }

    private Face[] getFaces() {
        Integer faceDetectMode = (Integer) get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
        if (faceDetectMode == null) {
            Log.w(TAG, "Face detect mode metadata is null, assuming the mode is SIMPLE");
            faceDetectMode = 1;
        } else if (faceDetectMode.intValue() == 0) {
            return new Face[0];
        } else {
            if (faceDetectMode.intValue() != 1 && faceDetectMode.intValue() != 2) {
                Log.w(TAG, "Unknown face detect mode: " + faceDetectMode);
                return new Face[0];
            }
        }
        byte[] faceScores = (byte[]) get(CaptureResult.STATISTICS_FACE_SCORES);
        Rect[] faceRectangles = (Rect[]) get(CaptureResult.STATISTICS_FACE_RECTANGLES);
        if (faceScores == null || faceRectangles == null) {
            Log.w(TAG, "Expect face scores and rectangles to be non-null");
            return new Face[0];
        }
        if (faceScores.length != faceRectangles.length) {
            Log.w(TAG, String.format("Face score size(%d) doesn match face rectangle size(%d)!", Integer.valueOf(faceScores.length), Integer.valueOf(faceRectangles.length)));
        }
        int numFaces = Math.min(faceScores.length, faceRectangles.length);
        int[] faceIds = (int[]) get(CaptureResult.STATISTICS_FACE_IDS);
        int[] faceLandmarks = (int[]) get(CaptureResult.STATISTICS_FACE_LANDMARKS);
        if (faceDetectMode.intValue() == 2) {
            if (faceIds == null || faceLandmarks == null) {
                Log.w(TAG, "Expect face ids and landmarks to be non-null for FULL mode,fallback to SIMPLE mode");
                faceDetectMode = 1;
            } else {
                if (faceIds.length != numFaces || faceLandmarks.length != numFaces * 6) {
                    Log.w(TAG, String.format("Face id size(%d), or face landmark size(%d) don'tmatch face number(%d)!", Integer.valueOf(faceIds.length), Integer.valueOf(faceLandmarks.length * 6), Integer.valueOf(numFaces)));
                }
                numFaces = Math.min(Math.min(numFaces, faceIds.length), faceLandmarks.length / 6);
            }
        }
        ArrayList<Face> faceList = new ArrayList<>();
        if (faceDetectMode.intValue() == 1) {
            for (int i = 0; i < numFaces; i++) {
                if (faceScores[i] <= 100 && faceScores[i] >= 1) {
                    faceList.add(new Face(faceRectangles[i], faceScores[i]));
                }
            }
        } else {
            for (int i2 = 0; i2 < numFaces; i2++) {
                if (faceScores[i2] <= 100 && faceScores[i2] >= 1 && faceIds[i2] >= 0) {
                    Point leftEye = new Point(faceLandmarks[i2 * 6], faceLandmarks[(i2 * 6) + 1]);
                    Point rightEye = new Point(faceLandmarks[(i2 * 6) + 2], faceLandmarks[(i2 * 6) + 3]);
                    Point mouth = new Point(faceLandmarks[(i2 * 6) + 4], faceLandmarks[(i2 * 6) + 5]);
                    Face face = new Face(faceRectangles[i2], faceScores[i2], faceIds[i2], leftEye, rightEye, mouth);
                    faceList.add(face);
                }
            }
        }
        Face[] faces = new Face[faceList.size()];
        faceList.toArray(faces);
        return faces;
    }

    private Rect[] fixFaceRectangles() {
        Rect[] faceRectangles = (Rect[]) getBase(CaptureResult.STATISTICS_FACE_RECTANGLES);
        if (faceRectangles == null) {
            return null;
        }
        Rect[] fixedFaceRectangles = new Rect[faceRectangles.length];
        for (int i = 0; i < faceRectangles.length; i++) {
            fixedFaceRectangles[i] = new Rect(faceRectangles[i].left, faceRectangles[i].top, faceRectangles[i].right - faceRectangles[i].left, faceRectangles[i].bottom - faceRectangles[i].top);
        }
        return fixedFaceRectangles;
    }

    private <T> void setBase(CameraMetadata.Key<T> key, T value) {
        int tag = key.getTag();
        if (value == null) {
            writeValues(tag, null);
            return;
        }
        int nativeType = getNativeType(tag);
        int size = packSingle(value, null, key.getType(), nativeType, true);
        byte[] values = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(values).order(ByteOrder.nativeOrder());
        packSingle(value, buffer, key.getType(), nativeType, false);
        writeValues(tag, values);
    }

    private <T> boolean setOverride(CameraMetadata.Key<T> key, T value) {
        if (key.equals(CameraCharacteristics.SCALER_AVAILABLE_FORMATS)) {
            return setAvailableFormats((int[]) value);
        }
        return false;
    }

    private boolean setAvailableFormats(int[] value) {
        if (value == null) {
            return false;
        }
        int[] newValues = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            newValues[i] = value[i];
            if (value[i] == 256) {
                newValues[i] = 33;
            }
        }
        setBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS, newValues);
        return true;
    }

    public void swap(CameraMetadataNative other) {
        nativeSwap(other);
    }

    public int getEntryCount() {
        return nativeGetEntryCount();
    }

    public boolean isEmpty() {
        return nativeIsEmpty();
    }

    public static int getTag(String key) {
        return nativeGetTagFromKey(key);
    }

    public static int getNativeType(int tag) {
        return nativeGetTypeFromTag(tag);
    }

    public void writeValues(int tag, byte[] src) {
        nativeWriteValues(tag, src);
    }

    public byte[] readValues(int tag) {
        return nativeReadValues(tag);
    }

    protected void finalize() throws Throwable {
        try {
            close();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public static <T extends Enum<T>> void registerEnumValues(Class<T> enumType, int[] values) {
        if (enumType.getEnumConstants().length != values.length) {
            throw new IllegalArgumentException("Expected values array to be the same size as the enumTypes values " + values.length + " for type " + enumType);
        }
        if (VERBOSE) {
            Log.v(TAG, "Registered enum values for type " + enumType + " values");
        }
        sEnumValues.put(enumType, values);
    }

    private static <T extends Enum<T>> int getEnumValue(T enumValue) {
        int[] values = sEnumValues.get(enumValue.getClass());
        int ordinal = enumValue.ordinal();
        if (values != null) {
            return values[ordinal];
        }
        return ordinal;
    }

    private static <T extends Enum<T>> T getEnumFromValue(Class<T> enumType, int value) {
        int ordinal;
        int[] registeredValues = sEnumValues.get(enumType);
        if (registeredValues != null) {
            ordinal = -1;
            int i = 0;
            while (true) {
                if (i >= registeredValues.length) {
                    break;
                } else if (registeredValues[i] != value) {
                    i++;
                } else {
                    ordinal = i;
                    break;
                }
            }
        } else {
            ordinal = value;
        }
        T[] values = enumType.getEnumConstants();
        if (ordinal < 0 || ordinal >= values.length) {
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(value);
            objArr[1] = enumType;
            objArr[2] = Boolean.valueOf(registeredValues != null);
            throw new IllegalArgumentException(String.format("Argument 'value' (%d) was not a valid enum value for type %s (registered? %b)", objArr));
        }
        return values[ordinal];
    }

    private static <T> void registerMarshaler(MetadataMarshalClass<T> marshaler) {
        sMarshalerMap.put(marshaler.getMarshalingClass(), marshaler);
    }

    private static <T> MetadataMarshalClass<T> getMarshaler(Class<T> type, int nativeType) {
        MetadataMarshalClass<T> marshaler = (MetadataMarshalClass<T>) sMarshalerMap.get(type);
        if (marshaler != null && !marshaler.isNativeTypeSupported(nativeType)) {
            throw new UnsupportedOperationException("Unsupported type " + nativeType + " to be marshalled to/from a " + type);
        }
        return marshaler;
    }
}