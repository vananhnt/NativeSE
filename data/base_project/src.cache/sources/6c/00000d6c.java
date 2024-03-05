package android.renderscript;

import java.io.UnsupportedEncodingException;

/* loaded from: BaseObj.class */
public class BaseObj {
    private int mID;
    private boolean mDestroyed;
    private String mName;
    RenderScript mRS;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseObj(int id, RenderScript rs) {
        rs.validate();
        this.mRS = rs;
        this.mID = id;
        this.mDestroyed = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setID(int id) {
        if (this.mID != 0) {
            throw new RSRuntimeException("Internal Error, reset of object ID.");
        }
        this.mID = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getID(RenderScript rs) {
        this.mRS.validate();
        if (this.mDestroyed) {
            throw new RSInvalidStateException("using a destroyed object.");
        }
        if (this.mID == 0) {
            throw new RSRuntimeException("Internal error: Object id 0.");
        }
        if (rs != null && rs != this.mRS) {
            throw new RSInvalidStateException("using object with mismatched context.");
        }
        return this.mID;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkValid() {
        if (this.mID == 0) {
            throw new RSIllegalArgumentException("Invalid object.");
        }
    }

    public void setName(String name) {
        if (name == null) {
            throw new RSIllegalArgumentException("setName requires a string of non-zero length.");
        }
        if (name.length() < 1) {
            throw new RSIllegalArgumentException("setName does not accept a zero length string.");
        }
        if (this.mName != null) {
            throw new RSIllegalArgumentException("setName object already has a name.");
        }
        try {
            byte[] bytes = name.getBytes("UTF-8");
            this.mRS.nAssignName(this.mID, bytes);
            this.mName = name;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finalize() throws Throwable {
        if (!this.mDestroyed) {
            if (this.mID != 0 && this.mRS.isAlive()) {
                this.mRS.nObjDestroy(this.mID);
            }
            this.mRS = null;
            this.mID = 0;
            this.mDestroyed = true;
        }
        super.finalize();
    }

    public synchronized void destroy() {
        if (this.mDestroyed) {
            throw new RSInvalidStateException("Object already destroyed.");
        }
        this.mDestroyed = true;
        this.mRS.nObjDestroy(this.mID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFromNative() {
        this.mRS.validate();
        this.mName = this.mRS.nGetName(getID(this.mRS));
    }

    public int hashCode() {
        return this.mID;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseObj b = (BaseObj) obj;
        return this.mID == b.mID;
    }
}