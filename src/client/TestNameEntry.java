
package client;

import common.NameEntry;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.rmi.RMISecurityManager;

import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

/**
 * Client.java
 *
 *
 * Created: Mon Mar 29 12:03:51 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    simplified Class.forName to Class.class
 */

public class TestNameEntry /*extends JFrame*/ implements DiscoveryListener {
    
    public TestNameEntry() {
	/*
	super("Name TestNameEntry");
	setSize(600, 400);
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowOpened(WindowEvent e) {}});
	*/

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
        Class[] classes = new Class[] {NameEntry.class};
        NameEntry entry = null;
        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);
 
        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];
            try {
                entry = (NameEntry) registrar.lookup(template);
            } catch(java.rmi.RemoteException e) {
		e.printStackTrace();
                System.exit(2);
            }
            if (entry == null) {
                System.out.println("Classifier null");
                continue;
            }
	    System.out.println("found");
            entry.show();
        }
    }

    public void discarded(DiscoveryEvent evt) {
        // empty
    }

    public static void main(String[] args) {
	
	TestNameEntry f = new TestNameEntry();

        // stay around long enough to receive replies	
        try {
            Thread.currentThread().sleep(10000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }
    
} // TestNameEntry
