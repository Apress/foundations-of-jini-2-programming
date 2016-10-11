
/**
 * ExtendedFileClassifierImpl.java
 *
 *
 * Created: Tue Oct 26 21:16:23 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 1.1
 *    removed UnicastRemoteObject for Jeri
 */

package extended;

import java.rmi.server.UnicastRemoteObject;
import common.MIMEType;
import java.util.HashMap;
import java.util.Map;

public class ExtendedFileClassifierImpl
    implements RemoteExtendedFileClassifier {

    /**
     * Map of String extensions to MIME types
     */
    protected Map map = new HashMap();
    
    public ExtendedFileClassifierImpl() throws java.rmi.RemoteException {
	/* This object will handle all classification attempts
	 * that fail in client-side classifiers. It will be around
	 * a long time, and may be called frequently. So it is worth
	 * optimising the implementation by using a hash map
	 */
	map.put("rtf", new MIMEType("application", "rtf"));
	map.put("dvi", new MIMEType("application", "x-dvi"));
	map.put("png", new MIMEType("image", "png"));
	// etc
    }
    
    public MIMEType getExtraMIMEType(String fileName) 
	throws java.rmi.RemoteException {
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
} // ExtendedFileClassifierImpl
