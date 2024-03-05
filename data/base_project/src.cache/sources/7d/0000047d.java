package android.filterfw.core;

import gov.nist.core.Separators;
import java.util.Arrays;
import java.util.Map;

/* loaded from: FrameFormat.class */
public class FrameFormat {
    public static final int TYPE_UNSPECIFIED = 0;
    public static final int TYPE_BIT = 1;
    public static final int TYPE_BYTE = 2;
    public static final int TYPE_INT16 = 3;
    public static final int TYPE_INT32 = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_POINTER = 7;
    public static final int TYPE_OBJECT = 8;
    public static final int TARGET_UNSPECIFIED = 0;
    public static final int TARGET_SIMPLE = 1;
    public static final int TARGET_NATIVE = 2;
    public static final int TARGET_GPU = 3;
    public static final int TARGET_VERTEXBUFFER = 4;
    public static final int TARGET_RS = 5;
    public static final int SIZE_UNSPECIFIED = 0;
    public static final int BYTES_PER_SAMPLE_UNSPECIFIED = 1;
    protected static final int SIZE_UNKNOWN = -1;
    protected int mBaseType;
    protected int mBytesPerSample;
    protected int mSize;
    protected int mTarget;
    protected int[] mDimensions;
    protected KeyValueMap mMetaData;
    protected Class mObjectClass;

    /* JADX INFO: Access modifiers changed from: protected */
    public FrameFormat() {
        this.mBaseType = 0;
        this.mBytesPerSample = 1;
        this.mSize = -1;
        this.mTarget = 0;
    }

    public FrameFormat(int baseType, int target) {
        this.mBaseType = 0;
        this.mBytesPerSample = 1;
        this.mSize = -1;
        this.mTarget = 0;
        this.mBaseType = baseType;
        this.mTarget = target;
        initDefaults();
    }

    public static FrameFormat unspecified() {
        return new FrameFormat(0, 0);
    }

    public int getBaseType() {
        return this.mBaseType;
    }

    public boolean isBinaryDataType() {
        return this.mBaseType >= 1 && this.mBaseType <= 6;
    }

    public int getBytesPerSample() {
        return this.mBytesPerSample;
    }

    public int getValuesPerSample() {
        return this.mBytesPerSample / bytesPerSampleOf(this.mBaseType);
    }

    public int getTarget() {
        return this.mTarget;
    }

    public int[] getDimensions() {
        return this.mDimensions;
    }

    public int getDimension(int i) {
        return this.mDimensions[i];
    }

    public int getDimensionCount() {
        if (this.mDimensions == null) {
            return 0;
        }
        return this.mDimensions.length;
    }

    public boolean hasMetaKey(String key) {
        if (this.mMetaData != null) {
            return this.mMetaData.containsKey(key);
        }
        return false;
    }

    public boolean hasMetaKey(String key, Class expectedClass) {
        if (this.mMetaData != null && this.mMetaData.containsKey(key)) {
            if (!expectedClass.isAssignableFrom(this.mMetaData.get(key).getClass())) {
                throw new RuntimeException("FrameFormat meta-key '" + key + "' is of type " + this.mMetaData.get(key).getClass() + " but expected to be of type " + expectedClass + "!");
            }
            return true;
        }
        return false;
    }

    public Object getMetaValue(String key) {
        if (this.mMetaData != null) {
            return this.mMetaData.get(key);
        }
        return null;
    }

    public int getNumberOfDimensions() {
        if (this.mDimensions != null) {
            return this.mDimensions.length;
        }
        return 0;
    }

    public int getLength() {
        if (this.mDimensions == null || this.mDimensions.length < 1) {
            return -1;
        }
        return this.mDimensions[0];
    }

    public int getWidth() {
        return getLength();
    }

    public int getHeight() {
        if (this.mDimensions == null || this.mDimensions.length < 2) {
            return -1;
        }
        return this.mDimensions[1];
    }

    public int getDepth() {
        if (this.mDimensions == null || this.mDimensions.length < 3) {
            return -1;
        }
        return this.mDimensions[2];
    }

    public int getSize() {
        if (this.mSize == -1) {
            this.mSize = calcSize(this.mDimensions);
        }
        return this.mSize;
    }

    public Class getObjectClass() {
        return this.mObjectClass;
    }

