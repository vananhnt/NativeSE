package android.support.constraint.solver;

import gov.nist.javax.sip.parser.TokenNames;
import java.util.Arrays;

/* loaded from: SolverVariable.class */
public class SolverVariable {
    private static final boolean INTERNAL_DEBUG = false;
    static final int MAX_STRENGTH = 7;
    public static final int STRENGTH_BARRIER = 7;
    public static final int STRENGTH_EQUALITY = 5;
    public static final int STRENGTH_FIXED = 6;
    public static final int STRENGTH_HIGH = 3;
    public static final int STRENGTH_HIGHEST = 4;
    public static final int STRENGTH_LOW = 1;
    public static final int STRENGTH_MEDIUM = 2;
    public static final int STRENGTH_NONE = 0;
    public float computedValue;
    int definitionId;
    public int id;
    ArrayRow[] mClientEquations;
    int mClientEquationsCount;
    private String mName;
    Type mType;
    public int strength;
    float[] strengthVector;
    public int usageInRowCount;
    private static int uniqueSlackId = 1;
    private static int uniqueErrorId = 1;
    private static int uniqueUnrestrictedId = 1;
    private static int uniqueConstantId = 1;
    private static int uniqueId = 1;

    /* renamed from: android.support.constraint.solver.SolverVariable$1  reason: invalid class name */
    /* loaded from: SolverVariable$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final int[] $SwitchMap$android$support$constraint$solver$SolverVariable$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.UNRESTRICTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.CONSTANT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.SLACK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.UNKNOWN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    /* loaded from: SolverVariable$Type.class */
    public enum Type {
        UNRESTRICTED,
        CONSTANT,
        SLACK,
        ERROR,
        UNKNOWN
    }

    public SolverVariable(Type type, String str) {
        this.id = -1;
        this.definitionId = -1;
        this.strength = 0;
        this.strengthVector = new float[7];
        this.mClientEquations = new ArrayRow[8];
        this.mClientEquationsCount = 0;
        this.usageInRowCount = 0;
        this.mType = type;
    }

    public SolverVariable(String str, Type type) {
        this.id = -1;
        this.definitionId = -1;
        this.strength = 0;
        this.strengthVector = new float[7];
        this.mClientEquations = new ArrayRow[8];
        this.mClientEquationsCount = 0;
        this.usageInRowCount = 0;
        this.mName = str;
        this.mType = type;
    }

    private static String getUniqueName(Type type, String str) {
        if (str != null) {
            return str + uniqueErrorId;
        }
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$SolverVariable$Type[type.ordinal()]) {
            case 1:
                StringBuilder sb = new StringBuilder();
                sb.append(TokenNames.U);
                int i = uniqueUnrestrictedId + 1;
                uniqueUnrestrictedId = i;
                sb.append(i);
                return sb.toString();
            case 2:
                StringBuilder sb2 = new StringBuilder();
                sb2.append(TokenNames.C);
                int i2 = uniqueConstantId + 1;
                uniqueConstantId = i2;
                sb2.append(i2);
                return sb2.toString();
            case 3:
                StringBuilder sb3 = new StringBuilder();
                sb3.append(TokenNames.S);
                int i3 = uniqueSlackId + 1;
                uniqueSlackId = i3;
                sb3.append(i3);
                return sb3.toString();
            case 4:
                StringBuilder sb4 = new StringBuilder();
                sb4.append("e");
                int i4 = uniqueErrorId + 1;
                uniqueErrorId = i4;
                sb4.append(i4);
                return sb4.toString();
            case 5:
                StringBuilder sb5 = new StringBuilder();
                sb5.append(TokenNames.V);
                int i5 = uniqueId + 1;
                uniqueId = i5;
                sb5.append(i5);
                return sb5.toString();
            default:
                throw new AssertionError(type.name());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void increaseErrorId() {
        uniqueErrorId++;
    }

    public final void addToRow(ArrayRow arrayRow) {
        int i = 0;
        while (true) {
            int i2 = this.mClientEquationsCount;
            if (i >= i2) {
                ArrayRow[] arrayRowArr = this.mClientEquations;
                if (i2 >= arrayRowArr.length) {
                    this.mClientEquations = (ArrayRow[]) Arrays.copyOf(arrayRowArr, arrayRowArr.length * 2);
                }
                ArrayRow[] arrayRowArr2 = this.mClientEquations;
                int i3 = this.mClientEquationsCount;
                arrayRowArr2[i3] = arrayRow;
                this.mClientEquationsCount = i3 + 1;
                return;
            } else if (this.mClientEquations[i] == arrayRow) {
                return;
            } else {
                i++;
            }
        }
    }

    void clearStrengths() {
        for (int i = 0; i < 7; i++) {
            this.strengthVector[i] = 0.0f;
        }
    }

    public String getName() {
        return this.mName;
    }

    public final void removeFromRow(ArrayRow arrayRow) {
        int i = this.mClientEquationsCount;
        for (int i2 = 0; i2 < i; i2++) {
            if (this.mClientEquations[i2] == arrayRow) {
                for (int i3 = 0; i3 < (i - i2) - 1; i3++) {
                    ArrayRow[] arrayRowArr = this.mClientEquations;
                    arrayRowArr[i2 + i3] = arrayRowArr[i2 + i3 + 1];
                }
                this.mClientEquationsCount--;
                return;
            }
        }
    }

    public void reset() {
        this.mName = null;
        this.mType = Type.UNKNOWN;
        this.strength = 0;
        this.id = -1;
        this.definitionId = -1;
        this.computedValue = 0.0f;
        this.mClientEquationsCount = 0;
        this.usageInRowCount = 0;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setType(Type type, String str) {
        this.mType = type;
    }

    String strengthsToString() {
        String str = this + "[";
        boolean z = false;
        boolean z2 = true;
        for (int i = 0; i < this.strengthVector.length; i++) {
            String str2 = str + this.strengthVector[i];
            float[] fArr = this.strengthVector;
            if (fArr[i] > 0.0f) {
                z = false;
            } else if (fArr[i] < 0.0f) {
                z = true;
            }
            if (this.strengthVector[i] != 0.0f) {
                z2 = false;
            }
            str = i < this.strengthVector.length - 1 ? str2 + ", " : str2 + "] ";
        }
        if (z) {
            str = str + " (-)";
        }
        if (z2) {
            str = str + " (*)";
        }
        return str;
    }

    public String toString() {
        return "" + this.mName;
    }

    public final void updateReferencesWithNewDefinition(ArrayRow arrayRow) {
        int i = this.mClientEquationsCount;
        for (int i2 = 0; i2 < i; i2++) {
            this.mClientEquations[i2].variables.updateFromRow(this.mClientEquations[i2], arrayRow, false);
        }
        this.mClientEquationsCount = 0;
    }
}