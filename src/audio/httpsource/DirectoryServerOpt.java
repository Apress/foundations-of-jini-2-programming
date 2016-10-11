
/**
 * DirectoryServerOpt.java
 *
 *
 * Created: Sun Jun 29 10:25:18 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.httpsource;

import common.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.entry.*;
import net.jini.core.entry.Entry;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import net.jini.core.discovery.LookupLocator;
import net.jini.discovery.LookupDiscovery;
import java.net.URL;
import net.jini.discovery.LookupDiscoveryManager;
import java.rmi.RemoteException;
import net.jini.lookup.ServiceIDListener;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.core.lookup.*;
import net.jini.core.lease.*;

import net.jini.id.UuidFactory;
import net.jini.id.Uuid;

import net.jini.config.*; 
import net.jini.export.*; 
import com.sun.jini.config.Config;
				       
import java.util.Vector;

import audio.common.Directory;
import audio.http.*;
import audio.transport.*;

public class DirectoryServerOpt implements Directory, DiscoveryListener {
    private ServiceID serviceID = null;
    private ServiceID[] serviceIDs;
    protected Remote proxy;
    protected HttpSourceImpl impl;
    private Entry[] dirEntries;
    private String cdInfo;

    // info pulled out of a Configuration
    private URL url = null;
    private Exporter exporter = null;
    private String cdIndexDiscID;
    private String cddbDiscID;
    private URL[] trackURLs;
    private String[] trackNames;
    private Configuration config;
    private File serviceIDFile;

    // JoinManager's to keep the services alive
    // private JoinManager[] joinMgrs;
    // private JoinManager dirJoinMgr;
    private LeaseRenewalManager leaseRenewalManager;
    private LookupDiscoveryManager mgr;

    private Vector sources = new Vector();

    public DirectoryServerOpt(String[] configArgs, 
			      LeaseRenewalManager leaseRenewalManager,
			      LookupDiscoveryManager lookupDiscoveryManager) {
	this.leaseRenewalManager = leaseRenewalManager;
	this.mgr = lookupDiscoveryManager;

	System.out.println("1");
	getConfiguration(configArgs);
	System.out.println("2");
	cdInfo = getCDInfo();
	System.out.println("3");

	// joinMgrs = new JoinManager[trackURLs.length];
	System.out.println("4");
	/*
	try {
	    mgr = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					     //new LookupLocator[] {
					     //    new LookupLocator("jini://jannote.jan.home/")},
					     null,
					     // unicast locators
					     this); // DiscoveryListener
	} catch(IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	*/
	mgr.addDiscoveryListener(this);

    }

    public void discovered(DiscoveryEvent e) {
	ServiceRegistrar[] regs = e.getRegistrars();
	for (int m = 0; m < regs.length; m++) {
	    ServiceRegistrar reg = regs[m];

	    System.out.println("5");
	    for (int n = 0; n < trackURLs.length; n++) {
		makeFileService(reg, n, trackURLs[n]);
	    }

	    makeDirService(reg);
	}
    }

    public void discarded(DiscoveryEvent e) {

    }

    public ServiceID[] getServiceIDs() throws RemoteException {
	return serviceIDs;
    }

    private void getConfiguration(String[] configArgs) {
	try {
	    // get the configuration (by default a FileConfiguration)
	    // in configArgs[0]
	    config = ConfigurationProvider.getInstance(configArgs); 
	    System.out.println("Config is " + config);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	try {
	    Class cls = Entry[].class;
	    System.out.println(cls.toString());
	    dirEntries = (Entry []) config.getEntry("HttpFileSourceServer",
						 "entries",
						 cls);
	} catch(Exception e) {
	    System.err.println("Config error: " + e.toString());
	}

	try {
	    cdIndexDiscID = (String) config.getEntry("HttpFileSourceServer",
						     "cdIndexDiscID",
						     String.class);
	} catch(Exception e) {
	    System.err.println("Config error: " + e.toString());
	}

	try {
	    // This is an unsigned long - sometimes too big for Java long
	    cddbDiscID = (String) config.getEntry("HttpFileSourceServer",
						  "cddbDiscID",
						  String.class,
						  null);
	} catch(Exception e) {
	    System.err.println("Config error: " + e.toString());
	}


	try {
	    Class cls = URL[].class;
	    System.out.println(cls.toString());
	    trackURLs = (URL []) config.getEntry("HttpFileSourceServer",
						 "trackURLs",
						 cls);
	} catch(Exception e) {
	    System.err.println("Config error: " + e.toString());
	    trackURLs = new URL[] {};
	}

	try {
	    Class cls = String[].class;
	    System.out.println(cls.toString());
	    trackNames = (String []) config.getEntry("HttpFileSourceServer",
						     "trackNames",
						     cls);
	} catch(Exception e) {
	    System.err.println("Config error: " + e.toString());
	}

	try {
	    serviceIDFile = (File) config.getEntry("HttpFileSourceServer",
						   "serviceIDFile",
						   File.class);
	    getOrMakeServiceIDs(serviceIDFile);
	} catch(Exception e) {
	    System.err.println("Config error: " + e.toString());
	}
    }

    private void getOrMakeServiceIDs(File serviceIDFile) {
	// try to read all the service IDs as
	// objects from the file
	serviceIDs = new ServiceID[trackURLs.length];
	try {
	    ObjectInputStream ois = 
		new ObjectInputStream(new FileInputStream(serviceIDFile));
	    serviceID = (ServiceID) ois.readObject();
	    System.out.println("Got dir service id " + serviceID);
	    for (int n = 0; n < trackURLs.length; n++) {
		serviceIDs[n] =  (ServiceID) ois.readObject();
		System.out.println("Got service id " + serviceIDs[n]);
	    }
	} catch(Exception e) {
	    System.out.println("Couldn't get service IDs - generating new ones");
	    try {
		ObjectOutputStream oos = 
		    new ObjectOutputStream(new FileOutputStream(serviceIDFile));

		Uuid uuid = UuidFactory.generate();
		serviceID = new ServiceID(uuid.getMostSignificantBits(), 
				      uuid.getLeastSignificantBits());
		oos.writeObject(serviceID);

		for (int n = 0; n < serviceIDs.length; n++) {
		    Uuid uuidFile = UuidFactory.generate();
		    ServiceID id = new ServiceID(uuidFile.getMostSignificantBits(), 
						 uuidFile.getLeastSignificantBits());
		    oos.writeObject(id);
		    serviceIDs[n] = id;
		    System.out.println("Generating service id " + serviceIDs[n]);
		}
	    } catch(Exception e2) {
		System.out.println("Couldn't save ids");
		e2.printStackTrace();
	    }
	}
    }

    private void makeDirService(ServiceRegistrar registrar) {
	try {
	    exporter = (Exporter) config.getEntry( "HttpFileSourceServer", 
						   "exporter", 
						   Exporter.class);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	try {
	    System.out.println("Export dir using exporter " + exporter);
	    // export an object of this class
	    proxy = exporter.export(this);
	} catch(java.rmi.server.ExportException e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}
	ServiceRegistration sr = null;
	try {
	    sr = registrar.register(new ServiceItem(serviceID,
						    proxy,
						    dirEntries),
				    Lease.FOREVER);
	} catch(RemoteException e) {
	    e.printStackTrace();
	}

	leaseRenewalManager.renewUntil(sr.getLease(), Lease.FOREVER, null);
	// dirJoinMgr = registerService(proxy, serviceID, dirEntries);
     }

    private void makeFileService(ServiceRegistrar registrar, int index, URL url) {

	Exporter exporter = null;
	try {
	    exporter = (Exporter) config.getEntry( "HttpFileSourceServer", 
							    "exporter", 
							    Exporter.class);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	String urlStr = url.toString();
 	System.out.println("URL is " + urlStr);
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
	} catch(Exception e) {
	    e.printStackTrace();
	    return;
	}
	sources.add(impl);

	try {
	    System.out.println("Export file " +  url + " using exporter " + exporter);
	    // export an object of this class
	    proxy = exporter.export(impl);
	} catch(java.rmi.server.ExportException e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	String  trackInfo = getTrackInfo(index);

	Entry[] entries = new Entry[] {new Name(trackInfo)};
	// joinMgrs[index] = registerService(proxy, serviceIDs[index], entries);

	ServiceRegistration sr = null;
	try {
	    sr = registrar.register(new ServiceItem(serviceIDs[index],
						    proxy,
						    entries),
				    Lease.FOREVER);
	} catch(RemoteException e) {
	    e.printStackTrace();
	}
	leaseRenewalManager.renewUntil(sr.getLease(), Lease.FOREVER, null);

    }

    private JoinManager registerService(Remote proxy, ServiceID serviceID, Entry[] entries) {
	JoinManager joinMgr = null;
	try {

	    System.out.println("Registering with id " + serviceID);
	    joinMgr = new JoinManager(proxy,     // service proxy
				      entries,   // attr sets
				      serviceID, // ServiceID
				      mgr,       // DiscoveryManager
				      leaseRenewalManager);
	    
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	return joinMgr;
    }

    public String getTrackInfo(int index) {
	// get from trackNames if possible
	if (trackNames != null) {
	    if ((trackNames.length > index) &&
		(trackNames[index] != null)) {
		return(cdInfo + ": " + trackNames[index]);
	    }
	}

	// failing that, get from CDDB d/b if possible
	// ... not yet implemented

	// failing that...
	String indexStr = null;
	if (index < 9) indexStr = "0" + (index + 1);
	else indexStr = "" + (index + 1);

	return(cdInfo + ": Track " + indexStr);
    }

    public String getCDInfo() {
	// get from CDDB d/b if possible
	// failing that...
	if (dirEntries == null) {
	    return "";
	}
	for (int n = 0; n  < dirEntries.length; n++) {
	    if (dirEntries[n] instanceof Name) {
		return ((Name) dirEntries[n]).name;
	    }
	}
	return "";
    }

    public static void main(String[] argv) {
	LeaseRenewalManager leaseRenewalManager = new LeaseRenewalManager();
	LookupDiscoveryManager lookupDiscoveryManager = null;
	try {
	    lookupDiscoveryManager =
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   //new LookupLocator[] {
					   //    new LookupLocator("jini://jannote.jan.home/")},
					   null,	// unicast locators
					   null);  // DiscoveryListener
	} catch(IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// install suitable security manager
	System.setSecurityManager(new RMISecurityManager());

	DirectoryServerOpt ds = new DirectoryServerOpt(argv, 
						       leaseRenewalManager,
						       lookupDiscoveryManager);

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
}// DirectoryServer
