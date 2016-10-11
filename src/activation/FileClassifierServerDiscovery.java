
package activation;

import rmi.RemoteFileClassifier;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryService;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.discovery.LookupDiscoveryRegistration;
import net.jini.discovery.LookupUnmarshalException;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;

import net.jini.lease.LeaseRenewalService;
import net.jini.lease.LeaseRenewalSet;
import net.jini.lease.LeaseRenewalManager;

import net.jini.lookup.ServiceDiscoveryManager;

import java.rmi.RMISecurityManager;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;
import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationSystem;
import java.rmi.RemoteException;
import java.rmi.Remote;

import java.util.Properties;
import java.util.Vector;



/**
 * FileClassifierServerDiscovery.java
 *
 *
 * Created: Wed Dec 22 1999
 *
 * @author Jan Newmarch
 * @version 2.0
 *   modified for Jini 2.0
 */

public class FileClassifierServerDiscovery
    /* implements DiscoveryListener */ {
    private static final long WAITFOR = 10000L;

    static final private String SECURITY_POLICY_FILE = 
	"/home/httpd/html/java/jini/tutorial/policy.all";
    
    static final private String CODEBASE = 
	"http://192.168.1.13/classes/activation.FileClassifierServerDiscovery-dl.jar";
    
    private RemoteFileClassifier serviceStub;

    private RemoteEventListener leaseStub,
	                          discoveryStub;

    // Services
    private LookupDiscoveryService discoveryService = null;
    private LeaseRenewalService leaseService = null;

    // Lease renewal management
    private LeaseRenewalSet leaseRenewalSet = null;

    // List of leases not yet managed by a LeaseRenewalService
    private Vector leases = new Vector();

    private ServiceDiscoveryManager serviceDiscoveryMgr = null;

    private ActivationGroupID groupID;
    private ActivationSystem actSys;

    public static void main(String argv[]) {
	new FileClassifierServerDiscovery();
	// stick around while lookup services are found
	try {
	    Thread.sleep(20000L);
	} catch(InterruptedException e) {
	    // do nothing
	}
	// the server doesn't need to exist anymore
	System.exit(0);
    }

    public FileClassifierServerDiscovery() {
	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	installActivationGroup();

	serviceStub = (RemoteFileClassifier) 
	              registerWithActivation("activation.FileClassifierImpl", null);
	
	leaseStub = (RemoteEventListener) 
	              registerWithActivation("activation.RenewLease", null);

	initServiceDiscoveryManager();

	findLeaseService();

	// the discovery change listener needs to know the service and the lease service
	Object[] discoveryInfo = {serviceStub, leaseRenewalSet};
	MarshalledObject discoveryData = null;
	try {
	    discoveryData = new MarshalledObject(discoveryInfo);
	} catch(java.io.IOException e) {
	    e.printStackTrace();
	}
	discoveryStub = (RemoteEventListener) 
	                 registerWithActivation("activation.DiscoveryChange",
						discoveryData);

	findDiscoveryService();

    }

    public void installActivationGroup() {

	try {
	    actSys = ActivationGroup.getSystem();
	} catch(ActivationException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	// Install an activation group
	Properties props = new Properties();
	props.put("java.security.policy",
	  SECURITY_POLICY_FILE);
	String[] options = {"-classpath", 
			    "/home/httpd/html/java/jini/tutorial/dist/activation.FileClassifierServerDiscovery-act.jar:/usr/local/jini2_0/lib/phoenix-init.jar:/usr/local/jini2_0/lib/jini-ext.jar"};
	CommandEnvironment commEnv =
	    new CommandEnvironment(null, options);
	System.out.println("1");
	ActivationGroupDesc group = new ActivationGroupDesc(props, commEnv);
	System.out.println("2");
	try {
	    groupID = actSys.registerGroup(group);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch(ActivationException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public Object registerWithActivation(String className, MarshalledObject data) {
	String codebase = CODEBASE;	
	Object stub = null;
	ActivationDesc desc = new ActivationDesc(groupID,
						 className,
						 codebase, data, true);

	ActivationID aid = null;
	try {
	    aid = actSys.registerObject(desc);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch(ActivationException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	try {
	System.out.println("3");
	    stub = (Remote) aid.activate(true);
	System.out.println("4 " + stub);
	    // stub = (RemoteFileClassifier) Activatable.register(desc);
	} catch(UnknownGroupException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch(ActivationException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	return stub;
    }

    public void initServiceDiscoveryManager() {
	LookupDiscoveryManager lookupDiscoveryMgr = null;
        try {
            lookupDiscoveryMgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null /* unicast locators */,
                                           null /* DiscoveryListener */);
            serviceDiscoveryMgr = new ServiceDiscoveryManager(lookupDiscoveryMgr, 
						new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void findLeaseService() {
	leaseService = (LeaseRenewalService) findService(LeaseRenewalService.class);
	if (leaseService == null) {
	    System.out.println("Lease service null");
	}
	try {
	    leaseRenewalSet = leaseService.createLeaseRenewalSet(20000);
	    leaseRenewalSet.setExpirationWarningListener(leaseStub, 5000,
                                                 null);
	} catch(RemoteException e) {
	    e.printStackTrace();
	}
    }

    public void findDiscoveryService() {
	System.out.println("Discovering discovery service");
	discoveryService = (LookupDiscoveryService) findService(LookupDiscoveryService.class);
	if (discoveryService == null) {
	    System.out.println("Discovery service null");
	}
	LookupDiscoveryRegistration registration = null;
	System.out.println("Registering discovery service with stub "
			   + discoveryStub);
	try {
	    registration =
		discoveryService.register(LookupDiscovery.ALL_GROUPS,
					  null, //new LookupLocator[] {},
					  discoveryStub,
					  null,
					  Lease.FOREVER);
	} catch(RemoteException e) {
	    e.printStackTrace();
	}
	// manage the lease for the lookup discovery service
	System.out.println("Leasing discovery service");
	try {
	    leaseRenewalSet.renewFor(registration.getLease(), Lease.FOREVER);
	} catch(RemoteException e) {
	    e.printStackTrace();
	}

	// register with the lookup services already found
	ServiceItem item = new ServiceItem(null, serviceStub, null);
	ServiceRegistrar[] registrars = null;
	try {
	    registrars = registration.getRegistrars();
	} catch(RemoteException e) {
	    e.printStackTrace();
	    return;
	} catch(LookupUnmarshalException e) {
	    e.printStackTrace();
	    return;
	}

	for (int n = 0; n < registrars.length; n++) {
	    ServiceRegistrar registrar = registrars[n];
	    ServiceRegistration reg = null;
	    try {
		reg = registrar.register(item, Lease.FOREVER);
		leaseRenewalSet.renewFor(reg.getLease(), Lease.FOREVER);
	    } catch(java.rmi.RemoteException e) {
		System.err.println("Register exception: " + e.toString());
	    }
	}
    }

    public Object findService(Class cls) {
        Class [] classes = new Class[] {cls};
        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);

        ServiceItem item = null;
        try {
            item = serviceDiscoveryMgr.lookup(template, 
					      null, /* no filter */ 
					      WAITFOR /* timeout */);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (item == null) {
            // couldn't find a service in time
	    System.out.println("No service found for " + cls.toString());
	    return null;
        }
        return item.service;
    }
} // FileClassifierServerDiscovery



    
