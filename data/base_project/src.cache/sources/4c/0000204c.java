package com.android.webview.chromium;

import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import com.android.org.chromium.content.browser.NavigationHistory;
import java.util.ArrayList;
import java.util.List;

/* loaded from: WebBackForwardListChromium.class */
public class WebBackForwardListChromium extends WebBackForwardList {
    private final List<WebHistoryItemChromium> mHistroryItemList;
    private final int mCurrentIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebBackForwardListChromium(NavigationHistory nav_history) {
        this.mCurrentIndex = nav_history.getCurrentEntryIndex();
        this.mHistroryItemList = new ArrayList(nav_history.getEntryCount());
        for (int i = 0; i < nav_history.getEntryCount(); i++) {
            this.mHistroryItemList.add(new WebHistoryItemChromium(nav_history.getEntryAtIndex(i)));
        }
    }

    @Override // android.webkit.WebBackForwardList
    public synchronized WebHistoryItem getCurrentItem() {
        if (getSize() == 0) {
            return null;
        }
        return getItemAtIndex(getCurrentIndex());
    }

    @Override // android.webkit.WebBackForwardList
    public synchronized int getCurrentIndex() {
        return this.mCurrentIndex;
    }

    @Override // android.webkit.WebBackForwardList
    public synchronized WebHistoryItem getItemAtIndex(int index) {
        if (index < 0 || index >= getSize()) {
            return null;
        }
        return this.mHistroryItemList.get(index);
    }

    @Override // android.webkit.WebBackForwardList
    public synchronized int getSize() {
        return this.mHistroryItemList.size();
    }

    private WebBackForwardListChromium(List<WebHistoryItemChromium> list, int currentIndex) {
        this.mHistroryItemList = list;
        this.mCurrentIndex = currentIndex;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.webkit.WebBackForwardList
    /* renamed from: clone */
    public synchronized WebBackForwardListChromium mo996clone() {
        List<WebHistoryItemChromium> list = new ArrayList<>(getSize());
        for (int i = 0; i < getSize(); i++) {
            list.add(this.mHistroryItemList.get(i).mo997clone());
        }
        return new WebBackForwardListChromium(list, this.mCurrentIndex);
    }
}