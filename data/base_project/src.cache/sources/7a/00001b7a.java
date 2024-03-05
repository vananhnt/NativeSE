package com.android.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;

/* loaded from: LockPatternView.class */
public class LockPatternView extends View {
    private static final int ASPECT_SQUARE = 0;
    private static final int ASPECT_LOCK_WIDTH = 1;
    private static final int ASPECT_LOCK_HEIGHT = 2;
    private static final boolean PROFILE_DRAWING = false;
    private boolean mDrawingProfilingStarted;
    private Paint mPaint;
    private Paint mPathPaint;
    private static final int MILLIS_PER_CIRCLE_ANIMATING = 700;
    private static final float DRAG_THRESHHOLD = 0.0f;
    private OnPatternListener mOnPatternListener;
    private ArrayList<Cell> mPattern;
    private boolean[][] mPatternDrawLookup;
    private float mInProgressX;
    private float mInProgressY;
    private long mAnimatingPeriodStart;
    private DisplayMode mPatternDisplayMode;
    private boolean mInputEnabled;
    private boolean mInStealthMode;
    private boolean mEnableHapticFeedback;
    private boolean mPatternInProgress;
    private float mDiameterFactor;
    private final int mStrokeAlpha = 128;
    private float mHitFactor;
    private float mSquareWidth;
    private float mSquareHeight;
    private Bitmap mBitmapBtnDefault;
    private Bitmap mBitmapBtnTouched;
    private Bitmap mBitmapCircleDefault;
    private Bitmap mBitmapCircleGreen;
    private Bitmap mBitmapCircleRed;
    private Bitmap mBitmapArrowGreenUp;
    private Bitmap mBitmapArrowRedUp;
    private final Path mCurrentPath;
    private final Rect mInvalidate;
    private final Rect mTmpInvalidateRect;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mAspect;
    private final Matrix mArrowMatrix;
    private final Matrix mCircleMatrix;

    /* loaded from: LockPatternView$DisplayMode.class */
    public enum DisplayMode {
        Correct,
        Animate,
        Wrong
    }

    /* loaded from: LockPatternView$OnPatternListener.class */
    public interface OnPatternListener {
        void onPatternStart();

        void onPatternCleared();

        void onPatternCellAdded(List<Cell> list);

        void onPatternDetected(List<Cell> list);
    }

    /* loaded from: LockPatternView$Cell.class */
    public static class Cell {
        int row;
        int column;
        static Cell[][] sCells = new Cell[3][3];

