
/**
 * FileServerImpl.java
 *
 *
 * Created: Tue Oct 26 10:23:03 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package socket;

import java.net.*;
import java.io.*;

public class FileServerImpl extends Thread {
    
    protected ServerSocket listenSocket;

    public FileServerImpl() {
	try {
	    listenSocket = new ServerSocket(FileClassifierProxy.PORT);
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }

    public void run() {
	try {
	    while(true) {
		Socket clientSocket = listenSocket.accept();
		new Connection(clientSocket).start();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
} // FileServerImpl

class Connection extends Thread {

    protected Socket client;

    public Connection(Socket clientSocket) {
	client = clientSocket;
    }

    public void run() {
	String contentType = null;
	String subType = null;

	try {
	    InputStreamReader inputReader = 
		new InputStreamReader(client.getInputStream());
	    BufferedReader reader = new BufferedReader(inputReader);
	    OutputStreamWriter outputWriter = 
		new OutputStreamWriter(client.getOutputStream());
	    BufferedWriter writer = new BufferedWriter(outputWriter);

	    String fileExtension = reader.readLine();

	    if (fileExtension.equals("gif")) {
		contentType = "image";
		subType = "gif";
	    } else if (fileExtension.equals("txt")) {
		contentType = "text";
		subType = "plain";
	    } // etc

	    if (contentType == null) {
		writer.write("null");
	    } else {
		writer.write(contentType);
		writer.newLine();
		writer.write(subType);
	    }
	    writer.newLine();
	    writer.close();
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }
}
