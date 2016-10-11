
/**
 * TcpSinkImpl.java
 *
 *
 * Created: Tue Sep  9 06:02:50 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.transport;

import java.io.*;
import java.net.*;

public class TcpSinkImpl implements TransportSink {

    protected TcpSource source;

    public TcpSinkImpl(TcpSource source) {
	this.source = source;
    }

    public InputStream getInputStream() {
	try {
	    InetSocketAddress addr = source.getInetSocketAddress();
	    Socket socket = new Socket(addr.getAddress(),
				       addr.getPort());

	    InputStream in = null;
	    
	    in = socket.getInputStream();
	    return in;
	} catch (IOException e) {
	    System.err.println("Getting in stream " + e.toString());
	    return null;
	}
    }
}// TcpSinkImpl
