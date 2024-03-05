package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.PopupWindow;
import com.android.internal.R;

/* loaded from: AutoCompleteTextView.class */
public class AutoCompleteTextView extends EditText implements Filter.FilterListener {
    static final boolean DEBUG = false;
    static final String TAG = "AutoCompleteTextView";
    static final int EXPAND_MAX = 3;
    private CharSequence mHintText;
    private TextView mHintView;
    private int mHintResource;
    private ListAdapter mAdapter;
    private Filter mFilter;
    private int mThreshold;
    private ListPopupWindow mPopup;
    private int mDropDownAnchorId;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    private boolean mDropDownDismissedOnCompletion;
    private int mLastKeyCode;
    private boolean mOpenBefore;
    private Validator mValidator;
    private boolean mBlockCompletion;
    private boolean mPopupCanBeUpdated;
    private PassThroughClickListener mPassThroughClickListener;
    private PopupDataSetObserver mObserver;

    /* loaded from: AutoCompleteTextView$OnDismissListener.class */
    public interface OnDismissListener {
        void onDismiss();
    }

    /* loaded from: AutoCompleteTextView$Validator.class */
    public interface Validator {
        boolean isValid(CharSequence charSequence);

        CharSequence fixText(CharSequence charSequence);
    }

    public AutoCompleteTextView(Context context) {
        this(context, null);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842859);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDropDownDismissedOnCompletion = true;
        this.mLastKeyCode = 0;
        this.mValidator = null;
        this.mPopupCanBeUpdated = true;
        this.mPopup = new ListPopupWindow(context, attrs, 16842859);
        this.mPopup.setSoftInputMode(16);
        this.mPopup.setPromptPosition(1);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoCompleteTextView, defStyle, 0);
        this.mThreshold = a.getInt(2, 2);
        this.mPopup.setListSelector(a.getDrawable(3));
        this.mPopup.setVerticalOffset((int) a.getDimension(9, 0.0f));
        this.mPopup.setHorizontalOffset((int) a.getDimension(8, 0.0f));
        this.mDropDownAnchorId = a.getResourceId(6, -1);
        this.mPopup.setWidth(a.getLayoutDimension(5, -2));
        this.mPopup.setHeight(a.getLayoutDimension(7, -2));
        this.mHintResource = a.getResourceId(1, R.layout.simple_dropdown_hint);
        this.mPopup.setOnItemClickListener(new DropDownItemClickListener());
        setCompletionHint(a.getText(0));
        int inputType = getInputType();
        if ((inputType & 15) == 1) {
            setRawInputType(inputType | 65536);
        }
        a.recycle();
        setFocusable(true);
        addTextChangedListener(new MyWatcher());
        this.mPassThroughClickListener = new PassThroughClickListener();
        super.setOnClickListener(this.mPassThroughClickListener);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener listener) {
        this.mPassThroughClickListener.mWrapped = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickImpl() {
        if (isPopupShowing()) {
            ensureImeVisible(true);
        }
    }

    public void setCompletionHint(CharSequence hint) {
        this.mHintText = hint;
        if (hint != null) {
            if (this.mHintView == null) {
                TextView hintView = (TextView) LayoutInflater.from(getContext()).inflate(this.mHintResource, (ViewGroup) null).findViewById(16908308);
                hintView.setText(this.mHintText);
                this.mHintView = hintView;
                this.mPopup.setPromptView(hintView);
                return;
            }
            this.mHintView.setText(hint);
            return;
        }
        this.mPopup.setPromptView(null);
        this.mHintView = null;
    }

    public CharSequence getCompletionHint() {
        return this.mHintText;
    }

    public int getDropDownWidth() {
        return this.mPopup.getWidth();
    }

    public void setDropDownWidth(int width) {
        this.mPopup.setWidth(width);
    }

    public int getDropDownHeight() {
        return this.mPopup.getHeight();
    }

    public void setDropDownHeight(int height) {
        this.mPopup.setHeight(height);
    }

    public int getDropDownAnchor() {
        return this.mDropDownAnchorId;
    }

    public void setDropDownAnchor(int id) {
        this.mDropDownAnchorId = id;
        this.mPopup.setAnchorView(null);
    }

    public Drawable getDropDownBackground() {
        return this.mPopup.getBackground();
    }

    public void setDropDownBackgroundDrawable(Drawable d) {
        this.mPopup.setBackgroundDrawable(d);
    }

    public void setDropDownBackgroundResource(int id) {
        this.mPopup.setBackgroundDrawable(getResources().getDrawable(id));
    }

    public void setDropDownVerticalOffset(int offset) {
        this.mPopup.setVerticalOffset(offset);
    }

    public int getDropDownVerticalOffset() {
        return this.mPopup.getVerticalOffset();
    }

    public void setDropDownHorizontalOffset(int offset) {
        this.mPopup.setHorizontalOffset(offset);
    }

    public int getDropDownHorizontalOffset() {
        return this.mPopup.getHorizontalOffset();
    }

    public void setDropDownAnimationStyle(int animationStyle) {
        this.mPopup.setAnimationStyle(animationStyle);
    }

    public int getDropDownAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mPopup.isDropDownAlwaysVisible();
    }

    public void setDropDownAlwaysVisible(boolean dropDownAlwaysVisible) {
        this.mPopup.setDropDownAlwaysVisible(dropDownAlwaysVisible);
    }

    public boolean isDropDownDismissedOnCompletion() {
        return this.mDropDownDismissedOnCompletion;
    }

    public void setDropDownDismissedOnCompletion(boolean dropDownDismissedOnCompletion) {
        this.mDropDownDismissedOnCompletion = dropDownDismissedOnCompletion;
    }

    public int getThreshold() {
        return this.mThreshold;
    }

    public void setThreshold(int threshold) {
        if (threshold <= 0) {
            threshold = 1;
        }
        this.mThreshold = threshold;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener l) {
        this.mItemSelectedListener = l;
    }

    @Deprecated
    public AdapterView.OnItemClickListener getItemClickListener() {
        return this.mItemClickListener;
    }

    @Deprecated
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return this.mItemClickListener;
    }

    public AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public void setOnDismissListener(final OnDismissListener dismissListener) {
        PopupWindow.OnDismissListener wrappedListener = null;
        if (dismissListener != null) {
            wrappedListener = new PopupWindow.OnDismissListener() { // from class: android.widget.AutoCompleteTextView.1
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    dismissListener.onDismiss();
                }
            };
        }
        this.mPopup.setOnDismissListener(wrappedListener);
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        if (this.mObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mObserver);
        }
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            this.mFilter = ((Filterable) this.mAdapter).getFilter();
            adapter.registerDataSetObserver(this.mObserver);
        } else {
            this.mFilter = null;
        }
        this.mPopup.setAdapter(this.mAdapter);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == 4 && isPopupShowing() && !this.mPopup.isDropDownAlwaysVisible()) {
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
                    dismissDropDown();
                    return true;
                }
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean consumed = this.mPopup.onKeyUp(keyCode, event);
        if (consumed) {
            switch (keyCode) {
                case 23:
                case 61:
                case 66:
                    if (event.hasNoModifiers()) {
                        performCompletion();
                        return true;
                    }
                    return true;
            }
        }
        if (isPopupShowing() && keyCode == 61 && event.hasNoModifiers()) {
            performCompletion();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mPopup.onKeyDown(keyCode, event)) {
            return true;
        }
        if (!isPopupShowing()) {
            switch (keyCode) {
                case 20:
                    if (event.hasNoModifiers()) {
                        performValidation();
                        break;
                    }
                    break;
            }
        }
        if (isPopupShowing() && keyCode == 61 && event.hasNoModifiers()) {
            return true;
        }
        this.mLastKeyCode = keyCode;
        boolean handled = super.onKeyDown(keyCode, event);
        this.mLastKeyCode = 0;
        if (handled && isPopupShowing()) {
            clearListSelection();
        }
        return handled;
    }

    public boolean enoughToFilter() {
        return getText().length() >= this.mThreshold;
    }

    /* loaded from: AutoCompleteTextView$MyWatcher.class */
    private class MyWatcher implements TextWatcher {
        private MyWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            AutoCompleteTextView.this.doAfterTextChanged();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            AutoCompleteTextView.this.doBeforeTextChanged();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doBeforeTextChanged() {
        if (this.mBlockCompletion) {
            return;
        }
        this.mOpenBefore = isPopupShowing();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doAfterTextChanged() {
        if (this.mBlockCompletion) {
            return;
        }
        if (this.mOpenBefore && !isPopupShowing()) {
            return;
        }
        if (enoughToFilter()) {
            if (this.mFilter != null) {
                this.mPopupCanBeUpdated = true;
                performFiltering(getText(), this.mLastKeyCode);
                return;
            }
            return;
        }
        if (!this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
        if (this.mFilter != null) {
            this.mFilter.filter(null);
        }
    }

    public boolean isPopupShowing() {
        return this.mPopup.isShowing();
    }

    protected CharSequence convertSelectionToString(Object selectedItem) {
        return this.mFilter.convertResultToString(selectedItem);
    }

    public void clearListSelection() {
        this.mPopup.clearListSelection();
    }

    public void setListSelection(int position) {
        this.mPopup.setSelection(position);
    }

    public int getListSelection() {
        return this.mPopup.getSelectedItemPosition();
    }

    protected void performFiltering(CharSequence text, int keyCode) {
        this.mFilter.filter(text, this);
    }

    public void performCompletion() {
        performCompletion(null, -1, -1L);
    }

    @Override // android.widget.TextView
    public void onCommitCompletion(CompletionInfo completion) {
        if (isPopupShowing()) {
            this.mPopup.performItemClick(completion.getPosition());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performCompletion(View selectedView, int position, long id) {
        Object selectedItem;
        if (isPopupShowing()) {
            if (position < 0) {
                selectedItem = this.mPopup.getSelectedItem();
            } else {
                selectedItem = this.mAdapter.getItem(position);
            }
            if (selectedItem == null) {
                Log.w(TAG, "performCompletion: no selected item");
                return;
            }
            this.mBlockCompletion = true;
            replaceText(convertSelectionToString(selectedItem));
            this.mBlockCompletion = false;
            if (this.mItemClickListener != null) {
                ListPopupWindow list = this.mPopup;
                if (selectedView == null || position < 0) {
                    selectedView = list.getSelectedView();
                    position = list.getSelectedItemPosition();
                    id = list.getSelectedItemId();
                }
                this.mItemClickListener.onItemClick(list.getListView(), selectedView, position, id);
            }
        }
        if (this.mDropDownDismissedOnCompletion && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    public boolean isPerformingCompletion() {
        return this.mBlockCompletion;
    }

    public void setText(CharSequence text, boolean filter) {
        if (filter) {
            setText(text);
            return;
        }
        this.mBlockCompletion = true;
        setText(text);
        this.mBlockCompletion = false;
    }

    protected void replaceText(CharSequence text) {
        clearComposingText();
        setText(text);
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }

    @Override // android.widget.Filter.FilterListener
    public void onFilterComplete(int count) {
        updateDropDownForFilter(count);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDropDownForFilter(int count) {
        if (getWindowVisibility() == 8) {
            return;
        }
        boolean dropDownAlwaysVisible = this.mPopup.isDropDownAlwaysVisible();
        boolean enoughToFilter = enoughToFilter();
        if ((count > 0 || dropDownAlwaysVisible) && enoughToFilter) {
            if (hasFocus() && hasWindowFocus() && this.mPopupCanBeUpdated) {
                showDropDown();
            }
        } else if (!dropDownAlwaysVisible && isPopupShowing()) {
            dismissDropDown();
            this.mPopupCanBeUpdated = true;
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
        switch (hint) {
            case 4:
                if (!this.mPopup.isDropDownAlwaysVisible()) {
                    dismissDropDown();
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused) {
            performValidation();
        }
        if (!focused && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDetachedFromWindow() {
        dismissDropDown();
        super.onDetachedFromWindow();
    }

    public void dismissDropDown() {
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (imm != null) {
            imm.displayCompletions(this, null);
        }
        this.mPopup.dismiss();
        this.mPopupCanBeUpdated = false;
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean result = super.setFrame(l, t, r, b);
        if (isPopupShowing()) {
            showDropDown();
        }
        return result;
    }

    public void showDropDownAfterLayout() {
        this.mPopup.postShow();
    }

    public void ensureImeVisible(boolean visible) {
        this.mPopup.setInputMethodMode(visible ? 1 : 2);
        if (this.mPopup.isDropDownAlwaysVisible() || (this.mFilter != null && enoughToFilter())) {
            showDropDown();
        }
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public void showDropDown() {
        buildImeCompletions();
        if (this.mPopup.getAnchorView() == null) {
            if (this.mDropDownAnchorId != -1) {
                this.mPopup.setAnchorView(getRootView().findViewById(this.mDropDownAnchorId));
            } else {
                this.mPopup.setAnchorView(this);
            }
        }
        if (!isPopupShowing()) {
            this.mPopup.setInputMethodMode(1);
            this.mPopup.setListItemExpandMax(3);
        }
        this.mPopup.show();
        this.mPopup.getListView().setOverScrollMode(0);
    }

    public void setForceIgnoreOutsideTouch(boolean forceIgnoreOutsideTouch) {
        this.mPopup.setForceIgnoreOutsideTouch(forceIgnoreOutsideTouch);
    }

    private void buildImeCompletions() {
        InputMethodManager imm;
        ListAdapter adapter = this.mAdapter;
        if (adapter != null && (imm = InputMethodManager.peekInstance()) != null) {
            int count = Math.min(adapter.getCount(), 20);
            CompletionInfo[] completions = new CompletionInfo[count];
            int realCount = 0;
            for (int i = 0; i < count; i++) {
                if (adapter.isEnabled(i)) {
                    Object item = adapter.getItem(i);
                    long id = adapter.getItemId(i);
                    completions[realCount] = new CompletionInfo(id, realCount, convertSelectionToString(item));
                    realCount++;
                }
            }
            if (realCount != count) {
                CompletionInfo[] tmp = new CompletionInfo[realCount];
                System.arraycopy(completions, 0, tmp, 0, realCount);
                completions = tmp;
            }
            imm.displayCompletions(this, completions);
        }
    }

    public void setValidator(Validator validator) {
        this.mValidator = validator;
    }

    public Validator getValidator() {
        return this.mValidator;
    }

    public void performValidation() {
        if (this.mValidator == null) {
            return;
        }
        CharSequence text = getText();
        if (!TextUtils.isEmpty(text) && !this.mValidator.isValid(text)) {
            setText(this.mValidator.fixText(text));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Filter getFilter() {
        return this.mFilter;
    }

    /* loaded from: AutoCompleteTextView$DropDownItemClickListener.class */
    private class DropDownItemClickListener implements AdapterView.OnItemClickListener {
        private DropDownItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            AutoCompleteTextView.this.performCompletion(v, position, id);
        }
    }

    /* loaded from: AutoCompleteTextView$PassThroughClickListener.class */
    private class PassThroughClickListener implements View.OnClickListener {
        private View.OnClickListener mWrapped;

        private PassThroughClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            AutoCompleteTextView.this.onClickImpl();
            if (this.mWrapped != null) {
                this.mWrapped.onClick(v);
            }
        }
    }

    /* loaded from: AutoCompleteTextView$PopupDataSetObserver.class */
    private class PopupDataSetObserver extends DataSetObserver {
        private PopupDataSetObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            if (AutoCompleteTextView.this.mAdapter != null) {
                AutoCompleteTextView.this.post(new Runnable() { // from class: android.widget.AutoCompleteTextView.PopupDataSetObserver.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ListAdapter adapter = AutoCompleteTextView.this.mAdapter;
                        if (adapter != null) {
                            AutoCompleteTextView.this.updateDropDownForFilter(adapter.getCount());
                        }
                    }
                });
            }
        }
    }
}