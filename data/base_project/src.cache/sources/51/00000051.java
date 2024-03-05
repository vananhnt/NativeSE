package android.accounts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.android.internal.R;

/* loaded from: CantAddAccountActivity.class */
public class CantAddAccountActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_not_authorized);
    }

    public void onCancelButtonClicked(View view) {
        onBackPressed();
    }
}