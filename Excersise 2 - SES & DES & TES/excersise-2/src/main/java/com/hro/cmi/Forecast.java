package com.hro.cmi;

import java.util.ArrayList;
import java.util.Map;

public abstract class Forecast
{
    public ArrayList<Vector2> originalVectors = new ArrayList<>();

    public int unforecastableVectorAmount;
    public int forecastAmount; 
    
    /*
    ABSTRACT METHODS
    */

    // Actually computes the smoothed version of the original vectors + forecast,
    // using the variables provided in the variables arg.
    public abstract ArrayList<Vector2> forecastFunction(VariableHolder variables);

    // Uses SSE to compute the error of the given smoothed vectors.
    public double computeError(ArrayList<Vector2> smoothedVectors)
    {
        double totalSSE = 0.0f;
        for (int i = unforecastableVectorAmount; i < originalVectors.size(); i++) 
        {
            totalSSE += Math.pow((smoothedVectors.get(i).y - originalVectors.get(i).y), 2); 
        }   
        return (double) totalSSE; 
    }
}