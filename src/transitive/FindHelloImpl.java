package transitive;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.core.lookup.ServiceID;

import java.rmi.*; 
import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

public class FindHelloImpl implements  FindHello, ServiceIDListener {

    private static final long WAITFOR = 100000L;

    public static void main(String[] args) throws Exception {
	FindHello impl = new FindHelloImpl();

	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FindHelloImpl() throws Exception {
	System.setSecurityManager(new RMISecurityManager());

	Exporter exporter = new BasicJeriExporter(
				     TcpServerEndpoint.getInstance(0),
				     new BasicILFactory());
	FindHello proxy = (FindHello) exporter.export(this);
	
	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null,  // unicast locators
					   null); // DiscoveryListener
	    joinMgr = new JoinManager(proxy, // service proxy
				      null,  // attr sets
				      this,  // ServiceIDListener
				      mgr,   // DiscoveryManager
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	Hello hello = doFind();
	if (hello != null) {
	    System.out.println("Hello says " + hello.sayHello());
	} else {
	    System.out.println("Hello is null");
	}
    }

    public Hello doFind() {
	ServiceDiscoveryManager serviceDiscoveryMgr = null;

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
  
	Class [] classes = new Class[] {transitive.Hello.class};
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
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("Service item null");
	    return null;
	}

	Hello hello = (Hello) item.service;
	if (hello == null) {
	    return null;
	} else {
	    System.out.println("Service null");
	    return hello;
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }
}
