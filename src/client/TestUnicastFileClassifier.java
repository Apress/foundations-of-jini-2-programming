
package client;

import common.FileClassifier;
import common.MIMEType;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import java.rmi.RMISecurityManager;
import net.jini.core.lookup.ServiceTemplate;

/**
 * TestUnicastFileClassifier.java
 *
 *
 * Created: Wed Aug 04
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class TestUnicastFileClassifier {

    public static void main(String argv[]) {
	new TestUnicastFileClassifier();
    }

    public TestUnicastFileClassifier() {
	LookupLocator lookup = null;
	ServiceRegistrar registrar = null;
	FileClassifier classifier = null;

        try {
            // lookup = new LookupLocator("jini://www.all_about_files.com");
            lookup = new LookupLocator("jini://192.168.1.13");
        } catch(java.net.MalformedURLException e) {
            System.err.println("Lookup failed: " + e.toString());
	    System.exit(1);
        }

	System.setSecurityManager(new RMISecurityManager());

	try {
	    registrar = lookup.getRegistrar();
	} catch (java.io.IOException e) {
            System.err.println("Registrar search failed: " + e.toString());
	    System.exit(1);
	} catch (java.lang.ClassNotFoundException e) {
            System.err.println("Registrar search failed: " + e.toString());
	    System.exit(1);
	}

	Class[] classes = new Class[] {FileClassifier.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, null);
	try {
	    classifier = (FileClassifier) registrar.lookup(template);
	} catch(java.rmi.RemoteException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	if (classifier == null) {
	    System.out.println("Classifier null");
	    System.exit(2);
	}
	MIMEType type;
	try {
	    type = classifier.getMIMEType("file1.txt");
	    System.out.println("Type is " + type.toString());
	} catch(java.rmi.RemoteException e) {
	    System.err.println(e.toString());
	}
	System.exit(0);
    }
} // TestUnicastFileClassifier






