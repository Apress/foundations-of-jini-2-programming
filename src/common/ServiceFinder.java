/**
 * ServiceFinder.java
 *
 *
 * Created: Wed Apr 18 22:19:07 2001
 *
 * @author <a href="mailto:jan.newmarch@infotech.monash.edu.au"Jan Newmarch</a>
 * @version 1.0
 */
package common;

import java.net.URL;
import java.rmi.RemoteException;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;

public interface ServiceFinder {

    public ServiceItem[] getServices()
          throws RemoteException;
}// ServiceFinder
