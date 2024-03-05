package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: PathList.class */
public class PathList extends SIPHeaderList<Path> {
    public PathList() {
        super(Path.class, "Path");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        PathList retval = new PathList();
        return retval.clonehlist(this.hlist);
    }
}