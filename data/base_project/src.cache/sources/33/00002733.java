package java.util.jar;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.SortedMap;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Pack200.class */
public abstract class Pack200 {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Pack200$Packer.class */
    public interface Packer {
        public static final String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
        public static final String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
        public static final String DEFLATE_HINT = "pack.deflate.hint";
        public static final String EFFORT = "pack.effort";
        public static final String ERROR = "error";
        public static final String FALSE = "false";
        public static final String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
        public static final String KEEP = "keep";
        public static final String KEEP_FILE_ORDER = "pack.keep.file.order";
        public static final String LATEST = "latest";
        public static final String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
        public static final String MODIFICATION_TIME = "pack.modification.time";
        public static final String PASS = "pass";
        public static final String PASS_FILE_PFX = "pack.pass.file.";
        public static final String PROGRESS = "pack.progress";
        public static final String SEGMENT_LIMIT = "pack.segment.limit";
        public static final String STRIP = "strip";
        public static final String TRUE = "true";
        public static final String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";

        SortedMap<String, String> properties();

        void pack(JarFile jarFile, OutputStream outputStream) throws IOException;

        void pack(JarInputStream jarInputStream, OutputStream outputStream) throws IOException;

        void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

        void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Pack200$Unpacker.class */
    public interface Unpacker {
        public static final String DEFLATE_HINT = "unpack.deflate.hint";
        public static final String FALSE = "false";
        public static final String KEEP = "keep";
        public static final String PROGRESS = "unpack.progress";
        public static final String TRUE = "true";

        SortedMap<String, String> properties();

        void unpack(InputStream inputStream, JarOutputStream jarOutputStream) throws IOException;

        void unpack(File file, JarOutputStream jarOutputStream) throws IOException;

        void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

        void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
    }

    Pack200() {
        throw new RuntimeException("Stub!");
    }

    public static Packer newPacker() {
        throw new RuntimeException("Stub!");
    }

    public static Unpacker newUnpacker() {
        throw new RuntimeException("Stub!");
    }
}