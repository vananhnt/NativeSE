package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: SecurityServerList.class */
public class SecurityServerList extends SIPHeaderList<SecurityServer> {
    private static final long serialVersionUID = -1392066520803180238L;

    public SecurityServerList() {
        super(SecurityServer.class, "Security-Server");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        SecurityServerList retval = new SecurityServerList();
        return retval.clonehlist(this.hlist);
    }
}