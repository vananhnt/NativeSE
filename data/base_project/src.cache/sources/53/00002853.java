package javax.sip.header;

/* loaded from: ReplyToHeader.class */
public interface ReplyToHeader extends HeaderAddress, Header, Parameters {
    public static final String NAME = "Reply-To";

    String getDisplayName();
}