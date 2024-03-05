package com.android.server.net;

import android.net.NetworkIdentity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: NetworkIdentitySet.class */
public class NetworkIdentitySet extends HashSet<NetworkIdentity> {
    private static final int VERSION_INIT = 1;
    private static final int VERSION_ADD_ROAMING = 2;
    private static final int VERSION_ADD_NETWORK_ID = 3;

    public NetworkIdentitySet() {
    }

    public NetworkIdentitySet(DataInputStream in) throws IOException {
        String networkId;
        boolean z;
        int version = in.readInt();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            if (version <= 1) {
                in.readInt();
            }
            int type = in.readInt();
            int subType = in.readInt();
            String subscriberId = readOptionalString(in);
            if (version >= 3) {
                networkId = readOptionalString(in);
            } else {
                networkId = null;
            }
            if (version >= 2) {
                z = in.readBoolean();
            } else {
                z = false;
            }
            add(new NetworkIdentity(type, subType, subscriberId, networkId, false));
        }
    }

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeInt(3);
        out.writeInt(size());
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            NetworkIdentity ident = i$.next();
            out.writeInt(ident.getType());
            out.writeInt(ident.getSubType());
            writeOptionalString(out, ident.getSubscriberId());
            writeOptionalString(out, ident.getNetworkId());
            out.writeBoolean(ident.getRoaming());
        }
    }

    private static void writeOptionalString(DataOutputStream out, String value) throws IOException {
        if (value != null) {
            out.writeByte(1);
            out.writeUTF(value);
            return;
        }
        out.writeByte(0);
    }

    private static String readOptionalString(DataInputStream in) throws IOException {
        if (in.readByte() != 0) {
            return in.readUTF();
        }
        return null;
    }
}