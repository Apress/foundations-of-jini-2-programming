
package mutable;

import common.MutableFileClassifier;
import common.MIMEType;

import java.io.Serializable;
import java.io.IOException;
import java.rmi.Remote;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;

/**
 * FileClassifierProxy
 *
 *
 * Created: Thu Jun 10 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 1.1
 *   use explicit proxy for Jini 2.0 in constructor
 */

public class FileClassifierProxy implements MutableFileClassifier, Serializable {

    RemoteFileClassifier serverProxy = null;

    public FileClassifierProxy(Remote servProxy) {
	this.serverProxy = (RemoteFileClassifier) servProxy;
    }

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
	return serverProxy.getMIMEType(fileName);
    }

    public void addType(String suffix, MIMEType type)
	throws java.rmi.RemoteException {
	serverProxy.addType(suffix, type);
    }

    public void removeType(String suffix)
	throws java.rmi.RemoteException {
	serverProxy.removeType(suffix);
    }

    public EventRegistration addRemoteListener(RemoteEventListener listener)
	throws java.rmi.RemoteException {
	return serverProxy.addRemoteListener(listener);
    }

} // FileClassifierProxy
