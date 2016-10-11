/**
 * FileClassifierServlet.java
 *
 *
 * Created: Wed Feb 10 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package servlet;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.LookupCache;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FileClassifierServlet extends HttpServlet {

    private LookupCache cache;

    public void init() {
        ServiceDiscoveryManager clientMgr = null;

        System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null /* unicast locators */,
                                           null /* DiscoveryListener */);
            clientMgr = new ServiceDiscoveryManager(mgr, 
                                                new LeaseRenewalManager());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
        Class [] classes = new Class[] {FileClassifier.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, 
                                                       null);

        try {
            cache = clientMgr.createLookupCache(template, 
                                                null, /* no filter */ 
                                                null /* no listener */);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
	throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType =
	    "<!DOCTYPE HTML PUBLIC \"-//W3C/DTD HTML 4.0 " +
	    "Transitional//EN\"\n";
        out.println(docType +
                    "<HTML>\n" +
                    "<head><title>File Classifier</title></head>\n" +
                    "<body>\n" +
                    "<h1> File Classifier </h1>");

	String fileName = request.getParameter("filename");
	if (fileName == null || fileName.length() == 0) {
	    showFirstPage(out);
	} else {
	    handleForm(fileName, out);
	}
	out.println("</BODY> </HTML>");
    }

    public void showFirstPage(PrintWriter out) {
	out.println("<form action=\"http://localhost:8088/jini/servlet/servlet.FileClassifierServlet\">\n" +
		    "File Name <input type=\"textfield\" name=\"filename\">\n" +
		    "<br>\n" +
		    "<input type=\"Submit\">\n" +
		    "</form>\n");                    
    }

    public void handleForm(String fileName, PrintWriter out) {

	out.println("<h1>Filename: " + fileName + "</h1>\n");
 
        ServiceItem item = null;
	item = cache.lookup(null);
	
	if (item == null) {
	    out.println("<P>No service- try again later</P>");
	    return;
	}

	FileClassifier classifier = (FileClassifier) item.service;
	// out.println(" Found classifier. ");
	MIMEType type = null;
	try {
	    type = classifier.getMIMEType(fileName);
	} catch(Exception e) {
	    out.println("<pre>" + e.toString() + "</pre>");
	    return;
	}
	// out.println(" Found type. " + type);

	if (type == null) {
	    out.println("<P>Unknown file type</P>");
	} else {
	    out.println("<P>Type is: " + type.toString() + "</P>");
	}
    }        
} // FileClassifierServlet
