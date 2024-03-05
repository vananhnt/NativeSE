package android.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.util.AndroidException;
import android.util.Log;
import android.util.Printer;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: IntentFilter.class */
public class IntentFilter implements Parcelable {
    private static final String SGLOB_STR = "sglob";
    private static final String PREFIX_STR = "prefix";
    private static final String LITERAL_STR = "literal";
    private static final String PATH_STR = "path";
    private static final String PORT_STR = "port";
    private static final String HOST_STR = "host";
    private static final String AUTH_STR = "auth";
    private static final String SSP_STR = "ssp";
    private static final String SCHEME_STR = "scheme";
    private static final String TYPE_STR = "type";
    private static final String CAT_STR = "cat";
    private static final String NAME_STR = "name";
    private static final String ACTION_STR = "action";
    public static final int SYSTEM_HIGH_PRIORITY = 1000;
    public static final int SYSTEM_LOW_PRIORITY = -1000;
    public static final int MATCH_CATEGORY_MASK = 268369920;
    public static final int MATCH_ADJUSTMENT_MASK = 65535;
    public static final int MATCH_ADJUSTMENT_NORMAL = 32768;
    public static final int MATCH_CATEGORY_EMPTY = 1048576;
    public static final int MATCH_CATEGORY_SCHEME = 2097152;
    public static final int MATCH_CATEGORY_HOST = 3145728;
    public static final int MATCH_CATEGORY_PORT = 4194304;
    public static final int MATCH_CATEGORY_PATH = 5242880;
    public static final int MATCH_CATEGORY_SCHEME_SPECIFIC_PART = 5767168;
    public static final int MATCH_CATEGORY_TYPE = 6291456;
    public static final int NO_MATCH_TYPE = -1;
    public static final int NO_MATCH_DATA = -2;
    public static final int NO_MATCH_ACTION = -3;
    public static final int NO_MATCH_CATEGORY = -4;
    private int mPriority;
    private final ArrayList<String> mActions;
    private ArrayList<String> mCategories;
    private ArrayList<String> mDataSchemes;
    private ArrayList<PatternMatcher> mDataSchemeSpecificParts;
    private ArrayList<AuthorityEntry> mDataAuthorities;
    private ArrayList<PatternMatcher> mDataPaths;
    private ArrayList<String> mDataTypes;
    private boolean mHasPartialTypes;
    public static final Parcelable.Creator<IntentFilter> CREATOR = new Parcelable.Creator<IntentFilter>() { // from class: android.content.IntentFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentFilter createFromParcel(Parcel source) {
            return new IntentFilter(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentFilter[] newArray(int size) {
            return new IntentFilter[size];
        }
    };

    private static int findStringInSet(String[] set, String string, int[] lengths, int lenPos) {
        if (set == null) {
            return -1;
        }
        int N = lengths[lenPos];
        for (int i = 0; i < N; i++) {
            if (set[i].equals(string)) {
                return i;
            }
        }
        return -1;
    }

    private static String[] addStringToSet(String[] set, String string, int[] lengths, int lenPos) {
        if (findStringInSet(set, string, lengths, lenPos) >= 0) {
            return set;
        }
        if (set == null) {
            String[] set2 = new String[2];
            set2[0] = string;
            lengths[lenPos] = 1;
            return set2;
        }
        int N = lengths[lenPos];
        if (N < set.length) {
            set[N] = string;
            lengths[lenPos] = N + 1;
            return set;
        }
        String[] newSet = new String[((N * 3) / 2) + 2];
        System.arraycopy(set, 0, newSet, 0, N);
        newSet[N] = string;
        lengths[lenPos] = N + 1;
        return newSet;
    }

    private static String[] removeStringFromSet(String[] set, String string, int[] lengths, int lenPos) {
        int pos = findStringInSet(set, string, lengths, lenPos);
        if (pos < 0) {
            return set;
        }
        int N = lengths[lenPos];
        if (N > set.length / 4) {
            int copyLen = N - (pos + 1);
            if (copyLen > 0) {
                System.arraycopy(set, pos + 1, set, pos, copyLen);
            }
            set[N - 1] = null;
            lengths[lenPos] = N - 1;
            return set;
        }
        String[] newSet = new String[set.length / 3];
        if (pos > 0) {
            System.arraycopy(set, 0, newSet, 0, pos);
        }
        if (pos + 1 < N) {
            System.arraycopy(set, pos + 1, newSet, pos, N - (pos + 1));
        }
        return newSet;
    }

    /* loaded from: IntentFilter$MalformedMimeTypeException.class */
    public static class MalformedMimeTypeException extends AndroidException {
        public MalformedMimeTypeException() {
        }

        public MalformedMimeTypeException(String name) {
            super(name);
        }
    }

    public static IntentFilter create(String action, String dataType) {
        try {
            return new IntentFilter(action, dataType);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Bad MIME type", e);
        }
    }

    public IntentFilter() {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList<>();
    }

    public IntentFilter(String action) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList<>();
        addAction(action);
    }

