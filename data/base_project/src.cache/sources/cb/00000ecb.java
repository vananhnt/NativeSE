package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import gov.nist.javax.sip.parser.TokenNames;
import java.util.ArrayList;

/* loaded from: ConstraintTableLayout.class */
public class ConstraintTableLayout extends ConstraintWidgetContainer {
    public static final int ALIGN_CENTER = 0;
    private static final int ALIGN_FULL = 3;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private ArrayList<Guideline> mHorizontalGuidelines;
    private ArrayList<HorizontalSlice> mHorizontalSlices;
    private int mNumCols;
    private int mNumRows;
    private int mPadding;
    private boolean mVerticalGrowth;
    private ArrayList<Guideline> mVerticalGuidelines;
    private ArrayList<VerticalSlice> mVerticalSlices;
    private LinearSystem system;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConstraintTableLayout$HorizontalSlice.class */
    public class HorizontalSlice {
        ConstraintWidget bottom;
        int padding;
        final ConstraintTableLayout this$0;
        ConstraintWidget top;

        HorizontalSlice(ConstraintTableLayout constraintTableLayout) {
            this.this$0 = constraintTableLayout;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConstraintTableLayout$VerticalSlice.class */
    public class VerticalSlice {
        int alignment = 1;
        ConstraintWidget left;
        int padding;
        ConstraintWidget right;
        final ConstraintTableLayout this$0;

        VerticalSlice(ConstraintTableLayout constraintTableLayout) {
            this.this$0 = constraintTableLayout;
        }
    }

    public ConstraintTableLayout() {
        this.mVerticalGrowth = true;
        this.mNumCols = 0;
        this.mNumRows = 0;
        this.mPadding = 8;
        this.mVerticalSlices = new ArrayList<>();
        this.mHorizontalSlices = new ArrayList<>();
        this.mVerticalGuidelines = new ArrayList<>();
        this.mHorizontalGuidelines = new ArrayList<>();
        this.system = null;
    }

    public ConstraintTableLayout(int i, int i2) {
        super(i, i2);
        this.mVerticalGrowth = true;
        this.mNumCols = 0;
        this.mNumRows = 0;
        this.mPadding = 8;
        this.mVerticalSlices = new ArrayList<>();
        this.mHorizontalSlices = new ArrayList<>();
        this.mVerticalGuidelines = new ArrayList<>();
        this.mHorizontalGuidelines = new ArrayList<>();
        this.system = null;
    }

    public ConstraintTableLayout(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.mVerticalGrowth = true;
        this.mNumCols = 0;
        this.mNumRows = 0;
        this.mPadding = 8;
        this.mVerticalSlices = new ArrayList<>();
        this.mHorizontalSlices = new ArrayList<>();
        this.mVerticalGuidelines = new ArrayList<>();
        this.mHorizontalGuidelines = new ArrayList<>();
        this.system = null;
    }

    private void setChildrenConnections() {
        int size = this.mChildren.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i2);
            int containerItemSkip = i + constraintWidget.getContainerItemSkip();
            int i3 = this.mNumCols;
            HorizontalSlice horizontalSlice = this.mHorizontalSlices.get(containerItemSkip / i3);
            VerticalSlice verticalSlice = this.mVerticalSlices.get(containerItemSkip % i3);
            ConstraintWidget constraintWidget2 = verticalSlice.left;
            ConstraintWidget constraintWidget3 = verticalSlice.right;
            ConstraintWidget constraintWidget4 = horizontalSlice.top;
            ConstraintWidget constraintWidget5 = horizontalSlice.bottom;
            constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT).connect(constraintWidget2.getAnchor(ConstraintAnchor.Type.LEFT), this.mPadding);
            if (constraintWidget3 instanceof Guideline) {
                constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).connect(constraintWidget3.getAnchor(ConstraintAnchor.Type.LEFT), this.mPadding);
            } else {
                constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).connect(constraintWidget3.getAnchor(ConstraintAnchor.Type.RIGHT), this.mPadding);
            }
            switch (verticalSlice.alignment) {
                case 1:
                    constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT).setStrength(ConstraintAnchor.Strength.STRONG);
                    constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).setStrength(ConstraintAnchor.Strength.WEAK);
                    break;
                case 2:
                    constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT).setStrength(ConstraintAnchor.Strength.WEAK);
                    constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).setStrength(ConstraintAnchor.Strength.STRONG);
                    break;
                case 3:
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                    break;
            }
            constraintWidget.getAnchor(ConstraintAnchor.Type.TOP).connect(constraintWidget4.getAnchor(ConstraintAnchor.Type.TOP), this.mPadding);
            if (constraintWidget5 instanceof Guideline) {
                constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM).connect(constraintWidget5.getAnchor(ConstraintAnchor.Type.TOP), this.mPadding);
            } else {
                constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM).connect(constraintWidget5.getAnchor(ConstraintAnchor.Type.BOTTOM), this.mPadding);
            }
            i = containerItemSkip + 1;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v14, types: [android.support.constraint.solver.widgets.ConstraintWidget] */
    private void setHorizontalSlices() {
        this.mHorizontalSlices.clear();
        float f = 100.0f / this.mNumRows;
        float f2 = f;
        ConstraintTableLayout constraintTableLayout = this;
        for (int i = 0; i < this.mNumRows; i++) {
            HorizontalSlice horizontalSlice = new HorizontalSlice(this);
            horizontalSlice.top = constraintTableLayout;
            if (i < this.mNumRows - 1) {
                Guideline guideline = new Guideline();
                guideline.setOrientation(0);
                guideline.setParent(this);
                guideline.setGuidePercent((int) f2);
                f2 += f;
                horizontalSlice.bottom = guideline;
                this.mHorizontalGuidelines.add(guideline);
            } else {
                horizontalSlice.bottom = this;
            }
            constraintTableLayout = horizontalSlice.bottom;
            this.mHorizontalSlices.add(horizontalSlice);
        }
        updateDebugSolverNames();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v14, types: [android.support.constraint.solver.widgets.ConstraintWidget] */
    private void setVerticalSlices() {
        this.mVerticalSlices.clear();
        ConstraintTableLayout constraintTableLayout = this;
        float f = 100.0f / this.mNumCols;
        float f2 = f;
        for (int i = 0; i < this.mNumCols; i++) {
            VerticalSlice verticalSlice = new VerticalSlice(this);
            verticalSlice.left = constraintTableLayout;
            if (i < this.mNumCols - 1) {
                Guideline guideline = new Guideline();
                guideline.setOrientation(1);
                guideline.setParent(this);
                guideline.setGuidePercent((int) f2);
                f2 += f;
                verticalSlice.right = guideline;
                this.mVerticalGuidelines.add(guideline);
            } else {
                verticalSlice.right = this;
            }
            constraintTableLayout = verticalSlice.right;
            this.mVerticalSlices.add(verticalSlice);
        }
        updateDebugSolverNames();
    }

    private void updateDebugSolverNames() {
        if (this.system == null) {
            return;
        }
        int size = this.mVerticalGuidelines.size();
        for (int i = 0; i < size; i++) {
            this.mVerticalGuidelines.get(i).setDebugSolverName(this.system, getDebugName() + ".VG" + i);
        }
        int size2 = this.mHorizontalGuidelines.size();
        for (int i2 = 0; i2 < size2; i2++) {
            this.mHorizontalGuidelines.get(i2).setDebugSolverName(this.system, getDebugName() + ".HG" + i2);
        }
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void addToSolver(LinearSystem linearSystem) {
        super.addToSolver(linearSystem);
        int size = this.mChildren.size();
        if (size == 0) {
            return;
        }
        setTableDimensions();
        if (linearSystem == this.mSystem) {
            int size2 = this.mVerticalGuidelines.size();
            int i = 0;
            while (true) {
                boolean z = false;
                if (i >= size2) {
                    break;
                }
                Guideline guideline = this.mVerticalGuidelines.get(i);
                if (getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    z = true;
                }
                guideline.setPositionRelaxed(z);
                guideline.addToSolver(linearSystem);
                i++;
            }
            int size3 = this.mHorizontalGuidelines.size();
            for (int i2 = 0; i2 < size3; i2++) {
                Guideline guideline2 = this.mHorizontalGuidelines.get(i2);
                guideline2.setPositionRelaxed(getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                guideline2.addToSolver(linearSystem);
            }
            for (int i3 = 0; i3 < size; i3++) {
                this.mChildren.get(i3).addToSolver(linearSystem);
            }
        }
    }

    public void computeGuidelinesPercentPositions() {
        int size = this.mVerticalGuidelines.size();
        for (int i = 0; i < size; i++) {
            this.mVerticalGuidelines.get(i).inferRelativePercentPosition();
        }
        int size2 = this.mHorizontalGuidelines.size();
        for (int i2 = 0; i2 < size2; i2++) {
            this.mHorizontalGuidelines.get(i2).inferRelativePercentPosition();
        }
    }

    public void cycleColumnAlignment(int i) {
        VerticalSlice verticalSlice = this.mVerticalSlices.get(i);
        switch (verticalSlice.alignment) {
            case 0:
                verticalSlice.alignment = 2;
                break;
            case 1:
                verticalSlice.alignment = 0;
                break;
            case 2:
                verticalSlice.alignment = 1;
                break;
        }
        setChildrenConnections();
    }

    public String getColumnAlignmentRepresentation(int i) {
        VerticalSlice verticalSlice = this.mVerticalSlices.get(i);
        return verticalSlice.alignment == 1 ? TokenNames.L : verticalSlice.alignment == 0 ? TokenNames.C : verticalSlice.alignment == 3 ? TokenNames.F : verticalSlice.alignment == 2 ? TokenNames.R : "!";
    }

    public String getColumnsAlignmentRepresentation() {
        int size = this.mVerticalSlices.size();
        String str = "";
        for (int i = 0; i < size; i++) {
            VerticalSlice verticalSlice = this.mVerticalSlices.get(i);
            if (verticalSlice.alignment == 1) {
                str = str + TokenNames.L;
            } else if (verticalSlice.alignment == 0) {
                str = str + TokenNames.C;
            } else if (verticalSlice.alignment == 3) {
                str = str + TokenNames.F;
            } else if (verticalSlice.alignment == 2) {
                str = str + TokenNames.R;
            }
        }
        return str;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidgetContainer
    public ArrayList<Guideline> getHorizontalGuidelines() {
        return this.mHorizontalGuidelines;
    }

    public int getNumCols() {
        return this.mNumCols;
    }

    public int getNumRows() {
        return this.mNumRows;
    }

    public int getPadding() {
        return this.mPadding;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidgetContainer, android.support.constraint.solver.widgets.ConstraintWidget
    public String getType() {
        return "ConstraintTableLayout";
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidgetContainer
    public ArrayList<Guideline> getVerticalGuidelines() {
        return this.mVerticalGuidelines;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidgetContainer
    public boolean handlesInternalConstraints() {
        return true;
    }

    public boolean isVerticalGrowth() {
        return this.mVerticalGrowth;
    }

    public void setColumnAlignment(int i, int i2) {
        if (i < this.mVerticalSlices.size()) {
            this.mVerticalSlices.get(i).alignment = i2;
            setChildrenConnections();
        }
    }

    public void setColumnAlignment(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == 'L') {
                setColumnAlignment(i, 1);
            } else if (charAt == 'C') {
                setColumnAlignment(i, 0);
            } else if (charAt == 'F') {
                setColumnAlignment(i, 3);
            } else if (charAt == 'R') {
                setColumnAlignment(i, 2);
            } else {
                setColumnAlignment(i, 0);
            }
        }
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void setDebugSolverName(LinearSystem linearSystem, String str) {
        this.system = linearSystem;
        super.setDebugSolverName(linearSystem, str);
        updateDebugSolverNames();
    }

    public void setNumCols(int i) {
        if (!this.mVerticalGrowth || this.mNumCols == i) {
            return;
        }
        this.mNumCols = i;
        setVerticalSlices();
        setTableDimensions();
    }

    public void setNumRows(int i) {
        if (this.mVerticalGrowth || this.mNumCols == i) {
            return;
        }
        this.mNumRows = i;
        setHorizontalSlices();
        setTableDimensions();
    }

    public void setPadding(int i) {
        if (i > 1) {
            this.mPadding = i;
        }
    }

    public void setTableDimensions() {
        int i = 0;
        int size = this.mChildren.size();
        for (int i2 = 0; i2 < size; i2++) {
            i += this.mChildren.get(i2).getContainerItemSkip();
        }
        int i3 = size + i;
        if (this.mVerticalGrowth) {
            if (this.mNumCols == 0) {
                setNumCols(1);
            }
            int i4 = this.mNumCols;
            int i5 = i3 / i4;
            if (i4 * i5 < i3) {
                i5++;
            }
            if (this.mNumRows == i5 && this.mVerticalGuidelines.size() == this.mNumCols - 1) {
                return;
            }
            this.mNumRows = i5;
            setHorizontalSlices();
        } else {
            if (this.mNumRows == 0) {
                setNumRows(1);
            }
            int i6 = this.mNumRows;
            int i7 = i3 / i6;
            if (i6 * i7 < i3) {
                i7++;
            }
            if (this.mNumCols == i7 && this.mHorizontalGuidelines.size() == this.mNumRows - 1) {
                return;
            }
            this.mNumCols = i7;
            setVerticalSlices();
        }
        setChildrenConnections();
    }

    public void setVerticalGrowth(boolean z) {
        this.mVerticalGrowth = z;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void updateFromSolver(LinearSystem linearSystem) {
        super.updateFromSolver(linearSystem);
        if (linearSystem == this.mSystem) {
            int size = this.mVerticalGuidelines.size();
            for (int i = 0; i < size; i++) {
                this.mVerticalGuidelines.get(i).updateFromSolver(linearSystem);
            }
            int size2 = this.mHorizontalGuidelines.size();
            for (int i2 = 0; i2 < size2; i2++) {
                this.mHorizontalGuidelines.get(i2).updateFromSolver(linearSystem);
            }
        }
    }
}