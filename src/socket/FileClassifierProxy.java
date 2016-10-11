
package socket;

import common.FileClassifier;
import common.MIMEType;
import java.net.Socket;

import java.io.Serializable;
import java.io.IOException;
import java.rmi.Naming;

import java.io.*;

/**
 * FileClassifierProxy
 *
 *
 * Created: Thu Oct 25 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierProxy implements FileClassifier, Serializable {

    static public final int PORT = 2981;
    protected String host;

    public FileClassifierProxy(String host) {
	this.host = host;
    }

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
	// open a connection to the service on port XXX
	int dotIndex = fileName.lastIndexOf('.');
	if (dotIndex == -1 || dotIndex + 1 == fileName.length()) {
	    // can't find suitable index
	    return null;
	}
	String fileExtension = fileName.substring(dotIndex + 1);

	// open a client socket connection
	Socket socket = null;
	try {
	     socket = new Socket(host, PORT);
	} catch(Exception e) {
	    return null;
	}

	String type = null;
	String subType = null;

	/* 
	 * protocol:
	 * Write: file extension
	 * Read: "null" + '\n' 
         *       type + '\n' + subtype + '\n'
	 */
	try {
	    InputStreamReader inputReader = 
		new InputStreamReader(socket.getInputStream());
	    BufferedReader reader = new BufferedReader(inputReader);
	    OutputStreamWriter outputWriter = 
		new OutputStreamWriter(socket.getOutputStream());
	    BufferedWriter writer = new BufferedWriter(outputWriter);

	    writer.write(fileExtension);
	    writer.newLine();
	    writer.flush();

	    type = reader.readLine();
	    if (type.equals("null")) {
		return null;
	    }
	    subType = reader.readLine();
	} catch(IOException e) {
	    return null;
	}
	// and finally
	return new MIMEType(type, subType);
    }
} // FileClassifierProxy
