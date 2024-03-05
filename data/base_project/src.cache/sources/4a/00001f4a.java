package com.android.server.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import com.android.internal.util.XmlUtils;
import com.android.server.PreferredComponent;
import gov.nist.core.Separators;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PreferredActivity.class */
public class PreferredActivity extends IntentFilter implements PreferredComponent.Callbacks {
    private static final String TAG = "PreferredActivity";
    private static final boolean DEBUG_FILTERS = false;
    final PreferredComponent mPref;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, boolean always) {
        super(filter);
        this.mPref = new PreferredComponent(this, match, set, activity, always);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreferredActivity(XmlPullParser parser) throws XmlPullParserException, IOException {
        this.mPref = new PreferredComponent(this, parser);
    }

    public void writeToXml(XmlSerializer serializer, boolean full) throws IOException {
        this.mPref.writeToXml(serializer, full);
        serializer.startTag(null, "filter");
        super.writeToXml(serializer);
        serializer.endTag(null, "filter");
    }

    @Override // com.android.server.PreferredComponent.Callbacks
    public boolean onReadTag(String tagName, XmlPullParser parser) throws XmlPullParserException, IOException {
        if (tagName.equals("filter")) {
            readFromXml(parser);
            return true;
        }
        PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + parser.getName());
        XmlUtils.skipCurrentTag(parser);
        return true;
    }

    public String toString() {
        return "PreferredActivity{0x" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.mPref.mComponent.flattenToShortString() + "}";
    }
}