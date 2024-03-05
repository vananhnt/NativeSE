package android.support.v4.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import java.util.List;
import java.util.Map;

/* loaded from: SharedElementCallback.class */
public abstract class SharedElementCallback {
    private Matrix mTempMatrix;

    public Parcelable onCaptureSharedElementSnapshot(View view, Matrix matrix, RectF rectF) {
        Bitmap bitmap;
        int round = Math.round(rectF.width());
        int round2 = Math.round(rectF.height());
        if (round <= 0 || round2 <= 0) {
            bitmap = null;
        } else {
            if (this.mTempMatrix == null) {
                this.mTempMatrix = new Matrix();
            }
            this.mTempMatrix.set(matrix);
            this.mTempMatrix.postTranslate(-rectF.left, -rectF.top);
            Bitmap createBitmap = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.concat(this.mTempMatrix);
            view.draw(canvas);
            bitmap = createBitmap;
        }
        return bitmap;
    }

    public View onCreateSnapshotView(Context context, Parcelable parcelable) {
        ImageView imageView;
        if (parcelable instanceof Bitmap) {
            imageView = new ImageView(context);
            imageView.setImageBitmap((Bitmap) parcelable);
        } else {
            imageView = null;
        }
        return imageView;
    }

    public void onMapSharedElements(List<String> list, Map<String, View> map) {
    }

    public void onRejectSharedElements(List<View> list) {
    }

    public void onSharedElementEnd(List<String> list, List<View> list2, List<View> list3) {
    }

    public void onSharedElementStart(List<String> list, List<View> list2, List<View> list3) {
    }
}