package android.webkit;

import android.graphics.Bitmap;

/* loaded from: WebHistoryItem.class */
public class WebHistoryItem implements Cloneable {
    @Deprecated
    public int getId() {
        throw new MustOverrideException();
    }

    public String getUrl() {
        throw new MustOverrideException();
    }

    public String getOriginalUrl() {
        throw new MustOverrideException();
    }

    public String getTitle() {
        throw new MustOverrideException();
    }

    public Bitmap getFavicon() {
        throw new MustOverrideException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* renamed from: clone */
    public synchronized WebHistoryItem mo997clone() {
        throw new MustOverrideException();
    }
}