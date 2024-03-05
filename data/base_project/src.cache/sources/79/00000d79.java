package android.renderscript;

import android.content.res.AssetManager;
import android.content.res.Resources;
import java.io.File;
import java.io.InputStream;

/* loaded from: FileA3D.class */
public class FileA3D extends BaseObj {
    IndexEntry[] mFileEntries;
    InputStream mInputStream;

    /* loaded from: FileA3D$EntryType.class */
    public enum EntryType {
        UNKNOWN(0),
        MESH(1);
        
        int mID;

        EntryType(int id) {
            this.mID = id;
        }

        static EntryType toEntryType(int intID) {
            return values()[intID];
        }
    }

    /* loaded from: FileA3D$IndexEntry.class */
    public static class IndexEntry {
        RenderScript mRS;
        int mIndex;
        int mID;
        String mName;
        EntryType mEntryType;
        BaseObj mLoadedObj = null;

        public String getName() {
            return this.mName;
        }

        public EntryType getEntryType() {
            return this.mEntryType;
        }

        public BaseObj getObject() {
            this.mRS.validate();
            BaseObj obj = internalCreate(this.mRS, this);
            return obj;
        }

        public Mesh getMesh() {
            return (Mesh) getObject();
        }

        static synchronized BaseObj internalCreate(RenderScript rs, IndexEntry entry) {
            int objectID;
            if (entry.mLoadedObj != null) {
                return entry.mLoadedObj;
            }
            if (entry.mEntryType == EntryType.UNKNOWN || (objectID = rs.nFileA3DGetEntryByIndex(entry.mID, entry.mIndex)) == 0) {
                return null;
            }
            switch (entry.mEntryType) {
                case MESH:
                    entry.mLoadedObj = new Mesh(objectID, rs);
                    break;
            }
            entry.mLoadedObj.updateFromNative();
            return entry.mLoadedObj;
        }

        IndexEntry(RenderScript rs, int index, int id, String name, EntryType type) {
            this.mRS = rs;
            this.mIndex = index;
            this.mID = id;
            this.mName = name;
            this.mEntryType = type;
        }
    }

    FileA3D(int id, RenderScript rs, InputStream stream) {
        super(id, rs);
        this.mInputStream = stream;
    }

    private void initEntries() {
        int numFileEntries = this.mRS.nFileA3DGetNumIndexEntries(getID(this.mRS));
        if (numFileEntries <= 0) {
            return;
        }
        this.mFileEntries = new IndexEntry[numFileEntries];
        int[] ids = new int[numFileEntries];
        String[] names = new String[numFileEntries];
        this.mRS.nFileA3DGetIndexEntries(getID(this.mRS), numFileEntries, ids, names);
        for (int i = 0; i < numFileEntries; i++) {
            this.mFileEntries[i] = new IndexEntry(this.mRS, i, getID(this.mRS), names[i], EntryType.toEntryType(ids[i]));
        }
    }

    public int getIndexEntryCount() {
        if (this.mFileEntries == null) {
            return 0;
        }
        return this.mFileEntries.length;
    }

    public IndexEntry getIndexEntry(int index) {
        if (getIndexEntryCount() == 0 || index < 0 || index >= this.mFileEntries.length) {
            return null;
        }
        return this.mFileEntries[index];
    }

    public static FileA3D createFromAsset(RenderScript rs, AssetManager mgr, String path) {
        rs.validate();
        int fileId = rs.nFileA3DCreateFromAsset(mgr, path);
        if (fileId == 0) {
            throw new RSRuntimeException("Unable to create a3d file from asset " + path);
        }
        FileA3D fa3d = new FileA3D(fileId, rs, null);
        fa3d.initEntries();
        return fa3d;
    }

    public static FileA3D createFromFile(RenderScript rs, String path) {
        int fileId = rs.nFileA3DCreateFromFile(path);
        if (fileId == 0) {
            throw new RSRuntimeException("Unable to create a3d file from " + path);
        }
        FileA3D fa3d = new FileA3D(fileId, rs, null);
        fa3d.initEntries();
        return fa3d;
    }

    public static FileA3D createFromFile(RenderScript rs, File path) {
        return createFromFile(rs, path.getAbsolutePath());
    }

    public static FileA3D createFromResource(RenderScript rs, Resources res, int id) {
        rs.validate();
        try {
            InputStream is = res.openRawResource(id);
            if (is instanceof AssetManager.AssetInputStream) {
                int asset = ((AssetManager.AssetInputStream) is).getAssetInt();
                int fileId = rs.nFileA3DCreateFromAssetStream(asset);
                if (fileId == 0) {
                    throw new RSRuntimeException("Unable to create a3d file from resource " + id);
                }
                FileA3D fa3d = new FileA3D(fileId, rs, is);
                fa3d.initEntries();
                return fa3d;
            }
            throw new RSRuntimeException("Unsupported asset stream");
        } catch (Exception e) {
            throw new RSRuntimeException("Unable to open resource " + id);
        }
    }
}