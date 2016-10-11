package complete;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceID ;
import net.jini.lease.LeaseListener;             
import net.jini.lease.LeaseRenewalEvent;         
import net.jini.lease.LeaseRenewalManager;       

import java.io.*;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    uses Jini 1.1
 *    moved to package complete from option2
 *    added LeaseRenewalManager
 *    moved sleep() from constructor to main()
 * @version 1.3
 *    made strong references to the implementation
 *    to avoid garbage collection
 */

public class FileClassifierServer implements DiscoveryListener, 
                                             LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    protected ServiceID serviceID = null;
    protected 	FileClassifierImpl impl;

    public static void main(String argv[]) {
	FileClassifierServer s = new FileClassifierServer();
	
        // keep server running forever to 
	// - allow time for locator discovery and
	// - keep re-registering the lease
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FileClassifierServer() {
	// Create the service
	impl = new FileClassifierImpl();

	// Try to load the service ID from file.
	// It isn't an error if we can't load it, because
	// maybe this is the first time this service has run
	DataInputStream din = null;
	try {
	    din = new DataInputStream(new FileInputStream("FileClassifier.id"));
	    serviceID = new ServiceID(din);
	} catch(Exception e) {
	    // ignore
	}

        System.setSecurityManager(new RMISecurityManager());

	LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println("Discovery failed " + e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

	    ServiceItem item = new ServiceItem(serviceID,
					       impl, 
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.println("Register exception: " + e.toString());
		continue;
	    }
	    System.out.println("Service registered with id " + reg.getServiceID());

	    // set lease renewal in place
	    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);

	    // set the serviceID if necessary
	    if (serviceID == null) {
		serviceID = reg.getServiceID();

		// try to save the service ID in a file
		DataOutputStream dout = null;
		try {
		    dout = new DataOutputStream(new FileOutputStream("FileClassifier.id"));
		    serviceID.writeBytes(dout);
		    dout.flush();
		} catch(Exception e) {
		    // ignore
		}

	    }
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }   
    
} // FileClassifierServer
