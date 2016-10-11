/**
 * AlreadyPlayingException.java
 *
 * Created: Sat Jul 12 09:53:53 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Exception thrown when <code>play()</code> is called on a source that is
 * already playing and cannot play to multiple sinks simultaneously
 */

public class AlreadyPlayingException extends Exception {

}// AlreadyPlayingException
