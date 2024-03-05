package android.webkit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: FindActionModeCallback.class */
public class FindActionModeCallback implements ActionMode.Callback, TextWatcher, View.OnClickListener, WebView.FindListener {
    private View mCustomView;
    private EditText mEditText;
    private TextView mMatches;
    private WebView mWebView;
    private InputMethodManager mInput;
    private Resources mResources;
    private boolean mMatchesFound;
    private int mNumberOfMatches;
    private int mActiveMatchIndex;
    private ActionMode mActionMode;
    private Rect mGlobalVisibleRect = new Rect();
    private Point mGlobalVisibleOffset = new Point();

    public FindActionModeCallback(Context context) {
        this.mCustomView = LayoutInflater.from(context).inflate(R.layout.webview_find, (ViewGroup) null);
        this.mEditText = (EditText) this.mCustomView.findViewById(16908291);
        this.mEditText.setCustomSelectionActionModeCallback(new NoAction());
        this.mEditText.setOnClickListener(this);
        setText("");
        this.mMatches = (TextView) this.mCustomView.findViewById(R.id.matches);
        this.mInput = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.mResources = context.getResources();
    }

    public void finish() {
        this.mActionMode.finish();
    }

    public void setText(String text) {
        this.mEditText.setText(text);
        Spannable span = this.mEditText.getText();
        int length = span.length();
        Selection.setSelection(span, length, length);
        span.setSpan(this, 0, length, 18);
        this.mMatchesFound = false;
    }

    public void setWebView(WebView webView) {
        if (null == webView) {
            throw new AssertionError("WebView supplied to FindActionModeCallback cannot be null");
        }
        this.mWebView = webView;
        this.mWebView.setFindDialogFindListener(this);
    }

    @Override // android.webkit.WebView.FindListener
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        if (isDoneCounting) {
            updateMatchCount(activeMatchOrdinal, numberOfMatches, numberOfMatches == 0);
        }
    }

    private void findNext(boolean next) {
        if (this.mWebView == null) {
            throw new AssertionError("No WebView for FindActionModeCallback::findNext");
        }
        if (!this.mMatchesFound) {
            findAll();
        } else if (0 == this.mNumberOfMatches) {
        } else {
            this.mWebView.findNext(next);
            updateMatchesString();
        }
    }

    public void findAll() {
        if (this.mWebView == null) {
            throw new AssertionError("No WebView for FindActionModeCallback::findAll");
        }
        CharSequence find = this.mEditText.getText();
        if (0 == find.length()) {
            this.mWebView.clearMatches();
            this.mMatches.setVisibility(8);
            this.mMatchesFound = false;
            this.mWebView.findAll(null);
            return;
        }
        this.mMatchesFound = true;
        this.mMatches.setVisibility(4);
        this.mNumberOfMatches = 0;
        this.mWebView.findAllAsync(find.toString());
    }

    public void showSoftInput() {
        this.mInput.startGettingWindowFocus(this.mEditText.getRootView());
        this.mInput.focusIn(this.mEditText);
        this.mInput.showSoftInput(this.mEditText, 0);
    }

    public void updateMatchCount(int matchIndex, int matchCount, boolean isEmptyFind) {
        if (!isEmptyFind) {
            this.mNumberOfMatches = matchCount;
            this.mActiveMatchIndex = matchIndex;
            updateMatchesString();
            return;
        }
        this.mMatches.setVisibility(8);
        this.mNumberOfMatches = 0;
    }

    private void updateMatchesString() {
        if (this.mNumberOfMatches == 0) {
            this.mMatches.setText(R.string.no_matches);
        } else {
            this.mMatches.setText(this.mResources.getQuantityString(R.plurals.matches_found, this.mNumberOfMatches, Integer.valueOf(this.mActiveMatchIndex + 1), Integer.valueOf(this.mNumberOfMatches)));
        }
        this.mMatches.setVisibility(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        findNext(true);
    }

    @Override // android.view.ActionMode.Callback
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (!mode.isUiFocusable()) {
            return false;
        }
        mode.setCustomView(this.mCustomView);
        mode.getMenuInflater().inflate(R.menu.webview_find, menu);
        this.mActionMode = mode;
        Editable edit = this.mEditText.getText();
        Selection.setSelection(edit, edit.length());
        this.mMatches.setVisibility(8);
        this.mMatchesFound = false;
        this.mMatches.setText("0");
        this.mEditText.requestFocus();
        return true;
    }

    @Override // android.view.ActionMode.Callback
    public void onDestroyActionMode(ActionMode mode) {
        this.mActionMode = null;
        this.mWebView.notifyFindDialogDismissed();
        this.mWebView.setFindDialogFindListener(null);
        this.mInput.hideSoftInputFromWindow(this.mWebView.getWindowToken(), 0);
    }

    @Override // android.view.ActionMode.Callback
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override // android.view.ActionMode.Callback
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (this.mWebView == null) {
            throw new AssertionError("No WebView for FindActionModeCallback::onActionItemClicked");
        }
        this.mInput.hideSoftInputFromWindow(this.mWebView.getWindowToken(), 0);
        switch (item.getItemId()) {
            case R.id.find_prev /* 16909188 */:
                findNext(false);
                return true;
            case R.id.find_next /* 16909189 */:
                findNext(true);
                return true;
            default:
                return false;
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        findAll();
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    public int getActionModeGlobalBottom() {
        if (this.mActionMode == null) {
            return 0;
        }
        View view = (View) this.mCustomView.getParent();
        if (view == null) {
            view = this.mCustomView;
        }
        view.getGlobalVisibleRect(this.mGlobalVisibleRect, this.mGlobalVisibleOffset);
        return this.mGlobalVisibleRect.bottom;
    }

    /* loaded from: FindActionModeCallback$NoAction.class */
    public static class NoAction implements ActionMode.Callback {
        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}