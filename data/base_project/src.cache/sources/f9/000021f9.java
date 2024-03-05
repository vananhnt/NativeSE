package gov.nist.javax.sip.parser;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.stack.SIPStackTimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: Pipeline.class */
public class Pipeline extends InputStream {
    private LinkedList buffList = new LinkedList();
    private Buffer currentBuffer;
    private boolean isClosed;
    private Timer timer;
    private InputStream pipe;
    private int readTimeout;
    private TimerTask myTimerTask;

    /* loaded from: Pipeline$MyTimer.class */
    class MyTimer extends SIPStackTimerTask {
        Pipeline pipeline;
        private boolean isCancelled;

        protected MyTimer(Pipeline pipeline) {
            this.pipeline = pipeline;
        }

        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            if (this.isCancelled) {
                return;
            }
            try {
                this.pipeline.close();
            } catch (IOException ex) {
                InternalErrorHandler.handleException(ex);
            }
        }

        @Override // java.util.TimerTask
        public boolean cancel() {
            boolean retval = super.cancel();
            this.isCancelled = true;
            return retval;
        }
    }

    /* loaded from: Pipeline$Buffer.class */
    class Buffer {
        byte[] bytes;
        int length;
        int ptr = 0;

        public Buffer(byte[] bytes, int length) {
            this.length = length;
            this.bytes = bytes;
        }

        public int getNextByte() {
            byte[] bArr = this.bytes;
            int i = this.ptr;
            this.ptr = i + 1;
            int retval = bArr[i] & 255;
            return retval;
        }
    }

    public void startTimer() {
        if (this.readTimeout == -1) {
            return;
        }
        this.myTimerTask = new MyTimer(this);
        this.timer.schedule(this.myTimerTask, this.readTimeout);
    }

    public void stopTimer() {
        if (this.readTimeout != -1 && this.myTimerTask != null) {
            this.myTimerTask.cancel();
        }
    }

    public Pipeline(InputStream pipe, int readTimeout, Timer timer) {
        this.timer = timer;
        this.pipe = pipe;
        this.readTimeout = readTimeout;
    }

    public void write(byte[] bytes, int start, int length) throws IOException {
        if (this.isClosed) {
            throw new IOException("Closed!!");
        }
        Buffer buff = new Buffer(bytes, length);
        buff.ptr = start;
        synchronized (this.buffList) {
            this.buffList.add(buff);
            this.buffList.notifyAll();
        }
    }

    public void write(byte[] bytes) throws IOException {
        if (this.isClosed) {
            throw new IOException("Closed!!");
        }
        Buffer buff = new Buffer(bytes, bytes.length);
        synchronized (this.buffList) {
            this.buffList.add(buff);
            this.buffList.notifyAll();
        }
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        this.isClosed = true;
        synchronized (this.buffList) {
            this.buffList.notifyAll();
        }
        this.pipe.close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        synchronized (this.buffList) {
            if (this.currentBuffer != null && this.currentBuffer.ptr < this.currentBuffer.length) {
                int retval = this.currentBuffer.getNextByte();
                if (this.currentBuffer.ptr == this.currentBuffer.length) {
                    this.currentBuffer = null;
                }
                return retval;
            } else if (this.isClosed && this.buffList.isEmpty()) {
                return -1;
            } else {
                do {
                    try {
                        try {
                            if (this.buffList.isEmpty()) {
                                this.buffList.wait();
                            } else {
                                this.currentBuffer = (Buffer) this.buffList.removeFirst();
                                int retval2 = this.currentBuffer.getNextByte();
                                if (this.currentBuffer.ptr == this.currentBuffer.length) {
                                    this.currentBuffer = null;
                                }
                                return retval2;
                            }
                        } catch (InterruptedException ex) {
                            throw new IOException(ex.getMessage());
                        }
                    } catch (NoSuchElementException ex2) {
                        ex2.printStackTrace();
                        throw new IOException(ex2.getMessage());
                    }
                } while (!this.isClosed);
                return -1;
            }
        }
    }
}