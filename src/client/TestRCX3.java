
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
import net.jini.core.discovery.LookupLocator;

/**
 * TestRCX3.java
 *
 *
 * Created: Wed Mar 17 14:29:15 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    added GUI front-end
 */

public class TestRCX3 {

    public static final int STOPPED = 1;
    public static final int FORWARDS = 2;
    public static final int BACKWARDS = 4;

    protected int state = STOPPED;

    public static void main(String argv[]) {
	new TestRCX3();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(10000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public TestRCX3() {
	System.setSecurityManager(new RMISecurityManager());

        LookupLocator lookup = null;
        ServiceRegistrar registrar = null;

        try {
            lookup = new LookupLocator("jini://www.jini.canberra.edu.au");
        } catch(java.net.MalformedURLException e) {
            System.err.println("Lookup failed: " + e.toString());
            System.exit(1);
        }

	Class [] classes = new Class[] {RCXPortInterface.class};
	RCXPortInterface port = null;
	ServiceTemplate template = new ServiceTemplate(null, classes, 
						       null);

        try {
            registrar = lookup.getRegistrar();
        } catch (java.io.IOException e) {
            System.err.println("Registrar search failed: " + e.toString());
            System.exit(1);
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("Registrar search failed: " + e.toString());
            System.exit(1);
        }
	
	try {
	    System.out.println("Registrar found at " + registrar.getLocator().getHost());
	} catch(Exception e) {
	}

	try {
	    port = (RCXPortInterface) registrar.lookup(template);
	} catch(java.rmi.RemoteException e) {
	    e.printStackTrace();
	    System.exit(2);
	}
	if (port == null) {
	    System.out.println("port null");
	    System.exit(1);
	}
	
	// add an EventHandler as an RCX Port listener
	try {
	    port.addListener(new EventHandler(port));
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    class EventHandler extends UnicastRemoteObject
                       implements RemoteEventListener, ActionListener {

	protected RCXPortInterface port = null;

	JFrame frame;
	JTextArea text;

        public EventHandler(RCXPortInterface port) throws RemoteException {
            super() ;
	    this.port = port;

	    frame = new JFrame("Lego MindStorms");
	    Container content = frame.getContentPane();
	    JLabel label = new JLabel(new ImageIcon("images/mindstorms.jpg"));
	    JPanel pane = new JPanel();	    
	    pane.setLayout(new GridLayout(2, 3));

	    content.add(label, "North");
	    content.add(pane, "Center");

	    JButton btn = new JButton("Forward");
	    pane.add(btn);
	    btn.addActionListener(this);

	    btn = new JButton("Stop");
	    pane.add(btn);
	    btn.addActionListener(this);

	    btn = new JButton("Back");
	    pane.add(btn);
	    btn.addActionListener(this);

	    btn = new JButton("Left");
	    pane.add(btn);
	    btn.addActionListener(this);

	    label = new JLabel("");
	    pane.add(label);

	    btn = new JButton("Right");
	    pane.add(btn);
	    btn.addActionListener(this);

	    frame.pack();
	    frame.setVisible(true);
        }

	public void sendCommand(String comm) {
	    byte[] command;
	    try {
		command = port.parseString(comm);
		if (! port.write(command)) {
		    System.err.println("command failed");
		}
	    } catch(RemoteException e) {
		e.printStackTrace();
	    }
	}

	public void forwards() {
	    sendCommand("e1 85");
	    sendCommand("21 85");
	    state = FORWARDS;
	}

	public void backwards() {
	    sendCommand("e1 45");
	    sendCommand("21 85");
	    state = BACKWARDS;
	}

	public void stop() {
	    sendCommand("21 45");
	    state = STOPPED;
	}

	public void restoreState() {
	    if (state == FORWARDS)
		forwards();
	    else if (state == BACKWARDS)
		backwards();
	    else
		stop();
	}

	public void actionPerformed(ActionEvent evt) {
	    String name = evt.getActionCommand();

	    if (name.equals("Forward")) {
		forwards();
	    } else  if (name.equals("Stop")) {
		stop();
	    } else  if (name.equals("Back")) {
		backwards();
	    } else  if (name.equals("Left")) {
		sendCommand("e1 84");
		sendCommand("21 84");
		sendCommand("21 41");
		try {
		    Thread.sleep(100);
		} catch(InterruptedException e) {
		}
		restoreState();
	
	    } else  if (name.equals("Right")) {
		sendCommand("e1 81");
		sendCommand("21 81");
		sendCommand("21 44");
		try {
		    Thread.sleep(100);
		} catch(InterruptedException e) {
		}
		restoreState();
	    }
	}

        public void notify(RemoteEvent evt) throws UnknownEventException, 
                                                 java.rmi.RemoteException {
	    // System.out.println(evt.toString());
    
	    long id = evt.getID();
	    long seqNo = evt.getSequenceNumber();
	    if (id == RCXPortInterface.MESSAGE_EVENT) {
		byte[] message = port.getMessage(seqNo);
		StringBuffer sbuffer = new StringBuffer();
		for(int n = 0; n < message.length; n++) {       
		    int newbyte = (int) message[n];
		    if (newbyte < 0) {
			newbyte += 256;
		    }
		    sbuffer.append(Integer.toHexString(newbyte) + " ");
		}
		System.out.println("MESSAGE: " + sbuffer.toString());
	    } else if (id == RCXPortInterface.ERROR_EVENT) {
		System.out.println("ERROR: " + port.getError(seqNo));
	    } else {
		throw new UnknownEventException("Unknown message " + evt.getID());
  	    }
        }
    }

} // TestRCX
