package com.hro.cmi.Regression;

import java.awt.List;
import java.util.ArrayList;

import javax.sound.sampled.Line;

import com.hro.cmi.Vector;

public class LinearRegression extends AbstractRegression
{
    private final double Intercept;
    private final double Slope;

    public LinearRegression(double intercept, double slope)
    {
        this.Intercept = intercept;
        this.Slope = slope;
    }


    public static LinearRegression Train(ArrayList<Vector> trainingSet)
    {
        // 'Training' the algorithm by selecting the alpha and beta params with the lowest SSE.
        // Using a very simple method for now.
        double bestIntercept = 0;
        double bestSlope = 0;
        double lowestSseYet = Double.MAX_VALUE;

        // instantiating only 1 regression object at a time (in the loop) instead of adding multiple regressions to a list. 
        // Is faster.
        LinearRegression regression; 

        for(double intercept = 0.01; intercept < 1; intercept += 0.001)
        {
            for(double slope = 0.01; slope < 1; slope += 0.001)
            {
                regression = new LinearRegression(intercept, slope);

                ArrayList<Vector> predictionForTheseCoefficients = regression.Predict(trainingSet);
                double sseUsingTheseCoefficients = regression.ComputeSse(predictionForTheseCoefficients);

                if(sseUsingTheseCoefficients < lowestSseYet) 
                {
                    lowestSseYet = sseUsingTheseCoefficients;
                    bestIntercept = intercept;
                    bestSlope = slope; 
                }
            }
        }

        return new LinearRegression(bestIntercept, bestSlope);
    }

    public ArrayList<Vector> Predict(ArrayList<Vector> input)
    {
        ArrayList<Vector> vectorsWithPredictions = new ArrayList<>();

        for (Vector inputVector : input) 
        {
            double vectorAsDouble = inputVector.VectorToDouble();
            double prediction = Intercept + (Slope * vectorAsDouble);

            Vector predictedVector = inputVector.Clone();
            predictedVector.PredictedPregnancyLikelihood = prediction;
            
            vectorsWithPredictions.add(predictedVector);
        }
        return vectorsWithPredictions;
    }
}