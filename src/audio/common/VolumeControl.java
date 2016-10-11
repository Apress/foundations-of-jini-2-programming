
/**
 * VolumeControl.java
 *
 *
 * Created: Thu Aug 21 05:51:26 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.common;

/**
 * Interface for an object that can its volume controlled
 */

public interface VolumeControl extends java.rmi.Remote {

    public void setVolume(int vol) throws java.rmi.RemoteException;
    public int getVolume() throws java.rmi.RemoteException;
    public int getMaxVolume() throws java.rmi.RemoteException;

}// VolumeControl
