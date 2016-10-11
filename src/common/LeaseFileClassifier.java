
package common;

/**
 * LeaseFileClassifier.java
 *
 *
 * Created: Thu Jun 02 14:14:36 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

import net.jini.core.lease.Lease;

public interface LeaseFileClassifier {

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException;
    
    /*
     * Add the MIME type for the given suffix.
     * The suffix does not contain '.' e.g. "gif".
     * @exception net.jini.core.lease.LeaseDeniedException
     * a previous MIME type for that suffix exists.
     * This type is removed on expiration or cancellation
     * of the lease.
     */
    public Lease addType(String suffix, MIMEType type)
	throws java.rmi.RemoteException,  
	       net.jini.core.lease.LeaseDeniedException;

    /**
     * Remove the MIME type for the suffix.
     */
    public void removeType(String suffix)
	throws java.rmi.RemoteException;
} // LeaseFileClasssifier



