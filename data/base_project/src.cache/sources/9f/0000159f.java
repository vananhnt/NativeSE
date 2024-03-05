package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import gov.nist.core.Separators;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: ViewDebug.class */
public class ViewDebug {
    @Deprecated
    public static final boolean TRACE_HIERARCHY = false;
    @Deprecated
    public static final boolean TRACE_RECYCLER = false;
    public static final boolean DEBUG_DRAG = false;
    private static HashMap<Class<?>, Method[]> mCapturedViewMethodsForClasses = null;
    private static HashMap<Class<?>, Field[]> mCapturedViewFieldsForClasses = null;
    private static final int CAPTURE_TIMEOUT = 4000;
    private static final String REMOTE_COMMAND_CAPTURE = "CAPTURE";
    private static final String REMOTE_COMMAND_DUMP = "DUMP";
    private static final String REMOTE_COMMAND_INVALIDATE = "INVALIDATE";
    private static final String REMOTE_COMMAND_REQUEST_LAYOUT = "REQUEST_LAYOUT";
    private static final String REMOTE_PROFILE = "PROFILE";
    private static final String REMOTE_COMMAND_CAPTURE_LAYERS = "CAPTURE_LAYERS";
    private static final String REMOTE_COMMAND_OUTPUT_DISPLAYLIST = "OUTPUT_DISPLAYLIST";
    private static HashMap<Class<?>, Field[]> sFieldsForClasses;
    private static HashMap<Class<?>, Method[]> sMethodsForClasses;
    private static HashMap<AccessibleObject, ExportedProperty> sAnnotations;

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: ViewDebug$CapturedViewProperty.class */
    public @interface CapturedViewProperty {
        boolean retrieveReturn() default false;
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: ViewDebug$ExportedProperty.class */
    public @interface ExportedProperty {
        boolean resolveId() default false;

        IntToString[] mapping() default {};

        IntToString[] indexMapping() default {};

        FlagToString[] flagMapping() default {};

        boolean deepExport() default false;

        String prefix() default "";

        String category() default "";
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: ViewDebug$FlagToString.class */
    public @interface FlagToString {
        int mask();

        int equals();

        String name();

        boolean outputIf() default true;
    }

    /* loaded from: ViewDebug$HierarchyHandler.class */
    public interface HierarchyHandler {
        void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i);

        View findHierarchyView(String str, int i);
    }

    @Deprecated
    /* loaded from: ViewDebug$HierarchyTraceType.class */
    public enum HierarchyTraceType {
        INVALIDATE,
        INVALIDATE_CHILD,
        INVALIDATE_CHILD_IN_PARENT,
        REQUEST_LAYOUT,
        ON_LAYOUT,
        ON_MEASURE,
        DRAW,
        BUILD_CACHE
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: ViewDebug$IntToString.class */
    public @interface IntToString {
        int from();

        String to();
    }

    @Deprecated
    /* loaded from: ViewDebug$RecyclerTraceType.class */
    public enum RecyclerTraceType {
        NEW_VIEW,
        BIND_VIEW,
        RECYCLE_FROM_ACTIVE_HEAP,
        RECYCLE_FROM_SCRAP_HEAP,
        MOVE_TO_SCRAP_HEAP,
        MOVE_FROM_ACTIVE_TO_SCRAP_HEAP
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewDebug$ViewOperation.class */
    public interface ViewOperation<T> {
        T[] pre();

        void run(T... tArr);

        void post(T... tArr);
    }

    public static long getViewInstanceCount() {
        return Debug.countInstancesOfClass(View.class);
    }

    public static long getViewRootImplCount() {
        return Debug.countInstancesOfClass(ViewRootImpl.class);
    }

    @Deprecated
    public static void trace(View view, RecyclerTraceType type, int... parameters) {
    }

    @Deprecated
    public static void startRecyclerTracing(String prefix, View view) {
    }

    @Deprecated
    public static void stopRecyclerTracing() {
    }

    @Deprecated
    public static void trace(View view, HierarchyTraceType type) {
    }

    @Deprecated
    public static void startHierarchyTracing(String prefix, View view) {
    }

