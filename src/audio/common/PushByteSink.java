
/**
 * PushByteSink.java
 *
 *
 * Created: Fri Jun 27 09:24:07 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.common;

public interface PushByteSink extends Sink {

    void record(byte [] bytes);
}// PushByteSink
