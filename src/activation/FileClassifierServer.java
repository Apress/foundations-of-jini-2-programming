
package activation;

//import rmi.RemoteFileClassifier;

import java.rmi.Remote;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import java.rmi.RMISecurityManager;
import java.rmi.MarshalledObject;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationSystem;
import java.rmi.activation.ActivationID;

import java.util.Properties;

import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.ActivationException;
import java.rmi.RemoteException;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Dec 22 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierServer implements DiscoveryListener {

    static final protected String SECURITY_POLICY_FILE = 
	"/home/httpd/html/java/jini/tutorial/policy.all";

    static final protected String CODEBASE = 
	"http://192.168.1.13/classes/activation.FileClassifierServer-dl.jar";
    
    // protected FileClassifierImpl impl;
    protected Remote stub;
    
    public static void main(String argv[]) {
	new FileClassifierServer(argv);
	// stick around while lookup services are found
	try {
	    Thread.sleep(100000L);
	} catch(InterruptedException e) {
	    // do nothing
	}
	// the server doesn't need to exist anymore
	System.exit(0);
    }

    public FileClassifierServer(String[] argv) {
	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	// new
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
	// props.put("java.rmi.server.codebase", 
	//  "http://192.168.1.13/classes/activation.FileClassifierServer-dl.jar");
	String[] options = {"-classpath", 
			    "/home/httpd/html/java/jini/tutorial/dist/activation.FileClassifierServer-act.jar:/usr/local/jini2_0/lib/phoenix-init.jar:/usr/local/jini2_0/lib/jini-ext.jar"};
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
	ActivationDesc desc = null;
	desc = new ActivationDesc(groupID,
				  "activation.FileClassifierImpl",
				  codebase, data, true);

	// new
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
	}
    }

    public void discarded(DiscoveryEvent evt) {

    }
} // FileClassifierServer
