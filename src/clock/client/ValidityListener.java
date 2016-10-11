

package clock.client;

import service.*;

import java.io.*;
import java.util.Date;

import java.rmi.*; 
import java.rmi.server.ExportException;
import net.jini.export.*; 
import net.jini.jrmp.JrmpExporter;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceRegistrar;
import java.rmi.RemoteException;

import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;

public class ValidityListener implements ServiceDiscoveryListener,
					 RemoteEventListener {

    private Timer timer;
    private Remote proxy;

    public static void main(String[] args) {
	ValidityListener validListen = new ValidityListener();

	// This is a hack to keep the server alive.
	Object keepAlive = new Object();
	synchronized(keepAlive) {
	    try {
		// Wait for a "notify" from another thread
		// that will never be sent.
		// So we stay alive for ever
		keepAlive.wait();
	    } catch(java.lang.InterruptedException e) {
		// do nothing
	    }
	}
    }

    public ValidityListener() {
	System.setSecurityManager(new RMISecurityManager());

	// When this registers as an event listener it gives an
	// object to the service to call notify() on it
	// The object must be a proxy for this
	Exporter exporter = new JrmpExporter();
	try {
	    proxy = exporter.export(this);
	} catch(ExportException e) {
	    System.exit(1);
	}

	// Build a cache of all discovered clocks and monitor changes
	ServiceDiscoveryManager serviceMgr = null;
        LookupCache cache = null;
	Class [] classes = new Class[] {Timer.class};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);	
	try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null,  // unicast locators
                                           null); // DiscoveryListener
            serviceMgr = new ServiceDiscoveryManager(mgr, 
						    new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	
        try {
            cache = serviceMgr.createLookupCache(template, 
                                                null,  // no filter
                                                this); // listener
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Change start    
    // an event has arrived
    public void notify(RemoteEvent evt) {
	System.out.println("Got event from " + evt.getSource());
	String value = evt.getID() == Timer.VALID ? "valid" : "invalid";
	System.out.println("   value is " + value);
    }
    // Change end

    public void serviceAdded(ServiceDiscoveryEvent evt) {
	// evt.getPreEventServiceItem() == null
	ServiceItem postItem = evt.getPostEventServiceItem();
	System.out.println("Service appeared: " +
			   postItem.service.getClass().toString());
	Timer timerSvc = (Timer) postItem.service;
	// Change start
	try {
	    // note the class cast: the proxy implements all the
	    // interfaces of its service
	    timerSvc.addEventListener((RemoteEventListener) proxy);
	} catch(RemoteException e) {
	    e.printStackTrace();
	}
	// Change end
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
	// should remove proxy from event listener list
    }

}

