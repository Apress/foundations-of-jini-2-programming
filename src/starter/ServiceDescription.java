package starter;

import java.rmi.RMISecurityManager;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

import com.sun.jini.start.ServiceDescriptor;
import com.sun.jini.start.NonActivatableServiceDescriptor;
import com.sun.jini.start.NonActivatableServiceDescriptor.Created;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.ServiceIDListener;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.discovery.LookupDiscovery;

import java.rmi.Remote;

import java.io.*;

/**
 * ServiceDescription.java
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class ServiceDescription implements ServiceIDListener {
    
    private Object impl;
    private Remote proxy;
    private File serviceIdFile;
    private Configuration config;
    private ServiceID serviceID;

    public static void main(String args[]) {
	if (System.getSecurityManager() == null) {
	    System.setSecurityManager(new RMISecurityManager());
	}

	ServiceDescription s = 
	    new ServiceDescription(args);
	
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

    public ServiceDescription(String[] args) {
        if (args.length == 0) {
            System.err.println("No configuration specified");
            System.exit(1);
        }

	try {
	    config = ConfigurationProvider.getInstance(args); 
	} catch(ConfigurationException e) {
	    System.err.println("Configuration error: " + e.toString() +
			       " in file " + args[0]);
	    System.exit(1);
	}
	startService();
	advertiseService();
    }

    private void startService() {
	String codebase = null;
	String policy = null;
	String classpath = null;
	String implClass = null;
	String[] serverConfigArgs = null;

	try {

	    codebase = (String) config.getEntry("ServiceDescription", 
						"codebase", 
						String.class); 
	    policy = (String) config.getEntry("ServiceDescription", 
					      "policy", 
					      String.class); 
	    classpath = (String) config.getEntry("ServiceDescription", 
						 "classpath", 
						 String.class); 
	    implClass = (String) config.getEntry("ServiceDescription", 
						 "implClass", 
						 String.class); 
	    serverConfigArgs = (String[]) config.getEntry("ServiceDescription", 
							  "serverConfigArgs", 
							  String[].class); 
	} catch(ConfigurationException e) {
	    System.err.println("Configuration error: " + e.toString());
	    System.exit(1);
	}

	// Create the new service descriptor
	ServiceDescriptor desc = 
	    new NonActivatableServiceDescriptor(codebase,
						policy,
						classpath,
						implClass,
						serverConfigArgs);

	// and create the service and its proxy
	Created created = null;
	try {
	    created = (Created) desc.create(config);
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	impl = created.impl;
	proxy = (Remote) created.proxy;
    }

    private void advertiseService() {
	Entry[] entries = null;
	LookupLocator[] unicastLocators = null;
	File serviceIdFile = null;
	String[] groups = null;

	// Now go on to register the proxy with lookup services, using
	// e.g. JoinManager.
	// This will need additional parameters: entries, unicast
	// locators, group and service ID
        try {
            unicastLocators = (LookupLocator[]) 
                config.getEntry("AdvertDescription", 
                                "unicastLocators", 
                                LookupLocator[].class,
                                null); // default
            
            entries = (Entry[]) 
                config.getEntry("AdvertDescription", 
                                "entries", 
                                Entry[].class,
                                null); // default
            groups = (String[]) 
                config.getEntry("AdvertDescription", 
                                "groups", 
                                String[].class,
                                LookupDiscovery.ALL_GROUPS); // default
            serviceIdFile = (File) 
                config.getEntry("AdvertDescription", 
                                "serviceIdFile", 
                                File.class,
                                null); // default 
        } catch(Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            System.exit(2);
        }
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

} // ServiceDescription
