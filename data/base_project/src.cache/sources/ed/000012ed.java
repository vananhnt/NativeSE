package android.support.v7.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.speech.RecognizerIntent;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.appcompat.R;
import android.support.v7.internal.widget.TintManager;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.view.CollapsibleActionView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import gov.nist.core.Separators;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

/* loaded from: SearchView.class */
public class SearchView extends LinearLayoutCompat implements CollapsibleActionView {
    private static final boolean DBG = false;
    static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER;
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private static final boolean IS_AT_LEAST_FROYO;
    private static final String LOG_TAG = "SearchView";
    private Bundle mAppSearchData;
    private boolean mClearingFocus;
    private final ImageView mCloseButton;
    private int mCollapsedImeOptions;
    private final View mDropDownAnchor;
    private boolean mExpandedInActionView;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final View.OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final TextView.OnEditorActionListener mOnEditorActionListener;
    private final AdapterView.OnItemClickListener mOnItemClickListener;
    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private OnQueryTextListener mOnQueryChangeListener;
    private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private View.OnClickListener mOnSearchClickListener;
    private OnSuggestionListener mOnSuggestionListener;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private final SearchAutoComplete mQueryTextView;
    private Runnable mReleaseCursorRunnable;
    private final ImageView mSearchButton;
    private final View mSearchEditFrame;
    private final ImageView mSearchHintIcon;
    private final int mSearchIconResId;
    private final View mSearchPlate;
    private SearchableInfo mSearchable;
    private Runnable mShowImeRunnable;
    private final View mSubmitArea;
    private final ImageView mSubmitButton;
    private boolean mSubmitButtonEnabled;
    private final int mSuggestionCommitIconResId;
    private final int mSuggestionRowLayout;
    private CursorAdapter mSuggestionsAdapter;
    View.OnKeyListener mTextKeyListener;
    private TextWatcher mTextWatcher;
    private final TintManager mTintManager;
    private final Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    private final ImageView mVoiceButton;
    private boolean mVoiceButtonEnabled;
    private final Intent mVoiceWebSearchIntent;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SearchView$AutoCompleteTextViewReflector.class */
    public static class AutoCompleteTextViewReflector {
        private Method doAfterTextChanged;
        private Method doBeforeTextChanged;
        private Method ensureImeVisible;
        private Method showSoftInputUnchecked;

        AutoCompleteTextViewReflector() {
            try {
                this.doBeforeTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doBeforeTextChanged", new Class[0]);
                this.doBeforeTextChanged.setAccessible(true);
            } catch (NoSuchMethodException e) {
            }
            try {
                this.doAfterTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doAfterTextChanged", new Class[0]);
                this.doAfterTextChanged.setAccessible(true);
            } catch (NoSuchMethodException e2) {
            }
            try {
                this.ensureImeVisible = AutoCompleteTextView.class.getMethod("ensureImeVisible", Boolean.TYPE);
                this.ensureImeVisible.setAccessible(true);
            } catch (NoSuchMethodException e3) {
            }
            try {
                this.showSoftInputUnchecked = InputMethodManager.class.getMethod("showSoftInputUnchecked", Integer.TYPE, ResultReceiver.class);
                this.showSoftInputUnchecked.setAccessible(true);
            } catch (NoSuchMethodException e4) {
            }
        }

        void doAfterTextChanged(AutoCompleteTextView autoCompleteTextView) {
            Method method = this.doAfterTextChanged;
            if (method != null) {
                try {
                    method.invoke(autoCompleteTextView, new Object[0]);
                } catch (Exception e) {
                }
            }
        }

        void doBeforeTextChanged(AutoCompleteTextView autoCompleteTextView) {
            Method method = this.doBeforeTextChanged;
            if (method != null) {
                try {
                    method.invoke(autoCompleteTextView, new Object[0]);
                } catch (Exception e) {
                }
            }
        }

        void ensureImeVisible(AutoCompleteTextView autoCompleteTextView, boolean z) {
            Method method = this.ensureImeVisible;
            if (method != null) {
                try {
                    method.invoke(autoCompleteTextView, Boolean.valueOf(z));
                } catch (Exception e) {
                }
            }
        }

        void showSoftInputUnchecked(InputMethodManager inputMethodManager, View view, int i) {
            Method method = this.showSoftInputUnchecked;
            if (method != null) {
                try {
                    method.invoke(inputMethodManager, Integer.valueOf(i), null);
                    return;
                } catch (Exception e) {
                }
            }
            inputMethodManager.showSoftInput(view, i);
        }
    }

    /* loaded from: SearchView$OnCloseListener.class */
    public interface OnCloseListener {
        boolean onClose();
    }

