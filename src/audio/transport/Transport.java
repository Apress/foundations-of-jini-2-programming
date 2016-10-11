
/**
 * Transport.java
 *
 *
 * Created: Sat Aug 23 08:37:24 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.transport;

import audio.common.*;

import java.io.*;
import java.net.*;
import java.rmi.*;

public class Transport {
    Source source;

    public static Transport createTransport(){
	return new Transport();
    }

    public static boolean compatable(Source source, Sink sink) {
	if (source instanceof HttpSource &&
	    sink instanceof HttpSink) {
	    return true;
	} else {
	    return false;
	}
    }
	
    public InputStream getInputStream(Source source) {
	if (source instanceof HttpSource) {
	    URLConnection connect = null;
	    HttpURL url = null;

	    try {
		url = ((HttpSource) source).getHttpURL();
	    } catch(RemoteException e) {
		e.printStackTrace();
		return null;
	    }

	    try {
		connect = url.openConnection();
		connect.setDoInput(true);
		connect.setDoOutput(false);
		return connect.getInputStream();
	    } catch (IOException e) {
		// ignore
		System.err.println("Getting in stream " + e.toString());
		return null;
	    }
	}
	return null;
    }
}// Transport
