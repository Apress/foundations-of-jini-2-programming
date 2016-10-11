
package rmi;

import common.FileClassifier;
import common.MIMEType;

import rmi.RemoteFileClassifier;

import java.io.Serializable;
import java.io.IOException;
import java.rmi.Naming;

/**
 * FileClassifierNamingProxy
 *
 *
 * Created: Thu Mar 18 14:32:32 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierNamingProxy implements FileClassifier, Serializable {
    
    protected String serviceLocation;
    transient RemoteFileClassifier server = null;

    public FileClassifierNamingProxy(String serviceLocation) {
	this.serviceLocation = serviceLocation;
    }

    private void readObject(java.io.ObjectInputStream stream) 
	throws java.io.IOException, ClassNotFoundException {
	stream.defaultReadObject();

	try {
	    Object obj = Naming.lookup(serviceLocation);
	    server = (RemoteFileClassifier) obj;
	} catch(Exception e) {
	    System.err.println(e.toString());
	    System.exit(1);
	}
    }

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
	return server.getMIMEType(fileName);
    }
} // FileClassifierNamingProxy
