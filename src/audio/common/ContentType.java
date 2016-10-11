
/**
 * ContentType.java
 *
 *
 * Created: Thu Aug 21 09:54:23 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.common;

import java.rmi.*;

public interface ContentType extends Remote { 

    public MimeType getContentType() throws RemoteException;
    public void setContentType(MimeType type) throws RemoteException;
    public MimeType[] getSupportedContentTypes() throws RemoteException;

}// ContentType
