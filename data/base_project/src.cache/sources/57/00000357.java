package android.content;

/* loaded from: UndoOwner.class */
public class UndoOwner {
    final String mTag;
    UndoManager mManager;
    Object mData;
    int mOpCount;
    int mStateSeq;
    int mSavedIdx;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UndoOwner(String tag) {
        this.mTag = tag;
    }

    public String getTag() {
        return this.mTag;
    }

    public Object getData() {
        return this.mData;
    }
}