
package basic;

import net.jini.core.discovery.LookupLocator;

/**
 * InvalidLookupLocator.java
 *
 *
 * Created: Tue Mar  9 14:14:06 1999
 *
 * @author Jan Newmarch
 * @version 1.1
 *    moved to package basic
 */

public class InvalidLookupLocator  {

    static public void main(String argv[]) {
	new InvalidLookupLocator();
    }
   
    public InvalidLookupLocator() {
	LookupLocator lookup;

	// this is valid
	try {
	    lookup = new LookupLocator("jini://localhost");
	    System.out.println("First lookup creation succeeded");
	} catch(java.net.MalformedURLException e) {
	    System.err.println("First lookup failed: " + e.toString());
	}

	// this is probably an invalid URL, 
	// but the URL is syntactically okay
	try {
	    lookup = new LookupLocator("jini://ABCDEFG.org");
	    System.out.println("Second lookup creation succeeded");
	} catch(java.net.MalformedURLException e) {
	    System.err.println("Second lookup failed: " + e.toString());
	}

	// this IS a malformed URL, and should throw an exception
	try {
	    lookup = new LookupLocator("A:B:C://ABCDEFG.org");
	    System.out.println("Third lookup creation succeeded");
	} catch(java.net.MalformedURLException e) {
	    System.err.println("Third lookup failed: " + e.toString());
	}

	// this is valid, but no check is made anyway
	lookup = new LookupLocator("localhost", 80);
	System.out.println("Fourth lookup creation succeeded");
    }
    
} // InvalidLookupLocator
