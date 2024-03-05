package android.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: SearchDialog.class */
public class SearchDialog extends Dialog {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "SearchDialog";
    private static final String INSTANCE_KEY_COMPONENT = "comp";
    private static final String INSTANCE_KEY_APPDATA = "data";
    private static final String INSTANCE_KEY_USER_QUERY = "uQry";
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private static final int SEARCH_PLATE_LEFT_PADDING_NON_GLOBAL = 7;
    private TextView mBadgeLabel;
    private ImageView mAppIcon;
    private AutoCompleteTextView mSearchAutoComplete;
    private View mSearchPlate;
    private SearchView mSearchView;
    private Drawable mWorkingSpinner;
    private View mCloseSearch;
    private SearchableInfo mSearchable;
    private ComponentName mLaunchComponent;
    private Bundle mAppSearchData;
    private Context mActivityContext;
    private final Intent mVoiceWebSearchIntent;
    private final Intent mVoiceAppSearchIntent;
    private String mUserQuery;
    private int mSearchAutoCompleteImeOptions;
    private BroadcastReceiver mConfChangeListener;
    private final SearchView.OnCloseListener mOnCloseListener;
    private final SearchView.OnQueryTextListener mOnQueryChangeListener;
    private final SearchView.OnSuggestionListener mOnSuggestionSelectionListener;

