import java.awt.*;
import java.util.*;

// an interface for all objects to be plotted
interface Plotable {
    public void plot(Graphics g, int xoffset, int yoffset);
}

// Canvas for plotting graph
class PlotCanvas2 extends Canvas {
	// size of plot area
	int width, height;
	// Axes and objects to be plotted
	Axis x_axis, y_axis;
	Vector<Plotable> objects;

	public PlotCanvas2(int wid, int hgt) {
		width = wid;
		height = hgt;
		x_axis = new Axis(true, width);
		y_axis = new Axis(false, height);
		objects = new Vector<Plotable>();
	}
	// add objects to plot
	public void addObject(Plotable obj) {
		objects.add(obj);
		repaint();
	}
	public void clearObjects() {
		objects.clear();
		repaint();
	}
	// redraw the canvas
	public void paint(Graphics g) {
		// draw axis
		int xoffset = (getWidth() - width) / 2;
		int yoffset = (getHeight() + height) / 2;
		x_axis.plot(g, xoffset, yoffset);
		y_axis.plot(g, xoffset, yoffset);
		// plot each object
		Iterator<Plotable> itr = objects.iterator();
		while(itr.hasNext())
			itr.next().plot(g, xoffset, yoffset);
	}
}

// Axis class for plotting X or Y axis
class Axis implements Plotable {
	// type and length of the axis
	boolean xAxis;
	int length, size=15;
	// Constructor
	public Axis(boolean isX, int len) {
		xAxis = isX;
		length = len;
	}
	// plot axis with arrow
	public void plot(Graphics g, int xoffset, int yoffset) {
		g.setColor(Color.BLACK);
		if ( xAxis ) {
			g.drawLine(xoffset-size, yoffset, xoffset+length+size, yoffset);
			g.fillArc(xoffset+length, yoffset-size, size*2, size*2, 160, 40);
		}
		else {
			g.drawLine(xoffset, yoffset+size, xoffset, yoffset-length-size);
			g.fillArc(xoffset-size, yoffset-length-size*2, size*2, size*2, 250, 40);
		}
	}
}

// Bar class defines for ploting a vertical line
class VerticalBar implements Plotable {
	// color, location, and length of the line
	Color color;
	int pos, length;
	// Constructor
	public VerticalBar(Color clr, int p, int len) {
		color = clr;
		pos = p; length = len;
	}
	public void plot(Graphics g, int xoffset, int yoffset) {
		g.setColor(color);
		g.drawLine(xoffset+pos, yoffset, xoffset+pos, yoffset-length);
	}
}
