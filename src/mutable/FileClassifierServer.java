package mutable;

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

/**
 * FileClassifierServer.java
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

public class FileClassifierServer 
    implements ServiceIDListener {

    // explicit proxy for Jini 2.0
    protected Remote proxy;
    protected FileClassifierImpl impl;
    private static String CONFIG_FILE = "jeri/file_classifier_server.config";
    
    public static void main(String argv[]) {
	FileClassifierServer server = new FileClassifierServer();

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

    public FileClassifierServer() {
	String[] configArgs = new String[] {CONFIG_FILE};

	try {
	    impl = new FileClassifierImpl(configArgs);


	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	proxy = (Remote) impl.getProxy();

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

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

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // FileClassifierServer
