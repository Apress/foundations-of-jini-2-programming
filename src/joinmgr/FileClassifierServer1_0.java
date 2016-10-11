package joinmgr;

import complete.FileClassifierImpl;

import com.sun.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import com.sun.jini.lookup.ServiceIDListener;
import com.sun.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscovery;

/**
 * FileClassifierServer1_0.java
 *
 *
 * Created: Wed Mar 17 14:23:44 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    renamed since Jini 1.1 has new JoinManager
 */

public class FileClassifierServer1_0 implements ServiceIDListener {
    
    public static void main(String argv[]) {
	new FileClassifierServer1_0();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }

    }

    public FileClassifierServer1_0() {

	JoinManager joinMgr = null;
	try {
	    /* this is one way of doing it
	    joinMgr = new JoinManager(new FileClassifierImpl(),
				      null,
				      this,
				      new LeaseRenewalManager());
	    joinMgr.setGroups(null);
	    */
	    /* here is another */
	    joinMgr = new JoinManager(new FileClassifierImpl(),
				      null,
				      LookupDiscovery.ALL_GROUPS,
				      null,
				      this,
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // FileClassifierServer1_0
