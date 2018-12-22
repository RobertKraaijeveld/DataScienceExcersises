package com.hro.cmi.Regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hro.cmi.Vector;

import org.apache.commons.lang3.time.StopWatch;

import Jama.Matrix;
import Jama.QRDecomposition;

// SOURCE (Partially): https://introcs.cs.princeton.edu/java/97data/MultipleLinearRegression.java.html
public class MultipleLinearRegression 
{
    private int N; // number of independent variables
    private int p; // number of dependent variables
    private Matrix beta; // regression coefficients
    private double SSE; 

    private HashMap<Vector, Matrix> vectorMatrices = new HashMap<Vector, Matrix>();

    // Pre-computing the matrixes of all vectors so we dont have to recompute them each iteration
    public MultipleLinearRegression(ArrayList<Vector> allVectors)
    {
        for (Vector vector : allVectors) 
        {
            vectorMatrices.put(vector, new Matrix(vector.values, vector.values.length));    
        }
    }


    // TODO: REPLACE THIS WITH A BETTER METHOD
    public double RunAndReturnSSE(double[][] betas, Vector vector) 
    {
        if (betas.length != vector.values.length) throw new RuntimeException("dimensions don't agree");
        N = vector.values.length;
        p = betas[0].length;

        Matrix betasMatrix = new Matrix(betas);

        // create matrix from vector
        Matrix vectorMatrix = vectorMatrices.get(vector);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(betasMatrix);
        beta = qr.solve(vectorMatrix);

        // variation not accounted for
        Matrix residuals = betasMatrix.times(beta).minus(vectorMatrix);
        SSE = residuals.norm2() * residuals.norm2();

        return SSE;
    }

    public double beta(int j) 
    {
        return beta.get(j, 0);
    }
}