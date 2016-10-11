

/**
 * FileClassifierJiniService.java
 *
 *
 * Created: Mar 22, 2006
 *
 * @author Jan Newmarch
 * @version 1.0
 */

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import java.rmi.RemoteException;

public class FileClassifierJiniService {

    private final static long WAITFOR = 10000;

    public String getMIMEType(String fileName)  throws RemoteException {
	
	ServiceDiscoveryManager clientMgr = null;

	// set a security policy file here since we don't have command line access
	System.setProperty("java.security.policy", 
			  "/home/httpd/html/java/jini/tutorial/policy.all");

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null, // unicast locators
                                           null); // DiscoveryListener
	    clientMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
	    throw new RemoteException("Lookup failed", e);
	}
	
  
	Class [] classes = new Class[] {FileClassifier.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);

	ServiceItem item = null;
	// Try to find the service, blocking till timeout if necessary
	try {
	    item = clientMgr.lookup(template, 
				    null, // no filter 
				    WAITFOR); // timeout
	} catch(Exception e) {
            throw new RemoteException("Discovery failed", e);
	}
	if (item == null) {
	    // couldn't find a service in time
	    return "";
	}

	// Get the service
	FileClassifier classifier = (FileClassifier) item.service;

	if (classifier == null) {
	    throw new RemoteException("Classifier null");
	}

	// Now we have a suitable service, use it
	MIMEType type;
	try {
	    type = classifier.getMIMEType(fileName);
	    return type.toString();
	} catch(java.rmi.RemoteException e) {
	    throw e;
	}
    }

    public FileClassifierJiniService() {
	// empty
    }
    
} // FileClassifierJiniService
