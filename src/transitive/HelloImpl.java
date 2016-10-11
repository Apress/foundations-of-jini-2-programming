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

public class HelloImpl implements Hello, ServiceIDListener {

   public static void main(String[] args) throws Exception {
	Hello impl = new HelloImpl();

	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public HelloImpl() throws Exception {
	System.setSecurityManager(new RMISecurityManager());

	Exporter exporter = new BasicJeriExporter(
				     TcpServerEndpoint.getInstance(0),
				     new BasicILFactory());
	Hello proxy = (Hello) exporter.export(this);

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
    }

    public String sayHello() {
	return "Hello";
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }
}
