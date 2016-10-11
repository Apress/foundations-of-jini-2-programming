
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

/** 
 * BasicServiceLister
 * 
 * 
 * Created: Aug 10 2004
 * 
 * @author Jan Newmarch
 * @version 1.0
 */

public class BasicServiceLister implements DiscoveryListener {

    /** Entry point to the program
     * 
     * @param argv Command line arguments
     */
    public static void main(String argv[]) {
	new BasicServiceLister();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    /** Constructor
     */
    public BasicServiceLister() {
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
    
    /** One or more registrars has been discovered
     * 
     * @param evt Discovery event
     */
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
		    printObject(service);
		} else {
		    System.out.println("Got null service");
		}
	    }
	}
    }

    /** @param evt 
     */
    public void discarded(DiscoveryEvent evt) {
	// empty
    }

    /** Print the object's class information within its hierarchy
     * 
     * @param obj the object to be printed
     */
    private void printObject(Object obj) {
	System.out.println("Discovered service belongs to class \n" + 
			   obj.getClass().getName());
	printInterfaces(obj.getClass());
	/*
	Class[] interfaces = obj.getClass().getInterfaces();
	if (interfaces.length != 0) {
	    System.out.println("  Implements interfaces");
	    for (int n = 0; n < interfaces.length; n++) {
		System.out.println("    " + interfaces[n].getName());
	    }
	}
	*/
	printSuperClasses(obj.getClass());
    }

    /** Print information about superclasses
     * 
     * @param cls The class to be displayed
     */
    private void printSuperClasses(Class cls) {
	System.out.println("  With superclasses");
	while ((cls = cls.getSuperclass()) != null) {
	    System.out.println("    " + cls.getName());

	    printInterfaces(cls);
	}
    }

    private void printInterfaces(Class cls) {
	Class[] interfaces = cls.getInterfaces();
	if (interfaces.length != 0) {
	    System.out.println("      which implements interfaces");
	    for (int n = 0; n < interfaces.length; n++) {
		System.out.println("        " + interfaces[n]);
		printInterfaces(interfaces[n]);
	    }
	}
    }
} // BasicServiceLister
