package com.android.internal.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: InputMethodUtils.class */
public class InputMethodUtils {
    public static final boolean DEBUG = false;
    public static final int NOT_A_SUBTYPE_ID = -1;
    public static final String SUBTYPE_MODE_KEYBOARD = "keyboard";
    public static final String SUBTYPE_MODE_VOICE = "voice";
    private static final String TAG = "InputMethodUtils";
    private static final Locale ENGLISH_LOCALE = new Locale("en");
    private static final String NOT_A_SUBTYPE_ID_STR = String.valueOf(-1);
    private static final String TAG_ENABLED_WHEN_DEFAULT_IS_NOT_ASCII_CAPABLE = "EnabledWhenDefaultIsNotAsciiCapable";
    private static final String TAG_ASCII_CAPABLE = "AsciiCapable";

    private InputMethodUtils() {
    }

    public static String getStackTrace() {
        StringBuilder sb = new StringBuilder();
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            StackTraceElement[] frames = e.getStackTrace();
            for (int j = 1; j < frames.length; j++) {
                sb.append(frames[j].toString() + Separators.RETURN);
            }
            return sb.toString();
        }
    }

    public static String getApiCallStack() {
        String apiCallStack = "";
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            StackTraceElement[] frames = e.getStackTrace();
            for (int j = 1; j < frames.length; j++) {
                String tempCallStack = frames[j].toString();
                if (!TextUtils.isEmpty(apiCallStack) && tempCallStack.indexOf("Transact(") >= 0) {
                    break;
                }
                apiCallStack = tempCallStack;
            }
            return apiCallStack;
        }
    }

    public static boolean isSystemIme(InputMethodInfo inputMethod) {
        return (inputMethod.getServiceInfo().applicationInfo.flags & 1) != 0;
    }

    public static boolean isSystemImeThatHasEnglishKeyboardSubtype(InputMethodInfo imi) {
        if (!isSystemIme(imi)) {
            return false;
        }
        return containsSubtypeOf(imi, ENGLISH_LOCALE.getLanguage(), SUBTYPE_MODE_KEYBOARD);
    }

    private static boolean isSystemAuxilialyImeThatHashAutomaticSubtype(InputMethodInfo imi) {
        if (!isSystemIme(imi) || !imi.isAuxiliaryIme()) {
            return false;
        }
        int subtypeCount = imi.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            InputMethodSubtype s = imi.getSubtypeAt(i);
            if (s.overridesImplicitlyEnabledSubtype()) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<InputMethodInfo> getDefaultEnabledImes(Context context, boolean isSystemReady, ArrayList<InputMethodInfo> imis) {
        ArrayList<InputMethodInfo> retval = new ArrayList<>();
        boolean auxilialyImeAdded = false;
        for (int i = 0; i < imis.size(); i++) {
            InputMethodInfo imi = imis.get(i);
            if (isDefaultEnabledIme(isSystemReady, imi, context)) {
                retval.add(imi);
                if (imi.isAuxiliaryIme()) {
                    auxilialyImeAdded = true;
                }
            }
        }
        if (auxilialyImeAdded) {
            return retval;
        }
        for (int i2 = 0; i2 < imis.size(); i2++) {
            InputMethodInfo imi2 = imis.get(i2);
            if (isSystemAuxilialyImeThatHashAutomaticSubtype(imi2)) {
                retval.add(imi2);
            }
        }
        return retval;
    }

    public static boolean isValidSystemDefaultIme(boolean isSystemReady, InputMethodInfo imi, Context context) {
        if (!isSystemReady || !isSystemIme(imi)) {
            return false;
        }
        if (imi.getIsDefaultResourceId() != 0) {
            try {
                if (imi.isDefault(context)) {
                    if (containsSubtypeOf(imi, context.getResources().getConfiguration().locale.getLanguage(), null)) {
                        return true;
                    }
                }
            } catch (Resources.NotFoundException e) {
            }
        }
        if (imi.getSubtypeCount() == 0) {
            Slog.w(TAG, "Found no subtypes in a system IME: " + imi.getPackageName());
            return false;
        }
        return false;
    }

    public static boolean isDefaultEnabledIme(boolean isSystemReady, InputMethodInfo imi, Context context) {
        return isValidSystemDefaultIme(isSystemReady, imi, context) || isSystemImeThatHasEnglishKeyboardSubtype(imi);
    }

    public static boolean containsSubtypeOf(InputMethodInfo imi, String language, String mode) {
        int N = imi.getSubtypeCount();
        for (int i = 0; i < N; i++) {
            if (imi.getSubtypeAt(i).getLocale().startsWith(language) && (TextUtils.isEmpty(mode) || imi.getSubtypeAt(i).getMode().equalsIgnoreCase(mode))) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<InputMethodSubtype> getSubtypes(InputMethodInfo imi) {
        ArrayList<InputMethodSubtype> subtypes = new ArrayList<>();
        int subtypeCount = imi.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            subtypes.add(imi.getSubtypeAt(i));
        }
        return subtypes;
    }

    public static ArrayList<InputMethodSubtype> getOverridingImplicitlyEnabledSubtypes(InputMethodInfo imi, String mode) {
        ArrayList<InputMethodSubtype> subtypes = new ArrayList<>();
        int subtypeCount = imi.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            InputMethodSubtype subtype = imi.getSubtypeAt(i);
            if (subtype.overridesImplicitlyEnabledSubtype() && subtype.getMode().equals(mode)) {
                subtypes.add(subtype);
            }
        }
        return subtypes;
    }

    public static InputMethodInfo getMostApplicableDefaultIME(List<InputMethodInfo> enabledImes) {
        if (enabledImes != null && enabledImes.size() > 0) {
            int i = enabledImes.size();
            int firstFoundSystemIme = -1;
            while (i > 0) {
                i--;
                InputMethodInfo imi = enabledImes.get(i);
                if (isSystemImeThatHasEnglishKeyboardSubtype(imi) && !imi.isAuxiliaryIme()) {
                    return imi;
                }
                if (firstFoundSystemIme < 0 && isSystemIme(imi) && !imi.isAuxiliaryIme()) {
                    firstFoundSystemIme = i;
                }
            }
            return enabledImes.get(Math.max(firstFoundSystemIme, 0));
        }
        return null;
    }

    public static boolean isValidSubtypeId(InputMethodInfo imi, int subtypeHashCode) {
        return getSubtypeIdFromHashCode(imi, subtypeHashCode) != -1;
    }

    public static int getSubtypeIdFromHashCode(InputMethodInfo imi, int subtypeHashCode) {
        if (imi != null) {
            int subtypeCount = imi.getSubtypeCount();
            for (int i = 0; i < subtypeCount; i++) {
                InputMethodSubtype ims = imi.getSubtypeAt(i);
                if (subtypeHashCode == ims.hashCode()) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ArrayList<InputMethodSubtype> getImplicitlyApplicableSubtypesLocked(Resources res, InputMethodInfo imi) {
        InputMethodSubtype lastResortKeyboardSubtype;
        InputMethodSubtype applicableSubtype;
        List<InputMethodSubtype> subtypes = getSubtypes(imi);
        String systemLocale = res.getConfiguration().locale.toString();
        if (TextUtils.isEmpty(systemLocale)) {
            return new ArrayList<>();
        }
        HashMap<String, InputMethodSubtype> applicableModeAndSubtypesMap = new HashMap<>();
        int N = subtypes.size();
        for (int i = 0; i < N; i++) {
            InputMethodSubtype subtype = subtypes.get(i);
            if (subtype.overridesImplicitlyEnabledSubtype()) {
                String mode = subtype.getMode();
                if (!applicableModeAndSubtypesMap.containsKey(mode)) {
                    applicableModeAndSubtypesMap.put(mode, subtype);
                }
            }
        }
        if (applicableModeAndSubtypesMap.size() > 0) {
            return new ArrayList<>(applicableModeAndSubtypesMap.values());
        }
        for (int i2 = 0; i2 < N; i2++) {
            InputMethodSubtype subtype2 = subtypes.get(i2);
            String locale = subtype2.getLocale();
            String mode2 = subtype2.getMode();
            if (systemLocale.startsWith(locale) && ((applicableSubtype = applicableModeAndSubtypesMap.get(mode2)) == null || (!systemLocale.equals(applicableSubtype.getLocale()) && systemLocale.equals(locale)))) {
                applicableModeAndSubtypesMap.put(mode2, subtype2);
            }
        }
        InputMethodSubtype keyboardSubtype = applicableModeAndSubtypesMap.get(SUBTYPE_MODE_KEYBOARD);
        ArrayList<InputMethodSubtype> applicableSubtypes = new ArrayList<>(applicableModeAndSubtypesMap.values());
        if (keyboardSubtype != null && !keyboardSubtype.containsExtraValueKey(TAG_ASCII_CAPABLE)) {
            for (int i3 = 0; i3 < N; i3++) {
                InputMethodSubtype subtype3 = subtypes.get(i3);
                if (SUBTYPE_MODE_KEYBOARD.equals(subtype3.getMode()) && subtype3.containsExtraValueKey(TAG_ENABLED_WHEN_DEFAULT_IS_NOT_ASCII_CAPABLE)) {
                    applicableSubtypes.add(subtype3);
                }
            }
        }
        if (keyboardSubtype == null && (lastResortKeyboardSubtype = findLastResortApplicableSubtypeLocked(res, subtypes, SUBTYPE_MODE_KEYBOARD, systemLocale, true)) != null) {
            applicableSubtypes.add(lastResortKeyboardSubtype);
        }
        return applicableSubtypes;
    }

    private static List<InputMethodSubtype> getEnabledInputMethodSubtypeList(Context context, InputMethodInfo imi, List<InputMethodSubtype> enabledSubtypes, boolean allowsImplicitlySelectedSubtypes) {
        if (allowsImplicitlySelectedSubtypes && enabledSubtypes.isEmpty()) {
            enabledSubtypes = getImplicitlyApplicableSubtypesLocked(context.getResources(), imi);
        }
        return InputMethodSubtype.sort(context, 0, imi, enabledSubtypes);
    }

    public static InputMethodSubtype findLastResortApplicableSubtypeLocked(Resources res, List<InputMethodSubtype> subtypes, String mode, String locale, boolean canIgnoreLocaleAsLastResort) {
        if (subtypes == null || subtypes.size() == 0) {
            return null;
        }
        if (TextUtils.isEmpty(locale)) {
            locale = res.getConfiguration().locale.toString();
        }
        String language = locale.substring(0, 2);
        boolean partialMatchFound = false;
        InputMethodSubtype applicableSubtype = null;
        InputMethodSubtype firstMatchedModeSubtype = null;
        int N = subtypes.size();
        int i = 0;
        while (true) {
            if (i >= N) {
                break;
            }
            InputMethodSubtype subtype = subtypes.get(i);
            String subtypeLocale = subtype.getLocale();
            if (mode == null || subtypes.get(i).getMode().equalsIgnoreCase(mode)) {
                if (firstMatchedModeSubtype == null) {
                    firstMatchedModeSubtype = subtype;
                }
                if (locale.equals(subtypeLocale)) {
                    applicableSubtype = subtype;
                    break;
                } else if (!partialMatchFound && subtypeLocale.startsWith(language)) {
                    applicableSubtype = subtype;
                    partialMatchFound = true;
                }
            }
            i++;
        }
        if (applicableSubtype == null && canIgnoreLocaleAsLastResort) {
            return firstMatchedModeSubtype;
        }
        return applicableSubtype;
    }

    public static boolean canAddToLastInputMethod(InputMethodSubtype subtype) {
        return subtype == null || !subtype.isAuxiliary();
    }

    public static void setNonSelectedSystemImesDisabledUntilUsed(PackageManager packageManager, List<InputMethodInfo> enabledImis) {
        String[] systemImesDisabledUntilUsed = Resources.getSystem().getStringArray(R.array.config_disabledUntilUsedPreinstalledImes);
        if (systemImesDisabledUntilUsed == null || systemImesDisabledUntilUsed.length == 0) {
            return;
        }
        SpellCheckerInfo currentSpellChecker = TextServicesManager.getInstance().getCurrentSpellChecker();
        for (String packageName : systemImesDisabledUntilUsed) {
            boolean enabledIme = false;
            int j = 0;
            while (true) {
                if (j >= enabledImis.size()) {
                    break;
                }
                InputMethodInfo imi = enabledImis.get(j);
                if (!packageName.equals(imi.getPackageName())) {
                    j++;
                } else {
                    enabledIme = true;
                    break;
                }
            }
            if (!enabledIme && (currentSpellChecker == null || !packageName.equals(currentSpellChecker.getPackageName()))) {
                ApplicationInfo ai = null;
                try {
                    ai = packageManager.getApplicationInfo(packageName, 32768);
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(TAG, "NameNotFoundException: " + packageName, e);
                }
                if (ai != null) {
                    boolean isSystemPackage = (ai.flags & 1) != 0;
                    if (isSystemPackage) {
                        setDisabledUntilUsed(packageManager, packageName);
                    }
                }
            }
        }
    }

    private static void setDisabledUntilUsed(PackageManager packageManager, String packageName) {
        int state = packageManager.getApplicationEnabledSetting(packageName);
        if (state == 0 || state == 1) {
            packageManager.setApplicationEnabledSetting(packageName, 4, 0);
        }
    }

    public static CharSequence getImeAndSubtypeDisplayName(Context context, InputMethodInfo imi, InputMethodSubtype subtype) {
        CharSequence imiLabel = imi.loadLabel(context.getPackageManager());
        if (subtype != null) {
            CharSequence[] charSequenceArr = new CharSequence[2];
            charSequenceArr[0] = subtype.getDisplayName(context, imi.getPackageName(), imi.getServiceInfo().applicationInfo);
            charSequenceArr[1] = TextUtils.isEmpty(imiLabel) ? "" : " - " + ((Object) imiLabel);
            return TextUtils.concat(charSequenceArr);
        }
        return imiLabel;
    }

    /* loaded from: InputMethodUtils$InputMethodSettings.class */
    public static class InputMethodSettings {
        private static final char INPUT_METHOD_SEPARATER = ':';
        private static final char INPUT_METHOD_SUBTYPE_SEPARATER = ';';
        private final TextUtils.SimpleStringSplitter mInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
        private final TextUtils.SimpleStringSplitter mSubtypeSplitter = new TextUtils.SimpleStringSplitter(';');
        private final Resources mRes;
        private final ContentResolver mResolver;
        private final HashMap<String, InputMethodInfo> mMethodMap;
        private final ArrayList<InputMethodInfo> mMethodList;
        private String mEnabledInputMethodsStrCache;
        private int mCurrentUserId;

        private static void buildEnabledInputMethodsSettingString(StringBuilder builder, Pair<String, ArrayList<String>> pair) {
            String id = pair.first;
            ArrayList<String> subtypes = pair.second;
            builder.append(id);
            Iterator i$ = subtypes.iterator();
            while (i$.hasNext()) {
                String subtypeId = i$.next();
                builder.append(';').append(subtypeId);
            }
        }

        public InputMethodSettings(Resources res, ContentResolver resolver, HashMap<String, InputMethodInfo> methodMap, ArrayList<InputMethodInfo> methodList, int userId) {
            setCurrentUserId(userId);
            this.mRes = res;
            this.mResolver = resolver;
            this.mMethodMap = methodMap;
            this.mMethodList = methodList;
        }

        public void setCurrentUserId(int userId) {
            this.mCurrentUserId = userId;
        }

        public List<InputMethodInfo> getEnabledInputMethodListLocked() {
            return createEnabledInputMethodListLocked(getEnabledInputMethodsAndSubtypeListLocked());
        }

        public List<Pair<InputMethodInfo, ArrayList<String>>> getEnabledInputMethodAndSubtypeHashCodeListLocked() {
            return createEnabledInputMethodAndSubtypeHashCodeListLocked(getEnabledInputMethodsAndSubtypeListLocked());
        }

        public List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(Context context, InputMethodInfo imi, boolean allowsImplicitlySelectedSubtypes) {
            List<InputMethodSubtype> enabledSubtypes = getEnabledInputMethodSubtypeListLocked(imi);
            if (allowsImplicitlySelectedSubtypes && enabledSubtypes.isEmpty()) {
                enabledSubtypes = InputMethodUtils.getImplicitlyApplicableSubtypesLocked(context.getResources(), imi);
            }
            return InputMethodSubtype.sort(context, 0, imi, enabledSubtypes);
        }

        public List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(InputMethodInfo imi) {
            List<Pair<String, ArrayList<String>>> imsList = getEnabledInputMethodsAndSubtypeListLocked();
            ArrayList<InputMethodSubtype> enabledSubtypes = new ArrayList<>();
            if (imi != null) {
                Iterator i$ = imsList.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    Pair<String, ArrayList<String>> imsPair = i$.next();
                    InputMethodInfo info = this.mMethodMap.get(imsPair.first);
                    if (info != null && info.getId().equals(imi.getId())) {
                        int subtypeCount = info.getSubtypeCount();
                        for (int i = 0; i < subtypeCount; i++) {
                            InputMethodSubtype ims = info.getSubtypeAt(i);
                            Iterator i$2 = imsPair.second.iterator();
                            while (i$2.hasNext()) {
                                String s = i$2.next();
                                if (String.valueOf(ims.hashCode()).equals(s)) {
                                    enabledSubtypes.add(ims);
                                }
                            }
                        }
                    }
                }
            }
            return enabledSubtypes;
        }

        public void enableAllIMEsIfThereIsNoEnabledIME() {
            if (TextUtils.isEmpty(getEnabledInputMethodsStr())) {
                StringBuilder sb = new StringBuilder();
                int N = this.mMethodList.size();
                for (int i = 0; i < N; i++) {
                    InputMethodInfo imi = this.mMethodList.get(i);
                    Slog.i(InputMethodUtils.TAG, "Adding: " + imi.getId());
                    if (i > 0) {
                        sb.append(':');
                    }
                    sb.append(imi.getId());
                }
                putEnabledInputMethodsStr(sb.toString());
            }
        }

        public List<Pair<String, ArrayList<String>>> getEnabledInputMethodsAndSubtypeListLocked() {
            ArrayList<Pair<String, ArrayList<String>>> imsList = new ArrayList<>();
            String enabledInputMethodsStr = getEnabledInputMethodsStr();
            if (TextUtils.isEmpty(enabledInputMethodsStr)) {
                return imsList;
            }
            this.mInputMethodSplitter.setString(enabledInputMethodsStr);
            while (this.mInputMethodSplitter.hasNext()) {
                String nextImsStr = this.mInputMethodSplitter.next();
                this.mSubtypeSplitter.setString(nextImsStr);
                if (this.mSubtypeSplitter.hasNext()) {
                    ArrayList<String> subtypeHashes = new ArrayList<>();
                    String imeId = this.mSubtypeSplitter.next();
                    while (this.mSubtypeSplitter.hasNext()) {
                        subtypeHashes.add(this.mSubtypeSplitter.next());
                    }
                    imsList.add(new Pair<>(imeId, subtypeHashes));
                }
            }
            return imsList;
        }

        public void appendAndPutEnabledInputMethodLocked(String id, boolean reloadInputMethodStr) {
            if (reloadInputMethodStr) {
                getEnabledInputMethodsStr();
            }
            if (TextUtils.isEmpty(this.mEnabledInputMethodsStrCache)) {
                putEnabledInputMethodsStr(id);
            } else {
                putEnabledInputMethodsStr(this.mEnabledInputMethodsStrCache + ':' + id);
            }
        }

        public boolean buildAndPutEnabledInputMethodsStrRemovingIdLocked(StringBuilder builder, List<Pair<String, ArrayList<String>>> imsList, String id) {
            boolean isRemoved = false;
            boolean needsAppendSeparator = false;
            for (Pair<String, ArrayList<String>> ims : imsList) {
                String curId = ims.first;
                if (curId.equals(id)) {
                    isRemoved = true;
                } else {
                    if (needsAppendSeparator) {
                        builder.append(':');
                    } else {
                        needsAppendSeparator = true;
                    }
                    buildEnabledInputMethodsSettingString(builder, ims);
                }
            }
            if (isRemoved) {
                putEnabledInputMethodsStr(builder.toString());
            }
            return isRemoved;
        }

        private List<InputMethodInfo> createEnabledInputMethodListLocked(List<Pair<String, ArrayList<String>>> imsList) {
            ArrayList<InputMethodInfo> res = new ArrayList<>();
            for (Pair<String, ArrayList<String>> ims : imsList) {
                InputMethodInfo info = this.mMethodMap.get(ims.first);
                if (info != null) {
                    res.add(info);
                }
            }
            return res;
        }

        private List<Pair<InputMethodInfo, ArrayList<String>>> createEnabledInputMethodAndSubtypeHashCodeListLocked(List<Pair<String, ArrayList<String>>> imsList) {
            ArrayList<Pair<InputMethodInfo, ArrayList<String>>> res = new ArrayList<>();
            for (Pair<String, ArrayList<String>> ims : imsList) {
                InputMethodInfo info = this.mMethodMap.get(ims.first);
                if (info != null) {
                    res.add(new Pair<>(info, ims.second));
                }
            }
            return res;
        }

        private void putEnabledInputMethodsStr(String str) {
            Settings.Secure.putStringForUser(this.mResolver, Settings.Secure.ENABLED_INPUT_METHODS, str, this.mCurrentUserId);
            this.mEnabledInputMethodsStrCache = str;
        }

        public String getEnabledInputMethodsStr() {
            this.mEnabledInputMethodsStrCache = Settings.Secure.getStringForUser(this.mResolver, Settings.Secure.ENABLED_INPUT_METHODS, this.mCurrentUserId);
            return this.mEnabledInputMethodsStrCache;
        }

        private void saveSubtypeHistory(List<Pair<String, String>> savedImes, String newImeId, String newSubtypeId) {
            StringBuilder builder = new StringBuilder();
            boolean isImeAdded = false;
            if (!TextUtils.isEmpty(newImeId) && !TextUtils.isEmpty(newSubtypeId)) {
                builder.append(newImeId).append(';').append(newSubtypeId);
                isImeAdded = true;
            }
            for (Pair<String, String> ime : savedImes) {
                String imeId = ime.first;
                String subtypeId = ime.second;
                if (TextUtils.isEmpty(subtypeId)) {
                    subtypeId = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                }
                if (isImeAdded) {
                    builder.append(':');
                } else {
                    isImeAdded = true;
                }
                builder.append(imeId).append(';').append(subtypeId);
            }
            putSubtypeHistoryStr(builder.toString());
        }

        private void addSubtypeToHistory(String imeId, String subtypeId) {
            List<Pair<String, String>> subtypeHistory = loadInputMethodAndSubtypeHistoryLocked();
            Iterator i$ = subtypeHistory.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Pair<String, String> ime = i$.next();
                if (ime.first.equals(imeId)) {
                    subtypeHistory.remove(ime);
                    break;
                }
            }
            saveSubtypeHistory(subtypeHistory, imeId, subtypeId);
        }

        private void putSubtypeHistoryStr(String str) {
            Settings.Secure.putStringForUser(this.mResolver, Settings.Secure.INPUT_METHODS_SUBTYPE_HISTORY, str, this.mCurrentUserId);
        }

        public Pair<String, String> getLastInputMethodAndSubtypeLocked() {
            return getLastSubtypeForInputMethodLockedInternal(null);
        }

        public String getLastSubtypeForInputMethodLocked(String imeId) {
            Pair<String, String> ime = getLastSubtypeForInputMethodLockedInternal(imeId);
            if (ime != null) {
                return ime.second;
            }
            return null;
        }

        private Pair<String, String> getLastSubtypeForInputMethodLockedInternal(String imeId) {
            List<Pair<String, ArrayList<String>>> enabledImes = getEnabledInputMethodsAndSubtypeListLocked();
            List<Pair<String, String>> subtypeHistory = loadInputMethodAndSubtypeHistoryLocked();
            for (Pair<String, String> imeAndSubtype : subtypeHistory) {
                String imeInTheHistory = imeAndSubtype.first;
                if (TextUtils.isEmpty(imeId) || imeInTheHistory.equals(imeId)) {
                    String subtypeInTheHistory = imeAndSubtype.second;
                    String subtypeHashCode = getEnabledSubtypeHashCodeForInputMethodAndSubtypeLocked(enabledImes, imeInTheHistory, subtypeInTheHistory);
                    if (!TextUtils.isEmpty(subtypeHashCode)) {
                        return new Pair<>(imeInTheHistory, subtypeHashCode);
                    }
                }
            }
            return null;
        }

        private String getEnabledSubtypeHashCodeForInputMethodAndSubtypeLocked(List<Pair<String, ArrayList<String>>> enabledImes, String imeId, String subtypeHashCode) {
            List<InputMethodSubtype> implicitlySelectedSubtypes;
            for (Pair<String, ArrayList<String>> enabledIme : enabledImes) {
                if (enabledIme.first.equals(imeId)) {
                    ArrayList<String> explicitlyEnabledSubtypes = enabledIme.second;
                    InputMethodInfo imi = this.mMethodMap.get(imeId);
                    if (explicitlyEnabledSubtypes.size() == 0) {
                        if (imi != null && imi.getSubtypeCount() > 0 && (implicitlySelectedSubtypes = InputMethodUtils.getImplicitlyApplicableSubtypesLocked(this.mRes, imi)) != null) {
                            int N = implicitlySelectedSubtypes.size();
                            for (int i = 0; i < N; i++) {
                                InputMethodSubtype st = implicitlySelectedSubtypes.get(i);
                                if (String.valueOf(st.hashCode()).equals(subtypeHashCode)) {
                                    return subtypeHashCode;
                                }
                            }
                        }
                    } else {
                        Iterator i$ = explicitlyEnabledSubtypes.iterator();
                        while (i$.hasNext()) {
                            String s = i$.next();
                            if (s.equals(subtypeHashCode)) {
                                try {
                                    int hashCode = Integer.valueOf(subtypeHashCode).intValue();
                                    if (!InputMethodUtils.isValidSubtypeId(imi, hashCode)) {
                                        return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                                    }
                                    return s;
                                } catch (NumberFormatException e) {
                                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                                }
                            }
                        }
                    }
                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                }
            }
            return null;
        }

        private List<Pair<String, String>> loadInputMethodAndSubtypeHistoryLocked() {
            ArrayList<Pair<String, String>> imsList = new ArrayList<>();
            String subtypeHistoryStr = getSubtypeHistoryStr();
            if (TextUtils.isEmpty(subtypeHistoryStr)) {
                return imsList;
            }
            this.mInputMethodSplitter.setString(subtypeHistoryStr);
            while (this.mInputMethodSplitter.hasNext()) {
                String nextImsStr = this.mInputMethodSplitter.next();
                this.mSubtypeSplitter.setString(nextImsStr);
                if (this.mSubtypeSplitter.hasNext()) {
                    String subtypeId = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                    String imeId = this.mSubtypeSplitter.next();
                    if (this.mSubtypeSplitter.hasNext()) {
                        subtypeId = this.mSubtypeSplitter.next();
                    }
                    imsList.add(new Pair<>(imeId, subtypeId));
                }
            }
            return imsList;
        }

        private String getSubtypeHistoryStr() {
            return Settings.Secure.getStringForUser(this.mResolver, Settings.Secure.INPUT_METHODS_SUBTYPE_HISTORY, this.mCurrentUserId);
        }

        public void putSelectedInputMethod(String imeId) {
            Settings.Secure.putStringForUser(this.mResolver, Settings.Secure.DEFAULT_INPUT_METHOD, imeId, this.mCurrentUserId);
        }

        public void putSelectedSubtype(int subtypeId) {
            Settings.Secure.putIntForUser(this.mResolver, Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE, subtypeId, this.mCurrentUserId);
        }

        public String getDisabledSystemInputMethods() {
            return Settings.Secure.getStringForUser(this.mResolver, Settings.Secure.DISABLED_SYSTEM_INPUT_METHODS, this.mCurrentUserId);
        }

        public String getSelectedInputMethod() {
            return Settings.Secure.getStringForUser(this.mResolver, Settings.Secure.DEFAULT_INPUT_METHOD, this.mCurrentUserId);
        }

        public boolean isSubtypeSelected() {
            return getSelectedInputMethodSubtypeHashCode() != -1;
        }

        private int getSelectedInputMethodSubtypeHashCode() {
            try {
                return Settings.Secure.getIntForUser(this.mResolver, Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE, this.mCurrentUserId);
            } catch (Settings.SettingNotFoundException e) {
                return -1;
            }
        }

        public int getCurrentUserId() {
            return this.mCurrentUserId;
        }

        public int getSelectedInputMethodSubtypeId(String selectedImiId) {
            InputMethodInfo imi = this.mMethodMap.get(selectedImiId);
            if (imi == null) {
                return -1;
            }
            int subtypeHashCode = getSelectedInputMethodSubtypeHashCode();
            return InputMethodUtils.getSubtypeIdFromHashCode(imi, subtypeHashCode);
        }

        public void saveCurrentInputMethodAndSubtypeToHistory(String curMethodId, InputMethodSubtype currentSubtype) {
            String subtypeId = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
            if (currentSubtype != null) {
                subtypeId = String.valueOf(currentSubtype.hashCode());
            }
            if (InputMethodUtils.canAddToLastInputMethod(currentSubtype)) {
                addSubtypeToHistory(curMethodId, subtypeId);
            }
        }
    }
}