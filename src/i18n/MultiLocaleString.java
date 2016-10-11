package i18n;

/**
 * MultiLocaleString.java
 *
 *
 * Created: Sun Jun  5 18:03:56 2005
 *
 * @author <a href="mailto:jan.newmarch@infotech.monash.edu.au">Jan Newmarch</a>
 * @version 1.0
 */

import java.util.Vector;
import java.util.Locale;

public class MultiLocaleString {
    private Vector strings = new Vector();
    private Vector locales = new Vector();

    public void add(String s, Locale loc) {
	strings.add(s);
	locales.add(loc);
    }

    public String getLocaleString(Locale loc) {
	for (int n = 0; n < locales.size(); n++) {
	    Locale locale = (Locale) locales.get(n);
	    if (locale.equals(loc)) {
		return (String) strings.get(n);
	    }
	}
	return null;
    }
    
} // MultiLocaleString
