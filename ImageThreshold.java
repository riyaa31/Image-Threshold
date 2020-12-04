// Skeletal program for the "Image Threshold" assignment
// Written by:  Minglun Gong

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import java.lang.Math;

// Main class
public class ImageThreshold extends Frame implements ActionListener {
	BufferedImage input;
	int width, height;
	TextField texThres, texOffset;
	ImageCanvas source, target;
	PlotCanvas2 plot;
	final int GREY_LEVEL = 256;
	// Constructor
	public ImageThreshold(String name) {
		super("Image Histogram");
		// load image
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		// prepare the panel for image canvas.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		plot = new PlotCanvas2(256, 200);
		target = new ImageCanvas(width, height);
		//target.copyImage(input);
		target.resetImage(input);
		main.setLayout(new GridLayout(1, 3, 10, 10));
		main.add(source);
		main.add(plot);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		controls.add(new Label("Threshold:"));
		texThres = new TextField("128", 2);
		controls.add(texThres);
		Button button = new Button("Manual Selection");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Automatic Selection");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Otsu's Method");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Offset:"));
		texOffset = new TextField("10", 2);
		controls.add(texOffset);
		button = new Button("Adaptive Mean-C");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+400, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {

		if (((Button) e.getSource()).getLabel().equals("Manual Selection")) {

			//Threshold value to be used across the image.
			int thresholdValue;

			try {

				//Get threshold value from the text box.
				thresholdValue = Integer.parseInt(texThres.getText());

			} catch (Exception ex) {

				//If the threshold value is not accepted, set to 128.
				texThres.setText("128");
				thresholdValue = 128;
			}

			//Remove previous image from the plot canvas.
			plot.clearObjects();
			//Draw vertical line where threshold value is located.
			plot.addObject(new VerticalBar(Color.BLACK, thresholdValue, 100));

			for (int y = 0; y < height; y++) {

				for (int x = 0; x < width; x++) {

					//Construct a color object.
					Color clr = new Color(source.image.getRGB(x, y));

					// Get the RGB components.
					int red = clr.getRed();
					int green = clr.getGreen();
					int blue = clr.getBlue();

					//Set value for RGB based on if they are object points or background points.
					red = (red < thresholdValue) ? 0 : GREY_LEVEL - 1;
					green = (green < thresholdValue) ? 0 : GREY_LEVEL - 1;
					blue = (blue < thresholdValue) ? 0 : GREY_LEVEL - 1;

					//Use bit operations to encode the RGB color.
					target.image.setRGB(x, y, red << 16 | green << 8 | blue);

				}
			}

			target.repaint();
		}

		if (((Button) e.getSource()).getLabel().equals("Automatic Selection")) //implementing automatic selection button
		{
			//default values of red, green and blue
			int Red1 = 128;
			int Green1 = 128;
			int Blue1 = 128;
			int Red2 = 9999;
			int Green2 = 9999;
			int Blue2 = 9999;
			int Redmean1, Greenmean1, Bluemean1;
			int Redmean2, Greenmean2, Bluemean2;

			//The array buffer into which the components of the vector are
			//* stored. The capacity of the vector is the length of this array buffer,
			//* and is at least large enough to contain all the vector's elements.
			Vector<Integer> groupR1 = new Vector<Integer>();
			Vector<Integer> groupR2 = new Vector<Integer>();
			Vector<Integer> groupG1 = new Vector<Integer>();
			Vector<Integer> groupG2 = new Vector<Integer>();
			Vector<Integer> groupB1 = new Vector<Integer>();
			Vector<Integer> groupB2 = new Vector<Integer>();

			//Here, we implement the automatic threshold for RGB channel separately
			while (Math.abs(Red2 - Red1) > 1 && Math.abs(Green2 - Green1) > 1 && Math.abs(Blue2 - Blue1) > 1)
			{
				for (int i = 0; i < height; i++)
				{
					for (int j = 0; j < width; j++)
					{
						Color color = new Color(source.image.getRGB(j, i));
						int red = color.getRed();
						int green = color.getGreen();
						int blue = color.getBlue();
						if (red < Red1)
						{
							groupR1.add(red);
						}
						else
						{
							groupR2.add(red);
						}
						if (green < Green1)
						{
							groupG1.add(red);
						}
						else
						{
							groupG2.add(red);
						}
						if (blue < Blue1)
						{
							groupB1.add(red);
						}
						else
						{
							groupB2.add(red);
						}
					}
				}
				int sum = 0;
				for (int i : groupR1)
					sum += groupR1.get(i);

				Redmean1 = sum / groupR1.size();

				sum = 0;
				for (int i : groupR2)
					sum += groupR2.get(i);

				Redmean2 = sum / groupR2.size();

				sum = 0;
				for (int i : groupG1)
					sum += groupG1.get(i);

				Greenmean1 = sum / groupG1.size();

				sum = 0;
				for (int i : groupG2)
					sum += groupG2.get(i); /////////////

				Greenmean2 = sum / groupG2.size();
				sum = 0;
				for (int i : groupB1)
					sum += groupB1.get(i);

				Bluemean1 = sum / groupB1.size();
				sum = 0;
				for (int i : groupB2)
					sum += groupB2.get(i);

				Bluemean2 = sum / groupB2.size();
				Red2 = Red1;
				Green2 = Green1;
				Blue2 = Blue1;
				Red1 = (Redmean1 + Redmean2) / 2;
				Green1 = (Greenmean1 + Greenmean2) / 2;
				Blue1 = (Bluemean1 + Bluemean2) / 2;
			}
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					Color color = new Color(source.image.getRGB(x, y));
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();

					//Grey scale value is 256, hence subtracting 1 from it
					red = (red < Red1) ? 0 : 256 - 1;
					green = (green < Green1) ? 0 : 256 - 1;
					blue = (blue < Blue1) ? 0 : 256 - 1;

					//Sets a pixel in this BufferedImage to the specified RGB value
					target.image.setRGB(x, y, red << 16 | green << 8 | blue);
				}
			}
			target.repaint();
			plot.clearObjects();

