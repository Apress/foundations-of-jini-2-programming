
/**
 * FileClassifierProxy.java
 *
 *
 * Created: Tue Oct 26 21:36:36 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 * @version 1.1
 *    constructor takes proxy instead of RMI/JRMP remote
 */

package extended;

import common.FileClassifier;
import common.ExtendedFileClassifier;
import common.MIMEType;
import java.rmi.RemoteException;
import java.rmi.Remote;

public class FileClassifierProxy implements FileClassifier, java.io.Serializable {
    
    /**
     * The service object that knows lots more MIME types
     */
    protected RemoteExtendedFileClassifier extension;

    public FileClassifierProxy(Remote ext) {
	this.extension = (RemoteExtendedFileClassifier) ext;
    }

    public MIMEType getMIMEType(String fileName) 
	throws RemoteException {
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
        } else {
	    // we don't know it, pass it on to the service
	    return extension.getExtraMIMEType(fileName);
	}
    }
} // FileClassifierProxy
