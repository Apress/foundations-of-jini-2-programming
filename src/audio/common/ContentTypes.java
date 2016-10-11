
/**
 * ContentTypes.java
 *
 *
 * Created: Sat Jun 28 01:45:16 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.common;

import javax.media.protocol.ContentDescriptor;

public interface ContentTypes {

    boolean setContentType(ContentDescriptor desc);
    ContentDescriptor[] getContentTypes();
}// ContentTypes
