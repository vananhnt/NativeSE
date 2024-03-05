package android.transition;

import android.util.ArrayMap;
import android.view.View;
import gov.nist.core.Separators;
import java.util.Map;

/* loaded from: TransitionValues.class */
public class TransitionValues {
    public View view;
    public final Map<String, Object> values = new ArrayMap();

    public boolean equals(Object other) {
        if ((other instanceof TransitionValues) && this.view == ((TransitionValues) other).view && this.values.equals(((TransitionValues) other).values)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (31 * this.view.hashCode()) + this.values.hashCode();
    }

    public String toString() {
        String returnValue = "TransitionValues@" + Integer.toHexString(hashCode()) + ":\n";
        String returnValue2 = (returnValue + "    view = " + this.view + Separators.RETURN) + "    values:";
        for (String s : this.values.keySet()) {
            returnValue2 = returnValue2 + "    " + s + ": " + this.values.get(s) + Separators.RETURN;
        }
        return returnValue2;
    }
}