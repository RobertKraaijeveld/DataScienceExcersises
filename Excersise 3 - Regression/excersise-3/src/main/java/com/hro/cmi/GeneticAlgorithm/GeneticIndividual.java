package com.hro.cmi.GeneticAlgorithm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import com.hro.cmi.Vector;


public class GeneticIndividual
{
    public static final int DIMENSION = Vector.DIMENSION;
    public byte[] bytes;

    public GeneticIndividual(double[] values)
    {
        ByteBuffer bb = ByteBuffer.allocate(values.length * 8);
        
        for(double d : values) 
        {
            bb.putDouble(d);
        }
        this.bytes = bb.array();
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
        for (int i = 0; i < bytes.length; i++)
        {
            if(index == i) bytes[i] = (byte) ~this.bytes[i];
        }
    }
} 
