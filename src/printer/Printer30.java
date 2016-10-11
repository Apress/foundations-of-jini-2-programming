package printer;

/**
 * Printer30.java
 *
 *
 * Created: Tue Apr 20 21:44:12 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class Printer30 implements common.Printer {

    protected int speed = 0;

    public Printer30() {
	speed = 30;
    }

    public void print(String str) {
	// fake stuff:
	System.out.println("I'm the " + speed + " pages/min printer");
	System.out.println(str);
    }

    public int getSpeed() {
	return speed;
    }
    
} // Printer30
