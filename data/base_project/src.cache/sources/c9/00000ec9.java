package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor;

/* loaded from: ConstraintHorizontalLayout.class */
public class ConstraintHorizontalLayout extends ConstraintWidgetContainer {
    private ContentAlignment mAlignment;

    /* loaded from: ConstraintHorizontalLayout$ContentAlignment.class */
    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public ConstraintHorizontalLayout() {
        this.mAlignment = ContentAlignment.MIDDLE;
    }

    public ConstraintHorizontalLayout(int i, int i2) {
        super(i, i2);
        this.mAlignment = ContentAlignment.MIDDLE;
    }

    public ConstraintHorizontalLayout(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.mAlignment = ContentAlignment.MIDDLE;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void addToSolver(LinearSystem linearSystem) {
        if (this.mChildren.size() != 0) {
            ConstraintWidget constraintWidget = this;
            int size = this.mChildren.size();
            for (int i = 0; i < size; i++) {
                ConstraintWidget constraintWidget2 = this.mChildren.get(i);
                if (constraintWidget != this) {
                    constraintWidget2.connect(ConstraintAnchor.Type.LEFT, constraintWidget, ConstraintAnchor.Type.RIGHT);
                    constraintWidget.connect(ConstraintAnchor.Type.RIGHT, constraintWidget2, ConstraintAnchor.Type.LEFT);
                } else {
                    ConstraintAnchor.Strength strength = ConstraintAnchor.Strength.STRONG;
                    if (this.mAlignment == ContentAlignment.END) {
                        strength = ConstraintAnchor.Strength.WEAK;
                    }
                    constraintWidget2.connect(ConstraintAnchor.Type.LEFT, constraintWidget, ConstraintAnchor.Type.LEFT, 0, strength);
                }
                constraintWidget2.connect(ConstraintAnchor.Type.TOP, this, ConstraintAnchor.Type.TOP);
                constraintWidget2.connect(ConstraintAnchor.Type.BOTTOM, this, ConstraintAnchor.Type.BOTTOM);
                constraintWidget = constraintWidget2;
            }
            if (constraintWidget != this) {
                ConstraintAnchor.Strength strength2 = ConstraintAnchor.Strength.STRONG;
                if (this.mAlignment == ContentAlignment.BEGIN) {
                    strength2 = ConstraintAnchor.Strength.WEAK;
                }
                constraintWidget.connect(ConstraintAnchor.Type.RIGHT, this, ConstraintAnchor.Type.RIGHT, 0, strength2);
            }
        }
        super.addToSolver(linearSystem);
    }
}