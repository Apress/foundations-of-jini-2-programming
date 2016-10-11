package jeri;

import java.rmi.*;
import java.rmi.server.*;

public class RmiExplicitExportDemo implements Remote { 

    public static void main(String[] args) throws Exception { 
	Remote demo = new RmiExplicitExportDemo(); 

	// this exports the RMI stub to the Java runtime
	RemoteStub stub = UnicastRemoteObject.exportObject(demo);

	System.out.println("Proxy is " + stub.toString());
	
	// This application will stay alive until killed by the user,
	// or it does a System.exit()
	// or it unexports the proxy

	// Note that the demo is "apparently" unexported, not the proxy 
	UnicastRemoteObject.unexportObject(demo, true);
    } 
}
