package complex;

import printer.Printer30;
import printer.Printer20;
import complex.DistanceImpl;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;

import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.lookup.entry.Location;
import net.jini.core.entry.Entry;
import net.jini.discovery.LookupDiscoveryManager;
import java.rmi.RMISecurityManager;

/**
 * PrinterServerLocation.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *   uses JoinManager 1.1
 */

public class PrinterServerLocation implements ServiceIDListener {
    
    public static void main(String argv[]) {
	new PrinterServerLocation();

        // run forever
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(InterruptedException e) {
		// do nothing
	    }
	}    
    }

    public PrinterServerLocation() {

	System.setSecurityManager(new RMISecurityManager());

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);
	    // distance service
	    joinMgr = new JoinManager(new DistanceImpl(),
				      null,
				      this,
				      mgr,
				      new LeaseRenewalManager());


	    // slow printer in room 20
	    joinMgr = new JoinManager(new Printer20(),
				      new Entry[] {new Location("1", "20", 
								"Building 1")},
				      this,
				      mgr,
				      new LeaseRenewalManager());

	    // fast printer in room 30
	    joinMgr = new JoinManager(new Printer30(),
				      new Entry[] {new Location("1", "30", 
								"Building 1")},
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
    
} // PrinterServerLocation
