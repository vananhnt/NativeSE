package android.gesture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: Gesture.class */
public class Gesture implements Parcelable {
    private static final int BITMAP_RENDERING_WIDTH = 2;
    private static final boolean BITMAP_RENDERING_ANTIALIAS = true;
    private static final boolean BITMAP_RENDERING_DITHER = true;
    private static final long GESTURE_ID_BASE = System.currentTimeMillis();
    private static final AtomicInteger sGestureCount = new AtomicInteger(0);
    public static final Parcelable.Creator<Gesture> CREATOR = new Parcelable.Creator<Gesture>() { // from class: android.gesture.Gesture.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Gesture createFromParcel(Parcel in) {
            Gesture gesture = null;
            long gestureID = in.readLong();
            DataInputStream inStream = new DataInputStream(new ByteArrayInputStream(in.createByteArray()));
            try {
                try {
                    gesture = Gesture.deserialize(inStream);
                    GestureUtils.closeStream(inStream);
                } catch (IOException e) {
                    Log.e(GestureConstants.LOG_TAG, "Error reading Gesture from parcel:", e);
                    GestureUtils.closeStream(inStream);
                }
                if (gesture != null) {
                    gesture.mGestureID = gestureID;
                }
                return gesture;
            } catch (Throwable th) {
                GestureUtils.closeStream(inStream);
                throw th;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Gesture[] newArray(int size) {
            return new Gesture[size];
        }
    };
    private final RectF mBoundingBox = new RectF();
    private final ArrayList<GestureStroke> mStrokes = new ArrayList<>();
    private long mGestureID = GESTURE_ID_BASE + sGestureCount.incrementAndGet();

    public Object clone() {
        Gesture gesture = new Gesture();
        gesture.mBoundingBox.set(this.mBoundingBox.left, this.mBoundingBox.top, this.mBoundingBox.right, this.mBoundingBox.bottom);
        int count = this.mStrokes.size();
        for (int i = 0; i < count; i++) {
            GestureStroke stroke = this.mStrokes.get(i);
            gesture.mStrokes.add((GestureStroke) stroke.clone());
        }
        return gesture;
    }

    public ArrayList<GestureStroke> getStrokes() {
        return this.mStrokes;
    }

    public int getStrokesCount() {
        return this.mStrokes.size();
    }

    public void addStroke(GestureStroke stroke) {
        this.mStrokes.add(stroke);
        this.mBoundingBox.union(stroke.boundingBox);
    }

    public float getLength() {
        int len = 0;
        ArrayList<GestureStroke> strokes = this.mStrokes;
        int count = strokes.size();
        for (int i = 0; i < count; i++) {
            len = (int) (len + strokes.get(i).length);
        }
        return len;
    }

    public RectF getBoundingBox() {
        return this.mBoundingBox;
    }

    public Path toPath() {
        return toPath(null);
    }

    public Path toPath(Path path) {
        if (path == null) {
            path = new Path();
        }
        ArrayList<GestureStroke> strokes = this.mStrokes;
        int count = strokes.size();
        for (int i = 0; i < count; i++) {
            path.addPath(strokes.get(i).getPath());
        }
        return path;
    }

    public Path toPath(int width, int height, int edge, int numSample) {
        return toPath(null, width, height, edge, numSample);
    }

    public Path toPath(Path path, int width, int height, int edge, int numSample) {
        if (path == null) {
            path = new Path();
        }
        ArrayList<GestureStroke> strokes = this.mStrokes;
        int count = strokes.size();
        for (int i = 0; i < count; i++) {
            path.addPath(strokes.get(i).toPath(width - (2 * edge), height - (2 * edge), numSample));
        }
        return path;
    }

    void setID(long id) {
        this.mGestureID = id;
    }

    public long getID() {
        return this.mGestureID;
    }

    public Bitmap toBitmap(int width, int height, int edge, int numSample, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(edge, edge);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2.0f);
        ArrayList<GestureStroke> strokes = this.mStrokes;
        int count = strokes.size();
        for (int i = 0; i < count; i++) {
            Path path = strokes.get(i).toPath(width - (2 * edge), height - (2 * edge), numSample);
            canvas.drawPath(path, paint);
        }
        return bitmap;
    }

    public Bitmap toBitmap(int width, int height, int inset, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2.0f);
        Path path = toPath();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        float sx = (width - (2 * inset)) / bounds.width();
        float sy = (height - (2 * inset)) / bounds.height();
        float scale = sx > sy ? sy : sx;
        paint.setStrokeWidth(2.0f / scale);
        path.offset((-bounds.left) + ((width - (bounds.width() * scale)) / 2.0f), (-bounds.top) + ((height - (bounds.height() * scale)) / 2.0f));
        canvas.translate(inset, inset);
        canvas.scale(scale, scale);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void serialize(DataOutputStream out) throws IOException {
        ArrayList<GestureStroke> strokes = this.mStrokes;
        int count = strokes.size();
        out.writeLong(this.mGestureID);
        out.writeInt(count);
        for (int i = 0; i < count; i++) {
            strokes.get(i).serialize(out);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Gesture deserialize(DataInputStream in) throws IOException {
        Gesture gesture = new Gesture();
        gesture.mGestureID = in.readLong();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            gesture.addStroke(GestureStroke.deserialize(in));
        }
        return gesture;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mGestureID);
        boolean result = false;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(32768);
        DataOutputStream outStream = new DataOutputStream(byteStream);
        try {
            try {
                serialize(outStream);
                result = true;
                GestureUtils.closeStream(outStream);
                GestureUtils.closeStream(byteStream);
            } catch (IOException e) {
                Log.e(GestureConstants.LOG_TAG, "Error writing Gesture to parcel:", e);
                GestureUtils.closeStream(outStream);
                GestureUtils.closeStream(byteStream);
            }
            if (result) {
                out.writeByteArray(byteStream.toByteArray());
            }
        } catch (Throwable th) {
            GestureUtils.closeStream(outStream);
            GestureUtils.closeStream(byteStream);
            throw th;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}