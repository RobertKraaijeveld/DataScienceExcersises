package com.hro.cmi.Regression;

import java.util.ArrayList;

import com.hro.cmi.Utils;
import com.hro.cmi.Vector;

public abstract class AbstractRegression
{
    public abstract ArrayList<Vector> Predict(ArrayList<Vector> input);

    protected double ComputeSse(ArrayList<Vector> predictions) 
    {
        double sum = 0;
        for(int i = 0; i < predictions.size(); i++)
        {
            Vector currPredictedVector = predictions.get(i);

            double predictedVal = currPredictedVector.PredictedPregnancyLikelihood;
            int actualVal = Utils.BoolToInt(currPredictedVector.IsActuallyPregnant);

            sum += (Math.pow((predictedVal - actualVal), 2));
        }
        return Math.sqrt(sum);
    }
}