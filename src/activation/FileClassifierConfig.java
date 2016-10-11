
package activation;

import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.activation.ActivationExporter;

import net.jini.jrmp.JrmpExporter;

import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;
import net.jini.export.ProxyAccessor;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

import common.MIMEType;
import common.FileClassifier;
import rmi.RemoteFileClassifier;
import java.rmi.Remote;

/**
 * FileClassifierConfig.java
 *
 * Created: Wed Mar 17 14:22:13 1999
 *
 * @author Jan Newmarch
 * @version 2.0
 *    modified for Jini 2.0
 */

public class FileClassifierConfig implements RemoteFileClassifier,
					   ProxyAccessor {

    private Remote proxy;

    public MIMEType getMIMEType(String fileName) 
	throws java.rmi.RemoteException {
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
            return new MIMEType(null, null);
    }


    public FileClassifierConfig(ActivationID activationID, MarshalledObject data)  
	throws java.rmi.RemoteException {

	// The marshalled object should be an array of strings
	// holding a configuration
	String[] args = null;
	try {
	    args = (String[]) data.get();
	} catch(Exception e) {
	    // empty
	}

	Exporter defaultExporter = 
	    new ActivationExporter(activationID,
			 new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
					       new BasicILFactory(),
					       false, true));
	Exporter exporter = defaultExporter;

	try {
	    Configuration config = ConfigurationProvider.getInstance(args); 

	    exporter = (Exporter) config.getEntry( "JeriExportDemo", 
						   "exporter", 
						   Exporter.class); 
	} catch(ConfigurationException e) {
	    // empty
	}

	proxy = (Remote) exporter.export(this);
    }

    // Implementation for ProxyAccessor
    public Object getProxy() {
	return proxy;
    }
} // FileClassifierImpl
