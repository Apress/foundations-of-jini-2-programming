
/**
 * PullByteSource.java
 *
 *
 * Created: Fri Jun 27 09:23:17 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.transport;

import audio.common.*;

public interface PullByteSource extends Source{

    byte [] playBytes();
}// PullByteSource
