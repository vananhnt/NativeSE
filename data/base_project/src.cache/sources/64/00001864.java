package android.widget;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.CollapsibleActionView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.WeakHashMap;

/* loaded from: SearchView.class */
public class SearchView extends LinearLayout implements CollapsibleActionView {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "SearchView";
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private OnQueryTextListener mOnQueryChangeListener;
    private OnCloseListener mOnCloseListener;
    private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private OnSuggestionListener mOnSuggestionListener;
    private View.OnClickListener mOnSearchClickListener;
    private boolean mIconifiedByDefault;
    private boolean mIconified;
    private CursorAdapter mSuggestionsAdapter;
    private View mSearchButton;
    private View mSubmitButton;
    private View mSearchPlate;
    private View mSubmitArea;
    private ImageView mCloseButton;
    private View mSearchEditFrame;
    private View mVoiceButton;
    private SearchAutoComplete mQueryTextView;
    private View mDropDownAnchor;
    private ImageView mSearchHintIcon;
    private boolean mSubmitButtonEnabled;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private boolean mClearingFocus;
    private int mMaxWidth;
    private boolean mVoiceButtonEnabled;
    private CharSequence mOldQueryText;
    private CharSequence mUserQuery;
    private boolean mExpandedInActionView;
    private int mCollapsedImeOptions;
    private SearchableInfo mSearchable;
    private Bundle mAppSearchData;
    private Runnable mShowImeRunnable;
    private Runnable mUpdateDrawableStateRunnable;
    private Runnable mReleaseCursorRunnable;
    private final Intent mVoiceWebSearchIntent;
    private final Intent mVoiceAppSearchIntent;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private final View.OnClickListener mOnClickListener;
    View.OnKeyListener mTextKeyListener;
    private final TextView.OnEditorActionListener mOnEditorActionListener;
    private final AdapterView.OnItemClickListener mOnItemClickListener;
    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private TextWatcher mTextWatcher;

    /* loaded from: SearchView$OnCloseListener.class */
    public interface OnCloseListener {
        boolean onClose();
    }

    /* loaded from: SearchView$OnQueryTextListener.class */
    public interface OnQueryTextListener {
        boolean onQueryTextSubmit(String str);

        boolean onQueryTextChange(String str);
    }

    /* loaded from: SearchView$OnSuggestionListener.class */
    public interface OnSuggestionListener {
        boolean onSuggestionSelect(int i);

