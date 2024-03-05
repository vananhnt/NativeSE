package android.hardware.camera2.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: Decorator.class */
public class Decorator<T> implements InvocationHandler {
    private final T mObject;
    private final DecoratorListener mListener;

    /* loaded from: Decorator$DecoratorListener.class */
    public interface DecoratorListener {
        void onBeforeInvocation(Method method, Object[] objArr);

        void onAfterInvocation(Method method, Object[] objArr, Object obj);

        boolean onCatchException(Method method, Object[] objArr, Throwable th);

        void onFinally(Method method, Object[] objArr);
    }

    public static <T> T newInstance(T obj, DecoratorListener listener) {
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new Decorator(obj, listener));
    }

    private Decorator(T obj, DecoratorListener listener) {
        this.mObject = obj;
        this.mListener = listener;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result = null;
        try {
            try {
                this.mListener.onBeforeInvocation(m, args);
                result = m.invoke(this.mObject, args);
                this.mListener.onAfterInvocation(m, args, result);
                this.mListener.onFinally(m, args);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (!this.mListener.onCatchException(m, args, t)) {
                    throw t;
                }
                this.mListener.onFinally(m, args);
            }
            return result;
        } catch (Throwable th) {
            this.mListener.onFinally(m, args);
            throw th;
        }
    }
}