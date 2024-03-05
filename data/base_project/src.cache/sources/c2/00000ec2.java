package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Chain.class */
public class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        int i2;
        int i3;
        ChainHead[] chainHeadArr;
        if (i == 0) {
            i2 = 0;
            i3 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
        } else {
            i2 = 2;
            i3 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i4 = 0; i4 < i3; i4++) {
            ChainHead chainHead = chainHeadArr[i4];
            chainHead.define();
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, linearSystem, i, i2, chainHead)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
            }
        }
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead) {
        boolean z;
        ConstraintWidget constraintWidget;
        boolean z2;
        boolean z3;
        boolean z4;
        ConstraintWidget constraintWidget2;
        ConstraintAnchor constraintAnchor;
        SolverVariable solverVariable;
        SolverVariable solverVariable2;
        ConstraintWidget constraintWidget3;
        SolverVariable solverVariable3;
        SolverVariable solverVariable4;
        ConstraintAnchor constraintAnchor2;
        ConstraintAnchor constraintAnchor3;
        int size;
        ConstraintWidget constraintWidget4;
        ConstraintWidget constraintWidget5 = chainHead.mFirst;
        ConstraintWidget constraintWidget6 = chainHead.mLast;
        ConstraintWidget constraintWidget7 = chainHead.mFirstVisibleWidget;
        ConstraintWidget constraintWidget8 = chainHead.mLastVisibleWidget;
        ConstraintWidget constraintWidget9 = chainHead.mHead;
        float f = chainHead.mTotalWeight;
        ConstraintWidget constraintWidget10 = chainHead.mFirstMatchConstraintWidget;
        ConstraintWidget constraintWidget11 = chainHead.mLastMatchConstraintWidget;
        boolean z5 = constraintWidgetContainer.mListDimensionBehaviors[i] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (i == 0) {
            z = constraintWidget9.mHorizontalChainStyle == 0;
            z2 = constraintWidget9.mHorizontalChainStyle == 1;
            constraintWidget = constraintWidget5;
            z4 = false;
            z3 = constraintWidget9.mHorizontalChainStyle == 2;
        } else {
            z = constraintWidget9.mVerticalChainStyle == 0;
            constraintWidget = constraintWidget5;
            z2 = constraintWidget9.mVerticalChainStyle == 1;
            z3 = constraintWidget9.mVerticalChainStyle == 2;
            z4 = false;
        }
        while (!z4) {
            ConstraintAnchor constraintAnchor4 = constraintWidget.mListAnchors[i2];
            int i3 = 4;
            i3 = (z5 || z3) ? 1 : 1;
            int margin = constraintAnchor4.getMargin();
            if (constraintAnchor4.mTarget != null && constraintWidget != constraintWidget5) {
                margin += constraintAnchor4.mTarget.getMargin();
            }
            if (z3 && constraintWidget != constraintWidget5 && constraintWidget != constraintWidget7) {
                i3 = 6;
            } else if (z && z5) {
                i3 = 4;
            }
            if (constraintAnchor4.mTarget != null) {
                if (constraintWidget == constraintWidget7) {
                    linearSystem.addGreaterThan(constraintAnchor4.mSolverVariable, constraintAnchor4.mTarget.mSolverVariable, margin, 5);
                } else {
                    linearSystem.addGreaterThan(constraintAnchor4.mSolverVariable, constraintAnchor4.mTarget.mSolverVariable, margin, 6);
                }
                linearSystem.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor4.mTarget.mSolverVariable, margin, i3);
            }
            if (z5) {
                if (constraintWidget.getVisibility() != 8 && constraintWidget.mListDimensionBehaviors[i] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    linearSystem.addGreaterThan(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 5);
                }
                linearSystem.addGreaterThan(constraintWidget.mListAnchors[i2].mSolverVariable, constraintWidgetContainer.mListAnchors[i2].mSolverVariable, 0, 6);
            }
            ConstraintAnchor constraintAnchor5 = constraintWidget.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor5 != null) {
                constraintWidget4 = constraintAnchor5.mOwner;
                if (constraintWidget4.mListAnchors[i2].mTarget == null || constraintWidget4.mListAnchors[i2].mTarget.mOwner != constraintWidget) {
                    constraintWidget4 = null;
                }
            } else {
                constraintWidget4 = null;
            }
            if (constraintWidget4 != null) {
                constraintWidget = constraintWidget4;
            } else {
                z4 = true;
            }
        }
        if (constraintWidget8 != null && constraintWidget6.mListAnchors[i2 + 1].mTarget != null) {
            ConstraintAnchor constraintAnchor6 = constraintWidget8.mListAnchors[i2 + 1];
            linearSystem.addLowerThan(constraintAnchor6.mSolverVariable, constraintWidget6.mListAnchors[i2 + 1].mTarget.mSolverVariable, -constraintAnchor6.getMargin(), 5);
        }
        if (z5) {
            linearSystem.addGreaterThan(constraintWidgetContainer.mListAnchors[i2 + 1].mSolverVariable, constraintWidget6.mListAnchors[i2 + 1].mSolverVariable, constraintWidget6.mListAnchors[i2 + 1].getMargin(), 6);
        }
        ArrayList<ConstraintWidget> arrayList = chainHead.mWeightedMatchConstraintsWidgets;
        if (arrayList != null && (size = arrayList.size()) > 1) {
            float f2 = (!chainHead.mHasUndefinedWeights || chainHead.mHasComplexMatchWeights) ? f : chainHead.mWidgetsMatchCount;
            int i4 = 0;
            float f3 = 0.0f;
            ConstraintWidget constraintWidget12 = null;
            while (i4 < size) {
                ConstraintWidget constraintWidget13 = arrayList.get(i4);
                float f4 = constraintWidget13.mWeight[i];
                if (f4 < 0.0f) {
                    if (chainHead.mHasComplexMatchWeights) {
                        linearSystem.addEquality(constraintWidget13.mListAnchors[i2 + 1].mSolverVariable, constraintWidget13.mListAnchors[i2].mSolverVariable, 0, 4);
                        f4 = f3;
                        i4++;
                        f3 = f4;
                    } else {
                        f4 = 1.0f;
                    }
                }
                if (f4 == 0.0f) {
                    linearSystem.addEquality(constraintWidget13.mListAnchors[i2 + 1].mSolverVariable, constraintWidget13.mListAnchors[i2].mSolverVariable, 0, 6);
                    f4 = f3;
                } else {
                    if (constraintWidget12 != null) {
                        SolverVariable solverVariable5 = constraintWidget12.mListAnchors[i2].mSolverVariable;
                        SolverVariable solverVariable6 = constraintWidget12.mListAnchors[i2 + 1].mSolverVariable;
                        SolverVariable solverVariable7 = constraintWidget13.mListAnchors[i2].mSolverVariable;
                        SolverVariable solverVariable8 = constraintWidget13.mListAnchors[i2 + 1].mSolverVariable;
                        ArrayRow createRow = linearSystem.createRow();
                        createRow.createRowEqualMatchDimensions(f3, f2, f4, solverVariable5, solverVariable6, solverVariable7, solverVariable8);
                        linearSystem.addConstraint(createRow);
                    }
                    constraintWidget12 = constraintWidget13;
                }
                i4++;
                f3 = f4;
            }
        }
        if (constraintWidget7 != null && (constraintWidget7 == constraintWidget8 || z3)) {
            ConstraintAnchor constraintAnchor7 = constraintWidget5.mListAnchors[i2];
            ConstraintAnchor constraintAnchor8 = constraintWidget6.mListAnchors[i2 + 1];
            SolverVariable solverVariable9 = constraintWidget5.mListAnchors[i2].mTarget != null ? constraintWidget5.mListAnchors[i2].mTarget.mSolverVariable : null;
            SolverVariable solverVariable10 = constraintWidget6.mListAnchors[i2 + 1].mTarget != null ? constraintWidget6.mListAnchors[i2 + 1].mTarget.mSolverVariable : null;
            if (constraintWidget7 == constraintWidget8) {
                constraintAnchor7 = constraintWidget7.mListAnchors[i2];
                constraintAnchor8 = constraintWidget7.mListAnchors[i2 + 1];
            }
            if (solverVariable9 != null && solverVariable10 != null) {
                linearSystem.addCentering(constraintAnchor7.mSolverVariable, solverVariable9, constraintAnchor7.getMargin(), i == 0 ? constraintWidget9.mHorizontalBiasPercent : constraintWidget9.mVerticalBiasPercent, solverVariable10, constraintAnchor8.mSolverVariable, constraintAnchor8.getMargin(), 5);
            }
        } else if (z && constraintWidget7 != null) {
            boolean z6 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
            ConstraintWidget constraintWidget14 = constraintWidget7;
            ConstraintWidget constraintWidget15 = constraintWidget7;
            while (constraintWidget14 != null) {
                ConstraintWidget constraintWidget16 = constraintWidget14.mNextChainWidget[i];
                while (true) {
                    constraintWidget3 = constraintWidget16;
                    if (constraintWidget3 == null || constraintWidget3.getVisibility() != 8) {
                        break;
                    }
                    constraintWidget16 = constraintWidget3.mNextChainWidget[i];
                }
                if (constraintWidget3 != null || constraintWidget14 == constraintWidget8) {
                    ConstraintAnchor constraintAnchor9 = constraintWidget14.mListAnchors[i2];
                    SolverVariable solverVariable11 = constraintAnchor9.mSolverVariable;
                    SolverVariable solverVariable12 = constraintAnchor9.mTarget != null ? constraintAnchor9.mTarget.mSolverVariable : null;
                    if (constraintWidget15 != constraintWidget14) {
                        solverVariable12 = constraintWidget15.mListAnchors[i2 + 1].mSolverVariable;
                    } else if (constraintWidget14 == constraintWidget7 && constraintWidget15 == constraintWidget14) {
                        solverVariable12 = constraintWidget5.mListAnchors[i2].mTarget != null ? constraintWidget5.mListAnchors[i2].mTarget.mSolverVariable : null;
                    }
                    int margin2 = constraintAnchor9.getMargin();
                    int margin3 = constraintWidget14.mListAnchors[i2 + 1].getMargin();
                    if (constraintWidget3 != null) {
                        constraintAnchor2 = constraintWidget3.mListAnchors[i2];
                        solverVariable3 = constraintAnchor2.mSolverVariable;
                        solverVariable4 = constraintWidget14.mListAnchors[i2 + 1].mSolverVariable;
                    } else {
                        ConstraintAnchor constraintAnchor10 = constraintWidget6.mListAnchors[i2 + 1].mTarget;
                        solverVariable3 = constraintAnchor10 != null ? constraintAnchor10.mSolverVariable : null;
                        solverVariable4 = constraintWidget14.mListAnchors[i2 + 1].mSolverVariable;
                        constraintAnchor2 = constraintAnchor10;
                    }
                    if (constraintAnchor2 != null) {
                        margin3 += constraintAnchor2.getMargin();
                    }
                    if (constraintWidget15 != null) {
                        margin2 += constraintWidget15.mListAnchors[i2 + 1].getMargin();
                    }
                    if (solverVariable11 != null && solverVariable12 != null && solverVariable3 != null && solverVariable4 != null) {
                        if (constraintWidget14 == constraintWidget7) {
                            margin2 = constraintWidget7.mListAnchors[i2].getMargin();
                        }
                        if (constraintWidget14 == constraintWidget8) {
                            margin3 = constraintWidget8.mListAnchors[i2 + 1].getMargin();
                        }
                        linearSystem.addCentering(solverVariable11, solverVariable12, margin2, 0.5f, solverVariable3, solverVariable4, margin3, z6 ? 6 : 4);
                    }
                }
                if (constraintWidget14.getVisibility() != 8) {
                    constraintWidget15 = constraintWidget14;
                }
                constraintWidget14 = constraintWidget3;
            }
        } else if (z2 && constraintWidget7 != null) {
            boolean z7 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
            ConstraintWidget constraintWidget17 = constraintWidget7;
            for (ConstraintWidget constraintWidget18 = constraintWidget7; constraintWidget18 != null; constraintWidget18 = constraintWidget2) {
                ConstraintWidget constraintWidget19 = constraintWidget18.mNextChainWidget[i];
                while (true) {
                    constraintWidget2 = constraintWidget19;
                    if (constraintWidget2 == null || constraintWidget2.getVisibility() != 8) {
                        break;
                    }
                    constraintWidget19 = constraintWidget2.mNextChainWidget[i];
                }
                if (constraintWidget18 != constraintWidget7 && constraintWidget18 != constraintWidget8 && constraintWidget2 != null) {
                    if (constraintWidget2 == constraintWidget8) {
                        constraintWidget2 = null;
                    }
                    ConstraintAnchor constraintAnchor11 = constraintWidget18.mListAnchors[i2];
                    SolverVariable solverVariable13 = constraintAnchor11.mSolverVariable;
                    if (constraintAnchor11.mTarget != null) {
                        SolverVariable solverVariable14 = constraintAnchor11.mTarget.mSolverVariable;
                    }
                    SolverVariable solverVariable15 = constraintWidget17.mListAnchors[i2 + 1].mSolverVariable;
                    int margin4 = constraintAnchor11.getMargin();
                    int margin5 = constraintWidget18.mListAnchors[i2 + 1].getMargin();
                    if (constraintWidget2 != null) {
                        constraintAnchor = constraintWidget2.mListAnchors[i2];
                        solverVariable = constraintAnchor.mSolverVariable;
                        solverVariable2 = constraintAnchor.mTarget != null ? constraintAnchor.mTarget.mSolverVariable : null;
                    } else {
                        constraintAnchor = constraintWidget18.mListAnchors[i2 + 1].mTarget;
                        solverVariable = constraintAnchor != null ? constraintAnchor.mSolverVariable : null;
                        solverVariable2 = constraintWidget18.mListAnchors[i2 + 1].mSolverVariable;
                    }
                    if (constraintAnchor != null) {
                        margin5 += constraintAnchor.getMargin();
                    }
                    if (constraintWidget17 != null) {
                        margin4 += constraintWidget17.mListAnchors[i2 + 1].getMargin();
                    }
                    int i5 = z7 ? 6 : 4;
                    if (solverVariable13 != null && solverVariable15 != null && solverVariable != null && solverVariable2 != null) {
                        linearSystem.addCentering(solverVariable13, solverVariable15, margin4, 0.5f, solverVariable, solverVariable2, margin5, i5);
                    }
                }
                if (constraintWidget18.getVisibility() != 8) {
                    constraintWidget17 = constraintWidget18;
                }
            }
            ConstraintAnchor constraintAnchor12 = constraintWidget7.mListAnchors[i2];
            ConstraintAnchor constraintAnchor13 = constraintWidget5.mListAnchors[i2].mTarget;
            ConstraintAnchor constraintAnchor14 = constraintWidget8.mListAnchors[i2 + 1];
            ConstraintAnchor constraintAnchor15 = constraintWidget6.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor13 != null) {
                if (constraintWidget7 != constraintWidget8) {
                    linearSystem.addEquality(constraintAnchor12.mSolverVariable, constraintAnchor13.mSolverVariable, constraintAnchor12.getMargin(), 5);
                } else if (constraintAnchor15 != null) {
                    linearSystem.addCentering(constraintAnchor12.mSolverVariable, constraintAnchor13.mSolverVariable, constraintAnchor12.getMargin(), 0.5f, constraintAnchor14.mSolverVariable, constraintAnchor15.mSolverVariable, constraintAnchor14.getMargin(), 5);
                }
            }
            if (constraintAnchor15 != null && constraintWidget7 != constraintWidget8) {
                linearSystem.addEquality(constraintAnchor14.mSolverVariable, constraintAnchor15.mSolverVariable, -constraintAnchor14.getMargin(), 5);
            }
        }
        if ((z || z2) && constraintWidget7 != null) {
            ConstraintAnchor constraintAnchor16 = constraintWidget7.mListAnchors[i2];
            ConstraintAnchor constraintAnchor17 = constraintWidget8.mListAnchors[i2 + 1];
            SolverVariable solverVariable16 = constraintAnchor16.mTarget != null ? constraintAnchor16.mTarget.mSolverVariable : null;
            SolverVariable solverVariable17 = constraintAnchor17.mTarget != null ? constraintAnchor17.mTarget.mSolverVariable : null;
            if (constraintWidget6 != constraintWidget8) {
                ConstraintAnchor constraintAnchor18 = constraintWidget6.mListAnchors[i2 + 1];
                solverVariable17 = constraintAnchor18.mTarget != null ? constraintAnchor18.mTarget.mSolverVariable : null;
            }
            if (constraintWidget7 == constraintWidget8) {
                ConstraintAnchor constraintAnchor19 = constraintWidget7.mListAnchors[i2];
                constraintAnchor17 = constraintWidget7.mListAnchors[i2 + 1];
                constraintAnchor3 = constraintAnchor19;
            } else {
                constraintAnchor3 = constraintAnchor16;
            }
            if (solverVariable16 == null || solverVariable17 == null) {
                return;
            }
            linearSystem.addCentering(constraintAnchor3.mSolverVariable, solverVariable16, constraintAnchor3.getMargin(), 0.5f, solverVariable17, constraintAnchor17.mSolverVariable, (constraintWidget8 == null ? constraintWidget6 : constraintWidget8).mListAnchors[i2 + 1].getMargin(), 5);
        }
    }
}