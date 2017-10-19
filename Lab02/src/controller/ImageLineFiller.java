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

package controller;


import model.*;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, �ric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.13 $
 */
public class ImageLineFiller extends AbstractTransformer {
	private ImageX currentImage;
	private Pixel fillColor = new Pixel(0xFFFFFFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageLineFiller() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				Point pt = e.getPoint();



				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}

				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {

					//On autorise la modification des pixels
					currentImage.beginPixelUpdate();

					//On choisis le bon remplissage en fonction du choix de l'utilisateur
					if(floodFill) floodFill(pt,currentImage.getPixel(pt.x,pt.y));
					else Boundaryfill(pt,currentImage.getPixel(pt.x,pt.y));


					currentImage.endPixelUpdate();											 	
					return true;
				}

			}
		}
		return false;
	}

	/*  Algorithme de remplissage par région interieur avec 4 voisins
	*	Prend en entré le point a la position du clique de la souris et la couleur de ce point
	*	Fonctionnement :
	*	On change la couleur de tous les points qui ont la même couleurs que le point d'origine et qui sont
	*	connecté entre eux par un des axes (NORD, SUD, EST, OUEST).
	*	L'algorithme est implémenté sous forme itérative a l'aide d'une stack de points.
	* */
	private void floodFill(Point pActuel, Pixel colorcible){

		// On créer la stack de points et on ajoute le point a la position du clique de la souris dans la stack
		Stack<Point> p = new Stack<Point>();
		p.push(pActuel);

			// on va itérer au travers de la stack tant qu'elle n'est pas vide
			while (!p.empty()) {

				// On récupère le point qui se trouve au-dessus de la stack et on set la couleur du point correspondant dans l'image
				pActuel = p.pop();
				currentImage.setPixel(pActuel.x, pActuel.y, fillColor);

				// Pour chaque axe (NORD, SUD, EST, OUEST) on regarde si le point suivant est de la même couleur que le point original (ou point cible).
				// On vérifie aussi si le point n'est pas déjà de la bonne couleur pour ne pas le set plusieurs fois pour rien
				// Si les critères sont remplis alors le point est ensuite ajouté sur la stack pour être traité..

				if(pActuel.y + 1 < currentImage.getImageHeight()){
					if (colorComparator(currentImage.getPixel(pActuel.x, pActuel.y + 1),colorcible) && currentImage.getPixel(pActuel.x, pActuel.y + 1).getARGB() != fillColor.getARGB()) {
						p.push(new Point(pActuel.x, pActuel.y + 1));
					}
				}
				if(0 <= pActuel.y - 1) {
					if (colorComparator(currentImage.getPixel(pActuel.x, pActuel.y - 1), colorcible) && currentImage.getPixel(pActuel.x, pActuel.y - 1).getARGB() != fillColor.getARGB()) {
						p.push(new Point(pActuel.x, pActuel.y - 1));
					}
				}
				if(pActuel.x + 1 < currentImage.getImageWidth()) {
					if (colorComparator(currentImage.getPixel(pActuel.x + 1, pActuel.y), colorcible) && currentImage.getPixel(pActuel.x + 1, pActuel.y).getARGB() != fillColor.getARGB()) {
						p.push(new Point(pActuel.x + 1, pActuel.y));
					}
				}
				if(0 <= pActuel.x - 1) {
					if (colorComparator(currentImage.getPixel(pActuel.x - 1, pActuel.y), colorcible) && currentImage.getPixel(pActuel.x - 1, pActuel.y).getARGB() != fillColor.getARGB()) {
						p.push(new Point(pActuel.x - 1, pActuel.y));
					}
				}


			}


	}

	/*  Algorithme de remplissage par bordure extérieure avec 4 voisins
	*	Prend en entré le point à la position du clique de la souris et la couleur de ce point
	*	Fonctionnement :
	*	On change la couleur de tous les points connecté entre eux par un des axes (NORD, SUD, EST, OUEST)
	*	qui ont une couleur différente de la couleur à remplir et aussi qui ont une couleur différente
	*	de la couleur de bordure.
	*	L'algorithme est implémenté sous forme itérative a l'aide d'une stack de points.
	* */
	private void Boundaryfill (Point pActuel, Pixel colorcible){

		// On créer la stack de points et on ajoute le point a la position du clique de la souris dans la stack
		Stack<Point> p = new Stack<Point>();
		p.push(pActuel);

			// on va itérer au travers de la stack tant qu'elle n'est pas vide
			while (!p.empty()) {
				pActuel = p.pop();
				currentImage.setPixel(pActuel.x, pActuel.y, fillColor);

				// Pour chaque axe (NORD, SUD, EST, OUEST) on regarde si le point suivant est de couleur différente de la couleur de remplissage
				// et de la couleur de la bordure.
				// Si les critères sont remplis alors le point est ensuite ajouté sur la stack pour être traité.

				if(pActuel.y + 1 < currentImage.getImageHeight()){
					if (!colorComparator(currentImage.getPixel(pActuel.x, pActuel.y + 1),fillColor) && !colorComparator(currentImage.getPixel(pActuel.x, pActuel.y + 1),borderColor)) {
						p.push(new Point(pActuel.x, pActuel.y + 1));
					}
				}
				if(0 <= pActuel.y - 1) {
					if (!colorComparator(currentImage.getPixel(pActuel.x, pActuel.y - 1), fillColor) && !colorComparator(currentImage.getPixel(pActuel.x, pActuel.y - 1), borderColor)) {
						p.push(new Point(pActuel.x, pActuel.y - 1));
					}
				}
				if(pActuel.x + 1 < currentImage.getImageWidth()) {
					if (!colorComparator(currentImage.getPixel(pActuel.x + 1, pActuel.y), fillColor) && !colorComparator(currentImage.getPixel(pActuel.x + 1, pActuel.y), borderColor)) {
						p.push(new Point(pActuel.x + 1, pActuel.y));
					}
				}
				if(0 <= pActuel.x - 1) {
					if (!colorComparator(currentImage.getPixel(pActuel.x - 1, pActuel.y), fillColor) && !colorComparator(currentImage.getPixel(pActuel.x - 1, pActuel.y), borderColor)) {
						p.push(new Point(pActuel.x - 1, pActuel.y));
					}
				}

			}


	}

	/*	Fonction de comparaison des couleurs avec seuil en format HSV
	*	Prend en entré les deux points à comparer
	* 	Fonctionnement :
	* 	On transforme les deux couleurs au format HSV puis on regarde si la première couleur
	* 	est dans le range de la deuxième couleur avec + ou - le seuil définit par les sliders pour chaque composantes
	* 	On retourne le résultat de la comparaison sous la forme d'un boolean
	* */
	private boolean colorComparator(Pixel color1, Pixel color2){

		if (color1 == null || color2 == null) return false;

		boolean H = false;
		boolean S = false;
		boolean V = false;
		double[] color1HSV = new double[3];
		double[] color2HSV = new double[3];

		//On transforme au format HSV
		color1HSV = RGBtoHSV(color1.getRed(),color1.getGreen(),color1.getBlue());
		color2HSV = RGBtoHSV(color2.getRed(),color2.getGreen(),color2.getBlue());

		//Pour les trois composantes on regarde si la première couleur est dans + ou - le range de la deuxième couleur
		if (((color1HSV[0]/2) + hueThreshold) >= (color2HSV[0]/2) && ((color1HSV[0]/2) - hueThreshold) <= (color2HSV[0]/2)){
			H = true;
		} else H = false;

		if (((color1HSV[1]*255) + saturationThreshold) >= (color2HSV[1]*255) && ((color1HSV[1]*255) - saturationThreshold) <= (color2HSV[1]*255)){
			S = true;
		} else S = false;

		if (((color1HSV[2]*255) + valueThreshold >= (color2HSV[2]*255)) && ((color1HSV[2]*255) - valueThreshold) <= (color2HSV[2]*255)){
			V = true;
		} else V = false;

		//On retourne true seulement si les trois composantes sont dans le range
		return (H && S && V);

	}







	/**
	 * Horizontal line fill with specified color
	 */
	private void horizontalLineFill(Point ptClicked) {
		Stack<Point> stack = new Stack<Point>();
		stack.push(ptClicked);
		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() &&
				!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				currentImage.setPixel(current.x, current.y, fillColor);
				
				// Next points to fill.
				Point nextLeft = new Point(current.x-1, current.y);
				Point nextRight = new Point(current.x+1, current.y);
				stack.push(nextLeft);
				stack.push(nextRight);
			}
		}
		// TODO EP In this method, we are creating many new Point instances. 
		//      We could try to reuse as many as possible to be more efficient.
		// TODO EP In this method, we could be creating many Point instances. 
		//      At some point we can run out of memory. We could create a new point
		//      class that uses shorts to cut the memory use.
		// TODO EP In this method, we could test if a pixel needs to be filled before
		//      adding it to the stack (to reduce memory needs and increase efficiency).
	}

	public double[] RGBtoHSV (double red, double green, double blue){

		double h = 0;
		double[] HSV = new double[3];
		double r = red/(double)255;
		double g = green/(double)255;
		double b = blue/(double)255;
		double minimum = Math.min(Math.min(r,g),b);
		double maximum = Math.max(Math.max(r,g),b);
		double c = maximum - minimum;

		if (c == 0) h = 0;
		else if (maximum == r) h = ((g - b)/c)%6;
		else if (maximum == g) h = ((b - r)/c) + 2;
		else if (maximum == b) h = ((r - g)/c) + 4;

		h = 60 * h;

		HSV[0] = h;
		if(c == 0)HSV[1] = 0;
		else HSV[1] = c/maximum;
		HSV[2] = maximum;
		return HSV;
	}
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}

}
