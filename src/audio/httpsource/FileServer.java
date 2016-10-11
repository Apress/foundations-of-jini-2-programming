package audio.httpsource;

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
import java.net.URL;
import net.jini.lookup.entry.*;
import net.jini.core.entry.Entry;
import net.jini.core.discovery.LookupLocator;

import net.jini.config.*; 
import net.jini.export.*; 
import net.jini.id.UuidFactory;
import net.jini.id.Uuid;

import java.io.*;

import audio.http.*;

/**
 * FileServer.java
 *
 *
 * Created: 30 June 2003
 *
 * @author Jan Newmarch
 * @version 1.1
 */

public class FileServer {

    // explicit proxy for Jini 2.0
    private Remote proxy;
    private HttpSourceImpl impl;
    private static String configFile;
    private Entry[] entries;
    private File serviceIDFile;
    private ServiceID serviceID;

    public static void main(String argv[]) {
	configFile = argv[0];

	FileServer serv = new FileServer(argv);

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

    public FileServer(String[] argv) {
	URL url = null;
	Exporter exporter = null;

	if (argv.length != 1) {
	    System.err.println("Usage: FileServer config_file");
	    System.exit(1);
	}

	try {
	} catch(Exception e) {
            System.err.println("New impl: " + e.toString());
            System.exit(1);
	}

	String[] configArgs = argv;
	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(configArgs); 

	    // and use this to construct an exporter
	    exporter = (Exporter) config.getEntry( "HttpFile", 
						   "exporter", 
						   Exporter.class);

	    url = (URL) config.getEntry("HttpFile",
					"url",
					URL.class);

	    serviceIDFile = (File) config.getEntry("HttpFile",
						    "serviceIDFile",
						    File.class);
	    getOrMakeServiceID(serviceIDFile);

	    
	    Class cls = Class.forName("[Lnet.jini.core.entry.Entry;");
	    System.out.println(cls.toString());
	    entries = (Entry []) config.getEntry("HttpFile",
						 "entries",
						 cls);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	// Find the right implementation for the content type
        String urlStr = url.toString();
	try {
            if (urlStr.endsWith("wav")) {
                impl = new HttpWAVSourceImpl(url);
            } else if (urlStr.endsWith("mp3")) {
                impl = new HttpMP3SourceImpl(url);
            } else if (urlStr.endsWith("ogg")) {
                impl = new HttpOggSourceImpl(url);
            } else {
                System.out.println("Can't handle presentation type: " +
                                   url);
                return;
            }
	} catch(java.net.MalformedURLException e) {
	    System.err.println(e.toString());
	    System.exit(1);
	}


	try {
	    // export an object of this class
	    proxy = exporter.export(impl);
	} catch(java.rmi.server.ExportException e) {
	    System.err.println(e.toString());
	    System.exit(1);
	}

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   new LookupLocator[] {
					       new LookupLocator("jini://jannote.jan.home/")},
					   // unicast locators
					   null); // DiscoveryListener
	    joinMgr = new JoinManager(proxy,     // service proxy
				      entries,   // attr sets
				      serviceID, // ServiceID
				      mgr,       // DiscoveryManager
				      new LeaseRenewalManager());
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    private void getOrMakeServiceID(File serviceIDFile) {
	// try to read the service ID as
	// object from the file
	serviceID = null;
	try {
	    ObjectInputStream ois = 
		new ObjectInputStream(new FileInputStream(serviceIDFile));
	    serviceID = (ServiceID) ois.readObject();
	    System.out.println("Got dir service id " + serviceID);
	} catch(Exception e) {
	    System.out.println("Couldn't get service IDs - generating new one");
	    try {
		ObjectOutputStream oos = 
		    new ObjectOutputStream(new FileOutputStream(serviceIDFile));

		Uuid uuid = UuidFactory.generate();
		serviceID = new ServiceID(uuid.getMostSignificantBits(), 
				      uuid.getLeastSignificantBits());
		oos.writeObject(serviceID);
		oos.close();
	    } catch(Exception e2) {
		System.out.println("Couldn't save ids");
		e2.printStackTrace();
	    }
	}
    }
} // FileServer
