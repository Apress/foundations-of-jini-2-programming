
package client;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceItem;

import java.util.Enumeration;
import java.net.URL;
import java.rmi.server.RMIClassLoader;

/**
 * CodebaseChecker
 *
 * For any Jini services, list the service class and the
 * class annotations - this will print the codebase used
 * by the service.
 *
 * Created: Aug 22 2004
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class CodebaseChecker implements DiscoveryListener {

    public static void main(String argv[]) {
	new CodebaseChecker();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public CodebaseChecker() {
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
	Class [] classes = new Class[] {Object.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);
 
        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];
	    System.out.print("Lookup service found at "); 
	    try {
		System.out.println(registrar.getLocator().getHost());
	    } catch(RemoteException e) {
		continue;
	    }
	    ServiceMatches matches = null;
	    try {
		matches = registrar.lookup(template, Integer.MAX_VALUE);
	    } catch(RemoteException e) {
		System.err.println("Can't decribe service: " + e.toString());
		continue;
	    }
	    ServiceItem[] items = matches.items;
	    for (int m = 0; m < items.length; m++) {
		Object service = items[m].service;
		if (service != null) {
		    checkService(service);
		} else {
		    System.out.println("Got null service, can't check it");
		}
	    }
	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }

    private void checkService(Object obj) {
	System.out.println("Checking service " + obj.toString());
	Class cls = obj.getClass();
	System.out.println("Annotation is " + 
			   RMIClassLoader.getClassAnnotation(cls));
    }
} // CodebaseChecker
