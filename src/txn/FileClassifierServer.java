package txn;

import txn.PayableFileClassifierImpl;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import java.rmi.RMISecurityManager;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Tue Aug 3 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    uses Jini 2.1
 */

public class FileClassifierServer implements ServiceIDListener {
    
    public static void main(String argv[]) {
	new FileClassifierServer();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }

    }

    public FileClassifierServer() {

        System.setSecurityManager(new RMISecurityManager());

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);
	    joinMgr = new JoinManager(new PayableFileClassifierImpl().getProxy(),
				      null,
				      this,
				      mgr,
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // FileClassifierServer
