package jeri;

import java.rmi.*;
import net.jini.export.*; 
import net.jini.iiop.IiopExporter;

public class ExportIiopDemo implements Remote { 

    public static void main(String[] args) throws Exception {
	
	Exporter exporter = new IiopExporter();

	// export an object of this class
	Remote proxy = exporter.export(new ExportIiopDemo()); 
	System.out.println("Proxy is " + proxy.toString()); 
	
	// now unexport it once finished
	exporter.unexport(true); 
    } 
}
