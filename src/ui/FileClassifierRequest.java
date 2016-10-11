/**
 * THIS IS BROKEN RIGHT NOW
 */

/**
 * FileClassifierRequest.java
 *
 *
 * Created: Wed Feb 10 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package ui;

import common.FileClassifier;
import common.MIMEType;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ClientLookupManager;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;

import cgi.*;

import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.factory.HTMLFactory;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;

import java.io.*;

public class FileClassifierRequest {

    private static final long WAITFOR = 20000L;

    public static void main(String argv[]) {

	new FileClassifierRequest();

        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(2*WAITFOR);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }

    public FileClassifierRequest() {

	// Get CGI info

	String method = System.getProperty("REQUEST_METHOD");
	String[] data = new String[1];
	if ("GET".equalsIgnoreCase(method))
	    data[0] = System.getProperty("QUERY_STRING");
	else {
	    try {
		DataInputStream in =
		    new DataInputStream(System.in);
		data[0] = in.readLine();
	    } catch(IOException ioe) {
		System.out.println("IOException: " + ioe);
		System.exit(-1);
	    }
	}
	QueryStringParser parser =
	    new QueryStringParser(data[0]);
	LookupTable table = parser.parse();
	String[] names = table.getNames();
	String[] values = table.getValues();
	
	System.out.println("<HTML> <HEAD> </HEAD> <BODY>");
	if (names.length > 0)
	    System.out.println("Data supplied:\n" +
			       "<CENTER>\n" +
			       "<TABLE BORDER=1>\n" +
			       "  <TR><TH>Name<TH>Value(s)");
	else
	    System.out.println("<H2>No data supplied.</H2>");
	String name, value;
	String[] fullValue;
	for(int i=0; i<names.length; i++) {
	    name = names[i];
	    System.out.println("  <TR><TD>" + name);
	    if (table.numValues(name) > 1) {
		fullValue = table.getFullValue(name);
		System.out.println
		    ("      <TD>Multiple values supplied:\n" +
		     "          <UL>");
		for(int j=0; j<fullValue.length; j++)
		    System.out.println("            <LI>" +
				       fullValue[j]);
		System.out.println("          </UL>");
	    } else {
		value = values[i];
		if (value.equals(""))
		    System.out.println
			("      <TD><I>No Value Supplied</I>");
		else
		    System.out.println
			("      <TD>" + value);
	    }
	}
	System.out.println("</TABLE>\n</CENTER>");
	String serviceIDName = table.getValue("serviceID");
	String fileName = table.getValue("filename");

	if (serviceIDName == null) System.out.println("is null");
	else System.out.println("id is " + serviceIDName);
	ServiceID id = getServiceID(serviceIDName);
	if (id == null) System.out.println("is null");
	else System.out.println("Service id constructed as " + id.toString());

	ClientLookupManager clientMgr = null;

	System.setSecurityManager(new RMISecurityManager());

        try {
            LookupDiscoveryManager mgr =
                new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                                           null /* unicast locators */,
                                           null /* DiscoveryListener */);
	    clientMgr = new ClientLookupManager(mgr, 
						new LeaseRenewalManager());
	} catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
  
	Class [] classes = new Class[] {FileClassifier.class};

	ServiceTemplate template = new ServiceTemplate(id, null, null);

	ServiceItem item = null;
	try {
	    item = clientMgr.lookup(template, 
				    null, /* no filter */ 
				    WAITFOR /* timeout */);
	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	
	if (item == null) {
	    // couldn't find a service in time
	    System.out.println("no service </BODY> </HTML>");
	    System.exit(0);
	}

	FileClassifier classifier = (FileClassifier) item.service;
	MIMEType type = null;
	try {
	    type = classifier.getMIMEType(fileName);
	} catch(Exception e) {
	    e.printStackTrace();
	}

	if (type == null) {
	    System.out.println("Unknown file type");
	} else {
	    System.out.println("Type is " + type.toString());
	}

	System.out.println("</BODY> </HTML>");
	System.exit(0);
    }

    /**
     * Construct a ServiceID from a String obtained from
     * ServiceId.toString(). Allows you to construct a 
     * ServiceID with the same id value as another one
     * whose String representation you have
     */
    ServiceID getServiceID(String name) {
	try {
	    String[] a = new String[5];
	    int start, end;

	    start = 0;
	    for (int n = 0; n < 4; n++) {
		end = name.indexOf('-', start);
		a[n] = name.substring(start, end);
		start = end+1;
	    }
	    a[4] = name.substring(start);
	    
	    long mostSig = (Long.parseLong(a[0], 16) << 32) +
		           (Long.parseLong(a[1], 16) << 16) +
		           Long.parseLong(a[2], 16);
	    long leastSig = (Long.parseLong(a[3], 16) << 48) +
		            Long.parseLong(a[4], 16);
	    return new ServiceID(mostSig, leastSig);
	} catch(Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
} // FileClassifierRequest
