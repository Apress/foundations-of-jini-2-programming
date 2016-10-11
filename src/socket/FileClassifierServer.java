
package socket;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
// import com.sun.jini.lease.LeaseRenewalManager;  // Jini 1.0
// import com.sun.jini.lease.LeaseListener;        // Jini 1.0
// import com.sun.jini.lease.LeaseRenewalEvent;    // Jini 1.0
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import java.rmi.RMISecurityManager;
import java.net.*;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Oct 25 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    uses Jini 1.1 LeaseRenewalManager
 */

public class FileClassifierServer implements DiscoveryListener, LeaseListener {

    protected FileClassifierProxy proxy;
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    
    public static void main(String argv[]) {
	new FileClassifierServer();
	try {
	    Thread.sleep(1000000L);
	} catch(Exception e) {
	}
    }

    public FileClassifierServer() {
	try {
	    new FileServerImpl().start();
	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	// set RMI scurity manager
	System.setSecurityManager(new RMISecurityManager());

	// proxy primed with address
	String host = null;
	try {
	    host = InetAddress.getLocalHost().getHostName();
	} catch(UnknownHostException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	proxy = new FileClassifierProxy(host);
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
