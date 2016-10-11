
/**
 * UIFactory.java
 *
 *
 * Created: Sun Nov 14 18:07:12 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package ui;

import net.jini.entry.AbstractEntry;
import java.util.Map;
import java.util.HashMap;

public class UIFactory extends AbstractEntry {
    public String name;
    public String role;
    public Map properties;

    public UIFactory() {
	this(null, null, null);
    }

    public UIFactory(String name, String role) {
	this(name, role, null);
    }

    public UIFactory(String name, String role, Map properties) {
	this.name = name;
	this.role = role;
	if (properties == null) {
	    this.properties = null;
	} else {
	    this.properties = new HashMap(properties);
	}
    }

    public Object getUI(Object service) throws UICreationException {
	throw new UICreationException();
    }
} // UIFactory
