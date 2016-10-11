/**
 *	Copyright (C) Satoshi Konno 2002-2003
 *      Minor changes Jan Newmarch 2004
 */

package clock.clock;

import clock.device.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ClockFrame extends JFrame implements Runnable, WindowListener
{
    private final static String DEFAULT_TITLE = "Sample Clock";
    private ClockDevice clockDev;
    private ClockPane clockPane;

    public ClockFrame(ClockDevice clockDev) {
	this(clockDev, DEFAULT_TITLE);
    }

    public ClockFrame(ClockDevice clockDev, String title)
    {
	super(title);

	this.clockDev = clockDev;
	
	getContentPane().setLayout(new BorderLayout());
	
	clockPane = new ClockPane(clockDev);		
	getContentPane().add(clockPane, BorderLayout.CENTER);
	
	addWindowListener(this);
	
	pack();
	setVisible(true);
    }
    
    public ClockPane getClockPanel()
    {
	return clockPane;
    }
    
    public ClockDevice getClockDevice()
    {
	return clockDev;
    }
    
    ////////////////////////////////////////////////
    //	run	
    ////////////////////////////////////////////////
    
    private Thread timerThread = null;
    
    public void run()
    {
	Thread thisThread = Thread.currentThread();
	
	while (timerThread == thisThread) {
	    // getClockDevice().update();
	    getClockPanel().repaint();
	    try {
		Thread.sleep(1000);
	    }
	    catch(InterruptedException e) {}
	}
    }
    
    public void start()
    {
	// clockDev.start();
	
	timerThread = new Thread(this);
	timerThread.start();
    }
    
    public void stop()
    {
	// clockDev.stop();
	timerThread = null;
    }
    
    ////////////////////////////////////////////////
    //	main
    ////////////////////////////////////////////////
    
    public void windowActivated(WindowEvent e) 
    {
    }
    
    public void windowClosed(WindowEvent e) 
    {
    }
    
    public void windowClosing(WindowEvent e) 
    {
	stop();
	System.exit(0);
    }
    
    public void windowDeactivated(WindowEvent e) 
    {
    }
    
    public void windowDeiconified(WindowEvent e) 
    {
    }
    
    public void windowIconified(WindowEvent e) 
    {
    }
    
    public void windowOpened(WindowEvent e)
    {
    }
}