    @Deprecated
    public static void stopHierarchyTracing() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dispatchCommand(View view, String command, String parameters, OutputStream clientStream) throws IOException {
        View view2 = view.getRootView();
        if (REMOTE_COMMAND_DUMP.equalsIgnoreCase(command)) {
            dump(view2, false, true, clientStream);
        } else if (REMOTE_COMMAND_CAPTURE_LAYERS.equalsIgnoreCase(command)) {
            captureLayers(view2, new DataOutputStream(clientStream));
        } else {
            String[] params = parameters.split(Separators.SP);
            if (REMOTE_COMMAND_CAPTURE.equalsIgnoreCase(command)) {
                capture(view2, clientStream, params[0]);
            } else if (REMOTE_COMMAND_OUTPUT_DISPLAYLIST.equalsIgnoreCase(command)) {
                outputDisplayList(view2, params[0]);
            } else if (REMOTE_COMMAND_INVALIDATE.equalsIgnoreCase(command)) {
                invalidate(view2, params[0]);
            } else if (REMOTE_COMMAND_REQUEST_LAYOUT.equalsIgnoreCase(command)) {
                requestLayout(view2, params[0]);
            } else if (REMOTE_PROFILE.equalsIgnoreCase(command)) {
                profile(view2, clientStream, params[0]);
            }
        }
    }

    public static View findView(View root, String parameter) {
        if (parameter.indexOf(64) != -1) {
            String[] ids = parameter.split(Separators.AT);
            String className = ids[0];
            int hashCode = (int) Long.parseLong(ids[1], 16);
            View view = root.getRootView();
            if (view instanceof ViewGroup) {
                return findView((ViewGroup) view, className, hashCode);
            }
            return null;
        }
        int id = root.getResources().getIdentifier(parameter, null, null);
        return root.getRootView().findViewById(id);
    }

    private static void invalidate(View root, String parameter) {
        View view = findView(root, parameter);
        if (view != null) {
            view.postInvalidate();
        }
    }

    private static void requestLayout(View root, String parameter) {
        final View view = findView(root, parameter);
        if (view != null) {
            root.post(new Runnable() { // from class: android.view.ViewDebug.1
                @Override // java.lang.Runnable
                public void run() {
                    View.this.requestLayout();
                }
            });
        }
    }

