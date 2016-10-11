
package activation;

import rmi.RemoteFileClassifier;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryService;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.LookupDiscoveryManager;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;

import net.jini.lease.LeaseRenewalService;
import net.jini.lease.LeaseRenewalSet;
import net.jini.lease.LeaseRenewalManager;

import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.LookupCache;

import java.rmi.RMISecurityManager;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;

import java.util.Properties;
import java.util.Vector;



/**
 * FileClassifierServerDiscovery.java
 *
 *
 * Created: Wed Dec 22 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class Buggy
     implements DiscoveryListener, ServiceDiscoveryListener  {
    private static final long WAITFOR = 10000L;

    protected LookupDiscoveryService discoveryService = null;
    protected LeaseRenewalService leaseService = null;

    protected ServiceDiscoveryManager clientMgr = null;

    public static void main(String argv[]) {
	new Buggy(argv);
	// stick around while lookup services are found
	try {
	    Thread.sleep(20000L);
	} catch(InterruptedException e) {
	    // do nothing
	}
	// the server doesn't need to exist anymore
	System.exit(0);
    }

    public Buggy(String[] argv) {

	System.setSecurityManager(new RMISecurityManager());

	LookupDiscoveryManager mgr = null;
        try {
            mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null /* unicast locators */,
                                           this /* DiscoveryListener */);
            clientMgr = new ServiceDiscoveryManager(mgr, new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

	// try a cache as well as direct find
	LookupCache cache = null;
	try {
	    Class [] classes = new Class[] {Object.class};
	    ServiceTemplate template = new ServiceTemplate(null, classes, 
							   null);
	    cache = clientMgr.createLookupCache(template, null, this);
	} catch(Exception e) {
	    System.out.println("cache failed");
	}

	if (clientMgr.getDiscoveryManager().equals(mgr))
	    System.out.println("manager same");

	leaseService = (LeaseRenewalService) findService(LeaseRenewalService.class);
	if (leaseService == null) {
	    System.out.println("Lease service null");
	} else {
	    System.out.println("Service found " + leaseService.toString());
	}
	discoveryService = (LookupDiscoveryService) findService(LookupDiscoveryService.class);
	if (discoveryService == null) {
	    System.out.println("Discovery service null");
	} else {
	    System.out.println("Service found " + discoveryService.toString());
	}

	// How many registrars does discovery manager know about?
	ServiceRegistrar[] registrars = mgr.getRegistrars();
	System.out.println("Registrars " + registrars.length);
    }

    public Object findService(Class cls) {
        Class [] classes = new Class[] {cls};
        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);
        ServiceItem item = null;
        try {
            item = clientMgr.lookup(template, 
                                    null, /* no filter */ 
                                    WAITFOR /* timeout */);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (item == null) {
            // couldn't find a service in time
	    System.out.println("No service found for " + cls.toString());
	    return null;
        }
        return item.service;
    }

    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();
	RemoteFileClassifier service;

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

	    System.out.println("Found a regsitrar " + registrar.toString());

	    // see if we can find a lookup discovery service this way
	    Class [] classes = new Class[] {LookupDiscoveryService.class};
	    ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);
	    Object obj = null;
	    try {
		obj = registrar.lookup(template);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    System.out.println("From our listener: " + obj.toString());

	    // see if we can find a lease service this way
	    classes = new Class[] {LeaseRenewalService.class};
	    template = new ServiceTemplate(null, classes, 
                                                       null);
	    obj = null;
	    try {
		obj = registrar.lookup(template);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    System.out.println("From our listener: " +obj.toString());

	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    // from cache listener
    public void serviceAdded(ServiceDiscoveryEvent event) {
	System.out.println("service added " + event.toString());
    }

    public void serviceChanged(ServiceDiscoveryEvent event) {
    }

    public void serviceRemoved(ServiceDiscoveryEvent event) {
    }

} // FileClassifierServerDiscovery



    
