/**
 * CDTrack.java
 *
 *
 * Created: Fri Aug 22 09:48:59 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 * 
 */

package audio.common;

import net.jini.entry.AbstractEntry;

/**
 * Information about a CD as stored in CDDB
 */

public class CDTrack extends AbstractEntry {

    public String artist;
    public String title;
    public String trackTitle;
    public int trackNumber;

    public CDTrack(String artist, String title,
		   String trackTitle, int trackNumber) {
	this.artist = artist;
	this.title = title;
	this.trackTitle = trackTitle;
	this.trackNumber = trackNumber;
    }
}
