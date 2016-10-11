
package lease;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;

import com.sun.jini.landlord.Landlord;

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

public class FileClassifierImpl implements RemoteLeaseFileClassifier {

    public final long DURATION = 2*60*1000L; // 2 minutes

    /**
     * Map of String extensions to MIME types
     */
    protected Map map = new HashMap();

    protected transient FileClassifierLandlord landlord;

    public MIMEType getMIMEType(String fileName) {
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
	throws LeaseDeniedException {
	if (map.containsKey(suffix)) {
	    throw new LeaseDeniedException("Extension already has a MIME type");
	}
	map.put(suffix, type);
	System.out.println("type added");
	Lease lease = landlord.newFileClassifierLease(this, suffix, DURATION);
	System.out.println("Lease is " + lease);
	return lease;
	//return null;
    }

    public void removeType(String suffix) {
	map.remove(suffix);
    }

    
    public FileClassifierImpl() throws RemoteException {
	// load a predefined set of MIME type mappings
	map.put("gif", new MIMEType("image", "gif"));
	map.put("jpeg", new MIMEType("image", "jpeg"));
	map.put("mpg", new MIMEType("video", "mpeg"));
	map.put("txt", new MIMEType("text", "plain"));
	map.put("html", new MIMEType("text", "html"));

	landlord  = new FileClassifierLandlord();
    }
} // FileClassifierImpl




