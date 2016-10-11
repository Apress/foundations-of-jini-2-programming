
package lease;

import java.rmi.*;

import net.jini.lease.LeaseRenewalManager;
import java.rmi.RMISecurityManager;
import net.jini.core.lookup.ServiceID;

import net.jini.lookup.ServiceIDListener;
import common.LeaseFileClassifier;

import net.jini.lookup.JoinManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;

import net.jini.export.Exporter; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.3
 *    added LeaseRenewalManager
 *    moved sleep() from constructor to main()
 *    uses Jini 1.1 LeaseRenewalManager
 */

public class FileClassifierServer implements ServiceIDListener  {


    protected FileClassifierImpl impl;

    
    public static void main(String argv[]) throws Exception {
	FileClassifierServer server = new FileClassifierServer();

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

    public FileClassifierServer() throws Exception {

	System.setSecurityManager(new RMISecurityManager());

	impl = new FileClassifierImpl();

	Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						  new BasicILFactory());

	// export an object of this class
	Remote proxy = exporter.export(impl); 

        JoinManager joinMgr = null;
        try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);

            joinMgr = new JoinManager(proxy,
                                      null,
                                      this,
				      mgr,
                                      new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }            


    }
    
    public void serviceIDNotify(ServiceID serviceID) {
        System.out.println("got service ID " + serviceID.toString());
    }
    
  
} // FileClassifierServer
