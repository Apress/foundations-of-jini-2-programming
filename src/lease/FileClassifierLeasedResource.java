
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
import com.sun.jini.landlord.LeasedResource;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

public class FileClassifierLeasedResource implements LeasedResource  {
    
    protected Uuid cookie;
    protected LeaseFileClassifier fileClassifier;
    protected long expiration = 0;
    protected String suffix = null;

    public FileClassifierLeasedResource(LeaseFileClassifier fileClassifier,
					String suffix) {
        this.fileClassifier = fileClassifier;
	this.suffix = suffix;
	cookie = UuidFactory.generate();
    }

    public void setExpiration(long newExpiration) {
	this.expiration = newExpiration;
    }

    public long getExpiration() {
	return expiration;
    }
    public Uuid getCookie() {
	return cookie;
    }

    public LeaseFileClassifier getFileClassifier() {
	return fileClassifier;
    }

    public String getSuffix() {
	return suffix;
    } 
} // FileClassifierLeasedResource
