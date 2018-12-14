package com.hro.cmi.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;


// TODO: CONVERT THIS TO USE BYTES[] AND RETURN A DOUBLE
public class BinaryVal
{
    private static final int BINARY_BASE = 2;
    public int[] bits;

    public BinaryVal(int[] bits)
    {
        this.bits = bits;
    }

    @Override 
    public String toString()
    {
        String retString = "";
        for(int i = 0; i < bits.length; i++)
        {
            retString += bits[i];
        }
        return retString;
    }

    public static int binaryToInt(BinaryVal bv)
    {
        int returnInt = 0;

        for (int i = 0; i < bv.bits.length; i++)
        {
            int exponent = bv.bits.length - (i + 1);
            returnInt += bv.bits[i] * (int) Math.pow(BINARY_BASE, exponent);
        }
        return returnInt;
    }

    public static BinaryVal intToBinary(int x)
    {
        ArrayList<Integer> returnBitsList = new ArrayList<>();
        int quotient;

        do
        {
            quotient = x / BINARY_BASE;
            
            returnBitsList.add(x % BINARY_BASE);
            x = quotient;                
        } 
        while (quotient != 0);
        
        Collections.reverse(returnBitsList);

        int[] bitsArray = returnBitsList.stream().mapToInt(i -> i).toArray();
        return new BinaryVal(bitsArray);
    }

    public void flipBit(int index)
    {
        for (int i = 0; i < bits.length; i++)
        {
            if(index == i) bits[i] = bits[i] == 1 ? 0 : 1;
        }
    }
} 
