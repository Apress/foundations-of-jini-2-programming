package starter;

import rmi.FileClassifierImpl;

import com.sun.jini.start.ServiceProxyAccessor;
import com.sun.jini.start.LifeCycle;

import net.jini.config.*; 
import net.jini.export.*; 
import java.rmi.Remote;
import java.rmi.RemoteException;

public class FileClassifierStarterImpl extends FileClassifierImpl
    implements ServiceProxyAccessor {
    Remote proxy;

    public FileClassifierStarterImpl(String[] configArgs, LifeCycle lifeCycle)
	throws RemoteException {
	super();

	try {
	    // get the configuration (by default a FileConfiguration) 
	    Configuration config = ConfigurationProvider.getInstance(configArgs); 
	    
	    // and use this to construct an exporter
	    Exporter exporter = (Exporter) config.getEntry( "FileClassifierServer", 
							    "exporter", 
							    Exporter.class); 
	    // export an object of this class
	    proxy = exporter.export(this);
	} catch(Exception e) {
	    // empty
	}
    }

    public Object getServiceProxy() {
	return proxy;
    }
}