        boolean onSuggestionClick(int i);
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShowImeRunnable = new Runnable() { // from class: android.widget.SearchView.1
            @Override // java.lang.Runnable
            public void run() {
                InputMethodManager imm = (InputMethodManager) SearchView.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInputUnchecked(0, null);
                }
            }
        };
        this.mUpdateDrawableStateRunnable = new Runnable() { // from class: android.widget.SearchView.2
            @Override // java.lang.Runnable
            public void run() {
                SearchView.this.updateFocusedState();
            }
        };
        this.mReleaseCursorRunnable = new Runnable() { // from class: android.widget.SearchView.3
            @Override // java.lang.Runnable
            public void run() {
                if (SearchView.this.mSuggestionsAdapter != null && (SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter)) {
                    SearchView.this.mSuggestionsAdapter.changeCursor(null);
                }
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<>();
        this.mOnClickListener = new View.OnClickListener() { // from class: android.widget.SearchView.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (v == SearchView.this.mSearchButton) {
                    SearchView.this.onSearchClicked();
                } else if (v == SearchView.this.mCloseButton) {
                    SearchView.this.onCloseClicked();
                } else if (v == SearchView.this.mSubmitButton) {
                    SearchView.this.onSubmitQuery();
                } else if (v == SearchView.this.mVoiceButton) {
                    SearchView.this.onVoiceClicked();
                } else if (v == SearchView.this.mQueryTextView) {
                    SearchView.this.forceSuggestionQuery();
                }
            }
        };
        this.mTextKeyListener = new View.OnKeyListener() { // from class: android.widget.SearchView.7
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SearchableInfo.ActionKeyInfo actionKey;
                if (SearchView.this.mSearchable != null) {
                    if (SearchView.this.mQueryTextView.isPopupShowing() && SearchView.this.mQueryTextView.getListSelection() != -1) {
                        return SearchView.this.onSuggestionsKey(v, keyCode, event);
                    }
                    if (!SearchView.this.mQueryTextView.isEmpty() && event.hasNoModifiers()) {
                        if (event.getAction() == 1 && keyCode == 66) {
                            v.cancelLongPress();
                            SearchView.this.launchQuerySearch(0, null, SearchView.this.mQueryTextView.getText().toString());
                            return true;
                        } else if (event.getAction() == 0 && (actionKey = SearchView.this.mSearchable.findActionKey(keyCode)) != null && actionKey.getQueryActionMsg() != null) {
                            SearchView.this.launchQuerySearch(keyCode, actionKey.getQueryActionMsg(), SearchView.this.mQueryTextView.getText().toString());
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
                return false;
            }
        };
        this.mOnEditorActionListener = new TextView.OnEditorActionListener() { // from class: android.widget.SearchView.8
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: android.widget.SearchView.9
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchView.this.onItemClicked(position, 0, null);
            }
        };
        this.mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: android.widget.SearchView.10
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchView.this.onItemSelected(position);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        this.mTextWatcher = new TextWatcher() { // from class: android.widget.SearchView.11
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int before, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int after) {
                SearchView.this.onTextChanged(s);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        };
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_view, (ViewGroup) this, true);
        this.mSearchButton = findViewById(R.id.search_button);
        this.mQueryTextView = (SearchAutoComplete) findViewById(R.id.search_src_text);
        this.mQueryTextView.setSearchView(this);
        this.mSearchEditFrame = findViewById(R.id.search_edit_frame);
        this.mSearchPlate = findViewById(R.id.search_plate);
        this.mSubmitArea = findViewById(R.id.submit_area);
        this.mSubmitButton = findViewById(R.id.search_go_btn);
        this.mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        this.mVoiceButton = findViewById(R.id.search_voice_btn);
        this.mSearchHintIcon = (ImageView) findViewById(R.id.search_mag_icon);
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
        this.mQueryTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: android.widget.SearchView.4
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
                    SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, hasFocus);
                }
            }
        });
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchView, 0, 0);
        setIconifiedByDefault(a.getBoolean(3, true));
        int maxWidth = a.getDimensionPixelSize(0, -1);
        if (maxWidth != -1) {
            setMaxWidth(maxWidth);
        }
        CharSequence queryHint = a.getText(4);
        if (!TextUtils.isEmpty(queryHint)) {
            setQueryHint(queryHint);
        }
        int imeOptions = a.getInt(2, -1);
        if (imeOptions != -1) {
            setImeOptions(imeOptions);
        }
        int inputType = a.getInt(1, -1);
        if (inputType != -1) {
            setInputType(inputType);
        }
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.View, 0, 0);
        boolean focusable = a2.getBoolean(18, true);
        a2.recycle();
        setFocusable(focusable);
        this.mVoiceWebSearchIntent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        this.mVoiceWebSearchIntent.addFlags(268435456);
        this.mVoiceWebSearchIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        this.mVoiceAppSearchIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mVoiceAppSearchIntent.addFlags(268435456);
        this.mDropDownAnchor = findViewById(this.mQueryTextView.getDropDownAnchor());
        if (this.mDropDownAnchor != null) {
            this.mDropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: android.widget.SearchView.5
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    SearchView.this.adjustDropDownSizeAndPosition();
                }
            });
        }
        updateViewsVisibility(this.mIconifiedByDefault);
        updateQueryHint();
    }

    public void setSearchableInfo(SearchableInfo searchable) {
        this.mSearchable = searchable;
        if (this.mSearchable != null) {
            updateSearchAutoComplete();
            updateQueryHint();
        }
        this.mVoiceButtonEnabled = hasVoiceSearch();
        if (this.mVoiceButtonEnabled) {
            this.mQueryTextView.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
        }
        updateViewsVisibility(isIconified());
    }

    public void setAppSearchData(Bundle appSearchData) {
        this.mAppSearchData = appSearchData;
    }

    public void setImeOptions(int imeOptions) {
        this.mQueryTextView.setImeOptions(imeOptions);
    }

    public int getImeOptions() {
        return this.mQueryTextView.getImeOptions();
    }

    public void setInputType(int inputType) {
        this.mQueryTextView.setInputType(inputType);
    }

    public int getInputType() {
        return this.mQueryTextView.getInputType();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (!this.mClearingFocus && isFocusable()) {
            if (!isIconified()) {
                boolean result = this.mQueryTextView.requestFocus(direction, previouslyFocusedRect);
                if (result) {
                    updateViewsVisibility(false);
                }
                return result;
            }
            return super.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void clearFocus() {
        this.mClearingFocus = true;
        setImeVisibility(false);
        super.clearFocus();
        this.mQueryTextView.clearFocus();
        this.mClearingFocus = false;
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        this.mOnQueryChangeListener = listener;
    }

    public void setOnCloseListener(OnCloseListener listener) {
        this.mOnCloseListener = listener;
    }

    public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener listener) {
        this.mOnQueryTextFocusChangeListener = listener;
    }

    public void setOnSuggestionListener(OnSuggestionListener listener) {
        this.mOnSuggestionListener = listener;
    }

    public void setOnSearchClickListener(View.OnClickListener listener) {
        this.mOnSearchClickListener = listener;
    }

    public CharSequence getQuery() {
        return this.mQueryTextView.getText();
    }

    public void setQuery(CharSequence query, boolean submit) {
        this.mQueryTextView.setText(query);
        if (query != null) {
            this.mQueryTextView.setSelection(this.mQueryTextView.length());
            this.mUserQuery = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void setQueryHint(CharSequence hint) {
        this.mQueryHint = hint;
        updateQueryHint();
    }

    public CharSequence getQueryHint() {
        if (this.mQueryHint != null) {
            return this.mQueryHint;
        }
        if (this.mSearchable != null) {
            CharSequence hint = null;
            int hintId = this.mSearchable.getHintId();
            if (hintId != 0) {
                hint = getContext().getString(hintId);
            }
            return hint;
        }
        return null;
    }

    public void setIconifiedByDefault(boolean iconified) {
        if (this.mIconifiedByDefault == iconified) {
            return;
        }
        this.mIconifiedByDefault = iconified;
        updateViewsVisibility(iconified);
        updateQueryHint();
    }

    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public void setIconified(boolean iconify) {
        if (iconify) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public boolean isIconified() {
        return this.mIconified;
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        this.mSubmitButtonEnabled = enabled;
        updateViewsVisibility(isIconified());
    }

    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }

    public void setQueryRefinementEnabled(boolean enable) {
        this.mQueryRefinement = enable;
        if (this.mSuggestionsAdapter instanceof SuggestionsAdapter) {
            ((SuggestionsAdapter) this.mSuggestionsAdapter).setQueryRefinement(enable ? 2 : 1);
        }
    }

    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }

    public void setSuggestionsAdapter(CursorAdapter adapter) {
        this.mSuggestionsAdapter = adapter;
        this.mQueryTextView.setAdapter(this.mSuggestionsAdapter);
    }

    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }

    public void setMaxWidth(int maxpixels) {
        this.mMaxWidth = maxpixels;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isIconified()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case Integer.MIN_VALUE:
                if (this.mMaxWidth > 0) {
                    width = Math.min(this.mMaxWidth, width);
                    break;
                } else {
                    width = Math.min(getPreferredWidth(), width);
                    break;
                }
            case 0:
                width = this.mMaxWidth > 0 ? this.mMaxWidth : getPreferredWidth();
                break;
            case 1073741824:
                if (this.mMaxWidth > 0) {
                    width = Math.min(this.mMaxWidth, width);
                    break;
                }
                break;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), heightMeasureSpec);
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.search_view_preferred_width);
    }

    private void updateViewsVisibility(boolean collapsed) {
        this.mIconified = collapsed;
        int visCollapsed = collapsed ? 0 : 8;
        boolean hasText = !TextUtils.isEmpty(this.mQueryTextView.getText());
        this.mSearchButton.setVisibility(visCollapsed);
        updateSubmitButton(hasText);
        this.mSearchEditFrame.setVisibility(collapsed ? 8 : 0);
        this.mSearchHintIcon.setVisibility(this.mIconifiedByDefault ? 8 : 0);
        updateCloseButton();
        updateVoiceButton(!hasText);
        updateSubmitArea();
    }

    private boolean hasVoiceSearch() {
        if (this.mSearchable != null && this.mSearchable.getVoiceSearchEnabled()) {
            Intent testIntent = null;
            if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
                testIntent = this.mVoiceWebSearchIntent;
            } else if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
                testIntent = this.mVoiceAppSearchIntent;
            }
            if (testIntent != null) {
                ResolveInfo ri = getContext().getPackageManager().resolveActivity(testIntent, 65536);
                return ri != null;
            }
            return false;
        }
        return false;
    }

    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !isIconified();
    }

    private void updateSubmitButton(boolean hasText) {
        int visibility = 8;
        if (this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && (hasText || !this.mVoiceButtonEnabled)) {
            visibility = 0;
        }
        this.mSubmitButton.setVisibility(visibility);
    }

    private void updateSubmitArea() {
        int visibility = 8;
        if (isSubmitAreaEnabled() && (this.mSubmitButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) {
            visibility = 0;
        }
        this.mSubmitArea.setVisibility(visibility);
    }

    private void updateCloseButton() {
        boolean hasText = !TextUtils.isEmpty(this.mQueryTextView.getText());
        boolean showClose = hasText || (this.mIconifiedByDefault && !this.mExpandedInActionView);
        this.mCloseButton.setVisibility(showClose ? 0 : 8);
        this.mCloseButton.getDrawable().setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusedState() {
        boolean focused = this.mQueryTextView.hasFocus();
        this.mSearchPlate.getBackground().setState(focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
        this.mSubmitArea.getBackground().setState(focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImeVisibility(boolean visible) {
        if (visible) {
            post(this.mShowImeRunnable);
            return;
        }
        removeCallbacks(this.mShowImeRunnable);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onQueryRefine(CharSequence queryText) {
        setQuery(queryText);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mSearchable == null) {
            return false;
        }
        SearchableInfo.ActionKeyInfo actionKey = this.mSearchable.findActionKey(keyCode);
        if (actionKey != null && actionKey.getQueryActionMsg() != null) {
            launchQuerySearch(keyCode, actionKey.getQueryActionMsg(), this.mQueryTextView.getText().toString());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onSuggestionsKey(View v, int keyCode, KeyEvent event) {
        SearchableInfo.ActionKeyInfo actionKey;
        int position;
        String actionMsg;
        if (this.mSearchable != null && this.mSuggestionsAdapter != null && event.getAction() == 0 && event.hasNoModifiers()) {
            if (keyCode == 66 || keyCode == 84 || keyCode == 61) {
                return onItemClicked(this.mQueryTextView.getListSelection(), 0, null);
            }
            if (keyCode == 21 || keyCode == 22) {
                int selPoint = keyCode == 21 ? 0 : this.mQueryTextView.length();
                this.mQueryTextView.setSelection(selPoint);
                this.mQueryTextView.setListSelection(0);
                this.mQueryTextView.clearListSelection();
                this.mQueryTextView.ensureImeVisible(true);
                return true;
            } else if ((keyCode != 19 || 0 != this.mQueryTextView.getListSelection()) && (actionKey = this.mSearchable.findActionKey(keyCode)) != null) {
                if ((actionKey.getSuggestActionMsg() != null || actionKey.getSuggestActionMsgColumn() != null) && (position = this.mQueryTextView.getListSelection()) != -1) {
                    Cursor c = this.mSuggestionsAdapter.getCursor();
                    if (c.moveToPosition(position) && (actionMsg = getActionKeyMessage(c, actionKey)) != null && actionMsg.length() > 0) {
                        return onItemClicked(position, keyCode, actionMsg);
                    }
                    return false;
                }
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    private static String getActionKeyMessage(Cursor c, SearchableInfo.ActionKeyInfo actionKey) {
        String result = null;
        String column = actionKey.getSuggestActionMsgColumn();
        if (column != null) {
            result = SuggestionsAdapter.getColumnString(c, column);
        }
        if (result == null) {
            result = actionKey.getSuggestActionMsg();
        }
        return result;
    }

    private int getSearchIconId() {
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.searchViewSearchIcon, outValue, true);
        return outValue.resourceId;
    }

    private CharSequence getDecoratedHint(CharSequence hintText) {
        if (this.mIconifiedByDefault) {
            SpannableStringBuilder ssb = new SpannableStringBuilder("   ");
            ssb.append(hintText);
            Drawable searchIcon = getContext().getResources().getDrawable(getSearchIconId());
            int textSize = (int) (this.mQueryTextView.getTextSize() * 1.25d);
            searchIcon.setBounds(0, 0, textSize, textSize);
            ssb.setSpan(new ImageSpan(searchIcon), 1, 2, 33);
            return ssb;
        }
        return hintText;
    }

    private void updateQueryHint() {
        if (this.mQueryHint != null) {
            this.mQueryTextView.setHint(getDecoratedHint(this.mQueryHint));
        } else if (this.mSearchable != null) {
            CharSequence hint = null;
            int hintId = this.mSearchable.getHintId();
            if (hintId != 0) {
                hint = getContext().getString(hintId);
            }
            if (hint != null) {
                this.mQueryTextView.setHint(getDecoratedHint(hint));
            }
        } else {
            this.mQueryTextView.setHint(getDecoratedHint(""));
        }
    }

    private void updateSearchAutoComplete() {
        this.mQueryTextView.setDropDownAnimationStyle(0);
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
        if (this.mSuggestionsAdapter != null) {
            this.mSuggestionsAdapter.changeCursor(null);
        }
        if (this.mSearchable.getSuggestAuthority() != null) {
            this.mSuggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
            this.mQueryTextView.setAdapter(this.mSuggestionsAdapter);
            ((SuggestionsAdapter) this.mSuggestionsAdapter).setQueryRefinement(this.mQueryRefinement ? 2 : 1);
        }
    }

    private void updateVoiceButton(boolean empty) {
        int visibility = 8;
        if (this.mVoiceButtonEnabled && !isIconified() && empty) {
            visibility = 0;
            this.mSubmitButton.setVisibility(8);
        }
        this.mVoiceButton.setVisibility(visibility);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTextChanged(CharSequence newText) {
        CharSequence text = this.mQueryTextView.getText();
        this.mUserQuery = text;
        boolean hasText = !TextUtils.isEmpty(text);
        updateSubmitButton(hasText);
        updateVoiceButton(!hasText);
        updateCloseButton();
        updateSubmitArea();
        if (this.mOnQueryChangeListener != null && !TextUtils.equals(newText, this.mOldQueryText)) {
            this.mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        this.mOldQueryText = newText.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubmitQuery() {
        CharSequence query = this.mQueryTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (this.mOnQueryChangeListener == null || !this.mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
                if (this.mSearchable != null) {
                    launchQuerySearch(0, null, query.toString());
                    setImeVisibility(false);
                }
                dismissSuggestions();
            }
        }
    }

    private void dismissSuggestions() {
        this.mQueryTextView.dismissDropDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCloseClicked() {
        CharSequence text = this.mQueryTextView.getText();
        if (TextUtils.isEmpty(text)) {
            if (this.mIconifiedByDefault) {
                if (this.mOnCloseListener == null || !this.mOnCloseListener.onClose()) {
                    clearFocus();
                    updateViewsVisibility(true);
                    return;
                }
                return;
            }
            return;
        }
        this.mQueryTextView.setText("");
        this.mQueryTextView.requestFocus();
        setImeVisibility(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearchClicked() {
        updateViewsVisibility(false);
        this.mQueryTextView.requestFocus();
        setImeVisibility(true);
        if (this.mOnSearchClickListener != null) {
            this.mOnSearchClickListener.onClick(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceClicked() {
        if (this.mSearchable == null) {
            return;
        }
        SearchableInfo searchable = this.mSearchable;
        try {
            if (searchable.getVoiceSearchLaunchWebSearch()) {
                Intent webSearchIntent = createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, searchable);
                getContext().startActivity(webSearchIntent);
            } else if (searchable.getVoiceSearchLaunchRecognizer()) {
                Intent appSearchIntent = createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, searchable);
                getContext().startActivity(appSearchIntent);
            }
        } catch (ActivityNotFoundException e) {
            Log.w(LOG_TAG, "Could not find voice search activity");
        }
    }

    void onTextFocusChanged() {
        updateViewsVisibility(isIconified());
        postUpdateFocusedState();
        if (this.mQueryTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        postUpdateFocusedState();
    }

    public void onActionViewCollapsed() {
        setQuery("", false);
        clearFocus();
        updateViewsVisibility(true);
        this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }

    @Override // android.view.CollapsibleActionView
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

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(SearchView.class.getName());
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(SearchView.class.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustDropDownSizeAndPosition() {
        int offset;
        if (this.mDropDownAnchor.getWidth() > 1) {
            Resources res = getContext().getResources();
            int anchorPadding = this.mSearchPlate.getPaddingLeft();
            Rect dropDownPadding = new Rect();
            boolean isLayoutRtl = isLayoutRtl();
            int iconOffset = this.mIconifiedByDefault ? res.getDimensionPixelSize(R.dimen.dropdownitem_icon_width) + res.getDimensionPixelSize(R.dimen.dropdownitem_text_padding_left) : 0;
            this.mQueryTextView.getDropDownBackground().getPadding(dropDownPadding);
            if (isLayoutRtl) {
                offset = -dropDownPadding.left;
            } else {
                offset = anchorPadding - (dropDownPadding.left + iconOffset);
            }
            this.mQueryTextView.setDropDownHorizontalOffset(offset);
            int width = (((this.mDropDownAnchor.getWidth() + dropDownPadding.left) + dropDownPadding.right) + iconOffset) - anchorPadding;
            this.mQueryTextView.setDropDownWidth(width);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemClicked(int position, int actionKey, String actionMsg) {
        if (this.mOnSuggestionListener == null || !this.mOnSuggestionListener.onSuggestionClick(position)) {
            launchSuggestion(position, 0, null);
            setImeVisibility(false);
            dismissSuggestions();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemSelected(int position) {
        if (this.mOnSuggestionListener == null || !this.mOnSuggestionListener.onSuggestionSelect(position)) {
            rewriteQueryFromSuggestion(position);
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        this.mQueryTextView.setLayoutDirection(layoutDirection);
    }

    private void rewriteQueryFromSuggestion(int position) {
        CharSequence oldQuery = this.mQueryTextView.getText();
        Cursor c = this.mSuggestionsAdapter.getCursor();
        if (c == null) {
            return;
        }
        if (c.moveToPosition(position)) {
            CharSequence newQuery = this.mSuggestionsAdapter.convertToString(c);
            if (newQuery != null) {
                setQuery(newQuery);
                return;
            } else {
                setQuery(oldQuery);
                return;
            }
        }
        setQuery(oldQuery);
    }

    private boolean launchSuggestion(int position, int actionKey, String actionMsg) {
        Cursor c = this.mSuggestionsAdapter.getCursor();
        if (c != null && c.moveToPosition(position)) {
            Intent intent = createIntentFromSuggestion(c, actionKey, actionMsg);
            launchIntent(intent);
            return true;
        }
        return false;
    }

    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            getContext().startActivity(intent);
        } catch (RuntimeException ex) {
            Log.e(LOG_TAG, "Failed launch activity: " + intent, ex);
        }
    }

    private void setQuery(CharSequence query) {
        this.mQueryTextView.setText(query, true);
        this.mQueryTextView.setSelection(TextUtils.isEmpty(query) ? 0 : query.length());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchQuerySearch(int actionKey, String actionMsg, String query) {
        Intent intent = createIntent(Intent.ACTION_SEARCH, null, null, query, actionKey, actionMsg);
        getContext().startActivity(intent);
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

    private Intent createVoiceWebSearchIntent(Intent baseIntent, SearchableInfo searchable) {
        Intent voiceIntent = new Intent(baseIntent);
        ComponentName searchActivity = searchable.getSearchActivity();
        voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, searchActivity == null ? null : searchActivity.flattenToShortString());
        return voiceIntent;
    }

    private Intent createVoiceAppSearchIntent(Intent baseIntent, SearchableInfo searchable) {
        ComponentName searchActivity = searchable.getSearchActivity();
        Intent queryIntent = new Intent(Intent.ACTION_SEARCH);
        queryIntent.setComponent(searchActivity);
        PendingIntent pending = PendingIntent.getActivity(getContext(), 0, queryIntent, 1073741824);
        Bundle queryExtras = new Bundle();
        if (this.mAppSearchData != null) {
            queryExtras.putParcelable(SearchManager.APP_DATA, this.mAppSearchData);
        }
        Intent voiceIntent = new Intent(baseIntent);
        String languageModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
        String prompt = null;
        String language = null;
        int maxResults = 1;
        Resources resources = getResources();
        if (searchable.getVoiceLanguageModeId() != 0) {
            languageModel = resources.getString(searchable.getVoiceLanguageModeId());
        }
        if (searchable.getVoicePromptTextId() != 0) {
            prompt = resources.getString(searchable.getVoicePromptTextId());
        }
        if (searchable.getVoiceLanguageId() != 0) {
            language = resources.getString(searchable.getVoiceLanguageId());
        }
        if (searchable.getVoiceMaxResults() != 0) {
            maxResults = searchable.getVoiceMaxResults();
        }
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, searchActivity == null ? null : searchActivity.flattenToShortString());
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, pending);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT_BUNDLE, queryExtras);
        return voiceIntent;
    }

    private Intent createIntentFromSuggestion(Cursor c, int actionKey, String actionMsg) {
        int rowNum;
        String id;
        try {
            String action = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_ACTION);
            if (action == null) {
                action = this.mSearchable.getSuggestIntentAction();
            }
            if (action == null) {
                action = Intent.ACTION_SEARCH;
            }
            String data = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA);
            if (data == null) {
                data = this.mSearchable.getSuggestIntentData();
            }
            if (data != null && (id = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID)) != null) {
                data = data + Separators.SLASH + Uri.encode(id);
            }
            Uri dataUri = data == null ? null : Uri.parse(data);
            String query = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_QUERY);
            String extraData = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);
            return createIntent(action, dataUri, extraData, query, actionKey, actionMsg);
        } catch (RuntimeException e) {
            try {
                rowNum = c.getPosition();
            } catch (RuntimeException e2) {
                rowNum = -1;
            }
            Log.w(LOG_TAG, "Search suggestions cursor at row " + rowNum + " returned exception.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceSuggestionQuery() {
        this.mQueryTextView.doBeforeTextChanged();
        this.mQueryTextView.doAfterTextChanged();
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    /* loaded from: SearchView$SearchAutoComplete.class */
    public static class SearchAutoComplete extends AutoCompleteTextView {
        private int mThreshold;
        private SearchView mSearchView;

        public SearchAutoComplete(Context context) {
            super(context);
            this.mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.mThreshold = getThreshold();
        }

        void setSearchView(SearchView searchView) {
            this.mSearchView = searchView;
        }

        @Override // android.widget.AutoCompleteTextView
        public void setThreshold(int threshold) {
            super.setThreshold(threshold);
            this.mThreshold = threshold;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEmpty() {
            return TextUtils.getTrimmedLength(getText()) == 0;
        }

        @Override // android.widget.AutoCompleteTextView
        protected void replaceText(CharSequence text) {
        }

        @Override // android.widget.AutoCompleteTextView
        public void performCompletion() {
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus && this.mSearchView.hasFocus() && getVisibility() == 0) {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(this, 0);
                if (SearchView.isLandscapeMode(getContext())) {
                    ensureImeVisible(true);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            this.mSearchView.onTextFocusChanged();
        }

        @Override // android.widget.AutoCompleteTextView
        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                        return true;
                    }
                    return true;
                } else if (event.getAction() == 1) {
                    KeyEvent.DispatcherState state2 = getKeyDispatcherState();
                    if (state2 != null) {
                        state2.handleUpEvent(event);
                    }
                    if (event.isTracking() && !event.isCanceled()) {
                        this.mSearchView.clearFocus();
                        this.mSearchView.setImeVisibility(false);
                        return true;
                    }
                }
            }
            return super.onKeyPreIme(keyCode, event);
        }
    }
}