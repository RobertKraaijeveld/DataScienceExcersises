package com.hro.cmi;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

class Parser
{
    public static ArrayList<Vector> parseCsvToPoints(String fileLocationString)
    {
        ArrayList<Vector> returnList = new ArrayList<>();
        try 
        {
            File f = new File(fileLocationString);
            if(f.exists())
            {
                Scanner scanner = new Scanner(f);
            
                //ignoring headers
                scanner.nextLine();

                while (scanner.hasNextLine()) 
                {
                    String line = scanner.nextLine(); 
                    line = line.replace("\"", "");

                    String[] delimiterSeparatedLine = line.split(",");
                    double[] vectorValuesForThisLine = new double[delimiterSeparatedLine.length];
                    boolean pregnantOrNotForThisLine = false;

                    for(int i = 0; i < delimiterSeparatedLine.length; i++)
                    {
                        String currentColumnValue = delimiterSeparatedLine[i];

                        // last column indicates result: 1 == pregnant, 0 == not.
                        if(i == 19)
                        {
                            if(currentColumnValue.equals("1")) pregnantOrNotForThisLine = true;
                        }
                        else
                        {
                            vectorValuesForThisLine[i] = Double.parseDouble(currentColumnValue);
                        }
                    }
                    returnList.add(new Vector(vectorValuesForThisLine, pregnantOrNotForThisLine));                    
                }
                scanner.close(); 
            }
            else
            {
                throw new Exception("Parser: File " + fileLocationString + " does not exist.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnList;
    }
}