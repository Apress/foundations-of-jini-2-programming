
/**
 * RemoteFileSystemView.java
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

public class RemoteFileSystemView extends FileSystemView {

    public File createNewFolder(File dir) {
	return null;
    }

    public File[] getFiles(File dir, boolean useFileHiding) {
	return null;
    }

    public File getDefaultDirectory() {
	return null;
    }

    public File getHomeDirectory() {
	return null;
    }
}
