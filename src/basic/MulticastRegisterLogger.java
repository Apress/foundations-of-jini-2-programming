package basic;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import java.lang.reflect.*;

import java.util.logging.*;

/**
 * MulticastRegisterLogger.java
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class MulticastRegisterLogger implements DiscoveryListener {

    static final String DISCOVERY_LOG = "net.jini.discovery.LookupDiscovery";
    static final Logger logger = Logger.getLogger(DISCOVERY_LOG);
    private static FileHandler fh;
  
    static public void main(String argv[]) {
	new MulticastRegisterLogger();

	// stay around long enough to receive replies
	try {
	    Thread.currentThread().sleep(10000L);
	} catch(java.lang.InterruptedException e) {
	    // do nothing
	}
    }
      
    public MulticastRegisterLogger() {
	try {
	    // this handler will save ALL log messages in the file
	    fh = new FileHandler("mylog.txt");
	    // the format is simple rather than XML
	    fh.setFormatter(new SimpleFormatter());
	    logger.addHandler(fh);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	// this handler will write all INFO and 
	// above messages to the console
	logger.addHandler(new ConsoleHandler());

	System.setSecurityManager(new java.rmi.RMISecurityManager());
        LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
        }
        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
	    ServiceRegistrar registrar = registrars[n];

	    // the code takes separate routes from here for client or service
	    System.out.println("found a service locator");
  	}
    }

    public void discarded(DiscoveryEvent evt) {

    }
} // MulticastRegister

