package android.widget;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.UndoManager;
import android.content.UndoOperation;
import android.content.UndoOwner;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.UserDictionary;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.WordIterator;
import android.text.style.EasyEditSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.DisplayList;
import android.view.DragEvent;
import android.view.HardwareCanvas;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.EditableInputConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/* loaded from: Editor.class */
public class Editor {
    private static final String TAG = "Editor";
    static final boolean DEBUG_UNDO = false;
    static final int BLINK = 500;
    private static final float[] TEMP_POSITION = new float[2];
    private static int DRAG_SHADOW_MAX_TEXT_LENGTH = 20;
    UndoManager mUndoManager;
    UndoOwner mUndoOwner;
    InputFilter mUndoInputFilter;
    InsertionPointCursorController mInsertionPointCursorController;
    SelectionModifierCursorController mSelectionModifierCursorController;
    ActionMode mSelectionActionMode;
    boolean mInsertionControllerEnabled;
    boolean mSelectionControllerEnabled;
    CorrectionHighlighter mCorrectionHighlighter;
    InputContentType mInputContentType;
    InputMethodState mInputMethodState;
    DisplayList[] mTextDisplayLists;
    boolean mFrozenWithFocus;
    boolean mSelectionMoved;
    boolean mTouchFocusSelected;
    KeyListener mKeyListener;
    boolean mDiscardNextActionUp;
    boolean mIgnoreActionUpEvent;
    long mShowCursor;
    Blink mBlink;
    boolean mSelectAllOnFocus;
    boolean mTextIsSelectable;
    CharSequence mError;
    boolean mErrorWasChanged;
    ErrorPopup mErrorPopup;
    boolean mShowErrorAfterAttach;
    boolean mInBatchEditControllers;
    boolean mPreserveDetachedSelection;
    boolean mTemporaryDetach;
    SuggestionsPopupWindow mSuggestionsPopupWindow;
    SuggestionRangeSpan mSuggestionRangeSpan;
    Runnable mShowSuggestionRunnable;
    int mCursorCount;
    private Drawable mSelectHandleLeft;
    private Drawable mSelectHandleRight;
    private Drawable mSelectHandleCenter;
    private PositionListener mPositionListener;
    float mLastDownPositionX;
    float mLastDownPositionY;
    ActionMode.Callback mCustomSelectionActionModeCallback;
    boolean mCreatedWithASelection;
    private SpanController mSpanController;
    WordIterator mWordIterator;
    SpellChecker mSpellChecker;
    private Rect mTempRect;
    private TextView mTextView;
    static final int EXTRACT_NOTHING = -2;
    static final int EXTRACT_UNKNOWN = -1;
    int mInputType = 0;
    boolean mCursorVisible = true;
    boolean mShowSoftInputOnFocus = true;
    final Drawable[] mCursorDrawable = new Drawable[2];

    /* loaded from: Editor$CursorController.class */
    private interface CursorController extends ViewTreeObserver.OnTouchModeChangeListener {
        void show();

        void hide();

