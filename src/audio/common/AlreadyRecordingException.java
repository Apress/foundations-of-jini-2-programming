
/**
 * AlreadyRecordingException.java
 *
 *
 * Created: Sat Jul 12 09:59:28 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Exception thrown when <code>record()</code> called on a sink that is
 * already recording and can only record one source at a time
 */

public class AlreadyRecordingException extends Exception {
    public AlreadyRecordingException (){
	
    }
}// AlreadyRecordingException
