package com.android.internal.app;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.ListFragment;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.R;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: LocalePicker.class */
public class LocalePicker extends ListFragment {
    private static final String TAG = "LocalePicker";
    private static final boolean DEBUG = false;
    LocaleSelectionListener mListener;

    /* loaded from: LocalePicker$LocaleSelectionListener.class */
    public interface LocaleSelectionListener {
        void onLocaleSelected(Locale locale);
    }

    protected boolean isInDeveloperMode() {
        return false;
    }

    /* loaded from: LocalePicker$LocaleInfo.class */
    public static class LocaleInfo implements Comparable<LocaleInfo> {
        static final Collator sCollator = Collator.getInstance();
        String label;
        Locale locale;

        public LocaleInfo(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        public String getLabel() {
            return this.label;
        }

        public Locale getLocale() {
            return this.locale;
        }

        public String toString() {
            return this.label;
        }

        @Override // java.lang.Comparable
        public int compareTo(LocaleInfo another) {
            return sCollator.compare(this.label, another.label);
        }
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context) {
        return constructAdapter(context, false);
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context, boolean isInDeveloperMode) {
        return constructAdapter(context, R.layout.locale_picker_item, R.id.locale, isInDeveloperMode);
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context, int layoutId, int fieldId) {
        return constructAdapter(context, layoutId, fieldId, false);
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context, final int layoutId, final int fieldId, boolean isInDeveloperMode) {
        String displayName;
        Resources resources = context.getResources();
        ArrayList<String> localeList = new ArrayList<>(Arrays.asList(Resources.getSystem().getAssets().getLocales()));
        if (isInDeveloperMode && !localeList.contains("zz_ZZ")) {
            localeList.add("zz_ZZ");
        }
        String[] locales = (String[]) localeList.toArray(new String[localeList.size()]);
        String[] specialLocaleCodes = resources.getStringArray(R.array.special_locale_codes);
        String[] specialLocaleNames = resources.getStringArray(R.array.special_locale_names);
        Arrays.sort(locales);
        int origSize = locales.length;
        LocaleInfo[] preprocess = new LocaleInfo[origSize];
        int finalSize = 0;
        for (String s : locales) {
            int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                Locale l = new Locale(language, country);
                if (finalSize == 0) {
                    int i = finalSize;
                    finalSize++;
                    preprocess[i] = new LocaleInfo(toTitleCase(l.getDisplayLanguage(l)), l);
                } else if (preprocess[finalSize - 1].locale.getLanguage().equals(language) && !preprocess[finalSize - 1].locale.getLanguage().equals("zz")) {
                    preprocess[finalSize - 1].label = toTitleCase(getDisplayName(preprocess[finalSize - 1].locale, specialLocaleCodes, specialLocaleNames));
                    int i2 = finalSize;
                    finalSize++;
                    preprocess[i2] = new LocaleInfo(toTitleCase(getDisplayName(l, specialLocaleCodes, specialLocaleNames)), l);
                } else {
                    if (s.equals("zz_ZZ")) {
                        displayName = "[Developer] Accented English";
                    } else if (s.equals("zz_ZY")) {
                        displayName = "[Developer] Fake Bi-Directional";
                    } else {
                        displayName = toTitleCase(l.getDisplayLanguage(l));
                    }
                    int i3 = finalSize;
                    finalSize++;
                    preprocess[i3] = new LocaleInfo(displayName, l);
                }
            }
        }
        LocaleInfo[] localeInfos = new LocaleInfo[finalSize];
        for (int i4 = 0; i4 < finalSize; i4++) {
            localeInfos[i4] = preprocess[i4];
        }
        Arrays.sort(localeInfos);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ArrayAdapter<LocaleInfo>(context, layoutId, fieldId, localeInfos) { // from class: com.android.internal.app.LocalePicker.1
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                TextView text;
                if (convertView == null) {
                    view = inflater.inflate(layoutId, parent, false);
                    text = (TextView) view.findViewById(fieldId);
                    view.setTag(text);
                } else {
                    view = convertView;
                    text = (TextView) view.getTag();
                }
                LocaleInfo item = getItem(position);
                text.setText(item.toString());
                text.setTextLocale(item.getLocale());
                return view;
            }
        };
    }

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String getDisplayName(Locale l, String[] specialLocaleCodes, String[] specialLocaleNames) {
        String code = l.toString();
        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }
        return l.getDisplayName(l);
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<LocaleInfo> adapter = constructAdapter(getActivity(), isInDeveloperMode());
        setListAdapter(adapter);
    }

    public void setLocaleSelectionListener(LocaleSelectionListener listener) {
        this.mListener = listener;
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        getListView().requestFocus();
    }

    @Override // android.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (this.mListener != null) {
            Locale locale = ((LocaleInfo) getListAdapter().getItem(position)).locale;
            this.mListener.onLocaleSelected(locale);
        }
    }

    public static void updateLocale(Locale locale) {
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            config.setLocale(locale);
            am.updateConfiguration(config);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {
        }
    }
}