package transitive;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;

import java.rmi.*; 
import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

public class FindHelloClient {

    private static final long WAITFOR = 100000L;

    public static void main(String[] args) throws Exception {
	FindHelloClient client = new FindHelloClient();

	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FindHelloClient() throws Exception {
	ServiceDiscoveryManager serviceDiscoveryMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null, // unicast locators
                                           null); // DiscoveryListener
	    serviceDiscoveryMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	Class [] classes = new Class[] {transitive.FindHello.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);

	ServiceItem item = null;
	// Try to find the service, blocking till timeout if necessary
	try {
	    item = serviceDiscoveryMgr.lookup(template, 
				    null, // no filter 
				    WAITFOR); // timeout
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	System.out.println("Found service");
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("... but it was null");
	    return;
	}

	FindHello findHello = (FindHello) item.service;
	if (findHello == null) {
	    System.out.println("Find hello is null");
	} else {
	    Hello hello = findHello.doFind();
	    if (hello == null)  {
		System.out.println("Hello is null");
	    } else {
		System.out.println("Hello says: " + hello.sayHello());
	    }
	}
    }
}
