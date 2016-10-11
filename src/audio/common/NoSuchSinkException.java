
/**
 * NoSuchSinkException.java
 *
 *
 * Created: Fri Jun 27 09:14:04 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Exception thrown when a source is asked to remove a sink that 
 * it does not know about
 */

public class NoSuchSinkException extends Exception{
    public NoSuchSinkException (){
	
    }
}// NoSuchSinkException
