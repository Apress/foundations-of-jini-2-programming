
/**
 * HttpURL.java
 *
 *
 * Created: Mon Sep  8 08:31:03 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.transport;

import java.net.MalformedURLException;
import java.net.*;
import java.io.*;

public class HttpURL implements java.io.Serializable {
    private URL url;

    public HttpURL(URL url) throws MalformedURLException {
	this.url = url;
	if (! url.getProtocol().equals("http")) {
	    throw new MalformedURLException("Not http URL");
	}
    }

    public HttpURL(String spec) throws MalformedURLException {
	url = new URL(spec);
	if (! url.getProtocol().equals("http")) {
	    throw new MalformedURLException("Not http URL");
	}
    }

    public URLConnection openConnection()
	throws IOException {
	return url.openConnection();
    }

    public InputStream openStream()
	throws IOException {
	return url.openStream();
    }
}// HttpURL
