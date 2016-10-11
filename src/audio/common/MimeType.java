
package audio.common;

import java.io.Serializable;

/**
 * MIMEType.java
 *
 *
 * Created: Wed Mar 17 14:17:32 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved to package audio.common
 *
 * defunct
 */

public class MimeType implements Serializable {

    /**
     * A MIME type is made up of 2 parts
     * contentType/subtype
     */    
    protected String contentType;
    protected String subtype;

    public static final MimeType WAV = new MimeType("audio/wav");
    public static final MimeType MPEG = new MimeType("audio/mpeg");
    public static final MimeType OGG_VORBIS = new MimeType("application/x-ogg");

    public MimeType(String type) {
        int slash = type.indexOf('/');
        contentType = type.substring(0, slash);
        subtype = type.substring(slash+1, type.length());
    }
    
    public MimeType(String contentType, String subtype) {
        this.contentType = contentType;
        this.subtype = subtype;
    }

    public String toString() {
        return contentType + "/" + subtype;
    }

    public boolean equals(Object obj) {
	if ( ! (obj instanceof MimeType)) {
	    return false;
	}
	return toString().equals(obj.toString());
    }
} // MimeType



