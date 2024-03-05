package android.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* loaded from: BluetoothGattService.class */
public class BluetoothGattService {
    public static final int SERVICE_TYPE_PRIMARY = 0;
    public static final int SERVICE_TYPE_SECONDARY = 1;
    protected BluetoothDevice mDevice;
    protected UUID mUuid;
    protected int mInstanceId;
    protected int mHandles;
    protected int mServiceType;
    protected List<BluetoothGattCharacteristic> mCharacteristics;
    protected List<BluetoothGattService> mIncludedServices;

    public BluetoothGattService(UUID uuid, int serviceType) {
        this.mHandles = 0;
        this.mDevice = null;
        this.mUuid = uuid;
        this.mInstanceId = 0;
        this.mServiceType = serviceType;
        this.mCharacteristics = new ArrayList();
        this.mIncludedServices = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothGattService(BluetoothDevice device, UUID uuid, int instanceId, int serviceType) {
        this.mHandles = 0;
        this.mDevice = device;
        this.mUuid = uuid;
        this.mInstanceId = instanceId;
        this.mServiceType = serviceType;
        this.mCharacteristics = new ArrayList();
        this.mIncludedServices = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public boolean addService(BluetoothGattService service) {
        this.mIncludedServices.add(service);
        return true;
    }

    public boolean addCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.mCharacteristics.add(characteristic);
        characteristic.setService(this);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothGattCharacteristic getCharacteristic(UUID uuid, int instanceId) {
        for (BluetoothGattCharacteristic characteristic : this.mCharacteristics) {
            if (uuid.equals(characteristic.getUuid()) && characteristic.getInstanceId() == instanceId) {
                return characteristic;
            }
        }
        return null;
    }

    public void setInstanceId(int instanceId) {
        this.mInstanceId = instanceId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHandles() {
        return this.mHandles;
    }

    public void setHandles(int handles) {
        this.mHandles = handles;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addIncludedService(BluetoothGattService includedService) {
        this.mIncludedServices.add(includedService);
    }

    public UUID getUuid() {
        return this.mUuid;
    }

    public int getInstanceId() {
        return this.mInstanceId;
    }

    public int getType() {
        return this.mServiceType;
    }

    public List<BluetoothGattService> getIncludedServices() {
        return this.mIncludedServices;
    }

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return this.mCharacteristics;
    }

    public BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        for (BluetoothGattCharacteristic characteristic : this.mCharacteristics) {
            if (uuid.equals(characteristic.getUuid())) {
                return characteristic;
            }
        }
        return null;
    }
}