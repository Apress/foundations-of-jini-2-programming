
/**
 * NoSuchSourceException.java
 *
 *
 * Created: Fri Jun 27 09:13:37 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Exception thrown when a sink is asked to remove a source that 
 * it does not know about
 */

public class NoSuchSourceException extends Exception{
    public NoSuchSourceException (){
	
    }
}// NoSuchSourceException
