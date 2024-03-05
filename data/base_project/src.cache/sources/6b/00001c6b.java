package com.android.server;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.FastImmutableArraySet;
import android.util.LogPrinter;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.Slog;
import com.android.internal.util.FastPrintWriter;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: IntentResolver.class */
public abstract class IntentResolver<F extends IntentFilter, R> {
    private static final String TAG = "IntentResolver";
    private static final boolean DEBUG = false;
    private static final boolean localLOGV = false;
    private static final Comparator mResolvePrioritySorter = new Comparator() { // from class: com.android.server.IntentResolver.1
        @Override // java.util.Comparator
        public int compare(Object o1, Object o2) {
            int q1 = ((IntentFilter) o1).getPriority();
            int q2 = ((IntentFilter) o2).getPriority();
            if (q1 > q2) {
                return -1;
            }
            return q1 < q2 ? 1 : 0;
        }
    };
    private final HashSet<F> mFilters = new HashSet<>();
    private final ArrayMap<String, F[]> mTypeToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mBaseTypeToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mWildTypeToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mSchemeToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mActionToFilter = new ArrayMap<>();
    private final ArrayMap<String, F[]> mTypedActionToFilter = new ArrayMap<>();

    protected abstract boolean isPackageForFilter(String str, F f);

    protected abstract F[] newArray(int i);

