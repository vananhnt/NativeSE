package android.filterfw;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterFunction;
import android.filterfw.core.FrameManager;
import gov.nist.core.Separators;

/* loaded from: FilterFunctionEnvironment.class */
public class FilterFunctionEnvironment extends MffEnvironment {
    public FilterFunctionEnvironment() {
        super(null);
    }

    public FilterFunctionEnvironment(FrameManager frameManager) {
        super(frameManager);
    }

    public FilterFunction createFunction(Class filterClass, Object... parameters) {
        String filterName = "FilterFunction(" + filterClass.getSimpleName() + Separators.RPAREN;
        Filter filter = FilterFactory.sharedFactory().createFilterByClass(filterClass, filterName);
        filter.initWithAssignmentList(parameters);
        return new FilterFunction(getContext(), filter);
    }
}