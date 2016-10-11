
/**
 * FileClassifierLandlord.java
 *
 *
 * Created: Mon Jun 14 22:48:11 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    changed cancellAll for Jini 1.1 beta
 */

package lease;

import common.LeaseFileClassifier;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
 
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;

import com.sun.jini.landlord.Landlord;
import com.sun.jini.landlord.LeaseFactory;
import com.sun.jini.landlord.LeasedResource;
import com.sun.jini.landlord.FixedLeasePeriodPolicy;
import com.sun.jini.landlord.LeasePeriodPolicy;
import com.sun.jini.landlord.LeasePeriodPolicy.Result;
import com.sun.jini.landlord.Landlord.RenewResults;
import com.sun.jini.landlord.LandlordUtil;
import com.sun.jini.landlord.LocalLandlord;

import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.export.*; 

import java.rmi.Remote;

public class FileClassifierLandlord implements Landlord, LocalLandlord {

    private static final long MAX_LEASE = Lease.FOREVER;
    private static final long DEFAULT_LEASE = 1000*60*5; // 5 minutes

    private Map leasedResourceMap = new HashMap();
    private LeasePeriodPolicy policy = new 
	FixedLeasePeriodPolicy(MAX_LEASE, DEFAULT_LEASE);
    private Uuid myUuid = UuidFactory.generate();
    private LeaseFactory factory; 

    public FileClassifierLandlord() throws java.rmi.RemoteException {
	Exporter exporter = new 
	    BasicJeriExporter(TcpServerEndpoint.getInstance(0),
			      new BasicILFactory());	
	Landlord proxy = (Landlord) exporter.export(this);
	factory = new LeaseFactory(proxy, myUuid); 

	// start a reaper to clean up expired leases
	new Reaper(leasedResourceMap).start();
    }
    
    public void cancel(Uuid cookie) throws UnknownLeaseException {
	Object value;
	if ((value = leasedResourceMap.remove(cookie)) == null) {
	    throw new UnknownLeaseException();
	}
	FileClassifierLeasedResource resource = (FileClassifierLeasedResource) value;	     
	
	try {
	    resource.getFileClassifier().removeType(resource.getSuffix());
	} catch (RemoteException e) {
	    // ignore??
	}
    }

    public Map cancelAll(Uuid[] cookies) {
	return LandlordUtil.cancelAll(this, cookies);
    }

    public long renew(Uuid cookie,
		      long extension) 
	throws net.jini.core.lease.LeaseDeniedException,
	       net.jini.core.lease.UnknownLeaseException {
	LeasedResource resource = (LeasedResource) 
	    leasedResourceMap.get(cookie);
	LeasePeriodPolicy.Result result = null;
	if (resource != null) {
	    result = policy.renew(resource, extension);
	} else {
	    throw new UnknownLeaseException();
	}
	return result.duration;
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

    public Lease newFileClassifierLease(LeaseFileClassifier fileClassifier, 
					String suffixKey, long duration) 
	throws LeaseDeniedException {

	FileClassifierLeasedResource resource = new FileClassifierLeasedResource(fileClassifier,
										 suffixKey);

	Uuid cookie = resource.getCookie();

	// find out how long we should grant the lease for
	LeasePeriodPolicy.Result result = grant(resource, duration);
	long expiration = result.expiration;
	resource.setExpiration(expiration);

	Lease lease = factory.newLease(cookie, expiration);
	return lease;
    }

    public Landlord.RenewResults renewAll(Uuid[] cookies,
					  long[] durations) {
	return LandlordUtil.renewAll(this, cookies, durations);
    }
} // FileClassifierLandlord
