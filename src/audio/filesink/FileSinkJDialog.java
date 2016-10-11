
/**
 * FileSinkJDialog.java
 *
 *
 * Created: Mon Oct 13 10:04:22 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.filesink;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import net.jini.lookup.ui.MainUI;
import net.jini.core.lookup.ServiceItem;
import java.rmi.RemoteException;
import javax.swing.filechooser.FileSystemView;

public class FileSinkJDialog extends JDialog implements MainUI, ActionListener  {
    private JFileChooser chooser;
    private FileSink sink;

    public FileSinkJDialog(Frame owner, ServiceItem item, 
			   String name, boolean modal) {
	super(owner, name, modal);

	sink = (FileSink) item.service;

	// setup a filter based on sink.getSource() ifaces
	// ...

	RemoteFileSystemViewWrapper fileView = new RemoteFileSystemViewWrapper(sink);

	chooser = new JFileChooser((FileSystemView) fileView);
	chooser.addActionListener(this);
	getContentPane().add(chooser, BorderLayout.CENTER);
	pack();
    }

    public FileSinkJDialog(Dialog owner, ServiceItem item, 
			   String name, boolean modal) {
	super(owner, name, modal);

	sink = (FileSink) item.service;

	// setup a filter based on sink.getSource() ifaces
	// e.g. to only show .wav or .ogg files
	// ... not done yet

	RemoteFileSystemViewWrapper fileView = new RemoteFileSystemViewWrapper(sink);

	chooser = new JFileChooser((FileSystemView) fileView);
	chooser.addActionListener(this);
	getContentPane().add(chooser, BorderLayout.CENTER);
	pack();
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
	    // file selected
	    System.out.println("Selected file: " + chooser.getSelectedFile());
	    try {
		sink.setFile(chooser.getSelectedFile());
		setVisible(false);
	    } catch(RemoteException re) {
		System.out.println("" + re);
	    }
	} else {
	    System.out.println("Other event: " + e);
	}
    }
}// FileSinkJDialog
