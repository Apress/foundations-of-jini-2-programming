
package ws;

import common.MIMEType;
import common.FileClassifier;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import javax.xml.namespace.QName;

/**
 * FileClassifierImpl.java
 *
 *
 * Created: Mar 22, 2006
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class FileClassifierImpl implements RemoteFileClassifier {
    
    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {

	try {
	    String endpoint =
		"http://localhost:8080/axis/FileClassifierService.jws";
	    
	    Service  service = new Service();
	    Call     call    = (Call) service.createCall();
	    
	    call.setTargetEndpointAddress( new java.net.URL(endpoint) );
	    call.setOperationName(new QName("http://soapinterop.org/", "getMIMEType"));
	    
	    String ret = (String) call.invoke( new Object[] { fileName } );
	    return new MIMEType(ret);
	} catch (Exception e) {
	    throw new RemoteException("SOAP failure", e);
	}
    }


    public FileClassifierImpl() throws java.rmi.RemoteException {
	// empty constructor required by RMI
    }
    
} // FileClassifierImpl
