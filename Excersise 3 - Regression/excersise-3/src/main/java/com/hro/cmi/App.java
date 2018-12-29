package com.hro.cmi;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

import javax.sound.sampled.Line;

import com.hro.cmi.GeneticAlgorithm.GeneticAlgorithm;
import com.hro.cmi.GeneticAlgorithm.GeneticIndividual;
import com.hro.cmi.Regression.LinearRegression;

import java.text.*;

public class App extends PApplet
{
    private static final int APPLET_WIDTH = 1000;
    private static final int APPLET_HEIGHT = 800;
    private static final int YAXIS_OFFSET = -1;
    private static final int XAXIS_OFFSET = 18;

    public static void main( String[] args )
    {
        PApplet.main(new String[] { App.class.getName() });
    }

    public void setup()
    {
        noSmooth();
        noLoop();
        size(APPLET_WIDTH, APPLET_HEIGHT);
    }

    public void draw()
    {
        background(225, 225, 225);
        fill(0,0,0);

        // Getting training/testing sets
        ArrayList<Vector> trainingSet = Parser.parseCsvToPoints("C:\\Projects\\DataScienceFinalRetake\\Excersise 3 - Regression\\excersise-3\\docs\\TrainingSet.csv");
        ArrayList<Vector> testSet = Parser.parseCsvToPoints("C:\\Projects\\DataScienceFinalRetake\\Excersise 3 - Regression\\excersise-3\\docs\\TestSet.csv");

        // Training using genetic algo for getting best betas
        double crossoverRate = 0.65;
        double mutationRate = 0.05;
        int populationSize = 20;
        int generations = 250;

        GeneticAlgorithm algo = new GeneticAlgorithm(trainingSet, crossoverRate, mutationRate, true, populationSize, generations);
        GeneticIndividual bestIndividual = algo.Run();
        
        double[] algoBetas = GeneticIndividual.ToDoublesArray(bestIndividual);
        double[] bookBetas = new double[]
        {
            -0.58,	-0.13, -0.15,	0.03, 2.37,	-2.29, -2.03, 4.08,	2.48, 2.95,	1.25, 1.94, 1.10, 1.31, -1.45, 1.80, 1.39, -1.56, 2.08, -0.24
        };

        // Running on test set using computed/book betas
        LinearRegression regression = new LinearRegression();

        ArrayList<Vector> vectorsWithBookLogisticPredictions = regression.Predict(bookBetas, testSet, true);
        ArrayList<Vector> vectorsWithRobertsLogisticPredictions = regression.Predict(algoBetas, testSet, true);

        double bookLogisticSSE = regression.GetSse(vectorsWithBookLogisticPredictions);
        double robertsLogisticSSE = regression.GetSse(vectorsWithRobertsLogisticPredictions);

        double[] cutOffVals = new double[]
        {
            0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1, 1.05, 1.1, 1.15, 1.2, 1.25
        };

        ArrayList<Tuple<Float, Float>> bookLogisticCurve = regression.GetRocCurve(vectorsWithBookLogisticPredictions, cutOffVals);
        ArrayList<Tuple<Float, Float>> robertsLogisticCurve = regression.GetRocCurve(vectorsWithRobertsLogisticPredictions, cutOffVals);


        drawGeneticAlgoValues(robertsLogisticSSE, bookLogisticSSE, 
                              crossoverRate, mutationRate, populationSize, generations);
        drawLegend(); 
        drawAxises();

        stroke(0, 0, 255);
        fill(0, 0, 255);
        drawROC(robertsLogisticCurve);

        stroke(255, 0, 0);
        fill(255, 0, 0);
        drawROC(bookLogisticCurve);
    }


    /***
    * Methods for drawing UI 
    */

    private void drawGeneticAlgoValues(double robertsLogisticSSE, double bookLogisticSSE, 
                                       double crossoverRate, double mutationRate, int populationSize, int generations)
    {
        textSize(12);
        fill(0,0,0);

        text("Genetic Algo SSE (Logistic): " + robertsLogisticSSE, 95, 660.0f);       
        text("Book SSE (Logistic): " + bookLogisticSSE, 95, 680.0f);      

        text("Crossover rate: " + crossoverRate, 395, 660.0f);       
        text("Mutation rate: " + mutationRate, 395, 680.0f);       
        text("Population size: " + populationSize, 395, 700.0f);       
        text("Amount of generations: " + generations, 395, 720.0f);       
    }
        

    private void drawLegend()
    {
        textSize(40);
        fill(0,0,0);
        text("ROC Curve of \'Pregnant\' data test-set.", 10.0f, 100.0f);     

        textSize(9);
        fill(0,0,0);
        text("Source: https://www.wiley.com/en-us/Data+Smart%3A+Using+Data+Science+to+Transform+Information+into+Insight-p-9781118661468", 10.0f, 120.0f);               

        textSize(16);
        fill(255, 0, 0);
        text("Red line: ROC Curve of logistic prediction using Wiley\'s beta-values.", 10.0f, 210.0f);

        fill(0, 0, 255);
        text("Blue line: ROC Curve of logistic prediction using implemented genetic algorithm's beta-values.", 10.0f, 240.0f);
    }

    private void drawAxises()
    {
        fill(0,0,0);

        invertYAxis();
        stroke(0, 0, 0);

        //x axis 
        line(90, 245, 280, 245);   

        //y axis
        line(90, 245, 90, 500);           
        popMatrix();         
        

        // X axis labels        
        textSize(12);

        text("100%", 260, 575);
        text("50%", 180, 575);
        text("0%", 95, 575);

        textSize(15);
        text("X: False Positive Rate", 95, 600);
        text("Y: True Positive Rate", 95, 620);


        // Y axis labels        
        textSize(12);

        text("0%", 55, 550);
        text("50%", 55, 430);
        text("100%", 55, 315);

        textSize(15);
    }


    /***
    * Methods for drawing regressions
    */

    private void drawROC(ArrayList<Tuple<Float, Float>> points)
    {
        float leftPadding = 10.0f;        
        float firstPointXposition = 40.0f;
        Tuple<Float, Float> previousPointVector = new Tuple<Float, Float>(firstPointXposition, 120.0f);

        for (Tuple<Float, Float> currentPoint : points) 
        {
            float shiftedXValue = ((currentPoint.Item1 * XAXIS_OFFSET) + leftPadding) * 10;
            float shiftedYValue = ((currentPoint.Item2 - YAXIS_OFFSET)) * 250;

            //inverting so we dont have to map/flip the y values
            invertYAxis();
           
            ellipse((float) shiftedXValue, (float) shiftedYValue, 5.0f, 5.0f);
           
            if(previousPointVector.Item1 != firstPointXposition)
            {
                line((float) previousPointVector.Item1, (float) previousPointVector.Item2, (float) shiftedXValue, (float) shiftedYValue);
            }

            previousPointVector.Item1 = shiftedXValue; 
            previousPointVector.Item2 = shiftedYValue;

            popMatrix();
        }
    }

    private void invertYAxis()
    {
        pushMatrix();
        translate(0, height);
        scale(1,-1);
    }
}
