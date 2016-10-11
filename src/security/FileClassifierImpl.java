
package security;

import common.MIMEType;
import common.FileClassifier;
import rmi.RemoteFileClassifier;

import net.jini.security.proxytrust.ServerProxyTrust;
import net.jini.security.TrustVerifier;
import net.jini.jeri.BasicJeriTrustVerifier;

/**
 * FileClassifierImpl.java
 *
 *
 * Created: Wed Mar 17 14:22:13 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved from package option3 to rmi
 * @version 1.2
 *    removed UnicastRemoteObject for Jini 2.0
 */

public class FileClassifierImpl implements RemoteFileClassifier /* ,
								   ServerProxyTrust */ {
    
    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
	System.out.println("Called with " + fileName);
        if (fileName.endsWith(".gif")) {
            return new MIMEType("image", "gif");
        } else if (fileName.endsWith(".jpeg")) {
            return new MIMEType("image", "jpeg");
        } else if (fileName.endsWith(".mpg")) { 
            return new MIMEType("video", "mpeg");
        } else if (fileName.endsWith(".txt")) {
            return new MIMEType("text", "plain");
        } else if (fileName.endsWith(".html")) {
            return new MIMEType("text", "html");
        } else
            // fill in lots of other types,
            // but eventually give up and
            return new MIMEType(null, null);
    }


    public FileClassifierImpl() throws java.rmi.RemoteException {
	// empty constructor required by RMI
    }
    
    /*
    public TrustVerifier getProxyVerifier() {
	// This should be specified in the configuration
	System.out.println("Getting verifier");
	return new BasicJeriTrustVerifier();
    }
    */
} // FileClassifierImpl
