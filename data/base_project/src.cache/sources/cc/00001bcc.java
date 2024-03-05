package com.android.server;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Atlas;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.GraphicBuffer;
import android.view.IAssetAtlas;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: AssetAtlasService.class */
public class AssetAtlasService extends IAssetAtlas.Stub {
    public static final String ASSET_ATLAS_SERVICE = "assetatlas";
    private static final String LOG_TAG = "Atlas";
    private static final boolean DEBUG_ATLAS = true;
    private static final boolean DEBUG_ATLAS_TEXTURE = false;
    private static final int MIN_SIZE = 768;
    private static final int MAX_SIZE = 2048;
    private static final int STEP = 64;
    private static final float PACKING_THRESHOLD = 0.8f;
    private static final int ATLAS_MAP_ENTRY_FIELD_COUNT = 4;
    private static final int GRAPHIC_BUFFER_USAGE = 256;
    private final AtomicBoolean mAtlasReady = new AtomicBoolean(false);
    private final Context mContext;
    private final String mVersionName;
    private GraphicBuffer mBuffer;
    private int[] mAtlasMap;

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nAcquireAtlasCanvas(Canvas canvas, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nReleaseAtlasCanvas(Canvas canvas, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean nUploadAtlas(GraphicBuffer graphicBuffer, int i);

    public AssetAtlasService(Context context) {
        this.mContext = context;
        this.mVersionName = queryVersionName(context);
        ArrayList<Bitmap> bitmaps = new ArrayList<>(300);
        int totalPixelCount = 0;
        Resources resources = context.getResources();
        LongSparseArray<Drawable.ConstantState> drawables = resources.getPreloadedDrawables();
        int count = drawables.size();
        for (int i = 0; i < count; i++) {
            Bitmap bitmap = drawables.valueAt(i).getBitmap();
            if (bitmap != null && bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
                bitmaps.add(bitmap);
                totalPixelCount += bitmap.getWidth() * bitmap.getHeight();
            }
        }
        Collections.sort(bitmaps, new Comparator<Bitmap>() { // from class: com.android.server.AssetAtlasService.1
            @Override // java.util.Comparator
            public int compare(Bitmap b1, Bitmap b2) {
                if (b1.getWidth() == b2.getWidth()) {
                    return b2.getHeight() - b1.getHeight();
                }
                return b2.getWidth() - b1.getWidth();
            }
        });
        new Thread(new Renderer(bitmaps, totalPixelCount)).start();
    }

    private static String queryVersionName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(LOG_TAG, "Could not get package info", e);
            return null;
        }
    }

    public void systemRunning() {
    }

    /* loaded from: AssetAtlasService$Renderer.class */
    private class Renderer implements Runnable {
        private final ArrayList<Bitmap> mBitmaps;
        private final int mPixelCount;
        private int mNativeBitmap;
        private Bitmap mAtlasBitmap;

        Renderer(ArrayList<Bitmap> bitmaps, int pixelCount) {
            this.mBitmaps = bitmaps;
            this.mPixelCount = pixelCount;
        }

        @Override // java.lang.Runnable
        public void run() {
            Configuration config = AssetAtlasService.this.chooseConfiguration(this.mBitmaps, this.mPixelCount, AssetAtlasService.this.mVersionName);
            Log.d(AssetAtlasService.LOG_TAG, "Loaded configuration: " + config);
            if (config != null) {
                AssetAtlasService.this.mBuffer = GraphicBuffer.create(config.width, config.height, 1, 256);
                if (AssetAtlasService.this.mBuffer != null) {
                    Atlas atlas = new Atlas(config.type, config.width, config.height, config.flags);
                    if (renderAtlas(AssetAtlasService.this.mBuffer, atlas, config.count)) {
                        AssetAtlasService.this.mAtlasReady.set(true);
                    }
                }
            }
        }

