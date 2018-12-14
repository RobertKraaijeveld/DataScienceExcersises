package com.hro.cmi;


public class Vector
{
    public static final int DIMENSION = 38;

    public final boolean IsActuallyPregnant;
    public double PredictedPregnancyLikelihood;
    public double[] values = new double[DIMENSION];

    public Vector(double[] values, boolean isActuallyPregnant) 
    {
        this.IsActuallyPregnant = isActuallyPregnant;
        this.values = values;
    }

    public Vector Clone()
    {
        Vector retVector = new Vector(this.values, this.IsActuallyPregnant);
        retVector.PredictedPregnancyLikelihood = this.PredictedPregnancyLikelihood;
        return retVector;
    }

    public double VectorToDouble()
    {
        double sum = 0;
        for(int i = 0; i < values.length; i++)
        {
            sum += values[i];
        }
        return sum;
    }
}