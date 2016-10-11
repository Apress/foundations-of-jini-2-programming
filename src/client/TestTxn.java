
package client;

import common.PayableFileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.core.transaction.server.TransactionParticipant;

import net.jini.lease.LeaseRenewalManager;
import net.jini.core.lease.Lease;
import net.jini.lookup.entry.Name;
import net.jini.core.entry.Entry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;

import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import java.rmi.server.ExportException;

/**
 * TestTxn.java
 *
 *
 * Created: Tue Aug 3 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    uses Jini 1.1 LeaseRenewalManager
 */

public class TestTxn implements TransactionParticipant {

    private static final long WAITFOR = 100000L;

    long crashCount = 0;

    PayableFileClassifier classifier = null;
    TransactionManager mgr = null;

    long myClientID; // my account id

    public static void main(String argv[]) {
	new TestTxn();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestTxn() {
	System.setSecurityManager(new RMISecurityManager());


	classifier = findClassifier();

	long cost = 0;
	try {
	    cost = classifier.getCost();
	} catch(java.rmi.RemoteException e) {
	    e.printStackTrace();
	}
	if (cost > 20) {
	    System.out.println("Costs too much: " + cost);
	    classifier = null;
	}
	
	mgr = findTxnMgr();

	TransactionManager.Created tcs = null;
	
	System.out.println("Creating transaction");
	try {
	    tcs = mgr.create(Lease.FOREVER);
	} catch(java.rmi.RemoteException e) {
	    mgr = null;
	    return;
	} catch(net.jini.core.lease.LeaseDeniedException e) {
	    mgr = null;
	    return;
	}
	
	long transactionID = tcs.id;
	
	// join in ourselves
	System.out.println("Joining transaction");

	// we need to give a proxy to the transaction mgr	
	Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						  new BasicILFactory());

	// export an object of this class
	TransactionParticipant proxy = null;
	try {
	    proxy = (TransactionParticipant) exporter.export(this);
	} catch (ExportException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	try {
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
	
	new LeaseRenewalManager().renewUntil(tcs.lease,
					     Lease.FOREVER,
					     null);
	System.out.println("crediting...");
	try {
	    classifier.credit(cost, myClientID,
			      mgr, transactionID);
	} catch(Exception e) {
	    System.err.println(e.toString());
	}
	
	System.out.println("classifying...");
	MIMEType type = null;
	try {
	    type = classifier.getMIMEType("file1.txt");
	} catch(java.rmi.RemoteException e) {
	    System.err.println(e.toString());
	}
	
	// if we get a good result, commit, else abort
	if (type != null) {
	    System.out.println("Type is " + type.toString());
	    System.out.println("Calling commit");
	    
	    try { 
		System.out.println("mgr state " + mgr.getState(transactionID));
		mgr.commit(transactionID);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    
	} else {
	    try {
		mgr.abort(transactionID);
	    } catch(java.rmi.RemoteException e) {
	    } catch(net.jini.core.transaction.CannotAbortException e) {
	    } catch( net.jini.core.transaction.UnknownTransactionException e) {
	    }
	}
    }
    
    public PayableFileClassifier findClassifier() {
	ServiceDiscoveryManager clientMgr = null;

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null, // unicast locators
                                           null); // DiscoveryListener
	    clientMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	Class [] classes = new Class[] {PayableFileClassifier.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);

	ServiceItem item = null;
	// Try to find the service, blocking till timeout if necessary
	try {
	    item = clientMgr.lookup(template, 
				    null, // no filter 
				    WAITFOR); // timeout
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("no service");
	    System.exit(1);
	}

	// Get the service
	PayableFileClassifier classifier = (PayableFileClassifier) item.service;

	if (classifier == null) {
	    System.out.println("Classifier null");
	    System.exit(1);
	}
	return classifier;
    }

    public TransactionManager findTxnMgr() {
	ServiceDiscoveryManager clientMgr = null;

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null, // unicast locators
                                           null); // DiscoveryListener
	    clientMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	Class [] classes = new Class[] {TransactionManager.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);

	ServiceItem item = null;
	// Try to find the service, blocking till timeout if necessary
	try {
	    item = clientMgr.lookup(template, 
				    null, // no filter 
				    WAITFOR); // timeout
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("no service");
	    System.exit(1);
	}

	// Get the service
	TransactionManager mgr = (TransactionManager) item.service;

	if (mgr == null) {
	    System.out.println("Mgr null");
	    System.exit(1);
	}
	return mgr;
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
} // TestTxn

