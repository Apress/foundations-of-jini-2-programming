
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
 * TestFileClassifierThread.java
 *
 *
 * Created: Tue Apr 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    simplified Class.forName to Class.class
 */

public class TestFileClassifierThread implements DiscoveryListener {

    public static void main(String argv[]) {
	new TestFileClassifierThread();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(10000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestFileClassifierThread() {
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

	    Class[] classes = new Class[] {FileClassifier.class};
	    FileClassifier classifier = null;
	    ServiceTemplate template = new ServiceTemplate(null, classes, 
							   null);
	    
	    try {
		classifier = (FileClassifier) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		return;
	    }
	    if (classifier == null) {
		System.out.println("Classifier null");
		return;
	    }
	    MIMEType type;
	    try {
		type = classifier.getMIMEType("file1.txt");
		System.out.println("Type is " + type.toString());
	    } catch(java.rmi.RemoteException e) {
		System.err.println(e.toString());
	    }
	}
    }

} // TestFileClassifier

