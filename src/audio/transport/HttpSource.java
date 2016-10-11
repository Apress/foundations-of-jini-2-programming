
/**
 * HttpSource.java
 *
 *
 * Created: Fri Jun 27 09:25:40 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.transport;

import audio.common.*;
import java.net.URL;
import java.rmi.RemoteException;

public interface HttpSource extends Source {

    HttpURL getHttpURL() throws RemoteException;
}// HttpSource
