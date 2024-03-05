package android.support.v7.internal.view.menu;

/* loaded from: BaseWrapper.class */
class BaseWrapper<T> {
    final T mWrappedObject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseWrapper(T t) {
        if (t == null) {
            throw new IllegalArgumentException("Wrapped Object can not be null.");
        }
        this.mWrappedObject = t;
    }

    public T getWrappedObject() {
        return this.mWrappedObject;
    }
}