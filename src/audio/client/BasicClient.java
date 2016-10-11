package audio.client;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;

import audio.common.Sink;
import audio.common.Source;

/**
 * BasicClient.java
 */

public class BasicClient {

    private static final long WAITFOR = 100000L;
    private ServiceDiscoveryManager clientMgr = null;

    public static void main(String argv[]) {
	new BasicClient();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public BasicClient() {
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

	// find a source and sink
	Sink sink = (Sink) getService(Sink.class);
	Source source = (Source) getService(Source.class);

	// tell them about each other
	try {
	    source.addSink(sink);
	    sink.addSource(source);
	} catch(Exception e) {
	    System.err.println("Error setting source or sink " + e);
	    e.printStackTrace();
	    System.exit(1);
	}

	// play the audio
	try {
	    System.out.println("Playing...");
	    source.play();
	    sink.record();
	} catch(Exception e) {
	    System.out.println("Error in playing " + e);
	    System.exit(1);
	}
    }

    private Object getService(Class cls) {
  
	Class [] classes = new Class[] {cls};
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
	    System.out.println("no service for class " + cls);
	    System.exit(1);
	}

	// Return the service
	return item.service;
    }
} // BasicClient

