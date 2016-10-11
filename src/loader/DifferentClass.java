package loader;

import java.rmi.server.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.rmi.RMISecurityManager;

public class DifferentClass {

    public static void main(String[] args) throws Exception {
	// System.setSecurityManager(new RMISecurityManager());

	ClassLoader ldr1 = new URLClassLoader(new URL[] {
	    new URL("http://192.168.1.13/classes/loader.DifferentClass-dl.jar")});
	ClassLoader ldr2 = new URLClassLoader(new URL[] {
	    new URL("file:///home/httpd/html/java/jini/tutorial/dist/loader.DifferentClass-dl.jar")});

	Object simple1 = Class.forName("loader.Simple", true, ldr1).newInstance();
	System.out.println("Simple1" +
                           "\n   class: " + simple1.getClass().toString() +
			   "\n   loader: " + ldr1.toString());

	Object simple2 = Class.forName("loader.Simple", true, ldr2).newInstance();
	System.out.println("Simple2" + 
                           "\n   class: " + simple2.getClass().toString() +
			   "\n   loader: " + ldr2.toString());

	if (simple1.getClass() == simple2.getClass()) {
	    System.out.println("Same class");
	} else {
	    System.out.println("Different classes");
	}
    }
}