    static int resolveDialogTheme(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.searchDialogTheme, outValue, true);
        return outValue.resourceId;
    }

    public SearchDialog(Context context, SearchManager searchManager) {
        super(context, resolveDialogTheme(context));
        this.mConfChangeListener = new BroadcastReceiver() { // from class: android.app.SearchDialog.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
                    SearchDialog.this.onConfigurationChanged();
                }
            }
        };
        this.mOnCloseListener = new SearchView.OnCloseListener() { // from class: android.app.SearchDialog.3
            @Override // android.widget.SearchView.OnCloseListener
            public boolean onClose() {
                return SearchDialog.this.onClosePressed();
            }
        };
        this.mOnQueryChangeListener = new SearchView.OnQueryTextListener() { // from class: android.app.SearchDialog.4
            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String query) {
                SearchDialog.this.dismiss();
                return false;
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        this.mOnSuggestionSelectionListener = new SearchView.OnSuggestionListener() { // from class: android.app.SearchDialog.5
            @Override // android.widget.SearchView.OnSuggestionListener
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override // android.widget.SearchView.OnSuggestionListener
            public boolean onSuggestionClick(int position) {
                SearchDialog.this.dismiss();
                return false;
            }
        };
        this.mVoiceWebSearchIntent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        this.mVoiceWebSearchIntent.addFlags(268435456);
        this.mVoiceWebSearchIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        this.mVoiceAppSearchIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mVoiceAppSearchIntent.addFlags(268435456);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window theWindow = getWindow();
        WindowManager.LayoutParams lp = theWindow.getAttributes();
        lp.width = -1;
        lp.height = -1;
        lp.gravity = 55;
        lp.softInputMode = 16;
        theWindow.setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    private void createContentView() {
        setContentView(R.layout.search_bar);
        SearchBar searchBar = (SearchBar) findViewById(R.id.search_bar);
        searchBar.setSearchDialog(this);
        this.mSearchView = (SearchView) findViewById(R.id.search_view);
        this.mSearchView.setIconified(false);
        this.mSearchView.setOnCloseListener(this.mOnCloseListener);
        this.mSearchView.setOnQueryTextListener(this.mOnQueryChangeListener);
        this.mSearchView.setOnSuggestionListener(this.mOnSuggestionSelectionListener);
        this.mSearchView.onActionViewExpanded();
        this.mCloseSearch = findViewById(16908327);
        this.mCloseSearch.setOnClickListener(new View.OnClickListener() { // from class: android.app.SearchDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SearchDialog.this.dismiss();
            }
        });
        this.mBadgeLabel = (TextView) this.mSearchView.findViewById(R.id.search_badge);
        this.mSearchAutoComplete = (AutoCompleteTextView) this.mSearchView.findViewById(R.id.search_src_text);
        this.mAppIcon = (ImageView) findViewById(R.id.search_app_icon);
        this.mSearchPlate = this.mSearchView.findViewById(R.id.search_plate);
        this.mWorkingSpinner = getContext().getResources().getDrawable(R.drawable.search_spinner);
        setWorking(false);
        this.mBadgeLabel.setVisibility(8);
        this.mSearchAutoCompleteImeOptions = this.mSearchAutoComplete.getImeOptions();
    }

    public boolean show(String initialQuery, boolean selectInitialQuery, ComponentName componentName, Bundle appSearchData) {
        boolean success = doShow(initialQuery, selectInitialQuery, componentName, appSearchData);
        if (success) {
            this.mSearchAutoComplete.showDropDownAfterLayout();
        }
        return success;
    }

    private boolean doShow(String initialQuery, boolean selectInitialQuery, ComponentName componentName, Bundle appSearchData) {
        if (!show(componentName, appSearchData)) {
            return false;
        }
        setUserQuery(initialQuery);
        if (selectInitialQuery) {
            this.mSearchAutoComplete.selectAll();
            return true;
        }
        return true;
    }

    private boolean show(ComponentName componentName, Bundle appSearchData) {
        SearchManager searchManager = (SearchManager) this.mContext.getSystemService("search");
        this.mSearchable = searchManager.getSearchableInfo(componentName);
        if (this.mSearchable == null) {
            return false;
        }
        this.mLaunchComponent = componentName;
        this.mAppSearchData = appSearchData;
        this.mActivityContext = this.mSearchable.getActivityContext(getContext());
        if (!isShowing()) {
            createContentView();
            this.mSearchView.setSearchableInfo(this.mSearchable);
            this.mSearchView.setAppSearchData(this.mAppSearchData);
            show();
        }
        updateUI();
        return true;
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        getContext().registerReceiver(this.mConfChangeListener, filter);
    }

    @Override // android.app.Dialog
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(this.mConfChangeListener);
        this.mLaunchComponent = null;
        this.mAppSearchData = null;
        this.mSearchable = null;
        this.mUserQuery = null;
    }

    public void setWorking(boolean working) {
        this.mWorkingSpinner.setAlpha(working ? 255 : 0);
        this.mWorkingSpinner.setVisible(working, false);
        this.mWorkingSpinner.invalidateSelf();
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        if (isShowing()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(INSTANCE_KEY_COMPONENT, this.mLaunchComponent);
            bundle.putBundle("data", this.mAppSearchData);
            bundle.putString(INSTANCE_KEY_USER_QUERY, this.mUserQuery);
            return bundle;
        }
        return null;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        ComponentName launchComponent = (ComponentName) savedInstanceState.getParcelable(INSTANCE_KEY_COMPONENT);
        Bundle appSearchData = savedInstanceState.getBundle("data");
        String userQuery = savedInstanceState.getString(INSTANCE_KEY_USER_QUERY);
        if (!doShow(userQuery, false, launchComponent, appSearchData)) {
        }
    }

    public void onConfigurationChanged() {
        if (this.mSearchable != null && isShowing()) {
            updateSearchAppIcon();
            updateSearchBadge();
            if (isLandscapeMode(getContext())) {
                this.mSearchAutoComplete.ensureImeVisible(true);
            }
        }
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    private void updateUI() {
        if (this.mSearchable != null) {
            this.mDecor.setVisibility(0);
            updateSearchAutoComplete();
            updateSearchAppIcon();
            updateSearchBadge();
            int inputType = this.mSearchable.getInputType();
            if ((inputType & 15) == 1) {
                inputType &= -65537;
                if (this.mSearchable.getSuggestAuthority() != null) {
                    inputType |= 65536;
                }
            }
            this.mSearchAutoComplete.setInputType(inputType);
            this.mSearchAutoCompleteImeOptions = this.mSearchable.getImeOptions();
            this.mSearchAutoComplete.setImeOptions(this.mSearchAutoCompleteImeOptions);
            if (this.mSearchable.getVoiceSearchEnabled()) {
                this.mSearchAutoComplete.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
            } else {
                this.mSearchAutoComplete.setPrivateImeOptions(null);
            }
        }
    }

    private void updateSearchAutoComplete() {
        this.mSearchAutoComplete.setDropDownDismissedOnCompletion(false);
        this.mSearchAutoComplete.setForceIgnoreOutsideTouch(false);
    }

    private void updateSearchAppIcon() {
        Drawable icon;
        PackageManager pm = getContext().getPackageManager();
        try {
            ActivityInfo info = pm.getActivityInfo(this.mLaunchComponent, 0);
            icon = pm.getApplicationIcon(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            icon = pm.getDefaultActivityIcon();
            Log.w(LOG_TAG, this.mLaunchComponent + " not found, using generic app icon");
        }
        this.mAppIcon.setImageDrawable(icon);
        this.mAppIcon.setVisibility(0);
        this.mSearchPlate.setPadding(7, this.mSearchPlate.getPaddingTop(), this.mSearchPlate.getPaddingRight(), this.mSearchPlate.getPaddingBottom());
    }

    private void updateSearchBadge() {
        int visibility = 8;
        Drawable icon = null;
        CharSequence text = null;
        if (this.mSearchable.useBadgeIcon()) {
            icon = this.mActivityContext.getResources().getDrawable(this.mSearchable.getIconId());
            visibility = 0;
        } else if (this.mSearchable.useBadgeLabel()) {
            text = this.mActivityContext.getResources().getText(this.mSearchable.getLabelId()).toString();
            visibility = 0;
        }
        this.mBadgeLabel.setCompoundDrawablesWithIntrinsicBounds(icon, (Drawable) null, (Drawable) null, (Drawable) null);
        this.mBadgeLabel.setText(text);
        this.mBadgeLabel.setVisibility(visibility);
    }

    @Override // android.app.Dialog
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mSearchAutoComplete.isPopupShowing() && isOutOfBounds(this.mSearchPlate, event)) {
            cancel();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(this.mContext).getScaledWindowTouchSlop();
        return x < (-slop) || y < (-slop) || x > v.getWidth() + slop || y > v.getHeight() + slop;
    }

    @Override // android.app.Dialog
    public void hide() {
        if (isShowing()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            super.hide();
        }
    }

    public void launchQuerySearch() {
        launchQuerySearch(0, null);
    }

    protected void launchQuerySearch(int actionKey, String actionMsg) {
        String query = this.mSearchAutoComplete.getText().toString();
        Intent intent = createIntent(Intent.ACTION_SEARCH, null, null, query, actionKey, actionMsg);
        launchIntent(intent);
    }

    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        Log.d(LOG_TAG, "launching " + intent);
        try {
            getContext().startActivity(intent);
            dismiss();
        } catch (RuntimeException ex) {
            Log.e(LOG_TAG, "Failed launch activity: " + intent, ex);
        }
    }

    public void setListSelection(int index) {
        this.mSearchAutoComplete.setListSelection(index);
    }

    private Intent createIntent(String action, Uri data, String extraData, String query, int actionKey, String actionMsg) {
        Intent intent = new Intent(action);
        intent.addFlags(268435456);
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra(SearchManager.USER_QUERY, this.mUserQuery);
        if (query != null) {
            intent.putExtra("query", query);
        }
        if (extraData != null) {
            intent.putExtra(SearchManager.EXTRA_DATA_KEY, extraData);
        }
        if (this.mAppSearchData != null) {
            intent.putExtra(SearchManager.APP_DATA, this.mAppSearchData);
        }
        if (actionKey != 0) {
            intent.putExtra(SearchManager.ACTION_KEY, actionKey);
            intent.putExtra(SearchManager.ACTION_MSG, actionMsg);
        }
        intent.setComponent(this.mSearchable.getSearchActivity());
        return intent;
    }

    /* loaded from: SearchDialog$SearchBar.class */
    public static class SearchBar extends LinearLayout {
        private SearchDialog mSearchDialog;

        public SearchBar(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SearchBar(Context context) {
            super(context);
        }

        public void setSearchDialog(SearchDialog searchDialog) {
            this.mSearchDialog = searchDialog;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public ActionMode startActionModeForChild(View child, ActionMode.Callback callback) {
            return null;
        }
    }

    private boolean isEmpty(AutoCompleteTextView actv) {
        return TextUtils.getTrimmedLength(actv.getText()) == 0;
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isFullscreenMode() && imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0)) {
            return;
        }
        cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onClosePressed() {
        if (isEmpty(this.mSearchAutoComplete)) {
            dismiss();
            return true;
        }
        return false;
    }

    private void setUserQuery(String query) {
        if (query == null) {
            query = "";
        }
        this.mUserQuery = query;
        this.mSearchAutoComplete.setText(query);
        this.mSearchAutoComplete.setSelection(query.length());
    }
}