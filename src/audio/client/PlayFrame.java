
/**
 * PlayFrame.java
 *
 *
 * Created: Wed Jul 16 09:20:37 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.client;

import audio.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.jini.core.event.RemoteEvent;
import java.rmi.Remote;
import net.jini.core.event.RemoteEventListener;
import java.rmi.MarshalledObject;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.event.*;

import net.jini.core.entry.Entry;
import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.factory.JDialogFactory;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;
import java.util.Set;
import java.util.Iterator;

import net.jini.core.lookup.ServiceItem;

/**
 * Play a selection of sources to a single sink
 */

public class PlayFrame extends JFrame implements ActionListener,
						 ChangeListener,
						 RemoteEventListener {
    private ServiceItem[] sources;
    private ServiceItem sinkItem;
    private Sink sink;
    private Source source;

    private boolean firstTime = true;

    private JList sourceList = new JList();
    private JButton stopBtn = new JButton("Stop");
    private JButton stopAllBtn = new JButton("Stop all");
    private JLabel sinkLabel = new JLabel();
    private JSlider sourceVolumeCtl = new JSlider();
    private JSlider sinkVolumeCtl = new JSlider();

    private DefaultListModel  model;
    private Remote proxy;
    private GUIClient client;

    public PlayFrame(GUIClient client) {
	setTitle("Player");
	this.client = client;

	JPanel bottomPanel = new JPanel();
	bottomPanel.setLayout(new BorderLayout());
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());
	contentPane.add(sinkLabel, BorderLayout.NORTH);
	contentPane.add(sourceList, BorderLayout.CENTER);
	contentPane.add(bottomPanel, BorderLayout.SOUTH);

	JPanel volumeControls = new JPanel();
	volumeControls.setLayout(new GridLayout(1, 2));
	volumeControls.add(sourceVolumeCtl);
	volumeControls.add(sinkVolumeCtl);

	JPanel buttons = new JPanel();
	buttons.setLayout(new GridLayout(1,3));
	buttons.add(stopBtn);
	buttons.add(stopAllBtn);

	bottomPanel.add(volumeControls, BorderLayout.NORTH);
	bottomPanel.add(buttons, BorderLayout.SOUTH);

	stopBtn.addActionListener(this);
	stopAllBtn.addActionListener(this);

	LabelCellRenderer labelRend = new LabelCellRenderer();
    
	sourceList.setCellRenderer(labelRend);
	model = new DefaultListModel();
	sourceList.setModel(model);

	sourceVolumeCtl.setEnabled(false);
	sinkVolumeCtl.setEnabled(false);
	sourceVolumeCtl.addChangeListener(this);
	sinkVolumeCtl.addChangeListener(this);
	sourceVolumeCtl.setToolTipText("Set volume on the source");
	sinkVolumeCtl.setToolTipText("Set volume on the sink");

	setSize(400, 300);
	setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
	if (evt.getSource() == stopBtn) {
	    try {
		sink.stop();
		sinkVolumeCtl.setEnabled(false);
		sourceVolumeCtl.setEnabled(false);
	    } catch(RemoteException e) {
		e.printStackTrace();
	    } catch(NotRecordingException e) {
		e.printStackTrace();
	    }
	} else if (evt.getSource() == stopAllBtn) {

	    /* This doesn;t work right
	    // kill the rest of the playlist by removing
	    // our listener and putting in a new one with
	    // null playback
	    try {
		sink.removeSinkListener((RemoteEventListener) proxy);
		System.out.println("Removed listener " + proxy);
		sink.addSinkListener((RemoteEventListener) proxy, 
				     new MarshalledObject(
					 new SourceSink(null, null)));
		System.out.println("Added listener with null playback");
	    } catch(RemoteException e) {
		// ignore
		e.printStackTrace();
	    } catch(NoSuchListenerException e) {
		System.err.println("Can't change listener " + proxy);
	    } catch(IOException e) {
		System.err.println("Can't marshall null " + proxy);
	    }
	    */

	    // to fix above... when the stop event arrives, we will try to play but just terminate
	    sources = null;

	    // now stop the play
	    try {
		sink.stop();
		sinkVolumeCtl.setEnabled(false);
		sourceVolumeCtl.setEnabled(false);
	    } catch(RemoteException e) {
		e.printStackTrace();
	    } catch(NotRecordingException e) {
		e.printStackTrace();
	    }
	}
    }

    public void stateChanged(ChangeEvent e) {
	JSlider slider = (JSlider) e.getSource();
	int vol = slider.getValue();
	VolumeControl volCtl = null;
	if (slider == sinkVolumeCtl) {
	    volCtl = (VolumeControl) sink;
	} else if (slider == sourceVolumeCtl) {
	    volCtl = (VolumeControl) source;
	}
	try {
	    volCtl.setVolume(vol);
	} catch(RemoteException ex) {
	    // ignore
	}
    }

    public void play(ServiceItem[] sources, ServiceItem sinkItem) {
	if ((sources == null) || (sources.length == 0)) {
	    System.out.println("Play: null sources");
	    // dispose();
	    Runnable doDispose = new Runnable() {
		    public void run() {
			dispose();
		    }
		};
	    SwingUtilities.invokeLater(doDispose);

	    return;
	}

	this.sources = sources;
	this.sinkItem = sinkItem;
	sink = (Sink) sinkItem.service;
	source = (Source) sources[0].service;

	sinkLabel.setText("Playing to " + sinkItem.toString());

	model.clear();
	for (int n = 0; n < sources.length; n++) {
	    model.addElement(sources[n]);
	}

	// just give the event source something it can put in a table
	MarshalledObject handback = null;
	try {
	    handback = new MarshalledObject(null);
	} catch(java.io.IOException e) {
	    e.printStackTrace();
	    return;
	}
	/*
	ServiceItem[] rest = new ServiceItem[sources.length - 1];
	for (int n = 0; n < rest.length; n++) {
	    rest[n] = sources[n + 1];
	}
	MarshalledObject handback = null;
	try {
	    handback = new MarshalledObject(new SourceSink(rest, sinkItem));
	} catch(java.io.IOException e) {
	    e.printStackTrace();
	    return;
	}
	*/

	try {
	    // this is a bit of a hack
	     if (firstTime) {
		if (proxy == null) {
		    proxy = client.export(this);
		}
		// source.addSourceListener((RemoteEventListener) proxy, null);
		System.out.println("Added source " + source + " proxy " + proxy);
		sink.addSinkListener((RemoteEventListener) proxy, handback);
		System.out.println("Added sink " + sink + " proxy " + proxy + " handback " + 
				   "null" /*((SourceSink) handback.get()).sources*/ );
		firstTime = false;
	    }
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
	    System.out.println("Playing" + sources[0].attributeSets[0]);

	    // check for UI elements
	    checkUI(sinkItem);

	    source.play();
	    sink.record();
	} catch(AlreadyPlayingException e) {
	    JOptionPane.showMessageDialog(null,
					  "Source already playing",
					  "Play error",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	} catch(AlreadyRecordingException e) {
	    JOptionPane.showMessageDialog(null,
					  "Sink already recording",
					  "Record error",
					  JOptionPane.ERROR_MESSAGE);
	    return;
	} catch(Exception e) {
	    // IncompatableSink/Source
	    e.printStackTrace();
	    return;
	}

	System.out.println("Checking VC " + sink);
	// printInterfaces(sink.getClass());
	if (source instanceof VolumeControl) {
	    System.out.println("Source is VC");
	    enableVolumeControl( (VolumeControl) source, sourceVolumeCtl);
	}
	if (sink instanceof VolumeControl) {
	    System.out.println("Sink is VC");
	    enableVolumeControl( (VolumeControl) sink, sinkVolumeCtl);
	}
    }

    /*
    private void printInterfaces(Class cls) {
	Class[] classes = cls.getInterfaces();
	for (int n = 0; n < classes.length; n++) {
	    System.out.println(classes[n].getName());
	}
	cls = cls.getSuperclass();
	if (cls != null) {
	    printInterfaces(cls);
	}
    }	
    */

    public void notify(RemoteEvent evt) {
	Object src = evt.getSource();
	System.out.println("Event notified " + src);
	System.out.println("Event seq id " + evt.getSequenceNumber());
	if ((src instanceof Sink) && 
	    (evt.getID() == Sink.STOP)) {
	    System.out.println("Sink stopped event");

	    ServiceItem[] rest =  null;
	    if (sources != null) {
		rest = new ServiceItem[sources.length - 1];
		for (int n = 0; n < rest.length; n++) {
		    rest[n] = sources[n + 1];
		}
	    }
	    play(rest, sinkItem);

	    /* The following code is broken, don't know why 
	       It just removes too many off the playlist
	    Sink sink = (Sink) src;

	    try {
		sink.removeSinkListener((RemoteEventListener) proxy);
		System.out.println("Removed listener " + proxy);
	    } catch(RemoteException e) {
		// ignore
		e.printStackTrace();
	    } catch(NoSuchListenerException e) {
		System.err.println("Can't remove listener " + proxy);
	    }

	    MarshalledObject handback = evt.getRegistrationObject();
	    SourceSink ss = null;
	    try {
		ss = (SourceSink) handback.get();
	    } catch(Exception e) {
		e.printStackTrace();
		return;
	    }
	    if (ss == null) {
		// no more playlist
		return;
	    }

	    final ServiceItem[] sources = ss.sources;
	    final ServiceItem sinkItem = ss.sink;
	    System.out.println("  stop -> play: sources " + sources + " sink " + sink);
	    Runnable doPlay = new Runnable() {
		    public void run() {
			play(sources, sinkItem);
		    }
		};
	    SwingUtilities.invokeLater(doPlay);
	    */
	} else if ((src instanceof Source) && 
	    (evt.getID() == Source.STOP)) {
	    System.out.println("Source stopped event");
	}
    }

    private void enableVolumeControl(VolumeControl vol, JSlider slider) {
	int maxVol = 0;
	int currVol = 0;
	try {
	    maxVol = vol.getMaxVolume();
	    if (maxVol <= 0) return;

	    currVol = vol.getVolume();
	    System.out.println("Current vol: " + currVol);
	    if (currVol < 0 || currVol > maxVol) return;
	} catch (RemoteException e) {
	    e.printStackTrace();
	    return;
	}
	slider.setMinimum(0);
	slider.setMaximum(maxVol);
	slider.setValue(currVol);
	slider.setEnabled(true);
    }


    /************************************
     * Check for UI entries
     */
    private void checkUI(ServiceItem item) {
        // Find and check the UIDescriptor's
        Entry[] attributes = item.attributeSets;
	System.out.println("Entries: " + attributes.length);
        for (int m = 0; m < attributes.length; m++) {
            Entry attr = attributes[m];
	    System.out.println("Checking Entry " + attr);
            if (attr instanceof UIDescriptor) {
		System.out.println("Found a UI");
                // does it deliver a Swing Dialog?
		// how do we decide if we want a Frame or Dialog?
                checkForSwingDialog(item, (UIDescriptor) attr);
            }
        }
    }
   
    private void checkForSwingDialog(ServiceItem item, UIDescriptor desc) {
        Set attributes = desc.attributes;
        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            // search through the attributes, to find a UIFactoryTypes
            Object obj = iter.next();
            if (obj instanceof UIFactoryTypes) {
                UIFactoryTypes types = (UIFactoryTypes) obj;
                // see if it produces a Swing Dialog Factory
                if (types.isAssignableTo(JDialogFactory.class)) {
                    JDialogFactory factory = null;
                    try {
                        factory = (JDialogFactory) desc.getUIFactory(this.getClass().
                                                                  getClassLoader());
                    } catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    System.out.println("calling dialog with " + item);
		    // how do we decide if it should be modal?
                    JDialog dialog = factory.getJDialog(item, this, true);
                    dialog.setVisible(true);
                } 
            }
        }
    }
}// PlayFrame
