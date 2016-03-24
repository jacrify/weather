package au.com.jc.weather.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Created by john on 20/03/16.
 */
public class WorldTest {
    @Test
    public void testCalculateTemperatureModifier() throws Exception {

    }

    @Test
    public void testCalculateLocalHour() throws Exception {

        ModelParameters p=new ModelParameters();
        p.setLatticeWidth(240);
        p.setLatticeHeight(100);
        p.setDensity(0);

        World g=new World(p);
        double h=g.calculateLocalHour(0, 0);
        assertEquals(0,h,0.01);

        h=g.calculateLocalHour(0, 12);
        assertEquals(12,h,0.01);

        h=g.calculateLocalHour(0, 23);
        assertEquals(23,h,0.01);

        h=g.calculateLocalHour(10, 0);
        assertEquals(1,h,0.01);

        h=g.calculateLocalHour(230, 0);
        assertEquals(23,h,0.01);

        p.setLatticeWidth(10);
        p.setLatticeHeight(10);
        g=new World(p);
        h=g.calculateLocalHour(1, 0);
        assertEquals(2.4,h,0.01);

    }

    @Test
    public void testCalculateLatitudeMultiplier() throws Exception {
        ModelParameters p=new ModelParameters();
        p.setLatticeWidth(400);
        p.setLatticeHeight(100);
        p.setDensity(0);
        World g=new World(p);





        double m=g.calculateLatitudeTempModifier(0);
        assertEquals(-1.0,m,0.01);

        m=g.calculateLatitudeTempModifier(50);
        assertEquals(1.0,m,0.01);

        m=g.calculateLatitudeTempModifier(100);
        assertEquals(-1.0,m,0.01);

        m=g.calculateLatitudeTempModifier(25);
        assertEquals(0,m,0.01);

    }

    @Test
    public void testCalculateTimeOfDayModifier() throws Exception {
        ModelParameters p=new ModelParameters();
        p.setLatticeWidth(400);
        p.setLatticeHeight(400);
        p.setDensity(0);
        World g=new World(p);
        // midday is hottest
        // 6am sunrise
        // 6am sunset

        assertEquals(1,g.calculateTimeOfDayTempModifier(12),.01);

        assertEquals(-1,g.calculateTimeOfDayTempModifier(0),.01);

        assertEquals(0,g.calculateTimeOfDayTempModifier(6),.01);

        assertEquals(0,g.calculateTimeOfDayTempModifier(18),.01);
    }

    @Test
    public void testCalculateTemperatureDelta() throws Exception {
        ModelParameters p=new ModelParameters();
        p.setLatticeWidth(240);
        p.setLatticeHeight(100);
        p.setTime_temp_delta(0.1);
        p.setDensity(0);
        World g=new World(p);
        //25N, at midday GMT, at Greenwich long
        double delta =g.calculateTemperatureDelta(0,25,12);

        assertEquals(0.1,delta,.01);

        //25N, at midnight GMT, at Greenwich long
         delta =g.calculateTemperatureDelta(0,25,0);

        assertEquals(-0.1,delta,.01);

        //25N, at midnight GMT, on the opposite side of the planet to greenwich
        delta =g.calculateTemperatureDelta(120,25,0);

        assertEquals(0.1,delta,.01);


        //25S, at midday GMT, at Greenwich long
        delta =g.calculateTemperatureDelta(0,75,12);

        assertEquals(0.1,delta,.01);

        //25S, at midnight GMT, at Greenwich long
        delta =g.calculateTemperatureDelta(0,75,0);

        assertEquals(-0.1,delta,.01);

        //25S, at midnight GMT, on the opposite side of the planet to greenwich
        delta =g.calculateTemperatureDelta(120,75,0);

        assertEquals(0.1,delta,.01);



    }

    @Test
    public void testConvertLongitudeToGridX() throws Exception {

        ModelParameters p = new ModelParameters();
        p.setLatticeWidth(360);
        p.setLatticeHeight(180);
        p.setTime_temp_delta(0.1);
        p.setDensity(0);
        World g = new World(p);
        assertEquals(0, g.convertLatitudeToGridY(-90), 0.01);
        assertEquals(180, g.convertLatitudeToGridY(90), 0.01);
        assertEquals(90, g.convertLatitudeToGridY(0), 0.01);

        assertEquals(0, g.convertLongitudeToGridX(0), 0.01);
        assertEquals(180, g.convertLongitudeToGridX(180), 0.01);
        assertEquals(180, g.convertLongitudeToGridX(-180), 0.01);
    }

    @Test
    public void testConvertLatticexToGridx() throws Exception {

        ModelParameters p = new ModelParameters();
        p.setLatticeWidth(3);
        p.setLatticeHeight(3);

        p.setDensity(0);
        World g = new World(p);
        assertEquals(0, g.convertLatticexToGridx(0, 0), 0.01);
        assertEquals(0.5, g.convertLatticexToGridx(0, 1), 0.01);
        assertEquals(1, g.convertLatticexToGridx(0, 2), 0.01);


        assertEquals(2, g.convertLatticexToGridx(2, 0), 0.01);
        assertEquals(2.5, g.convertLatticexToGridx(2, 1), 0.01);
        assertEquals(0, g.convertLatticexToGridx(2, 2), 0.01);

    }
}
