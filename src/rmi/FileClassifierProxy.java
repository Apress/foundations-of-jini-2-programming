
package rmi;

import common.FileClassifier;
import common.MIMEType;

import java.io.Serializable;
import java.io.IOException;
import java.rmi.Naming;

/**
 * FileClassifierProxy
 *
 *
 * Created: Thu Mar 18 14:32:32 1999
 *
 * @author Jan Newmarch
 * @version 1.2
 *    from from package option3 to rmi
 *    simplified by using RMI stub object for impl instead of
 *        name of object to be looked up
 */

public class FileClassifierProxy implements FileClassifier, Serializable {

    protected String serviceLocation;
    RemoteFileClassifier server = null;

    public FileClassifierProxy(FileClassifierImpl serv) {
	this.server = serv;
	if (serv==null) System.err.println("server is null");
    }

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
if (server==null) System.err.println("server2 is null");
if (fileName==null) System.err.println("filename is null");
	return server.getMIMEType(fileName);
    }
} // FileClassifierProxy
