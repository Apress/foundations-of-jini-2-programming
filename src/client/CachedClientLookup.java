
package client;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;

/**
 * CachedClientLookup.java
 *
 *
 * Created: Mon Dec 20 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class CachedClientLookup {

    private static final long WAITFOR = 100000L;

    public static void main(String argv[]) {
	new CachedClientLookup();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public CachedClientLookup() {
	ServiceDiscoveryManager clientMgr = null;
	LookupCache cache = null;

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
  
	Class [] classes = new Class[] {FileClassifier.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);

	try {
	    cache = clientMgr.createLookupCache(template, 
						null,  // no filter 
						null); // no listener
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// loop till we find a service
	ServiceItem item = null;
	while (item == null) {
	    System.out.println("no service yet");
	    try {
		Thread.currentThread().sleep(1000);
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	    // see if a service is there now
	    item = cache.lookup(null);
	}
	FileClassifier classifier = (FileClassifier) item.service;

	if (classifier == null) {
	    System.out.println("Classifier null");
	    System.exit(1);
	}

	// Now we have a suitable service, use it
	MIMEType type;
	try {
	    String fileName;
	    
	    fileName = "file1.txt";
	    type = classifier.getMIMEType(fileName);
	    printType(fileName, type);
	    
	    fileName = "file2.rtf";
	    type = classifier.getMIMEType(fileName);
	    printType(fileName, type);
	    
	    fileName = "file3.abc";
	    type = classifier.getMIMEType(fileName);
	    printType(fileName, type);
	} catch(java.rmi.RemoteException e) {
	    System.err.println(e.toString());
	}
	System.exit(0);
    }

    private void printType(String fileName, MIMEType type) {
	System.out.print("Type of " + fileName + " is ");
	if (type == null) {
	    System.out.println("null");
	} else {
	    System.out.println(type.toString());
	}
    }
} // CachedClientLookup
