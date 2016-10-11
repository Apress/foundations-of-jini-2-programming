
/**
 * PullSource.java
 *
 *
 * Created: Tue Sep  9 06:41:02 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.pull;

import audio.common.*;
import java.io.*;

public interface PullSource extends Source {
    
    public InputStream getInputStream();

}// PullSource
