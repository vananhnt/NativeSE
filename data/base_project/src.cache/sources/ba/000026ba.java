package java.util.concurrent;

import java.util.concurrent.ForkJoinPool;

/* loaded from: ForkJoinWorkerThread.class */
public class ForkJoinWorkerThread extends Thread {
    final ForkJoinPool pool;
    final ForkJoinPool.WorkQueue workQueue;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinWorkerThread.run():void, file: ForkJoinWorkerThread.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinWorkerThread.run():void, file: ForkJoinWorkerThread.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinWorkerThread.run():void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ForkJoinWorkerThread(ForkJoinPool pool) {
        super("aForkJoinWorkerThread");
        this.pool = pool;
        this.workQueue = pool.registerWorker(this);
    }

    public ForkJoinPool getPool() {
        return this.pool;
    }

    public int getPoolIndex() {
        return this.workQueue.poolIndex;
    }

    protected void onStart() {
    }

    protected void onTermination(Throwable exception) {
    }
}