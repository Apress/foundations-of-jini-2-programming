
package rmi;

import rmi.FileClassifierImpl;
import rmi.RemoteFileClassifier;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import java.rmi.RMISecurityManager;

import net.jini.config.*; 
import net.jini.export.*; 

/**
 * FileClassifierServerRMI.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    added LeaseRenewalManager
 *    moved sleep() from constructor to main()
 *    uses Jini 1.1 LeaseRenewalManager
 * @version 1.3
 *    modified for Jini 2.0
 * @version 1.4
 *    explicit keep alive since it might not be JRMP
 */

public class FileClassifierServerRMI implements DiscoveryListener, LeaseListener {

    protected FileClassifierImpl impl;
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();

    // explicit proxy for Jini 2.0
    protected RemoteFileClassifier proxy;
    private static String CONFIG_FILE = "resources/jeri/file_classifier_server.config";
    
    public static void main(String argv[]) {
	new FileClassifierServerRMI();

	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FileClassifierServerRMI() {
	try {
	    impl = new FileClassifierImpl();


	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	String[] configArgs = new String[] {CONFIG_FILE};

	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(configArgs); 
	    
	    // and use this to construct an exporter
	    Exporter exporter = (Exporter) config.getEntry( "FileClassifierServer", 
							    "exporter", 
							    Exporter.class); 
	    // export an object of this class
	    proxy = (RemoteFileClassifier) exporter.export(impl);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();
	RemoteFileClassifier service;

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

	    // export the proxy service - use the actual proxy in 2.0
	    ServiceItem item = new ServiceItem(null,
					       proxy,
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.print("Register exception: ");
		e.printStackTrace();
		// System.exit(2);
		continue;
	    }
	    try {
		System.out.println("service registered at " +
				   registrar.getLocator().getHost());
	    } catch(Exception e) {
	    }
	    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }

    public void notify(LeaseRenewalEvent evt) {
	System.out.println("Lease expired " + evt.toString());
    }
        
} // FileClassifierServerRMI
