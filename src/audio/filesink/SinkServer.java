package audio.filesink;

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
import java.rmi.RMISecurityManager;
import java.rmi.Remote;

import net.jini.config.*; 
import net.jini.export.*; 

import net.jini.id.UuidFactory;
import net.jini.id.Uuid;

import net.jini.core.entry.Entry;
import net.jini.lookup.entry.*;

import net.jini.lookup.ui.MainUI;
import java.rmi.MarshalledObject;
import java.util.Set;
import java.util.HashSet;
import net.jini.lookup.ui.factory.JDialogFactory;
import net.jini.lookup.ui.attribute.UIFactoryTypes;

import java.io.*;

/**
 * SinkServer.java
 *
 * @author Jan Newmarch
 */

public class SinkServer 
    implements ServiceIDListener {

    // explicit proxy for Jini 2.0
    protected Remote proxy;
    protected SinkImpl impl;
    private String sinkName = "No name";
    private ServiceID serviceID;

    public static void main(String argv[]) {
	new SinkServer(argv);

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

    public SinkServer(String[] argv) {
	File serviceIDFile = null;

	try {
	    impl = new SinkImpl();
	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(argv); 
	    
	    // and use this to construct an exporter
	    Exporter exporter = (Exporter) config.getEntry( "FileSinkServer", 
							    "exporter", 
							    Exporter.class); 
	    // export an object of this class
	    proxy = exporter.export(impl);
	    impl.setProxy(proxy);

	    sinkName = (String) config.getEntry( "FileSinkServer", 
						 "sinkName", 
						 String.class);

	    serviceIDFile = (File) config.getEntry("FileSinkServer", 
						   "serviceIdFile", 
						   File.class); 
	    getOrMakeServiceID(serviceIDFile);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null,  // unicast locators
					   null); // DiscoveryListener
	    joinMgr = new JoinManager(proxy, // service proxy
				      new Entry[] {new Name(sinkName),
				                   getUIEntry()},  // attr sets
				      serviceID,  // ServiceID
				      mgr,   // DiscoveryManager
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    private Entry getUIEntry() {
        // The typenames for the factory
        Set typeNames = new HashSet();
        typeNames.add(JDialogFactory.TYPE_NAME);

        // The attributes set
        Set attribs = new HashSet();
        attribs.add(new UIFactoryTypes(typeNames));

        // The factory
        MarshalledObject factory = null;
        try {
            factory = new MarshalledObject(new FileSinkJDialogFactory());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        UIDescriptor desc = new UIDescriptor(MainUI.ROLE,
                                             JDialogFactory.TOOLKIT,
                                             attribs,
                                             factory);

        return desc;
    }

    private void getOrMakeServiceID(File serviceIDFile) {
	try {
	    ObjectInputStream ois = 
		new ObjectInputStream(new FileInputStream(serviceIDFile));
	    serviceID = (ServiceID) ois.readObject();
	} catch(Exception e) {
	    System.out.println("Couldn't get service IDs - generating new ones");
	    try {
		ObjectOutputStream oos = 
		    new ObjectOutputStream(new FileOutputStream(serviceIDFile));

		Uuid uuid = UuidFactory.generate();
		serviceID = new ServiceID(uuid.getMostSignificantBits(), 
				      uuid.getLeastSignificantBits());
		oos.writeObject(serviceID);
	    } catch(Exception e2) {
		System.out.println("Couldn't save ids");
		e2.printStackTrace();
	    }
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	// Should save the id to permanent storage
	System.out.println("got service ID " + serviceID.toString());
    }
    
} // SinkServer
