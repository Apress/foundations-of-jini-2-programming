package printer;

/**
 * Printer20.java
 *
 *
 * Created: Tue Apr 20 21:47:08 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class Printer20 implements common.Printer {
    
    protected int speed = 0;

    public Printer20() {
	speed = 20;
    }

    public void print(String str) {
	// fake stuff:
	System.out.println("I'm the " + speed + " pages/min printer");
	System.out.println(str);
    }

    public int getSpeed() {
	return speed;
    }
    
} // Printer20