    private static void profile(View root, OutputStream clientStream, String parameter) throws IOException {
        View view = findView(root, parameter);
        BufferedWriter out = null;
        try {
            try {
                out = new BufferedWriter(new OutputStreamWriter(clientStream), 32768);
                if (view != null) {
                    profileViewAndChildren(view, out);
                } else {
                    out.write("-1 -1 -1");
                    out.newLine();
                }
                out.write("DONE.");
                out.newLine();
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                Log.w("View", "Problem profiling the view:", e);
                if (out != null) {
                    out.close();
                }
            }
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static void profileViewAndChildren(View view, BufferedWriter out) throws IOException {
        profileViewAndChildren(view, out, true);
    }

    private static void profileViewAndChildren(final View view, BufferedWriter out, boolean root) throws IOException {
        long durationMeasure = (root || (view.mPrivateFlags & 2048) != 0) ? profileViewOperation(view, new ViewOperation<Void>() { // from class: android.view.ViewDebug.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.view.ViewDebug.ViewOperation
            public Void[] pre() {
                forceLayout(View.this);
                return null;
            }

            private void forceLayout(View view2) {
                view2.forceLayout();
                if (view2 instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view2;
                    int count = group.getChildCount();
                    for (int i = 0; i < count; i++) {
                        forceLayout(group.getChildAt(i));
                    }
                }
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run(Void... data) {
                View.this.measure(View.this.mOldWidthMeasureSpec, View.this.mOldHeightMeasureSpec);
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void post(Void... data) {
            }
        }) : 0L;
        long durationLayout = (root || (view.mPrivateFlags & 8192) != 0) ? profileViewOperation(view, new ViewOperation<Void>() { // from class: android.view.ViewDebug.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.view.ViewDebug.ViewOperation
            public Void[] pre() {
                return null;
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run(Void... data) {
                View.this.layout(View.this.mLeft, View.this.mTop, View.this.mRight, View.this.mBottom);
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void post(Void... data) {
            }
        }) : 0L;
        long durationDraw = (!root && view.willNotDraw() && (view.mPrivateFlags & 32) == 0) ? 0L : profileViewOperation(view, new ViewOperation<Object>() { // from class: android.view.ViewDebug.4
            @Override // android.view.ViewDebug.ViewOperation
            public Object[] pre() {
                DisplayMetrics metrics = (View.this == null || View.this.getResources() == null) ? null : View.this.getResources().getDisplayMetrics();
                Bitmap bitmap = metrics != null ? Bitmap.createBitmap(metrics, metrics.widthPixels, metrics.heightPixels, Bitmap.Config.RGB_565) : null;
                Canvas canvas = bitmap != null ? new Canvas(bitmap) : null;
                return new Object[]{bitmap, canvas};
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run(Object... data) {
                if (data[1] != null) {
                    View.this.draw((Canvas) data[1]);
                }
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void post(Object... data) {
                if (data[1] != null) {
                    ((Canvas) data[1]).setBitmap(null);
                }
                if (data[0] != null) {
                    ((Bitmap) data[0]).recycle();
                }
            }
        });
        out.write(String.valueOf(durationMeasure));
        out.write(32);
        out.write(String.valueOf(durationLayout));
        out.write(32);
        out.write(String.valueOf(durationDraw));
        out.newLine();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                profileViewAndChildren(group.getChildAt(i), out, false);
            }
        }
    }

    private static <T> long profileViewOperation(View view, final ViewOperation<T> operation) {
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] duration = new long[1];
        view.post(new Runnable() { // from class: android.view.ViewDebug.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Object[] pre = ViewOperation.this.pre();
                    long start = Debug.threadCpuTimeNanos();
                    ViewOperation.this.run(pre);
                    duration[0] = Debug.threadCpuTimeNanos() - start;
                    ViewOperation.this.post(pre);
                    latch.countDown();
                } catch (Throwable th) {
                    latch.countDown();
                    throw th;
                }
            }
        });
        try {
            if (!latch.await(4000L, TimeUnit.MILLISECONDS)) {
                Log.w("View", "Could not complete the profiling of the view " + view);
                return -1L;
            }
            return duration[0];
        } catch (InterruptedException e) {
            Log.w("View", "Could not complete the profiling of the view " + view);
            Thread.currentThread().interrupt();
            return -1L;
        }
    }

    public static void captureLayers(View root, DataOutputStream clientStream) throws IOException {
        try {
            Rect outRect = new Rect();
            try {
                root.mAttachInfo.mSession.getDisplayFrame(root.mAttachInfo.mWindow, outRect);
            } catch (RemoteException e) {
            }
            clientStream.writeInt(outRect.width());
            clientStream.writeInt(outRect.height());
            captureViewLayer(root, clientStream, true);
            clientStream.write(2);
            clientStream.close();
        } catch (Throwable th) {
            clientStream.close();
            throw th;
        }
    }

    private static void captureViewLayer(View view, DataOutputStream clientStream, boolean visible) throws IOException {
        boolean localVisible = view.getVisibility() == 0 && visible;
        if ((view.mPrivateFlags & 128) != 128) {
            int id = view.getId();
            String name = view.getClass().getSimpleName();
            if (id != -1) {
                name = resolveId(view.getContext(), id).toString();
            }
            clientStream.write(1);
            clientStream.writeUTF(name);
            clientStream.writeByte(localVisible ? 1 : 0);
            int[] position = new int[2];
            view.getLocationInWindow(position);
            clientStream.writeInt(position[0]);
            clientStream.writeInt(position[1]);
            clientStream.flush();
            Bitmap b = performViewCapture(view, true);
            if (b != null) {
                ByteArrayOutputStream arrayOut = new ByteArrayOutputStream(b.getWidth() * b.getHeight() * 2);
                b.compress(Bitmap.CompressFormat.PNG, 100, arrayOut);
                clientStream.writeInt(arrayOut.size());
                arrayOut.writeTo(clientStream);
            }
            clientStream.flush();
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                captureViewLayer(group.getChildAt(i), clientStream, localVisible);
            }
        }
        if (view.mOverlay != null) {
            ViewGroup overlayContainer = view.getOverlay().mOverlayViewGroup;
            captureViewLayer(overlayContainer, clientStream, localVisible);
        }
    }

    private static void outputDisplayList(View root, String parameter) throws IOException {
        View view = findView(root, parameter);
        view.getViewRootImpl().outputDisplayList(view);
    }

    public static void outputDisplayList(View root, View target) {
        root.getViewRootImpl().outputDisplayList(target);
    }

    private static void capture(View root, OutputStream clientStream, String parameter) throws IOException {
        View captureView = findView(root, parameter);
        capture(root, clientStream, captureView);
    }

    public static void capture(View root, OutputStream clientStream, View captureView) throws IOException {
        Bitmap b = performViewCapture(captureView, false);
        if (b == null) {
            Log.w("View", "Failed to create capture bitmap!");
            b = Bitmap.createBitmap(root.getResources().getDisplayMetrics(), 1, 1, Bitmap.Config.ARGB_8888);
        }
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(clientStream, 32768);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            if (out != null) {
                out.close();
            }
            b.recycle();
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            b.recycle();
            throw th;
        }
    }

    private static Bitmap performViewCapture(final View captureView, final boolean skipChildren) {
        if (captureView != null) {
            final CountDownLatch latch = new CountDownLatch(1);
            final Bitmap[] cache = new Bitmap[1];
            captureView.post(new Runnable() { // from class: android.view.ViewDebug.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        try {
                            cache[0] = captureView.createSnapshot(Bitmap.Config.ARGB_8888, 0, skipChildren);
                            latch.countDown();
                        } catch (OutOfMemoryError e) {
                            Log.w("View", "Out of memory for bitmap");
                            latch.countDown();
                        }
                    } catch (Throwable th) {
                        latch.countDown();
                        throw th;
                    }
                }
            });
            try {
                latch.await(4000L, TimeUnit.MILLISECONDS);
                return cache[0];
            } catch (InterruptedException e) {
                Log.w("View", "Could not complete the capture of the view " + captureView);
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    public static void dump(View root, boolean skipChildren, boolean includeProperties, OutputStream clientStream) throws IOException {
        BufferedWriter out = null;
        try {
            try {
                out = new BufferedWriter(new OutputStreamWriter(clientStream, "utf-8"), 32768);
                View view = root.getRootView();
                if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    dumpViewHierarchy(group.getContext(), group, out, 0, skipChildren, includeProperties);
                }
                out.write("DONE.");
                out.newLine();
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                Log.w("View", "Problem dumping the view:", e);
                if (out != null) {
                    out.close();
                }
            }
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    private static View findView(ViewGroup group, String className, int hashCode) {
        View found;
        View found2;
        if (isRequestedView(group, className, hashCode)) {
            return group;
        }
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                View found3 = findView((ViewGroup) view, className, hashCode);
                if (found3 != null) {
                    return found3;
                }
            } else if (isRequestedView(view, className, hashCode)) {
                return view;
            }
            if (view.mOverlay != null && (found2 = findView(view.mOverlay.mOverlayViewGroup, className, hashCode)) != null) {
                return found2;
            }
            if ((view instanceof HierarchyHandler) && (found = ((HierarchyHandler) view).findHierarchyView(className, hashCode)) != null) {
                return found;
            }
        }
        return null;
    }

