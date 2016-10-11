package audio.http;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceRegistrar;
import java.rmi.RemoteException;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;

import net.jini.config.*; 
import net.jini.export.*; 

import net.jini.core.entry.Entry;
import net.jini.lookup.entry.*;

/**
 * HttpSinkServer.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    converted to Jini 1.1
 * @version 1.2
 *    converted to Jini 2.0
 */

public class HttpSinkServer 
    implements ServiceIDListener {

    // explicit proxy for Jini 2.0
    protected Remote proxy;
    protected HttpSinkImpl impl;
    private static String CONFIG_FILE = "jeri/http_sink_server.config";
    
    public static void main(String argv[]) {
	new HttpSinkServer();

        // stay around forever
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(InterruptedException e) {
		// do nothing
	    }
	}
    }

    public HttpSinkServer() {
	try {
	    impl = new HttpSinkImpl();

	    Class[] ifaces = impl.getClass().getInterfaces();
	    int len =ifaces.length;
	    System.out.println("Impl Sink Ifaces: " + len);
	    for (int n = 0; n < len; n++)
		System.out.println(ifaces[n].toString());
	    


	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	String[] configArgs = new String[] {CONFIG_FILE};

	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(configArgs); 
	    
	    // and use this to construct an exporter
	    Exporter exporter = (Exporter) config.getEntry( "HttpSinkServer", 
							    "exporter", 
							    Exporter.class); 
	    // export an object of this class
	    proxy = exporter.export(impl);
	    impl.setProxy(proxy);

	    System.out.println("Proxy class: " + proxy.getClass().toString());
	    Class[] ifaces = proxy.getClass().getInterfaces();
	    int len =ifaces.length;
	    System.out.println("Proxy Sink Ifaces: " + len);
	    for (int n = 0; n < len; n++)
		System.out.println(ifaces[n].toString());
	    


	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null,  // unicast locators
					   null); // DiscoveryListener
	    joinMgr = new JoinManager(proxy, // service proxy
				      new Entry[] {new Name("Jan's laptop")},  // attr sets
				      this,  // ServiceIDListener
				      mgr,   // DiscoveryManager
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // HttpSinkServer
