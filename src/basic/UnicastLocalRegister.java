
package basic;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import java.rmi.RMISecurityManager;
import com.sun.jini.config.ConfigUtil;
import java.net.UnknownHostException;

/**
 * UnicastLocalRegistrar.java
 *
 * @author Jan Newmarch
 * @version 1.o
 */

public class UnicastLocalRegister  {
    
    static public void main(String argv[]) {
        new UnicastLocalRegister();
    }
   
    public UnicastLocalRegister() {
	LookupLocator lookup = null;
	ServiceRegistrar registrar = null;

	System.setSecurityManager(new RMISecurityManager());

	String jiniUrl = null;
	try {
	    // get the url for "jini://local-host-name"
	    jiniUrl = ConfigUtil.concat(new Object[] {"jini://",
					              ConfigUtil.getHostName()
					             }
					);
	    // or
	    // jiniUrl = "jini://" + InetAddress.getLocalHost();
	} catch(UnknownHostException e) {
	    System.err.println("Can't get local host name");
	    System.exit(1);
	}

        try {
            lookup = new LookupLocator(jiniUrl);
        } catch(java.net.MalformedURLException e) {
            System.err.println("Lookup failed: " + e.toString());
	    System.exit(1);
        }

	try {
	    registrar = lookup.getRegistrar();
	} catch (java.io.IOException e) {
            System.err.println("Registrar search failed: " + e.toString());
	    System.exit(1);
	} catch (java.lang.ClassNotFoundException e) {
            System.err.println("Registrar search failed: " + e.toString());
	    System.exit(1);
	}
	System.out.println("Registrar found");

	// the code takes separate routes from here for client or service
    }
   
} // UnicastRegister
