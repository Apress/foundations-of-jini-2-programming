package complex;

import java.io.*;

/**
 * Size.java
 *
 *
 * Created: Mon Apr 12 14:49:20 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class Size {
    
    public Size() {
	NameEntryImpl1 impl = new NameEntryImpl1();
	ByteArrayOutputStream arr = new ByteArrayOutputStream();
	ObjectOutputStream out;
	try {
	    out = new ObjectOutputStream(arr);
	    out.writeObject(impl);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.out.println("size: " +
			   arr.toByteArray().length);
    }
    
    public static void main(String[] args) {
	new Size();
    }
    
} // Size
