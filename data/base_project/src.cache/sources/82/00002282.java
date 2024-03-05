package java.io;

import gov.nist.core.Separators;

/* loaded from: EmulatedFields.class */
class EmulatedFields {
    private ObjectSlot[] slotsToSerialize;
    private ObjectStreamField[] declaredFields;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: EmulatedFields$ObjectSlot.class */
    public static class ObjectSlot {
        ObjectStreamField field;
        Object fieldValue;
        boolean defaulted = true;

        ObjectSlot() {
        }

        public ObjectStreamField getField() {
            return this.field;
        }

        public Object getFieldValue() {
            return this.fieldValue;
        }
    }

    public EmulatedFields(ObjectStreamField[] fields, ObjectStreamField[] declared) {
        buildSlots(fields);
        this.declaredFields = declared;
    }

    private void buildSlots(ObjectStreamField[] fields) {
        this.slotsToSerialize = new ObjectSlot[fields.length];
        for (int i = 0; i < fields.length; i++) {
            ObjectSlot s = new ObjectSlot();
            this.slotsToSerialize[i] = s;
            s.field = fields[i];
        }
    }

    public boolean defaulted(String name) throws IllegalArgumentException {
        ObjectSlot slot = findSlot(name, null);
        if (slot == null) {
            throw new IllegalArgumentException("no field '" + name + Separators.QUOTE);
        }
        return slot.defaulted;
    }

    private ObjectSlot findSlot(String fieldName, Class<?> fieldType) {
        boolean isPrimitive = fieldType != null && fieldType.isPrimitive();
        for (int i = 0; i < this.slotsToSerialize.length; i++) {
            ObjectSlot slot = this.slotsToSerialize[i];
            if (slot.field.getName().equals(fieldName)) {
                if (isPrimitive) {
                    if (slot.field.getType() == fieldType) {
                        return slot;
                    }
                } else if (fieldType == null) {
                    return slot;
                } else {
                    if (slot.field.getType().isAssignableFrom(fieldType)) {
                        return slot;
                    }
                }
            }
        }
        if (this.declaredFields != null) {
            for (int i2 = 0; i2 < this.declaredFields.length; i2++) {
                ObjectStreamField field = this.declaredFields[i2];
                if (field.getName().equals(fieldName)) {
                    if (!isPrimitive) {
                        if (fieldType != null && !field.getType().isAssignableFrom(fieldType)) {
                        }
                        ObjectSlot slot2 = new ObjectSlot();
                        slot2.field = field;
                        slot2.defaulted = true;
                        return slot2;
                    } else if (fieldType == field.getType()) {
                        ObjectSlot slot22 = new ObjectSlot();
                        slot22.field = field;
                        slot22.defaulted = true;
                        return slot22;
                    }
                }
            }
            return null;
        }
        return null;
    }

    private ObjectSlot findMandatorySlot(String name, Class<?> type) {
        ObjectSlot slot = findSlot(name, type);
        if (slot == null || (type == null && slot.field.getType().isPrimitive())) {
            throw new IllegalArgumentException("no field '" + name + "' of type " + type);
        }
        return slot;
    }

    public byte get(String name, byte defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Byte.TYPE);
        return slot.defaulted ? defaultValue : ((Byte) slot.fieldValue).byteValue();
    }

    public char get(String name, char defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Character.TYPE);
        return slot.defaulted ? defaultValue : ((Character) slot.fieldValue).charValue();
    }

    public double get(String name, double defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Double.TYPE);
        return slot.defaulted ? defaultValue : ((Double) slot.fieldValue).doubleValue();
    }

    public float get(String name, float defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Float.TYPE);
        return slot.defaulted ? defaultValue : ((Float) slot.fieldValue).floatValue();
    }

    public int get(String name, int defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Integer.TYPE);
        return slot.defaulted ? defaultValue : ((Integer) slot.fieldValue).intValue();
    }

    public long get(String name, long defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Long.TYPE);
        return slot.defaulted ? defaultValue : ((Long) slot.fieldValue).longValue();
    }

    public Object get(String name, Object defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, null);
        return slot.defaulted ? defaultValue : slot.fieldValue;
    }

    public short get(String name, short defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Short.TYPE);
        return slot.defaulted ? defaultValue : ((Short) slot.fieldValue).shortValue();
    }

    public boolean get(String name, boolean defaultValue) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Boolean.TYPE);
        return slot.defaulted ? defaultValue : ((Boolean) slot.fieldValue).booleanValue();
    }

    public void put(String name, byte value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Byte.TYPE);
        slot.fieldValue = Byte.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, char value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Character.TYPE);
        slot.fieldValue = Character.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, double value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Double.TYPE);
        slot.fieldValue = Double.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, float value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Float.TYPE);
        slot.fieldValue = Float.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, int value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Integer.TYPE);
        slot.fieldValue = Integer.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, long value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Long.TYPE);
        slot.fieldValue = Long.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, Object value) throws IllegalArgumentException {
        Class<?> valueClass = null;
        if (value != null) {
            valueClass = value.getClass();
        }
        ObjectSlot slot = findMandatorySlot(name, valueClass);
        slot.fieldValue = value;
        slot.defaulted = false;
    }

    public void put(String name, short value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Short.TYPE);
        slot.fieldValue = Short.valueOf(value);
        slot.defaulted = false;
    }

    public void put(String name, boolean value) throws IllegalArgumentException {
        ObjectSlot slot = findMandatorySlot(name, Boolean.TYPE);
        slot.fieldValue = Boolean.valueOf(value);
        slot.defaulted = false;
    }

    public ObjectSlot[] slots() {
        return this.slotsToSerialize;
    }
}