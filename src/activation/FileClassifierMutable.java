
package activation;

import java.io.*;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.activation.ActivationID;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.EventRegistration;
import java.rmi.RemoteException;
import net.jini.core.event.UnknownEventException ;

import net.jini.export.ProxyAccessor;
import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.activation.ActivationExporter;

import javax.swing.event.EventListenerList;

import common.MIMEType;
import common.MutableFileClassifier;
import mutable.RemoteFileClassifier;
import java.util.Map;
import java.util.HashMap;

/**
 * FileClassifierMutable.java
 *
 *
 * Created: Wed Dec 27 1999
 *
 * @author Jan Newmarch
 * @version 2.0
 *     updated to Jini 2.0
 */

public class FileClassifierMutable implements RemoteFileClassifier,
					      ProxyAccessor {

    private Remote proxy;

    /**
     * Map of String extensions to MIME types
     */
    private Map map = new HashMap();

    /**
     * Permanent storage for the map while inactive
     */
    private String mapFile;

    /**
     * Listeners for change events
     */
    private EventListenerList listenerList = new EventListenerList();

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
	map.put(suffix, type);
	fireNotify(MutableFileClassifier.ADD_TYPE);
	saveMap();
    }

    public void removeType(String suffix)
	throws java.rmi.RemoteException {
	if (map.remove(suffix) != null) {
	    fireNotify(MutableFileClassifier.REMOVE_TYPE);
	    saveMap();
	}
    }

    public EventRegistration addRemoteListener(RemoteEventListener listener)
	throws java.rmi.RemoteException {
	listenerList.add(RemoteEventListener.class, listener);

	return new EventRegistration(0, this, null, 0);
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
		    remoteEvent = new RemoteEvent(this, eventID, 
						  0L, null);
		}
		try {
		    listener.notify(remoteEvent);
		} catch(UnknownEventException e) {
		    e.printStackTrace();
		} catch(RemoteException e) {
		    e.printStackTrace();
		}
	    }
	}
    }    

    /**
     * Restore map from file.
     * Install default map if any errors occur
     */
    public void restoreMap() {
	try {
	    FileInputStream istream = new FileInputStream(mapFile);
	    ObjectInputStream p = new ObjectInputStream(istream);
	    map = (Map) p.readObject();
	    
	    istream.close();
	} catch(Exception e) {
	    e.printStackTrace();
	    // restoration of state failed, so
	    // load a predefined set of MIME type mappings
	    map.put("gif", new MIMEType("image", "gif"));
	    map.put("jpeg", new MIMEType("image", "jpeg"));
	    map.put("mpg", new MIMEType("video", "mpeg"));
	    map.put("txt", new MIMEType("text", "plain"));
	    map.put("html", new MIMEType("text", "html"));
	    
	    this.mapFile = mapFile;
	    saveMap();
	}
    }

    /**
     * Save map to file.
     */
    public void saveMap() {
	try {
	    FileOutputStream ostream = new FileOutputStream(mapFile);
	    ObjectOutputStream p = new ObjectOutputStream(ostream);
	    p.writeObject(map);
	    p.flush();
	    ostream.close();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    public FileClassifierMutable(ActivationID activationID, 
				 MarshalledObject data)  
	throws java.rmi.RemoteException {
	Exporter exporter = 
	    new ActivationExporter(activationID,
			 new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
					       new BasicILFactory(),
					       false, true));
	    
	proxy = (Remote) exporter.export(this);
	try {
	    mapFile = (String) data.get();
	} catch(Exception e) {
	    e.printStackTrace();
	}
	restoreMap();
    }

    // Implementation for ProxyAccessor
    public Object getProxy() {
	return proxy;
    }
} // FileClassifierMutable
