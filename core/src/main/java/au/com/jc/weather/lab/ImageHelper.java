package au.com.jc.weather.lab;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created by john on 21/03/16.
 */
public class ImageHelper {

        public void generateBitmap(int[][] bytes,String filename) throws IOException {
            int width=bytes[0].length;
            int height=bytes.length;
            BufferedImage im = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
            WritableRaster raster = im.getRaster();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (bytes[x][y] > 0) {
                        raster.setSample(x, y, 0, 1);
                    }
                    else {
                        raster.setSample(x, y, 0, 0);
                    }
                }
            }
            ImageIO.write(im, "BMP", new File(filename));
        }



    public int[] greyscale(double minimum, double maximum, double value) {


        double ratio = 2.0 * (value - minimum) / (maximum - minimum);

        int r= (int)(ratio*255);
        int g= (int)(ratio*255);
        int b= (int)(ratio*255);

        return  new int[]{r, g, b, 0};

    }

    public int[] rgb(double minimum, double maximum, double value) {

        double mid=(maximum-minimum)/2.0;
        int r;
        int b;
        int g=0;


        if (value >=mid){
            r=255;
            b = (int)Math.round(255.0 * ((maximum - value) / (maximum - mid)));
        }
        else{
            b = 255;
            r = (int)Math.round(255.0 * ((value - minimum) / (mid - minimum)));
        }
        return new int[]{r, g, b, 0};

    }

    public void generateColourBitmap(double[][] bytes,String filename, double low, double high) throws IOException {
        int width=bytes[0].length;
        int height=bytes.length;
        BufferedImage im = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = im.getRaster();
        int[] black=new int[]{0,0,0,0};
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double t=bytes[x][y];
                if (t==0)
                    raster.setPixel(x,y,black);
                else
                raster.setPixel(x,y, rgb(low,high, t));
            }
        }

        ImageIO.write(im, "BMP", new File(filename));
    }
}
