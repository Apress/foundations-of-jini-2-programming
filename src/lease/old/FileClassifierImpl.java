
package lease;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;

import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;

import common.MIMEType;
import common.LeaseFileClassifier;

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
 */

public class FileClassifierImpl extends  UnicastRemoteObject
                                implements RemoteLeaseFileClassifier {

    public final long DURATION = 5*60*1000L;

    /**
     * Map of String extensions to MIME types
     */
    protected Map map = new HashMap();

    protected transient FileClassifierLandlord landlord;

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

    public Lease addType(String suffix, MIMEType type)
	throws java.rmi.RemoteException,
	       LeaseDeniedException {
	if (map.containsKey(suffix)) {
	    throw new LeaseDeniedException("Extension already has a MIME type");
	}
	map.put(suffix, type);
	return landlord.newFileClassifierLease(this, suffix, DURATION);
	//return null;
    }

    public void removeType(String suffix)
	throws java.rmi.RemoteException {
	map.remove(suffix);
    }

    
    public FileClassifierImpl()  throws java.rmi.RemoteException {
	// load a predefined set of MIME type mappings
	map.put("gif", new MIMEType("image", "gif"));
	map.put("jpeg", new MIMEType("image", "jpeg"));
	map.put("mpg", new MIMEType("video", "mpeg"));
	map.put("txt", new MIMEType("text", "plain"));
	map.put("html", new MIMEType("text", "html"));

	landlord  = new FileClassifierLandlord();
    }
} // FileClassifierImpl




