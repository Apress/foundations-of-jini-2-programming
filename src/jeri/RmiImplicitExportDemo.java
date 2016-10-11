package jeri;

import java.rmi.*;
import java.rmi.server.*;

public class RmiImplicitExportDemo extends UnicastRemoteObject implements Remote { 

    public static void main(String[] args) throws Exception { 
	// this exports the RMI stub to the Java runtime
	// a thread is started to keep the stub alive
	new RmiImplicitExportDemo(); 

	System.out.println("Proxy is now exported");

	// this application will then stay alive till killed by the user
    } 

    // An empty constructor is needed for the runtime to construct
    // the proxy stub
    public RmiImplicitExportDemo() throws java.rmi.RemoteException {
    }
}
