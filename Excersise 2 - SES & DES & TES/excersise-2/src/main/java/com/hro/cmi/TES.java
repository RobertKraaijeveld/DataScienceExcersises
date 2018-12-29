package com.hro.cmi;

import java.util.ArrayList;

import javax.management.openmbean.ArrayType;

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
        ArrayList<Double> seasonalComponents = this.GetInitialSeasonalComponents();

        double level = 0;
        double trend = 0; 
        double forecastedXValue = this.originalVectors.get(this.originalVectors.size() - 1).x; 

        for(int i = 0; i < this.originalVectors.size() + this.forecastAmount; i++)
        {
            if(i == 0) // starting, setting initial vals
            {
                level = this.originalVectors.get(0).y;
                trend = this.GetInitialTrend();
                result.add(this.originalVectors.get(0));
            }

            if(i >= this.originalVectors.size()) // forecasting
            {
                int m = i - this.originalVectors.size() + 1;
                double forecastedYValue = (level + m * trend) + seasonalComponents.get(i % this.seasonLength);

                result.add(new Vector2(forecastedXValue, forecastedYValue));

                forecastedXValue += 1;
            }
            else // smoothing existing points
            {
                Vector2 currentValue = this.originalVectors.get(i);
                double previousLevel = level;

                level = DES.GetLevelValue(currentValue, variables, previousLevel, trend);
                trend = DES.GetTrendValue(currentValue, variables, level, previousLevel, trend);
                
                double newSeasonalComponent = GetSeasonalComponentValue(currentValue, variables, 
                                                                        level, seasonalComponents.get(i % this.seasonLength));
                seasonalComponents.set(i % this.seasonLength, newSeasonalComponent);

                double smoothedYValue = level + trend + seasonalComponents.get(i % this.seasonLength);
                result.add(new Vector2(currentValue.x, smoothedYValue));
            }
        }

        return result;
    }

    private double GetInitialTrend()
    {
        double sum = 0;
        for(int i = 0; i < this.seasonLength; i++)
        {
            double seasonBeginValue = this.originalVectors.get(i).y;
            double seasonEndValue = this.originalVectors.get(i + this.seasonLength).y;

            sum += (double) (seasonEndValue - seasonBeginValue) / this.seasonLength; 
        }
        return sum / this.seasonLength;
    }

    private ArrayList<Double> GetInitialSeasonalComponents()
    {
        ArrayList<Double> seasonAverages = new ArrayList<>();
        ArrayList<Double> seasonalComponents = new ArrayList<>();
        int numberOfSeasons = getAmountOfSeasons();

        // Computing season averages
        for(int i = 0; i < numberOfSeasons; i++)
        {
            int seasonStartIndex = this.seasonLength * i;
            int seasonEndIndex = this.seasonLength * i + this.seasonLength;
            
            ArrayList<Vector2> season = new ArrayList<>(this.originalVectors.subList(seasonStartIndex, seasonEndIndex));

            double seasonSum = season.stream().mapToDouble(v -> v.y).sum();
            seasonAverages.add(seasonSum / this.seasonLength);
        }

        // Computing initial values
        for (int j = 0; j < this.seasonLength; j++) 
        {
            double sumOfValuesOverAverage = 0;

            for (int z = 0; z < numberOfSeasons; z++) 
            {
                sumOfValuesOverAverage += this.originalVectors.get(this.seasonLength * z + j).y - seasonAverages.get(z);
            }
            seasonalComponents.add(sumOfValuesOverAverage);
        }

        return seasonalComponents;
    }


    private double GetSeasonalComponentValue(Vector2 inputVector, VariableHolder variables, 
                                             double levelValue, double previousValOfThisSeasonalComponent)
    {
        return variables.seasonalSmoothing * (inputVector.y - levelValue) + (1 - variables.seasonalSmoothing) * previousValOfThisSeasonalComponent;
    }

    private int getAmountOfSeasons()
    {
        return  originalVectors.size() / this.seasonLength;
    }
}