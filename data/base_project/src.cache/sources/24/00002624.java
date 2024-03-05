package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Timer.class */
public class Timer {
    public Timer(String name, boolean isDaemon) {
        throw new RuntimeException("Stub!");
    }

    public Timer(String name) {
        throw new RuntimeException("Stub!");
    }

    public Timer(boolean isDaemon) {
        throw new RuntimeException("Stub!");
    }

    public Timer() {
        throw new RuntimeException("Stub!");
    }

    public void cancel() {
        throw new RuntimeException("Stub!");
    }

    public int purge() {
        throw new RuntimeException("Stub!");
    }

    public void schedule(TimerTask task, Date when) {
        throw new RuntimeException("Stub!");
    }

    public void schedule(TimerTask task, long delay) {
        throw new RuntimeException("Stub!");
    }

    public void schedule(TimerTask task, long delay, long period) {
        throw new RuntimeException("Stub!");
    }

    public void schedule(TimerTask task, Date when, long period) {
        throw new RuntimeException("Stub!");
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        throw new RuntimeException("Stub!");
    }

    public void scheduleAtFixedRate(TimerTask task, Date when, long period) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Timer$TimerImpl.class */
    private static final class TimerImpl extends Thread {
        private boolean cancelled;
        private boolean finished;
        private TimerHeap tasks = new TimerHeap();

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.Timer.TimerImpl.run():void, file: Timer$TimerImpl.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.Timer.TimerImpl.run():void, file: Timer$TimerImpl.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Timer.TimerImpl.run():void");
        }

        static /* synthetic */ boolean access$302(TimerImpl x0, boolean x1) {
            x0.finished = x1;
            return x1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: Timer$TimerImpl$TimerHeap.class */
        public static final class TimerHeap {
            private int DEFAULT_HEAP_SIZE;
            private TimerTask[] timers;
            private int size;
            private int deletedCancelledNumber;

            private TimerHeap() {
                this.DEFAULT_HEAP_SIZE = 256;
                this.timers = new TimerTask[this.DEFAULT_HEAP_SIZE];
                this.size = 0;
                this.deletedCancelledNumber = 0;
            }

            static /* synthetic */ int access$100(TimerHeap x0, TimerTask x1) {
                return x0.getTask(x1);
            }

            public TimerTask minimum() {
                return this.timers[0];
            }

            public boolean isEmpty() {
                return this.size == 0;
            }

            public void insert(TimerTask task) {
                if (this.timers.length == this.size) {
                    TimerTask[] appendedTimers = new TimerTask[this.size * 2];
                    System.arraycopy(this.timers, 0, appendedTimers, 0, this.size);
                    this.timers = appendedTimers;
                }
                TimerTask[] timerTaskArr = this.timers;
                int i = this.size;
                this.size = i + 1;
                timerTaskArr[i] = task;
                upHeap();
            }

            public void delete(int pos) {
                if (pos >= 0 && pos < this.size) {
                    TimerTask[] timerTaskArr = this.timers;
                    TimerTask[] timerTaskArr2 = this.timers;
                    int i = this.size - 1;
                    this.size = i;
                    timerTaskArr[pos] = timerTaskArr2[i];
                    this.timers[this.size] = null;
                    downHeap(pos);
                }
            }

            private void upHeap() {
                int i = this.size - 1;
                while (true) {
                    int current = i;
                    int parent = (current - 1) / 2;
                    if (this.timers[current].when < this.timers[parent].when) {
                        TimerTask tmp = this.timers[current];
                        this.timers[current] = this.timers[parent];
                        this.timers[parent] = tmp;
                        i = parent;
                    } else {
                        return;
                    }
                }
            }

            private void downHeap(int pos) {
                int i = pos;
                while (true) {
                    int current = i;
                    int child = (2 * current) + 1;
                    if (child < this.size && this.size > 0) {
                        if (child + 1 < this.size && this.timers[child + 1].when < this.timers[child].when) {
                            child++;
                        }
                        if (this.timers[current].when >= this.timers[child].when) {
                            TimerTask tmp = this.timers[current];
                            this.timers[current] = this.timers[child];
                            this.timers[child] = tmp;
                            i = child;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }

            public void reset() {
                this.timers = new TimerTask[this.DEFAULT_HEAP_SIZE];
                this.size = 0;
            }

            public void adjustMinimum() {
                downHeap(0);
            }

            public void deleteIfCancelled() {
                int i = 0;
                while (i < this.size) {
                    if (this.timers[i].cancelled) {
                        this.deletedCancelledNumber++;
                        delete(i);
                        i--;
                    }
                    i++;
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public int getTask(TimerTask task) {
                for (int i = 0; i < this.timers.length; i++) {
                    if (this.timers[i] == task) {
                        return i;
                    }
                }
                return -1;
            }
        }

        TimerImpl(String name, boolean isDaemon) {
            setName(name);
            setDaemon(isDaemon);
            start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void insertTask(TimerTask newTask) {
            this.tasks.insert(newTask);
            notify();
        }

        public synchronized void cancel() {
            this.cancelled = true;
            this.tasks.reset();
            notify();
        }

        public int purge() {
            if (!this.tasks.isEmpty()) {
                this.tasks.deletedCancelledNumber = 0;
                this.tasks.deleteIfCancelled();
                return this.tasks.deletedCancelledNumber;
            }
            return 0;
        }
    }

    /* loaded from: Timer$FinalizerHelper.class */
    private static final class FinalizerHelper {
        private final TimerImpl impl;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.Timer.FinalizerHelper.finalize():void, file: Timer$FinalizerHelper.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        protected void finalize() throws java.lang.Throwable {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.Timer.FinalizerHelper.finalize():void, file: Timer$FinalizerHelper.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Timer.FinalizerHelper.finalize():void");
        }

        FinalizerHelper(TimerImpl impl) {
            this.impl = impl;
        }
    }
}