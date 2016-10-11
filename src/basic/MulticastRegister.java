package basic;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.discovery.LookupLocator;
import java.rmi.RemoteException;

/**
 * MulticastRegister.java
 *
 *
 * Created: Fri Mar 12 22:49:33 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    renamed as MulticastRegister
 *    moved sleep() to main() from constructor
 *    moved to package basic
 */

public class MulticastRegister implements DiscoveryListener {
 
    static public void main(String argv[]) {
        new MulticastRegister();

	// stay around long enough to receive replies
	try {
	    Thread.currentThread().sleep(10000L);
	} catch(java.lang.InterruptedException e) {
	    // do nothing
	}
    }
      
    public MulticastRegister() {
	System.setSecurityManager(new java.rmi.RMISecurityManager());
        LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
        }
        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
	    ServiceRegistrar registrar = registrars[n];

	    // the code takes separate routes from here for client or service
	    try {
		System.out.println("found a service locator at " + 
				   registrar.getLocator().getHost() +
                                   " at port " +
                                   registrar.getLocator().getPort());
	    } catch(RemoteException e) {
		e.printStackTrace();
	    }
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }
} // MulticastRegister

