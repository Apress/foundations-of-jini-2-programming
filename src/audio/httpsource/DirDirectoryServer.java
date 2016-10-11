
/**
 * DirDirectoryServer.java
 *
 *
 * Created: Sun Jun 29 10:25:18 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.httpsource;

import common.*;
import java.io.*;
import java.rmi.RMISecurityManager;
import java.util.Vector;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.discovery.LookupDiscovery;

/**
 * Read a (file) directory for a list of .cfg files
 * each of which should list the specs for a directory 
 * of services, and publish each directory of services
 */
  
public class DirDirectoryServer {
    
    private Vector dirs = new Vector();

    public DirDirectoryServer(String dirStr) {
	LeaseRenewalManager leaseRenewalManager = new LeaseRenewalManager();
	LookupDiscoveryManager lookupDiscoveryManager = null;
	try {
	    lookupDiscoveryManager =
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   //new LookupLocator[] {
					   //    new LookupLocator("jini://jannote.jan.home/")},
					   null,	// unicast locators
					   null);  // DiscoveryListener
	} catch(IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	
	File dir = new File(dirStr);
	String[] files = dir.list(new java.io.FilenameFilter() {
		public boolean accept(File d, String name) {
		    System.out.println("Checking file " + name);
		    if (name.endsWith(".cfg"))
			return true;
		    else
			return false;
		}
	    });

	System.out.println("Dir length: " + files.length);
	for (int n = 0; n  < files.length; n++) {
	    dirs.add(
		     new DirectoryServerOpt(new String[] {dirStr + "/" + files[n]},
					    leaseRenewalManager,
					    lookupDiscoveryManager)
		     );
	}
    }

    public static void main(String[] argv) {

	System.setSecurityManager(new RMISecurityManager());

	DirDirectoryServer dd = new DirDirectoryServer(argv[0]);

       // stay around forever
        Object keepAlive = new Object();
        synchronized(keepAlive) {
            try {
                keepAlive.wait();
            } catch(InterruptedException e) {
                // do nothing
            }
        }
    }
}// DirDirectoryServer
