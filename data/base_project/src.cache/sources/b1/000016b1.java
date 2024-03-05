package android.webkit;

import java.io.Serializable;

/* loaded from: WebBackForwardList.class */
public class WebBackForwardList implements Cloneable, Serializable {
    public synchronized WebHistoryItem getCurrentItem() {
        throw new MustOverrideException();
    }

    public synchronized int getCurrentIndex() {
        throw new MustOverrideException();
    }

    public synchronized WebHistoryItem getItemAtIndex(int index) {
        throw new MustOverrideException();
    }

    public synchronized int getSize() {
        throw new MustOverrideException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* renamed from: clone */
    public synchronized WebBackForwardList mo996clone() {
        throw new MustOverrideException();
    }
}