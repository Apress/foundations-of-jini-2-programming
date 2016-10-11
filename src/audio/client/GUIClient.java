/**
 * GUIClient.java
 *
 *
 * Created: Wed Mar 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package audio.client;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.core.lookup.ServiceID;
import java.rmi.RemoteException;
import net.jini.lookup.ServiceItemFilter;
import java.rmi.Remote;
import net.jini.core.lookup.ServiceTemplate;

import net.jini.config.*;
import net.jini.export.*;

import audio.common.*;

/** 
 * An A/V client that monitors sources and sinks and
 * uses a  ClientFrame to display them
 */

public class GUIClient implements ServiceDiscoveryListener {

    private static final long WAITFOR = 100000L;

    private ClientFrame clientFrame;
    private LookupCache cache;
    private ServiceDiscoveryManager clientMgr;

    private static String CONFIG_FILE = "resources/audio/jeri/http_sink_server.config";

    public static void main(String argv[]) {
	ClientFrame cf = new ClientFrame();
	GUIClient client = new GUIClient(cf);
	cf.setClient(client);

	cf.setSize(600, 600);
	cf.setVisible(true);

	/*
        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
	*/
    }

    public GUIClient(ClientFrame cf) {
	clientFrame = cf;


	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null,  // unicast locators
                                           null); // DiscoveryListener
            clientMgr = new ServiceDiscoveryManager(mgr, 
                                                new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
        ServiceTemplate template = new ServiceTemplate(null, null, 
                                                       null);
        try {
            cache = clientMgr.createLookupCache(template, 
                                                null,  // no filter
                                                this); // listener
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // methods for ServiceDiscoveryListener
    public void serviceAdded(ServiceDiscoveryEvent evt) {
	// evt.getPreEventServiceItem() == null
	ServiceItem postItem = evt.getPostEventServiceItem();

	System.out.println("Service appeared: " +
			   postItem.service.getClass().toString());
	if (postItem.service instanceof Directory) {
	    System.out.println("  is dir");
	    addDirectory(postItem);

	}
	if (postItem.service instanceof Sink) {
	    System.out.println("  is sink");
	    clientFrame.addSink(postItem);
	}
	if (postItem.service instanceof Source) {
	    System.out.println("  is source");
	    clientFrame.addSource(postItem);
	}
    }

    public void serviceChanged(ServiceDiscoveryEvent evt) {
	ServiceItem preItem = evt.getPostEventServiceItem();
	ServiceItem postItem = evt.getPreEventServiceItem() ;
	System.out.println("Service changed: " +
			   postItem.service.getClass().toString());
    }
    public void serviceRemoved(ServiceDiscoveryEvent evt) {
	// evt.getPostEventServiceItem() == null
	ServiceItem preItem = evt.getPreEventServiceItem();
	System.out.println("Service disappeared: " +
			   preItem.service.getClass().toString());

	if (preItem.service instanceof Directory) {
	    System.out.println("  was dir");
	    clientFrame.removeDirectory(preItem);
	}
	if (preItem.service instanceof Sink) {
	    System.out.println("  was sink");
	    clientFrame.removeSink(preItem);
	}
	if (preItem.service instanceof Source) {
	    System.out.println("  was source");
	    clientFrame.removeSource(preItem);
	}

    }
    
    private void addDirectory(ServiceItem item) {
	Object node = clientFrame.addDirectory(item);
	ServiceID[] ids = null;
	try {
	    ids = ((Directory) item.service).getServiceIDs();
	} catch(RemoteException e) {
	    System.err.println(e.toString());
	    return;
	}

	for (int n = 0; n < ids.length; n++) {
	    /* My directories register all component services before themselves,
	     * so when the LUS gets the directory, it already has its contents.
	     * I asssumed that the LUS would give them to the ServiceDiscoveryManager
	     * with order preserved, so by the time the cache says it has a directory
	     * then it will already have the components. Experimentally, this ain't so.
	     * So we ask the discovery manager for a search instead

	    */
	    ServiceID id = ids[n];
	    ServiceTemplate tmpl = new ServiceTemplate(id, null, null);
	    ServiceItem dirItem = null;
	    try {
		dirItem = clientMgr.lookup(tmpl, null, WAITFOR);
	    } catch (Exception e) {
		// dirItem stays null
	    }
	    
	    /* Broken: see above
	    final ServiceID id = ids[n];
	    // find a service in the cache that matches the service ID
	    // we are looking for - doesn't seem to be a way to retrieve
	    // by serviceID
	    ServiceItemFilter filter = new ServiceItemFilter() {
		    public boolean check(ServiceItem dirItem) {
			if (dirItem.serviceID.equals(id)) {
			    return true;
			} else {
			    return false;
			}
		    }
		};
	    ServiceItem dirItem = cache.lookup(filter);
	    */
	    if (dirItem != null) {
		clientFrame.addDirectoryElement(node, dirItem);
	    } else {
		System.out.println("Adding to dir " + item.serviceID + 
				   ": couldn't find dir element " + id);
	    }
	    
	}
    }

    public Remote export(PlayFrame cf) {
	String[] configArgs = new String[] {CONFIG_FILE};
	Remote proxy;

	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(configArgs); 
	    
	    // and use this to construct an exporter
	    Exporter exporter = (Exporter) config.getEntry( "HttpSinkServer", 
							    "exporter", 
							    Exporter.class); 
	    // export an object for the client listener
	    proxy = exporter.export(cf);
	    return proxy;

	} catch(Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
} // GUIClient
