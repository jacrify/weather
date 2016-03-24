package au.com.jc.weather.lab;/*
 * 1.1 Swing version.
 */

import au.com.jc.weather.model.ModelParameters;
import au.com.jc.weather.model.World;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Horrible throwaway code to show an animated view of what is happening in the world.
 * It does this the dumbest possible way by writing images out to disk and then loading them up again to show.
 *
 *
 */
public class LatticeLab extends JApplet
        implements ActionListener {
    ImageSQPanel imageSQPanel;
    static int frameNumber = -1;
    int delay;
    Thread animatorThread;
    static boolean frozen = false;
    Timer timer;



    void buildUI(Container container, Image[] dukes) {
        int fps = 10;

        //How many milliseconds between frames?
        delay = (fps > 0) ? (1000 / fps) : 100;

        //Set up a timer that calls this object's action handler
        timer = new Timer(delay, this);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        imageSQPanel = new ImageSQPanel(dukes);
        container.add(imageSQPanel, BorderLayout.CENTER);

        imageSQPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (frozen) {
                    frozen = false;
                    startAnimation();
                } else {
                    frozen = true;
                    stopAnimation();
                }
            }
        });
    }

    public void startAnimation() {
        if (frozen) {
            //Do nothing.  The user has requested that we 
            //stop changing the image.
        } else {
            //Start animating!
            timer.start();
        }
    }

    public void stopAnimation() {
        //Stop the animating thread.
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
        frameNumber++;

        //Display it.
        imageSQPanel.repaint();
    }

    class ImageSQPanel extends JPanel{
        Image dukesWave[];

        public ImageSQPanel(Image[] dukesWave) {
            this.dukesWave = dukesWave;
        }

        //Draw the current frame of animation.
        public void paintComponent(Graphics g) {
            super.paintComponent(g); //paint background

            //Paint the frame into the image.
            try {
                g.drawImage(dukesWave[LatticeLab.frameNumber%dukesWave.length],
                        0, 0, this);

            } catch (ArrayIndexOutOfBoundsException e) {
                //On rare occasions, this method can be called 
                //when frameNumber is still -1.  Do nothing.
                return;
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        java.util.List<String> files=new ArrayList<String>();
        String tmpDir=System.getProperty("java.io.tmpdir");
//        String filestart="/Users/john/images/lattice";
        String filestart=tmpDir+"lattice";
        String fileend=".bmp";

        ModelParameters p=new ModelParameters();


        ImageHelper ih=new ImageHelper();

        int steps=100;
        World world=new World(p);
        //au.com.jc.weather.lga.Lattice l=new au.com.jc.weather.lga.Lattice(w,h,density);
        for (int i = 0; i < steps; i++) {
            String filename=filestart+i+fileend;


            ih.generateColourBitmap(world.generateTemperatureMap(),filename,-10,50);
//            ih.generateColourBitmap(world.getLattice().generateDenMap(),filename,0.0,1.0);


//            ih.generateColourBitmap(world.lattice.generateParticleMap(),filename);
//            ih.generateColourBitmap(world.lattice.generateParticleMap(),filename);
//            l.generateBitmap(filename);
            System.out.println("Running simulation step "+i+" of "+steps);
            world.step();
        }



        Image[] images = new Image[steps];

        for (int i = 0; i < steps; i++) {

                images[i] =
                        ImageIO.read(new File(filestart + i + fileend));
        }

        JFrame f = new JFrame("Temp");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        LatticeLab controller = new LatticeLab();
        controller.buildUI(f.getContentPane(), images);
        controller.startAnimation();
        f.setSize(ih.getWidth(),ih.getHeight()+22);
        f.setVisible(true);
    }
}