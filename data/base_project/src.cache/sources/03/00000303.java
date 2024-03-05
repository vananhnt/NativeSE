package android.content;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: Entity.class */
public final class Entity {
    private final ContentValues mValues;
    private final ArrayList<NamedContentValues> mSubValues = new ArrayList<>();

    public Entity(ContentValues values) {
        this.mValues = values;
    }

    public ContentValues getEntityValues() {
        return this.mValues;
    }

    public ArrayList<NamedContentValues> getSubValues() {
        return this.mSubValues;
    }

    public void addSubValue(Uri uri, ContentValues values) {
        this.mSubValues.add(new NamedContentValues(uri, values));
    }

    /* loaded from: Entity$NamedContentValues.class */
    public static class NamedContentValues {
        public final Uri uri;
        public final ContentValues values;

        public NamedContentValues(Uri uri, ContentValues values) {
            this.uri = uri;
            this.values = values;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity: ").append(getEntityValues());
        Iterator i$ = getSubValues().iterator();
        while (i$.hasNext()) {
            NamedContentValues namedValue = i$.next();
            sb.append("\n  ").append(namedValue.uri);
            sb.append("\n  -> ").append(namedValue.values);
        }
        return sb.toString();
    }
}