        private boolean renderAtlas(GraphicBuffer buffer, Atlas atlas, int packCount) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            Canvas canvas = acquireCanvas(buffer.getWidth(), buffer.getHeight());
            if (canvas == null) {
                return false;
            }
            Atlas.Entry entry = new Atlas.Entry();
            AssetAtlasService.this.mAtlasMap = new int[packCount * 4];
            int[] atlasMap = AssetAtlasService.this.mAtlasMap;
            int mapIndex = 0;
            boolean result = false;
            try {
                long startRender = System.nanoTime();
                int count = this.mBitmaps.size();
                int i = 0;
                while (true) {
                    if (i >= count) {
                        break;
                    }
                    Bitmap bitmap = this.mBitmaps.get(i);
                    if (atlas.pack(bitmap.getWidth(), bitmap.getHeight(), entry) != null) {
                        if (mapIndex >= AssetAtlasService.this.mAtlasMap.length) {
                            AssetAtlasService.deleteDataFile();
                            break;
                        }
                        canvas.save();
                        canvas.translate(entry.x, entry.y);
                        if (entry.rotated) {
                            canvas.translate(bitmap.getHeight(), 0.0f);
                            canvas.rotate(90.0f);
                        }
                        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                        canvas.restore();
                        int i2 = mapIndex;
                        int mapIndex2 = mapIndex + 1;
                        atlasMap[i2] = bitmap.mNativeBitmap;
                        int mapIndex3 = mapIndex2 + 1;
                        atlasMap[mapIndex2] = entry.x;
                        int mapIndex4 = mapIndex3 + 1;
                        atlasMap[mapIndex3] = entry.y;
                        mapIndex = mapIndex4 + 1;
                        atlasMap[mapIndex4] = entry.rotated ? 1 : 0;
                    }
                    i++;
                }
                long endRender = System.nanoTime();
                if (this.mNativeBitmap != 0) {
                    result = AssetAtlasService.nUploadAtlas(buffer, this.mNativeBitmap);
                }
                long endUpload = System.nanoTime();
                float renderDuration = (((float) (endRender - startRender)) / 1000.0f) / 1000.0f;
                float uploadDuration = (((float) (endUpload - endRender)) / 1000.0f) / 1000.0f;
                Log.d(AssetAtlasService.LOG_TAG, String.format("Rendered atlas in %.2fms (%.2f+%.2fms)", Float.valueOf(renderDuration + uploadDuration), Float.valueOf(renderDuration), Float.valueOf(uploadDuration)));
                releaseCanvas(canvas);
                return result;
            } catch (Throwable th) {
                releaseCanvas(canvas);
                throw th;
            }
        }

        private Canvas acquireCanvas(int width, int height) {
            Canvas canvas = new Canvas();
            this.mNativeBitmap = AssetAtlasService.nAcquireAtlasCanvas(canvas, width, height);
            return canvas;
        }

