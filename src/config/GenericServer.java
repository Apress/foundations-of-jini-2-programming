package config;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscoveryManager;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import net.jini.export.Exporter;
import net.jini.core.lookup.ServiceID;

import java.io.*;

import net.jini.config.*; 

/**
 * GenericServer.java
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class GenericServer 
    implements ServiceIDListener {

    private static final String SERVER = "GenericServer";

    private Remote proxy;
    private Remote impl;
    private Exporter exporter;
    private String[] groups;
    private Entry[] entries;
    private LookupLocator[] unicastLocators;
    private File serviceIdFile;
    private String codebase;
    private ServiceID serviceID;

    public static void main(String args[]) {
	new GenericServer(args);

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

    public GenericServer(String[] args) {

        if (args.length == 0) {
            System.err.println("No configuration specified");
            System.exit(1);
        }
        String[] configArgs = new String[] {args[0]};

	getConfiguration(configArgs);

	// set codebase
	System.setProperty("java.rmi.manager.codebase", codebase);

	// export a service object
	try {
	    proxy = exporter.export(impl);
	} catch(java.rmi.server.ExportException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());
	
	tryRetrieveServiceId();

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(groups,
					   unicastLocators,  // unicast locators
					   null); // DiscoveryListener
	    if (serviceID != null) {
		joinMgr = new JoinManager(proxy, // service proxy
					  entries,  // attr sets
					  serviceID,  // ServiceID
					  mgr,   // DiscoveryManager
					  new LeaseRenewalManager());
	    } else {
		joinMgr = new JoinManager(proxy, // service proxy
					  entries,  // attr sets
					  this,  // ServiceIDListener
					  mgr,   // DiscoveryManager
					  new LeaseRenewalManager());
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void tryRetrieveServiceId() {
	// Try to load the service ID from file.
	// It isn't an error if we can't load it, because
	// maybe this is the first time this service has run
	DataInputStream din = null;
	try {
	    din = new DataInputStream(new FileInputStream(serviceIdFile));
	    serviceID = new ServiceID(din);
	    System.out.println("Found service ID in file " + serviceIdFile);
	    din.close();
	} catch(Exception e) {
	    // ignore
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
	
	// try to save the service ID in a file
	if (serviceIdFile != null) {
	    DataOutputStream dout = null;
	    try {
		dout = new DataOutputStream(new FileOutputStream(serviceIdFile));
		serviceID.writeBytes(dout);
		dout.flush();
	    dout.close();
	    System.out.println("Service id saved in " +  serviceIdFile);
	    } catch(Exception e) {
		// ignore
	    } 
	}
    }

    private void getConfiguration(String[] configArgs) {
	Configuration config = null;

	// We have to get a configuration file or
	// we can't continue
	try {
	    config = ConfigurationProvider.getInstance(configArgs); 
	} catch(ConfigurationException e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}
	    
	// The config file must have an exporter, a service and a codebase
	try {
	    exporter = (Exporter) config.getEntry(SERVER, 
						  "exporter", 
						  Exporter.class); 
	    impl = (Remote) config.getEntry(SERVER, 
					    "service", 
					    Remote.class); 

	    codebase = (String) config.getEntry(SERVER,
						"codebase",
						String.class);
	} catch(NoSuchEntryException  e) {
	    System.err.println("No config entry for " + e);
	    System.exit(1);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(2);
	}

	// These fields can fallback to a default value 
	try {
	    unicastLocators = (LookupLocator[]) 
		config.getEntry("GenericServer", 
				"unicastLocators", 
				LookupLocator[].class,
				null); // default
	    
	    entries = (Entry[]) 
		config.getEntry("GenericServer", 
				"entries", 
				Entry[].class,
				null); // default
	    serviceIdFile = (File) 
		config.getEntry("GenericServer", 
				"serviceIdFile", 
				File.class,
				null); // default 
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(2);
	}

    }
    
} // GenericServer
