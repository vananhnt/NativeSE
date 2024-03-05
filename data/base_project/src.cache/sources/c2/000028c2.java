package libcore.icu;

import java.util.Locale;

/* loaded from: AlphabeticIndex.class */
public final class AlphabeticIndex {
    private long peer;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.AlphabeticIndex.finalize():void, file: AlphabeticIndex.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    protected synchronized void finalize() throws java.lang.Throwable {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.AlphabeticIndex.finalize():void, file: AlphabeticIndex.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.icu.AlphabeticIndex.finalize():void");
    }

    private static native long create(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void destroy(long j);

    private static native int getMaxLabelCount(long j);

    private static native void setMaxLabelCount(long j, int i);

    private static native void addLabels(long j, String str);

    private static native void addLabelRange(long j, int i, int i2);

    private static native int getBucketCount(long j);

    private static native int getBucketIndex(long j, String str);

    private static native String getBucketLabel(long j, int i);

    private static native long buildImmutableIndex(long j);

    static /* synthetic */ void access$000(long x0) {
        destroy(x0);
    }

    /* loaded from: AlphabeticIndex$ImmutableIndex.class */
    public static final class ImmutableIndex {
        private long peer;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.AlphabeticIndex.ImmutableIndex.finalize():void, file: AlphabeticIndex$ImmutableIndex.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        protected synchronized void finalize() throws java.lang.Throwable {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.AlphabeticIndex.ImmutableIndex.finalize():void, file: AlphabeticIndex$ImmutableIndex.class
            */
            throw new UnsupportedOperationException("Method not decompiled: libcore.icu.AlphabeticIndex.ImmutableIndex.finalize():void");
        }

        private static native int getBucketCount(long j);

        private static native int getBucketIndex(long j, String str);

        private static native String getBucketLabel(long j, int i);

        private ImmutableIndex(long peer) {
            this.peer = peer;
        }

        public int getBucketCount() {
            return getBucketCount(this.peer);
        }

        public int getBucketIndex(String s) {
            return getBucketIndex(this.peer, s);
        }

        public String getBucketLabel(int index) {
            return getBucketLabel(this.peer, index);
        }
    }

    public AlphabeticIndex(Locale locale) {
        this.peer = create(locale.toString());
    }

    public synchronized int getMaxLabelCount() {
        return getMaxLabelCount(this.peer);
    }

    public synchronized AlphabeticIndex setMaxLabelCount(int count) {
        setMaxLabelCount(this.peer, count);
        return this;
    }

    public synchronized AlphabeticIndex addLabels(Locale locale) {
        addLabels(this.peer, locale.toString());
        return this;
    }

    public synchronized AlphabeticIndex addLabelRange(int codePointStart, int codePointEnd) {
        addLabelRange(this.peer, codePointStart, codePointEnd);
        return this;
    }

    public synchronized int getBucketCount() {
        return getBucketCount(this.peer);
    }

    public synchronized int getBucketIndex(String s) {
        return getBucketIndex(this.peer, s);
    }

    public synchronized String getBucketLabel(int index) {
        return getBucketLabel(this.peer, index);
    }

    public synchronized ImmutableIndex getImmutableIndex() {
        return new ImmutableIndex(buildImmutableIndex(this.peer));
    }
}