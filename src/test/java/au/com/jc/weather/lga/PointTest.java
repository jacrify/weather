package au.com.jc.weather.lga; /**
 * Created by john on 20/03/16.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PointTest {
    @org.junit.Test
    public void testCalcAverageTemp() throws Exception {
        Point p=new Point(0,0);

        assertEquals(0.0,p.calcAverageTemp(),0.01);

        Mass m1=new Mass();
        m1.temp=20;

        p.addMass(m1,0);
        assertEquals(20.0,p.calcAverageTemp(),0.01);

        Mass m2=new Mass();
        m1.temp=10;

        p.addMass(m2,1);
        assertEquals(15.0,p.calcAverageTemp(),0.01);

    }

    @org.junit.Test
    public void testFlight() throws Exception {
        Point p=new Point(0,0);

        Point nw=new Point(0,0);
        Mass seMass=new Mass();
        nw.masses = new Mass[]{new Mass(),new Mass(),new Mass(),seMass,new Mass(),new Mass()};

        Point ne=new Point(0,0);
        Mass swMass=new Mass();
        ne.masses = new Mass[]{new Mass(),new Mass(),new Mass(),new Mass(),swMass,new Mass()};


        Point e=new Point(0,0);
        Mass wMass=new Mass();
        e.masses = new Mass[]{new Mass(),new Mass(),new Mass(),new Mass(),new Mass(),wMass};

        Point se=new Point(0,0);
        Mass nwMass=new Mass();
        se.masses = new Mass[]{nwMass,new Mass(),new Mass(),new Mass(),new Mass(),new Mass()};

        Point sw=new Point(0,0);
        Mass neMass=new Mass();
        sw.masses = new Mass[]{new Mass(),neMass,new Mass(),new Mass(),new Mass(),new Mass()};


        Point w=new Point(0,0);
        Mass eMass=new Mass();
        w.masses = new Mass[]{new Mass(),new Mass(),eMass,new Mass(),new Mass(),new Mass()};



        Point[] neighbours=new Point[]{nw,ne,e,se,sw,w};
        Point q=p.flight(neighbours);
        assertEquals(q.masses[3],seMass);
        assertEquals(q.masses[0],nwMass);
        assertEquals(q.masses[1],neMass);
        assertEquals(q.masses[2],eMass);
        assertEquals(q.masses[4],swMass);
        assertEquals(q.masses[5],wMass);


        assertEquals(63,q.getMask());
    }
    @org.junit.Test
    public void testScatterTwoBody() throws Exception {
        Point p = new Point(0,0);

        Point nw = new Point(0,0);

        nw.masses = new Mass[]{null, null, null, null, null, null};

        Point ne = new Point(0,0);

        ne.masses = new Mass[]{null, null, null, null, null, null};

        Point e = new Point(0,0);
        Mass wMass = new Mass();
        e.masses = new Mass[]{new Mass(), new Mass(), new Mass(), new Mass(), new Mass(), wMass};

        Point se = new Point(0,0);
        se.masses = new Mass[]{null, null, null, null, null, null};

        Point sw = new Point(0,0);
        Mass neMass = new Mass();
        sw.masses = new Mass[]{null, null, null, null, null, null};

        Point w = new Point(0,0);
        Mass eMass = new Mass();
        w.masses = new Mass[]{new Mass(), new Mass(), eMass, new Mass(), new Mass(), new Mass()};

        Point[] neighbours = new Point[]{nw, ne, e, se, sw, w};
        Point q = p.flight(neighbours);
        Point r = q.scatter();
        assertTrue(r.getMask() == 9 || r.getMask() == 18);
        if (r.getMask() == 9) {
            assertTrue(r.masses[0] == wMass || r.masses[0] == eMass);
            assertTrue(r.masses[3] == wMass || r.masses[3] == eMass);
        }

        if (r.getMask() == 18) {
            assertTrue(r.masses[1] == wMass || r.masses[1] == eMass);
            assertTrue(r.masses[4] == wMass || r.masses[4] == eMass);
        }

    }
    @org.junit.Test
    public void testScatterOneBody() throws Exception {
        Point p = new Point(0,0);

        Point nw = new Point(0,0);
        nw.masses = new Mass[]{null, null, null, null, null, null};

        Point ne = new Point(0,0);
        ne.masses = new Mass[]{null, null, null, null, null, null};

        Point e = new Point(0,0);
        Mass wMass = new Mass();
        e.masses = new Mass[]{new Mass(), new Mass(), new Mass(), new Mass(), new Mass(), wMass};

        Point se = new Point(0,0);
        se.masses = new Mass[]{null, null, null, null, null, null};

        Point sw = new Point(0,0);
        sw.masses = new Mass[]{null, null, null, null, null, null};

        Point w = new Point(0,0);
        w.masses = new Mass[]{null, null, null, null, null, null};

        Point[] neighbours = new Point[]{nw, ne, e, se, sw, w};
        Point q = p.flight(neighbours);
        Point r = q.scatter();
        assertTrue(r.getMask() == 32 );

        assertTrue(r.masses[5] == wMass );


    }




}
