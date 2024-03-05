package com.android.server;

import android.Manifest;
import android.content.Context;
import android.hardware.ISerialManager;
import android.os.ParcelFileDescriptor;
import com.android.internal.R;
import java.io.File;
import java.util.ArrayList;

/* loaded from: SerialService.class */
public class SerialService extends ISerialManager.Stub {
    private final Context mContext;
    private final String[] mSerialPorts;

    private native ParcelFileDescriptor native_open(String str);

    public SerialService(Context context) {
        this.mContext = context;
        this.mSerialPorts = context.getResources().getStringArray(R.array.config_serialPorts);
    }

    @Override // android.hardware.ISerialManager
    public String[] getSerialPorts() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SERIAL_PORT, null);
        ArrayList<String> ports = new ArrayList<>();
        for (int i = 0; i < this.mSerialPorts.length; i++) {
            String path = this.mSerialPorts[i];
            if (new File(path).exists()) {
                ports.add(path);
            }
        }
        String[] result = new String[ports.size()];
        ports.toArray(result);
        return result;
    }

    @Override // android.hardware.ISerialManager
    public ParcelFileDescriptor openSerialPort(String path) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SERIAL_PORT, null);
        for (int i = 0; i < this.mSerialPorts.length; i++) {
            if (this.mSerialPorts[i].equals(path)) {
                return native_open(path);
            }
        }
        throw new IllegalArgumentException("Invalid serial port " + path);
    }
}