
/**
 * HttpSinkImpl.java
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

public class HttpSinkImpl implements TransportSink {

    protected HttpSource source;

    public HttpSinkImpl(HttpSource source) {
	this.source = source;
    }

    public InputStream getInputStream() {
	try {
	    HttpURL url = source.getHttpURL();
	    URLConnection connect = url.openConnection();

	    connect.setDoInput(true);
	    connect.setDoOutput(false);
	    InputStream in = connect.getInputStream();
	    return in;
	} catch (IOException e) {
	    System.err.println("Getting in stream " + e.toString());
	    return null;
	}
    }
}// HttpSinkImpl
