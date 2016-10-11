

package clock.device;

import clock.service.*;

import java.io.*;
import java.util.Date;

import java.rmi.*; 
import java.rmi.server.ExportException;
import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceRegistrar;
import java.rmi.RemoteException;
import net.jini.lookup.ServiceIDListener;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscoveryManager;

import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;

public class ClockDevice implements ServiceIDListener, ServiceDiscoveryListener {

    private Timer timer;

    public ClockDevice() {
	System.setSecurityManager(new RMISecurityManager());

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

    public void setTimer(Timer t) {
	timer = t;
	System.out.println("Our timer service is " + t);

	Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                     new BasicILFactory());

	// export a Timer proxy
	Remote proxy = null;
	try {
	    proxy = exporter.export(timer);
	} catch(ExportException e) {
	    System.exit(1);
	}

	// Register with all lookup services as they are discovered
	JoinManager joinMgr = null;
	try {
	    LookupDiscoveryManager mgr = 
		new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
					   null,  // unicast locators
					   null); // DiscoveryListener
	    joinMgr = new JoinManager(proxy, // service proxy
				      null,  // attr sets
				      this,  // ServiceIDListener
				      mgr,   // DiscoveryManager
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


   public void serviceAdded(ServiceDiscoveryEvent evt) {
	// evt.getPreEventServiceItem() == null
	ServiceItem postItem = evt.getPostEventServiceItem();
	System.out.println("Service appeared: " +
			   postItem.service.getClass().toString());
	tryClockValidation((Timer) postItem.service);
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
    }

    private void tryClockValidation(Timer otherTimer) {
	try {
	    if (timer.isValidTime() &&  ! otherTimer.isValidTime()) {
		// other clock needs to be set by us
		otherTimer.setTime(timer.getTime());
	    } else if (! timer.isValidTime() && otherTimer.isValidTime()) {
		// we need to be set from the other clock
		timer.setTime(otherTimer.getTime());
	    }
	} catch(RemoteException e) {
	    // ignore other timer!
	}
    }

    public void setTime(Date t) throws RemoteException {
	timer.setTime(t);
    }

    public Date getTime()  throws RemoteException {
	return timer.getTime();
    }

    public boolean isValidTime()  throws RemoteException {
	return timer.isValidTime();
    }
}

