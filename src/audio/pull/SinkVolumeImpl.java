
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
import audio.aumix.*;

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

public class SinkVolumeImpl extends SinkImpl implements VolumeControl, Remote {

    /* 
     * Volume control
     */
    public void setVolume(int vol) {
	Aumix.setOutputVolume(vol);
    }

    public int getVolume() {
	return Aumix.getVolume();
    }

    public int getMaxVolume() {
	return Aumix.getMaxVolume();
    }
}// SinkImpl
