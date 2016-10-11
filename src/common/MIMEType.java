
package common;

import java.io.Serializable;

/**
 * MIMEType.java
 *
 *
 * Created: Wed Mar 17 14:17:32 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved to package common
 */

public class MIMEType implements Serializable {

    /**
     * A MIME type is made up of 2 parts
     * contentType/subtype
     */    
    private String contentType;
    private String subType;

    public MIMEType() {
        // empty constructor required just in case
        // we want to use this as a Java Bean
    }

    public MIMEType(String type) {
        int slash = type.indexOf('/');
        contentType = type.substring(0, slash-1);
        subType = type.substring(slash+1, type.length());
    }
    
    public MIMEType(String contentType, String subType) {
        this.contentType = contentType;
        this.subType = subType;
    }

    public String toString() {
        return contentType + "/" + subType;
    }

    /** 
     * Accessors/setters
     */
    public String getContentType() {
	return contentType;
    }

    public void setContentType(String type) {
	contentType = type;
    }

    public String getSubType() {
	return subType;
    }

    public void setSubType(String type) {
	subType = type;
    }
} // MIMEType



