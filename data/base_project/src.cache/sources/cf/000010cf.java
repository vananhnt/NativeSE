package android.support.v4.view;

import android.content.Context;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;

/* loaded from: PagerTitleStripIcs.class */
class PagerTitleStripIcs {

    /* loaded from: PagerTitleStripIcs$SingleLineAllCapsTransform.class */
    private static class SingleLineAllCapsTransform extends SingleLineTransformationMethod {
        private static final String TAG = "SingleLineAllCapsTransform";
        private Locale mLocale;

        public SingleLineAllCapsTransform(Context context) {
            this.mLocale = context.getResources().getConfiguration().locale;
        }

        @Override // android.text.method.ReplacementTransformationMethod, android.text.method.TransformationMethod
        public CharSequence getTransformation(CharSequence charSequence, View view) {
            CharSequence transformation = super.getTransformation(charSequence, view);
            return transformation != null ? transformation.toString().toUpperCase(this.mLocale) : null;
        }
    }

    PagerTitleStripIcs() {
    }

    public static void setSingleLineAllCaps(TextView textView) {
        textView.setTransformationMethod(new SingleLineAllCapsTransform(textView.getContext()));
    }
}