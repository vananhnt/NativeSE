package org.apache.commons.codec.language;

import com.android.internal.telephony.CommandsInterface;
import gov.nist.core.Separators;
import gov.nist.javax.sip.parser.TokenNames;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/* loaded from: DoubleMetaphone.class */
public class DoubleMetaphone implements StringEncoder {
    private static final String VOWELS = "AEIOUY";
    private static final String[] SILENT_START = {"GN", "KN", "PN", "WR", "PS"};
    private static final String[] L_R_N_M_B_H_F_V_W_SPACE = {TokenNames.L, TokenNames.R, "N", TokenNames.M, "B", "H", TokenNames.F, TokenNames.V, "W", Separators.SP};
    private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = {"ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER"};
    private static final String[] L_T_K_S_N_M_B_Z = {TokenNames.L, TokenNames.T, TokenNames.K, TokenNames.S, "N", TokenNames.M, "B", "Z"};
    protected int maxCodeLen = 4;

    public String doubleMetaphone(String value) {
        return doubleMetaphone(value, false);
    }

    public String doubleMetaphone(String value, boolean alternate) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        String value2 = cleanInput(value);
        if (value2 == null) {
            return null;
        }
        boolean slavoGermanic = isSlavoGermanic(value2);
        int index = isSilentStart(value2) ? 1 : 0;
        DoubleMetaphoneResult result = new DoubleMetaphoneResult(getMaxCodeLen());
        while (!result.isComplete() && index <= value2.length() - 1) {
            switch (value2.charAt(index)) {
                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case 'U':
                case 'Y':
                    index = handleAEIOUY(value2, result, index);
                    break;
                case 'B':
                    result.append('P');
                    if (charAt(value2, index + 1) == 'B') {
                        i13 = index;
                        i14 = 2;
                    } else {
                        i13 = index;
                        i14 = 1;
                    }
                    index = i13 + i14;
                    break;
                case 'C':
                    index = handleC(value2, result, index);
                    break;
                case 'D':
                    index = handleD(value2, result, index);
                    break;
                case 'F':
                    result.append('F');
                    if (charAt(value2, index + 1) == 'F') {
                        i11 = index;
                        i12 = 2;
                    } else {
                        i11 = index;
                        i12 = 1;
                    }
                    index = i11 + i12;
                    break;
                case 'G':
                    index = handleG(value2, result, index, slavoGermanic);
                    break;
                case 'H':
                    index = handleH(value2, result, index);
                    break;
                case 'J':
                    index = handleJ(value2, result, index, slavoGermanic);
                    break;
                case 'K':
                    result.append('K');
                    if (charAt(value2, index + 1) == 'K') {
                        i9 = index;
                        i10 = 2;
                    } else {
                        i9 = index;
                        i10 = 1;
                    }
                    index = i9 + i10;
                    break;
                case 'L':
                    index = handleL(value2, result, index);
                    break;
                case 'M':
                    result.append('M');
                    if (conditionM0(value2, index)) {
                        i7 = index;
                        i8 = 2;
                    } else {
                        i7 = index;
                        i8 = 1;
                    }
                    index = i7 + i8;
                    break;
                case 'N':
                    result.append('N');
                    if (charAt(value2, index + 1) == 'N') {
                        i5 = index;
                        i6 = 2;
                    } else {
                        i5 = index;
                        i6 = 1;
                    }
                    index = i5 + i6;
                    break;
                case 'P':
                    index = handleP(value2, result, index);
                    break;
                case 'Q':
                    result.append('K');
                    if (charAt(value2, index + 1) == 'Q') {
                        i3 = index;
                        i4 = 2;
                    } else {
                        i3 = index;
                        i4 = 1;
                    }
                    index = i3 + i4;
                    break;
                case 'R':
                    index = handleR(value2, result, index, slavoGermanic);
                    break;
                case 'S':
                    index = handleS(value2, result, index, slavoGermanic);
                    break;
                case 'T':
                    index = handleT(value2, result, index);
                    break;
                case 'V':
                    result.append('F');
                    if (charAt(value2, index + 1) == 'V') {
                        i = index;
                        i2 = 2;
                    } else {
                        i = index;
                        i2 = 1;
                    }
                    index = i + i2;
                    break;
                case 'W':
                    index = handleW(value2, result, index);
                    break;
                case 'X':
                    index = handleX(value2, result, index);
                    break;
                case 'Z':
                    index = handleZ(value2, result, index, slavoGermanic);
                    break;
                case 199:
                    result.append('S');
                    index++;
                    break;
                case 209:
                    result.append('N');
                    index++;
                    break;
                default:
                    index++;
                    break;
            }
        }
        return alternate ? result.getAlternate() : result.getPrimary();
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
        }
        return doubleMetaphone((String) obj);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String value) {
        return doubleMetaphone(value);
    }

    public boolean isDoubleMetaphoneEqual(String value1, String value2) {
        return isDoubleMetaphoneEqual(value1, value2, false);
    }

    public boolean isDoubleMetaphoneEqual(String value1, String value2, boolean alternate) {
        return doubleMetaphone(value1, alternate).equals(doubleMetaphone(value2, alternate));
    }

    public int getMaxCodeLen() {
        return this.maxCodeLen;
    }

    public void setMaxCodeLen(int maxCodeLen) {
        this.maxCodeLen = maxCodeLen;
    }

    private int handleAEIOUY(String value, DoubleMetaphoneResult result, int index) {
        if (index == 0) {
            result.append('A');
        }
        return index + 1;
    }

    private int handleC(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (conditionC0(value, index)) {
            result.append('K');
            index2 = index + 2;
        } else if (index == 0 && contains(value, index, 6, "CAESAR")) {
            result.append('S');
            index2 = index + 2;
        } else if (contains(value, index, 2, "CH")) {
            index2 = handleCH(value, result, index);
        } else if (contains(value, index, 2, "CZ") && !contains(value, index - 2, 4, "WICZ")) {
            result.append('S', 'X');
            index2 = index + 2;
        } else if (contains(value, index + 1, 3, "CIA")) {
            result.append('X');
            index2 = index + 3;
        } else if (contains(value, index, 2, "CC") && (index != 1 || charAt(value, 0) != 'M')) {
            return handleCC(value, result, index);
        } else {
            if (contains(value, index, 2, "CK", "CG", "CQ")) {
                result.append('K');
                index2 = index + 2;
            } else if (contains(value, index, 2, "CI", "CE", "CY")) {
                if (contains(value, index, 3, "CIO", "CIE", "CIA")) {
                    result.append('S', 'X');
                } else {
                    result.append('S');
                }
                index2 = index + 2;
            } else {
                result.append('K');
                if (contains(value, index + 1, 2, " C", " Q", " G")) {
                    index2 = index + 3;
                } else if (contains(value, index + 1, 1, TokenNames.C, TokenNames.K, "Q") && !contains(value, index + 1, 2, "CE", "CI")) {
                    index2 = index + 2;
                } else {
                    index2 = index + 1;
                }
            }
        }
        return index2;
    }

    private int handleCC(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index + 2, 1, TokenNames.I, TokenNames.E, "H") && !contains(value, index + 2, 2, "HU")) {
            if ((index == 1 && charAt(value, index - 1) == 'A') || contains(value, index - 1, 5, "UCCEE", "UCCES")) {
                result.append("KS");
            } else {
                result.append('X');
            }
            index2 = index + 3;
        } else {
            result.append('K');
            index2 = index + 2;
        }
        return index2;
    }

    private int handleCH(String value, DoubleMetaphoneResult result, int index) {
        if (index > 0 && contains(value, index, 4, "CHAE")) {
            result.append('K', 'X');
            return index + 2;
        } else if (conditionCH0(value, index)) {
            result.append('K');
            return index + 2;
        } else if (conditionCH1(value, index)) {
            result.append('K');
            return index + 2;
        } else {
            if (index > 0) {
                if (contains(value, 0, 2, "MC")) {
                    result.append('K');
                } else {
                    result.append('X', 'K');
                }
            } else {
                result.append('X');
            }
            return index + 2;
        }
    }

    private int handleD(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index, 2, "DG")) {
            if (contains(value, index + 2, 1, TokenNames.I, TokenNames.E, "Y")) {
                result.append('J');
                index2 = index + 3;
            } else {
                result.append("TK");
                index2 = index + 2;
            }
        } else if (contains(value, index, 2, "DT", "DD")) {
            result.append('T');
            index2 = index + 2;
        } else {
            result.append('T');
            index2 = index + 1;
        }
        return index2;
    }

    private int handleG(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (charAt(value, index + 1) == 'H') {
            index2 = handleGH(value, result, index);
        } else if (charAt(value, index + 1) == 'N') {
            if (index == 1 && isVowel(charAt(value, 0)) && !slavoGermanic) {
                result.append("KN", "N");
            } else if (!contains(value, index + 2, 2, "EY") && charAt(value, index + 1) != 'Y' && !slavoGermanic) {
                result.append("N", "KN");
            } else {
                result.append("KN");
            }
            index2 = index + 2;
        } else if (contains(value, index + 1, 2, "LI") && !slavoGermanic) {
            result.append("KL", TokenNames.L);
            index2 = index + 2;
        } else if (index == 0 && (charAt(value, index + 1) == 'Y' || contains(value, index + 1, 2, ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER))) {
            result.append('K', 'J');
            index2 = index + 2;
        } else if ((contains(value, index + 1, 2, "ER") || charAt(value, index + 1) == 'Y') && !contains(value, 0, 6, "DANGER", "RANGER", "MANGER") && !contains(value, index - 1, 1, TokenNames.E, TokenNames.I) && !contains(value, index - 1, 3, "RGY", "OGY")) {
            result.append('K', 'J');
            index2 = index + 2;
        } else if (contains(value, index + 1, 1, TokenNames.E, TokenNames.I, "Y") || contains(value, index - 1, 4, "AGGI", "OGGI")) {
            if (contains(value, 0, 4, "VAN ", "VON ") || contains(value, 0, 3, "SCH") || contains(value, index + 1, 2, "ET")) {
                result.append('K');
            } else if (contains(value, index + 1, 4, "IER")) {
                result.append('J');
            } else {
                result.append('J', 'K');
            }
            index2 = index + 2;
        } else if (charAt(value, index + 1) == 'G') {
            index2 = index + 2;
            result.append('K');
        } else {
            index2 = index + 1;
            result.append('K');
        }
        return index2;
    }

    private int handleGH(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (index > 0 && !isVowel(charAt(value, index - 1))) {
            result.append('K');
            index2 = index + 2;
        } else if (index == 0) {
            if (charAt(value, index + 2) == 'I') {
                result.append('J');
            } else {
                result.append('K');
            }
            index2 = index + 2;
        } else if ((index > 1 && contains(value, index - 2, 1, "B", "H", "D")) || ((index > 2 && contains(value, index - 3, 1, "B", "H", "D")) || (index > 3 && contains(value, index - 4, 1, "B", "H")))) {
            index2 = index + 2;
        } else {
            if (index > 2 && charAt(value, index - 1) == 'U' && contains(value, index - 3, 1, TokenNames.C, "G", TokenNames.L, TokenNames.R, TokenNames.T)) {
                result.append('F');
            } else if (index > 0 && charAt(value, index - 1) != 'I') {
                result.append('K');
            }
            index2 = index + 2;
        }
        return index2;
    }

    private int handleH(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if ((index == 0 || isVowel(charAt(value, index - 1))) && isVowel(charAt(value, index + 1))) {
            result.append('H');
            index2 = index + 2;
        } else {
            index2 = index + 1;
        }
        return index2;
    }

    private int handleJ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (contains(value, index, 4, "JOSE") || contains(value, 0, 4, "SAN ")) {
            if ((index == 0 && charAt(value, index + 4) == ' ') || value.length() == 4 || contains(value, 0, 4, "SAN ")) {
                result.append('H');
            } else {
                result.append('J', 'H');
            }
            index2 = index + 1;
        } else {
            if (index == 0 && !contains(value, index, 4, "JOSE")) {
                result.append('J', 'A');
            } else if (isVowel(charAt(value, index - 1)) && !slavoGermanic && (charAt(value, index + 1) == 'A' || charAt(value, index + 1) == 'O')) {
                result.append('J', 'H');
            } else if (index == value.length() - 1) {
                result.append('J', ' ');
            } else if (!contains(value, index + 1, 1, L_T_K_S_N_M_B_Z) && !contains(value, index - 1, 1, TokenNames.S, TokenNames.K, TokenNames.L)) {
                result.append('J');
            }
            if (charAt(value, index + 1) == 'J') {
                index2 = index + 2;
            } else {
                index2 = index + 1;
            }
        }
        return index2;
    }

    private int handleL(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        result.append('L');
        if (charAt(value, index + 1) == 'L') {
            if (conditionL0(value, index)) {
                result.appendAlternate(' ');
            }
            index2 = index + 2;
        } else {
            index2 = index + 1;
        }
        return index2;
    }

    private int handleP(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (charAt(value, index + 1) == 'H') {
            result.append('F');
            index2 = index + 2;
        } else {
            result.append('P');
            index2 = contains(value, index + 1, 1, "P", "B") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleR(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        if (index == value.length() - 1 && !slavoGermanic && contains(value, index - 2, 2, "IE") && !contains(value, index - 4, 2, "ME", "MA")) {
            result.appendAlternate('R');
        } else {
            result.append('R');
        }
        return charAt(value, index + 1) == 'R' ? index + 2 : index + 1;
    }

    private int handleS(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (contains(value, index - 1, 3, "ISL", "YSL")) {
            index2 = index + 1;
        } else if (index == 0 && contains(value, index, 5, "SUGAR")) {
            result.append('X', 'S');
            index2 = index + 1;
        } else if (contains(value, index, 2, "SH")) {
            if (contains(value, index + 1, 4, "HEIM", "HOEK", "HOLM", "HOLZ")) {
                result.append('S');
            } else {
                result.append('X');
            }
            index2 = index + 2;
        } else if (contains(value, index, 3, "SIO", "SIA") || contains(value, index, 4, "SIAN")) {
            if (slavoGermanic) {
                result.append('S');
            } else {
                result.append('S', 'X');
            }
            index2 = index + 3;
        } else if ((index == 0 && contains(value, index + 1, 1, TokenNames.M, "N", TokenNames.L, "W")) || contains(value, index + 1, 1, "Z")) {
            result.append('S', 'X');
            index2 = contains(value, index + 1, 1, "Z") ? index + 2 : index + 1;
        } else if (contains(value, index, 2, CommandsInterface.CB_FACILITY_BA_SIM)) {
            index2 = handleSC(value, result, index);
        } else {
            if (index == value.length() - 1 && contains(value, index - 2, 2, CommandsInterface.CB_FACILITY_BAIC, CommandsInterface.CB_FACILITY_BAOIC)) {
                result.appendAlternate('S');
            } else {
                result.append('S');
            }
            index2 = contains(value, index + 1, 1, TokenNames.S, "Z") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleSC(String value, DoubleMetaphoneResult result, int index) {
        if (charAt(value, index + 2) == 'H') {
            if (contains(value, index + 3, 2, "OO", "ER", "EN", "UY", "ED", "EM")) {
                if (contains(value, index + 3, 2, "ER", "EN")) {
                    result.append(TokenNames.X, "SK");
                } else {
                    result.append("SK");
                }
            } else if (index == 0 && !isVowel(charAt(value, 3)) && charAt(value, 3) != 'W') {
                result.append('X', 'S');
            } else {
                result.append('X');
            }
        } else if (contains(value, index + 2, 1, TokenNames.I, TokenNames.E, "Y")) {
            result.append('S');
        } else {
            result.append("SK");
        }
        return index + 3;
    }

    private int handleT(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index, 4, "TION")) {
            result.append('X');
            index2 = index + 3;
        } else if (contains(value, index, 3, "TIA", "TCH")) {
            result.append('X');
            index2 = index + 3;
        } else if (contains(value, index, 2, "TH") || contains(value, index, 3, "TTH")) {
            if (contains(value, index + 2, 2, "OM", "AM") || contains(value, 0, 4, "VAN ", "VON ") || contains(value, 0, 3, "SCH")) {
                result.append('T');
            } else {
                result.append('0', 'T');
            }
            index2 = index + 2;
        } else {
            result.append('T');
            index2 = contains(value, index + 1, 1, TokenNames.T, "D") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleW(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index, 2, "WR")) {
            result.append('R');
            index2 = index + 2;
        } else if (index == 0 && (isVowel(charAt(value, index + 1)) || contains(value, index, 2, "WH"))) {
            if (isVowel(charAt(value, index + 1))) {
                result.append('A', 'F');
            } else {
                result.append('A');
            }
            index2 = index + 1;
        } else if ((index == value.length() - 1 && isVowel(charAt(value, index - 1))) || contains(value, index - 1, 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY") || contains(value, 0, 3, "SCH")) {
            result.appendAlternate('F');
            index2 = index + 1;
        } else if (contains(value, index, 4, "WICZ", "WITZ")) {
            result.append("TS", "FX");
            index2 = index + 4;
        } else {
            index2 = index + 1;
        }
        return index2;
    }

    private int handleX(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (index == 0) {
            result.append('S');
            index2 = index + 1;
        } else {
            if (index != value.length() - 1 || (!contains(value, index - 3, 3, "IAU", "EAU") && !contains(value, index - 2, 2, "AU", "OU"))) {
                result.append("KS");
            }
            index2 = contains(value, index + 1, 1, TokenNames.C, TokenNames.X) ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleZ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (charAt(value, index + 1) == 'H') {
            result.append('J');
            index2 = index + 2;
        } else {
            if (contains(value, index + 1, 2, "ZO", "ZI", "ZA") || (slavoGermanic && index > 0 && charAt(value, index - 1) != 'T')) {
                result.append(TokenNames.S, "TS");
            } else {
                result.append('S');
            }
            index2 = charAt(value, index + 1) == 'Z' ? index + 2 : index + 1;
        }
        return index2;
    }

    private boolean conditionC0(String value, int index) {
        if (contains(value, index, 4, "CHIA")) {
            return true;
        }
        if (index <= 1 || isVowel(charAt(value, index - 2)) || !contains(value, index - 1, 3, "ACH")) {
            return false;
        }
        char c = charAt(value, index + 2);
        return !(c == 'I' || c == 'E') || contains(value, index - 2, 6, "BACHER", "MACHER");
    }

    private boolean conditionCH0(String value, int index) {
        if (index != 0) {
            return false;
        }
        if ((!contains(value, index + 1, 5, "HARAC", "HARIS") && !contains(value, index + 1, 3, "HOR", "HYM", "HIA", "HEM")) || contains(value, 0, 5, "CHORE")) {
            return false;
        }
        return true;
    }

    private boolean conditionCH1(String value, int index) {
        return contains(value, 0, 4, "VAN ", "VON ") || contains(value, 0, 3, "SCH") || contains(value, index - 2, 6, "ORCHES", "ARCHIT", "ORCHID") || contains(value, index + 2, 1, TokenNames.T, TokenNames.S) || ((contains(value, index - 1, 1, "A", TokenNames.O, TokenNames.U, TokenNames.E) || index == 0) && (contains(value, index + 2, 1, L_R_N_M_B_H_F_V_W_SPACE) || index + 1 == value.length() - 1));
    }

    private boolean conditionL0(String value, int index) {
        if (index == value.length() - 3 && contains(value, index - 1, 4, "ILLO", "ILLA", "ALLE")) {
            return true;
        }
        if ((contains(value, index - 1, 2, "AS", "OS") || contains(value, value.length() - 1, 1, "A", TokenNames.O)) && contains(value, index - 1, 4, "ALLE")) {
            return true;
        }
        return false;
    }

    private boolean conditionM0(String value, int index) {
        if (charAt(value, index + 1) == 'M') {
            return true;
        }
        return contains(value, index - 1, 3, "UMB") && (index + 1 == value.length() - 1 || contains(value, index + 2, 2, "ER"));
    }

    private boolean isSlavoGermanic(String value) {
        return value.indexOf(87) > -1 || value.indexOf(75) > -1 || value.indexOf("CZ") > -1 || value.indexOf("WITZ") > -1;
    }

    private boolean isVowel(char ch) {
        return VOWELS.indexOf(ch) != -1;
    }

    private boolean isSilentStart(String value) {
        boolean result = false;
        int i = 0;
        while (true) {
            if (i >= SILENT_START.length) {
                break;
            } else if (!value.startsWith(SILENT_START[i])) {
                i++;
            } else {
                result = true;
                break;
            }
        }
        return result;
    }

    private String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        String input2 = input.trim();
        if (input2.length() == 0) {
            return null;
        }
        return input2.toUpperCase();
    }

    protected char charAt(String value, int index) {
        if (index < 0 || index >= value.length()) {
            return (char) 0;
        }
        return value.charAt(index);
    }

    private static boolean contains(String value, int start, int length, String criteria) {
        return contains(value, start, length, new String[]{criteria});
    }

    private static boolean contains(String value, int start, int length, String criteria1, String criteria2) {
        return contains(value, start, length, new String[]{criteria1, criteria2});
    }

    private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3) {
        return contains(value, start, length, new String[]{criteria1, criteria2, criteria3});
    }

    private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3, String criteria4) {
        return contains(value, start, length, new String[]{criteria1, criteria2, criteria3, criteria4});
    }

    private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3, String criteria4, String criteria5) {
        return contains(value, start, length, new String[]{criteria1, criteria2, criteria3, criteria4, criteria5});
    }

    private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3, String criteria4, String criteria5, String criteria6) {
        return contains(value, start, length, new String[]{criteria1, criteria2, criteria3, criteria4, criteria5, criteria6});
    }

    protected static boolean contains(String value, int start, int length, String[] criteria) {
        boolean result = false;
        if (start >= 0 && start + length <= value.length()) {
            String target = value.substring(start, start + length);
            int i = 0;
            while (true) {
                if (i >= criteria.length) {
                    break;
                } else if (!target.equals(criteria[i])) {
                    i++;
                } else {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /* loaded from: DoubleMetaphone$DoubleMetaphoneResult.class */
    public class DoubleMetaphoneResult {
        private StringBuffer primary;
        private StringBuffer alternate;
        private int maxLength;

        public DoubleMetaphoneResult(int maxLength) {
            this.primary = new StringBuffer(DoubleMetaphone.this.getMaxCodeLen());
            this.alternate = new StringBuffer(DoubleMetaphone.this.getMaxCodeLen());
            this.maxLength = maxLength;
        }

        public void append(char value) {
            appendPrimary(value);
            appendAlternate(value);
        }

        public void append(char primary, char alternate) {
            appendPrimary(primary);
            appendAlternate(alternate);
        }

        public void appendPrimary(char value) {
            if (this.primary.length() < this.maxLength) {
                this.primary.append(value);
            }
        }

        public void appendAlternate(char value) {
            if (this.alternate.length() < this.maxLength) {
                this.alternate.append(value);
            }
        }

        public void append(String value) {
            appendPrimary(value);
            appendAlternate(value);
        }

        public void append(String primary, String alternate) {
            appendPrimary(primary);
            appendAlternate(alternate);
        }

        public void appendPrimary(String value) {
            int addChars = this.maxLength - this.primary.length();
            if (value.length() <= addChars) {
                this.primary.append(value);
            } else {
                this.primary.append(value.substring(0, addChars));
            }
        }

        public void appendAlternate(String value) {
            int addChars = this.maxLength - this.alternate.length();
            if (value.length() <= addChars) {
                this.alternate.append(value);
            } else {
                this.alternate.append(value.substring(0, addChars));
            }
        }

        public String getPrimary() {
            return this.primary.toString();
        }

        public String getAlternate() {
            return this.alternate.toString();
        }

        public boolean isComplete() {
            return this.primary.length() >= this.maxLength && this.alternate.length() >= this.maxLength;
        }
    }
}