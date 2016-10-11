
package clock.clock;

import clock.device.*;
import clock.service.*;

public class TickerClock {
    
    public static void main(String args[]) 
    {
	ClockDevice clockDev = new ClockDevice();

	clockDev.setTimer(new TickerTimer());

	ClockFrame clock;
	if (args.length > 0) {
	    clock= new ClockFrame(clockDev, args[0]);
	} else {
	    clock = new ClockFrame(clockDev);
	}
	clock.start();
    }
}
