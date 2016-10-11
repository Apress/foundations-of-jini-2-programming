
package complex;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * NameEntryImpl1.java
 *
 *
 * Created: Mon Mar 29 11:38:33 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class NameEntryImpl1 extends Frame implements common.NameEntry,
                                          ActionListener, java.io.Serializable {
    
    public NameEntryImpl1() {
	super("Name Entry");
	/*
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowOpened(WindowEvent e) {}});
	*/
	setLayout(new BorderLayout());
	Label label = new Label("Name");
	add(label, BorderLayout.WEST);
	TextField name = new TextField(20);
	add(name, BorderLayout.CENTER);
	name.addActionListener(this);

	// don't do this here!
	// pack();
    }

    /**
     * method invoked on pressing <return> in the TextField
     */
    public void actionPerformed(ActionEvent evt) {
	System.out.println("Name was: " + evt.getActionCommand());
    }

    public void show() {
	pack();
	super.show();
    }
    
    
} // NameEntryImpl1
