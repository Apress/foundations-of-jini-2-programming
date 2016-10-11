
package client;

import common.Printer;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceItemFilter;
/**
 * TestPrinterSpeedFilter.java
 *
 *
 * Created: Mon Dec 20 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class TestPrinterSpeedFilter implements ServiceItemFilter {
    private static final long WAITFOR = 100000L;
    
    public TestPrinterSpeedFilter() {
	ServiceDiscoveryManager clientMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null,  // unicast locators
                                           null); // DiscoveryListener
	    clientMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        Class[] classes = new Class[] {Printer.class};

        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);
 	ServiceItem item = null;
	try {
	    item = clientMgr.lookup(template, 
				    this,     // filter 
				    WAITFOR); // timeout
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	if (item == null) {
	    // couldn't find a service in time
	    System.exit(1);
	}

	Printer printer = (Printer) item.service;
	// Now use the printer
	// ...
    }

    public boolean check(ServiceItem item) {
	// This is the filter
	Printer printer = (Printer) item.service;
	if (printer.getSpeed() > 24) {
	    return true;
	} else {
	    return false;
	}
    }

    public static void main(String[] args) {
	
	TestPrinterSpeed f = new TestPrinterSpeed();

        // stay around long enough to receive replies	
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }
    
} // TestPrinterSpeed
