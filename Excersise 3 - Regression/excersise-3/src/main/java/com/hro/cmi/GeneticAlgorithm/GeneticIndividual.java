package com.hro.cmi.GeneticAlgorithm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.hro.cmi.Utils;
import com.hro.cmi.Vector;


public class GeneticIndividual
{
    public static final int DIMENSION = Vector.DIMENSION;
    public byte[][] bytes; // 2d array because multi regression lib needs 2d array
    public double currentFitness;

    public GeneticIndividual(double[] values)
    {
        this.bytes = ToBytesMatrix(values);
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

    public static byte[][] ToBytesMatrix(double[] values)
    {
        byte[][] bytes = new byte[values.length][8]; 

        for(int i = 0; i < values.length; i++) 
        {
            bytes[i] = Utils.DoubleToBytes(values[i]);
        }
        return bytes;
    }

    public static double[][] ToDoublesMatrix(GeneticIndividual individual)
    {
        double[][] doubles = new double[individual.bytes.length][1];

        for(int i = 0; i < individual.bytes.length; i++) 
        {
            doubles[i][0] = Utils.BytesToDouble(individual.bytes[i]);
        }
        return doubles;
    }

    public static double[] ToDoublesArray(GeneticIndividual individual)
    {
        double[] doubles = new double[individual.bytes.length];
        
        for(int i = 0; i < individual.bytes.length; i++)
        {
            doubles[i] = Utils.BytesToDouble(individual.bytes[i]);
        }
        return doubles;
    }

    public void flipValue(int index)
    {
        double[][] bytesAsDoubles = ToDoublesMatrix(this);

        Random random = new Random();
        int randomIndex = random.nextInt((bytesAsDoubles.length - 1) + 1);

        bytesAsDoubles[randomIndex][0] = bytesAsDoubles[randomIndex][0] * -1;
    }
} 
