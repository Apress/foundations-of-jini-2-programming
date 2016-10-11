
package activation;

// import rmi.RemoteFileClassifier;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.lease.LeaseRenewalService;
import net.jini.lease.LeaseRenewalSet;

import java.rmi.Remote;
import java.rmi.RMISecurityManager;
import java.rmi.MarshalledObject;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationSystem;

import java.rmi.MarshalledObject;

import java.util.Properties;
import java.util.Vector;

import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.ActivationException;
import java.rmi.RemoteException;

/**
 * FileClassifierServerLease.java
 *
 *
 * Created: Wed Dec 22 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 2.0
 *   converted to Jini 2.0
 */

public class FileClassifierServerLease
    implements DiscoveryListener {

    static final protected String SECURITY_POLICY_FILE = 
	"/home/httpd/html/java/jini/tutorial/policy.all";
    static final protected String CODEBASE = 
	"http://192.168.1.13/classes/activation.FileClassifierServerLease-dl.jar";;
    
    protected Remote stub;

    protected RemoteEventListener leaseStub;

    // Lease renewal management
    protected LeaseRenewalSet leaseRenewalSet = null;

    // List of leases not yet managed by a LeaseRenewalService
    protected Vector leases = new Vector();

    public static void main(String argv[]) {
	new FileClassifierServerLease(argv);
	// stick around while lookup services are found
	try {
	    Thread.sleep(100000L);
	} catch(InterruptedException e) {
	    // do nothing
	}
	// the server doesn't need to exist anymore
	System.exit(0);
    }

    public FileClassifierServerLease(String[] argv) {
	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	ActivationSystem actSys = null;
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
			    "/home/httpd/html/java/jini/tutorial/dist/activation.FileClassifierServerLease-act.jar:/usr/local/jini2_0/lib/phoenix-init.jar:/usr/local/jini2_0/lib/jini-ext.jar"};
	CommandEnvironment commEnv =
	    new CommandEnvironment(null, options);
	System.out.println("1");
	ActivationGroupDesc group = new ActivationGroupDesc(props, commEnv);
	System.out.println("2");
	ActivationGroupID groupID = null;
	try {
	    groupID = actSys.registerGroup(group);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch(ActivationException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	String codebase = CODEBASE;	
	MarshalledObject data = null;
	ActivationDesc serviceDesc = 
	    new ActivationDesc(groupID,
			       "activation.FileClassifierImpl",
			       codebase, data, true);
	ActivationDesc leaseDesc  = 
	    new ActivationDesc(groupID, 
			       "activation.RenewLease",
			       codebase, data, true);

	// Get an activation ID for both service and listener
	ActivationID serviceActivationID = null;
	ActivationID leaseActivationID = null;
	try {
	    serviceActivationID = actSys.registerObject(serviceDesc);
	    leaseActivationID = actSys.registerObject(leaseDesc);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch(ActivationException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// Activate both service and listener and get their proxies
	try {
	System.out.println("3");
	    stub = (Remote) serviceActivationID.activate(true);
	    leaseStub = (RemoteEventListener) leaseActivationID.activate(true);
	System.out.println("4 " + stub);
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
	System.out.println("Registrar discovered");
        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

	    // export the proxy service
	    ServiceItem item = new ServiceItem(null,
					       stub,
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

	    Lease lease = reg.getLease();
	    // if we have a lease renewal manager, use it
	    if (leaseRenewalSet != null) {
		try {
		    leaseRenewalSet.renewFor(lease, Lease.FOREVER);
		} catch(RemoteException e) {
		    e.printStackTrace();
		}
	    } else {
		// add to the list of unmanaged leases
		leases.add(lease);
		// see if this lookup service has a lease renewal manager
		findLeaseService(registrar);
	    }
	}
    }

    public void findLeaseService(ServiceRegistrar registrar) {
	System.out.println("Trying to find a lease service");
	Class[] classes = {LeaseRenewalService.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                   null);
	LeaseRenewalService leaseService = null;
	try {
	    leaseService = (LeaseRenewalService) registrar.lookup(template);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    return;
	}
	if (leaseService == null) {
	    System.out.println("No lease service found");
	    return;
	}
	try {
	    // This time is unrealistically small - try 10000000L
	    leaseRenewalSet = leaseService.createLeaseRenewalSet(20000);
	    System.out.println("Found a lease service");
	    // register a timeout listener
	    leaseRenewalSet.setExpirationWarningListener(leaseStub, 5000,
	    					 null);
	    // manage all the leases found so far
	    for (int n = 0; n < leases.size(); n++) {
		Lease ll = (Lease) leases.elementAt(n);
		leaseRenewalSet.renewFor(ll, Lease.FOREVER);
	    }
	    leases = null;
	} catch(RemoteException e) {
	    e.printStackTrace();
	}
	Lease renewalLease = leaseRenewalSet.getRenewalSetLease();
	System.out.println("Lease expires in " +
			   (renewalLease.getExpiration() -
			    System.currentTimeMillis()));
    }

    public void discarded(DiscoveryEvent evt) {

    }
} // FileClassifierServerLease



    
