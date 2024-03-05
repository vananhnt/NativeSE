package android.os.storage;

/* loaded from: StorageEventListener.class */
public abstract class StorageEventListener {
    public void onUsbMassStorageConnectionChanged(boolean connected) {
    }

    public void onStorageStateChanged(String path, String oldState, String newState) {
    }
}