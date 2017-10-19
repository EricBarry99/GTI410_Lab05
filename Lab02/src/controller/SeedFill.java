package controller;

import model.ImageX;
import model.Pixel;

import java.awt.*;
import java.util.Stack;
import view.HSVColorMediator;

public class SeedFill {

    public ImageX currentImage;
    private Pixel fillColor;
    private Pixel borderColor;
    private int hueThreshold;
    private int saturationThreshold;
    private int valueThreshold;

    public SeedFill(ImageX imageEntree,Pixel fillColor, Pixel borderColor ,int hueThreshold, int saturationThreshold, int valueThreshold){

        this.currentImage = imageEntree;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.hueThreshold = hueThreshold;
        this.saturationThreshold = saturationThreshold;
        this.valueThreshold = valueThreshold;
    }


    /*  Algorithme de remplissage par région interieur avec 4 voisins
	*	Prend en entré le point a la position du clique de la souris et la couleur de ce point
	*	Fonctionnement :
	*	On change la couleur de tous les points qui ont la même couleurs que le point d'origine et qui sont
	*	connecté entre eux par un des axes (NORD, SUD, EST, OUEST).
	*	L'algorithme est implémenté sous forme itérative a l'aide d'une stack de points.
	* */
    public void floodFill(Point pActuel, Pixel colorcible){

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
    public void Boundaryfill (Point pActuel, Pixel colorcible){

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
        color1HSV = HSVColorMediator.RGBtoHSV(color1.getRed(),color1.getGreen(),color1.getBlue());
        color2HSV = HSVColorMediator.RGBtoHSV(color2.getRed(),color2.getGreen(),color2.getBlue());

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


}
