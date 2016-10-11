package security;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.PrivilegedActionException;

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

import java.util.logging.*;

import java.io.*;

/**
 * FileClassifierServerAuth
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierServerAuth implements  LeaseListener {
    
    private LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    private ServiceID serviceID = null;
    private RemoteFileClassifier impl;
    private File serviceIdFile;
    private Configuration config;

    static final String TRUST_LOG = "net.jini.security.trust";
    static final String INTEGRITY_LOG = "net.jini.security.integrity";
    static final String POLICY_LOG = "net.jini.security.policy";
    static final Logger trustLogger = Logger.getLogger(TRUST_LOG);
    static final Logger integrityLogger = Logger.getLogger(INTEGRITY_LOG);
    static final Logger policyLogger = Logger.getLogger(POLICY_LOG);
    private static FileHandler trustFh;
    private static FileHandler integrityFh;
    private static FileHandler policyFh;


    private static FileClassifierServerAuth server;

    static final String DISCOVERY_LOG = "net.jini.security.trust";
    static final Logger logger = Logger.getLogger(DISCOVERY_LOG);
    private static FileHandler fh;

    public static void main(String args[]) {
	/*
	try {
	    // this handler will save ALL log messages in the file
	    fh = new FileHandler("mylog.svr.txt");
	    // the format is simple rather than XML
	    fh.setFormatter(new SimpleFormatter());
	    logger.addHandler(fh);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	*/
	installLoggers();

	init(args);

	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    private static void init(final String[] args) {
	try {
	    LoginContext loginContext = 
		new LoginContext("security.FileClassifierServerAuth");
	    if (loginContext == null) {
		System.out.println("No login context");
		server = new FileClassifierServerAuth(args);
	    } else {
		loginContext.login();
		System.out.println("Login succeeded as " + 
				   loginContext.getSubject().toString());
		Subject.doAsPrivileged(
				       loginContext.getSubject(),
				       new PrivilegedExceptionAction() {
					   public Object run() throws Exception {
					       server = new FileClassifierServerAuth(args);
					       return null;
					   }
				       },
				       null);
	    }
	} catch(LoginException e) {
	    e.printStackTrace();
	    System.exit(3);
	} catch(PrivilegedActionException e) {
	    e.printStackTrace();
	    System.exit(3);
	}
    }

    public FileClassifierServerAuth(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
	Exporter exporter = null;
	String serviceName = null;
	try {
	    config = ConfigurationProvider.getInstance(args); 

	    exporter = (Exporter) 
		config.getEntry( "security.FileClassifierServer", 
				 "exporter", 
				 Exporter.class);
	    serviceName = (String)
		config.getEntry( "security.FileClassifierServer", 
				 "serviceName", 
				 String.class);
	} catch(ConfigurationException e) {
	    System.err.println("Configuration error: " + e.toString());
	    System.exit(1);
	}

	// Create the service and its proxy
	try {
	    // impl = new security.FileClassifierImpl();
	    impl = (RemoteFileClassifier) Class.forName(serviceName).newInstance();
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	Remote proxy = null;
	try {
	    proxy = exporter.export(impl);
	    System.out.println("Proxy is " + proxy.toString());
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

    private static void installLoggers() {
	try {
	    // this handler will save ALL log messages in the file
	    trustFh = new FileHandler("log.server.trust.txt");
	    integrityFh = new FileHandler("log.server.integrity.txt");
	    policyFh = new FileHandler("log.server.policy.txt");

	    // the format is simple rather than XML
	    trustFh.setFormatter(new SimpleFormatter());
	    integrityFh.setFormatter(new SimpleFormatter());
	    policyFh.setFormatter(new SimpleFormatter());

	    trustLogger.addHandler(trustFh);
	    integrityLogger.addHandler(integrityFh);
	    policyLogger.addHandler(policyFh);

	    trustLogger.setLevel(java.util.logging.Level.ALL);
	    integrityLogger.setLevel(java.util.logging.Level.ALL);
	    policyLogger.setLevel(java.util.logging.Level.ALL);
	} catch(Exception e) {
	    e.printStackTrace();
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
    
} // FileClassifierServerAuth
