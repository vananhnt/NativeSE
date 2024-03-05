package com.android.internal.util;

import java.io.File;
import java.io.IOException;

@Deprecated
/* loaded from: JournaledFile.class */
public class JournaledFile {
    File mReal;
    File mTemp;
    boolean mWriting;

    public JournaledFile(File real, File temp) {
        this.mReal = real;
        this.mTemp = temp;
    }

    public File chooseForRead() {
        File result;
        if (this.mReal.exists()) {
            result = this.mReal;
            if (this.mTemp.exists()) {
                this.mTemp.delete();
            }
        } else if (this.mTemp.exists()) {
            result = this.mTemp;
            this.mTemp.renameTo(this.mReal);
        } else {
            return this.mReal;
        }
        return result;
    }

    public File chooseForWrite() {
        if (this.mWriting) {
            throw new IllegalStateException("uncommitted write already in progress");
        }
        if (!this.mReal.exists()) {
            try {
                this.mReal.createNewFile();
            } catch (IOException e) {
            }
        }
        if (this.mTemp.exists()) {
            this.mTemp.delete();
        }
        this.mWriting = true;
        return this.mTemp;
    }

    public void commit() {
        if (!this.mWriting) {
            throw new IllegalStateException("no file to commit");
        }
        this.mWriting = false;
        this.mTemp.renameTo(this.mReal);
    }

    public void rollback() {
        if (!this.mWriting) {
            throw new IllegalStateException("no file to roll back");
        }
        this.mWriting = false;
        this.mTemp.delete();
    }
}