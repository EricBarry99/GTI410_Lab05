
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
     * Converts an ImageDouble to an ImageX using a clamping strategy (ABS to 255).
     */
    public ImageX convert(ImageDouble image) {
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;
        newImage.beginPixelUpdate();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                curPixelDouble = image.getPixel(x,y);

                newImage.setPixel(x, y, new Pixel((int)(clampAbsTo255(curPixelDouble.getRed())),
                        (int)(clampAbsTo255(curPixelDouble.getGreen())),
                        (int)(clampAbsTo255(curPixelDouble.getBlue())),
                        (int)(clampAbsTo255(curPixelDouble.getAlpha()))));
            }
        }
        newImage.endPixelUpdate();
        return newImage;
    }

    // On prend la valeur absolue si la valeur est négative sinon on la limite a 255
    private double clampAbsTo255(double value) {
        if (value < 0)
            value = Math.abs(value);
        else if (value > 255)
            value = 255;
        return value;
    }
}