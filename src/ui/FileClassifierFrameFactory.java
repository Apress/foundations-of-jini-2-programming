
/**
 * FileClassifierFrameFactory.java
 *
 *
 * Created: Sun Feb 13 17:15:16 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package ui;

import net.jini.lookup.ui.factory.FrameFactory;
import net.jini.lookup.entry.UIDescriptor;
import java.awt.Frame;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;

public class FileClassifierFrameFactory implements FrameFactory {

    /**
     * Return a new FileClassifierFrame that implements the
     * MainUI role
     */
    public Frame getFrame(Object roleObject) {
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
		    Frame frame = new FileClassifierFrame(item, "File Classifier");
		    return frame;
		}
	    }
	}
	// couldn't find a role the factory can create
	return null;
    }
} // FileClassifierFrameFactory
