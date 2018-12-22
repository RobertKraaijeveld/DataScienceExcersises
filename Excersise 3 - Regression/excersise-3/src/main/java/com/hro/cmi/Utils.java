package com.hro.cmi;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
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

    public static Tuple<byte[][], byte[][]> splitMatrixOnIndex(byte[][] array, int index)
    {
        byte[][] firstArray = Arrays.copyOfRange(array, 0, index);
        byte[][] secondArray = Arrays.copyOfRange(array, index, array.length);
        return new Tuple<byte[][], byte[][]>(firstArray, secondArray);
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

    public static byte[][] concatenate(byte[][] a, byte[][] b)
    {
        int aLen = a.length;
        int bLen = b.length;
    
        byte[][] c = (byte[][]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
    
        return c;
    }


    // These REALLY should be one method that uses a generic param, but since Java sucks at casting thats not gonna happen
    public static double[] FlattenDouble2DArray(double[][] twoDArray)
    {
        double[] ret = new double[twoDArray.length];
        for(int i = 0; i < twoDArray.length; i++)
        {
            ret[i] = twoDArray[i][0];
        }
        return ret;
    }

    public static byte[] FlattenBytes2DArray(byte[][] twoDArray)
    {
        byte[] ret = new byte[twoDArray.length];
        for(int i = 0; i < twoDArray.length; i++)
        {
            ret[i] = twoDArray[i][0];
        }
        return ret;
    }

    
    public static byte[] DoubleToBytes(double d)
    {
        byte[] output = new byte[8];
        long lng = Double.doubleToLongBits(d);

        for(int i = 0; i < 8; i++) 
        {
            output[i] = (byte) ((lng >> ((7 - i) * 8)) & 0xff);
        }
        return output;
    }

    public static double BytesToDouble(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getDouble();
    }
}