        static {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    sCells[i][j] = new Cell(i, j);
                }
            }
        }

        private Cell(int row, int column) {
            checkRange(row, column);
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return this.row;
        }

        public int getColumn() {
            return this.column;
        }

        public static synchronized Cell of(int row, int column) {
            checkRange(row, column);
            return sCells[row][column];
        }

        private static void checkRange(int row, int column) {
            if (row < 0 || row > 2) {
                throw new IllegalArgumentException("row must be in range 0-2");
            }
            if (column < 0 || column > 2) {
                throw new IllegalArgumentException("column must be in range 0-2");
            }
        }

        public String toString() {
            return "(row=" + this.row + ",clmn=" + this.column + Separators.RPAREN;
        }
    }

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawingProfilingStarted = false;
        this.mPaint = new Paint();
        this.mPathPaint = new Paint();
        this.mPattern = new ArrayList<>(9);
        this.mPatternDrawLookup = new boolean[3][3];
        this.mInProgressX = -1.0f;
        this.mInProgressY = -1.0f;
        this.mPatternDisplayMode = DisplayMode.Correct;
        this.mInputEnabled = true;
        this.mInStealthMode = false;
        this.mEnableHapticFeedback = true;
        this.mPatternInProgress = false;
        this.mDiameterFactor = 0.1f;
        this.mStrokeAlpha = 128;
        this.mHitFactor = 0.6f;
        this.mCurrentPath = new Path();
        this.mInvalidate = new Rect();
        this.mTmpInvalidateRect = new Rect();
        this.mArrowMatrix = new Matrix();
        this.mCircleMatrix = new Matrix();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LockPatternView);
        String aspect = a.getString(0);
        if ("square".equals(aspect)) {
            this.mAspect = 0;
        } else if ("lock_width".equals(aspect)) {
            this.mAspect = 1;
        } else if ("lock_height".equals(aspect)) {
            this.mAspect = 2;
        } else {
            this.mAspect = 0;
        }
        setClickable(true);
        this.mPathPaint.setAntiAlias(true);
        this.mPathPaint.setDither(true);
        this.mPathPaint.setColor(-1);
        this.mPathPaint.setAlpha(128);
        this.mPathPaint.setStyle(Paint.Style.STROKE);
        this.mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mBitmapBtnDefault = getBitmapFor(R.drawable.btn_code_lock_default_holo);
        this.mBitmapBtnTouched = getBitmapFor(R.drawable.btn_code_lock_touched_holo);
        this.mBitmapCircleDefault = getBitmapFor(R.drawable.indicator_code_lock_point_area_default_holo);
        this.mBitmapCircleGreen = getBitmapFor(R.drawable.indicator_code_lock_point_area_green_holo);
        this.mBitmapCircleRed = getBitmapFor(R.drawable.indicator_code_lock_point_area_red_holo);
        this.mBitmapArrowGreenUp = getBitmapFor(R.drawable.indicator_code_lock_drag_direction_green_up);
        this.mBitmapArrowRedUp = getBitmapFor(R.drawable.indicator_code_lock_drag_direction_red_up);
        Bitmap[] bitmaps = {this.mBitmapBtnDefault, this.mBitmapBtnTouched, this.mBitmapCircleDefault, this.mBitmapCircleGreen, this.mBitmapCircleRed};
        for (Bitmap bitmap : bitmaps) {
            this.mBitmapWidth = Math.max(this.mBitmapWidth, bitmap.getWidth());
            this.mBitmapHeight = Math.max(this.mBitmapHeight, bitmap.getHeight());
        }
    }

    private Bitmap getBitmapFor(int resId) {
        return BitmapFactory.decodeResource(getContext().getResources(), resId);
    }

    public boolean isInStealthMode() {
        return this.mInStealthMode;
    }

    public boolean isTactileFeedbackEnabled() {
        return this.mEnableHapticFeedback;
    }

    public void setInStealthMode(boolean inStealthMode) {
        this.mInStealthMode = inStealthMode;
    }

    public void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
        this.mEnableHapticFeedback = tactileFeedbackEnabled;
    }

    public void setOnPatternListener(OnPatternListener onPatternListener) {
        this.mOnPatternListener = onPatternListener;
    }

    public void setPattern(DisplayMode displayMode, List<Cell> pattern) {
        this.mPattern.clear();
        this.mPattern.addAll(pattern);
        clearPatternDrawLookup();
        for (Cell cell : pattern) {
            this.mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        }
        setDisplayMode(displayMode);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.mPatternDisplayMode = displayMode;
        if (displayMode == DisplayMode.Animate) {
            if (this.mPattern.size() == 0) {
                throw new IllegalStateException("you must have a pattern to animate if you want to set the display mode to animate");
            }
            this.mAnimatingPeriodStart = SystemClock.elapsedRealtime();
            Cell first = this.mPattern.get(0);
            this.mInProgressX = getCenterXForColumn(first.getColumn());
            this.mInProgressY = getCenterYForRow(first.getRow());
            clearPatternDrawLookup();
        }
        invalidate();
    }

    private void notifyCellAdded() {
        sendAccessEvent(R.string.lockscreen_access_pattern_cell_added);
        if (this.mOnPatternListener != null) {
            this.mOnPatternListener.onPatternCellAdded(this.mPattern);
        }
    }

    private void notifyPatternStarted() {
        sendAccessEvent(R.string.lockscreen_access_pattern_start);
        if (this.mOnPatternListener != null) {
            this.mOnPatternListener.onPatternStart();
        }
    }

    private void notifyPatternDetected() {
        sendAccessEvent(R.string.lockscreen_access_pattern_detected);
        if (this.mOnPatternListener != null) {
            this.mOnPatternListener.onPatternDetected(this.mPattern);
        }
    }

    private void notifyPatternCleared() {
        sendAccessEvent(R.string.lockscreen_access_pattern_cleared);
        if (this.mOnPatternListener != null) {
            this.mOnPatternListener.onPatternCleared();
        }
    }

    public void clearPattern() {
        resetPattern();
    }

    private void resetPattern() {
        this.mPattern.clear();
        clearPatternDrawLookup();
        this.mPatternDisplayMode = DisplayMode.Correct;
        invalidate();
    }

    private void clearPatternDrawLookup() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.mPatternDrawLookup[i][j] = false;
            }
        }
    }

    public void disableInput() {
        this.mInputEnabled = false;
    }

    public void enableInput() {
        this.mInputEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        int width = (w - this.mPaddingLeft) - this.mPaddingRight;
        this.mSquareWidth = width / 3.0f;
        int height = (h - this.mPaddingTop) - this.mPaddingBottom;
        this.mSquareHeight = height / 3.0f;
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result;
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case Integer.MIN_VALUE:
                result = Math.max(specSize, desired);
                break;
            case 0:
                result = desired;
                break;
            case 1073741824:
            default:
                result = specSize;
                break;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int getSuggestedMinimumWidth() {
        return 3 * this.mBitmapWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int getSuggestedMinimumHeight() {
        return 3 * this.mBitmapWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
        switch (this.mAspect) {
            case 0:
                int min = Math.min(viewWidth, viewHeight);
                viewHeight = min;
                viewWidth = min;
                break;
            case 1:
                viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case 2:
                viewWidth = Math.min(viewWidth, viewHeight);
                break;
        }
        setMeasuredDimension(viewWidth, viewHeight);
    }

    private Cell detectAndAddHit(float x, float y) {
        Cell cell = checkForNewHit(x, y);
        if (cell != null) {
            Cell fillInGapCell = null;
            ArrayList<Cell> pattern = this.mPattern;
            if (!pattern.isEmpty()) {
                Cell lastCell = pattern.get(pattern.size() - 1);
                int dRow = cell.row - lastCell.row;
                int dColumn = cell.column - lastCell.column;
                int fillInRow = lastCell.row;
                int fillInColumn = lastCell.column;
                if (Math.abs(dRow) == 2 && Math.abs(dColumn) != 1) {
                    fillInRow = lastCell.row + (dRow > 0 ? 1 : -1);
                }
                if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
                    fillInColumn = lastCell.column + (dColumn > 0 ? 1 : -1);
                }
                fillInGapCell = Cell.of(fillInRow, fillInColumn);
            }
            if (fillInGapCell != null && !this.mPatternDrawLookup[fillInGapCell.row][fillInGapCell.column]) {
                addCellToPattern(fillInGapCell);
            }
            addCellToPattern(cell);
            if (this.mEnableHapticFeedback) {
                performHapticFeedback(1, 3);
            }
            return cell;
        }
        return null;
    }

    private void addCellToPattern(Cell newCell) {
        this.mPatternDrawLookup[newCell.getRow()][newCell.getColumn()] = true;
        this.mPattern.add(newCell);
        notifyCellAdded();
    }

    private Cell checkForNewHit(float x, float y) {
        int columnHit;
        int rowHit = getRowHit(y);
        if (rowHit < 0 || (columnHit = getColumnHit(x)) < 0 || this.mPatternDrawLookup[rowHit][columnHit]) {
            return null;
        }
        return Cell.of(rowHit, columnHit);
    }

    private int getRowHit(float y) {
        float squareHeight = this.mSquareHeight;
        float hitSize = squareHeight * this.mHitFactor;
        float offset = this.mPaddingTop + ((squareHeight - hitSize) / 2.0f);
        for (int i = 0; i < 3; i++) {
            float hitTop = offset + (squareHeight * i);
            if (y >= hitTop && y <= hitTop + hitSize) {
                return i;
            }
        }
        return -1;
    }

    private int getColumnHit(float x) {
        float squareWidth = this.mSquareWidth;
        float hitSize = squareWidth * this.mHitFactor;
        float offset = this.mPaddingLeft + ((squareWidth - hitSize) / 2.0f);
        for (int i = 0; i < 3; i++) {
            float hitLeft = offset + (squareWidth * i);
            if (x >= hitLeft && x <= hitLeft + hitSize) {
                return i;
            }
        }
        return -1;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        if (AccessibilityManager.getInstance(this.mContext).isTouchExplorationEnabled()) {
            int action = event.getAction();
            switch (action) {
                case 7:
                    event.setAction(2);
                    break;
                case 9:
                    event.setAction(0);
                    break;
                case 10:
                    event.setAction(1);
                    break;
            }
            onTouchEvent(event);
            event.setAction(action);
        }
        return super.onHoverEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mInputEnabled || !isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                handleActionDown(event);
                return true;
            case 1:
                handleActionUp(event);
                return true;
            case 2:
                handleActionMove(event);
                return true;
            case 3:
                if (this.mPatternInProgress) {
                    this.mPatternInProgress = false;
                    resetPattern();
                    notifyPatternCleared();
                    return true;
                }
                return true;
            default:
                return false;
        }
    }

    private void handleActionMove(MotionEvent event) {
        float radius = this.mSquareWidth * this.mDiameterFactor * 0.5f;
        int historySize = event.getHistorySize();
        this.mTmpInvalidateRect.setEmpty();
        boolean invalidateNow = false;
        int i = 0;
        while (i < historySize + 1) {
            float x = i < historySize ? event.getHistoricalX(i) : event.getX();
            float y = i < historySize ? event.getHistoricalY(i) : event.getY();
            Cell hitCell = detectAndAddHit(x, y);
            int patternSize = this.mPattern.size();
            if (hitCell != null && patternSize == 1) {
                this.mPatternInProgress = true;
                notifyPatternStarted();
            }
            float dx = Math.abs(x - this.mInProgressX);
            float dy = Math.abs(y - this.mInProgressY);
            if (dx > 0.0f || dy > 0.0f) {
                invalidateNow = true;
            }
            if (this.mPatternInProgress && patternSize > 0) {
                ArrayList<Cell> pattern = this.mPattern;
                Cell lastCell = pattern.get(patternSize - 1);
                float lastCellCenterX = getCenterXForColumn(lastCell.column);
                float lastCellCenterY = getCenterYForRow(lastCell.row);
                float left = Math.min(lastCellCenterX, x) - radius;
                float right = Math.max(lastCellCenterX, x) + radius;
                float top = Math.min(lastCellCenterY, y) - radius;
                float bottom = Math.max(lastCellCenterY, y) + radius;
                if (hitCell != null) {
                    float width = this.mSquareWidth * 0.5f;
                    float height = this.mSquareHeight * 0.5f;
                    float hitCellCenterX = getCenterXForColumn(hitCell.column);
                    float hitCellCenterY = getCenterYForRow(hitCell.row);
                    left = Math.min(hitCellCenterX - width, left);
                    right = Math.max(hitCellCenterX + width, right);
                    top = Math.min(hitCellCenterY - height, top);
                    bottom = Math.max(hitCellCenterY + height, bottom);
                }
                this.mTmpInvalidateRect.union(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
            }
            i++;
        }
        this.mInProgressX = event.getX();
        this.mInProgressY = event.getY();
        if (invalidateNow) {
            this.mInvalidate.union(this.mTmpInvalidateRect);
            invalidate(this.mInvalidate);
            this.mInvalidate.set(this.mTmpInvalidateRect);
        }
    }

    private void sendAccessEvent(int resId) {
        announceForAccessibility(this.mContext.getString(resId));
    }

    private void handleActionUp(MotionEvent event) {
        if (!this.mPattern.isEmpty()) {
            this.mPatternInProgress = false;
            notifyPatternDetected();
            invalidate();
        }
    }

    private void handleActionDown(MotionEvent event) {
        resetPattern();
        float x = event.getX();
        float y = event.getY();
        Cell hitCell = detectAndAddHit(x, y);
        if (hitCell != null) {
            this.mPatternInProgress = true;
            this.mPatternDisplayMode = DisplayMode.Correct;
            notifyPatternStarted();
        } else if (this.mPatternInProgress) {
            this.mPatternInProgress = false;
            notifyPatternCleared();
        }
        if (hitCell != null) {
            float startX = getCenterXForColumn(hitCell.column);
            float startY = getCenterYForRow(hitCell.row);
            float widthOffset = this.mSquareWidth / 2.0f;
            float heightOffset = this.mSquareHeight / 2.0f;
            invalidate((int) (startX - widthOffset), (int) (startY - heightOffset), (int) (startX + widthOffset), (int) (startY + heightOffset));
        }
        this.mInProgressX = x;
        this.mInProgressY = y;
    }

    private float getCenterXForColumn(int column) {
        return this.mPaddingLeft + (column * this.mSquareWidth) + (this.mSquareWidth / 2.0f);
    }

    private float getCenterYForRow(int row) {
        return this.mPaddingTop + (row * this.mSquareHeight) + (this.mSquareHeight / 2.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        ArrayList<Cell> pattern = this.mPattern;
        int count = pattern.size();
        boolean[][] drawLookup = this.mPatternDrawLookup;
        if (this.mPatternDisplayMode == DisplayMode.Animate) {
            int oneCycle = (count + 1) * 700;
            int spotInCycle = ((int) (SystemClock.elapsedRealtime() - this.mAnimatingPeriodStart)) % oneCycle;
            int numCircles = spotInCycle / 700;
            clearPatternDrawLookup();
            for (int i = 0; i < numCircles; i++) {
                Cell cell = pattern.get(i);
                drawLookup[cell.getRow()][cell.getColumn()] = true;
            }
            boolean needToUpdateInProgressPoint = numCircles > 0 && numCircles < count;
            if (needToUpdateInProgressPoint) {
                float percentageOfNextCircle = (spotInCycle % 700) / 700.0f;
                Cell currentCell = pattern.get(numCircles - 1);
                float centerX = getCenterXForColumn(currentCell.column);
                float centerY = getCenterYForRow(currentCell.row);
                Cell nextCell = pattern.get(numCircles);
                float dx = percentageOfNextCircle * (getCenterXForColumn(nextCell.column) - centerX);
                float dy = percentageOfNextCircle * (getCenterYForRow(nextCell.row) - centerY);
                this.mInProgressX = centerX + dx;
                this.mInProgressY = centerY + dy;
            }
            invalidate();
        }
        float squareWidth = this.mSquareWidth;
        float squareHeight = this.mSquareHeight;
        float radius = squareWidth * this.mDiameterFactor * 0.5f;
        this.mPathPaint.setStrokeWidth(radius);
        Path currentPath = this.mCurrentPath;
        currentPath.rewind();
        int paddingTop = this.mPaddingTop;
        int paddingLeft = this.mPaddingLeft;
        for (int i2 = 0; i2 < 3; i2++) {
            float topY = paddingTop + (i2 * squareHeight);
            for (int j = 0; j < 3; j++) {
                float leftX = paddingLeft + (j * squareWidth);
                drawCircle(canvas, (int) leftX, (int) topY, drawLookup[i2][j]);
            }
        }
        boolean drawPath = !this.mInStealthMode || this.mPatternDisplayMode == DisplayMode.Wrong;
        boolean oldFlag = (this.mPaint.getFlags() & 2) != 0;
        this.mPaint.setFilterBitmap(true);
        if (drawPath) {
            for (int i3 = 0; i3 < count - 1; i3++) {
                Cell cell2 = pattern.get(i3);
                Cell next = pattern.get(i3 + 1);
                if (!drawLookup[next.row][next.column]) {
                    break;
                }
                float leftX2 = paddingLeft + (cell2.column * squareWidth);
                float topY2 = paddingTop + (cell2.row * squareHeight);
                drawArrow(canvas, leftX2, topY2, cell2, next);
            }
        }
        if (drawPath) {
            boolean anyCircles = false;
            for (int i4 = 0; i4 < count; i4++) {
                Cell cell3 = pattern.get(i4);
                if (!drawLookup[cell3.row][cell3.column]) {
                    break;
                }
                anyCircles = true;
                float centerX2 = getCenterXForColumn(cell3.column);
                float centerY2 = getCenterYForRow(cell3.row);
                if (i4 == 0) {
                    currentPath.moveTo(centerX2, centerY2);
                } else {
                    currentPath.lineTo(centerX2, centerY2);
                }
            }
            if ((this.mPatternInProgress || this.mPatternDisplayMode == DisplayMode.Animate) && anyCircles) {
                currentPath.lineTo(this.mInProgressX, this.mInProgressY);
            }
            canvas.drawPath(currentPath, this.mPathPaint);
        }
        this.mPaint.setFilterBitmap(oldFlag);
    }

    private void drawArrow(Canvas canvas, float leftX, float topY, Cell start, Cell end) {
        boolean green = this.mPatternDisplayMode != DisplayMode.Wrong;
        int endRow = end.row;
        int startRow = start.row;
        int endColumn = end.column;
        int startColumn = start.column;
        int offsetX = (((int) this.mSquareWidth) - this.mBitmapWidth) / 2;
        int offsetY = (((int) this.mSquareHeight) - this.mBitmapHeight) / 2;
        Bitmap arrow = green ? this.mBitmapArrowGreenUp : this.mBitmapArrowRedUp;
        int cellWidth = this.mBitmapWidth;
        int cellHeight = this.mBitmapHeight;
        float theta = (float) Math.atan2(endRow - startRow, endColumn - startColumn);
        float angle = ((float) Math.toDegrees(theta)) + 90.0f;
        float sx = Math.min(this.mSquareWidth / this.mBitmapWidth, 1.0f);
        float sy = Math.min(this.mSquareHeight / this.mBitmapHeight, 1.0f);
        this.mArrowMatrix.setTranslate(leftX + offsetX, topY + offsetY);
        this.mArrowMatrix.preTranslate(this.mBitmapWidth / 2, this.mBitmapHeight / 2);
        this.mArrowMatrix.preScale(sx, sy);
        this.mArrowMatrix.preTranslate((-this.mBitmapWidth) / 2, (-this.mBitmapHeight) / 2);
        this.mArrowMatrix.preRotate(angle, cellWidth / 2.0f, cellHeight / 2.0f);
        this.mArrowMatrix.preTranslate((cellWidth - arrow.getWidth()) / 2.0f, 0.0f);
        canvas.drawBitmap(arrow, this.mArrowMatrix, this.mPaint);
    }

    private void drawCircle(Canvas canvas, int leftX, int topY, boolean partOfPattern) {
        Bitmap outerCircle;
        Bitmap innerCircle;
        if (!partOfPattern || (this.mInStealthMode && this.mPatternDisplayMode != DisplayMode.Wrong)) {
            outerCircle = this.mBitmapCircleDefault;
            innerCircle = this.mBitmapBtnDefault;
        } else if (this.mPatternInProgress) {
            outerCircle = this.mBitmapCircleGreen;
            innerCircle = this.mBitmapBtnTouched;
        } else if (this.mPatternDisplayMode == DisplayMode.Wrong) {
            outerCircle = this.mBitmapCircleRed;
            innerCircle = this.mBitmapBtnDefault;
        } else if (this.mPatternDisplayMode == DisplayMode.Correct || this.mPatternDisplayMode == DisplayMode.Animate) {
            outerCircle = this.mBitmapCircleGreen;
            innerCircle = this.mBitmapBtnDefault;
        } else {
            throw new IllegalStateException("unknown display mode " + this.mPatternDisplayMode);
        }
        int width = this.mBitmapWidth;
        int height = this.mBitmapHeight;
        float squareWidth = this.mSquareWidth;
        float squareHeight = this.mSquareHeight;
        int offsetX = (int) ((squareWidth - width) / 2.0f);
        int offsetY = (int) ((squareHeight - height) / 2.0f);
        float sx = Math.min(this.mSquareWidth / this.mBitmapWidth, 1.0f);
        float sy = Math.min(this.mSquareHeight / this.mBitmapHeight, 1.0f);
        this.mCircleMatrix.setTranslate(leftX + offsetX, topY + offsetY);
        this.mCircleMatrix.preTranslate(this.mBitmapWidth / 2, this.mBitmapHeight / 2);
        this.mCircleMatrix.preScale(sx, sy);
        this.mCircleMatrix.preTranslate((-this.mBitmapWidth) / 2, (-this.mBitmapHeight) / 2);
        canvas.drawBitmap(outerCircle, this.mCircleMatrix, this.mPaint);
        canvas.drawBitmap(innerCircle, this.mCircleMatrix, this.mPaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, LockPatternUtils.patternToString(this.mPattern), this.mPatternDisplayMode.ordinal(), this.mInputEnabled, this.mInStealthMode, this.mEnableHapticFeedback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setPattern(DisplayMode.Correct, LockPatternUtils.stringToPattern(ss.getSerializedPattern()));
        this.mPatternDisplayMode = DisplayMode.values()[ss.getDisplayMode()];
        this.mInputEnabled = ss.isInputEnabled();
        this.mInStealthMode = ss.isInStealthMode();
        this.mEnableHapticFeedback = ss.isTactileFeedbackEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LockPatternView$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        private final String mSerializedPattern;
        private final int mDisplayMode;
        private final boolean mInputEnabled;
        private final boolean mInStealthMode;
        private final boolean mTactileFeedbackEnabled;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.widget.LockPatternView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcelable superState, String serializedPattern, int displayMode, boolean inputEnabled, boolean inStealthMode, boolean tactileFeedbackEnabled) {
            super(superState);
            this.mSerializedPattern = serializedPattern;
            this.mDisplayMode = displayMode;
            this.mInputEnabled = inputEnabled;
            this.mInStealthMode = inStealthMode;
            this.mTactileFeedbackEnabled = tactileFeedbackEnabled;
        }

        private SavedState(Parcel in) {
            super(in);
            this.mSerializedPattern = in.readString();
            this.mDisplayMode = in.readInt();
            this.mInputEnabled = ((Boolean) in.readValue(null)).booleanValue();
            this.mInStealthMode = ((Boolean) in.readValue(null)).booleanValue();
            this.mTactileFeedbackEnabled = ((Boolean) in.readValue(null)).booleanValue();
        }

        public String getSerializedPattern() {
            return this.mSerializedPattern;
        }

        public int getDisplayMode() {
            return this.mDisplayMode;
        }

        public boolean isInputEnabled() {
            return this.mInputEnabled;
        }

        public boolean isInStealthMode() {
            return this.mInStealthMode;
        }

        public boolean isTactileFeedbackEnabled() {
            return this.mTactileFeedbackEnabled;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.mSerializedPattern);
            dest.writeInt(this.mDisplayMode);
            dest.writeValue(Boolean.valueOf(this.mInputEnabled));
            dest.writeValue(Boolean.valueOf(this.mInStealthMode));
            dest.writeValue(Boolean.valueOf(this.mTactileFeedbackEnabled));
        }
    }
}