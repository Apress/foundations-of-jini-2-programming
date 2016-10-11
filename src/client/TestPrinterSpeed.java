
package client;

import common.Printer;

import java.rmi.RMISecurityManager;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;

/**
 * TestPrinterSpeed.java
 *
 *
 * Created: Mon Mar 29 12:03:51 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    simplified Class.forName to Class.class
 */

public class TestPrinterSpeed implements DiscoveryListener {
    
    public TestPrinterSpeed() {

	System.setSecurityManager(new RMISecurityManager());

        LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);

    }
    

    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();
        Class[] classes = new Class[] {Printer.class};

        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);
 
        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];
	    ServiceMatches matches;

	    try {
		matches = registrar.lookup(template, 10);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		continue;
	    }
	    // NB: matches.totalMatches may be greater than matches.items.length
	    for (int m = 0; m < matches.items.length; m++) {
		Printer printer = (Printer) matches.items[m].service;

		// Inexact matching is not performed by lookup()
		// we have to do it ourselves on each printer
		// we get
		int speed = printer.getSpeed();
		if (speed >= 24) {
		    // this one is okay, use its print() method
		    printer.print("fast enough printer");
		} else {
		    // we can't use this printer, so just say so
		    System.out.println("Printer too slow at " + speed);
		}
	    }

        }
    }

    public void discarded(DiscoveryEvent evt) {
        // empty
    }

    public static void main(String[] args) {
	
	TestPrinterSpeed f = new TestPrinterSpeed();

        // stay around long enough to receive replies	
        try {
            Thread.currentThread().sleep(10000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }
    
} // TestPrinterSpeed
