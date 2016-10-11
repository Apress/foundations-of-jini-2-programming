package ws;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import javax.xml.namespace.QName;
import ws.MIMEType;

public class TestWSClient {
    public static void main(String [] args) {
	try {
	    String endpoint =
		"http://localhost:8080/axis/FileClassifierService.jws";
	    
	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName(new QName("http://soapinterop.org/", "getMIMEType"));
	    
	    String ret = (String) call.invoke( new Object[] { "file.txt" } );
	    
	    System.out.println("Type of file 'file.txt' is " + ret);
	} catch (Exception e) {
	    System.err.println(e.toString());
	}
    }
}