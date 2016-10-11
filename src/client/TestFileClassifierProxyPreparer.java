
package client;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

import java.rmi.RemoteException;

import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

import java.util.logging.*;

/**
 * TestFileClassifierProxyPreparer.java
 *
 *
 * Created: Aug 14 2004
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class TestFileClassifierProxyPreparer implements DiscoveryListener {

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


    public static void main(String argv[]) 
	throws ConfigurationException {

	installLoggers();

	new TestFileClassifierProxyPreparer(argv);

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestFileClassifierProxyPreparer(String[] argv) 
	throws ConfigurationException {
	config = ConfigurationProvider.getInstance(argv);

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

    private static void installLoggers() {
	try {
	    // this handler will save ALL log messages in the file
	    trustFh = new FileHandler("log.client.trust.txt");
	    integrityFh = new FileHandler("log.client.integrity.txt");
	    policyFh = new FileHandler("log.client.policy.txt");

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

    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();
	Class [] classes = new Class[] {FileClassifier.class};
	FileClassifier classifier = null;
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Lookup service found");
            ServiceRegistrar registrar = registrars[n];
	    try {
		classifier = (FileClassifier) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		System.exit(4);
		continue;
	    }
	    if (classifier == null) {
		System.out.println("Classifier null");
		continue;
	    }

	    System.out.println("Getting the proxy");
	    // Get the proxy preparer
	    ProxyPreparer preparer = null;
	    try {
		preparer = 
		(ProxyPreparer) config.getEntry(
						"client.TestFileClassifierProxyPreparer",
						"preparer", ProxyPreparer.class,
						new BasicProxyPreparer());
	    } catch(ConfigurationException e) {
		e.printStackTrace();
		preparer = new BasicProxyPreparer();
	    }

	    // Prepare the new proxy
	    System.out.println("Preparing the proxy");
	    try {
		classifier = (FileClassifier) preparer.prepareProxy(classifier);
	    } catch(RemoteException e) {
		e.printStackTrace();
		System.exit(3);
	    } catch(java.lang.SecurityException e) {
		e.printStackTrace();
		System.exit(6);
	    }

	    // Use the service to classify a few file types
	    System.out.println("Calling the proxy");
	    MIMEType type;
	    try {
		String fileName;

		fileName = "file1.txt";
		type = classifier.getMIMEType(fileName);
		printType(fileName, type);

		fileName = "file2.rtf";
		type = classifier.getMIMEType(fileName);
		printType(fileName, type);

		fileName = "file3.abc";
		type = classifier.getMIMEType(fileName);
		printType(fileName, type);
	    } catch(java.rmi.RemoteException e) {
		System.out.println("Failed to call method");
		System.err.println(e.toString());
		System.exit(5);
		continue;
	    }
	    // success
	    System.exit(0);
	}
    }

    private void printType(String fileName, MIMEType type) {
	System.out.print("Type of " + fileName + " is ");
	if (type == null) {
	    System.out.println("null");
	} else {
	    System.out.println(type.toString());
	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }
} // TestFileClassifier
