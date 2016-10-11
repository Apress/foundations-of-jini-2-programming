
/**
 * HTMLFileClassifierFactory.java
 *
 *
 * Created: Thu Feb 10 07:02:44 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package ui;

import net.jini.lookup.ui.factory.HTMLFactory;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceID;
import java.net.URL;

public class HTMLFileClassifierFactory implements HTMLFactory {
    
    public URL getURL(Object roleObj) {
	ServiceItem item= (ServiceItem) roleObj;
	ServiceID id = item.serviceID;
	try {
	    return new URL("http://localhost/cgi-bin/FileClassifier.pl?serviceID=" + id);
	} catch(Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
    
} // HTMLFileClassifierFactory
