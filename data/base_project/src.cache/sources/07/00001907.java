package com.android.dex;

import com.android.dex.ClassData;
import com.android.dex.Code;
import com.android.dex.util.ByteInput;
import com.android.dex.util.ByteOutput;
import com.android.dex.util.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.zip.Adler32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: Dex.class */
public final class Dex {
    private static final int CHECKSUM_OFFSET = 8;
    private static final int CHECKSUM_SIZE = 4;
    private static final int SIGNATURE_OFFSET = 12;
    private static final int SIGNATURE_SIZE = 20;
    static final short[] EMPTY_SHORT_ARRAY = new short[0];
    private ByteBuffer data;
    private final TableOfContents tableOfContents;
    private int nextSectionStart;
    private final StringTable strings;
    private final TypeIndexToDescriptorIndexTable typeIds;
    private final TypeIndexToDescriptorTable typeNames;
    private final ProtoIdTable protoIds;
    private final FieldIdTable fieldIds;
    private final MethodIdTable methodIds;

    public Dex(byte[] data) throws IOException {
        this(ByteBuffer.wrap(data));
    }

    private Dex(ByteBuffer data) throws IOException {
        this.tableOfContents = new TableOfContents();
        this.nextSectionStart = 0;
        this.strings = new StringTable();
        this.typeIds = new TypeIndexToDescriptorIndexTable();
        this.typeNames = new TypeIndexToDescriptorTable();
        this.protoIds = new ProtoIdTable();
        this.fieldIds = new FieldIdTable();
        this.methodIds = new MethodIdTable();
        this.data = data;
        this.data.order(ByteOrder.LITTLE_ENDIAN);
        this.tableOfContents.readFrom(this);
    }

    public Dex(int byteCount) throws IOException {
        this.tableOfContents = new TableOfContents();
        this.nextSectionStart = 0;
        this.strings = new StringTable();
        this.typeIds = new TypeIndexToDescriptorIndexTable();
        this.typeNames = new TypeIndexToDescriptorTable();
        this.protoIds = new ProtoIdTable();
        this.fieldIds = new FieldIdTable();
        this.methodIds = new MethodIdTable();
        this.data = ByteBuffer.wrap(new byte[byteCount]);
        this.data.order(ByteOrder.LITTLE_ENDIAN);
    }

    public Dex(InputStream in) throws IOException {
        this.tableOfContents = new TableOfContents();
        this.nextSectionStart = 0;
        this.strings = new StringTable();
        this.typeIds = new TypeIndexToDescriptorIndexTable();
        this.typeNames = new TypeIndexToDescriptorTable();
        this.protoIds = new ProtoIdTable();
        this.fieldIds = new FieldIdTable();
        this.methodIds = new MethodIdTable();
        loadFrom(in);
    }

    public Dex(File file) throws IOException {
        this.tableOfContents = new TableOfContents();
        this.nextSectionStart = 0;
        this.strings = new StringTable();
        this.typeIds = new TypeIndexToDescriptorIndexTable();
        this.typeNames = new TypeIndexToDescriptorTable();
        this.protoIds = new ProtoIdTable();
        this.fieldIds = new FieldIdTable();
        this.methodIds = new MethodIdTable();
        if (!FileUtils.hasArchiveSuffix(file.getName())) {
            if (file.getName().endsWith(".dex")) {
                loadFrom(new FileInputStream(file));
                return;
            }
            throw new DexException("unknown output extension: " + file);
        }
        ZipFile zipFile = new ZipFile(file);
        ZipEntry entry = zipFile.getEntry(DexFormat.DEX_IN_JAR_NAME);
        if (entry != null) {
            loadFrom(zipFile.getInputStream(entry));
            zipFile.close();
            return;
        }
        throw new DexException("Expected classes.dex in " + file);
    }

