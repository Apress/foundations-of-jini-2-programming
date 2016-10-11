
/**
 * SinkImpl.java
 *
 *
 * Created: Sat Jun 28 05:42:34 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.pull;

import audio.transport.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import java.util.Vector;
import java.util.Enumeration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import java.util.Hashtable;

import audio.common.*;

public class SinkImpl implements Sink, Remote {
    private Source source;
    private boolean stopped;
    private CopyIO copyIO;
    private Hashtable listeners = new Hashtable();
    private int seqNum = 0;
    private Remote proxy;
    private MimeType contentType = null;
    private InputStream in = null;

    public SinkImpl() {

    }

    public void setProxy(Remote proxy) {
	this.proxy = proxy;
    }

    public void record() throws RemoteException, AlreadyRecordingException {

	if ((copyIO != null) && ( ! stopped)) {
	    throw new AlreadyRecordingException();
	}

	if (source == null) {
	    return;
	}

	stopped = false;

	if (in  == null) {
	    System.out.println("Couldn't get input stream");
	    stopped = true;
	    return;
	}

        // hand play over to a CopyIO object
        // This will run a ContentSink in its own thread
	copyIO = new CopyIO(this, in, source);
	copyIO.start();
	System.out.println("Play returning");
    }

    public void stop() throws RemoteException {
	stopped = true;
	if (copyIO != null) {
	    copyIO.stopRecord();
	}
    }

    public void contentStopped() {
	copyIO = null;
	fireNotify(Sink.STOP);
	System.out.println("Stopped");
    }

    public void addSource(Source source) throws
	IncompatableSourceException,
	TooManySourcesException {
	TransportSink transportSink = null;

	this.source = source;

        // which transport sink to use?
	if (source instanceof HttpSource) {
	    transportSink = new HttpSinkImpl((HttpSource) source);
	    in = transportSink.getInputStream();
	} else if (source instanceof TcpSource) {
	    System.out.println("Setting up Tcp sink");
	    transportSink = new TcpSinkImpl((TcpSource) source);
	    in = transportSink.getInputStream();
	    System.out.println("Got tcp source input stream " + in);
	} else {
	    throw new IncompatableSourceException();
	}
    }

    public void removeSource(Source source) throws
	RemoteException,
	NoSuchSourceException {
	if (this.source == source) {
	    this.source = null;
	} else {
	    throw new NoSuchSourceException();
	}
    }

    public EventRegistration addSinkListener(RemoteEventListener listener,
					      MarshalledObject handback) {
	System.out.println("Adding listener: " + listener);
	listeners.put(listener, handback);
	System.out.println("  listeners size " + listeners.size());
	return new EventRegistration(0L, proxy, null, 0L);
    }

    public void removeSinkListener(RemoteEventListener listener) {
	listeners.remove(listener);
    }

    public void fireNotify(int evtType) {
	Enumeration elmts = listeners.keys();
	seqNum++;
	System.out.println("Fire notify event seq id " + seqNum);
	while (elmts.hasMoreElements()) {
	    RemoteEventListener listener = (RemoteEventListener) elmts.nextElement();
	    MarshalledObject handback = (MarshalledObject) listeners.get(listener);
	    RemoteEvent evt = new RemoteEvent(proxy, evtType, seqNum, handback);
	    System.out.println("Notifying listener " + listener);
	    try {
		listener.notify(evt);
	    } catch(UnknownEventException e) {
		// ??
	    } catch(RemoteException e) {
		// ?
	    }
	}
    }

    class CopyIO extends Thread {

	private SinkImpl sink;
	private ContentSink contentSink;

	CopyIO(SinkImpl sink, InputStream in, Source source) {
	    contentSink = ContentSink.createSink(sink, in, source);
	    this.sink = sink;
	}
	
	public void stopRecord() {
	    if (contentSink != null) {
		contentSink.stop();
	    }
	}

	public void run() {
	    contentSink.record();
	}
    }
}// SinkImpl
