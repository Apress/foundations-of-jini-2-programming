package config;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceID ;
import net.jini.lease.LeaseListener;             
import net.jini.lease.LeaseRenewalEvent;         
import net.jini.lease.LeaseRenewalManager;       
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.lookup.JoinManager;
import net.jini.id.UuidFactory;
import net.jini.id.Uuid;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;

import rmi.RemoteFileClassifier;
import rmi.FileClassifierImpl;

import java.io.*;

/**
 * FileClassifierServerConfig.java
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierServerConfig implements  LeaseListener {
    
    private LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    private ServiceID serviceID = null;
    private RemoteFileClassifier impl;
    private File serviceIdFile;
    private Configuration config;

    public static void main(String args[]) {
	FileClassifierServerConfig s = new FileClassifierServerConfig(args);
	
        // keep server running forever to 
	// - allow time for locator discovery and
	// - keep re-registering the lease
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FileClassifierServerConfig(String[] args) {
        System.setSecurityManager(new RMISecurityManager());

	try {
	    config = ConfigurationProvider.getInstance(args); 
	} catch(ConfigurationException e) {
	    System.err.println("Configuration error: " + e.toString());
	    System.exit(1);
	}

	Exporter exporter = null;
	try {
	    exporter = (Exporter) 
		config.getEntry( "config.FileClassifierServerConfig", 
				 "exporter", 
				 Exporter.class); 
	} catch(ConfigurationException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// Create the service and its proxy
	try {
	    impl = new rmi.FileClassifierImpl();
	} catch(RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	Remote proxy = null;
	try {
	    proxy = exporter.export(impl);
	} catch(ExportException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// register proxy with lookup services
	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null,  // unicast locators
					   null); // DiscoveryListener
	    joinMgr = new JoinManager(proxy, // service proxy
				      null,  // attr sets
				      serviceID,
				      mgr,   // DiscoveryManager
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    void getServiceID() {
	// Make up our own
	Uuid id = UuidFactory.generate();
	serviceID = new ServiceID(id.getMostSignificantBits(),
				  id.getLeastSignificantBits());
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }   
    
} // FileClassifierServer
