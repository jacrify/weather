package au.com.jc.weather.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Load a file containing a greyscale image of elevation,
 * return an array of elevations in meters
 *
 * Created by john on 23/03/16.
 */
public class ElevationData {
    //TODO move to central model?
    private static final int HEIGHT_OF_EVEREST = 8848;
//    private static final String ELEVATION_IMAGE = "gebco_08_rev_elev_21600x10800.png";

    private static final String ELEVATION_IMAGE = "gebco_08_rev_elev_small.png";

    private int[][] elevation;

    private int elevationWidth;
    private int elevationHeight;
    /**
     * Create class.
     */
    public ElevationData() {
        super();
        loadElevations();
    }

    /**
     * Load an array of elevations from a resource file.
     *
     * @return array of elevations in metres
     * @throws IOException
     */
    public void loadElevations() {

        BufferedImage image = null;
        try {
            image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(ELEVATION_IMAGE));
        } catch (IOException e) {
           throw new IllegalStateException("Cannot load resource as stream. Has strange stuff happened with packaging?", e);
        }

        elevationWidth = image.getWidth();
        elevationHeight = image.getHeight();

        elevation = new int[elevationWidth][elevationHeight];

        for (int x = 0; x < elevationWidth; x++) {

            for (int y = 0; y < elevationHeight; y++) {

                //heightVal is between 0 and 255
                int heightVal = image.getRaster().getSample(x, y, 0);

                if (heightVal==0) {
                    elevation[x][y] = 0;
                }else {

                    int heightInMeters = (int) Math.ceil(Util.interpolate(1, HEIGHT_OF_EVEREST, 0, 255, heightVal));
                    elevation[x][y] = heightInMeters;
                }
            }
        }

    }

    public int getElevation(int x,int y) {
        return elevation[x][y];
    }

    public double getElevationHeight() {
        return elevationHeight;
    }

    public int getElevationWidth() {
        return elevationWidth;
    }
}
