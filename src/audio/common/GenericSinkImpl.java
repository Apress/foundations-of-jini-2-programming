
/**
 * GenericSinkImpl.java
 *
 *
 * Created: Sat Jun 28 05:42:34 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

import transport.*;
import aumix.*;

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

/**
 * obsolete
 */

public class GenericSinkImpl implements Sink, VolumeControl, Remote {
    private Source source;
    private boolean stopped;
    private CopyIO copyIO;
    private Hashtable listeners = new Hashtable();
    private int seqNum = 0;
    private Remote proxy;
    // private MimeType contentType = null;

    public GenericSinkImpl() {

    }

    public void setProxy(Remote proxy) {
	this.proxy = proxy;
    }

    public void record() throws RemoteException, AlreadyRecordingException {
	URL url = null;

	if ((copyIO != null) && ( ! stopped)) {
	    throw new AlreadyRecordingException();
	}

	InputStream in = null;

	if (source == null) {
	    return;
	}

	// ask the source what the type is
	/*
	contentType = null;
	if (source instanceof ContentType) {
	    try {
		contentType = ((ContentType) source).getContentType();
		System.out.println("Source content mime type: " + contentType);
		    } catch(RemoteException e) {
		e.printStackTrace();
	    }
	} else {
	    System.out.println("Source doesn't implement ContentType");
	}
	*/

	in = Transport.createTransport().getInputStream(source);

	/*
	// source couldn't tell us, use file extension instead
	if (contentType == null) {
	    url = source.getUrl();
	    System.out.println("Getting from " + url.toString());
	    String urlStr = url.toString();
	    if (urlStr.endsWith(".wav")) {
		contentType = MimeType.WAV;
	    } else if (urlStr.endsWith(".mp3")) {
		contentType = MimeType.MPEG;
	    } else if (urlStr.endsWith(".ogg")) {
		contentType = MimeType.OGG_VORBIS;
	    } else {
		contentType = null;
	    }
	}
	*/

	stopped = false;

	/*
	URLConnection connect = null;
	try {
	    connect = url.openConnection();
	    connect.setDoInput(true);
	    connect.setDoOutput(false);
	    in = connect.getInputStream();
	} catch (IOException e) {
	    stopped = true;
	    // ignore
	    System.err.println("Getting in stream " + e.toString());
	    return;
	}
	*/
	if (in  == null) {
	    System.out.println("Couldn't get input stream");
	    stopped = true;
	    return;
	}

	copyIO = new CopyIO(this, in, contentType);
	copyIO.start();
	System.out.println("Play returning");
    }

    public void stop() throws RemoteException {
	stopped = true;
	copyIO.stopRecord();
	fireNotify(Sink.STOP);
	System.out.println("Stopped");
    }

    public void addSource(Source source) throws
	IncompatableSourceException {
	if (Transport.compatable(source, this)) {
	    this.source = source;
	} else {
	    throw new IncompatableSourceException();
	}
    }

    public void removeSource(Source source) throws
	RemoteException,
	NoSuchSourceException {
    }

    public EventRegistration addSinkListener(RemoteEventListener listener,
					      MarshalledObject handback) {
	listeners.put(listener, handback);
	try {
	    SourceSink ss = (SourceSink) handback.get();
	    System.out.println("SS source " + ss.sources + " sink " + ss.sink);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return new EventRegistration(0L, proxy, null, 0L);
    }

    public void removeSinkListener(RemoteEventListener listener) {
	listeners.remove(listener);
    }

    public void fireNotify(int evtType) {
	Enumeration elmts = listeners.keys();
	while (elmts.hasMoreElements()) {
	    RemoteEventListener listener = (RemoteEventListener) elmts.nextElement();
	    MarshalledObject handback = (MarshalledObject) listeners.get(listener);
	    RemoteEvent evt = new RemoteEvent(proxy, evtType, seqNum++, handback);
	    System.out.println("Updating listener " + listener);
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

	private Sink sink;
	private ContentSink contentSink;

	CopyIO(Sink sink, InputStream in, MimeType type) {
	    contentSink = ContentSink.createSink(in, type);
	    this.sink = sink;
	}
	
	public void stopRecord() {
	    if (contentSink != null) {
		contentSink.stop();
		// stopped = true;
	    }
	}

	public void run() {
	    contentSink.record();
	    try {
		sink.stop();
	    } catch(RemoteException e) {
		e.printStackTrace();
	    }
	}
    }

    /* 
     * Volume control
     */
    public void setVolume(int vol) {
	Aumix.setVolume(vol);
    }

    public int getVolume() {
	return Aumix.getVolume();
    }

    public int getMaxVolume() {
	return Aumix.getMaxVolume();
    }
}// GenericSinkImpl
