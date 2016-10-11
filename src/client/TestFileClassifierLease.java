
package client;

import common.LeaseFileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lease.Lease;

/**
 * TestFileClassifierLease.java
 *
 *
 * Created: Wed Mar 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.3
 *    moved sleep() from constructor to main()
 *    moved to package client
 *    simplified Class.forName to Class.class
 */

public class TestFileClassifierLease implements DiscoveryListener {

    public static void main(String argv[]) {
	new TestFileClassifierLease();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(20*60*1000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
	System.out.println("Exiting normally");
    }

    public TestFileClassifierLease() {
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
	Class [] classes = new Class[] {LeaseFileClassifier.class};
	LeaseFileClassifier classifier = null;
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Service found");
            ServiceRegistrar registrar = registrars[n];
	    try {
		classifier = (LeaseFileClassifier) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		System.exit(2);
	    }
	    if (classifier == null) {
		System.out.println("Classifier null");
		continue;
	    }
	    MIMEType type;
	    try {
		type = classifier.getMIMEType("file1.txt");
		System.out.println("Type of known type file1.txt is " + type.toString());

		type = classifier.getMIMEType("file1.ps");
		System.out.println("Type of unknown type file1.ps is " + type);

		// Add a type
		Lease lease = classifier.addType("ps", new MIMEType("text", "postscript"));
		if (lease != null) {
		    System.out.println("Added type for ps");
		    System.out.println("lease for " + (lease.getExpiration() -
						       System.currentTimeMillis())/1000 +
				       " seconds");
		    type = classifier.getMIMEType("file1.ps");
		    System.out.println("Type for now known type file1.ps is " + type.toString());

		    // sleep for 1 min and try again
		    System.out.println("Sleeping for 1 min");
		    Thread.sleep(1*60*1000L);

		    type = classifier.getMIMEType("file1.ps");
		    System.out.println("Type for still known type file1.ps is " + type.toString());

		    // renew lease
		    lease.renew(3*60*1000L);
		    System.out.println("renewed lease for " + (lease.getExpiration() -
							       System.currentTimeMillis())/1000 +
				       " seconds");

		    // let lease lapse
		    System.out.println("Sleeping for 4 min to let lease lapse");
		    Thread.sleep(4*60*1000L);

		    type = classifier.getMIMEType("file1.ps");
		    System.out.println("Type for now unknown type file1.ps is " + type);
		} else {
		    System.err.println("was null");
		}
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    // System.exit(0);
	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }
} // TestFileClassifierLease
