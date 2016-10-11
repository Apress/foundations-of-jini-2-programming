package common;

import java.io.Serializable;

/**
 * Printer.java
 *
 *
 * Created: Tue Apr 20 21:16:22 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public interface Printer extends Serializable {
    
    public void print(String str);
    public int getSpeed();

} // Printer
