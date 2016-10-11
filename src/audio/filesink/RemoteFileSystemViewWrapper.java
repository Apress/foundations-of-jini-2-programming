
/**
 * RemoteFileSystemViewWrapper.java
 *
 *
 * Created: Sat Oct  4 06:19:19 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.filesink;

import javax.swing.filechooser.*;
import java.io.*;
import java.rmi.RemoteException;

public class RemoteFileSystemViewWrapper extends FileSystemView {
    private FileSink sink;

    public RemoteFileSystemViewWrapper(FileSink sink) {
	this.sink = sink; 
    }

    public File createNewFolder(File dir) throws IOException {
	try {
	    return sink.createNewFolder(dir);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public File[] getFiles(File dir, boolean useFileHiding) {
	try {
	    return sink.getFiles(dir, useFileHiding);
	} catch(RemoteException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public File getDefaultDirectory() {
	try {
	    return sink.getDefaultDirectory();
	} catch(RemoteException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public File getHomeDirectory() {
	try {
	return sink.getHomeDirectory();
	} catch(RemoteException e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
