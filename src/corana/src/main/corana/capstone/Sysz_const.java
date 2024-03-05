// For Capstone Engine. AUTO-GENERATED FILE, DO NOT EDIT
package main.corana.capstone;

public class Sysz_const {

    // Enums corresponding to SystemZ condition codes

    public static final int SYSZ_CC_INVALID = 0;
    public static final int SYSZ_CC_O = 1;
    public static final int SYSZ_CC_H = 2;
    public static final int SYSZ_CC_NLE = 3;
    public static final int SYSZ_CC_L = 4;
    public static final int SYSZ_CC_NHE = 5;
    public static final int SYSZ_CC_LH = 6;
    public static final int SYSZ_CC_NE = 7;
    public static final int SYSZ_CC_E = 8;
    public static final int SYSZ_CC_NLH = 9;
    public static final int SYSZ_CC_HE = 10;
    public static final int SYSZ_CC_NL = 11;
    public static final int SYSZ_CC_LE = 12;
    public static final int SYSZ_CC_NH = 13;
    public static final int SYSZ_CC_NO = 14;

    // Operand type for instruction's operands

    public static final int SYSZ_OP_INVALID = 0;
    public static final int SYSZ_OP_REG = 1;
    public static final int SYSZ_OP_IMM = 2;
    public static final int SYSZ_OP_MEM = 3;
    public static final int SYSZ_OP_ACREG = 64;

    // SystemZ registers

    public static final int SYSZ_REG_INVALID = 0;
    public static final int SYSZ_REG_0 = 1;
    public static final int SYSZ_REG_1 = 2;
    public static final int SYSZ_REG_2 = 3;
    public static final int SYSZ_REG_3 = 4;
    public static final int SYSZ_REG_4 = 5;
    public static final int SYSZ_REG_5 = 6;
    public static final int SYSZ_REG_6 = 7;
    public static final int SYSZ_REG_7 = 8;
    public static final int SYSZ_REG_8 = 9;
    public static final int SYSZ_REG_9 = 10;
    public static final int SYSZ_REG_10 = 11;
    public static final int SYSZ_REG_11 = 12;
    public static final int SYSZ_REG_12 = 13;
    public static final int SYSZ_REG_13 = 14;
    public static final int SYSZ_REG_14 = 15;
    public static final int SYSZ_REG_15 = 16;
    public static final int SYSZ_REG_CC = 17;
    public static final int SYSZ_REG_F0 = 18;
    public static final int SYSZ_REG_F1 = 19;
    public static final int SYSZ_REG_F2 = 20;
    public static final int SYSZ_REG_F3 = 21;
    public static final int SYSZ_REG_F4 = 22;
    public static final int SYSZ_REG_F5 = 23;
    public static final int SYSZ_REG_F6 = 24;
    public static final int SYSZ_REG_F7 = 25;
    public static final int SYSZ_REG_F8 = 26;
    public static final int SYSZ_REG_F9 = 27;
    public static final int SYSZ_REG_F10 = 28;
    public static final int SYSZ_REG_F11 = 29;
    public static final int SYSZ_REG_F12 = 30;
    public static final int SYSZ_REG_F13 = 31;
    public static final int SYSZ_REG_F14 = 32;
    public static final int SYSZ_REG_F15 = 33;
    public static final int SYSZ_REG_R0L = 34;
    public static final int SYSZ_REG_ENDING = 35;

    // SystemZ instruction

