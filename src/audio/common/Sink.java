
/**
 * Sink.java
 *
 *
 * Created: Fri Jun 27 09:09:55 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

import java.rmi.RemoteException;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import java.rmi.MarshalledObject;

/**
 * A sink for audio
 */

public interface Sink extends java.rmi.Remote {

    int STOP = 1;

    void record() throws
	        RemoteException,
	        AlreadyRecordingException;
    void stop() throws 
                RemoteException,
	        NotRecordingException;
    void addSource(Source src) throws
                RemoteException,
                TooManySourcesException,
                IncompatableSourceException;
    void removeSource(Source src) throws
                RemoteException,
                NoSuchSourceException;
    EventRegistration addSinkListener(RemoteEventListener listener, 
				      MarshalledObject handback) throws
	        RemoteException;
    void removeSinkListener(RemoteEventListener listener) throws
	        RemoteException,
		NoSuchListenerException;

}// Sink
