
package client;

import common.MutableFileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import java.rmi.*;
import java.rmi.server.ExportException;

import net.jini.export.Exporter; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

/**
 * TestFileClassifierEvent.java
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

public class TestFileClassifierEvent implements DiscoveryListener, RemoteEventListener {

    public static void main(String argv[]) {
	TestFileClassifierEvent client = new TestFileClassifierEvent();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestFileClassifierEvent() {
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
	Class [] classes = new Class[] {MutableFileClassifier.class};
	MutableFileClassifier classifier = null;
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Lookup service found");
            ServiceRegistrar registrar = registrars[n];
	    try {
		classifier = (MutableFileClassifier) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		continue;
	    }
	    if (classifier == null) {
		System.out.println("Classifier null");
		continue;
	    }

	    // Add ourselves as an event listener
	    Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						  new BasicILFactory());

	    // export an object of this class
	    RemoteEventListener proxy = null;
	    try {
		proxy =  (RemoteEventListener) exporter.export(this); 
	    } catch (ExportException e) {
		e.printStackTrace();
		continue;
	    }

	    try {
		classifier.addRemoteListener(proxy);
	    } catch (RemoteException e) {
		e.printStackTrace();
		continue;
	    }
	    
	    // Add some types to the service to generate events
	    try {
		classifier.addType("ps", new MIMEType("text", "postscript"));
		classifier.removeType("ps");
	    } catch(java.rmi.RemoteException e) {
		System.err.println(e.toString());
		continue;
	    }
	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }

    public void notify(RemoteEvent evt) {
	System.out.println("Event of type " + evt.getID());
    }
} // TestFileClassifier
