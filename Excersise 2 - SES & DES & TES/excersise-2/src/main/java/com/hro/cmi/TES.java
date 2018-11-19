package com.hro.cmi;

import java.util.ArrayList;

public class TES extends Forecast
{
    public int seasonLength; 

    public TES(ArrayList<Vector2> originalVectors, int forecastAmount, int seasonLength)
    {
        this.originalVectors = originalVectors;
        this.forecastAmount = forecastAmount;
        this.seasonLength = seasonLength;
        this.unforecastableVectorAmount = 2;
    }


    @Override
    public ArrayList<Vector2> forecastFunction(VariableHolder variables) 
    {
        ArrayList<Vector2> result = new ArrayList<>();

        ArrayList<Double> seasonals = this.computeInitialSeasonalComponents();
        double trend = this.computeInitialTrend();
        double lastSmoothedY = 0;
        double currentSmoothedY = 0;
        double xValueForCurrentForecastedPoint = this.originalVectors.get(this.originalVectors.size() - 1).x;

        for(int i = 0; i < this.originalVectors.size() + this.forecastAmount - 1; i++)
        {

            if(i == 0) // First point so not applying smoothing
            {
                Vector2 originalValue = originalVectors.get(i);

                currentSmoothedY = originalValue.y;
                result.add(originalValue);

                continue;
            }
            else if (i >= this.originalVectors.size()) // Forecasting in progress
            {
                double m = i -  originalVectors.size() + 1;
                double smoothedY = (currentSmoothedY + m * trend) + seasonals.get(i % this.seasonLength);

                Vector2 smoothedVector = new Vector2(xValueForCurrentForecastedPoint, smoothedY);
                result.add(smoothedVector);

                xValueForCurrentForecastedPoint += 1;
            }
            else // Smoothing of existing points
            {
                Vector2 originalValue = originalVectors.get(i);

                lastSmoothedY = currentSmoothedY;
                currentSmoothedY = variables.alpha * (originalValue.y - seasonals.get(i % this.seasonLength)
                                                      + (1 - variables.alpha) * (currentSmoothedY + trend));
                
                trend = variables.beta * (currentSmoothedY - lastSmoothedY) + (1 - variables.beta) * trend;

                seasonals.set(i % this.seasonLength, variables.gamma * (originalValue.y - currentSmoothedY)
                                                     + (1 - variables.gamma) * seasonals.get(i % this.seasonLength));

                result.add(new Vector2(originalValue.x, currentSmoothedY 
                                                        + trend 
                                                        + seasonals.get(i % this.seasonLength)));
            }
        }
        return result;
    }

    @Override
    public ErrorMeasurer getErrorMeasurements()
    {
        ArrayList<VariableHolder> variableHolders = new ArrayList<>();

        for(double alpha = 0.01f; alpha < 1.0f; alpha += 0.1f)
        {
            VariableHolder variableHolderForThisAlphaBetaGammaValue = new VariableHolder();
            variableHolderForThisAlphaBetaGammaValue.alpha = alpha;

            for(double beta = 0.01f; beta < 1.0f; beta += 0.01f)
            {
                variableHolderForThisAlphaBetaGammaValue.beta = beta;

                for(double gamma = 0.01f; gamma < 1.0f; gamma += 0.01f)
                {
                    variableHolderForThisAlphaBetaGammaValue.gamma = gamma;

                    ArrayList<Vector2> smoothedVectors = this.forecastFunction(variableHolderForThisAlphaBetaGammaValue);
                
                    double errorValue = this.computeError(smoothedVectors); 
                    variableHolderForThisAlphaBetaGammaValue.error = errorValue;
    
                    variableHolders.add(variableHolderForThisAlphaBetaGammaValue);    
                }            
            }
        }
        return new ErrorMeasurer(variableHolders);
    }

    @Override
    public double computeError(ArrayList<Vector2> smoothedVectors) 
    {
        double totalTESerror = 0.0f;
        for (int i = unforecastableVectorAmount; i < originalVectors.size(); i++) 
        {
            double combinedSmoothAndTrendValue = smoothedVectors.get(i - 1).y;
            totalTESerror += Math.pow((combinedSmoothAndTrendValue - originalVectors.get(i).y), 2); 
        }   
        return (double) Math.sqrt(totalTESerror/ (originalVectors.size() - unforecastableVectorAmount)); 
    }


    private ArrayList<Double> computeInitialSeasonalComponents()
    {
        ArrayList<Double> seasonalComponents = new ArrayList<>();
        ArrayList<Double> seasonAverages = new ArrayList<>();

        int numberOfSeasons = this.getAmountOfSeasons();
        for(int i = 0; i < numberOfSeasons; i++)
        {
            ArrayList<Vector2> pointsOfCurrentSeason = new ArrayList<Vector2>(originalVectors.subList(this.seasonLength * 1, 
                                                                                                     (this.seasonLength * 1) + seasonLength)
                                                                             );
            double currentSeasonAverage = pointsOfCurrentSeason.stream()
                                                        .map(v -> v.y)
                                                        .mapToDouble(Double::doubleValue) // incredibly doofy that this is necessary considering v.y IS A DOUBLE
                                                        .sum();
            seasonAverages.add(currentSeasonAverage);
        }

        for(int j = 0; j < this.seasonLength; j++)
        {
            double sumOfValuesOverAverage = 0.0;

            for(int z = 0; z < numberOfSeasons; z++)
            {
                sumOfValuesOverAverage += originalVectors.get((this.seasonLength * z) + j).y - seasonAverages.get(z);
            }
            seasonalComponents.add(j, sumOfValuesOverAverage);
        }

        return seasonalComponents;
    }

    // Computes the initial trend value: In DES, this was simply the first two values, but in TES
    // this is a little bit more complicated.
    private double computeInitialTrend()
    {
        double initialTrendValue = 0.0;    
        
        for(int i = 0; i < this.seasonLength; i++)
        {
            initialTrendValue += (this.originalVectors.get( + this.seasonLength).y - this.originalVectors.get(i).y) 
                                  / this.seasonLength;  
        }
        return initialTrendValue;
    }

    private int getAmountOfSeasons()
    {
        return  originalVectors.size() / this.seasonLength;
    }
}