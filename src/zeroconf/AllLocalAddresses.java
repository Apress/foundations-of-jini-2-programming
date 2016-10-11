package zeroconf;

import java.net.*;
import java.util.Enumeration;

public class AllLocalAddresses {

    public static void main(String[] args) throws UnknownHostException,
						  SocketException {
	Enumeration ifaces = NetworkInterface.getNetworkInterfaces();
	while (ifaces.hasMoreElements()) {
	    NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	    Enumeration addrs = iface.getInetAddresses();
	    System.out.println("Interface is " + iface.getName());
	    while (addrs.hasMoreElements()) {
		InetAddress addr = (InetAddress) addrs.nextElement();
		String type;
		if (addr instanceof Inet4Address) {
		    type = "   Inet v4 ";
		} else {
		    type = "   Inet v6 ";
		}
		System.out.println(type + "address is " + addr.getHostAddress());
	    }
	}
    }
}
