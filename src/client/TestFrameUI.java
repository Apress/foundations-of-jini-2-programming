
package client;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import net.jini.core.entry.Entry;

import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.factory.FrameFactory;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;

import java.awt.*;
import javax.swing.*;

import java.util.Set;
import java.util.Iterator;
import java.net.URL;

/**
 * TestFrameUI.java
 *
 *
 * Created: Wed Feb 10 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class TestFrameUI {

    private static final long WAITFOR = 100000L;

    public static void main(String argv[]) {
	new TestFrameUI();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestFrameUI() {
	ServiceDiscoveryManager clientMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null /* unicast locators */,
                                           null /* DiscoveryListener */);
	    clientMgr = new ServiceDiscoveryManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	Class [] classes = new Class[] {FileClassifier.class};
	UIDescriptor desc = new UIDescriptor(MainUI.ROLE, 
					     FrameFactory.TOOLKIT, 
					     null, null);
	Entry [] entries = null; // {desc};

	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       entries);

	ServiceItem item = null;
	try {
	    item = clientMgr.lookup(template, 
				    null, /* no filter */ 
				    WAITFOR /* timeout */);
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("no service");
	    System.exit(1);
	}

	if (item.service == null) {
	    // found a broken service
	    System.out.println("service is null");
	    System.exit(1);
	}

	// We now have a service that plays the MainUI role and
	// uses the FrameFactory toolkit of "java.awt".
	// We now have to find if there is a UIDescriptor
	// with a Factory generating an AWT Frame
	checkUI(item);
    }

    private void checkUI(ServiceItem item) {
	// Find and check the UIDescriptor's
	Entry[] attributes = item.attributeSets;
	for (int m = 0; m < attributes.length; m++) {
	    Entry attr = attributes[m];
	    if (attr instanceof UIDescriptor) {
		// does it deliver an AWT Frame?
		checkForAWTFrame(item, (UIDescriptor) attr);
	    }
	}
    }
   
    private void checkForAWTFrame(ServiceItem item, UIDescriptor desc) {
	Set attributes = desc.attributes;
	Iterator iter = attributes.iterator();
	while (iter.hasNext()) {
	    // search through the attributes, to find a UIFactoryTypes
	    Object obj = iter.next();
	    if (obj instanceof UIFactoryTypes) {
		UIFactoryTypes types = (UIFactoryTypes) obj;
		// see if it produces an AWT Frame Factory
		if (types.isAssignableTo(FrameFactory.class)) {
		    FrameFactory factory = null;
		    try {
			factory = (FrameFactory) desc.getUIFactory(this.getClass().
								  getClassLoader());
		    } catch(Exception e) {
			e.printStackTrace();
			continue;
		    }

		    System.out.println("calling frame with " + item);
                    Frame frame = factory.getFrame(item); 
		    frame.setVisible(true);
		} 
	    }
	}
    }
	

} // TestFrameUI

