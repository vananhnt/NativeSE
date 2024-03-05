package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintWidget;

/* loaded from: Optimizer.class */
public class Optimizer {
    static final int FLAG_CHAIN_DANGLING = 1;
    static final int FLAG_RECOMPUTE_BOUNDS = 2;
    static final int FLAG_USE_OPTIMIZE = 0;
    public static final int OPTIMIZATION_BARRIER = 2;
    public static final int OPTIMIZATION_CHAIN = 4;
    public static final int OPTIMIZATION_DIMENSIONS = 8;
    public static final int OPTIMIZATION_DIRECT = 1;
    public static final int OPTIMIZATION_GROUPS = 32;
    public static final int OPTIMIZATION_NONE = 0;
    public static final int OPTIMIZATION_RATIO = 16;
    public static final int OPTIMIZATION_STANDARD = 7;
    static boolean[] flags = new boolean[3];

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void analyze(int i, ConstraintWidget constraintWidget) {
        constraintWidget.updateResolutionNodes();
        ResolutionAnchor resolutionNode = constraintWidget.mLeft.getResolutionNode();
        ResolutionAnchor resolutionNode2 = constraintWidget.mTop.getResolutionNode();
        ResolutionAnchor resolutionNode3 = constraintWidget.mRight.getResolutionNode();
        ResolutionAnchor resolutionNode4 = constraintWidget.mBottom.getResolutionNode();
        boolean z = (i & 8) == 8;
        boolean z2 = constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget, 0);
        if (resolutionNode.type != 4 && resolutionNode3.type != 4) {
            if (constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.FIXED || (z2 && constraintWidget.getVisibility() == 8)) {
                if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget == null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (z) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                    }
                } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget == null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (z) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                    }
                } else if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget != null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                    if (z) {
                        resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                    }
                } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget != null) {
                    resolutionNode.setType(2);
                    resolutionNode3.setType(2);
                    if (z) {
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                        resolutionNode.setOpposite(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                        resolutionNode3.setOpposite(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.setOpposite(resolutionNode3, -constraintWidget.getWidth());
                        resolutionNode3.setOpposite(resolutionNode, constraintWidget.getWidth());
                    }
                }
            } else if (z2) {
                int width = constraintWidget.getWidth();
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget == null) {
                    if (z) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, width);
                    }
                } else if (constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget != null) {
                    if (constraintWidget.mLeft.mTarget != null || constraintWidget.mRight.mTarget == null) {
                        if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget != null) {
                            if (z) {
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                            }
                            if (constraintWidget.mDimensionRatio == 0.0f) {
                                resolutionNode.setType(3);
                                resolutionNode3.setType(3);
                                resolutionNode.setOpposite(resolutionNode3, 0.0f);
                                resolutionNode3.setOpposite(resolutionNode, 0.0f);
                            } else {
                                resolutionNode.setType(2);
                                resolutionNode3.setType(2);
                                resolutionNode.setOpposite(resolutionNode3, -width);
                                resolutionNode3.setOpposite(resolutionNode, width);
                                constraintWidget.setWidth(width);
                            }
                        }
                    } else if (z) {
                        resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.dependsOn(resolutionNode3, -width);
                    }
                } else if (z) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode3.dependsOn(resolutionNode, width);
                }
            }
        }
        boolean z3 = constraintWidget.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget, 1);
        if (resolutionNode2.type == 4 || resolutionNode4.type == 4) {
            return;
        }
        if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.FIXED && (!z3 || constraintWidget.getVisibility() != 8)) {
            if (z3) {
                int height = constraintWidget.getHeight();
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
                    if (z) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, height);
                    }
                } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget == null) {
                    if (z) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, height);
                    }
                } else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget != null) {
                    if (z) {
                        resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode2.dependsOn(resolutionNode4, -height);
                    }
                } else if (constraintWidget.mTop.mTarget == null || constraintWidget.mBottom.mTarget == null) {
                } else {
                    if (z) {
                        constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                    }
                    if (constraintWidget.mDimensionRatio == 0.0f) {
                        resolutionNode2.setType(3);
                        resolutionNode4.setType(3);
                        resolutionNode2.setOpposite(resolutionNode4, 0.0f);
                        resolutionNode4.setOpposite(resolutionNode2, 0.0f);
                        return;
                    }
                    resolutionNode2.setType(2);
                    resolutionNode4.setType(2);
                    resolutionNode2.setOpposite(resolutionNode4, -height);
                    resolutionNode4.setOpposite(resolutionNode2, height);
                    constraintWidget.setHeight(height);
                    if (constraintWidget.mBaselineDistance > 0) {
                        constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                    }
                }
            }
        } else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
            resolutionNode2.setType(1);
            resolutionNode4.setType(1);
            if (z) {
                resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
            } else {
                resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
            }
            if (constraintWidget.mBaseline.mTarget != null) {
                constraintWidget.mBaseline.getResolutionNode().setType(1);
                resolutionNode2.dependsOn(1, constraintWidget.mBaseline.getResolutionNode(), -constraintWidget.mBaselineDistance);
            }
        } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget == null) {
            resolutionNode2.setType(1);
            resolutionNode4.setType(1);
            if (z) {
                resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
            } else {
                resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
            }
            if (constraintWidget.mBaselineDistance > 0) {
                constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
            }
        } else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget != null) {
            resolutionNode2.setType(1);
            resolutionNode4.setType(1);
            if (z) {
                resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
            } else {
                resolutionNode2.dependsOn(resolutionNode4, -constraintWidget.getHeight());
            }
            if (constraintWidget.mBaselineDistance > 0) {
                constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
            }
        } else if (constraintWidget.mTop.mTarget == null || constraintWidget.mBottom.mTarget == null) {
        } else {
            resolutionNode2.setType(2);
            resolutionNode4.setType(2);
            if (z) {
                resolutionNode2.setOpposite(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                resolutionNode4.setOpposite(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
            } else {
                resolutionNode2.setOpposite(resolutionNode4, -constraintWidget.getHeight());
                resolutionNode4.setOpposite(resolutionNode2, constraintWidget.getHeight());
            }
            if (constraintWidget.mBaselineDistance > 0) {
                constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean applyChainOptimized(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead) {
        boolean z;
        boolean z2;
        boolean z3;
        ConstraintWidget constraintWidget;
        float f;
        ConstraintWidget constraintWidget2;
        ConstraintWidget constraintWidget3 = chainHead.mFirst;
        ConstraintWidget constraintWidget4 = chainHead.mLast;
        ConstraintWidget constraintWidget5 = chainHead.mFirstVisibleWidget;
        ConstraintWidget constraintWidget6 = chainHead.mLastVisibleWidget;
        ConstraintWidget constraintWidget7 = chainHead.mHead;
        boolean z4 = false;
        float f2 = chainHead.mTotalWeight;
        ConstraintWidget constraintWidget8 = chainHead.mFirstMatchConstraintWidget;
        ConstraintWidget constraintWidget9 = chainHead.mLastMatchConstraintWidget;
        if (constraintWidgetContainer.mListDimensionBehaviors[i] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
        }
        if (i == 0) {
            z = constraintWidget7.mHorizontalChainStyle == 0;
            z2 = constraintWidget7.mHorizontalChainStyle == 1;
            z3 = constraintWidget7.mHorizontalChainStyle == 2;
        } else {
            z = constraintWidget7.mVerticalChainStyle == 0;
            z2 = constraintWidget7.mVerticalChainStyle == 1;
            z3 = constraintWidget7.mVerticalChainStyle == 2;
        }
        int i3 = 0;
        int i4 = 0;
        float f3 = 0.0f;
        float f4 = 0.0f;
        ConstraintWidget constraintWidget10 = constraintWidget3;
        while (!z4) {
            if (constraintWidget10.getVisibility() != 8) {
                i4++;
                f = i == 0 ? f4 + constraintWidget10.getWidth() : f4 + constraintWidget10.getHeight();
                if (constraintWidget10 != constraintWidget5) {
                    f += constraintWidget10.mListAnchors[i2].getMargin();
                }
                if (constraintWidget10 != constraintWidget6) {
                    f += constraintWidget10.mListAnchors[i2 + 1].getMargin();
                }
                f3 = f3 + constraintWidget10.mListAnchors[i2].getMargin() + constraintWidget10.mListAnchors[i2 + 1].getMargin();
            } else {
                f = f4;
            }
            ConstraintAnchor constraintAnchor = constraintWidget10.mListAnchors[i2];
            if (constraintWidget10.getVisibility() != 8 && constraintWidget10.mListDimensionBehaviors[i] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                i3++;
                if (i == 0) {
                    if (constraintWidget10.mMatchConstraintDefaultWidth != 0 || constraintWidget10.mMatchConstraintMinWidth != 0 || constraintWidget10.mMatchConstraintMaxWidth != 0) {
                        return false;
                    }
                } else if (constraintWidget10.mMatchConstraintDefaultHeight != 0 || constraintWidget10.mMatchConstraintMinHeight != 0 || constraintWidget10.mMatchConstraintMaxHeight != 0) {
                    return false;
                }
                if (constraintWidget10.mDimensionRatio != 0.0f) {
                    return false;
                }
            }
            ConstraintAnchor constraintAnchor2 = constraintWidget10.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor2 != null) {
                constraintWidget2 = constraintAnchor2.mOwner;
                if (constraintWidget2.mListAnchors[i2].mTarget == null || constraintWidget2.mListAnchors[i2].mTarget.mOwner != constraintWidget10) {
                    constraintWidget2 = null;
                }
            } else {
                constraintWidget2 = null;
            }
            if (constraintWidget2 != null) {
                constraintWidget10 = constraintWidget2;
            } else {
                z4 = true;
            }
            f4 = f;
        }
        ResolutionAnchor resolutionNode = constraintWidget3.mListAnchors[i2].getResolutionNode();
        ResolutionAnchor resolutionNode2 = constraintWidget4.mListAnchors[i2 + 1].getResolutionNode();
        if (resolutionNode.target == null || resolutionNode2.target == null || resolutionNode.target.state != 1 || resolutionNode2.target.state != 1) {
            return false;
        }
        if (i3 <= 0 || i3 == i4) {
            float f5 = 0.0f;
            if (z3 || z || z2) {
                if (constraintWidget5 != null) {
                    f5 = constraintWidget5.mListAnchors[i2].getMargin();
                }
                if (constraintWidget6 != null) {
                    f5 += constraintWidget6.mListAnchors[i2 + 1].getMargin();
                }
            } else {
                f5 = 0.0f;
            }
            float f6 = resolutionNode.target.resolvedOffset;
            float f7 = resolutionNode2.target.resolvedOffset;
            float f8 = f6 < f7 ? (f7 - f6) - f4 : (f6 - f7) - f4;
            if (i3 > 0 && i3 == i4) {
                if (constraintWidget10.getParent() == null || constraintWidget10.getParent().mListDimensionBehaviors[i] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    float f9 = (f8 + f4) - f3;
                    ConstraintWidget constraintWidget11 = constraintWidget3;
                    float f10 = f6;
                    while (constraintWidget11 != null) {
                        if (LinearSystem.sMetrics != null) {
                            LinearSystem.sMetrics.nonresolvedWidgets--;
                            LinearSystem.sMetrics.resolvedWidgets++;
                            LinearSystem.sMetrics.chainConnectionResolved++;
                        }
                        ConstraintWidget constraintWidget12 = constraintWidget11.mNextChainWidget[i];
                        if (constraintWidget12 != null || constraintWidget11 == constraintWidget4) {
                            float f11 = f9 / i3;
                            if (f2 > 0.0f) {
                                f11 = constraintWidget11.mWeight[i] == -1.0f ? 0.0f : (constraintWidget11.mWeight[i] * f9) / f2;
                            }
                            if (constraintWidget11.getVisibility() == 8) {
                                f11 = 0.0f;
                            }
                            float margin = f10 + constraintWidget11.mListAnchors[i2].getMargin();
                            constraintWidget11.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, margin);
                            constraintWidget11.mListAnchors[i2 + 1].getResolutionNode().resolve(resolutionNode.resolvedTarget, margin + f11);
                            constraintWidget11.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem);
                            constraintWidget11.mListAnchors[i2 + 1].getResolutionNode().addResolvedValue(linearSystem);
                            f10 = margin + f11 + constraintWidget11.mListAnchors[i2 + 1].getMargin();
                        }
                        constraintWidget11 = constraintWidget12;
                    }
                    return true;
                }
                return false;
            }
            if (f8 < 0.0f) {
                z = false;
                z2 = false;
                z3 = true;
            }
            if (z3) {
                ConstraintWidget constraintWidget13 = constraintWidget3;
                float biasPercent = (constraintWidget3.getBiasPercent(i) * (f8 - f5)) + f6;
                while (constraintWidget13 != null) {
                    if (LinearSystem.sMetrics != null) {
                        LinearSystem.sMetrics.nonresolvedWidgets--;
                        LinearSystem.sMetrics.resolvedWidgets++;
                        LinearSystem.sMetrics.chainConnectionResolved++;
                    }
                    ConstraintWidget constraintWidget14 = constraintWidget13.mNextChainWidget[i];
                    if (constraintWidget14 != null || constraintWidget13 == constraintWidget4) {
                        float width = i == 0 ? constraintWidget13.getWidth() : constraintWidget13.getHeight();
                        float margin2 = biasPercent + constraintWidget13.mListAnchors[i2].getMargin();
                        constraintWidget13.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, margin2);
                        constraintWidget13.mListAnchors[i2 + 1].getResolutionNode().resolve(resolutionNode.resolvedTarget, margin2 + width);
                        constraintWidget13.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem);
                        constraintWidget13.mListAnchors[i2 + 1].getResolutionNode().addResolvedValue(linearSystem);
                        biasPercent = margin2 + width + constraintWidget13.mListAnchors[i2 + 1].getMargin();
                    }
                    constraintWidget13 = constraintWidget14;
                }
                return true;
            } else if (z || z2) {
                if (z) {
                    f8 -= f5;
                } else if (z2) {
                    f8 -= f5;
                }
                float f12 = z2 ? i4 > 1 ? f8 / (i4 - 1) : f8 / 2.0f : f8 / (i4 + 1);
                float f13 = f6;
                if (constraintWidget3.getVisibility() != 8) {
                    f13 += f12;
                }
                if (z2 && i4 > 1) {
                    f13 = f6 + constraintWidget5.mListAnchors[i2].getMargin();
                }
                if (!z || constraintWidget5 == null) {
                    constraintWidget = constraintWidget3;
                } else {
                    constraintWidget = constraintWidget3;
                    f13 += constraintWidget5.mListAnchors[i2].getMargin();
                }
                while (constraintWidget != null) {
                    if (LinearSystem.sMetrics != null) {
                        LinearSystem.sMetrics.nonresolvedWidgets--;
                        LinearSystem.sMetrics.resolvedWidgets++;
                        LinearSystem.sMetrics.chainConnectionResolved++;
                    }
                    ConstraintWidget constraintWidget15 = constraintWidget.mNextChainWidget[i];
                    if (constraintWidget15 != null || constraintWidget == constraintWidget4) {
                        float width2 = i == 0 ? constraintWidget.getWidth() : constraintWidget.getHeight();
                        if (constraintWidget != constraintWidget5) {
                            f13 += constraintWidget.mListAnchors[i2].getMargin();
                        }
                        constraintWidget.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, f13);
                        constraintWidget.mListAnchors[i2 + 1].getResolutionNode().resolve(resolutionNode.resolvedTarget, f13 + width2);
                        constraintWidget.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem);
                        constraintWidget.mListAnchors[i2 + 1].getResolutionNode().addResolvedValue(linearSystem);
                        float margin3 = f13 + constraintWidget.mListAnchors[i2 + 1].getMargin() + width2;
                        if (constraintWidget15 != null) {
                            f13 = margin3;
                            if (constraintWidget15.getVisibility() != 8) {
                                f13 = margin3 + f12;
                            }
                        } else {
                            f13 = margin3;
                        }
                    }
                    constraintWidget = constraintWidget15;
                }
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            int i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(i, width);
        }
        if (constraintWidgetContainer.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            return;
        }
        int i2 = constraintWidget.mTop.mMargin;
        int height = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
        constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
        constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
        linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i2);
        linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
        if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
            constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
            linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i2);
        }
        constraintWidget.mVerticalResolution = 2;
        constraintWidget.setVerticalDimension(i2, height);
    }

    private static boolean optimizableMatchConstraint(ConstraintWidget constraintWidget, int i) {
        if (constraintWidget.mListDimensionBehaviors[i] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            return false;
        }
        if (constraintWidget.mDimensionRatio != 0.0f) {
            return constraintWidget.mListDimensionBehaviors[(i == 0 ? (byte) 1 : (byte) 0) == 1 ? 1 : 0] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT ? false : false;
        }
        return i == 0 ? constraintWidget.mMatchConstraintDefaultWidth == 0 && constraintWidget.mMatchConstraintMinWidth == 0 && constraintWidget.mMatchConstraintMaxWidth == 0 : constraintWidget.mMatchConstraintDefaultHeight == 0 && constraintWidget.mMatchConstraintMinHeight == 0 && constraintWidget.mMatchConstraintMaxHeight == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setOptimizedWidget(ConstraintWidget constraintWidget, int i, int i2) {
        int i3 = i * 2;
        int i4 = i3 + 1;
        constraintWidget.mListAnchors[i3].getResolutionNode().resolvedTarget = constraintWidget.getParent().mLeft.getResolutionNode();
        constraintWidget.mListAnchors[i3].getResolutionNode().resolvedOffset = i2;
        constraintWidget.mListAnchors[i3].getResolutionNode().state = 1;
        constraintWidget.mListAnchors[i4].getResolutionNode().resolvedTarget = constraintWidget.mListAnchors[i3].getResolutionNode();
        constraintWidget.mListAnchors[i4].getResolutionNode().resolvedOffset = constraintWidget.getLength(i);
        constraintWidget.mListAnchors[i4].getResolutionNode().state = 1;
    }
}