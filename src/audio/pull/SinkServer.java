package audio.pull;

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

import net.jini.id.UuidFactory;
import net.jini.id.Uuid;

import net.jini.core.entry.Entry;
import net.jini.lookup.entry.*;
import java.io.*;

/**
 * PullSinkServer.java
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

public class SinkServer {

    // explicit proxy for Jini 2.0
    protected Remote proxy;
    protected SinkImpl impl;
    private String sinkName = "No name";
    private ServiceID serviceID;

    public static void main(String argv[]) {
	new SinkServer(argv);

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

    public SinkServer(String[] argv) {
	File serviceIDFile = null;

	try {
	    impl = new SinkImpl();
	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	String[] configArgs = new String[] {argv[0]};

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

	    sinkName = (String) config.getEntry( "HttpSinkServer", 
						   "sinkName", 
						   String.class);

	    serviceIDFile = (File) config.getEntry("HttpSinkServer", 
						   "serviceIdFile", 
						   File.class); 
	    getOrMakeServiceID(serviceIDFile);
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
				      new Entry[] {new Name(sinkName)},  // attr sets
				      serviceID,  // ServiceID
				      mgr,   // DiscoveryManager
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    private void getOrMakeServiceID(File serviceIDFile) {
	try {
	    ObjectInputStream ois = 
		new ObjectInputStream(new FileInputStream(serviceIDFile));
	    serviceID = (ServiceID) ois.readObject();
	} catch(Exception e) {
	    System.out.println("Couldn't get service IDs - generating new ones");
	    try {
		ObjectOutputStream oos = 
		    new ObjectOutputStream(new FileOutputStream(serviceIDFile));

		Uuid uuid = UuidFactory.generate();
		serviceID = new ServiceID(uuid.getMostSignificantBits(), 
				      uuid.getLeastSignificantBits());
		oos.writeObject(serviceID);
	    } catch(Exception e2) {
		System.out.println("Couldn't save ids");
		e2.printStackTrace();
	    }
	}
    }
} // SinkServer
