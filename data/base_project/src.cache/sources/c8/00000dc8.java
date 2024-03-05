package android.renderscript;

import android.content.res.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: ScriptC.class */
public class ScriptC extends Script {
    private static final String TAG = "ScriptC";
    private static final String CACHE_PATH = "com.android.renderscript.cache";
    static String mCachePath;

    protected ScriptC(int id, RenderScript rs) {
        super(id, rs);
    }

    protected ScriptC(RenderScript rs, Resources resources, int resourceID) {
        super(0, rs);
        int id = internalCreate(rs, resources, resourceID);
        if (id == 0) {
            throw new RSRuntimeException("Loading of ScriptC script failed.");
        }
        setID(id);
    }

    private static synchronized int internalCreate(RenderScript rs, Resources resources, int resourceID) {
        InputStream is = resources.openRawResource(resourceID);
        try {
            byte[] pgm = new byte[1024];
            int pgmLength = 0;
            while (true) {
                int bytesLeft = pgm.length - pgmLength;
                if (bytesLeft == 0) {
                    byte[] buf2 = new byte[pgm.length * 2];
                    System.arraycopy(pgm, 0, buf2, 0, pgm.length);
                    pgm = buf2;
                    bytesLeft = pgm.length - pgmLength;
                }
                int bytesRead = is.read(pgm, pgmLength, bytesLeft);
                if (bytesRead <= 0) {
                    break;
                }
                pgmLength += bytesRead;
            }
            is.close();
            String resName = resources.getResourceEntryName(resourceID);
            if (mCachePath == null) {
                File f = new File(RenderScript.mCacheDir, CACHE_PATH);
                mCachePath = f.getAbsolutePath();
                f.mkdirs();
            }
            return rs.nScriptCCreate(resName, mCachePath, pgm, pgmLength);
        } catch (IOException e) {
            throw new Resources.NotFoundException();
        }
    }
}