
package lease;

import net.jini.core.lease.Lease;
// import com.sun.jini.lease.LeaseRenewalManager;
// import com.sun.jini.lease.LeaseListener;
// import com.sun.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import java.rmi.RMISecurityManager;
import net.jini.core.lookup.ServiceID;
// import com.sun.jini.lookup.ServiceIDListener;
import net.jini.lookup.ServiceIDListener;
import common.LeaseFileClassifier;
// import com.sun.jini.lookup.JoinManager;
import net.jini.lookup.JoinManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.3
 *    added LeaseRenewalManager
 *    moved sleep() from constructor to main()
 *    uses Jini 1.1 LeaseRenewalManager
 */

public class FileClassifierServer implements LeaseListener, ServiceIDListener  {

    // this is just a name - can be anything
    // impl object forces search for Stub
    // static final String serviceName = "FileClassifier";
    // String registeredName;

    protected FileClassifierImpl impl;
    //protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    
    public static void main(String argv[]) {
	new FileClassifierServer();
        // no need to keep server alive, RMI will do that
	try {
	    Thread.sleep(1000000L);
	} catch(Exception e) {
	}
    }

    public FileClassifierServer() {

	System.setSecurityManager(new RMISecurityManager());

	/*
	System.err.println("standalone");
        try {
	    FileClassifierImpl impl = new FileClassifierImpl();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }            
	System.err.println("standalone");
	*/

        JoinManager joinMgr = null;
        try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);

            joinMgr = new JoinManager(new FileClassifierImpl(),
                                      null,
                                      this,
				      mgr,
                                      new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }            


    }
    
    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }

    public void serviceIDNotify(ServiceID serviceID) {
        System.out.println("got service ID " + serviceID.toString());
    }
    
  
} // FileClassifierServer
