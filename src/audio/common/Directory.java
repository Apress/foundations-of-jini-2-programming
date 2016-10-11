
/**
 * Directory.java
 *
 *
 * Created: Sun Jun 29 10:17:32 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import net.jini.core.lookup.ServiceID;

/**
 * A one-level directory of services. If the directory is also
 * a service then it allows a directory tree hierarchy to be built
 */

public interface Directory extends Remote {

    ServiceID[] getServiceIDs() throws RemoteException;
}// Directory
