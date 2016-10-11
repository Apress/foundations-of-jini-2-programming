
/**
 * HttpBasicClient.java
 *
 *
 * Created: Sat Jun 28 22:33:03 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package client;

import audio.common.*;
import audio.transport.*;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;

/**
 * obsolete?
 */

public class HttpBasicClient implements net.jini.discovery.DiscoveryListener{

    private static final long WAITFOR = 100000L;
    private ServiceDiscoveryManager clientMgr;

    public static void main(String argv[]) {
	new HttpBasicClient();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public HttpBasicClient (){
	clientMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null, // unicast locators
                                           this); // DiscoveryListener
	    clientMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	HttpSource source = getSource();
	System.out.println("Found source " + source.toString());
	HttpSink sink = getSink();
	System.out.println("Found sink " + sink.toString());

	try {
	    sink.addSource(source);
	    source.addSink(sink);
	    source.play();
	    sink.record();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Record/play over");
	System.exit(0);
    }

    private HttpSource getSource() {
	Class [] classes = new Class[] {HttpSource.class};
	// Class [] classes = new Class[] {Object.class};
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
	HttpSource source = (HttpSource) item.service;

	if (source == null) {
	    System.out.println("Source null");
	    System.exit(1);
	}
	return source;
    }

    private HttpSink getSink() {
	// Class [] classes = new Class[] {HttpSink.class};
	Class [] classes = new Class[] {Sink.class};
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
	HttpSink sink = (HttpSink) item.service;

	if (sink == null) {
	    System.out.println("Sink null");
	    System.exit(1);
	}
	return sink;
    }

    public void discovered(net.jini.discovery.DiscoveryEvent e) {
	System.out.println("LUS discovered" + e.toString());
    }


    public void discarded(net.jini.discovery.DiscoveryEvent e) {
	System.out.println("LUS discovered" + e.toString());
    }
}// HttpBasicClient
