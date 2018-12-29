package com.hro.cmi;

import java.util.ArrayList;
import java.util.Set;

class SES extends Forecast
{
    public SES(ArrayList<Vector2> originalVectors, int forecastAmount)
    {
        this.originalVectors = originalVectors;
        this.forecastAmount = forecastAmount;
        this.unforecastableVectorAmount = 1;
    }


    @Override
    public ArrayList<Vector2> forecastFunction(VariableHolder variables)
    {
        ArrayList<Vector2> smoothedVectors = new ArrayList<>();

        Vector2 firstOriginal = originalVectors.get(0);
        smoothedVectors.add(firstOriginal);

        for (int i = 1; i < originalVectors.size() + 1; i++) 
        {
            smoothedVectors.add(computeSESSmoothedVector(smoothedVectors, variables.levelSmoothing, i));       
        }
        Vector2 lastSmoothedPoint = smoothedVectors.get(smoothedVectors.size() - 1);
        smoothedVectors.addAll(getSESForecast(lastSmoothedPoint));

        return smoothedVectors;
    }


    private Vector2 computeSESSmoothedVector(ArrayList<Vector2> smoothedVectors, double alpha, int positionOfVector)
    {
        double originalVectorX;
        double smoothedY; 

        /* NOTE: The implemented Formula uses the Hunter (1986) rather than Roberts (1959) method. 
           The latter is more correct since it does not 'skip' the first position.
        */

        if(positionOfVector == originalVectors.size())
        {
            originalVectorX = originalVectors.get(positionOfVector - 1).x + 1;             
        }
        else
        {
            originalVectorX = originalVectors.get(positionOfVector).x;                 
        }

        smoothedY = alpha * originalVectors.get(positionOfVector - 1).y + (1.0f - alpha) * smoothedVectors.get(positionOfVector - 1).y;      
        return new Vector2(originalVectorX, smoothedY);
    }

    

    public static double GetLevelValue(Vector2 inputVector, VariableHolder variables, 
                                 double previousLevelValue, double currentTrendValue)
    {
        return variables.levelSmoothing * inputVector.y + (1 - variables.levelSmoothing) 
               * (previousLevelValue + currentTrendValue);
    }

    private ArrayList<Vector2> getSESForecast(Vector2 lastSmoothedPoint)
    {
        ArrayList<Vector2> returnListToBeMerged = new ArrayList<>();
        for(int i = 0; i < forecastAmount; i++)
        {
            Vector2 newPoint = new Vector2(lastSmoothedPoint.x + (i - 1), lastSmoothedPoint.y); 
            returnListToBeMerged.add(newPoint);
        }
        return returnListToBeMerged;
    } 
}