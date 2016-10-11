
/**
 * ContentSink.java
 *
 *
 * Created: Fri Aug 22 09:48:59 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.filesink;

import java.io.*;
import audio.presentation.*;
import audio.common.*;

public class ContentSink {

    private InputStream in;
    private OutputStream out;
    private boolean stopped = false;
    private SinkImpl sink;
    private File outFile;

    public static ContentSink createSink(SinkImpl sink, 
					 InputStream in, Source source,
					 File outFile) {

	ContentSink csink = new ContentSink(sink, in, outFile);
	return csink;
    }

    private ContentSink(SinkImpl sink, InputStream in, File outFile) {
	this.sink = sink;
	this.in = in;
	this.outFile = outFile;
	System.out.println("Content sink set to " + outFile);
    }

    public void record() {
	
	InputStream err = null;
	try {
	    out = new DataOutputStream(new FileOutputStream(outFile));
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
	    }
	} catch(IOException e) {
	    // ignore
	    System.err.println("Exception writing " + e.toString());
	    int navail = 0;
	    try {
		if ((navail = err.available()) > 0 ) {
		    byte avail[] = new byte[navail];
		    int nread = err.read(avail, 0, navail);
		    System.out.println("Error channel " + 
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
}// ContentSink
