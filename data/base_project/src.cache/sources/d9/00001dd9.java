package com.android.server.am;

import android.graphics.Bitmap;

/* loaded from: ThumbnailHolder.class */
public class ThumbnailHolder {
    Bitmap lastThumbnail;
    CharSequence lastDescription;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disposeThumbnail() {
        this.lastThumbnail = null;
        this.lastDescription = null;
    }
}