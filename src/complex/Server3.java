
package complex;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.discovery.LookupLocator;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import java.rmi.SecurityManager;

/**
 * Server3.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *   uses Jini 1.1 JoinManager
 */

public class Server3 implements ServiceIDListener {
    
    JoinManager joinMgr = null;

    public static void main(String argv[]) {
	new Server3();
    }

    public Server3() {

        System.setSecurityManager(new RMISecurityManager());
	
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);

	    joinMgr = new JoinManager(new NameEntryImpl3(),
				      null,
				      this,
				      mgr,
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }

    }

    public void serviceIDNotify(ServiceID serviceID) {
	System.out.println("got service ID " + serviceID.toString());
	ServiceRegistrar[] registrars = joinMgr.getJoinSet();
	for (int n = 0; n < registrars.length; n++) {
	    LookupLocator locator = null;
	    try {
		locator = registrars[n].getLocator();
	    } catch(java.rmi.RemoteException e) {
		System.err.println("No host");
	    }
	    String hostName = locator.getHost();
	    System.out.println("Host: " + hostName);
	}
    }

} // Server2
