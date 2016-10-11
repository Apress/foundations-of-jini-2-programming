package zeroconf;

import java.net.*;

public class LocalAddress {

    public static void main(String[] args) throws UnknownHostException {
	InetAddress addr = InetAddress.getLocalHost();
	System.out.println("Host address is " + addr.getHostAddress());
    }
}
