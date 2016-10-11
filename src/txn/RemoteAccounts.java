
package txn;

/**
 * RemoteAccounts.java
 *
 *
 * Created: Tue Aug 10 14:45:08 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

import common.Accounts;
import java.rmi.Remote;

public interface RemoteAccounts extends Accounts, Remote {
    
} // RemoteAccounts
