package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import android.support.constraint.solver.SolverVariable;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.HashSet;

/* loaded from: ConstraintAnchor.class */
public class ConstraintAnchor {
    private static final boolean ALLOW_BINARY = false;
    public static final int AUTO_CONSTRAINT_CREATOR = 2;
    public static final int SCOUT_CREATOR = 1;
    private static final int UNSET_GONE_MARGIN = -1;
    public static final int USER_CREATOR = 0;
    final ConstraintWidget mOwner;
    SolverVariable mSolverVariable;
    ConstraintAnchor mTarget;
    final Type mType;
    private ResolutionAnchor mResolutionAnchor = new ResolutionAnchor(this);
    public int mMargin = 0;
    int mGoneMargin = -1;
    private Strength mStrength = Strength.NONE;
    private ConnectionType mConnectionType = ConnectionType.RELAXED;
    private int mConnectionCreator = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.support.constraint.solver.widgets.ConstraintAnchor$1  reason: invalid class name */
    /* loaded from: ConstraintAnchor$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final int[] $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.TOP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.BOTTOM.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.BASELINE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.CENTER_X.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.CENTER_Y.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[Type.NONE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    /* loaded from: ConstraintAnchor$ConnectionType.class */
    public enum ConnectionType {
        RELAXED,
        STRICT
    }

    /* loaded from: ConstraintAnchor$Strength.class */
    public enum Strength {
        NONE,
        STRONG,
        WEAK
    }

    /* loaded from: ConstraintAnchor$Type.class */
    public enum Type {
        NONE,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        BASELINE,
        CENTER,
        CENTER_X,
        CENTER_Y
    }

    public ConstraintAnchor(ConstraintWidget constraintWidget, Type type) {
        this.mOwner = constraintWidget;
        this.mType = type;
    }

