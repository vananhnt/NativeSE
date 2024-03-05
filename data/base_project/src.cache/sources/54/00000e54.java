package android.speech.tts;

import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/* loaded from: FileSynthesisCallback.class */
class FileSynthesisCallback extends AbstractSynthesisCallback {
    private static final String TAG = "FileSynthesisRequest";
    private static final boolean DBG = false;
    private static final int MAX_AUDIO_BUFFER_SIZE = 8192;
    private static final int WAV_HEADER_LENGTH = 44;
    private static final short WAV_FORMAT_PCM = 1;
    private int mSampleRateInHz;
    private int mAudioFormat;
    private int mChannelCount;
    private FileChannel mFileChannel;
    private final Object mStateLock = new Object();
    private boolean mStarted = false;
    private boolean mStopped = false;
    private boolean mDone = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileSynthesisCallback(FileChannel fileChannel) {
        this.mFileChannel = fileChannel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.AbstractSynthesisCallback
    public void stop() {
        synchronized (this.mStateLock) {
            this.mStopped = true;
            cleanUp();
        }
    }

    private void cleanUp() {
        closeFile();
    }

    private void closeFile() {
        try {
            if (this.mFileChannel != null) {
                this.mFileChannel.close();
                this.mFileChannel = null;
            }
        } catch (IOException ex) {
            Log.e(TAG, "Failed to close output file descriptor", ex);
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int getMaxBufferSize() {
        return 8192;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.AbstractSynthesisCallback
    public boolean isDone() {
        return this.mDone;
    }

    @Override // android.speech.tts.SynthesisCallback
    public int start(int sampleRateInHz, int audioFormat, int channelCount) {
        synchronized (this.mStateLock) {
            if (this.mStopped) {
                return -1;
            }
            if (this.mStarted) {
                cleanUp();
                throw new IllegalArgumentException("FileSynthesisRequest.start() called twice");
            }
            this.mStarted = true;
            this.mSampleRateInHz = sampleRateInHz;
            this.mAudioFormat = audioFormat;
            this.mChannelCount = channelCount;
            try {
                this.mFileChannel.write(ByteBuffer.allocate(44));
                return 0;
            } catch (IOException ex) {
                Log.e(TAG, "Failed to write wav header to output file descriptor" + ex);
                cleanUp();
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int audioAvailable(byte[] buffer, int offset, int length) {
        synchronized (this.mStateLock) {
            if (this.mStopped) {
                return -1;
            }
            if (this.mFileChannel == null) {
                Log.e(TAG, "File not open");
                return -1;
            }
            try {
                this.mFileChannel.write(ByteBuffer.wrap(buffer, offset, length));
                return 0;
            } catch (IOException ex) {
                Log.e(TAG, "Failed to write to output file descriptor", ex);
                cleanUp();
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int done() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                return -1;
            }
            if (this.mStopped) {
                return -1;
            }
            if (this.mFileChannel == null) {
                Log.e(TAG, "File not open");
                return -1;
            }
            try {
                this.mFileChannel.position(0L);
                int dataLength = (int) (this.mFileChannel.size() - 44);
                this.mFileChannel.write(makeWavHeader(this.mSampleRateInHz, this.mAudioFormat, this.mChannelCount, dataLength));
                closeFile();
                this.mDone = true;
                return 0;
            } catch (IOException ex) {
                Log.e(TAG, "Failed to write to output file descriptor", ex);
                cleanUp();
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error() {
        synchronized (this.mStateLock) {
            cleanUp();
        }
    }

    private ByteBuffer makeWavHeader(int sampleRateInHz, int audioFormat, int channelCount, int dataLength) {
        int sampleSizeInBytes = audioFormat == 3 ? 1 : 2;
        int byteRate = sampleRateInHz * sampleSizeInBytes * channelCount;
        short blockAlign = (short) (sampleSizeInBytes * channelCount);
        short bitsPerSample = (short) (sampleSizeInBytes * 8);
        byte[] headerBuf = new byte[44];
        ByteBuffer header = ByteBuffer.wrap(headerBuf);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.put(new byte[]{82, 73, 70, 70});
        header.putInt((dataLength + 44) - 8);
        header.put(new byte[]{87, 65, 86, 69});
        header.put(new byte[]{102, 109, 116, 32});
        header.putInt(16);
        header.putShort((short) 1);
        header.putShort((short) channelCount);
        header.putInt(sampleRateInHz);
        header.putInt(byteRate);
        header.putShort(blockAlign);
        header.putShort(bitsPerSample);
        header.put(new byte[]{100, 97, 116, 97});
        header.putInt(dataLength);
        header.flip();
        return header;
    }
}