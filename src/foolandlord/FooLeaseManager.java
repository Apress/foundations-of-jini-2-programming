
/**
 * FooLeaseManager.java
 *
 *
 * Created: Mon Jun 14 19:14:45 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */
package foolandlord;

import java.util.*;
import net.jini.core.lease.Lease;
import com.sun.jini.lease.landlord.LeaseManager;
import com.sun.jini.lease.landlord.LeasedResource;
import com.sun.jini.lease.landlord.LeaseDurationPolicy;
import com.sun.jini.lease.landlord.Landlord;
import com.sun.jini.lease.landlord.LandlordLease;
import com.sun.jini.lease.landlord.LeasePolicy;
import java.util.Map;

public class FooLeaseManager implements LeaseManager {

    protected static long DEFAULT_TIME = 30*1000L;

    protected Vector fooResources = new Vector();
    protected LeaseDurationPolicy policy;

    public FooLeaseManager(Landlord landlord) {
	policy = new LeaseDurationPolicy(Lease.FOREVER,
					 DEFAULT_TIME,
					 landlord,
					 this,
					 new LandlordLease.Factory());
	new LeaseReaper().run();
    }
    
    public void register(LeasedResource r,long duration) {
        fooResources.add(r);
    }

    public void renewed(LeasedResource r, long duration, long olddur) {
	// no smarts in the scheduling, so do nothing
    }

    public Map cancelAll(Object[] cookies) {
	for (int n = cookies.length; --n >= 0; ) {
	    cancel(cookies[n]);
	}
	return null;
    }

    public void cancel(Object cookie) {
	for (int n = fooResources.size(); --n >= 0; ) {
	    FooLeasedResource r = (FooLeasedResource) fooResources.elementAt(n);
	    if (r.getCookie().equals(cookie)) {
		fooResources.removeElementAt(n);
	    }
	}
    }

    public LeasePolicy getPolicy() {
	return policy;
    }

    public LeasedResource getResource(Object cookie) {
	for (int n = fooResources.size(); --n >= 0; ) {
	    FooLeasedResource r = (FooLeasedResource) fooResources.elementAt(n);
	    if (r.getCookie().equals(cookie)) {
		return r;
	    }
	}
	return null;
    }

    class LeaseReaper extends Thread {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(DEFAULT_TIME) ;
                }
                catch (InterruptedException e) {
		}
                for (int n = fooResources.size()-1; n >= 0; n--) {
                    FooLeasedResource r = (FooLeasedResource) 
			                   fooResources.elementAt(n)
 ;
                    if (!policy.ensureCurrent(r)) {
                        System.out.println("Lease expired for cookie = " + 
					   r.getCookie()) ;
			fooResources.removeElementAt(n);
			// replace this landlord.cancel(r.getCookie()) ;
			
                    }
                }
            }
        }
    }

} // FooLeaseManager
