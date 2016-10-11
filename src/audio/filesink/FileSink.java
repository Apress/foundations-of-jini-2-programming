
/**
 * FileSink.java
 *
 *
 * Created: Tue Oct 14 05:46:12 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.filesink;

import java.io.File;
import java.rmi.RemoteException;

public interface FileSink extends audio.common.Sink {

    public boolean setFile(File sinkFile) throws RemoteException;

    /**
     * methods to browse the file system
     * Based on FileSystemView from JFileChooser
     */

    public File[] getFiles(File dir, boolean useFileHiding) throws RemoteException;
    public File getHomeDirectory() throws RemoteException;
    public File getDefaultDirectory() throws RemoteException;
    public File createNewFolder(File dir) throws RemoteException, java.io.IOException;

}// FileSink
