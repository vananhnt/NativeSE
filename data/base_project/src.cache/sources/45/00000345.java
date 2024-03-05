package android.content;

import android.content.pm.RegisteredServicesCache;
import android.content.pm.XmlSerializerAndParser;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: SyncAdaptersCache.class */
public class SyncAdaptersCache extends RegisteredServicesCache<SyncAdapterType> {
    private static final String TAG = "Account";
    private static final String SERVICE_INTERFACE = "android.content.SyncAdapter";
    private static final String SERVICE_META_DATA = "android.content.SyncAdapter";
    private static final String ATTRIBUTES_NAME = "sync-adapter";
    private static final MySerializer sSerializer = new MySerializer();

    public SyncAdaptersCache(Context context) {
        super(context, "android.content.SyncAdapter", "android.content.SyncAdapter", ATTRIBUTES_NAME, sSerializer);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.content.pm.RegisteredServicesCache
    public SyncAdapterType parseServiceAttributes(Resources res, String packageName, AttributeSet attrs) {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.SyncAdapter);
        try {
            String authority = sa.getString(2);
            String accountType = sa.getString(1);
            if (authority == null || accountType == null) {
                return null;
            }
            boolean userVisible = sa.getBoolean(3, true);
            boolean supportsUploading = sa.getBoolean(4, true);
            boolean isAlwaysSyncable = sa.getBoolean(6, false);
            boolean allowParallelSyncs = sa.getBoolean(5, false);
            String settingsActivity = sa.getString(0);
            SyncAdapterType syncAdapterType = new SyncAdapterType(authority, accountType, userVisible, supportsUploading, isAlwaysSyncable, allowParallelSyncs, settingsActivity);
            sa.recycle();
            return syncAdapterType;
        } finally {
            sa.recycle();
        }
    }

    /* loaded from: SyncAdaptersCache$MySerializer.class */
    static class MySerializer implements XmlSerializerAndParser<SyncAdapterType> {
        MySerializer() {
        }

        @Override // android.content.pm.XmlSerializerAndParser
        public void writeAsXml(SyncAdapterType item, XmlSerializer out) throws IOException {
            out.attribute(null, ContactsContract.Directory.DIRECTORY_AUTHORITY, item.authority);
            out.attribute(null, "accountType", item.accountType);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.content.pm.XmlSerializerAndParser
        public SyncAdapterType createFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
            String authority = parser.getAttributeValue(null, ContactsContract.Directory.DIRECTORY_AUTHORITY);
            String accountType = parser.getAttributeValue(null, "accountType");
            return SyncAdapterType.newKey(authority, accountType);
        }
    }
}