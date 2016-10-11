package discoverymgt;

import net.jini.discovery.LookupLocatorDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.discovery.LookupLocator;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;

/**
 * UniicastRegister.java
 *
 *
 * Created: Sunday 19 Dec 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class UnicastRegister implements DiscoveryListener {
 
    static public void main(String argv[]) {
        new UnicastRegister();

	// stay around long enough to receive replies
	try {
	    Thread.currentThread().sleep(10000L);
	} catch(java.lang.InterruptedException e) {
	    // do nothing
	}
    }
      
    public UnicastRegister() {
	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

        LookupLocatorDiscovery discover = null;
	LookupLocator[] locators = null;
	try {
	    locators = new LookupLocator[] {new LookupLocator("jini://localhost")};
	} catch(MalformedURLException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
        try {
            discover = new LookupLocatorDiscovery(locators);
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
	    System.out.println("found a service locator");
  	}
    }

    public void discarded(DiscoveryEvent evt) {

    }
} // UnicastRegister

