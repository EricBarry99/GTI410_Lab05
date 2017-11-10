package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class BorderManager {

    public ImageDouble imageDouble;
    public ImageX imageX;

    public BorderManager(ImageDouble imageDouble) {
        this.imageDouble = imageDouble;
    }

    public ImageDouble ManageBorder(String borderType) {

        // si l'algorithme par copie est choisi
        if (borderType.equals("copy") || borderType.equals("Copy")) {

            int width = this.imageDouble.getImageWidth()+2;
            int height = this.imageDouble.getImageHeight()+2;
            ImageDouble imageBordee = new ImageDouble(width, height);

            // on commence par copier l'image au complet dans la nouvelle matrice
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    // le centre
                    if((i > 0 && i < width-1) && (j > 0 && j < height-1)) {
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-1, j-1));
                    }
                }
            }

            // puis on parcours les bords en ajoutant des valeurs issues de l'image originale
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    if((i == 0) && (j == 0 )){ // coin haut gauche
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j));
                    }
                    else if((i == 0) && (j == height - 1)) {// coin haut droit
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j-2));
                    }
                    else if((i == width-1) && (j == height-1)) {// coin bas droit
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-2, j-2));
                    }
                    else if((i == width -1) && (j == 0)){ // coin bas gauche
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-2, j));
                    }
                    else{
                        if((i == 0) && (j > 0 && j < height-1)){// cote haut
                            imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j-1));
                        }
                        else if((i > 0 && i < width-1) && (j == 0)){   // cote gauche
                            imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-1, j));
                        }
                        else if((i > 0 && i < width-1) && (j == height-1)) {// cote droit
                            imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-1, j-2));
                        }
                        else if((i == width-1) && (j > 0 && j < height-1)) {// cote bas
                            imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-2, j-1));
                        }
                    }
                }
            }
            return imageBordee;
        }
        else{
            return this.imageDouble;
        }
    }


    public void displayImageBordee(ImageDouble imageDouble) {
        int width = imageDouble.getImageWidth();
        int height = imageDouble.getImageHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print("(" + i +"," + j + ") [" + imageDouble.getPixel(i, j).getRed() + ',' + imageDouble.getPixel(i, j).getGreen() + ',' + imageDouble.getPixel(i, j).getBlue() + "], ");
            }
            System.out.println("");
        }
    }
}