package com.hro.cmi;

import java.util.ArrayList;
import java.util.Map;

public abstract class Forecast
{
    public ArrayList<Vector2> originalVectors = new ArrayList<>();

    public VariableHolder bestVariables;

    public int unforecastableVectorAmount;
    public int forecastAmount; 
    
    /*
    ABSTRACT METHODS
    */

    // Actually computes the smoothed version of the original vectors + forecast,
    // using the variables provided in the variables arg.
    public abstract ArrayList<Vector2> forecastFunction(VariableHolder variables);

    // Uses SSE to compute the error of the given smoothed vectors.
    public abstract double computeError(ArrayList<Vector2> smoothedVectors);
    
    // Runs the smoothing and forecast functions for alpha values ranging from 0.01 to 1,
    // returns the combination of alpha and error with the lowest error.
    public abstract ErrorMeasurer getErrorMeasurements();

    /*
    COMMON METHODS
    */

    public ArrayList<Vector2> runForecastWithBestError()
    {
        VariableHolder variablesWithSmallestError = this.getErrorMeasurements().getBestAlphaAndBeta();
        this.bestVariables = variablesWithSmallestError;

        return forecastFunction(variablesWithSmallestError); 
    }

}