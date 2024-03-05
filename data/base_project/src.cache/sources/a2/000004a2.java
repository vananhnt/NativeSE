package android.filterfw.core;

import android.filterfw.core.GraphRunner;
import android.os.ConditionVariable;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: SyncRunner.class */
public class SyncRunner extends GraphRunner {
    private Scheduler mScheduler;
    private GraphRunner.OnRunnerDoneListener mDoneListener;
    private ScheduledThreadPoolExecutor mWakeExecutor;
    private ConditionVariable mWakeCondition;
    private StopWatchMap mTimer;
    private final boolean mLogVerbose;
    private static final String TAG = "SyncRunner";

    public SyncRunner(FilterContext context, FilterGraph graph, Class schedulerClass) {
        super(context);
        this.mScheduler = null;
        this.mDoneListener = null;
        this.mWakeExecutor = new ScheduledThreadPoolExecutor(1);
        this.mWakeCondition = new ConditionVariable();
        this.mTimer = null;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
        if (this.mLogVerbose) {
            Log.v(TAG, "Initializing SyncRunner");
        }
        if (Scheduler.class.isAssignableFrom(schedulerClass)) {
            try {
                Constructor schedulerConstructor = schedulerClass.getConstructor(FilterGraph.class);
                this.mScheduler = (Scheduler) schedulerConstructor.newInstance(graph);
                this.mFilterContext = context;
                this.mFilterContext.addGraph(graph);
                this.mTimer = new StopWatchMap();
                if (this.mLogVerbose) {
                    Log.v(TAG, "Setting up filters");
                }
                graph.setupFilters();
                return;
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access Scheduler constructor!", e);
            } catch (InstantiationException e2) {
                throw new RuntimeException("Could not instantiate the Scheduler instance!", e2);
            } catch (NoSuchMethodException e3) {
                throw new RuntimeException("Scheduler does not have constructor <init>(FilterGraph)!", e3);
            } catch (InvocationTargetException e4) {
                throw new RuntimeException("Scheduler constructor threw an exception", e4);
            } catch (Exception e5) {
                throw new RuntimeException("Could not instantiate Scheduler", e5);
            }
        }
        throw new IllegalArgumentException("Class provided is not a Scheduler subclass!");
    }

    @Override // android.filterfw.core.GraphRunner
    public FilterGraph getGraph() {
        if (this.mScheduler != null) {
            return this.mScheduler.getGraph();
        }
        return null;
    }

    public int step() {
        assertReadyToStep();
        if (!getGraph().isReady()) {
            throw new RuntimeException("Trying to process graph that is not open!");
        }
        if (performStep()) {
            return 1;
        }
        return determinePostRunState();
    }

    public void beginProcessing() {
        this.mScheduler.reset();
        getGraph().beginProcessing();
    }

    @Override // android.filterfw.core.GraphRunner
    public void close() {
        if (this.mLogVerbose) {
            Log.v(TAG, "Closing graph.");
        }
        getGraph().closeFilters(this.mFilterContext);
        this.mScheduler.reset();
    }

    /* JADX WARN: Incorrect condition in loop: B:7:0x001f */
    @Override // android.filterfw.core.GraphRunner
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void run() {
        /*
            r3 = this;
            r0 = r3
            boolean r0 = r0.mLogVerbose
            if (r0 == 0) goto Lf
            java.lang.String r0 = "SyncRunner"
            java.lang.String r1 = "Beginning run."
            int r0 = android.util.Log.v(r0, r1)
        Lf:
            r0 = r3
            r0.assertReadyToStep()
            r0 = r3
            r0.beginProcessing()
            r0 = r3
            boolean r0 = r0.activateGlContext()
            r4 = r0
            r0 = 1
            r5 = r0
        L1e:
            r0 = r5
            if (r0 == 0) goto L2a
            r0 = r3
            boolean r0 = r0.performStep()
            r5 = r0
            goto L1e
        L2a:
            r0 = r4
            if (r0 == 0) goto L32
            r0 = r3
            r0.deactivateGlContext()
        L32:
            r0 = r3
            android.filterfw.core.GraphRunner$OnRunnerDoneListener r0 = r0.mDoneListener
            if (r0 == 0) goto L55
            r0 = r3
            boolean r0 = r0.mLogVerbose
            if (r0 == 0) goto L48
            java.lang.String r0 = "SyncRunner"
            java.lang.String r1 = "Calling completion listener."
            int r0 = android.util.Log.v(r0, r1)
        L48:
            r0 = r3
            android.filterfw.core.GraphRunner$OnRunnerDoneListener r0 = r0.mDoneListener
            r1 = r3
            int r1 = r1.determinePostRunState()
            r0.onRunnerDone(r1)
        L55:
            r0 = r3
            boolean r0 = r0.mLogVerbose
            if (r0 == 0) goto L64
            java.lang.String r0 = "SyncRunner"
            java.lang.String r1 = "Run complete"
            int r0 = android.util.Log.v(r0, r1)
        L64:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.filterfw.core.SyncRunner.run():void");
    }

    @Override // android.filterfw.core.GraphRunner
    public boolean isRunning() {
        return false;
    }

    @Override // android.filterfw.core.GraphRunner
    public void setDoneCallback(GraphRunner.OnRunnerDoneListener listener) {
        this.mDoneListener = listener;
    }

    @Override // android.filterfw.core.GraphRunner
    public void stop() {
        throw new RuntimeException("SyncRunner does not support stopping a graph!");
    }

    @Override // android.filterfw.core.GraphRunner
    public synchronized Exception getError() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void waitUntilWake() {
        this.mWakeCondition.block();
    }

    protected void processFilterNode(Filter filter) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Processing filter node");
        }
        filter.performProcess(this.mFilterContext);
        if (filter.getStatus() == 6) {
            throw new RuntimeException("There was an error executing " + filter + "!");
        }
        if (filter.getStatus() == 4) {
            if (this.mLogVerbose) {
                Log.v(TAG, "Scheduling filter wakeup");
            }
            scheduleFilterWake(filter, filter.getSleepDelay());
        }
    }

    protected void scheduleFilterWake(final Filter filter, int delay) {
        this.mWakeCondition.close();
        final ConditionVariable conditionToWake = this.mWakeCondition;
        this.mWakeExecutor.schedule(new Runnable() { // from class: android.filterfw.core.SyncRunner.1
            @Override // java.lang.Runnable
            public void run() {
                filter.unsetStatus(4);
                conditionToWake.open();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int determinePostRunState() {
        for (Filter filter : this.mScheduler.getGraph().getFilters()) {
            if (filter.isOpen()) {
                if (filter.getStatus() == 4) {
                    return 3;
                }
                return 4;
            }
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean performStep() {
        if (this.mLogVerbose) {
            Log.v(TAG, "Performing one step.");
        }
        Filter filter = this.mScheduler.scheduleNextNode();
        if (filter != null) {
            this.mTimer.start(filter.getName());
            processFilterNode(filter);
            this.mTimer.stop(filter.getName());
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void assertReadyToStep() {
        if (this.mScheduler == null) {
            throw new RuntimeException("Attempting to run schedule with no scheduler in place!");
        }
        if (getGraph() == null) {
            throw new RuntimeException("Calling step on scheduler with no graph in place!");
        }
    }
}