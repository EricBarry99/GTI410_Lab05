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
/*
        this.imageDouble = new ImageDouble(4,4);
        PixelDouble pixNoir = new PixelDouble();
        pixNoir.setRed(120);
        pixNoir.setBlue(120);
        pixNoir.setGreen(120);

        PixelDouble pixBlanc = new PixelDouble();
        pixBlanc.setRed(255);
        pixBlanc.setBlue(255);
        pixBlanc.setGreen(255);

        this.imageDouble.setPixel(0,0, pixBlanc);
        this.imageDouble.setPixel(0,1, pixNoir);
        this.imageDouble.setPixel(0,2, pixNoir);
        this.imageDouble.setPixel(0,3, pixBlanc);

        this.imageDouble.setPixel(1,0, pixBlanc);
        this.imageDouble.setPixel(1,1, pixNoir);
        this.imageDouble.setPixel(1,2, pixNoir);
        this.imageDouble.setPixel(1,3, pixBlanc);

        this.imageDouble.setPixel(2,0, pixBlanc);
        this.imageDouble.setPixel(2,1, pixNoir);
        this.imageDouble.setPixel(2,2, pixNoir);
        this.imageDouble.setPixel(2,3, pixBlanc);

        this.imageDouble.setPixel(3,0, pixBlanc);
        this.imageDouble.setPixel(3,1, pixNoir);
        this.imageDouble.setPixel(3,2, pixNoir);
        this.imageDouble.setPixel(3,3, pixBlanc);
*/
    }

    public ImageDouble ManageBorder(String borderType) {

        if (borderType.equals("copy")) {

            int width = this.imageDouble.getImageWidth()+2;
            int height = this.imageDouble.getImageHeight()+2;
            ImageDouble imageBordee = new ImageDouble(width, height);

            System.out.println("------------------------");
            displayImageBordee(this.imageDouble);
            System.out.println("------------------------");

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    // le centre
                    if((i > 0 && i < width-1) && (j > 0 && j < height-1)) {
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-1, j-1));
                    }
                }
            }
            System.out.println("------------------------");
            displayImageBordee(imageBordee);

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
                System.out.println("------------------------");
                displayImageBordee(imageBordee);
            }


/*
                    if ((i == 0) && (j > 0 && j < height-1)){   // cote gauche
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j-1));
                    } else if ((i == width - 1) && (j > 0 && j < height-1)){    // cote droit
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-2, j-1));
                    }
*/
/*
                    if (j == 0) { // le haut
                        imageBordee.setPixel(i, j, imageBordee.getPixel(i, j+1));
                    } else if (j == height - 1) {  // le bas
                        imageBordee.setPixel(i, j, imageBordee.getPixel(i, j-2));
                    }
*/
/*


                    else if((i == 0) && (j > 0 && j < height-1)){   // cote gauche
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j-1));
                    } else if ((i == width - 1) && (j > 0 && j < height-1)){    // cote droit
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i-2, j-1));
                    }


                    if (j == 0) { // le haut
                        imageBordee.setPixel(i, j, imageBordee.getPixel(i, j+1));
                    } else if (j == height - 1) {  // le bas
                        imageBordee.setPixel(i, j, imageBordee.getPixel(i, j-2));
                    }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    if (i == 0) {   // si i = 0: on copie tout les y de x=0 vers x'=0
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j));
                    } else if (i == width - 1) {    // si i vaut Xmax:on copie tout les y de x=Xmax vers X'=Xmax+1
                        imageBordee.setPixel(i + 1, j, this.imageDouble.getPixel(i, j));
                    }

                    if (j == 0) { // si j = 0; on copie tout les y=0 vers y'=0
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j));
                    } else if (j == height - 1) {
                        imageBordee.setPixel(i, j + 1, this.imageDouble.getPixel(i, j));
                    }

                    imageBordee.setPixel(i + 1, j + 1, this.imageDouble.getPixel(i, j));
                }
            }
            */

            return imageBordee;
        }
        return null;
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