package au.com.jc.weather.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ElevationDataTest {
    @Test
    public void testLoadElevations() throws Exception {
        ModelParameters p=new ModelParameters();
        p.setLatticeWidth(240);
        p.setLatticeHeight(100);
        p.setDensity(0);

        World g=new World(p);

        ElevationData e=new ElevationData();
        g.setElevationData(e);


        e.loadElevations();
        int elevation = g.getElevation(27.9879, 86.9253);
        //close enough :) lost some detail in scaling down image
        assertEquals(8779, elevation);


    }


}
