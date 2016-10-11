
/**
 * @version 1.1
 *   changed getLease() to getRenewalSetLease() for Jini 1.1. beta
 * @version 2.0
 *   converted to Jini 2.0
 */

package activation;

import java.rmi.Remote;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.lease.ExpirationWarningEvent;

import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.activation.ActivationExporter;

public class RenewLease implements RemoteEventListener,
				   ProxyAccessor  {
    
    private Remote proxy;

    public RenewLease(ActivationID activationID, MarshalledObject data)  
	throws java.rmi.RemoteException {

	Exporter exporter = 
	    new ActivationExporter(activationID,
			 new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
					       new BasicILFactory(),
					       false, true));
	    
	proxy = (Remote) exporter.export(this);
    }
    
    public void notify(RemoteEvent evt) {
	System.out.println("expiring... " + evt.toString());
	ExpirationWarningEvent eevt = (ExpirationWarningEvent) evt;
	Lease lease = eevt.getRenewalSetLease();
	try {
	    // This is short, for testing. Try 2+ hours
	    lease.renew(20000L);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Lease renewed for " +
			   (lease.getExpiration() -
			    System.currentTimeMillis()));
    }

    public Object getProxy() {
	return proxy;
    }
}
