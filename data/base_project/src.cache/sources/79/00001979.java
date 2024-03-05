package com.android.internal.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.android.internal.R;

/* loaded from: ChooserActivity.class */
public class ChooserActivity extends ResolverActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.app.ResolverActivity, com.android.internal.app.AlertActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Parcelable targetParcelable = intent.getParcelableExtra(Intent.EXTRA_INTENT);
        if (!(targetParcelable instanceof Intent)) {
            Log.w("ChooseActivity", "Target is not an intent: " + targetParcelable);
            finish();
            return;
        }
        Intent target = (Intent) targetParcelable;
        CharSequence title = intent.getCharSequenceExtra(Intent.EXTRA_TITLE);
        if (title == null) {
            title = getResources().getText(R.string.chooseActivity);
        }
        Parcelable[] pa = intent.getParcelableArrayExtra(Intent.EXTRA_INITIAL_INTENTS);
        Intent[] initialIntents = null;
        if (pa != null) {
            initialIntents = new Intent[pa.length];
            for (int i = 0; i < pa.length; i++) {
                if (!(pa[i] instanceof Intent)) {
                    Log.w("ChooseActivity", "Initial intent #" + i + " not an Intent: " + pa[i]);
                    finish();
                    return;
                }
                initialIntents[i] = (Intent) pa[i];
            }
        }
        super.onCreate(savedInstanceState, target, title, initialIntents, null, false);
    }
}