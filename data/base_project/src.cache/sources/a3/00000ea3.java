package android.support.constraint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.R;
import android.support.constraint.solver.widgets.Helper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import gov.nist.core.Separators;
import java.util.Arrays;

/* loaded from: ConstraintHelper.class */
public abstract class ConstraintHelper extends View {
    protected int mCount;
    protected Helper mHelperWidget;
    protected int[] mIds;
    private String mReferenceIds;
    protected boolean mUseViewMeasure;
    protected Context myContext;

    public ConstraintHelper(Context context) {
        super(context);
        this.mIds = new int[32];
        this.mUseViewMeasure = false;
        this.myContext = context;
        init(null);
    }

    public ConstraintHelper(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIds = new int[32];
        this.mUseViewMeasure = false;
        this.myContext = context;
        init(attributeSet);
    }

    public ConstraintHelper(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIds = new int[32];
        this.mUseViewMeasure = false;
        this.myContext = context;
        init(attributeSet);
    }

    private void addID(String str) {
        Object designInformation;
        if (str == null || this.myContext == null) {
            return;
        }
        String trim = str.trim();
        int i = 0;
        try {
            i = R.id.class.getField(trim).getInt(null);
        } catch (Exception e) {
        }
        if (i == 0) {
            i = this.myContext.getResources().getIdentifier(trim, "id", this.myContext.getPackageName());
        }
        if (i == 0 && isInEditMode() && (getParent() instanceof ConstraintLayout) && (designInformation = ((ConstraintLayout) getParent()).getDesignInformation(0, trim)) != null && (designInformation instanceof Integer)) {
            i = ((Integer) designInformation).intValue();
        }
        if (i != 0) {
            setTag(i, null);
            return;
        }
        Log.w("ConstraintHelper", "Could not find id of \"" + trim + Separators.DOUBLE_QUOTE);
    }

    private void setIds(String str) {
        if (str == null) {
            return;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            int indexOf = str.indexOf(44, i2);
            if (indexOf == -1) {
                addID(str.substring(i2));
                return;
            } else {
                addID(str.substring(i2, indexOf));
                i = indexOf + 1;
            }
        }
    }

    public int[] getReferencedIds() {
        return Arrays.copyOf(this.mIds, this.mCount);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.ConstraintLayout_Layout);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = obtainStyledAttributes.getIndex(i);
                if (index == R.styleable.ConstraintLayout_Layout_constraint_referenced_ids) {
                    this.mReferenceIds = obtainStyledAttributes.getString(index);
                    setIds(this.mReferenceIds);
                }
            }
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        if (this.mUseViewMeasure) {
            super.onMeasure(i, i2);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    public void setReferencedIds(int[] iArr) {
        this.mCount = 0;
        for (int i : iArr) {
            setTag(i, null);
        }
    }

    @Override // android.view.View
    public void setTag(int i, Object obj) {
        int i2 = this.mCount;
        int[] iArr = this.mIds;
        if (i2 + 1 > iArr.length) {
            this.mIds = Arrays.copyOf(iArr, iArr.length * 2);
        }
        int[] iArr2 = this.mIds;
        int i3 = this.mCount;
        iArr2[i3] = i;
        this.mCount = i3 + 1;
    }

    public void updatePostLayout(ConstraintLayout constraintLayout) {
    }

    public void updatePostMeasure(ConstraintLayout constraintLayout) {
    }

    public void updatePreLayout(ConstraintLayout constraintLayout) {
        if (isInEditMode()) {
            setIds(this.mReferenceIds);
        }
        Helper helper = this.mHelperWidget;
        if (helper == null) {
            return;
        }
        helper.removeAllIds();
        for (int i = 0; i < this.mCount; i++) {
            View viewById = constraintLayout.getViewById(this.mIds[i]);
            if (viewById != null) {
                this.mHelperWidget.add(constraintLayout.getViewWidget(viewById));
            }
        }
    }

    public void validateParams() {
        if (this.mHelperWidget == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ConstraintLayout.LayoutParams) {
            ((ConstraintLayout.LayoutParams) layoutParams).widget = this.mHelperWidget;
        }
    }
}