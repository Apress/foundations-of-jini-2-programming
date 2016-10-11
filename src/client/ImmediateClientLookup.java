
package client;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;

/**
 * ImmediateClientLookup.java
 *
 *
 * Created: Mon Dec 20 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class ImmediateClientLookup {

    private static final long WAITFOR = 100000L;

    public static void main(String argv[]) {
	new ImmediateClientLookup();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public ImmediateClientLookup() {
	ServiceDiscoveryManager clientMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null, // unicast locators
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

	ServiceItem item = null;
	// Try to find the service, blocking till timeout if necessary
	try {
	    item = clientMgr.lookup(template, 
				    null, // no filter 
				    WAITFOR); // timeout
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("no service");
	    System.exit(1);
	}

	// Get the service
	FileClassifier classifier = (FileClassifier) item.service;

	if (classifier == null) {
	    System.out.println("Classifier null");
	    System.exit(1);
	}

	// Now we have a suitable service, use it
	MIMEType type;
	try {
	    String fileName;
	    
	    // Try several file types: .txt, .rtf, .abc
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
} // ImmediateClientLookup
