package jeri;

import java.rmi.*; 
import net.jini.config.*; 
import net.jini.export.*; 

public class ConfigExportDemo implements Remote { 

    // We are using an explicit config file here so you can see 
    // where the Configuration is coming from. Really, another
    // level of indirection (such as a command-line argument)
    // should be used
    private static String CONFIG_FILE = "jeri/jeri.config";

    public static void main(String[] args) throws Exception { 
	String[] configArgs = new String[] {CONFIG_FILE};

	// get the configuration (by default a FileConfiguration) 
	Configuration config = ConfigurationProvider.getInstance(configArgs); 
	System.out.println("Configuration: " + config.toString());
	// and use this to construct an exporter
	Exporter exporter = (Exporter) config.getEntry( "JeriExportDemo", 
							"exporter", 
							Exporter.class); 

	// export an object of this class
	Remote proxy = exporter.export(new ConfigExportDemo()); 
	System.out.println("Proxy is " + proxy.toString()); 

	// now unexport it once finished
	exporter.unexport(true); 
    } 
}
