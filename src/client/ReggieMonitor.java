
package client;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.config.*; 
import java.util.Vector;
import observer.RegistrarObserver;

/**
 * ReggieMonitor.java
 *
 *
 * Created: Wed Mar 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 1.1
 *    use explicit proxy for Jini 2.0
 */

public class ReggieMonitor implements DiscoveryListener {

    private Vector observers = new Vector();
    private Configuration config;

    public static void main(String argv[]) {
	new ReggieMonitor(argv);

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public ReggieMonitor(String[] argv) {
	String[] configArgs = new String[] {argv[0]};

	try {
	    // get the configuration (by default a FileConfiguration) 
	    config = ConfigurationProvider.getInstance(configArgs); 
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}	

	System.setSecurityManager(new RMISecurityManager());

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
	    System.out.println("Service lookup found");
            ServiceRegistrar registrar = registrars[n];
	    if (registrar == null) {
		System.out.println("registrar null");
		continue;
	    }
	    try {
		System.out.println("Lookup service at " +
			       registrar.getLocator().getHost());
	    } catch(RemoteException e) {
		System.out.println("Lookup service infor unavailable");
	    }

	    try {
		observers.add(new RegistrarObserver(config, registrar));
	    } catch(RemoteException e) {
		System.out.println("adding observer failed");
	    }

	    ServiceTemplate templ = new ServiceTemplate(null, new Class[] {Object.class}, null);
	    ServiceMatches matches = null;
	    try {
		matches = registrar.lookup(templ, 10);
	    } catch(RemoteException e) {
		System.out.println("lookup failed");
	    }

	    for (int m = 0; m < matches.items.length; m++) {
		if (matches.items[m] != null && matches.items[m].service != null) {
		    System.out.println("Reg knows about " + matches.items[m].service.toString() +
				   " with id " + matches.items[m].serviceID);
		}
	    }

	}
    }

    public void discarded(DiscoveryEvent evt) {
	// remove observer
    }
} // ReggieMonitor
