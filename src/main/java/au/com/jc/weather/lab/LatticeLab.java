package au.com.jc.weather.lab;/*
 * 1.1 Swing version.
 */

import au.com.jc.weather.model.ElevationData;
import au.com.jc.weather.model.ModelParameters;
import au.com.jc.weather.model.World;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Horrible throwaway code to show an animated view of what is happening in the world.
 * It does this the dumbest possible way by writing images out to disk and then loading them up again to show.
 *
 *
 */
public class LatticeLab {


    public static void main(String[] args) throws IOException {


        ModelParameters p=new ModelParameters();
        p.setDensity(0.07);

        ImageHelper ih=new ImageHelper();

        int steps=3;
        World world=new World(p);
        ElevationData e=new ElevationData();
        world.setElevationData(e);

        ImageApplet tempWindow=new ImageApplet("Temperature");
        ImageApplet pressureWindow=new ImageApplet("Pressure");


        for (int i = 0; i < steps; i++) {
            BufferedImage temp=ih.getColourBitmap(world.generateMap(360,180,s -> s.getAverageTemp()),-10,50);
            BufferedImage pressure=ih.getColourBitmap(world.generateMap(360,180,s -> s.getPressure()),0,1200);
            tempWindow.addImage(temp);
            pressureWindow.addImage(pressure);
            System.out.println("Running simulation step "+i+" of "+steps);
            world.step();
        }

        tempWindow.go(0,0);
        pressureWindow.go(tempWindow.getWidth(),0);

    }
}