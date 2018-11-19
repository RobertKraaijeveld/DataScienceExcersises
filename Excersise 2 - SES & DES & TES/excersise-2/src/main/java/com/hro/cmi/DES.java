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
            smoothedVectors.add(computeSmoothedVector(smoothedVectors, variables.alpha, i));
            this.trendSmoothedVectors.add(computeTrendSmoothedVector(smoothedVectors, variables.beta, i));
        }
        Vector2 lastSmoothedVector = smoothedVectors.get(smoothedVectors.size() - 1);
        Vector2 lastTrendSmoothedVector = this.trendSmoothedVectors.get(this.trendSmoothedVectors.size() - 1);        

        smoothedVectors.addAll(getDESForecast(lastSmoothedVector, lastTrendSmoothedVector));
        return smoothedVectors;
    }


    @Override
    public ErrorMeasurer getErrorMeasurements()
    {
        ArrayList<VariableHolder> variableHolders = new ArrayList<>();

        for(double alpha = 0.01f; alpha < 1.0f; alpha += 0.1f)
        {
            VariableHolder variableHolderForThisAlphaAndBetaValue = new VariableHolder();
            variableHolderForThisAlphaAndBetaValue.alpha = alpha;

            for(double beta = 0.01f; beta < 1.0f; beta += 0.01f)
            {
                variableHolderForThisAlphaAndBetaValue.beta = beta;

                ArrayList<Vector2> smoothedVectors = this.forecastFunction(variableHolderForThisAlphaAndBetaValue);
                
                double errorValue = this.computeError(smoothedVectors); 
                variableHolderForThisAlphaAndBetaValue.error = errorValue;

                variableHolders.add(variableHolderForThisAlphaAndBetaValue);
            }
        }
        return new ErrorMeasurer(variableHolders);
    }

    @Override
    public double computeError(ArrayList<Vector2> smoothedVectors) 
    {
        double totalDESerror = 0.0f;
        for (int i = unforecastableVectorAmount; i < originalVectors.size(); i++) 
        {
            double combinedSmoothAndTrendValue = smoothedVectors.get(i - 1).y + trendSmoothedVectors.get(i - 1).y;
            totalDESerror += Math.pow((combinedSmoothAndTrendValue - originalVectors.get(i).y), 2); 
        }   
        return (double) Math.sqrt(totalDESerror / (originalVectors.size() - unforecastableVectorAmount)); 
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

    private Vector2 computeSmoothedVector(ArrayList<Vector2> smoothedVectors, double alpha, int position)
    {
        double originalVectorX = originalVectors.get(position).x;     
        double smoothedY; 

        if(position < unforecastableVectorAmount)
        {
            smoothedY = originalVectors.get(position).y;
            return new Vector2(originalVectorX, smoothedY);
        }
        else
        {
            //X is a time series so we dont smooth it
            smoothedY = alpha * originalVectors.get(position).y + (1 - alpha)
                       * (smoothedVectors.get(position - 1).y + this.trendSmoothedVectors.get(position - 1).y);

            return new Vector2(originalVectorX, smoothedY);
        }
    }

    private Vector2 computeTrendSmoothedVector(ArrayList<Vector2> smoothedVectors, double beta, int position)
    {
        double originalVectorX = originalVectors.get(position).x;
        double smoothedY = smoothedVectors.get(position).y;
        
        if(position < unforecastableVectorAmount)
        {
            if (position > 0)
            {
                double placeHolderForUnsmoothable = originalVectors.get(unforecastableVectorAmount).y 
                                                    - originalVectors.get(unforecastableVectorAmount - 1).y;

                return new Vector2(originalVectorX, placeHolderForUnsmoothable);
            }
            else
                return new Vector2(originalVectorX, smoothedY);
        }
        else
        {
            //X is a time series so we dont smooth it
            double combinedSmoothAndTrendValueForecast = beta * (smoothedVectors.get(position).y - smoothedVectors.get(position - 1).y)
                                                      + (1.0f - beta) * this.trendSmoothedVectors.get(position - 1).y;

            return new Vector2(originalVectorX, combinedSmoothAndTrendValueForecast);
        }
    }
}