    public void addFilter(F f) {
        this.mFilters.add(f);
        int numS = register_intent_filter(f, f.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
        int numT = register_mime_types(f, "      Type: ");
        if (numS == 0 && numT == 0) {
            register_intent_filter(f, f.actionsIterator(), this.mActionToFilter, "      Action: ");
        }
        if (numT != 0) {
            register_intent_filter(f, f.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
        }
    }

    public void removeFilter(F f) {
        removeFilterInternal(f);
        this.mFilters.remove(f);
    }

    void removeFilterInternal(F f) {
        int numS = unregister_intent_filter(f, f.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
        int numT = unregister_mime_types(f, "      Type: ");
        if (numS == 0 && numT == 0) {
            unregister_intent_filter(f, f.actionsIterator(), this.mActionToFilter, "      Action: ");
        }
        if (numT != 0) {
            unregister_intent_filter(f, f.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
        }
    }

    boolean dumpMap(PrintWriter out, String titlePrefix, String title, String prefix, Map<String, F[]> map, String packageName, boolean printFilter) {
        F filter;
        String eprefix = prefix + "  ";
        String fprefix = prefix + "    ";
        boolean printedSomething = false;
        Printer printer = null;
        for (Map.Entry<String, F[]> e : map.entrySet()) {
            F[] a = e.getValue();
            int N = a.length;
            boolean printedHeader = false;
            for (int i = 0; i < N && (filter = a[i]) != null; i++) {
                if (packageName == null || isPackageForFilter(packageName, filter)) {
                    if (title != null) {
                        out.print(titlePrefix);
                        out.println(title);
                        title = null;
                    }
                    if (!printedHeader) {
                        out.print(eprefix);
                        out.print(e.getKey());
                        out.println(Separators.COLON);
                        printedHeader = true;
                    }
                    printedSomething = true;
                    dumpFilter(out, fprefix, filter);
                    if (printFilter) {
                        if (printer == null) {
                            printer = new PrintWriterPrinter(out);
                        }
                        filter.dump(printer, fprefix + "  ");
                    }
                }
            }
        }
        return printedSomething;
    }

    public boolean dump(PrintWriter out, String title, String prefix, String packageName, boolean printFilter) {
        String innerPrefix = prefix + "  ";
        String sepPrefix = Separators.RETURN + prefix;
        String curPrefix = title + Separators.RETURN + prefix;
        if (dumpMap(out, curPrefix, "Full MIME Types:", innerPrefix, this.mTypeToFilter, packageName, printFilter)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Base MIME Types:", innerPrefix, this.mBaseTypeToFilter, packageName, printFilter)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Wild MIME Types:", innerPrefix, this.mWildTypeToFilter, packageName, printFilter)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Schemes:", innerPrefix, this.mSchemeToFilter, packageName, printFilter)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "Non-Data Actions:", innerPrefix, this.mActionToFilter, packageName, printFilter)) {
            curPrefix = sepPrefix;
        }
        if (dumpMap(out, curPrefix, "MIME Typed Actions:", innerPrefix, this.mTypedActionToFilter, packageName, printFilter)) {
            curPrefix = sepPrefix;
        }
        return curPrefix == sepPrefix;
    }

    /* loaded from: IntentResolver$IteratorWrapper.class */
    private class IteratorWrapper implements Iterator<F> {
        private final Iterator<F> mI;
        private F mCur;

        IteratorWrapper(Iterator<F> it) {
            this.mI = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mI.hasNext();
        }

        @Override // java.util.Iterator
        public F next() {
            F next = this.mI.next();
            this.mCur = next;
            return next;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.mCur != null) {
                IntentResolver.this.removeFilterInternal(this.mCur);
            }
            this.mI.remove();
        }
    }

    public Iterator<F> filterIterator() {
        return new IteratorWrapper(this.mFilters.iterator());
    }

    public Set<F> filterSet() {
        return Collections.unmodifiableSet(this.mFilters);
    }

    public List<R> queryIntentFromList(Intent intent, String resolvedType, boolean defaultOnly, ArrayList<F[]> listCut, int userId) {
        ArrayList<R> resultList = new ArrayList<>();
        boolean debug = (intent.getFlags() & 8) != 0;
        FastImmutableArraySet<String> categories = getFastIntentCategories(intent);
        String scheme = intent.getScheme();
        int N = listCut.size();
        for (int i = 0; i < N; i++) {
            buildResolveList(intent, categories, debug, defaultOnly, resolvedType, scheme, listCut.get(i), resultList, userId);
        }
        sortResults(resultList);
        return resultList;
    }

    public List<R> queryIntent(Intent intent, String resolvedType, boolean defaultOnly, int userId) {
        int slashpos;
        String scheme = intent.getScheme();
        ArrayList<R> finalList = new ArrayList<>();
        boolean debug = (intent.getFlags() & 8) != 0;
        if (debug) {
            Slog.v(TAG, "Resolving type=" + resolvedType + " scheme=" + scheme + " defaultOnly=" + defaultOnly + " userId=" + userId + " of " + intent);
        }
        F[] firstTypeCut = null;
        F[] secondTypeCut = null;
        F[] thirdTypeCut = null;
        F[] schemeCut = null;
        if (resolvedType != null && (slashpos = resolvedType.indexOf(47)) > 0) {
            String baseType = resolvedType.substring(0, slashpos);
            if (!baseType.equals("*")) {
                if (resolvedType.length() != slashpos + 2 || resolvedType.charAt(slashpos + 1) != '*') {
                    firstTypeCut = this.mTypeToFilter.get(resolvedType);
                    if (debug) {
                        Slog.v(TAG, "First type cut: " + Arrays.toString(firstTypeCut));
                    }
                    secondTypeCut = this.mWildTypeToFilter.get(baseType);
                    if (debug) {
                        Slog.v(TAG, "Second type cut: " + Arrays.toString(secondTypeCut));
                    }
                } else {
                    firstTypeCut = this.mBaseTypeToFilter.get(baseType);
                    if (debug) {
                        Slog.v(TAG, "First type cut: " + Arrays.toString(firstTypeCut));
                    }
                    secondTypeCut = this.mWildTypeToFilter.get(baseType);
                    if (debug) {
                        Slog.v(TAG, "Second type cut: " + Arrays.toString(secondTypeCut));
                    }
                }
                thirdTypeCut = this.mWildTypeToFilter.get("*");
                if (debug) {
                    Slog.v(TAG, "Third type cut: " + Arrays.toString(thirdTypeCut));
                }
            } else if (intent.getAction() != null) {
                firstTypeCut = this.mTypedActionToFilter.get(intent.getAction());
                if (debug) {
                    Slog.v(TAG, "Typed Action list: " + Arrays.toString(firstTypeCut));
                }
            }
        }
        if (scheme != null) {
            schemeCut = this.mSchemeToFilter.get(scheme);
            if (debug) {
                Slog.v(TAG, "Scheme list: " + Arrays.toString(schemeCut));
            }
        }
        if (resolvedType == null && scheme == null && intent.getAction() != null) {
            firstTypeCut = this.mActionToFilter.get(intent.getAction());
            if (debug) {
                Slog.v(TAG, "Action list: " + Arrays.toString(firstTypeCut));
            }
        }
        FastImmutableArraySet<String> categories = getFastIntentCategories(intent);
        if (firstTypeCut != null) {
            buildResolveList(intent, categories, debug, defaultOnly, resolvedType, scheme, firstTypeCut, finalList, userId);
        }
        if (secondTypeCut != null) {
            buildResolveList(intent, categories, debug, defaultOnly, resolvedType, scheme, secondTypeCut, finalList, userId);
        }
        if (thirdTypeCut != null) {
            buildResolveList(intent, categories, debug, defaultOnly, resolvedType, scheme, thirdTypeCut, finalList, userId);
        }
        if (schemeCut != null) {
            buildResolveList(intent, categories, debug, defaultOnly, resolvedType, scheme, schemeCut, finalList, userId);
        }
        sortResults(finalList);
        if (debug) {
            Slog.v(TAG, "Final result list:");
            for (int i = 0; i < finalList.size(); i++) {
                Slog.v(TAG, "  " + finalList.get(i));
            }
        }
        return finalList;
    }

    protected boolean allowFilterResult(F filter, List<R> dest) {
        return true;
    }

    protected boolean isFilterStopped(F filter, int userId) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public R newResult(F filter, int match, int userId) {
        return filter;
    }

    protected void sortResults(List<R> results) {
        Collections.sort(results, mResolvePrioritySorter);
    }

    protected void dumpFilter(PrintWriter out, String prefix, F filter) {
        out.print(prefix);
        out.println(filter);
    }

    private final void addFilter(ArrayMap<String, F[]> map, String name, F filter) {
        F[] array = map.get(name);
        if (array == null) {
            F[] array2 = newArray(2);
            map.put(name, array2);
            array2[0] = filter;
            return;
        }
        int N = array.length;
        int i = N;
        while (i > 0 && array[i - 1] == null) {
            i--;
        }
        if (i < N) {
            array[i] = filter;
            return;
        }
        F[] newa = newArray((N * 3) / 2);
        System.arraycopy(array, 0, newa, 0, N);
        newa[N] = filter;
        map.put(name, newa);
    }

    private final int register_mime_types(F filter, String prefix) {
        Iterator<String> i = filter.typesIterator();
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            String name = i.next();
            num++;
            String baseName = name;
            int slashpos = name.indexOf(47);
            if (slashpos > 0) {
                baseName = name.substring(0, slashpos).intern();
            } else {
                name = name + "/*";
            }
            addFilter(this.mTypeToFilter, name, filter);
            if (slashpos > 0) {
                addFilter(this.mBaseTypeToFilter, baseName, filter);
            } else {
                addFilter(this.mWildTypeToFilter, baseName, filter);
            }
        }
        return num;
    }

    private final int unregister_mime_types(F filter, String prefix) {
        Iterator<String> i = filter.typesIterator();
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            String name = i.next();
            num++;
            String baseName = name;
            int slashpos = name.indexOf(47);
            if (slashpos > 0) {
                baseName = name.substring(0, slashpos).intern();
            } else {
                name = name + "/*";
            }
            remove_all_objects(this.mTypeToFilter, name, filter);
            if (slashpos > 0) {
                remove_all_objects(this.mBaseTypeToFilter, baseName, filter);
            } else {
                remove_all_objects(this.mWildTypeToFilter, baseName, filter);
            }
        }
        return num;
    }

