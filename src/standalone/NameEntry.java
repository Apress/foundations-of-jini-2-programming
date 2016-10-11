
package standalone;

import java.awt.*;
import java.awt.event.*;

/**
 * NameEntry.java
 *
 *
 * Created: Sun Mar 28 23:47:02 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class NameEntry extends Frame {
    
    public NameEntry() {
	super("Name Entry");
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    });

	Label label = new Label("Name");
	TextField name = new TextField(20);
	add(label, BorderLayout.WEST);
	add(name, BorderLayout.CENTER);
	name.addActionListener(new NameHandler());

	pack();
    }
    
    public static void main(String[] args) {
	
	NameEntry f = new NameEntry();
	f.setVisible(true);
    }
} // NameEntry

class NameHandler implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
	System.out.println("Name was: " + evt.getActionCommand());
    }
}
