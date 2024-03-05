package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: ServiceRouteList.class */
public class ServiceRouteList extends SIPHeaderList<ServiceRoute> {
    private static final long serialVersionUID = -4264811439080938519L;

    public ServiceRouteList() {
        super(ServiceRoute.class, "Service-Route");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ServiceRouteList retval = new ServiceRouteList();
        return retval.clonehlist(this.hlist);
    }
}