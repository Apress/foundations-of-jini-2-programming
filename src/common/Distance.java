package common;

import net.jini.lookup.entry.Location;

/**
 * Distance.java
 *
 *
 * Created: Wed Apr 21 13:12:35 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public interface Distance extends java.io.Serializable {
    
    int getDistance(Location loc1, Location loc2);

} // Distance
