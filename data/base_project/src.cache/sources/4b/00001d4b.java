package com.android.server.accounts;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.XmlSerializerAndParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: AccountAuthenticatorCache.class */
class AccountAuthenticatorCache extends RegisteredServicesCache<AuthenticatorDescription> implements IAccountAuthenticatorCache {
    private static final String TAG = "Account";
    private static final MySerializer sSerializer = new MySerializer();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountAuthenticatorCache.parseServiceAttributes(android.content.res.Resources, java.lang.String, android.util.AttributeSet):android.accounts.AuthenticatorDescription, file: AccountAuthenticatorCache.class
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
    @Override // android.content.pm.RegisteredServicesCache
    public android.accounts.AuthenticatorDescription parseServiceAttributes(android.content.res.Resources r1, java.lang.String r2, android.util.AttributeSet r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accounts.AccountAuthenticatorCache.parseServiceAttributes(android.content.res.Resources, java.lang.String, android.util.AttributeSet):android.accounts.AuthenticatorDescription, file: AccountAuthenticatorCache.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountAuthenticatorCache.parseServiceAttributes(android.content.res.Resources, java.lang.String, android.util.AttributeSet):android.accounts.AuthenticatorDescription");
    }

    @Override // com.android.server.accounts.IAccountAuthenticatorCache
    public /* bridge */ /* synthetic */ RegisteredServicesCache.ServiceInfo getServiceInfo(AuthenticatorDescription x0, int x1) {
        return super.getServiceInfo((AccountAuthenticatorCache) x0, x1);
    }

    public AccountAuthenticatorCache(Context context) {
        super(context, "android.accounts.AccountAuthenticator", "android.accounts.AccountAuthenticator", AccountManager.AUTHENTICATOR_ATTRIBUTES_NAME, sSerializer);
    }

    /* loaded from: AccountAuthenticatorCache$MySerializer.class */
    private static class MySerializer implements XmlSerializerAndParser<AuthenticatorDescription> {
        private MySerializer() {
        }

        @Override // android.content.pm.XmlSerializerAndParser
        public void writeAsXml(AuthenticatorDescription item, XmlSerializer out) throws IOException {
            out.attribute(null, "type", item.type);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.content.pm.XmlSerializerAndParser
        public AuthenticatorDescription createFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
            return AuthenticatorDescription.newKey(parser.getAttributeValue(null, "type"));
        }
    }
}