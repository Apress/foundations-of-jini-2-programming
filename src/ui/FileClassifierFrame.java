
/**
 * FileClassifierFrame.java
 *
 *
 * Created: Sun Feb 13 19:29:20 2000
 *
 * @author Jan Newmarch
 * @version 1.0
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import net.jini.lookup.ui.MainUI;
import net.jini.core.lookup.ServiceItem;
import common.MIMEType;
import common.FileClassifier;
import java.rmi.RemoteException;

/**
 * Object implementing MainUI for FileClassifier.
 */
public class FileClassifierFrame extends Frame implements MainUI {
 
    ServiceItem item;
    TextField text;

    public FileClassifierFrame(ServiceItem item, String name) {
	super(name);

	this.item = item;

	Panel top = new Panel();
	Panel bottom = new Panel();
	add(top, BorderLayout.CENTER);
	add(bottom, BorderLayout.SOUTH);
	
	top.setLayout(new BorderLayout());
	top.add(new Label("Filename"), BorderLayout.WEST);
	text = new TextField(20);
	top.add(text, BorderLayout.CENTER);

	bottom.setLayout(new FlowLayout());
	Button classify = new Button("Classify");
	Button quit = new Button("Quit");
	bottom.add(classify);
	bottom.add(quit);

	// listeners
	quit.addActionListener(new QuitListener());
	classify.addActionListener(new ClassifyListener());

	// We pack, but don't make it visible	
	pack();
    }

    class QuitListener implements ActionListener {
	public void actionPerformed(ActionEvent evt) {
	    System.exit(0);
	}
    }

    class ClassifyListener implements ActionListener {
	public void actionPerformed(ActionEvent evt) {
	    String fileName = text.getText();
	    final Dialog dlg = new Dialog((Frame) text.getParent().getParent());
	    dlg.setLayout(new BorderLayout());
	    TextArea response = new TextArea(3, 20);

	    // invoke service
	    FileClassifier classifier = (FileClassifier) item.service;
	    MIMEType type = null;
	    try {
		type = classifier.getMIMEType(fileName);
		if (type == null) {
		    response.setText("The type of file " + fileName +
				     " is unknown");
		} else {
		    response.setText("The type of file " + fileName +
				     " is " + type.toString());
		}
	    } catch(RemoteException e) {
		response.setText(e.toString());
	    }

	    Button ok = new Button("ok");
	    ok.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    dlg.setVisible(false);
		}
	    });

	    dlg.add(response, BorderLayout.CENTER);
	    dlg.add(ok, BorderLayout.SOUTH);
	    dlg.setSize(300, 100);
	    dlg.setVisible(true);
	}
    }
    
} // FileClassifierFrame
