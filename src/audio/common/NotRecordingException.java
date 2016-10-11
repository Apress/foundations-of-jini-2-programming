
/**
 * NotRecordingException.java
 *
 *
 * Created: Sat Jul 12 10:00:03 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Exception thrown when a sink is asked to stop recording and
 * is not currently recording
 */

public class NotRecordingException extends Exception {
    public NotRecordingException (){
	
    }
}// NotRecordingException
