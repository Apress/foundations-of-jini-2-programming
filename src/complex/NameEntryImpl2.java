
package complex;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * NameEntryImpl2.java
 *
 *
 * Created: Mon Mar 29 11:38:33 1999
 *
 * @author Jan Newmarch
 * @version 1.0
 */

public class NameEntryImpl2 implements common.NameEntry,
                                       ActionListener, java.io.Serializable {
    
    public NameEntryImpl2() {
    }

    /**
     * method invoked on pressing <return> in the TextField
     */
    public void actionPerformed(ActionEvent evt) {
	System.out.println("Name was: " + evt.getActionCommand());
    }

    public void show() {
	Frame fr = new Frame("Name Entry");

	fr.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowOpened(WindowEvent e) {}});

	fr.setLayout(new BorderLayout());
	Label label = new Label("Name");
	fr.add(label, BorderLayout.WEST);
	TextField name = new TextField(20);
	fr.add(name, BorderLayout.CENTER);
	name.addActionListener(this);

	fr.pack();
	fr.show();
    }
    
    
} // NameEntryImpl2
