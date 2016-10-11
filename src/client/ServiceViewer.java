
/**
 * ServiceViewer.java
 *
 *
 * Created: Thu Apr 19 04:54:57 2001
 *
 * @author <a href="mailto: "Jan Newmarch</a>
 * @version
 */

package client;

import common.ServiceFinder;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.discovery.LookupLocator;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.core.lookup.ServiceRegistrar;

public class ServiceViewer {

    private static final long WAITFOR = 10000L;

    protected ServiceFinder finder;

    public static void main(String argv[]) {
        new ServiceViewer();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
	System.err.println("Client timed out");
	System.exit(0);
    }

    public ServiceViewer () {

        LookupLocator lookup = null;
	ServiceRegistrar registrar = null;

        System.setSecurityManager(new RMISecurityManager());

        try {
            lookup = new LookupLocator("jini://localhost");
	    registrar = lookup.getRegistrar();
        } catch(Exception e) {
            System.err.println("First lookup failed: " + e.toString());
        }

  
        Class [] classes = new Class[] {ServiceFinder.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);

	Object service = null;
	try {
	    service = registrar.lookup(template);
	} catch(Exception e) {
	    System.err.println(e.toString());
	}
        if (service == null) {
            // couldn't find a service in time
            System.out.println("no service");
            System.exit(1);
        }
	System.out.println("Found service " + service.toString());

        // Get the service
        finder = (ServiceFinder) service;
	printServices();

	System.exit(0);
    }

    void printServices() {
	System.out.println("Finding services");
	ServiceItem[] services = null;
	try {
	    services = finder.getServices();
	} catch(java.rmi.RemoteException e) {
	    System.err.println(e.toString());
	    return;
	}
	for (int n = 0; n < services.length; n++) {
	    ServiceItem serviceItem = services[n];
	    System.out.println(serviceItem.service.toString());
	}
    }
}// ServiceViewer
