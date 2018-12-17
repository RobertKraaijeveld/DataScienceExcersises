package com.hro.cmi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Utils
{
    public static int BoolToInt(boolean b)
    {
        if(b == true) return 1;
        else return 0;
    }

    public static ArrayList<Double> calculatePercentagesList(double[] values, double total)
    {
        ArrayList<Double> returnList = new ArrayList<>();
        for (int i = 0; i < values.length; i++)
        {
            double percentage = (values[i] / total) * 100.0;
            percentage = percentage < 0.0 ? 0.0 : percentage;   
            returnList.add(percentage);
        }
        return returnList;
    }

    public static double getRandomDoubleWithMinMax(Random random, double min, double max)
    {
        return random.nextDouble() * (max - min) + min;
    }

    public static int getExistingRandomIndex(byte[] a, byte[] b)
    {
        Random random = new Random();
        //this makes sure the random index is not beyond one of the parents' length 
        if(a.length >= b.length)
            return random.nextInt(b.length - 1);
        else
            return random.nextInt(a.length - 1);                
    } 

    public static Tuple<byte[], byte[]> splitArrayOnIndex(byte[] array, int index)
    {
        byte[] firstArray = Arrays.copyOfRange(array, 0, index);
        byte[] secondArray = Arrays.copyOfRange(array, index, array.length);
        return new Tuple<byte[], byte[]>(firstArray, secondArray);
    }

    public static double sum(double...values) 
    {
        double result = 0;

        for (double value : values)
          result += value;

        return result;
    }

    public static double sum(ArrayList<Double> values) 
    {
        double result = 0;

        for (double value : values)
          result += value;

        return result;
    }

    public static byte[] concatenate(byte[] a, byte[] b)
    {
        int aLen = a.length;
        int bLen = b.length;
    
        @SuppressWarnings("unchecked")
        byte[] c = (byte[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
    
        return c;
    }

    public static double[][] To2DArrayWithSingle2ndElement(double[] oneDimArray)
    {
        double[][] ret = new double[oneDimArray.length][1];

        for (int i = 0; i < oneDimArray.length; i++) 
        {
            ret[i][0] = oneDimArray[i];    
        }
        return ret;
    }
}