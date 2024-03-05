package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import gov.nist.core.Separators;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

/* loaded from: LinearSystem.class */
public class LinearSystem {
    private static final boolean DEBUG = false;
    public static final boolean FULL_DEBUG = false;
    private static int POOL_SIZE = 1000;
    public static Metrics sMetrics;
    public boolean graphOptimizer;
    private boolean[] mAlreadyTestedCandidates;
    final Cache mCache;
    private Row mGoal;
    private int mMaxColumns;
    private int mMaxRows;
    int mNumColumns;
    int mNumRows;
    private SolverVariable[] mPoolVariables;
    private int mPoolVariablesCount;
    ArrayRow[] mRows;
    private final Row mTempGoal;
    private ArrayRow[] tempClientsCopy;
    int mVariablesID = 0;
    private HashMap<String, SolverVariable> mVariables = null;
    private int TABLE_SIZE = 32;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LinearSystem$Row.class */
    public interface Row {
        void addError(SolverVariable solverVariable);

        void clear();

        SolverVariable getKey();

        SolverVariable getPivotCandidate(LinearSystem linearSystem, boolean[] zArr);

        void initFromRow(Row row);

        boolean isEmpty();
    }

    public LinearSystem() {
        int i = this.TABLE_SIZE;
        this.mMaxColumns = i;
        this.mRows = null;
        this.graphOptimizer = false;
        this.mAlreadyTestedCandidates = new boolean[i];
        this.mNumColumns = 1;
        this.mNumRows = 0;
        this.mMaxRows = i;
        this.mPoolVariables = new SolverVariable[POOL_SIZE];
        this.mPoolVariablesCount = 0;
        this.tempClientsCopy = new ArrayRow[i];
        this.mRows = new ArrayRow[i];
        releaseRows();
        this.mCache = new Cache();
        this.mGoal = new GoalRow(this.mCache);
        this.mTempGoal = new ArrayRow(this.mCache);
    }

    private SolverVariable acquireSolverVariable(SolverVariable.Type type, String str) {
        SolverVariable solverVariable;
        SolverVariable acquire = this.mCache.solverVariablePool.acquire();
        if (acquire == null) {
            SolverVariable solverVariable2 = new SolverVariable(type, str);
            solverVariable2.setType(type, str);
            solverVariable = solverVariable2;
        } else {
            acquire.reset();
            acquire.setType(type, str);
            solverVariable = acquire;
        }
        int i = this.mPoolVariablesCount;
        int i2 = POOL_SIZE;
        if (i >= i2) {
            POOL_SIZE = i2 * 2;
            this.mPoolVariables = (SolverVariable[]) Arrays.copyOf(this.mPoolVariables, POOL_SIZE);
        }
        SolverVariable[] solverVariableArr = this.mPoolVariables;
        int i3 = this.mPoolVariablesCount;
        this.mPoolVariablesCount = i3 + 1;
        solverVariableArr[i3] = solverVariable;
        return solverVariable;
    }

    private void addError(ArrayRow arrayRow) {
        arrayRow.addError(this, 0);
    }

    private final void addRow(ArrayRow arrayRow) {
        if (this.mRows[this.mNumRows] != null) {
            this.mCache.arrayRowPool.release(this.mRows[this.mNumRows]);
        }
        this.mRows[this.mNumRows] = arrayRow;
        SolverVariable solverVariable = arrayRow.variable;
        int i = this.mNumRows;
        solverVariable.definitionId = i;
        this.mNumRows = i + 1;
        arrayRow.variable.updateReferencesWithNewDefinition(arrayRow);
    }

    private void addSingleError(ArrayRow arrayRow, int i) {
        addSingleError(arrayRow, i, 0);
    }

    private void computeValues() {
        for (int i = 0; i < this.mNumRows; i++) {
            ArrayRow arrayRow = this.mRows[i];
            arrayRow.variable.computedValue = arrayRow.constantValue;
        }
    }

    public static ArrayRow createRowCentering(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, float f, SolverVariable solverVariable3, SolverVariable solverVariable4, int i2, boolean z) {
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowCentering(solverVariable, solverVariable2, i, f, solverVariable3, solverVariable4, i2);
        if (z) {
            createRow.addError(linearSystem, 4);
        }
        return createRow;
    }

