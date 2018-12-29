package com.hro.cmi.Regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hro.cmi.Vector;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import Jama.Matrix;
import Jama.QRDecomposition;

// SOURCE (Partially): https://introcs.cs.princeton.edu/java/97data/MultipleLinearRegression.java.html
public class MultipleLinearRegression 
{
    private HashMap<Vector, Matrix> vectorMatrices = new HashMap<Vector, Matrix>();

    // Pre-computing the matrixes of all vectors so we dont have to recompute them each iteration
    public MultipleLinearRegression(ArrayList<Vector> allVectors)
    {
        for (Vector vector : allVectors) 
        {
            vectorMatrices.put(vector, new Matrix(vector.values, vector.values.length));    
        }
    }

    public double RunAndReturnSSE(double[][] betas, Vector vector) 
    {
        if (betas.length != vector.values.length) throw new RuntimeException("dimensions don't agree");

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(vector.values, betas);

        return regression.calculateTotalSumOfSquares();
    }
}