
/**
 * Source.java
 *
 *
 * Created: Fri Jun 27 08:35:55 2003
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
 * A source for A/V data
 */

public interface Source extends java.rmi.Remote {

    int STOP = 1;

    void play() throws 
                RemoteException,
	        AlreadyPlayingException;
    void stop() throws 
                RemoteException,
                NotPlayingException;
    void addSink(Sink sink) throws
                RemoteException,
                TooManySinksException,
                IncompatableSinkException;
    void removeSink(Sink sink) throws
                RemoteException,
                NoSuchSinkException;
    EventRegistration addSourceListener(RemoteEventListener listener,
					MarshalledObject handback) throws
					    RemoteException;
}// Source