    private boolean isConnectionToMe(ConstraintWidget constraintWidget, HashSet<ConstraintWidget> hashSet) {
        if (hashSet.contains(constraintWidget)) {
            return false;
        }
        hashSet.add(constraintWidget);
        if (constraintWidget == getOwner()) {
            return true;
        }
        ArrayList<ConstraintAnchor> anchors = constraintWidget.getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = anchors.get(i);
            if (constraintAnchor.isSimilarDimensionConnection(this) && constraintAnchor.isConnected() && isConnectionToMe(constraintAnchor.getTarget().getOwner(), hashSet)) {
                return true;
            }
        }
        return false;
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i) {
        return connect(constraintAnchor, i, -1, Strength.STRONG, 0, false);
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, int i2) {
        return connect(constraintAnchor, i, -1, Strength.STRONG, i2, false);
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, int i2, Strength strength, int i3, boolean z) {
        if (constraintAnchor == null) {
            this.mTarget = null;
            this.mMargin = 0;
            this.mGoneMargin = -1;
            this.mStrength = Strength.NONE;
            this.mConnectionCreator = 2;
            return true;
        } else if (z || isValidConnection(constraintAnchor)) {
            this.mTarget = constraintAnchor;
            if (i > 0) {
                this.mMargin = i;
            } else {
                this.mMargin = 0;
            }
            this.mGoneMargin = i2;
            this.mStrength = strength;
            this.mConnectionCreator = i3;
            return true;
        } else {
            return false;
        }
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, Strength strength, int i2) {
        return connect(constraintAnchor, i, -1, strength, i2, false);
    }

    public int getConnectionCreator() {
        return this.mConnectionCreator;
    }

    public ConnectionType getConnectionType() {
        return this.mConnectionType;
    }

    public int getMargin() {
        ConstraintAnchor constraintAnchor;
        if (this.mOwner.getVisibility() == 8) {
            return 0;
        }
        return (this.mGoneMargin <= -1 || (constraintAnchor = this.mTarget) == null || constraintAnchor.mOwner.getVisibility() != 8) ? this.mMargin : this.mGoneMargin;
    }

    public final ConstraintAnchor getOpposite() {
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
                return null;
            case 2:
                return this.mOwner.mRight;
            case 3:
                return this.mOwner.mLeft;
            case 4:
                return this.mOwner.mBottom;
            case 5:
                return this.mOwner.mTop;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public ConstraintWidget getOwner() {
        return this.mOwner;
    }

    public int getPriorityLevel() {
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                return 2;
            case 2:
                return 2;
            case 3:
                return 2;
            case 4:
                return 2;
            case 5:
                return 2;
            case 6:
                return 1;
            case 7:
                return 0;
            case 8:
                return 0;
            case 9:
                return 0;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public ResolutionAnchor getResolutionNode() {
        return this.mResolutionAnchor;
    }

    public int getSnapPriorityLevel() {
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                return 3;
            case 2:
                return 1;
            case 3:
                return 1;
            case 4:
                return 0;
            case 5:
                return 0;
            case 6:
                return 2;
            case 7:
                return 0;
            case 8:
                return 1;
            case 9:
                return 0;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public SolverVariable getSolverVariable() {
        return this.mSolverVariable;
    }

    public Strength getStrength() {
        return this.mStrength;
    }

    public ConstraintAnchor getTarget() {
        return this.mTarget;
    }

    public Type getType() {
        return this.mType;
    }

    public boolean isConnected() {
        return this.mTarget != null;
    }

    public boolean isConnectionAllowed(ConstraintWidget constraintWidget) {
        if (isConnectionToMe(constraintWidget, new HashSet<>())) {
            return false;
        }
        ConstraintWidget parent = getOwner().getParent();
        return parent == constraintWidget || constraintWidget.getParent() == parent;
    }

    public boolean isConnectionAllowed(ConstraintWidget constraintWidget, ConstraintAnchor constraintAnchor) {
        return isConnectionAllowed(constraintWidget);
    }

    public boolean isSideAnchor() {
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
                return false;
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isSimilarDimensionConnection(ConstraintAnchor constraintAnchor) {
        Type type = constraintAnchor.getType();
        boolean z = true;
        if (type == this.mType) {
            return true;
        }
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                if (type == Type.BASELINE) {
                    z = false;
                }
                return z;
            case 2:
            case 3:
            case 7:
                boolean z2 = true;
                if (type != Type.LEFT) {
                    z2 = true;
                    if (type != Type.RIGHT) {
                        z2 = type == Type.CENTER_X;
                    }
                }
                return z2;
            case 4:
            case 5:
            case 6:
            case 8:
                boolean z3 = true;
                if (type != Type.TOP) {
                    z3 = true;
                    if (type != Type.BOTTOM) {
                        z3 = true;
                        if (type != Type.CENTER_Y) {
                            z3 = type == Type.BASELINE;
                        }
                    }
                }
                return z3;
            case 9:
                return false;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isSnapCompatibleWith(ConstraintAnchor constraintAnchor) {
        if (this.mType == Type.CENTER) {
            return false;
        }
        if (this.mType == constraintAnchor.getType()) {
            return true;
        }
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 6:
            case 9:
                return false;
            case 2:
                int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i == 3 || i == 7;
            case 3:
                int i2 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i2 == 2 || i2 == 7;
            case 4:
                int i3 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i3 == 5 || i3 == 8;
            case 5:
                int i4 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i4 == 4 || i4 == 8;
            case 7:
                switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()]) {
                    case 2:
                        return true;
                    case 3:
                        return true;
                    default:
                        return false;
                }
            case 8:
                switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()]) {
                    case 4:
                        return true;
                    case 5:
                        return true;
                    default:
                        return false;
                }
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isValidConnection(ConstraintAnchor constraintAnchor) {
        if (constraintAnchor == null) {
            return false;
        }
        Type type = constraintAnchor.getType();
        Type type2 = this.mType;
        if (type == type2) {
            if (type2 == Type.BASELINE) {
                return constraintAnchor.getOwner().hasBaseline() && getOwner().hasBaseline();
            }
            return true;
        }
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                boolean z = false;
                if (type != Type.BASELINE) {
                    z = false;
                    if (type != Type.CENTER_X) {
                        z = false;
                        if (type != Type.CENTER_Y) {
                            z = true;
                        }
                    }
                }
                return z;
            case 2:
            case 3:
                boolean z2 = type == Type.LEFT || type == Type.RIGHT;
                if (constraintAnchor.getOwner() instanceof Guideline) {
                    z2 = z2 || type == Type.CENTER_X;
                }
                return z2;
            case 4:
            case 5:
                boolean z3 = type == Type.TOP || type == Type.BOTTOM;
                if (constraintAnchor.getOwner() instanceof Guideline) {
                    z3 = z3 || type == Type.CENTER_Y;
                }
                return z3;
            case 6:
            case 7:
            case 8:
            case 9:
                return false;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isVerticalAnchor() {
        switch (AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 7:
                return false;
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
                return true;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public void reset() {
        this.mTarget = null;
        this.mMargin = 0;
        this.mGoneMargin = -1;
        this.mStrength = Strength.STRONG;
        this.mConnectionCreator = 0;
        this.mConnectionType = ConnectionType.RELAXED;
        this.mResolutionAnchor.reset();
    }

    public void resetSolverVariable(Cache cache) {
        SolverVariable solverVariable = this.mSolverVariable;
        if (solverVariable == null) {
            this.mSolverVariable = new SolverVariable(SolverVariable.Type.UNRESTRICTED, (String) null);
        } else {
            solverVariable.reset();
        }
    }

    public void setConnectionCreator(int i) {
        this.mConnectionCreator = i;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.mConnectionType = connectionType;
    }

    public void setGoneMargin(int i) {
        if (isConnected()) {
            this.mGoneMargin = i;
        }
    }

    public void setMargin(int i) {
        if (isConnected()) {
            this.mMargin = i;
        }
    }

    public void setStrength(Strength strength) {
        if (isConnected()) {
            this.mStrength = strength;
        }
    }

    public String toString() {
        return this.mOwner.getDebugName() + Separators.COLON + this.mType.toString();
    }
}