    /* loaded from: SearchView$OnQueryTextListener.class */
    public interface OnQueryTextListener {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    /* loaded from: SearchView$OnSuggestionListener.class */
    public interface OnSuggestionListener {
        boolean onSuggestionClick(int i);

        boolean onSuggestionSelect(int i);
    }

    /* loaded from: SearchView$SearchAutoComplete.class */
    public static class SearchAutoComplete extends AutoCompleteTextView {
        private final int[] POPUP_WINDOW_ATTRS;
        private SearchView mSearchView;
        private int mThreshold;
        private final TintManager mTintManager;

        public SearchAutoComplete(Context context) {
            this(context, null);
        }

        public SearchAutoComplete(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 16842859);
        }

        public SearchAutoComplete(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.POPUP_WINDOW_ATTRS = new int[]{16843126};
            this.mThreshold = getThreshold();
            TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, this.POPUP_WINDOW_ATTRS, i, 0);
            if (obtainStyledAttributes.hasValue(0)) {
                setDropDownBackgroundDrawable(obtainStyledAttributes.getDrawable(0));
            }
            obtainStyledAttributes.recycle();
            this.mTintManager = obtainStyledAttributes.getTintManager();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEmpty() {
            return TextUtils.getTrimmedLength(getText()) == 0;
        }

