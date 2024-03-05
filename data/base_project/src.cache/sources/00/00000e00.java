package android.security;

import android.content.Context;
import java.security.KeyStore;

/* loaded from: KeyStoreParameter.class */
public final class KeyStoreParameter implements KeyStore.ProtectionParameter {
    private int mFlags;

    private KeyStoreParameter(int flags) {
        this.mFlags = flags;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isEncryptionRequired() {
        return (this.mFlags & 1) != 0;
    }

    /* loaded from: KeyStoreParameter$Builder.class */
    public static final class Builder {
        private int mFlags;

        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
        }

        public Builder setEncryptionRequired(boolean required) {
            if (required) {
                this.mFlags |= 1;
            } else {
                this.mFlags &= -2;
            }
            return this;
        }

        public KeyStoreParameter build() {
            return new KeyStoreParameter(this.mFlags);
        }
    }
}