    private static boolean isRequestedView(View view, String className, int hashCode) {
        if (view.hashCode() == hashCode) {
            String viewClassName = view.getClass().getName();
            if (className.equals("ViewOverlay")) {
                return viewClassName.equals("android.view.ViewOverlay$OverlayViewGroup");
            }
            return className.equals(viewClassName);
        }
        return false;
    }

    private static void dumpViewHierarchy(Context context, ViewGroup group, BufferedWriter out, int level, boolean skipChildren, boolean includeProperties) {
        if (!dumpView(context, group, out, level, includeProperties) || skipChildren) {
            return;
        }
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                dumpViewHierarchy(context, (ViewGroup) view, out, level + 1, skipChildren, includeProperties);
            } else {
                dumpView(context, view, out, level + 1, includeProperties);
            }
            if (view.mOverlay != null) {
                ViewOverlay overlay = view.getOverlay();
                ViewGroup overlayContainer = overlay.mOverlayViewGroup;
                dumpViewHierarchy(context, overlayContainer, out, level + 2, skipChildren, includeProperties);
            }
        }
        if (group instanceof HierarchyHandler) {
            ((HierarchyHandler) group).dumpViewHierarchyWithProperties(out, level + 1);
        }
    }

    private static boolean dumpView(Context context, View view, BufferedWriter out, int level, boolean includeProperties) {
        for (int i = 0; i < level; i++) {
            try {
                out.write(32);
            } catch (IOException e) {
                Log.w("View", "Error while dumping hierarchy tree");
                return false;
            }
        }
        String className = view.getClass().getName();
        if (className.equals("android.view.ViewOverlay$OverlayViewGroup")) {
            className = "ViewOverlay";
        }
        out.write(className);
        out.write(64);
        out.write(Integer.toHexString(view.hashCode()));
        out.write(32);
        if (includeProperties) {
            dumpViewProperties(context, view, out);
        }
        out.newLine();
        return true;
    }

