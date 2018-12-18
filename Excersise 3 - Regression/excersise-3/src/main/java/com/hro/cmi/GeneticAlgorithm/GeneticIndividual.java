package com.hro.cmi.GeneticAlgorithm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.hro.cmi.Vector;


public class GeneticIndividual
{
    public static final int DIMENSION = Vector.DIMENSION;
    public byte[] bytes;

    public GeneticIndividual(double[] values)
    {
        this.bytes = ToBytes(values);
    }

    @Override 
    public String toString()
    {
        String retString = "";
        for(int i = 0; i < bytes.length; i++)
        {
            retString += (" | " + bytes[i] + " | ");
        }
        return retString;
    }

    public static byte[] ToBytes(double[] values)
    {
        ByteBuffer bb = ByteBuffer.allocate(values.length * 8);
        
        for(double d : values) 
        {
            bb.putDouble(d);
        }
        return bb.array();
    }

    public static double[] ToDoubles(GeneticIndividual individual)
    {
        ByteBuffer bb = ByteBuffer.wrap(individual.bytes);
        double[] doubles = new double[individual.bytes.length / 8];

        for(int i = 0; i < doubles.length; i++) 
        {
            doubles[i] = bb.getDouble();
        }
        return doubles;
    }

    public void flipByte(int index)
    {
        double[] bytesAsDoubles = ToDoubles(this);

        Random random = new Random();
        int randomIndex = random.nextInt((bytesAsDoubles.length - 1) + 1);

        bytesAsDoubles[randomIndex] = bytesAsDoubles[randomIndex] * -1;
    }
} 
