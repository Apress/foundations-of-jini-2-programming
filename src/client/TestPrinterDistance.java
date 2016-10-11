
package client;

import common.Printer;
import common.Distance;

import java.util.Vector;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.lookup.entry.Location;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.entry.Entry;

/**
 * TestPrinterDistance.java
 *
 *
 * Created: Tue Apr 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    simplified Class.forName to Class.class
 */

public class TestPrinterDistance implements DiscoveryListener {

    protected Distance distance = null;
    protected Object distanceLock = new Object();
    protected Vector printers = new Vector();

    public static void main(String argv[]) {
	new TestPrinterDistance();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(10000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestPrinterDistance() {
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
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Service found");
            ServiceRegistrar registrar = registrars[n];

	    new LookupThread(registrar).start();
	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }

    class LookupThread extends Thread {

	ServiceRegistrar registrar;

	LookupThread(ServiceRegistrar registrar) {
	    this.registrar = registrar;
	}


	public void run() {

	    synchronized(distanceLock) {
		// only look for one distance service
		if (distance == null) {
		    lookupDistance();
		}
		if (distance != null) {
		    // found a new distance service
		    // process any previously found printers
		    synchronized(printers) {
			for (int n = 0; n < printers.size(); n++) {
			    ServiceItem item = (ServiceItem) printers.elementAt(n);
			    reportDistance(item);
			}
		    }
		}
	    }

	    ServiceMatches matches = lookupPrinters();
	    for (int n = 0; n < matches.items.length; n++) {
		if (matches.items[n] != null) {
		    synchronized(distanceLock) {
			if (distance != null) {
			    reportDistance(matches.items[n]);
			} else {
			    synchronized(printers) {
				printers.addElement(matches.items[n]);
			    }
			}
		    }
		}
	    }
	}

	/*
	 * We must be protected by the lock on distanceLock here
	 */
	void lookupDistance() {
	    // If we don't have a distance service, see if this
	    // locator knows of one
	    Class[] classes = new Class[] {Distance.class};
	    ServiceTemplate template = new ServiceTemplate(null, classes, 
							   null);
	    
	    try {
		distance = (Distance) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
	    }

	}

	ServiceMatches lookupPrinters() {
	    // look for printers with
	    // wildcard matching on all fields of Location
	    Entry[] entries = new Entry[] {new Location(null, null, null)};

	    Class[] classes = new Class[1];
	    try {
		classes[0] = Class.forName("common.Printer");
	    } catch(ClassNotFoundException e) {
		System.err.println("Class not found");
		System.exit(1);
	    }
	    ServiceTemplate template = new ServiceTemplate(null, classes, 
							   entries);
	    ServiceMatches matches = null;
	    try {
		matches =  registrar.lookup(template, 10);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
	    }
	    return matches;
	}

	/**
	 * report on the distance of the printer from
	 * this client
	 */
	void reportDistance(ServiceItem item) {
	    Location whereAmI = getMyLocation();
	    Location whereIsPrinter = getPrinterLocation(item);
	    if (whereIsPrinter != null) {
		int dist = distance.getDistance(whereAmI, whereIsPrinter);
		System.out.println("Found a printer at " + dist +
				   " units of length away");
	    }
	}

	Location getMyLocation() {
	    return new Location("1", "1", "Building 1");
	}

	Location getPrinterLocation(ServiceItem item) {
	    Entry[] entries = item.attributeSets;
	    for (int n = 0; n < entries.length; n++) {
		if (entries[n] instanceof Location) {
		    return (Location) entries[n];
		}
	    }
	    return null;
	}
    }

} // TestFileClassifier