    private static Field[] getExportedPropertyFields(Class<?> klass) {
        if (sFieldsForClasses == null) {
            sFieldsForClasses = new HashMap<>();
        }
        if (sAnnotations == null) {
            sAnnotations = new HashMap<>(512);
        }
        HashMap<Class<?>, Field[]> map = sFieldsForClasses;
        Field[] fields = map.get(klass);
        if (fields != null) {
            return fields;
        }
        ArrayList<Field> foundFields = new ArrayList<>();
        Field[] fields2 = klass.getDeclaredFields();
        for (Field field : fields2) {
            if (field.isAnnotationPresent(ExportedProperty.class)) {
                field.setAccessible(true);
                foundFields.add(field);
                sAnnotations.put(field, field.getAnnotation(ExportedProperty.class));
            }
        }
        Field[] fields3 = (Field[]) foundFields.toArray(new Field[foundFields.size()]);
        map.put(klass, fields3);
        return fields3;
    }

    private static Method[] getExportedPropertyMethods(Class<?> klass) {
        if (sMethodsForClasses == null) {
            sMethodsForClasses = new HashMap<>(100);
        }
        if (sAnnotations == null) {
            sAnnotations = new HashMap<>(512);
        }
        HashMap<Class<?>, Method[]> map = sMethodsForClasses;
        Method[] methods = map.get(klass);
        if (methods != null) {
            return methods;
        }
        ArrayList<Method> foundMethods = new ArrayList<>();
        Method[] methods2 = klass.getDeclaredMethods();
        for (Method method : methods2) {
            if (method.getParameterTypes().length == 0 && method.isAnnotationPresent(ExportedProperty.class) && method.getReturnType() != Void.class) {
                method.setAccessible(true);
                foundMethods.add(method);
                sAnnotations.put(method, method.getAnnotation(ExportedProperty.class));
            }
        }
        Method[] methods3 = (Method[]) foundMethods.toArray(new Method[foundMethods.size()]);
        map.put(klass, methods3);
        return methods3;
    }

    private static void dumpViewProperties(Context context, Object view, BufferedWriter out) throws IOException {
        dumpViewProperties(context, view, out, "");
    }

    private static void dumpViewProperties(Context context, Object view, BufferedWriter out, String prefix) throws IOException {
        if (view == null) {
            out.write(prefix + "=4,null ");
            return;
        }
        Class<?> klass = view.getClass();
        do {
            exportFields(context, view, out, klass, prefix);
            exportMethods(context, view, out, klass, prefix);
            klass = klass.getSuperclass();
        } while (klass != Object.class);
    }

