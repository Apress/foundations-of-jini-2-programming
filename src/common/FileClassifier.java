package common;

/**
 * FileClassifier.java
 *
 *
 * Created: Wed Mar 17 14:14:36 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved to package common
 */

public interface FileClassifier {
    
    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException;
    
} // FileClasssifier
