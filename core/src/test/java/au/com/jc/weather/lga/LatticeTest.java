package au.com.jc.weather.lga;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LatticeTest {
    @Test
    public void testSample() throws Exception {
        Lattice l=new Lattice(10,10);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                Point p = new Point(x, y);
                l.setPoint(p);
            }
        }

        Point p=l.getPoint(5,5);

        Mass m=new Mass();
        m.temp=20;
        p.addMass(m,0);
        l.setPoint(p);

        Sample s=l.sample(5,5,1);

        assertEquals(1,s.getPointCount());
        assertEquals(1,s.getMassCount());
        assertEquals(1.0/6.0,s.getDensity(),0.01);
        assertEquals(20.0,s.getAverageTemp(),0.01);


        Mass m2=new Mass();
        m2.temp=10;
        p.addMass(m2,0);
        l.setPoint(p);

        s=l.sample(5,5,1);

        assertEquals(1,s.getPointCount());
        assertEquals(2,s.getMassCount());
        assertEquals(15.0,s.getAverageTemp(),0.01);

        Mass m3=new Mass();
        Point p2=l.getPoint(4,5);
        p2.addMass(m3,0);

        s=l.sample(5,5,2);
        assertEquals(7,s.getPointCount());
        assertEquals(3,s.getMassCount());

        s=l.sample(5,5,3);

        assertEquals(19,s.getPointCount());
        assertEquals(3,s.getMassCount());

        s=l.sample(5,5,4);

        assertEquals(37,s.getPointCount());
        assertEquals(3,s.getMassCount());

    }
}
