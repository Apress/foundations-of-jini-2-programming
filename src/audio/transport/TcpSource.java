
/**
 * TcpSource.java
 *
 *
 * Created: Fri Jun 27 09:25:40 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.transport;

import audio.common.*;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;

public interface TcpSource extends Source {

    InetSocketAddress getInetSocketAddress() throws RemoteException;
}// TcpSource
