package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RejectedExecutionHandler.class */
public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor);
}