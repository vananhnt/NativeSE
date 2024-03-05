package android.hardware.camera2.utils;

import android.hardware.camera2.utils.Decorator;
import android.os.DeadObjectException;
import android.os.RemoteException;
import java.lang.reflect.Method;

/* loaded from: CameraBinderDecorator.class */
public class CameraBinderDecorator {
    public static final int NO_ERROR = 0;
    public static final int PERMISSION_DENIED = -1;
    public static final int ALREADY_EXISTS = -17;
    public static final int BAD_VALUE = -22;
    public static final int DEAD_OBJECT = -32;
    public static final int EACCES = -13;
    public static final int EBUSY = -16;
    public static final int ENODEV = -19;
    public static final int EOPNOTSUPP = -95;
    public static final int EUSERS = -87;

    /* loaded from: CameraBinderDecorator$CameraBinderDecoratorListener.class */
    private static class CameraBinderDecoratorListener implements Decorator.DecoratorListener {
        private CameraBinderDecoratorListener() {
        }

        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public void onBeforeInvocation(Method m, Object[] args) {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public void onAfterInvocation(Method m, Object[] args, Object result) {
            if (m.getReturnType() == Integer.TYPE) {
                int returnValue = ((Integer) result).intValue();
                switch (returnValue) {
                    case CameraBinderDecorator.EOPNOTSUPP /* -95 */:
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                        break;
                    case CameraBinderDecorator.EUSERS /* -87 */:
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                        break;
                    case CameraBinderDecorator.DEAD_OBJECT /* -32 */:
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(4));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                        break;
                    case -22:
                        throw new IllegalArgumentException("Bad argument passed to camera service");
                    case -19:
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                        break;
                    case -17:
                        return;
                    case -16:
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(4));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                        break;
                    case -13:
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(4));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                        break;
                    case -1:
                        throw new SecurityException("Lacking privileges to access camera service");
                    case 0:
                        return;
                }
                if (returnValue < 0) {
                    throw new UnsupportedOperationException(String.format("Unknown error %d", Integer.valueOf(returnValue)));
                }
            }
        }

        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public boolean onCatchException(Method m, Object[] args, Throwable t) {
            if (t instanceof DeadObjectException) {
                UncheckedThrow.throwAnyException(new CameraRuntimeException(2, "Process hosting the camera service has died unexpectedly", t));
                return false;
            } else if (t instanceof RemoteException) {
                throw new UnsupportedOperationException("An unknown RemoteException was thrown which should never happen.", t);
            } else {
                return false;
            }
        }

        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public void onFinally(Method m, Object[] args) {
        }
    }

    public static <T> T newInstance(T obj) {
        return (T) Decorator.newInstance(obj, new CameraBinderDecoratorListener());
    }
}