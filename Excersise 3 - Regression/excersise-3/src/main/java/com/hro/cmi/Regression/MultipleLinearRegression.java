package com.hro.cmi.Regression;

import com.hro.cmi.Vector;

import Jama.Matrix;
import Jama.QRDecomposition;

// SOURCE: https://introcs.cs.princeton.edu/java/97data/MultipleLinearRegression.java.html
public class MultipleLinearRegression 
{
    private final int N; // number of
    private final int p; // number of dependent variables
    private final Matrix beta; // regression coefficients
    private double SSE; // sum of squared
    private double SST; // sum of squared

    public MultipleLinearRegression(double[][] betas, Vector vector) 
    {
        if (betas.length != vector.values.length) throw new RuntimeException("dimensions don't agree");
        N = vector.values.length;
        p = betas[0].length;

        Matrix betasMatrix = new Matrix(betas);

        // create matrix from vector
        Matrix vectorMatrix = new Matrix(vector.values, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(betasMatrix);
        beta = qr.solve(vectorMatrix);

        // mean of vector.values[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
        {
            sum += vector.values[i];
        }
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++) 
        {
            double dev = vector.values[i] - mean;
            SST += dev * dev;
        }

        // variation not accounted for
        Matrix residuals = betasMatrix.times(beta).minus(vectorMatrix);
        SSE = residuals.norm2() * residuals.norm2();

    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }
}