    public IntentFilter(String action, String dataType) throws MalformedMimeTypeException {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList<>();
        addAction(action);
        addDataType(dataType);
    }

    public IntentFilter(IntentFilter o) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = o.mPriority;
        this.mActions = new ArrayList<>(o.mActions);
        if (o.mCategories != null) {
            this.mCategories = new ArrayList<>(o.mCategories);
        }
        if (o.mDataTypes != null) {
            this.mDataTypes = new ArrayList<>(o.mDataTypes);
        }
        if (o.mDataSchemes != null) {
            this.mDataSchemes = new ArrayList<>(o.mDataSchemes);
        }
        if (o.mDataSchemeSpecificParts != null) {
            this.mDataSchemeSpecificParts = new ArrayList<>(o.mDataSchemeSpecificParts);
        }
        if (o.mDataAuthorities != null) {
            this.mDataAuthorities = new ArrayList<>(o.mDataAuthorities);
        }
        if (o.mDataPaths != null) {
            this.mDataPaths = new ArrayList<>(o.mDataPaths);
        }
        this.mHasPartialTypes = o.mHasPartialTypes;
    }

    public final void setPriority(int priority) {
        this.mPriority = priority;
    }

    public final int getPriority() {
        return this.mPriority;
    }

    public final void addAction(String action) {
        if (!this.mActions.contains(action)) {
            this.mActions.add(action.intern());
        }
    }

    public final int countActions() {
        return this.mActions.size();
    }

    public final String getAction(int index) {
        return this.mActions.get(index);
    }

    public final boolean hasAction(String action) {
        return action != null && this.mActions.contains(action);
    }

    public final boolean matchAction(String action) {
        return hasAction(action);
    }

    public final Iterator<String> actionsIterator() {
        if (this.mActions != null) {
            return this.mActions.iterator();
        }
        return null;
    }

    public final void addDataType(String type) throws MalformedMimeTypeException {
        int slashpos = type.indexOf(47);
        int typelen = type.length();
        if (slashpos > 0 && typelen >= slashpos + 2) {
            if (this.mDataTypes == null) {
                this.mDataTypes = new ArrayList<>();
            }
            if (typelen == slashpos + 2 && type.charAt(slashpos + 1) == '*') {
                String str = type.substring(0, slashpos);
                if (!this.mDataTypes.contains(str)) {
                    this.mDataTypes.add(str.intern());
                }
                this.mHasPartialTypes = true;
                return;
            } else if (!this.mDataTypes.contains(type)) {
                this.mDataTypes.add(type.intern());
                return;
            } else {
                return;
            }
        }
        throw new MalformedMimeTypeException(type);
    }

    public final boolean hasDataType(String type) {
        return this.mDataTypes != null && findMimeType(type);
    }

    public final int countDataTypes() {
        if (this.mDataTypes != null) {
            return this.mDataTypes.size();
        }
        return 0;
    }

    public final String getDataType(int index) {
        return this.mDataTypes.get(index);
    }

    public final Iterator<String> typesIterator() {
        if (this.mDataTypes != null) {
            return this.mDataTypes.iterator();
        }
        return null;
    }

    public final void addDataScheme(String scheme) {
        if (this.mDataSchemes == null) {
            this.mDataSchemes = new ArrayList<>();
        }
        if (!this.mDataSchemes.contains(scheme)) {
            this.mDataSchemes.add(scheme.intern());
        }
    }

    public final int countDataSchemes() {
        if (this.mDataSchemes != null) {
            return this.mDataSchemes.size();
        }
        return 0;
    }

    public final String getDataScheme(int index) {
        return this.mDataSchemes.get(index);
    }

    public final boolean hasDataScheme(String scheme) {
        return this.mDataSchemes != null && this.mDataSchemes.contains(scheme);
    }

    public final Iterator<String> schemesIterator() {
        if (this.mDataSchemes != null) {
            return this.mDataSchemes.iterator();
        }
        return null;
    }

    /* loaded from: IntentFilter$AuthorityEntry.class */
    public static final class AuthorityEntry {
        private final String mOrigHost;
        private final String mHost;
        private final boolean mWild;
        private final int mPort;

        public AuthorityEntry(String host, String port) {
            this.mOrigHost = host;
            this.mWild = host.length() > 0 && host.charAt(0) == '*';
            this.mHost = this.mWild ? host.substring(1).intern() : host;
            this.mPort = port != null ? Integer.parseInt(port) : -1;
        }

        AuthorityEntry(Parcel src) {
            this.mOrigHost = src.readString();
            this.mHost = src.readString();
            this.mWild = src.readInt() != 0;
            this.mPort = src.readInt();
        }

        void writeToParcel(Parcel dest) {
            dest.writeString(this.mOrigHost);
            dest.writeString(this.mHost);
            dest.writeInt(this.mWild ? 1 : 0);
            dest.writeInt(this.mPort);
        }

        public String getHost() {
            return this.mOrigHost;
        }

        public int getPort() {
            return this.mPort;
        }

        public int match(Uri data) {
            String host = data.getHost();
            if (host == null) {
                return -2;
            }
            if (this.mWild) {
                if (host.length() < this.mHost.length()) {
                    return -2;
                }
                host = host.substring(host.length() - this.mHost.length());
            }
            if (host.compareToIgnoreCase(this.mHost) != 0) {
                return -2;
            }
            if (this.mPort >= 0) {
                if (this.mPort != data.getPort()) {
                    return -2;
                }
                return 4194304;
            }
            return IntentFilter.MATCH_CATEGORY_HOST;
        }
    }

    public final void addDataSchemeSpecificPart(String ssp, int type) {
        addDataSchemeSpecificPart(new PatternMatcher(ssp, type));
    }

    public final void addDataSchemeSpecificPart(PatternMatcher ssp) {
        if (this.mDataSchemeSpecificParts == null) {
            this.mDataSchemeSpecificParts = new ArrayList<>();
        }
        this.mDataSchemeSpecificParts.add(ssp);
    }

    public final int countDataSchemeSpecificParts() {
        if (this.mDataSchemeSpecificParts != null) {
            return this.mDataSchemeSpecificParts.size();
        }
        return 0;
    }

    public final PatternMatcher getDataSchemeSpecificPart(int index) {
        return this.mDataSchemeSpecificParts.get(index);
    }

    public final boolean hasDataSchemeSpecificPart(String data) {
        if (this.mDataSchemeSpecificParts == null) {
            return false;
        }
        int numDataSchemeSpecificParts = this.mDataSchemeSpecificParts.size();
        for (int i = 0; i < numDataSchemeSpecificParts; i++) {
            PatternMatcher pe = this.mDataSchemeSpecificParts.get(i);
            if (pe.match(data)) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<PatternMatcher> schemeSpecificPartsIterator() {
        if (this.mDataSchemeSpecificParts != null) {
            return this.mDataSchemeSpecificParts.iterator();
        }
        return null;
    }

    public final void addDataAuthority(String host, String port) {
        if (port != null) {
            port = port.intern();
        }
        addDataAuthority(new AuthorityEntry(host.intern(), port));
    }

    public final void addDataAuthority(AuthorityEntry ent) {
        if (this.mDataAuthorities == null) {
            this.mDataAuthorities = new ArrayList<>();
        }
        this.mDataAuthorities.add(ent);
    }

    public final int countDataAuthorities() {
        if (this.mDataAuthorities != null) {
            return this.mDataAuthorities.size();
        }
        return 0;
    }

    public final AuthorityEntry getDataAuthority(int index) {
        return this.mDataAuthorities.get(index);
    }

    public final boolean hasDataAuthority(Uri data) {
        return matchDataAuthority(data) >= 0;
    }

    public final Iterator<AuthorityEntry> authoritiesIterator() {
        if (this.mDataAuthorities != null) {
            return this.mDataAuthorities.iterator();
        }
        return null;
    }

    public final void addDataPath(String path, int type) {
        addDataPath(new PatternMatcher(path.intern(), type));
    }

    public final void addDataPath(PatternMatcher path) {
        if (this.mDataPaths == null) {
            this.mDataPaths = new ArrayList<>();
        }
        this.mDataPaths.add(path);
    }

    public final int countDataPaths() {
        if (this.mDataPaths != null) {
            return this.mDataPaths.size();
        }
        return 0;
    }

    public final PatternMatcher getDataPath(int index) {
        return this.mDataPaths.get(index);
    }

    public final boolean hasDataPath(String data) {
        if (this.mDataPaths == null) {
            return false;
        }
        int numDataPaths = this.mDataPaths.size();
        for (int i = 0; i < numDataPaths; i++) {
            PatternMatcher pe = this.mDataPaths.get(i);
            if (pe.match(data)) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<PatternMatcher> pathsIterator() {
        if (this.mDataPaths != null) {
            return this.mDataPaths.iterator();
        }
        return null;
    }

    public final int matchDataAuthority(Uri data) {
        if (this.mDataAuthorities == null) {
            return -2;
        }
        int numDataAuthorities = this.mDataAuthorities.size();
        for (int i = 0; i < numDataAuthorities; i++) {
            AuthorityEntry ae = this.mDataAuthorities.get(i);
            int match = ae.match(data);
            if (match >= 0) {
                return match;
            }
        }
        return -2;
    }

    public final int matchData(String type, String scheme, Uri data) {
        ArrayList<String> types = this.mDataTypes;
        ArrayList<String> schemes = this.mDataSchemes;
        int match = 1048576;
        if (types == null && schemes == null) {
            return (type == null && data == null) ? 1081344 : -2;
        }
        if (schemes != null) {
            if (schemes.contains(scheme != null ? scheme : "")) {
                match = 2097152;
                ArrayList<PatternMatcher> schemeSpecificParts = this.mDataSchemeSpecificParts;
                if (schemeSpecificParts != null) {
                    match = hasDataSchemeSpecificPart(data.getSchemeSpecificPart()) ? MATCH_CATEGORY_SCHEME_SPECIFIC_PART : -2;
                }
                if (match != 5767168) {
                    ArrayList<AuthorityEntry> authorities = this.mDataAuthorities;
                    if (authorities != null) {
                        int authMatch = matchDataAuthority(data);
                        if (authMatch >= 0) {
                            ArrayList<PatternMatcher> paths = this.mDataPaths;
                            if (paths == null) {
                                match = authMatch;
                            } else if (hasDataPath(data.getPath())) {
                                match = 5242880;
                            } else {
                                return -2;
                            }
                        } else {
                            return -2;
                        }
                    }
                }
                if (match == -2) {
                    return -2;
                }
            } else {
                return -2;
            }
        } else if (scheme != null && !"".equals(scheme) && !"content".equals(scheme) && !ContentResolver.SCHEME_FILE.equals(scheme)) {
            return -2;
        }
        if (types != null) {
            if (findMimeType(type)) {
                match = 6291456;
            } else {
                return -1;
            }
        } else if (type != null) {
            return -1;
        }
        return match + 32768;
    }

    public final void addCategory(String category) {
        if (this.mCategories == null) {
            this.mCategories = new ArrayList<>();
        }
        if (!this.mCategories.contains(category)) {
            this.mCategories.add(category.intern());
        }
    }

    public final int countCategories() {
        if (this.mCategories != null) {
            return this.mCategories.size();
        }
        return 0;
    }

    public final String getCategory(int index) {
        return this.mCategories.get(index);
    }

    public final boolean hasCategory(String category) {
        return this.mCategories != null && this.mCategories.contains(category);
    }

    public final Iterator<String> categoriesIterator() {
        if (this.mCategories != null) {
            return this.mCategories.iterator();
        }
        return null;
    }

    public final String matchCategories(Set<String> categories) {
        if (categories == null) {
            return null;
        }
        Iterator<String> it = categories.iterator();
        if (this.mCategories == null) {
            if (it.hasNext()) {
                return it.next();
            }
            return null;
        }
        while (it.hasNext()) {
            String category = it.next();
            if (!this.mCategories.contains(category)) {
                return category;
            }
        }
        return null;
    }

    public final int match(ContentResolver resolver, Intent intent, boolean resolve, String logTag) {
        String type = resolve ? intent.resolveType(resolver) : intent.getType();
        return match(intent.getAction(), type, intent.getScheme(), intent.getData(), intent.getCategories(), logTag);
    }

    public final int match(String action, String type, String scheme, Uri data, Set<String> categories, String logTag) {
        if (action != null && !matchAction(action)) {
            return -3;
        }
        int dataMatch = matchData(type, scheme, data);
        if (dataMatch < 0) {
            return dataMatch;
        }
        String categoryMismatch = matchCategories(categories);
        if (categoryMismatch != null) {
            return -4;
        }
        return dataMatch;
    }

    public void writeToXml(XmlSerializer serializer) throws IOException {
        int N = countActions();
        for (int i = 0; i < N; i++) {
            serializer.startTag(null, "action");
            serializer.attribute(null, "name", this.mActions.get(i));
            serializer.endTag(null, "action");
        }
        int N2 = countCategories();
        for (int i2 = 0; i2 < N2; i2++) {
            serializer.startTag(null, CAT_STR);
            serializer.attribute(null, "name", this.mCategories.get(i2));
            serializer.endTag(null, CAT_STR);
        }
        int N3 = countDataTypes();
        for (int i3 = 0; i3 < N3; i3++) {
            serializer.startTag(null, "type");
            String type = this.mDataTypes.get(i3);
            if (type.indexOf(47) < 0) {
                type = type + "/*";
            }
            serializer.attribute(null, "name", type);
            serializer.endTag(null, "type");
        }
        int N4 = countDataSchemes();
        for (int i4 = 0; i4 < N4; i4++) {
            serializer.startTag(null, SCHEME_STR);
            serializer.attribute(null, "name", this.mDataSchemes.get(i4));
            serializer.endTag(null, SCHEME_STR);
        }
        int N5 = countDataSchemeSpecificParts();
        for (int i5 = 0; i5 < N5; i5++) {
            serializer.startTag(null, SSP_STR);
            PatternMatcher pe = this.mDataSchemeSpecificParts.get(i5);
            switch (pe.getType()) {
                case 0:
                    serializer.attribute(null, "literal", pe.getPath());
                    break;
                case 1:
                    serializer.attribute(null, PREFIX_STR, pe.getPath());
                    break;
                case 2:
                    serializer.attribute(null, SGLOB_STR, pe.getPath());
                    break;
            }
            serializer.endTag(null, SSP_STR);
        }
        int N6 = countDataAuthorities();
        for (int i6 = 0; i6 < N6; i6++) {
            serializer.startTag(null, AUTH_STR);
            AuthorityEntry ae = this.mDataAuthorities.get(i6);
            serializer.attribute(null, "host", ae.getHost());
            if (ae.getPort() >= 0) {
                serializer.attribute(null, "port", Integer.toString(ae.getPort()));
            }
            serializer.endTag(null, AUTH_STR);
        }
        int N7 = countDataPaths();
        for (int i7 = 0; i7 < N7; i7++) {
            serializer.startTag(null, "path");
            PatternMatcher pe2 = this.mDataPaths.get(i7);
            switch (pe2.getType()) {
                case 0:
                    serializer.attribute(null, "literal", pe2.getPath());
                    break;
                case 1:
                    serializer.attribute(null, PREFIX_STR, pe2.getPath());
                    break;
                case 2:
                    serializer.attribute(null, SGLOB_STR, pe2.getPath());
                    break;
            }
            serializer.endTag(null, "path");
        }
    }

    public void readFromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals("action")) {
                        String name = parser.getAttributeValue(null, "name");
                        if (name != null) {
                            addAction(name);
                        }
                    } else if (tagName.equals(CAT_STR)) {
                        String name2 = parser.getAttributeValue(null, "name");
                        if (name2 != null) {
                            addCategory(name2);
                        }
                    } else if (tagName.equals("type")) {
                        String name3 = parser.getAttributeValue(null, "name");
                        if (name3 != null) {
                            try {
                                addDataType(name3);
                            } catch (MalformedMimeTypeException e) {
                            }
                        }
                    } else if (tagName.equals(SCHEME_STR)) {
                        String name4 = parser.getAttributeValue(null, "name");
                        if (name4 != null) {
                            addDataScheme(name4);
                        }
                    } else if (tagName.equals(SSP_STR)) {
                        String ssp = parser.getAttributeValue(null, "literal");
                        if (ssp != null) {
                            addDataSchemeSpecificPart(ssp, 0);
                        } else {
                            String ssp2 = parser.getAttributeValue(null, PREFIX_STR);
                            if (ssp2 != null) {
                                addDataSchemeSpecificPart(ssp2, 1);
                            } else {
                                String ssp3 = parser.getAttributeValue(null, SGLOB_STR);
                                if (ssp3 != null) {
                                    addDataSchemeSpecificPart(ssp3, 2);
                                }
                            }
                        }
                    } else if (tagName.equals(AUTH_STR)) {
                        String host = parser.getAttributeValue(null, "host");
                        String port = parser.getAttributeValue(null, "port");
                        if (host != null) {
                            addDataAuthority(host, port);
                        }
                    } else if (tagName.equals("path")) {
                        String path = parser.getAttributeValue(null, "literal");
                        if (path != null) {
                            addDataPath(path, 0);
                        } else {
                            String path2 = parser.getAttributeValue(null, PREFIX_STR);
                            if (path2 != null) {
                                addDataPath(path2, 1);
                            } else {
                                String path3 = parser.getAttributeValue(null, SGLOB_STR);
                                if (path3 != null) {
                                    addDataPath(path3, 2);
                                }
                            }
                        }
                    } else {
                        Log.w("IntentFilter", "Unknown tag parsing IntentFilter: " + tagName);
                    }
                    XmlUtils.skipCurrentTag(parser);
                }
            } else {
                return;
            }
        }
    }

    public void dump(Printer du, String prefix) {
        StringBuilder sb = new StringBuilder(256);
        if (this.mActions.size() > 0) {
            Iterator<String> it = this.mActions.iterator();
            while (it.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Action: \"");
                sb.append(it.next());
                sb.append(Separators.DOUBLE_QUOTE);
                du.println(sb.toString());
            }
        }
        if (this.mCategories != null) {
            Iterator<String> it2 = this.mCategories.iterator();
            while (it2.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Category: \"");
                sb.append(it2.next());
                sb.append(Separators.DOUBLE_QUOTE);
                du.println(sb.toString());
            }
        }
        if (this.mDataSchemes != null) {
            Iterator<String> it3 = this.mDataSchemes.iterator();
            while (it3.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Scheme: \"");
                sb.append(it3.next());
                sb.append(Separators.DOUBLE_QUOTE);
                du.println(sb.toString());
            }
        }
        if (this.mDataSchemeSpecificParts != null) {
            Iterator<PatternMatcher> it4 = this.mDataSchemeSpecificParts.iterator();
            while (it4.hasNext()) {
                PatternMatcher pe = it4.next();
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Ssp: \"");
                sb.append(pe);
                sb.append(Separators.DOUBLE_QUOTE);
                du.println(sb.toString());
            }
        }
        if (this.mDataAuthorities != null) {
            Iterator<AuthorityEntry> it5 = this.mDataAuthorities.iterator();
            while (it5.hasNext()) {
                AuthorityEntry ae = it5.next();
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Authority: \"");
                sb.append(ae.mHost);
                sb.append("\": ");
                sb.append(ae.mPort);
                if (ae.mWild) {
                    sb.append(" WILD");
                }
                du.println(sb.toString());
            }
        }
        if (this.mDataPaths != null) {
            Iterator<PatternMatcher> it6 = this.mDataPaths.iterator();
            while (it6.hasNext()) {
                PatternMatcher pe2 = it6.next();
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Path: \"");
                sb.append(pe2);
                sb.append(Separators.DOUBLE_QUOTE);
                du.println(sb.toString());
            }
        }
        if (this.mDataTypes != null) {
            Iterator<String> it7 = this.mDataTypes.iterator();
            while (it7.hasNext()) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("Type: \"");
                sb.append(it7.next());
                sb.append(Separators.DOUBLE_QUOTE);
                du.println(sb.toString());
            }
        }
        if (this.mPriority != 0 || this.mHasPartialTypes) {
            sb.setLength(0);
            sb.append(prefix);
            sb.append("mPriority=");
            sb.append(this.mPriority);
            sb.append(", mHasPartialTypes=");
            sb.append(this.mHasPartialTypes);
            du.println(sb.toString());
        }
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mActions);
        if (this.mCategories != null) {
            dest.writeInt(1);
            dest.writeStringList(this.mCategories);
        } else {
            dest.writeInt(0);
        }
        if (this.mDataSchemes != null) {
            dest.writeInt(1);
            dest.writeStringList(this.mDataSchemes);
        } else {
            dest.writeInt(0);
        }
        if (this.mDataTypes != null) {
            dest.writeInt(1);
            dest.writeStringList(this.mDataTypes);
        } else {
            dest.writeInt(0);
        }
        if (this.mDataSchemeSpecificParts != null) {
            int N = this.mDataSchemeSpecificParts.size();
            dest.writeInt(N);
            for (int i = 0; i < N; i++) {
                this.mDataSchemeSpecificParts.get(i).writeToParcel(dest, flags);
            }
        } else {
            dest.writeInt(0);
        }
        if (this.mDataAuthorities != null) {
            int N2 = this.mDataAuthorities.size();
            dest.writeInt(N2);
            for (int i2 = 0; i2 < N2; i2++) {
                this.mDataAuthorities.get(i2).writeToParcel(dest);
            }
        } else {
            dest.writeInt(0);
        }
        if (this.mDataPaths != null) {
            int N3 = this.mDataPaths.size();
            dest.writeInt(N3);
            for (int i3 = 0; i3 < N3; i3++) {
                this.mDataPaths.get(i3).writeToParcel(dest, flags);
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.mPriority);
        dest.writeInt(this.mHasPartialTypes ? 1 : 0);
    }

    public boolean debugCheck() {
        return true;
    }

    private IntentFilter(Parcel source) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mActions = new ArrayList<>();
        source.readStringList(this.mActions);
        if (source.readInt() != 0) {
            this.mCategories = new ArrayList<>();
            source.readStringList(this.mCategories);
        }
        if (source.readInt() != 0) {
            this.mDataSchemes = new ArrayList<>();
            source.readStringList(this.mDataSchemes);
        }
        if (source.readInt() != 0) {
            this.mDataTypes = new ArrayList<>();
            source.readStringList(this.mDataTypes);
        }
        int N = source.readInt();
        if (N > 0) {
            this.mDataSchemeSpecificParts = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                this.mDataSchemeSpecificParts.add(new PatternMatcher(source));
            }
        }
        int N2 = source.readInt();
        if (N2 > 0) {
            this.mDataAuthorities = new ArrayList<>(N2);
            for (int i2 = 0; i2 < N2; i2++) {
                this.mDataAuthorities.add(new AuthorityEntry(source));
            }
        }
        int N3 = source.readInt();
        if (N3 > 0) {
            this.mDataPaths = new ArrayList<>(N3);
            for (int i3 = 0; i3 < N3; i3++) {
                this.mDataPaths.add(new PatternMatcher(source));
            }
        }
        this.mPriority = source.readInt();
        this.mHasPartialTypes = source.readInt() > 0;
    }

    private final boolean findMimeType(String type) {
        ArrayList<String> t = this.mDataTypes;
        if (type == null) {
            return false;
        }
        if (t.contains(type)) {
            return true;
        }
        int typeLength = type.length();
        if (typeLength == 3 && type.equals("*/*")) {
            return !t.isEmpty();
        } else if (this.mHasPartialTypes && t.contains("*")) {
            return true;
        } else {
            int slashpos = type.indexOf(47);
            if (slashpos > 0) {
                if (this.mHasPartialTypes && t.contains(type.substring(0, slashpos))) {
                    return true;
                }
                if (typeLength == slashpos + 2 && type.charAt(slashpos + 1) == '*') {
                    int numTypes = t.size();
                    for (int i = 0; i < numTypes; i++) {
                        String v = t.get(i);
                        if (type.regionMatches(0, v, 0, slashpos + 1)) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
    }
}