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
// import com.sun.jini.lease.LeaseRenewalManager; // Jini 1.0
// import com.sun.jini.lease.LeaseListener;       // Jini 1.0
// import com.sun.jini.lease.LeaseRenewalEvent;   // Jini 1.0
import net.jini.lease.LeaseListener;              // Jini 1.1
import net.jini.lease.LeaseRenewalEvent;          // Jini 1.1
import net.jini.lease.LeaseRenewalManager;        // Jini 1.1

import java.io.*;

/**
 * FileClassifierServerID.java
 *
 *
 * Created: Wed Apr 20 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierServerID implements DiscoveryListener, 
                                             LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    protected ServiceID serviceID = null;

    public static void main(String argv[]) {
	new FileClassifierServerID();
	
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

    public FileClassifierServerID() {
	// try to load the service ID from file
	DataInput din = null;
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
					       new FileClassifierImpl(), 
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.println("Register exception: " + e.toString());
		continue;
	    }
	    System.out.println("service registered with id " + reg.getServiceID());

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
