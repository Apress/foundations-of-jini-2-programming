package discoverymgt;

import net.jini.discovery.LookupDiscoveryManager;
import net.jini.discovery.DiscoveryGroupManagement;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.discovery.LookupLocator;
import java.net.MalformedURLException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;

/**
 * AllcastRegister.java
 *
 *
 * Created: Sunday 19 Dec 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class AllcastRegister implements DiscoveryListener {
 
    static public void main(String argv[]) {
        new AllcastRegister();

	// stay around long enough to receive replies
	try {
	    Thread.currentThread().sleep(10000L);
	} catch(java.lang.InterruptedException e) {
	    // do nothing
	}
    }
      
    public AllcastRegister() {
	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

        LookupDiscoveryManager discover = null;
	LookupLocator[] locators = null;
	try {
	    locators = new LookupLocator[] {new LookupLocator("jini://localhost")};
	} catch(MalformedURLException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
        try {
            discover = new LookupDiscoveryManager(DiscoveryGroupManagement.ALL_GROUPS,
						  locators,
						  this);
        } catch(IOException e) {
            System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
        }
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
	    ServiceRegistrar registrar = registrars[n];

	    try {
		System.out.println("found a service locator at " +
			       registrar.getLocator().getHost());
	    } catch(RemoteException e) {
		e.printStackTrace();
		continue;
	    }
	    // the code takes separate routes from here for client or service
  	}
    }

    public void discarded(DiscoveryEvent evt) {

    }
} // AllcastRegister

