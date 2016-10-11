
/**
 * ExtendedFileClassifier.java
 *
 *
 * Created: Tue Oct 26 21:11:49 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package common;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface ExtendedFileClassifier extends Serializable {
    
    public MIMEType getExtraMIMEType(String fileName)
	throws RemoteException;
    
} // ExtendedFileClassifier
