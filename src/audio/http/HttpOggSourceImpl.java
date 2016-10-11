
/**
 * HttpOggSourceImpl.java
 *
 *
 * Created: Mon Sep  8 08:49:52 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.http;

import audio.presentation.Ogg;
import audio.transport.HttpURL;

import java.net.*;

/**
 * Adds Ogg interface to HttpSourceImpl for Ogg files
 * delivered by HTTP
 */

public class HttpOggSourceImpl extends HttpSourceImpl
    implements Ogg{

    public HttpOggSourceImpl(HttpURL url) throws MalformedURLException {
	super(url);
    }

    public HttpOggSourceImpl(URL url) throws MalformedURLException {
	super(url);
    }
}// HttpOggSourceImpl
