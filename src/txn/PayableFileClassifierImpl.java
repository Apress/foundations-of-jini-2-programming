
package txn;

import common.MIMEType;
import common.Accounts;
import rmi.FileClassifierImpl;

import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.discovery.LookupLocator;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import net.jini.export.ProxyAccessor;

import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

/**
 * PayableFileClassifierImpl.java
 *
 *
 * Created: Tue Aug  3 14:39:48 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 */

public class PayableFileClassifierImpl extends FileClassifierImpl 
    implements RemotePayableFileClassifier, TransactionParticipant, ProxyAccessor {

    protected TransactionManager mgr = null;
    protected Accounts accts = null;
    protected long crashCount = 0; // ???
    protected long cost = 10;
    protected final long myID = 54321;
    protected  TransactionParticipant proxy;

    public PayableFileClassifierImpl() throws java.rmi.RemoteException {
	super();

	System.setSecurityManager(new RMISecurityManager());

	try {
	     Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						  new BasicILFactory());
	     proxy = (TransactionParticipant) exporter.export(this); 
	} catch (Exception e) {
	    
	}
	
    }
    
    public void credit(long amount, long accountID,
		       TransactionManager mgr, 
		       long transactionID) {
	System.out.println("crediting");

	this.mgr = mgr;

	// before findAccounts
	System.out.println("Joining txn");
	try {
	    mgr.join(transactionID, proxy, crashCount);
	} catch(UnknownTransactionException e) {
	    e.printStackTrace();
	} catch(CannotJoinException e) {
	    e.printStackTrace();
	} catch(CrashCountException e) {
	    e.printStackTrace();
	} catch(RemoteException e) {
	    e.printStackTrace();
	}
	System.out.println("Joined txn");


	findAccounts();

	if (accts == null) {
	    try {
		mgr.abort(transactionID);
	    } catch(UnknownTransactionException e) {
		e.printStackTrace();
	    } catch(CannotAbortException e) {
		e.printStackTrace();
	    } catch(RemoteException e) {
		e.printStackTrace();
	    }
	}

	try {
	    accts.creditDebit(amount, accountID, myID,
			      transactionID, mgr);
	} catch(java.rmi.RemoteException e) {
	    e.printStackTrace();
	}


    }

    public long getCost() {
	return cost;
    }

    protected void findAccounts() {
	// find a known account service
        LookupLocator lookup = null;
        ServiceRegistrar registrar = null;

        try {
            lookup = new LookupLocator("jini://localhost");
        } catch(java.net.MalformedURLException e) {
            System.err.println("Lookup failed: " + e.toString());
            System.exit(1);
        }

        try {
            registrar = lookup.getRegistrar();
        } catch (java.io.IOException e) {
            System.err.println("Registrar search failed: " + e.toString());
            System.exit(1);
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("Registrar search failed: " + e.toString());
            System.exit(1);
        }
        System.out.println("Registrar found");

	Class[] classes = new Class[] {Accounts.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                   null);
	try {
	    accts = (Accounts) registrar.lookup(template);
	} catch(java.rmi.RemoteException e) {
	    System.exit(2);
	}
    }

    public MIMEType getMIMEType(String fileName) throws RemoteException {

	if (mgr == null) {
	    // don't process the request
	    return null;
	}

	return super.getMIMEType(fileName);
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

    public Object getProxy() {
	return proxy;
    }
} // PayableFileClassifierImpl
