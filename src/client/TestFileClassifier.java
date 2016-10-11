
package client;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

/**
 * TestFileClassifier.java
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

public class TestFileClassifier implements DiscoveryListener {

    public static void main(String argv[]) {
	new TestFileClassifier();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestFileClassifier() {
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
	Class [] classes = new Class[] {FileClassifier.class};
	FileClassifier classifier = null;
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Lookup service found");
            ServiceRegistrar registrar = registrars[n];
	    try {
		classifier = (FileClassifier) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		continue;
	    }
	    if (classifier == null) {
		System.out.println("Classifier null");
		continue;
	    }

	    // Use the service to classify a few file types
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
		continue;
	    }
	    // success
	    System.exit(0);
	}
    }

    private void printType(String fileName, MIMEType type) {
	System.out.print("Type of " + fileName + " is ");
	if (type == null) {
	    System.out.println("null");
	} else {
	    System.out.println(type.toString());
	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }
} // TestFileClassifier
