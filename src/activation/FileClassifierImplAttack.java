
package activation;

import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;

import common.MIMEType;
import common.FileClassifier;
import option3.RemoteFileClassifier;

/**
 * FileClassifierImpl.java
 *
 *
 * Created: Wed Mar 17 14:22:13 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierImpl extends Activatable
                                implements RemoteFileClassifier {
    
    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
	System.out.println("Called with " + fileName);
	try {Runtime.getRuntime().exec("/usr/bin/whoami");} catch(Exception e){e.printStackTrace();}
	java.util.Properties props = System.getProperties();
	System.out.println("user " + props.getProperty("user.name"));
	if (new java.io.File("/etc/at.deny").canRead())
	    System.out.println("can read at.deny");
	else
	    System.out.println("can't read at.deny");
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


    public FileClassifierImpl(ActivationID id, MarshalledObject data)  
	throws java.rmi.RemoteException {
	super(id, 0);
    }
    
} // FileClassifierImpl
