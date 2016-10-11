
package basic;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import java.rmi.RMISecurityManager;

/**
 * UnicastRegistrar.java
 *
 *
 * Created: Fri Mar 12 22:34:53 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    renamed as UnicastRegister
 *    moved to package basic
 */

public class UnicastRegister  {
    
    static public void main(String argv[]) {
        new UnicastRegister();
    }
   
    public UnicastRegister() {
	LookupLocator lookup = null;
	ServiceRegistrar registrar = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            lookup = new LookupLocator("jini://localhost");
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