        void onDetached();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$EasyEditDeleteListener.class */
    public interface EasyEditDeleteListener {
        void onDeleteClick(EasyEditSpan easyEditSpan);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$TextViewPositionListener.class */
    public interface TextViewPositionListener {
        void updatePosition(int i, int i2, boolean z, boolean z2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Editor(TextView textView) {
        this.mTextView = textView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAttachedToWindow() {
        if (this.mShowErrorAfterAttach) {
            showError();
            this.mShowErrorAfterAttach = false;
        }
        this.mTemporaryDetach = false;
        ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
        if (this.mInsertionPointCursorController != null) {
            observer.addOnTouchModeChangeListener(this.mInsertionPointCursorController);
        }
        if (this.mSelectionModifierCursorController != null) {
            this.mSelectionModifierCursorController.resetTouchOffsets();
            observer.addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
        }
        updateSpellCheckSpans(0, this.mTextView.getText().length(), true);
        if (this.mTextView.hasTransientState() && this.mTextView.getSelectionStart() != this.mTextView.getSelectionEnd()) {
            this.mTextView.setHasTransientState(false);
            startSelectionActionMode();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetachedFromWindow() {
        if (this.mError != null) {
            hideError();
        }
        if (this.mBlink != null) {
            this.mBlink.removeCallbacks(this.mBlink);
        }
        if (this.mInsertionPointCursorController != null) {
            this.mInsertionPointCursorController.onDetached();
        }
        if (this.mSelectionModifierCursorController != null) {
            this.mSelectionModifierCursorController.onDetached();
        }
        if (this.mShowSuggestionRunnable != null) {
            this.mTextView.removeCallbacks(this.mShowSuggestionRunnable);
        }
        invalidateTextDisplayList();
        if (this.mSpellChecker != null) {
            this.mSpellChecker.closeSession();
            this.mSpellChecker = null;
        }
        this.mPreserveDetachedSelection = true;
        hideControllers();
        this.mPreserveDetachedSelection = false;
        this.mTemporaryDetach = false;
    }

    private void showError() {
        if (this.mTextView.getWindowToken() == null) {
            this.mShowErrorAfterAttach = true;
            return;
        }
        if (this.mErrorPopup == null) {
            LayoutInflater inflater = LayoutInflater.from(this.mTextView.getContext());
            TextView err = (TextView) inflater.inflate(R.layout.textview_hint, (ViewGroup) null);
            float scale = this.mTextView.getResources().getDisplayMetrics().density;
            this.mErrorPopup = new ErrorPopup(err, (int) ((200.0f * scale) + 0.5f), (int) ((50.0f * scale) + 0.5f));
            this.mErrorPopup.setFocusable(false);
            this.mErrorPopup.setInputMethodMode(1);
        }
        TextView tv = (TextView) this.mErrorPopup.getContentView();
        chooseSize(this.mErrorPopup, this.mError, tv);
        tv.setText(this.mError);
        this.mErrorPopup.showAsDropDown(this.mTextView, getErrorX(), getErrorY());
        this.mErrorPopup.fixDirection(this.mErrorPopup.isAboveAnchor());
    }

    public void setError(CharSequence error, Drawable icon) {
        this.mError = TextUtils.stringOrSpannedString(error);
        this.mErrorWasChanged = true;
        if (this.mError == null) {
            setErrorIcon(null);
            if (this.mErrorPopup != null) {
                if (this.mErrorPopup.isShowing()) {
                    this.mErrorPopup.dismiss();
                }
                this.mErrorPopup = null;
                return;
            }
            return;
        }
        setErrorIcon(icon);
        if (this.mTextView.isFocused()) {
            showError();
        }
    }

    private void setErrorIcon(Drawable icon) {
        TextView.Drawables dr = this.mTextView.mDrawables;
        if (dr == null) {
            TextView textView = this.mTextView;
            TextView.Drawables drawables = new TextView.Drawables(this.mTextView.getContext());
            dr = drawables;
            textView.mDrawables = drawables;
        }
        dr.setErrorDrawable(icon, this.mTextView);
        this.mTextView.resetResolvedDrawables();
        this.mTextView.invalidate();
        this.mTextView.requestLayout();
    }

    private void hideError() {
        if (this.mErrorPopup != null && this.mErrorPopup.isShowing()) {
            this.mErrorPopup.dismiss();
        }
        this.mShowErrorAfterAttach = false;
    }

    private int getErrorX() {
        int errorX;
        float scale = this.mTextView.getResources().getDisplayMetrics().density;
        TextView.Drawables dr = this.mTextView.mDrawables;
        int layoutDirection = this.mTextView.getLayoutDirection();
        switch (layoutDirection) {
            case 0:
            default:
                int offset = ((-(dr != null ? dr.mDrawableSizeRight : 0)) / 2) + ((int) ((25.0f * scale) + 0.5f));
                errorX = ((this.mTextView.getWidth() - this.mErrorPopup.getWidth()) - this.mTextView.getPaddingRight()) + offset;
                break;
            case 1:
                int offset2 = ((dr != null ? dr.mDrawableSizeLeft : 0) / 2) - ((int) ((25.0f * scale) + 0.5f));
                errorX = this.mTextView.getPaddingLeft() + offset2;
                break;
        }
        return errorX;
    }

    private int getErrorY() {
        int height;
        int compoundPaddingTop = this.mTextView.getCompoundPaddingTop();
        int vspace = ((this.mTextView.getBottom() - this.mTextView.getTop()) - this.mTextView.getCompoundPaddingBottom()) - compoundPaddingTop;
        TextView.Drawables dr = this.mTextView.mDrawables;
        int layoutDirection = this.mTextView.getLayoutDirection();
        switch (layoutDirection) {
            case 0:
            default:
                height = dr != null ? dr.mDrawableHeightRight : 0;
                break;
            case 1:
                height = dr != null ? dr.mDrawableHeightLeft : 0;
                break;
        }
        int icontop = compoundPaddingTop + ((vspace - height) / 2);
        float scale = this.mTextView.getResources().getDisplayMetrics().density;
        return ((icontop + height) - this.mTextView.getHeight()) - ((int) ((2.0f * scale) + 0.5f));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createInputContentTypeIfNeeded() {
        if (this.mInputContentType == null) {
            this.mInputContentType = new InputContentType();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createInputMethodStateIfNeeded() {
        if (this.mInputMethodState == null) {
            this.mInputMethodState = new InputMethodState();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCursorVisible() {
        return this.mCursorVisible && this.mTextView.isTextEditable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prepareCursorControllers() {
        boolean windowSupportsHandles = false;
        ViewGroup.LayoutParams params = this.mTextView.getRootView().getLayoutParams();
        if (params instanceof WindowManager.LayoutParams) {
            WindowManager.LayoutParams windowParams = (WindowManager.LayoutParams) params;
            windowSupportsHandles = windowParams.type < 1000 || windowParams.type > 1999;
        }
        boolean enabled = windowSupportsHandles && this.mTextView.getLayout() != null;
        this.mInsertionControllerEnabled = enabled && isCursorVisible();
        this.mSelectionControllerEnabled = enabled && this.mTextView.textCanBeSelected();
        if (!this.mInsertionControllerEnabled) {
            hideInsertionPointCursorController();
            if (this.mInsertionPointCursorController != null) {
                this.mInsertionPointCursorController.onDetached();
                this.mInsertionPointCursorController = null;
            }
        }
        if (!this.mSelectionControllerEnabled) {
            stopSelectionActionMode();
            if (this.mSelectionModifierCursorController != null) {
                this.mSelectionModifierCursorController.onDetached();
                this.mSelectionModifierCursorController = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideInsertionPointCursorController() {
        if (this.mInsertionPointCursorController != null) {
            this.mInsertionPointCursorController.hide();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideControllers() {
        hideCursorControllers();
        hideSpanControllers();
    }

    private void hideSpanControllers() {
        if (this.mSpanController != null) {
            this.mSpanController.hide();
        }
    }

    private void hideCursorControllers() {
        if (this.mSuggestionsPopupWindow != null && !this.mSuggestionsPopupWindow.isShowingUp()) {
            this.mSuggestionsPopupWindow.hide();
        }
        hideInsertionPointCursorController();
        stopSelectionActionMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSpellCheckSpans(int start, int end, boolean createSpellChecker) {
        this.mTextView.removeAdjacentSuggestionSpans(start);
        this.mTextView.removeAdjacentSuggestionSpans(end);
        if (this.mTextView.isTextEditable() && this.mTextView.isSuggestionsEnabled() && !(this.mTextView instanceof ExtractEditText)) {
            if (this.mSpellChecker == null && createSpellChecker) {
                this.mSpellChecker = new SpellChecker(this.mTextView);
            }
            if (this.mSpellChecker != null) {
                this.mSpellChecker.spellCheck(start, end);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onScreenStateChanged(int screenState) {
        switch (screenState) {
            case 0:
                suspendBlink();
                return;
            case 1:
                resumeBlink();
                return;
            default:
                return;
        }
    }

    private void suspendBlink() {
        if (this.mBlink != null) {
            this.mBlink.cancel();
        }
    }

    private void resumeBlink() {
        if (this.mBlink != null) {
            this.mBlink.uncancel();
            makeBlink();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustInputType(boolean password, boolean passwordInputType, boolean webPasswordInputType, boolean numberPasswordInputType) {
        if ((this.mInputType & 15) == 1) {
            if (password || passwordInputType) {
                this.mInputType = (this.mInputType & (-4081)) | 128;
            }
            if (webPasswordInputType) {
                this.mInputType = (this.mInputType & (-4081)) | 224;
            }
        } else if ((this.mInputType & 15) == 2 && numberPasswordInputType) {
            this.mInputType = (this.mInputType & (-4081)) | 16;
        }
    }

    private void chooseSize(PopupWindow pop, CharSequence text, TextView tv) {
        int wid = tv.getPaddingLeft() + tv.getPaddingRight();
        int ht = tv.getPaddingTop() + tv.getPaddingBottom();
        int defaultWidthInPixels = this.mTextView.getResources().getDimensionPixelSize(R.dimen.textview_error_popup_default_width);
        Layout l = new StaticLayout(text, tv.getPaint(), defaultWidthInPixels, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        float max = 0.0f;
        for (int i = 0; i < l.getLineCount(); i++) {
            max = Math.max(max, l.getLineWidth(i));
        }
        pop.setWidth(wid + ((int) Math.ceil(max)));
        pop.setHeight(ht + l.getHeight());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFrame() {
        if (this.mErrorPopup != null) {
            TextView tv = (TextView) this.mErrorPopup.getContentView();
            chooseSize(this.mErrorPopup, this.mError, tv);
            this.mErrorPopup.update(this.mTextView, getErrorX(), getErrorY(), this.mErrorPopup.getWidth(), this.mErrorPopup.getHeight());
        }
    }

    private boolean canSelectText() {
        return hasSelectionController() && this.mTextView.getText().length() != 0;
    }

    private boolean hasPasswordTransformationMethod() {
        return this.mTextView.getTransformationMethod() instanceof PasswordTransformationMethod;
    }

    private boolean selectCurrentWord() {
        int selectionStart;
        int selectionEnd;
        if (!canSelectText()) {
            return false;
        }
        if (hasPasswordTransformationMethod()) {
            return this.mTextView.selectAllText();
        }
        int inputType = this.mTextView.getInputType();
        int klass = inputType & 15;
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        if (klass == 2 || klass == 3 || klass == 4 || variation == 16 || variation == 32 || variation == 208 || variation == 176) {
            return this.mTextView.selectAllText();
        }
        long lastTouchOffsets = getLastTouchOffsets();
        int minOffset = TextUtils.unpackRangeStartFromLong(lastTouchOffsets);
        int maxOffset = TextUtils.unpackRangeEndFromLong(lastTouchOffsets);
        if (minOffset < 0 || minOffset >= this.mTextView.getText().length() || maxOffset < 0 || maxOffset >= this.mTextView.getText().length()) {
            return false;
        }
        URLSpan[] urlSpans = (URLSpan[]) ((Spanned) this.mTextView.getText()).getSpans(minOffset, maxOffset, URLSpan.class);
        if (urlSpans.length >= 1) {
            URLSpan urlSpan = urlSpans[0];
            selectionStart = ((Spanned) this.mTextView.getText()).getSpanStart(urlSpan);
            selectionEnd = ((Spanned) this.mTextView.getText()).getSpanEnd(urlSpan);
        } else {
            WordIterator wordIterator = getWordIterator();
            wordIterator.setCharSequence(this.mTextView.getText(), minOffset, maxOffset);
            selectionStart = wordIterator.getBeginning(minOffset);
            selectionEnd = wordIterator.getEnd(maxOffset);
            if (selectionStart == -1 || selectionEnd == -1 || selectionStart == selectionEnd) {
                long range = getCharRange(minOffset);
                selectionStart = TextUtils.unpackRangeStartFromLong(range);
                selectionEnd = TextUtils.unpackRangeEndFromLong(range);
            }
        }
        Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart, selectionEnd);
        return selectionEnd > selectionStart;
    }

    void onLocaleChanged() {
        this.mWordIterator = null;
    }

    public WordIterator getWordIterator() {
        if (this.mWordIterator == null) {
            this.mWordIterator = new WordIterator(this.mTextView.getTextServicesLocale());
        }
        return this.mWordIterator;
    }

    private long getCharRange(int offset) {
        int textLength = this.mTextView.getText().length();
        if (offset + 1 < textLength) {
            char currentChar = this.mTextView.getText().charAt(offset);
            char nextChar = this.mTextView.getText().charAt(offset + 1);
            if (Character.isSurrogatePair(currentChar, nextChar)) {
                return TextUtils.packRangeInLong(offset, offset + 2);
            }
        }
        if (offset < textLength) {
            return TextUtils.packRangeInLong(offset, offset + 1);
        }
        if (offset - 2 >= 0) {
            char previousChar = this.mTextView.getText().charAt(offset - 1);
            char previousPreviousChar = this.mTextView.getText().charAt(offset - 2);
            if (Character.isSurrogatePair(previousPreviousChar, previousChar)) {
                return TextUtils.packRangeInLong(offset - 2, offset);
            }
        }
        if (offset - 1 >= 0) {
            return TextUtils.packRangeInLong(offset - 1, offset);
        }
        return TextUtils.packRangeInLong(offset, offset);
    }

    private boolean touchPositionIsInSelection() {
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            return false;
        }
        if (selectionStart > selectionEnd) {
            selectionStart = selectionEnd;
            selectionEnd = selectionStart;
            Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart, selectionEnd);
        }
        SelectionModifierCursorController selectionController = getSelectionController();
        int minOffset = selectionController.getMinTouchOffset();
        int maxOffset = selectionController.getMaxTouchOffset();
        return minOffset >= selectionStart && maxOffset < selectionEnd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PositionListener getPositionListener() {
        if (this.mPositionListener == null) {
            this.mPositionListener = new PositionListener();
        }
        return this.mPositionListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public boolean isPositionVisible(int positionX, int positionY) {
        synchronized (TEMP_POSITION) {
            float[] position = TEMP_POSITION;
            position[0] = positionX;
            position[1] = positionY;
            View view = this.mTextView;
            while (view != null) {
                if (view != this.mTextView) {
                    position[0] = position[0] - view.getScrollX();
                    position[1] = position[1] - view.getScrollY();
                }
                if (position[0] < 0.0f || position[1] < 0.0f || position[0] > view.getWidth() || position[1] > view.getHeight()) {
                    return false;
                }
                if (!view.getMatrix().isIdentity()) {
                    view.getMatrix().mapPoints(position);
                }
                position[0] = position[0] + view.getLeft();
                position[1] = position[1] + view.getTop();
                ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    view = (View) parent;
                } else {
                    view = null;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isOffsetVisible(int offset) {
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return false;
        }
        int line = layout.getLineForOffset(offset);
        int lineBottom = layout.getLineBottom(line);
        int primaryHorizontal = (int) layout.getPrimaryHorizontal(offset);
        return isPositionVisible(primaryHorizontal + this.mTextView.viewportToContentHorizontalOffset(), lineBottom + this.mTextView.viewportToContentVerticalOffset());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPositionOnText(float x, float y) {
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return false;
        }
        int line = this.mTextView.getLineAtCoordinate(y);
        float x2 = this.mTextView.convertToLocalHorizontalCoordinate(x);
        return x2 >= layout.getLineLeft(line) && x2 <= layout.getLineRight(line);
    }

    public boolean performLongClick(boolean handled) {
        if (!handled && !isPositionOnText(this.mLastDownPositionX, this.mLastDownPositionY) && this.mInsertionControllerEnabled) {
            int offset = this.mTextView.getOffsetForPosition(this.mLastDownPositionX, this.mLastDownPositionY);
            stopSelectionActionMode();
            Selection.setSelection((Spannable) this.mTextView.getText(), offset);
            getInsertionController().showWithActionPopup();
            handled = true;
        }
        if (!handled && this.mSelectionActionMode != null) {
            if (touchPositionIsInSelection()) {
                int start = this.mTextView.getSelectionStart();
                int end = this.mTextView.getSelectionEnd();
                CharSequence selectedText = this.mTextView.getTransformedText(start, end);
                ClipData data = ClipData.newPlainText(null, selectedText);
                DragLocalState localState = new DragLocalState(this.mTextView, start, end);
                this.mTextView.startDrag(data, getTextThumbnailBuilder(selectedText), localState, 0);
                stopSelectionActionMode();
            } else {
                getSelectionController().hide();
                selectCurrentWord();
                getSelectionController().show();
            }
            handled = true;
        }
        if (!handled) {
            handled = startSelectionActionMode();
        }
        return handled;
    }

    private long getLastTouchOffsets() {
        SelectionModifierCursorController selectionController = getSelectionController();
        int minOffset = selectionController.getMinTouchOffset();
        int maxOffset = selectionController.getMaxTouchOffset();
        return TextUtils.packRangeInLong(minOffset, maxOffset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFocusChanged(boolean focused, int direction) {
        this.mShowCursor = SystemClock.uptimeMillis();
        ensureEndedBatchEdit();
        if (focused) {
            int selStart = this.mTextView.getSelectionStart();
            int selEnd = this.mTextView.getSelectionEnd();
            boolean isFocusHighlighted = this.mSelectAllOnFocus && selStart == 0 && selEnd == this.mTextView.getText().length();
            this.mCreatedWithASelection = this.mFrozenWithFocus && this.mTextView.hasSelection() && !isFocusHighlighted;
            if (!this.mFrozenWithFocus || selStart < 0 || selEnd < 0) {
                int lastTapPosition = getLastTapPosition();
                if (lastTapPosition >= 0) {
                    Selection.setSelection((Spannable) this.mTextView.getText(), lastTapPosition);
                }
                MovementMethod mMovement = this.mTextView.getMovementMethod();
                if (mMovement != null) {
                    mMovement.onTakeFocus(this.mTextView, (Spannable) this.mTextView.getText(), direction);
                }
                if (((this.mTextView instanceof ExtractEditText) || this.mSelectionMoved) && selStart >= 0 && selEnd >= 0) {
                    Selection.setSelection((Spannable) this.mTextView.getText(), selStart, selEnd);
                }
                if (this.mSelectAllOnFocus) {
                    this.mTextView.selectAllText();
                }
                this.mTouchFocusSelected = true;
            }
            this.mFrozenWithFocus = false;
            this.mSelectionMoved = false;
            if (this.mError != null) {
                showError();
            }
            makeBlink();
            return;
        }
        if (this.mError != null) {
            hideError();
        }
        this.mTextView.onEndBatchEdit();
        if (this.mTextView instanceof ExtractEditText) {
            int selStart2 = this.mTextView.getSelectionStart();
            int selEnd2 = this.mTextView.getSelectionEnd();
            hideControllers();
            Selection.setSelection((Spannable) this.mTextView.getText(), selStart2, selEnd2);
        } else {
            if (this.mTemporaryDetach) {
                this.mPreserveDetachedSelection = true;
            }
            hideControllers();
            if (this.mTemporaryDetach) {
                this.mPreserveDetachedSelection = false;
            }
            downgradeEasyCorrectionSpans();
        }
        if (this.mSelectionModifierCursorController != null) {
            this.mSelectionModifierCursorController.resetTouchOffsets();
        }
    }

    private void downgradeEasyCorrectionSpans() {
        CharSequence text = this.mTextView.getText();
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;
            SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(0, spannable.length(), SuggestionSpan.class);
            for (int i = 0; i < suggestionSpans.length; i++) {
                int flags = suggestionSpans[i].getFlags();
                if ((flags & 1) != 0 && (flags & 2) == 0) {
                    suggestionSpans[i].setFlags(flags & (-2));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendOnTextChanged(int start, int after) {
        updateSpellCheckSpans(start, start + after, false);
        hideCursorControllers();
    }

    private int getLastTapPosition() {
        if (this.mSelectionModifierCursorController != null) {
            int lastTapPosition = this.mSelectionModifierCursorController.getMinTouchOffset();
            if (lastTapPosition >= 0) {
                if (lastTapPosition > this.mTextView.getText().length()) {
                    lastTapPosition = this.mTextView.getText().length();
                }
                return lastTapPosition;
            }
            return -1;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            if (this.mBlink != null) {
                this.mBlink.uncancel();
                makeBlink();
                return;
            }
            return;
        }
        if (this.mBlink != null) {
            this.mBlink.cancel();
        }
        if (this.mInputContentType != null) {
            this.mInputContentType.enterDown = false;
        }
        hideControllers();
        if (this.mSuggestionsPopupWindow != null) {
            this.mSuggestionsPopupWindow.onParentLostFocus();
        }
        ensureEndedBatchEdit();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTouchEvent(MotionEvent event) {
        if (hasSelectionController()) {
            getSelectionController().onTouchEvent(event);
        }
        if (this.mShowSuggestionRunnable != null) {
            this.mTextView.removeCallbacks(this.mShowSuggestionRunnable);
            this.mShowSuggestionRunnable = null;
        }
        if (event.getActionMasked() == 0) {
            this.mLastDownPositionX = event.getX();
            this.mLastDownPositionY = event.getY();
            this.mTouchFocusSelected = false;
            this.mIgnoreActionUpEvent = false;
        }
    }

    public void beginBatchEdit() {
        this.mInBatchEditControllers = true;
        InputMethodState ims = this.mInputMethodState;
        if (ims != null) {
            int nesting = ims.mBatchEditNesting + 1;
            ims.mBatchEditNesting = nesting;
            if (nesting == 1) {
                ims.mCursorChanged = false;
                ims.mChangedDelta = 0;
                if (ims.mContentChanged) {
                    ims.mChangedStart = 0;
                    ims.mChangedEnd = this.mTextView.getText().length();
                } else {
                    ims.mChangedStart = -1;
                    ims.mChangedEnd = -1;
                    ims.mContentChanged = false;
                }
                this.mTextView.onBeginBatchEdit();
            }
        }
    }

    public void endBatchEdit() {
        this.mInBatchEditControllers = false;
        InputMethodState ims = this.mInputMethodState;
        if (ims != null) {
            int nesting = ims.mBatchEditNesting - 1;
            ims.mBatchEditNesting = nesting;
            if (nesting == 0) {
                finishBatchEdit(ims);
            }
        }
    }

    void ensureEndedBatchEdit() {
        InputMethodState ims = this.mInputMethodState;
        if (ims != null && ims.mBatchEditNesting != 0) {
            ims.mBatchEditNesting = 0;
            finishBatchEdit(ims);
        }
    }

    void finishBatchEdit(InputMethodState ims) {
        this.mTextView.onEndBatchEdit();
        if (ims.mContentChanged || ims.mSelectionModeChanged) {
            this.mTextView.updateAfterEdit();
            reportExtractedText();
        } else if (ims.mCursorChanged) {
            this.mTextView.invalidateCursor();
        }
        sendUpdateSelection();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        return extractTextInternal(request, -1, -1, -1, outText);
    }

    private boolean extractTextInternal(ExtractedTextRequest request, int partialStartOffset, int partialEndOffset, int delta, ExtractedText outText) {
        int partialEndOffset2;
        CharSequence content = this.mTextView.getText();
        if (content != null) {
            if (partialStartOffset != -2) {
                int N = content.length();
                if (partialStartOffset < 0) {
                    outText.partialEndOffset = -1;
                    outText.partialStartOffset = -1;
                    partialStartOffset = 0;
                    partialEndOffset2 = N;
                } else {
                    partialEndOffset2 = partialEndOffset + delta;
                    if (content instanceof Spanned) {
                        Spanned spanned = (Spanned) content;
                        Object[] spans = spanned.getSpans(partialStartOffset, partialEndOffset2, ParcelableSpan.class);
                        int i = spans.length;
                        while (i > 0) {
                            i--;
                            int j = spanned.getSpanStart(spans[i]);
                            if (j < partialStartOffset) {
                                partialStartOffset = j;
                            }
                            int j2 = spanned.getSpanEnd(spans[i]);
                            if (j2 > partialEndOffset2) {
                                partialEndOffset2 = j2;
                            }
                        }
                    }
                    outText.partialStartOffset = partialStartOffset;
                    outText.partialEndOffset = partialEndOffset2 - delta;
                    if (partialStartOffset > N) {
                        partialStartOffset = N;
                    } else if (partialStartOffset < 0) {
                        partialStartOffset = 0;
                    }
                    if (partialEndOffset2 > N) {
                        partialEndOffset2 = N;
                    } else if (partialEndOffset2 < 0) {
                        partialEndOffset2 = 0;
                    }
                }
                if ((request.flags & 1) != 0) {
                    outText.text = content.subSequence(partialStartOffset, partialEndOffset2);
                } else {
                    outText.text = TextUtils.substring(content, partialStartOffset, partialEndOffset2);
                }
            } else {
                outText.partialStartOffset = 0;
                outText.partialEndOffset = 0;
                outText.text = "";
            }
            outText.flags = 0;
            if (MetaKeyKeyListener.getMetaState(content, 2048) != 0) {
                outText.flags |= 2;
            }
            if (this.mTextView.isSingleLine()) {
                outText.flags |= 1;
            }
            outText.startOffset = 0;
            outText.selectionStart = this.mTextView.getSelectionStart();
            outText.selectionEnd = this.mTextView.getSelectionEnd();
            return true;
        }
        return false;
    }

    boolean reportExtractedText() {
        InputMethodManager imm;
        InputMethodState ims = this.mInputMethodState;
        if (ims != null) {
            boolean contentChanged = ims.mContentChanged;
            if (contentChanged || ims.mSelectionModeChanged) {
                ims.mContentChanged = false;
                ims.mSelectionModeChanged = false;
                ExtractedTextRequest req = ims.mExtractedTextRequest;
                if (req != null && (imm = InputMethodManager.peekInstance()) != null) {
                    if (ims.mChangedStart < 0 && !contentChanged) {
                        ims.mChangedStart = -2;
                    }
                    if (extractTextInternal(req, ims.mChangedStart, ims.mChangedEnd, ims.mChangedDelta, ims.mExtractedText)) {
                        imm.updateExtractedText(this.mTextView, req.token, ims.mExtractedText);
                        ims.mChangedStart = -1;
                        ims.mChangedEnd = -1;
                        ims.mChangedDelta = 0;
                        ims.mContentChanged = false;
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUpdateSelection() {
        InputMethodManager imm;
        if (null != this.mInputMethodState && this.mInputMethodState.mBatchEditNesting <= 0 && null != (imm = InputMethodManager.peekInstance())) {
            int selectionStart = this.mTextView.getSelectionStart();
            int selectionEnd = this.mTextView.getSelectionEnd();
            int candStart = -1;
            int candEnd = -1;
            if (this.mTextView.getText() instanceof Spannable) {
                Spannable sp = (Spannable) this.mTextView.getText();
                candStart = EditableInputConnection.getComposingSpanStart(sp);
                candEnd = EditableInputConnection.getComposingSpanEnd(sp);
            }
            imm.updateSelection(this.mTextView, selectionStart, selectionEnd, candStart, candEnd);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDraw(Canvas canvas, Layout layout, Path highlight, Paint highlightPaint, int cursorOffsetVertical) {
        InputMethodManager imm;
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        InputMethodState ims = this.mInputMethodState;
        if (ims != null && ims.mBatchEditNesting == 0 && (imm = InputMethodManager.peekInstance()) != null) {
            if (imm.isActive(this.mTextView) && (ims.mContentChanged || ims.mSelectionModeChanged)) {
                reportExtractedText();
            }
            if (imm.isWatchingCursor(this.mTextView) && highlight != null) {
                highlight.computeBounds(ims.mTmpRectF, true);
                float[] fArr = ims.mTmpOffset;
                ims.mTmpOffset[1] = 0.0f;
                fArr[0] = 0.0f;
                canvas.getMatrix().mapPoints(ims.mTmpOffset);
                ims.mTmpRectF.offset(ims.mTmpOffset[0], ims.mTmpOffset[1]);
                ims.mTmpRectF.offset(0.0f, cursorOffsetVertical);
                ims.mCursorRectInWindow.set((int) (ims.mTmpRectF.left + 0.5d), (int) (ims.mTmpRectF.top + 0.5d), (int) (ims.mTmpRectF.right + 0.5d), (int) (ims.mTmpRectF.bottom + 0.5d));
                imm.updateCursor(this.mTextView, ims.mCursorRectInWindow.left, ims.mCursorRectInWindow.top, ims.mCursorRectInWindow.right, ims.mCursorRectInWindow.bottom);
            }
        }
        if (this.mCorrectionHighlighter != null) {
            this.mCorrectionHighlighter.draw(canvas, cursorOffsetVertical);
        }
        if (highlight != null && selectionStart == selectionEnd && this.mCursorCount > 0) {
            drawCursor(canvas, cursorOffsetVertical);
            highlight = null;
        }
        if (this.mTextView.canHaveDisplayList() && canvas.isHardwareAccelerated()) {
            drawHardwareAccelerated(canvas, layout, highlight, highlightPaint, cursorOffsetVertical);
        } else {
            layout.draw(canvas, highlight, highlightPaint, cursorOffsetVertical);
        }
    }

    /* JADX WARN: Finally extract failed */
    private void drawHardwareAccelerated(Canvas canvas, Layout layout, Path highlight, Paint highlightPaint, int cursorOffsetVertical) {
        long lineRange = layout.getLineRangeForDraw(canvas);
        int firstLine = TextUtils.unpackRangeStartFromLong(lineRange);
        int lastLine = TextUtils.unpackRangeEndFromLong(lineRange);
        if (lastLine < 0) {
            return;
        }
        layout.drawBackground(canvas, highlight, highlightPaint, cursorOffsetVertical, firstLine, lastLine);
        if (layout instanceof DynamicLayout) {
            if (this.mTextDisplayLists == null) {
                this.mTextDisplayLists = new DisplayList[ArrayUtils.idealObjectArraySize(0)];
            }
            DynamicLayout dynamicLayout = (DynamicLayout) layout;
            int[] blockEndLines = dynamicLayout.getBlockEndLines();
            int[] blockIndices = dynamicLayout.getBlockIndices();
            int numberOfBlocks = dynamicLayout.getNumberOfBlocks();
            int indexFirstChangedBlock = dynamicLayout.getIndexFirstChangedBlock();
            int endOfPreviousBlock = -1;
            int searchStartIndex = 0;
            for (int i = 0; i < numberOfBlocks; i++) {
                int blockEndLine = blockEndLines[i];
                int blockIndex = blockIndices[i];
                boolean blockIsInvalid = blockIndex == -1;
                if (blockIsInvalid) {
                    blockIndex = getAvailableDisplayListIndex(blockIndices, numberOfBlocks, searchStartIndex);
                    blockIndices[i] = blockIndex;
                    searchStartIndex = blockIndex + 1;
                }
                DisplayList blockDisplayList = this.mTextDisplayLists[blockIndex];
                if (blockDisplayList == null) {
                    DisplayList createDisplayList = this.mTextView.getHardwareRenderer().createDisplayList("Text " + blockIndex);
                    this.mTextDisplayLists[blockIndex] = createDisplayList;
                    blockDisplayList = createDisplayList;
                } else if (blockIsInvalid) {
                    blockDisplayList.clear();
                }
                boolean blockDisplayListIsInvalid = !blockDisplayList.isValid();
                if (i >= indexFirstChangedBlock || blockDisplayListIsInvalid) {
                    int blockBeginLine = endOfPreviousBlock + 1;
                    int top = layout.getLineTop(blockBeginLine);
                    int bottom = layout.getLineBottom(blockEndLine);
                    int left = 0;
                    int right = this.mTextView.getWidth();
                    if (this.mTextView.getHorizontallyScrolling()) {
                        float min = Float.MAX_VALUE;
                        float max = Float.MIN_VALUE;
                        for (int line = blockBeginLine; line <= blockEndLine; line++) {
                            min = Math.min(min, layout.getLineLeft(line));
                            max = Math.max(max, layout.getLineRight(line));
                        }
                        left = (int) min;
                        right = (int) (max + 0.5f);
                    }
                    if (blockDisplayListIsInvalid) {
                        HardwareCanvas hardwareCanvas = blockDisplayList.start(right - left, bottom - top);
                        try {
                            hardwareCanvas.translate(-left, -top);
                            layout.drawText(hardwareCanvas, blockBeginLine, blockEndLine);
                            blockDisplayList.end();
                            blockDisplayList.setClipToBounds(false);
                        } catch (Throwable th) {
                            blockDisplayList.end();
                            blockDisplayList.setClipToBounds(false);
                            throw th;
                        }
                    }
                    blockDisplayList.setLeftTopRightBottom(left, top, right, bottom);
                }
                ((HardwareCanvas) canvas).drawDisplayList(blockDisplayList, null, 0);
                endOfPreviousBlock = blockEndLine;
            }
            dynamicLayout.setIndexFirstChangedBlock(numberOfBlocks);
            return;
        }
        layout.drawText(canvas, firstLine, lastLine);
    }

    private int getAvailableDisplayListIndex(int[] blockIndices, int numberOfBlocks, int searchStartIndex) {
        int length = this.mTextDisplayLists.length;
        for (int i = searchStartIndex; i < length; i++) {
            boolean blockIndexFound = false;
            int j = 0;
            while (true) {
                if (j < numberOfBlocks) {
                    if (blockIndices[j] != i) {
                        j++;
                    } else {
                        blockIndexFound = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!blockIndexFound) {
                return i;
            }
        }
        int newSize = ArrayUtils.idealIntArraySize(length + 1);
        DisplayList[] displayLists = new DisplayList[newSize];
        System.arraycopy(this.mTextDisplayLists, 0, displayLists, 0, length);
        this.mTextDisplayLists = displayLists;
        return length;
    }

    private void drawCursor(Canvas canvas, int cursorOffsetVertical) {
        boolean translate = cursorOffsetVertical != 0;
        if (translate) {
            canvas.translate(0.0f, cursorOffsetVertical);
        }
        for (int i = 0; i < this.mCursorCount; i++) {
            this.mCursorDrawable[i].draw(canvas);
        }
        if (translate) {
            canvas.translate(0.0f, -cursorOffsetVertical);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateTextDisplayList(Layout layout, int start, int end) {
        if (this.mTextDisplayLists != null && (layout instanceof DynamicLayout)) {
            int firstLine = layout.getLineForOffset(start);
            int lastLine = layout.getLineForOffset(end);
            DynamicLayout dynamicLayout = (DynamicLayout) layout;
            int[] blockEndLines = dynamicLayout.getBlockEndLines();
            int[] blockIndices = dynamicLayout.getBlockIndices();
            int numberOfBlocks = dynamicLayout.getNumberOfBlocks();
            int i = 0;
            while (i < numberOfBlocks && blockEndLines[i] < firstLine) {
                i++;
            }
            while (i < numberOfBlocks) {
                int blockIndex = blockIndices[i];
                if (blockIndex != -1) {
                    this.mTextDisplayLists[blockIndex].clear();
                }
                if (blockEndLines[i] < lastLine) {
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateTextDisplayList() {
        if (this.mTextDisplayLists != null) {
            for (int i = 0; i < this.mTextDisplayLists.length; i++) {
                if (this.mTextDisplayLists[i] != null) {
                    this.mTextDisplayLists[i].clear();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateCursorsPositions() {
        if (this.mTextView.mCursorDrawableRes == 0) {
            this.mCursorCount = 0;
            return;
        }
        Layout layout = this.mTextView.getLayout();
        Layout hintLayout = this.mTextView.getHintLayout();
        int offset = this.mTextView.getSelectionStart();
        int line = layout.getLineForOffset(offset);
        int top = layout.getLineTop(line);
        int bottom = layout.getLineTop(line + 1);
        this.mCursorCount = layout.isLevelBoundary(offset) ? 2 : 1;
        int middle = bottom;
        if (this.mCursorCount == 2) {
            middle = (top + bottom) >> 1;
        }
        boolean clamped = layout.shouldClampCursor(line);
        updateCursorPosition(0, top, middle, getPrimaryHorizontal(layout, hintLayout, offset, clamped));
        if (this.mCursorCount == 2) {
            updateCursorPosition(1, middle, bottom, layout.getSecondaryHorizontal(offset, clamped));
        }
    }

    private float getPrimaryHorizontal(Layout layout, Layout hintLayout, int offset, boolean clamped) {
        if (TextUtils.isEmpty(layout.getText()) && hintLayout != null && !TextUtils.isEmpty(hintLayout.getText())) {
            return hintLayout.getPrimaryHorizontal(offset, clamped);
        }
        return layout.getPrimaryHorizontal(offset, clamped);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean startSelectionActionMode() {
        InputMethodManager imm;
        if (this.mSelectionActionMode != null) {
            return false;
        }
        if (!canSelectText() || !this.mTextView.requestFocus()) {
            Log.w("TextView", "TextView does not support text selection. Action mode cancelled.");
            return false;
        } else if (!this.mTextView.hasSelection() && !selectCurrentWord()) {
            return false;
        } else {
            boolean willExtract = extractedTextModeWillBeStarted();
            if (!willExtract) {
                ActionMode.Callback actionModeCallback = new SelectionActionModeCallback();
                this.mSelectionActionMode = this.mTextView.startActionMode(actionModeCallback);
            }
            boolean selectionStarted = this.mSelectionActionMode != null || willExtract;
            if (selectionStarted && !this.mTextView.isTextSelectable() && this.mShowSoftInputOnFocus && (imm = InputMethodManager.peekInstance()) != null) {
                imm.showSoftInput(this.mTextView, 0, null);
            }
            return selectionStarted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean extractedTextModeWillBeStarted() {
        InputMethodManager imm;
        return ((this.mTextView instanceof ExtractEditText) || (imm = InputMethodManager.peekInstance()) == null || !imm.isFullscreenMode()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCursorInsideSuggestionSpan() {
        CharSequence text = this.mTextView.getText();
        if (text instanceof Spannable) {
            SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) ((Spannable) text).getSpans(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), SuggestionSpan.class);
            return suggestionSpans.length > 0;
        }
        return false;
    }

    private boolean isCursorInsideEasyCorrectionSpan() {
        Spannable spannable = (Spannable) this.mTextView.getText();
        SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), SuggestionSpan.class);
        for (SuggestionSpan suggestionSpan : suggestionSpans) {
            if ((suggestionSpan.getFlags() & 1) != 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTouchUpEvent(MotionEvent event) {
        boolean selectAllGotFocus = this.mSelectAllOnFocus && this.mTextView.didTouchFocusSelect();
        hideControllers();
        CharSequence text = this.mTextView.getText();
        if (!selectAllGotFocus && text.length() > 0) {
            int offset = this.mTextView.getOffsetForPosition(event.getX(), event.getY());
            Selection.setSelection((Spannable) text, offset);
            if (this.mSpellChecker != null) {
                this.mSpellChecker.onSelectionChanged();
            }
            if (!extractedTextModeWillBeStarted()) {
                if (isCursorInsideEasyCorrectionSpan()) {
                    this.mShowSuggestionRunnable = new Runnable() { // from class: android.widget.Editor.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Editor.this.showSuggestions();
                        }
                    };
                    this.mTextView.postDelayed(this.mShowSuggestionRunnable, ViewConfiguration.getDoubleTapTimeout());
                } else if (hasInsertionController()) {
                    getInsertionController().show();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopSelectionActionMode() {
        if (this.mSelectionActionMode != null) {
            this.mSelectionActionMode.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasInsertionController() {
        return this.mInsertionControllerEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSelectionController() {
        return this.mSelectionControllerEnabled;
    }

    InsertionPointCursorController getInsertionController() {
        if (!this.mInsertionControllerEnabled) {
            return null;
        }
        if (this.mInsertionPointCursorController == null) {
            this.mInsertionPointCursorController = new InsertionPointCursorController();
            ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
            observer.addOnTouchModeChangeListener(this.mInsertionPointCursorController);
        }
        return this.mInsertionPointCursorController;
    }

    SelectionModifierCursorController getSelectionController() {
        if (!this.mSelectionControllerEnabled) {
            return null;
        }
        if (this.mSelectionModifierCursorController == null) {
            this.mSelectionModifierCursorController = new SelectionModifierCursorController();
            ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
            observer.addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
        }
        return this.mSelectionModifierCursorController;
    }

    private void updateCursorPosition(int cursorIndex, int top, int bottom, float horizontal) {
        if (this.mCursorDrawable[cursorIndex] == null) {
            this.mCursorDrawable[cursorIndex] = this.mTextView.getResources().getDrawable(this.mTextView.mCursorDrawableRes);
        }
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        this.mCursorDrawable[cursorIndex].getPadding(this.mTempRect);
        int width = this.mCursorDrawable[cursorIndex].getIntrinsicWidth();
        int left = ((int) Math.max(0.5f, horizontal - 0.5f)) - this.mTempRect.left;
        this.mCursorDrawable[cursorIndex].setBounds(left, top - this.mTempRect.top, left + width, bottom + this.mTempRect.bottom);
    }

    public void onCommitCorrection(CorrectionInfo info) {
        if (this.mCorrectionHighlighter == null) {
            this.mCorrectionHighlighter = new CorrectionHighlighter();
        } else {
            this.mCorrectionHighlighter.invalidate(false);
        }
        this.mCorrectionHighlighter.highlight(info);
    }

    void showSuggestions() {
        if (this.mSuggestionsPopupWindow == null) {
            this.mSuggestionsPopupWindow = new SuggestionsPopupWindow();
        }
        hideControllers();
        this.mSuggestionsPopupWindow.show();
    }

    boolean areSuggestionsShown() {
        return this.mSuggestionsPopupWindow != null && this.mSuggestionsPopupWindow.isShowing();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onScrollChanged() {
        if (this.mPositionListener != null) {
            this.mPositionListener.onScrollChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldBlink() {
        int start;
        int end;
        return isCursorVisible() && this.mTextView.isFocused() && (start = this.mTextView.getSelectionStart()) >= 0 && (end = this.mTextView.getSelectionEnd()) >= 0 && start == end;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeBlink() {
        if (shouldBlink()) {
            this.mShowCursor = SystemClock.uptimeMillis();
            if (this.mBlink == null) {
                this.mBlink = new Blink();
            }
            this.mBlink.removeCallbacks(this.mBlink);
            this.mBlink.postAtTime(this.mBlink, this.mShowCursor + 500);
        } else if (this.mBlink != null) {
            this.mBlink.removeCallbacks(this.mBlink);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$Blink.class */
    public class Blink extends Handler implements Runnable {
        private boolean mCancelled;

        private Blink() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mCancelled) {
                return;
            }
            removeCallbacks(this);
            if (Editor.this.shouldBlink()) {
                if (Editor.this.mTextView.getLayout() != null) {
                    Editor.this.mTextView.invalidateCursorPath();
                }
                postAtTime(this, SystemClock.uptimeMillis() + 500);
            }
        }

        void cancel() {
            if (!this.mCancelled) {
                removeCallbacks(this);
                this.mCancelled = true;
            }
        }

        void uncancel() {
            this.mCancelled = false;
        }
    }

    private View.DragShadowBuilder getTextThumbnailBuilder(CharSequence text) {
        TextView shadowView = (TextView) View.inflate(this.mTextView.getContext(), R.layout.text_drag_thumbnail, null);
        if (shadowView == null) {
            throw new IllegalArgumentException("Unable to inflate text drag thumbnail");
        }
        if (text.length() > DRAG_SHADOW_MAX_TEXT_LENGTH) {
            text = text.subSequence(0, DRAG_SHADOW_MAX_TEXT_LENGTH);
        }
        shadowView.setText(text);
        shadowView.setTextColor(this.mTextView.getTextColors());
        shadowView.setTextAppearance(this.mTextView.getContext(), 16);
        shadowView.setGravity(17);
        shadowView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        int size = View.MeasureSpec.makeMeasureSpec(0, 0);
        shadowView.measure(size, size);
        shadowView.layout(0, 0, shadowView.getMeasuredWidth(), shadowView.getMeasuredHeight());
        shadowView.invalidate();
        return new View.DragShadowBuilder(shadowView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$DragLocalState.class */
    public static class DragLocalState {
        public TextView sourceTextView;
        public int start;
        public int end;

        public DragLocalState(TextView sourceTextView, int start, int end) {
            this.sourceTextView = sourceTextView;
            this.start = start;
            this.end = end;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDrop(DragEvent event) {
        StringBuilder content = new StringBuilder("");
        ClipData clipData = event.getClipData();
        int itemCount = clipData.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            ClipData.Item item = clipData.getItemAt(i);
            content.append(item.coerceToStyledText(this.mTextView.getContext()));
        }
        int offset = this.mTextView.getOffsetForPosition(event.getX(), event.getY());
        Object localState = event.getLocalState();
        DragLocalState dragLocalState = null;
        if (localState instanceof DragLocalState) {
            dragLocalState = (DragLocalState) localState;
        }
        boolean dragDropIntoItself = dragLocalState != null && dragLocalState.sourceTextView == this.mTextView;
        if (dragDropIntoItself && offset >= dragLocalState.start && offset < dragLocalState.end) {
            return;
        }
        int originalLength = this.mTextView.getText().length();
        long minMax = this.mTextView.prepareSpacesAroundPaste(offset, offset, content);
        int min = TextUtils.unpackRangeStartFromLong(minMax);
        int max = TextUtils.unpackRangeEndFromLong(minMax);
        Selection.setSelection((Spannable) this.mTextView.getText(), max);
        this.mTextView.replaceText_internal(min, max, content);
        if (dragDropIntoItself) {
            int dragSourceStart = dragLocalState.start;
            int dragSourceEnd = dragLocalState.end;
            if (max <= dragSourceStart) {
                int shift = this.mTextView.getText().length() - originalLength;
                dragSourceStart += shift;
                dragSourceEnd += shift;
            }
            this.mTextView.deleteText_internal(dragSourceStart, dragSourceEnd);
            int prevCharIdx = Math.max(0, dragSourceStart - 1);
            int nextCharIdx = Math.min(this.mTextView.getText().length(), dragSourceStart + 1);
            if (nextCharIdx > prevCharIdx + 1) {
                CharSequence t = this.mTextView.getTransformedText(prevCharIdx, nextCharIdx);
                if (Character.isSpaceChar(t.charAt(0)) && Character.isSpaceChar(t.charAt(1))) {
                    this.mTextView.deleteText_internal(prevCharIdx, prevCharIdx + 1);
                }
            }
        }
    }

    public void addSpanWatchers(Spannable text) {
        int textLength = text.length();
        if (this.mKeyListener != null) {
            text.setSpan(this.mKeyListener, 0, textLength, 18);
        }
        if (this.mSpanController == null) {
            this.mSpanController = new SpanController();
        }
        text.setSpan(this.mSpanController, 0, textLength, 18);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Editor$SpanController.class */
    public class SpanController implements SpanWatcher {
        private static final int DISPLAY_TIMEOUT_MS = 3000;
        private EasyEditPopupWindow mPopupWindow;
        private Runnable mHidePopup;

        SpanController() {
        }

        private boolean isNonIntermediateSelectionSpan(Spannable text, Object span) {
            return (Selection.SELECTION_START == span || Selection.SELECTION_END == span) && (text.getSpanFlags(span) & 512) == 0;
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable text, Object span, int start, int end) {
            if (isNonIntermediateSelectionSpan(text, span)) {
                Editor.this.sendUpdateSelection();
            } else if (span instanceof EasyEditSpan) {
                if (this.mPopupWindow == null) {
                    this.mPopupWindow = new EasyEditPopupWindow();
                    this.mHidePopup = new Runnable() { // from class: android.widget.Editor.SpanController.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SpanController.this.hide();
                        }
                    };
                }
                if (this.mPopupWindow.mEasyEditSpan != null) {
                    this.mPopupWindow.mEasyEditSpan.setDeleteEnabled(false);
                }
                this.mPopupWindow.setEasyEditSpan((EasyEditSpan) span);
                this.mPopupWindow.setOnDeleteListener(new EasyEditDeleteListener() { // from class: android.widget.Editor.SpanController.2
                    @Override // android.widget.Editor.EasyEditDeleteListener
                    public void onDeleteClick(EasyEditSpan span2) {
                        Editable editable = (Editable) Editor.this.mTextView.getText();
                        int start2 = editable.getSpanStart(span2);
                        int end2 = editable.getSpanEnd(span2);
                        if (start2 >= 0 && end2 >= 0) {
                            SpanController.this.sendEasySpanNotification(1, span2);
                            Editor.this.mTextView.deleteText_internal(start2, end2);
                        }
                        editable.removeSpan(span2);
                    }
                });
                if (Editor.this.mTextView.getWindowVisibility() != 0 || Editor.this.mTextView.getLayout() == null || Editor.this.extractedTextModeWillBeStarted()) {
                    return;
                }
                this.mPopupWindow.show();
                Editor.this.mTextView.removeCallbacks(this.mHidePopup);
                Editor.this.mTextView.postDelayed(this.mHidePopup, 3000L);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable text, Object span, int start, int end) {
            if (isNonIntermediateSelectionSpan(text, span)) {
                Editor.this.sendUpdateSelection();
            } else if (this.mPopupWindow != null && span == this.mPopupWindow.mEasyEditSpan) {
                hide();
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable text, Object span, int previousStart, int previousEnd, int newStart, int newEnd) {
            if (isNonIntermediateSelectionSpan(text, span)) {
                Editor.this.sendUpdateSelection();
            } else if (this.mPopupWindow != null && (span instanceof EasyEditSpan)) {
                EasyEditSpan easyEditSpan = (EasyEditSpan) span;
                sendEasySpanNotification(2, easyEditSpan);
                text.removeSpan(easyEditSpan);
            }
        }

        public void hide() {
            if (this.mPopupWindow != null) {
                this.mPopupWindow.hide();
                Editor.this.mTextView.removeCallbacks(this.mHidePopup);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendEasySpanNotification(int textChangedType, EasyEditSpan span) {
            try {
                PendingIntent pendingIntent = span.getPendingIntent();
                if (pendingIntent != null) {
                    Intent intent = new Intent();
                    intent.putExtra(EasyEditSpan.EXTRA_TEXT_CHANGED_TYPE, textChangedType);
                    pendingIntent.send(Editor.this.mTextView.getContext(), 0, intent);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.w(Editor.TAG, "PendingIntent for notification cannot be sent", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$EasyEditPopupWindow.class */
    public class EasyEditPopupWindow extends PinnedPopupWindow implements View.OnClickListener {
        private static final int POPUP_TEXT_LAYOUT = 17367213;
        private TextView mDeleteTextView;
        private EasyEditSpan mEasyEditSpan;
        private EasyEditDeleteListener mOnDeleteListener;

        private EasyEditPopupWindow() {
            super();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, 16843464);
            this.mPopupWindow.setInputMethodMode(2);
            this.mPopupWindow.setClippingEnabled(true);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            LinearLayout linearLayout = new LinearLayout(Editor.this.mTextView.getContext());
            linearLayout.setOrientation(0);
            this.mContentView = linearLayout;
            this.mContentView.setBackgroundResource(R.drawable.text_edit_side_paste_window);
            LayoutInflater inflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams wrapContent = new ViewGroup.LayoutParams(-2, -2);
            this.mDeleteTextView = (TextView) inflater.inflate(17367213, (ViewGroup) null);
            this.mDeleteTextView.setLayoutParams(wrapContent);
            this.mDeleteTextView.setText(R.string.delete);
            this.mDeleteTextView.setOnClickListener(this);
            this.mContentView.addView(this.mDeleteTextView);
        }

        public void setEasyEditSpan(EasyEditSpan easyEditSpan) {
            this.mEasyEditSpan = easyEditSpan;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOnDeleteListener(EasyEditDeleteListener listener) {
            this.mOnDeleteListener = listener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == this.mDeleteTextView && this.mEasyEditSpan != null && this.mEasyEditSpan.isDeleteEnabled() && this.mOnDeleteListener != null) {
                this.mOnDeleteListener.onDeleteClick(this.mEasyEditSpan);
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void hide() {
            if (this.mEasyEditSpan != null) {
                this.mEasyEditSpan.setDeleteEnabled(false);
            }
            this.mOnDeleteListener = null;
            super.hide();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            Editable editable = (Editable) Editor.this.mTextView.getText();
            return editable.getSpanEnd(this.mEasyEditSpan);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int line) {
            return Editor.this.mTextView.getLayout().getLineBottom(line);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int positionY) {
            return positionY;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$PositionListener.class */
    public class PositionListener implements ViewTreeObserver.OnPreDrawListener {
        private final int MAXIMUM_NUMBER_OF_LISTENERS = 6;
        private TextViewPositionListener[] mPositionListeners;
        private boolean[] mCanMove;
        private boolean mPositionHasChanged;
        private int mPositionX;
        private int mPositionY;
        private int mNumberOfListeners;
        private boolean mScrollHasChanged;
        final int[] mTempCoords;

        private PositionListener() {
            this.MAXIMUM_NUMBER_OF_LISTENERS = 6;
            this.mPositionListeners = new TextViewPositionListener[6];
            this.mCanMove = new boolean[6];
            this.mPositionHasChanged = true;
            this.mTempCoords = new int[2];
        }

        public void addSubscriber(TextViewPositionListener positionListener, boolean canMove) {
            if (this.mNumberOfListeners == 0) {
                updatePosition();
                ViewTreeObserver vto = Editor.this.mTextView.getViewTreeObserver();
                vto.addOnPreDrawListener(this);
            }
            int emptySlotIndex = -1;
            for (int i = 0; i < 6; i++) {
                TextViewPositionListener listener = this.mPositionListeners[i];
                if (listener == positionListener) {
                    return;
                }
                if (emptySlotIndex < 0 && listener == null) {
                    emptySlotIndex = i;
                }
            }
            this.mPositionListeners[emptySlotIndex] = positionListener;
            this.mCanMove[emptySlotIndex] = canMove;
            this.mNumberOfListeners++;
        }

        public void removeSubscriber(TextViewPositionListener positionListener) {
            int i = 0;
            while (true) {
                if (i >= 6) {
                    break;
                } else if (this.mPositionListeners[i] != positionListener) {
                    i++;
                } else {
                    this.mPositionListeners[i] = null;
                    this.mNumberOfListeners--;
                    break;
                }
            }
            if (this.mNumberOfListeners == 0) {
                ViewTreeObserver vto = Editor.this.mTextView.getViewTreeObserver();
                vto.removeOnPreDrawListener(this);
            }
        }

        public int getPositionX() {
            return this.mPositionX;
        }

        public int getPositionY() {
            return this.mPositionY;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            TextViewPositionListener positionListener;
            updatePosition();
            for (int i = 0; i < 6; i++) {
                if ((this.mPositionHasChanged || this.mScrollHasChanged || this.mCanMove[i]) && (positionListener = this.mPositionListeners[i]) != null) {
                    positionListener.updatePosition(this.mPositionX, this.mPositionY, this.mPositionHasChanged, this.mScrollHasChanged);
                }
            }
            this.mScrollHasChanged = false;
            return true;
        }

        private void updatePosition() {
            Editor.this.mTextView.getLocationInWindow(this.mTempCoords);
            this.mPositionHasChanged = (this.mTempCoords[0] == this.mPositionX && this.mTempCoords[1] == this.mPositionY) ? false : true;
            this.mPositionX = this.mTempCoords[0];
            this.mPositionY = this.mTempCoords[1];
        }

        public void onScrollChanged() {
            this.mScrollHasChanged = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$PinnedPopupWindow.class */
    public abstract class PinnedPopupWindow implements TextViewPositionListener {
        protected PopupWindow mPopupWindow;
        protected ViewGroup mContentView;
        int mPositionX;
        int mPositionY;

        protected abstract void createPopupWindow();

        protected abstract void initContentView();

        protected abstract int getTextOffset();

        protected abstract int getVerticalLocalPosition(int i);

        protected abstract int clipVertically(int i);

        public PinnedPopupWindow() {
            createPopupWindow();
            this.mPopupWindow.setWindowLayoutType(1002);
            this.mPopupWindow.setWidth(-2);
            this.mPopupWindow.setHeight(-2);
            initContentView();
            ViewGroup.LayoutParams wrapContent = new ViewGroup.LayoutParams(-2, -2);
            this.mContentView.setLayoutParams(wrapContent);
            this.mPopupWindow.setContentView(this.mContentView);
        }

        public void show() {
            Editor.this.getPositionListener().addSubscriber(this, false);
            computeLocalPosition();
            PositionListener positionListener = Editor.this.getPositionListener();
            updatePosition(positionListener.getPositionX(), positionListener.getPositionY());
        }

        protected void measureContent() {
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, Integer.MIN_VALUE));
        }

        private void computeLocalPosition() {
            measureContent();
            int width = this.mContentView.getMeasuredWidth();
            int offset = getTextOffset();
            this.mPositionX = (int) (Editor.this.mTextView.getLayout().getPrimaryHorizontal(offset) - (width / 2.0f));
            this.mPositionX += Editor.this.mTextView.viewportToContentHorizontalOffset();
            int line = Editor.this.mTextView.getLayout().getLineForOffset(offset);
            this.mPositionY = getVerticalLocalPosition(line);
            this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
        }

        private void updatePosition(int parentPositionX, int parentPositionY) {
            int positionX = parentPositionX + this.mPositionX;
            int positionY = clipVertically(parentPositionY + this.mPositionY);
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            int width = this.mContentView.getMeasuredWidth();
            int positionX2 = Math.max(0, Math.min(displayMetrics.widthPixels - width, positionX));
            if (!isShowing()) {
                this.mPopupWindow.showAtLocation(Editor.this.mTextView, 0, positionX2, positionY);
            } else {
                this.mPopupWindow.update(positionX2, positionY, -1, -1);
            }
        }

        public void hide() {
            this.mPopupWindow.dismiss();
            Editor.this.getPositionListener().removeSubscriber(this);
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int parentPositionX, int parentPositionY, boolean parentPositionChanged, boolean parentScrolled) {
            if (isShowing() && Editor.this.isOffsetVisible(getTextOffset())) {
                if (parentScrolled) {
                    computeLocalPosition();
                }
                updatePosition(parentPositionX, parentPositionY);
                return;
            }
            hide();
        }

        public boolean isShowing() {
            return this.mPopupWindow.isShowing();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$SuggestionsPopupWindow.class */
    public class SuggestionsPopupWindow extends PinnedPopupWindow implements AdapterView.OnItemClickListener {
        private static final int MAX_NUMBER_SUGGESTIONS = 5;
        private static final int ADD_TO_DICTIONARY = -1;
        private static final int DELETE_TEXT = -2;
        private SuggestionInfo[] mSuggestionInfos;
        private int mNumberOfSuggestions;
        private boolean mCursorWasVisibleBeforeSuggestions;
        private boolean mIsShowingUp;
        private SuggestionAdapter mSuggestionsAdapter;
        private final Comparator<SuggestionSpan> mSuggestionSpanComparator;
        private final HashMap<SuggestionSpan, Integer> mSpansLengths;

        /* loaded from: Editor$SuggestionsPopupWindow$CustomPopupWindow.class */
        private class CustomPopupWindow extends PopupWindow {
            public CustomPopupWindow(Context context, int defStyle) {
                super(context, (AttributeSet) null, defStyle);
            }

            @Override // android.widget.PopupWindow
            public void dismiss() {
                super.dismiss();
                Editor.this.getPositionListener().removeSubscriber(SuggestionsPopupWindow.this);
                ((Spannable) Editor.this.mTextView.getText()).removeSpan(Editor.this.mSuggestionRangeSpan);
                Editor.this.mTextView.setCursorVisible(SuggestionsPopupWindow.this.mCursorWasVisibleBeforeSuggestions);
                if (Editor.this.hasInsertionController()) {
                    Editor.this.getInsertionController().show();
                }
            }
        }

        public SuggestionsPopupWindow() {
            super();
            this.mIsShowingUp = false;
            this.mCursorWasVisibleBeforeSuggestions = Editor.this.mCursorVisible;
            this.mSuggestionSpanComparator = new SuggestionSpanComparator();
            this.mSpansLengths = new HashMap<>();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new CustomPopupWindow(Editor.this.mTextView.getContext(), 16843635);
            this.mPopupWindow.setInputMethodMode(2);
            this.mPopupWindow.setFocusable(true);
            this.mPopupWindow.setClippingEnabled(false);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            ListView listView = new ListView(Editor.this.mTextView.getContext());
            this.mSuggestionsAdapter = new SuggestionAdapter();
            listView.setAdapter((ListAdapter) this.mSuggestionsAdapter);
            listView.setOnItemClickListener(this);
            this.mContentView = listView;
            this.mSuggestionInfos = new SuggestionInfo[7];
            for (int i = 0; i < this.mSuggestionInfos.length; i++) {
                this.mSuggestionInfos[i] = new SuggestionInfo();
            }
        }

        public boolean isShowingUp() {
            return this.mIsShowingUp;
        }

        public void onParentLostFocus() {
            this.mIsShowingUp = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: Editor$SuggestionsPopupWindow$SuggestionInfo.class */
        public class SuggestionInfo {
            int suggestionStart;
            int suggestionEnd;
            SuggestionSpan suggestionSpan;
            int suggestionIndex;
            SpannableStringBuilder text;
            TextAppearanceSpan highlightSpan;

            private SuggestionInfo() {
                this.text = new SpannableStringBuilder();
                this.highlightSpan = new TextAppearanceSpan(Editor.this.mTextView.getContext(), 16974104);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: Editor$SuggestionsPopupWindow$SuggestionAdapter.class */
        public class SuggestionAdapter extends BaseAdapter {
            private LayoutInflater mInflater;

            private SuggestionAdapter() {
                this.mInflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override // android.widget.Adapter
            public int getCount() {
                return SuggestionsPopupWindow.this.mNumberOfSuggestions;
            }

            @Override // android.widget.Adapter
            public Object getItem(int position) {
                return SuggestionsPopupWindow.this.mSuggestionInfos[position];
            }

            @Override // android.widget.Adapter
            public long getItemId(int position) {
                return position;
            }

            @Override // android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) convertView;
                if (textView == null) {
                    textView = (TextView) this.mInflater.inflate(Editor.this.mTextView.mTextEditSuggestionItemLayout, parent, false);
                }
                SuggestionInfo suggestionInfo = SuggestionsPopupWindow.this.mSuggestionInfos[position];
                textView.setText(suggestionInfo.text);
                if (suggestionInfo.suggestionIndex == -1 || suggestionInfo.suggestionIndex == -2) {
                    textView.setBackgroundColor(0);
                } else {
                    textView.setBackgroundColor(-1);
                }
                return textView;
            }
        }

        /* loaded from: Editor$SuggestionsPopupWindow$SuggestionSpanComparator.class */
        private class SuggestionSpanComparator implements Comparator<SuggestionSpan> {
            private SuggestionSpanComparator() {
            }

            @Override // java.util.Comparator
            public int compare(SuggestionSpan span1, SuggestionSpan span2) {
                int flag1 = span1.getFlags();
                int flag2 = span2.getFlags();
                if (flag1 != flag2) {
                    boolean easy1 = (flag1 & 1) != 0;
                    boolean easy2 = (flag2 & 1) != 0;
                    boolean misspelled1 = (flag1 & 2) != 0;
                    boolean misspelled2 = (flag2 & 2) != 0;
                    if (easy1 && !misspelled1) {
                        return -1;
                    }
                    if (easy2 && !misspelled2) {
                        return 1;
                    }
                    if (misspelled1) {
                        return -1;
                    }
                    if (misspelled2) {
                        return 1;
                    }
                }
                return ((Integer) SuggestionsPopupWindow.this.mSpansLengths.get(span1)).intValue() - ((Integer) SuggestionsPopupWindow.this.mSpansLengths.get(span2)).intValue();
            }
        }

        private SuggestionSpan[] getSuggestionSpans() {
            int pos = Editor.this.mTextView.getSelectionStart();
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(pos, pos, SuggestionSpan.class);
            this.mSpansLengths.clear();
            for (SuggestionSpan suggestionSpan : suggestionSpans) {
                int start = spannable.getSpanStart(suggestionSpan);
                int end = spannable.getSpanEnd(suggestionSpan);
                this.mSpansLengths.put(suggestionSpan, Integer.valueOf(end - start));
            }
            Arrays.sort(suggestionSpans, this.mSuggestionSpanComparator);
            return suggestionSpans;
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void show() {
            if ((Editor.this.mTextView.getText() instanceof Editable) && updateSuggestions()) {
                this.mCursorWasVisibleBeforeSuggestions = Editor.this.mCursorVisible;
                Editor.this.mTextView.setCursorVisible(false);
                this.mIsShowingUp = true;
                super.show();
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void measureContent() {
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            int horizontalMeasure = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, Integer.MIN_VALUE);
            int verticalMeasure = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, Integer.MIN_VALUE);
            int width = 0;
            View view = null;
            for (int i = 0; i < this.mNumberOfSuggestions; i++) {
                view = this.mSuggestionsAdapter.getView(i, view, this.mContentView);
                view.getLayoutParams().width = -2;
                view.measure(horizontalMeasure, verticalMeasure);
                width = Math.max(width, view.getMeasuredWidth());
            }
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), verticalMeasure);
            Drawable popupBackground = this.mPopupWindow.getBackground();
            if (popupBackground != null) {
                if (Editor.this.mTempRect == null) {
                    Editor.this.mTempRect = new Rect();
                }
                popupBackground.getPadding(Editor.this.mTempRect);
                width += Editor.this.mTempRect.left + Editor.this.mTempRect.right;
            }
            this.mPopupWindow.setWidth(width);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int line) {
            return Editor.this.mTextView.getLayout().getLineBottom(line);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int positionY) {
            int height = this.mContentView.getMeasuredHeight();
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            return Math.min(positionY, displayMetrics.heightPixels - height);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void hide() {
            super.hide();
        }

        private boolean updateSuggestions() {
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            SuggestionSpan[] suggestionSpans = getSuggestionSpans();
            int nbSpans = suggestionSpans.length;
            if (nbSpans == 0) {
                return false;
            }
            this.mNumberOfSuggestions = 0;
            int spanUnionStart = Editor.this.mTextView.getText().length();
            int spanUnionEnd = 0;
            SuggestionSpan misspelledSpan = null;
            int underlineColor = 0;
            int spanIndex = 0;
            while (spanIndex < nbSpans) {
                SuggestionSpan suggestionSpan = suggestionSpans[spanIndex];
                int spanStart = spannable.getSpanStart(suggestionSpan);
                int spanEnd = spannable.getSpanEnd(suggestionSpan);
                spanUnionStart = Math.min(spanStart, spanUnionStart);
                spanUnionEnd = Math.max(spanEnd, spanUnionEnd);
                if ((suggestionSpan.getFlags() & 2) != 0) {
                    misspelledSpan = suggestionSpan;
                }
                if (spanIndex == 0) {
                    underlineColor = suggestionSpan.getUnderlineColor();
                }
                String[] suggestions = suggestionSpan.getSuggestions();
                int nbSuggestions = suggestions.length;
                int suggestionIndex = 0;
                while (true) {
                    if (suggestionIndex < nbSuggestions) {
                        String suggestion = suggestions[suggestionIndex];
                        boolean suggestionIsDuplicate = false;
                        int i = 0;
                        while (true) {
                            if (i >= this.mNumberOfSuggestions) {
                                break;
                            }
                            if (this.mSuggestionInfos[i].text.toString().equals(suggestion)) {
                                SuggestionSpan otherSuggestionSpan = this.mSuggestionInfos[i].suggestionSpan;
                                int otherSpanStart = spannable.getSpanStart(otherSuggestionSpan);
                                int otherSpanEnd = spannable.getSpanEnd(otherSuggestionSpan);
                                if (spanStart == otherSpanStart && spanEnd == otherSpanEnd) {
                                    suggestionIsDuplicate = true;
                                    break;
                                }
                            }
                            i++;
                        }
                        if (!suggestionIsDuplicate) {
                            SuggestionInfo suggestionInfo = this.mSuggestionInfos[this.mNumberOfSuggestions];
                            suggestionInfo.suggestionSpan = suggestionSpan;
                            suggestionInfo.suggestionIndex = suggestionIndex;
                            suggestionInfo.text.replace(0, suggestionInfo.text.length(), (CharSequence) suggestion);
                            this.mNumberOfSuggestions++;
                            if (this.mNumberOfSuggestions == 5) {
                                spanIndex = nbSpans;
                                break;
                            }
                        }
                        suggestionIndex++;
                    }
                }
                spanIndex++;
            }
            for (int i2 = 0; i2 < this.mNumberOfSuggestions; i2++) {
                highlightTextDifferences(this.mSuggestionInfos[i2], spanUnionStart, spanUnionEnd);
            }
            if (misspelledSpan != null) {
                int misspelledStart = spannable.getSpanStart(misspelledSpan);
                int misspelledEnd = spannable.getSpanEnd(misspelledSpan);
                if (misspelledStart >= 0 && misspelledEnd > misspelledStart) {
                    SuggestionInfo suggestionInfo2 = this.mSuggestionInfos[this.mNumberOfSuggestions];
                    suggestionInfo2.suggestionSpan = misspelledSpan;
                    suggestionInfo2.suggestionIndex = -1;
                    suggestionInfo2.text.replace(0, suggestionInfo2.text.length(), (CharSequence) Editor.this.mTextView.getContext().getString(R.string.addToDictionary));
                    suggestionInfo2.text.setSpan(suggestionInfo2.highlightSpan, 0, 0, 33);
                    this.mNumberOfSuggestions++;
                }
            }
            SuggestionInfo suggestionInfo3 = this.mSuggestionInfos[this.mNumberOfSuggestions];
            suggestionInfo3.suggestionSpan = null;
            suggestionInfo3.suggestionIndex = -2;
            suggestionInfo3.text.replace(0, suggestionInfo3.text.length(), (CharSequence) Editor.this.mTextView.getContext().getString(R.string.deleteText));
            suggestionInfo3.text.setSpan(suggestionInfo3.highlightSpan, 0, 0, 33);
            this.mNumberOfSuggestions++;
            if (Editor.this.mSuggestionRangeSpan == null) {
                Editor.this.mSuggestionRangeSpan = new SuggestionRangeSpan();
            }
            if (underlineColor == 0) {
                Editor.this.mSuggestionRangeSpan.setBackgroundColor(Editor.this.mTextView.mHighlightColor);
            } else {
                int newAlpha = (int) (Color.alpha(underlineColor) * 0.4f);
                Editor.this.mSuggestionRangeSpan.setBackgroundColor((underlineColor & 16777215) + (newAlpha << 24));
            }
            spannable.setSpan(Editor.this.mSuggestionRangeSpan, spanUnionStart, spanUnionEnd, 33);
            this.mSuggestionsAdapter.notifyDataSetChanged();
            return true;
        }

        private void highlightTextDifferences(SuggestionInfo suggestionInfo, int unionStart, int unionEnd) {
            Spannable text = (Spannable) Editor.this.mTextView.getText();
            int spanStart = text.getSpanStart(suggestionInfo.suggestionSpan);
            int spanEnd = text.getSpanEnd(suggestionInfo.suggestionSpan);
            suggestionInfo.suggestionStart = spanStart - unionStart;
            suggestionInfo.suggestionEnd = suggestionInfo.suggestionStart + suggestionInfo.text.length();
            suggestionInfo.text.setSpan(suggestionInfo.highlightSpan, 0, suggestionInfo.text.length(), 33);
            String textAsString = text.toString();
            suggestionInfo.text.insert(0, (CharSequence) textAsString.substring(unionStart, spanStart));
            suggestionInfo.text.append((CharSequence) textAsString.substring(spanEnd, unionEnd));
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Editable editable = (Editable) Editor.this.mTextView.getText();
            SuggestionInfo suggestionInfo = this.mSuggestionInfos[position];
            if (suggestionInfo.suggestionIndex == -2) {
                int spanUnionStart = editable.getSpanStart(Editor.this.mSuggestionRangeSpan);
                int spanUnionEnd = editable.getSpanEnd(Editor.this.mSuggestionRangeSpan);
                if (spanUnionStart >= 0 && spanUnionEnd > spanUnionStart) {
                    if (spanUnionEnd < editable.length() && Character.isSpaceChar(editable.charAt(spanUnionEnd)) && (spanUnionStart == 0 || Character.isSpaceChar(editable.charAt(spanUnionStart - 1)))) {
                        spanUnionEnd++;
                    }
                    Editor.this.mTextView.deleteText_internal(spanUnionStart, spanUnionEnd);
                }
                hide();
                return;
            }
            int spanStart = editable.getSpanStart(suggestionInfo.suggestionSpan);
            int spanEnd = editable.getSpanEnd(suggestionInfo.suggestionSpan);
            if (spanStart < 0 || spanEnd <= spanStart) {
                hide();
                return;
            }
            String originalText = editable.toString().substring(spanStart, spanEnd);
            if (suggestionInfo.suggestionIndex == -1) {
                Intent intent = new Intent(Settings.ACTION_USER_DICTIONARY_INSERT);
                intent.putExtra(UserDictionary.Words.WORD, originalText);
                intent.putExtra(UserDictionary.Words.LOCALE, Editor.this.mTextView.getTextServicesLocale().toString());
                intent.setFlags(intent.getFlags() | 268435456);
                Editor.this.mTextView.getContext().startActivity(intent);
                editable.removeSpan(suggestionInfo.suggestionSpan);
                Selection.setSelection(editable, spanEnd);
                Editor.this.updateSpellCheckSpans(spanStart, spanEnd, false);
            } else {
                SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) editable.getSpans(spanStart, spanEnd, SuggestionSpan.class);
                int length = suggestionSpans.length;
                int[] suggestionSpansStarts = new int[length];
                int[] suggestionSpansEnds = new int[length];
                int[] suggestionSpansFlags = new int[length];
                for (int i = 0; i < length; i++) {
                    SuggestionSpan suggestionSpan = suggestionSpans[i];
                    suggestionSpansStarts[i] = editable.getSpanStart(suggestionSpan);
                    suggestionSpansEnds[i] = editable.getSpanEnd(suggestionSpan);
                    suggestionSpansFlags[i] = editable.getSpanFlags(suggestionSpan);
                    int suggestionSpanFlags = suggestionSpan.getFlags();
                    if ((suggestionSpanFlags & 2) > 0) {
                        suggestionSpan.setFlags(suggestionSpanFlags & (-3) & (-2));
                    }
                }
                int suggestionStart = suggestionInfo.suggestionStart;
                int suggestionEnd = suggestionInfo.suggestionEnd;
                String suggestion = suggestionInfo.text.subSequence(suggestionStart, suggestionEnd).toString();
                Editor.this.mTextView.replaceText_internal(spanStart, spanEnd, suggestion);
                suggestionInfo.suggestionSpan.notifySelection(Editor.this.mTextView.getContext(), originalText, suggestionInfo.suggestionIndex);
                String[] suggestions = suggestionInfo.suggestionSpan.getSuggestions();
                suggestions[suggestionInfo.suggestionIndex] = originalText;
                int lengthDifference = suggestion.length() - (spanEnd - spanStart);
                for (int i2 = 0; i2 < length; i2++) {
                    if (suggestionSpansStarts[i2] <= spanStart && suggestionSpansEnds[i2] >= spanEnd) {
                        Editor.this.mTextView.setSpan_internal(suggestionSpans[i2], suggestionSpansStarts[i2], suggestionSpansEnds[i2] + lengthDifference, suggestionSpansFlags[i2]);
                    }
                }
                int newCursorPosition = spanEnd + lengthDifference;
                Editor.this.mTextView.setCursorPosition_internal(newCursorPosition, newCursorPosition);
            }
            hide();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$SelectionActionModeCallback.class */
    public class SelectionActionModeCallback implements ActionMode.Callback {
        private SelectionActionModeCallback() {
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            TypedArray styledAttributes = Editor.this.mTextView.getContext().obtainStyledAttributes(R.styleable.SelectionModeDrawables);
            mode.setTitle(Editor.this.mTextView.getContext().getString(R.string.textSelectionCABTitle));
            mode.setSubtitle((CharSequence) null);
            mode.setTitleOptionalHint(true);
            menu.add(0, 16908319, 0, 17039373).setIcon(styledAttributes.getResourceId(3, 0)).setAlphabeticShortcut('a').setShowAsAction(6);
            if (Editor.this.mTextView.canCut()) {
                menu.add(0, 16908320, 0, 17039363).setIcon(styledAttributes.getResourceId(0, 0)).setAlphabeticShortcut('x').setShowAsAction(6);
            }
            if (Editor.this.mTextView.canCopy()) {
                menu.add(0, 16908321, 0, 17039361).setIcon(styledAttributes.getResourceId(1, 0)).setAlphabeticShortcut('c').setShowAsAction(6);
            }
            if (Editor.this.mTextView.canPaste()) {
                menu.add(0, 16908322, 0, 17039371).setIcon(styledAttributes.getResourceId(2, 0)).setAlphabeticShortcut('v').setShowAsAction(6);
            }
            styledAttributes.recycle();
            if (Editor.this.mCustomSelectionActionModeCallback != null && !Editor.this.mCustomSelectionActionModeCallback.onCreateActionMode(mode, menu)) {
                return false;
            }
            if (menu.hasVisibleItems() || mode.getCustomView() != null) {
                Editor.this.getSelectionController().show();
                Editor.this.mTextView.setHasTransientState(true);
                return true;
            }
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (Editor.this.mCustomSelectionActionModeCallback != null) {
                return Editor.this.mCustomSelectionActionModeCallback.onPrepareActionMode(mode, menu);
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (Editor.this.mCustomSelectionActionModeCallback == null || !Editor.this.mCustomSelectionActionModeCallback.onActionItemClicked(mode, item)) {
                return Editor.this.mTextView.onTextContextMenuItem(item.getItemId());
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            if (Editor.this.mCustomSelectionActionModeCallback != null) {
                Editor.this.mCustomSelectionActionModeCallback.onDestroyActionMode(mode);
            }
            if (!Editor.this.mPreserveDetachedSelection) {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionEnd());
                Editor.this.mTextView.setHasTransientState(false);
            }
            if (Editor.this.mSelectionModifierCursorController != null) {
                Editor.this.mSelectionModifierCursorController.hide();
            }
            Editor.this.mSelectionActionMode = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$ActionPopupWindow.class */
    public class ActionPopupWindow extends PinnedPopupWindow implements View.OnClickListener {
        private static final int POPUP_TEXT_LAYOUT = 17367213;
        private TextView mPasteTextView;
        private TextView mReplaceTextView;

        private ActionPopupWindow() {
            super();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, 16843464);
            this.mPopupWindow.setClippingEnabled(true);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            LinearLayout linearLayout = new LinearLayout(Editor.this.mTextView.getContext());
            linearLayout.setOrientation(0);
            this.mContentView = linearLayout;
            this.mContentView.setBackgroundResource(R.drawable.text_edit_paste_window);
            LayoutInflater inflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams wrapContent = new ViewGroup.LayoutParams(-2, -2);
            this.mPasteTextView = (TextView) inflater.inflate(17367213, (ViewGroup) null);
            this.mPasteTextView.setLayoutParams(wrapContent);
            this.mContentView.addView(this.mPasteTextView);
            this.mPasteTextView.setText(17039371);
            this.mPasteTextView.setOnClickListener(this);
            this.mReplaceTextView = (TextView) inflater.inflate(17367213, (ViewGroup) null);
            this.mReplaceTextView.setLayoutParams(wrapContent);
            this.mContentView.addView(this.mReplaceTextView);
            this.mReplaceTextView.setText(R.string.replace);
            this.mReplaceTextView.setOnClickListener(this);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void show() {
            boolean canPaste = Editor.this.mTextView.canPaste();
            boolean canSuggest = Editor.this.mTextView.isSuggestionsEnabled() && Editor.this.isCursorInsideSuggestionSpan();
            this.mPasteTextView.setVisibility(canPaste ? 0 : 8);
            this.mReplaceTextView.setVisibility(canSuggest ? 0 : 8);
            if (canPaste || canSuggest) {
                super.show();
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == this.mPasteTextView && Editor.this.mTextView.canPaste()) {
                Editor.this.mTextView.onTextContextMenuItem(16908322);
                hide();
            } else if (view == this.mReplaceTextView) {
                int middle = (Editor.this.mTextView.getSelectionStart() + Editor.this.mTextView.getSelectionEnd()) / 2;
                Editor.this.stopSelectionActionMode();
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), middle);
                Editor.this.showSuggestions();
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            return (Editor.this.mTextView.getSelectionStart() + Editor.this.mTextView.getSelectionEnd()) / 2;
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int line) {
            return Editor.this.mTextView.getLayout().getLineTop(line) - this.mContentView.getMeasuredHeight();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int positionY) {
            if (positionY < 0) {
                int offset = getTextOffset();
                Layout layout = Editor.this.mTextView.getLayout();
                int line = layout.getLineForOffset(offset);
                int positionY2 = positionY + (layout.getLineBottom(line) - layout.getLineTop(line)) + this.mContentView.getMeasuredHeight();
                Drawable handle = Editor.this.mTextView.getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleRes);
                positionY = positionY2 + handle.getIntrinsicHeight();
            }
            return positionY;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$HandleView.class */
    public abstract class HandleView extends View implements TextViewPositionListener {
        protected Drawable mDrawable;
        protected Drawable mDrawableLtr;
        protected Drawable mDrawableRtl;
        private final PopupWindow mContainer;
        private int mPositionX;
        private int mPositionY;
        private boolean mIsDragging;
        private float mTouchToWindowOffsetX;
        private float mTouchToWindowOffsetY;
        protected int mHotspotX;
        private float mTouchOffsetY;
        private float mIdealVerticalOffset;
        private int mLastParentX;
        private int mLastParentY;
        protected ActionPopupWindow mActionPopupWindow;
        private int mPreviousOffset;
        private boolean mPositionHasChanged;
        private Runnable mActionPopupShower;
        private static final int HISTORY_SIZE = 5;
        private static final int TOUCH_UP_FILTER_DELAY_AFTER = 150;
        private static final int TOUCH_UP_FILTER_DELAY_BEFORE = 350;
        private final long[] mPreviousOffsetsTimes;
        private final int[] mPreviousOffsets;
        private int mPreviousOffsetIndex;
        private int mNumberPreviousOffsets;

        protected abstract int getHotspotX(Drawable drawable, boolean z);

        public abstract int getCurrentCursorOffset();

        protected abstract void updateSelection(int i);

        public abstract void updatePosition(float f, float f2);

        public HandleView(Drawable drawableLtr, Drawable drawableRtl) {
            super(Editor.this.mTextView.getContext());
            this.mPreviousOffset = -1;
            this.mPositionHasChanged = true;
            this.mPreviousOffsetsTimes = new long[5];
            this.mPreviousOffsets = new int[5];
            this.mPreviousOffsetIndex = 0;
            this.mNumberPreviousOffsets = 0;
            this.mContainer = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, 16843464);
            this.mContainer.setSplitTouchEnabled(true);
            this.mContainer.setClippingEnabled(false);
            this.mContainer.setWindowLayoutType(1002);
            this.mContainer.setContentView(this);
            this.mDrawableLtr = drawableLtr;
            this.mDrawableRtl = drawableRtl;
            updateDrawable();
            int handleHeight = this.mDrawable.getIntrinsicHeight();
            this.mTouchOffsetY = (-0.3f) * handleHeight;
            this.mIdealVerticalOffset = 0.7f * handleHeight;
        }

        protected void updateDrawable() {
            int offset = getCurrentCursorOffset();
            boolean isRtlCharAtOffset = Editor.this.mTextView.getLayout().isRtlCharAt(offset);
            this.mDrawable = isRtlCharAtOffset ? this.mDrawableRtl : this.mDrawableLtr;
            this.mHotspotX = getHotspotX(this.mDrawable, isRtlCharAtOffset);
        }

        private void startTouchUpFilter(int offset) {
            this.mNumberPreviousOffsets = 0;
            addPositionToTouchUpFilter(offset);
        }

        private void addPositionToTouchUpFilter(int offset) {
            this.mPreviousOffsetIndex = (this.mPreviousOffsetIndex + 1) % 5;
            this.mPreviousOffsets[this.mPreviousOffsetIndex] = offset;
            this.mPreviousOffsetsTimes[this.mPreviousOffsetIndex] = SystemClock.uptimeMillis();
            this.mNumberPreviousOffsets++;
        }

        private void filterOnTouchUp() {
            long now = SystemClock.uptimeMillis();
            int i = 0;
            int index = this.mPreviousOffsetIndex;
            int iMax = Math.min(this.mNumberPreviousOffsets, 5);
            while (i < iMax && now - this.mPreviousOffsetsTimes[index] < 150) {
                i++;
                index = ((this.mPreviousOffsetIndex - i) + 5) % 5;
            }
            if (i > 0 && i < iMax && now - this.mPreviousOffsetsTimes[index] > 350) {
                positionAtCursorOffset(this.mPreviousOffsets[index], false);
            }
        }

        public boolean offsetHasBeenChanged() {
            return this.mNumberPreviousOffsets > 1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        }

        public void show() {
            if (isShowing()) {
                return;
            }
            Editor.this.getPositionListener().addSubscriber(this, true);
            this.mPreviousOffset = -1;
            positionAtCursorOffset(getCurrentCursorOffset(), false);
            hideActionPopupWindow();
        }

        protected void dismiss() {
            this.mIsDragging = false;
            this.mContainer.dismiss();
            onDetached();
        }

        public void hide() {
            dismiss();
            Editor.this.getPositionListener().removeSubscriber(this);
        }

        void showActionPopupWindow(int delay) {
            if (this.mActionPopupWindow == null) {
                this.mActionPopupWindow = new ActionPopupWindow();
            }
            if (this.mActionPopupShower != null) {
                Editor.this.mTextView.removeCallbacks(this.mActionPopupShower);
            } else {
                this.mActionPopupShower = new Runnable() { // from class: android.widget.Editor.HandleView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        HandleView.this.mActionPopupWindow.show();
                    }
                };
            }
            Editor.this.mTextView.postDelayed(this.mActionPopupShower, delay);
        }

        protected void hideActionPopupWindow() {
            if (this.mActionPopupShower != null) {
                Editor.this.mTextView.removeCallbacks(this.mActionPopupShower);
            }
            if (this.mActionPopupWindow != null) {
                this.mActionPopupWindow.hide();
            }
        }

        public boolean isShowing() {
            return this.mContainer.isShowing();
        }

        private boolean isVisible() {
            if (!this.mIsDragging) {
                if (!Editor.this.mTextView.isInBatchEditMode()) {
                    return Editor.this.isPositionVisible(this.mPositionX + this.mHotspotX, this.mPositionY);
                }
                return false;
            }
            return true;
        }

        protected void positionAtCursorOffset(int offset, boolean parentScrolled) {
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                Editor.this.prepareCursorControllers();
                return;
            }
            boolean offsetChanged = offset != this.mPreviousOffset;
            if (offsetChanged || parentScrolled) {
                if (offsetChanged) {
                    updateSelection(offset);
                    addPositionToTouchUpFilter(offset);
                }
                int line = layout.getLineForOffset(offset);
                this.mPositionX = (int) ((layout.getPrimaryHorizontal(offset) - 0.5f) - this.mHotspotX);
                this.mPositionY = layout.getLineBottom(line);
                this.mPositionX += Editor.this.mTextView.viewportToContentHorizontalOffset();
                this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
                this.mPreviousOffset = offset;
                this.mPositionHasChanged = true;
            }
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int parentPositionX, int parentPositionY, boolean parentPositionChanged, boolean parentScrolled) {
            positionAtCursorOffset(getCurrentCursorOffset(), parentScrolled);
            if (parentPositionChanged || this.mPositionHasChanged) {
                if (this.mIsDragging) {
                    if (parentPositionX != this.mLastParentX || parentPositionY != this.mLastParentY) {
                        this.mTouchToWindowOffsetX += parentPositionX - this.mLastParentX;
                        this.mTouchToWindowOffsetY += parentPositionY - this.mLastParentY;
                        this.mLastParentX = parentPositionX;
                        this.mLastParentY = parentPositionY;
                    }
                    onHandleMoved();
                }
                if (isVisible()) {
                    int positionX = parentPositionX + this.mPositionX;
                    int positionY = parentPositionY + this.mPositionY;
                    if (!isShowing()) {
                        this.mContainer.showAtLocation(Editor.this.mTextView, 0, positionX, positionY);
                    } else {
                        this.mContainer.update(positionX, positionY, -1, -1);
                    }
                } else if (isShowing()) {
                    dismiss();
                }
                this.mPositionHasChanged = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onDraw(Canvas c) {
            this.mDrawable.setBounds(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            this.mDrawable.draw(c);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            float newVerticalOffset;
            switch (ev.getActionMasked()) {
                case 0:
                    startTouchUpFilter(getCurrentCursorOffset());
                    this.mTouchToWindowOffsetX = ev.getRawX() - this.mPositionX;
                    this.mTouchToWindowOffsetY = ev.getRawY() - this.mPositionY;
                    PositionListener positionListener = Editor.this.getPositionListener();
                    this.mLastParentX = positionListener.getPositionX();
                    this.mLastParentY = positionListener.getPositionY();
                    this.mIsDragging = true;
                    return true;
                case 1:
                    filterOnTouchUp();
                    this.mIsDragging = false;
                    return true;
                case 2:
                    float rawX = ev.getRawX();
                    float rawY = ev.getRawY();
                    float previousVerticalOffset = this.mTouchToWindowOffsetY - this.mLastParentY;
                    float currentVerticalOffset = (rawY - this.mPositionY) - this.mLastParentY;
                    if (previousVerticalOffset < this.mIdealVerticalOffset) {
                        float newVerticalOffset2 = Math.min(currentVerticalOffset, this.mIdealVerticalOffset);
                        newVerticalOffset = Math.max(newVerticalOffset2, previousVerticalOffset);
                    } else {
                        float newVerticalOffset3 = Math.max(currentVerticalOffset, this.mIdealVerticalOffset);
                        newVerticalOffset = Math.min(newVerticalOffset3, previousVerticalOffset);
                    }
                    this.mTouchToWindowOffsetY = newVerticalOffset + this.mLastParentY;
                    float newPosX = (rawX - this.mTouchToWindowOffsetX) + this.mHotspotX;
                    float newPosY = (rawY - this.mTouchToWindowOffsetY) + this.mTouchOffsetY;
                    updatePosition(newPosX, newPosY);
                    return true;
                case 3:
                    this.mIsDragging = false;
                    return true;
                default:
                    return true;
            }
        }

        public boolean isDragging() {
            return this.mIsDragging;
        }

        void onHandleMoved() {
            hideActionPopupWindow();
        }

        public void onDetached() {
            hideActionPopupWindow();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$InsertionHandleView.class */
    public class InsertionHandleView extends HandleView {
        private static final int DELAY_BEFORE_HANDLE_FADES_OUT = 4000;
        private static final int RECENT_CUT_COPY_DURATION = 15000;
        private float mDownPositionX;
        private float mDownPositionY;
        private Runnable mHider;

        public InsertionHandleView(Drawable drawable) {
            super(drawable, drawable);
        }

        @Override // android.widget.Editor.HandleView
        public void show() {
            super.show();
            long durationSinceCutOrCopy = SystemClock.uptimeMillis() - TextView.LAST_CUT_OR_COPY_TIME;
            if (durationSinceCutOrCopy < 15000) {
                showActionPopupWindow(0);
            }
            hideAfterDelay();
        }

        public void showWithActionPopup() {
            show();
            showActionPopupWindow(0);
        }

        private void hideAfterDelay() {
            if (this.mHider == null) {
                this.mHider = new Runnable() { // from class: android.widget.Editor.InsertionHandleView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        InsertionHandleView.this.hide();
                    }
                };
            } else {
                removeHiderCallback();
            }
            Editor.this.mTextView.postDelayed(this.mHider, 4000L);
        }

        private void removeHiderCallback() {
            if (this.mHider != null) {
                Editor.this.mTextView.removeCallbacks(this.mHider);
            }
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean isRtlRun) {
            return drawable.getIntrinsicWidth() / 2;
        }

        @Override // android.widget.Editor.HandleView, android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            boolean result = super.onTouchEvent(ev);
            switch (ev.getActionMasked()) {
                case 0:
                    this.mDownPositionX = ev.getRawX();
                    this.mDownPositionY = ev.getRawY();
                    break;
                case 1:
                    if (!offsetHasBeenChanged()) {
                        float deltaX = this.mDownPositionX - ev.getRawX();
                        float deltaY = this.mDownPositionY - ev.getRawY();
                        float distanceSquared = (deltaX * deltaX) + (deltaY * deltaY);
                        ViewConfiguration viewConfiguration = ViewConfiguration.get(Editor.this.mTextView.getContext());
                        int touchSlop = viewConfiguration.getScaledTouchSlop();
                        if (distanceSquared < touchSlop * touchSlop) {
                            if (this.mActionPopupWindow != null && this.mActionPopupWindow.isShowing()) {
                                this.mActionPopupWindow.hide();
                            } else {
                                showWithActionPopup();
                            }
                        }
                    }
                    hideAfterDelay();
                    break;
                case 3:
                    hideAfterDelay();
                    break;
            }
            return result;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int offset) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), offset);
        }

        @Override // android.widget.Editor.HandleView
        public void updatePosition(float x, float y) {
            positionAtCursorOffset(Editor.this.mTextView.getOffsetForPosition(x, y), false);
        }

        @Override // android.widget.Editor.HandleView
        void onHandleMoved() {
            super.onHandleMoved();
            removeHiderCallback();
        }

        @Override // android.widget.Editor.HandleView
        public void onDetached() {
            super.onDetached();
            removeHiderCallback();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$SelectionStartHandleView.class */
    public class SelectionStartHandleView extends HandleView {
        public SelectionStartHandleView(Drawable drawableLtr, Drawable drawableRtl) {
            super(drawableLtr, drawableRtl);
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean isRtlRun) {
            if (isRtlRun) {
                return drawable.getIntrinsicWidth() / 4;
            }
            return (drawable.getIntrinsicWidth() * 3) / 4;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int offset) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), offset, Editor.this.mTextView.getSelectionEnd());
            updateDrawable();
        }

        @Override // android.widget.Editor.HandleView
        public void updatePosition(float x, float y) {
            int offset = Editor.this.mTextView.getOffsetForPosition(x, y);
            int selectionEnd = Editor.this.mTextView.getSelectionEnd();
            if (offset >= selectionEnd) {
                offset = Math.max(0, selectionEnd - 1);
            }
            positionAtCursorOffset(offset, false);
        }

        public ActionPopupWindow getActionPopupWindow() {
            return this.mActionPopupWindow;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$SelectionEndHandleView.class */
    public class SelectionEndHandleView extends HandleView {
        public SelectionEndHandleView(Drawable drawableLtr, Drawable drawableRtl) {
            super(drawableLtr, drawableRtl);
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean isRtlRun) {
            if (isRtlRun) {
                return (drawable.getIntrinsicWidth() * 3) / 4;
            }
            return drawable.getIntrinsicWidth() / 4;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionEnd();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int offset) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionStart(), offset);
            updateDrawable();
        }

        @Override // android.widget.Editor.HandleView
        public void updatePosition(float x, float y) {
            int offset = Editor.this.mTextView.getOffsetForPosition(x, y);
            int selectionStart = Editor.this.mTextView.getSelectionStart();
            if (offset <= selectionStart) {
                offset = Math.min(selectionStart + 1, Editor.this.mTextView.getText().length());
            }
            positionAtCursorOffset(offset, false);
        }

        public void setActionPopupWindow(ActionPopupWindow actionPopupWindow) {
            this.mActionPopupWindow = actionPopupWindow;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$InsertionPointCursorController.class */
    public class InsertionPointCursorController implements CursorController {
        private InsertionHandleView mHandle;

        private InsertionPointCursorController() {
        }

        @Override // android.widget.Editor.CursorController
        public void show() {
            getHandle().show();
        }

        public void showWithActionPopup() {
            getHandle().showWithActionPopup();
        }

        @Override // android.widget.Editor.CursorController
        public void hide() {
            if (this.mHandle != null) {
                this.mHandle.hide();
            }
        }

        @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
        public void onTouchModeChanged(boolean isInTouchMode) {
            if (!isInTouchMode) {
                hide();
            }
        }

        private InsertionHandleView getHandle() {
            if (Editor.this.mSelectHandleCenter == null) {
                Editor.this.mSelectHandleCenter = Editor.this.mTextView.getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleRes);
            }
            if (this.mHandle == null) {
                this.mHandle = new InsertionHandleView(Editor.this.mSelectHandleCenter);
            }
            return this.mHandle;
        }

        @Override // android.widget.Editor.CursorController
        public void onDetached() {
            ViewTreeObserver observer = Editor.this.mTextView.getViewTreeObserver();
            observer.removeOnTouchModeChangeListener(this);
            if (this.mHandle != null) {
                this.mHandle.onDetached();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Editor$SelectionModifierCursorController.class */
    public class SelectionModifierCursorController implements CursorController {
        private static final int DELAY_BEFORE_REPLACE_ACTION = 200;
        private SelectionStartHandleView mStartHandle;
        private SelectionEndHandleView mEndHandle;
        private int mMinTouchOffset;
        private int mMaxTouchOffset;
        private long mPreviousTapUpTime = 0;
        private float mDownPositionX;
        private float mDownPositionY;
        private boolean mGestureStayedInTapRegion;

        SelectionModifierCursorController() {
            resetTouchOffsets();
        }

        @Override // android.widget.Editor.CursorController
        public void show() {
            if (Editor.this.mTextView.isInBatchEditMode()) {
                return;
            }
            initDrawables();
            initHandles();
            Editor.this.hideInsertionPointCursorController();
        }

        private void initDrawables() {
            if (Editor.this.mSelectHandleLeft == null) {
                Editor.this.mSelectHandleLeft = Editor.this.mTextView.getContext().getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleLeftRes);
            }
            if (Editor.this.mSelectHandleRight == null) {
                Editor.this.mSelectHandleRight = Editor.this.mTextView.getContext().getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleRightRes);
            }
        }

        private void initHandles() {
            if (this.mStartHandle == null) {
                this.mStartHandle = new SelectionStartHandleView(Editor.this.mSelectHandleLeft, Editor.this.mSelectHandleRight);
            }
            if (this.mEndHandle == null) {
                this.mEndHandle = new SelectionEndHandleView(Editor.this.mSelectHandleRight, Editor.this.mSelectHandleLeft);
            }
            this.mStartHandle.show();
            this.mEndHandle.show();
            this.mStartHandle.showActionPopupWindow(200);
            this.mEndHandle.setActionPopupWindow(this.mStartHandle.getActionPopupWindow());
            Editor.this.hideInsertionPointCursorController();
        }

        @Override // android.widget.Editor.CursorController
        public void hide() {
            if (this.mStartHandle != null) {
                this.mStartHandle.hide();
            }
            if (this.mEndHandle != null) {
                this.mEndHandle.hide();
            }
        }

        public void onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case 0:
                    float x = event.getX();
                    float y = event.getY();
                    int offsetForPosition = Editor.this.mTextView.getOffsetForPosition(x, y);
                    this.mMaxTouchOffset = offsetForPosition;
                    this.mMinTouchOffset = offsetForPosition;
                    if (this.mGestureStayedInTapRegion) {
                        long duration = SystemClock.uptimeMillis() - this.mPreviousTapUpTime;
                        if (duration <= ViewConfiguration.getDoubleTapTimeout()) {
                            float deltaX = x - this.mDownPositionX;
                            float deltaY = y - this.mDownPositionY;
                            float distanceSquared = (deltaX * deltaX) + (deltaY * deltaY);
                            ViewConfiguration viewConfiguration = ViewConfiguration.get(Editor.this.mTextView.getContext());
                            int doubleTapSlop = viewConfiguration.getScaledDoubleTapSlop();
                            boolean stayedInArea = distanceSquared < ((float) (doubleTapSlop * doubleTapSlop));
                            if (stayedInArea && Editor.this.isPositionOnText(x, y)) {
                                Editor.this.startSelectionActionMode();
                                Editor.this.mDiscardNextActionUp = true;
                            }
                        }
                    }
                    this.mDownPositionX = x;
                    this.mDownPositionY = y;
                    this.mGestureStayedInTapRegion = true;
                    return;
                case 1:
                    this.mPreviousTapUpTime = SystemClock.uptimeMillis();
                    return;
                case 2:
                    if (this.mGestureStayedInTapRegion) {
                        float deltaX2 = event.getX() - this.mDownPositionX;
                        float deltaY2 = event.getY() - this.mDownPositionY;
                        float distanceSquared2 = (deltaX2 * deltaX2) + (deltaY2 * deltaY2);
                        ViewConfiguration viewConfiguration2 = ViewConfiguration.get(Editor.this.mTextView.getContext());
                        int doubleTapTouchSlop = viewConfiguration2.getScaledDoubleTapTouchSlop();
                        if (distanceSquared2 > doubleTapTouchSlop * doubleTapTouchSlop) {
                            this.mGestureStayedInTapRegion = false;
                            return;
                        }
                        return;
                    }
                    return;
                case 3:
                case 4:
                default:
                    return;
                case 5:
                case 6:
                    if (Editor.this.mTextView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
                        updateMinAndMaxOffsets(event);
                        return;
                    }
                    return;
            }
        }

        private void updateMinAndMaxOffsets(MotionEvent event) {
            int pointerCount = event.getPointerCount();
            for (int index = 0; index < pointerCount; index++) {
                int offset = Editor.this.mTextView.getOffsetForPosition(event.getX(index), event.getY(index));
                if (offset < this.mMinTouchOffset) {
                    this.mMinTouchOffset = offset;
                }
                if (offset > this.mMaxTouchOffset) {
                    this.mMaxTouchOffset = offset;
                }
            }
        }

        public int getMinTouchOffset() {
            return this.mMinTouchOffset;
        }

        public int getMaxTouchOffset() {
            return this.mMaxTouchOffset;
        }

        public void resetTouchOffsets() {
            this.mMaxTouchOffset = -1;
            this.mMinTouchOffset = -1;
        }

        public boolean isSelectionStartDragged() {
            return this.mStartHandle != null && this.mStartHandle.isDragging();
        }

        @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
        public void onTouchModeChanged(boolean isInTouchMode) {
            if (!isInTouchMode) {
                hide();
            }
        }

        @Override // android.widget.Editor.CursorController
        public void onDetached() {
            ViewTreeObserver observer = Editor.this.mTextView.getViewTreeObserver();
            observer.removeOnTouchModeChangeListener(this);
            if (this.mStartHandle != null) {
                this.mStartHandle.onDetached();
            }
            if (this.mEndHandle != null) {
                this.mEndHandle.onDetached();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$CorrectionHighlighter.class */
    public class CorrectionHighlighter {
        private final Path mPath = new Path();
        private final Paint mPaint = new Paint(1);
        private int mStart;
        private int mEnd;
        private long mFadingStartTime;
        private RectF mTempRectF;
        private static final int FADE_OUT_DURATION = 400;

        public CorrectionHighlighter() {
            this.mPaint.setCompatibilityScaling(Editor.this.mTextView.getResources().getCompatibilityInfo().applicationScale);
            this.mPaint.setStyle(Paint.Style.FILL);
        }

        public void highlight(CorrectionInfo info) {
            this.mStart = info.getOffset();
            this.mEnd = this.mStart + info.getNewText().length();
            this.mFadingStartTime = SystemClock.uptimeMillis();
            if (this.mStart < 0 || this.mEnd < 0) {
                stopAnimation();
            }
        }

        public void draw(Canvas canvas, int cursorOffsetVertical) {
            if (updatePath() && updatePaint()) {
                if (cursorOffsetVertical != 0) {
                    canvas.translate(0.0f, cursorOffsetVertical);
                }
                canvas.drawPath(this.mPath, this.mPaint);
                if (cursorOffsetVertical != 0) {
                    canvas.translate(0.0f, -cursorOffsetVertical);
                }
                invalidate(true);
                return;
            }
            stopAnimation();
            invalidate(false);
        }

        private boolean updatePaint() {
            long duration = SystemClock.uptimeMillis() - this.mFadingStartTime;
            if (duration > 400) {
                return false;
            }
            float coef = 1.0f - (((float) duration) / 400.0f);
            int highlightColorAlpha = Color.alpha(Editor.this.mTextView.mHighlightColor);
            int color = (Editor.this.mTextView.mHighlightColor & 16777215) + (((int) (highlightColorAlpha * coef)) << 24);
            this.mPaint.setColor(color);
            return true;
        }

        private boolean updatePath() {
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                return false;
            }
            int length = Editor.this.mTextView.getText().length();
            int start = Math.min(length, this.mStart);
            int end = Math.min(length, this.mEnd);
            this.mPath.reset();
            layout.getSelectionPath(start, end, this.mPath);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void invalidate(boolean delayed) {
            if (Editor.this.mTextView.getLayout() == null) {
                return;
            }
            if (this.mTempRectF == null) {
                this.mTempRectF = new RectF();
            }
            this.mPath.computeBounds(this.mTempRectF, false);
            int left = Editor.this.mTextView.getCompoundPaddingLeft();
            int top = Editor.this.mTextView.getExtendedPaddingTop() + Editor.this.mTextView.getVerticalOffset(true);
            if (delayed) {
                Editor.this.mTextView.postInvalidateOnAnimation(left + ((int) this.mTempRectF.left), top + ((int) this.mTempRectF.top), left + ((int) this.mTempRectF.right), top + ((int) this.mTempRectF.bottom));
            } else {
                Editor.this.mTextView.postInvalidate((int) this.mTempRectF.left, (int) this.mTempRectF.top, (int) this.mTempRectF.right, (int) this.mTempRectF.bottom);
            }
        }

        private void stopAnimation() {
            Editor.this.mCorrectionHighlighter = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Editor$ErrorPopup.class */
    public static class ErrorPopup extends PopupWindow {
        private boolean mAbove;
        private final TextView mView;
        private int mPopupInlineErrorBackgroundId;
        private int mPopupInlineErrorAboveBackgroundId;

        ErrorPopup(TextView v, int width, int height) {
            super(v, width, height);
            this.mAbove = false;
            this.mPopupInlineErrorBackgroundId = 0;
            this.mPopupInlineErrorAboveBackgroundId = 0;
            this.mView = v;
            this.mPopupInlineErrorBackgroundId = getResourceId(this.mPopupInlineErrorBackgroundId, 225);
            this.mView.setBackgroundResource(this.mPopupInlineErrorBackgroundId);
        }

        void fixDirection(boolean above) {
            this.mAbove = above;
            if (above) {
                this.mPopupInlineErrorAboveBackgroundId = getResourceId(this.mPopupInlineErrorAboveBackgroundId, 226);
            } else {
                this.mPopupInlineErrorBackgroundId = getResourceId(this.mPopupInlineErrorBackgroundId, 225);
            }
            this.mView.setBackgroundResource(above ? this.mPopupInlineErrorAboveBackgroundId : this.mPopupInlineErrorBackgroundId);
        }

        private int getResourceId(int currentId, int index) {
            if (currentId == 0) {
                TypedArray styledAttributes = this.mView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
                currentId = styledAttributes.getResourceId(index, 0);
                styledAttributes.recycle();
            }
            return currentId;
        }

        @Override // android.widget.PopupWindow
        public void update(int x, int y, int w, int h, boolean force) {
            super.update(x, y, w, h, force);
            boolean above = isAboveAnchor();
            if (above != this.mAbove) {
                fixDirection(above);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Editor$InputContentType.class */
    public static class InputContentType {
        int imeOptions = 0;
        String privateImeOptions;
        CharSequence imeActionLabel;
        int imeActionId;
        Bundle extras;
        TextView.OnEditorActionListener onEditorActionListener;
        boolean enterDown;

        InputContentType() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Editor$InputMethodState.class */
    public static class InputMethodState {
        ExtractedTextRequest mExtractedTextRequest;
        int mBatchEditNesting;
        boolean mCursorChanged;
        boolean mSelectionModeChanged;
        boolean mContentChanged;
        int mChangedStart;
        int mChangedEnd;
        int mChangedDelta;
        Rect mCursorRectInWindow = new Rect();
        RectF mTmpRectF = new RectF();
        float[] mTmpOffset = new float[2];
        final ExtractedText mExtractedText = new ExtractedText();

        InputMethodState() {
        }
    }

    /* loaded from: Editor$UndoInputFilter.class */
    public static class UndoInputFilter implements InputFilter {
        final Editor mEditor;

        public UndoInputFilter(Editor editor) {
            this.mEditor = editor;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            SpannableStringBuilder str;
            UndoManager um = this.mEditor.mUndoManager;
            if (um.isInUndo()) {
                return null;
            }
            um.beginUpdate("Edit text");
            TextModifyOperation op = (TextModifyOperation) um.getLastOperation(TextModifyOperation.class, this.mEditor.mUndoOwner, 1);
            if (op != null) {
                if (op.mOldText == null) {
                    if (start < end && ((dstart >= op.mRangeStart && dend <= op.mRangeEnd) || (dstart == op.mRangeEnd && dend == op.mRangeEnd))) {
                        op.mRangeEnd = dstart + (end - start);
                        um.endUpdate();
                        return null;
                    }
                } else if (start == end && dend == op.mRangeStart - 1) {
                    if (op.mOldText instanceof SpannableString) {
                        str = (SpannableStringBuilder) op.mOldText;
                    } else {
                        str = new SpannableStringBuilder(op.mOldText);
                    }
                    str.insert(0, (CharSequence) dest, dstart, dend);
                    op.mRangeStart = dstart;
                    op.mOldText = str;
                    um.endUpdate();
                    return null;
                }
                um.commitState(null);
                um.setUndoLabel("Edit text");
            }
            TextModifyOperation op2 = new TextModifyOperation(this.mEditor.mUndoOwner);
            op2.mRangeStart = dstart;
            if (start < end) {
                op2.mRangeEnd = dstart + (end - start);
            } else {
                op2.mRangeEnd = dstart;
            }
            if (dstart < dend) {
                op2.mOldText = dest.subSequence(dstart, dend);
            }
            um.addOperation(op2, 0);
            um.endUpdate();
            return null;
        }
    }

    /* loaded from: Editor$TextModifyOperation.class */
    public static class TextModifyOperation extends UndoOperation<TextView> {
        int mRangeStart;
        int mRangeEnd;
        CharSequence mOldText;
        public static final Parcelable.ClassLoaderCreator<TextModifyOperation> CREATOR = new Parcelable.ClassLoaderCreator<TextModifyOperation>() { // from class: android.widget.Editor.TextModifyOperation.1
            @Override // android.os.Parcelable.Creator
            public TextModifyOperation createFromParcel(Parcel in) {
                return new TextModifyOperation(in, null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public TextModifyOperation createFromParcel(Parcel in, ClassLoader loader) {
                return new TextModifyOperation(in, loader);
            }

            @Override // android.os.Parcelable.Creator
            public TextModifyOperation[] newArray(int size) {
                return new TextModifyOperation[size];
            }
        };

        public TextModifyOperation(UndoOwner owner) {
            super(owner);
        }

        public TextModifyOperation(Parcel src, ClassLoader loader) {
            super(src, loader);
            this.mRangeStart = src.readInt();
            this.mRangeEnd = src.readInt();
            this.mOldText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(src);
        }

        @Override // android.content.UndoOperation
        public void commit() {
        }

        @Override // android.content.UndoOperation
        public void undo() {
            swapText();
        }

        @Override // android.content.UndoOperation
        public void redo() {
            swapText();
        }

        private void swapText() {
            CharSequence curText;
            TextView tv = getOwnerData();
            Editable editable = (Editable) tv.getText();
            if (this.mRangeStart >= this.mRangeEnd) {
                curText = null;
            } else {
                curText = editable.subSequence(this.mRangeStart, this.mRangeEnd);
            }
            if (this.mOldText == null) {
                editable.delete(this.mRangeStart, this.mRangeEnd);
                this.mRangeEnd = this.mRangeStart;
            } else {
                editable.replace(this.mRangeStart, this.mRangeEnd, this.mOldText);
                this.mRangeEnd = this.mRangeStart + this.mOldText.length();
            }
            this.mOldText = curText;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mRangeStart);
            dest.writeInt(this.mRangeEnd);
            TextUtils.writeToParcel(this.mOldText, dest, flags);
        }
    }
}