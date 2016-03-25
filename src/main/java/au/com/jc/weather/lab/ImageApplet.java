package au.com.jc.weather.lab;/*
 * 1.1 Swing version.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Show a window with an image animation in it.
 *
 *
 */
public class ImageApplet extends JApplet
        implements ActionListener {
    private final String title;
    private ImageSQPanel imageSQPanel;
    private static int frameNumber = -1;
    private Thread animatorThread;
    static boolean frozen = false;
    private Timer timer;
    private List<BufferedImage> images=new ArrayList<BufferedImage>();

    public int getWidth() {
        return width;
    }

    private int width;
    public ImageApplet(String title) {
        super();
        this.title=title;


    }

    void buildUI(Container container, Image[] images) {
        int fps = 10;

        //How many milliseconds between frames?
        int delay = (fps > 0) ? (1000 / fps) : 100;

        //Set up a timer that calls this object's action handler
        timer = new Timer(delay, this);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        imageSQPanel = new ImageSQPanel(images);
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

    public void go(int windowx,int windowy) {

        JFrame f = new JFrame(title);
        f.setLocation(windowx,windowy);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


        buildUI(f.getContentPane(), images.toArray(new Image[0]));
        startAnimation();
        f.setSize(images.get(0).getWidth(),images.get(0).getHeight()+22);
        f.setVisible(true);

    }

    public void addImage(BufferedImage im) {
        images.add(im);
        width=im.getWidth();
    }

    class ImageSQPanel extends JPanel{
        Image images[];

        public ImageSQPanel(Image[] images) {
            this.images = images;
        }

        //Draw the current frame of animation.
        public void paintComponent(Graphics g) {
            super.paintComponent(g); //paint background

            //Paint the frame into the image.
            try {
                g.drawImage(images[ImageApplet.frameNumber%images.length],
                        0, 0, this);

            } catch (ArrayIndexOutOfBoundsException e) {
                //On rare occasions, this method can be called 
                //when frameNumber is still -1.  Do nothing.
            }
        }
    }


}