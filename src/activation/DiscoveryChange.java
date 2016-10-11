
package activation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.lease.ExpirationWarningEvent;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.lease.LeaseRenewalSet;
import net.jini.discovery.RemoteDiscoveryEvent;
import java.rmi.RemoteException;
import  net.jini.discovery.LookupUnmarshalException;

import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.activation.ActivationExporter;

import rmi.RemoteFileClassifier;

public class DiscoveryChange
    implements RemoteEventListener, ProxyAccessor  {
    
    private LeaseRenewalSet leaseRenewalSet;
    private RemoteFileClassifier service;
    private Remote proxy;
    
    public DiscoveryChange(ActivationID id, MarshalledObject data) 
	throws RemoteException {
	Object[] objs = null;
	try {
	    objs = (Object []) data.get();
	} catch(ClassNotFoundException e) {
	    e.printStackTrace();
	} catch(java.io.IOException e) {
	    e.printStackTrace();
	}
	service = (RemoteFileClassifier) objs[0];
	leaseRenewalSet= (LeaseRenewalSet) objs[1];

	Exporter exporter = 
	    new ActivationExporter(id,
			 new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
					       new BasicILFactory(),
					       false, true));
	    
	proxy = (Remote) exporter.export(this);
    }
    
    public void notify(RemoteEvent evt) {
	System.out.println("lookups changing... " + evt.toString());
	RemoteDiscoveryEvent revt = (RemoteDiscoveryEvent) evt;

	if (! revt.isDiscarded()) {
	    // The event is a discovery event
	    ServiceItem item = new ServiceItem(null, service, null);
	    ServiceRegistrar[] registrars = null;
	    try {
		registrars = revt.getRegistrars();
	    } catch(LookupUnmarshalException e) {
		e.printStackTrace();
		return;
	    }
	    for (int n = 0; n < registrars.length; n++) {
		ServiceRegistrar registrar = registrars[n];
		
		ServiceRegistration reg = null;
		try {
		    reg = registrar.register(item, Lease.FOREVER);
		    leaseRenewalSet.renewFor(reg.getLease(), Lease.FOREVER);
		} catch(RemoteException e) {
		    System.err.println("Register exception: " + e.toString());
		}
	    }
	}
    }

    public Object getProxy() {
	return proxy;
    }

}
