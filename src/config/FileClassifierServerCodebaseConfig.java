package config;

import java.rmi.RMISecurityManager;
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

import java.io.*;

/**
 * FileClassifierServerConfig.java
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierServerCodebaseConfig implements DiscoveryListener, 
                                             LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    protected complete.FileClassifierImpl impl;
    protected File serviceIdFile;

    public static void main(String args[]) {
	FileClassifierServerCodebaseConfig s = new FileClassifierServerCodebaseConfig(args);
	
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

    public FileClassifierServerCodebaseConfig(String[] args) {
	// Create the service
	impl = new complete.FileClassifierImpl();

        if (args.length == 0) {
            System.err.println("No configuration specified");
            System.exit(1);
        }
        String[] configArgs = new String[] {args[0]};

        Configuration config = null;
	String codebase = null;
	try {
	    config = ConfigurationProvider.getInstance(configArgs); 
	    codebase = (String) config.getEntry("ServiceCodebaseDemo",
						"codebase",
						String.class);
	} catch(ConfigurationException e) {
	    System.err.println("Configuration error: " + e.toString());
	    System.exit(1);
	}
	System.setProperty("java.rmi.manager.codebase", codebase);

        System.setSecurityManager(new RMISecurityManager());

	LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println("Discovery failed " + e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

	    ServiceItem item = new ServiceItem(null,
					       impl, 
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.println("Register exception: " + e.toString());
		continue;
	    }
	    System.out.println("Service registered with id " + reg.getServiceID());

	    // set lease renewal in place
	    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }   
    
} // FileClassifierServer
