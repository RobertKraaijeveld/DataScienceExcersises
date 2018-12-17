package com.hro.cmi;


public class Vector
{
    public static final int DIMENSION = 20;

    public final boolean IsActuallyPregnant;
    public double PredictedPregnancyLikelihood;
    public double[] values = new double[DIMENSION];

    public Vector(double[] inputValues, boolean isActuallyPregnant) 
    {
        this.IsActuallyPregnant = isActuallyPregnant;

        for (int i = 0; i < inputValues.length; i++) 
        {
            this.values[i] = inputValues[i];
        }
        this.values[this.values.length - 1] = 1; // Represents intercept, which is always 1 to start with.
    }

    public Vector Clone()
    {
        Vector retVector = new Vector(this.values, this.IsActuallyPregnant);
        retVector.PredictedPregnancyLikelihood = this.PredictedPregnancyLikelihood;
        return retVector;
    }
}