    public static Dex create(ByteBuffer data) throws IOException {
        data.order(ByteOrder.LITTLE_ENDIAN);
        if (data.get(0) == 100 && data.get(1) == 101 && data.get(2) == 121 && data.get(3) == 10) {
            data.position(8);
            int offset = data.getInt();
            int length = data.getInt();
            data.position(offset);
            data.limit(offset + length);
            data = data.slice();
        }
        return new Dex(data);
    }

    private void loadFrom(InputStream in) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        while (true) {
            int count = in.read(buffer);
            if (count != -1) {
                bytesOut.write(buffer, 0, count);
            } else {
                in.close();
                this.data = ByteBuffer.wrap(bytesOut.toByteArray());
                this.data.order(ByteOrder.LITTLE_ENDIAN);
                this.tableOfContents.readFrom(this);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkBounds(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index:" + index + ", length=" + length);
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        ByteBuffer data = this.data.duplicate();
        data.clear();
        while (data.hasRemaining()) {
            int count = Math.min(buffer.length, data.remaining());
            data.get(buffer, 0, count);
            out.write(buffer, 0, count);
        }
    }

    public void writeTo(File dexOut) throws IOException {
        OutputStream out = new FileOutputStream(dexOut);
        writeTo(out);
        out.close();
    }

    public TableOfContents getTableOfContents() {
        return this.tableOfContents;
    }

    public Section open(int position) {
        if (position < 0 || position >= this.data.capacity()) {
            throw new IllegalArgumentException("position=" + position + " length=" + this.data.capacity());
        }
        ByteBuffer sectionData = this.data.duplicate();
        sectionData.order(ByteOrder.LITTLE_ENDIAN);
        sectionData.position(position);
        sectionData.limit(this.data.capacity());
        return new Section("section", sectionData);
    }

    public Section appendSection(int maxByteCount, String name) {
        if ((maxByteCount & 3) != 0) {
            throw new IllegalStateException("Not four byte aligned!");
        }
        int limit = this.nextSectionStart + maxByteCount;
        ByteBuffer sectionData = this.data.duplicate();
        sectionData.order(ByteOrder.LITTLE_ENDIAN);
        sectionData.position(this.nextSectionStart);
        sectionData.limit(limit);
        Section result = new Section(name, sectionData);
        this.nextSectionStart = limit;
        return result;
    }

    public int getLength() {
        return this.data.capacity();
    }

    public int getNextSectionStart() {
        return this.nextSectionStart;
    }

    public byte[] getBytes() {
        ByteBuffer data = this.data.duplicate();
        byte[] result = new byte[data.capacity()];
        data.position(0);
        data.get(result);
        return result;
    }

    public List<String> strings() {
        return this.strings;
    }

    public List<Integer> typeIds() {
        return this.typeIds;
    }

    public List<String> typeNames() {
        return this.typeNames;
    }

    public List<ProtoId> protoIds() {
        return this.protoIds;
    }

    public List<FieldId> fieldIds() {
        return this.fieldIds;
    }

    public List<MethodId> methodIds() {
        return this.methodIds;
    }

    public Iterable<ClassDef> classDefs() {
        return new ClassDefIterable();
    }

    public TypeList readTypeList(int offset) {
        if (offset == 0) {
            return TypeList.EMPTY;
        }
        return open(offset).readTypeList();
    }

    public ClassData readClassData(ClassDef classDef) {
        int offset = classDef.getClassDataOffset();
        if (offset == 0) {
            throw new IllegalArgumentException("offset == 0");
        }
        return open(offset).readClassData();
    }

    public Code readCode(ClassData.Method method) {
        int offset = method.getCodeOffset();
        if (offset == 0) {
            throw new IllegalArgumentException("offset == 0");
        }
        return open(offset).readCode();
    }

    public byte[] computeSignature() throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192];
            ByteBuffer data = this.data.duplicate();
            data.limit(data.capacity());
            data.position(32);
            while (data.hasRemaining()) {
                int count = Math.min(buffer.length, data.remaining());
                data.get(buffer, 0, count);
                digest.update(buffer, 0, count);
            }
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    public int computeChecksum() throws IOException {
        Adler32 adler32 = new Adler32();
        byte[] buffer = new byte[8192];
        ByteBuffer data = this.data.duplicate();
        data.limit(data.capacity());
        data.position(12);
        while (data.hasRemaining()) {
            int count = Math.min(buffer.length, data.remaining());
            data.get(buffer, 0, count);
            adler32.update(buffer, 0, count);
        }
        return (int) adler32.getValue();
    }

    public void writeHashes() throws IOException {
        open(12).write(computeSignature());
        open(8).writeInt(computeChecksum());
    }

    public int nameIndexFromFieldIndex(int fieldIndex) {
        checkBounds(fieldIndex, this.tableOfContents.fieldIds.size);
        int position = this.tableOfContents.fieldIds.off + (8 * fieldIndex);
        return this.data.getInt(position + 2 + 2);
    }

    public int findStringIndex(String s) {
        return Collections.binarySearch(this.strings, s);
    }

    public int findTypeIndex(String descriptor) {
        return Collections.binarySearch(this.typeNames, descriptor);
    }

    public int findFieldIndex(FieldId fieldId) {
        return Collections.binarySearch(this.fieldIds, fieldId);
    }

    public int findMethodIndex(MethodId methodId) {
        return Collections.binarySearch(this.methodIds, methodId);
    }

    public int findClassDefIndexFromTypeIndex(int typeIndex) {
        checkBounds(typeIndex, this.tableOfContents.typeIds.size);
        if (!this.tableOfContents.classDefs.exists()) {
            return -1;
        }
        for (int i = 0; i < this.tableOfContents.classDefs.size; i++) {
            if (typeIndexFromClassDefIndex(i) == typeIndex) {
                return i;
            }
        }
        return -1;
    }

    public int typeIndexFromFieldIndex(int fieldIndex) {
        checkBounds(fieldIndex, this.tableOfContents.fieldIds.size);
        int position = this.tableOfContents.fieldIds.off + (8 * fieldIndex);
        return this.data.getShort(position + 2) & 65535;
    }

    public int declaringClassIndexFromMethodIndex(int methodIndex) {
        checkBounds(methodIndex, this.tableOfContents.methodIds.size);
        int position = this.tableOfContents.methodIds.off + (8 * methodIndex);
        return this.data.getShort(position) & 65535;
    }

    public int nameIndexFromMethodIndex(int methodIndex) {
        checkBounds(methodIndex, this.tableOfContents.methodIds.size);
        int position = this.tableOfContents.methodIds.off + (8 * methodIndex);
        return this.data.getInt(position + 2 + 2);
    }

    public short[] parameterTypeIndicesFromMethodIndex(int methodIndex) {
        checkBounds(methodIndex, this.tableOfContents.methodIds.size);
        int position = this.tableOfContents.methodIds.off + (8 * methodIndex);
        int protoIndex = this.data.getShort(position + 2) & 65535;
        checkBounds(protoIndex, this.tableOfContents.protoIds.size);
        int position2 = this.tableOfContents.protoIds.off + (12 * protoIndex);
        int parametersOffset = this.data.getInt(position2 + 4 + 4);
        if (parametersOffset == 0) {
            return EMPTY_SHORT_ARRAY;
        }
        int size = this.data.getInt(parametersOffset);
        if (size <= 0) {
            throw new AssertionError("Unexpected parameter type list size: " + size);
        }
        int position3 = parametersOffset + 4;
        short[] types = new short[size];
        for (int i = 0; i < size; i++) {
            types[i] = this.data.getShort(position3);
            position3 += 2;
        }
        return types;
    }

    public int returnTypeIndexFromMethodIndex(int methodIndex) {
        checkBounds(methodIndex, this.tableOfContents.methodIds.size);
        int position = this.tableOfContents.methodIds.off + (8 * methodIndex);
        int protoIndex = this.data.getShort(position + 2) & 65535;
        checkBounds(protoIndex, this.tableOfContents.protoIds.size);
        int position2 = this.tableOfContents.protoIds.off + (12 * protoIndex);
        return this.data.getInt(position2 + 4);
    }

    public int descriptorIndexFromTypeIndex(int typeIndex) {
        checkBounds(typeIndex, this.tableOfContents.typeIds.size);
        int position = this.tableOfContents.typeIds.off + (4 * typeIndex);
        return this.data.getInt(position);
    }

    public int typeIndexFromClassDefIndex(int classDefIndex) {
        checkBounds(classDefIndex, this.tableOfContents.classDefs.size);
        int position = this.tableOfContents.classDefs.off + (32 * classDefIndex);
        return this.data.getInt(position);
    }

    public int annotationDirectoryOffsetFromClassDefIndex(int classDefIndex) {
        checkBounds(classDefIndex, this.tableOfContents.classDefs.size);
        int position = this.tableOfContents.classDefs.off + (32 * classDefIndex);
        return this.data.getInt(position + 4 + 4 + 4 + 4 + 4);
    }

    public short[] interfaceTypeIndicesFromClassDefIndex(int classDefIndex) {
        checkBounds(classDefIndex, this.tableOfContents.classDefs.size);
        int position = this.tableOfContents.classDefs.off + (32 * classDefIndex);
        int interfacesOffset = this.data.getInt(position + 4 + 4 + 4);
        if (interfacesOffset == 0) {
            return EMPTY_SHORT_ARRAY;
        }
        int size = this.data.getInt(interfacesOffset);
        if (size <= 0) {
            throw new AssertionError("Unexpected interfaces list size: " + size);
        }
        int position2 = interfacesOffset + 4;
        short[] types = new short[size];
        for (int i = 0; i < size; i++) {
            types[i] = this.data.getShort(position2);
            position2 += 2;
        }
        return types;
    }

    /* loaded from: Dex$Section.class */
    public final class Section implements ByteInput, ByteOutput {
        private final String name;
        private final ByteBuffer data;
        private final int initialPosition;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.dex.Dex.Section.readString():java.lang.String, file: Dex$Section.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        public java.lang.String readString() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.dex.Dex.Section.readString():java.lang.String, file: Dex$Section.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.dex.Dex.Section.readString():java.lang.String");
        }

        private Section(String name, ByteBuffer data) {
            this.name = name;
            this.data = data;
            this.initialPosition = data.position();
        }

        public int getPosition() {
            return this.data.position();
        }

        public int readInt() {
            return this.data.getInt();
        }

        public short readShort() {
            return this.data.getShort();
        }

        public int readUnsignedShort() {
            return readShort() & 65535;
        }

        @Override // com.android.dex.util.ByteInput
        public byte readByte() {
            return this.data.get();
        }

        public byte[] readByteArray(int length) {
            byte[] result = new byte[length];
            this.data.get(result);
            return result;
        }

        public short[] readShortArray(int length) {
            if (length == 0) {
                return Dex.EMPTY_SHORT_ARRAY;
            }
            short[] result = new short[length];
            for (int i = 0; i < length; i++) {
                result[i] = readShort();
            }
            return result;
        }

        public int readUleb128() {
            return Leb128.readUnsignedLeb128(this);
        }

        public int readUleb128p1() {
            return Leb128.readUnsignedLeb128(this) - 1;
        }

        public int readSleb128() {
            return Leb128.readSignedLeb128(this);
        }

        public void writeUleb128p1(int i) {
            writeUleb128(i + 1);
        }

        public TypeList readTypeList() {
            int size = readInt();
            short[] types = readShortArray(size);
            alignToFourBytes();
            return new TypeList(Dex.this, types);
        }

        public FieldId readFieldId() {
            int declaringClassIndex = readUnsignedShort();
            int typeIndex = readUnsignedShort();
            int nameIndex = readInt();
            return new FieldId(Dex.this, declaringClassIndex, typeIndex, nameIndex);
        }

        public MethodId readMethodId() {
            int declaringClassIndex = readUnsignedShort();
            int protoIndex = readUnsignedShort();
            int nameIndex = readInt();
            return new MethodId(Dex.this, declaringClassIndex, protoIndex, nameIndex);
        }

        public ProtoId readProtoId() {
            int shortyIndex = readInt();
            int returnTypeIndex = readInt();
            int parametersOffset = readInt();
            return new ProtoId(Dex.this, shortyIndex, returnTypeIndex, parametersOffset);
        }

        public ClassDef readClassDef() {
            int offset = getPosition();
            int type = readInt();
            int accessFlags = readInt();
            int supertype = readInt();
            int interfacesOffset = readInt();
            int sourceFileIndex = readInt();
            int annotationsOffset = readInt();
            int classDataOffset = readInt();
            int staticValuesOffset = readInt();
            return new ClassDef(Dex.this, offset, type, accessFlags, supertype, interfacesOffset, sourceFileIndex, annotationsOffset, classDataOffset, staticValuesOffset);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Code readCode() {
            Code.Try[] tries;
            Code.CatchHandler[] catchHandlers;
            int registersSize = readUnsignedShort();
            int insSize = readUnsignedShort();
            int outsSize = readUnsignedShort();
            int triesSize = readUnsignedShort();
            int debugInfoOffset = readInt();
            int instructionsSize = readInt();
            short[] instructions = readShortArray(instructionsSize);
            if (triesSize > 0) {
                if (instructions.length % 2 == 1) {
                    readShort();
                }
                Section triesSection = Dex.this.open(this.data.position());
                skip(triesSize * 8);
                catchHandlers = readCatchHandlers();
                tries = triesSection.readTries(triesSize, catchHandlers);
            } else {
                tries = new Code.Try[0];
                catchHandlers = new Code.CatchHandler[0];
            }
            return new Code(registersSize, insSize, outsSize, debugInfoOffset, instructions, tries, catchHandlers);
        }

        private Code.CatchHandler[] readCatchHandlers() {
            int baseOffset = this.data.position();
            int catchHandlersSize = readUleb128();
            Code.CatchHandler[] result = new Code.CatchHandler[catchHandlersSize];
            for (int i = 0; i < catchHandlersSize; i++) {
                int offset = this.data.position() - baseOffset;
                result[i] = readCatchHandler(offset);
            }
            return result;
        }

        private Code.Try[] readTries(int triesSize, Code.CatchHandler[] catchHandlers) {
            Code.Try[] result = new Code.Try[triesSize];
            for (int i = 0; i < triesSize; i++) {
                int startAddress = readInt();
                int instructionCount = readUnsignedShort();
                int handlerOffset = readUnsignedShort();
                int catchHandlerIndex = findCatchHandlerIndex(catchHandlers, handlerOffset);
                result[i] = new Code.Try(startAddress, instructionCount, catchHandlerIndex);
            }
            return result;
        }

        private int findCatchHandlerIndex(Code.CatchHandler[] catchHandlers, int offset) {
            for (int i = 0; i < catchHandlers.length; i++) {
                Code.CatchHandler catchHandler = catchHandlers[i];
                if (catchHandler.getOffset() == offset) {
                    return i;
                }
            }
            throw new IllegalArgumentException();
        }

        private Code.CatchHandler readCatchHandler(int offset) {
            int size = readSleb128();
            int handlersCount = Math.abs(size);
            int[] typeIndexes = new int[handlersCount];
            int[] addresses = new int[handlersCount];
            for (int i = 0; i < handlersCount; i++) {
                typeIndexes[i] = readUleb128();
                addresses[i] = readUleb128();
            }
            int catchAllAddress = size <= 0 ? readUleb128() : -1;
            return new Code.CatchHandler(typeIndexes, addresses, catchAllAddress, offset);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ClassData readClassData() {
            int staticFieldsSize = readUleb128();
            int instanceFieldsSize = readUleb128();
            int directMethodsSize = readUleb128();
            int virtualMethodsSize = readUleb128();
            ClassData.Field[] staticFields = readFields(staticFieldsSize);
            ClassData.Field[] instanceFields = readFields(instanceFieldsSize);
            ClassData.Method[] directMethods = readMethods(directMethodsSize);
            ClassData.Method[] virtualMethods = readMethods(virtualMethodsSize);
            return new ClassData(staticFields, instanceFields, directMethods, virtualMethods);
        }

        private ClassData.Field[] readFields(int count) {
            ClassData.Field[] result = new ClassData.Field[count];
            int fieldIndex = 0;
            for (int i = 0; i < count; i++) {
                fieldIndex += readUleb128();
                int accessFlags = readUleb128();
                result[i] = new ClassData.Field(fieldIndex, accessFlags);
            }
            return result;
        }

        private ClassData.Method[] readMethods(int count) {
            ClassData.Method[] result = new ClassData.Method[count];
            int methodIndex = 0;
            for (int i = 0; i < count; i++) {
                methodIndex += readUleb128();
                int accessFlags = readUleb128();
                int codeOff = readUleb128();
                result[i] = new ClassData.Method(methodIndex, accessFlags, codeOff);
            }
            return result;
        }

        private byte[] getBytesFrom(int start) {
            int end = this.data.position();
            byte[] result = new byte[end - start];
            this.data.position(start);
            this.data.get(result);
            return result;
        }

        public Annotation readAnnotation() {
            byte visibility = readByte();
            int start = this.data.position();
            new EncodedValueReader(this, 29).skipValue();
            return new Annotation(Dex.this, visibility, new EncodedValue(getBytesFrom(start)));
        }

        public EncodedValue readEncodedArray() {
            int start = this.data.position();
            new EncodedValueReader(this, 28).skipValue();
            return new EncodedValue(getBytesFrom(start));
        }

        public void skip(int count) {
            if (count < 0) {
                throw new IllegalArgumentException();
            }
            this.data.position(this.data.position() + count);
        }

        public void alignToFourBytes() {
            this.data.position((this.data.position() + 3) & (-4));
        }

        public void alignToFourBytesWithZeroFill() {
            while ((this.data.position() & 3) != 0) {
                this.data.put((byte) 0);
            }
        }

        public void assertFourByteAligned() {
            if ((this.data.position() & 3) != 0) {
                throw new IllegalStateException("Not four byte aligned!");
            }
        }

        public void write(byte[] bytes) {
            this.data.put(bytes);
        }

        @Override // com.android.dex.util.ByteOutput
        public void writeByte(int b) {
            this.data.put((byte) b);
        }

        public void writeShort(short i) {
            this.data.putShort(i);
        }

        public void writeUnsignedShort(int i) {
            short s = (short) i;
            if (i != (s & 65535)) {
                throw new IllegalArgumentException("Expected an unsigned short: " + i);
            }
            writeShort(s);
        }

        public void write(short[] shorts) {
            for (short s : shorts) {
                writeShort(s);
            }
        }

        public void writeInt(int i) {
            this.data.putInt(i);
        }

        public void writeUleb128(int i) {
            try {
                Leb128.writeUnsignedLeb128(this, i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new DexException("Section limit " + this.data.limit() + " exceeded by " + this.name);
            }
        }

        public void writeSleb128(int i) {
            try {
                Leb128.writeSignedLeb128(this, i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new DexException("Section limit " + this.data.limit() + " exceeded by " + this.name);
            }
        }

        public void writeStringData(String value) {
            try {
                int length = value.length();
                writeUleb128(length);
                write(Mutf8.encode(value));
                writeByte(0);
            } catch (UTFDataFormatException e) {
                throw new AssertionError();
            }
        }

        public void writeTypeList(TypeList typeList) {
            short[] types = typeList.getTypes();
            writeInt(types.length);
            for (short type : types) {
                writeShort(type);
            }
            alignToFourBytesWithZeroFill();
        }

        public int remaining() {
            return this.data.remaining();
        }

        public int used() {
            return this.data.position() - this.initialPosition;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Dex$StringTable.class */
    public final class StringTable extends AbstractList<String> implements RandomAccess {
        private StringTable() {
        }

        @Override // java.util.AbstractList, java.util.List
        public String get(int index) {
            Dex.checkBounds(index, Dex.this.tableOfContents.stringIds.size);
            return Dex.this.open(Dex.this.tableOfContents.stringIds.off + (index * 4)).readString();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Dex.this.tableOfContents.stringIds.size;
        }
    }

    /* loaded from: Dex$TypeIndexToDescriptorIndexTable.class */
    private final class TypeIndexToDescriptorIndexTable extends AbstractList<Integer> implements RandomAccess {
        private TypeIndexToDescriptorIndexTable() {
        }

        @Override // java.util.AbstractList, java.util.List
        public Integer get(int index) {
            return Integer.valueOf(Dex.this.descriptorIndexFromTypeIndex(index));
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Dex.this.tableOfContents.typeIds.size;
        }
    }

    /* loaded from: Dex$TypeIndexToDescriptorTable.class */
    private final class TypeIndexToDescriptorTable extends AbstractList<String> implements RandomAccess {
        private TypeIndexToDescriptorTable() {
        }

        @Override // java.util.AbstractList, java.util.List
        public String get(int index) {
            return Dex.this.strings.get(Dex.this.descriptorIndexFromTypeIndex(index));
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Dex.this.tableOfContents.typeIds.size;
        }
    }

    /* loaded from: Dex$ProtoIdTable.class */
    private final class ProtoIdTable extends AbstractList<ProtoId> implements RandomAccess {
        private ProtoIdTable() {
        }

        @Override // java.util.AbstractList, java.util.List
        public ProtoId get(int index) {
            Dex.checkBounds(index, Dex.this.tableOfContents.protoIds.size);
            return Dex.this.open(Dex.this.tableOfContents.protoIds.off + (12 * index)).readProtoId();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Dex.this.tableOfContents.protoIds.size;
        }
    }

    /* loaded from: Dex$FieldIdTable.class */
    private final class FieldIdTable extends AbstractList<FieldId> implements RandomAccess {
        private FieldIdTable() {
        }

        @Override // java.util.AbstractList, java.util.List
        public FieldId get(int index) {
            Dex.checkBounds(index, Dex.this.tableOfContents.fieldIds.size);
            return Dex.this.open(Dex.this.tableOfContents.fieldIds.off + (8 * index)).readFieldId();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Dex.this.tableOfContents.fieldIds.size;
        }
    }

    /* loaded from: Dex$MethodIdTable.class */
    private final class MethodIdTable extends AbstractList<MethodId> implements RandomAccess {
        private MethodIdTable() {
        }

        @Override // java.util.AbstractList, java.util.List
        public MethodId get(int index) {
            Dex.checkBounds(index, Dex.this.tableOfContents.methodIds.size);
            return Dex.this.open(Dex.this.tableOfContents.methodIds.off + (8 * index)).readMethodId();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Dex.this.tableOfContents.methodIds.size;
        }
    }

    /* loaded from: Dex$ClassDefIterator.class */
    private final class ClassDefIterator implements Iterator<ClassDef> {
        private final Section in;
        private int count;

        private ClassDefIterator() {
            this.in = Dex.this.open(Dex.this.tableOfContents.classDefs.off);
            this.count = 0;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.count < Dex.this.tableOfContents.classDefs.size;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ClassDef next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.count++;
            return this.in.readClassDef();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: Dex$ClassDefIterable.class */
    private final class ClassDefIterable implements Iterable<ClassDef> {
        private ClassDefIterable() {
        }

        @Override // java.lang.Iterable
        public Iterator<ClassDef> iterator() {
            return !Dex.this.tableOfContents.classDefs.exists() ? Collections.emptySet().iterator() : new ClassDefIterator();
        }
    }
}