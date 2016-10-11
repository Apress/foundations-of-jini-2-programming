
package rmi;

import java.rmi.Naming;
import java.net.InetAddress;
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
import java.rmi.RMISecurityManager;

/**
 * FileClassifierServerNaming.java
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

public class FileClassifierServerNaming implements DiscoveryListener, LeaseListener {

    // This is just a name for the service
    // It can be anything, just needs to shared by both
    // ends of the Naming service
    static final String serviceName = "FileClassifier";

    protected rmi.FileClassifierImpl impl;
    protected FileClassifierNamingProxy proxy;
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    
    public static void main(String argv[]) {
	new FileClassifierServerNaming();

        // no need to keep server alive, RMI will do that
    }

    public FileClassifierServerNaming() {
	try {
	    impl = new rmi.FileClassifierImpl();
	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	// register this with RMI registry
	System.setSecurityManager(new RMISecurityManager());
	try {
	    Naming.rebind("rmi://localhost/" + serviceName, impl);
	} catch(java.net.MalformedURLException e) {
            System.err.println("Binding: " + e.toString());
            System.exit(1);
	} catch(java.rmi.RemoteException e) {
            System.err.println("Binding: " + e.toString());
            System.exit(1);
        }

	System.out.println("bound");
	// find where we are running
	String address = null;
	try {
	    address = InetAddress.getLocalHost().getHostName();
	} catch(java.net.UnknownHostException e) {
            System.err.println("Address: " + e.toString());
            System.exit(1);
        }

	String registeredName = "//" + address + "/" + serviceName;

	// make a proxy that knows the service address
	proxy = new FileClassifierNamingProxy(registeredName);

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
        
} // FileClassifierServerNaming
