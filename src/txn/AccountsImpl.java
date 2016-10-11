
/**
 * AccountsImpl.java
 *
 *
 * Created: Thu Jul 29 17:36:35 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 */

package txn;

// import common.Accounts;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.core.transaction.server.TransactionConstants;
import java.util.Hashtable;

import net.jini.export.ProxyAccessor;

import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

// debug
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.discovery.LookupLocator;
// end debug

public class AccountsImpl
    implements RemoteAccounts, TransactionParticipant, ProxyAccessor {
    
    protected long crashCount = 0; // value??
    protected Hashtable accountBalances = new Hashtable();
    protected Hashtable pendingCreditDebit = new Hashtable();
    protected  TransactionParticipant proxy;

    public AccountsImpl() throws java.rmi.RemoteException {
	try {
	     Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						  new BasicILFactory());
	     proxy = (TransactionParticipant) exporter.export(this); 
	} catch (Exception e) {
	    
	}

    }

    public void creditDebit(long amount, long creditorID,
			    long debitorID, long transactionID,
			    TransactionManager mgr) {
	
	try {
	    System.out.println("Trying to join");
	    mgr.join(transactionID, proxy, crashCount);
	} catch(net.jini.core.transaction.UnknownTransactionException e) {
	    e.printStackTrace();
	} catch(java.rmi.RemoteException e) {
	    e.printStackTrace();
	} catch(net.jini.core.transaction.server.CrashCountException e) {
	    e.printStackTrace();
	} catch(net.jini.core.transaction.CannotJoinException e) {
	    e.printStackTrace();
	}
	System.out.println("joined");
	pendingCreditDebit.put(new TransactionPair(mgr, 
						   transactionID),
			       new CreditDebit(amount, creditorID,
					       debitorID));
    }

    public int prepare(TransactionManager mgr, long id) {
	System.out.println("Preparing...");
	return TransactionConstants.PREPARED;
    }

    public void commit(TransactionManager mgr, long id) {
	System.out.println("committing");

    }


    public void abort(TransactionManager mgr, long id) {
	System.out.println("aborting");
    }

    public int prepareAndCommit(TransactionManager mgr, long id) {
	int result = prepare(mgr, id);
	if (result == TransactionConstants.PREPARED) {
	    commit(mgr, id);
	    result = TransactionConstants.COMMITTED;
	}
	return result;
    }

    class CreditDebit {
	long amount;
	long creditorID;
	long debitorID;

	CreditDebit(long a, long c, long d) {
	    amount = a;
	    creditorID = c;
	    debitorID = d;
	}
    }

    class TransactionPair {

	TransactionPair(TransactionManager mgr, long id) {

	}
    }

    public Object getProxy() {
	return proxy;
    }
} // AccountsImpl






