package android.filterpacks.imageproc;

import android.app.Instrumentation;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import java.io.OutputStream;

/* loaded from: ImageEncoder.class */
public class ImageEncoder extends Filter {
    @GenerateFieldPort(name = Instrumentation.REPORT_KEY_STREAMRESULT)
    private OutputStream mOutputStream;
    @GenerateFieldPort(name = "quality", hasDefault = true)
    private int mQuality;

    public ImageEncoder(String name) {
        super(name);
        this.mQuality = 80;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("image", ImageFormat.create(3, 0));
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame input = pullInput("image");
        Bitmap bitmap = input.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, this.mQuality, this.mOutputStream);
    }
}