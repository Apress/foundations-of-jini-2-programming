package complex;

import net.jini.lookup.entry.Location;

/**
 * DistanceImpl.java
 *
 *
 * Created: Wed Apr 21 15:24:15 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class DistanceImpl implements common.Distance {
    
    public DistanceImpl() {
	
    }

    /**
     * A very naive distance metric
     */
    public int getDistance(Location loc1, Location loc2) {
	int room1, room2;
	try {
	    room1 = Integer.parseInt(loc1.room);
	    room2 = Integer.parseInt(loc2.room);
	} catch(Exception e) {
	    return -1;
	}
	int value = room1 - room2;
	return (value > 0 ? value : -value);
    }
    
} // DistanceImpl
