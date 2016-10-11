
package client;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ClientLookupManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import net.jini.core.entry.Entry;

import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.factory.HTMLFactory;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;

import java.awt.*;
import javax.swing.*;

import java.util.Set;
import java.util.Iterator;
import java.net.URL;

/**
 * HTMLui.java
 *
 *
 * Created: Wed Feb 10 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class HTMLui {

    private static final long WAITFOR = 100000L;

    public static void main(String argv[]) {
	new HTMLui();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public HTMLui() {
	ClientLookupManager clientMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null /* unicast locators */,
                                           null /* DiscoveryListener */);
	    clientMgr = new ClientLookupManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	Class [] classes = new Class[] {FileClassifier.class};
	UIDescriptor desc = new UIDescriptor(MainUI.role, null, null);
	desc.toolkit =  HTMLFactory.toolkit; // due to bug
	Entry [] entries = {desc};

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

	// Now we should have a service which we shall ignore,
	// because the item.attributeSets should have an HTML
	// UI that we can use
System.out.println("Have service");
	Entry[] attributes = item.attributeSets;
	for (int m = 0; m < attributes.length; m++) {
	    Entry attr = attributes[m];
	    if (attr instanceof UIDescriptor) {
		showUI(item, (UIDescriptor) attr);
	    }
	}
    }
	

    private void showUI(ServiceItem item,
			UIDescriptor desc) {
System.out.println("Showing ui");
	Set attribs = desc.attributes;
	Iterator iter = attribs.iterator();
	while (iter.hasNext()) {
	    Object obj = iter.next();
	    if (obj instanceof UIFactoryTypes) {
		UIFactoryTypes types = (UIFactoryTypes) obj;
		Set typeNames = types.getTypeNames();
		if (typeNames.contains(HTMLFactory.typeName)) {
		    HTMLFactory factory = null;
		    try {
			factory = (HTMLFactory) desc.getUIFactory(this.getClass().
								  getClassLoader());
		    } catch(Exception e) {
			e.printStackTrace();
			continue;
		    }
		    URL url = factory.getURL(item);
System.out.println("Found factory, url " + url.toString());

		    JFrame frame = new JFrame("File Classifier");
		    JEditorPane pane = null;
		    try {
			pane = new JEditorPane(url);
		    } catch(Exception e) {
			e.printStackTrace();
			continue;
		    }
			    
		    frame.getContentPane().add(pane, BorderLayout.CENTER);
		    frame.setSize(400, 300);
		    frame.setVisible(true);
System.out.println("showing frame");
		} 
	    }
	}
    }

} // HTMLui
