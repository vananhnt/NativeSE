package java.text;

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AttributedString.class */
public class AttributedString {
    public AttributedString(AttributedCharacterIterator iterator) {
        throw new RuntimeException("Stub!");
    }

    public AttributedString(AttributedCharacterIterator iterator, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public AttributedString(AttributedCharacterIterator iterator, int start, int end, AttributedCharacterIterator.Attribute[] attributes) {
        throw new RuntimeException("Stub!");
    }

    public AttributedString(String value) {
        throw new RuntimeException("Stub!");
    }

    public AttributedString(String value, Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
        throw new RuntimeException("Stub!");
    }

    public void addAttribute(AttributedCharacterIterator.Attribute attribute, Object value) {
        throw new RuntimeException("Stub!");
    }

    public void addAttribute(AttributedCharacterIterator.Attribute attribute, Object value, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public void addAttributes(Map<? extends AttributedCharacterIterator.Attribute, ?> attributes, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public AttributedCharacterIterator getIterator() {
        throw new RuntimeException("Stub!");
    }

    public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] attributes) {
        throw new RuntimeException("Stub!");
    }

    public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] attributes, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AttributedString$Range.class */
    public static class Range {
        int start;
        int end;
        Object value;

        Range(int s, int e, Object v) {
            this.start = s;
            this.end = e;
            this.value = v;
        }
    }

    /* loaded from: AttributedString$AttributedIterator.class */
    static class AttributedIterator implements AttributedCharacterIterator {
        private int begin;
        private int end;
        private int offset;
        private AttributedString attrString;
        private HashSet<AttributedCharacterIterator.Attribute> attributesAllowed;

        AttributedIterator(AttributedString attrString) {
            this.attrString = attrString;
            this.begin = 0;
            this.end = attrString.text.length();
            this.offset = 0;
        }

        AttributedIterator(AttributedString attrString, AttributedCharacterIterator.Attribute[] attributes, int begin, int end) {
            if (begin < 0 || end > attrString.text.length() || begin > end) {
                throw new IllegalArgumentException();
            }
            this.begin = begin;
            this.end = end;
            this.offset = begin;
            this.attrString = attrString;
            if (attributes != null) {
                HashSet<AttributedCharacterIterator.Attribute> set = new HashSet<>(((attributes.length * 4) / 3) + 1);
                int i = attributes.length;
                while (true) {
                    i--;
                    if (i >= 0) {
                        set.add(attributes[i]);
                    } else {
                        this.attributesAllowed = set;
                        return;
                    }
                }
            }
        }

        @Override // java.text.CharacterIterator
        public Object clone() {
            try {
                AttributedIterator clone = (AttributedIterator) super.clone();
                if (this.attributesAllowed != null) {
                    clone.attributesAllowed = (HashSet) this.attributesAllowed.clone();
                }
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override // java.text.CharacterIterator
        public char current() {
            if (this.offset == this.end) {
                return (char) 65535;
            }
            return this.attrString.text.charAt(this.offset);
        }

        @Override // java.text.CharacterIterator
        public char first() {
            if (this.begin == this.end) {
                return (char) 65535;
            }
            this.offset = this.begin;
            return this.attrString.text.charAt(this.offset);
        }

        @Override // java.text.CharacterIterator
        public int getBeginIndex() {
            return this.begin;
        }

        @Override // java.text.CharacterIterator
        public int getEndIndex() {
            return this.end;
        }

        @Override // java.text.CharacterIterator
        public int getIndex() {
            return this.offset;
        }

        private boolean inRange(Range range) {
            if (range.value instanceof Annotation) {
                return range.start >= this.begin && range.start < this.end && range.end > this.begin && range.end <= this.end;
            }
            return true;
        }

        private boolean inRange(List<Range> ranges) {
            for (Range range : ranges) {
                if (range.start >= this.begin && range.start < this.end) {
                    return !(range.value instanceof Annotation) || (range.end > this.begin && range.end <= this.end);
                } else if (range.end > this.begin && range.end <= this.end) {
                    return !(range.value instanceof Annotation) || (range.start >= this.begin && range.start < this.end);
                }
            }
            return false;
        }

        @Override // java.text.AttributedCharacterIterator
        public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys() {
            if (this.begin == 0 && this.end == this.attrString.text.length() && this.attributesAllowed == null) {
                return this.attrString.attributeMap.keySet();
            }
            HashSet hashSet = new HashSet(((this.attrString.attributeMap.size() * 4) / 3) + 1);
            for (Map.Entry<AttributedCharacterIterator.Attribute, List<Range>> entry : this.attrString.attributeMap.entrySet()) {
                if (this.attributesAllowed == null || this.attributesAllowed.contains(entry.getKey())) {
                    List<Range> ranges = entry.getValue();
                    if (inRange(ranges)) {
                        hashSet.add(entry.getKey());
                    }
                }
            }
            return hashSet;
        }

        private Object currentValue(List<Range> ranges) {
            for (Range range : ranges) {
                if (this.offset >= range.start && this.offset < range.end) {
                    if (inRange(range)) {
                        return range.value;
                    }
                    return null;
                }
            }
            return null;
        }

        @Override // java.text.AttributedCharacterIterator
        public Object getAttribute(AttributedCharacterIterator.Attribute attribute) {
            ArrayList<Range> ranges;
            if ((this.attributesAllowed != null && !this.attributesAllowed.contains(attribute)) || (ranges = (ArrayList) this.attrString.attributeMap.get(attribute)) == null) {
                return null;
            }
            return currentValue(ranges);
        }

        @Override // java.text.AttributedCharacterIterator
        public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
            HashMap hashMap = new HashMap(((this.attrString.attributeMap.size() * 4) / 3) + 1);
            for (Map.Entry<AttributedCharacterIterator.Attribute, List<Range>> entry : this.attrString.attributeMap.entrySet()) {
                if (this.attributesAllowed == null || this.attributesAllowed.contains(entry.getKey())) {
                    Object value = currentValue(entry.getValue());
                    if (value != null) {
                        hashMap.put(entry.getKey(), value);
                    }
                }
            }
            return hashMap;
        }

        @Override // java.text.AttributedCharacterIterator
        public int getRunLimit() {
            return getRunLimit(getAllAttributeKeys());
        }

        private int runLimit(List<Range> ranges) {
            int result = this.end;
            ListIterator<Range> it = ranges.listIterator(ranges.size());
            while (it.hasPrevious()) {
                Range range = it.previous();
                if (range.end <= this.begin) {
                    break;
                } else if (this.offset >= range.start && this.offset < range.end) {
                    return inRange(range) ? range.end : result;
                } else if (this.offset >= range.end) {
                    break;
                } else {
                    result = range.start;
                }
            }
            return result;
        }

        @Override // java.text.AttributedCharacterIterator
        public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
            if (this.attributesAllowed != null && !this.attributesAllowed.contains(attribute)) {
                return this.end;
            }
            ArrayList<Range> ranges = (ArrayList) this.attrString.attributeMap.get(attribute);
            if (ranges == null) {
                return this.end;
            }
            return runLimit(ranges);
        }

