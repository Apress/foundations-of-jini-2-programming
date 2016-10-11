package jeri;

import java.rmi.*; 
import net.jini.export.*; 
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.tcp.TcpServerEndpoint;

public class ExportJeriDemo implements Remote { 

    public static void main(String[] args) throws Exception { 

	Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						  new BasicILFactory());

	// export an object of this class
	Remote proxy = exporter.export(new ExportJeriDemo()); 
	System.out.println("Proxy is " + proxy.toString()); 

	// now unexport it once finished
	exporter.unexport(true); 
    } 
}
