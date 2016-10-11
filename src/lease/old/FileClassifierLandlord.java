
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

import com.sun.jini.lease.landlord.*;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.Lease; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
import java.util.Map;

public class FileClassifierLandlord extends UnicastRemoteObject implements Landlord, Remote {

    FileClassifierLeaseManager manager = null;

    public FileClassifierLandlord() throws java.rmi.RemoteException {
	manager = new FileClassifierLeaseManager(this);
    }
    
    public void cancel(Object cookie) {
	manager.cancel(cookie);
    }

    public Map cancelAll(Object[] cookies) {
	return manager.cancelAll(cookies);
    }

    public long renew(java.lang.Object cookie,
		      long extension) 
	throws net.jini.core.lease.LeaseDeniedException,
	       net.jini.core.lease.UnknownLeaseException {
	LeasedResource resource = manager.getResource(cookie);
	if (resource != null) {
	    return manager.getPolicy().renew(resource, extension);
	}
	return -1;
    }

    public Lease newFileClassifierLease(LeaseFileClassifier fileClassifier, 
					String suffixKey, long duration) 
	throws LeaseDeniedException {
	FileClassifierLeasedResource r = new FileClassifierLeasedResource(fileClassifier,
									  suffixKey);
	return manager.getPolicy().leaseFor(r, duration);
    }

    public Landlord.RenewResults renewAll(java.lang.Object[] cookie,
					  long[] extension) {
	return null;
    }
} // FileClassifierLandlord
