
/**
 * FooLeasedResource.java
 *
 *
 * Created: Mon Jun 14 18:04:33 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */
package foolandlord;

import com.sun.jini.landlord.LeasedResource;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

public class FooLeasedResource implements LeasedResource  {
    
    protected Uuid cookie;
    protected Foo foo;
    protected long expiration = 0;

    public FooLeasedResource(Foo foo) {
        this.foo = foo;
	cookie = UuidFactory.generate();
    }

    public void setExpiration(long newExpiration) {
	this.expiration = newExpiration;
    }

    public long getExpiration() {
	return expiration;
    }
    public Uuid getCookie() {
	return cookie;
    }

    public Foo getFoo() {
	return foo;
    }
} // FooLeasedResource