			//creation of the Threshold line
			plot.addObject(new VerticalBar(Color.RED, Red1, 100));
			plot.addObject(new VerticalBar(Color.GREEN, Green1, 100));
			plot.addObject(new VerticalBar(Color.BLUE, Blue1, 100));
		}


		if (((Button) e.getSource()).getLabel().equals("Otsu's Method")) {

			/*
			Objectives to do:
			calculate weighted within-class variance
			calculate between class variance
			mean intensities of the classes
			 */
			int red = 0, green = 0, blue = 0;
			int redBackGround = 0, greenBackGround = 0, blueBackGround = 0;
			int redForeGround = 0, greenForeGround = 0, blueForeGround = 0;
			int grayScale = 256;
			int pixels = width * height;
			double redSum = 0, greenSum = 0, blueSum = 0;
			double redSum2 = 0, greenSum2 = 0, blueSum2 = 0;
			double redVariance = 0, greenVariance = 0, blueVariance = 0;
			double redThreshold = 0, greenThreshold = 0, blueThreshold = 0;

			int[] redHistogram = new int[grayScale];
			int[] greenHistogram = new int[grayScale];
			int[] blueHistogram = new int[grayScale];

			for (int y = 0, i = 0; y < height; y++) {
				for (int x = 0; x < width; x++, i++) {
					Color clr = new Color(source.image.getRGB(x, y));
					red = clr.getRed();
					green = clr.getGreen();
					blue = clr.getBlue();
					redHistogram[red]++;
					greenHistogram[green]++;
					blueHistogram[blue]++;
				}
			}

			for (int t = 0; t < 256; t++) {
				// let t = threshold t
				redSum += t * redHistogram[t];
				greenSum += t * greenHistogram[t];
				blueSum += t * blueHistogram[t];
			}



			for (int t = 0; t < 256; t++) {
				// let t = threshold t
				redBackGround += redHistogram[t];
				greenBackGround += greenHistogram[t];
				blueBackGround += blueHistogram[t];

				redForeGround = pixels - redBackGround;
				greenForeGround = pixels - greenBackGround;
				blueForeGround = pixels - blueBackGround;

				redSum2 += (float) (t * redHistogram[t]);
				greenSum2 += (float) (t * greenHistogram[t]);
				blueSum2 += (float) (t * blueHistogram[t]);

				// Mean intensities

				double redMBackGround = redSum2/redBackGround;
				double greenMBackGround = greenSum2/greenBackGround;
				double blueMBackGround = blueSum2/blueBackGround;

				double redMForeGround = (redSum - redSum2)/redForeGround;
				double greenMForeGround = (greenSum - greenSum2)/greenForeGround;
				double blueMForeGround = (blueSum - blueSum2)/blueForeGround;

				//Between Variance
				double r =(redMBackGround - redMForeGround);
				double g =(greenMBackGround - greenMForeGround);
				double b = (blueMBackGround - blueMForeGround);

				double red_between_class = (double) redBackGround * (double) redForeGround * (Math.pow(r,2));
				double green_between_class = (double) greenBackGround * (double) greenForeGround * (Math.pow(g,2));
				double blue_between_class = (double) blueBackGround * (double) blueForeGround * (Math.pow(b,2));

				if (red_between_class > redVariance) {
					redVariance = red_between_class;
					redThreshold = t;
				}
				if (green_between_class > greenVariance) {
					greenVariance = green_between_class;
					greenThreshold = t;
				}

				if (blue_between_class > blueVariance) {
					blueVariance = blue_between_class;
					blueThreshold = t;
				}
			}

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color clr = new Color(source.image.getRGB(x, y));
					int red2 = clr.getRed();
					int green2 = clr.getGreen();
					int blue2 = clr.getBlue();
					red = (red2 < redThreshold) ? 0 : GREY_LEVEL - 1;
					green = (green2 < greenThreshold) ? 0 : GREY_LEVEL - 1;
					blue = (blue2 < blueThreshold ) ? 0 : GREY_LEVEL - 1;
					target.image.setRGB(x, y, red << 16 | green << 8 | blue);
				}
			}

			target.repaint();

		}
		if (((Button) e.getSource()).getLabel().equals("Adaptive Mean-C")) {
			int c = 7;
			int w = 3;
			int pixSize = ((2*w+1)*(2*w+1));
			for (int i = w; i < height - w; i++) {
				for (int j = w; j < width - w; j++) {
					int redSum = 0, greenSum = 0, blueSum = 0;
					for (int k = -w; k <= w ; k++) {
						for (int l = -k; l <= w ; l++) {
							Color clr = new Color(source.image.getRGB(j + l, i + k));
							int red = clr.getRed();
							int green = clr.getGreen();
							int blue = clr.getBlue();
							redSum += red;
							greenSum += green;
							blueSum += blue;
						}
					}
					Color clr = new Color(source.image.getRGB(j, i));
					int red = clr.getRed();
					int green = clr.getGreen();
					int blue = clr.getBlue();
					int redThreshold = (redSum / pixSize) - 7;
					int greenThreshold = (greenSum / pixSize) - 7;
					int blueThreshold = (blueSum / pixSize) - 7;
					red = (red < redThreshold) ? 0 : 256 - 1;
					green = (green < greenThreshold) ? 0 : 256 - 1;
					blue = (blue < blueThreshold) ? 0 : 256 - 1;
					target.image.setRGB(j, i, red << 16 | green << 8 | blue);
				}
			}
			target.repaint();
		}
	}

	public static void main(String[] args) {
		new ImageThreshold(args.length==1 ? args[0] : "fingerprint.png");
	}
}
