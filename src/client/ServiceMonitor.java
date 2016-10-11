
package client;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;

/**
 * ServiceMonitor.java
 *
 *
 * Created: Wed Mar 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class ServiceMonitor implements ServiceDiscoveryListener {

    public static void main(String argv[]) {
	new ServiceMonitor();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public ServiceMonitor() {
        ServiceDiscoveryManager clientMgr = null;
        LookupCache cache = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null,  // unicast locators
                                           null); // DiscoveryListener
            clientMgr = new ServiceDiscoveryManager(mgr, 
                                                new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
        ServiceTemplate template = new ServiceTemplate(null, null, 
                                                       null);
        try {
            cache = clientMgr.createLookupCache(template, 
                                                null,  // no filter
                                                this); // listener
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // methods for ServiceDiscoveryListener
    public void serviceAdded(ServiceDiscoveryEvent evt) {
	// evt.getPreEventServiceItem() == null
	ServiceItem postItem = evt.getPostEventServiceItem();
	System.out.println("Service appeared: " +
			   postItem.service.getClass().toString());
    }

    public void serviceChanged(ServiceDiscoveryEvent evt) {
	ServiceItem preItem = evt.getPostEventServiceItem();
	ServiceItem postItem = evt.getPreEventServiceItem() ;
	System.out.println("Service changed: " +
			   postItem.service.getClass().toString());
    }
    public void serviceRemoved(ServiceDiscoveryEvent evt) {
	// evt.getPostEventServiceItem() == null
	ServiceItem preItem = evt.getPreEventServiceItem();
	System.out.println("Service disappeared: " +
			   preItem.service.getClass().toString());
    }
    
} // ServiceMonitor
