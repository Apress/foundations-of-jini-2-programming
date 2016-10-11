
package common;

import java.io.Serializable;
import net.jini.core.transaction.server.TransactionManager;

/**
 * Payable.java
 *
 *
 * Created: Tue Aug  3 13:58:01 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public interface Payable extends Serializable {
    
    void credit(long amount, long accountID,
		TransactionManager mgr, 
		long transactionID)
	throws java.rmi.RemoteException;

    long getCost() throws java.rmi.RemoteException;
} // Payable




