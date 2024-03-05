package android.media.videoeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/* loaded from: WaveformData.class */
public class WaveformData {
    private final int mFrameDurationMs;
    private final int mFramesCount;
    private final short[] mGains;

    private WaveformData() throws IOException {
        this.mFrameDurationMs = 0;
        this.mFramesCount = 0;
        this.mGains = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WaveformData(String audioWaveformFilename) throws IOException {
        if (audioWaveformFilename == null) {
            throw new IllegalArgumentException("WaveformData : filename is null");
        }
        FileInputStream audioGraphFileReadHandle = null;
        try {
            File audioGraphFileContext = new File(audioWaveformFilename);
            audioGraphFileReadHandle = new FileInputStream(audioGraphFileContext);
            byte[] tempFrameDuration = new byte[4];
            audioGraphFileReadHandle.read(tempFrameDuration, 0, 4);
            int tempFrameDurationMs = 0;
            int tempFramesCounter = 0;
            for (int i = 0; i < 4; i++) {
                tempFrameDurationMs = (tempFrameDurationMs << 8) | (tempFrameDuration[i] & 255);
            }
            this.mFrameDurationMs = tempFrameDurationMs;
            byte[] tempFramesCount = new byte[4];
            audioGraphFileReadHandle.read(tempFramesCount, 0, 4);
            for (int i2 = 0; i2 < 4; i2++) {
                tempFramesCounter = (tempFramesCounter << 8) | (tempFramesCount[i2] & 255);
            }
            this.mFramesCount = tempFramesCounter;
            this.mGains = new short[this.mFramesCount];
            for (int i3 = 0; i3 < this.mFramesCount; i3++) {
                this.mGains[i3] = (short) audioGraphFileReadHandle.read();
            }
            if (audioGraphFileReadHandle != null) {
                audioGraphFileReadHandle.close();
            }
        } catch (Throwable th) {
            if (audioGraphFileReadHandle != null) {
                audioGraphFileReadHandle.close();
            }
            throw th;
        }
    }

    public int getFrameDuration() {
        return this.mFrameDurationMs;
    }

    public int getFramesCount() {
        return this.mFramesCount;
    }

    public short[] getFrameGains() {
        return this.mGains;
    }
}