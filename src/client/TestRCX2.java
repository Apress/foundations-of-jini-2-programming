
package client;

import rcx.jini.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceItem;

import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.attribute.UIFactoryTypes;
import net.jini.lookup.ui.factory.FrameFactory;
import net.jini.lookup.ui.factory.JFrameFactory;

import java.util.Set;
import java.util.Iterator;

/**
 * TestRCX2.java
 *
 *
 * Created: Wed Mar 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class TestRCX2 implements DiscoveryListener {

    public static void main(String argv[]) {
	new TestRCX2();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(1000000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestRCX2() {
	System.setSecurityManager(new RMISecurityManager());

	LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);

    }
    
    public void discovered(DiscoveryEvent evt) {

        ServiceRegistrar[] registrars = evt.getRegistrars();
	Class [] classes = new Class[] {RCXPortInterface.class};
	RCXPortInterface port = null;

	UIDescriptor desc = new UIDescriptor(MainUI.ROLE, null, null, null);
	Entry[] entries = {desc};
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       entries);
 
        for (int n = 0; n < registrars.length; n++) {
	    System.out.println("Service found");
            ServiceRegistrar registrar = registrars[n];
	    ServiceMatches matches = null;
	    try {
		matches = registrar.lookup(template, 10);
	    } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
		System.exit(2);
	    }
	    for (int nn = 0; nn < matches.items.length; nn++) {
		ServiceItem item = matches.items[nn];
		port = (RCXPortInterface) item.service;
		if (port == null) {
		    System.out.println("port null");
		    continue;
		}	

		Entry[] attributes = item.attributeSets;
		for (int m = 0; m < attributes.length; m++) {
		    Entry attr = attributes[m];
		    if (attr instanceof UIDescriptor) {
			showUI(port, item, (UIDescriptor) attr);
		    }
		}
	    }
	


	}
    }

    public void discarded(DiscoveryEvent evt) {
	// empty
    }

    private void showUI(RCXPortInterface port,
			ServiceItem item,
			UIDescriptor desc) {
	Set attribs = desc.attributes;
	Iterator iter = attribs.iterator();
	while (iter.hasNext()) {
	    Object obj = iter.next();
	    if (obj instanceof UIFactoryTypes) {
		UIFactoryTypes types = (UIFactoryTypes) obj;
		Set typeNames = types.getTypeNames();
		if (typeNames.contains(FrameFactory.TYPE_NAME)) {
		    FrameFactory factory = null;
		    try {
			factory = (FrameFactory) desc.getUIFactory(this.getClass().
								   getClassLoader());
		    } catch(Exception e) {
			e.printStackTrace();
			continue;
		    }
		    Frame frame = factory.getFrame(item);
		    frame.setVisible(true);
		} else if (typeNames.contains(JFrameFactory.TYPE_NAME)) {
		    JFrameFactory factory = null;
		    try {
			factory = (JFrameFactory) desc.getUIFactory(this.getClass().
								   getClassLoader());
		    } catch(Exception e) {
			e.printStackTrace();
			continue;
		    }
		    JFrame frame = factory.getJFrame(item);
		    frame.setVisible(true);
		}
	    } else {
		System.out.println("non-gui entry");
	    }	
	}
    }
} // TestRCX
