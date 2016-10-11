
/**
 * HttpSourceImpl.java
 *
 *
 * Created: Sat Jun 28 05:42:34 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.http;

import audio.common.*;
import audio.transport.*;

import java.net.*;
import java.rmi.*;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import java.rmi.MarshalledObject;

/**
 * Stores an HTTP reference
 */

public class HttpSourceImpl implements HttpSource, Remote {
    private HttpURL url;

    private HttpSourceImpl() {
    }

    public HttpSourceImpl(HttpURL url) {
	this.url = url;
    }

    public HttpSourceImpl(URL url) throws MalformedURLException {
	this.url = new HttpURL(url);
    }

    public HttpURL getHttpURL() {
	return url;
    }

    public void play() {}
    public void stop() {}
    public void addSink(Sink sink) throws IncompatableSinkException { }

    public void removeSink(Sink sink) {}

    public EventRegistration addSourceListener(RemoteEventListener listener,
					       MarshalledObject handback) {
	return null;
    }
}// HttpSourceImpl
