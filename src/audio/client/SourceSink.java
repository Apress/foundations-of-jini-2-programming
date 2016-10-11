
/**
 * SourceSink.java
 *
 *
 * Created: Tue Jul 15 07:30:52 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.client;

import net.jini.core.lookup.ServiceItem;
import java.rmi.MarshalledObject;

/**
 * A slightly klunky class that is used as a handback to a sink
 * STOP event generator - it is used to say "these are the sources
 * I still want to play when you have finished playing the current
 * track"
 */

public class SourceSink implements java.io.Serializable {
    public ServiceItem[] sources;
    public ServiceItem sink;

    public SourceSink (ServiceItem[] sources, ServiceItem sink) {
	this.sources = sources;
	this.sink = sink;
    }
}// SourceSink
