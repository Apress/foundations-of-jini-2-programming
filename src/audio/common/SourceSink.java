
/**
 * SourceSink.java
 *
 *
 * Created: Tue Jul 15 07:30:52 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.common;

import net.jini.core.lookup.ServiceItem;
import java.rmi.MarshalledObject;

public class SourceSink implements java.io.Serializable {
    public ServiceItem[] sources;
    public ServiceItem sink;

    public SourceSink (ServiceItem[] sources, ServiceItem sink) {
	this.sources = sources;
	this.sink = sink;
    }
}// SourceSink
