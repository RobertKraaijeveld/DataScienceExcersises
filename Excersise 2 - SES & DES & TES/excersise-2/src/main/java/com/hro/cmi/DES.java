package com.hro.cmi;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class DES extends Forecast
{
    private ArrayList<Vector2> trendSmoothedVectors = new ArrayList<>();

    public DES(ArrayList<Vector2> originalVectors, int forecastAmount)
    {
        this.originalVectors = originalVectors;
        this.forecastAmount = forecastAmount;
        this.unforecastableVectorAmount = 2;
    }


    @Override
    public ArrayList<Vector2> forecastFunction(VariableHolder variables)
    {
        ArrayList<Vector2> smoothedVectors = new ArrayList<>();
        Vector2 firstOriginal = originalVectors.get(0);
        Vector2 secondOriginal = originalVectors.get(1);

        // DES uses pairs of two vectors so first two arent smoothed and are added to the array as-is        
        smoothedVectors.add(firstOriginal);
        smoothedVectors.add(secondOriginal);
        
        this.trendSmoothedVectors.add(firstOriginal);
        this.trendSmoothedVectors.add(new Vector2(secondOriginal.x, (secondOriginal.y - firstOriginal.y)));

        for (int i = unforecastableVectorAmount; i < originalVectors.size(); i++) 
        {
            smoothedVectors.add(computeSmoothedVector(smoothedVectors, variables, i));
            this.trendSmoothedVectors.add(computeTrendSmoothedVector(smoothedVectors, variables, i));
        }
        Vector2 lastSmoothedVector = smoothedVectors.get(smoothedVectors.size() - 1);
        Vector2 lastTrendSmoothedVector = this.trendSmoothedVectors.get(this.trendSmoothedVectors.size() - 1);        

        smoothedVectors.addAll(getDESForecast(lastSmoothedVector, lastTrendSmoothedVector));
        return smoothedVectors;
    }

    private ArrayList<Vector2> getDESForecast(Vector2 lastSmoothedVector, Vector2 lastTrendSmoothedVector)
    {
        ArrayList<Vector2> forecastedVectors = new ArrayList<>();

        for(int i = 0; i < forecastAmount; i++)
        {
            double forecastedValue = lastSmoothedVector.y + (i * lastTrendSmoothedVector.y);
            forecastedVectors.add(new Vector2(lastSmoothedVector.x + (i), forecastedValue));
        }
        return forecastedVectors;
    }

    private Vector2 computeSmoothedVector(ArrayList<Vector2> smoothedVectors, VariableHolder variables, int vectorIndex)
    {
        Vector2 originalVector = this.originalVectors.get(vectorIndex);
        double smoothedY; 

        if(vectorIndex < unforecastableVectorAmount)
        {
            smoothedY = originalVectors.get(vectorIndex).y;
            return new Vector2(originalVector.x, smoothedY);
        }
        else
        {
            double previousLevelValue = smoothedVectors.get(vectorIndex - 1).y;
            double previousTrendValue = this.trendSmoothedVectors.get(vectorIndex - 1).y; 

            smoothedY = DES.GetLevelValue(originalVector, variables, previousLevelValue, previousTrendValue);

            return new Vector2(originalVector.x, smoothedY);
        }
    }

    private Vector2 computeTrendSmoothedVector(ArrayList<Vector2> smoothedVectors, VariableHolder variables, int vectorIndex)
    {
        Vector2 originalVector = originalVectors.get(vectorIndex);
        double smoothedY = smoothedVectors.get(vectorIndex).y;
        
        if(vectorIndex < unforecastableVectorAmount)
        {
            if (vectorIndex > 0)
            {
                double placeHolderForUnsmoothable = originalVectors.get(unforecastableVectorAmount).y 
                                                    - originalVectors.get(unforecastableVectorAmount - 1).y;

                return new Vector2(originalVector.x, placeHolderForUnsmoothable);
            }
            else
                return new Vector2(originalVector.x, smoothedY);
        }
        else
        {
            double previousLevelValue = smoothedVectors.get(vectorIndex - 1).y;
            double currentLevelValue = smoothedVectors.get(vectorIndex).y;
            double previousTrendValue = this.trendSmoothedVectors.get(vectorIndex - 1).y; 

            double combinedSmoothAndTrendValueForecast = DES.GetTrendValue(originalVector, variables, 
                                                                           currentLevelValue, previousLevelValue, previousTrendValue);

            return new Vector2(originalVector.x, combinedSmoothAndTrendValueForecast);
        }
    }


    public static double GetLevelValue(Vector2 inputVector, VariableHolder variables, 
                                 double previousLevelValue, double currentTrendValue)
    {
        return variables.levelSmoothing * inputVector.y + (1 - variables.levelSmoothing) 
               * (previousLevelValue + currentTrendValue);
    }

    public static double GetTrendValue(Vector2 inputVector, VariableHolder variables, 
                                 double currentLevelValue, double previousLevelValue, double currentTrendValue)
    { 
        return variables.trendSmoothing * (currentLevelValue - previousLevelValue)
               + (1 - variables.trendSmoothing) * currentTrendValue;
    }
}