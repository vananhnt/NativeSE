package android.drm;

import gov.nist.core.Separators;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: DrmUtils.class */
public class DrmUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] readBytes(String path) throws IOException {
        File file = new File(path);
        return readBytes(file);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] readBytes(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        byte[] data = null;
        try {
            int length = bufferedStream.available();
            if (length > 0) {
                data = new byte[length];
                bufferedStream.read(data);
            }
            return data;
        } finally {
            quietlyDispose(bufferedStream);
            quietlyDispose(inputStream);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeToFile(String path, byte[] data) throws IOException {
        FileOutputStream outputStream = null;
        if (null != path && null != data) {
            try {
                outputStream = new FileOutputStream(path);
                outputStream.write(data);
                quietlyDispose(outputStream);
            } catch (Throwable th) {
                quietlyDispose(outputStream);
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void removeFile(String path) throws IOException {
        File file = new File(path);
        file.delete();
    }

    private static void quietlyDispose(InputStream stream) {
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    private static void quietlyDispose(OutputStream stream) {
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static ExtendedMetadataParser getExtendedMetadataParser(byte[] extendedMetadata) {
        return new ExtendedMetadataParser(extendedMetadata);
    }

    /* loaded from: DrmUtils$ExtendedMetadataParser.class */
    public static class ExtendedMetadataParser {
        HashMap<String, String> mMap;

        private int readByte(byte[] constraintData, int arrayIndex) {
            return constraintData[arrayIndex];
        }

        private String readMultipleBytes(byte[] constraintData, int numberOfBytes, int arrayIndex) {
            byte[] returnBytes = new byte[numberOfBytes];
            int j = arrayIndex;
            int i = 0;
            while (j < arrayIndex + numberOfBytes) {
                returnBytes[i] = constraintData[j];
                j++;
                i++;
            }
            return new String(returnBytes);
        }

        private ExtendedMetadataParser(byte[] constraintData) {
            this.mMap = new HashMap<>();
            int index = 0;
            while (index < constraintData.length) {
                int keyLength = readByte(constraintData, index);
                int index2 = index + 1;
                int valueLength = readByte(constraintData, index2);
                int index3 = index2 + 1;
                String strKey = readMultipleBytes(constraintData, keyLength, index3);
                int index4 = index3 + keyLength;
                String strValue = readMultipleBytes(constraintData, valueLength, index4);
                if (strValue.equals(Separators.SP)) {
                    strValue = "";
                }
                index = index4 + valueLength;
                this.mMap.put(strKey, strValue);
            }
        }

        public Iterator<String> iterator() {
            return this.mMap.values().iterator();
        }

        public Iterator<String> keyIterator() {
            return this.mMap.keySet().iterator();
        }

        public String get(String key) {
            return this.mMap.get(key);
        }
    }
}