    public MutableFrameFormat mutableCopy() {
        MutableFrameFormat result = new MutableFrameFormat();
        result.setBaseType(getBaseType());
        result.setTarget(getTarget());
        result.setBytesPerSample(getBytesPerSample());
        result.setDimensions(getDimensions());
        result.setObjectClass(getObjectClass());
        result.mMetaData = this.mMetaData == null ? null : (KeyValueMap) this.mMetaData.clone();
        return result;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FrameFormat)) {
            return false;
        }
        FrameFormat format = (FrameFormat) object;
        return format.mBaseType == this.mBaseType && format.mTarget == this.mTarget && format.mBytesPerSample == this.mBytesPerSample && Arrays.equals(format.mDimensions, this.mDimensions) && format.mMetaData.equals(this.mMetaData);
    }

    public int hashCode() {
        return ((4211 ^ this.mBaseType) ^ this.mBytesPerSample) ^ getSize();
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x00b1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean isCompatibleWith(android.filterfw.core.FrameFormat r5) {
        /*
            Method dump skipped, instructions count: 234
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.filterfw.core.FrameFormat.isCompatibleWith(android.filterfw.core.FrameFormat):boolean");
    }

    public boolean mayBeCompatibleWith(FrameFormat specification) {
        if (specification.getBaseType() != 0 && getBaseType() != 0 && getBaseType() != specification.getBaseType()) {
            return false;
        }
        if (specification.getTarget() != 0 && getTarget() != 0 && getTarget() != specification.getTarget()) {
            return false;
        }
        if (specification.getBytesPerSample() != 1 && getBytesPerSample() != 1 && getBytesPerSample() != specification.getBytesPerSample()) {
            return false;
        }
        if (specification.getDimensionCount() > 0 && getDimensionCount() > 0 && getDimensionCount() != specification.getDimensionCount()) {
            return false;
        }
        for (int i = 0; i < specification.getDimensionCount(); i++) {
            int specDim = specification.getDimension(i);
            if (specDim != 0 && getDimension(i) != 0 && getDimension(i) != specDim) {
                return false;
            }
        }
        if (specification.getObjectClass() != null && getObjectClass() != null && !specification.getObjectClass().isAssignableFrom(getObjectClass())) {
            return false;
        }
        if (specification.mMetaData != null && this.mMetaData != null) {
            for (String specKey : specification.mMetaData.keySet()) {
                if (this.mMetaData.containsKey(specKey) && !this.mMetaData.get(specKey).equals(specification.mMetaData.get(specKey))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static int bytesPerSampleOf(int baseType) {
        switch (baseType) {
            case 1:
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
            case 5:
            case 7:
                return 4;
            case 6:
                return 8;
            default:
                return 1;
        }
    }

    public static String dimensionsToString(int[] dimensions) {
        StringBuffer buffer = new StringBuffer();
        if (dimensions != null) {
            int n = dimensions.length;
            for (int i = 0; i < n; i++) {
                if (dimensions[i] == 0) {
                    buffer.append("[]");
                } else {
                    buffer.append("[" + String.valueOf(dimensions[i]) + "]");
                }
            }
        }
        return buffer.toString();
    }

    public static String baseTypeToString(int baseType) {
        switch (baseType) {
            case 0:
                return "unspecified";
            case 1:
                return "bit";
            case 2:
                return "byte";
            case 3:
                return "int";
            case 4:
                return "int";
            case 5:
                return "float";
            case 6:
                return "double";
            case 7:
                return "pointer";
            case 8:
                return "object";
            default:
                return "unknown";
        }
    }

    public static String targetToString(int target) {
        switch (target) {
            case 0:
                return "unspecified";
            case 1:
                return "simple";
            case 2:
                return "native";
            case 3:
                return "gpu";
            case 4:
                return "vbo";
            case 5:
                return "renderscript";
            default:
                return "unknown";
        }
    }

    public static String metaDataToString(KeyValueMap metaData) {
        if (metaData == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("{ ");
        for (Map.Entry<String, Object> entry : metaData.entrySet()) {
            buffer.append(entry.getKey() + ": " + entry.getValue() + Separators.SP);
        }
        buffer.append("}");
        return buffer.toString();
    }

    public static int readTargetString(String targetString) {
        if (targetString.equalsIgnoreCase("CPU") || targetString.equalsIgnoreCase("NATIVE")) {
            return 2;
        }
        if (targetString.equalsIgnoreCase("GPU")) {
            return 3;
        }
        if (targetString.equalsIgnoreCase("SIMPLE")) {
            return 1;
        }
        if (targetString.equalsIgnoreCase("VERTEXBUFFER")) {
            return 4;
        }
        if (targetString.equalsIgnoreCase("UNSPECIFIED")) {
            return 0;
        }
        throw new RuntimeException("Unknown target type '" + targetString + "'!");
    }

    public String toString() {
        int valuesPerSample = getValuesPerSample();
        String sampleCountString = valuesPerSample == 1 ? "" : String.valueOf(valuesPerSample);
        String targetString = this.mTarget == 0 ? "" : targetToString(this.mTarget) + Separators.SP;
        String classString = this.mObjectClass == null ? "" : " class(" + this.mObjectClass.getSimpleName() + ") ";
        return targetString + baseTypeToString(this.mBaseType) + sampleCountString + dimensionsToString(this.mDimensions) + classString + metaDataToString(this.mMetaData);
    }

    private void initDefaults() {
        this.mBytesPerSample = bytesPerSampleOf(this.mBaseType);
    }

    int calcSize(int[] dimensions) {
        if (dimensions != null && dimensions.length > 0) {
            int size = getBytesPerSample();
            for (int dim : dimensions) {
                size *= dim;
            }
            return size;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isReplaceableBy(FrameFormat format) {
        return this.mTarget == format.mTarget && getSize() == format.getSize() && Arrays.equals(format.mDimensions, this.mDimensions);
    }
}