package gov.nist.javax.sip.header;

import java.util.ListIterator;

/* loaded from: RouteList.class */
public class RouteList extends SIPHeaderList<Route> {
    private static final long serialVersionUID = 3407603519354809748L;

    public RouteList() {
        super(Route.class, "Route");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        RouteList retval = new RouteList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.javax.sip.header.SIPHeader, gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        return this.hlist.isEmpty() ? "" : super.encode();
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (!(other instanceof RouteList)) {
            return false;
        }
        RouteList that = (RouteList) other;
        if (size() != that.size()) {
            return false;
        }
        ListIterator<Route> it = listIterator();
        ListIterator<Route> it1 = that.listIterator();
        while (it.hasNext()) {
            Route route = it.next();
            Route route1 = it1.next();
            if (!route.equals(route1)) {
                return false;
            }
        }
        return true;
    }
}