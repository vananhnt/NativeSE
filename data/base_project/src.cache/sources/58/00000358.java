package android.content;

import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.regex.Pattern;

/* loaded from: UriMatcher.class */
public class UriMatcher {
    public static final int NO_MATCH = -1;
    static final Pattern PATH_SPLIT_PATTERN = Pattern.compile(Separators.SLASH);
    private static final int EXACT = 0;
    private static final int NUMBER = 1;
    private static final int TEXT = 2;
    private int mCode;
    private int mWhich;
    private String mText;
    private ArrayList<UriMatcher> mChildren;

    public UriMatcher(int code) {
        this.mCode = code;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    private UriMatcher() {
        this.mCode = -1;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    public void addURI(String authority, String path, int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code " + code + " is invalid: it must be positive");
        }
        String[] tokens = null;
        if (path != null) {
            String newPath = path;
            if (path.length() > 0 && path.charAt(0) == '/') {
                newPath = path.substring(1);
            }
            tokens = PATH_SPLIT_PATTERN.split(newPath);
        }
        int numTokens = tokens != null ? tokens.length : 0;
        UriMatcher node = this;
        int i = -1;
        while (i < numTokens) {
            String token = i < 0 ? authority : tokens[i];
            ArrayList<UriMatcher> children = node.mChildren;
            int numChildren = children.size();
            int j = 0;
            while (true) {
                if (j >= numChildren) {
                    break;
                }
                UriMatcher child = children.get(j);
                if (!token.equals(child.mText)) {
                    j++;
                } else {
                    node = child;
                    break;
                }
            }
            if (j == numChildren) {
                UriMatcher child2 = new UriMatcher();
                if (token.equals(Separators.POUND)) {
                    child2.mWhich = 1;
                } else if (token.equals("*")) {
                    child2.mWhich = 2;
                } else {
                    child2.mWhich = 0;
                }
                child2.mText = token;
                node.mChildren.add(child2);
                node = child2;
            }
            i++;
        }
        node.mCode = code;
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00f5 A[LOOP:0: B:9:0x0022->B:45:0x00f5, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00f3 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int match(android.net.Uri r4) {
        /*
            Method dump skipped, instructions count: 257
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.UriMatcher.match(android.net.Uri):int");
    }
}