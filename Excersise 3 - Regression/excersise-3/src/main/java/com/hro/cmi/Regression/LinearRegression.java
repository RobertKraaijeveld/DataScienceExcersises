package com.hro.cmi.Regression;

import java.awt.List;
import java.util.ArrayList;

import javax.sound.sampled.Line;

import com.hro.cmi.Tuple;
import com.hro.cmi.Utils;
import com.hro.cmi.Vector;

public class LinearRegression
{
    public ArrayList<Vector> Predict(double[] betas, ArrayList<Vector> input, boolean logistic)
    {
        ArrayList<Vector> vectorsWithPredictions = new ArrayList<>();

        for (Vector vector : input) 
        {
            double prediction = SumProduct(betas, vector.values);

            if(logistic) prediction = LogisticLinkFunction(prediction);

            Vector predictedVector = vector.Clone();
            predictedVector.PredictedPregnancyLikelihood = prediction;            
            vectorsWithPredictions.add(predictedVector);
        }
        return vectorsWithPredictions;
    }

    public double GetSse(ArrayList<Vector> predictions) 
    {
        double sum = 0;
        for(int i = 0; i < predictions.size(); i++)
        {
            Vector currPredictedVector = predictions.get(i);

            double predictedVal = currPredictedVector.PredictedPregnancyLikelihood;
            int actualVal = Utils.BoolToInt(currPredictedVector.IsActuallyPregnant);

            sum += (Math.pow((predictedVal - actualVal), 2));
        }
        return sum;
    }

    public ArrayList<Tuple<Float, Float>> GetRocCurve(ArrayList<Vector> predictions, double[] positiveClassificationCutoffs)
    {
        ArrayList<Tuple<Float, Float>> curveValues = new ArrayList<>();

        for (int i = 0; i < positiveClassificationCutoffs.length; i++) 
        {
            // TODO: FIX ONE INDICATOR AT A TIME. TEST USING BOOK VALS

            double currentcutoff = positiveClassificationCutoffs[i];
            double precision = Precision(predictions, positiveClassificationCutoffs[i]);

            float truePositivesForThisCutoff = (float) TruePositivesRate(predictions, positiveClassificationCutoffs[i]);
            float trueNegativesCount = (float) TrueNegativesRate(predictions, positiveClassificationCutoffs[i]);
            float falsePositivesForThisCutoff = 1.0f - (float) TrueNegativesRate(predictions, positiveClassificationCutoffs[i]);

            boolean x = true;
            curveValues.add(new Tuple<Float, Float>(falsePositivesForThisCutoff, truePositivesForThisCutoff));
        }
        return curveValues;
    }

    public double Precision(ArrayList<Vector> predictions, double positiveClassificationCutoff)
    {
        double truePositivesForCutoff = 0;
        double allPositivesForCutoff = 0; // also includes false positives
        
        for (Vector vector : predictions) 
        {
            if(vector.PredictedPregnancyLikelihood >= positiveClassificationCutoff)
            {
                allPositivesForCutoff += 1;

                if(vector.IsActuallyPregnant) truePositivesForCutoff += 1;
            }
        }
        return truePositivesForCutoff / allPositivesForCutoff;
    }

    public double TruePositivesRate(ArrayList<Vector> predictions, double positiveClassificationCutoff)
    {
        double truePositivesCount = 0;
        double actualPositivesCount = 0;

        for (Vector vector : predictions) 
        {
            if(vector.IsActuallyPregnant) actualPositivesCount++;

            if(vector.PredictedPregnancyLikelihood >= positiveClassificationCutoff 
                && vector.IsActuallyPregnant)
            {
                truePositivesCount++;
            }
        }
        return truePositivesCount / actualPositivesCount;
    }

    public double TrueNegativesRate(ArrayList<Vector> predictions, double positiveClassificationCutoff)
    {
        double trueNegativesCount = 0;
        double actualNegativesCount = 0;
        
        for (Vector vector : predictions) 
        {
            if(!vector.IsActuallyPregnant) actualNegativesCount++;

            if(vector.PredictedPregnancyLikelihood < positiveClassificationCutoff 
                && !vector.IsActuallyPregnant)
            {
                trueNegativesCount++;
            }
        }
        return trueNegativesCount / actualNegativesCount;
    }


    private double SumProduct(double[] vectorValues, double[] betas)
    {
        if(betas.length != vectorValues.length) throw new RuntimeException("SUMPRODUCT: Vector lengths must match");

        double sumProduct = 0;
        for (int i = 0; i < betas.length; i++) 
        {
            sumProduct += (betas[i] * vectorValues[i]);
        }
        return sumProduct;
    }

    // Is used to clamp the outcome of the multiple regression between 0 and 1.
    private double LogisticLinkFunction(double prediction)
    {
        return Math.exp(prediction) / (1 + Math.exp(prediction));
    }
}