package clock.service;

import java.util.Date;
import net.jini.core.event.*;
import java.util.Vector;
import java.rmi.RemoteException;

public class ComputerTimer implements Timer {

    public ComputerTimer() {
    }

    public void setTime(Date t) {
	// void
    }

    public Date getTime() {
	return new Date();
    }

    public boolean isValidTime() {
	return true;
    }
}
