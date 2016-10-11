/**
 * FileClassifierServer.java
 *
 *
 * Created: Wed Feb 10 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 *
 * A version of FileClassifierServer that exports
 * a MainUI user interface
 */

package ui;

import complete.FileClassifierImpl;

import java.rmi.RMISecurityManager;
import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceRegistrar;
import java.rmi.RemoteException;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.core.entry.Entry;

import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.factory.FrameFactory;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;

import java.rmi.MarshalledObject;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import com.artima.lookup.util.ConsistentSet;

public class FileClassifierServer 
    implements ServiceIDListener {
    
    public static void main(String argv[]) {
	new FileClassifierServer();

        // stay around forever
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		keepAlive.wait();
	    } catch(InterruptedException e) {
		// do nothing
	    }
	}
    }

    public FileClassifierServer() {

	System.setSecurityManager(new RMISecurityManager());

	JoinManager joinMgr = null;

	// The typenames for the factory
	Set typeNames = new HashSet();
	typeNames.add(FrameFactory.TYPE_NAME);
	typeNames = new ConsistentSet(typeNames);

	// The attributes set
	Set attribs = new HashSet();
	attribs.add(new UIFactoryTypes(typeNames));
	attribs = new ConsistentSet(attribs);

	// The factory
	MarshalledObject factory = null;
	try {
	    factory = new MarshalledObject(new FileClassifierFrameFactory());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(2);
	}
	UIDescriptor desc = new UIDescriptor(MainUI.ROLE,
					     FileClassifierFrameFactory.TOOLKIT,
					     attribs,
					     factory);

	Entry[] entries = {desc};

	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null,
					   null);
	    joinMgr = new JoinManager(new FileClassifierImpl(), /* service */
				      entries /* attr sets */,
				      this /* ServiceIDListener*/,
				      mgr /* DiscoveryManagement */,
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // FileClassifierServer
