package android.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.R;
import com.android.server.content.SyncStorageEngine;
import java.util.HashMap;

/* loaded from: ChooseAccountActivity.class */
public class ChooseAccountActivity extends Activity {
    private static final String TAG = "AccountManager";
    private Bundle mResult;
    private Parcelable[] mAccounts = null;
    private AccountManagerResponse mAccountManagerResponse = null;
    private HashMap<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap<>();

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAccounts = getIntent().getParcelableArrayExtra(AccountManager.KEY_ACCOUNTS);
        this.mAccountManagerResponse = (AccountManagerResponse) getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_MANAGER_RESPONSE);
        if (this.mAccounts == null) {
            setResult(0);
            finish();
            return;
        }
        getAuthDescriptions();
        AccountInfo[] mAccountInfos = new AccountInfo[this.mAccounts.length];
        for (int i = 0; i < this.mAccounts.length; i++) {
            mAccountInfos[i] = new AccountInfo(((Account) this.mAccounts[i]).name, getDrawableForType(((Account) this.mAccounts[i]).type));
        }
        setContentView(R.layout.choose_account);
        ListView list = (ListView) findViewById(16908298);
        list.setAdapter((ListAdapter) new AccountArrayAdapter(this, 17367043, mAccountInfos));
        list.setChoiceMode(1);
        list.setTextFilterEnabled(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.accounts.ChooseAccountActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ChooseAccountActivity.this.onListItemClick((ListView) parent, v, position, id);
            }
        });
    }

    private void getAuthDescriptions() {
        AuthenticatorDescription[] arr$ = AccountManager.get(this).getAuthenticatorTypes();
        for (AuthenticatorDescription desc : arr$) {
            this.mTypeToAuthDescription.put(desc.type, desc);
        }
    }

    private Drawable getDrawableForType(String accountType) {
        Drawable icon = null;
        if (this.mTypeToAuthDescription.containsKey(accountType)) {
            try {
                AuthenticatorDescription desc = this.mTypeToAuthDescription.get(accountType);
                Context authContext = createPackageContext(desc.packageName, 0);
                icon = authContext.getResources().getDrawable(desc.iconId);
            } catch (PackageManager.NameNotFoundException e) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "No icon name for account type " + accountType);
                }
            } catch (Resources.NotFoundException e2) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "No icon resource for account type " + accountType);
                }
            }
        }
        return icon;
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Account account = (Account) this.mAccounts[position];
        Log.d(TAG, "selected account " + account);
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString("accountType", account.type);
        this.mResult = bundle;
        finish();
    }

    @Override // android.app.Activity
    public void finish() {
        if (this.mAccountManagerResponse != null) {
            if (this.mResult != null) {
                this.mAccountManagerResponse.onResult(this.mResult);
            } else {
                this.mAccountManagerResponse.onError(4, SyncStorageEngine.MESG_CANCELED);
            }
        }
        super.finish();
    }

    /* loaded from: ChooseAccountActivity$AccountInfo.class */
    private static class AccountInfo {
        final String name;
        final Drawable drawable;

        AccountInfo(String name, Drawable drawable) {
            this.name = name;
            this.drawable = drawable;
        }
    }

    /* loaded from: ChooseAccountActivity$ViewHolder.class */
    private static class ViewHolder {
        ImageView icon;
        TextView text;

        private ViewHolder() {
        }
    }

    /* loaded from: ChooseAccountActivity$AccountArrayAdapter.class */
    private static class AccountArrayAdapter extends ArrayAdapter<AccountInfo> {
        private LayoutInflater mLayoutInflater;
        private AccountInfo[] mInfos;

        public AccountArrayAdapter(Context context, int textViewResourceId, AccountInfo[] infos) {
            super(context, textViewResourceId, infos);
            this.mInfos = infos;
            this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = this.mLayoutInflater.inflate(R.layout.choose_account_row, (ViewGroup) null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.account_row_text);
                holder.icon = (ImageView) convertView.findViewById(R.id.account_row_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(this.mInfos[position].name);
            holder.icon.setImageDrawable(this.mInfos[position].drawable);
            return convertView;
        }
    }
}