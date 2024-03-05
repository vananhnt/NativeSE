package android.widget;

/* loaded from: SectionIndexer.class */
public interface SectionIndexer {
    Object[] getSections();

    int getPositionForSection(int i);

    int getSectionForPosition(int i);
}