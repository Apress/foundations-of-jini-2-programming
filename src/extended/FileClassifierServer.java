
package extended;

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
import java.rmi.Remote;

import net.jini.config.*; 
import net.jini.export.*; 

import rmi.RemoteFileClassifier;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Oct 25 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    uses Jini 1.1 LeaseRenewalManager
 * @version 1.2
 *    uses Jeri configuration stuff
 */

public class FileClassifierServer implements DiscoveryListener, LeaseListener {

    protected FileClassifierProxy smartProxy;
    protected Remote rmiProxy;
    protected ExtendedFileClassifierImpl impl;
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    private static String CONFIG_FILE = "jeri/file_classifier_server.config";
    
    public static void main(String argv[]) {
	new FileClassifierServer();
	// RMI keeps this alive
    }

    public FileClassifierServer() {
	try {
	    impl = new ExtendedFileClassifierImpl();
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
	    System.out.println("exporter found");
	    // export an object of this class
	    rmiProxy = (Remote) exporter.export(impl);
	    System.out.println("proxy exported");
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}


	// set RMI scurity manager
	System.setSecurityManager(new RMISecurityManager());

	// proxy primed with impl
	smartProxy = new FileClassifierProxy(rmiProxy);

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

        for (int n = 0; n < registrars.length; n++) {
System.out.println("found registrars");
            ServiceRegistrar registrar = registrars[n];

	    // export the proxy service
	    ServiceItem item = new ServiceItem(null,
					       smartProxy, 
					       null);
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.print("Register exception: ");
		e.printStackTrace();
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
        
} // FileClassifierServer
