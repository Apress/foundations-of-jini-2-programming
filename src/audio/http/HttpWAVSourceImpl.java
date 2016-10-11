
/**
 * HttpWAVSourceImpl.java
 *
 *
 * Created: Mon Sep  8 08:46:41 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.http;

import audio.presentation.WAV;
import java.net.*;

/**
 * Extends HttpSourceImpl for a WAV source
 */

public class HttpWAVSourceImpl extends HttpSourceImpl
    implements WAV {

    public HttpWAVSourceImpl(URL url) throws MalformedURLException {
	super(url);
    }
}// HttpWAVSourceImpl
