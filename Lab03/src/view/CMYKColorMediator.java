/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*
 * source consult�e
 * http://www.rapidtables.com/convert/color/cmyk-to-rgb.htm
 * 
 * 
 */
package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider keyCS;
	
	float cyan;
	float magenta;
	float yellow;
	float key;
	
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage keyImage;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		float[] cmykColors = rgbTocmyk(result.getPixel().getRed(), result.getPixel().getGreen(), result.getPixel().getBlue());
		
		this.cyan = cmykColors[0];
		this.magenta = cmykColors[1];
		this.yellow = cmykColors[2];
		this.key = cmykColors[3];
		this.result = result;
		
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		keyImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeCyanImage(cyan, magenta, yellow, key);
		computeMagentaImage(cyan, magenta, yellow, key);
		computeYellowImage(cyan, magenta, yellow, key); 	
		computeKeyImage(cyan, magenta, yellow, key); 	
	}
	

	/*
	 * @see View.SliderObserver#update(float)
	 */
	public void update(ColorSlider s, int v) {
		
		float val =  v/(float)255;
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateKey = false;
		
		if (s == cyanCS && val != cyan) {
			cyan = val;
			updateMagenta = true;
			updateYellow = true;
			updateKey = true;
		}
		
		if (s == magentaCS && val != magenta) {
			magenta = val;
			updateCyan = true;
			updateYellow = true;
			updateKey = true;
		}
		
		if (s == yellowCS && val != yellow) {
			yellow = val;
			updateCyan = true;
			updateMagenta = true;
			updateKey = true;
		}
		
		if (s == keyCS && val != key) {
			key = val;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		
		if (updateCyan) {
			computeCyanImage(cyan, magenta, yellow, key);
		}
		
		if (updateMagenta) {
			computeMagentaImage(cyan, magenta, yellow, key);
		}
		
		if (updateYellow) {
			computeYellowImage(cyan, magenta, yellow, key);
		}
		
		if (updateKey) {
			computeKeyImage(cyan, magenta, yellow, key);
		}
		
		int[] rgbColors = cmykTorgb(cyan, magenta, yellow, key);		
		Pixel pixel = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		result.setPixel(pixel);
	}
	
	
	public void computeCyanImage(float cyan, float magenta, float yellow, float key) { 
		int[] rgbColors;
		Pixel p;

			for (int i = 0; i<imagesWidth; ++i) {
			rgbColors = cmykTorgb((float)(i/(float)imagesWidth), magenta, yellow, key);
			p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
			//p.setRed((int)(((float)i / (float)imagesWidth)*255.0));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}
	
	public void computeMagentaImage(float cyan, float magenta, float yellow, float key) { 
		int[] rgbColors;
		Pixel p ;
		
		for (int i = 0; i<imagesWidth; ++i) {
			//p.setGreen((int)(((float)i / (float)imagesWidth)*255.0));
			rgbColors = cmykTorgb(cyan, (float)(i/(float)imagesWidth), yellow, key);
			p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}
	
	public void computeYellowImage(float cyan, float magenta, float yellow, float key) { 
		int[] rgbColors ;
		Pixel p ;
		
		for (int i = 0; i<imagesWidth; ++i) {
			//p.setBlue((int)(((float)i / (float)imagesWidth)*255.0));
			rgbColors = cmykTorgb(cyan, magenta, (float)(i/(float)imagesWidth), key);
			p =new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}
	 
	public void computeKeyImage(float cyan, float magenta, float yellow, float key) { 
		
		int[] rgbColors;

		Pixel p;
		
		for (int i = 0; i<imagesWidth; ++i) {
			rgbColors =  cmykTorgb(cyan, magenta, yellow, i/(float)imagesWidth);
			p = new Pixel(rgbColors[0],rgbColors[1],rgbColors[2],255);
			int rgb = p.getARGB();
			
		/*
		 * 	reverse
			for (int j = imagesHeight-1; j>=0; j--) {
				keyImage.setRGB(imagesWidth-i-1, j, rgb);
			}	
			*/
			
			for (int j = 0; j<imagesHeight; ++j) {
				keyImage.setRGB(i, j, rgb);
			}
			
		}
		if (keyCS != null) {
			keyCS.update(keyImage);
		}
	}
	
	
	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getKeyImage() {
		return keyImage;
	}
	
	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setKeyCS(ColorSlider slider) {
		keyCS = slider;
		slider.addObserver(this);
	}
	
	/**
	 * @return
	 */
	public float getYellow() {
		return yellow;
	}

	/**
	 * @return
	 */
	public float getMagenta() {
		return magenta;
	}

	/**
	 * @return
	 */
	public float getCyan() {
		return cyan;
	}

	public float getKey() {
		return key;
	}

	
	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		
		int[] rgbColors = cmykTorgb(cyan, magenta, yellow, key);		
		Pixel currentColor = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		float [] CMYK = rgbTocmyk(result.getPixel().getRed(),result.getPixel().getGreen(),result.getPixel().getBlue());

		cyan = CMYK[0];
		magenta = CMYK[1];
		yellow = CMYK[2];
		key = CMYK[3];

		cyanCS.setValue((int)CMYK[0]*255);
		magentaCS.setValue((int)CMYK[1]*255);
		yellowCS.setValue((int)CMYK[2]*255);
		keyCS.setValue((int)CMYK[3]*255);
	//	keyCS.setValue(250);
		
		computeCyanImage(cyan, magenta, yellow, key);
		computeMagentaImage(cyan, magenta, yellow, key);
		computeYellowImage(cyan, magenta, yellow, key);
		computeKeyImage(cyan, magenta, yellow, key);

		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
	
	/*
	 * inspired from
	 * http://www.rapidtables.com/convert/color/rgb-to-cmyk.htm
	 * https://stackoverflow.com/questions/4982210/find-the-max-of-3-numbers-in-java-with-different-data-types
	 */
	public float[] rgbTocmyk(int r, int g,int b) {
		float R = (float) r/255; // check for correct division
		float G = (float) g/255;
		float B = (float) b/255;
		
		float K = 1-Math.max(R, Math.max(G, B));		
		float C = (1-R-K) / (1-K);
		float M = (1-G-K) / (1-K);
		float Y = (1-B-K) / (1-K);
		return (new float[] {C,M,Y,K});
	}
	
	/*
	 * inspired from
	 * http://www.rapidtables.com/convert/color/cmyk-to-rgb.htm
	 */
	public int[] cmykTorgb(float c, float m, float y, float k) {
		int R = (int) Math.ceil(255*(1-c)*(1-k));
		int G = (int) Math.ceil(255*(1-m)*(1-k));
		int B = (int) Math.ceil(255*(1-y)*(1-k));
		return new int[] {R,G,B};
	}
}
