
/**
 * ComparableServiceItem.java
 *
 *
 * Created: Wed Jul 16 07:36:24 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

import net.jini.core.lookup.ServiceID;
import net.jini.lookup.entry.*;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.entry.Entry;

/**
 * Extends ServiceItem for A/V clients that will have
 * several services and want to sort them into order
 * (e.g. a SortedList view of the services). It looks for
 * entries such as CDTrack to compare to other sources
 */

public class ComparableServiceItem extends ServiceItem implements Comparable {
    private String compareStr;
    
    public ComparableServiceItem(ServiceItem item) {
	super(item.serviceID, item.service, item.attributeSets);
	
	if (attributeSets != null) {
	    for (int n = 0; n < attributeSets.length; n++) {
		if (attributeSets[n] instanceof CDTrack) {
		    CDTrack track = (CDTrack) attributeSets[n];
		    String trackPrefix;
		    if (track.trackNumber <= 9) {
			trackPrefix = "0";
		    } else {
			trackPrefix = "";
		    }
		    compareStr = track.artist + " / " 
			+ track.title + " " + trackPrefix + track.trackNumber;
		    break;
		} else if (attributeSets[0] instanceof Name) {
		    compareStr = ((Name) attributeSets[0]).name;
		    break;
		}
	    }
	}
 
    }
    
    public int compareTo(Object obj) {
	ComparableServiceItem item = (ComparableServiceItem) obj;
	
	if (serviceID.equals(item.serviceID)) {
	    System.out.println("Equal serviceID");
	    return 0;
	}
	



	if ((compareStr != null) &&
	    (item.attributeSets != null) &&
	    (item.attributeSets.length != 0) &&
	    (item.attributeSets[0] instanceof Name)) {
	    // System.out.println("Comparing on name");
	    String name = ((Name) item.attributeSets[0]).name;
	    return compareStr.compareTo(name);
	}
	
	System.out.println("Comparing on serviceID");
	if (serviceID.getMostSignificantBits() >= 
	    item.serviceID.getMostSignificantBits()) {
	    return 1;
	} else {
	    return -1;
	}
    }
    
    public boolean equals(Object item) {
	System.out.println("Testing equals");
	return (compareTo(item) == 0);
    }

    public String toString() {
	String ret;
	Entry[] entries = attributeSets;
	ret = service.toString();
	if (entries != null) {
	    for (int n = 0; n < entries.length; n++) {
		if (entries[n] instanceof Name) {
		    ret = ((Name) entries[n]).name;
		    break;
		}
	    }
	}
	return ret;
    }
}
