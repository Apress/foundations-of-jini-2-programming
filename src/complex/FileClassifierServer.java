package complex;

import complete.FileClassifierImpl;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
// import com.sun.jini.lease.LeaseRenewalManager;
// import com.sun.jini.lease.LeaseListener;
// import com.sun.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    added LeaseRenewalManager
 *    moved sleep() from constructor to main()
 *    uses Jini 1.1 LeaseRenewalManager
 */

public class FileClassifierServer implements DiscoveryListener, 
                                             LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();

    public static void main(String argv[]) {
	new FileClassifierServer();

        // keep server running forever to 
	// - allow time for locator discovery and
	// - keep re-registering the lease
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FileClassifierServer() {

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
            ServiceRegistrar registrar = registrars[n];

	    new RegisterThread(registrar).start();
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }   

    /**
     * an inner class to register the service in its own thread
     */
    class RegisterThread extends Thread {

	ServiceRegistrar registrar;

	RegisterThread(ServiceRegistrar registrar) {
	    this.registrar = registrar;
	}

	public void run() {
	    ServiceItem item = new ServiceItem(null,
					       new FileClassifierImpl(), 
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.println("Register exception: " + e.toString());
		return;
	    }
	    System.out.println("service registered");

	    // set lease renewal in place
	    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, 
				    FileClassifierServer.this);
	}
    }
} // FileClassifierServer
