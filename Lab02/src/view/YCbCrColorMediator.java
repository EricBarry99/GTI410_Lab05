package view;

        import model.ObserverIF;
        import model.Pixel;

        import java.awt.image.BufferedImage;

class YCbCrColorMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider yCS;
    ColorSlider cbCS;
    ColorSlider crCS;
    private double y;
    private double cb;
    private double cr;
    private BufferedImage yImage;
    private BufferedImage cbImage;
    private BufferedImage crImage;
    private int imagesWidth;
    private int imagesHeight;
    ColorDialogResult result;

    YCbCrColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight){
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;

        double[] YCbCR = RgbToYCbCR(result.getPixel().getRed(),result.getPixel().getGreen(),result.getPixel().getBlue());
        this.y = YCbCR[0];
        this.cb = YCbCR[1];
        this.cr = YCbCR[2];
        this.result = result;
        result.addObserver(this);

        yImage = new BufferedImage(imagesWidth, imagesHeight,BufferedImage.TYPE_INT_ARGB);
        cbImage = new BufferedImage(imagesWidth, imagesHeight,BufferedImage.TYPE_INT_ARGB);
        crImage = new BufferedImage(imagesWidth, imagesHeight,BufferedImage.TYPE_INT_ARGB);
        computeYImage(y,cb,cr);
        computeCbImage(y,cb,cr);
        computeCrImage(y,cb,cr);
    }

    public void update(ColorSlider s, int v){

        boolean updateY = false;
        boolean updateCb = false;
        boolean updateCr = false;
        int[] arrayRGB;
        if (s == yCS && v != y) {
            y = v;
            updateCb = true;
            updateCr = true;
        }
        if (s == cbCS && v != cb) {
            cb = v;
            updateY = true;
            updateCr = true;
        }
        if (s == crCS && v != cr) {
            cr = v;
            updateY = true;
            updateCb= true;
        }
        if (updateY) {
            computeYImage(y,cb,cr);
        }
        if (updateCb) {
            computeCbImage(y,cb,cr);
        }
        if (updateCr) {
            computeCrImage(y,cb,cr);
        }
        arrayRGB = YCbCrToRgb(y,cb,cr);
        System.out.println("Y: " + y + " Cb: " + cb + "Cr: " + cr);
        System.out.println("R: " + arrayRGB[0] + " G: " + arrayRGB[1] + " B: " + arrayRGB[2]);
        Pixel pixel = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
        result.setPixel(pixel);

    }

    private void computeYImage(double y, double cb, double cr){
        int[] arrayRGB;
        Pixel p;
        for (int i = 0; i<imagesWidth; ++i) {

            arrayRGB = YCbCrToRgb((i/(double)imagesWidth)*255,cb,cr);
            p = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                yImage.setRGB(i, j, rgb);
            }
        }
        if (yCS != null) {
            yCS.update(yImage);
        }
    }

    private void computeCbImage(double y,double cb,double cr){
        int[] arrayRGB;
        Pixel p;
        for (int i = 0; i<imagesWidth; ++i) {

            arrayRGB = YCbCrToRgb(y,(i/imagesWidth)*255,cr);
            p = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                cbImage.setRGB(i, j, rgb);
            }
        }
        if (cbCS != null) {
            cbCS.update(cbImage);
        }
    }

    private void computeCrImage(double y,double cb,double cr){
        int[] arrayRGB;
        Pixel p;
        for (int i = 0; i<imagesWidth; ++i) {

            arrayRGB = YCbCrToRgb(y, cb, (i/imagesWidth)*255);
            p = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                crImage.setRGB(i, j, rgb);
            }
        }
        if (crCS != null) {
            crCS.update(crImage);
        }
    }

    private double[] RgbToYCbCR (double red, double green, double blue){
        double [] YCbCr = new double[3];
        YCbCr[0] = (0.257 * red) + (0.504 * green) + (0.098 * blue) + 16;
        YCbCr[1] = (-0.148 * red) - (0.291 * green) + (0.439 * blue) + 128;
        YCbCr[2] = (0.439 * red) - (0.368 * green) - (0.071 * blue) + 128;
        return YCbCr;
    }

    private int[] YCbCrToRgb (double y, double Cb, double Cr){
        int [] RGB = new int[3];
        RGB[0] = (int)((double)1.164 * (double)(y-16)  + (double)1.596 * (cr - (double)128));
        RGB[1] = (int)((double)1.164 * (double)(y-16)  - (double)0.813 * (cr - (double)128) - (double)0.391 * (cb -(double)128));
        RGB[2] = (int)((double)1.164 * (double)(y-16)  + (double)2.018 * (cb - (double)128));
        return RGB;
    }

    /**
     * @return
     */
    public BufferedImage getYImage() {
        return yImage;
    }

    /**
     * @return
     */
    public BufferedImage getCbImage() {
        return cbImage;
    }

    /**
     * @return
     */
    public BufferedImage getCrImage() {
        return crImage;
    }

    /**
     * @param slider
     */
    public void setYCS(ColorSlider slider) {
        yCS = slider;
        slider.addObserver(this);
    }

    /**
     * @param slider
     */
    public void setCbCS(ColorSlider slider) {
        cbCS = slider;
        slider.addObserver(this);
    }

    /**
     * @param slider
     */
    public void setCrCS(ColorSlider slider) {
        crCS = slider;
        slider.addObserver(this);
    }
    /**
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     * @return
     */
    public double getCb() {
        return cb;
    }

    /**
     * @return
     */
    public double getCr() {
        return cr;
    }

    /* (non-Javadoc)
    * @see model.ObserverIF#update()
    */
    public void update() {
        // When updated with the new "result" color, if the "currentColor"
        // is aready properly set, there is no need to recompute the images.
        int[] arrayRGB = YCbCrToRgb(y,cb,cr);
        Pixel currentColor = new Pixel(arrayRGB[0], arrayRGB[1], arrayRGB[2], 255);
        if(currentColor.getARGB() == result.getPixel().getARGB()) return;
        double[] arrayYCbCr = RgbToYCbCR((double)result.getPixel().getRed(),(double)result.getPixel().getGreen(),(double)result.getPixel().getBlue());
        y = arrayYCbCr[0];
        cb = arrayYCbCr[1];
        cr = arrayYCbCr[2];

        yCS.setValue((int)y);
        cbCS.setValue((int)cb);
        crCS.setValue((int)cr);

        computeYImage(y,cb,cr);
        computeCbImage(y,cb,cr);
        computeCrImage(y,cb,cr);


        System.out.println("Y: " + y + " Cb: " + cb + "Cr: " + cr);
        System.out.println("R: " + arrayRGB[0] + " G: " + arrayRGB[1] + " B: " + arrayRGB[2]);
        // Efficiency issue: When the color is adjusted on a tab in the
        // user interface, the sliders color of the other tabs are recomputed,
        // even though they are invisible. For an increased efficiency, the
        // other tabs (mediators) should be notified when there is a tab
        // change in the user interface. This solution was not implemented
        // here since it would increase the complexity of the code, making it
        // harder to understand.
    }


}
