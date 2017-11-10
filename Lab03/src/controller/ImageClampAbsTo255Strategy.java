
package controller;

import model.*;

/**
 * <p>Title: ImageClampStrategy</p>
 * <p>Description: Image-related strategy</p>
 * <p>Copyright: Copyright (c) 2004 Colin Barr�-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.8 $
 */
public class ImageClampAbsTo255Strategy extends ImageConversionStrategy {
    /**
     * Converts an ImageDouble to an ImageX using a clamping strategy (ABS and normalize to 255).
     */
    public ImageX convert(ImageDouble image) {
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();
        PixelDouble tempPixel;
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;
        newImage.beginPixelUpdate();

        //La conversion se fait maintenant en prenant la valeur absolue des valeurs du pixels
        //On normalise ensuite entre 0 et 255
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {

                curPixelDouble = image.getPixel(x,y);
                tempPixel = curPixelDouble;

                //On prend la valeurs absolue pour chaque couleurs
                tempPixel.setBlue(Math.abs(curPixelDouble.getBlue()));
                tempPixel.setGreen(Math.abs(curPixelDouble.getGreen()));
                tempPixel.setRed(Math.abs(curPixelDouble.getRed()));

                //On cherche la valeur maximum
                double maxValue = Math.max(tempPixel.getBlue(),Math.max(tempPixel.getGreen(),tempPixel.getRed()));

                //Si la valeur maximum est superieur a 255 on normalise vers 255
                if(maxValue > 255){
                    tempPixel.setBlue((curPixelDouble.getBlue()/maxValue)*255);
                    tempPixel.setGreen((curPixelDouble.getGreen()/maxValue)*255);
                    tempPixel.setRed((curPixelDouble.getRed()/maxValue)*255);
                }

                //On set les pixels de la nouvelle image
                newImage.setPixel(x, y, new Pixel((int)tempPixel.getRed(),
                        (int)tempPixel.getGreen(),
                        (int)tempPixel.getBlue(),
                        (int)tempPixel.getAlpha()));
            }
        }
        newImage.endPixelUpdate();
        return newImage;
    }

}