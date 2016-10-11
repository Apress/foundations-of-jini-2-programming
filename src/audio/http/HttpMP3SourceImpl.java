
/**
 * HttpMP3SourceImpl.java
 *
 *
 * Created: Mon Sep  8 08:49:04 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.http;

import audio.presentation.MP3;
import java.net.*;

/**
 * Adds MP3 interface to HttpSourceImpl for MP3 files
 * delivered by HTTP
 */

public class HttpMP3SourceImpl extends HttpSourceImpl
    implements MP3{

    public HttpMP3SourceImpl(URL url) throws MalformedURLException {
	super(url);
    }

}// HttpMP3SourceImpl
