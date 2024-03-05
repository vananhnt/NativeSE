package android.support.v4.app;

import android.os.Bundle;

/* loaded from: RemoteInputCompatBase.class */
class RemoteInputCompatBase {

    /* loaded from: RemoteInputCompatBase$RemoteInput.class */
    public static abstract class RemoteInput {

        /* loaded from: RemoteInputCompatBase$RemoteInput$Factory.class */
        public interface Factory {
            RemoteInput build(String str, CharSequence charSequence, CharSequence[] charSequenceArr, boolean z, Bundle bundle);

            RemoteInput[] newArray(int i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract boolean getAllowFreeFormInput();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract CharSequence[] getChoices();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract Bundle getExtras();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract CharSequence getLabel();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract String getResultKey();
    }

    RemoteInputCompatBase() {
    }
}