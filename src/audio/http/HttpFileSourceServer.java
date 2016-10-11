package audio.http;

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

import java.util.StringTokenizer;
import java.io.*;

/**
 * HttpFileSourceServer.java
 *
 *
 * Created: 30 June 2003
 *
 * @author Jan Newmarch
 * @version 1.1
 */

public class HttpFileSourceServer 
    implements ServiceIDListener {

    // explicit proxy for Jini 2.0
    protected Remote proxy;
    protected HttpSourceImpl impl;
    private static String configFile;
    private Entry[] entries;

    public static void main(String argv[]) {
	configFile = argv[0];

	HttpFileSourceServer serv = new HttpFileSourceServer(argv);

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

    public HttpFileSourceServer(String[] argv) {
	URL url = null;
	Exporter exporter = null;
	ServiceID serviceID = null;

	if (argv.length != 1) {
	    System.err.println("Usage: HttpFileSourceServer config_file");
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
	    exporter = (Exporter) config.getEntry( "HttpFileSourceServer", 
							    "exporter", 
							    Exporter.class);

	    url = (URL) config.getEntry("HttpFileSourceServer",
					"url",
					URL.class);

	    serviceID = (ServiceID) config.getEntry("HttpFileSourceServer",
					"serviceID",
					ServiceID.class);

	    Class cls = Class.forName("[Lnet.jini.core.entry.Entry;");
	    System.out.println(cls.toString());
	    entries = (Entry []) config.getEntry("HttpFileSourceServer",
						 "entries",
						 cls);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}

	impl = new HttpSourceImpl(url);

	try {
	    // export an object of this class
	    proxy = exporter.export(impl);
	} catch(java.rmi.server.ExportException e) {
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
					   new LookupLocator[] {
					       new LookupLocator("jini://jannote.jan.home/")},
					   // unicast locators
					   null); // DiscoveryListener
	    if (serviceID != null) {
		joinMgr = new JoinManager(proxy,     // service proxy
					  entries,   // attr sets
					  serviceID, // ServiceID
					  mgr,       // DiscoveryManager
					  new LeaseRenewalManager());

	    } else {
		joinMgr = new JoinManager(proxy,    // service proxy
					  entries,  // attr sets
					  this,     // ServiceIDListener
					  mgr,      // DiscoveryManager
					  new LeaseRenewalManager());
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void serviceIDNotify(ServiceID serviceID) {
	// called as a ServiceIDListener
	System.out.println("got service ID " + serviceID.toString());
	
	// Should save the id to permanent storage
	saveID(configFile, serviceID.toString());
    }

    private void saveID(String configFile, String serviceID) {
	try {
	    File cFile = new File(configFile);
	    File tmpFile = File.createTempFile("config", null, new File("."));
	    File backFile = new File(configFile + ".bak");

	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
					new FileInputStream(cFile)));
	    PrintStream out = new PrintStream(new FileOutputStream(tmpFile));

	    String line;
	    while ((line = in.readLine()) != null) {
		System.out.println("Line " + line);
		String[] split;
		split = line.split("ServiceIDFactory.getServiceID\\(null\\)", 2);
		if (split.length == 1) {
		    // no match
		    out.println(line);
		} else {
		    System.out.println("Matched!");
		    out.print(split[0]);
		    out.print("ServiceIDFactory.getServiceID(\"" +
			      serviceID + "\")");
		    out.println(split[1]);
		}
	    }
	    in.close();
	    out.close();
	    System.out.println("Copy made");
	    
	    cFile.renameTo(backFile);
	    tmpFile.renameTo(new File(configFile));
	    System.out.println("Files renamed");
	} catch(IOException e) {
	    // ignore
	    e.printStackTrace();
	    return;
	}
    }
    
} // HttpFileSourceServer