        @Override // android.widget.AutoCompleteTextView
        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onFocusChanged(boolean z, int i, Rect rect) {
            super.onFocusChanged(z, i, rect);
            this.mSearchView.onTextFocusChanged();
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
            if (i == 4) {
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState keyDispatcherState = getKeyDispatcherState();
                    if (keyDispatcherState != null) {
                        keyDispatcherState.startTracking(keyEvent, this);
                        return true;
                    }
                    return true;
                } else if (keyEvent.getAction() == 1) {
                    KeyEvent.DispatcherState keyDispatcherState2 = getKeyDispatcherState();
                    if (keyDispatcherState2 != null) {
                        keyDispatcherState2.handleUpEvent(keyEvent);
                    }
                    if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                        this.mSearchView.clearFocus();
                        this.mSearchView.setImeVisibility(false);
                        return true;
                    }
                }
            }
            return super.onKeyPreIme(i, keyEvent);
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onWindowFocusChanged(boolean z) {
            super.onWindowFocusChanged(z);
            if (z && this.mSearchView.hasFocus() && getVisibility() == 0) {
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this, 0);
                if (SearchView.isLandscapeMode(getContext())) {
                    SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible(this, true);
                }
            }
        }

        @Override // android.widget.AutoCompleteTextView
        public void performCompletion() {
        }

        @Override // android.widget.AutoCompleteTextView
        protected void replaceText(CharSequence charSequence) {
        }

        @Override // android.widget.AutoCompleteTextView
        public void setDropDownBackgroundResource(int i) {
            setDropDownBackgroundDrawable(this.mTintManager.getDrawable(i));
        }

        void setSearchView(SearchView searchView) {
            this.mSearchView = searchView;
        }

        @Override // android.widget.AutoCompleteTextView
        public void setThreshold(int i) {
            super.setThreshold(i);
            this.mThreshold = i;
        }
    }

    static {
        IS_AT_LEAST_FROYO = Build.VERSION.SDK_INT >= 8;
        HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.searchViewStyle);
    }

    public SearchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowImeRunnable = new Runnable(this) { // from class: android.support.v7.widget.SearchView.1
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) this.this$0.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    SearchView.HIDDEN_METHOD_INVOKER.showSoftInputUnchecked(inputMethodManager, this.this$0, 0);
                }
            }
        };
        this.mUpdateDrawableStateRunnable = new Runnable(this) { // from class: android.support.v7.widget.SearchView.2
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.updateFocusedState();
            }
        };
        this.mReleaseCursorRunnable = new Runnable(this) { // from class: android.support.v7.widget.SearchView.3
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (this.this$0.mSuggestionsAdapter == null || !(this.this$0.mSuggestionsAdapter instanceof SuggestionsAdapter)) {
                    return;
                }
                this.this$0.mSuggestionsAdapter.changeCursor(null);
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<>();
        this.mOnClickListener = new View.OnClickListener(this) { // from class: android.support.v7.widget.SearchView.7
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (view == this.this$0.mSearchButton) {
                    this.this$0.onSearchClicked();
                } else if (view == this.this$0.mCloseButton) {
                    this.this$0.onCloseClicked();
                } else if (view == this.this$0.mSubmitButton) {
                    this.this$0.onSubmitQuery();
                } else if (view == this.this$0.mVoiceButton) {
                    if (SearchView.IS_AT_LEAST_FROYO) {
                        this.this$0.onVoiceClicked();
                    }
                } else if (view == this.this$0.mQueryTextView) {
                    this.this$0.forceSuggestionQuery();
                }
            }
        };
        this.mTextKeyListener = new View.OnKeyListener(this) { // from class: android.support.v7.widget.SearchView.8
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i2, KeyEvent keyEvent) {
                if (this.this$0.mSearchable == null) {
                    return false;
                }
                if (!this.this$0.mQueryTextView.isPopupShowing() || this.this$0.mQueryTextView.getListSelection() == -1) {
                    if (!this.this$0.mQueryTextView.isEmpty() && KeyEventCompat.hasNoModifiers(keyEvent) && keyEvent.getAction() == 1 && i2 == 66) {
                        view.cancelLongPress();
                        SearchView searchView = this.this$0;
                        searchView.launchQuerySearch(0, null, searchView.mQueryTextView.getText().toString());
                        return true;
                    }
                    return false;
                }
                return this.this$0.onSuggestionsKey(view, i2, keyEvent);
            }
        };
        this.mOnEditorActionListener = new TextView.OnEditorActionListener(this) { // from class: android.support.v7.widget.SearchView.9
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                this.this$0.onSubmitQuery();
                return true;
            }
        };
        this.mOnItemClickListener = new AdapterView.OnItemClickListener(this) { // from class: android.support.v7.widget.SearchView.10
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                this.this$0.onItemClicked(i2, 0, null);
            }
        };
        this.mOnItemSelectedListener = new AdapterView.OnItemSelectedListener(this) { // from class: android.support.v7.widget.SearchView.11
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                this.this$0.onItemSelected(i2);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        this.mTextWatcher = new TextWatcher(this) { // from class: android.support.v7.widget.SearchView.12
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                this.this$0.onTextChanged(charSequence);
            }
        };
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.SearchView, i, 0);
        this.mTintManager = obtainStyledAttributes.getTintManager();
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(obtainStyledAttributes.getResourceId(R.styleable.SearchView_layout, 0), (ViewGroup) this, true);
        this.mQueryTextView = (SearchAutoComplete) findViewById(R.id.search_src_text);
        this.mQueryTextView.setSearchView(this);
        this.mSearchEditFrame = findViewById(R.id.search_edit_frame);
        this.mSearchPlate = findViewById(R.id.search_plate);
        this.mSubmitArea = findViewById(R.id.submit_area);
        this.mSearchButton = (ImageView) findViewById(R.id.search_button);
        this.mSubmitButton = (ImageView) findViewById(R.id.search_go_btn);
        this.mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        this.mVoiceButton = (ImageView) findViewById(R.id.search_voice_btn);
        this.mSearchHintIcon = (ImageView) findViewById(R.id.search_mag_icon);
        this.mSearchPlate.setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_queryBackground));
        this.mSubmitArea.setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_submitBackground));
        this.mSearchIconResId = obtainStyledAttributes.getResourceId(R.styleable.SearchView_searchIcon, 0);
        this.mSearchButton.setImageResource(this.mSearchIconResId);
        this.mSubmitButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_goIcon));
        this.mCloseButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_closeIcon));
        this.mVoiceButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_voiceIcon));
        this.mSearchHintIcon.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchIcon));
        this.mSuggestionRowLayout = obtainStyledAttributes.getResourceId(R.styleable.SearchView_suggestionRowLayout, 0);
        this.mSuggestionCommitIconResId = obtainStyledAttributes.getResourceId(R.styleable.SearchView_commitIcon, 0);
        this.mSearchButton.setOnClickListener(this.mOnClickListener);
        this.mCloseButton.setOnClickListener(this.mOnClickListener);
        this.mSubmitButton.setOnClickListener(this.mOnClickListener);
        this.mVoiceButton.setOnClickListener(this.mOnClickListener);
        this.mQueryTextView.setOnClickListener(this.mOnClickListener);
        this.mQueryTextView.addTextChangedListener(this.mTextWatcher);
        this.mQueryTextView.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mQueryTextView.setOnItemClickListener(this.mOnItemClickListener);
        this.mQueryTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
        this.mQueryTextView.setOnKeyListener(this.mTextKeyListener);
        this.mQueryTextView.setOnFocusChangeListener(new View.OnFocusChangeListener(this) { // from class: android.support.v7.widget.SearchView.4
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (this.this$0.mOnQueryTextFocusChangeListener != null) {
                    this.this$0.mOnQueryTextFocusChangeListener.onFocusChange(this.this$0, z);
                }
            }
        });
        setIconifiedByDefault(obtainStyledAttributes.getBoolean(R.styleable.SearchView_iconifiedByDefault, true));
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SearchView_android_maxWidth, -1);
        if (dimensionPixelSize != -1) {
            setMaxWidth(dimensionPixelSize);
        }
        CharSequence text = obtainStyledAttributes.getText(R.styleable.SearchView_queryHint);
        if (!TextUtils.isEmpty(text)) {
            setQueryHint(text);
        }
        int i2 = obtainStyledAttributes.getInt(R.styleable.SearchView_android_imeOptions, -1);
        if (i2 != -1) {
            setImeOptions(i2);
        }
        int i3 = obtainStyledAttributes.getInt(R.styleable.SearchView_android_inputType, -1);
        if (i3 != -1) {
            setInputType(i3);
        }
        setFocusable(obtainStyledAttributes.getBoolean(R.styleable.SearchView_android_focusable, true));
        obtainStyledAttributes.recycle();
        this.mVoiceWebSearchIntent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        this.mVoiceWebSearchIntent.addFlags(268435456);
        this.mVoiceWebSearchIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        this.mVoiceAppSearchIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mVoiceAppSearchIntent.addFlags(268435456);
        this.mDropDownAnchor = findViewById(this.mQueryTextView.getDropDownAnchor());
        if (this.mDropDownAnchor != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                addOnLayoutChangeListenerToDropDownAnchorSDK11();
            } else {
                addOnLayoutChangeListenerToDropDownAnchorBase();
            }
        }
        updateViewsVisibility(this.mIconifiedByDefault);
        updateQueryHint();
    }

    private void addOnLayoutChangeListenerToDropDownAnchorBase() {
        this.mDropDownAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(this) { // from class: android.support.v7.widget.SearchView.6
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                this.this$0.adjustDropDownSizeAndPosition();
            }
        });
    }

    @TargetApi(11)
    private void addOnLayoutChangeListenerToDropDownAnchorSDK11() {
        this.mDropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener(this) { // from class: android.support.v7.widget.SearchView.5
            final SearchView this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                this.this$0.adjustDropDownSizeAndPosition();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustDropDownSizeAndPosition() {
        if (this.mDropDownAnchor.getWidth() > 1) {
            Resources resources = getContext().getResources();
            int paddingLeft = this.mSearchPlate.getPaddingLeft();
            Rect rect = new Rect();
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int dimensionPixelSize = this.mIconifiedByDefault ? resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_icon_width) + resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_text_padding_left) : 0;
            this.mQueryTextView.getDropDownBackground().getPadding(rect);
            this.mQueryTextView.setDropDownHorizontalOffset(isLayoutRtl ? -rect.left : paddingLeft - (rect.left + dimensionPixelSize));
            this.mQueryTextView.setDropDownWidth((((this.mDropDownAnchor.getWidth() + rect.left) + rect.right) + dimensionPixelSize) - paddingLeft);
        }
    }

    private Intent createIntent(String str, Uri uri, String str2, String str3, int i, String str4) {
        Intent intent = new Intent(str);
        intent.addFlags(268435456);
        if (uri != null) {
            intent.setData(uri);
        }
        intent.putExtra(SearchManager.USER_QUERY, this.mUserQuery);
        if (str3 != null) {
            intent.putExtra("query", str3);
        }
        if (str2 != null) {
            intent.putExtra(SearchManager.EXTRA_DATA_KEY, str2);
        }
        Bundle bundle = this.mAppSearchData;
        if (bundle != null) {
            intent.putExtra(SearchManager.APP_DATA, bundle);
        }
        if (i != 0) {
            intent.putExtra(SearchManager.ACTION_KEY, i);
            intent.putExtra(SearchManager.ACTION_MSG, str4);
        }
        if (IS_AT_LEAST_FROYO) {
            intent.setComponent(this.mSearchable.getSearchActivity());
        }
        return intent;
    }

    private Intent createIntentFromSuggestion(Cursor cursor, int i, String str) {
        int i2;
        String columnString;
        try {
            String columnString2 = SuggestionsAdapter.getColumnString(cursor, SearchManager.SUGGEST_COLUMN_INTENT_ACTION);
            if (columnString2 == null && Build.VERSION.SDK_INT >= 8) {
                columnString2 = this.mSearchable.getSuggestIntentAction();
            }
            String str2 = columnString2 == null ? Intent.ACTION_SEARCH : columnString2;
            String columnString3 = SuggestionsAdapter.getColumnString(cursor, SearchManager.SUGGEST_COLUMN_INTENT_DATA);
            if (IS_AT_LEAST_FROYO && columnString3 == null) {
                columnString3 = this.mSearchable.getSuggestIntentData();
            }
            if (columnString3 != null && (columnString = SuggestionsAdapter.getColumnString(cursor, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID)) != null) {
                columnString3 = columnString3 + Separators.SLASH + Uri.encode(columnString);
            }
            return createIntent(str2, columnString3 == null ? null : Uri.parse(columnString3), SuggestionsAdapter.getColumnString(cursor, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA), SuggestionsAdapter.getColumnString(cursor, SearchManager.SUGGEST_COLUMN_QUERY), i, str);
        } catch (RuntimeException e) {
            try {
                i2 = cursor.getPosition();
            } catch (RuntimeException e2) {
                i2 = -1;
            }
            Log.w(LOG_TAG, "Search suggestions cursor at row " + i2 + " returned exception.", e);
            return null;
        }
    }

    @TargetApi(8)
    private Intent createVoiceAppSearchIntent(Intent intent, SearchableInfo searchableInfo) {
        ComponentName searchActivity = searchableInfo.getSearchActivity();
        Intent intent2 = new Intent(Intent.ACTION_SEARCH);
        intent2.setComponent(searchActivity);
        PendingIntent activity = PendingIntent.getActivity(getContext(), 0, intent2, 1073741824);
        Bundle bundle = new Bundle();
        Bundle bundle2 = this.mAppSearchData;
        if (bundle2 != null) {
            bundle.putParcelable(SearchManager.APP_DATA, bundle2);
        }
        Intent intent3 = new Intent(intent);
        String str = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
        String str2 = null;
        String str3 = null;
        int i = 1;
        if (Build.VERSION.SDK_INT >= 8) {
            Resources resources = getResources();
            if (searchableInfo.getVoiceLanguageModeId() != 0) {
                str = resources.getString(searchableInfo.getVoiceLanguageModeId());
            }
            if (searchableInfo.getVoicePromptTextId() != 0) {
                str2 = resources.getString(searchableInfo.getVoicePromptTextId());
            }
            if (searchableInfo.getVoiceLanguageId() != 0) {
                str3 = resources.getString(searchableInfo.getVoiceLanguageId());
            }
            if (searchableInfo.getVoiceMaxResults() != 0) {
                i = searchableInfo.getVoiceMaxResults();
            }
        } else {
            str3 = null;
            str2 = null;
        }
        intent3.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, str);
        intent3.putExtra(RecognizerIntent.EXTRA_PROMPT, str2);
        intent3.putExtra(RecognizerIntent.EXTRA_LANGUAGE, str3);
        intent3.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, i);
        intent3.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, searchActivity == null ? null : searchActivity.flattenToShortString());
        intent3.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, activity);
        intent3.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT_BUNDLE, bundle);
        return intent3;
    }

    @TargetApi(8)
    private Intent createVoiceWebSearchIntent(Intent intent, SearchableInfo searchableInfo) {
        Intent intent2 = new Intent(intent);
        ComponentName searchActivity = searchableInfo.getSearchActivity();
        intent2.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, searchActivity == null ? null : searchActivity.flattenToShortString());
        return intent2;
    }

    private void dismissSuggestions() {
        this.mQueryTextView.dismissDropDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceSuggestionQuery() {
        HIDDEN_METHOD_INVOKER.doBeforeTextChanged(this.mQueryTextView);
        HIDDEN_METHOD_INVOKER.doAfterTextChanged(this.mQueryTextView);
    }

    private CharSequence getDecoratedHint(CharSequence charSequence) {
        if (this.mIconifiedByDefault) {
            Drawable drawable = this.mTintManager.getDrawable(this.mSearchIconResId);
            int textSize = (int) (this.mQueryTextView.getTextSize() * 1.25d);
            drawable.setBounds(0, 0, textSize, textSize);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("   ");
            spannableStringBuilder.append(charSequence);
            spannableStringBuilder.setSpan(new ImageSpan(drawable), 1, 2, 33);
            return spannableStringBuilder;
        }
        return charSequence;
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
    }

    @TargetApi(8)
    private boolean hasVoiceSearch() {
        SearchableInfo searchableInfo = this.mSearchable;
        boolean z = false;
        if (searchableInfo == null || !searchableInfo.getVoiceSearchEnabled()) {
            return false;
        }
        Intent intent = null;
        if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
            intent = this.mVoiceWebSearchIntent;
        } else if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
            intent = this.mVoiceAppSearchIntent;
        }
        if (intent != null) {
            if (getContext().getPackageManager().resolveActivity(intent, 65536) != null) {
                z = true;
            }
            return z;
        }
        return false;
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !isIconified();
    }

    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            getContext().startActivity(intent);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "Failed launch activity: " + intent, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchQuerySearch(int i, String str, String str2) {
        getContext().startActivity(createIntent(Intent.ACTION_SEARCH, null, null, str2, i, str));
    }

    private boolean launchSuggestion(int i, int i2, String str) {
        Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor == null || !cursor.moveToPosition(i)) {
            return false;
        }
        launchIntent(createIntentFromSuggestion(cursor, i2, str));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCloseClicked() {
        if (!TextUtils.isEmpty(this.mQueryTextView.getText())) {
            this.mQueryTextView.setText("");
            this.mQueryTextView.requestFocus();
            setImeVisibility(true);
        } else if (this.mIconifiedByDefault) {
            OnCloseListener onCloseListener = this.mOnCloseListener;
            if (onCloseListener == null || !onCloseListener.onClose()) {
                clearFocus();
                updateViewsVisibility(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemClicked(int i, int i2, String str) {
        OnSuggestionListener onSuggestionListener = this.mOnSuggestionListener;
        if (onSuggestionListener == null || !onSuggestionListener.onSuggestionClick(i)) {
            launchSuggestion(i, 0, null);
            setImeVisibility(false);
            dismissSuggestions();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemSelected(int i) {
        OnSuggestionListener onSuggestionListener = this.mOnSuggestionListener;
        if (onSuggestionListener == null || !onSuggestionListener.onSuggestionSelect(i)) {
            rewriteQueryFromSuggestion(i);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearchClicked() {
        updateViewsVisibility(false);
        this.mQueryTextView.requestFocus();
        setImeVisibility(true);
        View.OnClickListener onClickListener = this.mOnSearchClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubmitQuery() {
        Editable text = this.mQueryTextView.getText();
        if (text == null || TextUtils.getTrimmedLength(text) <= 0) {
            return;
        }
        OnQueryTextListener onQueryTextListener = this.mOnQueryChangeListener;
        if (onQueryTextListener == null || !onQueryTextListener.onQueryTextSubmit(text.toString())) {
            if (this.mSearchable != null) {
                launchQuerySearch(0, null, text.toString());
            }
            setImeVisibility(false);
            dismissSuggestions();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onSuggestionsKey(View view, int i, KeyEvent keyEvent) {
        if (this.mSearchable != null && this.mSuggestionsAdapter != null && keyEvent.getAction() == 0 && KeyEventCompat.hasNoModifiers(keyEvent)) {
            if (i == 66 || i == 84 || i == 61) {
                return onItemClicked(this.mQueryTextView.getListSelection(), 0, null);
            }
            if (i != 21 && i != 22) {
                return (i == 19 && this.mQueryTextView.getListSelection() == 0) ? false : false;
            }
            this.mQueryTextView.setSelection(i == 21 ? 0 : this.mQueryTextView.length());
            this.mQueryTextView.setListSelection(0);
            this.mQueryTextView.clearListSelection();
            HIDDEN_METHOD_INVOKER.ensureImeVisible(this.mQueryTextView, true);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTextChanged(CharSequence charSequence) {
        Editable text = this.mQueryTextView.getText();
        this.mUserQuery = text;
        boolean z = true;
        boolean z2 = !TextUtils.isEmpty(text);
        updateSubmitButton(z2);
        if (z2) {
            z = false;
        }
        updateVoiceButton(z);
        updateCloseButton();
        updateSubmitArea();
        if (this.mOnQueryChangeListener != null && !TextUtils.equals(charSequence, this.mOldQueryText)) {
            this.mOnQueryChangeListener.onQueryTextChange(charSequence.toString());
        }
        this.mOldQueryText = charSequence.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(8)
    public void onVoiceClicked() {
        if (this.mSearchable == null) {
            return;
        }
        SearchableInfo searchableInfo = this.mSearchable;
        try {
            if (searchableInfo.getVoiceSearchLaunchWebSearch()) {
                getContext().startActivity(createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, searchableInfo));
            } else if (searchableInfo.getVoiceSearchLaunchRecognizer()) {
                getContext().startActivity(createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, searchableInfo));
            }
        } catch (ActivityNotFoundException e) {
            Log.w(LOG_TAG, "Could not find voice search activity");
        }
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    private void rewriteQueryFromSuggestion(int i) {
        Editable text = this.mQueryTextView.getText();
        Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToPosition(i)) {
            setQuery(text);
            return;
        }
        CharSequence convertToString = this.mSuggestionsAdapter.convertToString(cursor);
        if (convertToString != null) {
            setQuery(convertToString);
        } else {
            setQuery(text);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImeVisibility(boolean z) {
        if (z) {
            post(this.mShowImeRunnable);
            return;
        }
        removeCallbacks(this.mShowImeRunnable);
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    private void setQuery(CharSequence charSequence) {
        this.mQueryTextView.setText(charSequence);
        this.mQueryTextView.setSelection(TextUtils.isEmpty(charSequence) ? 0 : charSequence.length());
    }

    private void updateCloseButton() {
        boolean z = !TextUtils.isEmpty(this.mQueryTextView.getText());
        boolean z2 = true;
        if (!z) {
            z2 = this.mIconifiedByDefault && !this.mExpandedInActionView;
        }
        this.mCloseButton.setVisibility(z2 ? 0 : 8);
        this.mCloseButton.getDrawable().setState(z ? ENABLED_STATE_SET : EMPTY_STATE_SET);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusedState() {
        boolean hasFocus = this.mQueryTextView.hasFocus();
        this.mSearchPlate.getBackground().setState(hasFocus ? ENABLED_FOCUSED_STATE_SET : EMPTY_STATE_SET);
        this.mSubmitArea.getBackground().setState(hasFocus ? ENABLED_FOCUSED_STATE_SET : EMPTY_STATE_SET);
        invalidate();
    }

    private void updateQueryHint() {
        SearchableInfo searchableInfo;
        CharSequence charSequence = this.mQueryHint;
        if (charSequence != null) {
            this.mQueryTextView.setHint(getDecoratedHint(charSequence));
        } else if (!IS_AT_LEAST_FROYO || (searchableInfo = this.mSearchable) == null) {
            this.mQueryTextView.setHint(getDecoratedHint(""));
        } else {
            String str = null;
            int hintId = searchableInfo.getHintId();
            if (hintId != 0) {
                str = getContext().getString(hintId);
            }
            if (str != null) {
                this.mQueryTextView.setHint(getDecoratedHint(str));
            }
        }
    }

    @TargetApi(8)
    private void updateSearchAutoComplete() {
        this.mQueryTextView.setThreshold(this.mSearchable.getSuggestThreshold());
        this.mQueryTextView.setImeOptions(this.mSearchable.getImeOptions());
        int inputType = this.mSearchable.getInputType();
        if ((inputType & 15) == 1) {
            inputType &= -65537;
            if (this.mSearchable.getSuggestAuthority() != null) {
                inputType = inputType | 65536 | 524288;
            }
        }
        this.mQueryTextView.setInputType(inputType);
        CursorAdapter cursorAdapter = this.mSuggestionsAdapter;
        if (cursorAdapter != null) {
            cursorAdapter.changeCursor(null);
        }
        if (this.mSearchable.getSuggestAuthority() != null) {
            this.mSuggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
            this.mQueryTextView.setAdapter(this.mSuggestionsAdapter);
            SuggestionsAdapter suggestionsAdapter = (SuggestionsAdapter) this.mSuggestionsAdapter;
            int i = 1;
            if (this.mQueryRefinement) {
                i = 2;
            }
            suggestionsAdapter.setQueryRefinement(i);
        }
    }

    private void updateSubmitArea() {
        int i = 8;
        if (isSubmitAreaEnabled() && (this.mSubmitButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) {
            i = 0;
        }
        this.mSubmitArea.setVisibility(i);
    }

    private void updateSubmitButton(boolean z) {
        int i = 8;
        if (this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && (z || !this.mVoiceButtonEnabled)) {
            i = 0;
        }
        this.mSubmitButton.setVisibility(i);
    }

    private void updateViewsVisibility(boolean z) {
        this.mIconified = z;
        int i = z ? 0 : 8;
        boolean z2 = !TextUtils.isEmpty(this.mQueryTextView.getText());
        this.mSearchButton.setVisibility(i);
        updateSubmitButton(z2);
        this.mSearchEditFrame.setVisibility(z ? 8 : 0);
        this.mSearchHintIcon.setVisibility(this.mIconifiedByDefault ? 8 : 0);
        updateCloseButton();
        boolean z3 = false;
        if (!z2) {
            z3 = true;
        }
        updateVoiceButton(z3);
        updateSubmitArea();
    }

    private void updateVoiceButton(boolean z) {
        int i = 8;
        if (this.mVoiceButtonEnabled && !isIconified() && z) {
            i = 0;
            this.mSubmitButton.setVisibility(8);
        }
        this.mVoiceButton.setVisibility(i);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void clearFocus() {
        this.mClearingFocus = true;
        setImeVisibility(false);
        super.clearFocus();
        this.mQueryTextView.clearFocus();
        this.mClearingFocus = false;
    }

    public int getImeOptions() {
        return this.mQueryTextView.getImeOptions();
    }

    public int getInputType() {
        return this.mQueryTextView.getInputType();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public CharSequence getQuery() {
        return this.mQueryTextView.getText();
    }

    public CharSequence getQueryHint() {
        SearchableInfo searchableInfo;
        CharSequence charSequence = this.mQueryHint;
        if (charSequence != null) {
            return charSequence;
        }
        if (!IS_AT_LEAST_FROYO || (searchableInfo = this.mSearchable) == null) {
            return null;
        }
        String str = null;
        int hintId = searchableInfo.getHintId();
        if (hintId != 0) {
            str = getContext().getString(hintId);
        }
        return str;
    }

    int getSuggestionCommitIconResId() {
        return this.mSuggestionCommitIconResId;
    }

    int getSuggestionRowLayout() {
        return this.mSuggestionRowLayout;
    }

    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }

    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public boolean isIconified() {
        return this.mIconified;
    }

    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }

    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }

    @Override // android.support.v7.view.CollapsibleActionView
    public void onActionViewCollapsed() {
        setQuery("", false);
        clearFocus();
        updateViewsVisibility(true);
        this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }

    @Override // android.support.v7.view.CollapsibleActionView
    public void onActionViewExpanded() {
        if (this.mExpandedInActionView) {
            return;
        }
        this.mExpandedInActionView = true;
        this.mCollapsedImeOptions = this.mQueryTextView.getImeOptions();
        this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions | 33554432);
        this.mQueryTextView.setText("");
        setIconified(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        if (isIconified()) {
            super.onMeasure(i, i2);
            return;
        }
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode == Integer.MIN_VALUE) {
            int i4 = this.mMaxWidth;
            size = i4 > 0 ? Math.min(i4, size) : Math.min(getPreferredWidth(), size);
        } else if (mode == 0) {
            size = this.mMaxWidth;
            if (size <= 0) {
                size = getPreferredWidth();
            }
        } else if (mode == 1073741824 && (i3 = this.mMaxWidth) > 0) {
            size = Math.min(i3, size);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onQueryRefine(CharSequence charSequence) {
        setQuery(charSequence);
    }

    void onTextFocusChanged() {
        updateViewsVisibility(isIconified());
        postUpdateFocusedState();
        if (this.mQueryTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        postUpdateFocusedState();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int i, Rect rect) {
        if (!this.mClearingFocus && isFocusable()) {
            if (isIconified()) {
                return super.requestFocus(i, rect);
            }
            boolean requestFocus = this.mQueryTextView.requestFocus(i, rect);
            if (requestFocus) {
                updateViewsVisibility(false);
            }
            return requestFocus;
        }
        return false;
    }

    public void setAppSearchData(Bundle bundle) {
        this.mAppSearchData = bundle;
    }

    public void setIconified(boolean z) {
        if (z) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public void setIconifiedByDefault(boolean z) {
        if (this.mIconifiedByDefault == z) {
            return;
        }
        this.mIconifiedByDefault = z;
        updateViewsVisibility(z);
        updateQueryHint();
    }

    public void setImeOptions(int i) {
        this.mQueryTextView.setImeOptions(i);
    }

    public void setInputType(int i) {
        this.mQueryTextView.setInputType(i);
    }

    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
        requestLayout();
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
    }

    public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mOnQueryTextFocusChangeListener = onFocusChangeListener;
    }

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        this.mOnQueryChangeListener = onQueryTextListener;
    }

    public void setOnSearchClickListener(View.OnClickListener onClickListener) {
        this.mOnSearchClickListener = onClickListener;
    }

    public void setOnSuggestionListener(OnSuggestionListener onSuggestionListener) {
        this.mOnSuggestionListener = onSuggestionListener;
    }

    public void setQuery(CharSequence charSequence, boolean z) {
        this.mQueryTextView.setText(charSequence);
        if (charSequence != null) {
            SearchAutoComplete searchAutoComplete = this.mQueryTextView;
            searchAutoComplete.setSelection(searchAutoComplete.length());
            this.mUserQuery = charSequence;
        }
        if (!z || TextUtils.isEmpty(charSequence)) {
            return;
        }
        onSubmitQuery();
    }

    public void setQueryHint(CharSequence charSequence) {
        this.mQueryHint = charSequence;
        updateQueryHint();
    }

    public void setQueryRefinementEnabled(boolean z) {
        this.mQueryRefinement = z;
        CursorAdapter cursorAdapter = this.mSuggestionsAdapter;
        if (cursorAdapter instanceof SuggestionsAdapter) {
            ((SuggestionsAdapter) cursorAdapter).setQueryRefinement(z ? 2 : 1);
        }
    }

    public void setSearchableInfo(SearchableInfo searchableInfo) {
        this.mSearchable = searchableInfo;
        if (this.mSearchable != null) {
            if (IS_AT_LEAST_FROYO) {
                updateSearchAutoComplete();
            }
            updateQueryHint();
        }
        this.mVoiceButtonEnabled = IS_AT_LEAST_FROYO && hasVoiceSearch();
        if (this.mVoiceButtonEnabled) {
            this.mQueryTextView.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
        }
        updateViewsVisibility(isIconified());
    }

    public void setSubmitButtonEnabled(boolean z) {
        this.mSubmitButtonEnabled = z;
        updateViewsVisibility(isIconified());
    }

    public void setSuggestionsAdapter(CursorAdapter cursorAdapter) {
        this.mSuggestionsAdapter = cursorAdapter;
        this.mQueryTextView.setAdapter(this.mSuggestionsAdapter);
    }
}