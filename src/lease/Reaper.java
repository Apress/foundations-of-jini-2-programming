package lease;

/**
 * Reaper.java
 *
 *
 * Created: Fri Feb 17 02:21:54 2006
 *
 * @author <a href="mailto:jan.newmarch@infotech.monash.edu.au">Jan Newmarch</a>
 * @version 1.0
 */

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.rmi.RemoteException;

/**
 * Every minute, scan list of resources, remove those that
 * have expired and cancel their things
 */

public class Reaper extends Thread {

    private Map leasedResources;

    public Reaper(Map leasedResources) {
	this.leasedResources = leasedResources;
    } // Reaper constructor
    
    public void run() {
	while (true) {
	    try {
		Thread.sleep(10*1000L);
	    } catch (InterruptedException e) {
		// ignore
	    }
	    Set keys = leasedResources.keySet();
	    Iterator iter = keys.iterator();
	    System.out.println("Reaper running");
	    while (iter.hasNext()) {
		Object key = iter.next();
		FileClassifierLeasedResource res = (FileClassifierLeasedResource) leasedResources.get(key);
		long expires = res.getExpiration() - System.currentTimeMillis();

		if (expires < 0) {
		    leasedResources.remove(key);
		    try {
			res.getFileClassifier().removeType(res.getSuffix());
		    } catch (RemoteException e) {
			// ignore
		    }
		    
		}
	    }
	}
    }
    
} // Reaper
