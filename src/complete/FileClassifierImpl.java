
package complete;

import common.MIMEType;
import common.FileClassifier;

/**
 * FileClassifierImpl.java
 *
 *
 * Created: Wed Mar 17 14:22:13 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved to package complete from option2
 */

public class FileClassifierImpl implements FileClassifier, java.io.Serializable {

    public MIMEType getMIMEType(String fileName) {
        if (fileName.endsWith(".gif")) {
            return new MIMEType("image", "gif");
        } else if (fileName.endsWith(".jpeg")) {
            return new MIMEType("image", "jpeg");
        } else if (fileName.endsWith(".mpg")) {
            return new MIMEType("video", "mpeg");
        } else if (fileName.endsWith(".txt")) {
            return new MIMEType("text", "plain");
        } else if (fileName.endsWith(".html")) {
            return new MIMEType("text", "html");
        } else
            // fill in lots of other types,
            // but eventually give up and
            return null;
    }


    public FileClassifierImpl() {
	// empty
    }
    
} // FileClassifierImpl
