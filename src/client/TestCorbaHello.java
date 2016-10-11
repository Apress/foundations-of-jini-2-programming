
package client;

import corba.JavaHello;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;


/**
 * TestCorbaHello.java
 *
 *
 * Created: Tue Aug 24
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class TestCorbaHello implements DiscoveryListener {

    public static void main(String argv[]) {
	new TestCorbaHello();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(10000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestCorbaHello() {
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
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Service found");
            ServiceRegistrar registrar = registrars[n];

	    new LookupThread(registrar).start();
	}

	    // System.exit(0);
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }

    class LookupThread extends Thread {

	ServiceRegistrar registrar;

	LookupThread(ServiceRegistrar registrar) {
	    this.registrar = registrar;
	}


	public void run() {

	    Class[] classes = new Class[] {JavaHello.class};
	    JavaHello hello = null;
	    ServiceTemplate template = new ServiceTemplate(null, classes, 
							   null);
	    
	    try {
		hello = (JavaHello) registrar.lookup(template);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		System.exit(2);
	    }
	    if (hello == null) {
		System.out.println("hello null");
		return;
	    }
	    String msg;
	    try {
		msg = hello.sayHello();
		System.out.println(msg);
	    } catch(Exception e) {
		// we may get a CORBA runtime error
		System.err.println(e.toString());
	    }
	}
    }

} // TestCorbaHello

