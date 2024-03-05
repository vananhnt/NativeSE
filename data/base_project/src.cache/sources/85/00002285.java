package java.io;

import java.io.ObjectInputStream;

/* loaded from: EmulatedFieldsForLoading.class */
class EmulatedFieldsForLoading extends ObjectInputStream.GetField {
    private ObjectStreamClass streamClass;
    private EmulatedFields emulatedFields;

    EmulatedFieldsForLoading(ObjectStreamClass streamClass) {
        this.streamClass = streamClass;
        this.emulatedFields = new EmulatedFields(streamClass.getLoadFields(), streamClass.fields());
    }

    @Override // java.io.ObjectInputStream.GetField
    public boolean defaulted(String name) throws IOException, IllegalArgumentException {
        return this.emulatedFields.defaulted(name);
    }

    EmulatedFields emulatedFields() {
        return this.emulatedFields;
    }

    @Override // java.io.ObjectInputStream.GetField
    public byte get(String name, byte defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public char get(String name, char defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public double get(String name, double defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public float get(String name, float defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public int get(String name, int defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public long get(String name, long defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public Object get(String name, Object defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public short get(String name, short defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public boolean get(String name, boolean defaultValue) throws IOException, IllegalArgumentException {
        return this.emulatedFields.get(name, defaultValue);
    }

    @Override // java.io.ObjectInputStream.GetField
    public ObjectStreamClass getObjectStreamClass() {
        return this.streamClass;
    }
}