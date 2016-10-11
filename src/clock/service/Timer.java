/**
 * Timer service
 */
package clock.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface Timer extends Remote {

    public void setTime(Date t) throws RemoteException;

    public Date getTime() throws RemoteException;

    public boolean isValidTime() throws RemoteException;
}

