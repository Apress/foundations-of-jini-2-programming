
package mutable;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.MarshalledObject;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.EventRegistration;
import java.rmi.RemoteException;
import java.rmi.Remote;
import net.jini.core.event.UnknownEventException ;

import javax.swing.event.EventListenerList;

import net.jini.export.*;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;

import net.jini.export.ProxyAccessor;

import net.jini.config.*;

import common.MIMEType;
import common.MutableFileClassifier;
import java.util.Map;
import java.util.HashMap;

/**
 * FileClassifierImpl.java
 *
 *
 * Created: Thu Jun 02
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 1.1
 *    removed UnicastRemoteObject for Jini 2.0
 */

public class FileClassifierImpl implements RemoteFileClassifier, ProxyAccessor {

    /**
     * Map of String extensions to MIME types
     */
    protected Map map = new HashMap();

    /**
     * Listeners for change events
     */
    protected EventListenerList listenerList = new EventListenerList();

    protected long seqNum = 0L;

    protected Remote proxy;

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
	System.out.println("Called with " + fileName);

	MIMEType type;
	String fileExtension;
	int dotIndex = fileName.lastIndexOf('.');

	if (dotIndex == -1 || dotIndex + 1 == fileName.length()) {
	    // can't find suitable suffix
	    return null;
	}

	fileExtension= fileName.substring(dotIndex + 1);
	type = (MIMEType) map.get(fileExtension);
	return type; 

    }

    public void addType(String suffix, MIMEType type)
	throws java.rmi.RemoteException {
	System.out.println("type added");
	map.put(suffix, type);
	fireNotify(ADD_TYPE);
    }

    public void removeType(String suffix)
	throws java.rmi.RemoteException {
	System.out.println("Type removed");
	if (map.remove(suffix) != null) {
	    fireNotify(REMOVE_TYPE);
	}
    }

    public EventRegistration addRemoteListener(RemoteEventListener listener)
	throws java.rmi.RemoteException {
	listenerList.add(RemoteEventListener.class, listener);

	return new EventRegistration(0, 
				     proxy, 
				     null, /* Lease is null for simplicity only.
					      It should be e.g. a LandlordLease
					   */
				     0);
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance 
    // is lazily created using the parameters passed into 
    // the fire method.

    protected void fireNotify(long eventID) {
	RemoteEvent remoteEvent = null;
	
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == RemoteEventListener.class) {
		RemoteEventListener listener = (RemoteEventListener) listeners[i+1];
		if (remoteEvent == null) {
		    remoteEvent = new RemoteEvent(proxy, eventID, 
						  seqNum++, null);
		}
		try {
		    listener.notify(remoteEvent);
		} catch(UnknownEventException e) {
		    e.printStackTrace();
		} catch(RemoteException e) {
		    // Remove this listener from the list due to failure
		    listenerList.remove(RemoteEventListener.class, listener);
		    System.out.println("notification failed, listener removed");
		}
	    }
	}
    }    

    // Implementation for ProxyAccessor
    public Object getProxy() {
        return proxy;
    }

    public FileClassifierImpl()  throws java.rmi.RemoteException {
	// empty constructor for proxy generation
    }

    public FileClassifierImpl(String[] configArgs)  throws java.rmi.RemoteException {
	// load a predefined set of MIME type mappings
	map.put("gif", new MIMEType("image", "gif"));
	map.put("jpeg", new MIMEType("image", "jpeg"));
	map.put("mpg", new MIMEType("video", "mpeg"));
	map.put("txt", new MIMEType("text", "plain"));
	map.put("html", new MIMEType("text", "html"));

	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(configArgs); 
	    
	    // and use this to construct an exporter
	    Exporter exporter = (Exporter) config.getEntry( "FileClassifierServer", 
							    "exporter", 
							    Exporter.class); 
	    // export an object of this class
	    proxy = exporter.export(this);
	} catch(Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    System.exit(1);
	}
    }
} // FileClassifierImpl
