package basic;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import java.io.Serializable;
import java.rmi.RMISecurityManager;

/**
 * SimpleService.java
 *
 *
 * Created: Fri Mar 12 22:34:53 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved to package basic
 */

public class SimpleService implements Serializable {
    
    static public void main(String argv[]) {
        new SimpleService();
    }
   
    public SimpleService() {
	LookupLocator lookup = null;
	ServiceRegistrar registrar = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            lookup = new LookupLocator("jini://localhost");
        } catch(java.net.MalformedURLException e) {
            System.err.println("Lookup failed: " + e.toString());
	    System.exit(1);
        }

	try {
	    registrar = lookup.getRegistrar();
	} catch (java.io.IOException e) {
            System.err.println("Registrar search failed: " + e.toString());
	    System.exit(1);
	} catch (java.lang.ClassNotFoundException e) {
            System.err.println("Registrar search failed: " + e.toString());
	    System.exit(1);
	}
	System.out.println("Found a registrar");

	// register ourselves as service, with no serviceID
	// or set of attributes
	ServiceItem item = new ServiceItem(null, this, null);
	ServiceRegistration reg = null;
	try {
	    // ask to register for 10,000,000 milliseconds
	    reg = registrar.register(item, 10000000L);
	} catch(java.rmi.RemoteException e) {
	    System.err.println("Register exception: " + e.toString());
	}
	System.out.println("Service registered with registration id: " + 
			   reg.getServiceID());

	// we can exit here if the exported service object can do
	// everything, or we can sleep if it needs to communicate 
	// to us or we need to renew a lease later
	//
	// Typically, we will need to renew a lease later 
    }
   
} // SimpleService
