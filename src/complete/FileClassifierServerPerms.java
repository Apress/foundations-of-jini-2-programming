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
 */

public class FileClassifierServer implements DiscoveryListener, 
                                             LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    protected ServiceID serviceID = null;

    public static void main(String argv[]) {
	new FileClassifierServer();
	
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
	    System.out.println("service registered");

	    System.out.println("Class " + registrar.getClass().toString());
	    Object [] signers = registrar.getClass().getSigners();
	    if (signers == null) {
		System.out.println("No signers");
	    } else {
		System.out.println("Signers");
		for (int m = 0; m < signers.length; m++)
		    System.out.println(signers[m].toString());
	    }
	    java.security.ProtectionDomain domain = registrar.
		getClass().getProtectionDomain();
	    java.security.CodeSource codeSource = domain.getCodeSource();
	    System.out.println("CodeSource " + codeSource.toString());
	    RuntimePermission perm = new RuntimePermission("getProtectionDomain");
	    if (domain.implies(perm))
		System.out.println("Permission allowed");
	    else
		System.out.println("Permission not allowed");
	    // should be ok - we aren't in "domain", we have given permission
	    System.getSecurityManager().checkPermission(perm);

	    // set lease renewal in place
	    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);

	    // set the serviceID if necessary
	    if (serviceID == null) {
		serviceID = reg.getServiceID();
	    }
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }   
    
} // FileClassifierServer