    private static Object callMethodOnAppropriateTheadBlocking(final Method method, Object object) throws IllegalAccessException, InvocationTargetException, TimeoutException {
        if (!(object instanceof View)) {
            return method.invoke(object, null);
        }
        final View view = (View) object;
        Callable<Object> callable = new Callable<Object>() { // from class: android.view.ViewDebug.7
            @Override // java.util.concurrent.Callable
            public Object call() throws IllegalAccessException, InvocationTargetException {
                return Method.this.invoke(view, null);
            }
        };
        FutureTask<Object> future = new FutureTask<>(callable);
        Handler handler = view.getHandler();
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(future);
        while (true) {
            try {
                return future.get(4000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            } catch (CancellationException e2) {
                throw new RuntimeException("Unexpected cancellation exception", e2);
            } catch (ExecutionException e3) {
                Throwable t = e3.getCause();
                if (t instanceof IllegalAccessException) {
                    throw ((IllegalAccessException) t);
                }
                if (t instanceof InvocationTargetException) {
                    throw ((InvocationTargetException) t);
                }
                throw new RuntimeException("Unexpected exception", t);
            }
        }
    }

    private static void exportMethods(Context context, Object view, BufferedWriter out, Class<?> klass, String prefix) throws IOException {
        Object methodValue;
        Class<?> returnType;
        ExportedProperty property;
        String categoryPrefix;
        Method[] methods = getExportedPropertyMethods(klass);
        for (Method method : methods) {
            try {
                methodValue = callMethodOnAppropriateTheadBlocking(method, view);
                returnType = method.getReturnType();
                property = sAnnotations.get(method);
                categoryPrefix = property.category().length() != 0 ? property.category() + Separators.COLON : "";
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            } catch (TimeoutException e3) {
            }
            if (returnType == Integer.TYPE) {
                if (property.resolveId() && context != null) {
                    int id = ((Integer) methodValue).intValue();
                    methodValue = resolveId(context, id);
                } else {
                    FlagToString[] flagsMapping = property.flagMapping();
                    if (flagsMapping.length > 0) {
                        String valuePrefix = categoryPrefix + prefix + method.getName() + '_';
                        exportUnrolledFlags(out, flagsMapping, ((Integer) methodValue).intValue(), valuePrefix);
                    }
                    IntToString[] mapping = property.mapping();
                    if (mapping.length > 0) {
                        int intValue = ((Integer) methodValue).intValue();
                        boolean mapped = false;
                        int mappingCount = mapping.length;
                        int j = 0;
                        while (true) {
                            if (j >= mappingCount) {
                                break;
                            }
                            IntToString mapper = mapping[j];
                            if (mapper.from() != intValue) {
                                j++;
                            } else {
                                methodValue = mapper.to();
                                mapped = true;
                                break;
                            }
                        }
                        if (!mapped) {
                            methodValue = Integer.valueOf(intValue);
                        }
                    }
                }
            } else if (returnType == int[].class) {
                int[] array = (int[]) methodValue;
                String valuePrefix2 = categoryPrefix + prefix + method.getName() + '_';
                exportUnrolledArray(context, out, property, array, valuePrefix2, "()");
                return;
            } else if (!returnType.isPrimitive() && property.deepExport()) {
                dumpViewProperties(context, methodValue, out, prefix + property.prefix());
            }
            writeEntry(out, categoryPrefix + prefix, method.getName(), "()", methodValue);
        }
    }

    private static void exportFields(Context context, Object view, BufferedWriter out, Class<?> klass, String prefix) throws IOException {
        Object fieldValue;
        Class<?> type;
        ExportedProperty property;
        String categoryPrefix;
        Field[] fields = getExportedPropertyFields(klass);
        for (Field field : fields) {
            try {
                fieldValue = null;
                type = field.getType();
                property = sAnnotations.get(field);
                categoryPrefix = property.category().length() != 0 ? property.category() + Separators.COLON : "";
            } catch (IllegalAccessException e) {
            }
            if (type == Integer.TYPE || type == Byte.TYPE) {
                if (property.resolveId() && context != null) {
                    int id = field.getInt(view);
                    fieldValue = resolveId(context, id);
                } else {
                    FlagToString[] flagsMapping = property.flagMapping();
                    if (flagsMapping.length > 0) {
                        int intValue = field.getInt(view);
                        String valuePrefix = categoryPrefix + prefix + field.getName() + '_';
                        exportUnrolledFlags(out, flagsMapping, intValue, valuePrefix);
                    }
                    IntToString[] mapping = property.mapping();
                    if (mapping.length > 0) {
                        int intValue2 = field.getInt(view);
                        int mappingCount = mapping.length;
                        int j = 0;
                        while (true) {
                            if (j >= mappingCount) {
                                break;
                            }
                            IntToString mapped = mapping[j];
                            if (mapped.from() != intValue2) {
                                j++;
                            } else {
                                fieldValue = mapped.to();
                                break;
                            }
                        }
                        if (fieldValue == null) {
                            fieldValue = Integer.valueOf(intValue2);
                        }
                    }
                }
            } else if (type == int[].class) {
                int[] array = (int[]) field.get(view);
                String valuePrefix2 = categoryPrefix + prefix + field.getName() + '_';
                exportUnrolledArray(context, out, property, array, valuePrefix2, "");
                return;
            } else if (!type.isPrimitive() && property.deepExport()) {
                dumpViewProperties(context, field.get(view), out, prefix + property.prefix());
            }
            if (fieldValue == null) {
                fieldValue = field.get(view);
            }
            writeEntry(out, categoryPrefix + prefix, field.getName(), "", fieldValue);
        }
    }

    private static void writeEntry(BufferedWriter out, String prefix, String name, String suffix, Object value) throws IOException {
        out.write(prefix);
        out.write(name);
        out.write(suffix);
        out.write(Separators.EQUALS);
        writeValue(out, value);
        out.write(32);
    }

    private static void exportUnrolledFlags(BufferedWriter out, FlagToString[] mapping, int intValue, String prefix) throws IOException {
        for (FlagToString flagMapping : mapping) {
            boolean ifTrue = flagMapping.outputIf();
            int maskResult = intValue & flagMapping.mask();
            boolean test = maskResult == flagMapping.equals();
            if ((test && ifTrue) || (!test && !ifTrue)) {
                String name = flagMapping.name();
                String value = "0x" + Integer.toHexString(maskResult);
                writeEntry(out, prefix, name, "", value);
            }
        }
    }

    private static void exportUnrolledArray(Context context, BufferedWriter out, ExportedProperty property, int[] array, String prefix, String suffix) throws IOException {
        IntToString[] indexMapping = property.indexMapping();
        boolean hasIndexMapping = indexMapping.length > 0;
        IntToString[] mapping = property.mapping();
        boolean hasMapping = mapping.length > 0;
        boolean resolveId = property.resolveId() && context != null;
        int valuesCount = array.length;
        for (int j = 0; j < valuesCount; j++) {
            String value = null;
            int intValue = array[j];
            String name = String.valueOf(j);
            if (hasIndexMapping) {
                int mappingCount = indexMapping.length;
                int k = 0;
                while (true) {
                    if (k >= mappingCount) {
                        break;
                    }
                    IntToString mapped = indexMapping[k];
                    if (mapped.from() != j) {
                        k++;
                    } else {
                        name = mapped.to();
                        break;
                    }
                }
            }
            if (hasMapping) {
                int mappingCount2 = mapping.length;
                int k2 = 0;
                while (true) {
                    if (k2 >= mappingCount2) {
                        break;
                    }
                    IntToString mapped2 = mapping[k2];
                    if (mapped2.from() != intValue) {
                        k2++;
                    } else {
                        value = mapped2.to();
                        break;
                    }
                }
            }
            if (resolveId) {
                if (value == null) {
                    value = (String) resolveId(context, intValue);
                }
            } else {
                value = String.valueOf(intValue);
            }
            writeEntry(out, prefix, name, suffix, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object resolveId(Context context, int id) {
        Object fieldValue;
        Resources resources = context.getResources();
        if (id >= 0) {
            try {
                fieldValue = resources.getResourceTypeName(id) + '/' + resources.getResourceEntryName(id);
            } catch (Resources.NotFoundException e) {
                fieldValue = "id/0x" + Integer.toHexString(id);
            }
        } else {
            fieldValue = "NO_ID";
        }
        return fieldValue;
    }

    private static void writeValue(BufferedWriter out, Object value) throws IOException {
        if (value != null) {
            String output = "[EXCEPTION]";
            try {
                output = value.toString().replace(Separators.RETURN, "\\n");
                out.write(String.valueOf(output.length()));
                out.write(Separators.COMMA);
                out.write(output);
                return;
            } catch (Throwable th) {
                out.write(String.valueOf(output.length()));
                out.write(Separators.COMMA);
                out.write(output);
                throw th;
            }
        }
        out.write("4,null");
    }

    private static Field[] capturedViewGetPropertyFields(Class<?> klass) {
        if (mCapturedViewFieldsForClasses == null) {
            mCapturedViewFieldsForClasses = new HashMap<>();
        }
        HashMap<Class<?>, Field[]> map = mCapturedViewFieldsForClasses;
        Field[] fields = map.get(klass);
        if (fields != null) {
            return fields;
        }
        ArrayList<Field> foundFields = new ArrayList<>();
        Field[] fields2 = klass.getFields();
        for (Field field : fields2) {
            if (field.isAnnotationPresent(CapturedViewProperty.class)) {
                field.setAccessible(true);
                foundFields.add(field);
            }
        }
        Field[] fields3 = (Field[]) foundFields.toArray(new Field[foundFields.size()]);
        map.put(klass, fields3);
        return fields3;
    }

    private static Method[] capturedViewGetPropertyMethods(Class<?> klass) {
        if (mCapturedViewMethodsForClasses == null) {
            mCapturedViewMethodsForClasses = new HashMap<>();
        }
        HashMap<Class<?>, Method[]> map = mCapturedViewMethodsForClasses;
        Method[] methods = map.get(klass);
        if (methods != null) {
            return methods;
        }
        ArrayList<Method> foundMethods = new ArrayList<>();
        Method[] methods2 = klass.getMethods();
        for (Method method : methods2) {
            if (method.getParameterTypes().length == 0 && method.isAnnotationPresent(CapturedViewProperty.class) && method.getReturnType() != Void.class) {
                method.setAccessible(true);
                foundMethods.add(method);
            }
        }
        Method[] methods3 = (Method[]) foundMethods.toArray(new Method[foundMethods.size()]);
        map.put(klass, methods3);
        return methods3;
    }

    private static String capturedViewExportMethods(Object obj, Class<?> klass, String prefix) {
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        Method[] methods = capturedViewGetPropertyMethods(klass);
        for (Method method : methods) {
            try {
                Object methodValue = method.invoke(obj, null);
                Class<?> returnType = method.getReturnType();
                CapturedViewProperty property = (CapturedViewProperty) method.getAnnotation(CapturedViewProperty.class);
                if (property.retrieveReturn()) {
                    sb.append(capturedViewExportMethods(methodValue, returnType, method.getName() + Separators.POUND));
                } else {
                    sb.append(prefix);
                    sb.append(method.getName());
                    sb.append("()=");
                    if (methodValue != null) {
                        String value = methodValue.toString().replace(Separators.RETURN, "\\n");
                        sb.append(value);
                    } else {
                        sb.append("null");
                    }
                    sb.append("; ");
                }
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
        return sb.toString();
    }

    private static String capturedViewExportFields(Object obj, Class<?> klass, String prefix) {
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        Field[] fields = capturedViewGetPropertyFields(klass);
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(obj);
                sb.append(prefix);
                sb.append(field.getName());
                sb.append(Separators.EQUALS);
                if (fieldValue != null) {
                    String value = fieldValue.toString().replace(Separators.RETURN, "\\n");
                    sb.append(value);
                } else {
                    sb.append("null");
                }
                sb.append(' ');
            } catch (IllegalAccessException e) {
            }
        }
        return sb.toString();
    }

    public static void dumpCapturedView(String tag, Object view) {
        Class<?> klass = view.getClass();
        Log.d(tag, (klass.getName() + ": ") + capturedViewExportFields(view, klass, "") + capturedViewExportMethods(view, klass, ""));
    }

    public static Object invokeViewMethod(final View view, final Method method, final Object[] args) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        view.post(new Runnable() { // from class: android.view.ViewDebug.8
            @Override // java.lang.Runnable
            public void run() {
                try {
                    AtomicReference.this.set(method.invoke(view, args));
                } catch (InvocationTargetException e) {
                    exception.set(e.getCause());
                } catch (Exception e2) {
                    exception.set(e2);
                }
                latch.countDown();
            }
        });
        try {
            latch.await();
            if (exception.get() != null) {
                throw new RuntimeException(exception.get());
            }
            return result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLayoutParameter(final View view, String param, int value) throws NoSuchFieldException, IllegalAccessException {
        final ViewGroup.LayoutParams p = view.getLayoutParams();
        Field f = p.getClass().getField(param);
        if (f.getType() != Integer.TYPE) {
            throw new RuntimeException("Only integer layout parameters can be set. Field " + param + " is of type " + f.getType().getSimpleName());
        }
        f.set(p, Integer.valueOf(value));
        view.post(new Runnable() { // from class: android.view.ViewDebug.9
            @Override // java.lang.Runnable
            public void run() {
                View.this.setLayoutParams(p);
            }
        });
    }
}