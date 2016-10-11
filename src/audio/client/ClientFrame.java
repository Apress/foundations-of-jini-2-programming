
/**
 * ClientFrame.java
 *
 *
 * Created: Tue Jul  1 09:18:21 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version 1.0
 */

package audio.client;

import audio.common.*;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceID;
import java.rmi.RemoteException;
import java.util.Enumeration;
import net.jini.core.event.RemoteEvent;
import java.rmi.Remote;
import net.jini.core.event.RemoteEventListener;
import java.rmi.MarshalledObject;
import java.io.IOException;


/**
 * The main Swing JFrame for the GUIClient
 * Displays three panels for directories, sources and sinks
 */

public class ClientFrame extends JFrame
				 implements ActionListener, 
					    ListSelectionListener,
					    TreeSelectionListener,
					    TreeExpansionListener,
					    RemoteEventListener {

    private GUIClient client;

    private JList sources = new JList();
    private JList sinks = new JList();
    private JTree directories = new JTree();

    private JButton playFolderBtn = new JButton("Play folder");
    private JButton playSourcesBtn = new JButton("Play sources");
    private JButton stopBtn = new JButton("Stop");

    private Remote proxy;

    public ClientFrame() {
	super("Audio Router");
	makeLayout();
	addListeners();
	setupLists();
    }

    public void setClient(GUIClient client) {
	this.client = client;
    }

    private void makeLayout() {
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	JPanel bottom = new JPanel();
	JTabbedPane top = new JTabbedPane();
	contentPane.add(bottom, BorderLayout.SOUTH);
	contentPane.add(top, BorderLayout.CENTER);

	JPanel dirPane = new JPanel();
	JPanel sourcePane = new JPanel();
	JPanel sinkPane = new JPanel();
	top.add("Folders", dirPane);
	top.add("Sources", sourcePane);
	top.add("Sinks", sinkPane);
	
	dirPane.setLayout(new BorderLayout());
	JScrollPane js0 = new  JScrollPane();
	js0.getViewport().setView(directories);
	dirPane.add(js0,
		       BorderLayout.CENTER);

	sourcePane.setLayout(new BorderLayout());
	JScrollPane js1 = new  JScrollPane();
	js1.getViewport().setView(sources);
	sourcePane.add(js1,
		       BorderLayout.CENTER);

	sinkPane.setLayout(new BorderLayout());
	JScrollPane js2 = new  JScrollPane();
	js2.getViewport().setView(sinks);
	sinkPane.add(js2, BorderLayout.CENTER);

	bottom.setLayout(new BorderLayout());

	JPanel buttons = new JPanel();
	buttons.setLayout(new GridLayout(1, 3));
	buttons.add(playFolderBtn);
	buttons.add(playSourcesBtn);
	// buttons.add(stopBtn);

	bottom.add(buttons, BorderLayout.CENTER);
    }

    private void addListeners() {
	playFolderBtn.addActionListener(this);
	playSourcesBtn.addActionListener(this);
 	stopBtn.addActionListener(this);

	directories.addTreeSelectionListener(this);
	directories.addTreeExpansionListener(this);

	sources.addListSelectionListener(this);

	sinks.addListSelectionListener(this);
    }

    private void setupLists() {
	LabelCellRenderer labelRend = new LabelCellRenderer();
    
	directories.setCellRenderer(labelRend);
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Directories");
	directories.setModel(new DefaultTreeModel(root));

	sources.setCellRenderer(labelRend);
	sources.setModel(new DefaultListModel());

	sinks.setCellRenderer(labelRend);
	sinks.setModel(new DefaultListModel());
    }

    public void actionPerformed(ActionEvent evt) {
	Object[] sourceSels = null;
	if (evt.getSource() == playSourcesBtn) {
	    sourceSels = sources.getSelectedValues();
	} else if (evt.getSource() == playFolderBtn) {
	    TreePath[] selectionPaths = directories.getSelectionPaths();
	    if (selectionPaths == null) {
		sourceSels = null;
	    } else {
		sourceSels = new Object[selectionPaths.length];
		for (int n = 0; n < sourceSels.length; n++) {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
			selectionPaths[n].
			getLastPathComponent();
		    Object lastComponent = node.getUserObject();
		    sourceSels[n] = lastComponent;
		    }
	    }
	}

	if ((sourceSels == null) || (sourceSels.length == 0)) {
	    JOptionPane.showMessageDialog(null, 
					  "No source selected", "Source is null",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	
	Object[] sinkSels = sinks.getSelectedValues();
	if ((sinkSels == null) || (sinkSels.length == 0)) {
	    JOptionPane.showMessageDialog(null, 
					  "No sink selected", "Sink is null",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	
	ServiceItem[] sourceSelections = new ServiceItem[sourceSels.length];
	for (int n = 0; n < sourceSels.length; n++) {
	    sourceSelections[n] = (ServiceItem) sourceSels[n];
	}
	
	ServiceItem[] sinkSelections = new ServiceItem[sinkSels.length];
	for (int n = 0; n < sinkSels.length; n++) {
	    sinkSelections[n] = (ServiceItem) sinkSels[n];
	}
	// Sink sink = (Sink) sinkSelections[0].service;
	
	new PlayFrame(client).play(sourceSelections, sinkSelections[0]);
	// play(sourceSelections, sinkSelections[0]);
    }

    private void play(ServiceItem[] sourceSelections, ServiceItem sinkItem) {
	if ((sourceSelections == null) || (sourceSelections.length == 0)) {
	    System.out.println("Play: null sources");
	    return;
	}
	Source source = (Source) sourceSelections[0].service;
	Sink sink = (Sink) sinkItem.service;

	ServiceItem[] rest = new ServiceItem[sourceSelections.length - 1];
	for (int n = 0; n < rest.length; n++) {
	    rest[n] = sourceSelections[n + 1];
	}

	MarshalledObject handback = null;
	try {
	    handback = new MarshalledObject(new SourceSink(rest, sinkItem));
	} catch(java.io.IOException e) {
	    e.printStackTrace();
	    return;
	}

	try {
	    if (proxy == null) {
		// proxy = client.export(this);
	    }
	    // source.addSourceListener((RemoteEventListener) proxy, null);
	    System.out.println("Added source " + source + " proxy " + proxy);
	    sink.addSinkListener((RemoteEventListener) proxy, handback);
	    System.out.println("Added sink " + sink + " proxy " + proxy + " handback " + 
			       ((SourceSink) handback.get()).sources);
	} catch(RemoteException e) {
	    e.printStackTrace();
	} catch(Exception e) {
	    e.printStackTrace();
	}

	try{
	    System.out.println("Setting sink to: " + sink);
	    source.addSink(sink);
	    System.out.println("Setting source to: " + source);
	    sink.addSource(source);
	    System.out.println("Playing " + sourceSelections[0].attributeSets[0]);

	    // and then play...
	    source.play();
	    sink.record();
	} catch(AlreadyPlayingException e) {
	    JOptionPane.showMessageDialog(null,
					  "Source already playing",
					  "Play error",
					  JOptionPane.ERROR_MESSAGE);
	} catch(AlreadyRecordingException e) {
	    JOptionPane.showMessageDialog(null,
					  "Sink already recording",
					  "Record error",
					  JOptionPane.ERROR_MESSAGE);
	} catch(Exception e) {
	    // IncompatableSink/Source
	    e.printStackTrace();
	}
    }

    private void stop(ServiceItem[] sourceSelections, ServiceItem sinkItem) {
	// Source source = (Source) sourceSelections[0].service;
	System.out.println("Stopping");
	Sink sink = (Sink) sinkItem.service;

	try{
	    // source.stop();
	    sink.stop();
	    /*
	} catch(NotPlayingException e) {
	    JOptionPane.showMessageDialog(null,
					  "Source not playing",
					  "Play stop error",
					  JOptionPane.ERROR_MESSAGE);
	    */
	} catch(NotRecordingException e) {
	    JOptionPane.showMessageDialog(null,
					  "Sink not recording",
					  "Record stop error",
					  JOptionPane.ERROR_MESSAGE);
	} catch(Exception e) {
	    // ignore?
	    e.printStackTrace();
	}
    }

    public void notify(RemoteEvent evt) {
	Object src = evt.getSource();
	System.out.println("Updating " + src);
	if ((src instanceof Sink) && 
	    (evt.getID() == Sink.STOP)) {
	    System.out.println("Sink stopped event");
	    Sink sink = (Sink) src;

	    try {
		sink.removeSinkListener((RemoteEventListener) proxy);
	    } catch(RemoteException e) {
		// ignore
	    } catch(NoSuchListenerException e) {
		// ignore
	    }

	    MarshalledObject handback = evt.getRegistrationObject();
	    SourceSink ss = null;
	    try {
		ss = (SourceSink) handback.get();
	    } catch(Exception e) {
		e.printStackTrace();
		return;
	    }

	    ServiceItem[] sources = ss.sources;
	    ServiceItem sinkItem = ss.sink;
	    System.out.println("  stop -> play: sources " + sources + " sink " + sink);
	    play(sources, sinkItem);

	} else if ((src instanceof Source) && 
	    (evt.getID() == Source.STOP)) {
	    System.out.println("Source stopped event");
	}
    }

    /**********************************
     * Source tree manipulation methods
     */
    public void valueChanged(TreeSelectionEvent evt) {
	JTree tree = (JTree) evt.getSource();
	
	TreePath selectionPath = tree.getSelectionPath();
	if (selectionPath == null) {
	    return;
	}

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.
	    getLastPathComponent();
	Object lastComponent = node.getUserObject();
	System.out.println("Selection: " + lastComponent.toString());

	if ( ! node.isLeaf()) {
	    // select all children of this node
	    TreePath[] childPaths = new TreePath[node.getChildCount()];
	    Object[] selPathObjects = selectionPath.getPath();
	    int selPathObjectsLength = selPathObjects.length;
	    
	    int n = 0;
	    Enumeration children = node.children();
	    while (children.hasMoreElements()) {
		Object[] childPathObjects = new Object[selPathObjectsLength + 1]; 
		for (int m = 0; m < selPathObjectsLength; m++) {
		    childPathObjects[m] = selPathObjects[m];
		}
		childPathObjects[selPathObjectsLength] = children.nextElement();
		
		childPaths[n++] = new TreePath(childPathObjects);
	    }
	    tree.setSelectionPaths(childPaths);
	    
	}
    }
    
    public void treeCollapsed(TreeExpansionEvent evt) {
	System.out.println("Tree collapsed " + evt.getPath());
    }
    
    public void treeExpanded(TreeExpansionEvent evt) {
	System.out.println("Tree expanded " + evt.getPath());
    }
    
    public DefaultMutableTreeNode addDirectory(ServiceItem item) {
	DefaultTreeModel model = (DefaultTreeModel) directories.getModel();
	
	DefaultMutableTreeNode newNode =     
	    addDirectoryTreeNode(new ComparableServiceItem(item), model);
	return newNode;
    }
    
    public void addDirectoryElement(Object node, ServiceItem item) {
	final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node;
	final DefaultMutableTreeNode newNode =     
	    new DefaultMutableTreeNode(new ComparableServiceItem(item));
	Runnable addDirElmt = new Runnable() {
		public void run() {
		    parentNode.add(newNode);
		}
	    };
	// Swing isn't thread-safe: all model changes 
	// have to be placed in the event queue
	SwingUtilities.invokeLater(addDirElmt);
    }
    
    public void removeDirectory(ServiceItem item) {
 	DefaultTreeModel model = (DefaultTreeModel) directories.getModel();
	removeTreeItem(new ComparableServiceItem(item), model);
    }
    
    
    private DefaultMutableTreeNode addDirectoryTreeNode(final ComparableServiceItem item, 
							final DefaultTreeModel model) {
	final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(item);
	
	Runnable insertElement = new Runnable() {
		public void run() {
		    System.out.println("Inserting dir tree node " + item);
		    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		    
		    Enumeration elmts = root.children();
		    
		    int n = 0;
		    while (elmts.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) elmts.nextElement();
			if (item.compareTo(node.getUserObject()) <= 0) {
			    break;
			}
			n++;
		    }
		    model.insertNodeInto(newNode, root, n);
		}
	    };
	try {
	    // Swing isn't thread-safe: all model changes 
	    // have to be placed in the event queue
	    SwingUtilities.invokeAndWait(insertElement);
	} catch(InterruptedException e) {
	    e.printStackTrace();
	} catch(java.lang.reflect.InvocationTargetException e) {
	    e.printStackTrace();
	}
  	return newNode;
    }

    private DefaultMutableTreeNode removeTreeItem(final ComparableServiceItem item, 
						  final DefaultTreeModel model) {
	final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(item);

	Runnable removeElement = new Runnable() {
		public void run() {
		    System.out.println("Removing tree node " + item);
		    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		    
		    Enumeration elmts = root.children();
		    
		    DefaultMutableTreeNode foundNode = null;
		    while (elmts.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) elmts.nextElement();
			if (item.compareTo(node.getUserObject()) == 0) {
			    System.out.println("Removing directory");
			    node.removeFromParent();
			    return;
			}
		    }
		    System.out.println("Failed to remove directory");
		}
	    };
	try {
	    // Swing isn't thread-safe: all model changes 
	    // have to be placed in the event queue
	    SwingUtilities.invokeAndWait(removeElement);
	} catch(InterruptedException e) {
	    e.printStackTrace();
	} catch(java.lang.reflect.InvocationTargetException e) {
	    e.printStackTrace();
	}
  	return newNode;
    }



    /********************************** 
     * Source List manipulation methods
     */
    public void addSource(ServiceItem item) {
	DefaultListModel model = (DefaultListModel) sources.getModel();

	insertListItem(new ComparableServiceItem(item), model);
    }
    public void removeSource(final ServiceItem item) {
 	final DefaultListModel model = (DefaultListModel) sources.getModel();
	Runnable removeElement = new Runnable() {
		public void run() {
		    if (model.removeElement(new ComparableServiceItem(item))) {
			System.err.println("Removed source elmt " + item.service);
		    } else {
			System.err.println("Failed to remove source elmt " + item.service);
		    }
		}
	    };
	// Swing isn't thread-safe: all model changes 
	// have to be placed in the event queue
	SwingUtilities.invokeLater(removeElement);
   }



    /********************************
     * Sink list manipulation methods
     */

    public void addSink(ServiceItem item) {
	DefaultListModel model = (DefaultListModel) sinks.getModel();

	insertListItem(new ComparableServiceItem(item), model);
     }

    public void removeSink(final ServiceItem item) {
 	final DefaultListModel model = (DefaultListModel) sinks.getModel();
	Runnable removeElement = new Runnable() {
		public void run() {
		    if (model.removeElement(new ComparableServiceItem(item))) {
			System.err.println("Removed sink elmt " + item.service);
		    } else {
			System.err.println("Failed to remove sink elmt " + item.service);
		    }
		}
	    };
	// Swing isn't thread-safe: all model changes 
	// have to be placed in the event queue
	SwingUtilities.invokeLater(removeElement);
    }

    /*********************************
     * General list methods
     */
    public void valueChanged(ListSelectionEvent evt) {
	if (evt.getValueIsAdjusting()) {
	    return;
	}
    }


    private void insertListItem(final ComparableServiceItem item, 
				final DefaultListModel model) {
	Runnable insertElement = new Runnable() {
		public void run() {
		    Enumeration elmts = model.elements();
		    
		    int n = 0;
		    while (elmts.hasMoreElements()) {
			if (item.compareTo(elmts.nextElement()) <= 0) {
			    break;
			}
			n++;
		    }
		    model.add(n, item);
		}
	    };
	// there may be other insert's in the event queue, so we can't
	// just compare the current list with the item. We have to wait
	// till all the pending events have been processed, and then compare
	try {
	    SwingUtilities.invokeAndWait(insertElement);
	} catch(InterruptedException e) {
	    e.printStackTrace();
	} catch(java.lang.reflect.InvocationTargetException e) {
	    e.printStackTrace();
	}
    }

        
 }// ClientFrame
