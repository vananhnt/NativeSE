package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import gov.nist.core.Separators;

/* loaded from: ResolutionAnchor.class */
public class ResolutionAnchor extends ResolutionNode {
    public static final int BARRIER_CONNECTION = 5;
    public static final int CENTER_CONNECTION = 2;
    public static final int CHAIN_CONNECTION = 4;
    public static final int DIRECT_CONNECTION = 1;
    public static final int MATCH_CONNECTION = 3;
    public static final int UNCONNECTED = 0;
    float computedValue;
    ConstraintAnchor myAnchor;
    float offset;
    private ResolutionAnchor opposite;
    private float oppositeOffset;
    float resolvedOffset;
    ResolutionAnchor resolvedTarget;
    ResolutionAnchor target;
    int type = 0;
    private ResolutionDimension dimension = null;
    private int dimensionMultiplier = 1;
    private ResolutionDimension oppositeDimension = null;
    private int oppositeDimensionMultiplier = 1;

    public ResolutionAnchor(ConstraintAnchor constraintAnchor) {
        this.myAnchor = constraintAnchor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addResolvedValue(LinearSystem linearSystem) {
        SolverVariable solverVariable = this.myAnchor.getSolverVariable();
        ResolutionAnchor resolutionAnchor = this.resolvedTarget;
        if (resolutionAnchor == null) {
            linearSystem.addEquality(solverVariable, (int) (this.resolvedOffset + 0.5f));
        } else {
            linearSystem.addEquality(solverVariable, linearSystem.createObjectVariable(resolutionAnchor.myAnchor), (int) (this.resolvedOffset + 0.5f), 6);
        }
    }

    public void dependsOn(int i, ResolutionAnchor resolutionAnchor, int i2) {
        this.type = i;
        this.target = resolutionAnchor;
        this.offset = i2;
        this.target.addDependent(this);
    }

    public void dependsOn(ResolutionAnchor resolutionAnchor, int i) {
        this.target = resolutionAnchor;
        this.offset = i;
        this.target.addDependent(this);
    }

    public void dependsOn(ResolutionAnchor resolutionAnchor, int i, ResolutionDimension resolutionDimension) {
        this.target = resolutionAnchor;
        this.target.addDependent(this);
        this.dimension = resolutionDimension;
        this.dimensionMultiplier = i;
        this.dimension.addDependent(this);
    }

    public float getResolvedValue() {
        return this.resolvedOffset;
    }

    @Override // android.support.constraint.solver.widgets.ResolutionNode
    public void remove(ResolutionDimension resolutionDimension) {
        ResolutionDimension resolutionDimension2 = this.dimension;
        if (resolutionDimension2 == resolutionDimension) {
            this.dimension = null;
            this.offset = this.dimensionMultiplier;
        } else if (resolutionDimension2 == this.oppositeDimension) {
            this.oppositeDimension = null;
            this.oppositeOffset = this.oppositeDimensionMultiplier;
        }
        resolve();
    }

    @Override // android.support.constraint.solver.widgets.ResolutionNode
    public void reset() {
        super.reset();
        this.target = null;
        this.offset = 0.0f;
        this.dimension = null;
        this.dimensionMultiplier = 1;
        this.oppositeDimension = null;
        this.oppositeDimensionMultiplier = 1;
        this.resolvedTarget = null;
        this.resolvedOffset = 0.0f;
        this.computedValue = 0.0f;
        this.opposite = null;
        this.oppositeOffset = 0.0f;
        this.type = 0;
    }

    @Override // android.support.constraint.solver.widgets.ResolutionNode
    public void resolve() {
        ResolutionAnchor resolutionAnchor;
        ResolutionAnchor resolutionAnchor2;
        ResolutionAnchor resolutionAnchor3;
        ResolutionAnchor resolutionAnchor4;
        ResolutionAnchor resolutionAnchor5;
        ResolutionAnchor resolutionAnchor6;
        float width;
        float f;
        ResolutionAnchor resolutionAnchor7;
        if (this.state == 1 || this.type == 4) {
            return;
        }
        ResolutionDimension resolutionDimension = this.dimension;
        if (resolutionDimension != null) {
            if (resolutionDimension.state != 1) {
                return;
            }
            this.offset = this.dimensionMultiplier * this.dimension.value;
        }
        ResolutionDimension resolutionDimension2 = this.oppositeDimension;
        if (resolutionDimension2 != null) {
            if (resolutionDimension2.state != 1) {
                return;
            }
            this.oppositeOffset = this.oppositeDimensionMultiplier * this.oppositeDimension.value;
        }
        if (this.type == 1 && ((resolutionAnchor7 = this.target) == null || resolutionAnchor7.state == 1)) {
            ResolutionAnchor resolutionAnchor8 = this.target;
            if (resolutionAnchor8 == null) {
                this.resolvedTarget = this;
                this.resolvedOffset = this.offset;
            } else {
                this.resolvedTarget = resolutionAnchor8.resolvedTarget;
                this.resolvedOffset = resolutionAnchor8.resolvedOffset + this.offset;
            }
            didResolve();
        } else if (this.type != 2 || (resolutionAnchor4 = this.target) == null || resolutionAnchor4.state != 1 || (resolutionAnchor5 = this.opposite) == null || (resolutionAnchor6 = resolutionAnchor5.target) == null || resolutionAnchor6.state != 1) {
            if (this.type != 3 || (resolutionAnchor = this.target) == null || resolutionAnchor.state != 1 || (resolutionAnchor2 = this.opposite) == null || (resolutionAnchor3 = resolutionAnchor2.target) == null || resolutionAnchor3.state != 1) {
                if (this.type == 5) {
                    this.myAnchor.mOwner.resolve();
                    return;
                }
                return;
            }
            if (LinearSystem.getMetrics() != null) {
                LinearSystem.getMetrics().matchConnectionResolved++;
            }
            ResolutionAnchor resolutionAnchor9 = this.target;
            this.resolvedTarget = resolutionAnchor9.resolvedTarget;
            ResolutionAnchor resolutionAnchor10 = this.opposite;
            ResolutionAnchor resolutionAnchor11 = resolutionAnchor10.target;
            resolutionAnchor10.resolvedTarget = resolutionAnchor11.resolvedTarget;
            this.resolvedOffset = resolutionAnchor9.resolvedOffset + this.offset;
            resolutionAnchor10.resolvedOffset = resolutionAnchor11.resolvedOffset + resolutionAnchor10.offset;
            didResolve();
            this.opposite.didResolve();
        } else {
            if (LinearSystem.getMetrics() != null) {
                LinearSystem.getMetrics().centerConnectionResolved++;
            }
            this.resolvedTarget = this.target.resolvedTarget;
            ResolutionAnchor resolutionAnchor12 = this.opposite;
            resolutionAnchor12.resolvedTarget = resolutionAnchor12.target.resolvedTarget;
            boolean z = true;
            if (this.myAnchor.mType != ConstraintAnchor.Type.RIGHT) {
                z = this.myAnchor.mType == ConstraintAnchor.Type.BOTTOM;
            }
            float f2 = z ? this.target.resolvedOffset - this.opposite.target.resolvedOffset : this.opposite.target.resolvedOffset - this.target.resolvedOffset;
            if (this.myAnchor.mType == ConstraintAnchor.Type.LEFT || this.myAnchor.mType == ConstraintAnchor.Type.RIGHT) {
                width = f2 - this.myAnchor.mOwner.getWidth();
                f = this.myAnchor.mOwner.mHorizontalBiasPercent;
            } else {
                width = f2 - this.myAnchor.mOwner.getHeight();
                f = this.myAnchor.mOwner.mVerticalBiasPercent;
            }
            int margin = this.myAnchor.getMargin();
            int margin2 = this.opposite.myAnchor.getMargin();
            if (this.myAnchor.getTarget() == this.opposite.myAnchor.getTarget()) {
                f = 0.5f;
                margin = 0;
                margin2 = 0;
            }
            float f3 = (width - margin) - margin2;
            if (z) {
                ResolutionAnchor resolutionAnchor13 = this.opposite;
                resolutionAnchor13.resolvedOffset = resolutionAnchor13.target.resolvedOffset + margin2 + (f3 * f);
                this.resolvedOffset = (this.target.resolvedOffset - margin) - ((1.0f - f) * f3);
            } else {
                this.resolvedOffset = this.target.resolvedOffset + margin + (f3 * f);
                ResolutionAnchor resolutionAnchor14 = this.opposite;
                resolutionAnchor14.resolvedOffset = (resolutionAnchor14.target.resolvedOffset - margin2) - ((1.0f - f) * f3);
            }
            didResolve();
            this.opposite.didResolve();
        }
    }

    public void resolve(ResolutionAnchor resolutionAnchor, float f) {
        if (this.state == 0 || !(this.resolvedTarget == resolutionAnchor || this.resolvedOffset == f)) {
            this.resolvedTarget = resolutionAnchor;
            this.resolvedOffset = f;
            if (this.state == 1) {
                invalidate();
            }
            didResolve();
        }
    }

    String sType(int i) {
        return i == 1 ? "DIRECT" : i == 2 ? "CENTER" : i == 3 ? "MATCH" : i == 4 ? "CHAIN" : i == 5 ? "BARRIER" : "UNCONNECTED";
    }

    public void setOpposite(ResolutionAnchor resolutionAnchor, float f) {
        this.opposite = resolutionAnchor;
        this.oppositeOffset = f;
    }

    public void setOpposite(ResolutionAnchor resolutionAnchor, int i, ResolutionDimension resolutionDimension) {
        this.opposite = resolutionAnchor;
        this.oppositeDimension = resolutionDimension;
        this.oppositeDimensionMultiplier = i;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String toString() {
        if (this.state != 1) {
            return "{ " + this.myAnchor + " UNRESOLVED} type: " + sType(this.type);
        } else if (this.resolvedTarget == this) {
            return "[" + this.myAnchor + ", RESOLVED: " + this.resolvedOffset + "]  type: " + sType(this.type);
        } else {
            return "[" + this.myAnchor + ", RESOLVED: " + this.resolvedTarget + Separators.COLON + this.resolvedOffset + "] type: " + sType(this.type);
        }
    }

    public void update() {
        ConstraintAnchor target = this.myAnchor.getTarget();
        if (target == null) {
            return;
        }
        if (target.getTarget() == this.myAnchor) {
            this.type = 4;
            target.getResolutionNode().type = 4;
        }
        int margin = this.myAnchor.getMargin();
        if (this.myAnchor.mType == ConstraintAnchor.Type.RIGHT || this.myAnchor.mType == ConstraintAnchor.Type.BOTTOM) {
            margin = -margin;
        }
        dependsOn(target.getResolutionNode(), margin);
    }
}