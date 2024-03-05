package android.webkit;

/* loaded from: MustOverrideException.class */
class MustOverrideException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MustOverrideException() {
        super("abstract function called: must be overriden!");
    }
}