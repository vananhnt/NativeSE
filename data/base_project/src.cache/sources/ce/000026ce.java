package java.util.concurrent;

/* loaded from: RecursiveAction.class */
public abstract class RecursiveAction extends ForkJoinTask<Void> {
    private static final long serialVersionUID = 5232453952276485070L;

    protected abstract void compute();

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.ForkJoinTask
    public final Void getRawResult() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.util.concurrent.ForkJoinTask
    public final void setRawResult(Void mustBeNull) {
    }

    @Override // java.util.concurrent.ForkJoinTask
    protected final boolean exec() {
        compute();
        return true;
    }
}