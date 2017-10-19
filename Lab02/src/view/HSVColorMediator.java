package view;

import model.ObserverIF;
import model.Pixel;

import java.awt.image.BufferedImage;

 public class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider hueCS;
    ColorSlider saturationCS;
    ColorSlider valueCS;
    private double hue;
    private double saturation;
    private double value;
    private BufferedImage hueImage;
    private BufferedImage saturationImage;
    private BufferedImage valueImage;
    private int imagesWidth;
    private int imagesHeight;
    ColorDialogResult result;

    HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight){
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;

        double[] HSV = RGBtoHSV(result.getPixel().getRed(),result.getPixel().getGreen(),result.getPixel().getBlue());
        this.hue = HSV[0];
        this.saturation = HSV[1];
        this.value = HSV[2];
        this.result = result;
        result.addObserver(this);

        hueImage = new BufferedImage(imagesWidth, imagesHeight,BufferedImage.TYPE_INT_ARGB);
        saturationImage = new BufferedImage(imagesWidth, imagesHeight,BufferedImage.TYPE_INT_ARGB);
        valueImage = new BufferedImage(imagesWidth, imagesHeight,BufferedImage.TYPE_INT_ARGB);
        computeHueImage(hue,saturation,value);
        computeSaturationImage(hue,saturation,value);
        computeValueImage(hue,saturation,value);
    }

     public void update(ColorSlider s, int v) {
         boolean updateHue = false;
         boolean updateSaturation = false;
         boolean updateValue = false;
         int[] arrayRGB;
         if (s == hueCS && v != hue) {
             hue = (v / (double)255)*360;
             updateSaturation = true;
             updateValue = true;
         }
         if (s == saturationCS && v != saturation) {
             saturation = v / (double)255;
             updateHue = true;
             updateValue = true;
         }
         if (s == valueCS && v != value) {
             value = v / (double)255;
             updateHue = true;
             updateSaturation = true;
         }
         if (updateHue) {
             computeHueImage(hue,saturation,value);
         }
         if (updateSaturation) {
             computeSaturationImage(hue,saturation,value);
         }
         if (updateValue) {
             computeValueImage(hue,saturation,value);
         }
         arrayRGB = HSVtoRGB(hue,saturation,value);
         Pixel pixel = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
         result.setPixel(pixel);
     }

    private void computeHueImage(double hue, double saturation, double value){
        int[] arrayRGB;
        Pixel p;
        for (int i = 0; i<imagesWidth; ++i) {

            arrayRGB = HSVtoRGB((i/(double)imagesWidth)*360,saturation,value);
            p = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                hueImage.setRGB(i, j, rgb);
            }
        }
        if (hueCS != null) {
            hueCS.update(hueImage);
        }
    }

     private void computeSaturationImage(double hue,double saturation,double value){
         int[] arrayRGB;
         Pixel p;
         for (int i = 0; i<imagesWidth; ++i) {

             arrayRGB = HSVtoRGB(hue,(i/(double)imagesWidth),value);
             p = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
             int rgb = p.getARGB();
             for (int j = 0; j<imagesHeight; ++j) {
                 saturationImage.setRGB(i, j, rgb);
             }
         }
         if (saturationCS != null) {
             saturationCS.update(saturationImage);
         }
     }

     private void computeValueImage(double hue,double saturation,double value){
         int[] arrayRGB;
         Pixel p;
         for (int i = 0; i<imagesWidth; ++i) {

             arrayRGB = HSVtoRGB(hue, saturation, (i/(double)imagesWidth));
             p = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
             int rgb = p.getARGB();
             for (int j = 0; j<imagesHeight; ++j) {
                 valueImage.setRGB(i, j, rgb);
             }
         }
         if (valueCS != null) {
             valueCS.update(valueImage);
         }
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

    public int[] HSVtoRGB (double hue, double saturation, double value){
        System.out.println("HSV TO RGB");
        System.out.println("hue: " + hue + " saturation: " + saturation + " value" + value);
        double []RGB = new double[3];

        double t = (hue/(double)60);
        double c = value * saturation;
        double x = c * (1 - Math.abs((t % 2) -1));
        if (0<=t && t<=1) RGB = new double[]{c,x,0};
        else if (1<=t && t<=2) RGB = new double[]{x,c,0};
        else if (2<=t && t<=3) RGB = new double[]{0,c,x};
        else if (3<=t && t<=4) RGB = new double[]{0,x,c};
        else if (4<=t && t<=5) RGB = new double[]{x,0,c};
        else if (5<=t && t<=6) RGB = new double[]{c,0,x};
        else RGB = new double[]{0,0,0};
        double m = value - c;

        return new int[] {(int)((RGB[0]+m)*255),(int)((RGB[1]+m)*255),(int)((RGB[2]+m)*255)};
    }

     /**
      * @return
      */
     public BufferedImage getHueImage() {
         return hueImage;
     }

     /**
      * @return
      */
     public BufferedImage getSaturationImage() {
         return saturationImage;
     }

     /**
      * @return
      */
     public BufferedImage getValueImage() {
         return valueImage;
     }

     /**
      * @param slider
      */
     public void setHueCS(ColorSlider slider) {
         hueCS = slider;
         slider.addObserver(this);
     }

     /**
      * @param slider
      */
     public void setSaturationCS(ColorSlider slider) {
         saturationCS = slider;
         slider.addObserver(this);
     }

     /**
      * @param slider
      */
     public void setValueCS(ColorSlider slider) {
         valueCS = slider;
         slider.addObserver(this);
     }
     /**
      * @return
      */
     public double getHue() {
         return hue;
     }

     /**
      * @return
      */
     public double getSaturation() {
         return saturation;
     }

     /**
      * @return
      */
     public double getValue() {
         return value;
     }

     /* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
     public void update() {
         // When updated with the new "result" color, if the "currentColor"
         // is aready properly set, there is no need to recompute the images.
         int[] arrayRGB = HSVtoRGB(hue,saturation,value);
         Pixel currentColor = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
         if(currentColor.getARGB() == result.getPixel().getARGB()) return;

         double[] arrayHSV = RGBtoHSV(result.getPixel().getRed(),result.getPixel().getGreen(),result.getPixel().getBlue());
         hue = arrayHSV[0];
         saturation = arrayHSV[1];
         value = arrayHSV[2];

         hueCS.setValue((int)hue);
         saturationCS.setValue((int)(saturation*255));
         valueCS.setValue((int)(value*255));

         computeHueImage(hue,saturation,value);
         computeSaturationImage(hue,saturation,value);
         computeValueImage(hue,saturation,value);

         // Efficiency issue: When the color is adjusted on a tab in the
         // user interface, the sliders color of the other tabs are recomputed,
         // even though they are invisible. For an increased efficiency, the
         // other tabs (mediators) should be notified when there is a tab
         // change in the user interface. This solution was not implemented
         // here since it would increase the complexity of the code, making it
         // harder to understand.
     }


 }
