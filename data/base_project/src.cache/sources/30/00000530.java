package android.graphics;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Log;
import android.util.TypedValue;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: BitmapFactory.class */
public class BitmapFactory {
    private static final int DECODE_BUFFER_SIZE = 16384;

    private static native Bitmap nativeDecodeStream(InputStream inputStream, byte[] bArr, Rect rect, Options options);

    private static native Bitmap nativeDecodeFileDescriptor(FileDescriptor fileDescriptor, Rect rect, Options options);

    private static native Bitmap nativeDecodeAsset(int i, Rect rect, Options options);

    private static native Bitmap nativeDecodeByteArray(byte[] bArr, int i, int i2, Options options);

    private static native boolean nativeIsSeekable(FileDescriptor fileDescriptor);

    /* loaded from: BitmapFactory$Options.class */
    public static class Options {
        public Bitmap inBitmap;
        public boolean inMutable;
        public boolean inJustDecodeBounds;
        public int inSampleSize;
        public int inDensity;
        public int inTargetDensity;
        public int inScreenDensity;
        public boolean inPurgeable;
        public boolean inInputShareable;
        public boolean inPreferQualityOverSpeed;
        public int outWidth;
        public int outHeight;
        public String outMimeType;
        public byte[] inTempStorage;
        public boolean mCancel;
        public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;
        public boolean inDither = false;
        public boolean inScaled = true;
        public boolean inPremultiplied = true;

        private native void requestCancel();

        public void requestCancelDecode() {
            this.mCancel = true;
            requestCancel();
        }
    }

    public static Bitmap decodeFile(String pathName, Options opts) {
        Bitmap bm = null;
        InputStream stream = null;
        try {
            try {
                stream = new FileInputStream(pathName);
                bm = decodeStream(stream, null, opts);
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e2) {
                Log.e("BitmapFactory", "Unable to decode stream: " + e2);
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e3) {
                    }
                }
            }
            return bm;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    public static Bitmap decodeFile(String pathName) {
        return decodeFile(pathName, null);
    }

    public static Bitmap decodeResourceStream(Resources res, TypedValue value, InputStream is, Rect pad, Options opts) {
        if (opts == null) {
            opts = new Options();
        }
        if (opts.inDensity == 0 && value != null) {
            int density = value.density;
            if (density == 0) {
                opts.inDensity = 160;
            } else if (density != 65535) {
                opts.inDensity = density;
            }
        }
        if (opts.inTargetDensity == 0 && res != null) {
            opts.inTargetDensity = res.getDisplayMetrics().densityDpi;
        }
        return decodeStream(is, pad, opts);
    }

    public static Bitmap decodeResource(Resources res, int id, Options opts) {
        Bitmap bm = null;
        InputStream is = null;
        try {
            TypedValue value = new TypedValue();
            is = res.openRawResource(id, value);
            bm = decodeResourceStream(res, value, is, null, opts);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                    throw th;
                }
            }
            throw th;
        }
        if (bm != null || opts == null || opts.inBitmap == null) {
            return bm;
        }
        throw new IllegalArgumentException("Problem decoding into existing bitmap");
    }

    public static Bitmap decodeResource(Resources res, int id) {
        return decodeResource(res, id, null);
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
        if ((offset | length) < 0 || data.length < offset + length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Trace.traceBegin(2L, "decodeBitmap");
        try {
            Bitmap bm = nativeDecodeByteArray(data, offset, length, opts);
            if (bm == null && opts != null && opts.inBitmap != null) {
                throw new IllegalArgumentException("Problem decoding into existing bitmap");
            }
            setDensityFromOptions(bm, opts);
            Trace.traceEnd(2L);
            return bm;
        } catch (Throwable th) {
            Trace.traceEnd(2L);
            throw th;
        }
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        return decodeByteArray(data, offset, length, null);
    }

    private static void setDensityFromOptions(Bitmap outputBitmap, Options opts) {
        if (outputBitmap == null || opts == null) {
            return;
        }
        int density = opts.inDensity;
        if (density == 0) {
            if (opts.inBitmap != null) {
                outputBitmap.setDensity(Bitmap.getDefaultDensity());
                return;
            }
            return;
        }
        outputBitmap.setDensity(density);
        int targetDensity = opts.inTargetDensity;
        if (targetDensity == 0 || density == targetDensity || density == opts.inScreenDensity) {
            return;
        }
        byte[] np = outputBitmap.getNinePatchChunk();
        boolean isNinePatch = np != null && NinePatch.isNinePatchChunk(np);
        if (opts.inScaled || isNinePatch) {
            outputBitmap.setDensity(targetDensity);
        }
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, Options opts) {
        Bitmap bm;
        if (is == null) {
            return null;
        }
        Trace.traceBegin(2L, "decodeBitmap");
        try {
            if (is instanceof AssetManager.AssetInputStream) {
                int asset = ((AssetManager.AssetInputStream) is).getAssetInt();
                bm = nativeDecodeAsset(asset, outPadding, opts);
            } else {
                bm = decodeStreamInternal(is, outPadding, opts);
            }
            if (bm == null && opts != null && opts.inBitmap != null) {
                throw new IllegalArgumentException("Problem decoding into existing bitmap");
            }
            setDensityFromOptions(bm, opts);
            Trace.traceEnd(2L);
            return bm;
        } catch (Throwable th) {
            Trace.traceEnd(2L);
            throw th;
        }
    }

    private static Bitmap decodeStreamInternal(InputStream is, Rect outPadding, Options opts) {
        byte[] tempStorage = null;
        if (opts != null) {
            tempStorage = opts.inTempStorage;
        }
        if (tempStorage == null) {
            tempStorage = new byte[16384];
        }
        return nativeDecodeStream(is, tempStorage, outPadding, opts);
    }

    public static Bitmap decodeStream(InputStream is) {
        return decodeStream(is, null, null);
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding, Options opts) {
        Bitmap bm;
        Trace.traceBegin(2L, "decodeFileDescriptor");
        try {
            if (nativeIsSeekable(fd)) {
                bm = nativeDecodeFileDescriptor(fd, outPadding, opts);
            } else {
                FileInputStream fis = new FileInputStream(fd);
                bm = decodeStreamInternal(fis, outPadding, opts);
                try {
                    fis.close();
                } catch (Throwable th) {
                }
            }
            if (bm == null && opts != null && opts.inBitmap != null) {
                throw new IllegalArgumentException("Problem decoding into existing bitmap");
            }
            setDensityFromOptions(bm, opts);
            Trace.traceEnd(2L);
            return bm;
        } catch (Throwable th2) {
            Trace.traceEnd(2L);
            throw th2;
        }
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd) {
        return decodeFileDescriptor(fd, null, null);
    }
}