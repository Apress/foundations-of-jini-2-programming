
/**
 * SinkImpl.java
 *
 *
 * Created: Sat Jun 28 05:42:34 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.filesink;

import audio.common.*;
import audio.transport.*;
import audio.aumix.*;
import audio.pull.*;

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
import javax.swing.filechooser.FileSystemView;

import common.*;

public class SinkImpl implements FileSink, Remote {
    private Source source;
    private boolean stopped;
    private CopyIO copyIO;
    private Hashtable listeners = new Hashtable();
    private int seqNum = 0;
    private Remote proxy;
    private File sinkFile;
    private FileSystemView fileView;

    public SinkImpl() {
	fileView = FileSystemView.getFileSystemView();
    }

    public void setProxy(Remote proxy) {
	this.proxy = proxy;
    }

    public boolean setFile(File sinkFile) {
	this.sinkFile = sinkFile;
	System.out.println("File sink set to " + sinkFile);
	return true;
    }

    public void record() throws RemoteException, AlreadyRecordingException {
	TransportSink transportSink = null;
	InputStream in = null;

	if (source == null) {
	    return;
	}

	if (source instanceof HttpSource) {
	    transportSink = new HttpSinkImpl((HttpSource) source);
	    in = transportSink.getInputStream();
	} else if (source instanceof TcpSource) {
	    System.out.println("Setting up Tcp sink");
	    transportSink = new TcpSinkImpl((TcpSource) source);
	    in = transportSink.getInputStream();
	    System.out.println("Got tcp source input stream " + in);
	} else if (source instanceof PullSource) {
	    in = ((PullSource) source).getInputStream();
	} else {
	    return;
	}


	if ((copyIO != null) && ( ! stopped)) {
	    throw new AlreadyRecordingException();
	}

	stopped = false;

	if (in  == null) {
	    System.out.println("Couldn't get input stream");
	    stopped = true;
	    return;
	}

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
	IncompatableSourceException {
	//if (Transport.compatable(source, this)) {
	this.source = source;
	    //} else {
	    //throw new IncompatableSourceException();
	    //}
    }

    public void removeSource(Source source) throws
	RemoteException,
	NoSuchSourceException {
    }

    public EventRegistration addSinkListener(RemoteEventListener listener,
					      MarshalledObject handback) {
	System.out.println("Adding listener: " + listener);
	listeners.put(listener, handback);
	return new EventRegistration(0L, proxy, null, 0L);
    }

    public void removeSinkListener(RemoteEventListener listener) {
	listeners.remove(listener);
    }

    public void fireNotify(int evtType) {
	Enumeration elmts = listeners.keys();
	System.out.println("Fire notify");
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

	private SinkImpl sink;
	private ContentSink contentSink;

	CopyIO(SinkImpl sink, InputStream in, Source source) {
	    contentSink = ContentSink.createSink(sink, in, source, sinkFile);
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
	}
    }

    // For remote file browsing
    public File[] getFiles(File dir, boolean useFileHiding) {
	return fileView.getFiles(dir, useFileHiding);
    }

    public File getHomeDirectory() {
	return fileView.getHomeDirectory();
    }

    public File getDefaultDirectory() {
	return fileView.getDefaultDirectory();
    }

    public File createNewFolder(File dir) throws java.io.IOException {
	return fileView.createNewFolder(dir);
    }

}// SinkImpl
