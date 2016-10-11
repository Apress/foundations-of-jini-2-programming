
/**
 * FileClassifierLeasedResource.java
 *
 *
 * Created: Mon Jun 14 18:04:33 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */
package lease;

import common.LeaseFileClassifier;
import com.sun.jini.lease.landlord.LeasedResource;

public class FileClassifierLeasedResource implements LeasedResource  {
    
    static protected int cookie = 0;
    protected int thisCookie;
    protected LeaseFileClassifier fileClassifier;
    protected long expiration = 0;
    protected String suffix = null;

    public FileClassifierLeasedResource(LeaseFileClassifier fileClassifier,
					String suffix) {
        this.fileClassifier = fileClassifier;
	this.suffix = suffix;
	thisCookie = cookie++;
    }

    public void setExpiration(long newExpiration) {
	this.expiration = newExpiration;
    }

    public long getExpiration() {
	return expiration;
    }
    public Object getCookie() {
	return new Integer(thisCookie);
    }

    public LeaseFileClassifier getFileClassifier() {
	return fileClassifier;
    }

    public String getSuffix() {
	return suffix;
    } 
} // FileClassifierLeasedResource
