package au.com.jc.weather.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Created by john on 23/03/16.
 */
public class UtilTest {
    @Test
    public void testTriangle() throws Exception {

    }

    @Test
    public void testInterpolate() throws Exception {

        assertEquals(5,Util.interpolate(0,10,0,100,50),0.01);

        assertEquals(0,Util.interpolate(0,10,0,100,0),0.01);

        assertEquals(10,Util.interpolate(0,10,0,100,100),0.01);

    }
}
