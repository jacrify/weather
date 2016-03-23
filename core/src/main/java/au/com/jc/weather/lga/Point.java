package au.com.jc.weather.lga;

import java.util.Random;

/**
 * Heavily optimised (hacked :) point class. Read Lattice comments first.
 * Represents one point in the lattice, handles flight and scatter.
 * Lots of shortcuts here to try to make it fast.
 *
 * Created by john on 20/03/16.
 */
public class Point {
    //Array of current masses moving through this getPoint
    //Position in array indicates the direction mass is moving
    //directions are nw, ne, e, se, sw, w in that order
    //Can have empty slots
    Mass[] masses = new Mass[6];

    //For performance reasons, just hold the non empty masses, with index NOT signifying direction
    Mass[] massesPopulated= new Mass[6];
    int massCount=0;


    int x;
    int y;

    private static final int NW = 1;
    private static final int NE = 2;
    private static final int E = 4;
    private static final int SE = 8;
    private static final int SW = 16;
    private static final int W = 32;

    public int getMask() {
        return mask;
    }

    private int mask = 0;

    private static final int[] result1 = new int[64];
    private static final int[] result2 = new int[64];

    public void setRand(Random rand) {
        this.rand = rand;
    }

    private Random rand = new Random();

    static {

        for (int i = 0; i < 64; i++) {

            result1[i] = i;
            result2[i] = -1;
        }


        //two body collisions- randomly rotate particle direction
        result1[NW + SE] = NE + SW;
        result2[NW + SE] = E + W;

        result1[NE + SW] = NW + SE;
        result2[NE + SW] = E + W;

        result1[E + W] = NW + SE;
        result2[E + W] = NE + SW;

        //four body collisions-randomly rotate particle direction
        result1[NW + NE + SE + SW] = W + E + SE + SW;
        result2[NW + NE + SE + SW] = W + E + NW + SE;

        result1[W + E + SE + SW] = NW + NE + SE + SW;
        result2[W + E + SE + SW] = W + E + NW + SE;

        result1[W + E + NW + SE] = NW + NE + SE + SW;
        result2[W + E + NW + SE] = W + E + SE + SW;

        //three body collisions: change to alernate direction. No randomness, so result1
        //array is set only. Result1 being -1 indicates that no random choice is needed.

        result1[NE + SE + W] = NW + E + SW;
        result1[NW + E + SW] = NE + SE + W;

        result1[NW + SE + SW] = E + W + SW;
        result1[E + W + SW] = NW + SE + SW;

        result1[NE + SW + SE] = W + E + SE;
        result1[W + E + SE] = NE + SW + SE;

        result1[NW + E + SE] = SE + E + SW;
        result1[SE + E + SW] = NW + E + SE;


    }


    public Point(int x,int y) {
        super();
        this.x=x;
        this.y=y;
    }


    /**
     * Examine neighbours. If neighbours have a mass
     * that is heading this way, move it to this getPoint
     * Order of neighbours array is expected to be
     * nw, ne, e, se, sw, w
     * If a neighbour is null, it indicates a wall. Masses should bounce off walls.
     * @param neighbours
     * @return
     */
    public Point flight(Point[] neighbours) {
        Point p = new Point(x,y);
        p.setRand(rand);

        for (int mass = 0; mass < 6; mass++) {
            //j is opposite direction to i
            int opposite=(mass + 3) % 6;

            //examine opposite neighbour first
            if (neighbours[opposite]==null) {
                //we need to bounce. Work out which neighbour we're going to get bounce particle from.
                //Walls are only at north and south of map  currently
                //so NE<->NW and SE<->SW
                int source;
                switch (mass) {
                    case 0:
                        source = 1;
                        break;
                    case 1:
                        source = 0;
                        break;
                    case 3:
                        source = 4;
                        break;
                    case 4:
                        source = 3;
                        break;
                    case 2:
                        source = 5;
                        break;
                    case 5:
                        source = 2;
                        break;
                    default: throw new IllegalArgumentException("Mass is illegal number");
                }
                //now work out what mass to pull from that neighbour- if
                // it's the SE neighbour get the NW mass
                opposite=(source + 3) % 6;
                //and put it in the SW slot, as it's bounced and is now heading SW
                p.addMass(neighbours[source].masses[opposite], mass);
            } else {
                //otherwise, get the mass from the opposite neighbour and put it in this slot.
                p.addMass(neighbours[opposite].masses[mass], mass);
            }
        }

        return p;
    }

    /**
     * There are three possible results of scatter:
     * 1) No scatter occurs. Masses remain on their current paths
     * 2) A one way transform happens. This is indicated by a new mask value in result1 at
     * the index of the old mask value, and a value of -1 in result 2 at the index of the old
     * mask value
     *  3) One of two new mask values is possible, chosen randomly. This is indicated by
     * non -1 mask values in result1 and result2 and the index of the old mask value.
     *
     * Note this method works by doing using pre calculated lookups of different
     * configurations, where each configuration is asssigned a number
     * based on what masses are present, and the result1/2 arrays hold
     * new configuration numbers and are indexed by the old ones.
     *
     * Some configurations require a random choice, hence there are two arrays.
     * @return a new Point
     */

    public Point scatter() {
        Point p = new Point(x,y);
        //TODO create a Point(Point p) constructor
        p.massCount=massCount;
        p.massesPopulated=massesPopulated;
        p.setRand(rand);

        int r1 = result1[mask];
        if (r1 == mask) {
            //no scatter. Copy masses as is.
            p.mask = mask;
            p.masses = masses;
            return p;
        }

        int r2 = result2[mask];

        if (r2 == -1) {
            //no random scatter.
            p.mask = r1;

        } else {
            //choose one of the two random scatter options

            if (rand.nextBoolean())
                p.mask = r1;
            else
                p.mask = r2;
        }
        //now we need to rejig the array of masses to reflect the new mask.

        Mass[] temp = new Mass[6];
        int j = 0;
        for (int i = 0; i < 6; i++) {
            if (masses[i] != null) {
                temp[j] = masses[i];
                j++;
            }
        }
        //TODO shuffle masses here
        j = 0;

        for (int i = 0; i < 6; i++) {
            if ((p.mask & (int)Math.pow(2,i)) > 0) {
                p.masses[i] = temp[j];
                j++;
            } else {
                p.masses[i] = null;
            }

        }
        return p;

    }

    public void addMass(Mass m, int i) {
        masses[i]=m;
        if (masses[i] != null) {
            massesPopulated[massCount]=m;
            massCount++;

            //2^(i+2) should give constants from above (NW, NE, etc etc)
            mask = mask + (int)Math.pow(2, (i));
        }
    }



    /**
     * Add delta temp to all masses at this getPoi
     * @param delta
     */
    public void adjustTemp(double delta) {
        //TODO set max and min temp bounds

        delta=delta/massCount;

        for (int i = 1; i <= massCount; i++) {
            massesPopulated[i-1].temp+=delta;
        }
    }

    /**
     * Calculate average temperature of the masses in this point.
     * @return
     */
    public double calcAverageTemp() {
        double average=0.0;
        for (int i = 1; i <= massCount; i++) {
            average+=massesPopulated[i-1].temp;

        }
        return (massCount==0)? 0:average/(double)massCount;
    }
}
