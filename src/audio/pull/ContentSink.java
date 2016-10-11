
/**
 * ContentSink.java
 *
 *
 * Created: Fri Aug 22 09:48:59 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.pull;

import java.io.*;
import audio.presentation.*;
import audio.common.*;

public class ContentSink {

    private InputStream in;
    private OutputStream out;
    private String cmd;
    private boolean stopped = false;
    private SinkImpl sink;

    public static ContentSink createSink(SinkImpl sink, 
					 InputStream in, Source source) {
	String cmd = "";

	if (source instanceof WAV) {
	    cmd = "playwav";
	} else if (source instanceof Ogg) {
	    cmd = "playogg";
	} else if (source instanceof MP3) {
	    cmd = "playmp3";
	} else {
	    cmd = "true";
	}

	ContentSink csink = new ContentSink(sink, in, cmd);
	return csink;
    }

    /**
     * There should really be a
     * WAVContentSink, OggContentSink, etc
     * I cheated since they would be so simple
     */
    private ContentSink(SinkImpl sink, InputStream in, String cmd) {
	this.sink = sink;
	this.in = in;
	this.cmd = cmd;
    }

    public void record() {
	
	Process proc = null;
	InputStream err = null;
	InputStream inn = null;
	try {
	    proc = Runtime.getRuntime().exec(cmd);
	    out = proc.getOutputStream();
	    err = proc.getErrorStream();
	    inn = proc.getInputStream();
	} catch(IOException e) {
	    System.err.println("Playing " + e.toString());
	    // ignore
	    return;
	}
	
	int ch;
	try {
	    while (((ch = in.read()) != -1) &&
		   (! stopped)) {
		out.write(ch);
		// System.out.println("Wrote byte");
	    }
	} catch(IOException e) {
	    // ignore
	    System.err.println("Exception writing: " + e.toString());
	    int navail = 0;
	    try {
		if ((navail = err.available()) > 0 ) {
		    byte avail[] = new byte[navail];
		    int nread = err.read(avail, 0, navail);
		    System.out.println("Error channel: " + 
				       new String(avail));
		}

		if ((navail = inn.available()) > 0 ) {
		    byte avail[] = new byte[navail];
		    int nread = inn.read(avail, 0, navail);
		    System.out.println("Out channel: " + 
				       new String(avail));
		}
	    } catch(IOException ee) {
		ee.printStackTrace();
	    }
	    return;
	} finally {
	    if (stopped) {
		System.out.println("Record stop called");
	    } else {
		System.out.println("Record finished naturally");
		stopped = true;
	    }
	    try {
		if (proc != null) {
		    proc.destroy();
		    try {
			// wait for soundcard to be released
			proc.waitFor();
		    } catch(InterruptedException ei) {
			System.out.println("Int " + ei);
		    }
		}
		in.close();
		out.close();
	    } catch(IOException e) {
		// ignore
		System.out.println("Finally " + e);
	    }

	    sink.contentStopped();
	}
    }

    public void stop() {
	if (stopped) {
	    return;
	}
	stopped  = true;
    }
} // ContentSink