        private void releaseCanvas(Canvas canvas) {
            AssetAtlasService.nReleaseAtlasCanvas(canvas, this.mNativeBitmap);
        }
    }

    @Override // android.view.IAssetAtlas
    public boolean isCompatible(int ppid) {
        return ppid == Process.myPpid();
    }

    @Override // android.view.IAssetAtlas
    public GraphicBuffer getBuffer() throws RemoteException {
        if (this.mAtlasReady.get()) {
            return this.mBuffer;
        }
        return null;
    }

    @Override // android.view.IAssetAtlas
    public int[] getMap() throws RemoteException {
        if (this.mAtlasReady.get()) {
            return this.mAtlasMap;
        }
        return null;
    }

    private static Configuration computeBestConfiguration(ArrayList<Bitmap> bitmaps, int pixelCount) {
        Log.d(LOG_TAG, "Computing best atlas configuration...");
        long begin = System.nanoTime();
        List<WorkerResult> results = Collections.synchronizedList(new ArrayList());
        int cpuCount = Runtime.getRuntime().availableProcessors();
        if (cpuCount == 1) {
            new ComputeWorker(768, 2048, 64, bitmaps, pixelCount, results, null).run();
        } else {
            int start = 768;
            int end = 2048 - ((cpuCount - 1) * 64);
            int step = 64 * cpuCount;
            CountDownLatch signal = new CountDownLatch(cpuCount);
            int i = 0;
            while (i < cpuCount) {
                ComputeWorker worker = new ComputeWorker(start, end, step, bitmaps, pixelCount, results, signal);
                new Thread(worker, "Atlas Worker #" + (i + 1)).start();
                i++;
                start += 64;
                end += 64;
            }
            try {
                signal.await(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.w(LOG_TAG, "Could not complete configuration computation");
                return null;
            }
        }
        Collections.sort(results, new Comparator<WorkerResult>() { // from class: com.android.server.AssetAtlasService.2
            @Override // java.util.Comparator
            public int compare(WorkerResult r1, WorkerResult r2) {
                int delta = r2.count - r1.count;
                return delta != 0 ? delta : (r1.width * r1.height) - (r2.width * r2.height);
            }
        });
        float delay = ((((float) (System.nanoTime() - begin)) / 1000.0f) / 1000.0f) / 1000.0f;
        Log.d(LOG_TAG, String.format("Found best atlas configuration in %.2fs", Float.valueOf(delay)));
        WorkerResult result = results.get(0);
        return new Configuration(result.type, result.width, result.height, result.count);
    }

    private static File getDataFile() {
        File systemDirectory = new File(Environment.getDataDirectory(), "system");
        return new File(systemDirectory, "framework_atlas.config");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteDataFile() {
        Log.w(LOG_TAG, "Current configuration inconsistent with assets list");
        if (!getDataFile().delete()) {
            Log.w(LOG_TAG, "Could not delete the current configuration");
        }
    }

    private File getFrameworkResourcesFile() {
        return new File(this.mContext.getApplicationInfo().sourceDir);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Configuration chooseConfiguration(ArrayList<Bitmap> bitmaps, int pixelCount, String versionName) {
        Configuration config = null;
        File dataFile = getDataFile();
        if (dataFile.exists()) {
            config = readConfiguration(dataFile, versionName);
        }
        if (config == null) {
            config = computeBestConfiguration(bitmaps, pixelCount);
            if (config != null) {
                writeConfiguration(config, dataFile, versionName);
            }
        }
        return config;
    }

    private void writeConfiguration(Configuration config, File file, String versionName) {
        BufferedWriter writer = null;
        try {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                writer.write(getBuildIdentifier(versionName));
                writer.newLine();
                writer.write(config.type.toString());
                writer.newLine();
                writer.write(String.valueOf(config.width));
                writer.newLine();
                writer.write(String.valueOf(config.height));
                writer.newLine();
                writer.write(String.valueOf(config.count));
                writer.newLine();
                writer.write(String.valueOf(config.flags));
                writer.newLine();
                if (writer == null) {
                    return;
                }
                try {
                    writer.close();
                } catch (IOException e) {
                }
            } catch (FileNotFoundException e2) {
                Log.w(LOG_TAG, "Could not write " + file, e2);
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e3) {
                    }
                }
            } catch (IOException e4) {
                Log.w(LOG_TAG, "Could not write " + file, e4);
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e5) {
                    }
                }
            }
        } catch (Throwable th) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e6) {
                }
            }
            throw th;
        }
    }

    private Configuration readConfiguration(File file, String versionName) {
        BufferedReader reader = null;
        Configuration config = null;
        try {
            try {
                try {
                    try {
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        if (checkBuildIdentifier(reader, versionName)) {
                            Atlas.Type type = Atlas.Type.valueOf(reader.readLine());
                            int width = readInt(reader, 768, 2048);
                            int height = readInt(reader, 768, 2048);
                            int count = readInt(reader, 0, Integer.MAX_VALUE);
                            int flags = readInt(reader, Integer.MIN_VALUE, Integer.MAX_VALUE);
                            config = new Configuration(type, width, height, count, flags);
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                            }
                        }
                    } catch (IllegalArgumentException e2) {
                        Log.w(LOG_TAG, "Invalid parameter value in " + file, e2);
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e3) {
                            }
                        }
                    }
                } catch (FileNotFoundException e4) {
                    Log.w(LOG_TAG, "Could not read " + file, e4);
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e5) {
                        }
                    }
                }
            } catch (IOException e6) {
                Log.w(LOG_TAG, "Could not read " + file, e6);
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e7) {
                    }
                }
            }
            return config;
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e8) {
                }
            }
            throw th;
        }
    }

    private static int readInt(BufferedReader reader, int min, int max) throws IOException {
        return Math.max(min, Math.min(max, Integer.parseInt(reader.readLine())));
    }

    private boolean checkBuildIdentifier(BufferedReader reader, String versionName) throws IOException {
        String deviceBuildId = getBuildIdentifier(versionName);
        String buildId = reader.readLine();
        return deviceBuildId.equals(buildId);
    }

    private String getBuildIdentifier(String versionName) {
        return SystemProperties.get("ro.build.fingerprint", "") + '/' + versionName + '/' + String.valueOf(getFrameworkResourcesFile().length());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AssetAtlasService$Configuration.class */
    public static class Configuration {
        final Atlas.Type type;
        final int width;
        final int height;
        final int count;
        final int flags;

        Configuration(Atlas.Type type, int width, int height, int count) {
            this(type, width, height, count, 2);
        }

        Configuration(Atlas.Type type, int width, int height, int count, int flags) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.count = count;
            this.flags = flags;
        }

        public String toString() {
            return this.type.toString() + " (" + this.width + "x" + this.height + ") flags=0x" + Integer.toHexString(this.flags) + " count=" + this.count;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AssetAtlasService$WorkerResult.class */
    public static class WorkerResult {
        Atlas.Type type;
        int width;
        int height;
        int count;

        WorkerResult(Atlas.Type type, int width, int height, int count) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.count = count;
        }

        public String toString() {
            return String.format("%s %dx%d", this.type.toString(), Integer.valueOf(this.width), Integer.valueOf(this.height));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AssetAtlasService$ComputeWorker.class */
    public static class ComputeWorker implements Runnable {
        private final int mStart;
        private final int mEnd;
        private final int mStep;
        private final List<Bitmap> mBitmaps;
        private final List<WorkerResult> mResults;
        private final CountDownLatch mSignal;
        private final int mThreshold;

        ComputeWorker(int start, int end, int step, List<Bitmap> bitmaps, int pixelCount, List<WorkerResult> results, CountDownLatch signal) {
            this.mStart = start;
            this.mEnd = end;
            this.mStep = step;
            this.mBitmaps = bitmaps;
            this.mResults = results;
            this.mSignal = signal;
            int i = (int) (pixelCount * 0.8f);
            while (true) {
                int threshold = i;
                if (threshold > 4194304) {
                    i = threshold >> 1;
                } else {
                    this.mThreshold = threshold;
                    return;
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int count;
            Log.d(AssetAtlasService.LOG_TAG, "Running " + Thread.currentThread().getName());
            Atlas.Entry entry = new Atlas.Entry();
            Atlas.Type[] arr$ = Atlas.Type.values();
            for (Atlas.Type type : arr$) {
                int i = this.mStart;
                while (true) {
                    int width = i;
                    if (width < this.mEnd) {
                        for (int height = 768; height < 2048; height += 64) {
                            if (width * height > this.mThreshold && (count = packBitmaps(type, width, height, entry)) > 0) {
                                this.mResults.add(new WorkerResult(type, width, height, count));
                                if (count == this.mBitmaps.size()) {
                                    break;
                                }
                            }
                        }
                        i = width + this.mStep;
                    }
                }
            }
            if (this.mSignal != null) {
                this.mSignal.countDown();
            }
        }

        private int packBitmaps(Atlas.Type type, int width, int height, Atlas.Entry entry) {
            int total = 0;
            Atlas atlas = new Atlas(type, width, height);
            int count = this.mBitmaps.size();
            for (int i = 0; i < count; i++) {
                Bitmap bitmap = this.mBitmaps.get(i);
                if (atlas.pack(bitmap.getWidth(), bitmap.getHeight(), entry) != null) {
                    total++;
                }
            }
            return total;
        }
    }
}