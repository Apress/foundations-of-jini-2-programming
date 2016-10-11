
/**
 * LabelCellRenderer.java
 *
 *
 * Created: Wed Jul 16 10:01:29 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.client;

import net.jini.core.entry.Entry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.*;
import net.jini.core.lookup.ServiceItem;

import audio.common.CDTrack;


/**
 * Render a cell containing A/V data for a single track.
 * Looks for entry information such as CDTrack or Name
 */

public class LabelCellRenderer extends JLabel 
    implements ListCellRenderer, TreeCellRenderer {
    
    public LabelCellRenderer() {
	setOpaque(true);
    }
    
    public Component
	getListCellRendererComponent(
				     JList list, 
				     Object value, 
				     int index, 
				     boolean isSelected, 
				     boolean cellHasFocus) {
	ServiceItem item = (ServiceItem) value;
	// System.out.println("Rendering cell " + item.service);
	
	Entry[] entries = item.attributeSets;
	setText(item.service.toString());
	if (entries != null) {
	    for (int n = 0; n < entries.length; n++) {
		if (entries[n] instanceof CDTrack) {
		    CDTrack track = (CDTrack) entries[n];
		    setText(track.artist + " / " +
			    track.title + ": " +
			    track.trackTitle);
		    break;
		} else if (entries[n] instanceof Name) {
		    setText(((Name) entries[n]).name);
		    break;
		}

	    }
	    
	}
	
	setBackground(isSelected ?
		      list.getSelectionBackground() : list.getBackground());
	setForeground(isSelected ?
		      list.getSelectionForeground() : list.getForeground());
	return this;
    }
    
    public Component
	getTreeCellRendererComponent(
				     JTree tree, 
				     Object value, 
				     boolean isSelected, 
				     boolean isExpanded,
				     boolean isLeaf,
				     int row,
				     boolean cellHasFocus) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	Object nodeObj = node.getUserObject();
	
	if (isSelected) {
	    setBackground(UIManager.getColor("Tree.selectionBackground"));
	    setForeground(UIManager.getColor("Tree.selectionForeground"));
	} else {
	    setBackground(UIManager.getColor("Tree.textBackground"));
	    setForeground(UIManager.getColor("Tree.selectionForeground"));
	}
	
	if (nodeObj instanceof String) {
	    setText((String) nodeObj);
	    return this;
	} else if (nodeObj instanceof ServiceItem) {
	    ServiceItem item = (ServiceItem) node.getUserObject();
	    setText(item.toString());
	    /*
	    // System.out.println("Rendering cell " + item.service);
	    
	    Entry[] entries = item.attributeSets;
	    setText(item.service.toString());
	    if (entries != null) {
		for (int n = 0; n < entries.length; n++) {
		    if (entries[n] instanceof Name) {
			setText(((Name) entries[n]).name);
			break;
		    }
		}
		
	    }
	    */
	    return this;
	}
	return null;
    }
}
