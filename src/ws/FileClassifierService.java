

/**
 * FileClassifierService.java
 *
 *
 * Created: Mar 22, 2006
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierService {

    public String getMIMEType(String fileName) {
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".mpg")) {
            return "video/mpeg";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else
            // fill in lots of other types,
            // but eventually give up and
            return "";
    }

    public FileClassifierService() {
	// empty
    }
    
} // FileClassifierService
