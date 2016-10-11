
package common;

import java.io.Serializable;

/**
 * MutableFileClassifier.java
 *
 *
 * Created: Thu Jun 02 14:14:36 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.EventRegistration;

public interface MutableFileClassifier extends FileClassifier {

    static final public long ADD_TYPE = 1;
    static final public long REMOVE_TYPE = 2;

    /*
     * Add the MIME type for the given suffix.
     * The suffix does not contain '.' e.g. "gif".
     * Overrides any previous MIME type for that suffix
     */
    public void addType(String suffix, MIMEType type)
	throws java.rmi.RemoteException;

    /*
     * Delete the MIME type for the given suffix.
     * The suffix does not contain '.' e.g. "gif".
     * Does nothing if the suffix is not known
     */
    public void removeType(String suffix)
	throws java.rmi.RemoteException;

    public EventRegistration addRemoteListener(RemoteEventListener listener)
	throws java.rmi.RemoteException;


} // MutableFileClasssifier
