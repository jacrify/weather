package au.com.jc.weather.lga;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * This is a Lattice Gas Automata for simulating a small atmosphere.
 * See http://new.math.uiuc.edu/im2008/dakkak/papers/files/fhp1986.pdf for details.
 * This is the FHP-I variant currently.
 * Basically we have a lattice of points, which correspond to a hex grid on the earth.
 * Each point has one or more air masses, constantly moving.
 * Masses "fly through" on each cycle (flight() method )
 * then collide (scatter() ) with conserved momentum.
 *
 * Created by john on 20/03/16.
 */
public class Lattice {
    private Point[][] lattice;

    private int width;
    private int height;

    public Lattice(int width,int height) {
        super();
        this.width=width;
        this.height=height;
        lattice=new Point[width][height];
    }

    /**
     * Step the lattice forward one stage.
     * For each Point, calculate who the neighbour are, call flight(), then scatter()
     */
    public void step() {
        //flight
        Point[][] templattice = new Point[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //work out neighbours. Wrap at edges
                //TODO poles should not wrap to each other,
                //but should either reflect or send particles
                //"over the top" to opposite longitude
                Point[] neighbours = getNeighbours(x, y);

                templattice[x][y] = lattice[x][y].flight(neighbours);
            }

        }

        //scatter
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                templattice[x][y] = templattice[x][y].scatter();
            }
        }
        lattice=templattice;
    }

    /**
     * Lattice is responsbile for calculating neighbours,
     * allowing us to manage the topology.
     * We wrap east and west, and bounce particles off the pole.
     * @param x
     * @param y
     * @return
     */
    protected Point[] getNeighbours(int x, int y) {
        int left = (x == 0 ? width - 1 : x - 1);
        int right = (x == width - 1 ? 0 : x + 1);
        int up = y - 1;
        int down = y+1;

        Point nw = (y==0 ? null :lattice[x][up]);
        Point ne = (y==0 ? null: lattice[right][up]);
        Point e = lattice[right][y];

        Point se = (y==height-1 ? null : lattice[x][down]);
        Point sw = (y==height-1 ? null : lattice[left][down]);
        Point w = lattice[left][y];

        return new Point[]{nw, ne, e, se, sw, w};
    }


    //TODO replace following with Visitor to remove repeated code
    /**
     * Debug method for visualising particle map
     * @return
     */
    public double[][] generateParticleMap() {
        double[][] grid =  new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (lattice[x][y].getMask() > 0) {
                    grid[x][y]=1;
                }
                else {
                    grid[x][y]=0;
                }
            }
        }
        return grid;
    }



    /**
     * Debug method for visualising temperature map
     * @return
     */
    public double[][] generateTemperatureMap() {
        double[][] grid =  new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double t=sample(x,y,2).getAverageTemp();

                grid[x][y]=t;

            }
        }
        return grid;
    }

    /**
     * Debug method for visualising density map
     * @return
     */
    public double[][] generateDenMap() {
        double[][] grid =  new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double t=sample(x,y,2).getDensity();

                grid[x][y]=t;

            }
        }
        return grid;
    }

    /**
     *add delta temperature to lattice at this getPoint
     */
    public void adjustTemp(int x, int y, double delta) {
        lattice[x][y].adjustTemp(delta);
    }


    public Point getPoint(int x, int y) {
        return lattice[x][y];
    }

    public void setPoint(Point p) {
        lattice[p.x][p.y]=p;
    }

    //step out from central cell a number of times, collecting
    //neighbours as we go.
    //For efficiency remember which neighbours we've already checked,
    private void recursiveSample(Set<Point> newNeighbours, Sample sample,int count) {
        if (count>1) {
            Set<Point> oldNewNeighbours=newNeighbours;
            newNeighbours=new HashSet<Point>();
            for (Point p: oldNewNeighbours) {
                    Point[] neighbours = getNeighbours(p.x, p.y);
                    for (Point n: neighbours) {
                        if (n!=null)

                            newNeighbours.add(n);
                    }
                    sample.addAll(newNeighbours);
            }
            recursiveSample(newNeighbours,sample,count-1);
        }
    }

    /**
     *
     * @param x x coord to start sample
     * @param y y coord to start sample
     * @param sampleSize radius of sample. 1 means just 1 point, 2 means 1 point and it's neighbours, etc
     * @return A au.com.jc.weather.lga.Sample object holding the sampled values
     */
    public Sample sample(int x, int y, int sampleSize) {
        Set<Point> initial=new HashSet<Point>();
        Sample sample=new Sample();

        sample.addPoint(lattice[x][y]);
        initial.add(lattice[x][y]);
        recursiveSample(initial,sample,sampleSize);
        return sample;
    }


 }
