
package activation;

import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;

import common.MIMEType;
import common.FileClassifier;
import option3.RemoteFileClassifier;

public class Attack extends Activatable
                                implements RemoteFileClassifier {
    
    public void attack() 
	throws java.rmi.RemoteException {
	try {
	    Runtime.getRuntime().exec("/usr/bin/whoami");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	java.util.Properties props = System.getProperties();
	System.out.println("user " + props.getProperty("user.name"));
	if (new java.io.File("/etc/shadow").canRead())
	    System.out.println("can read shadow");
	else
	    System.out.println("can't read shadow");
    }

    public Attack(ActivationID id, MarshalledObject data)  
	throws java.rmi.RemoteException {
	super(id, 0);
    }
    
} // Attack
