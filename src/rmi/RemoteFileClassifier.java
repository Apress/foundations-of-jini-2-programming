
package rmi;

import common.FileClassifier;
import java.rmi.Remote;

/**
 * RemoteFileClassifier.java
 *
 *
 * Created: Wed Mar 17 14:14:36 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved from package option3 to rmi
 */

public interface RemoteFileClassifier extends FileClassifier, Remote {
        
} // RemoteFileClasssifier
