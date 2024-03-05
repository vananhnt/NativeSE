package android.util;

import android.text.format.Time;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: LocalLog.class */
public final class LocalLog {
    private int mMaxLines;
    private LinkedList<String> mLog = new LinkedList<>();
    private Time mNow = new Time();

    public LocalLog(int maxLines) {
        this.mMaxLines = maxLines;
    }

    public synchronized void log(String msg) {
        if (this.mMaxLines > 0) {
            this.mNow.setToNow();
            this.mLog.add(this.mNow.format("%H:%M:%S") + " - " + msg);
            while (this.mLog.size() > this.mMaxLines) {
                this.mLog.remove();
            }
        }
    }

    public synchronized void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        Iterator<String> itr = this.mLog.listIterator(0);
        while (itr.hasNext()) {
            pw.println(itr.next());
        }
    }
}