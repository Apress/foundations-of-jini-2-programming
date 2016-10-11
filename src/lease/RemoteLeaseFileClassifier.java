
/**
 * RemoteLeaseFileClassifier.java
 *
 *
 * Created: Sun Jun 20 17:55:16 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package lease;

import common.LeaseFileClassifier;
import java.rmi.Remote;

public interface RemoteLeaseFileClassifier extends LeaseFileClassifier, Remote {
    
} // RemoteLeaseFileClassifier
