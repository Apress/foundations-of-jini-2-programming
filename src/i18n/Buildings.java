package i18n;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Buildings extends Remote {
    
    Length getHeight(String buildingName) throws RemoteException;
}
