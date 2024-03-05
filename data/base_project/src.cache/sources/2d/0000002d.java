package android.accounts;

import android.app.Activity;
import android.os.Bundle;
import com.android.server.content.SyncStorageEngine;

/* loaded from: AccountAuthenticatorActivity.class */
public class AccountAuthenticatorActivity extends Activity {
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    public final void setAccountAuthenticatorResult(Bundle result) {
        this.mResultBundle = result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mAccountAuthenticatorResponse = (AccountAuthenticatorResponse) getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (this.mAccountAuthenticatorResponse != null) {
            this.mAccountAuthenticatorResponse.onRequestContinued();
        }
    }

    @Override // android.app.Activity
    public void finish() {
        if (this.mAccountAuthenticatorResponse != null) {
            if (this.mResultBundle != null) {
                this.mAccountAuthenticatorResponse.onResult(this.mResultBundle);
            } else {
                this.mAccountAuthenticatorResponse.onError(4, SyncStorageEngine.MESG_CANCELED);
            }
            this.mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}