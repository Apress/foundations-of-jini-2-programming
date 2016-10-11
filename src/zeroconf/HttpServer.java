/**
 * @author Lei Huang
 */

package zeroconf;

import com.sun.jini.logging.Levels;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.*;
import net.jini.config.*;

/**
 */
public class HttpServer extends Thread
{
    private static Logger logger = Logger.getLogger("zeroconf.HttpServer");
    
    private ServerSocket server;
    private String hostAddr;
    private String dir;
    private int port = 0;
    private HashMap map;
    private boolean verbose;
    private FilePermission perm;
    
    public HttpServer(String hostAddr, int port, String dir)throws IOException
    {
	init(hostAddr, port, dir, null);
    }
    
    public HttpServer(Configuration config)throws IOException
    {
	init(null, 0, null, config);
    }
    
    /**
     * Describe <code>init</code> method here.
     *
     * @param hostAddr a <code>String</code> value
     * @param port an <code>int</code> value
     * @param dir a <code>String</code> value
     * @param config a <code>Configuration</code> value
     * @exception IOException if an error occurs
     */
    private void init(String hostAddr, int port, String dir, Configuration config)throws IOException
    {
	this.hostAddr = hostAddr;
	this.port = port;
	if (!dir.endsWith(File.separator))
	    dir += File.separatorChar;
	this.dir = dir;
	getConfig(config);

	server = new ServerSocket(port);
	// if port was zero, it is chosen by server socket
	port = server.getLocalPort();

	// now we can set codebase
	System.setProperty("java.rmi.server.codebase",
			   "http://" + hostAddr + ":" +
			   port + "/" + dir);


	perm = new FilePermission(dir + '-', "read");
	map = new HashMap();
	File fdir = new File(dir);
	String[] files = fdir.list();
	if (files == null)
	    return;
	URL base = fdir.toURL();
	for (int i = 0; i < files.length; i++) {
	    String jar = files[i];
	    if (!jar.endsWith(".jar") && !jar.endsWith(".zip"))
		continue;
	    ArrayList jfs = new ArrayList(1);
	    addJar(jar, jfs, base);
	    map.put(jar.substring(0, jar.length() - 4),jfs.toArray(new JarFile[jfs.size()]));
	}
    }
    
    private void getConfig(Configuration config) {
	String hostAddr = null;
	if (config != null) {
	    try {
		Integer iPort = (Integer) config.getEntry("zeroconf.http",
							  "port",
							  Integer.class,
							  new Integer(port));
		port = iPort.intValue();
		dir = (String) config.getEntry("zeroconf.http",
					       "basedir",
					       String.class,
					       dir);
		hostAddr = (String) config.getEntry("zeroconf.http",
						    "address",
						    String.class,
						    null);
	    } catch(ConfigurationException e) {
		logger.log(Level.SEVERE, e.toString());
	    }
	}

	if (hostAddr == null) {
	    hostAddr = getLocalAddress();
	}

    }

    private String getLocalAddress() {
	String addr = null;
	try {
	    addr = InetAddress.getLocalHost().getHostName();
	} catch(UnknownHostException e) {
	    logger.log(Level.SEVERE, e.toString());
	}
	return addr;
    }

    private void addJar(String jar, ArrayList jfs, URL base)throws IOException
    {
	base = new URL(base, jar);
	jar = base.getFile().replace('/', File.separatorChar);
	for (int i = jfs.size(); --i >= 0; ) {
	    if (jar.equals(((JarFile) jfs.get(i)).getName()))
		return;
	}
	
	logger.config(jar);
	JarFile jf = new JarFile(jar);
	jfs.add(jf);
	Manifest man = jf.getManifest();
	if (man == null)
	    return;
	Attributes attrs = man.getMainAttributes();
	if (attrs == null)
	    return;
	String val = attrs.getValue(Attributes.Name.CLASS_PATH);
	if (val == null)
	    return;
	for (StringTokenizer st = new StringTokenizer(val);st.hasMoreTokens(); )
	    {
	    	addJar(st.nextToken(), jfs, base);
	    }
    }
    
    public void run()
    {
	logger.log(Level.INFO, "HTTP Server started [{0}, port {1}]",
		   new Object[]{dir, Integer.toString(getPort())});
	try {
	    while (true) {
		new Task(server.accept()).start();
	    }
	} catch (IOException e)
	    {
	    	synchronized (this) {
		    if (!server.isClosed())
		    	logger.log(Level.SEVERE, "accepting connection", e);
		    terminate();
	    	}
	    }
    }
    
