package gov.nist.javax.sip.parser.ims;

import gov.nist.javax.sip.header.ims.PChargingFunctionAddresses;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.ParametersParser;
import gov.nist.javax.sip.parser.TokenTypes;
import java.text.ParseException;

/* loaded from: PChargingFunctionAddressesParser.class */
public class PChargingFunctionAddressesParser extends ParametersParser implements TokenTypes {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PChargingFunctionAddressesParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PChargingFunctionAddressesParser.class
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
    @Override // gov.nist.javax.sip.parser.HeaderParser
    public gov.nist.javax.sip.header.SIPHeader parse() throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PChargingFunctionAddressesParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PChargingFunctionAddressesParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.PChargingFunctionAddressesParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PChargingFunctionAddressesParser.parseParameter(gov.nist.javax.sip.header.ims.PChargingFunctionAddresses):void, file: PChargingFunctionAddressesParser.class
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
    protected void parseParameter(gov.nist.javax.sip.header.ims.PChargingFunctionAddresses r1) throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PChargingFunctionAddressesParser.parseParameter(gov.nist.javax.sip.header.ims.PChargingFunctionAddresses):void, file: PChargingFunctionAddressesParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.PChargingFunctionAddressesParser.parseParameter(gov.nist.javax.sip.header.ims.PChargingFunctionAddresses):void");
    }

    public PChargingFunctionAddressesParser(String charging) {
        super(charging);
    }

    protected PChargingFunctionAddressesParser(Lexer lexer) {
        super(lexer);
    }

    public static void main(String[] args) throws ParseException {
        String[] r = {"P-Charging-Function-Addresses: ccf=\"test str\"; ecf=token\n", "P-Charging-Function-Addresses: ccf=192.1.1.1; ccf=192.1.1.2; ecf=192.1.1.3; ecf=192.1.1.4\n", "P-Charging-Function-Addresses: ccf=[5555::b99:c88:d77:e66]; ccf=[5555::a55:b44:c33:d22]; ecf=[5555::1ff:2ee:3dd:4cc]; ecf=[5555::6aa:7bb:8cc:9dd]\n"};
        for (int i = 0; i < r.length; i++) {
            PChargingFunctionAddressesParser parser = new PChargingFunctionAddressesParser(r[i]);
            System.out.println("original = " + r[i]);
            PChargingFunctionAddresses chargAddr = (PChargingFunctionAddresses) parser.parse();
            System.out.println("encoded = " + chargAddr.encode());
        }
    }
}