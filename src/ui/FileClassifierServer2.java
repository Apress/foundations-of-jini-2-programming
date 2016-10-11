/**
 * FileClassifierServer2.java
 *
 *
 * Created: Wed Feb 10 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 *
 * A version of FileClassifierServer that exports
 * an HTML user interface
 */

package ui;

import complete.FileClassifierImpl;

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
import net.jini.lookup.ui.factory.HTMLFactory;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;

import java.rmi.MarshalledObject;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class FileClassifierServer2 
    implements ServiceIDListener {
    
    public static void main(String argv[]) {
	new FileClassifierServer2();

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

    public FileClassifierServer2() {

	JoinManager joinMgr = null;

	Set attribs = new HashSet();
	Set typeNames = new HashSet();
	typeNames.add(HTMLFactory.typeName);
	attribs.add(new UIFactoryTypes(typeNames));
	MarshalledObject factory = null;
	try {
	    factory = new MarshalledObject(new HTMLFileClassifierFactory());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(2);
	}
	UIDescriptor desc = new UIDescriptor(MainUI.role,
					     attribs,
					     factory);
	desc.toolkit = HTMLFactory.toolkit; // wrong arg count in constructor
	Entry[] entries = {desc};

	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null /* unicast locators */,
					   null /* DiscoveryListener */);
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
    
} // FileClassifierServer2
