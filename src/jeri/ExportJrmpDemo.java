package jeri;

import java.rmi.*; 
import net.jini.export.*; 
import net.jini.jrmp.JrmpExporter;

public class ExportJrmpDemo implements Remote { 

    public static void main(String[] args) throws Exception { 

	Exporter exporter = new JrmpExporter();

	// export an object of this class
	Remote proxy = exporter.export(new ExportJrmpDemo()); 
	System.out.println("Proxy is " + proxy.toString()); 

	// now unexport it once finished
	exporter.unexport(true); 
    } 
}
