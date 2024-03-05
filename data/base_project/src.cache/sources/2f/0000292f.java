package libcore.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import libcore.io.BufferIterator;
import libcore.io.ErrnoException;
import libcore.io.MemoryMappedFile;

/* loaded from: ZoneInfoDB.class */
public final class ZoneInfoDB {
    private static final TzData DATA = new TzData(System.getenv("ANDROID_DATA") + "/misc/zoneinfo/tzdata", System.getenv("ANDROID_ROOT") + "/usr/share/zoneinfo/tzdata");

    /* loaded from: ZoneInfoDB$TzData.class */
    public static class TzData {
        private MemoryMappedFile mappedFile;
        private String version;
        private String zoneTab;
        private String[] ids;
        private int[] byteOffsets;
        private int[] rawUtcOffsets;

        public TzData(String... paths) {
            for (String path : paths) {
                if (loadData(path)) {
                    return;
                }
            }
            System.logE("Couldn't find any tzdata!");
            this.version = "missing";
            this.zoneTab = "# Emergency fallback data.\n";
            this.ids = new String[]{"GMT"};
            int[] iArr = new int[1];
            this.rawUtcOffsets = iArr;
            this.byteOffsets = iArr;
        }

        private boolean loadData(String path) {
            try {
                this.mappedFile = MemoryMappedFile.mmapRO(path);
                try {
                    readHeader();
                    return true;
                } catch (Exception ex) {
                    System.logE("tzdata file \"" + path + "\" was present but invalid!", ex);
                    return false;
                }
            } catch (ErrnoException e) {
                return false;
            }
        }

        private void readHeader() {
            BufferIterator it = this.mappedFile.bigEndianIterator();
            byte[] tzdata_version = new byte[12];
            it.readByteArray(tzdata_version, 0, tzdata_version.length);
            String magic = new String(tzdata_version, 0, 6, StandardCharsets.US_ASCII);
            if (!magic.equals("tzdata") || tzdata_version[11] != 0) {
                throw new RuntimeException("bad tzdata magic: " + Arrays.toString(tzdata_version));
            }
            this.version = new String(tzdata_version, 6, 5, StandardCharsets.US_ASCII);
            int index_offset = it.readInt();
            int data_offset = it.readInt();
            int zonetab_offset = it.readInt();
            readIndex(it, index_offset, data_offset);
            readZoneTab(it, zonetab_offset, ((int) this.mappedFile.size()) - zonetab_offset);
        }

        private void readZoneTab(BufferIterator it, int zoneTabOffset, int zoneTabSize) {
            byte[] bytes = new byte[zoneTabSize];
            it.seek(zoneTabOffset);
            it.readByteArray(bytes, 0, bytes.length);
            this.zoneTab = new String(bytes, 0, bytes.length, StandardCharsets.US_ASCII);
        }

        private void readIndex(BufferIterator it, int indexOffset, int dataOffset) {
            it.seek(indexOffset);
            byte[] idBytes = new byte[40];
            int indexSize = dataOffset - indexOffset;
            int entryCount = indexSize / 52;
            char[] idChars = new char[entryCount * 40];
            int[] idEnd = new int[entryCount];
            int idOffset = 0;
            this.byteOffsets = new int[entryCount];
            this.rawUtcOffsets = new int[entryCount];
            for (int i = 0; i < entryCount; i++) {
                it.readByteArray(idBytes, 0, idBytes.length);
                this.byteOffsets[i] = it.readInt();
                int[] iArr = this.byteOffsets;
                int i2 = i;
                iArr[i2] = iArr[i2] + dataOffset;
                int length = it.readInt();
                if (length < 44) {
                    throw new AssertionError("length in index file < sizeof(tzhead)");
                }
                this.rawUtcOffsets[i] = it.readInt();
                int len = idBytes.length;
                for (int j = 0; j < len && idBytes[j] != 0; j++) {
                    int i3 = idOffset;
                    idOffset++;
                    idChars[i3] = (char) (idBytes[j] & 255);
                }
                idEnd[i] = idOffset;
            }
            String allIds = new String(idChars, 0, idOffset);
            this.ids = new String[entryCount];
            int i4 = 0;
            while (i4 < entryCount) {
                this.ids[i4] = allIds.substring(i4 == 0 ? 0 : idEnd[i4 - 1], idEnd[i4]);
                i4++;
            }
        }

        public String[] getAvailableIDs() {
            return (String[]) this.ids.clone();
        }

        public String[] getAvailableIDs(int rawOffset) {
            List<String> matches = new ArrayList<>();
            int end = this.rawUtcOffsets.length;
            for (int i = 0; i < end; i++) {
                if (this.rawUtcOffsets[i] == rawOffset) {
                    matches.add(this.ids[i]);
                }
            }
            return (String[]) matches.toArray(new String[matches.size()]);
        }

        public String getVersion() {
            return this.version;
        }

        public String getZoneTab() {
            return this.zoneTab;
        }

        public TimeZone makeTimeZone(String id) throws IOException {
            int index = Arrays.binarySearch(this.ids, id);
            if (index < 0) {
                return null;
            }
            BufferIterator it = this.mappedFile.bigEndianIterator();
            it.skip(this.byteOffsets[index]);
            return ZoneInfo.makeTimeZone(id, it);
        }
    }

    private ZoneInfoDB() {
    }

    public static TzData getInstance() {
        return DATA;
    }
}