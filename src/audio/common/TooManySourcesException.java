
/**
 * TooManySourcesException.java
 *
 *
 * Created: Fri Jun 27 09:11:11 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Exception thrown by a sink when a source is added beyond
 * it's capacity to handle
 */

public class TooManySourcesException extends Exception{
    public TooManySourcesException (){
	
    }
}// TooManySourcesException
