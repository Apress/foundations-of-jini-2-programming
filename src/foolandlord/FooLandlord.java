
/**
 * FooLandlord.java
 *
 *
 * Created: Mon Jun 14 22:48:11 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 1.1
 *    uses LandlordUtil
 */

package foolandlord;

import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.Lease;

import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.export.*; 

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.Map;
import java.util.HashMap;
import net.jini.id.Uuid;

import com.sun.jini.landlord.Landlord;
import com.sun.jini.landlord.LeaseFactory;
import com.sun.jini.landlord.LeasedResource;
import com.sun.jini.landlord.FixedLeasePeriodPolicy;
import com.sun.jini.landlord.LeasePeriodPolicy;
import com.sun.jini.landlord.LeasePeriodPolicy.Result;
import com.sun.jini.landlord.Landlord.RenewResults;
import com.sun.jini.landlord.LandlordUtil;
import com.sun.jini.landlord.LocalLandlord;

import net.jini.id.UuidFactory;

public class FooLandlord implements Landlord, LocalLandlord {

    private static final long MAX_LEASE = Lease.FOREVER;
    private static final long DEFAULT_LEASE = 1000*60*5; // 5 minutes

    private Map leasedResourceMap = new HashMap();
    private LeasePeriodPolicy policy = new 
	FixedLeasePeriodPolicy(MAX_LEASE, DEFAULT_LEASE);
    private Uuid myUuid = UuidFactory.generate();
    private LeaseFactory factory; 

    public FooLandlord() throws java.rmi.RemoteException {
	Exporter exporter = new 
	    BasicJeriExporter(TcpServerEndpoint.getInstance(0),
			      new BasicILFactory());	
	Landlord proxy = (Landlord) exporter.export(this);
	factory = new LeaseFactory(proxy, myUuid); 
    }
    
    public void cancel(Uuid cookie) throws UnknownLeaseException {
	if (leasedResourceMap.remove(cookie) == null) {
	    throw new UnknownLeaseException();
	}
    }

    public Map cancelAll(Uuid[] cookies) {
	return LandlordUtil.cancelAll(this, cookies);
    }

    public long renew(Uuid cookie,
		      long extension) throws LeaseDeniedException,
					     UnknownLeaseException {
	LeasedResource resource = (LeasedResource) 
	    leasedResourceMap.get(cookie);
	LeasePeriodPolicy.Result result = null;
	if (resource != null) {
	    result = policy.renew(resource, extension);
	    resource.setExpiration(result.expiration);
	} else {
	    throw new UnknownLeaseException();
	}
	return result.duration;
    }

    public Landlord.RenewResults renewAll(Uuid[] cookies, long[] durations) {
	return LandlordUtil.renewAll(this, cookies, durations);
    }


    public LeasePeriodPolicy.Result grant(LeasedResource resource,
					  long requestedDuration)
	throws LeaseDeniedException {
	Uuid cookie = resource.getCookie();
	try {
	    leasedResourceMap.put(cookie, resource);
	} catch(Exception e) {
	    throw new LeaseDeniedException(e.toString());
	}
	return policy.grant(resource, requestedDuration);
    }

    public Lease newFooLease(Foo foo, long duration) 
	throws LeaseDeniedException {


	FooLeasedResource resource = new FooLeasedResource(foo);
	Uuid cookie = resource.getCookie();

	// find out how long we should grant the lease for
	LeasePeriodPolicy.Result result = grant(resource, duration);
	long expiration = result.expiration;
	resource.setExpiration(expiration);

	Lease lease = factory.newLease(cookie, expiration);
	return lease;
    }

    public static void main(String[] args) throws RemoteException,
						  LeaseDeniedException,
						  UnknownLeaseException {
	// simple test harness
	
	long DURATION = 2000; // 2 secs;
	
	FooLandlord landlord = new FooLandlord();

	Lease lease = landlord.newFooLease(new Foo(), DURATION);
	long duration = lease.getExpiration() - System.currentTimeMillis();
	System.out.println("Lease granted for " + duration + " msecs");
	try {
	    Thread.sleep(1000);
	} catch(InterruptedException e) {
	    // ignore
	}
	lease.renew(5000);
	duration = lease.getExpiration() - System.currentTimeMillis();
	System.out.println("Lease renewed for " + duration + " msecs");
	lease.cancel();
	System.out.println("Lease cancelled");
    }
} // FooLandlord