    public static ArrayRow createRowDimensionPercent(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, float f, boolean z) {
        ArrayRow createRow = linearSystem.createRow();
        if (z) {
            linearSystem.addError(createRow);
        }
        return createRow.createRowDimensionPercent(solverVariable, solverVariable2, solverVariable3, f);
    }

    public static ArrayRow createRowEquals(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, boolean z) {
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowEquals(solverVariable, solverVariable2, i);
        if (z) {
            linearSystem.addSingleError(createRow, 1);
        }
        return createRow;
    }

    public static ArrayRow createRowGreaterThan(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, boolean z) {
        SolverVariable createSlackVariable = linearSystem.createSlackVariable();
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowGreaterThan(solverVariable, solverVariable2, createSlackVariable, i);
        if (z) {
            linearSystem.addSingleError(createRow, (int) ((-1.0f) * createRow.variables.get(createSlackVariable)));
        }
        return createRow;
    }

    public static ArrayRow createRowLowerThan(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, boolean z) {
        SolverVariable createSlackVariable = linearSystem.createSlackVariable();
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowLowerThan(solverVariable, solverVariable2, createSlackVariable, i);
        if (z) {
            linearSystem.addSingleError(createRow, (int) ((-1.0f) * createRow.variables.get(createSlackVariable)));
        }
        return createRow;
    }

    private SolverVariable createVariable(String str, SolverVariable.Type type) {
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.variables++;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(type, null);
        acquireSolverVariable.setName(str);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        if (this.mVariables == null) {
            this.mVariables = new HashMap<>();
        }
        this.mVariables.put(str, acquireSolverVariable);
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        return acquireSolverVariable;
    }

    private void displayRows() {
        displaySolverVariables();
        String str = "";
        for (int i = 0; i < this.mNumRows; i++) {
            str = (str + this.mRows[i]) + Separators.RETURN;
        }
        System.out.println(str + this.mGoal + Separators.RETURN);
    }

    private void displaySolverVariables() {
        System.out.println("Display Rows (" + this.mNumRows + "x" + this.mNumColumns + ")\n");
    }

    private int enforceBFS(Row row) throws Exception {
        boolean z;
        int i;
        float f;
        int i2;
        int i3;
        int i4;
        int i5 = 0;
        while (true) {
            z = false;
            if (i5 >= this.mNumRows) {
                break;
            } else if (this.mRows[i5].variable.mType != SolverVariable.Type.UNRESTRICTED && this.mRows[i5].constantValue < 0.0f) {
                z = true;
                break;
            } else {
                i5++;
            }
        }
        if (z) {
            boolean z2 = false;
            int i6 = 0;
            while (true) {
                i = i6;
                if (z2) {
                    break;
                }
                Metrics metrics = sMetrics;
                if (metrics != null) {
                    metrics.bfs++;
                }
                int i7 = i + 1;
                float f2 = Float.MAX_VALUE;
                int i8 = 0;
                int i9 = -1;
                int i10 = -1;
                for (int i11 = 0; i11 < this.mNumRows; i11++) {
                    ArrayRow arrayRow = this.mRows[i11];
                    if (arrayRow.variable.mType != SolverVariable.Type.UNRESTRICTED && !arrayRow.isSimpleDefinition && arrayRow.constantValue < 0.0f) {
                        int i12 = 1;
                        while (i12 < this.mNumColumns) {
                            SolverVariable solverVariable = this.mCache.mIndexedVariables[i12];
                            float f3 = arrayRow.variables.get(solverVariable);
                            if (f3 <= 0.0f) {
                                f = f2;
                                i2 = i8;
                                i3 = i9;
                                i4 = i10;
                            } else {
                                int i13 = i10;
                                int i14 = 0;
                                while (true) {
                                    f = f2;
                                    i2 = i8;
                                    i3 = i9;
                                    i4 = i13;
                                    if (i14 < 7) {
                                        float f4 = solverVariable.strengthVector[i14] / f3;
                                        if ((f4 < f2 && i14 == i8) || i14 > i8) {
                                            f2 = f4;
                                            i9 = i11;
                                            i13 = i12;
                                            i8 = i14;
                                        }
                                        i14++;
                                    }
                                }
                            }
                            i12++;
                            f2 = f;
                            i8 = i2;
                            i9 = i3;
                            i10 = i4;
                        }
                    }
                }
                if (i9 != -1) {
                    ArrayRow arrayRow2 = this.mRows[i9];
                    arrayRow2.variable.definitionId = -1;
                    Metrics metrics2 = sMetrics;
                    if (metrics2 != null) {
                        metrics2.pivots++;
                    }
                    arrayRow2.pivot(this.mCache.mIndexedVariables[i10]);
                    arrayRow2.variable.definitionId = i9;
                    arrayRow2.variable.updateReferencesWithNewDefinition(arrayRow2);
                } else {
                    z2 = true;
                }
                if (i7 > this.mNumColumns / 2) {
                    z2 = true;
                }
                i6 = i7;
            }
        } else {
            i = 0;
        }
        return i;
    }

