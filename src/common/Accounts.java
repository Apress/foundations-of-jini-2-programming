
/**
 * Accounts.java
 *
 *
 * Created: Thu Jul 29 17:33:14 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package common;

import net.jini.core.transaction.server.TransactionManager;

public interface Accounts  {
    
    void creditDebit(long amount, long creditorID,
		     long debitorID, long transactionID,
		     TransactionManager tm)
	throws java.rmi.RemoteException;
    
} // Accounts
