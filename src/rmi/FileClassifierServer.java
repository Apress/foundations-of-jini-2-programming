
package rmi;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import java.rmi.RMISecurityManager;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.4
 *    moved from package option3 to rmi
 *    added LeaseRenewalManager
 *    moved sleep() from constructor to main()
 *    replaced FCImpl name in RCProxy with actual object, 
 *        and got rind of Naming() etc
 *    uses Jini 1.1 LeaseRenewalManager
 * @version 1.5
 *    modified for Jini 2.0
 */

public class FileClassifierServer implements DiscoveryListener, LeaseListener {

    // this is just a name - can be anything
    // impl object forces search for Stub
    static final String serviceName = "FileClassifier";

    protected FileClassifierImpl impl;
    protected FileClassifierProxy proxy;
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    
    public static void main(String argv[]) {
	new FileClassifierServer();
	try {
	    Thread.sleep(1000000L);
	} catch(Exception e) {
	}
        // no need to keep server alive, RMI will do that
    }

    public FileClassifierServer() {
	try {
	    impl = new FileClassifierImpl();
	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	// set RMI scurity manager
	System.setSecurityManager(new RMISecurityManager());

	// make a proxy with the impl (will be made into an RMI stub)
	proxy = new FileClassifierProxy(impl);

	// now continue as before
	LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
System.out.println("found registrars");
            ServiceRegistrar registrar = registrars[n];

	    // export the proxy service
	    ServiceItem item = new ServiceItem(null,
					       proxy, 
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.print("Register exception: ");
		e.printStackTrace();
		// System.exit(2);
		continue;
	    }
	    try {
		System.out.println("service registered at " +
				   registrar.getLocator().getHost());
	    } catch(Exception e) {
	    }
	    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }
        
} // FileClassifierServer
