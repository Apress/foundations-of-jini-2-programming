/**
 *	Copyright (C) Satoshi Konno 2002
 *	Minor changes Jan Newmarch 2004
 */

package clock.clock;

import clock.device.*;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.rmi.RemoteException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ClockPane extends JPanel
{
    private ClockDevice clockDev;
    private Color lastBlink = Color.BLACK;
    private DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss");

    public ClockPane(ClockDevice clockDev)
    {
	this.clockDev = clockDev;
	loadImage();
	initPanel();
    }
    
    ////////////////////////////////////////////////
    //	Background
    ////////////////////////////////////////////////
    
    private final static int DEFAULT_WIDTH = 200;
    private final static int DEFAULT_HEIGHT = 60;
    private final static String CLOCK_PANEL_IMAGE = "images/clock.jpg";
    private final static String CLOCK_PANEL_IMAGE_FILE = "resources/" + CLOCK_PANEL_IMAGE;
    
    private Image panelmage;
    private int imageWidth = DEFAULT_WIDTH;
    private int imageHeight = DEFAULT_HEIGHT;
    
    private void loadImage()
    {
	// Try to get the image form the local file system
	File f = new File(CLOCK_PANEL_IMAGE_FILE);
	try {
	    panelmage = ImageIO.read(f);
	    imageWidth = ((BufferedImage) panelmage).getWidth();
	    imageHeight = ((BufferedImage) panelmage).getHeight();
	    return;
	}
	catch (Exception e) {
	    // Not in local file system
	}

	// Try to get the image from classpath jar files
	java.net.URL url = getClass().getClassLoader().getResource(CLOCK_PANEL_IMAGE);
	if (url != null) {
	    ImageIcon icon = new ImageIcon(url); 
	    panelmage = icon.getImage();
	    imageWidth = icon.getIconWidth();
	    imageHeight = icon.getIconHeight();
	    return;
	}
	
	// couldn't find an image, leave panelmage as null
    }
    
    private Image getPaneImage()
    {
	return panelmage;
    }
    
    ////////////////////////////////////////////////
    //	Background
    ////////////////////////////////////////////////
    
    private void initPanel()
    {
	Image panelmage = getPaneImage();
	setPreferredSize(new Dimension(imageWidth, imageHeight));
    }
    
    ////////////////////////////////////////////////
    //	Font
    ////////////////////////////////////////////////
    
    private final static String DEFAULT_FONT_NAME = "Lucida Console";
    private final static int DEFAULT_TIME_FONT_SIZE = 48;
    private final static int DEFAULT_DATE_FONT_SIZE = 18;
    private final static int DEFAULT_SECOND_BLOCK_HEIGHT = 8;
    private final static int DEFAULT_SECOND_BLOCK_FONT_SIZE = 10;
    
    private Font timeFont = null;
    private Font dateFont = null;
    private Font secondFont = null;
    
    private Font getFont(Graphics g, int size)
    {
	Font font = new Font(DEFAULT_FONT_NAME, Font.PLAIN, size);
	if (font != null)
	    return font;
	return g.getFont();
    }
    
    private Font getTimeFont(Graphics g)
    {
	if (timeFont == null)
	    timeFont = getFont(g, DEFAULT_TIME_FONT_SIZE);
	return timeFont;
    }
    
    private Font getDateFont(Graphics g)
    {
	if (dateFont == null)
	    dateFont = getFont(g, DEFAULT_DATE_FONT_SIZE);
	return dateFont;
    }
    
    private Font getSecondFont(Graphics g)
    {
	if (secondFont == null)
	    secondFont = getFont(g, DEFAULT_SECOND_BLOCK_FONT_SIZE);
	return secondFont;
    }
    
    ////////////////////////////////////////////////
    //	paint
    ////////////////////////////////////////////////
    
    private void drawClockInfo(Graphics g)
    {
	int winWidth = getWidth();
	int winHeight = getHeight();
	
	boolean valid = false;
	try {
	    valid = clockDev.isValidTime();
	} catch(RemoteException e) {
	    // valid is already false
	}

	if (valid) {
	    g.setColor(Color.BLACK);
	} else {
	    if (lastBlink == Color.WHITE) {
		g.setColor(Color.BLACK);
		lastBlink = Color.BLACK;
	    } else {
		g.setColor(Color.WHITE);
		lastBlink = Color.WHITE;
	    }
	}
	
	//// Time String ////
	Date now = null;
	try {
	    now = clockDev.getTime();
	} catch(RemoteException e) {
	    now = new Date(0);
	}
	String timeStr = dateFormat.format(now);
	
	Font timeFont = getTimeFont(g);
	g.setFont(timeFont);
	
	FontMetrics timeFontMetric = g.getFontMetrics();
	Rectangle2D timeStrBounds = timeFontMetric.getStringBounds(timeStr, g);
	
	int timeStrWidth = (int)timeStrBounds.getWidth();		
	int timeStrHeight = (int)timeStrBounds.getHeight();
	int timeStrX = (winWidth-timeStrWidth)/2;
	int timeStrY = (winHeight+timeStrHeight)/2;
	int timeStrOffset = timeStrHeight/8/2;
	g.drawString(
		     timeStr,
		     timeStrX,
		     timeStrY);
	
	//// Date String ////
	
	String dateStr = "Time";
	
	Font dateFont = getDateFont(g);
	g.setFont(dateFont);
	
	FontMetrics dateFontMetric = g.getFontMetrics();
	Rectangle2D dateStrBounds = dateFontMetric.getStringBounds(dateStr, g);
	
	g.drawString(
		     dateStr,
		     (winWidth-(int)dateStrBounds.getWidth())/2,
		     timeStrY-timeStrHeight-timeStrOffset);
	

    }
    
    private void clear(Graphics g)
    {
	g.setColor(Color.GRAY);
	g.clearRect(0, 0, getWidth(), getHeight());
    }
    
    
    private void drawPanelImage(Graphics g)
    {
	if (getPaneImage() == null) {
	    return;
	}
	g.drawImage(getPaneImage(), 0, 0, null);
    }
    
    public void paint(Graphics g)
    {
	clear(g);
	drawPanelImage(g);
	drawClockInfo(g);
    }
}

