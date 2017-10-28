package controller;

import model.ImageDouble;
import model.ImageX;

public class BorderManager {

    public ImageDouble imageDouble;
    public ImageX imageX;

    public BorderManager(ImageDouble imageDouble) {
        this.imageDouble = imageDouble;
    }

    public ImageDouble ManageBorder(String borderType) {

        if (borderType.equals("copy")) {

            int width = this.imageDouble.getImageWidth();
            int height = this.imageDouble.getImageHeight();
            ImageDouble imageBordee = new ImageDouble(width + 2, height + 2);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    if (i == 0) {   // si i = 0: on copie tout les y de x=0 vers x'=0
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j));
                    } else if (i == width - 1) {    // si i vaut Xmax:on copie tout les y de x=Xmax vers X'=Xmax+1
                        imageBordee.setPixel(i + 1, j, this.imageDouble.getPixel(i, j));
                    } else if (j == 0) { // si j = 0; on copie tout les y=0 vers y'=0
                        imageBordee.setPixel(i, j, this.imageDouble.getPixel(i, j));
                    } else if (j == height - 1) {
                        imageBordee.setPixel(i, j + 1, this.imageDouble.getPixel(i, j));
                    } else {
                        imageBordee.setPixel(i + 1, j + 1, this.imageDouble.getPixel(i, j));
                    }
                }
            }

            return imageBordee;
        }
        return null;
    }


    public void displayImageBordee(ImageDouble imageDouble) {
        int width = this.imageDouble.getImageWidth();
        int height = this.imageDouble.getImageHeight();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                System.out.print("(" + i +"," + j + ") [" + imageDouble.getPixel(i, j).getRed() + ',' + imageDouble.getPixel(i, j).getGreen() + ',' + imageDouble.getPixel(i, j).getBlue() + "], ");
            }
            System.out.println("");
        }
    }
}