    private String getDisplaySize(int i) {
        int i2 = ((i * 4) / 1024) / 1024;
        if (i2 > 0) {
            return "" + i2 + " Mb";
        }
        int i3 = (i * 4) / 1024;
        if (i3 > 0) {
            return "" + i3 + " Kb";
        }
        return "" + (i * 4) + " bytes";
    }

    private String getDisplayStrength(int i) {
        return i == 1 ? "LOW" : i == 2 ? "MEDIUM" : i == 3 ? "HIGH" : i == 4 ? "HIGHEST" : i == 5 ? "EQUALITY" : i == 6 ? "FIXED" : "NONE";
    }

    public static Metrics getMetrics() {
        return sMetrics;
    }

    private void increaseTableSize() {
        this.TABLE_SIZE *= 2;
        this.mRows = (ArrayRow[]) Arrays.copyOf(this.mRows, this.TABLE_SIZE);
        Cache cache = this.mCache;
        cache.mIndexedVariables = (SolverVariable[]) Arrays.copyOf(cache.mIndexedVariables, this.TABLE_SIZE);
        int i = this.TABLE_SIZE;
        this.mAlreadyTestedCandidates = new boolean[i];
        this.mMaxColumns = i;
        this.mMaxRows = i;
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.tableSizeIncrease++;
            Metrics metrics2 = sMetrics;
            metrics2.maxTableSize = Math.max(metrics2.maxTableSize, this.TABLE_SIZE);
            Metrics metrics3 = sMetrics;
            metrics3.lastTableSize = metrics3.maxTableSize;
        }
    }

    private final int optimize(Row row, boolean z) {
        boolean z2;
        int i;
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.optimize++;
        }
        int i2 = 0;
        while (true) {
            z2 = false;
            i = 0;
            if (i2 >= this.mNumColumns) {
                break;
            }
            this.mAlreadyTestedCandidates[i2] = false;
            i2++;
        }
        while (!z2) {
            Metrics metrics2 = sMetrics;
            if (metrics2 != null) {
                metrics2.iterations++;
            }
            int i3 = i + 1;
            if (i3 >= this.mNumColumns * 2) {
                return i3;
            }
            if (row.getKey() != null) {
                this.mAlreadyTestedCandidates[row.getKey().id] = true;
            }
            SolverVariable pivotCandidate = row.getPivotCandidate(this, this.mAlreadyTestedCandidates);
            if (pivotCandidate != null) {
                if (this.mAlreadyTestedCandidates[pivotCandidate.id]) {
                    return i3;
                }
                this.mAlreadyTestedCandidates[pivotCandidate.id] = true;
            }
            if (pivotCandidate != null) {
                float f = Float.MAX_VALUE;
                int i4 = -1;
                for (int i5 = 0; i5 < this.mNumRows; i5++) {
                    ArrayRow arrayRow = this.mRows[i5];
                    if (arrayRow.variable.mType != SolverVariable.Type.UNRESTRICTED && !arrayRow.isSimpleDefinition && arrayRow.hasVariable(pivotCandidate)) {
                        float f2 = arrayRow.variables.get(pivotCandidate);
                        if (f2 < 0.0f) {
                            float f3 = (-arrayRow.constantValue) / f2;
                            if (f3 < f) {
                                f = f3;
                                i4 = i5;
                            }
                        }
                    }
                }
                if (i4 > -1) {
                    ArrayRow arrayRow2 = this.mRows[i4];
                    arrayRow2.variable.definitionId = -1;
                    Metrics metrics3 = sMetrics;
                    if (metrics3 != null) {
                        metrics3.pivots++;
                    }
                    arrayRow2.pivot(pivotCandidate);
                    arrayRow2.variable.definitionId = i4;
                    arrayRow2.variable.updateReferencesWithNewDefinition(arrayRow2);
                } else {
                    z2 = true;
                }
            } else {
                z2 = true;
            }
            i = i3;
        }
        return i;
    }

    private void releaseRows() {
        int i = 0;
        while (true) {
            ArrayRow[] arrayRowArr = this.mRows;
            if (i >= arrayRowArr.length) {
                return;
            }
            ArrayRow arrayRow = arrayRowArr[i];
            if (arrayRow != null) {
                this.mCache.arrayRowPool.release(arrayRow);
            }
            this.mRows[i] = null;
            i++;
        }
    }

    private final void updateRowFromVariables(ArrayRow arrayRow) {
        if (this.mNumRows > 0) {
            arrayRow.variables.updateFromSystem(arrayRow, this.mRows);
            if (arrayRow.variables.currentSize == 0) {
                arrayRow.isSimpleDefinition = true;
            }
        }
    }

    public void addCenterPoint(ConstraintWidget constraintWidget, ConstraintWidget constraintWidget2, float f, int i) {
        SolverVariable createObjectVariable = createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT));
        SolverVariable createObjectVariable2 = createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.TOP));
        SolverVariable createObjectVariable3 = createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT));
        SolverVariable createObjectVariable4 = createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM));
        SolverVariable createObjectVariable5 = createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.LEFT));
        SolverVariable createObjectVariable6 = createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.TOP));
        SolverVariable createObjectVariable7 = createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.RIGHT));
        SolverVariable createObjectVariable8 = createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.BOTTOM));
        ArrayRow createRow = createRow();
        createRow.createRowWithAngle(createObjectVariable2, createObjectVariable4, createObjectVariable6, createObjectVariable8, (float) (Math.sin(f) * i));
        addConstraint(createRow);
        ArrayRow createRow2 = createRow();
        createRow2.createRowWithAngle(createObjectVariable, createObjectVariable3, createObjectVariable5, createObjectVariable7, (float) (Math.cos(f) * i));
        addConstraint(createRow2);
    }

    public void addCentering(SolverVariable solverVariable, SolverVariable solverVariable2, int i, float f, SolverVariable solverVariable3, SolverVariable solverVariable4, int i2, int i3) {
        ArrayRow createRow = createRow();
        createRow.createRowCentering(solverVariable, solverVariable2, i, f, solverVariable3, solverVariable4, i2);
        if (i3 != 6) {
            createRow.addError(this, i3);
        }
        addConstraint(createRow);
    }

    public void addConstraint(ArrayRow arrayRow) {
        SolverVariable pickPivot;
        if (arrayRow == null) {
            return;
        }
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.constraints++;
            if (arrayRow.isSimpleDefinition) {
                sMetrics.simpleconstraints++;
            }
        }
        if (this.mNumRows + 1 >= this.mMaxRows || this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        boolean z = false;
        if (arrayRow.isSimpleDefinition) {
            z = false;
        } else {
            updateRowFromVariables(arrayRow);
            if (arrayRow.isEmpty()) {
                return;
            }
            arrayRow.ensurePositiveConstant();
            if (arrayRow.chooseSubject(this)) {
                SolverVariable createExtraVariable = createExtraVariable();
                arrayRow.variable = createExtraVariable;
                addRow(arrayRow);
                z = true;
                this.mTempGoal.initFromRow(arrayRow);
                optimize(this.mTempGoal, true);
                if (createExtraVariable.definitionId == -1) {
                    if (arrayRow.variable == createExtraVariable && (pickPivot = arrayRow.pickPivot(createExtraVariable)) != null) {
                        Metrics metrics2 = sMetrics;
                        if (metrics2 != null) {
                            metrics2.pivots++;
                        }
                        arrayRow.pivot(pickPivot);
                    }
                    if (!arrayRow.isSimpleDefinition) {
                        arrayRow.variable.updateReferencesWithNewDefinition(arrayRow);
                    }
                    this.mNumRows--;
                }
            }
            if (!arrayRow.hasKeyVariable()) {
                return;
            }
        }
        if (z) {
            return;
        }
        addRow(arrayRow);
    }

    public ArrayRow addEquality(SolverVariable solverVariable, SolverVariable solverVariable2, int i, int i2) {
        ArrayRow createRow = createRow();
        createRow.createRowEquals(solverVariable, solverVariable2, i);
        if (i2 != 6) {
            createRow.addError(this, i2);
        }
        addConstraint(createRow);
        return createRow;
    }

    public void addEquality(SolverVariable solverVariable, int i) {
        int i2 = solverVariable.definitionId;
        if (solverVariable.definitionId == -1) {
            ArrayRow createRow = createRow();
            createRow.createRowDefinition(solverVariable, i);
            addConstraint(createRow);
            return;
        }
        ArrayRow arrayRow = this.mRows[i2];
        if (arrayRow.isSimpleDefinition) {
            arrayRow.constantValue = i;
        } else if (arrayRow.variables.currentSize == 0) {
            arrayRow.isSimpleDefinition = true;
            arrayRow.constantValue = i;
        } else {
            ArrayRow createRow2 = createRow();
            createRow2.createRowEquals(solverVariable, i);
            addConstraint(createRow2);
        }
    }

    public void addEquality(SolverVariable solverVariable, int i, int i2) {
        int i3 = solverVariable.definitionId;
        if (solverVariable.definitionId == -1) {
            ArrayRow createRow = createRow();
            createRow.createRowDefinition(solverVariable, i);
            createRow.addError(this, i2);
            addConstraint(createRow);
            return;
        }
        ArrayRow arrayRow = this.mRows[i3];
        if (arrayRow.isSimpleDefinition) {
            arrayRow.constantValue = i;
            return;
        }
        ArrayRow createRow2 = createRow();
        createRow2.createRowEquals(solverVariable, i);
        createRow2.addError(this, i2);
        addConstraint(createRow2);
    }

    public void addGreaterBarrier(SolverVariable solverVariable, SolverVariable solverVariable2, boolean z) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = 0;
        createRow.createRowGreaterThan(solverVariable, solverVariable2, createSlackVariable, 0);
        if (z) {
            addSingleError(createRow, (int) ((-1.0f) * createRow.variables.get(createSlackVariable)), 1);
        }
        addConstraint(createRow);
    }

    public void addGreaterThan(SolverVariable solverVariable, int i) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = 0;
        createRow.createRowGreaterThan(solverVariable, i, createSlackVariable);
        addConstraint(createRow);
    }

    public void addGreaterThan(SolverVariable solverVariable, SolverVariable solverVariable2, int i, int i2) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = 0;
        createRow.createRowGreaterThan(solverVariable, solverVariable2, createSlackVariable, i);
        if (i2 != 6) {
            addSingleError(createRow, (int) ((-1.0f) * createRow.variables.get(createSlackVariable)), i2);
        }
        addConstraint(createRow);
    }

    public void addLowerBarrier(SolverVariable solverVariable, SolverVariable solverVariable2, boolean z) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = 0;
        createRow.createRowLowerThan(solverVariable, solverVariable2, createSlackVariable, 0);
        if (z) {
            addSingleError(createRow, (int) ((-1.0f) * createRow.variables.get(createSlackVariable)), 1);
        }
        addConstraint(createRow);
    }

    public void addLowerThan(SolverVariable solverVariable, SolverVariable solverVariable2, int i, int i2) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = 0;
        createRow.createRowLowerThan(solverVariable, solverVariable2, createSlackVariable, i);
        if (i2 != 6) {
            addSingleError(createRow, (int) ((-1.0f) * createRow.variables.get(createSlackVariable)), i2);
        }
        addConstraint(createRow);
    }

    public void addRatio(SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, SolverVariable solverVariable4, float f, int i) {
        ArrayRow createRow = createRow();
        createRow.createRowDimensionRatio(solverVariable, solverVariable2, solverVariable3, solverVariable4, f);
        if (i != 6) {
            createRow.addError(this, i);
        }
        addConstraint(createRow);
    }

    void addSingleError(ArrayRow arrayRow, int i, int i2) {
        arrayRow.addSingleError(createErrorVariable(i2, null), i);
    }

    public SolverVariable createErrorVariable(int i, String str) {
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.errors++;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(SolverVariable.Type.ERROR, str);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        acquireSolverVariable.strength = i;
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        this.mGoal.addError(acquireSolverVariable);
        return acquireSolverVariable;
    }

    public SolverVariable createExtraVariable() {
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.extravariables++;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(SolverVariable.Type.SLACK, null);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        return acquireSolverVariable;
    }

    public SolverVariable createObjectVariable(Object obj) {
        SolverVariable solverVariable;
        if (obj == null) {
            return null;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        if (obj instanceof ConstraintAnchor) {
            SolverVariable solverVariable2 = ((ConstraintAnchor) obj).getSolverVariable();
            if (solverVariable2 == null) {
                ((ConstraintAnchor) obj).resetSolverVariable(this.mCache);
                solverVariable = ((ConstraintAnchor) obj).getSolverVariable();
            } else {
                solverVariable = solverVariable2;
            }
            if (solverVariable.id == -1 || solverVariable.id > this.mVariablesID || this.mCache.mIndexedVariables[solverVariable.id] == null) {
                if (solverVariable.id != -1) {
                    solverVariable.reset();
                }
                this.mVariablesID++;
                this.mNumColumns++;
                solverVariable.id = this.mVariablesID;
                solverVariable.mType = SolverVariable.Type.UNRESTRICTED;
                this.mCache.mIndexedVariables[this.mVariablesID] = solverVariable;
            }
        } else {
            solverVariable = null;
        }
        return solverVariable;
    }

    public ArrayRow createRow() {
        ArrayRow acquire = this.mCache.arrayRowPool.acquire();
        if (acquire == null) {
            acquire = new ArrayRow(this.mCache);
        } else {
            acquire.reset();
        }
        SolverVariable.increaseErrorId();
        return acquire;
    }

    public SolverVariable createSlackVariable() {
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.slackvariables++;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(SolverVariable.Type.SLACK, null);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        return acquireSolverVariable;
    }

    void displayReadableRows() {
        displaySolverVariables();
        String str = " #  ";
        for (int i = 0; i < this.mNumRows; i++) {
            str = (str + this.mRows[i].toReadableString()) + "\n #  ";
        }
        if (this.mGoal != null) {
            str = str + this.mGoal + Separators.RETURN;
        }
        System.out.println(str);
    }

    void displaySystemInformations() {
        int i = 0;
        for (int i2 = 0; i2 < this.TABLE_SIZE; i2++) {
            ArrayRow[] arrayRowArr = this.mRows;
            if (arrayRowArr[i2] != null) {
                i += arrayRowArr[i2].sizeInBytes();
            }
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.mNumRows; i4++) {
            ArrayRow[] arrayRowArr2 = this.mRows;
            if (arrayRowArr2[i4] != null) {
                i3 += arrayRowArr2[i4].sizeInBytes();
            }
        }
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("Linear System -> Table size: ");
        sb.append(this.TABLE_SIZE);
        sb.append(" (");
        int i5 = this.TABLE_SIZE;
        sb.append(getDisplaySize(i5 * i5));
        sb.append(") -- row sizes: ");
        sb.append(getDisplaySize(i));
        sb.append(", actual size: ");
        sb.append(getDisplaySize(i3));
        sb.append(" rows: ");
        sb.append(this.mNumRows);
        sb.append(Separators.SLASH);
        sb.append(this.mMaxRows);
        sb.append(" cols: ");
        sb.append(this.mNumColumns);
        sb.append(Separators.SLASH);
        sb.append(this.mMaxColumns);
        sb.append(Separators.SP);
        sb.append(0);
        sb.append(" occupied cells, ");
        sb.append(getDisplaySize(0));
        printStream.println(sb.toString());
    }

    public void displayVariablesReadableRows() {
        displaySolverVariables();
        String str = "";
        for (int i = 0; i < this.mNumRows; i++) {
            if (this.mRows[i].variable.mType == SolverVariable.Type.UNRESTRICTED) {
                str = (str + this.mRows[i].toReadableString()) + Separators.RETURN;
            }
        }
        System.out.println(str + this.mGoal + Separators.RETURN);
    }

    public void fillMetrics(Metrics metrics) {
        sMetrics = metrics;
    }

    public Cache getCache() {
        return this.mCache;
    }

    Row getGoal() {
        return this.mGoal;
    }

    public int getMemoryUsed() {
        int i = 0;
        for (int i2 = 0; i2 < this.mNumRows; i2++) {
            ArrayRow[] arrayRowArr = this.mRows;
            if (arrayRowArr[i2] != null) {
                i += arrayRowArr[i2].sizeInBytes();
            }
        }
        return i;
    }

    public int getNumEquations() {
        return this.mNumRows;
    }

    public int getNumVariables() {
        return this.mVariablesID;
    }

    public int getObjectVariableValue(Object obj) {
        SolverVariable solverVariable = ((ConstraintAnchor) obj).getSolverVariable();
        if (solverVariable != null) {
            return (int) (solverVariable.computedValue + 0.5f);
        }
        return 0;
    }

    ArrayRow getRow(int i) {
        return this.mRows[i];
    }

    float getValueFor(String str) {
        SolverVariable variable = getVariable(str, SolverVariable.Type.UNRESTRICTED);
        if (variable == null) {
            return 0.0f;
        }
        return variable.computedValue;
    }

    SolverVariable getVariable(String str, SolverVariable.Type type) {
        if (this.mVariables == null) {
            this.mVariables = new HashMap<>();
        }
        SolverVariable solverVariable = this.mVariables.get(str);
        return solverVariable == null ? createVariable(str, type) : solverVariable;
    }

    public void minimize() throws Exception {
        boolean z;
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.minimize++;
        }
        if (!this.graphOptimizer) {
            minimizeGoal(this.mGoal);
            return;
        }
        Metrics metrics2 = sMetrics;
        if (metrics2 != null) {
            metrics2.graphOptimizer++;
        }
        int i = 0;
        while (true) {
            z = true;
            if (i >= this.mNumRows) {
                break;
            } else if (!this.mRows[i].isSimpleDefinition) {
                z = false;
                break;
            } else {
                i++;
            }
        }
        if (!z) {
            minimizeGoal(this.mGoal);
            return;
        }
        Metrics metrics3 = sMetrics;
        if (metrics3 != null) {
            metrics3.fullySolved++;
        }
        computeValues();
    }

    void minimizeGoal(Row row) throws Exception {
        Metrics metrics = sMetrics;
        if (metrics != null) {
            metrics.minimizeGoal++;
            Metrics metrics2 = sMetrics;
            metrics2.maxVariables = Math.max(metrics2.maxVariables, this.mNumColumns);
            Metrics metrics3 = sMetrics;
            metrics3.maxRows = Math.max(metrics3.maxRows, this.mNumRows);
        }
        updateRowFromVariables((ArrayRow) row);
        enforceBFS(row);
        optimize(row, false);
        computeValues();
    }

    public void reset() {
        for (int i = 0; i < this.mCache.mIndexedVariables.length; i++) {
            SolverVariable solverVariable = this.mCache.mIndexedVariables[i];
            if (solverVariable != null) {
                solverVariable.reset();
            }
        }
        this.mCache.solverVariablePool.releaseAll(this.mPoolVariables, this.mPoolVariablesCount);
        this.mPoolVariablesCount = 0;
        Arrays.fill(this.mCache.mIndexedVariables, (Object) null);
        HashMap<String, SolverVariable> hashMap = this.mVariables;
        if (hashMap != null) {
            hashMap.clear();
        }
        this.mVariablesID = 0;
        this.mGoal.clear();
        this.mNumColumns = 1;
        for (int i2 = 0; i2 < this.mNumRows; i2++) {
            this.mRows[i2].used = false;
        }
        releaseRows();
        this.mNumRows = 0;
    }
}