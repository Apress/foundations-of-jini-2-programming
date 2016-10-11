package complex;

import printer.Printer30;
import printer.Printer20;

// import com.sun.jini.lookup.JoinManager;
import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
// import com.sun.jini.lookup.ServiceIDListener;
// import com.sun.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;

/**
 * PrinterServerSpeed.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *   uses Jini 1.1 JoinManager
 */

public class PrinterServerSpeed implements ServiceIDListener {
    
    public static void main(String argv[]) {
	new PrinterServerSpeed();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }

    }

    public PrinterServerSpeed() {

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);
	    // slow printer
	    joinMgr = new JoinManager(new Printer20(),
				      null,
				      this,
				      mgr,
				      new LeaseRenewalManager());
	    // joinMgr.setGroups(null);

	    // fast printer
	    joinMgr = new JoinManager(new Printer30(),
				      null,
				      this,
				      mgr,
				      new LeaseRenewalManager());
	    // joinMgr.setGroups(null);

	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // PrinterServerSpeed