    public static final int SYSZ_INS_INVALID = 0;
    public static final int SYSZ_INS_A = 1;
    public static final int SYSZ_INS_ADB = 2;
    public static final int SYSZ_INS_ADBR = 3;
    public static final int SYSZ_INS_AEB = 4;
    public static final int SYSZ_INS_AEBR = 5;
    public static final int SYSZ_INS_AFI = 6;
    public static final int SYSZ_INS_AG = 7;
    public static final int SYSZ_INS_AGF = 8;
    public static final int SYSZ_INS_AGFI = 9;
    public static final int SYSZ_INS_AGFR = 10;
    public static final int SYSZ_INS_AGHI = 11;
    public static final int SYSZ_INS_AGHIK = 12;
    public static final int SYSZ_INS_AGR = 13;
    public static final int SYSZ_INS_AGRK = 14;
    public static final int SYSZ_INS_AGSI = 15;
    public static final int SYSZ_INS_AH = 16;
    public static final int SYSZ_INS_AHI = 17;
    public static final int SYSZ_INS_AHIK = 18;
    public static final int SYSZ_INS_AHY = 19;
    public static final int SYSZ_INS_AIH = 20;
    public static final int SYSZ_INS_AL = 21;
    public static final int SYSZ_INS_ALC = 22;
    public static final int SYSZ_INS_ALCG = 23;
    public static final int SYSZ_INS_ALCGR = 24;
    public static final int SYSZ_INS_ALCR = 25;
    public static final int SYSZ_INS_ALFI = 26;
    public static final int SYSZ_INS_ALG = 27;
    public static final int SYSZ_INS_ALGF = 28;
    public static final int SYSZ_INS_ALGFI = 29;
    public static final int SYSZ_INS_ALGFR = 30;
    public static final int SYSZ_INS_ALGHSIK = 31;
    public static final int SYSZ_INS_ALGR = 32;
    public static final int SYSZ_INS_ALGRK = 33;
    public static final int SYSZ_INS_ALHSIK = 34;
    public static final int SYSZ_INS_ALR = 35;
    public static final int SYSZ_INS_ALRK = 36;
    public static final int SYSZ_INS_ALY = 37;
    public static final int SYSZ_INS_AR = 38;
    public static final int SYSZ_INS_ARK = 39;
    public static final int SYSZ_INS_ASI = 40;
    public static final int SYSZ_INS_AXBR = 41;
    public static final int SYSZ_INS_AY = 42;
    public static final int SYSZ_INS_BCR = 43;
    public static final int SYSZ_INS_BRC = 44;
    public static final int SYSZ_INS_BRCL = 45;
    public static final int SYSZ_INS_CGIJ = 46;
    public static final int SYSZ_INS_CGRJ = 47;
    public static final int SYSZ_INS_CIJ = 48;
    public static final int SYSZ_INS_CLGIJ = 49;
    public static final int SYSZ_INS_CLGRJ = 50;
    public static final int SYSZ_INS_CLIJ = 51;
    public static final int SYSZ_INS_CLRJ = 52;
    public static final int SYSZ_INS_CRJ = 53;
    public static final int SYSZ_INS_BER = 54;
    public static final int SYSZ_INS_JE = 55;
    public static final int SYSZ_INS_JGE = 56;
    public static final int SYSZ_INS_LOCE = 57;
    public static final int SYSZ_INS_LOCGE = 58;
    public static final int SYSZ_INS_LOCGRE = 59;
    public static final int SYSZ_INS_LOCRE = 60;
    public static final int SYSZ_INS_STOCE = 61;
    public static final int SYSZ_INS_STOCGE = 62;
    public static final int SYSZ_INS_BHR = 63;
    public static final int SYSZ_INS_BHER = 64;
    public static final int SYSZ_INS_JHE = 65;
    public static final int SYSZ_INS_JGHE = 66;
    public static final int SYSZ_INS_LOCHE = 67;
    public static final int SYSZ_INS_LOCGHE = 68;
    public static final int SYSZ_INS_LOCGRHE = 69;
    public static final int SYSZ_INS_LOCRHE = 70;
    public static final int SYSZ_INS_STOCHE = 71;
    public static final int SYSZ_INS_STOCGHE = 72;
    public static final int SYSZ_INS_JH = 73;
    public static final int SYSZ_INS_JGH = 74;
    public static final int SYSZ_INS_LOCH = 75;
    public static final int SYSZ_INS_LOCGH = 76;
    public static final int SYSZ_INS_LOCGRH = 77;
    public static final int SYSZ_INS_LOCRH = 78;
    public static final int SYSZ_INS_STOCH = 79;
    public static final int SYSZ_INS_STOCGH = 80;
    public static final int SYSZ_INS_CGIJNLH = 81;
    public static final int SYSZ_INS_CGRJNLH = 82;
    public static final int SYSZ_INS_CIJNLH = 83;
    public static final int SYSZ_INS_CLGIJNLH = 84;
    public static final int SYSZ_INS_CLGRJNLH = 85;
    public static final int SYSZ_INS_CLIJNLH = 86;
    public static final int SYSZ_INS_CLRJNLH = 87;
    public static final int SYSZ_INS_CRJNLH = 88;
    public static final int SYSZ_INS_CGIJE = 89;
    public static final int SYSZ_INS_CGRJE = 90;
    public static final int SYSZ_INS_CIJE = 91;
    public static final int SYSZ_INS_CLGIJE = 92;
    public static final int SYSZ_INS_CLGRJE = 93;
    public static final int SYSZ_INS_CLIJE = 94;
    public static final int SYSZ_INS_CLRJE = 95;
    public static final int SYSZ_INS_CRJE = 96;
    public static final int SYSZ_INS_CGIJNLE = 97;
    public static final int SYSZ_INS_CGRJNLE = 98;
    public static final int SYSZ_INS_CIJNLE = 99;
    public static final int SYSZ_INS_CLGIJNLE = 100;
    public static final int SYSZ_INS_CLGRJNLE = 101;
    public static final int SYSZ_INS_CLIJNLE = 102;
    public static final int SYSZ_INS_CLRJNLE = 103;
    public static final int SYSZ_INS_CRJNLE = 104;
    public static final int SYSZ_INS_CGIJH = 105;
    public static final int SYSZ_INS_CGRJH = 106;
    public static final int SYSZ_INS_CIJH = 107;
    public static final int SYSZ_INS_CLGIJH = 108;
    public static final int SYSZ_INS_CLGRJH = 109;
    public static final int SYSZ_INS_CLIJH = 110;
    public static final int SYSZ_INS_CLRJH = 111;
    public static final int SYSZ_INS_CRJH = 112;
    public static final int SYSZ_INS_CGIJNL = 113;
    public static final int SYSZ_INS_CGRJNL = 114;
    public static final int SYSZ_INS_CIJNL = 115;
    public static final int SYSZ_INS_CLGIJNL = 116;
    public static final int SYSZ_INS_CLGRJNL = 117;
    public static final int SYSZ_INS_CLIJNL = 118;
    public static final int SYSZ_INS_CLRJNL = 119;
    public static final int SYSZ_INS_CRJNL = 120;
    public static final int SYSZ_INS_CGIJHE = 121;
    public static final int SYSZ_INS_CGRJHE = 122;
    public static final int SYSZ_INS_CIJHE = 123;
    public static final int SYSZ_INS_CLGIJHE = 124;
    public static final int SYSZ_INS_CLGRJHE = 125;
    public static final int SYSZ_INS_CLIJHE = 126;
    public static final int SYSZ_INS_CLRJHE = 127;
    public static final int SYSZ_INS_CRJHE = 128;
    public static final int SYSZ_INS_CGIJNHE = 129;
    public static final int SYSZ_INS_CGRJNHE = 130;
    public static final int SYSZ_INS_CIJNHE = 131;
    public static final int SYSZ_INS_CLGIJNHE = 132;
    public static final int SYSZ_INS_CLGRJNHE = 133;
    public static final int SYSZ_INS_CLIJNHE = 134;
    public static final int SYSZ_INS_CLRJNHE = 135;
    public static final int SYSZ_INS_CRJNHE = 136;
    public static final int SYSZ_INS_CGIJL = 137;
    public static final int SYSZ_INS_CGRJL = 138;
    public static final int SYSZ_INS_CIJL = 139;
    public static final int SYSZ_INS_CLGIJL = 140;
    public static final int SYSZ_INS_CLGRJL = 141;
    public static final int SYSZ_INS_CLIJL = 142;
    public static final int SYSZ_INS_CLRJL = 143;
    public static final int SYSZ_INS_CRJL = 144;
    public static final int SYSZ_INS_CGIJNH = 145;
    public static final int SYSZ_INS_CGRJNH = 146;
    public static final int SYSZ_INS_CIJNH = 147;
    public static final int SYSZ_INS_CLGIJNH = 148;
    public static final int SYSZ_INS_CLGRJNH = 149;
    public static final int SYSZ_INS_CLIJNH = 150;
    public static final int SYSZ_INS_CLRJNH = 151;
    public static final int SYSZ_INS_CRJNH = 152;
    public static final int SYSZ_INS_CGIJLE = 153;
    public static final int SYSZ_INS_CGRJLE = 154;
    public static final int SYSZ_INS_CIJLE = 155;
    public static final int SYSZ_INS_CLGIJLE = 156;
    public static final int SYSZ_INS_CLGRJLE = 157;
    public static final int SYSZ_INS_CLIJLE = 158;
    public static final int SYSZ_INS_CLRJLE = 159;
    public static final int SYSZ_INS_CRJLE = 160;
    public static final int SYSZ_INS_CGIJNE = 161;
    public static final int SYSZ_INS_CGRJNE = 162;
    public static final int SYSZ_INS_CIJNE = 163;
    public static final int SYSZ_INS_CLGIJNE = 164;
    public static final int SYSZ_INS_CLGRJNE = 165;
    public static final int SYSZ_INS_CLIJNE = 166;
    public static final int SYSZ_INS_CLRJNE = 167;
    public static final int SYSZ_INS_CRJNE = 168;
    public static final int SYSZ_INS_CGIJLH = 169;
    public static final int SYSZ_INS_CGRJLH = 170;
    public static final int SYSZ_INS_CIJLH = 171;
    public static final int SYSZ_INS_CLGIJLH = 172;
    public static final int SYSZ_INS_CLGRJLH = 173;
    public static final int SYSZ_INS_CLIJLH = 174;
    public static final int SYSZ_INS_CLRJLH = 175;
    public static final int SYSZ_INS_CRJLH = 176;
    public static final int SYSZ_INS_BLR = 177;
    public static final int SYSZ_INS_BLER = 178;
    public static final int SYSZ_INS_JLE = 179;
    public static final int SYSZ_INS_JGLE = 180;
    public static final int SYSZ_INS_LOCLE = 181;
    public static final int SYSZ_INS_LOCGLE = 182;
    public static final int SYSZ_INS_LOCGRLE = 183;
    public static final int SYSZ_INS_LOCRLE = 184;
    public static final int SYSZ_INS_STOCLE = 185;
    public static final int SYSZ_INS_STOCGLE = 186;
    public static final int SYSZ_INS_BLHR = 187;
    public static final int SYSZ_INS_JLH = 188;
    public static final int SYSZ_INS_JGLH = 189;
    public static final int SYSZ_INS_LOCLH = 190;
    public static final int SYSZ_INS_LOCGLH = 191;
    public static final int SYSZ_INS_LOCGRLH = 192;
    public static final int SYSZ_INS_LOCRLH = 193;
    public static final int SYSZ_INS_STOCLH = 194;
    public static final int SYSZ_INS_STOCGLH = 195;
    public static final int SYSZ_INS_JL = 196;
    public static final int SYSZ_INS_JGL = 197;
    public static final int SYSZ_INS_LOCL = 198;
    public static final int SYSZ_INS_LOCGL = 199;
    public static final int SYSZ_INS_LOCGRL = 200;
    public static final int SYSZ_INS_LOCRL = 201;
    public static final int SYSZ_INS_LOC = 202;
    public static final int SYSZ_INS_LOCG = 203;
    public static final int SYSZ_INS_LOCGR = 204;
    public static final int SYSZ_INS_LOCR = 205;
    public static final int SYSZ_INS_STOCL = 206;
    public static final int SYSZ_INS_STOCGL = 207;
    public static final int SYSZ_INS_BNER = 208;
    public static final int SYSZ_INS_JNE = 209;
    public static final int SYSZ_INS_JGNE = 210;
    public static final int SYSZ_INS_LOCNE = 211;
    public static final int SYSZ_INS_LOCGNE = 212;
    public static final int SYSZ_INS_LOCGRNE = 213;
    public static final int SYSZ_INS_LOCRNE = 214;
    public static final int SYSZ_INS_STOCNE = 215;
    public static final int SYSZ_INS_STOCGNE = 216;
    public static final int SYSZ_INS_BNHR = 217;
    public static final int SYSZ_INS_BNHER = 218;
    public static final int SYSZ_INS_JNHE = 219;
    public static final int SYSZ_INS_JGNHE = 220;
    public static final int SYSZ_INS_LOCNHE = 221;
    public static final int SYSZ_INS_LOCGNHE = 222;
    public static final int SYSZ_INS_LOCGRNHE = 223;
    public static final int SYSZ_INS_LOCRNHE = 224;
    public static final int SYSZ_INS_STOCNHE = 225;
    public static final int SYSZ_INS_STOCGNHE = 226;
    public static final int SYSZ_INS_JNH = 227;
    public static final int SYSZ_INS_JGNH = 228;
    public static final int SYSZ_INS_LOCNH = 229;
    public static final int SYSZ_INS_LOCGNH = 230;
    public static final int SYSZ_INS_LOCGRNH = 231;
    public static final int SYSZ_INS_LOCRNH = 232;
    public static final int SYSZ_INS_STOCNH = 233;
    public static final int SYSZ_INS_STOCGNH = 234;
    public static final int SYSZ_INS_BNLR = 235;
    public static final int SYSZ_INS_BNLER = 236;
    public static final int SYSZ_INS_JNLE = 237;
    public static final int SYSZ_INS_JGNLE = 238;
    public static final int SYSZ_INS_LOCNLE = 239;
    public static final int SYSZ_INS_LOCGNLE = 240;
    public static final int SYSZ_INS_LOCGRNLE = 241;
    public static final int SYSZ_INS_LOCRNLE = 242;
    public static final int SYSZ_INS_STOCNLE = 243;
    public static final int SYSZ_INS_STOCGNLE = 244;
    public static final int SYSZ_INS_BNLHR = 245;
    public static final int SYSZ_INS_JNLH = 246;
    public static final int SYSZ_INS_JGNLH = 247;
    public static final int SYSZ_INS_LOCNLH = 248;
    public static final int SYSZ_INS_LOCGNLH = 249;
    public static final int SYSZ_INS_LOCGRNLH = 250;
    public static final int SYSZ_INS_LOCRNLH = 251;
    public static final int SYSZ_INS_STOCNLH = 252;
    public static final int SYSZ_INS_STOCGNLH = 253;
    public static final int SYSZ_INS_JNL = 254;
    public static final int SYSZ_INS_JGNL = 255;
    public static final int SYSZ_INS_LOCNL = 256;
    public static final int SYSZ_INS_LOCGNL = 257;
    public static final int SYSZ_INS_LOCGRNL = 258;
    public static final int SYSZ_INS_LOCRNL = 259;
    public static final int SYSZ_INS_STOCNL = 260;
    public static final int SYSZ_INS_STOCGNL = 261;
    public static final int SYSZ_INS_BNOR = 262;
    public static final int SYSZ_INS_JNO = 263;
    public static final int SYSZ_INS_JGNO = 264;
    public static final int SYSZ_INS_LOCNO = 265;
    public static final int SYSZ_INS_LOCGNO = 266;
    public static final int SYSZ_INS_LOCGRNO = 267;
    public static final int SYSZ_INS_LOCRNO = 268;
    public static final int SYSZ_INS_STOCNO = 269;
    public static final int SYSZ_INS_STOCGNO = 270;
    public static final int SYSZ_INS_BOR = 271;
    public static final int SYSZ_INS_JO = 272;
    public static final int SYSZ_INS_JGO = 273;
    public static final int SYSZ_INS_LOCO = 274;
    public static final int SYSZ_INS_LOCGO = 275;
    public static final int SYSZ_INS_LOCGRO = 276;
    public static final int SYSZ_INS_LOCRO = 277;
    public static final int SYSZ_INS_STOCO = 278;
    public static final int SYSZ_INS_STOCGO = 279;
    public static final int SYSZ_INS_STOC = 280;
    public static final int SYSZ_INS_STOCG = 281;
    public static final int SYSZ_INS_BASR = 282;
    public static final int SYSZ_INS_BR = 283;
    public static final int SYSZ_INS_BRAS = 284;
    public static final int SYSZ_INS_BRASL = 285;
    public static final int SYSZ_INS_J = 286;
    public static final int SYSZ_INS_JG = 287;
    public static final int SYSZ_INS_BRCT = 288;
    public static final int SYSZ_INS_BRCTG = 289;
    public static final int SYSZ_INS_C = 290;
    public static final int SYSZ_INS_CDB = 291;
    public static final int SYSZ_INS_CDBR = 292;
    public static final int SYSZ_INS_CDFBR = 293;
    public static final int SYSZ_INS_CDGBR = 294;
    public static final int SYSZ_INS_CDLFBR = 295;
    public static final int SYSZ_INS_CDLGBR = 296;
    public static final int SYSZ_INS_CEB = 297;
    public static final int SYSZ_INS_CEBR = 298;
    public static final int SYSZ_INS_CEFBR = 299;
    public static final int SYSZ_INS_CEGBR = 300;
    public static final int SYSZ_INS_CELFBR = 301;
    public static final int SYSZ_INS_CELGBR = 302;
    public static final int SYSZ_INS_CFDBR = 303;
    public static final int SYSZ_INS_CFEBR = 304;
    public static final int SYSZ_INS_CFI = 305;
    public static final int SYSZ_INS_CFXBR = 306;
    public static final int SYSZ_INS_CG = 307;
    public static final int SYSZ_INS_CGDBR = 308;
    public static final int SYSZ_INS_CGEBR = 309;
    public static final int SYSZ_INS_CGF = 310;
    public static final int SYSZ_INS_CGFI = 311;
    public static final int SYSZ_INS_CGFR = 312;
    public static final int SYSZ_INS_CGFRL = 313;
    public static final int SYSZ_INS_CGH = 314;
    public static final int SYSZ_INS_CGHI = 315;
    public static final int SYSZ_INS_CGHRL = 316;
    public static final int SYSZ_INS_CGHSI = 317;
    public static final int SYSZ_INS_CGR = 318;
    public static final int SYSZ_INS_CGRL = 319;
    public static final int SYSZ_INS_CGXBR = 320;
    public static final int SYSZ_INS_CH = 321;
    public static final int SYSZ_INS_CHF = 322;
    public static final int SYSZ_INS_CHHSI = 323;
    public static final int SYSZ_INS_CHI = 324;
    public static final int SYSZ_INS_CHRL = 325;
    public static final int SYSZ_INS_CHSI = 326;
    public static final int SYSZ_INS_CHY = 327;
    public static final int SYSZ_INS_CIH = 328;
    public static final int SYSZ_INS_CL = 329;
    public static final int SYSZ_INS_CLC = 330;
    public static final int SYSZ_INS_CLFDBR = 331;
    public static final int SYSZ_INS_CLFEBR = 332;
    public static final int SYSZ_INS_CLFHSI = 333;
    public static final int SYSZ_INS_CLFI = 334;
    public static final int SYSZ_INS_CLFXBR = 335;
    public static final int SYSZ_INS_CLG = 336;
    public static final int SYSZ_INS_CLGDBR = 337;
    public static final int SYSZ_INS_CLGEBR = 338;
    public static final int SYSZ_INS_CLGF = 339;
    public static final int SYSZ_INS_CLGFI = 340;
    public static final int SYSZ_INS_CLGFR = 341;
    public static final int SYSZ_INS_CLGFRL = 342;
    public static final int SYSZ_INS_CLGHRL = 343;
    public static final int SYSZ_INS_CLGHSI = 344;
    public static final int SYSZ_INS_CLGR = 345;
    public static final int SYSZ_INS_CLGRL = 346;
    public static final int SYSZ_INS_CLGXBR = 347;
    public static final int SYSZ_INS_CLHF = 348;
    public static final int SYSZ_INS_CLHHSI = 349;
    public static final int SYSZ_INS_CLHRL = 350;
    public static final int SYSZ_INS_CLI = 351;
    public static final int SYSZ_INS_CLIH = 352;
    public static final int SYSZ_INS_CLIY = 353;
    public static final int SYSZ_INS_CLR = 354;
    public static final int SYSZ_INS_CLRL = 355;
    public static final int SYSZ_INS_CLST = 356;
    public static final int SYSZ_INS_CLY = 357;
    public static final int SYSZ_INS_CPSDR = 358;
    public static final int SYSZ_INS_CR = 359;
    public static final int SYSZ_INS_CRL = 360;
    public static final int SYSZ_INS_CS = 361;
    public static final int SYSZ_INS_CSG = 362;
    public static final int SYSZ_INS_CSY = 363;
    public static final int SYSZ_INS_CXBR = 364;
    public static final int SYSZ_INS_CXFBR = 365;
    public static final int SYSZ_INS_CXGBR = 366;
    public static final int SYSZ_INS_CXLFBR = 367;
    public static final int SYSZ_INS_CXLGBR = 368;
    public static final int SYSZ_INS_CY = 369;
    public static final int SYSZ_INS_DDB = 370;
    public static final int SYSZ_INS_DDBR = 371;
    public static final int SYSZ_INS_DEB = 372;
    public static final int SYSZ_INS_DEBR = 373;
    public static final int SYSZ_INS_DL = 374;
    public static final int SYSZ_INS_DLG = 375;
    public static final int SYSZ_INS_DLGR = 376;
    public static final int SYSZ_INS_DLR = 377;
    public static final int SYSZ_INS_DSG = 378;
    public static final int SYSZ_INS_DSGF = 379;
    public static final int SYSZ_INS_DSGFR = 380;
    public static final int SYSZ_INS_DSGR = 381;
    public static final int SYSZ_INS_DXBR = 382;
    public static final int SYSZ_INS_EAR = 383;
    public static final int SYSZ_INS_FIDBR = 384;
    public static final int SYSZ_INS_FIDBRA = 385;
    public static final int SYSZ_INS_FIEBR = 386;
    public static final int SYSZ_INS_FIEBRA = 387;
    public static final int SYSZ_INS_FIXBR = 388;
    public static final int SYSZ_INS_FIXBRA = 389;
    public static final int SYSZ_INS_FLOGR = 390;
    public static final int SYSZ_INS_IC = 391;
    public static final int SYSZ_INS_ICY = 392;
    public static final int SYSZ_INS_IIHF = 393;
    public static final int SYSZ_INS_IIHH = 394;
    public static final int SYSZ_INS_IIHL = 395;
    public static final int SYSZ_INS_IILF = 396;
    public static final int SYSZ_INS_IILH = 397;
    public static final int SYSZ_INS_IILL = 398;
    public static final int SYSZ_INS_IPM = 399;
    public static final int SYSZ_INS_L = 400;
    public static final int SYSZ_INS_LA = 401;
    public static final int SYSZ_INS_LAA = 402;
    public static final int SYSZ_INS_LAAG = 403;
    public static final int SYSZ_INS_LAAL = 404;
    public static final int SYSZ_INS_LAALG = 405;
    public static final int SYSZ_INS_LAN = 406;
    public static final int SYSZ_INS_LANG = 407;
    public static final int SYSZ_INS_LAO = 408;
    public static final int SYSZ_INS_LAOG = 409;
    public static final int SYSZ_INS_LARL = 410;
    public static final int SYSZ_INS_LAX = 411;
    public static final int SYSZ_INS_LAXG = 412;
    public static final int SYSZ_INS_LAY = 413;
    public static final int SYSZ_INS_LB = 414;
    public static final int SYSZ_INS_LBH = 415;
    public static final int SYSZ_INS_LBR = 416;
    public static final int SYSZ_INS_LCDBR = 417;
    public static final int SYSZ_INS_LCEBR = 418;
    public static final int SYSZ_INS_LCGFR = 419;
    public static final int SYSZ_INS_LCGR = 420;
    public static final int SYSZ_INS_LCR = 421;
    public static final int SYSZ_INS_LCXBR = 422;
    public static final int SYSZ_INS_LD = 423;
    public static final int SYSZ_INS_LDEB = 424;
    public static final int SYSZ_INS_LDEBR = 425;
    public static final int SYSZ_INS_LDGR = 426;
    public static final int SYSZ_INS_LDR = 427;
    public static final int SYSZ_INS_LDXBR = 428;
    public static final int SYSZ_INS_LDXBRA = 429;
    public static final int SYSZ_INS_LDY = 430;
    public static final int SYSZ_INS_LE = 431;
    public static final int SYSZ_INS_LEDBR = 432;
    public static final int SYSZ_INS_LEDBRA = 433;
    public static final int SYSZ_INS_LER = 434;
    public static final int SYSZ_INS_LEXBR = 435;
    public static final int SYSZ_INS_LEXBRA = 436;
    public static final int SYSZ_INS_LEY = 437;
    public static final int SYSZ_INS_LFH = 438;
    public static final int SYSZ_INS_LG = 439;
    public static final int SYSZ_INS_LGB = 440;
    public static final int SYSZ_INS_LGBR = 441;
    public static final int SYSZ_INS_LGDR = 442;
    public static final int SYSZ_INS_LGF = 443;
    public static final int SYSZ_INS_LGFI = 444;
    public static final int SYSZ_INS_LGFR = 445;
    public static final int SYSZ_INS_LGFRL = 446;
    public static final int SYSZ_INS_LGH = 447;
    public static final int SYSZ_INS_LGHI = 448;
    public static final int SYSZ_INS_LGHR = 449;
    public static final int SYSZ_INS_LGHRL = 450;
    public static final int SYSZ_INS_LGR = 451;
    public static final int SYSZ_INS_LGRL = 452;
    public static final int SYSZ_INS_LH = 453;
    public static final int SYSZ_INS_LHH = 454;
    public static final int SYSZ_INS_LHI = 455;
    public static final int SYSZ_INS_LHR = 456;
    public static final int SYSZ_INS_LHRL = 457;
    public static final int SYSZ_INS_LHY = 458;
    public static final int SYSZ_INS_LLC = 459;
    public static final int SYSZ_INS_LLCH = 460;
    public static final int SYSZ_INS_LLCR = 461;
    public static final int SYSZ_INS_LLGC = 462;
    public static final int SYSZ_INS_LLGCR = 463;
    public static final int SYSZ_INS_LLGF = 464;
    public static final int SYSZ_INS_LLGFR = 465;
    public static final int SYSZ_INS_LLGFRL = 466;
    public static final int SYSZ_INS_LLGH = 467;
    public static final int SYSZ_INS_LLGHR = 468;
    public static final int SYSZ_INS_LLGHRL = 469;
    public static final int SYSZ_INS_LLH = 470;
    public static final int SYSZ_INS_LLHH = 471;
    public static final int SYSZ_INS_LLHR = 472;
    public static final int SYSZ_INS_LLHRL = 473;
    public static final int SYSZ_INS_LLIHF = 474;
    public static final int SYSZ_INS_LLIHH = 475;
    public static final int SYSZ_INS_LLIHL = 476;
    public static final int SYSZ_INS_LLILF = 477;
    public static final int SYSZ_INS_LLILH = 478;
    public static final int SYSZ_INS_LLILL = 479;
    public static final int SYSZ_INS_LMG = 480;
    public static final int SYSZ_INS_LNDBR = 481;
    public static final int SYSZ_INS_LNEBR = 482;
    public static final int SYSZ_INS_LNGFR = 483;
    public static final int SYSZ_INS_LNGR = 484;
    public static final int SYSZ_INS_LNR = 485;
    public static final int SYSZ_INS_LNXBR = 486;
    public static final int SYSZ_INS_LPDBR = 487;
    public static final int SYSZ_INS_LPEBR = 488;
    public static final int SYSZ_INS_LPGFR = 489;
    public static final int SYSZ_INS_LPGR = 490;
    public static final int SYSZ_INS_LPR = 491;
    public static final int SYSZ_INS_LPXBR = 492;
    public static final int SYSZ_INS_LR = 493;
    public static final int SYSZ_INS_LRL = 494;
    public static final int SYSZ_INS_LRV = 495;
    public static final int SYSZ_INS_LRVG = 496;
    public static final int SYSZ_INS_LRVGR = 497;
    public static final int SYSZ_INS_LRVR = 498;
    public static final int SYSZ_INS_LT = 499;
    public static final int SYSZ_INS_LTDBR = 500;
    public static final int SYSZ_INS_LTEBR = 501;
    public static final int SYSZ_INS_LTG = 502;
    public static final int SYSZ_INS_LTGF = 503;
    public static final int SYSZ_INS_LTGFR = 504;
    public static final int SYSZ_INS_LTGR = 505;
    public static final int SYSZ_INS_LTR = 506;
    public static final int SYSZ_INS_LTXBR = 507;
    public static final int SYSZ_INS_LXDB = 508;
    public static final int SYSZ_INS_LXDBR = 509;
    public static final int SYSZ_INS_LXEB = 510;
    public static final int SYSZ_INS_LXEBR = 511;
    public static final int SYSZ_INS_LXR = 512;
    public static final int SYSZ_INS_LY = 513;
    public static final int SYSZ_INS_LZDR = 514;
    public static final int SYSZ_INS_LZER = 515;
    public static final int SYSZ_INS_LZXR = 516;
    public static final int SYSZ_INS_MADB = 517;
    public static final int SYSZ_INS_MADBR = 518;
    public static final int SYSZ_INS_MAEB = 519;
    public static final int SYSZ_INS_MAEBR = 520;
    public static final int SYSZ_INS_MDB = 521;
    public static final int SYSZ_INS_MDBR = 522;
    public static final int SYSZ_INS_MDEB = 523;
    public static final int SYSZ_INS_MDEBR = 524;
    public static final int SYSZ_INS_MEEB = 525;
    public static final int SYSZ_INS_MEEBR = 526;
    public static final int SYSZ_INS_MGHI = 527;
    public static final int SYSZ_INS_MH = 528;
    public static final int SYSZ_INS_MHI = 529;
    public static final int SYSZ_INS_MHY = 530;
    public static final int SYSZ_INS_MLG = 531;
    public static final int SYSZ_INS_MLGR = 532;
    public static final int SYSZ_INS_MS = 533;
    public static final int SYSZ_INS_MSDB = 534;
    public static final int SYSZ_INS_MSDBR = 535;
    public static final int SYSZ_INS_MSEB = 536;
    public static final int SYSZ_INS_MSEBR = 537;
    public static final int SYSZ_INS_MSFI = 538;
    public static final int SYSZ_INS_MSG = 539;
    public static final int SYSZ_INS_MSGF = 540;
    public static final int SYSZ_INS_MSGFI = 541;
    public static final int SYSZ_INS_MSGFR = 542;
    public static final int SYSZ_INS_MSGR = 543;
    public static final int SYSZ_INS_MSR = 544;
    public static final int SYSZ_INS_MSY = 545;
    public static final int SYSZ_INS_MVC = 546;
    public static final int SYSZ_INS_MVGHI = 547;
    public static final int SYSZ_INS_MVHHI = 548;
    public static final int SYSZ_INS_MVHI = 549;
    public static final int SYSZ_INS_MVI = 550;
    public static final int SYSZ_INS_MVIY = 551;
    public static final int SYSZ_INS_MVST = 552;
    public static final int SYSZ_INS_MXBR = 553;
    public static final int SYSZ_INS_MXDB = 554;
    public static final int SYSZ_INS_MXDBR = 555;
    public static final int SYSZ_INS_N = 556;
    public static final int SYSZ_INS_NC = 557;
    public static final int SYSZ_INS_NG = 558;
    public static final int SYSZ_INS_NGR = 559;
    public static final int SYSZ_INS_NGRK = 560;
    public static final int SYSZ_INS_NI = 561;
    public static final int SYSZ_INS_NIHF = 562;
    public static final int SYSZ_INS_NIHH = 563;
    public static final int SYSZ_INS_NIHL = 564;
    public static final int SYSZ_INS_NILF = 565;
    public static final int SYSZ_INS_NILH = 566;
    public static final int SYSZ_INS_NILL = 567;
    public static final int SYSZ_INS_NIY = 568;
    public static final int SYSZ_INS_NR = 569;
    public static final int SYSZ_INS_NRK = 570;
    public static final int SYSZ_INS_NY = 571;
    public static final int SYSZ_INS_O = 572;
    public static final int SYSZ_INS_OC = 573;
    public static final int SYSZ_INS_OG = 574;
    public static final int SYSZ_INS_OGR = 575;
    public static final int SYSZ_INS_OGRK = 576;
    public static final int SYSZ_INS_OI = 577;
    public static final int SYSZ_INS_OIHF = 578;
    public static final int SYSZ_INS_OIHH = 579;
    public static final int SYSZ_INS_OIHL = 580;
    public static final int SYSZ_INS_OILF = 581;
    public static final int SYSZ_INS_OILH = 582;
    public static final int SYSZ_INS_OILL = 583;
    public static final int SYSZ_INS_OIY = 584;
    public static final int SYSZ_INS_OR = 585;
    public static final int SYSZ_INS_ORK = 586;
    public static final int SYSZ_INS_OY = 587;
    public static final int SYSZ_INS_PFD = 588;
    public static final int SYSZ_INS_PFDRL = 589;
    public static final int SYSZ_INS_RISBG = 590;
    public static final int SYSZ_INS_RISBHG = 591;
    public static final int SYSZ_INS_RISBLG = 592;
    public static final int SYSZ_INS_RLL = 593;
    public static final int SYSZ_INS_RLLG = 594;
    public static final int SYSZ_INS_RNSBG = 595;
    public static final int SYSZ_INS_ROSBG = 596;
    public static final int SYSZ_INS_RXSBG = 597;
    public static final int SYSZ_INS_S = 598;
    public static final int SYSZ_INS_SDB = 599;
    public static final int SYSZ_INS_SDBR = 600;
    public static final int SYSZ_INS_SEB = 601;
    public static final int SYSZ_INS_SEBR = 602;
    public static final int SYSZ_INS_SG = 603;
    public static final int SYSZ_INS_SGF = 604;
    public static final int SYSZ_INS_SGFR = 605;
    public static final int SYSZ_INS_SGR = 606;
    public static final int SYSZ_INS_SGRK = 607;
    public static final int SYSZ_INS_SH = 608;
    public static final int SYSZ_INS_SHY = 609;
    public static final int SYSZ_INS_SL = 610;
    public static final int SYSZ_INS_SLB = 611;
    public static final int SYSZ_INS_SLBG = 612;
    public static final int SYSZ_INS_SLBR = 613;
    public static final int SYSZ_INS_SLFI = 614;
    public static final int SYSZ_INS_SLG = 615;
    public static final int SYSZ_INS_SLBGR = 616;
    public static final int SYSZ_INS_SLGF = 617;
    public static final int SYSZ_INS_SLGFI = 618;
    public static final int SYSZ_INS_SLGFR = 619;
    public static final int SYSZ_INS_SLGR = 620;
    public static final int SYSZ_INS_SLGRK = 621;
    public static final int SYSZ_INS_SLL = 622;
    public static final int SYSZ_INS_SLLG = 623;
    public static final int SYSZ_INS_SLLK = 624;
    public static final int SYSZ_INS_SLR = 625;
    public static final int SYSZ_INS_SLRK = 626;
    public static final int SYSZ_INS_SLY = 627;
    public static final int SYSZ_INS_SQDB = 628;
    public static final int SYSZ_INS_SQDBR = 629;
    public static final int SYSZ_INS_SQEB = 630;
    public static final int SYSZ_INS_SQEBR = 631;
    public static final int SYSZ_INS_SQXBR = 632;
    public static final int SYSZ_INS_SR = 633;
    public static final int SYSZ_INS_SRA = 634;
    public static final int SYSZ_INS_SRAG = 635;
    public static final int SYSZ_INS_SRAK = 636;
    public static final int SYSZ_INS_SRK = 637;
    public static final int SYSZ_INS_SRL = 638;
    public static final int SYSZ_INS_SRLG = 639;
    public static final int SYSZ_INS_SRLK = 640;
    public static final int SYSZ_INS_SRST = 641;
    public static final int SYSZ_INS_ST = 642;
    public static final int SYSZ_INS_STC = 643;
    public static final int SYSZ_INS_STCH = 644;
    public static final int SYSZ_INS_STCY = 645;
    public static final int SYSZ_INS_STD = 646;
    public static final int SYSZ_INS_STDY = 647;
    public static final int SYSZ_INS_STE = 648;
    public static final int SYSZ_INS_STEY = 649;
    public static final int SYSZ_INS_STFH = 650;
    public static final int SYSZ_INS_STG = 651;
    public static final int SYSZ_INS_STGRL = 652;
    public static final int SYSZ_INS_STH = 653;
    public static final int SYSZ_INS_STHH = 654;
    public static final int SYSZ_INS_STHRL = 655;
    public static final int SYSZ_INS_STHY = 656;
    public static final int SYSZ_INS_STMG = 657;
    public static final int SYSZ_INS_STRL = 658;
    public static final int SYSZ_INS_STRV = 659;
    public static final int SYSZ_INS_STRVG = 660;
    public static final int SYSZ_INS_STY = 661;
    public static final int SYSZ_INS_SXBR = 662;
    public static final int SYSZ_INS_SY = 663;
    public static final int SYSZ_INS_TM = 664;
    public static final int SYSZ_INS_TMHH = 665;
    public static final int SYSZ_INS_TMHL = 666;
    public static final int SYSZ_INS_TMLH = 667;
    public static final int SYSZ_INS_TMLL = 668;
    public static final int SYSZ_INS_TMY = 669;
    public static final int SYSZ_INS_X = 670;
    public static final int SYSZ_INS_XC = 671;
    public static final int SYSZ_INS_XG = 672;
    public static final int SYSZ_INS_XGR = 673;
    public static final int SYSZ_INS_XGRK = 674;
    public static final int SYSZ_INS_XI = 675;
    public static final int SYSZ_INS_XIHF = 676;
    public static final int SYSZ_INS_XILF = 677;
    public static final int SYSZ_INS_XIY = 678;
    public static final int SYSZ_INS_XR = 679;
    public static final int SYSZ_INS_XRK = 680;
    public static final int SYSZ_INS_XY = 681;
    public static final int SYSZ_INS_ENDING = 682;

    // Group of SystemZ instructions

    public static final int SYSZ_GRP_INVALID = 0;

    // Generic groups
    public static final int SYSZ_GRP_JUMP = 1;

    // Architecture-specific groups
    public static final int SYSZ_GRP_DISTINCTOPS = 128;
    public static final int SYSZ_GRP_FPEXTENSION = 129;
    public static final int SYSZ_GRP_HIGHWORD = 130;
    public static final int SYSZ_GRP_INTERLOCKEDACCESS1 = 131;
    public static final int SYSZ_GRP_LOADSTOREONCOND = 132;
    public static final int SYSZ_GRP_ENDING = 133;
}