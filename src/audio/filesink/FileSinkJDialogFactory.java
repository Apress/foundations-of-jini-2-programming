
/**
 * FileSinkJDialogFactory.java
 *
 *
 * Created: Mon Oct 13 09:59:55 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.filesink;

import net.jini.lookup.ui.factory.JDialogFactory;
import net.jini.lookup.entry.UIDescriptor;
import javax.swing.JDialog;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import java.awt.*;

public class FileSinkJDialogFactory implements JDialogFactory {

    /**
     * Return a new FileSinkJDialog that implements the
     * MainUI role
     */
    public JDialog getJDialog(Object roleObject, Frame owner, boolean modal) {
        // we should check to see what role we have to return
        if (! (roleObject instanceof ServiceItem)) {
            // unkown role type object
            // can we return null?
            return null;
        }
        ServiceItem item = (ServiceItem) roleObject;

        // Do sanity checking that the UIDescriptor has a MainUI role
        Entry[] entries = item.attributeSets;
        for (int n = 0; n < entries.length; n++) {
            if (entries[n] instanceof UIDescriptor) {
                UIDescriptor desc = (UIDescriptor) entries[n];
                if (desc.role.equals(net.jini.lookup.ui.MainUI.ROLE)) {
                    // Ok, we are in the MainUI role, so return a UI for that
                    JDialog dialog = new FileSinkJDialog(owner, item, 
							 "File sink", modal);
                    return dialog;
                }
            }
        }
        // couldn't find a role the factory can create
        return null;
    }

    public JDialog getJDialog(Object roleObject) {
	return getJDialog(roleObject, (Frame) null, false);
    }

    public JDialog getJDialog(Object roleObject, Dialog owner) {
	return getJDialog(roleObject, owner, false);
    }

    public JDialog getJDialog(Object roleObject, Dialog owner,
			     boolean modal) {
	        // we should check to see what role we have to return
        if (! (roleObject instanceof ServiceItem)) {
            // unkown role type object
            // can we return null?
            return null;
        }
        ServiceItem item = (ServiceItem) roleObject;

        // Do sanity checking that the UIDescriptor has a MainUI role
        Entry[] entries = item.attributeSets;
        for (int n = 0; n < entries.length; n++) {
            if (entries[n] instanceof UIDescriptor) {
                UIDescriptor desc = (UIDescriptor) entries[n];
                if (desc.role.equals(net.jini.lookup.ui.MainUI.ROLE)) {
                    // Ok, we are in the MainUI role, so return a UI for that
                    JDialog dialog = new FileSinkJDialog(owner, item, 
							 "File sink", modal);
                    return dialog;
                }
            }
        }
        // couldn't find a role the factory can create
        return null;
    }

    public JDialog getJDialog(Object roleObject, Frame owner) {
	return getJDialog(roleObject, owner, false);
    }

}// FileSinkJDialogFactory