    private final int register_intent_filter(F filter, Iterator<String> i, ArrayMap<String, F[]> dest, String prefix) {
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            String name = i.next();
            num++;
            addFilter(dest, name, filter);
        }
        return num;
    }

    private final int unregister_intent_filter(F filter, Iterator<String> i, ArrayMap<String, F[]> dest, String prefix) {
        if (i == null) {
            return 0;
        }
        int num = 0;
        while (i.hasNext()) {
            String name = i.next();
            num++;
            remove_all_objects(dest, name, filter);
        }
        return num;
    }

    private final void remove_all_objects(ArrayMap<String, F[]> map, String name, Object object) {
        F[] array = map.get(name);
        if (array != null) {
            int LAST = array.length - 1;
            while (LAST >= 0 && array[LAST] == null) {
                LAST--;
            }
            for (int idx = LAST; idx >= 0; idx--) {
                if (array[idx] == object) {
                    int remain = LAST - idx;
                    if (remain > 0) {
                        System.arraycopy(array, idx + 1, array, idx, remain);
                    }
                    array[LAST] = null;
                    LAST--;
                }
            }
            if (LAST < 0) {
                map.remove(name);
            } else if (LAST < array.length / 2) {
                F[] newa = newArray(LAST + 2);
                System.arraycopy(array, 0, newa, 0, LAST + 1);
                map.put(name, newa);
            }
        }
    }

    private static FastImmutableArraySet<String> getFastIntentCategories(Intent intent) {
        Set<String> categories = intent.getCategories();
        if (categories == null) {
            return null;
        }
        return new FastImmutableArraySet<>(categories.toArray(new String[categories.size()]));
    }

    private void buildResolveList(Intent intent, FastImmutableArraySet<String> categories, boolean debug, boolean defaultOnly, String resolvedType, String scheme, F[] src, List<R> dest, int userId) {
        Printer logPrinter;
        PrintWriter logPrintWriter;
        F filter;
        String reason;
        String action = intent.getAction();
        Uri data = intent.getData();
        String packageName = intent.getPackage();
        boolean excludingStopped = intent.isExcludingStopped();
        if (debug) {
            logPrinter = new LogPrinter(2, TAG, 3);
            logPrintWriter = new FastPrintWriter(logPrinter);
        } else {
            logPrinter = null;
            logPrintWriter = null;
        }
        int N = src != null ? src.length : 0;
        boolean hasNonDefaults = false;
        for (int i = 0; i < N && (filter = src[i]) != null; i++) {
            if (debug) {
                Slog.v(TAG, "Matching against filter " + filter);
            }
            if (excludingStopped && isFilterStopped(filter, userId)) {
                if (debug) {
                    Slog.v(TAG, "  Filter's target is stopped; skipping");
                }
            } else if (packageName != null && !isPackageForFilter(packageName, filter)) {
                if (debug) {
                    Slog.v(TAG, "  Filter is not from package " + packageName + "; skipping");
                }
            } else if (!allowFilterResult(filter, dest)) {
                if (debug) {
                    Slog.v(TAG, "  Filter's target already added");
                }
            } else {
                int match = filter.match(action, resolvedType, scheme, data, categories, TAG);
                if (match >= 0) {
                    if (debug) {
                        Slog.v(TAG, "  Filter matched!  match=0x" + Integer.toHexString(match) + " hasDefault=" + filter.hasCategory(Intent.CATEGORY_DEFAULT));
                    }
                    if (!defaultOnly || filter.hasCategory(Intent.CATEGORY_DEFAULT)) {
                        R oneResult = newResult(filter, match, userId);
                        if (oneResult != null) {
                            dest.add(oneResult);
                            if (debug) {
                                dumpFilter(logPrintWriter, "    ", filter);
                                logPrintWriter.flush();
                                filter.dump(logPrinter, "    ");
                            }
                        }
                    } else {
                        hasNonDefaults = true;
                    }
                } else if (debug) {
                    switch (match) {
                        case -4:
                            reason = "category";
                            break;
                        case -3:
                            reason = "action";
                            break;
                        case -2:
                            reason = "data";
                            break;
                        case -1:
                            reason = "type";
                            break;
                        default:
                            reason = "unknown reason";
                            break;
                    }
                    Slog.v(TAG, "  Filter did not match: " + reason);
                }
            }
        }
        if (hasNonDefaults) {
            if (dest.size() == 0) {
                Slog.w(TAG, "resolveIntent failed: found match, but none with CATEGORY_DEFAULT");
            } else if (dest.size() > 1) {
                Slog.w(TAG, "resolveIntent: multiple matches, only some with CATEGORY_DEFAULT");
            }
        }
    }
}