    public synchronized void terminate()
    {
	try {
	    server.close();
	} catch (IOException e) {}
	logger.log(Level.INFO, "HTTPServer terminated [port {0}]",Integer.toString(getPort()));
    }
    
    public int getPort() {
	return server.getLocalPort();
    }
    
    private class Task extends Thread {
	
	private Socket sock;
	public Task(Socket sock) {
	    this.sock = sock;
	    setDaemon(true);
	}
	private String getRequest() throws IOException
	{
	    BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    String req=in.readLine();
	    System.out.println(req);
	    return req;
	}
	
	private byte[] getBytes(InputStream in, long length)throws IOException
	{
	    DataInputStream din = new DataInputStream(in);
	    byte[] bytes = new byte[(int)length];
	    try {
		din.readFully(bytes);
	    } finally {
		din.close();
	    }
	    return bytes;
	}
	
	private byte[] getBytes(String path) throws IOException
	{
	    if (map != null) {
		int i = path.indexOf('/');
		if (i > 0) {
		    JarFile[] jfs = (JarFile[])map.get(path.substring(0, i));
		    if (jfs != null) {
			String jpath = path.substring(i + 1);
			for (i = 0; i < jfs.length; i++) {
			    JarEntry je = jfs[i].getJarEntry(jpath);
			    if (je != null)
				return getBytes(jfs[i].getInputStream(je),je.getSize());
			}
		    }
		}
	    }
	    File f = new File(dir + path.replace('/', File.separatorChar));
	    if (perm.implies(new FilePermission(f.getPath(), "read"))) {
		try {
		    return getBytes(new FileInputStream(f), f.length());
		} catch (FileNotFoundException e) {}
	    }
	    return null;
	}
	
	public void run()
	{
	    try
	    	{
		    DataOutputStream out =new DataOutputStream(sock.getOutputStream());
		    String req;
		    try {
			req = getRequest();
			
		    } catch (Exception e) {
		    	logger.log(Levels.HANDLED, "reading request", e);
		    	return;
		    }
		    if (req == null)
			return;
		    if (req.startsWith("SHUTDOWN *")) {
			try {
			    new ServerSocket(0, 1, sock.getInetAddress());
			} catch (IOException e) {
			    out.writeBytes("HTTP/1.0 403 Forbidden\r\n\r\n");
			    out.flush();
			    return;
			}
			terminate();
			return;
		    }
		    String[] args = null;
		    if (logger.isLoggable(Level.FINE))
			args = new String[]{req,sock.getInetAddress().getHostName(),Integer.toString(sock.getPort())};
		    boolean get = req.startsWith("GET ");
		    if (!get && !req.startsWith("HEAD "))
			{
			    logger.log(Level.FINE,"bad request \"{0}\" from {1}:{2}", args);
			    out.writeBytes("HTTP/1.0 400 Bad Request\r\n\r\n");
			    out.flush();
			    return;
			}
		    String path = req.substring(get ? 5 : 6);
		    int i = path.indexOf(' ');
		    if (i > 0)
			path = path.substring(0, i);
		    if (path == null) {
			logger.log(Level.FINE,"bad request \"{0}\" from {1}:{2}", args);
			out.writeBytes("HTTP/1.0 400 Bad Request\r\n\r\n");
			out.flush();
			return;
		    }
		    if (args != null)
			args[0] = path;
		    logger.log(Level.FINER,get ?
			       "{0} requested from {1}:{2}" :
			       "{0} probed from {1}:{2}",
			       args);
		    byte[] bytes;
		    try {
			bytes = getBytes(path);
		    } catch (Exception e) {
			logger.log(Level.WARNING, "getting bytes", e);
			out.writeBytes("HTTP/1.0 500 Internal Error\r\n\r\n");
			out.flush();
			return;
		    }
		    if (bytes == null) {
			logger.log(Level.FINE, "{0} not found", path);
			out.writeBytes("HTTP/1.0 404 Not Found\r\n\r\n");
			out.flush();
			return;
		    }
		    out.writeBytes("HTTP/1.0 200 OK\r\n");
		    out.writeBytes("Content-Length: " + bytes.length + "\r\n");
		    out.writeBytes("Content-Type: application/java\r\n\r\n");
		    if (get)
			out.write(bytes);
		    out.flush();
		    
	    	} catch (Exception e) {
		    logger.log(Levels.HANDLED, "writing response", e);
	    	} finally {
		    try {
			sock.close();
		    } catch (IOException e) {}
		}//finally
	}//run
    }//task
}