        @Override // java.text.AttributedCharacterIterator
        public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> attributes) {
            int limit = this.end;
            for (AttributedCharacterIterator.Attribute attribute : attributes) {
                int newLimit = getRunLimit(attribute);
                if (newLimit < limit) {
                    limit = newLimit;
                }
            }
            return limit;
        }

        @Override // java.text.AttributedCharacterIterator
        public int getRunStart() {
            return getRunStart(getAllAttributeKeys());
        }

        private int runStart(List<Range> ranges) {
            int result = this.begin;
            for (Range range : ranges) {
                if (range.start >= this.end) {
                    break;
                } else if (this.offset >= range.start && this.offset < range.end) {
                    return inRange(range) ? range.start : result;
                } else if (this.offset < range.start) {
                    break;
                } else {
                    result = range.end;
                }
            }
            return result;
        }

        @Override // java.text.AttributedCharacterIterator
        public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
            if (this.attributesAllowed != null && !this.attributesAllowed.contains(attribute)) {
                return this.begin;
            }
            ArrayList<Range> ranges = (ArrayList) this.attrString.attributeMap.get(attribute);
            if (ranges == null) {
                return this.begin;
            }
            return runStart(ranges);
        }

        @Override // java.text.AttributedCharacterIterator
        public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> attributes) {
            int start = this.begin;
            for (AttributedCharacterIterator.Attribute attribute : attributes) {
                int newStart = getRunStart(attribute);
                if (newStart > start) {
                    start = newStart;
                }
            }
            return start;
        }

        @Override // java.text.CharacterIterator
        public char last() {
            if (this.begin == this.end) {
                return (char) 65535;
            }
            this.offset = this.end - 1;
            return this.attrString.text.charAt(this.offset);
        }

        @Override // java.text.CharacterIterator
        public char next() {
            if (this.offset >= this.end - 1) {
                this.offset = this.end;
                return (char) 65535;
            }
            String str = this.attrString.text;
            int i = this.offset + 1;
            this.offset = i;
            return str.charAt(i);
        }

        @Override // java.text.CharacterIterator
        public char previous() {
            if (this.offset == this.begin) {
                return (char) 65535;
            }
            String str = this.attrString.text;
            int i = this.offset - 1;
            this.offset = i;
            return str.charAt(i);
        }

        @Override // java.text.CharacterIterator
        public char setIndex(int location) {
            if (location < this.begin || location > this.end) {
                throw new IllegalArgumentException();
            }
            this.offset = location;
            if (this.offset == this.end) {
                return (char) 65535;
            }
            return this.attrString.text.charAt(this.offset);
        }
    }
}